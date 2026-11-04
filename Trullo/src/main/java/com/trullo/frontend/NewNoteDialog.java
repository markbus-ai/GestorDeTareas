package com.trullo.frontend;

import com.trullo.controller.NoteController;
import com.trullo.model.Note;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.util.Optional;

/**
 * Diálogo para crear una nota. El controlador exige título y contenido no vacíos.
 */
public final class NewNoteDialog extends BaseDialog {

    private final NoteController noteController;
    private final JTextField titleField;
    private final JTextArea contentArea;
    // Nota creada, para devolverla a NotesScreen.
    private Note createdNote;

    /**
     * Construye el formulario con título y área de contenido.
     */
    private NewNoteDialog(Window owner, NoteController noteController) {
        super(owner, "Nueva nota", 580, 520);
        this.noteController = noteController;

        titleField = formTextField("Ponle un título a la nota");
        addFormField("Título", titleField);

        JLabel contentLabel = formLabel("Contenido");
        formPanel.add(contentLabel);
        formPanel.add(javax.swing.Box.createVerticalStrut(8));

        // Área de texto grande, dentro de un scroll con borde del tema.
        contentArea = UiKit.textArea();
        JScrollPane contentScrollPane = UiKit.scrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(Theme.border(), 1, true));
        contentScrollPane.setPreferredSize(new Dimension(100, 230));
        contentScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        contentScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        Theme.addListener(() ->
                contentScrollPane.setBorder(BorderFactory.createLineBorder(Theme.border(), 1, true))
        );

        formPanel.add(contentScrollPane);

        // Pie con Cancelar y Crear nota.
        JPanel footer = formFooter();
        JButton cancelButton = secondaryAction("Cancelar");
        JButton createButton = primaryAction("Crear nota");
        cancelButton.addActionListener(actionEvent -> dispose());
        createButton.addActionListener(actionEvent -> createNote());

        footer.add(cancelButton);
        footer.add(createButton);
        setFooter(footer);
        getRootPane().setDefaultButton(createButton);
    }

    /**
     * Abre el diálogo modal y devuelve la nota creada, si hubo una.
     */
    public static Optional<Note> open(Component parent, NoteController noteController) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        NewNoteDialog dialog = new NewNoteDialog(owner, noteController);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.createdNote);
    }

    /**
     * Valida título y contenido, arma la Note y la manda al controlador.
     */
    private void createNote() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String content = contentArea.getText() == null ? "" : contentArea.getText().trim();

        // Título obligatorio.
        if (title.isEmpty()) {
            markInvalid(titleField);
            return;
        }
        clearInvalid(titleField);

        // Contenido obligatorio.
        if (content.isEmpty()) {
            markInvalid(contentArea);
            return;
        }
        clearInvalid(contentArea);

        Note note = new Note(null, title, content);
        try {
            createdNote = noteController.create(note);
            dispose();
        } catch (RuntimeException exception) {
            UiKit.showError(this, "No se pudo crear la nota", exception);
        }
    }
}
