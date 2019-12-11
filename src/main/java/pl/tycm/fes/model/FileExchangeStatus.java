package pl.tycm.fes.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "FILE_EXCHANGE_STATUS_SPRING")
public class FileExchangeStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileExchangeStatusSeq")
	@SequenceGenerator(name = "fileExchangeStatusSeq", sequenceName = "FILE_EXCHANGE_SPRING_SEQ", allocationSize = 1)
	@JsonView(View.FileExchangeStatusView.class)
	private Long id;
	
	@Column(name = "TASK_ID")
	@JsonView(View.FileExchangeStatusView.class)
	private Long taskID;
	
	@Column(name = "EVENT_DATE_TIME")
	@Size(max = 14)
	@JsonView(View.FileExchangeStatusView.class)
	private String eventDateTime;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "fileExchangeStatus", orphanRemoval = true)
	@JsonView(View.FileExchangeStatusView.class)
	@OrderBy("createdDateTime ASC, fileExchangeStatus DESC")
	private List<Event> events = new ArrayList<>();

	@Override
	public String toString() {
		return "FileExchangeStatus [id=" + id + ", taskID=" + taskID + ", eventDateTime=" + eventDateTime + ", events="
				+ events + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskID() {
		return taskID;
	}

	public void setTaskID(Long taskID) {
		this.taskID = taskID;
	}

	public String getEventDateTime() {
		return eventDateTime;
	}

	public void setEventDateTime(String eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
