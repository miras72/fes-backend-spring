package pl.tycm.fes.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "FILE_LIST_SPRING")
public class FileList {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileListSeq")
	@SequenceGenerator(name = "fileListSeq", sequenceName = "FILE_LIST_SPRING_SEQ", allocationSize = 1)
	@JsonView(View.TaskConfigView.class)
	private Long id;
	
	@NotBlank
	@Column(name = "FILE_NAME")
	@Size(max = 50)
	@JsonView(View.TaskConfigView.class)
	private String fileName;
	
	@NotNull
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name = "TASK_ID")
	private TaskConfig taskConfig;

	@Override
	public String toString() {
		return "FileList [id=" + id + ", fileName=" + fileName + ", taskConfig=" + taskConfig + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public TaskConfig getTaskConfig() {
		return taskConfig;
	}

	public void setTaskConfig(TaskConfig taskConfig) {
		this.taskConfig = taskConfig;
	}
}
