package pl.tycm.fes.service;

import pl.tycm.fes.model.Report;

public interface ReportService {

	public void addMessage(Report report, String message);

	public String getReport(Report report);
}
