package oleksii.stepaniak.noteswebapplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequestDto {
    private String title;
    private String content;
}
