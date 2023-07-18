package com.framework.factory;

import com.aventstack.extentreports.Status;
import com.framework.DAO.XpathDAO;
import com.framework.DynamicTest;
import com.framework.services.MyConfig;
import com.google.common.io.Files;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelFactory {
    private static volatile ExcelFactory instance = null;
    private static ThreadLocal<Workbook> thrWorkbook = new ThreadLocal<>();

    private ExcelFactory() {

    }

    public static ExcelFactory init() {
        if (instance == null) {
            synchronized (ExcelFactory.class) {
                if (instance == null)
                    instance = new ExcelFactory();
            }
        }
        return instance;
    }

    /**
     * Get Workbook
     * @param strPathExcel path Datatable location
     */
    public Workbook getWorkBook(String strPathExcel) {
        Workbook workbook;
        try {
            File file = new File(strPathExcel);
            FileInputStream inputStream = null;
            inputStream = new FileInputStream(file);
            String fileExtension = Files.getFileExtension(strPathExcel);

            if (fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xlsm")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (fileExtension.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = null;
            }
            return workbook;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found.");
        } catch (IOException e) {
            throw new RuntimeException("Path not found.");
        }

    }

    /**
     * Get specific sheet from workbook
     * @param strPathExcel path Datatable location
     * @param strSheetName which sheet
     * @return
     */
    public Sheet getSheetExcel(String strPathExcel, String strSheetName) {
        try {
            return getWorkBook(strPathExcel).getSheet(strSheetName);
        } catch (Exception e) {
            throw new RuntimeException("Sheet not found");
        }
    }

    /**
     * check if specific sheet is exist
     * @param strPathExcel
     * @param strSheetName
     * @return 'true' if sheet is exist, false isn't exist
     */
    public Boolean sheetIsExist(String strPathExcel, String strSheetName) {
        try {
            return getWorkBook(strPathExcel).getSheet(strSheetName) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Search column in sheet and return the index column
     * @param sheet which sheet
     * @param strSearch search column
     * @return index column
     */
    public int getColumnByName(Sheet sheet, String strSearch) {
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            String strValue = getCellValue(sheet.getWorkbook(), sheet.getRow(0).getCell(i));
            if (strValue.equalsIgnoreCase(strSearch))
                return sheet.getRow(0).getCell(i).getColumnIndex();
        }
        throw new NullPointerException("Column " + strSearch + " not Found on Sheet " + sheet.getSheetName());
    }


    /**
     * Get column number by specific name and row number
     * @param sheet name of sheet
     * @param strSearch name of column
     * @param intHeaderRow row number
     * @return
     */
    public int getColumnByName(Sheet sheet, String strSearch, Integer intHeaderRow) {
        for (int i = 0; i < sheet.getRow(intHeaderRow).getLastCellNum(); i++) {
            String strValue = getCellValue(sheet.getWorkbook(), sheet.getRow(intHeaderRow).getCell(i));
            if (strValue.equalsIgnoreCase(strSearch))
                return sheet.getRow(intHeaderRow).getCell(i).getColumnIndex();
        }
        throw new NullPointerException("Column " + strSearch + " not Found on Sheet " + sheet.getSheetName());
    }

    /**
     * Get cell value
     * @param workbook which workbook
     * @param cell cell will be checked by type
     * @param intRow which row. intRow is optional
     * @return value
     */
    public String getCellValue(Workbook workbook, Cell cell, int... intRow) {
        String strValue = "";
        try {
            switch (cell.getCellType()) {
                case STRING:
                    strValue = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    strValue = (cell.getNumericCellValue() + "").replace(".0", "");
                    break;
                case BOOLEAN:
                    strValue = cell.getBooleanCellValue() + "";
                    break;
                case FORMULA:
                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    strValue = processFormula(evaluator, cell, intRow[0]);
                    break;
                default:
                    strValue = "";
                    break;
            }
        } catch (NullPointerException e) {
            strValue = "";
        }

        return strValue;

    }


    /**
     * process cell if in the cell is formula
     * @param evaluator evaluator for process the formula
     * @param cell which cell
     * @param intRow which row
     * @return String value
     */
    public String processFormula(FormulaEvaluator evaluator, Cell cell, int intRow) {
        String strValue = "";
        String strCellFormula = cell.getCellFormula();
        String strSheetDatatable = ExtractFormulaSheet(strCellFormula);
        int intIndexDatatable = ExtractFormulaAddress(strCellFormula);
        String strColumn = CellReference.convertNumToColString(intIndexDatatable);
        String strFormula =  strSheetDatatable + "!" + strColumn + (intRow + 1);
        cell.setCellFormula(strFormula);

        switch (evaluator.evaluateInCell(cell).getCellType()) {
            case STRING:
                strValue = evaluator.evaluateInCell(cell).getStringCellValue();
                break;
            case NUMERIC:
                strValue = evaluator.evaluateInCell(cell).getNumericCellValue()+"";
                if (!strValue.equalsIgnoreCase("0.0")) {
                    strValue = (evaluator.evaluateInCell(cell).getNumericCellValue() + "").replace(".0", "");
                } else {
                    strValue = "";
                }
                break;
            case BOOLEAN:
                strValue = evaluator.evaluateInCell(cell).getBooleanCellValue() + "";
                break;
            default:
                break;
        }
        cell.setCellFormula(strFormula);

        return strValue;
    }

    /**
     * Extract the formula for get Sheet
     * @param strFormula formula will split
     * @return sheet from contains the formula
     */
    public static String ExtractFormulaSheet(String strFormula) {
        String strSheet[] = strFormula.split("!");

        return strSheet[0];
    }

    /**
     * Extract the formula for get Address
     * @param strFormula formula will split
     * @return Address from the formula
     */
    public static int ExtractFormulaAddress(String strFormula) {

        String strSheet[] = strFormula.split("!");
        CellReference cellReference = new CellReference(strSheet[1]);

        return cellReference.getCol();
    }

    /**
     * @return sheet of specific testcase on Datatable
     */
    public static Sheet readSpecificSheet(String strPathFile, String strSheetName){
        File file = new File(strPathFile);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("Sheet doesn't exist");
            throw new RuntimeException(e);
        }
        Workbook workbook;
        String fileExtension = FilenameUtils.getExtension(strPathFile);

        if(fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xlsm") ) {
            try {
                workbook = new XSSFWorkbook(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(fileExtension.equalsIgnoreCase("xls")) {
            try {
                workbook = new HSSFWorkbook(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            workbook = null;
        }

        Sheet sheet = workbook.getSheet(strSheetName);
        return sheet;
    }

    /**
     * Get xpath from all executed excel sheet XPATH
     * @return Map(StNameObject,StrXpath)
     */
    public Map getXpath(File[] listDatatableFile) {
        Map<String, XpathDAO> mapXpath = new HashedMap<>();
        for (File fleDatatable : listDatatableFile) {
            Sheet shtXpath = ExcelFactory.init().getSheetExcel(fleDatatable.getPath(), "XPATH");
            for (int i = 1; i <= shtXpath.getLastRowNum(); i++) {
                try {
                    int intColumnObject = ExcelFactory.init().getColumnByName(shtXpath, "OBJECT");
                    int intColumnXpath = ExcelFactory.init().getColumnByName(shtXpath, "XPATH");
                    int intColumnDescription = ExcelFactory.init().getColumnByName(shtXpath, "PAGE");
                    String strObject = ExcelFactory.init().getCellValue(shtXpath.getWorkbook(), shtXpath.getRow(i).getCell(intColumnObject));
                    String strXpath = ExcelFactory.init().getCellValue(shtXpath.getWorkbook(), shtXpath.getRow(i).getCell(intColumnXpath));
                    String strDescription = ExcelFactory.init().getCellValue(shtXpath.getWorkbook(), shtXpath.getRow(i).getCell(intColumnDescription));

                    mapXpath.put(strObject, new XpathDAO(strObject, strXpath, strDescription));
                } catch (NullPointerException e) {
                    System.out.println("Null pointer");
                    e.printStackTrace();
                    break;
                }
            }
        }
        return mapXpath;
    }

    /**
     * Get row number by specific keyword start from current row
     * @param strAnchorKeyword specific keyword name
     * @return row number
     */
    public static int getRowFromCurrentRow(String strAnchorKeyword) {
        Sheet shtAction = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, DynamicTest.trdStrAction.get());
        int intColumnKeyword = ExcelFactory.init().getColumnByName(shtAction, "Keyword");

        int intCounterRow = -1;

        for (int j = DynamicTest.trdIntCounterRow.get(); j <= shtAction.getLastRowNum(); j++) {
            try {
                String strKeyword = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(j).getCell(intColumnKeyword));
                if (strKeyword.equalsIgnoreCase(strAnchorKeyword)) {
                    intCounterRow = j;
                    break;
                }
            } catch (NullPointerException ex) {
            }
        }
        return intCounterRow;
    }

    /**
     * Get row number of specific value and keyword start from current row
     * @param strCompareValue specific value name
     * @param strAnchorKeyword specific keyword name
     * @return row number
     */
    public static int getRowFromCurrentRowByValue(String strCompareValue, String strAnchorKeyword) {
        Sheet shtAction = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, DynamicTest.trdStrAction.get());
        int intColumnValue = ExcelFactory.init().getColumnByName(shtAction, "Value");
        int intColumnKeyword = ExcelFactory.init().getColumnByName(shtAction, "Keyword");

        int intCounterRow = -1;

        for (int j = DynamicTest.trdIntCounterRow.get(); j <= shtAction.getLastRowNum(); j++) {
            try {
                String strKeyword = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(j).getCell(intColumnKeyword));
                String strValue = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(j).getCell(intColumnValue), 1);
                if (strValue.equalsIgnoreCase(strCompareValue) && strKeyword.contains(strAnchorKeyword)) {
                    intCounterRow = j;
                    break;
                }
            } catch (NullPointerException ex) {
            }
        }
        return intCounterRow;
    }

    /**
     * Get row number of specific keyword start from first row
     * @param KeywordName specific keyword name
     * @return
     */
    public static int getRowFromFirstRow(String KeywordName) {
        Sheet shtAction = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, DynamicTest.trdStrAction.get());
        int intColumnKeyword = ExcelFactory.init().getColumnByName(shtAction, "Keyword");

        int intCounterRow = -1;
        for (int j = 0; j <= shtAction.getLastRowNum(); j++) {
            try {
                String strKeyword = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(j).getCell(intColumnKeyword));
                if (strKeyword.equalsIgnoreCase(KeywordName)) {
                    intCounterRow = j;
                    break;
                }
            } catch (NullPointerException ex) {
            }
        }
        return  intCounterRow;
    }



    /**
     * Counter data for forDataBySheet
     * @param intNoData
     * @return int array [Start Row Data,IntTotalData for looping]
     */
    public static int[] getDataForDataBySheet(int intNoData, String strValue) {
        Sheet shtData = null;
        if (strValue.contains(";")) {
            shtData = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, strValue.split(";")[0]);
        } else {
            shtData = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, strValue);
        }
        int intColumnNo = ExcelFactory.init().getColumnByName(shtData, "No");
        int intTotalData = 0;
        int intStartRowData = -1;

        for (int j = 1; j <= shtData.getLastRowNum(); j++) {
            try {
                String strNoData = ExcelFactory.init().getCellValue(shtData.getWorkbook(), shtData.getRow(j).getCell(intColumnNo));
                if (strNoData.equalsIgnoreCase(intNoData + "")) {
                    if (intStartRowData == -1)
                        intStartRowData = j;
                    intTotalData++;
                }
            } catch (NullPointerException ex) {
            }
        }
        return new int[]{intStartRowData, intTotalData};
    }

    public static Map<Integer, String> getTestCaseList(String strDatatablePath) {
        Map<Integer, String> mapTestCaseList = new HashMap<>();
        MyConfig.strPathDatatable = strDatatablePath;

        Sheet shtInfo = ExcelFactory.init().getSheetExcel(strDatatablePath, MyConfig.strDataInfo);

        int intColumnStart = ExcelFactory.init().getColumnByName(shtInfo, "Start Data");
        int intColumnEnd = ExcelFactory.init().getColumnByName(shtInfo, "End Data");
        int intColumnStartData = Integer.parseInt(ExcelFactory.init().getCellValue(shtInfo.getWorkbook(), shtInfo.getRow(1).getCell(intColumnStart)));
        int intColumnEndData = Integer.parseInt(ExcelFactory.init().getCellValue(shtInfo.getWorkbook(), shtInfo.getRow(1).getCell(intColumnEnd)));

        for (int i = intColumnStartData; i <= intColumnEndData; i++) {
            Sheet shtMain = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, MyConfig.strMainSheet);
            int intColumnAction = ExcelFactory.init().getColumnByName(shtMain, MyConfig.strColumnMainSheet);
            String strAction = ExcelFactory.init().getCellValue(shtMain.getWorkbook(), shtMain.getRow(1).getCell(intColumnAction));
            Sheet shtAction = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, strAction);

            if (shtAction == null) {
                ExtentReportsFactory.init().get().log(Status.FAIL, "Sheet action " + strAction + " doesn't exist!");
                throw new NullPointerException("Sheet action " + strAction + " doesn't exist!");
            }

            int intColumnTestCase = ExcelFactory.init().getColumnByName(shtAction, "Testcase");
            String strTestcaseName = "";

            int intTestcaseCount = 1;

            for (int j = 1; j <= shtAction.getLastRowNum(); j++) {
                strTestcaseName = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(j).getCell(intColumnTestCase));
                if (!strTestcaseName.equals("")) {
                    mapTestCaseList.put(intTestcaseCount, strTestcaseName);
                    intTestcaseCount++;
                }
            }
        }

        return mapTestCaseList;
    }
}
