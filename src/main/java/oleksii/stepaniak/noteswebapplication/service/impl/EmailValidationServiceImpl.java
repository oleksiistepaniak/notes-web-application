package oleksii.stepaniak.noteswebapplication.service.impl;

import java.util.regex.Pattern;
import oleksii.stepaniak.noteswebapplication.service.EmailValidationService;
import org.springframework.stereotype.Service;

@Service
public class EmailValidationServiceImpl implements EmailValidationService {
    private static final String EMAIL_VALIDATION_REGEX = "^(.+)@(.+)$";
    private static final String EMPTY_STRING = "";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_VALIDATION_REGEX);

    @Override
    public boolean emailIsNotValid(String email) {
        if (email != null && !email.strip().equals(EMPTY_STRING)) {
            return !EMAIL_PATTERN.matcher(email).matches();
        }
        return true;
    }
}
