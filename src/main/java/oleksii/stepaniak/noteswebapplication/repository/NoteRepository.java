package oleksii.stepaniak.noteswebapplication.repository;

import java.util.List;
import oleksii.stepaniak.noteswebapplication.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("FROM Note n WHERE n.user.id = ?1")
    List<Note> findAllByUserId(Long userId);
}
