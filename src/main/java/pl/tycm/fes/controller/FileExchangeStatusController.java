package pl.tycm.fes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import pl.tycm.fes.controller.service.FileExchangeStatusService;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.View;

@RestController
@RequestMapping("/api")
public class FileExchangeStatusController {

	private final FileExchangeStatusService fileExchangeStatusService;
	
	public FileExchangeStatusController(FileExchangeStatusService fileExchangeStatusService) {
		this.fileExchangeStatusService = fileExchangeStatusService;
	}

	@JsonView(View.FileExchangeStatusView.class)
	@GetMapping("/file-exchange-status")
	@ResponseStatus(HttpStatus.OK)
	public List<FileExchangeStatus> getFileExchangeStatusService(@RequestParam("taskID") long id, @RequestParam("eventDateTime") String eventDateTime){

		return  fileExchangeStatusService.getFileExchangeStatus(id, eventDateTime);
	}
}
