package com.trullo.frontend;

import com.trullo.controller.ProyectoController;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Window;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Formulario modal para crear un proyecto. Si la validación falla, el diálogo queda abierto y el
 * usuario no pierde lo que escribió.
 */
public final class NewProjectDialog extends BaseDialog {

    // Textos en español del estado y su equivalente en el enum del backend.
    static final String[] STATUS_LABELS = {
            "Activo", "Pausado", "Finalizado", "Cancelado"
    };
    static final EstadoProyecto[] STATUS_VALUES = {
            EstadoProyecto.ACTIVO,
            EstadoProyecto.PAUSADO,
            EstadoProyecto.FINALIZADO,
            EstadoProyecto.CANCELADO
    };

    private final ProyectoController proyectoController;
    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JComboBox<String> statusComboBox;
    private final OptionalDateField dueDateField;
    // Proyecto creado, para devolverlo a ProjectsScreen cuando el diálogo se cierra.
    private Proyecto createdProject;

    /**
     * Construye el formulario: nombre, descripción, estado y fecha límite.
     */
    private NewProjectDialog(Window owner, ProyectoController proyectoController) {
        super(owner, "Nuevo proyecto", 560, 500);
        this.proyectoController = proyectoController;

        nameField = formTextField("¿Cómo se llama el proyecto?");
        descriptionField = formTextField("Una descripción breve");
        statusComboBox = formComboBox(STATUS_LABELS);
        dueDateField = new OptionalDateField("Establecer fecha límite");

        addFormField("Nombre", nameField);
        addFormField("Descripción (opcional)", descriptionField);
        addFormField("Estado", statusComboBox);
        addFormField("Fecha límite (opcional)", dueDateField);

        // Botones del pie: Cancelar cierra y Crear arma el proyecto.
        JPanel footer = formFooter();
        JButton cancelButton = secondaryAction("Cancelar");
        JButton createButton = primaryAction("Crear proyecto");
        cancelButton.addActionListener(actionEvent -> dispose());
        createButton.addActionListener(actionEvent -> createProject());

        footer.add(cancelButton);
        footer.add(createButton);
        setFooter(footer);
        getRootPane().setDefaultButton(createButton);
    }

    /**
     * Abre el diálogo de forma modal y devuelve el proyecto creado (si el usuario lo creó).
     */
    public static Optional<Proyecto> open(Component parent, ProyectoController proyectoController) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        NewProjectDialog dialog = new NewProjectDialog(owner, proyectoController);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.createdProject);
    }

    /**
     * Valida los datos del formulario, crea el modelo Proyecto y lo manda al controlador.
     */
    private void createProject() {
        // El nombre es obligatorio: si está vacío, marca el campo y corta.
        String name = trimmedText(nameField);
        if (name.isEmpty()) {
            markInvalid(nameField);
            return;
        }
        clearInvalid(nameField);

        LocalDate dueDate = dueDateField.getDate();
        if (warnIfPastDate(dueDate)) {
            return;
        }

        EstadoProyecto status = STATUS_VALUES[statusComboBox.getSelectedIndex()];
        Proyecto project = new Proyecto(null, name, trimmedText(descriptionField), status, dueDate);

        try {
            createdProject = proyectoController.create(project);
            dispose();
        } catch (RuntimeException exception) {
            UiKit.showError(this, "No se pudo crear el proyecto", exception);
        }
    }
}
