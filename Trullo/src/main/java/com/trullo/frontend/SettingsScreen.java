package com.trullo.frontend;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pantalla de configuración: apariencia, privacidad y creadores.
 *
 * <p>Las preferencias que solo afectan a esta aplicación se manejan localmente.
 */
public final class SettingsScreen extends JPanel {

    private final JLabel themeStateLabel;

    /**
     * Construye la pantalla: encabezado, tarjetas de configuración dentro de un scroll y pie con
     * la versión.
     */
    public SettingsScreen() {
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);
        setBorder(new EmptyBorder(36, 48, 18, 48));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(UiKit.sectionHeader(
                "Configuración",
                "Personalizá Trullo y revisá cómo se maneja tu información."
        ));
        add(headerPanel, BorderLayout.NORTH);

        // Controles que después se reutilizan en las tarjetas.
        themeStateLabel = UiKit.themedLabel("", Font.PLAIN, 12, Theme::mutedText);

        // Las tarjetas se apilan verticalmente y se adaptan al ancho del scroll.
        ScrollablePanel cardsPanel = new ScrollablePanel(new GridBagLayout());
        cardsPanel.setOpaque(false);

        GridBagConstraints cardConstraints = new GridBagConstraints();
        cardConstraints.gridx = 0;
        cardConstraints.weightx = 1.0;
        cardConstraints.fill = GridBagConstraints.HORIZONTAL;
        cardConstraints.anchor = GridBagConstraints.NORTHWEST;
        cardConstraints.insets = new Insets(0, 0, 16, 0);

        cardConstraints.gridy = 0;
        cardsPanel.add(createAppearanceCard(), cardConstraints);

        cardConstraints.gridy = 1;
        cardsPanel.add(createPrivacyCard(), cardConstraints);

        cardConstraints.gridy = 2;
        cardsPanel.add(createCreatorsCard(), cardConstraints);

