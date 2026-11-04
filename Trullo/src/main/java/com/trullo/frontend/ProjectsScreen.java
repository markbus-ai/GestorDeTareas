package com.trullo.frontend;

import com.trullo.controller.ProyectoController;
import com.trullo.controller.TaskController;
import com.trullo.model.Proyecto;
import com.trullo.model.Task;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de proyectos: lista de tarjetas y botón flotante para crear proyectos.
 */
public final class ProjectsScreen extends JPanel implements RefreshableScreen {

    // Controladores: proyectos para la lista y tareas para contar las de cada proyecto.
    private final ProyectoController proyectoController;
    private final TaskController taskController;
    // Panel donde se van agregando las ProjectCard.
    private final JPanel projectListPanel;

    // Últimos datos leídos de la base.
    private final List<Proyecto> projects = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();
    private String loadErrorMessage;

    /**
     * Arma el encabezado, la lista scrolleable y el FAB de creación.
     */
    public ProjectsScreen(ProyectoController proyectoController, TaskController taskController) {
        this.proyectoController = proyectoController;
        this.taskController = taskController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        JPanel headerPanel = UiKit.sectionHeader(
                "Proyectos",
                "Agrupá tus tareas y seguí el estado de cada proyecto."
        );
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Lista vertical de tarjetas, dentro de un scroll.
        projectListPanel = new JPanel();
        projectListPanel.setLayout(new javax.swing.BoxLayout(
                projectListPanel,
                javax.swing.BoxLayout.Y_AXIS
        ));
        projectListPanel.setOpaque(false);

        JScrollPane scrollPane = UiKit.scrollPane(projectListPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Zona inferior con el botón flotante para crear un proyecto nuevo.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        FloatingButton createProjectButton = new FloatingButton(
                Theme::accent,
                AppIcon.Glyph.PLUS,
                54,
                "Crear proyecto"
        );
        createProjectButton.addActionListener(actionEvent -> openNewProjectDialog());
        bottomPanel.add(createProjectButton);

        add(headerPanel, BorderLayout.NORTH);
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
        loadProjects();
    }

    /**
     * Pide proyectos y tareas a los controladores y guarda el error si la lectura falla.
     */
    private void loadProjects() {
        try {
            List<Proyecto> loadedProjects = proyectoController.findAll();
            List<Task> loadedTasks = taskController.findAll();
            projects.clear();
            projects.addAll(loadedProjects);
            tasks.clear();
            tasks.addAll(loadedTasks);
            loadErrorMessage = null;
        } catch (RuntimeException exception) {
            loadErrorMessage = exception.getMessage();
        }
        refreshList();
    }

    /**
     * Abre el diálogo de creación y, si devuelve un proyecto, lo agrega a la lista.
     */
    private void openNewProjectDialog() {
        NewProjectDialog.open(this, proyectoController).ifPresent(project -> {
            projects.add(project);
            refreshList();
        });
    }

    /**
     * Vacía la lista visual y la vuelve a armar con los proyectos que ya se trajeron.
     */
    private void refreshList() {
        projectListPanel.removeAll();

        if (loadErrorMessage != null) {
            addEmptyState("No se pudieron cargar los proyectos", loadErrorMessage);
        } else if (projects.isEmpty()) {
            addEmptyState("Todavía no hay proyectos", "Creá tu primer proyecto con el botón +.");
        } else {
            // Una tarjeta por proyecto, con un pequeño espacio entre ellas.
            for (Proyecto project : projects) {
                projectListPanel.add(new ProjectCard(
                        project,
                        countTasks(project),
                        proyectoController,
                        this::loadProjects
                ));
                projectListPanel.add(javax.swing.Box.createVerticalStrut(8));
            }
        }

        projectListPanel.revalidate();
        projectListPanel.repaint();
    }

    /**
     * Agrega a la lista un mensaje en lugar de las tarjetas.
     */
    private void addEmptyState(String headline, String message) {
        JPanel emptyState = UiKit.emptyState(headline, message);
        emptyState.setAlignmentX(Component.LEFT_ALIGNMENT);
        projectListPanel.add(emptyState);
    }

    /**
     * Cuenta las tareas asignadas a un proyecto.
     */
    private long countTasks(Proyecto project) {
        return tasks.stream()
                .filter(task -> project.getId().equals(task.getProjectId()))
                .count();
    }
}
