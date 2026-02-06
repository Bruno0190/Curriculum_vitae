package curriculum_vitae.cv.repository;

import curriculum_vitae.cv.model.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    Curriculum findByUserName(String name);

}
