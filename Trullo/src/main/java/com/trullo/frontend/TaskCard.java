package com.trullo.frontend;

import com.trullo.controller.TaskController;
import com.trullo.model.Proyecto;
import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Representación visual de una tarea: checkbox redondo, título, prioridad, fecha límite,
 * proyecto y estado.
 */
public final class TaskCard extends RoundedPanel {

    // Datos de la tarea y callback para avisar que la lista debe refrescarse.
    private final Task task;
    private final TaskController taskController;
    private final Runnable onChanged;
    private final JLabel titleLabel;
    private final RoundCheckBox completedCheckBox;
    private final JComboBox<String> statusComboBox;
    private final JComboBox<Proyecto> projectComboBox;

    /**
     * Recibe la tarea, los proyectos disponibles, el controlador y la acción a ejecutar cuando cambia el estado.
     */
    public TaskCard(Task task,
                    List<Proyecto> projects,
                    TaskController taskController,
                    Runnable onChanged) {
        super(new BorderLayout(12, 0), Theme::surface, UiKit.CARD_RADIUS);
        this.task = task;
        this.taskController = taskController;
        this.onChanged = onChanged;

        setBorder(new EmptyBorder(12, 16, 12, 16));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // El checkbox arranca marcado si la tarea ya está completada.
        completedCheckBox = new RoundCheckBox();
        boolean completed = task.getStatus() == TaskStatus.COMPLETADA;
        completedCheckBox.setSelected(completed);

        titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(UiKit.font(Font.PLAIN, 14));
        titleLabel.setForeground(Theme.text());
        // La descripción se muestra como tooltip para no recargar la tarjeta.
        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            titleLabel.setToolTipText(task.getDescription());
        }

        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(completedCheckBox, BorderLayout.WEST);
        leftPanel.add(titleLabel, BorderLayout.CENTER);

        // Lado derecho: prioridad, fecha límite si hay, proyecto y estado.
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        Color priorityColor = priorityColor(task.getPriority());
        rightPanel.add(new TagLabel(
                priorityLabel(task.getPriority()),
                Theme.withAlpha(priorityColor, 38),
                priorityColor
        ));

        if (task.getDueDate() != null) {
            boolean overdue = task.getDueDate().isBefore(LocalDate.now()) && !completed;
            Color dueDateForeground = overdue ? Theme.DANGER : Theme.INFO;
            Color dueDateBackground = overdue
                    ? Theme.withAlpha(Theme.DANGER, 42)
                    : Theme.deadlineBackground();
            rightPanel.add(new TagLabel(
                    "Vence " + UiKit.formatDate(task.getDueDate()),
                    dueDateBackground,
                    dueDateForeground
            ));
        }

        projectComboBox = UiKit.projectComboBox(projects, task.getProjectId());
        projectComboBox.setPreferredSize(new Dimension(150, 34));
        projectComboBox.setToolTipText("Proyecto de la tarea");
        projectComboBox.addActionListener(actionEvent -> changeProject());
        rightPanel.add(projectComboBox);

