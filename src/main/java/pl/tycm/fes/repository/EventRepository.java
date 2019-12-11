package pl.tycm.fes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.tycm.fes.model.Event;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

}
