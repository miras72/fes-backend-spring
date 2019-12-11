package pl.tycm.fes.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.tycm.fes.model.FileExchangeStatus;

@Repository
public interface FileExchangeStatusRepository extends CrudRepository<FileExchangeStatus, Long> {

	List<FileExchangeStatus> findByTaskIDAndEventDateTimeContainingOrderById(Long id, String eventDateTime);
}
