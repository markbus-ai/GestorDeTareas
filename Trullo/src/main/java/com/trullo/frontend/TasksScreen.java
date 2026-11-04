package com.trullo.frontend;

import com.trullo.controller.TaskController;
import com.trullo.model.Task;
import com.trullo.model.TaskStatus;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pantalla de tareas: filtros, lista de tarjetas y botón flotante para crear tareas.
 */
public final class TasksScreen extends JPanel implements RefreshableScreen {

    // Los tres filtros disponibles en la parte superior.
    private enum TaskFilter {
        ALL,
        PENDING,
        COMPLETED
    }

    // Controlador que habla con el backend de tareas.
    private final TaskController taskController;
    // Panel donde se van agregando las TaskCard.
    private final JPanel taskListPanel;
    private final FilterChip allFilterChip;
    private final FilterChip pendingFilterChip;
    private final FilterChip completedFilterChip;

    // Última lista leída de la base y filtro activo.
    private final List<Task> tasks = new ArrayList<>();
    private TaskFilter currentFilter = TaskFilter.ALL;
    private String loadErrorMessage;

    /**
     * Arma el encabezado, los filtros, la lista scrolleable y el FAB de creación.
     */
    public TasksScreen(TaskController taskController) {
        this.taskController = taskController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(UiKit.sectionHeader(
                "Mis tareas",
                "Planificá, priorizá y terminá lo que importa."
        ));
        topPanel.add(javax.swing.Box.createVerticalStrut(20));

        // Fila de chips: solo uno puede quedar seleccionado a la vez.
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        allFilterChip = new FilterChip("Todas");
        pendingFilterChip = new FilterChip("Pendientes");
        completedFilterChip = new FilterChip("Completadas");

        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(allFilterChip);
        filterGroup.add(pendingFilterChip);
        filterGroup.add(completedFilterChip);

        allFilterChip.setSelected(true);
        // Cada chip cambia el filtro actual y vuelve a dibujar la lista.
        allFilterChip.addActionListener(actionEvent -> {
            currentFilter = TaskFilter.ALL;
            refreshList();
        });
        pendingFilterChip.addActionListener(actionEvent -> {
            currentFilter = TaskFilter.PENDING;
            refreshList();
        });
        completedFilterChip.addActionListener(actionEvent -> {
            currentFilter = TaskFilter.COMPLETED;
            refreshList();
        });

        filterRow.add(allFilterChip);
        filterRow.add(pendingFilterChip);
        filterRow.add(completedFilterChip);
        topPanel.add(filterRow);

        // Lista vertical de tarjetas, dentro de un scroll.
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new javax.swing.BoxLayout(
                taskListPanel,
                javax.swing.BoxLayout.Y_AXIS
        ));
        taskListPanel.setOpaque(false);

        JScrollPane scrollPane = UiKit.scrollPane(taskListPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Zona inferior con el botón flotante para crear una tarea nueva.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        FloatingButton createTaskButton = new FloatingButton(
                Theme::accent,
                AppIcon.Glyph.PLUS,
                54,
                "Crear tarea"
        );
        createTaskButton.addActionListener(actionEvent -> openNewTaskDialog());
        bottomPanel.add(createTaskButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Al cambiar el tema se rehacen las tarjetas para que tomen los colores nuevos.
        Theme.addListener(() -> {
            refreshList();
            repaint();
        });
    }

    /**
     * MainWindow llama a esto cada vez que se muestra la pantalla.
     */
    @Override
    public void onShown() {
        loadTasks();
    }

    /**
     * Pide todas las tareas al TaskController y guarda el error si la lectura falla.
     */
    private void loadTasks() {
        try {
            tasks.clear();
            tasks.addAll(taskController.findAll());
            loadErrorMessage = null;
        } catch (RuntimeException exception) {
            loadErrorMessage = exception.getMessage();
        }
        refreshList();
    }

    /**
     * Abre el diálogo de creación y, si devuelve una tarea, la agrega y vuelve al filtro "Todas".
     */
    private void openNewTaskDialog() {
        Optional<Task> createdTask = NewTaskDialog.open(this, taskController);
        createdTask.ifPresent(task -> {
            tasks.add(task);
            currentFilter = TaskFilter.ALL;
            allFilterChip.setSelected(true);
            refreshList();
        });
    }

    /**
     * Vacía la lista visual y la vuelve a armar con las tareas que ya se trajeron del
     * TaskController, aplicando el filtro activo.
     */
    private void refreshList() {
        taskListPanel.removeAll();

        if (loadErrorMessage != null) {
            JPanel emptyState = UiKit.emptyState(
                    "No se pudieron cargar las tareas",
                    loadErrorMessage
            );
            emptyState.setAlignmentX(Component.LEFT_ALIGNMENT);
            taskListPanel.add(emptyState);
        } else {
            List<Task> visibleTasks = tasks.stream()
                    .filter(this::matchesCurrentFilter)
                    .toList();

            if (visibleTasks.isEmpty()) {
                // Distingue entre "no hay tareas" y "no hay tareas con este filtro".
                String headline = tasks.isEmpty() ? "Todavía no hay tareas" : "No hay nada acá";
                String message = tasks.isEmpty()
                        ? "Creá tu primera tarea con el botón +."
                        : "No hay tareas que coincidan con este filtro.";
                JPanel emptyState = UiKit.emptyState(headline, message);
                emptyState.setAlignmentX(Component.LEFT_ALIGNMENT);
                taskListPanel.add(emptyState);
            } else {
                // Una tarjeta por tarea, con un pequeño espacio entre ellas.
                for (Task task : visibleTasks) {
                    taskListPanel.add(new TaskCard(task, taskController, this::refreshList));
                    taskListPanel.add(javax.swing.Box.createVerticalStrut(8));
                }
            }
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    /**
     * Decide si una tarea entra en el filtro actual (completadas o no completadas).
     */
    private boolean matchesCurrentFilter(Task task) {
        return switch (currentFilter) {
            case ALL -> true;
            case PENDING -> task.getStatus() != TaskStatus.COMPLETADA;
            case COMPLETED -> task.getStatus() == TaskStatus.COMPLETADA;
        };
    }
}
