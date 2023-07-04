package oleksii.stepaniak.noteswebapplication.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.UserDetails;
import oleksii.stepaniak.noteswebapplication.security.AuthenticationRequest;
import oleksii.stepaniak.noteswebapplication.security.JsonWebTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JsonWebTokenUtil jsonWebTokenUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest
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
}
