package com.framework;

import com.framework.factory.DynamicXMLFactory;
import com.framework.factory.ExcelFactory;
import com.framework.factory.ExcelReportsFactory;
import com.framework.services.MyConfig;
import com.framework.utilities.FilesUtil;
import org.apache.log4j.Logger;
import java.io.File;

public class MainEngine {
    private static Logger log = Logger.getLogger("FILE");

    public static void main(String[] args) {
        MyConfig.strPathDatatable = FilesUtil.init().getListOfDatatable()[0].getPath();
        log.info("Start Automation");

        try {
            if (!MyConfig.boolReportOnly) {
                if (MyConfig.boolReplaceReportExisting) {
                    FilesUtil.init().deleteFolder(new File(MyConfig.strPathReport));
                }
                runningTest();
            } else {
                WPReportOnly();
                System.out.println("Done Create Report");
            }
        } catch (NullPointerException e) {
            log.error("Action not found!");
            e.printStackTrace();
        } catch (Exception e) {
            log.error(e.getCause());
            e.printStackTrace();
        }

        log.info("End Automation");
        copyFileLog();

         //System.exit(0);
    }

    /**
     * To running test by creating XML and executed by TestNG
     */
    public static void runningTest() {
        DynamicXMLFactory.init().createTest("Test Dynamic XML");
    }

    /**
     * Create Excel & PDF report only
     */
    public static void WPReportOnly() {
        File[] listDatatableFile = FilesUtil.init().getListOfDatatable();

        //currently Report Only doesn't support WP Per Data
        MyConfig.boolCreateWPByData = false;

        for (File fleDatatable : listDatatableFile) {
            DynamicTest.trdMapTestCaseList.set(ExcelFactory.getTestCaseList(fleDatatable.getPath()));

            ExcelReportsFactory.init().copyFileForWP();
            ExcelReportsFactory.init().createWPExcel();
        }
    }

    public static void copyFileLog() {
        FilesUtil.init().copyFile("Log/log.log",
                MyConfig.strPathReport,
                "log.log");
        FilesUtil.init().copyFile("Log/logNetwork.log",
                MyConfig.strPathReport,
                "logNetwork.log");

        FilesUtil.init().deleteFile(System.getProperty("user.dir") + "/Log/log.log",Logger.getLogger("FILE"));
        FilesUtil.init().deleteFile(System.getProperty("user.dir") + "/Log/logNetwork.log",Logger.getLogger("NETWORK"));
    }
}
