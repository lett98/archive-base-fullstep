package demo.archiving.repository.entity.arc;

import demo.archiving.model.entity.arc.EntityInsert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityInsertRepository extends JpaRepository<EntityInsert, Integer> {
}
