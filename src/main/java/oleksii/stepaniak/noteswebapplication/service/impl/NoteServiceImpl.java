package oleksii.stepaniak.noteswebapplication.service.impl;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.Note;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.repository.NoteRepository;
import oleksii.stepaniak.noteswebapplication.service.NoteService;
import oleksii.stepaniak.noteswebapplication.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@AllArgsConstructor
public class NoteServiceImpl implements NoteService {
    private static final String EMPTY_STRING = "";
    private static final String LINK = "localhost:8080/notes/";
    private final NoteRepository noteRepository;
    private final UserService userService;

    @Override
    public ResponseEntity<String> create(Long userId, Note note, Principal principal) {
        if (note.getTitle() == null || note.getTitle().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered an invalid name."
                    + " The note name must not be empty!", HttpStatus.BAD_REQUEST);
        } else if (note.getContent() == null || note.getContent().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered invalid content."
                    + " The note content must not be empty!", HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<?> possessor = userService.getById(userId, principal);
        if (possessor.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>((String) possessor.getBody(), possessor.getStatusCode());
        } else if (possessor.getBody() == null) {
            return new ResponseEntity<>("You have entered invalid data."
                    + " Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User possessorUser = (User) possessor.getBody();
        note.setUser(possessorUser);
        noteRepository.save(note);
        URI location = ServletUriComponentsBuilder
                .fromPath(LINK)
                .queryParam("userId", String.valueOf(userId))
                .queryParam("noteId", String.valueOf(note.getId()))
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>("The note was created with success!",
                headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getById(Long userId, Long noteId,
                                     Principal principal, boolean isReceived) {
        if (userId == null || noteId == null) {
            return new ResponseEntity<>("You have entered invalid user"
                    + " id or note id.", HttpStatus.BAD_REQUEST);
        }
        Optional<Note> noteById = noteRepository.findById(noteId);
        if (noteById.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid identifier of the note."
                    + " The note by id " + noteId + " does not exists!", HttpStatus.BAD_REQUEST);
        } else if (noteById.get().isRestricted() || !isReceived) {
            ResponseEntity<?> possessor = userService.getById(userId, principal);
            if (possessor.getStatusCode() != HttpStatus.OK) {
                return new ResponseEntity<>((String) possessor.getBody(),
                        possessor.getStatusCode());
            } else if (possessor.getBody() == null) {
                return new ResponseEntity<>("You have entered invalid data."
                        + " Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(noteById.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllByUserId(Long userId, Principal principal) {
        ResponseEntity<?> possessor = userService.getById(userId, principal);
        if (possessor.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>((String) possessor.getBody(), possessor.getStatusCode());
        } else if (possessor.getBody() == null) {
            return new ResponseEntity<>("You have entered invalid data."
                    + " Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Note> userNotes = noteRepository.findAllByUserId(userId);
        if (userNotes.size() == 0) {
            return new ResponseEntity<>("This user has no notes!", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userNotes, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> remove(Long userId, Long noteId, Principal principal) {
        ResponseEntity<?> noteForDeletion = getById(userId, noteId, principal, false);
        if (noteForDeletion.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>((String) noteForDeletion.getBody(),
                    noteForDeletion.getStatusCode());
        } else if (noteForDeletion.getBody() == null) {
            return new ResponseEntity<>("You have entered invalid data."
                    + " Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        noteRepository.delete((Note) noteForDeletion.getBody());
        return new ResponseEntity<>("The note was removed with success!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> update(Long userId, Long noteId, Note note, Principal principal) {
        if (note.getId() != null) {
            return new ResponseEntity<>("It is not possible to change"
                    + " the identifier of the note!", HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<?> noteForUpdate = getById(userId, noteId, principal, false);
        if (noteForUpdate.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>((String) noteForUpdate.getBody(),
                    noteForUpdate.getStatusCode());
        } else if (noteForUpdate.getBody() == null) {
            return new ResponseEntity<>("You have entered invalid data."
                    + " Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Note updatedNote = (Note) noteForUpdate.getBody();
        if (note.getTitle() != null && !note.getTitle().equals(EMPTY_STRING)) {
            updatedNote.setTitle(note.getTitle());
        }
        if (note.getContent() != null && !note.getContent().strip().equals(EMPTY_STRING)) {
            updatedNote.setContent(note.getContent());
        }
        noteRepository.saveAndFlush(updatedNote);
        return new ResponseEntity<>("The node was updated with success!", HttpStatus.OK);
    }
}
