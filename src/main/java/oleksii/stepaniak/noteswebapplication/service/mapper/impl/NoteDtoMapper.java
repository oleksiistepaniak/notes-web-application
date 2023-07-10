package oleksii.stepaniak.noteswebapplication.service.mapper.impl;

import oleksii.stepaniak.noteswebapplication.dto.NoteRequestDto;
import oleksii.stepaniak.noteswebapplication.dto.NoteResponseDto;
import oleksii.stepaniak.noteswebapplication.model.Note;
import oleksii.stepaniak.noteswebapplication.service.mapper.DtoMapper;
import org.springframework.stereotype.Component;

@Component
public class NoteDtoMapper implements DtoMapper<NoteResponseDto, NoteRequestDto, Note> {
    @Override
    public NoteResponseDto mapToDto(Note model) {
        NoteResponseDto responseDto = new NoteResponseDto();
        responseDto.setId(model.getId());
        responseDto.setTitle(model.getTitle());
        responseDto.setContent(model.getContent());
        responseDto.setUserId(model.getUser().getId());
        return responseDto;
    }

    @Override
    public Note mapToModel(NoteRequestDto requestDto) {
        Note note = new Note();
        note.setTitle(requestDto.getTitle());
        note.setContent(requestDto.getContent());
        return note;
    }
}
