package com.trullo.frontend;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Calendario mensual dibujado a mano: navegación de meses, marca de hoy, selección y puntitos
 * en los días que tienen recordatorios.
 */
public final class MonthlyCalendarView extends JPanel {

    // Formato del título ("October 2026") y nombres de las columnas.
    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("es", "ES"));
    private static final String[] WEEK_DAY_LABELS = {
            "LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM"
    };

    private final JLabel monthLabel;
    private final JPanel daysGrid;
    private final List<JLabel> weekDayLabels = new ArrayList<>();

    // Mes que se está mostrando, día elegido y fechas que tienen recordatorios.
    private YearMonth displayedMonth = YearMonth.now();
    private LocalDate selectedDate = LocalDate.now();
    private Set<LocalDate> reminderDates = new HashSet<>();
    private Consumer<LocalDate> onDateSelected;

    /**
     * Arma el encabezado (mes + botones) y la grilla de 6x7 días.
     */
    public MonthlyCalendarView() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(UiKit.font(Font.BOLD, 17));
        monthLabel.setForeground(Theme.text());
        Theme.addListener(() -> {
            monthLabel.setFont(UiKit.font(Font.BOLD, 17));
            monthLabel.setForeground(Theme.text());
        });

        // Botones para retroceder y avanzar un mes.
        JButton previousButton = UiKit.iconButton(
                AppIcon.Glyph.CHEVRON_LEFT,
                18,
                Theme::mutedText
        );
        previousButton.setToolTipText("Mes anterior");
        previousButton.addActionListener(actionEvent -> changeMonth(-1));

        JButton nextButton = UiKit.iconButton(
                AppIcon.Glyph.CHEVRON_RIGHT,
                18,
                Theme::mutedText
        );
        nextButton.setToolTipText("Mes siguiente");
        nextButton.addActionListener(actionEvent -> changeMonth(1));

        // Botón que vuelve al día de hoy.
        JButton todayButton = UiKit.secondaryButton("Hoy");
        todayButton.setPreferredSize(new Dimension(76, 30));
        todayButton.setFont(UiKit.font(Font.BOLD, 12));
        todayButton.addActionListener(actionEvent -> selectDate(LocalDate.now()));

        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        headerRightPanel.setOpaque(false);
        headerRightPanel.add(todayButton);
        headerRightPanel.add(nextButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(previousButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(headerRightPanel, BorderLayout.EAST);

        // Fila con los nombres de los días de la semana.
        JPanel weekDaysPanel = new JPanel(new GridLayout(1, 7, 4, 4));
        weekDaysPanel.setOpaque(false);
        weekDaysPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));

        for (String weekDayLabel : WEEK_DAY_LABELS) {
            JLabel label = UiKit.themedLabel(weekDayLabel, Font.BOLD, 10, Theme::mutedText);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            weekDayLabels.add(label);
            weekDaysPanel.add(label);
        }

        // Grilla fija de 6 semanas x 7 días (42 celdas) para que el alto no salte de mes a mes.
        daysGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        daysGrid.setOpaque(false);

        JPanel calendarBody = new JPanel(new BorderLayout());
        calendarBody.setOpaque(false);
        calendarBody.add(weekDaysPanel, BorderLayout.NORTH);
        calendarBody.add(daysGrid, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(calendarBody, BorderLayout.CENTER);

        rebuildMonth();
    }

    /**
     * Registra el callback que se ejecuta al elegir un día.
     */
    public void setOnDateSelected(Consumer<LocalDate> listener) {
        this.onDateSelected = listener;
    }

    /**
     * Devuelve el día que está seleccionado ahora.
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /**
     * Guarda las fechas con recordatorios para dibujar el puntito naranja.
     */
    public void setReminderDates(Set<LocalDate> dates) {
        reminderDates = dates == null ? new HashSet<>() : new HashSet<>(dates);
        repaint();
    }

    /**
     * Selecciona un día, cambia de mes si hace falta y avisa al listener.
     */
    public void selectDate(LocalDate date) {
        if (date == null) {
            return;
        }
        selectedDate = date;
        YearMonth newMonth = YearMonth.from(date);
        if (!newMonth.equals(displayedMonth)) {
            displayedMonth = newMonth;
            rebuildMonth();
        } else {
            repaint();
        }
        if (onDateSelected != null) {
            onDateSelected.accept(date);
        }
    }

    /**
     * Suma o resta meses al mes mostrado (monthDelta = 1 o -1).
     */
    private void changeMonth(int monthDelta) {
        displayedMonth = displayedMonth.plusMonths(monthDelta);
        rebuildMonth();
    }

    /**
     * Recalcula las 42 celdas: espacios vacíos al principio, días del mes y relleno al final.
     */
    private void rebuildMonth() {
        monthLabel.setText(displayedMonth.atDay(1).format(MONTH_FORMATTER));
        daysGrid.removeAll();

        LocalDate firstDayOfMonth = displayedMonth.atDay(1);
        // getDayOfWeek().getValue() devuelve 1 para lunes ... 7 para domingo.
        int emptyCellsBefore = firstDayOfMonth.getDayOfWeek().getValue() - 1;

        for (int index = 0; index < emptyCellsBefore; index++) {
            daysGrid.add(createEmptyCell());
        }

        for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
            daysGrid.add(new DayCell(displayedMonth.atDay(day)));
        }

        int totalCells = emptyCellsBefore + displayedMonth.lengthOfMonth();
        for (int index = totalCells; index < 42; index++) {
            daysGrid.add(createEmptyCell());
        }

        daysGrid.revalidate();
        daysGrid.repaint();
        revalidate();
        repaint();
    }

    /**
     * Celda transparente para completar los huecos antes y después del mes.
     */
    private JPanel createEmptyCell() {
        JPanel emptyCell = new JPanel();
        emptyCell.setOpaque(false);
        emptyCell.setPreferredSize(new Dimension(52, 52));
        return emptyCell;
    }

    /**
     * Un día del calendario. Pinta el número directamente para mantener simple el árbol de
     * componentes.
     */
    private final class DayCell extends JPanel {

        private final LocalDate date;
        private boolean hovered;

        /**
         * Configura la celda y los listeners de hover y click.
         */
        private DayCell(LocalDate date) {
            this.date = date;
            setOpaque(false);
            setPreferredSize(new Dimension(52, 52));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                /**
                 * Marca la celda como hover y la repinta al entrar el mouse.
                 */
                @Override
                public void mouseEntered(MouseEvent event) {
                    hovered = true;
                    repaint();
                }

                /**
                 * Saca el estado hover cuando el mouse sale de la celda.
                 */
                @Override
                public void mouseExited(MouseEvent event) {
                    hovered = false;
                    repaint();
                }

                /**
                 * Selecciona el día clickeado y avisa al calendario.
                 */
                @Override
                public void mouseClicked(MouseEvent event) {
                    MonthlyCalendarView.this.selectDate(DayCell.this.date);
                }
            });
        }

        /**
         * Dibuja el círculo de fondo (hoy, seleccionado o hover), el número y el puntito.
         */
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            boolean isToday = date.equals(LocalDate.now());
            boolean isSelected = date.equals(selectedDate);

            int diameter = 36;
            int circleX = (getWidth() - diameter) / 2;
            int circleY = (getHeight() - diameter) / 2 - 1;

            // Prioridad visual: hoy > seleccionado > hover.
            if (isToday) {
                graphics2D.setColor(Theme.accent());
                graphics2D.fillOval(circleX, circleY, diameter, diameter);
            } else if (isSelected) {
                graphics2D.setColor(Theme.ACCENT_SOFT);
                graphics2D.fillOval(circleX, circleY, diameter, diameter);
            } else if (hovered) {
                graphics2D.setColor(Theme.hoverOverlay());
                graphics2D.fillOval(circleX, circleY, diameter, diameter);
            }

            Color textColor;
            if (isToday) {
                textColor = Color.WHITE;
            } else if (isSelected) {
                textColor = Theme.accent();
            } else {
                textColor = Theme.text();
            }

            graphics2D.setColor(textColor);
            graphics2D.setFont(UiKit.font(
                    isToday || isSelected ? Font.BOLD : Font.PLAIN,
                    13
            ));

            // Centra el número del día dentro del círculo.
            String dayText = String.valueOf(date.getDayOfMonth());
            FontMetrics fontMetrics = graphics2D.getFontMetrics();
            int textX = (getWidth() - fontMetrics.stringWidth(dayText)) / 2;
            int textY = circleY + (diameter - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
            graphics2D.drawString(dayText, textX, textY);

            // Puntito naranja debajo del número si ese día tiene recordatorios.
            if (reminderDates.contains(date)) {
                graphics2D.setColor(Theme.WARNING);
                graphics2D.fillOval(
                        getWidth() / 2 - 3,
                        circleY + diameter + 1,
                        6,
                        6
                );
            }

            graphics2D.dispose();
        }
    }
}
