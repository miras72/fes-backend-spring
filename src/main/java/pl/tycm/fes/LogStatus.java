package pl.tycm.fes;

public enum LogStatus {

	INFO("INFO  "),
	ERROR("ERROR "),
	FATAL("FATAL ");
	
	private String desc;
	 
	LogStatus(String desc) {
        this.desc = desc;
    }
 
    public String getDesc() {
        return desc;
    }
}
