package com.trullo.frontend;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Fábrica de componentes y helpers compartidos del lenguaje visual de la app. Tener estos
 * pedazos acá evita que cada pantalla repita colores, bordes y tipografías.
 */
public final class UiKit {

    // Fuente base de toda la interfaz.
    public static final String FONT_FAMILY = "Segoe UI";

    // Medidas estándar para mantener proporciones parejas entre pantallas.
    public static final int SIDEBAR_WIDTH = 260;
    public static final int CARD_RADIUS = 16;
    public static final int CONTROL_RADIUS = 12;
    public static final int SCREEN_PADDING = 40;
    public static final int CONTENT_GAP = 20;

    // Formatos usados para mostrar fechas y horas en las tarjetas.
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("es-ES"));
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("es-ES"));

    /**
     * Clase de utilidades: no se instancia.
     */
    private UiKit() {
    }

    /**
     * Crea una fuente de la familia base con el estilo (negrita, cursiva, etc.) y tamaño pedidos.
     */
    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    /**
     * Panel transparente con padding estándar para envolver el contenido de una pantalla.
     */
    public static JPanel screenContainer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(
                SCREEN_PADDING - 4,
                SCREEN_PADDING + 8,
                SCREEN_PADDING - 4,
                SCREEN_PADDING + 8
        ));
        return panel;
    }

    /**
     * Arma el encabezado de una pantalla: título grande y subtítulo explicativo.
     */
    public static JPanel sectionHeader(String titleText, String subtitleText) {
        JPanel header = new JPanel();
        header.setLayout(new javax.swing.BoxLayout(header, javax.swing.BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel titleLabel = title(titleText);
        JLabel subtitleLabel = subtitle(subtitleText);

        header.add(titleLabel);
        header.add(javax.swing.Box.createVerticalStrut(6));
        header.add(subtitleLabel);

        return header;
    }

    /**
     * Título principal, en negrita y con el color de texto del tema.
     */
    public static JLabel title(String text) {
        return themedLabel(text, Font.BOLD, 26, Theme::text);
    }

    /**
     * Subtítulo secundario, más chico y en color atenuado.
     */
    public static JLabel subtitle(String text) {
        return themedLabel(text, Font.PLAIN, 13, Theme::mutedText);
    }

    /**
     * Badge redondeado que enmarca un ícono vectorial. Lo usan las tarjetas de configuración.
     */
    public static JPanel iconBadge(AppIcon.Glyph glyph, int badgeSize, int iconSize) {
        JPanel badge = new JPanel(new BorderLayout()) {
            /**
             * Pinta el fondo redondeado y translúcido del badge.
             */
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                graphics2D.setColor(Theme.withAlpha(Theme.accent(), 34));
                graphics2D.fillRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        badgeSize,
                        badgeSize
                );
                graphics2D.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new java.awt.Dimension(badgeSize, badgeSize));
        badge.setMinimumSize(new java.awt.Dimension(badgeSize, badgeSize));
        badge.setMaximumSize(new java.awt.Dimension(badgeSize, badgeSize));

        JLabel iconLabel = new JLabel(new AppIcon(glyph, iconSize, Theme::accent));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        badge.add(iconLabel, BorderLayout.CENTER);
        return badge;
    }

    /**
     * Crea una etiqueta y la deja suscripta al tema: cuando cambia el modo, actualiza fuente y color.
     */
    public static JLabel themedLabel(String text,
                                     int style,
                                     int size,
                                     Supplier<Color> colorSupplier) {
        JLabel label = new JLabel(text);
        label.setFont(font(style, size));
        label.setForeground(colorSupplier.get());
        Theme.addListener(() -> {
            label.setFont(font(style, size));
            label.setForeground(colorSupplier.get());
        });
        return label;
    }

    /**
     * Botón principal (fondo azul de acento) con texto blanco.
     */
    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        stylePrimaryButton(button);
        Theme.addListener(() -> stylePrimaryButton(button));
        return button;
    }

    /**
     * Aplica al botón principal los colores y bordes redondeados del tema actual.
     */
    private static void stylePrimaryButton(JButton button) {
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.accent());
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("JButton.arc", CONTROL_RADIUS);
    }

    /**
     * Botón secundario (fondo de input y borde suave) para acciones alternativas.
     */
    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("JButton.arc", CONTROL_RADIUS);
        styleSecondaryButton(button);
        Theme.addListener(() -> styleSecondaryButton(button));
        return button;
    }

    /**
     * Reaplica colores y borde al botón secundario cuando cambia el tema.
     */
    private static void styleSecondaryButton(JButton button) {
        button.setFont(font(Font.PLAIN, 13));
        button.setBackground(Theme.input());
        button.setForeground(Theme.text());
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.border(), 1),
                new EmptyBorder(9, 16, 9, 16)
        ));
    }

    /**
     * Botón con un color de fondo configurable (por ejemplo, el botón naranja de recordatorios).
     */
    public static JButton coloredButton(String text, Supplier<Color> backgroundSupplier) {
        Objects.requireNonNull(backgroundSupplier, "backgroundSupplier");

        JButton button = new JButton(text);
        styleColoredButton(button, backgroundSupplier);
        Theme.addListener(() -> styleColoredButton(button, backgroundSupplier));
        return button;
    }

    /**
     * Aplica el color recibido y el mismo formato redondeado que los demás botones.
     */
    private static void styleColoredButton(JButton button, Supplier<Color> backgroundSupplier) {
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundSupplier.get());
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("JButton.arc", CONTROL_RADIUS);
    }

    /**
     * Botón cuadrado que solo muestra un ícono (cerrar, flechas, eliminar, etc.).
     */
    public static JButton iconButton(AppIcon.Glyph glyph, int size, Supplier<Color> colorSupplier) {
        JButton button = new JButton(new AppIcon(glyph, size, colorSupplier));
        styleIconButton(button, size);
        Theme.addListener(() -> styleIconButton(button, size));
        return button;
    }

    /**
     * Deja el botón de ícono sin fondo ni borde, con tamaño fijo y cursor de mano.
     */
    private static void styleIconButton(JButton button, int size) {
        button.setPreferredSize(new java.awt.Dimension(size + 8, size + 8));
        button.setMinimumSize(new java.awt.Dimension(size + 8, size + 8));
        button.setMaximumSize(new java.awt.Dimension(size + 8, size + 8));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Campo de texto con placeholder y estilo del tema.
     */
    public static JTextField textField(String placeholder) {
        JTextField field = new JTextField();
        styleTextField(field, placeholder);
        Theme.addListener(() -> styleTextField(field, placeholder));
        return field;
    }

    /**
     * Aplica fuente, colores, placeholder y borde redondeado al campo.
     */
    private static void styleTextField(JTextField field, String placeholder) {
        field.setFont(font(Font.PLAIN, 14));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JComponent.arc", CONTROL_RADIUS);
        field.setBackground(Theme.input());
        field.setForeground(Theme.text());
        field.setCaretColor(Theme.text());
        field.setFocusable(true);
        applyTextFieldBorder(field, false);
    }

    /**
     * Área de texto multilínea con el estilo del tema.
     */
    public static JTextArea textArea() {
        JTextArea area = new JTextArea();
        styleTextArea(area);
        Theme.addListener(() -> styleTextArea(area));
        return area;
    }

    /**
     * Configura fuente, salto de línea, colores y padding interno del área de texto.
     */
    private static void styleTextArea(JTextArea area) {
        area.setFont(font(Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(Theme.input());
        area.setForeground(Theme.text());
        area.setCaretColor(Theme.text());
        area.setBorder(new EmptyBorder(10, 14, 10, 14));
    }

    /**
     * Combo box de texto con renderer propio para que las opciones respeten los colores del tema.
     */
    public static JComboBox<String> comboBox(String[] options) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            /**
             * Aplica fuente, padding y colores del tema a cada opción del combo.
             */
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );
                label.setFont(font(Font.PLAIN, 13));
                label.setBorder(new EmptyBorder(4, 10, 4, 10));
                label.setBackground(isSelected ? Theme.accent() : Theme.input());
                label.setForeground(isSelected ? Color.WHITE : Theme.text());
                return label;
            }
        });
        styleComboBox(comboBox);
        Theme.addListener(() -> styleComboBox(comboBox));
        return comboBox;
    }

    /**
     * Aplica colores y borde al combo cuando se crea o cambia el tema.
     */
    private static void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(font(Font.PLAIN, 14));
        comboBox.setFocusable(false);
        comboBox.setBackground(Theme.input());
        comboBox.setForeground(Theme.text());
        comboBox.setBorder(BorderFactory.createLineBorder(Theme.border(), 1, true));
    }

    /**
     * Scroll pane sin borde y con la barra fina personalizada.
     */
    public static JScrollPane scrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        styleScrollPane(scrollPane);
        Theme.addListener(() -> styleScrollPane(scrollPane));
        return scrollPane;
    }

    /**
     * Deja transparente el scroll, oculta la barra horizontal y cambia el thumb por el fino.
     */
    private static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
    }

    /**
     * Tarjeta redondeada con el radio estándar y el color de superficie del tema.
     */
    public static RoundedPanel card(LayoutManager layout) {
        return new RoundedPanel(layout, Theme::surface, CARD_RADIUS);
    }

    /**
     * Igual que la anterior, pero con un radio de esquina personalizado.
     */
    public static RoundedPanel card(LayoutManager layout, int radius) {
        return new RoundedPanel(layout, Theme::surface, radius);
    }

    /**
     * Estado vacío reutilizable: ícono, título y mensaje centrados.
     */
    public static JPanel emptyState(String headline, String message) {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(48, 20, 48, 20));

        JLabel iconLabel = new JLabel(BrandIcon.at(30));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel headlineLabel = themedLabel(headline, Font.BOLD, 16, Theme::text);
        headlineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headlineLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel messageLabel = themedLabel(message, Font.PLAIN, 13, Theme::mutedText);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(iconLabel);
        panel.add(javax.swing.Box.createVerticalStrut(12));
        panel.add(headlineLabel);
        panel.add(javax.swing.Box.createVerticalStrut(6));
        panel.add(messageLabel);

        return panel;
    }

    /**
     * Pinta el borde del campo de texto: rojo y más grueso si el dato es inválido.
     */
    public static void applyTextFieldBorder(JTextField field, boolean invalid) {
        Color borderColor = invalid ? Theme.DANGER : Theme.border();
        int borderWidth = invalid ? 2 : 1;
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, borderWidth, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
    }

    /**
     * Lo mismo que el campo de texto, pero para el área de texto.
     */
    public static void applyTextAreaBorder(JTextArea area, boolean invalid) {
        Color borderColor = invalid ? Theme.DANGER : Theme.border();
        int borderWidth = invalid ? 2 : 1;
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, borderWidth, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
    }

    /**
     * Muestra un diálogo de error con el mensaje de la excepción recibida.
     */
    public static void showError(Component parent, String title, Throwable error) {
        String message = error == null ? "Error desconocido" : error.getMessage();
        if (message == null || message.isBlank()) {
            message = error == null ? "Error desconocido" : error.getClass().getSimpleName();
        }
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un diálogo informativo simple.
     */
    public static void showInfo(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un diálogo de advertencia (por ejemplo, cuando falta un dato obligatorio).
     */
    public static void showWarning(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Convierte la fecha que devuelve el selector a una fecha local usando la zona horaria del sistema.
     */
    public static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Convierte una fecha y hora a un horario local, sin segundos ni nanosegundos.
     */
    public static LocalTime toLocalTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0)
                .withNano(0);
    }

    /**
     * Formatea una fecha como "dd MMM yyyy" (devuelve vacío si es null).
     */
    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    /**
     * Formatea una hora como "HH:mm" (devuelve vacío si es null).
     */
    public static String formatTime(LocalTime time) {
        return time == null ? "" : time.format(TIME_FORMATTER);
    }

    /**
     * Formatea fecha y hora juntas, separadas por un punto medio.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.toLocalDate().format(DATE_FORMATTER)
                + " · "
                + dateTime.toLocalTime().format(TIME_FORMATTER);
    }

    /**
     * Espacio vertical reutilizable para separar bloques dentro de una distribución vertical.
     */
    public static JComponent verticalStrut(int height) {
        return (JComponent) javax.swing.Box.createVerticalStrut(height);
    }

    /**
     * Listener de mouse que ejecuta una acción al entrar y otra al salir del componente.
     */
    public static MouseAdapter onHover(Runnable onEnter, Runnable onExit) {
        return new MouseAdapter() {
            /**
             * Ejecuta la acción configurada cuando el mouse entra al componente.
             */
            @Override
            public void mouseEntered(MouseEvent event) {
                onEnter.run();
            }

            /**
             * Ejecuta la acción configurada cuando el mouse sale del componente.
             */
            @Override
            public void mouseExited(MouseEvent event) {
                onExit.run();
            }
        };
    }
}
