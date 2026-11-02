package com.trullo.frontend;

import com.trullo.controller.TaskController;
import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;

import javax.swing.JLabel;
import javax.swing.JPanel;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Representación visual de una tarea: checkbox redondo, título, prioridad y fecha límite.
 */
public final class TaskCard extends RoundedPanel {

    // Datos de la tarea y callback para avisar que la lista debe refrescarse.
    private final Task task;
    private final TaskController taskController;
    private final Runnable onChanged;
    private final JLabel titleLabel;
    private final RoundCheckBox completedCheckBox;

    /**
     * Recibe la tarea, el controlador y la acción a ejecutar cuando cambia el estado.
     */
    public TaskCard(Task task, TaskController taskController, Runnable onChanged) {
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

        // Lado derecho: etiqueta de prioridad y, si hay, la fecha límite.
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

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        applyCompletedStyle(completed);
        completedCheckBox.addActionListener(actionEvent -> toggleCompleted());
    }

    /**
     * Cambia el estado en el backend y actualiza el estilo de la tarjeta.
     */
    private void toggleCompleted() {
        boolean completedNow = completedCheckBox.isSelected();
        TaskStatus newStatus = completedNow ? TaskStatus.COMPLETADA : TaskStatus.PENDIENTE;

        try {
            taskController.cambiarEstado(task.getId(), newStatus);
            task.setStatus(newStatus);
            applyCompletedStyle(completedNow);

            if (completedNow) {
                // Deja ver el tachado un momento antes de que la tarea desaparezca del filtro.
                Timer refreshTimer = new Timer(900, timerEvent -> onChanged.run());
                refreshTimer.setRepeats(false);
                refreshTimer.start();
            } else {
                onChanged.run();
            }
        } catch (RuntimeException exception) {
            // Si el backend falla, revierte el checkbox y muestra el error.
            completedCheckBox.setSelected(!completedNow);
            UiKit.showError(this, "No se pudo actualizar la tarea", exception);
        }
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
