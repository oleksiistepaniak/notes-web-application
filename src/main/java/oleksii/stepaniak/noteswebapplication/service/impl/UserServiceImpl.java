package oleksii.stepaniak.noteswebapplication.service.impl;

import java.util.Optional;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.repository.UserRepository;
import oleksii.stepaniak.noteswebapplication.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void remove(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User update(Long id, User user) {
        if (userRepository.existsById(id)) {
            return userRepository.saveAndFlush(user);
        }
        throw new RuntimeException("There is no such user " + user);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }
}
