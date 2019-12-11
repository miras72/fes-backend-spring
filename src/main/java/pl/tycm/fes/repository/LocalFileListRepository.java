package pl.tycm.fes.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.tycm.fes.model.LocalFileList;

@Repository
public interface LocalFileListRepository extends CrudRepository<LocalFileList, Long> {

	List<LocalFileList> findByTaskConfigId(Long taskConfigId);
}
