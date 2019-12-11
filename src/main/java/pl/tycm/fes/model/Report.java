package pl.tycm.fes.model;

import java.util.ArrayList;
import java.util.List;

public class Report {

	private List<String> messageBody = new ArrayList<String>();

	@Override
	public String toString() {
		return "Report [messageBody=" + messageBody + "]";
	}

	public List<String> getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(List<String> messageBody) {
		this.messageBody = messageBody;
	}
	
}
