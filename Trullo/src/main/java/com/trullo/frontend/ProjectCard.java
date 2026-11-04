package com.trullo.frontend;

import com.trullo.controller.ProyectoController;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;

/**
 * Representación visual de un proyecto: nombre, descripción, cantidad de tareas, fecha límite,
 * selector de estado y botón para eliminarlo.
 */
public final class ProjectCard extends RoundedPanel {

    private final Proyecto project;
    private final ProyectoController proyectoController;
    private final Runnable onDeleted;
    private final JComboBox<String> statusComboBox;

    /**
     * Recibe el proyecto, cuántas tareas tiene, el controlador y la acción a ejecutar al eliminarlo.
     */
    public ProjectCard(Proyecto project,
                       long taskCount,
                       ProyectoController proyectoController,
                       Runnable onDeleted) {
        super(new BorderLayout(14, 0), Theme::surface, UiKit.CARD_RADIUS);
        this.project = project;
        this.proyectoController = proyectoController;
        this.onDeleted = onDeleted;

        setBorder(new EmptyBorder(12, 16, 12, 12));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Lado derecho: cantidad de tareas, fecha límite, estado y eliminar.
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(new TagLabel(
                taskCount == 1 ? "1 tarea" : taskCount + " tareas",
                Theme.withAlpha(Theme.INFO, 38),
                Theme.INFO
        ));
        if (project.getDueDate() != null) {
            rightPanel.add(new TagLabel(
                    "Vence " + UiKit.formatDate(project.getDueDate()),
                    Theme.deadlineBackground(),
                    Theme.INFO
            ));
        }

        statusComboBox = UiKit.comboBox(NewProjectDialog.STATUS_LABELS);
        statusComboBox.setPreferredSize(new Dimension(130, 34));
        statusComboBox.setToolTipText("Estado del proyecto");
        statusComboBox.setSelectedIndex(statusIndex(project.getStatus()));
        statusComboBox.addActionListener(actionEvent -> changeStatus());
        rightPanel.add(statusComboBox);

        JButton deleteButton = UiKit.iconButton(AppIcon.Glyph.CLOSE, 16, Theme::mutedText);
        deleteButton.setToolTipText("Eliminar proyecto");
        deleteButton.addActionListener(actionEvent -> deleteProject());
        rightPanel.add(deleteButton);

        add(UiKit.iconBadge(AppIcon.Glyph.FOLDER, 40, 20), BorderLayout.WEST);
        add(createTextPanel(), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Crea el bloque de texto con el nombre y la descripción del proyecto.
     */
    private JPanel createTextPanel() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(UiKit.themedLabel(project.getName(), Font.BOLD, 14, Theme::text));
        textPanel.add(Box.createVerticalStrut(3));

        boolean hasDescription = project.getDescription() != null
                && !project.getDescription().isBlank();
        JLabel descriptionLabel = UiKit.themedLabel(
                hasDescription ? project.getDescription() : "Sin descripción",
                Font.PLAIN,
                12,
                Theme::mutedText
        );
        if (hasDescription) {
            descriptionLabel.setToolTipText(project.getDescription());
        }
        textPanel.add(descriptionLabel);
        return textPanel;
    }

    /**
     * Cambia el estado en el backend; si falla, vuelve el combo al estado anterior.
     */
    private void changeStatus() {
        EstadoProyecto newStatus = NewProjectDialog.STATUS_VALUES[statusComboBox.getSelectedIndex()];
        if (newStatus == project.getStatus()) {
            return;
        }

        try {
            proyectoController.cambiarEstado(project.getId(), newStatus);
            project.setStatus(newStatus);
        } catch (RuntimeException exception) {
            statusComboBox.setSelectedIndex(statusIndex(project.getStatus()));
            UiKit.showError(this, "No se pudo actualizar el proyecto", exception);
        }
    }

    /**
     * Pide confirmación y elimina el proyecto. Sus tareas se conservan sin proyecto.
     */
    private void deleteProject() {
        int answer = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar \"" + project.getName() + "\"? Sus tareas se conservan sin proyecto.",
                "Eliminar proyecto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            proyectoController.delete(project.getId());
            onDeleted.run();
        } catch (RuntimeException exception) {
            UiKit.showError(this, "No se pudo eliminar el proyecto", exception);
        }
    }

    /**
     * Devuelve la posición del estado dentro del combo.
     */
    private int statusIndex(EstadoProyecto status) {
        return Arrays.asList(NewProjectDialog.STATUS_VALUES).indexOf(status);
    }
}
