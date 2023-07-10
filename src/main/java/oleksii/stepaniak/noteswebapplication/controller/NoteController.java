package oleksii.stepaniak.noteswebapplication.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.dto.NoteRequestDto;
import oleksii.stepaniak.noteswebapplication.dto.NoteResponseDto;
import oleksii.stepaniak.noteswebapplication.model.Note;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.service.NoteService;
import oleksii.stepaniak.noteswebapplication.service.UserService;
import oleksii.stepaniak.noteswebapplication.service.mapper.DtoMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/notes")
@AllArgsConstructor
public class NoteController {
    private static final String EMPTY_STRING = "";
    private static final String LINK = "localhost:8080/notes/";
    private final NoteService noteService;
    private final UserService userService;
    private final DtoMapper<NoteResponseDto, NoteRequestDto, Note> noteDtoMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> create(@RequestParam Long userId,
                                         @RequestBody NoteRequestDto noteRequestDto,
                                         Principal principal) {
        if (userId == null) {
            return new ResponseEntity<>("You have entered the invalid data of identifiers."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        if (noteRequestDto.getTitle() == null || noteRequestDto.getTitle().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered an invalid name."
                    + " The note name must not be empty!", HttpStatus.BAD_REQUEST);
        } else if (noteRequestDto.getContent() == null
                || noteRequestDto.getContent().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered invalid content."
                    + " The note content must not be empty!", HttpStatus.BAD_REQUEST);
        }
        Optional<User> userOwner = userService.getById(userId);
        if (userOwner.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = userOwner.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You are not authorized to create notes for"
                    + " other users!", HttpStatus.UNAUTHORIZED);
        }
        Note note = noteDtoMapper.mapToModel(noteRequestDto);
        note.setUser(user);
        Note savedNote = noteService.create(note);
        URI location = ServletUriComponentsBuilder
                .fromPath(LINK)
                .queryParam("userId", String.valueOf(userId))
                .queryParam("noteId", String.valueOf(savedNote.getId()))
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>("The note was created with success!",
                headers, HttpStatus.CREATED);
    }

    @GetMapping("/{noteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getById(@PathVariable Long noteId, @RequestParam Long userId,
                                     Principal principal) {
        if (userId == null || noteId == null) {
            return new ResponseEntity<>("You have entered the invalid data of identifiers."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        Optional<Note> receivedNote = noteService.getById(noteId);
        Optional<User> userPossessorOfReceivedNote = userService.getById(userId);
        if (receivedNote.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid identifier of the note."
                    + " The note by id " + noteId + " does not exists!", HttpStatus.BAD_REQUEST);
        }
        if (userPossessorOfReceivedNote.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = userPossessorOfReceivedNote.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You are not allowed to view notes"
                    + " of other users!", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(noteDtoMapper.mapToDto(receivedNote.get()), HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllByUserId(@RequestParam Long userId, Principal principal) {
        if (userId == null) {
            return new ResponseEntity<>("You have entered the invalid data of identifiers."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        Optional<User> userOwnerOfAllNotes = userService.getById(userId);
        if (userOwnerOfAllNotes.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = userOwnerOfAllNotes.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You are not allowed to view all notes"
                    + " of another user!", HttpStatus.UNAUTHORIZED);
        }
        List<Note> userNotes = noteService.getAllByUserId(userId);
        if (userNotes.size() == 0) {
            return new ResponseEntity<>("This user has no notes!", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userNotes.stream()
                .map(noteDtoMapper::mapToDto)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping("/{noteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> remove(@PathVariable Long noteId,
                                         @RequestParam Long userId,
                                         Principal principal) {
        if (userId == null || noteId == null) {
            return new ResponseEntity<>("You have entered the invalid data of identifiers."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        Optional<Note> noteForDeletion = noteService.getById(noteId);
        if (noteForDeletion.isEmpty()) {
            return new ResponseEntity<>("You have entered the invalid note identifier! You"
                    + " cannot delete a note that does not exist!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<User> ownerOfNoteForDeletion = userService.getById(userId);
        if (ownerOfNoteForDeletion.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = ownerOfNoteForDeletion.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You cannot remove notes of other users!",
                    HttpStatus.UNAUTHORIZED);
        }
        noteService.remove(noteId);
        return new ResponseEntity<>("The note was removed with success!", HttpStatus.OK);
    }

    @PutMapping("/{noteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@PathVariable Long noteId,
                                    @RequestParam Long userId,
                                    @RequestBody NoteRequestDto noteRequestDto,
                                    Principal principal) {
        if (userId == null || noteId == null) {
            return new ResponseEntity<>("You have entered the invalid data of identifiers."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        Optional<Note> noteForUpdate = noteService.getById(noteId);
        if (noteForUpdate.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid note identifier."
                    + " You cannot update a note that does not exist!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Note note = noteForUpdate.get();
        Optional<User> userPossessorOfUpdatedNote = userService.getById(userId);
        if (userPossessorOfUpdatedNote.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = userPossessorOfUpdatedNote.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You cannot update notes of other users!",
                    HttpStatus.UNAUTHORIZED);
        }
        if (noteRequestDto.getTitle() != null && !noteRequestDto.getTitle().equals(EMPTY_STRING)) {
            note.setTitle(noteRequestDto.getTitle());
        }
        if (noteRequestDto.getContent() != null
                && !noteRequestDto.getContent().strip().equals(EMPTY_STRING)) {
            note.setContent(noteRequestDto.getContent());
        }
        noteService.update(noteId, note);
        return new ResponseEntity<>(noteDtoMapper.mapToDto(note), HttpStatus.OK);
    }
}
