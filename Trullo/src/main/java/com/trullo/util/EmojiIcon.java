package com.trullo.util;

import javax.swing.*;
import java.awt.*;

// Esta clase permite dibujar emojis como iconos en Swing
// porque Swing no soporta emojis nativamente, tuvimos que hacer este wrapper
// prueba varias fuentes para que los emojis se vean en cualquier SO
public class EmojiIcon implements Icon {

    // fuentes que soportan emojis, en orden de preferencia
    private static final String[] FUENTES_EMOJI = {
        "Segoe UI Emoji",
        "Segoe UI Symbol",
        "Noto Color Emoji",
        "Apple Color Emoji",
        "Twemoji Mozilla",
        "Arial Unicode MS"
    };

    // el emoji como string (por ejemplo "\uD83D\uDCDD")
    private final String emoji;

    // el tamaño del icono en pixeles
    private final int size;

    // la fuente que funciono para este emoji
    private final Font fontDetectada;

    // constructor, recibe el emoji y el tamaño
    public EmojiIcon(String emoji, int size) {
        this.emoji = emoji;
        this.size = size;
        this.fontDetectada = detectarFuente();
    }

    // detecta la mejor fuente disponible en el sistema para este emoji
    private Font detectarFuente() {
        Font base = new Font("Segoe UI", Font.PLAIN, size);
        // primero probamos las fuentes de emojis
        for (String nombre : FUENTES_EMOJI) {
            Font candidata = new Font(nombre, Font.PLAIN, size);
            if (!candidata.getFontName().equals(base.getFontName())) {
                // verificamos si la fuente tiene el glyph para este emoji
                FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(candidata);
                if (fm.charWidth(emoji.charAt(0)) > 0) {
                    return candidata;
                }
            }
        }
        // si ninguna funciono, usamos la base
        return base;
    }

    // dibuja el emoji en la posicion x, y
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        // activamos el antialiasing para que se vea suave
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(fontDetectada);

        // calculamos la posicion Y para que quede centrado verticalmente
        FontMetrics fm = g2.getFontMetrics();
        int textY = y + (getIconHeight() - fm.getHeight()) / 2 + fm.getAscent();

        // dibujamos el emoji
        g2.drawString(emoji, x, textY);
        g2.dispose(); // liberamos recursos
    }

    // ancho del icono: tamaño + un poco de padding
    @Override
    public int getIconWidth() {
        return size + 4;
    }

    // alto del icono: tamaño + un poco de padding
    @Override
    public int getIconHeight() {
        return size + 4;
    }
}
