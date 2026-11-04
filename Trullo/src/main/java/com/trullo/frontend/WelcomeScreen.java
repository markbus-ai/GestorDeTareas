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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

/**
 * Pantalla de inicio que se muestra al abrir la aplicación. Resume para qué sirve Trullo y
 * ofrece atajos directos a las secciones principales.
 */
public final class WelcomeScreen extends JPanel {

    /**
     * Recibe el callback de MainWindow para navegar al hacer click en los atajos.
     */
    public WelcomeScreen(Consumer<NavigationSection> onNavigate) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(40, 56, 40, 56));

        // Tarjeta central que contiene toda la bienvenida.
        RoundedPanel card = new RoundedPanel(
                new BorderLayout(),
                Theme::surface,
                Theme::border,
                24
        );
        card.setBorder(new EmptyBorder(46, 56, 42, 56));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Ícono de Trullo arriba del título.
        JLabel badge = new JLabel(BrandIcon.at(64));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = UiKit.themedLabel(
                "Bienvenido a Trullo",
                Font.BOLD,
                30,
                Theme::text
        );
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = UiKit.themedLabel(
                "Tu espacio personal de productividad.",
                Font.PLAIN,
                15,
                Theme::mutedText
        );
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descriptionLabel = UiKit.themedLabel(
                "Organizá tus tareas, capturá ideas y mantené todos tus recordatorios en un solo lugar.",
                Font.PLAIN,
                13,
                Theme::mutedText
        );
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Fila de botones: uno principal y dos secundarios.
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton tasksButton = UiKit.primaryButton("Ir a Mis tareas");
        tasksButton.addActionListener(actionEvent -> onNavigate.accept(NavigationSection.TASKS));

        JButton calendarButton = UiKit.secondaryButton("Abrir calendario");
        calendarButton.addActionListener(
                actionEvent -> onNavigate.accept(NavigationSection.CALENDAR)
        );

        JButton notesButton = UiKit.secondaryButton("Escribir una nota");
        notesButton.addActionListener(actionEvent -> onNavigate.accept(NavigationSection.NOTES));

        actionPanel.add(tasksButton);
        actionPanel.add(calendarButton);
        actionPanel.add(notesButton);

        // Fila de características con ícono + texto.
        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        featurePanel.setOpaque(false);
        featurePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        featurePanel.add(createFeatureLabel(AppIcon.Glyph.TASKS, "Planificá tu día"));
        featurePanel.add(createFeatureLabel(AppIcon.Glyph.CALENDAR, "No te pierdas ningún recordatorio"));
        featurePanel.add(createFeatureLabel(AppIcon.Glyph.NOTES, "Capturá ideas rápido"));

        JLabel tipLabel = UiKit.themedLabel(
                "Usá la barra lateral para moverte entre secciones cuando quieras.",
                Font.PLAIN,
                11,
                Theme::mutedText
        );
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tipLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Orden vertical de todos los bloques de la tarjeta.
        contentPanel.add(badge);
        contentPanel.add(Box.createVerticalStrut(22));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(descriptionLabel);
        contentPanel.add(Box.createVerticalStrut(28));
        contentPanel.add(actionPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(featurePanel);
        contentPanel.add(Box.createVerticalStrut(24));
        contentPanel.add(tipLabel);

        card.add(contentPanel, BorderLayout.CENTER);
        add(card);

        // Al cambiar el tema alcanza con repintar porque los labels ya se actualizan solos.
        Theme.addListener(this::repaint);
    }

    /**
     * Crea un ítem de característica: ícono de acento y texto en una sola fila.
     */
    private JPanel createFeatureLabel(AppIcon.Glyph glyph, String text) {
        JPanel feature = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        feature.setOpaque(false);

        JLabel iconLabel = new JLabel(new AppIcon(glyph, 14, Theme::accent));
        JLabel textLabel = UiKit.themedLabel(text, Font.BOLD, 11, Theme::mutedText);

        feature.add(iconLabel);
        feature.add(textLabel);
        return feature;
    }
}
