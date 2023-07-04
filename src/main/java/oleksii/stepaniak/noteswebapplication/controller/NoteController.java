package oleksii.stepaniak.noteswebapplication.controller;

import java.security.Principal;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.Note;
import oleksii.stepaniak.noteswebapplication.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
@AllArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<String> create(@RequestParam Long userId,
                                         @RequestBody Note note, Principal principal) {
        return noteService.create(userId, note, principal);
    }

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam Long userId,
                                     @RequestParam Long noteId, Principal principal) {
        return noteService.getById(userId, noteId, principal, true);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllByUserId(@RequestParam Long userId, Principal principal) {
        return noteService.getAllByUserId(userId, principal);
    }

    @DeleteMapping
    public ResponseEntity<String> remove(@RequestParam Long userId,
                                         @RequestParam Long noteId, Principal principal) {
        return noteService.remove(userId, noteId, principal);
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestParam Long userId, @RequestParam Long noteId,
                                         @RequestBody Note note, Principal principal) {
        return noteService.update(userId, noteId, note, principal);
    }
}
