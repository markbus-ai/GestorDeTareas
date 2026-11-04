package com.trullo.frontend;

import com.trullo.controller.TaskController;
import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

/**
 * Formulario modal para crear una tarea. Habla directo con el controlador, así que si la
 * validación falla el diálogo queda abierto y el usuario no pierde lo que escribió.
 */
public final class NewTaskDialog extends BaseDialog {

    // Textos en español que se muestran en el combo de estado...
    private static final String[] STATUS_LABELS = {
            "Pendiente", "En progreso", "Completada", "Atrasada"
    };
    // ...y su equivalente en el enum del backend.
    private static final TaskStatus[] STATUS_VALUES = {
            TaskStatus.PENDIENTE,
            TaskStatus.EN_PROGRESO,
            TaskStatus.COMPLETADA,
            TaskStatus.ATRASADA
    };

    // Lo mismo para las prioridades.
    private static final String[] PRIORITY_LABELS = {
            "Baja", "Media", "Alta", "Urgente"
    };
    private static final TaskPriority[] PRIORITY_VALUES = {
            TaskPriority.BAJA,
            TaskPriority.MEDIA,
            TaskPriority.ALTA,
            TaskPriority.URGENTE
    };

    private final TaskController taskController;
    private final JTextField titleField;
    private final JTextField descriptionField;
    // Tarea creada, para devolverla a TasksScreen cuando el diálogo se cierra.
    private Task createdTask;

    /**
     * Construye el formulario completo: título, descripción, estado, prioridad y fecha límite.
     */
    private NewTaskDialog(Window owner, TaskController taskController) {
        super(owner, "Nueva tarea", 560, 560);
        this.taskController = taskController;

        titleField = formTextField("¿Qué hay que hacer?");
        descriptionField = formTextField("Una descripción breve");

        addFormField("Título", titleField);
        addFormField("Descripción (opcional)", descriptionField);

        // Estado y prioridad van lado a lado para ahorrar altura.
        JPanel statusAndPriorityRow = new JPanel(new GridLayout(1, 2, 12, 0));
        statusAndPriorityRow.setOpaque(false);
        statusAndPriorityRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusAndPriorityRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));

        JComboBox<String> statusComboBox = formComboBox(STATUS_LABELS);
        statusComboBox.setSelectedIndex(0);
        JComboBox<String> priorityComboBox = formComboBox(PRIORITY_LABELS);
        priorityComboBox.setSelectedIndex(1);

        statusAndPriorityRow.add(createFieldColumn("Estado", statusComboBox));
        statusAndPriorityRow.add(createFieldColumn("Prioridad", priorityComboBox));
        formPanel.add(statusAndPriorityRow);
        formPanel.add(javax.swing.Box.createVerticalStrut(14));

        // Checkbox que habilita o deshabilita el selector de fecha.
        JCheckBox dueDateCheckBox = new JCheckBox("Establecer fecha límite");
        dueDateCheckBox.setOpaque(false);
        dueDateCheckBox.setFocusPainted(false);
        dueDateCheckBox.setFont(UiKit.font(Font.PLAIN, 13));
        dueDateCheckBox.setForeground(Theme.text());
        dueDateCheckBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Theme.addListener(() -> dueDateCheckBox.setForeground(Theme.text()));

        JSpinner dueDateSpinner = new JSpinner(new SpinnerDateModel());
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "dd/MM/yyyy"));
        dueDateSpinner.setFont(UiKit.font(Font.PLAIN, 13));
        dueDateSpinner.setEnabled(false);
        dueDateSpinner.setPreferredSize(new Dimension(150, 34));

        dueDateCheckBox.addActionListener(actionEvent ->
                dueDateSpinner.setEnabled(dueDateCheckBox.isSelected())
        );

        JPanel dueDateRow = new JPanel(new BorderLayout(12, 0));
        dueDateRow.setOpaque(false);
        dueDateRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dueDateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        dueDateRow.add(dueDateCheckBox, BorderLayout.CENTER);
        dueDateRow.add(dueDateSpinner, BorderLayout.EAST);

        formPanel.add(formLabel("Fecha límite (opcional)"));
        formPanel.add(javax.swing.Box.createVerticalStrut(8));
        formPanel.add(dueDateRow);

        // Botones del pie: Cancelar cierra y Crear arma la tarea.
        JPanel footer = formFooter();
        JButton cancelButton = secondaryAction("Cancelar");
        JButton createButton = primaryAction("Crear tarea");
        cancelButton.addActionListener(actionEvent -> dispose());
        createButton.addActionListener(actionEvent -> createTask(
                statusComboBox,
                priorityComboBox,
                dueDateCheckBox,
                dueDateSpinner
        ));

        footer.add(cancelButton);
        footer.add(createButton);
        setFooter(footer);
        getRootPane().setDefaultButton(createButton);
    }

    /**
     * Abre el diálogo de forma modal y devuelve la tarea creada (si el usuario la creó).
     */
    public static Optional<Task> open(Component parent, TaskController taskController) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        NewTaskDialog dialog = new NewTaskDialog(owner, taskController);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.createdTask);
    }

    /**
     * Arma una columna con label + combo (usada para Estado y Prioridad).
     */
    private JPanel createFieldColumn(String labelText, JComboBox<String> comboBox) {
        JPanel column = new JPanel();
        column.setLayout(new javax.swing.BoxLayout(column, javax.swing.BoxLayout.Y_AXIS));
        column.setOpaque(false);
        column.add(formLabel(labelText));
        column.add(javax.swing.Box.createVerticalStrut(8));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        column.add(comboBox);
        return column;
    }

    /**
     * Valida los datos del formulario, crea el modelo Task y lo manda al controlador.
     */
    private void createTask(JComboBox<String> statusComboBox,
                            JComboBox<String> priorityComboBox,
                            JCheckBox dueDateCheckBox,
                            JSpinner dueDateSpinner) {
        // El título es obligatorio: si está vacío, marca el campo y corta.
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        if (title.isEmpty()) {
            markInvalid(titleField);
            return;
        }
        clearInvalid(titleField);

        // El backend exige descripción, así que si el usuario no escribió nada se usa el título.
        String description = descriptionField.getText() == null
                ? ""
                : descriptionField.getText().trim();
        if (description.isEmpty()) {
            description = title;
        }

        // La fecha límite solo se lee si el checkbox está marcado.
        LocalDate dueDate = null;
        if (dueDateCheckBox.isSelected()) {
            Date selectedDate = (Date) dueDateSpinner.getValue();
            dueDate = UiKit.toLocalDate(selectedDate);
            if (dueDate.isBefore(LocalDate.now())) {
                UiKit.showWarning(
                        this,
                        "Fecha límite inválida",
                        "La fecha límite no puede ser anterior a hoy."
                );
                return;
            }
        }

        // Convierte los índices de los combos a los enums reales del backend.
        TaskStatus status = STATUS_VALUES[statusComboBox.getSelectedIndex()];
        TaskPriority priority = PRIORITY_VALUES[priorityComboBox.getSelectedIndex()];
        Task task = new Task(null, title, description, status, priority, dueDate, null);

        try {
            // El controlador valida y persiste la tarea; si sale bien, cierra el diálogo.
            createdTask = taskController.create(task);
            dispose();
        } catch (RuntimeException exception) {
            UiKit.showError(this, "No se pudo crear la tarea", exception);
        }
    }
}
