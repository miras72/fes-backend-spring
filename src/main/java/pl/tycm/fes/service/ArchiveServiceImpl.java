package pl.tycm.fes.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.controller.service.EventService;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.util.MTTools;

@Service
public class ArchiveServiceImpl implements ArchiveService {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private EventService eventService;

	@Autowired
	private ReportService reportService;
	
	@Override
	public boolean archiveFiles(String workingDirectory, String archiveDirectory, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report) {

		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		Date dataTime = new Date();
		String dateFile = dateFormat.format(dataTime);
		String zipFileName = dateFile + ".zip";

		boolean isOK = true;
		
		//if (archiveDirectory.endsWith("/"))
		//	archiveDirectory = archiveDirectory.substring(0, archiveDirectory.length() - 1);

		try {
			if (!Files.isDirectory(Paths.get(archiveDirectory))) {
				logger.info("Tworzę katalog archiwum: " + archiveDirectory);
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Tworzę katalog archiwum: " + archiveDirectory + "..."));

				if (!new File(archiveDirectory).mkdirs()) {
					logger.error("Nie można utworzyć katalogu archiwum.");
					eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "Nie można utworzyć katalogu archiwum."));
					isOK = false;
					return isOK;
				}			
				logger.info("Katalog utworzony.");
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Katalog utworzony."));
			}
			FileOutputStream fos = new FileOutputStream(archiveDirectory + File.separator + zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			logger.info("Tworzę archiwum: " + zipFileName);
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Tworzę archiwum: " + zipFileName + "..."));

			for (String fileName : receiveFileList) {
				logger.info("Dodaję do archiwum plik: " + fileName);
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Dodaję do archiwum plik: " + fileName));
				
				ZipEntry zipEntry = new ZipEntry(fileName);
				zos.putNextEntry(zipEntry);
				FileInputStream in = new FileInputStream(workingDirectory + File.separator + fileName);

				int read;
				//int progressSign = 0;
				byte[] buffer = new byte[16 * 1024];
				while ((read = in.read(buffer, 0, buffer.length)) > 0) {
					zos.write(buffer, 0, read);
					/*switch (progressSign) {
					case 0:
						System.out.print("\r|");
						progressSign++;
						break;
					case 1:
						System.out.print("\r/");
						progressSign++;
						break;
					case 2:
						System.out.print("\r-");
						progressSign++;
						break;
					case 3:
						System.out.print("\r\\");
						progressSign = 0;
						break;
					}*/
				}
				in.close();
				logger.info("Plik został dodany do archiwum.");
				eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik został dodany do archiwum."));
			}
			zos.closeEntry();
			zos.close();
			logger.info("Archiwum gotowe");
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Archiwum gotowe"));
			reportService.addMessage(report, "- Pliki skopiowano do archiwum.");

		} catch (IOException ex) {	
			logger.error("StackTrace: ", ex);
			eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc() + ex.getMessage()));
			reportService.addMessage(report, "-> Błąd: Nie powiodła się operacja skopiowania plików do archiwum.");
			isOK = false;
			ex.printStackTrace();
		}
		return isOK;
	}
}