        statusComboBox = UiKit.comboBox(NewTaskDialog.STATUS_LABELS);
        statusComboBox.setPreferredSize(new Dimension(130, 34));
        statusComboBox.setToolTipText("Estado de la tarea");
        statusComboBox.setSelectedIndex(statusIndex(task.getStatus()));
        statusComboBox.addActionListener(actionEvent -> changeStatus(
                NewTaskDialog.STATUS_VALUES[statusComboBox.getSelectedIndex()]
        ));
        rightPanel.add(statusComboBox);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        applyCompletedStyle(completed);
        completedCheckBox.addActionListener(actionEvent -> changeStatus(
                completedCheckBox.isSelected() ? TaskStatus.COMPLETADA : TaskStatus.PENDIENTE
        ));
    }

    /**
     * Cambia el estado en el backend y sincroniza el checkbox, el combo y el estilo.
     */
    private void changeStatus(TaskStatus newStatus) {
        // Sincronizar el combo vuelve a disparar su listener: ahí no hay nada que cambiar.
        if (newStatus == task.getStatus()) {
            return;
        }

        try {
            taskController.cambiarEstado(task.getId(), newStatus);
            task.setStatus(newStatus);
            syncStatusControls();

            if (newStatus == TaskStatus.COMPLETADA) {
                // Deja ver el tachado un momento antes de que la tarea desaparezca del filtro.
                Timer refreshTimer = new Timer(900, timerEvent -> onChanged.run());
                refreshTimer.setRepeats(false);
                refreshTimer.start();
            } else {
                // Espera a que termine el evento del control antes de rehacer las tarjetas.
                SwingUtilities.invokeLater(onChanged);
            }
        } catch (RuntimeException exception) {
            // Si el backend falla, revierte los controles y muestra el error.
            syncStatusControls();
            UiKit.showError(this, "No se pudo actualizar la tarea", exception);
        }
    }

    /**
     * Asigna la tarea al proyecto elegido; si falla, vuelve el combo al proyecto anterior.
     */
    private void changeProject() {
        Long newProjectId = UiKit.selectedProjectId(projectComboBox);
        // Revertir el combo vuelve a disparar su listener: ahí no hay nada que cambiar.
        if (Objects.equals(newProjectId, task.getProjectId())) {
            return;
        }

        try {
            taskController.asignarProyecto(task.getId(), newProjectId);
            task.setProjectId(newProjectId);
        } catch (RuntimeException exception) {
            selectCurrentProject();
            UiKit.showError(this, "No se pudo asignar el proyecto", exception);
        }
    }

    /**
     * Deja seleccionado en el combo el proyecto que la tarea tiene guardado.
     */
    private void selectCurrentProject() {
        for (int index = 0; index < projectComboBox.getItemCount(); index++) {
            Proyecto project = projectComboBox.getItemAt(index);
            if (Objects.equals(project.getId(), task.getProjectId())) {
                projectComboBox.setSelectedIndex(index);
                return;
            }
        }
    }

    /**
     * Hace que el checkbox, el combo y el estilo reflejen el estado actual de la tarea.
     */
    private void syncStatusControls() {
        boolean completed = task.getStatus() == TaskStatus.COMPLETADA;
        completedCheckBox.setSelected(completed);
        statusComboBox.setSelectedIndex(statusIndex(task.getStatus()));
        applyCompletedStyle(completed);
    }

    /**
     * Devuelve la posición del estado dentro del combo.
     */
    private int statusIndex(TaskStatus status) {
        return Arrays.asList(NewTaskDialog.STATUS_VALUES).indexOf(status);
    }

    /**
     * Aplica tachado y color atenuado cuando la tarea está completada.
     */
    private void applyCompletedStyle(boolean completed) {
        Font baseFont = UiKit.font(Font.PLAIN, 14);
        if (completed) {
            Map<TextAttribute, Object> attributes = new HashMap<>(baseFont.getAttributes());
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            titleLabel.setFont(baseFont.deriveFont(attributes));
            titleLabel.setForeground(Theme.mutedText());
        } else {
            titleLabel.setFont(baseFont);
            titleLabel.setForeground(Theme.text());
        }
    }

    /**
     * Traduce la prioridad del backend a un color de la paleta.
     */
    private Color priorityColor(TaskPriority priority) {
        return switch (priority) {
            case BAJA -> Theme.mutedText();
            case MEDIA -> Theme.INFO;
            case ALTA -> Theme.WARNING;
            case URGENTE -> Theme.DANGER;
        };
    }

    /**
     * Traduce la prioridad del backend a la etiqueta en español que ve el usuario.
     */
    private String priorityLabel(TaskPriority priority) {
        return switch (priority) {
            case BAJA -> "Baja";
            case MEDIA -> "Media";
            case ALTA -> "Alta";
            case URGENTE -> "Urgente";
        };
    }
}
