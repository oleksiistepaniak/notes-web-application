package oleksii.stepaniak.noteswebapplication.service;

import java.util.List;
import java.util.Optional;
import oleksii.stepaniak.noteswebapplication.model.Note;
import oleksii.stepaniak.noteswebapplication.model.User;

public interface NoteService {
    Note create(Note note);

    Optional<Note> getById(Long noteId);

    List<Note> getAllByUserId(Long userId);

    void remove(Long noteId);

    void removeAll(User user);

    Note update(Long noteId, Note note);
}
