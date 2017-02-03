package blr;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static blr.Utils.*;

public class FilesIO {

    static Logger log = Logger.getLogger(AppParseExport.class.getName());

    public static void main(String[] args) {
        copyDefaultImg();
    }

    public static void uploadFtp() {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(FTP_SERVER, FTP_PORT);
            ftpClient.login(FTP_USER, FTP_PASS);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            ArrayList<Path> localFiles = new ArrayList<>();
            try (Stream<Path> paths = Files.walk(Paths.get(IMG_DIR))) {
                paths.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        localFiles.add(filePath);
                    }
                });
            }

            log.info("No. of files on localhost " + localFiles.size() + " for ftp upload");
            String[] filesOnFtp = ftpClient.listNames(SERVER_IMG_IMPORT_PATH);
            log.info("No. of files on server: " + filesOnFtp.length);

            for (Path file : localFiles) {
                if (isFileOnFtp(filesOnFtp, file)) {
                    log.trace(file.getFileName() + " exist on server, skipping it.");
                } else {
                    String remoteFile = SERVER_IMG_IMPORT_PATH + file.getFileName();
                    InputStream inputStream = new FileInputStream(file.toFile());

                    //log.trace(remoteFile + " has started uploading.");
                    boolean done = ftpClient.storeFile(remoteFile, inputStream);
                    inputStream.close();
                    if (done) {
                        log.trace(remoteFile + " file is uploaded successfully.");
                    } else {
                        log.trace(remoteFile + " file is not uploaded.");
                    }

                }
            }

        } catch (IOException ex) {
            log.error("Error: " + ex);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                log.error(ex);
            }
        }


    }

    public static boolean isFileOnFtp(String[] filesOnFtp, Path path) {
        String fileToCheck = path.getFileName().toString();
        for (String fileFromList : filesOnFtp) {
            if (fileFromList.equalsIgnoreCase(fileToCheck)) {
                return true;
            }
        }
        return false;
        //return Arrays.asList(filesOnFtp).contains(file.getFileName());
    }

    public static void download(List<ProdPojo> drl) {
        List<String> imgs = new ArrayList<>();
        for (ProdPojo p : drl) {
            imgs.add(p.getImage());
        }
        imgs = new ArrayList<>(new HashSet<String>(imgs));
        downloadListImg(imgs);

    }

    public static void downloadListImg(List<String> imgs) {
        copyDefaultImg();
        for (String img : imgs) {
            String[] fileNameUrl = img.split("/");
            String fileName = fileNameUrl[fileNameUrl.length - 1].toLowerCase();

            if (!isLocalFile(IMG_DIR + fileName)) {
                try (InputStream in = new URL(img).openStream()) {
                    Files.copy(in, Paths.get(IMG_DIR + fileName), StandardCopyOption.REPLACE_EXISTING);
                    log.trace(img + " -> " + fileName);
                } catch (Exception e) {
                    log.error(e.getCause());
                }
            } else {
                log.trace(img + " -> " + fileName + " exist on localdrive. Skiping it from download.");
            }
        }
    }

    public static void copyDefaultImg(){
        if (!isLocalFile(IMG_DIR + DEFAULT_IMG)) {
            try (InputStream in = FilesIO.class.getClassLoader().getResource(DEFAULT_IMG).openStream()) {
                Files.copy(in, Paths.get(IMG_DIR + DEFAULT_IMG), StandardCopyOption.REPLACE_EXISTING);
                log.trace(in.toString() + " -> " + DEFAULT_IMG);
            } catch (Exception e) {
                log.error(e.getCause());
            }
        } else {
            log.trace(DEFAULT_IMG + " -> " + DEFAULT_IMG + " exist on localdrive. Skiping it from download.");
        }
    }

    public static boolean isLocalFile(String path) {
        return new File(path).exists();
    }

    public static void CreatePaths() {
        List<String> pathsToCreate = Arrays.asList(CSV_DIR, WORKING_DIR, IMG_DIR);
        pathsToCreate.stream().forEach(s -> {
            File directory = new File(s);
            if (!directory.exists()) {
                directory.mkdir();
            }
        });

    }

}
