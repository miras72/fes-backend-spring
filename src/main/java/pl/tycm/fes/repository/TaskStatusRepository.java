package pl.tycm.fes.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pl.tycm.fes.model.TaskStatus;

@Repository
public interface TaskStatusRepository extends PagingAndSortingRepository<TaskStatus, Long> {

	List<TaskStatus> findAll(Sort sort);
	
	TaskStatus findByTaskConfigId(Long taskConfigId);
}
