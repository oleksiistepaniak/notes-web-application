package oleksii.stepaniak.noteswebapplication.service;

import java.security.Principal;
import oleksii.stepaniak.noteswebapplication.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<String> create(User user);

    ResponseEntity<String> remove(Long id, Principal principal);

    ResponseEntity<?> getById(Long id, Principal principal);

    ResponseEntity<String> update(Long id, User user, Principal principal);
}
