package oleksii.stepaniak.noteswebapplication.service.mapper;

public interface DtoMapper<R, T, M> {
    R mapToDto(M model);

    M mapToModel(T requestDto);
}
