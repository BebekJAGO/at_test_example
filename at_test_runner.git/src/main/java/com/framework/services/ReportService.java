package com.framework.services;

import com.framework.factory.ExcelReportsFactory;
import com.framework.factory.PDFReportsFactory;

public class ReportService {

    public static void createExcelPDFReport() {
        if (MyConfig.propertiesTestNG.getProperty("Parallel").equalsIgnoreCase("none")) {
            ExcelReportsFactory.init().copyFileForWP();
            PDFReportsFactory.init().createPdfFile();
        }
    }

    public static void appendStatusToReport(String strStatus, String strKeterangan) {
        if (MyConfig.propertiesTestNG.getProperty("Parallel").equalsIgnoreCase("none")) {
            ExcelReportsFactory.init().appendStatusDT(strStatus, strKeterangan);
            createWPReport();
            ExcelReportsFactory.init().createSummaryReport(strStatus, strKeterangan);
            MyConfig.intDataCounter++;
        }
    }

    public static void createWPReport() {
        ExcelReportsFactory.init().createWPExcel();
        PDFReportsFactory.init().createPDFReport();
        ScreenshotService.init().resetInstance();
    }
}
