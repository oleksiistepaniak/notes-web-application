package oleksii.stepaniak.noteswebapplication.service;

import java.util.Optional;
import oleksii.stepaniak.noteswebapplication.model.User;

public interface UserService {
    User create(User user);

    void remove(Long id);

    Optional<User> getById(Long id);

    User update(Long id, User user);

    boolean existsUserByEmail(String email);
}
