package com.trullo.frontend;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Set de íconos vectoriales que usan la barra lateral, los diálogos y los botones de acción.
 * Se dibujan a mano en vez de usar emojis para que se vean nítidos e iguales en cualquier
 * plataforma y para que puedan cambiar de color con el tema.
 */
public final class AppIcon implements Icon {

    // Cada constante es un ícono que sabe dibujarse solo.
    public enum Glyph {
        HOME,
        TASKS,
        CALENDAR,
        NOTES,
        FOLDER,
        SETTINGS,
        PLUS,
        CLOSE,
        CHEVRON_LEFT,
        CHEVRON_RIGHT,
        CHEVRON_DOWN,
        CHEVRON_UP,
        SUN,
        MOON,
        LANGUAGE,
        BELL,
        SHIELD,
        PEOPLE
    }

    // Los íconos se diseñan en una grilla de 24x24 y después se escalan al tamaño pedido.
    private static final double DESIGN_SIZE = 24.0;

    private final Glyph glyph;
    private final int size;
    private final Supplier<Color> colorSupplier;

    /**
     * Constructor cómodo cuando el color es fijo.
     */
    public AppIcon(Glyph glyph, int size, Color color) {
        this(glyph, size, () -> color);
    }

    /**
     * Constructor completo: el color se pide en cada repintado para seguir el tema.
     */
    public AppIcon(Glyph glyph, int size, Supplier<Color> colorSupplier) {
        this.glyph = Objects.requireNonNull(glyph, "glyph");
        this.size = size;
        this.colorSupplier = Objects.requireNonNull(colorSupplier, "colorSupplier");
    }

    /**
     * Ancho del ícono.
     */
    @Override
    public int getIconWidth() {
        return size;
    }

    /**
     * Alto del ícono.
     */
    @Override
    public int getIconHeight() {
        return size;
    }

