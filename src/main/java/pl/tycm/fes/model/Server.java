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
@Table(name = "SERVERS_SPRING")
public class Server {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serverSeq")
	@SequenceGenerator(name = "serverSeq", sequenceName = "SERVERS_SPRING_SEQ", allocationSize = 1)
	@JsonView(View.TaskConfigView.class)
	private Long id;
	
	@NotBlank
	@Column(name = "SERVER_ADDRESS")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String serverAddress;
	
	@Column(name = "SERVER_LOGIN")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String serverLogin;
	
	@Column(name = "SERVER_PASSWORD")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String serverPassword;
	
	@Column(name = "SERVER_DIRECTORY")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String serverDirectory;
	
	@NotNull
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name = "TASK_ID")
	private TaskConfig taskConfig;

	@Override
	public String toString() {
		return "Server [id=" + id + ", serverAddress=" + serverAddress + ", serverLogin=" + serverLogin
				+ ", serverPassword=" + serverPassword + ", serverDirectory=" + serverDirectory
				+ ", taskConfig=" + taskConfig + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getServerLogin() {
		return serverLogin;
	}

	public void setServerLogin(String serverLogin) {
		this.serverLogin = serverLogin;
	}

	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	public String getServerDirectory() {
		return serverDirectory;
	}

	public void setServerDirectory(String serverDirectory) {
		this.serverDirectory = serverDirectory;
	}

	public TaskConfig getTaskConfig() {
		return taskConfig;
	}

	public void setTaskConfig(TaskConfig taskConfig) {
		this.taskConfig = taskConfig;
	}
}
