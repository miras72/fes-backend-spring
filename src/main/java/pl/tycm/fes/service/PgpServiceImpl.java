package pl.tycm.fes.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.LogStatus;
import pl.tycm.fes.model.Event;
import pl.tycm.fes.model.FileExchangeStatus;
import pl.tycm.fes.model.Report;
import pl.tycm.fes.service.controller.EventService;
import pl.tycm.fes.util.MTTools;

@Service
public class PgpServiceImpl implements PgpService {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private EventService eventService;

	@Autowired
	private ReportService reportService;

	@Override
	public List<String> decryptPGPFile(String workingDirectory, byte[] decryptionKey, List<String> receiveFileList,
			FileExchangeStatus fileExchangeStatus, Report report) {

		List<String> newReceiveFileList = new ArrayList<>();

		for (String fileName : receiveFileList) {

			if (fileName.contains(".pgp")) {
				String outputFileName = fileName.replace(".pgp", "");

				logger.info("Dekrypcja pliku: " + fileName + "...");
				eventService.createEvent(new Event(fileExchangeStatus,
						MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Dekrypcja pliku: " + fileName + "..."));

				InputStream keyIn = new ByteArrayInputStream(decryptionKey);

				FileInputStream in = null;
				FileOutputStream out = null;
				try {
					in = new FileInputStream(workingDirectory + File.separator + fileName);
					out = new FileOutputStream(workingDirectory + File.separator + outputFileName);

					char[] passwd = null;

					decryptFile(in, keyIn, passwd, workingDirectory, out);

					logger.info("Plik z dekryptowany.");
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik z dekryptowany."));

					in.close();
					out.close();
					newReceiveFileList.add(outputFileName);
				} catch (PGPException ex) {
					logger.error("StackTrace: ", ex);
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage()));
					reportService.addMessage(report, "-> Błąd: Nie powiodła się operacja dekrypcji pliku: " + fileName);
				} catch (IllegalArgumentException ex) {
					logger.error("StackTrace: ", ex);
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage()));
					reportService.addMessage(report, "-> Błąd: Nie powiodła się operacja dekrypcji pliku: " + fileName);
				} catch (NoSuchProviderException ex) {
					logger.error("StackTrace: ", ex);
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage()));
					reportService.addMessage(report, "-> Błąd: Nie powiodła się operacja dekrypcji pliku: " + fileName);
				} catch (IOException ex) {
					logger.error("StackTrace: ", ex);
					eventService.createEvent(new Event(fileExchangeStatus,
							MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage()));
					reportService.addMessage(report, "-> Błąd: Nie powiodła się operacja dekrypcji pliku: " + fileName);
				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} catch (IOException ex) {
						logger.error("StackTrace: ", ex);
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage()));
					}
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException ex) {
						logger.error("StackTrace: ", ex);
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage()));
					}
					try {
						if (keyIn != null) {
							keyIn.close();
						}
					} catch (IOException ex) {
						logger.error("StackTrace: ", ex);
						eventService.createEvent(new Event(fileExchangeStatus,
								MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "ERROR " + ex.getMessage()));
					}
					File file = new File(workingDirectory + File.separator + fileName);					
					logger.info("Kasuje plik: " + fileName + "...");
					eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Kasuje plik: " + fileName + "..."));
					
					if (file.delete()) {						
						logger.info("Plik skasowany.");
						eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.INFO.getDesc() + "Plik skasowany."));
					} else {						
						logger.error("Błąd: Nie można skasować pliku: " + fileName);
						eventService.createEvent(new Event(fileExchangeStatus, MTTools.getLogDate() + LogStatus.ERROR.getDesc() + "Błąd: Nie można skasować pliku: " + fileName));
					}
				}

			} else {
				newReceiveFileList.add(fileName);
			}
		}
		return newReceiveFileList;
	}

	/**
	 * decrypt the passed in message stream
	 */
	private void decryptFile(InputStream in, InputStream keyIn, char[] passwd, String directory, OutputStream out)
			throws IOException, NoSuchProviderException, PGPException, IllegalArgumentException {

		Security.addProvider(new BouncyCastleProvider());
		in = PGPUtil.getDecoderStream(in);

		try {
			JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
			PGPEncryptedDataList enc;

			Object o = pgpF.nextObject();
			//
			// the first object might be a PGP marker packet.
			//
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			//
			// find the secret key
			//
			Iterator<?> it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;
			PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn),
					new JcaKeyFingerprintCalculator());

			while (sKey == null && it.hasNext()) {
				pbe = (PGPPublicKeyEncryptedData) it.next();

				sKey = this.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
			}

			if (sKey == null) {
				throw new IllegalArgumentException("Secret key for message not found.");
			}

			InputStream clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));

			JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);

			PGPCompressedData cData = (PGPCompressedData) plainFact.nextObject();

			InputStream compressedStream = new BufferedInputStream(cData.getDataStream());
			JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(compressedStream);

			Object message = pgpFact.nextObject();

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;
				InputStream unc = ld.getInputStream();

				byte[] bytesArray = new byte[16 * 4096];
				int bytesRead = -1;
				// int progressSign = 0;
				while ((bytesRead = unc.read(bytesArray)) != -1) {
					out.write(bytesArray, 0, bytesRead);
					/*
					 * switch (progressSign) { case 0: System.out.print("\r|"); progressSign++;
					 * break; case 1: System.out.print("\r/"); progressSign++; break; case 2:
					 * System.out.print("\r-"); progressSign++; break; case 3:
					 * System.out.print("\r\\"); progressSign = 0; break; }
					 */
				}
			} else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("Encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException("Message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					throw new PGPException("Błąd: Kontroli integralności pliku");
				} else {
					logger.info("OK: Kontrola integralności pliku");
				}
			} else {
				// logger.info("Brak kontroli integralności pliku");
			}
		} catch (PGPException e) {
			logger.error("StackTrace: ", e);
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}
		}
	}

	/**
	 * Search a secret key ring collection for a secret key corresponding to keyID
	 * if it exists.
	 * 
	 * @param pgpSec
	 *            a secret key ring collection.
	 * @param keyID
	 *            keyID we want.
	 * @param pass
	 *            passphrase to decrypt secret key with.
	 * @return the private key.
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	private PGPPrivateKey findSecretKey(PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass)
			throws PGPException, NoSuchProviderException {
		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

		if (pgpSecKey == null) {
			return null;
		}
		return pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass));
	}
}
