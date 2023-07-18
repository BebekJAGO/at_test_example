package com.framework.factory;

import com.framework.DAO.ReportSummaryDAO;
import com.framework.DynamicTest;
import com.framework.listeners.DynamicListeners;
import com.framework.services.MyConfig;
import com.framework.utilities.FilesUtil;
import org.apache.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExcelReportsFactory {
    protected ThreadLocal<String> trdStrPath = new ThreadLocal<>();
    protected ThreadLocal<String> trdStrTitle = new ThreadLocal<>();
    protected ThreadLocal<String> trdStrWPFile = new ThreadLocal<>();
    public static volatile ExcelReportsFactory instance = null;
    private static Map<Integer, ReportSummaryDAO> mapReportSummary = new HashMap<>();
    private static String strWPSheetName = "";
    private static Float[] arrProgressPercentage = new Float[2];
    private static int intColumnEndData;
    private Logger log = Logger.getLogger("FILE");

    /**
     * constructor
     */
    private ExcelReportsFactory() {
        String[] arrSplitPathDatatable = MyConfig.strPathDatatable.split("\\\\");
        trdStrTitle.set(arrSplitPathDatatable[arrSplitPathDatatable.length - 1]);
        trdStrPath.set(MyConfig.strPathReport + FilesUtil.init().getDatatableFileName() + "\\");
        setWPFilePath();
        Sheet shtInfo = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, MyConfig.strDataInfo);
        int intColumnEnd = ExcelFactory.init().getColumnByName(shtInfo, "End Data");
        intColumnEndData = Integer.parseInt(ExcelFactory.init().getCellValue(shtInfo.getWorkbook(), shtInfo.getRow(1).getCell(intColumnEnd)));
    }

    public static ExcelReportsFactory init() {
        if (instance == null) {
            synchronized (ExtentReportsFactory.class) {
                if (instance == null)
                    instance = new ExcelReportsFactory();
            }
        }
        return instance;
    }

    /**
     * creating excel report with horizontal dynamic type
     */
    public void createWPExcel() {
        int intWidth = 15;
        int intHeight = 24;
        int intSpace = 2;
        int intRowDescription = 2;
        int intMasterRow = intRowDescription + intSpace + 1;
        int intMasterColumn = intSpace - 1;
        int intTestcaseNumb = 1;
        int intCurrentTestCase = 1;

        File ImagePath = new File(trdStrPath.get() + "Screenshot\\");

        try {
            ZipSecureFile.setMinInflateRatio(-1.0d);
            Workbook workBook = new XSSFWorkbook(new FileInputStream(trdStrWPFile.get()));
            Sheet sheet = null;

            XSSFFont headerFont = (XSSFFont) workBook.createFont();
            headerFont.setBold(true);
            headerFont.setUnderline(XSSFFont.U_SINGLE);
            headerFont.setFontHeightInPoints((short) 24);

            XSSFFont descriptionFont = (XSSFFont) workBook.createFont();
            descriptionFont.setBold(true);
            descriptionFont.setColor(IndexedColors.WHITE.getIndex());

            XSSFCellStyle styleHeader = (XSSFCellStyle) workBook.createCellStyle();
            styleHeader.setFont(headerFont);

            XSSFCellStyle styleDescription = (XSSFCellStyle) workBook.createCellStyle();
            styleDescription.setFont(descriptionFont);
            styleDescription.setFillForegroundColor(IndexedColors.RED.getIndex());
            styleDescription.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            HiddenSheet(workBook);

            File[] matchingFiles = ImagePath.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("png");
                }
            });

            if (matchingFiles != null) {

                Arrays.sort(matchingFiles);

                int intCodeDataDefault = 0;
                int intCodeData = 1;

                for (int i = 0; i < matchingFiles.length; i++) {
                    Boolean boolIsRunTest = false;

                    if (!MyConfig.boolReportOnly) {
                        if (Integer.parseInt(matchingFiles[i].getName().split("-")[0]) == DynamicTest.trdIntNoData.get()) {
                            boolIsRunTest = true;
                        }
                    }

                    if (MyConfig.boolReportOnly || boolIsRunTest) {
                        if (i != 0 && !matchingFiles[i].getName().split("_")[0].equalsIgnoreCase(matchingFiles[i - 1].getName().split("_")[0])) {
                            intCodeData++;
                        }

                        BufferedImage bufferedImage = ImageIO.read(new File(trdStrPath.get() + "Screenshot\\" + matchingFiles[i].getName()));
                        int imageWidth = bufferedImage.getWidth();
                        int imageHeight = bufferedImage.getHeight();

                        if (imageHeight > imageWidth) {
                            intHeight = 28;
                            intWidth = 5;
                        } else if (imageHeight < imageWidth) {
                            intHeight = 28;
                            intWidth = 15;
                        }

                        if (intCodeData > intCodeDataDefault) {
                            intCodeDataDefault = intCodeData;
                            strWPSheetName = "WP-" + matchingFiles[i].getName().split("_")[0];

                            sheet = CreateSheet((strWPSheetName.length() > 31 ? strWPSheetName.substring(0, 31) : strWPSheetName), workBook);
                            Cell celHeader = sheet.createRow(0).createCell(0);
                            celHeader.setCellValue(strWPSheetName);
                            celHeader.setCellStyle(styleHeader);

                            intMasterRow = intRowDescription + intSpace + 1;
                            intMasterColumn = intSpace - 1;

                            // TestcaseName
                            Row rowBackground = sheet.createRow(intMasterRow - 2);
                            Cell celTestcase = rowBackground.createCell(0);
                            for (int j = 0; j < 500; j++)
                                rowBackground.createCell(j).setCellStyle(styleDescription);
                            intCurrentTestCase = Integer.parseInt(matchingFiles[i].getName().split("_")[1]);
                            celTestcase.setCellValue(DynamicTest.trdMapTestCaseList.get().get(intCurrentTestCase));
                            intTestcaseNumb = intCurrentTestCase;
                        }

                        intCurrentTestCase = Integer.parseInt(matchingFiles[i].getName().split("_")[1]);

                        if (intCurrentTestCase > intTestcaseNumb) {
                            intMasterColumn = 1; // B
                            intMasterRow += intHeight + 5; // 6

                            // TestcaseName
                            Row rowBackground2 = sheet.createRow(intMasterRow - 2);
                            Cell celTestcase = rowBackground2.createCell(0);
                            for (int j = 0; j < 500; j++)
                                rowBackground2.createCell(j).setCellStyle(styleDescription);

                            celTestcase.setCellValue(DynamicTest.trdMapTestCaseList.get().get(intCurrentTestCase));

                            intTestcaseNumb = intCurrentTestCase;
                        }

                        InputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(matchingFiles[i]);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        int pictureIdx = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        CreationHelper helper = workBook.getCreationHelper();
                        Drawing drawing = sheet.createDrawingPatriarch();
                        ClientAnchor anchor = helper.createClientAnchor();

                        //create an anchor with upper left cell _and_ bottom right cell
                        anchor.setCol1(intMasterColumn); //Column B
                        anchor.setRow1(intMasterRow); //Row 6
                        anchor.setCol2(intMasterColumn + intWidth); //Column B to Column P
                        anchor.setRow2(intMasterRow + intHeight); //Row 6 to Row 29

                        intMasterColumn += intWidth + intSpace; //next image column

                        //Creates a picture
                        drawing.createPicture(anchor, pictureIdx);
                    }
                }
            } else {
                log.error("WP sheet doesn't create, there are no images found from Screenshot path");
                System.out.println("WP sheet doesn't create, there are no images found from Screenshot path");
            }

            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(trdStrWPFile.get());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                workBook.write(fileOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                fileOut.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            log.error("Failed to create Excel WP Report");
            System.out.println("Failed to create Excel WP Report");
            new Throwable().getCause();
        }

        instance = (MyConfig.boolReportOnly) ? null : instance;
    }

    /**
     * Create Summary Report on new File excel if WP file is more than one
     * OR create new REPORT sheet on specific WP file if WP file is a single file
     *
     * @param strStatus     PASSED or FAILED result
     * @param strKeterangan description of status result
     */
    public void createSummaryReport(String strStatus, String strKeterangan) {
        try {
            File fileSummaryReport;
            String strExcelReportFile;

            XSSFWorkbook workbook = null;
            XSSFSheet sheet = null;

            int intDatatableLength = FilesUtil.init().getListOfDatatable().length;

            if (intDatatableLength > 1 || MyConfig.boolCreateWPByData) {
                strExcelReportFile = MyConfig.strPathReport + "AutomationReport.xlsx";
                fileSummaryReport = new File(strExcelReportFile);

                if (MyConfig.boolReplaceReportExisting) {
                if (MyConfig.intDataCounter == 1) {
                    if (fileSummaryReport.exists()) {
                        fileSummaryReport.delete();
                    }
                }
                }

                if (!fileSummaryReport.exists()) {
                    fileSummaryReport.createNewFile();
                    workbook = new XSSFWorkbook();
                } else {
                    workbook = new XSSFWorkbook(new FileInputStream(fileSummaryReport));
                }

            } else {
                strExcelReportFile = trdStrWPFile.get();
                fileSummaryReport = new File(strExcelReportFile);
                workbook = new XSSFWorkbook(new FileInputStream(fileSummaryReport));

                if (MyConfig.boolReplaceReportExisting) {
                if (MyConfig.intDataCounter == 1) {
                    if (ExcelFactory.init().sheetIsExist(strExcelReportFile, "REPORT")) {
                        workbook.removeSheetAt(workbook.getSheetIndex("REPORT"));

                        FileOutputStream fileOutputStream = new FileOutputStream(fileSummaryReport);
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        workbook.close();

                        workbook = new XSSFWorkbook(new FileInputStream(fileSummaryReport));
                    }
                }
                }
            }

            if (!ExcelFactory.init().sheetIsExist(strExcelReportFile, "REPORT")) {
                sheet = workbook.createSheet("REPORT");
                XSSFRow rowHeader = sheet.createRow(3);

                //Create font and style config for title & header Report
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeight(20);

                XSSFFont headerTableFont = workbook.createFont();
                headerTableFont.setBold(true);
                headerTableFont.setColor(IndexedColors.WHITE.getIndex());

                XSSFCellStyle cellHeaderStyle = workbook.createCellStyle();
                cellHeaderStyle.setFont(headerFont);
                cellHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
                cellHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                XSSFCellStyle cellHeaderTableStyle = workbook.createCellStyle();
                cellHeaderTableStyle.setBorderBottom(BorderStyle.THIN);
                cellHeaderTableStyle.setBorderLeft(BorderStyle.THIN);
                cellHeaderTableStyle.setBorderTop(BorderStyle.THIN);
                cellHeaderTableStyle.setBorderRight(BorderStyle.THIN);
                cellHeaderTableStyle.setFont(headerTableFont);
                cellHeaderTableStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
                cellHeaderTableStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellHeaderTableStyle.setAlignment(HorizontalAlignment.CENTER);
                cellHeaderTableStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                //Create report header
                XSSFCell cellHeader = sheet.createRow(0).createCell(0);
                cellHeader.setCellValue("REPORT AUTOMATION");
                cellHeader.setCellStyle(cellHeaderStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 11));

                String[] strHeaderTable = {"NO", "DATATABLE NAME", "ACTION", "SCENARIO", "STATUS", "DESCRIPTION", "DURATION"};

                for (int i = 0; i < strHeaderTable.length; i++) {
                    XSSFCell cellHeaderTable = rowHeader.createCell(i);
                    cellHeaderTable.setCellValue(strHeaderTable[i]);
                    cellHeaderTable.setCellStyle(cellHeaderTableStyle);
                    sheet.autoSizeColumn(i);
                }
            } else {
                sheet = workbook.getSheet("REPORT");
            }

            //Create style and font for Result Data
            XSSFCellStyle cellDataStyle = workbook.createCellStyle();
            cellDataStyle.setBorderBottom(BorderStyle.THIN);
            cellDataStyle.setBorderLeft(BorderStyle.THIN);
            cellDataStyle.setBorderTop(BorderStyle.THIN);
            cellDataStyle.setBorderRight(BorderStyle.THIN);
            cellDataStyle.setAlignment(HorizontalAlignment.CENTER);
            cellDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            //Create style and font for Status Result Data
            XSSFCellStyle cellResultDataStyle = workbook.createCellStyle();
            cellResultDataStyle.setBorderBottom(BorderStyle.THIN);
            cellResultDataStyle.setBorderLeft(BorderStyle.THIN);
            cellResultDataStyle.setBorderTop(BorderStyle.THIN);
            cellResultDataStyle.setBorderRight(BorderStyle.THIN);
            cellResultDataStyle.setAlignment(HorizontalAlignment.CENTER);
            cellResultDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFFont fontResultData = workbook.createFont();

            String strAPIColumnLetter = "";

            if (ExcelFactory.init().sheetIsExist(trdStrWPFile.get(), "Report API")) {
                File fileWPAPIG = new File(trdStrWPFile.get());
                XSSFWorkbook workbookWPAPIG = new XSSFWorkbook(new FileInputStream(fileWPAPIG));
                XSSFSheet shtReportAPI = workbookWPAPIG.getSheet("Report API");
                int intColumnStatusAPIG = ExcelFactory.init().getColumnByName(shtReportAPI, "STATUS");
                Cell cellStatusAPIG = shtReportAPI.getRow(0).getCell(intColumnStatusAPIG);
                strAPIColumnLetter = CellReference.convertNumToColString(cellStatusAPIG.getColumnIndex());

                //Adjust strStatus according to the results from API
                if (ExtentReportsFactory.trdAPIExtentSuccessStatus.get() != null) {
                    if (strStatus.equalsIgnoreCase("PASSED")) {
                        strStatus = ExtentReportsFactory.trdAPIExtentSuccessStatus.get().toString().equalsIgnoreCase("Pass") ? "PASSED" : "FAILED";
                    }
                }
            }

            //Get data from ReportSummaryDAO Map object and input into cell
            Map<Integer, ReportSummaryDAO> mapResultReportSummary = putResultForReportSummary(strStatus, strKeterangan);

            for (int i = 1; i <= MyConfig.intDataCounter; i++) {
                if (i == MyConfig.intDataCounter) {

                    String[] arrData = {
                            DynamicTest.trdIntNoData.get().toString(),
                            mapResultReportSummary.get(i).getStrDatatableName(),
                            mapResultReportSummary.get(i).getStrAction(),
                            mapResultReportSummary.get(i).getStrScenario(),
                            mapResultReportSummary.get(i).getStrStatus(),
                            mapResultReportSummary.get(i).getStrDescription(),
                            mapResultReportSummary.get(i).getStrDuration()
                    };

                    XSSFRow rowData = null;
                    if (MyConfig.boolReplaceReportExisting) {
                    rowData = sheet.createRow(3 + i);
                    } else {

                        int intRowCreate = 0;
                        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                            try {
                                Cell cell = sheet.getRow(3 + j).getCell(0);
                                String strCell = cell.toString();
                                if (strCell.equalsIgnoreCase(String.valueOf(DynamicTest.trdIntNoData.get()))) {
                                    intRowCreate = j;
                                    break;
                                } else if (!strCell.equals("")) {
                                    continue;
                                }
                            } catch (Exception e) {
                                intRowCreate = j;
                                break;
                            }
                        }

                        if (intRowCreate == 0) {
                            continue;
                        }
                        rowData = sheet.createRow(3 + intRowCreate);
                    }

                    for (int j = 0; j < 7; j++) {

                        XSSFCell cellData = rowData.createCell(j);

                        if (j == 4) {
                            String strSheetNameOfHyperlink = "";
                            String strHyperlinkValue = "";

                            fontResultData.setColor((arrData[j].equalsIgnoreCase("PASSED")) ? IndexedColors.BLUE.getIndex() : IndexedColors.RED.getIndex());
                            cellResultDataStyle.setFont(fontResultData);
                            cellData.setCellStyle(cellResultDataStyle);

                            if (!strAPIColumnLetter.equalsIgnoreCase("")) {
                                strSheetNameOfHyperlink = "Report API";
                                int intDataRow = DynamicTest.trdIntNoData.get() + 1;
                                strHyperlinkValue = "HYPERLINK(\"[" + trdStrWPFile.get() + "#'" + strSheetNameOfHyperlink + "'!" + strAPIColumnLetter + intDataRow + "]\",\"" + arrData[j] + "\")";
                                cellData.setCellFormula(strHyperlinkValue);
                            } else {
                                strSheetNameOfHyperlink = strWPSheetName.length() > 31 ? strWPSheetName.substring(0, 31) : strWPSheetName;

                                if (strExcelReportFile.contains("AutomationReport")) {
                                    String strPathStatDetails = FilesUtil.init().getDatatableFileName() + "\\Report\\" + getWPFileName();
                                    strHyperlinkValue = "HYPERLINK(\"[" + strPathStatDetails + "#'" + strSheetNameOfHyperlink + "'!A1]\",\"" + arrData[j] + "\")";
                                    cellData.setCellFormula(strHyperlinkValue);
                                } else {
                                    strHyperlinkValue = "='" + strSheetNameOfHyperlink + "'!A1";

                                    Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
                                    href.setAddress(strHyperlinkValue);

                                    cellData.setHyperlink(href);
                                }
                            }
                        } else {
                            cellData.setCellStyle(cellDataStyle);
                        }

                        cellData.setCellValue(arrData[j]);
                        sheet.autoSizeColumn(j);
                    }
                    break;
                }
            }

            //Create summary table beside Summary Report table on same sheet
            createSummaryTableOnSummaryReport(strExcelReportFile, sheet, workbook);
            //Create pie chart of Summary's Progress below Summary Table
            createSummaryPieChart(sheet);

            if (intDatatableLength == 1) {
                if (!MyConfig.boolCreateWPByData) {
                    workbook.setSheetOrder("REPORT", workbook.getSheetIndex(MyConfig.strMainSheet) + 1);
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileSummaryReport);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();

            instance = null;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }catch (NullPointerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * write running result of each data to excel report
     *
     * @param strRunningStatus -> status of running
     * @param strKeterangan    -> description of runnning (if any)
     */
    public void appendStatusDT(String strRunningStatus, String strKeterangan) {
        int intDataNo = DynamicTest.trdIntNoData.get();

        File file = new File(trdStrWPFile.get());

        try {
            //Get the workbook instance for XLSX file
            XSSFWorkbook workbook = null;
            try {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //get spreadsheet
            XSSFSheet spreadsheet = workbook.getSheet(MyConfig.strMainSheet);
            Sheet shtMain = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, MyConfig.strMainSheet);

            int intColumnStatus = ExcelFactory.init().getColumnByName(shtMain, "STATUS");
            int intColumnKeterangan = ExcelFactory.init().getColumnByName(shtMain, "KETERANGAN");

            CellStyle runningStatusStyle = workbook.createCellStyle();
            XSSFFont runningStatusFont = workbook.createFont();
            Row row = spreadsheet.getRow(intDataNo);
            Cell cell = row.createCell(intColumnStatus);

            String strAddress = "";

            if (ExcelFactory.init().sheetIsExist(trdStrWPFile.get(), "Report API")) {
                File fileWPAPIG = new File(trdStrWPFile.get());
                XSSFWorkbook workbookWPAPIG = new XSSFWorkbook(new FileInputStream(fileWPAPIG));
                XSSFSheet shtReportAPI = workbookWPAPIG.getSheet("Report API");
                int intColumnStatusAPIG = ExcelFactory.init().getColumnByName(shtReportAPI, "STATUS");
                Cell cellStatusAPIG = shtReportAPI.getRow(0).getCell(intColumnStatusAPIG);
                String strAPIColumnLetter = CellReference.convertNumToColString(cellStatusAPIG.getColumnIndex());

                String strSheetNameOfHyperlink = "Report API";
                int intDataRow = DynamicTest.trdIntNoData.get() + 1;

                //Only adjust Running Status while strRunningStatus is PASSED, if FAILED will set to default strRunningStatus
                if (strRunningStatus.equalsIgnoreCase("PASSED")) {
                    strRunningStatus = ExtentReportsFactory.trdAPIExtentSuccessStatus.get().toString().equalsIgnoreCase("Pass") ? "PASSED" : "FAILED";
                    strAddress = "='" + strSheetNameOfHyperlink + "'!" + strAPIColumnLetter + intDataRow;
                }
            } else {
                String strTempAddress = "='WP-" + intDataNo + "-" + DynamicTest.trdScenario.get();
                strAddress = (strTempAddress.length() > 33 ? strTempAddress.substring(0, 33) : strTempAddress) + "'!A1";
            }

            runningStatusFont.setUnderline(XSSFFont.U_SINGLE);
            runningStatusFont.setColor((strRunningStatus.equalsIgnoreCase("PASSED")) ? IndexedColors.BLUE.getIndex() : IndexedColors.RED.getIndex());
            runningStatusStyle.setFont(runningStatusFont);

            Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
            href.setAddress(strAddress);

            cell.setCellStyle(runningStatusStyle);
            cell.setHyperlink(href);
            cell.setCellValue(strRunningStatus);

            spreadsheet.getRow(intDataNo).createCell(intColumnKeterangan).setCellValue(strKeterangan.replace("<br/>", ";"));

            if (MyConfig.boolCreateWPByData || intDataNo == intColumnEndData) {
                createSummaryTable(spreadsheet, workbook);
            }

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                workbook.write(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Throwable e) {
            log.error("Failed to write status result");
            System.out.println("Failed to write status result");
            e.printStackTrace();
        }
    }

    /**
     * Put all of running result parameter to map object of Report Summary so it will be use for REPORT Information
     *
     * @param strStatus
     * @param strKeterangan
     * @return
     */
    private static Map putResultForReportSummary(String strStatus, String strKeterangan) {
        String strNo = String.valueOf(MyConfig.intDataCounter);
        String strDatatablename = FilesUtil.init().getDatatableFileName();
        String strAction = DynamicTest.trdStrAction.get();
        String strScenario = DynamicTest.trdScenario.get();
        String strDuration = DynamicListeners.trdTestDuration.get();
        String strDescription = strKeterangan.replace("<br/>", ";");

        mapReportSummary.put(MyConfig.intDataCounter, new ReportSummaryDAO(strNo, strDatatablename, strAction, strScenario, strStatus, strDescription, strDuration));
        return mapReportSummary;
    }

    /**
     * copy the datatable to create new excel file as a report
     */
    public void copyFileForWP() {
        String strPathOutput = trdStrPath.get() + "Report\\";
        File file = new File(strPathOutput);

        if (!file.exists()) {
            file.mkdirs();
        }

        File fleTestCaseSource = new File(MyConfig.strPathDatatable);
        File fleTestCaseDestination = new File(trdStrWPFile.get());

        if (fleTestCaseDestination.exists() && MyConfig.boolReportOnly) {
            FilesUtil.init().deleteFile(fleTestCaseDestination.getPath(), log);
        }

        if (!fleTestCaseDestination.exists()) {
            try {
                Files.copy(fleTestCaseSource.toPath(), fleTestCaseDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * To creating new sheet
     *
     * @param strSheetName -> name of the sheet
     * @param wb           -> workbook where the new sheet will be create
     * @return sheet that has been created
     */
    private Sheet CreateSheet(String strSheetName, Workbook wb) {
        Sheet shtTemp = null;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (wb.getSheetAt(i).getSheetName().equalsIgnoreCase(strSheetName)) {
                wb.removeSheetAt(i);
                break;
            }
        }
        shtTemp = wb.createSheet(strSheetName);
        return shtTemp;
    }

    /**
     * to hidden all the sheet on workbook
     *
     * @param wb -> the workbook
     */
    private void HiddenSheet(Workbook wb) {
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (!(wb.getSheetName(i).equalsIgnoreCase(MyConfig.strMainSheet) || wb.getSheetName(i).contains("WP") || wb.getSheetName(i).contains("Report") || wb.getSheetName(i).equalsIgnoreCase("Report"))) {
                wb.setSheetHidden(i, true);
            }
        }
    }

    /**
     * create Summary Table in Datatable sheet which provide total and percetage of PASSED & FAILED running result
     *
     * @param spreadSheet
     * @param workbook
     */
    public void createSummaryTable(XSSFSheet spreadSheet, XSSFWorkbook workbook) {
        int passedTtl = 0;
        int failedTtl = 0;
        int Count = 0;
        int j = 0;
        int idxStatus = ExcelFactory.init().getColumnByName(spreadSheet, "STATUS");
        String[] List_Header_Table = new String[]{"SUMMARY'S STAT", "SUMMARY'S PROGRESS(%)", "TOTAL SCRIPT"};

        /*
        set header cell style and value with List of Header above
         */
        XSSFCellStyle styleDescription = workbook.createCellStyle();
        styleDescription.setBorderBottom(BorderStyle.THIN);
        styleDescription.setBorderLeft(BorderStyle.THIN);
        styleDescription.setBorderTop(BorderStyle.THIN);
        styleDescription.setBorderRight(BorderStyle.THIN);
        styleDescription.setAlignment(HorizontalAlignment.CENTER);
        styleDescription.setVerticalAlignment(VerticalAlignment.CENTER);
        styleDescription.setFillForegroundColor(IndexedColors.LIME.getIndex());
        styleDescription.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFRow row = spreadSheet.getRow(0);

        for (int i = idxStatus + 3; i <= idxStatus + 2 + List_Header_Table.length; i++) {
            XSSFCell head = row.createCell(i);
            head.setCellStyle(styleDescription);
            head.setCellValue(List_Header_Table[j++]);
            spreadSheet.autoSizeColumn(i);
        }

        XSSFCellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setAlignment(HorizontalAlignment.CENTER);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 1; i <= spreadSheet.getLastRowNum(); i++) {
            try {
                String status = spreadSheet.getRow(i).getCell(ExcelFactory.init().getColumnByName(spreadSheet, "STATUS")).toString();
                if (status.equalsIgnoreCase("PASSED")) {
                    passedTtl += 1;
                } else {
                    failedTtl += 1;
                }
                Count += 1;
            } catch (Exception ignored) {}
        }
        float passedPercent = ((float) passedTtl / (float) Count) * 100;
        float failedPercent = ((float) failedTtl / (float) Count) * 100;

        /*
        set percentage value of PASSED status
         */
        try {
            spreadSheet.getRow(1).getCell(0);
        } catch (NullPointerException e) {
            spreadSheet.createRow(1);
        }

        spreadSheet.getRow(1).createCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S STAT")).setCellValue("PASSED");
        spreadSheet.getRow(1).createCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S PROGRESS(%)")).setCellValue(Math.round(passedPercent) + "%");
        spreadSheet.getRow(1).createCell(ExcelFactory.init().getColumnByName(spreadSheet, "TOTAL SCRIPT")).setCellValue(passedTtl);

        spreadSheet.getRow(1).getCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S STAT")).setCellStyle(bodyStyle);
        spreadSheet.getRow(1).getCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S PROGRESS(%)")).setCellStyle(bodyStyle);
        spreadSheet.getRow(1).getCell(ExcelFactory.init().getColumnByName(spreadSheet, "TOTAL SCRIPT")).setCellStyle(bodyStyle);

        /*
        set percentage value of FAILED status
         */
        try {
            spreadSheet.getRow(2).getCell(0);
        } catch (NullPointerException e) {
            spreadSheet.createRow(2);
        }

        spreadSheet.getRow(2).createCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S STAT")).setCellValue("FAILED");
        spreadSheet.getRow(2).createCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S PROGRESS(%)")).setCellValue(Math.round(failedPercent) + "%");
        spreadSheet.getRow(2).createCell(ExcelFactory.init().getColumnByName(spreadSheet, "TOTAL SCRIPT")).setCellValue(failedTtl);

        spreadSheet.getRow(2).getCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S STAT")).setCellStyle(bodyStyle);
        spreadSheet.getRow(2).getCell(ExcelFactory.init().getColumnByName(spreadSheet, "SUMMARY'S PROGRESS(%)")).setCellStyle(bodyStyle);
        spreadSheet.getRow(2).getCell(ExcelFactory.init().getColumnByName(spreadSheet, "TOTAL SCRIPT")).setCellStyle(bodyStyle);
    }

    /**
     * Creating Summary Table on REPORT sheet which provide total and percentage of PASSED & FAILED result
     *
     * @param strExcelFile WP-...xlsx file for single WP Report & AutomationReport.xlsx file for multiple WP report
     * @param sheet        REPORT sheet
     * @param workbook
     */
    public void createSummaryTableOnSummaryReport(String strExcelFile, XSSFSheet sheet, XSSFWorkbook workbook) {
        int intPassedTotal = 0;
        int intFailedTotal = 0;
        int intTotalData = 0;
        int j = 0;

        if (!ExcelFactory.init().sheetIsExist(strExcelFile, "REPORT")) {
            int idxStatus = ExcelFactory.init().getColumnByName(sheet, "STATUS", 3);
            String[] List_Header_Table = new String[]{"SUMMARY'S STAT", "SUMMARY'S PROGRESS(%)", "TOTAL SCRIPT", "TOTAL DURATION"};

            /*
            set header cell style and value with List of Header above
             */
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFRow row = sheet.getRow(3);

            for (int i = idxStatus + 4; i <= idxStatus + 3 + List_Header_Table.length; i++) {
                XSSFCell head = row.createCell(i);
                head.setCellStyle(headerStyle);
                head.setCellValue(List_Header_Table[j++]);
                sheet.autoSizeColumn(i);
            }
        }

        XSSFCellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setAlignment(HorizontalAlignment.CENTER);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        int intFinalSecond = 0;
        int intFinalMinute = 0;
        int intFinalHour = 0;

        for (int i = 4; i <= sheet.getLastRowNum(); i++) {
            try {
                String strStatus = sheet.getRow(i).getCell(ExcelFactory.init().getColumnByName(sheet, "STATUS", 3)).toString();
                if (strStatus.contains("PASSED")) {
                    intPassedTotal += 1;
                } else {
                    intFailedTotal += 1;
                }
                intTotalData += 1;

                if (i - 3 >= MyConfig.intDataCounter) {

                    String[] arrDuration = sheet.getRow(i).getCell(ExcelFactory.init().getColumnByName(sheet, "DURATION", 3)).toString().replace(".", ":").split(":");

                    int intMiliSecond = Integer.parseInt(arrDuration[3]);
                    int intSecond = Integer.parseInt(arrDuration[2]);
                    int intMinute = Integer.parseInt(arrDuration[1]);
                    int intHour = Integer.parseInt(arrDuration[0]);

                    String[] arrTotalTestDuration = MyConfig.strTotalTestDuration.replace(".", ":").split(":");

                    int intTotMiliSecond = Integer.parseInt(arrTotalTestDuration[3]);
                    int intTotSecond = Integer.parseInt(arrTotalTestDuration[2]);
                    int intTotMinute = Integer.parseInt(arrTotalTestDuration[1]);
                    int intTotHour = Integer.parseInt(arrTotalTestDuration[0]);

                    intTotMiliSecond = intTotMiliSecond + intMiliSecond;
                    intTotSecond = intTotSecond + intSecond;
                    intTotMinute = intTotMinute + intMinute;
                    intTotHour = intTotHour + intHour;

                    if (intTotMiliSecond >= 1000) {
                        intFinalSecond = intTotMiliSecond / 1000;
                        intTotMiliSecond = intTotMiliSecond % 1000;
                        intTotSecond = intTotSecond + intFinalSecond;
                    }

                    if (intTotSecond >= 60) {
                        intFinalMinute = intTotSecond / 60;
                        intTotSecond = intTotSecond % 60;
                        intTotMinute = intTotMinute + intFinalMinute;
                    }

                    if (intTotMinute >= 60) {
                        intFinalHour = intTotMinute / 60;
                        intTotMinute = intTotMinute % 60;
                        intTotHour = intTotHour + intFinalHour;
                    }

                    String strMiliSecond = String.valueOf(intTotMiliSecond);
                    String strSecond = String.valueOf(intTotSecond);
                    String strMinute = String.valueOf(intTotMinute);
                    String strHour = String.valueOf(intTotHour);

                    while (strMiliSecond.length() < 3) {
                        strMiliSecond = "0" + strMiliSecond;
                    }

                    while (strSecond.length() < 2) {
                        strSecond = "0" + strSecond;
                    }

                    while (strMinute.length() < 2) {
                        strMinute = "0" + strMinute;
                    }

                    while (strHour.length() < 2) {
                        strHour = "0" + strHour;
                    }

                    MyConfig.strTotalTestDuration = strHour + ":" + strMinute + ":" + strSecond + "." + strMiliSecond;
                }
            } catch (Exception ignored) {}
        }

        arrProgressPercentage = getProgressPercentage(intPassedTotal, intFailedTotal, intTotalData);

        float passedPercent = arrProgressPercentage[0];
        float failedPercent = arrProgressPercentage[1];

        //set percentage value of PASSED status
        try {
            sheet.getRow(4).getCell(0);
        } catch (NullPointerException e) {
            sheet.createRow(4);
        }

        sheet.getRow(4).createCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S STAT", 3)).setCellValue("PASSED");
        sheet.getRow(4).createCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S PROGRESS(%)", 3)).setCellValue(Math.round(passedPercent) + "%");
        sheet.getRow(4).createCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL SCRIPT", 3)).setCellValue(intPassedTotal);

        sheet.getRow(4).getCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S STAT", 3)).setCellStyle(bodyStyle);
        sheet.getRow(4).getCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S PROGRESS(%)", 3)).setCellStyle(bodyStyle);
        sheet.getRow(4).getCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL SCRIPT", 3)).setCellStyle(bodyStyle);

        //set percentage value of FAILED status
        try {
            sheet.getRow(5).getCell(0);
        } catch (NullPointerException e) {
            sheet.createRow(5);
        }

        sheet.getRow(5).createCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S STAT", 3)).setCellValue("FAILED");
        sheet.getRow(5).createCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S PROGRESS(%)", 3)).setCellValue(Math.round(failedPercent) + "%");
        sheet.getRow(5).createCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL SCRIPT", 3)).setCellValue(intFailedTotal);

        sheet.getRow(5).getCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S STAT", 3)).setCellStyle(bodyStyle);
        sheet.getRow(5).getCell(ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S PROGRESS(%)", 3)).setCellStyle(bodyStyle);
        sheet.getRow(5).getCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL SCRIPT", 3)).setCellStyle(bodyStyle);

        //set Total Duration value on merged cell (Row 4 & Row 5)
        sheet.getRow(4).createCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL DURATION", 3)).setCellValue(MyConfig.strTotalTestDuration);
        sheet.getRow(4).getCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL DURATION", 3)).setCellStyle(bodyStyle);
        sheet.getRow(5).createCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL DURATION", 3));
        sheet.getRow(5).getCell(ExcelFactory.init().getColumnByName(sheet, "TOTAL DURATION", 3)).setCellStyle(bodyStyle);

        try {
            sheet.addMergedRegion(new CellRangeAddress(4, 5, ExcelFactory.init().getColumnByName(sheet, "TOTAL DURATION", 3), ExcelFactory.init().getColumnByName(sheet, "TOTAL DURATION", 3)));
        } catch (Exception e) {}
    }

    /**
     * Creating pie chart of Summary Progress on "REPORT" sheet and put it below Summary Table Report
     *
     * @param sheet
     */
    public void createSummaryPieChart(XSSFSheet sheet) {
        int intColumnSummaryStat = ExcelFactory.init().getColumnByName(sheet, "SUMMARY'S STAT", 3);
        int intColumnTotalDuration = ExcelFactory.init().getColumnByName(sheet, "TOTAL DURATION", 3);

        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, intColumnSummaryStat, 8, intColumnTotalDuration + 1, 23);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("SUMMARY'S PROGRESS CHART (%)");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        //add data source for Status label in chart
        XDDFDataSource<String> dSSummaryStat = XDDFDataSourcesFactory.fromArray(new String[]{"PASSED", "FAILED"});
        //add data source for percentage of Summary's Progress in chart
        XDDFNumericalDataSource<Float> dSSummaryProgress = XDDFDataSourcesFactory.fromArray(arrProgressPercentage);

        //create and store data on Pie chart
        XDDFChartData chartData = chart.createData(ChartTypes.PIE, null, null);
        chartData.setVaryColors(true);
        chartData.addSeries(dSSummaryStat, dSSummaryProgress);
        chart.plot(chartData);

        //set Summary's Progress labels
        if (!chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).isSetDLbls()) {
            chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).addNewDLbls();
        }

        chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).getDLbls().addNewShowVal().setVal(true);
        chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).getDLbls().addNewShowSerName().setVal(false);
        chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).getDLbls().addNewShowCatName().setVal(false);
        chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).getDLbls().addNewShowPercent().setVal(false);
        chart.getCTChart().getPlotArea().getPieChartArray(0).getSerArray(0).getDLbls().addNewShowLegendKey().setVal(false);
    }

    /**
     * Counting percentage of Summary's Progress on REPORT sheet
     *
     * @param intPassedTotal total of PASSED result
     * @param intFailedTotal total of FAILED result
     * @param intTotalData   amount of total data
     * @return list of percetage value of PASSED and FAILED result
     */
    private Float[] getProgressPercentage(int intPassedTotal, int intFailedTotal, int intTotalData) {
        float fltPassedPercentage = ((float) intPassedTotal / (float) intTotalData) * 100;
        float fltFailedPercentage = ((float) intFailedTotal / (float) intTotalData) * 100;

        Float[] arrSummaryProgressPercentage = {fltPassedPercentage, fltFailedPercentage};

        return arrSummaryProgressPercentage;
    }

    /**
     * set path value of Excel WP File
     */
    private void setWPFilePath() {
        if (MyConfig.boolCreateWPByData) {
            trdStrWPFile.set(trdStrPath.get() + "Report\\WP-" + DynamicTest.trdIntNoData.get() + "_" + trdStrTitle.get());
        } else {
            trdStrWPFile.set(trdStrPath.get() + "Report\\WP-" + trdStrTitle.get());
        }
    }

    /**
     * @return Excel WP Filename
     */
    private String getWPFileName() {
        return trdStrWPFile.get().replace(trdStrPath.get() + "Report\\", "");
    }

}
