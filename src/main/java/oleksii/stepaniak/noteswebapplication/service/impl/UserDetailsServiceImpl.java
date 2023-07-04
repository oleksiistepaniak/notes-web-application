package oleksii.stepaniak.noteswebapplication.service.impl;

import java.util.Optional;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.model.UserDetails;
import oleksii.stepaniak.noteswebapplication.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userByEmail = userRepository.getUserByEmail(username);
        if (userByEmail.isPresent()) {
            return new UserDetails(userByEmail.get());
        }
        throw new UsernameNotFoundException("Couldn't find user by username " + username);
    }
}
