package oleksii.stepaniak.noteswebapplication.controller;

import java.security.Principal;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.service.UserService;
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
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return userService.create(user);
    }

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam Long id, Principal principal) {
        return userService.getById(id, principal);
    }

    @DeleteMapping
    public ResponseEntity<String> remove(@RequestParam Long id, Principal principal) {
        return userService.remove(id, principal);
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestParam Long id, @RequestBody User user,
                                         Principal principal) {
        return userService.update(id, user, principal);
    }
}
