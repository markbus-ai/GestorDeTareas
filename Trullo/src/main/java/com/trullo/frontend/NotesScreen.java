package com.trullo.frontend;

import com.trullo.controller.NoteController;
import com.trullo.model.Note;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Pantalla de notas: grilla de tarjetas de colores con buscador y botón flotante para crear.
 */
public final class NotesScreen extends JPanel implements RefreshableScreen {

    // Controlador que lee, crea y elimina notas en el backend.
    private final NoteController noteController;
    // Panel donde se dibujan las NoteCard (o el estado vacío).
    private final JPanel notesPanel;
    private final JTextField searchField;
    // Última lista de notas traída de la base.
    private final List<Note> notes = new ArrayList<>();

    private String loadErrorMessage;

    /**
     * Arma encabezado, buscador, grilla scrolleable y botón de creación.
     */
    public NotesScreen(NoteController noteController) {
        this.noteController = noteController;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(UiKit.sectionHeader(
                "Notas",
                "Capturá ideas, recordatorios y todo lo demás."
        ));
        topPanel.add(javax.swing.Box.createVerticalStrut(16));

        // Buscador: cada cambio en el texto vuelve a aplicar el filtro.
        searchField = UiKit.textField("Buscar notas...");
        searchField.setPreferredSize(new java.awt.Dimension(100, 38));
        searchField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 38));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            /**
             * Vuelve a filtrar la grilla cuando se escribe o pega texto.
             */
            @Override
            public void insertUpdate(DocumentEvent event) {
                applyFilter();
            }

            /**
             * Vuelve a filtrar la grilla cuando se borra texto.
             */
            @Override
            public void removeUpdate(DocumentEvent event) {
                applyFilter();
            }

            /**
             * Vuelve a filtrar si cambia algún atributo del documento.
             */
            @Override
            public void changedUpdate(DocumentEvent event) {
                applyFilter();
            }
        });
        topPanel.add(searchField);
        topPanel.add(javax.swing.Box.createVerticalStrut(18));

        // Grilla de 3 columnas; ScrollablePanel la adapta al ancho del scroll.
        notesPanel = new ScrollablePanel(new GridLayout(0, 3, 16, 16));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        FloatingButton createNoteButton = new FloatingButton(
                () -> Theme.SUCCESS,
                AppIcon.Glyph.PLUS,
                54,
                "Crear nota"
        );
        createNoteButton.addActionListener(actionEvent -> openNewNoteDialog());
        bottomPanel.add(createNoteButton);

        add(topPanel, BorderLayout.NORTH);
        add(UiKit.scrollPane(notesPanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Las tarjetas de notas usan su propia paleta fija, así que alcanza con repintar
        // cuando cambia el tema. Rehacer la grilla perdería las tarjetas expandidas y
        // acumularía listeners sin beneficio visual.
        Theme.addListener(this::repaint);
    }

    /**
     * MainWindow lo llama cada vez que se entra a la pantalla.
     */
    @Override
    public void onShown() {
        loadNotes();
    }

    /**
     * Trae todas las notas del NoteController y luego aplica el filtro de búsqueda.
     */
    private void loadNotes() {
        try {
            notes.clear();
            notes.addAll(noteController.findAll());
            loadErrorMessage = null;
        } catch (RuntimeException exception) {
            loadErrorMessage = exception.getMessage();
        }
        applyFilter();
    }

    /**
     * Abre el diálogo de creación y, si se creó una nota, la suma a la lista.
     */
    private void openNewNoteDialog() {
        Optional<Note> createdNote = NewNoteDialog.open(this, noteController);
        createdNote.ifPresent(note -> {
            notes.add(note);
            applyFilter();
        });
    }

    /**
     * Elimina la nota en el backend y la saca de la lista local.
     */
    private void deleteNote(Note note) {
        try {
            noteController.delete(note.getId());
            notes.remove(note);
            applyFilter();
        } catch (RuntimeException exception) {
            UiKit.showError(this, "No se pudo eliminar la nota", exception);
        }
    }

    /**
     * Reconstruye la grilla con las notas cargadas desde el NoteController que coinciden con
     * el texto del buscador.
     */
    private void applyFilter() {
        notesPanel.removeAll();

        if (loadErrorMessage != null) {
            notesPanel.setLayout(new BorderLayout());
            notesPanel.add(UiKit.emptyState("No se pudieron cargar las notas", loadErrorMessage), BorderLayout.CENTER);
            notesPanel.revalidate();
            notesPanel.repaint();
            return;
        }

        String query = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase(Locale.ENGLISH);
        List<Note> visibleNotes = notes.stream()
                .filter(note -> matches(note, query))
                .toList();

        if (visibleNotes.isEmpty()) {
            // Puede ser que no haya notas o que ninguna coincida con la búsqueda.
            notesPanel.setLayout(new BorderLayout());
            String headline = notes.isEmpty() ? "Todavía no hay notas" : "No se encontraron notas";
            String message = notes.isEmpty()
                    ? "Creá tu primera nota con el botón +."
                    : "Probá con otra búsqueda.";
            notesPanel.add(UiKit.emptyState(headline, message), BorderLayout.CENTER);
        } else {
            // Una tarjeta por nota; el índice define el color de la tarjeta.
            notesPanel.setLayout(new GridLayout(0, 3, 16, 16));
            for (int index = 0; index < visibleNotes.size(); index++) {
                Note note = visibleNotes.get(index);
                notesPanel.add(new NoteCard(note, index, this::deleteNote));
            }
        }

        notesPanel.revalidate();
        notesPanel.repaint();
    }

    /**
     * true si el texto buscado aparece en el título o en el contenido de la nota.
     */
    private boolean matches(Note note, String query) {
        if (query.isEmpty()) {
            return true;
        }
        String title = note.getTitle() == null ? "" : note.getTitle().toLowerCase(Locale.ENGLISH);
        String content = note.getContent() == null ? "" : note.getContent().toLowerCase(Locale.ENGLISH);
        return title.contains(query) || content.contains(query);
    }
}
