package pl.tycm.fes.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "SERVER_CONFIG_SPRING")
public class ServerConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serverConfigSeq")
	@SequenceGenerator(name = "serverConfigSeq", sequenceName = "SERVER_CONFIG_SPRING_SEQ", allocationSize = 1)
	private Long id;
	
	@NotBlank
	@Column(name = "WORK_DIRECTORY")
	@Size(max = 50)
	private String workDirectory;
	
	@NotBlank
	@Column(name = "ARCHDIRECTORY")
	@Size(max = 50)
	private String archDirectory;

	@Override
	public String toString() {
		return "ServerConfig [id=" + id + ", workDirectory=" + workDirectory + ", archDirectory=" + archDirectory + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorkDirectory() {
		return workDirectory;
	}

	public void setWorkDirectory(String workDirectory) {
		this.workDirectory = workDirectory;
	}

	public String getArchDirectory() {
		return archDirectory;
	}

	public void setArchDirectory(String archDirectory) {
		this.archDirectory = archDirectory;
	}
}