    /**
     * Prepara el contexto de dibujo (traslado, escala y trazo) y deriva al método que dibuja el glifo.
     */
    @Override
    public void paintIcon(Component component, Graphics graphics, int originX, int originY) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.translate(originX, originY);
        double scale = size / DESIGN_SIZE;
        graphics2D.scale(scale, scale);
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2D.setColor(colorSupplier.get());
        graphics2D.setStroke(new BasicStroke(
                1.8f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        switch (glyph) {
            case HOME -> paintHome(graphics2D);
            case TASKS -> paintTasks(graphics2D);
            case CALENDAR -> paintCalendar(graphics2D);
            case NOTES -> paintNotes(graphics2D);
            case FOLDER -> paintFolder(graphics2D);
            case SETTINGS -> paintSettings(graphics2D);
            case PLUS -> paintPlus(graphics2D);
            case CLOSE -> paintClose(graphics2D);
            case CHEVRON_LEFT, CHEVRON_RIGHT, CHEVRON_DOWN, CHEVRON_UP ->
                    paintChevron(graphics2D, glyph);
            case SUN -> paintSun(graphics2D);
            case MOON -> paintMoon(graphics2D);
            case LANGUAGE -> paintLanguage(graphics2D);
            case BELL -> paintBell(graphics2D);
            case SHIELD -> paintShield(graphics2D);
            case PEOPLE -> paintPeople(graphics2D);
            default -> throw new IllegalStateException("Unsupported icon: " + glyph);
        }

        graphics2D.dispose();
    }

    /**
     * Casa: techo a dos aguas, cuerpo y puerta.
     */
    private void paintHome(Graphics2D graphics2D) {
        Path2D roof = new Path2D.Double();
        roof.moveTo(3.5, 11.0);
        roof.lineTo(12.0, 4.0);
        roof.lineTo(20.5, 11.0);
        graphics2D.draw(roof);

        graphics2D.draw(new RoundRectangle2D.Double(6.0, 10.5, 12.0, 9.5, 2.0, 2.0));
        graphics2D.draw(new RoundRectangle2D.Double(10.0, 15.0, 4.0, 5.0, 1.5, 1.5));
    }

    /**
     * Lista de tareas: hoja con dos renglones y sus tildes.
     */
    private void paintTasks(Graphics2D graphics2D) {
        graphics2D.draw(new RoundRectangle2D.Double(4.0, 3.0, 16.0, 18.0, 4.0, 4.0));

        graphics2D.drawPolyline(
                new int[]{7, 9, 12},
                new int[]{9, 11, 7},
                3
        );
        graphics2D.drawLine(14, 9, 18, 9);

        graphics2D.drawPolyline(
                new int[]{7, 9, 12},
                new int[]{15, 17, 13},
                3
        );
        graphics2D.drawLine(14, 15, 18, 15);
    }

    /**
     * Calendario: cuerpo, línea del encabezado, argollas y puntitos de días.
     */
    private void paintCalendar(Graphics2D graphics2D) {
        graphics2D.draw(new RoundRectangle2D.Double(3.0, 5.0, 18.0, 16.0, 3.0, 3.0));
        graphics2D.drawLine(3, 10, 21, 10);
        graphics2D.drawLine(8, 2, 8, 7);
        graphics2D.drawLine(16, 2, 16, 7);

        graphics2D.fill(new Ellipse2D.Double(7, 13, 2, 2));
        graphics2D.fill(new Ellipse2D.Double(11, 13, 2, 2));
        graphics2D.fill(new Ellipse2D.Double(15, 13, 2, 2));
        graphics2D.fill(new Ellipse2D.Double(7, 17, 2, 2));
        graphics2D.fill(new Ellipse2D.Double(11, 17, 2, 2));
    }

    /**
     * Nota: hoja con la esquina doblada y dos líneas de texto.
     */
    private void paintNotes(Graphics2D graphics2D) {
        Path2D note = new Path2D.Double();
        note.moveTo(4.0, 4.0);
        note.lineTo(15.0, 4.0);
        note.lineTo(20.0, 9.0);
        note.lineTo(20.0, 20.0);
        note.lineTo(4.0, 20.0);
        note.closePath();
        graphics2D.draw(note);
        graphics2D.drawLine(15, 4, 15, 9);
        graphics2D.drawLine(15, 9, 20, 9);
        graphics2D.drawLine(8, 13, 16, 13);
        graphics2D.drawLine(8, 16, 14, 16);
    }

    /**
     * Carpeta de proyecto con su pestaña.
     */
    private void paintFolder(Graphics2D graphics2D) {
        Path2D folder = new Path2D.Double();
        folder.moveTo(3.5, 6.0);
        folder.lineTo(9.5, 6.0);
        folder.lineTo(11.5, 8.5);
        folder.lineTo(20.5, 8.5);
        folder.lineTo(20.5, 19.0);
        folder.lineTo(3.5, 19.0);
        folder.closePath();
        graphics2D.draw(folder);
    }

    /**
     * Engranaje de configuración: círculo central y ocho dientes alrededor.
     */
    private void paintSettings(Graphics2D graphics2D) {
        graphics2D.draw(new Ellipse2D.Double(8.5, 8.5, 7.0, 7.0));
        graphics2D.draw(new Ellipse2D.Double(10.5, 10.5, 3.0, 3.0));

        for (int index = 0; index < 8; index++) {
            double angle = Math.toRadians(index * 45.0);
            int startX = (int) Math.round(12 + Math.cos(angle) * 7.0);
            int startY = (int) Math.round(12 + Math.sin(angle) * 7.0);
            int endX = (int) Math.round(12 + Math.cos(angle) * 10.0);
            int endY = (int) Math.round(12 + Math.sin(angle) * 10.0);
            graphics2D.drawLine(startX, startY, endX, endY);
        }
    }

    /**
     * Cruz del botón "crear".
     */
    private void paintPlus(Graphics2D graphics2D) {
        graphics2D.drawLine(12, 5, 12, 19);
        graphics2D.drawLine(5, 12, 19, 12);
    }

    /**
     * X del botón cerrar/eliminar.
     */
    private void paintClose(Graphics2D graphics2D) {
        graphics2D.drawLine(6, 6, 18, 18);
        graphics2D.drawLine(18, 6, 6, 18);
    }

    /**
     * Flechas de navegación (izquierda, derecha, arriba y abajo).
     */
    private void paintChevron(Graphics2D graphics2D, Glyph chevronGlyph) {
        switch (chevronGlyph) {
            case CHEVRON_LEFT -> {
                graphics2D.drawLine(15, 6, 9, 12);
                graphics2D.drawLine(9, 12, 15, 18);
            }
            case CHEVRON_RIGHT -> {
                graphics2D.drawLine(9, 6, 15, 12);
                graphics2D.drawLine(15, 12, 9, 18);
            }
            case CHEVRON_UP -> {
                graphics2D.drawLine(6, 15, 12, 9);
                graphics2D.drawLine(12, 9, 18, 15);
            }
            case CHEVRON_DOWN -> {
                graphics2D.drawLine(6, 9, 12, 15);
                graphics2D.drawLine(12, 15, 18, 9);
            }
            default -> throw new IllegalStateException("Unsupported chevron: " + chevronGlyph);
        }
    }

    /**
     * Sol: círculo central y rayos alrededor (tema claro).
     */
    private void paintSun(Graphics2D graphics2D) {
        graphics2D.draw(new Ellipse2D.Double(8, 8, 8, 8));
        for (int index = 0; index < 8; index++) {
            double angle = Math.toRadians(index * 45.0);
            int startX = (int) Math.round(12 + Math.cos(angle) * 7.5);
            int startY = (int) Math.round(12 + Math.sin(angle) * 7.5);
            int endX = (int) Math.round(12 + Math.cos(angle) * 10.5);
            int endY = (int) Math.round(12 + Math.sin(angle) * 10.5);
            graphics2D.drawLine(startX, startY, endX, endY);
        }
    }

    /**
     * Luna: dos elipses restadas para formar la medialuna (tema oscuro).
     */
    private void paintMoon(Graphics2D graphics2D) {
        Area crescent = new Area(new Ellipse2D.Double(4.0, 3.0, 16.0, 18.0));
        crescent.subtract(new Area(new Ellipse2D.Double(9.0, 1.0, 16.0, 18.0)));
        graphics2D.fill(crescent);
    }

    /**
     * Globo terráqueo con meridianos y paralelos (selector de idioma).
     */
    private void paintLanguage(Graphics2D graphics2D) {
        graphics2D.draw(new Ellipse2D.Double(3, 3, 18, 18));
        graphics2D.draw(new Ellipse2D.Double(8, 3, 8, 18));
        graphics2D.drawLine(3, 12, 21, 12);
        graphics2D.drawLine(5, 7, 19, 7);
        graphics2D.drawLine(5, 17, 19, 17);
    }

    /**
     * Campana de notificaciones.
     */
    private void paintBell(Graphics2D graphics2D) {
        Path2D bell = new Path2D.Double();
        bell.moveTo(6.0, 17.0);
        bell.lineTo(6.0, 11.0);
        bell.curveTo(6.0, 7.0, 8.5, 4.5, 12.0, 4.5);
        bell.curveTo(15.5, 4.5, 18.0, 7.0, 18.0, 11.0);
        bell.lineTo(18.0, 17.0);
        bell.closePath();
        graphics2D.draw(bell);
        graphics2D.drawLine(4, 17, 20, 17);
        graphics2D.drawLine(10, 20, 14, 20);
    }

    /**
     * Escudo con un tilde (privacidad).
     */
    private void paintShield(Graphics2D graphics2D) {
        Path2D shield = new Path2D.Double();
        shield.moveTo(12.0, 3.0);
        shield.lineTo(20.0, 6.0);
        shield.lineTo(20.0, 11.0);
        shield.curveTo(20.0, 16.0, 16.0, 19.0, 12.0, 21.0);
        shield.curveTo(8.0, 19.0, 4.0, 16.0, 4.0, 11.0);
        shield.lineTo(4.0, 6.0);
        shield.closePath();
        graphics2D.draw(shield);
        graphics2D.drawPolyline(
                new int[]{8, 11, 16},
                new int[]{11, 14, 8},
                3
        );
    }

    /**
     * Dos personas (tarjeta de creadores).
     */
    private void paintPeople(Graphics2D graphics2D) {
        graphics2D.draw(new Ellipse2D.Double(4, 4, 6, 6));
        graphics2D.draw(new Ellipse2D.Double(14, 6, 5, 5));
        graphics2D.draw(new RoundRectangle2D.Double(2.5, 12, 9, 7, 4, 4));
        graphics2D.draw(new RoundRectangle2D.Double(12.5, 13, 8, 6, 4, 4));
    }
}
