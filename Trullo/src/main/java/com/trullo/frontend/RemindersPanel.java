package com.trullo.frontend;

import com.trullo.model.Reminder;
import com.trullo.model.ReminderTarget;
import com.trullo.model.Task;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Lado derecho de la pantalla de calendario: muestra los recordatorios del día seleccionado.
 */
public final class RemindersPanel extends JPanel {

    private final JLabel dateLabel;
    private final JPanel remindersListPanel;

    /**
     * Recibe la acción que abre el diálogo para agregar un recordatorio.
     */
    public RemindersPanel(Runnable onAddRequested) {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setPreferredSize(new Dimension(340, 500));

        dateLabel = UiKit.themedLabel("Recordatorios", Font.BOLD, 16, Theme::text);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

        // Lista vertical de ReminderCard dentro de un scroll.
        remindersListPanel = new JPanel();
        remindersListPanel.setLayout(new javax.swing.BoxLayout(
                remindersListPanel,
                javax.swing.BoxLayout.Y_AXIS
        ));
        remindersListPanel.setOpaque(false);

        JScrollPane scrollPane = UiKit.scrollPane(remindersListPanel);

        // Botón naranja que dispara la creación de un recordatorio.
        JButton addButton = UiKit.coloredButton("Añadir recordatorio", () -> Theme.WARNING);
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addButton.setPreferredSize(new Dimension(100, 40));
        addButton.addActionListener(actionEvent -> onAddRequested.run());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        buttonPanel.add(addButton, BorderLayout.CENTER);

        add(dateLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Filtra los recordatorios del día, los ordena por hora y arma una tarjeta por cada uno.
     */
    public void showReminders(LocalDate selectedDate,
                              List<Reminder> reminders,
                              Map<Long, Task> tasksById,
                              Function<Reminder, List<ReminderTarget>> targetsOf,
                              Consumer<Reminder> onDelete) {
        dateLabel.setText("Recordatorios · " + UiKit.formatDate(selectedDate));
        remindersListPanel.removeAll();

        List<Reminder> remindersForDay = reminders.stream()
                .filter(reminder -> reminder.getScheduledAt() != null)
                .filter(reminder -> reminder.getScheduledAt().toLocalDate().equals(selectedDate))
                .sorted(Comparator.comparing(Reminder::getScheduledAt))
                .toList();

        if (remindersForDay.isEmpty()) {
            JLabel emptyLabel = UiKit.themedLabel(
                    "No hay recordatorios para este día.",
                    Font.PLAIN,
                    13,
                    Theme::mutedText
            );
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 4, 0, 0));
            remindersListPanel.add(emptyLabel);
        } else {
            // Busca la tarea de cada recordatorio para mostrar su título.
            for (Reminder reminder : remindersForDay) {
                Task task = tasksById.get(reminder.getTaskId());
                remindersListPanel.add(new ReminderCard(reminder, task, targetsOf.apply(reminder), onDelete));
                remindersListPanel.add(javax.swing.Box.createVerticalStrut(8));
            }
        }

        remindersListPanel.revalidate();
        remindersListPanel.repaint();
    }

    /**
     * Muestra un mensaje de error en lugar de la lista.
     */
    public void showError(String message) {
        remindersListPanel.removeAll();
        JLabel errorLabel = UiKit.themedLabel(
                message,
                Font.PLAIN,
                13,
                () -> Theme.DANGER
        );
        errorLabel.setBorder(BorderFactory.createEmptyBorder(10, 4, 0, 0));
        remindersListPanel.add(errorLabel);
        remindersListPanel.revalidate();
        remindersListPanel.repaint();
    }
}
