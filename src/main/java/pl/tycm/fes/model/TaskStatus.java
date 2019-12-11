package pl.tycm.fes.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "TASK_STATUS_SPRING")
public class TaskStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskStatusSeq")
	@SequenceGenerator(name = "taskStatusSeq", sequenceName = "TASK_STATUS_SPRING_SEQ", allocationSize = 1)
	@JsonView(View.TaskStatusView.class)
	private Long id;

	@NotNull
	@OneToOne
	@JoinColumn(name = "TASK_ID")
	@JsonView(View.TaskStatusView.class)
	private TaskConfig taskConfig;

	@Column(name = "LAST_STATUS")
	@Size(max = 20)
	@ColumnDefault("'Pending'")
	@JsonView(View.TaskStatusView.class)
	private String lastStatus;
	
	@Column(name = "LAST_DATA_STATUS")
	@Size(max = 20)
	@JsonView(View.TaskStatusView.class)
	private String lastDataStatus;

	@Column(name = "NEXT_SCHEDULED_DATE")
	@Size(max = 20)
	@JsonView(View.TaskStatusView.class)
	private String nextScheduledDate;

	/*@Column(name = "SCHEDULED_IS_ACTIVE")
	@ColumnDefault("0")
	@JsonView(View.TaskStatusView.class)
	private boolean scheduledIsActive;*/

	@Override
	public String toString() {
		return "TaskStatus [id=" + id + ", taskConfig=" + taskConfig + ", lastStatus=" + lastStatus
				+ ", lastDataStatus=" + lastDataStatus + ", nextScheduledDate=" + nextScheduledDate + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TaskConfig getTaskConfig() {
		return taskConfig;
	}

	public void setTaskConfig(TaskConfig taskConfig) {
		this.taskConfig = taskConfig;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public String getLastDataStatus() {
		return lastDataStatus;
	}

	public void setLastDataStatus(String lastDataStatus) {
		this.lastDataStatus = lastDataStatus;
	}

	public String getNextScheduledDate() {
		return nextScheduledDate;
	}

	public void setNextScheduledDate(String nextScheduledDate) {
		this.nextScheduledDate = nextScheduledDate;
	}
}
