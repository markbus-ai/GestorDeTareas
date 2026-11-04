package com.trullo.frontend;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Barra lateral de navegación: identidad del producto, secciones principales y pie con la
 * versión. Es la encargada de avisar a MainWindow qué pantalla mostrar.
 */
public final class Sidebar extends JPanel {

    // Callback que MainWindow pasa para cambiar de pantalla.
    private final Consumer<NavigationSection> onNavigate;
    // Guarda el botón de cada sección para poder marcarlo como activo.
    private final Map<NavigationSection, SidebarButton> navigationButtons =
            new EnumMap<>(NavigationSection.class);

    /**
     * Arma la barra con tres zonas: marca arriba, navegación al centro y versión abajo.
     */
    public Sidebar(Consumer<NavigationSection> onNavigate) {
        this.onNavigate = onNavigate;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(UiKit.SIDEBAR_WIDTH, 0));

        add(createBrandPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Cuando cambia el tema, actualiza los botones y se vuelve a pintar.
        Theme.addListener(this::refreshTheme);
    }

    /**
     * Pinta el fondo de la barra y la línea divisoria con el borde derecho.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2D.setColor(Theme.sidebar());
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        graphics2D.setColor(Theme.border());
        graphics2D.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        graphics2D.dispose();
    }

    /**
     * Marca como activo el botón de la sección actual y desactiva el resto.
     */
    public void setActiveSection(NavigationSection section) {
        navigationButtons.forEach(
                (navigationSection, button) -> button.setActive(navigationSection == section)
        );
    }

    /**
     * Zona superior: ícono de la app, nombre "Trullo" y bajada "Productividad personal".
     */
    private JPanel createBrandPanel() {
        JPanel brandPanel = new JPanel(new BorderLayout(12, 0));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(26, 22, 24, 22));
        brandPanel.add(new JLabel(BrandIcon.at(42)), BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(UiKit.themedLabel("Trullo", Font.BOLD, 20, Theme::text));
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(UiKit.themedLabel(
                "Productividad personal",
                Font.PLAIN,
                11,
                Theme::mutedText
        ));
        brandPanel.add(textPanel, BorderLayout.CENTER);
        return brandPanel;
    }

    /**
     * Zona central: etiqueta "WORKSPACE" y un botón por cada valor de NavigationSection.
     */
    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setOpaque(false);
        navigationPanel.setBorder(new EmptyBorder(6, 14, 6, 14));

        JLabel sectionLabel = UiKit.themedLabel(
                "ESPACIO DE TRABAJO",
                Font.BOLD,
                10,
                Theme::mutedText
        );
        sectionLabel.setBorder(new EmptyBorder(0, 10, 10, 0));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navigationPanel.add(sectionLabel);

        for (NavigationSection section : NavigationSection.values()) {
            SidebarButton button = new SidebarButton(section);
            navigationButtons.put(section, button);
            navigationPanel.add(button);
            navigationPanel.add(Box.createVerticalStrut(4));
        }

        navigationPanel.add(Box.createVerticalGlue());
        return navigationPanel;
    }

    /**
     * Zona inferior: tarjeta con la versión de la aplicación.
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(16, 20, 22, 20));

        RoundedPanel footerCard = new RoundedPanel(
                new BorderLayout(0, 4),
                Theme::surfaceAlt,
                12
        );
        footerCard.setBorder(new EmptyBorder(10, 12, 10, 12));
        footerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        footerCard.add(UiKit.themedLabel("Trullo 1.0", Font.BOLD, 11, Theme::text), BorderLayout.NORTH);
        footerCard.add(UiKit.themedLabel(
                "Hecho con Java Swing y FlatLaf",
                Font.PLAIN,
                10,
                Theme::mutedText
        ), BorderLayout.CENTER);

        footerPanel.add(footerCard);
        return footerPanel;
    }

    /**
     * Reaplica el estilo de todos los botones y repinta la barra al cambiar el tema.
     */
    private void refreshTheme() {
        navigationButtons.values().forEach(SidebarButton::updateStyle);
        repaint();
    }

    /**
     * Entrada de navegación. La sección activa recibe un fondo suave de acento y una pequeña
     * barra vertical azul; al pasar el mouse se pinta un overlay más tenue.
     */
    private final class SidebarButton extends JButton {

        private final NavigationSection section;
        private boolean active;

        /**
         * Crea el botón con el ícono y el texto de la sección recibida.
         */
        private SidebarButton(NavigationSection section) {
            super(section.getTitle());
            this.section = section;

            setIcon(new AppIcon(
                    section.getIcon(),
                    18,
                    () -> active ? Theme.accent() : Theme.mutedText()
            ));
            setFont(UiKit.font(Font.BOLD, 13));
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(12);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 16, 0, 14));
            setPreferredSize(new Dimension(UiKit.SIDEBAR_WIDTH - 28, 44));
            setMinimumSize(new Dimension(0, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setToolTipText(section.getTitle());

            /**
             * Al hacer click, avisa a MainWindow qué sección se quiere mostrar.
             */
            addActionListener(actionEvent -> onNavigate.accept(section));
            updateStyle();
        }

        /**
         * Dibuja el fondo del ítem activo/hover y la barrita azul antes de pintar texto e ícono.
         */
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int arc = 12;
            if (active) {
                graphics2D.setColor(Theme.activeOverlay());
                graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else if (getModel().isRollover()) {
                graphics2D.setColor(Theme.hoverOverlay());
                graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            }

            if (active) {
                graphics2D.setColor(Theme.accent());
                graphics2D.fillRoundRect(0, (getHeight() - 18) / 2, 3, 18, 3, 3);
            }

            graphics2D.dispose();
            super.paintComponent(graphics);
        }

        /**
         * Cambia el estado activo y refresca los colores del botón.
         */
        private void setActive(boolean active) {
            this.active = active;
            updateStyle();
        }

        /**
         * Aplica tipografía, colores y medidas según el tema y si el botón está activo.
         */
        private void updateStyle() {
            setFont(UiKit.font(Font.BOLD, 13));
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(12);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 16, 0, 14));
            setPreferredSize(new Dimension(UiKit.SIDEBAR_WIDTH - 28, 44));
            setMinimumSize(new Dimension(0, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setForeground(active ? Theme.text() : Theme.mutedText());
            repaint();
        }
    }
}
