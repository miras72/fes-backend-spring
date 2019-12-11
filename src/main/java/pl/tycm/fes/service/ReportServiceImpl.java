package pl.tycm.fes.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import pl.tycm.fes.model.Report;

@Service
public class ReportServiceImpl implements ReportService {

	@Override
	public void addMessage(Report report, String message) {
		List<String> messageBody = report.getMessageBody();
		messageBody.add(message);
		report.setMessageBody(messageBody);
	}

	@Override
	public String getReport(Report report) {
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		String dateString = dateFormat.format(currentDate);

		String reportMessage = "---------- RAPORT " + dateString + " ----------\t\n\t\n";

		for (String s : report.getMessageBody()) {
			reportMessage += s + "\t\n";
		}
		return reportMessage;
	}
}
