package pl.tycm.fes.service;

import java.util.List;

import javax.mail.MessagingException;

import pl.tycm.fes.model.MailingList;

public interface MailService {

	public void sendMail(String mailFrom, String mailSubject, List<MailingList> mailingList, String raportMessage ) throws MessagingException;
	
}
