package oleksii.stepaniak.noteswebapplication.service.impl;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.Note;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.repository.NoteRepository;
import oleksii.stepaniak.noteswebapplication.repository.UserRepository;
import oleksii.stepaniak.noteswebapplication.service.EmailValidationService;
import oleksii.stepaniak.noteswebapplication.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String EMPTY_STRING = "";
    private static final String LINK = "localhost:8080/users/";
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final EmailValidationService emailValidationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> create(User user) {
        if (emailValidationService.emailIsNotValid(user.getEmail())) {
            return new ResponseEntity<>("You have entered an invalid email."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        } else if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("A user with this email already exists",
                    HttpStatus.BAD_REQUEST);
        } else if (user.getPassword() == null || user.getPassword().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered an invalid password."
                    + " The password must not be empty!", HttpStatus.BAD_REQUEST);
        } else if (user.getName() == null || user.getName().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered an invalid username."
                    + " The username must not be empty!", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromPath(LINK)
                .queryParam("id", user.getId())
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>("The user was created with success!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> remove(Long id, Principal principal) {
        ResponseEntity<?> userForDeletion = getById(id, principal);
        if (userForDeletion.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>((String) userForDeletion.getBody(),
                    userForDeletion.getStatusCode());
        } else if (userForDeletion.getBody() == null) {
            return new ResponseEntity<>("You have entered invalid data. Please try again!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = (User) userForDeletion.getBody();
        List<Note> userNotes = noteRepository.findAllByUserId(user.getId());
        noteRepository.deleteAll(userNotes);
        userRepository.deleteById(user.getId());
        return new ResponseEntity<>("The user was removed with success!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getById(Long id, Principal principal) {
        if (id == null) {
            return new ResponseEntity<>("You have entered an invalid identifier of the user."
                    + " The user by id " + id + " does not exists!", HttpStatus.BAD_REQUEST);
        }
        Optional<User> userById = userRepository.findById(id);
        if (userById.isEmpty()) {
            return new ResponseEntity<>("This user does not exist.", HttpStatus.BAD_REQUEST);
        } else if (!userById.get().getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("Your token is invalid for"
                    + " performing this action", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userById.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> update(Long id, User user, Principal principal) {
        ResponseEntity<?> userForUpdate = getById(id, principal);
        if (userForUpdate.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>((String) userForUpdate.getBody(),
                    userForUpdate.getStatusCode());
        } else if (userForUpdate.getBody() == null) {
            return new ResponseEntity<>("You have entered invalid data."
                    + " Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User updatedUser = (User) userForUpdate.getBody();
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().strip()
                .equals(EMPTY_STRING)) {
            if (emailValidationService.emailIsNotValid(updatedUser.getEmail())) {
                return new ResponseEntity<>("You have entered an invalid email!",
                        HttpStatus.BAD_REQUEST);
            } else if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
                return new ResponseEntity<>("A user with this email already exists",
                        HttpStatus.BAD_REQUEST);
            } else {
                updatedUser.setEmail(user.getEmail());
            }
        }
        if (user.getName() != null && !user.getName().strip().equals(EMPTY_STRING)) {
            updatedUser.setName(user.getName());
        } else if (user.getPassword() != null && !user.getPassword().strip().equals(EMPTY_STRING)) {
            updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.saveAndFlush(updatedUser);
        return new ResponseEntity<>("The user was updated with success!", HttpStatus.OK);
    }
}
