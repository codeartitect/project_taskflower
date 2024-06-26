package codeartitect.taskflower.routine.repository;

import codeartitect.taskflower.routine.model.Routine;
import codeartitect.taskflower.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Optional<Routine>> findAllByUser(User user);

    void deleteAllByUser(User user);
}
