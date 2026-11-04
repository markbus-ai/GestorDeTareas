package com.trullo.frontend;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Base de todos los diálogos de la aplicación: superficie redondeada, encabezado propio que se
 * puede arrastrar y helpers para que los formularios se vean consistentes.
 */
public abstract class BaseDialog extends JDialog {

    // Panel vertical donde cada diálogo agrega sus campos.
    protected final JPanel formPanel;
    // Panel raíz redondeado que hace de fondo del diálogo.
    private final RoundedPanel rootPanel;

    /**
     * Configura el diálogo sin decoración del sistema, modal y centrado sobre la ventana dueña.
     */
    protected BaseDialog(Window owner, String title, int width, int height) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(width, height);
        setResizable(false);
        setBackground(Theme.dialogBackground());

        rootPanel = new RoundedPanel(
                new BorderLayout(),
                Theme::dialogBackground,
                Theme::border,
                20
        );
        rootPanel.setBorder(new EmptyBorder(1, 1, 1, 1));

        formPanel = new JPanel();
        formPanel.setLayout(new javax.swing.BoxLayout(formPanel, javax.swing.BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(18, 28, 6, 28));

        rootPanel.add(createHeader(title), BorderLayout.NORTH);
        rootPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
        registerEscapeAction();
        setLocationRelativeTo(owner);
    }

    /**
     * Encabezado con título, botón de cerrar y arrastre con el mouse.
     */
    private JPanel createHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 28, 12, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UiKit.font(Font.BOLD, 18));
        titleLabel.setForeground(Theme.text());

        JButton closeButton = UiKit.iconButton(
                AppIcon.Glyph.CLOSE,
                16,
                Theme::mutedText
        );
        closeButton.setToolTipText("Cerrar");
        closeButton.addActionListener(actionEvent -> dispose());

        header.add(titleLabel, BorderLayout.WEST);
        header.add(closeButton, BorderLayout.EAST);

        // Guarda el punto donde se presionó para mover la ventana mientras se arrastra.
        MouseAdapter dragHandler = new MouseAdapter() {
            private Point pressedPoint;

            /**
             * Guarda el punto donde se presionó el mouse para calcular el arrastre.
             */
            @Override
            public void mousePressed(MouseEvent event) {
                pressedPoint = event.getPoint();
            }

            /**
             * Mueve el diálogo mientras se arrastra el encabezado.
             */
            @Override
            public void mouseDragged(MouseEvent event) {
                if (pressedPoint == null) {
                    return;
                }
                Point currentLocation = getLocation();
                setLocation(
                        currentLocation.x + event.getX() - pressedPoint.x,
                        currentLocation.y + event.getY() - pressedPoint.y
                );
            }
        };

        header.addMouseListener(dragHandler);
        header.addMouseMotionListener(dragHandler);
        titleLabel.addMouseListener(dragHandler);
        titleLabel.addMouseMotionListener(dragHandler);

        return header;
    }

    /**
     * Coloca el panel de botones en la parte inferior del diálogo.
     */
    protected void setFooter(JPanel footer) {
        rootPanel.add(footer, BorderLayout.SOUTH);
    }

    /**
     * Crea el contenedor estándar para los botones Cancelar / Crear.
     */
    protected JPanel formFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(4, 28, 20, 28));
        return footer;
    }

    /**
     * Label en negrita para nombrar un campo del formulario.
     */
    protected JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiKit.font(Font.BOLD, 12));
        label.setForeground(Theme.text());
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Agrega al formulario el label, un espacio y el campo recibido.
     */
    protected void addFormField(String labelText, JComponent field) {
        field.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.add(formLabel(labelText));
        formPanel.add(javax.swing.Box.createVerticalStrut(8));
        formPanel.add(field);
        formPanel.add(javax.swing.Box.createVerticalStrut(14));
    }

    /**
     * Campo de texto de una línea con el tamaño usado en los formularios.
     */
    protected JTextField formTextField(String placeholder) {
        JTextField field = UiKit.textField(placeholder);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(100, 40));
        field.setAlignmentX(LEFT_ALIGNMENT);
        return field;
    }

    /**
     * Combo box de opciones con el mismo tamaño que los campos de texto.
     */
    protected JComboBox<String> formComboBox(String[] options) {
        JComboBox<String> comboBox = UiKit.comboBox(options);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboBox.setPreferredSize(new Dimension(100, 40));
        comboBox.setAlignmentX(LEFT_ALIGNMENT);
        return comboBox;
    }

    /**
     * Botón de acción principal del diálogo.
     */
    protected JButton primaryAction(String text) {
        JButton button = UiKit.primaryButton(text);
        button.setPreferredSize(new Dimension(128, 38));
        return button;
    }

    /**
     * Botón secundario (por ejemplo, Cancelar).
     */
    protected JButton secondaryAction(String text) {
        JButton button = UiKit.secondaryButton(text);
        button.setPreferredSize(new Dimension(108, 38));
        return button;
    }

    /**
     * Marca un campo de texto como inválido (borde rojo) y le da el foco.
     */
    protected void markInvalid(JTextField field) {
        UiKit.applyTextFieldBorder(field, true);
        field.requestFocusInWindow();
    }

    /**
     * Lo mismo para un área de texto.
     */
    protected void markInvalid(JTextArea area) {
        UiKit.applyTextAreaBorder(area, true);
        area.requestFocusInWindow();
    }

    /**
     * Vuelve a poner el borde normal en un campo de texto.
     */
    protected void clearInvalid(JTextField field) {
        UiKit.applyTextFieldBorder(field, false);
    }

    /**
     * Vuelve a poner el borde normal en un área de texto.
     */
    protected void clearInvalid(JTextArea area) {
        UiKit.applyTextAreaBorder(area, false);
    }

    /**
     * Permite cerrar el diálogo con la tecla Escape.
     */
    private void registerEscapeAction() {
        getRootPane().registerKeyboardAction(
                actionEvent -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * Reemplaza el borde por una línea simple (lo usan algunos diálogos al abrirse).
     */
    protected void surfaceBorder() {
        rootPanel.setBorder(BorderFactory.createLineBorder(Theme.border(), 1, true));
    }
}
