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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "TASK_CONFIG_SPRING")
public class TaskConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskConfigSeq")
	@SequenceGenerator(name = "taskConfigSeq", sequenceName = "TASK_CONFIG_SPRING_SEQ", allocationSize = 1)
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private Long id;

	@NotBlank
	@Column(name = "SUBJECT_NAME")
	@Size(max = 20)
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private String subjectName;

	@NotBlank
	@Column(name = "SUBJECT_ADDRESS")
	@Size(max = 50)
	@JsonView(View.TaskConfigView.class)
	private String subjectAddress;

	@Column(name = "SUBJECT_LOGIN")
	@Size(max = 50)
	@JsonView(View.TaskConfigView.class)
	private String subjectLogin;

	@Column(name = "SUBJECT_PASSWORD")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String subjectPassword;

	@Column(name = "SUBJECT_DIRECTORY")
	@Size(max = 150)
	@JsonView(View.TaskConfigView.class)
	private String subjectDirectory;

	@NotBlank
	@Column(name = "SUBJECT_EXCHANGE_PROTOCOL")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String subjectExchangeProtocol;

	@Column(name = "SUBJECT_ENCRYPTION_KEY_ID")
	@JsonView(View.TaskConfigView.class)
	private int subjectEncryptionKeyID;

	@NotBlank
	@Column(name = "SUBJECT_MODE")
	@Size(max = 20)
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private String subjectMode;
	
	@Column(name = "SUBJECT_LOGIN_FORM")
	@Size(max = 120)
	@JsonView(View.TaskConfigView.class)
	private String subjectLoginForm;
	
	@Column(name = "SUBJECT_LOGOUT_FORM")
	@Size(max = 50)
	@JsonView(View.TaskConfigView.class)
	private String subjectLogoutForm;
	
	@Column(name = "SUBJECT_POST_OPTIONS")
	@Size(max = 50)
	@JsonView(View.TaskConfigView.class)
	private String subjectPostOptions;
	
	@Column(name = "SUBJECT_RESPONSE_STRING")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String subjectResponseString;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "taskConfig", orphanRemoval = true)
	@JsonView(View.TaskConfigView.class)
	private List<Server> servers = new ArrayList<>();
	
	@Column(name = "SOURCE_FILE_MODE")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String sourceFileMode;
	
	@Column(name = "DECOMPRESSION_METHOD")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String decompressionMethod;
	
	@Column(name = "DECRYPTION_METHOD")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String decryptionMethod;
	
	@Column(name = "DECRYPTION_KEY_ID")
	@ColumnDefault("0")
	@JsonView(View.TaskConfigView.class)
	private int decryptionKeyID;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "taskConfig", orphanRemoval = true)
	@JsonView(View.TaskConfigView.class)
	private List<FileList> fileList = new ArrayList<>();
	
	@Column(name = "FILE_ARCHIVE")
	@ColumnDefault("0")
	@JsonView(View.TaskConfigView.class)
	private boolean fileArchive;
	
	@Column(name = "DATE_FORMAT")
	@Size(max = 20)
	@JsonView(View.TaskConfigView.class)
	private String dateFormat;
	
	@Column(name = "MINUTES")
	@Size(max = 20)
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private String minutes;
	
	@Column(name = "HOURS")
	@Size(max = 20)
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private String hours;
	
	@Column(name = "DAYS")
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private int days;
	
	@Column(name = "MONTHS")
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private int months;

	@Column(name = "SCHEDULED_IS_ACTIVE")
	@ColumnDefault("0")
	@JsonView({View.TaskStatusView.class, View.TaskConfigView.class})
	private boolean scheduledIsActive;
	
	@Column(name = "MAIL_FROM")
	@Size(max = 50)
	@JsonView(View.TaskConfigView.class)
	private String mailFrom;
	
	@Column(name = "MAIL_SUBJECT")
	@Size(max = 100)
	@JsonView(View.TaskConfigView.class)
	private String mailSubject;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "taskConfig", orphanRemoval = true)
	@JsonView(View.TaskConfigView.class)
	private List<MailingList> mailingList = new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "taskConfig")
	private TaskStatus taskStatus;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "taskConfig", orphanRemoval = true)
	private List<LocalFileList> localFileList = new ArrayList<>();
	
	@Override
	public String toString() {
		return "TaskConfig [id=" + id + ", subjectName=" + subjectName + ", subjectAddress=" + subjectAddress
				+ ", subjectLogin=" + subjectLogin + ", subjectPassword=" + subjectPassword + ", subjectDirectory="
				+ subjectDirectory + ", subjectExchangeProtocol=" + subjectExchangeProtocol
				+ ", subjectEncryptionKeyID=" + subjectEncryptionKeyID + ", subjectMode=" + subjectMode
				+ ", subjectLoginForm=" + subjectLoginForm + ", subjectLogoutForm=" + subjectLogoutForm
				+ ", subjectPostOptions=" + subjectPostOptions + ", subjectResponseString=" + subjectResponseString
				+ ", servers=" + servers + ", sourceFileMode=" + sourceFileMode + ", decompressionMethod="
				+ decompressionMethod + ", decryptionMethod=" + decryptionMethod + ", decryptionKeyID="
				+ decryptionKeyID + ", fileList=" + fileList + ", fileArchive=" + fileArchive + ", dateFormat="
				+ dateFormat + ", minutes=" + minutes + ", hours=" + hours + ", days=" + days + ", months=" + months
				+ ", scheduledIsActive=" + scheduledIsActive + ", mailFrom=" + mailFrom + ", mailSubject=" + mailSubject
				+ ", mailingList=" + mailingList + ", taskStatus=" + taskStatus + ", localFileList=" + localFileList
				+ "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectAddress() {
		return subjectAddress;
	}

	public void setSubjectAddress(String subjectAddress) {
		this.subjectAddress = subjectAddress;
	}

	public String getSubjectLogin() {
		return subjectLogin;
	}

	public void setSubjectLogin(String subjectLogin) {
		this.subjectLogin = subjectLogin;
	}

	public String getSubjectPassword() {
		return subjectPassword;
	}

	public void setSubjectPassword(String subjectPassword) {
		this.subjectPassword = subjectPassword;
	}

	public String getSubjectDirectory() {
		return subjectDirectory;
	}

	public void setSubjectDirectory(String subjectDirectory) {
		this.subjectDirectory = subjectDirectory;
	}

	public String getSubjectExchangeProtocol() {
		return subjectExchangeProtocol;
	}

	public void setSubjectExchangeProtocol(String subjectExchangeProtocol) {
		this.subjectExchangeProtocol = subjectExchangeProtocol;
	}

	public int getSubjectEncryptionKeyID() {
		return subjectEncryptionKeyID;
	}

	public void setSubjectEncryptionKeyID(int subjectEncryptionKeyID) {
		this.subjectEncryptionKeyID = subjectEncryptionKeyID;
	}

	public String getSubjectMode() {
		return subjectMode;
	}

	public void setSubjectMode(String subjectMode) {
		this.subjectMode = subjectMode;
	}

	public String getSubjectLoginForm() {
		return subjectLoginForm;
	}

	public void setSubjectLoginForm(String subjectLoginForm) {
		this.subjectLoginForm = subjectLoginForm;
	}

	public String getSubjectLogoutForm() {
		return subjectLogoutForm;
	}

	public void setSubjectLogoutForm(String subjectLogoutForm) {
		this.subjectLogoutForm = subjectLogoutForm;
	}

	public String getSubjectPostOptions() {
		return subjectPostOptions;
	}

	public void setSubjectPostOptions(String subjectPostOptions) {
		this.subjectPostOptions = subjectPostOptions;
	}

	public String getSubjectResponseString() {
		return subjectResponseString;
	}

	public void setSubjectResponseString(String subjectResponseString) {
		this.subjectResponseString = subjectResponseString;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public String getSourceFileMode() {
		return sourceFileMode;
	}

	public void setSourceFileMode(String sourceFileMode) {
		this.sourceFileMode = sourceFileMode;
	}

	public String getDecompressionMethod() {
		return decompressionMethod;
	}

	public void setDecompressionMethod(String decompressionMethod) {
		this.decompressionMethod = decompressionMethod;
	}

	public String getDecryptionMethod() {
		return decryptionMethod;
	}

	public void setDecryptionMethod(String decryptionMethod) {
		this.decryptionMethod = decryptionMethod;
	}

	public int getDecryptionKeyID() {
		return decryptionKeyID;
	}

	public void setDecryptionKeyID(int decryptionKeyID) {
		this.decryptionKeyID = decryptionKeyID;
	}

	public List<FileList> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileList> fileList) {
		this.fileList = fileList;
	}

	public boolean isFileArchive() {
		return fileArchive;
	}

	public void setFileArchive(boolean fileArchive) {
		this.fileArchive = fileArchive;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public boolean isScheduledIsActive() {
		return scheduledIsActive;
	}

	public void setScheduledIsActive(boolean scheduledIsActive) {
		this.scheduledIsActive = scheduledIsActive;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public List<MailingList> getMailingList() {
		return mailingList;
	}

	public void setMailingList(List<MailingList> mailingList) {
		this.mailingList = mailingList;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public List<LocalFileList> getLocalFileList() {
		return localFileList;
	}

	public void setLocalFileList(List<LocalFileList> localFileList) {
		this.localFileList = localFileList;
	}
}
