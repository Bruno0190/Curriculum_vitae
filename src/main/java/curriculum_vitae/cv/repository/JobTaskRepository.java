package curriculum_vitae.cv.repository;

import curriculum_vitae.cv.model.JobTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTaskRepository extends JpaRepository<JobTask, Long> {

}
