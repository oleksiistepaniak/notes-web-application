package oleksii.stepaniak.noteswebapplication.service;

import java.security.Principal;
import oleksii.stepaniak.noteswebapplication.model.Note;
import org.springframework.http.ResponseEntity;

public interface NoteService {
    ResponseEntity<String> create(Long userId, Note note, Principal principal);

    ResponseEntity<?> getById(Long userId, Long noteId, Principal principal, boolean isReceived);

    ResponseEntity<?> getAllByUserId(Long userId, Principal principal);

    ResponseEntity<String> remove(Long userId, Long noteId, Principal principal);

    ResponseEntity<String> update(Long userId, Long noteId, Note note, Principal principal);
}
