package com.trullo.frontend;

import com.trullo.controller.ReminderController;
import com.trullo.model.NotificationChannel;
import com.trullo.model.Reminder;
import com.trullo.model.Task;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Diálogo para programar un recordatorio de una tarea en un día y horario determinados.
 */
public final class NewReminderDialog extends BaseDialog {

    // Etiquetas visibles y su equivalente en el enum del backend.
    private static final String[] CHANNEL_LABELS = {
            "WhatsApp", "Correo electrónico", "WhatsApp + Correo electrónico"
    };
    private static final NotificationChannel[] CHANNEL_VALUES = {
            NotificationChannel.WHATSAPP,
            NotificationChannel.EMAIL,
            NotificationChannel.AMBOS
    };

    private final ReminderController reminderController;
    private final LocalDate selectedDate;
    private final JComboBox<Task> taskComboBox;
    private final JSpinner timeSpinner;
    private final JTextField phoneField;
    private final JTextField emailField;
    private final JPanel phoneRow;
    private final JPanel emailRow;
    // Recordatorio creado, para devolverlo a CalendarScreen.
    private Reminder createdReminder;

    /**
     * Arma el formulario: fecha, tarea, hora, canal y destino (teléfono y/o email).
     */
    private NewReminderDialog(Window owner,
                              ReminderController reminderController,
                              LocalDate selectedDate,
                              List<Task> tasks) {
        super(owner, "Recordatorio", 560, 600);
        this.reminderController = reminderController;
        this.selectedDate = selectedDate;

        // Muestra arriba la fecha elegida en el calendario.
        JLabel dateLabel = new JLabel("Recordatorio para " + UiKit.formatDate(selectedDate));
        dateLabel.setFont(UiKit.font(Font.BOLD, 13));
        dateLabel.setForeground(Theme.mutedText());
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(dateLabel);
        formPanel.add(javax.swing.Box.createVerticalStrut(18));

        // Solo se pueden elegir tareas que ya existen en la base.
        taskComboBox = new JComboBox<>(tasks.toArray(new Task[0]));
        styleTaskComboBox(taskComboBox);
        addFormField("Tarea", taskComboBox);

        // Hora del recordatorio (la fecha viene del día seleccionado).
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        timeSpinner.setFont(UiKit.font(Font.PLAIN, 14));
        timeSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        timeSpinner.setPreferredSize(new Dimension(100, 40));
        addFormField("Hora", timeSpinner);

        JComboBox<String> channelComboBox = formComboBox(CHANNEL_LABELS);
        channelComboBox.setSelectedIndex(0);
        addFormField("Canal de notificación", channelComboBox);

        phoneField = formTextField("+54 9 11 1234-5678");
        emailField = formTextField("name@example.com");

        // Las filas de destino se muestran u ocultan según el canal elegido.
        phoneRow = createTargetRow("Teléfono", phoneField);
        emailRow = createTargetRow("Correo electrónico", emailField);
        phoneRow.setVisible(true);
        emailRow.setVisible(false);

        formPanel.add(phoneRow);
        formPanel.add(javax.swing.Box.createVerticalStrut(12));
        formPanel.add(emailRow);

        channelComboBox.addActionListener(actionEvent ->
                updateTargetVisibility(channelComboBox.getSelectedIndex())
        );

        // Botones Cancelar / Crear recordatorio.
        JPanel footer = formFooter();
        JButton cancelButton = secondaryAction("Cancelar");
        JButton createButton = primaryAction("Crear recordatorio");
        cancelButton.addActionListener(actionEvent -> dispose());
        createButton.addActionListener(actionEvent -> createReminder(
                channelComboBox.getSelectedIndex()
        ));

        footer.add(cancelButton);
        footer.add(createButton);
        setFooter(footer);
        getRootPane().setDefaultButton(createButton);
    }

