package pl.tycm.fes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.tycm.fes.model.ManualFileExchange;
import pl.tycm.fes.service.controller.ManualFileExchangeService;

@RestController
@RequestMapping("/api")
public class ManualFileExchangeController {

	@Autowired
	private ManualFileExchangeService manualFileExchangeService;
	
	@PostMapping("/file")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public void startManualFileExchange(@RequestBody ManualFileExchange manualFileExchange) {

		manualFileExchangeService.startManualFileExchange(manualFileExchange);
	}
	
	@DeleteMapping("/file/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public void deleteTaskConfig(@PathVariable long id) {

		manualFileExchangeService.stopManualFileExchange(id);
	}
}
