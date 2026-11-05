package com.trullo.frontend;

import com.trullo.controller.ReminderController;
import com.trullo.controller.ReminderTargetController;
import com.trullo.model.Reminder;
import com.trullo.model.Task;
import com.trullo.notification.email.EmailNotifier;
import com.trullo.notification.whatsapp.WhatsAppApiNotifier;

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

    // Etiquetas visibles de los canales; la posición decide a qué destinos se envía.
    private static final String[] CHANNEL_LABELS = {
            "WhatsApp", "Correo electrónico", "WhatsApp + Correo electrónico"
    };

    private static final int WHATSAPP_ONLY_INDEX = 0;
    private static final int EMAIL_ONLY_INDEX = 1;
    private static final int WHATSAPP_AND_EMAIL_INDEX = 2;

    private final ReminderController reminderController;
    private final ReminderTargetController reminderTargetController;
    private final LocalDate selectedDate;
    private final JComboBox<Task> taskComboBox;
    private final JSpinner timeSpinner;
    private final JTextField phoneField;
    private final JTextField emailField;
    private final JPanel phoneRow;
    private final JPanel emailRow;
    // Recordatorio creado, para devolverlo a CalendarScreen.
    private Reminder createdReminder;
    private boolean saving;
    private final JButton createButton;
    private final JButton cancelButton;

    /**
     * Arma el formulario: fecha, tarea, hora, canal y destino (teléfono y/o email).
     */
    private NewReminderDialog(Window owner,
                              ReminderController reminderController,
                              ReminderTargetController reminderTargetController,
                              LocalDate selectedDate,
                              List<Task> tasks) {
        super(owner, "Recordatorio", 560, 600);
        this.reminderController = reminderController;
        this.reminderTargetController = reminderTargetController;
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

        phoneField = formTextField("+5491112345678");
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
        cancelButton = secondaryAction("Cancelar");
        createButton = primaryAction("Crear recordatorio");
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
                                           ReminderTargetController reminderTargetController,
                                           LocalDate selectedDate,
                                           List<Task> tasks) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        NewReminderDialog dialog = new NewReminderDialog(
                owner,
                reminderController,
                reminderTargetController,
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
        phoneRow.setVisible(sendsWhatsApp(selectedChannelIndex));
        emailRow.setVisible(sendsEmail(selectedChannelIndex));
        revalidate();
        repaint();
    }

    /**
     * Indica si la opción elegida en el combo envía por WhatsApp.
     */
    private boolean sendsWhatsApp(int selectedChannelIndex) {
        return selectedChannelIndex == WHATSAPP_ONLY_INDEX
                || selectedChannelIndex == WHATSAPP_AND_EMAIL_INDEX;
    }

    /**
     * Indica si la opción elegida en el combo envía por correo electrónico.
     */
    private boolean sendsEmail(int selectedChannelIndex) {
        return selectedChannelIndex == EMAIL_ONLY_INDEX
                || selectedChannelIndex == WHATSAPP_AND_EMAIL_INDEX;
    }

    /**
     * Valida el destino según el canal elegido y guarda el Reminder con sus destinos.
     */
    private void createReminder(int selectedChannelIndex) {
        Task selectedTask = (Task) taskComboBox.getSelectedItem();
        if (selectedTask == null) {
            UiKit.showWarning(this, "Tarea requerida", "Seleccioná una tarea para este recordatorio.");
            return;
        }

        boolean sendsWhatsApp = sendsWhatsApp(selectedChannelIndex);
        boolean sendsEmail = sendsEmail(selectedChannelIndex);
        String phone = trimmedText(phoneField);
        String email = trimmedText(emailField);

        // WhatsApp y la opción combinada necesitan teléfono.
        if (sendsWhatsApp && phone.isEmpty()) {
            markInvalid(phoneField);
            UiKit.showWarning(this, "Teléfono requerido", "Ingresá un número de teléfono para WhatsApp.");
            return;
        }
        clearInvalid(phoneField);

        // El correo electrónico y la opción combinada necesitan una dirección.
        if (sendsEmail && email.isEmpty()) {
            markInvalid(emailField);
            UiKit.showWarning(this, "Correo electrónico requerido", "Ingresá una dirección de correo.");
            return;
        }
        clearInvalid(emailField);

        // Combina el día seleccionado con la hora elegida.
        LocalTime time = UiKit.toLocalTime((Date) timeSpinner.getValue());
        LocalDateTime scheduledAt = LocalDateTime.of(selectedDate, time);

        saveReminder(
                new Reminder(null, selectedTask.getId(), scheduledAt),
                sendsWhatsApp ? phone : null,
                sendsEmail ? email : null
        );
    }

    /**
     * Guarda el recordatorio y un destino por cada dirección informada (null significa que no
     * se envía por ese canal). Si un destino falla, deshace el recordatorio y muestra el error.
     */
    private void saveReminder(Reminder reminder, String phone, String email) {
        if (saving) return;
        saving = true;
        createButton.setEnabled(false);
        cancelButton.setEnabled(false);
        createButton.setText("Programando...");
        new javax.swing.SwingWorker<Reminder, Void>() {
            @Override
            protected Reminder doInBackground() {
                Reminder saved = null;
                try {
                    saved = reminderController.create(reminder);
                    // Crear primero los destinos locales; la solicitud remota va al final.
                    if (email != null) {
                        reminderTargetController.agregarDestino(saved.getId(), EmailNotifier.CHANNEL, email);
                    }
                    if (phone != null) {
                        reminderTargetController.agregarDestino(saved.getId(), WhatsAppApiNotifier.CHANNEL, phone);
                    }
                    return saved;
                } catch (RuntimeException exception) {
                    discardReminder(saved, exception);
                    throw exception;
                }
            }

            @Override
            protected void done() {
                saving = false;
                createButton.setEnabled(true);
                cancelButton.setEnabled(true);
                createButton.setText("Crear recordatorio");
                try {
                    createdReminder = get();
                    dispose();
                } catch (java.util.concurrent.ExecutionException exception) {
                    UiKit.showError(NewReminderDialog.this, "No se pudo crear el recordatorio", exception.getCause());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    UiKit.showError(NewReminderDialog.this, "Se interrumpio la programacion", exception);
                }
            }
        }.execute();
    }

    @Override
    public void dispose() {
        // No cerrar mientras el servidor puede estar aceptando el recordatorio.
        if (!saving) super.dispose();
    }

    /**
     * Borra el recordatorio si no se pudieron guardar sus destinos, para no dejarlo a medias.
     */
    private void discardReminder(Reminder reminder, RuntimeException cause) {
        if (reminder == null) {
            return;
        }
        try {
            reminderController.delete(reminder.getId());
        } catch (RuntimeException deleteFailure) {
            cause.addSuppressed(deleteFailure);
        }
    }
}
