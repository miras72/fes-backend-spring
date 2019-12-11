package pl.tycm.fes.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "EVENTS_SPRING")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventSeq")
	@SequenceGenerator(name = "eventSeq", sequenceName = "EVENTS_SPRING_SEQ", allocationSize = 1)
	@JsonView(View.FileExchangeStatusView.class)
	private Long id;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name = "FILE_EXCHANGE_STATUS_ID")
	private FileExchangeStatus fileExchangeStatus;
	
	@Column(name = "EVENT_TEXT")
	@Size(max = 200)
	@JsonView(View.FileExchangeStatusView.class)
	private String eventText;
	
	@Column(name = "CREATED_DATA_TIME")
	private Long createdDateTime;
	
	public Event () {}
	
	public Event (FileExchangeStatus fileExchangeStatus, String eventText) {
		this.fileExchangeStatus = fileExchangeStatus;
		this.eventText = eventText;
	}
	
	@Override
	public String toString() {
		return "Event [id=" + id + ", fileExchangeStatus=" + fileExchangeStatus + ", eventText=" + eventText
				+ ", createDateTime=" + createdDateTime + "]";
	}

	@PrePersist
	protected void onCreate() {
		createdDateTime = System.currentTimeMillis();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FileExchangeStatus getFileExchangeStatus() {
		return fileExchangeStatus;
	}

	public void setFileExchangeStatus(FileExchangeStatus fileExchangeStatus) {
		this.fileExchangeStatus = fileExchangeStatus;
	}

	public String getEventText() {
		return eventText;
	}

	public void setEventText(String eventText) {
		this.eventText = eventText;
	}

	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
}
