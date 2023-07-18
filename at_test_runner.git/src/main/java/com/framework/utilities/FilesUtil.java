package com.framework.utilities;

import com.framework.DynamicTest;
import com.framework.services.MyConfig;
import com.framework.services.PropertiesService;
import com.framework.services.ScreenshotService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FilesUtil {
    private static volatile FilesUtil instance = null;
    private static Logger log = Logger.getLogger("FILE");
    private static Logger logNetwork = Logger.getLogger("NETWORK");
    private static Properties propFTP = PropertiesService.readProperties("configuration\\ftpConfig.properties");
    private static List<String> filesListInDir = new ArrayList<>();

    private FilesUtil() {

    }

    public static FilesUtil init() {
        if (instance == null) {
            synchronized (FilesUtil.class) {
                if (instance == null) {
                    instance = new FilesUtil();
                }
            }
        }
        return instance;
    }

    /**
     * Getting file name of datatable by splitting the path per "\" and taking the last index value
     *
     * @return strTitle as datatable file name
     */
    public synchronized String getDatatableFileName() {
        String[] arrSplitPathDatatable = MyConfig.strPathDatatable.replace("/", "\\\\").split("\\\\");
        String strTitle = arrSplitPathDatatable[arrSplitPathDatatable.length - 1].replace(".xlsx", "").replace(".xlsm", "");

        return strTitle;
    }

    /**
     * Getting file of datatable with file type by splitting the path per "\" and taking the last index value
     *
     * @return strTitle as datatable file name with file type
     */
    public synchronized String getDatatableFile() {
        String[] arrSplitPathDatatable = MyConfig.strPathDatatable.split("\\\\");
        String strFile = arrSplitPathDatatable[arrSplitPathDatatable.length - 1];

        return strFile;
    }

    /**
     * Extract file using AHK and put it into AutoHotKey directory
     *
     * @param strFileName
     * @throws IOException
     */
    public synchronized String extractFileAHK(String strFileName) throws IOException {
        String strPath = sourcePath() + "AutoHotKey\\";
        String strFilePath = strPath + strFileName;
        File isExist = new File(strFilePath);
        if (!isExist.exists()) {
            URL resourceAHK = PropertiesService.class.getClassLoader().getResource(strFileName);
            File folderAHK = new File(strPath);
            if (!folderAHK.exists()) {
                folderAHK.mkdirs();
                System.out.println("AutoHotKey has been created! !\n");
            }
            isExist.createNewFile();
            FileUtils.copyURLToFile(resourceAHK, isExist);
        }
        return strFilePath;
    }

    /**
     * Copy file to folder into folder
     * ex:
     * FilesUtil.copyFile("Log/log.log",
     * MyConfig.strPathDatatable + FilesUtil.getDatatableFileName()+"\\Report\\Log\\","log.log");
     *
     * @param strPathSource
     * @param strPathFileName
     * @param strFileName
     */
    public synchronized void copyFile(String strPathSource, String strPathFileName, String strFileName) {
        String strPathDestination = strPathFileName + strFileName;
        File file = new File(strPathFileName);

        if (!file.exists()) {
            file.mkdirs();
        }
        File fleSource = new File(strPathSource);
        File fleDestination = new File(strPathDestination);

        try {
            Files.copy(fleSource.toPath(), fleDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.info("Failed copy file from " + strPathSource + " to " + strPathDestination);
            throw new RuntimeException("Failed copy file from " + strPathSource + " to " + strPathDestination);
        }
    }

    public synchronized void deleteFile(String strPathSource, Logger log) {
        try {
            log.getAppender(log.getName()).close();
            Files.delete(Paths.get(strPathSource));
        } catch (Exception e) {
            log.info("File cannot access or not found. On " + strPathSource);
            throw new RuntimeException("File cannot access or not found. On " + strPathSource);
        }

    }

    /**
     * Call this if you want to delete folder
     *
     * @param file
     */
    public synchronized void deleteFolder(File file){
        if (file.exists()) {
            deleteDirectory(file);
            file.delete();
        }
    }

    /**
     * Delete Folder for recursive
     * @param file
     */
    public synchronized void deleteDirectory(File file) {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory())
                deleteDirectory(subfile);
            subfile.delete();
        }
    }


    /**
     * extrack file AHK from parametered path
     *
     * @param strPath
     * @param strFileName
     * @throws IOException
     */
    public synchronized void extractFileAHK(String strPath, String strFileName) throws IOException {
        File isExist = new File(strPath + strFileName);
        if (!isExist.exists()) {
            URL resourceAHK = PropertiesService.class.getClass().getClassLoader().getResource(strFileName);
            File folderAHK = new File(strPath);
            if (!folderAHK.exists()) {
                folderAHK.mkdirs();
                System.out.println("AutoHotKey has been created! !\n");
            }
            isExist.createNewFile();
            FileUtils.copyURLToFile(resourceAHK, isExist);
        }
    }

    /**
     * Extract image file and put it into SikuliImage directory
     *
     * @param strFileName
     * @throws IOException
     */
    public synchronized void extractImage(String strFileName) throws IOException {
        String strPath = sourcePath() + "SikuliImage\\";
        File isExist = new File(strPath + strFileName);
        if (!isExist.exists()) {
            URL resourceImg = PropertiesService.class.getClassLoader().getResource(strFileName);
            File folderSikuli = new File(strPath);
            if (!folderSikuli.exists()) {
                folderSikuli.mkdirs();
                System.out.println("Image Sikuli has been extracted! !\n");
            }
            isExist.createNewFile();
            FileUtils.copyURLToFile(resourceImg, isExist);
        }
    }

    /**
     * method for capture BDS with AHK
     * then strValue is file AHK .exe for capturing image
     *
     * @param strValue
     * @return path folder report specific Application
     * @throws IOException
     */
    public synchronized String captureAutoHotKey(String strValue) throws IOException {
        String strFilePath = extractFileAHK(strValue + ".txt");
        String strFileCapture = strFilePath;
        File file = new File(strFileCapture);

        if (file.exists()) {
            file.delete();
        } else {
            file.mkdirs();
        }

        file.createNewFile();

        FileWriter fileWriter = new FileWriter(strFileCapture, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        String strImageName = MyConfig.strPathReport
                + getDatatableFileName() + "\\Screenshot\\"
                + ScreenshotService.init().createFileName();

        bufferedWriter.write(strImageName);
        bufferedWriter.newLine();
        bufferedWriter.close();

        extractFileAHK(strValue + ".exe");
        Runtime.getRuntime().exec(sourcePath() + "AutoHotKey\\" + strValue + ".exe");

        DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);

        return strImageName;
    }

    /**
     * method for capture BDS with AHK
     * parameter strImageFolder is name folder containing image inside folder SikuliImage on resources
     * if image placed in folder SikuliImage, just put "" on strImageFolder parameter
     * then strValue is file AHK .exe for capturing image
     *
     * @param strImageFolder
     * @param strValue
     * @return path folder report specific Application
     * @throws IOException
     */
    public synchronized void captureAutoHotKey(String strImageFolder, String strValue) throws IOException {
        String path_to_image_sikuli = "";
        if (strImageFolder.equals("")) {
            path_to_image_sikuli = sourcePath() + "\\AutoHotKey\\";
        } else {
            path_to_image_sikuli = sourcePath() + "\\AutoHotKey\\" + strImageFolder + "\\";
        }
        extractFileAHK(path_to_image_sikuli + ".txt", strValue + ".txt");
        String strFileCapture = path_to_image_sikuli + strValue + ".txt";
        File file = new File(strFileCapture);
        if (file.exists()) {
            file.delete();
        } else {
            file.mkdirs();
        }
        file.createNewFile();

        FileWriter fileWriter = new FileWriter(strFileCapture, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        String strImageName = MyConfig.strPathReport
                + getDatatableFileName() + "\\Screenshot\\"
                + ScreenshotService.init().createFileName();

        bufferedWriter.write(strImageName);
        bufferedWriter.newLine();
        bufferedWriter.close();

        extractFileAHK(path_to_image_sikuli + strValue + ".exe", strValue + ".exe");
        Runtime.getRuntime().exec(path_to_image_sikuli + strValue + ".exe");

        DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);
    }

    /**
     * return sources path folder from project
     *
     * @return path folder project
     */
    public synchronized String sourcePath() {
        return System.getProperty("user.dir") + "\\src\\main\\resources\\";
    }

    public synchronized File[] getListOfDatatable() {
        File[] fileDatatableSet = new File[1];

        if (MyConfig.strPathDatatableDir.contains(".xlsx")) {
            fileDatatableSet[0] = new File(MyConfig.strPathDatatableDir);
        } else {
            File[] fileDatatableSetMLT = new File(MyConfig.strPathDatatableDir).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String fileName) {
                    return (fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".xlsm")) && !fileName.startsWith("~$");
                }
            });

            Arrays.sort(fileDatatableSetMLT);

            return fileDatatableSetMLT;
        }
        return fileDatatableSet;
    }

    /**
     * Get all running application on task list
     *
     * @return list of all running application
     */
    public synchronized String getRunningTaskLists() {
        String lines;
        String strTaskLists = "";

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader bfInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

        while (true) {
            try {
                if (!((lines = bfInput.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            strTaskLists += lines;
        }
        try {
            bfInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return strTaskLists;
    }

    /**
     * Change strPathReport into .zip
     * Put .zip into folder strPathReport
     * Put absolute path .zip into MyConfig.strPathZip
     *
     * @param strSourceDir
     */
    public synchronized void zipDirectory(String strSourceDir) throws IOException {
        try {
            filesListInDir = new ArrayList<>();
            String strPath = strSourceDir;
            File fileZip = new File(strPath);

            populateFilesList(fileZip);

            String zipDirName = strPath + "\\" + MyConfig.strDatePathReport + ".zip";
            MyConfig.strPathSourceZip = zipDirName;

            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            log.info("Zipping file on : " + strPath);
            for (String filePath : filesListInDir) {
                ZipEntry ze = new ZipEntry(filePath.substring(fileZip.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            log.error("Source File Zip not found!");
            e.printStackTrace();
            throw new IOException(e);
        }

    }

    /**
     * part of function ZipDirectory
     * put all file from source path into ZipEntry
     *
     * @param dir
     */
    private synchronized void populateFilesList(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile())
                filesListInDir.add(file.getAbsolutePath());
            else
                populateFilesList(file);
        }
    }

    /**
     * Login into FTP as configuration on ftpConfig.properties
     * Change directory as configuration on ftpConfig.properties
     * Make folder date MyConfig.strDatePathReport on FTP which is that get from beginning start automation
     * Upload file strPathSource into FTP Destination from ftpConfig.properties
     *
     * @param strPathSource
     */
    public synchronized void transferZipToFTP(String strPathSource) throws IOException {
        String server = propFTP.getProperty("server");
        int port = Integer.parseInt(propFTP.getProperty("port"));
        String user = propFTP.getProperty("user");
        String pass = propFTP.getProperty("password");


        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            MyConfig.strTrack = (MyConfig.strTrack.equals("") ? "RUNNING" : MyConfig.strTrack);
            String strDestinationFTP = propFTP.getProperty("destinationPath");
            String strRootPath = propFTP.getProperty("rootPath");

            ftpClient.changeWorkingDirectory(strDestinationFTP);
            ftpClient.changeWorkingDirectory(MyConfig.strTrack);
            ftpClient.makeDirectory(MyConfig.strDatePathReport);
            ftpClient.changeWorkingDirectory(MyConfig.strDatePathReport);

            log.info("Transfer Zip to FTP");
            File fleSource = new File(strPathSource);
            InputStream inputStream = new FileInputStream(strPathSource);

            boolean done = ftpClient.storeFile(fleSource.getName(), inputStream);

            // ex: ftp:\\\\10.20.200.23\\BERTA\\RUNNING\\22022023-1138\\22022023-1138.zip
            MyConfig.strDestinationFTP = strRootPath + "\\" + strDestinationFTP + "\\"
                    + MyConfig.strTrack + "\\" + MyConfig.strDatePathReport + "\\" + fleSource.getName();

            inputStream.close();
            if (done)
                log.info("Transfer Successfully!");


        } catch (IOException ex) {
            log.error("Transfer FTP Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new IOException(ex);
            }
        }
    }

    /**
     * For Jenkins BERTA.
     *
     * @throws IOException
     */
    public synchronized void createJsonFileJenkins() throws IOException {
        log.info("Create slaveConfiguration.json for Jenkins");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pathReport", MyConfig.strPathReport + "AutomationReport.xlsx");
            jsonObject.put("pathFTP", MyConfig.strDestinationFTP);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        System.out.println(jsonObject.toString());
        FileWriter file = new FileWriter("slaveConfiguration.json");
        file.write(jsonObject.toString());
        file.close();

    }


}
