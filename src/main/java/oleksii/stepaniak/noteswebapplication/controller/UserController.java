package oleksii.stepaniak.noteswebapplication.controller;

import java.security.Principal;
import java.util.Optional;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.dto.UserRequestDto;
import oleksii.stepaniak.noteswebapplication.dto.UserResponseDto;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.service.NoteService;
import oleksii.stepaniak.noteswebapplication.service.UserService;
import oleksii.stepaniak.noteswebapplication.service.mapper.DtoMapper;
import oleksii.stepaniak.noteswebapplication.util.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private static final String EMPTY_STRING = "";
    private final UserService userService;
    private final DtoMapper<UserResponseDto, UserRequestDto, User> userDtoMapper;
    private final NoteService noteService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getById(@PathVariable Long id, Principal principal) {
        Optional<User> receivedUser = userService.getById(id);
        if (receivedUser.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = receivedUser.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You cannot access information about"
                    + " other users!", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> remove(@PathVariable Long id, Principal principal) {
        Optional<User> userForDeletion = userService.getById(id);
        if (userForDeletion.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = userForDeletion.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You cannot remove other users!",
                    HttpStatus.UNAUTHORIZED);
        }
        noteService.removeAll(user);
        userService.remove(user.getId());
        return new ResponseEntity<>("The user was removed with success!", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody UserRequestDto userRequestDto,
                                    Principal principal) {
        Optional<User> userForUpdate = userService.getById(id);
        if (userForUpdate.isEmpty()) {
            return new ResponseEntity<>("You have entered an invalid user identifier"
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        }
        User user = userForUpdate.get();
        if (!user.getEmail().equals(principal.getName())) {
            return new ResponseEntity<>("You cannot update information of other users!",
                    HttpStatus.UNAUTHORIZED);
        }
        if (user.getEmail() != null && !user.getEmail().strip()
                .equals(EMPTY_STRING)) {
            if (EmailValidator.emailIsNotValid(user.getEmail())) {
                return new ResponseEntity<>("You have entered an invalid email!",
                        HttpStatus.BAD_REQUEST);
            } else if (userService.existsUserByEmail(userRequestDto.getEmail())) {
                return new ResponseEntity<>("A user with this email already exists",
                        HttpStatus.BAD_REQUEST);
            } else {
                user.setEmail(userRequestDto.getEmail());
            }
        }
        if (userRequestDto.getName() != null
                && !userRequestDto.getName().strip().equals(EMPTY_STRING)) {
            user.setName(userRequestDto.getName());
        } else if (userRequestDto.getPassword() != null
                && !userRequestDto.getPassword().strip().equals(EMPTY_STRING)) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }
        userService.update(id, user);
        return new ResponseEntity<>(userDtoMapper.mapToDto(user), HttpStatus.OK);
    }
}
