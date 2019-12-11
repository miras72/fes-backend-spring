package pl.tycm.fes.service.controller;

import pl.tycm.fes.model.ManualFileExchange;

public interface ManualFileExchangeService {

	public void startManualFileExchange(ManualFileExchange manualFileExchange);
	
	public void stopManualFileExchange(Long id);
}
