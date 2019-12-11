package pl.tycm.fes.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import pl.tycm.fes.model.MailingList;

@Service
public class MailServiceImpl implements MailService {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void sendMail(String mailSubject, String mailFrom, List<MailingList> mailingList, String raportMessage) throws MessagingException {
		logger.info("Wysyłam raport....");
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		String[] addressTo = new String[mailingList.size()];
		int i = 0;
		for (MailingList mailling : mailingList) {
			addressTo[i] = new String(mailling.getRecipientName());
			i++;
		}
		helper.setTo(addressTo);
		helper.setSubject(mailSubject);
		helper.setFrom(mailFrom);
		helper.setText(raportMessage);
		javaMailSender.send(message);
		logger.info("Raport został wysłany.");
	}
}
