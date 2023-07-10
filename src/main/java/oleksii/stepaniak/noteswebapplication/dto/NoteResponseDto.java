package oleksii.stepaniak.noteswebapplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long userId;
}
