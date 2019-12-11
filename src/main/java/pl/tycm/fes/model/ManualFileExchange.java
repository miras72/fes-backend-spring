package pl.tycm.fes.model;

import java.util.List;

public class ManualFileExchange {

	private String eventDateTime;
	private Long taskID;
	private String fileDate;

	private List<FileList> fileList;

	@Override
	public String toString() {
		return "ManualFileExchange [eventDateTime=" + eventDateTime + ", taskID=" + taskID + ", fileDate=" + fileDate
				+ ", fileList=" + fileList + "]";
	}

	public String getEventDateTime() {
		return eventDateTime;
	}

	public void setEventDateTime(String eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

	public Long getTaskID() {
		return taskID;
	}

	public void setTaskID(Long taskID) {
		this.taskID = taskID;
	}

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	public List<FileList> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileList> fileList) {
		this.fileList = fileList;
	}
}
