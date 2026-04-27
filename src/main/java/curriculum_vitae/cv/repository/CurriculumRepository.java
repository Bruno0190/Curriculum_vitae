package curriculum_vitae.cv.repository;

import curriculum_vitae.cv.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    Curriculum findByUserName(String name);
    List<Curriculum> findByUserNameContainingIgnoreCase(String name);
    Curriculum findByUserEmail(String email);
    Curriculum findByUser(User user);

}
