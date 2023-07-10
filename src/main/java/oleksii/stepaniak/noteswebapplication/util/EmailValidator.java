package oleksii.stepaniak.noteswebapplication.util;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    private static final String EMAIL_VALIDATION_REGEX = "^(.+)@(.+)$";
    private static final String EMPTY_STRING = "";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_VALIDATION_REGEX);

    public static boolean emailIsNotValid(String email) {
        if (email != null && !email.strip().equals(EMPTY_STRING)) {
            return !EMAIL_PATTERN.matcher(email).matches();
        }
        return true;
    }
}
