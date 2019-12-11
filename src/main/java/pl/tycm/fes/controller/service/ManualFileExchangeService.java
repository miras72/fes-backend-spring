package pl.tycm.fes.controller.service;

import pl.tycm.fes.model.ManualFileExchange;

public interface ManualFileExchangeService {

	public void startManualFileExchange(ManualFileExchange manualFileExchange);
	
	public void stopManualFileExchange(Long id);
}
