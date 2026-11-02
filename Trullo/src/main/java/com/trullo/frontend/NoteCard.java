package com.trullo.frontend;

import com.trullo.model.Note;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Tarjeta de nota tipo post-it de color. El contenido se expande cuando se hace click.
 */
public final class NoteCard extends JPanel {

    // Paleta fija de las tarjetas; se repite si hay más notas que colores.
    private static final Color[] CARD_COLORS = {
            new Color(62, 85, 105),
            new Color(72, 95, 78),
            new Color(95, 78, 68),
            new Color(80, 68, 95),
            new Color(68, 85, 98)
    };

    // Nota que muestra la tarjeta y su color base.
    private final Note note;
    private final Color baseColor;
    private final JTextArea contentArea;
    private final JScrollPane contentScrollPane;
    private final JButton chevronButton;

    // Estado visual: si está expandida y si el mouse está encima.
    private boolean expanded;
    private boolean hovered;

    /**
     * Recibe la nota, el índice de color y la acción de eliminar.
     */
    public NoteCard(Note note, int colorIndex, Consumer<Note> onDelete) {
        this.note = note;
        // Math.floorMod evita índices negativos o fuera de rango.
        this.baseColor = CARD_COLORS[Math.floorMod(colorIndex, CARD_COLORS.length)];

        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(16, 16, 14, 16));
        setPreferredSize(new Dimension(300, 210));
        setMinimumSize(new Dimension(240, 190));

        JLabel titleLabel = new JLabel(note.getTitle());
        titleLabel.setFont(UiKit.font(Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setToolTipText(note.getTitle());

        // Botón para expandir/colapsar el contenido de la nota.
        chevronButton = createIconAction(
                AppIcon.Glyph.CHEVRON_DOWN,
                16,
                new Color(255, 255, 255, 200),
                "Expandir nota"
        );
        chevronButton.addActionListener(actionEvent -> toggleExpanded());

        // Botón para eliminar la nota (avisa a NotesScreen mediante el callback).
        JButton deleteButton = createIconAction(
                AppIcon.Glyph.CLOSE,
                14,
                new Color(255, 255, 255, 150),
                "Eliminar nota"
        );
        deleteButton.addActionListener(actionEvent -> onDelete.accept(note));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(chevronButton);
        actionsPanel.add(deleteButton);

        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(actionsPanel, BorderLayout.EAST);

        // Área de texto con el contenido de la nota (solo lectura).
        contentArea = new JTextArea(
                note.getContent() == null || note.getContent().isBlank()
                        ? "(Sin contenido)"
                        : note.getContent()
        );
        contentArea.setFont(UiKit.font(Font.PLAIN, 12));
        contentArea.setForeground(new Color(255, 255, 255, 215));
        contentArea.setOpaque(false);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(new EmptyBorder(8, 0, 0, 0));

        // El contenido arranca oculto y aparece al expandir la tarjeta.
        contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(null);
        contentScrollPane.setOpaque(false);
        contentScrollPane.getViewport().setOpaque(false);
        contentScrollPane.setHorizontalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        contentScrollPane.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        contentScrollPane.getVerticalScrollBar().setOpaque(false);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        contentScrollPane.setVisible(false);

        add(headerPanel, BorderLayout.NORTH);
        add(contentScrollPane, BorderLayout.CENTER);

        // Click en la tarjeta o en el encabezado también expande/colapsa.
        MouseAdapter toggleAdapter = new MouseAdapter() {
            /**
             * Expande o colapsa la nota al hacer click sobre la tarjeta.
             */
            @Override
            public void mouseClicked(MouseEvent event) {
                toggleExpanded();
            }
        };
        addMouseListener(toggleAdapter);
        headerPanel.addMouseListener(toggleAdapter);
        titleLabel.addMouseListener(toggleAdapter);

        /**
         * Hover: aclara apenas el color de la tarjeta.
         */
        addMouseListener(UiKit.onHover(
                () -> {
                    hovered = true;
                    repaint();
                },
                () -> {
                    hovered = false;
                    repaint();
                }
        ));
        headerPanel.addMouseListener(UiKit.onHover(
                () -> {
                    hovered = true;
                    repaint();
                },
                () -> {
                    hovered = false;
                    repaint();
                }
        ));
    }

    /**
     * Crea un botón transparente de ícono con tooltip (chevron o X).
     */
    private static JButton createIconAction(AppIcon.Glyph glyph,
                                            int iconSize,
                                            Color iconColor,
                                            String tooltip) {
        JButton button = new JButton(new AppIcon(glyph, iconSize, iconColor));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(iconSize + 8, iconSize + 8));
        button.setMinimumSize(new Dimension(iconSize + 8, iconSize + 8));
        button.setMaximumSize(new Dimension(iconSize + 8, iconSize + 8));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Alterna el contenido visible y cambia la flecha para arriba/abajo.
     */
    private void toggleExpanded() {
        expanded = !expanded;
        contentScrollPane.setVisible(expanded);
        chevronButton.setIcon(new AppIcon(
                expanded ? AppIcon.Glyph.CHEVRON_UP : AppIcon.Glyph.CHEVRON_DOWN,
                16,
                new Color(255, 255, 255, 200)
        ));
        chevronButton.setToolTipText(expanded ? "Contraer nota" : "Expandir nota");
        revalidate();
        repaint();
    }

    /**
     * Pinta la sombra, el rectángulo de color y la línea clara superior de la tarjeta.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        graphics2D.setColor(new Color(0, 0, 0, 34));
        graphics2D.fillRoundRect(3, 4, getWidth() - 3, getHeight() - 4, 16, 16);

        Color fillColor = hovered ? baseColor.brighter() : baseColor;
        graphics2D.setColor(fillColor);
        graphics2D.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);

        graphics2D.setColor(new Color(255, 255, 255, 22));
        graphics2D.fillRect(1, 1, getWidth() - 6, 3);

        graphics2D.dispose();
    }

    /**
     * Devuelve la nota asociada (por si otra pantalla necesita el modelo).
     */
    public Note getNote() {
        return note;
    }
}
