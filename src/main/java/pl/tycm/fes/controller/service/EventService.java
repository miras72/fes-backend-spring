package pl.tycm.fes.controller.service;

import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;

public interface EventService {

	public Event createEvent(FileExchangeStatus fileExchangeStatus, String eventText);
}
