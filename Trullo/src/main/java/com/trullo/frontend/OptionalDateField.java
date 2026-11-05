package com.trullo.frontend;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.util.Date;

/**
 * Fila de formulario para una fecha opcional: un checkbox habilita o deshabilita el selector.
 */
final class OptionalDateField extends JPanel {

    private final JCheckBox enabledCheckBox;
    private final JSpinner dateSpinner;

    /**
     * Arma la fila con el texto del checkbox y el selector deshabilitado.
     */
    OptionalDateField(String checkBoxText) {
        super(new BorderLayout(12, 0));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        enabledCheckBox = new JCheckBox(checkBoxText);
        enabledCheckBox.setOpaque(false);
        enabledCheckBox.setFocusPainted(false);
        enabledCheckBox.setFont(UiKit.font(Font.PLAIN, 13));
        enabledCheckBox.setForeground(Theme.text());
        enabledCheckBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Theme.addListener(() -> enabledCheckBox.setForeground(Theme.text()));

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setFont(UiKit.font(Font.PLAIN, 13));
        dateSpinner.setEnabled(false);
        dateSpinner.setPreferredSize(new Dimension(150, 34));

        enabledCheckBox.addActionListener(actionEvent ->
                dateSpinner.setEnabled(enabledCheckBox.isSelected())
        );

        add(enabledCheckBox, BorderLayout.CENTER);
        add(dateSpinner, BorderLayout.EAST);
    }

    /**
     * Devuelve la fecha elegida, o null si el checkbox no está marcado.
     */
    LocalDate getDate() {
        if (!enabledCheckBox.isSelected()) {
            return null;
        }
        return UiKit.toLocalDate((Date) dateSpinner.getValue());
    }
}