    /**
     * Abre el diálogo modal y devuelve el recordatorio creado, si lo hubo.
     */
    public static Optional<Reminder> open(Component parent,
                                           ReminderController reminderController,
                                           LocalDate selectedDate,
                                           List<Task> tasks) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        NewReminderDialog dialog = new NewReminderDialog(
                owner,
                reminderController,
                selectedDate,
                tasks
        );
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.createdReminder);
    }

    /**
     * Personaliza el combo de tareas para que muestre el título de cada Task.
     */
    private void styleTaskComboBox(JComboBox<Task> comboBox) {
        comboBox.setFont(UiKit.font(Font.PLAIN, 14));
        comboBox.setFocusable(false);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboBox.setPreferredSize(new Dimension(100, 40));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            /**
             * Muestra el título de cada tarea y aplica los colores del tema a las opciones.
             */
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );
                Task task = value instanceof Task ? (Task) value : null;
                label.setText(task == null ? "" : task.getTitle());
                label.setFont(UiKit.font(Font.PLAIN, 13));
                label.setBorder(new javax.swing.border.EmptyBorder(4, 10, 4, 10));
                label.setBackground(isSelected ? Theme.accent() : Theme.input());
                label.setForeground(isSelected ? java.awt.Color.WHITE : Theme.text());
                return label;
            }
        });

        comboBox.setBackground(Theme.input());
        comboBox.setForeground(Theme.text());
        comboBox.setBorder(BorderFactory.createLineBorder(Theme.border(), 1, true));
        Theme.addListener(() -> {
            comboBox.setBackground(Theme.input());
            comboBox.setForeground(Theme.text());
            comboBox.setBorder(BorderFactory.createLineBorder(Theme.border(), 1, true));
        });
    }

    /**
     * Fila con label a la izquierda y campo de destino a la derecha.
     */
    private JPanel createTargetRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel label = new JLabel(labelText);
        label.setFont(UiKit.font(Font.BOLD, 12));
        label.setForeground(Theme.text());
        label.setPreferredSize(new Dimension(64, 34));
        Theme.addListener(() -> label.setForeground(Theme.text()));

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /**
     * WhatsApp muestra teléfono, correo electrónico muestra la dirección y la opción combinada
     * muestra ambos.
     */
    private void updateTargetVisibility(int selectedChannelIndex) {
        boolean showPhone = selectedChannelIndex == 0 || selectedChannelIndex == 2;
        boolean showEmail = selectedChannelIndex == 1 || selectedChannelIndex == 2;

        phoneRow.setVisible(showPhone);
        emailRow.setVisible(showEmail);
        revalidate();
        repaint();
    }

    /**
     * Valida el destino según el canal elegido y manda el Reminder al controlador.
     */
    private void createReminder(int selectedChannelIndex) {
        Task selectedTask = (Task) taskComboBox.getSelectedItem();
        if (selectedTask == null) {
            UiKit.showWarning(this, "Tarea requerida", "Seleccioná una tarea para este recordatorio.");
            return;
        }

        NotificationChannel channel = CHANNEL_VALUES[selectedChannelIndex];
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();

        // WhatsApp y "Both" necesitan teléfono.
        if ((channel == NotificationChannel.WHATSAPP || channel == NotificationChannel.AMBOS)
                && phone.isEmpty()) {
            markInvalid(phoneField);
            UiKit.showWarning(this, "Teléfono requerido", "Ingresá un número de teléfono para WhatsApp.");
            return;
        }
        clearInvalid(phoneField);

        // El correo electrónico y la opción combinada necesitan una dirección.
        if ((channel == NotificationChannel.EMAIL || channel == NotificationChannel.AMBOS)
                && email.isEmpty()) {
            markInvalid(emailField);
            UiKit.showWarning(this, "Correo electrónico requerido", "Ingresá una dirección de correo.");
            return;
        }
        clearInvalid(emailField);

        // Combina el día seleccionado con la hora elegida.
        LocalTime time = UiKit.toLocalTime((Date) timeSpinner.getValue());
        LocalDateTime scheduledAt = LocalDateTime.of(selectedDate, time);
        Reminder reminder = new Reminder(
                null,
                selectedTask.getId(),
                scheduledAt,
                channel,
                channel == NotificationChannel.EMAIL ? null : phone,
                channel == NotificationChannel.WHATSAPP ? null : email,
                false
        );

        try {
            createdReminder = reminderController.create(reminder);
            dispose();
        } catch (RuntimeException exception) {
            UiKit.showError(this, "No se pudo crear el recordatorio", exception);
        }
    }
}
