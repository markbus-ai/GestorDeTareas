package com.trullo.frontend;

import com.trullo.controller.ReminderController;
import com.trullo.controller.TaskController;
import com.trullo.model.Reminder;
import com.trullo.model.Task;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Pantalla de calendario: vista mensual a la izquierda y recordatorios del día seleccionado a
 * la derecha.
 */
public final class CalendarScreen extends JPanel implements RefreshableScreen {

    // Controladores: recordatorios para el panel derecho y tareas para vincular cada recordatorio.
    private final ReminderController reminderController;
    private final TaskController taskController;
    private final MonthlyCalendarView calendarView;
    private final RemindersPanel remindersPanel;

    // Datos cargados desde la base y un índice de tareas por id.
    private List<Task> tasks = new ArrayList<>();
    private List<Reminder> reminders = new ArrayList<>();
    private Map<Long, Task> tasksById = new HashMap<>();

    /**
     * Arma las dos tarjetas (calendario y recordatorios) y conecta sus callbacks.
     */
    public CalendarScreen(ReminderController reminderController, TaskController taskController) {
        this.reminderController = reminderController;
        this.taskController = taskController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        // El calendario avisa qué día se seleccionó para refrescar el panel derecho.
        calendarView = new MonthlyCalendarView();
        calendarView.setOnDateSelected(this::showRemindersForDate);

        // El panel de recordatorios pide abrir el diálogo al tocar "Añadir recordatorio".
        remindersPanel = new RemindersPanel(this::openNewReminderDialog);

        RoundedPanel calendarCard = UiKit.card(new BorderLayout());
        calendarCard.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        calendarCard.setPreferredSize(new Dimension(560, 0));
        calendarCard.add(calendarView, BorderLayout.CENTER);

        RoundedPanel remindersCard = UiKit.card(new BorderLayout());
        remindersCard.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        remindersCard.setPreferredSize(new Dimension(340, 0));
        remindersCard.add(remindersPanel, BorderLayout.CENTER);

        // La distribución reparte el ancho: el calendario ocupa un poco más que los recordatorios.
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints calendarConstraints = new GridBagConstraints();
        calendarConstraints.gridx = 0;
        calendarConstraints.gridy = 0;
        calendarConstraints.weightx = 1.55;
        calendarConstraints.weighty = 1.0;
        calendarConstraints.fill = GridBagConstraints.BOTH;
        calendarConstraints.insets = new Insets(0, 0, 0, 12);

        GridBagConstraints remindersConstraints = new GridBagConstraints();
        remindersConstraints.gridx = 1;
        remindersConstraints.gridy = 0;
        remindersConstraints.weightx = 1.0;
        remindersConstraints.weighty = 1.0;
        remindersConstraints.fill = GridBagConstraints.BOTH;
        remindersConstraints.insets = new Insets(0, 12, 0, 0);

        contentPanel.add(calendarCard, calendarConstraints);
        contentPanel.add(remindersCard, remindersConstraints);

        JPanel headerPanel = UiKit.sectionHeader(
                "Calendario",
                "Mirá tu mes y programá recordatorios para tus tareas."
        );
        headerPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Al cambiar el tema se repintan las dos vistas.
        Theme.addListener(() -> {
            calendarView.repaint();
            remindersPanel.repaint();
            repaint();
        });
    }

    /**
     * MainWindow llama a esto cada vez que se abre la pantalla.
     */
    @Override
    public void onShown() {
        reloadData();
    }

    /**
     * Trae tareas y recordatorios, arma el índice de tareas y marca los días con recordatorios.
     */
    private void reloadData() {
        try {
            tasks = taskController.findAll();
            reminders = reminderController.findAll();

            tasksById = new HashMap<>();
            for (Task task : tasks) {
                tasksById.put(task.getId(), task);
            }

            // El calendario solo necesita las fechas que tienen al menos un recordatorio.
            Set<LocalDate> reminderDates = new HashSet<>();
            for (Reminder reminder : reminders) {
                if (reminder.getScheduledAt() != null) {
                    reminderDates.add(reminder.getScheduledAt().toLocalDate());
                }
            }

            calendarView.setReminderDates(reminderDates);
            showRemindersForDate(calendarView.getSelectedDate());
        } catch (RuntimeException exception) {
            remindersPanel.showError("No se pudieron cargar los datos del calendario.");
            calendarView.setReminderDates(Set.of());
        }
    }

    /**
     * Toma la lista de recordatorios que vino del ReminderController, filtra los del día
     * elegido y se los pasa al panel derecho junto con el mapa de tareas.
     */
    private void showRemindersForDate(LocalDate date) {
        remindersPanel.showReminders(
                date,
                reminders,
                tasksById,
                this::deleteReminder
        );
    }

    /**
     * Abre el diálogo de recordatorio; si no hay tareas, avisa que primero hay que crear una.
     */
    private void openNewReminderDialog() {
        if (tasks.isEmpty()) {
            UiKit.showInfo(
                    this,
                    "No hay tareas disponibles",
                    "Creá una tarea primero, luego programá un recordatorio para ella."
            );
            return;
        }

        Optional<Reminder> createdReminder = NewReminderDialog.open(
                this,
                reminderController,
                calendarView.getSelectedDate(),
                tasks
        );
        createdReminder.ifPresent(reminder -> reloadData());
    }

    /**
     * Elimina el recordatorio en el backend y recarga la pantalla.
     */
    private void deleteReminder(Reminder reminder) {
        try {
            reminderController.delete(reminder.getId());
            reloadData();
        } catch (RuntimeException exception) {
            UiKit.showError(this, "No se pudo eliminar el recordatorio", exception);
        }
    }
}