        JScrollPane scrollPane = UiKit.scrollPane(cardsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Pie con la versión de la aplicación.
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        footerPanel.add(UiKit.themedLabel(
                "Trullo · Versión 1.0.0",
                Font.PLAIN,
                11,
                Theme::mutedText
        ));
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea la tarjeta de apariencia con el switch de tema oscuro/claro y el texto que indica
     * cuál está activo.
     */
    private RoundedPanel createAppearanceCard() {
        JPanel controlPanel = new JPanel(new BorderLayout(16, 0));
        controlPanel.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(UiKit.themedLabel(
                "Modo oscuro",
                Font.BOLD,
                13,
                Theme::text
        ));
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(themeStateLabel);

        ThemeSwitch themeSwitch = new ThemeSwitch();
        controlPanel.add(textPanel, BorderLayout.CENTER);
        controlPanel.add(themeSwitch, BorderLayout.EAST);

        return createSettingsCard(
                AppIcon.Glyph.MOON,
                "Apariencia",
                "Alterná entre la paleta oscura y la clara.",
                controlPanel
        );
    }

    /**
     * Crea la tarjeta expandible de privacidad con tres párrafos que explican dónde se guardan
     * los datos y cómo se usan los destinos de los recordatorios.
     */
    private ExpandableSettingsCard createPrivacyCard() {
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.add(createTextBlock(
                "Trullo guarda tus tareas, notas y recordatorios en la base de datos local configurada "
                        + "para la aplicación. Nada se comparte con terceros.",
                4
        ));
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(createTextBlock(
                "Los recordatorios usan el canal de notificación que elegís, y el destinatario "
                        + "teléfono o dirección de correo solo se usa para enviar ese recordatorio.",
                4
        ));
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(createTextBlock(
                "Podés eliminar tareas, notas y recordatorios en cualquier momento desde sus pantallas. "
                        + "Las preferencias de esta página solo afectan a este dispositivo.",
                4
        ));

        return new ExpandableSettingsCard(
                AppIcon.Glyph.SHIELD,
                "Privacidad y datos",
                "Cómo Trullo maneja la información que creás.",
                bodyPanel
        );
    }

    /**
     * Crea la tarjeta expandible de creadores con una fila por persona o comunidad.
     */
    private ExpandableSettingsCard createCreatorsCard() {
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.add(UiKit.themedLabel(
                "Trullo se construyó con dedicación por:",
                Font.PLAIN,
                12,
                Theme::mutedText
        ));
        bodyPanel.add(Box.createVerticalStrut(12));
        bodyPanel.add(createCreatorRow(
                "Nahuel Romero",
                "Arquitectura backend, persistencia y servicios."
        ));
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(createCreatorRow(
                "Santiago Vera",
                "Diseño frontend y experiencia de usuario."
        ));
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(createCreatorRow(
                "Marcos Bustos",
                "Backend y optimización de rendimiento."
        ));

        return new ExpandableSettingsCard(
                AppIcon.Glyph.PEOPLE,
                "Creadores",
                "Las personas y comunidad detrás de Trullo.",
                bodyPanel
        );
    }

    /**
     * Arma la estructura común de una tarjeta de configuración: badge, título, descripción y el
     * control que recibe por parámetro.
     */
    private RoundedPanel createSettingsCard(AppIcon.Glyph glyph,
                                            String titleText,
                                            String descriptionText,
                                            JComponent control) {
        RoundedPanel card = new RoundedPanel(
                new BorderLayout(0, 16),
                Theme::surface,
                Theme::border,
                UiKit.CARD_RADIUS
        );
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel headerPanel = new JPanel(new BorderLayout(14, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(UiKit.iconBadge(glyph, 40, 20), BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(UiKit.themedLabel(titleText, Font.BOLD, 15, Theme::text));
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(UiKit.themedLabel(descriptionText, Font.PLAIN, 12, Theme::mutedText));
        headerPanel.add(textPanel, BorderLayout.CENTER);

        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(control, BorderLayout.CENTER);
        return card;
    }

    /**
     * Crea un área de texto de solo lectura para los párrafos de privacidad.
     * Queda suscripta al tema para actualizar fuente y color.
     */
    private JTextArea createTextBlock(String text, int rows) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(UiKit.font(Font.PLAIN, 12));
        textArea.setForeground(Theme.mutedText());
        textArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        textArea.setRows(rows);
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        textArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, textArea.getPreferredSize().height + 6));
        Theme.addListener(() -> {
            textArea.setFont(UiKit.font(Font.PLAIN, 12));
            textArea.setForeground(Theme.mutedText());
            textArea.setOpaque(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        });
        return textArea;
    }

    /**
     * Crea una fila de la tarjeta de creadores con un badge, el nombre y el rol.
     */
    private JPanel createCreatorRow(String name, String role) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.add(UiKit.iconBadge(AppIcon.Glyph.PEOPLE, 34, 16), BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(UiKit.themedLabel(name, Font.BOLD, 13, Theme::text));
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(UiKit.themedLabel(role, Font.PLAIN, 11, Theme::mutedText));
        row.add(textPanel, BorderLayout.CENTER);
        return row;
    }

    /**
     * Switch que cambia el tema global y se mantiene sincronizado si el tema cambia desde otro
     * lugar.
     */
    private final class ThemeSwitch extends JToggleButton {

        /**
         * Configura el tamaño, el estado inicial y los listeners del switch.
         */
        private ThemeSwitch() {
            setPreferredSize(new Dimension(54, 30));
            setMinimumSize(new Dimension(54, 30));
            setMaximumSize(new Dimension(54, 30));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setSelected(Theme.isDark());
            setToolTipText("Alternar modo oscuro");

            // Al hacer click, aplica el modo que corresponde al nuevo estado.
            addActionListener(actionEvent -> Theme.setMode(
                    isSelected() ? Theme.Mode.DARK : Theme.Mode.LIGHT
            ));

            Theme.addListener(() -> {
                boolean darkMode = Theme.isDark();
                if (isSelected() != darkMode) {
                    setSelected(darkMode);
                }
                themeStateLabel.setText(
                        darkMode ? "Modo oscuro activado" : "Modo claro activado"
                );
                repaint();
            });

            themeStateLabel.setText(
                    Theme.isDark() ? "Modo oscuro activado" : "Modo claro activado"
            );
        }

        /**
         * Dibuja el riel y la bolita del switch según el estado y el tema activo.
         */
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int trackWidth = 52;
            int trackHeight = 28;
            int trackX = (getWidth() - trackWidth) / 2;
            int trackY = (getHeight() - trackHeight) / 2;

            graphics2D.setColor(isSelected() ? Theme.accent() : Theme.switchOff());
            graphics2D.fillRoundRect(
                    trackX,
                    trackY,
                    trackWidth,
                    trackHeight,
                    trackHeight,
                    trackHeight
            );

            int knobDiameter = 20;
            int knobX = isSelected()
                    ? trackX + trackWidth - knobDiameter - 4
                    : trackX + 4;
            int knobY = trackY + (trackHeight - knobDiameter) / 2;

            graphics2D.setColor(new Color(0, 0, 0, 45));
            graphics2D.fillOval(knobX, knobY + 1, knobDiameter, knobDiameter);
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillOval(knobX, knobY, knobDiameter, knobDiameter);
            graphics2D.dispose();
        }
    }

    /**
     * Tarjeta de configuración cuyo cuerpo se muestra recién cuando se hace click en el
     * encabezado (privacidad y creadores).
     */
    private final class ExpandableSettingsCard extends RoundedPanel {

        private final JPanel bodyWrapper;
        private final JLabel chevronLabel;
        private boolean expanded;

        /**
         * Arma el encabezado clickeable, el cuerpo oculto y el listener de apertura.
         */
        private ExpandableSettingsCard(AppIcon.Glyph glyph,
                                       String titleText,
                                       String descriptionText,
                                       JPanel bodyContent) {
            super(new BorderLayout(), Theme::surface, Theme::border, UiKit.CARD_RADIUS);
            setBorder(new EmptyBorder(18, 22, 18, 22));

            JPanel headerPanel = new JPanel(new BorderLayout(14, 0));
            headerPanel.setOpaque(false);
            headerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JPanel iconBadge = UiKit.iconBadge(glyph, 40, 20);
            headerPanel.add(iconBadge, BorderLayout.WEST);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            JLabel titleLabel = UiKit.themedLabel(titleText, Font.BOLD, 15, Theme::text);
            JLabel descriptionLabel = UiKit.themedLabel(
                    descriptionText,
                    Font.PLAIN,
                    12,
                    Theme::mutedText
            );
            textPanel.add(titleLabel);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(descriptionLabel);
            headerPanel.add(textPanel, BorderLayout.CENTER);

            chevronLabel = new JLabel(new AppIcon(
                    AppIcon.Glyph.CHEVRON_DOWN,
                    16,
                    Theme::mutedText
            ));
            chevronLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            headerPanel.add(chevronLabel, BorderLayout.EAST);

            // Cualquier click con el botón izquierdo sobre el encabezado abre o cierra.
            MouseAdapter toggleHandler = new MouseAdapter() {
                /**
                 * Abre o cierra la tarjeta solo con el botón izquierdo.
                 */
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getButton() == MouseEvent.BUTTON1) {
                        toggleExpanded();
                    }
                }
            };
            installToggleHandler(headerPanel, toggleHandler);
            installToggleHandler(textPanel, toggleHandler);
            installToggleHandler(titleLabel, toggleHandler);
            installToggleHandler(descriptionLabel, toggleHandler);
            installToggleHandler(chevronLabel, toggleHandler);
            installToggleHandler(iconBadge, toggleHandler);

            // Cuerpo que arranca oculto hasta que se expande la tarjeta.
            bodyWrapper = new JPanel();
            bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
            bodyWrapper.setOpaque(false);
            bodyWrapper.setBorder(new EmptyBorder(14, 0, 0, 0));
            bodyWrapper.add(bodyContent);
            bodyWrapper.setVisible(false);

            add(headerPanel, BorderLayout.NORTH);
            add(bodyWrapper, BorderLayout.CENTER);
            Theme.addListener(this::repaint);
        }

        /**
         * Agrega el mismo listener de click y el cursor de mano a cada parte del encabezado.
         */
        private void installToggleHandler(Component component, MouseAdapter handler) {
            component.addMouseListener(handler);
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        /**
         * Muestra u oculta el cuerpo de la tarjeta y cambia la flecha del encabezado.
         */
        private void toggleExpanded() {
            expanded = !expanded;
            bodyWrapper.setVisible(expanded);
            chevronLabel.setIcon(new AppIcon(
                    expanded ? AppIcon.Glyph.CHEVRON_UP : AppIcon.Glyph.CHEVRON_DOWN,
                    16,
                    Theme::mutedText
            ));
            revalidate();
            repaint();
        }
    }
}
