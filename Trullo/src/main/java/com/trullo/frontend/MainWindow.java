package com.trullo.frontend;

import com.trullo.controller.NoteController;
import com.trullo.controller.ReminderController;
import com.trullo.controller.TaskController;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

/**
 * Ventana principal de la aplicación. Es la dueña de la barra lateral, del contenedor de
 * pantallas que muestra una a la vez y del cableado de navegación. Las pantallas se crean una sola
 * vez y se refrescan cuando vuelven a mostrarse.
 */
public final class MainWindow extends JFrame {

    // El contenedor de pantallas las mantiene apiladas y muestra una por vez.
    private final CardLayout cardLayout = new CardLayout();
    // Contenedor central: pinta el fondo del tema detrás de la pantalla activa.
    private final JPanel screenContainer = new JPanel(cardLayout) {
        /**
         * Pinta el fondo del contenedor con el color de fondo del tema.
         */
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setColor(Theme.background());
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
            graphics2D.dispose();
        }
    };
    // Relaciona cada sección del menú con su pantalla para poder refrescarla.
    private final Map<NavigationSection, JPanel> screens = new EnumMap<>(NavigationSection.class);
    private final Sidebar sidebar;

    /**
     * Recibe los controladores ya armados y construye todas las pantallas.
     */
    public MainWindow(TaskController taskController,
                      NoteController noteController,
                      ReminderController reminderController) {
        super("Trullo · Gestor de tareas personal");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setSize(1440, 860);
        setLocationRelativeTo(null);
        setIconImage(BrandIcon.image(64));

        // Se registra antes de crear las pantallas para que el árbol de componentes se
        // actualice primero y después cada pantalla reaplique sus propios colores.
        Theme.addListener(this::refreshAfterThemeChange);

        sidebar = new Sidebar(this::showSection);
        screenContainer.setOpaque(false);

        /**
         * Cada pantalla recibe solo los controladores que realmente necesita.
         */
        registerScreen(NavigationSection.HOME, new WelcomeScreen(this::showSection));
        registerScreen(NavigationSection.TASKS, new TasksScreen(taskController));
        registerScreen(
                NavigationSection.CALENDAR,
                new CalendarScreen(reminderController, taskController)
        );
        registerScreen(NavigationSection.NOTES, new NotesScreen(noteController));
        registerScreen(NavigationSection.SETTINGS, new SettingsScreen());

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(screenContainer, BorderLayout.CENTER);
        getContentPane().setBackground(Theme.background());

        /**
         * La app siempre arranca en la pantalla de bienvenida.
         */
        showSection(NavigationSection.HOME);
    }

    /**
     * Guarda la pantalla en el mapa y la agrega al contenedor con el nombre de la sección.
     */
    private void registerScreen(NavigationSection section, JPanel screen) {
        screens.put(section, screen);
        screenContainer.add(screen, section.name());
    }

    /**
     * Muestra la sección pedida, marca el botón activo en la barra lateral y, si la pantalla
     * implementa RefreshableScreen, le pide que recargue sus datos (tareas, notas o recordatorios).
     */
    public void showSection(NavigationSection section) {
        if (section == null) {
            return;
        }

        sidebar.setActiveSection(section);
        cardLayout.show(screenContainer, section.name());

        JPanel screen = screens.get(section);
        if (screen instanceof RefreshableScreen refreshableScreen) {
            refreshableScreen.onShown();
        }

        screenContainer.revalidate();
        screenContainer.repaint();
    }

    /**
     * Se ejecuta cuando Theme cambia: reaplica el estilo visual y repinta toda la ventana.
     */
    private void refreshAfterThemeChange() {
        Theme.applyLookAndFeel();
        SwingUtilities.updateComponentTreeUI(this);
        getContentPane().setBackground(Theme.background());
        screenContainer.repaint();
        sidebar.repaint();
        repaint();
    }
}
