package pl.tycm.fes.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.model.Event;
import pl.tycm.fes.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;

	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public Event createEvent(Event event) {
		return eventRepository.save(event);
	}
}
