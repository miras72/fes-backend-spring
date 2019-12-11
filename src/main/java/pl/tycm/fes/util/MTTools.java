package pl.tycm.fes.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import pl.tycm.fes.model.FileList;
import pl.tycm.fes.model.TaskConfig;

public class MTTools {

	public static List<FileList> getConvertFileList(List<FileList> fileList, String filedate, String datePattern,
			String exchangeProtocol) {

		//final Logger logger = Logger.getLogger("MTTools");

		List<FileList> convertFileList = new ArrayList<FileList>();

		if (datePattern == null || datePattern.isEmpty())
			datePattern = "ddMMyyyy";

		if (filedate == null || filedate.isEmpty()) {
			try {
				DateFormat dateFormat = new SimpleDateFormat(datePattern);
				Date dataTime = new Date();
				filedate = dateFormat.format(dataTime);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Niepoprawny format daty. Dopuszczalne formaty: ddMMyyyy, yyyyMMdd");
				//logger.fatal("Niepoprawny format daty. Dopuszczalne formaty: ddMMyyyy, yyyyMMdd");
			}

		}
		if (datePattern.equals("yyyyMMdd")) {
			String dayString = filedate.substring(0, 2);
			String monthString = filedate.substring(2, 4);
			String yearString = filedate.substring(4, 8);
			filedate = yearString + monthString + dayString;
		}

		for (FileList fileListModel : fileList) {
			if (fileListModel.getFileName().contains("<data>")) {
				String fileName = fileListModel.getFileName().replace("<data>", filedate);
				fileListModel.setFileName(fileName);
				convertFileList.add(fileListModel);
			} else
				convertFileList.add(fileListModel);
		}

		if (exchangeProtocol.equals("ssl")) {

			List<FileList> arrayFileListTmp = new ArrayList<FileList>();

			for (FileList fileListModel : convertFileList) {
				if (fileListModel.getFileName().contains("?")) {
					for (int j = 1; j < 10; j++) {
						FileList tmpfileListModel = new FileList();
						String fileName = fileListModel.getFileName().replaceFirst(Pattern.quote("?"),
								Integer.toString(j));
						tmpfileListModel.setFileName(fileName);
						arrayFileListTmp.add(tmpfileListModel);
					}
					FileList tmpfileListModel = new FileList();
					String fileName = fileListModel.getFileName().replaceFirst(Pattern.quote("?"), "0");
					tmpfileListModel.setFileName(fileName);
					arrayFileListTmp.add(tmpfileListModel);

				} else {
					FileList tmpfileListModel = new FileList();
					String fileName = fileListModel.getFileName();
					tmpfileListModel.setFileName(fileName);
					arrayFileListTmp.add(tmpfileListModel);
				}
			}
			convertFileList.clear();
			convertFileList.addAll(arrayFileListTmp);
		}
		return convertFileList;
	}

	public static String getLogDate() {

		DateFormat logDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss,SSS");
		Date logDataTime = new Date();
		String logDate = logDateFormat.format(logDataTime);

		return logDate + " ";
	}

	public static String getConvertUrlForm(String url, String remoteLogin, String remotePassword, String loginForm) {
		String urlForm;
		String insertLogin;
		String insertPassword;

		insertLogin = loginForm.replace("$Login", remoteLogin);
		insertPassword = insertLogin.replace("$Haslo", remotePassword);

		urlForm = url + insertPassword;
		return urlForm;
	}

	public static String dayOfWeekScheduleExpression(TaskConfig taskConfig) {

		String daysOfWeek = null;

		if ((taskConfig.getDays() % 2) >= 1) {
			daysOfWeek = "Mon";
		}

		if ((taskConfig.getDays() % 4) >= 2) {
			if (daysOfWeek == null)
				daysOfWeek = "Tue";
			else
				daysOfWeek = daysOfWeek + ",Tue";
		}

		if ((taskConfig.getDays() % 8) >= 4) {
			if (daysOfWeek == null)
				daysOfWeek = "Wed";
			else
				daysOfWeek = daysOfWeek + ",Wed";
		}

		if ((taskConfig.getDays() % 16) >= 8) {
			if (daysOfWeek == null)
				daysOfWeek = "Thu";
			else
				daysOfWeek = daysOfWeek + ",Thu";
		}

		if ((taskConfig.getDays() % 32) >= 16) {
			if (daysOfWeek == null)
				daysOfWeek = "Fri";
			else
				daysOfWeek = daysOfWeek + ",Fri";
		}

		if ((taskConfig.getDays() % 64) >= 32) {
			if (daysOfWeek == null)
				daysOfWeek = "Sat";
			else
				daysOfWeek = daysOfWeek + ",Sat";
		}

		if ((taskConfig.getDays() % 128) >= 64) {
			if (daysOfWeek == null)
				daysOfWeek = "Sun";
			else
				daysOfWeek = daysOfWeek + ",Sun";
		}

		if (daysOfWeek == null)
			daysOfWeek = "*";
		return daysOfWeek;
	}

	public static String monthScheduleExpression(TaskConfig taskConfig) {

		String month = null;

		if ((taskConfig.getMonths() % 2) >= 1) {
			month = "Jan";
		}

		if ((taskConfig.getMonths() % 4) >= 2) {
			if (month == null)
				month = "Feb";
			else
				month = month + ",Feb";
		}

		if ((taskConfig.getMonths() % 8) >= 4) {
			if (month == null)
				month = "Mar";
			else
				month = month + ",Mar";
		}

		if ((taskConfig.getMonths() % 16) >= 8) {
			if (month == null)
				month = "Apr";
			else
				month = month + ",Apr";
		}

		if ((taskConfig.getMonths() % 32) >= 16) {
			if (month == null)
				month = "May";
			else
				month = month + ",May";
		}

		if ((taskConfig.getMonths() % 64) >= 32) {
			if (month == null)
				month = "Jun";
			else
				month = month + ",Jun";
		}

		if ((taskConfig.getMonths() % 128) >= 64) {
			if (month == null)
				month = "Jul";
			else
				month = month + ",Jul";
		}

		if ((taskConfig.getMonths() % 256) >= 128) {
			if (month == null)
				month = "Aug";
			else
				month = month + ",Aug";
		}

		if ((taskConfig.getMonths() % 512) >= 256) {
			if (month == null)
				month = "Sep";
			else
				month = month + ",Sep";
		}

		if ((taskConfig.getMonths() % 1024) >= 512) {
			if (month == null)
				month = "Oct";
			else
				month = month + ",Oct";
		}

		if ((taskConfig.getMonths() % 2048) >= 1024) {
			if (month == null)
				month = "Nov";
			else
				month = month + ",Nov";
		}

		if ((taskConfig.getMonths() % 4096) >= 2048) {
			if (month == null)
				month = "Dec";
			else
				month = month + ",Dec";
		}

		if (month == null)
			month = "*";
		return month;
	}
}
