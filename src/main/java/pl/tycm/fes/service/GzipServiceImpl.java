package pl.tycm.fes.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.controller.service.EventService;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.util.MTTools;

@Service
public class GzipServiceImpl implements GzipService {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	
	private final EventService eventService;

	private final ReportService reportService;
	
	public GzipServiceImpl(EventService eventService, ReportService reportService) {
		this.eventService = eventService;
		this.reportService = reportService;
	}

	@Override
	public List<String> decompressGzipFile(String workingDirectory, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report) {

		List<String> newReceiveFileList = new ArrayList<>();
		
		for (String fileName : receiveFileList) {
			if (fileName.contains(".gz")) {
				FileInputStream fis = null;
				try {
					logger.info("Dekompresuje plik: " + fileName + "...");
					eventService.createEvent(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Dekompresuje plik: " + fileName + "...");
					
					fis = new FileInputStream(workingDirectory + File.separator + fileName);
					GzipCompressorInputStream gis = new GzipCompressorInputStream(fis);
					String decompressedFileName = gis.getMetaData().getFilename();
					FileOutputStream fos = new FileOutputStream(workingDirectory + File.separator + decompressedFileName);

					byte[] bytesArray = new byte[16 * 4096];
					int bytesRead = -1;
					while ((bytesRead = gis.read(bytesArray)) != -1) {
						fos.write(bytesArray, 0, bytesRead);
					}
					fos.close();
					gis.close();
					fis.close();
					
					logger.info("Plik z dekompresowany.");
					eventService.createEvent(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik z dekompresowany.");
					
					newReceiveFileList.add(decompressedFileName);
				} catch (IOException ex) {				
					logger.error("StackTrace: ", ex);
					eventService.createEvent(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage());
					reportService.addMessage(report, "-> Błąd: Nie powiodła się operacja dekompresji pliku: " + fileName);
				} finally {
					try {
						if (fis != null) {
							fis.close();
						}
					} catch (IOException e) {
						logger.fatal("StackTrace: ", e);
					}
					File file = new File(workingDirectory + File.separator + fileName);					
					logger.info("Kasuje plik: " + fileName + "...");
					eventService.createEvent(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Kasuje plik: " + fileName + "...");
					
					if (file.delete()) {						
						logger.info("Plik skasowany.");
						eventService.createEvent(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik skasowany.");
					} else {						
						logger.error("Błąd: Nie można skasować pliku: " + fileName);
						eventService.createEvent(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "Błąd: Nie można skasować pliku: " + fileName);
					}
				}
			} else {
				newReceiveFileList.add(fileName);
			}
		}
		return newReceiveFileList;
	}
}
