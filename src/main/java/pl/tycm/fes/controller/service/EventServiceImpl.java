package pl.tycm.fes.controller.service;

import org.springframework.stereotype.Service;

import pl.tycm.fes.model.Event;
import pl.tycm.fes.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;

	public EventServiceImpl(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public Event createEvent(Event event) {
		return eventRepository.save(event);
	}
}
