package oleksii.stepaniak.noteswebapplication.repository;

import java.util.Optional;
import oleksii.stepaniak.noteswebapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
