package oleksii.stepaniak.noteswebapplication.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.dto.AuthenticationRequestDto;
import oleksii.stepaniak.noteswebapplication.dto.UserRequestDto;
import oleksii.stepaniak.noteswebapplication.dto.UserResponseDto;
import oleksii.stepaniak.noteswebapplication.model.User;
import oleksii.stepaniak.noteswebapplication.model.UserDetails;
import oleksii.stepaniak.noteswebapplication.security.JsonWebTokenUtil;
import oleksii.stepaniak.noteswebapplication.service.UserService;
import oleksii.stepaniak.noteswebapplication.service.mapper.DtoMapper;
import oleksii.stepaniak.noteswebapplication.util.EmailValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@AllArgsConstructor
public class AuthenticationController {
    private static final String EMPTY_STRING = "";
    private static final String LINK = "localhost:8080/";
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final DtoMapper<UserResponseDto, UserRequestDto, User> userDtoMapper;
    private final JsonWebTokenUtil jsonWebTokenUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDto
                                                      authenticationRequest) {
        UserDetails user;
        try {
            user = (UserDetails) userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("This username does not exist", HttpStatus.UNAUTHORIZED);
        }
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest
                            .getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("You have entered a wrong password."
                    + " Please try again!", HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> token = new HashMap<>();
        token.put("token", jsonWebTokenUtil.generateToken(user));
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequestDto userRequestDto) {
        if (EmailValidator.emailIsNotValid(userRequestDto.getEmail())) {
            return new ResponseEntity<>("You have entered an invalid email."
                    + " Please try again!", HttpStatus.BAD_REQUEST);
        } else if (userService.existsUserByEmail(userRequestDto.getEmail())) {
            return new ResponseEntity<>("A user with this email already exists",
                    HttpStatus.BAD_REQUEST);
        } else if (userRequestDto.getPassword() == null
                || userRequestDto.getPassword().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered an invalid password."
                    + " The password must not be empty!", HttpStatus.BAD_REQUEST);
        } else if (userRequestDto.getName() == null
                || userRequestDto.getName().equals(EMPTY_STRING)) {
            return new ResponseEntity<>("You have entered an invalid username."
                    + " The username must not be empty!", HttpStatus.BAD_REQUEST);
        }
        User createdUser = userService.create(userDtoMapper.mapToModel(userRequestDto));
        URI location = ServletUriComponentsBuilder
                .fromPath(LINK)
                .queryParam("id", createdUser.getId())
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>("The user was created with success!", headers, HttpStatus.OK);
    }
}
