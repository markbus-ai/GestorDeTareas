package com.trullo.frontend;

import com.trullo.model.Reminder;
import com.trullo.model.ReminderTarget;
import com.trullo.model.Task;
import com.trullo.notification.email.EmailNotifier;
import com.trullo.notification.whatsapp.WhatsAppApiNotifier;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Fila de un recordatorio: título de la tarea, hora, canal y botón para eliminarlo.
 */
public final class ReminderCard extends RoundedPanel {

    /**
     * Recibe el recordatorio, la tarea asociada (puede ser null), sus destinos y la acción de borrar.
     */
    public ReminderCard(Reminder reminder,
                        Task task,
                        List<ReminderTarget> targets,
                        Consumer<Reminder> onDelete) {
        super(new BorderLayout(10, 0), Theme::surfaceAlt, 12);

        setBorder(new EmptyBorder(10, 12, 10, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        setAlignmentX(LEFT_ALIGNMENT);

        // Puntito naranja que indica que es un recordatorio.
        JPanel dotPanel = new JPanel() {
            /**
             * Dibuja el puntito naranja que identifica un recordatorio.
             */
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                graphics2D.setColor(Theme.WARNING);
                graphics2D.fillOval(3, 3, 7, 7);
                graphics2D.dispose();
            }
        };
        dotPanel.setOpaque(false);
        dotPanel.setPreferredSize(new Dimension(14, 20));

        // Si la tarea no se encontró en el mapa, muestra el id como respaldo.
        String taskTitle = task == null
                ? "Tarea #" + reminder.getTaskId()
                : task.getTitle();

        JLabel titleLabel = UiKit.themedLabel(taskTitle, Font.BOLD, 13, Theme::text);
        titleLabel.setToolTipText(taskTitle);

        // Línea secundaria: hora y canal de notificación.
        String timeText = UiKit.formatTime(
                reminder.getScheduledAt() == null ? null : reminder.getScheduledAt().toLocalTime()
        );
        String channelText = channelLabel(targets);
        JLabel detailLabel = UiKit.themedLabel(
                timeText + " · " + channelText,
                Font.PLAIN,
                11,
                Theme::mutedText
        );

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(javax.swing.Box.createVerticalStrut(3));
        textPanel.add(detailLabel);

        JButtonWithIcon deleteButton = new JButtonWithIcon(reminder, onDelete);

        add(dotPanel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.EAST);
    }

    /**
     * Traduce los canales de los destinos a una etiqueta legible en español.
     */
    private String channelLabel(List<ReminderTarget> targets) {
        if (targets.isEmpty()) {
            return "Notificación";
        }
        return targets.stream()
                .map(target -> switch (target.getChannel()) {
                    case WhatsAppApiNotifier.CHANNEL -> "WhatsApp";
                    case EmailNotifier.CHANNEL -> "Correo electrónico";
                    default -> target.getChannel();
                })
                .collect(Collectors.joining(" + "));
    }

    /**
     * Envoltorio chico que mantiene el botón de borrar pegado al recordatorio al que pertenece.
     */
    private static final class JButtonWithIcon extends javax.swing.JButton {

        /**
         * Crea el botón X y le asocia el recordatorio que debe eliminar.
         */
        private JButtonWithIcon(Reminder reminder, Consumer<Reminder> onDelete) {
            setIcon(new AppIcon(
                    AppIcon.Glyph.CLOSE,
                    14,
                    () -> new Color(140, 145, 158)
            ));
            setToolTipText("Eliminar recordatorio");
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(0, 4, 0, 0));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            addActionListener(actionEvent -> onDelete.accept(reminder));
        }
    }
}
