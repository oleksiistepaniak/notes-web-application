package oleksii.stepaniak.noteswebapplication.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.Note;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.repository.NoteRepository;
import oleksii.stepaniak.noteswebapplication.service.NoteService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;

    @Override
    public Note create(Note note) {
        return noteRepository.save(note);
    }

    @Override
    public Optional<Note> getById(Long noteId) {
        return noteRepository.findById(noteId);
    }

    @Override
    public List<Note> getAllByUserId(Long userId) {
        return noteRepository.findAllByUserId(userId);
    }

    @Override
    public void remove(Long noteId) {
        noteRepository.deleteById(noteId);
    }

    @Override
    public void removeAll(User user) {
        noteRepository.deleteAllByUser(user);
    }

    @Override
    public Note update(Long noteId, Note note) {
        if (noteRepository.existsById(noteId)) {
            return noteRepository.saveAndFlush(note);
        }
        throw new RuntimeException("There is no such note " + note);
    }
}
