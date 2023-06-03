package demo.archiving.repository.entity.master;

import demo.archiving.model.entity.master.EntitySource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<EntitySource, Integer> {
}
