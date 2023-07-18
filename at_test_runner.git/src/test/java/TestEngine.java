import com.framework.MainEngine;
import com.framework.factory.ExcelFactory;
import com.framework.factory.ExcelReportsFactory;
import com.framework.services.MyConfig;
import com.framework.utilities.FilesUtil;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FilenameFilter;

public class TestEngine {

    /**
     * Adjust the test (the method you want to run) on testng.xml OR run them one by one manually
     * Custom several variables based on need
     * You can put your test datatable in "src\\test\\resources\\Datatable\..."
     *
     * Especially for the runCreateReportOnly... method, make sure the screenshot image is in :
     * MyConfig.strPathReport + {datatable name} + \\Screenshot\\
     */

    Logger log = Logger.getLogger("FILE");

    /**
     * setup configuration for Unit Test
     * No need to modify
     */
    @BeforeMethod
    public void setupTest() {
        TestNGConfig.strParallel = "none";
        MyConfig.boolReportOnly = false;
        ExcelReportsFactory.instance = null;
    }

    /**
     * run test with specific Datatable file and generate the report in a single WP file
     */
    @Test
    public void runSpecificDT_WithSingleWP() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\Tes Datatable.xlsx";

        MyConfig.boolCreateWPByData = false;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\");

            if (fileReport.exists()) {
                File fileScreenshot = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\Tes Datatable\\Screenshot\\");
                File fileWPExcel = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\Tes Datatable\\Report\\WP-Tes Datatable.xlsx");
                File fileWPPDF = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\Tes Datatable\\Report\\WP-Tes Datatable.pdf");

                if (fileScreenshot.exists()) {
                    File[] matchingFiles = fileScreenshot.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith("png");
                        }
                    });

                    assert matchingFiles.length == 6 : strErrorMsg.append("Images files aren't equal to 6\n");
                }

                if (fileWPExcel.exists()) {
                    System.out.println("WP Excel file found");

                    assert fileWPExcel.length() > 200000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "WP-1-A.1 Step Login Passed") : strErrorMsg.append("WP-1-A.1 Step Login Passed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "WP-2-A.2 Step Login Failed") : strErrorMsg.append("WP-2-A.2 Step Login Failed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "WP-3-Tes Datatable") : strErrorMsg.append("WP-3-Tes Datatable sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file not found\n");
                }

                if (fileWPPDF.exists()) {
                    System.out.println("WP PDF file found\n");
                    assert fileWPPDF.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 200000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run test with specific Datatable file and generate reports based on the amount of data executed
     */
    @Test
    public void runSpecificDT_WithWPPerData() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\Tes Datatable.xlsx";

        MyConfig.boolCreateWPByData = true;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\");

            if (fileReport.exists()) {
                File fileScreenshot = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\Tes Datatable\\Screenshot\\");

                if (fileScreenshot.exists()) {
                    File[] matchingFiles = fileScreenshot.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith("png");
                        }
                    });

                    assert matchingFiles.length == 6 : strErrorMsg.append("Images files aren't equal to 6\n");
                }

                File fileWPExcel1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\Tes Datatable\\Report\\WP-1_Tes Datatable.xlsx");
                File fileWPPDF1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\Tes Datatable\\Report\\WP-1_Tes Datatable.pdf");

                if (fileWPExcel1.exists()) {
                    System.out.println("WP Excel file (WP-1_Tes Datatable) found");

                    assert fileWPExcel1.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-1_Tes Datatable)");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel1.getPath(), "WP-1-A.1 Step Login Passed") : strErrorMsg.append("WP-1-A.1 Step Login Passed sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-1_Tes Datatable) not found\n");
                }

                if (fileWPPDF1.exists()) {
                    System.out.println("WP PDF file (WP-1_Tes Datatable) found\n");
                    assert fileWPPDF1.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-1_Tes Datatable) not found\n");
                }

                File fileWPExcel2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\Tes Datatable\\Report\\WP-2_Tes Datatable.xlsx");
                File fileWPPDF2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\Tes Datatable\\Report\\WP-2_Tes Datatable.pdf");

                if (fileWPExcel2.exists()) {
                    System.out.println("WP Excel file (WP-2_Tes Datatable) found");

                    assert fileWPExcel2.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-2_Tes Datatable)");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel2.getPath(), "WP-2-A.2 Step Login Failed") : strErrorMsg.append("WP-2-A.2 Step Login Failed sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-2_Tes Datatable) not found\n");
                }

                if (fileWPPDF2.exists()) {
                    System.out.println("WP PDF file (WP-2_Tes Datatable) found\n");
                    assert fileWPPDF2.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-2_Tes Datatable) not found\n");
                }

                File fileWPExcel3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\Tes Datatable\\Report\\WP-3_Tes Datatable.xlsx");
                File fileWPPDF3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\Tes Datatable\\Report\\WP-3_Tes Datatable.pdf");

                if (fileWPExcel3.exists()) {
                    System.out.println("WP Excel file (WP-3_Tes Datatable) found");

                    assert fileWPExcel3.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-3_Tes Datatable)");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel3.getPath(), "WP-3-Tes Datatable") : strErrorMsg.append("WP-3-Tes Datatable sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-3_Tes Datatable) not found\n");
                }

                if (fileWPPDF3.exists()) {
                    System.out.println("WP PDF file (WP-3_Tes Datatable) found\n");
                    assert fileWPPDF3.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-3_Tes Datatable) not found\n");
                }

                File fileAutomationReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\AutomationReport.xlsx");

                if (fileAutomationReport.exists()) {
                    System.out.println("Automation Report file found");
                    assert ExcelFactory.init().sheetIsExist(fileAutomationReport.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert fileAutomationReport.length() > 3000 : strErrorMsg.append("something went wrong with the Automation Report file\n");
                } else {
                    strErrorMsg.append("Automation Report file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithWPPerData\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 200000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run all Datatable files in specified Directory and generate the report in a single WP file
     */
    @Test
    public void runDTOnDir_WithSingleWP() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\dir\\";

        MyConfig.boolCreateWPByData = false;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\");

            if (fileReport.exists()) {
                File file1Screenshot = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 1\\Screenshot\\");
                File file1WPExcel = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 1\\Report\\WP-Tes Datatable 1.xlsx");
                File file1WPPDF = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 1\\Report\\WP-Tes Datatable 1.pdf");

                if (file1Screenshot.exists()) {
                    File[] matchingFiles = file1Screenshot.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith("png");
                        }
                    });

                    assert matchingFiles.length == 8 : strErrorMsg.append("Images files of Tes Datatable 1 aren't equal to 8\n");
                }

                if (file1WPExcel.exists()) {
                    System.out.println("WP Excel file (WP-Tes Datatable 1) found");

                    assert file1WPExcel.length() > 200000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-Tes Datatable 1)");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel.getPath(), "WP-1-A.1 Step Login Passed") : strErrorMsg.append("WP-1-A.1 Step Login Passed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel.getPath(), "WP-2-A.2 Step Login Failed") : strErrorMsg.append("WP-2-A.2 Step Login Failed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel.getPath(), "WP-3-Tes Datatable 1") : strErrorMsg.append("WP-3-Tes Datatable 1 sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel.getPath(), "WP-4-TestScenarioNameLengthIs31") : strErrorMsg.append("WP-4-TestScenarioNameLengthIs31\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-Tes Datatable 1) not found\n");
                }

                if (file1WPPDF.exists()) {
                    System.out.println("WP PDF file (WP-Tes Datatable 1) found\n");
                    assert file1WPPDF.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-Tes Datatable 1) not found\n");
                }

                File file2Screenshot = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 2\\Screenshot\\");
                File file2WPExcel = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 2\\Report\\WP-Tes Datatable 2.xlsx");
                File file2WPPDF = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 2\\Report\\WP-Tes Datatable 2.pdf");

                if (file2Screenshot.exists()) {
                    File[] matchingFiles = file2Screenshot.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith("png");
                        }
                    });

                    assert matchingFiles.length == 8 : strErrorMsg.append("Images files of Tes Datatable 2 aren't equal to 8\n");
                }

                if (file2WPExcel.exists()) {
                    System.out.println("WP Excel file (WP-Tes Datatable 2) found");

                    assert file2WPExcel.length() > 200000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-Tes Datatable 2)");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel.getPath(), "WP-5-TestScenarioNameLengthIsMo") : strErrorMsg.append("WP-5-TestScenarioNameLengthIsMo\n");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel.getPath(), "WP-6-UserID Kosong") : strErrorMsg.append("WP-6-UserID Kosong\n");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel.getPath(), "WP-7-12345678901234567890123456") : strErrorMsg.append("WP-7-12345678901234567890123456\n");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel.getPath(), "WP-8-UserID & Password Kosong") : strErrorMsg.append("WP-8-UserID & Password Kosong\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-Tes Datatable 2) not found\n");
                }

                if (file2WPPDF.exists()) {
                    System.out.println("WP PDF file (WP-Tes Datatable 2) found\n");
                    assert file2WPPDF.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-Tes Datatable 2) not found\n");
                }

                File fileAutomationReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\AutomationReport.xlsx");

                if (fileAutomationReport.exists()) {
                    System.out.println("Automation Report file found");
                    assert ExcelFactory.init().sheetIsExist(fileAutomationReport.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert fileAutomationReport.length() > 3000 : strErrorMsg.append("something went wrong with the Automation Report file\n");
                } else {
                    strErrorMsg.append("Automation Report file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 500000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run all Datatable files in specified Directory and generate reports based on the amount of data executed
     */
    @Test
    public void runDTOnDir_WithWPPerData() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\dir\\";

        MyConfig.boolCreateWPByData = true;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\");

            if (fileReport.exists()) {
                File file1Screenshot = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Screenshot\\");

                if (file1Screenshot.exists()) {
                    File[] matchingFiles = file1Screenshot.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith("png");
                        }
                    });

                    assert matchingFiles.length == 8 : strErrorMsg.append("Images files of Tes Datatable 1 aren't equal to 8\n");
                }

                File file1WPExcel1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-1_Tes Datatable 1.xlsx");
                File file1WPPDF1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-1_Tes Datatable 1.pdf");

                if (file1WPExcel1.exists()) {
                    System.out.println("WP Excel file (WP-1_Tes Datatable 1) found");

                    assert file1WPExcel1.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-1_Tes Datatable 1)");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel1.getPath(), "WP-1-A.1 Step Login Passed") : strErrorMsg.append("WP-1-A.1 Step Login Passed sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-1_Tes Datatable 1) not found\n");
                }

                if (file1WPPDF1.exists()) {
                    System.out.println("WP PDF file (WP-1_Tes Datatable 1) found\n");
                    assert file1WPPDF1.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-1_Tes Datatable 1) not found\n");
                }

                File file1WPExcel2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-2_Tes Datatable 1.xlsx");
                File file1WPPDF2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-2_Tes Datatable 1.pdf");

                if (file1WPExcel2.exists()) {
                    System.out.println("WP Excel file (WP-2_Tes Datatable 1) found");

                    assert file1WPExcel2.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-2_Tes Datatable 1)");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel2.getPath(), "WP-2-A.2 Step Login Failed") : strErrorMsg.append("WP-2-A.2 Step Login Failed sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-2_Tes Datatable 1) not found\n");
                }

                if (file1WPPDF2.exists()) {
                    System.out.println("WP PDF file (WP-2_Tes Datatable 1) found\n");
                    assert file1WPPDF2.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-2_Tes Datatable 1) not found\n");
                }

                File file1WPExcel3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-3_Tes Datatable 1.xlsx");
                File file1WPPDF3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-3_Tes Datatable 1.pdf");

                if (file1WPExcel3.exists()) {
                    System.out.println("WP Excel file (WP-3_Tes Datatable 1) found");

                    assert file1WPExcel3.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-3_Tes Datatable 1)");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel3.getPath(), "WP-3-Tes Datatable 1") : strErrorMsg.append("WP-3-Tes Datatable 1 sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-3_Tes Datatable 1) not found\n");
                }

                if (file1WPPDF3.exists()) {
                    System.out.println("WP PDF file (WP-3_Tes Datatable 1) found\n");
                    assert file1WPPDF3.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-3_Tes Datatable 1) not found\n");
                }

                File file1WPExcel4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-4_Tes Datatable 1.xlsx");
                File file1WPPDF4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 1\\Report\\WP-4_Tes Datatable 1.pdf");

                if (file1WPExcel4.exists()) {
                    System.out.println("WP Excel file (WP-4_Tes Datatable 1) found");

                    assert file1WPExcel4.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-4_Tes Datatable 1)");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel4.getPath(), "WP-4-TestScenarioNameLengthIs31") : strErrorMsg.append("WP-4-TestScenarioNameLengthIs31 sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-4_Tes Datatable 1) not found\n");
                }

                if (file1WPPDF4.exists()) {
                    System.out.println("WP PDF file (WP-4_Tes Datatable 1) found\n");
                    assert file1WPPDF4.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-4_Tes Datatable 1) not found\n");
                }

                File file2Screenshot = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Screenshot\\");

                if (file2Screenshot.exists()) {
                    File[] matchingFiles = file2Screenshot.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith("png");
                        }
                    });

                    assert matchingFiles.length == 8 : strErrorMsg.append("Images files of Tes Datatable 2 aren't equal to 8\n");
                }

                File file2WPExcel1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-5_Tes Datatable 2.xlsx");
                File file2WPPDF1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-5_Tes Datatable 2.pdf");

                if (file2WPExcel1.exists()) {
                    System.out.println("WP Excel file (WP-5_Tes Datatable 2) found");

                    assert file2WPExcel1.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-5_Tes Datatable 2)");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel1.getPath(), "WP-5-TestScenarioNameLengthIsMo") : strErrorMsg.append("WP-5-TestScenarioNameLengthIsMo sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-5_Tes Datatable 2) not found\n");
                }

                if (file2WPPDF1.exists()) {
                    System.out.println("WP PDF file (WP-5_Tes Datatable 2) found\n");
                    assert file2WPPDF1.length() > 1000 : "something went wrong with the WP PDF file";

                } else {
                    strErrorMsg.append("WP PDF file (WP-5_Tes Datatable 2) not found\n");
                }

                File file2WPExcel2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-6_Tes Datatable 2.xlsx");
                File file2WPPDF2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-6_Tes Datatable 2.pdf");

                if (file2WPExcel2.exists()) {
                    System.out.println("WP Excel file (WP-6_Tes Datatable 2) found");

                    assert file2WPExcel2.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-6_Tes Datatable 2)");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel2.getPath(), "WP-6-UserID Kosong") : strErrorMsg.append("WP-6-UserID Kosong sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-6_Tes Datatable 2) not found\n");
                }

                if (file2WPPDF2.exists()) {
                    System.out.println("WP PDF file (WP-6_Tes Datatable 2) found\n");
                    assert file2WPPDF2.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-6_Tes Datatable 2) not found\n");
                }

                File file2WPExcel3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-7_Tes Datatable 2.xlsx");
                File file2WPPDF3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-7_Tes Datatable 2.pdf");

                if (file2WPExcel3.exists()) {
                    System.out.println("WP Excel file (WP-7_Tes Datatable 2) found");

                    assert file2WPExcel3.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-7_Tes Datatable 2)");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel3.getPath(), "WP-7-12345678901234567890123456") : strErrorMsg.append("WP-7-12345678901234567890123456 sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-7_Tes Datatable 2) not found\n");
                }

                if (file2WPPDF3.exists()) {
                    System.out.println("WP PDF file (WP-7_Tes Datatable 2) found\n");
                    assert file2WPPDF3.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-7_Tes Datatable 2) not found\n");
                }

                File file2WPExcel4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-8_Tes Datatable 2.xlsx");
                File file2WPPDF4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\Tes Datatable 2\\Report\\WP-8_Tes Datatable 2.pdf");

                if (file2WPExcel4.exists()) {
                    System.out.println("WP Excel file (WP-8_Tes Datatable 2) found");

                    assert file2WPExcel4.length() > 100000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-8_Tes Datatable 2)");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel4.getPath(), "WP-8-UserID & Password Kosong") : strErrorMsg.append("WP-8-UserID & Password Kosong sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-8_Tes Datatable 2) not found\n");
                }

                if (file2WPPDF4.exists()) {
                    System.out.println("WP PDF file (WP-8_Tes Datatable 2) found\n");
                    assert file2WPPDF4.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-8_Tes Datatable 2) not found\n");
                }

                File fileAutomationReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\AutomationReport.xlsx");

                if (fileAutomationReport.exists()) {
                    System.out.println("Automation Report file found");
                    assert ExcelFactory.init().sheetIsExist(fileAutomationReport.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert fileAutomationReport.length() > 3000 : strErrorMsg.append("something went wrong with the Automation Report file\n");
                } else {
                    strErrorMsg.append("Automation Report file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithWPPerData\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 500000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run API test with specific Datatable file and generate the report in single WP file
     */
    @Test
    public void runSpecificAPIDT_WithSingleWP() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithSingleWP\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\Tes API Account Name Validation (Simulator API).xlsx";

        MyConfig.boolCreateWPByData = false;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithSingleWP\\");

            if (fileReport.exists()) {
                File fileWPExcel = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithSingleWP\\Tes API Account Name Validation (Simulator API)\\Report\\WP-Tes API Account Name Validation (Simulator API).xlsx");
                File fileWPPDF = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithSingleWP\\Tes API Account Name Validation (Simulator API)\\Report\\WP-Tes API Account Name Validation (Simulator API).pdf");

                if (fileWPExcel.exists()) {
                    System.out.println("WP Excel file found");

                    assert fileWPExcel.length() > 20000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file not found\n");
                }

                if (fileWPPDF.exists()) {
                    System.out.println("WP PDF file found\n");
                    assert fileWPPDF.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithSingleWP\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 20000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run API test with specific Datatable file and generate reports based on the amount of data executed
     */
    @Test
    public void runSpecificAPIDT_WithWPPerData() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\Tes API Account Name Validation (Simulator API).xlsx";

        MyConfig.boolCreateWPByData = true;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\");

            if (fileReport.exists()) {
                File fileWPExcel1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\Tes API Account Name Validation (Simulator API)\\Report\\WP-1_Tes API Account Name Validation (Simulator API).xlsx");
                File fileWPPDF1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\Tes API Account Name Validation (Simulator API)\\Report\\WP-1_Tes API Account Name Validation (Simulator API).pdf");

                if (fileWPExcel1.exists()) {
                    System.out.println("WP Excel file (WP-1_Tes API Account Name Validation (Simulator API)) found");

                    assert fileWPExcel1.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel1.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-1_Tes API Account Name Validation (Simulator API)) not found\n");
                }

                if (fileWPPDF1.exists()) {
                    System.out.println("WP PDF file (WP-1_Tes API Account Name Validation (Simulator API)) found\n");
                    assert fileWPPDF1.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-1_Tes API Account Name Validation (Simulator API)) not found\n");
                }

                File fileWPExcel2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\Tes API Account Name Validation (Simulator API)\\Report\\WP-2_Tes API Account Name Validation (Simulator API).xlsx");
                File fileWPPDF2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\Tes API Account Name Validation (Simulator API)\\Report\\WP-2_Tes API Account Name Validation (Simulator API).pdf");

                if (fileWPExcel2.exists()) {
                    System.out.println("WP Excel file (WP-2_Tes API Account Name Validation (Simulator API)) found");

                    assert fileWPExcel2.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel2.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-2_Tes API Account Name Validation (Simulator API)) not found\n");
                }

                if (fileWPPDF2.exists()) {
                    System.out.println("WP PDF file (WP-2_Tes API Account Name Validation (Simulator API)) found\n");
                    assert fileWPPDF2.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-2_Tes API Account Name Validation (Simulator API)) not found\n");
                }

                File fileWPExcel3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\Tes API Account Name Validation (Simulator API)\\Report\\WP-3_Tes API Account Name Validation (Simulator API).xlsx");
                File fileWPPDF3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\Tes API Account Name Validation (Simulator API)\\Report\\WP-3_Tes API Account Name Validation (Simulator API).pdf");

                if (fileWPExcel3.exists()) {
                    System.out.println("WP Excel file (WP-3_Tes API Account Name Validation (Simulator API)) found");

                    assert fileWPExcel3.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel3.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-3_Tes API Account Name Validation (Simulator API)) not found\n");
                }

                if (fileWPPDF3.exists()) {
                    System.out.println("WP PDF file (WP-3_Tes API Account Name Validation (Simulator API)) found\n");
                    assert fileWPPDF3.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-3_Tes API Account Name Validation (Simulator API)) not found\n");
                }

                File fileAutomationReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\AutomationReport.xlsx");

                if (fileAutomationReport.exists()) {
                    System.out.println("Automation Report file found");
                    assert ExcelFactory.init().sheetIsExist(fileAutomationReport.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert fileAutomationReport.length() > 3000 : strErrorMsg.append("something went wrong with the Automation Report file\n");
                } else {
                    strErrorMsg.append("Automation Report file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APISpecificDTWithWPPerData\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 25000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run all API Datatable files in specified Directory and generate the report in a single WP file
     */
    @Test
    public void runAPIDT_OnDir_WithSingleWP() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\api\\";

        MyConfig.boolCreateWPByData = false;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\");

            if (fileReport.exists()) {
                File file1WPExcel = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-Tes API Account Name Validation (Simulator API) 1.xlsx");
                File file1WPPDF = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-Tes API Account Name Validation (Simulator API) 1.pdf");

                if (file1WPExcel.exists()) {
                    System.out.println("WP Excel file (WP-Tes API Account Name Validation (Simulator API) 1) found");

                    assert file1WPExcel.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                if (file1WPPDF.exists()) {
                    System.out.println("WP PDF file (WP-Tes API Account Name Validation (Simulator API) 1) found\n");
                    assert file1WPPDF.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                File file2WPExcel = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-Tes API Account Name Validation (Simulator API) 2.xlsx");
                File file2WPPDF = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-Tes API Account Name Validation (Simulator API) 2.pdf");

                if (file2WPExcel.exists()) {
                    System.out.println("WP Excel file (WP-Tes API Account Name Validation (Simulator API) 2) found");

                    assert file2WPExcel.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                if (file2WPPDF.exists()) {
                    System.out.println("WP PDF file (WP-Tes API Account Name Validation (Simulator API) 2) found\n");
                    assert file2WPPDF.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                File fileAutomationReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\AutomationReport.xlsx");

                if (fileAutomationReport.exists()) {
                    System.out.println("Automation Report file found");
                    assert ExcelFactory.init().sheetIsExist(fileAutomationReport.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert fileAutomationReport.length() > 10000 : strErrorMsg.append("something went wrong with the Automation Report file\n");
                } else {
                    strErrorMsg.append("Automation Report file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 20000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run all API Datatable files in specified Directory and generate reports based on the amount of data executed
     */
    @Test
    public void runAPIDT_OnDir_WithWPPerData() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\api\\";

        MyConfig.boolCreateWPByData = true;

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\");

            if (fileReport.exists()) {
                File file1WPExcel1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-1_Tes API Account Name Validation (Simulator API) 1.xlsx");
                File file1WPPDF1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-1_Tes API Account Name Validation (Simulator API) 1.pdf");

                if (file1WPExcel1.exists()) {
                    System.out.println("WP Excel file (WP-1_Tes API Account Name Validation (Simulator API) 1) found");

                    assert file1WPExcel1.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel1.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-1_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                if (file1WPPDF1.exists()) {
                    System.out.println("WP PDF file (WP-1_Tes API Account Name Validation (Simulator API) 1) found\n");
                    assert file1WPPDF1.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-1_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                File file1WPExcel2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-2_Tes API Account Name Validation (Simulator API) 1.xlsx");
                File file1WPPDF2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-2_Tes API Account Name Validation (Simulator API) 1.pdf");

                if (file1WPExcel2.exists()) {
                    System.out.println("WP Excel file (WP-2_Tes API Account Name Validation (Simulator API) 1) found");

                    assert file1WPExcel2.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel2.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-2_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                if (file1WPPDF2.exists()) {
                    System.out.println("WP PDF file (WP-2_Tes API Account Name Validation (Simulator API) 1) found\n");
                    assert file1WPPDF2.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-2_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                File file1WPExcel3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-3_Tes API Account Name Validation (Simulator API) 1.xlsx");
                File file1WPPDF3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-3_Tes API Account Name Validation (Simulator API) 1.pdf");

                if (file1WPExcel3.exists()) {
                    System.out.println("WP Excel file (WP-3_Tes API Account Name Validation (Simulator API) 1) found");

                    assert file1WPExcel3.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel3.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-3_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                if (file1WPPDF3.exists()) {
                    System.out.println("WP PDF file (WP-3_Tes API Account Name Validation (Simulator API) 1) found\n");
                    assert file1WPPDF3.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-3_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                File file1WPExcel4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-4_Tes API Account Name Validation (Simulator API) 1.xlsx");
                File file1WPPDF4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 1\\Report\\WP-4_Tes API Account Name Validation (Simulator API) 1.pdf");

                if (file1WPExcel4.exists()) {
                    System.out.println("WP Excel file (WP-4_Tes API Account Name Validation (Simulator API) 1) found");

                    assert file1WPExcel4.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file1WPExcel2.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-4_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                if (file1WPPDF4.exists()) {
                    System.out.println("WP PDF file (WP-4_Tes API Account Name Validation (Simulator API) 1) found\n");
                    assert file1WPPDF4.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-4_Tes API Account Name Validation (Simulator API) 1) not found\n");
                }

                File file2WPExcel1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-5_Tes API Account Name Validation (Simulator API) 2.xlsx");
                File file2WPPDF1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-5_Tes API Account Name Validation (Simulator API) 2.pdf");

                if (file2WPExcel1.exists()) {
                    System.out.println("WP Excel file (WP-5_Tes API Account Name Validation (Simulator API) 2) found");

                    assert file2WPExcel1.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel1.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-5_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                if (file2WPPDF1.exists()) {
                    System.out.println("WP PDF file (WP-5_Tes API Account Name Validation (Simulator API) 2) found\n");
                    assert file2WPPDF1.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-5_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                File file2WPExcel2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-6_Tes API Account Name Validation (Simulator API) 2.xlsx");
                File file2WPPDF2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-6_Tes API Account Name Validation (Simulator API) 2.pdf");

                if (file2WPExcel2.exists()) {
                    System.out.println("WP Excel file (WP-6_Tes API Account Name Validation (Simulator API) 2) found");

                    assert file2WPExcel2.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel2.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-6_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                if (file2WPPDF2.exists()) {
                    System.out.println("WP PDF file (WP-6_Tes API Account Name Validation (Simulator API) 2) found\n");
                    assert file2WPPDF2.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-6_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                File file2WPExcel3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-7_Tes API Account Name Validation (Simulator API) 2.xlsx");
                File file2WPPDF3 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-7_Tes API Account Name Validation (Simulator API) 2.pdf");

                if (file2WPExcel3.exists()) {
                    System.out.println("WP Excel file (WP-7_Tes API Account Name Validation (Simulator API) 2) found");

                    assert file2WPExcel3.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel3.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-7_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                if (file2WPPDF3.exists()) {
                    System.out.println("WP PDF file (WP-7_Tes API Account Name Validation (Simulator API) 2) found\n");
                    assert file2WPPDF3.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-7_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                File file2WPExcel4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-8_Tes API Account Name Validation (Simulator API) 2.xlsx");
                File file2WPPDF4 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\Tes API Account Name Validation (Simulator API) 2\\Report\\WP-8_Tes API Account Name Validation (Simulator API) 2.pdf");

                if (file2WPExcel4.exists()) {
                    System.out.println("WP Excel file (WP-8_Tes API Account Name Validation (Simulator API) 2) found");

                    assert file2WPExcel4.length() > 15000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(file2WPExcel4.getPath(), "Report API") : strErrorMsg.append("Report API sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-8_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                if (file2WPPDF4.exists()) {
                    System.out.println("WP PDF file (WP-8_Tes API Account Name Validation (Simulator API) 2) found\n");
                    assert file2WPPDF4.length() > 1000 : "something went wrong with the WP PDF file";
                } else {
                    strErrorMsg.append("WP PDF file (WP-8_Tes API Account Name Validation (Simulator API) 2) not found\n");
                }

                File fileAutomationReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithWPPerData\\AutomationReport.xlsx");

                if (fileAutomationReport.exists()) {
                    System.out.println("Automation Report file found");
                    assert ExcelFactory.init().sheetIsExist(fileAutomationReport.getPath(), "REPORT") : strErrorMsg.append("REPORT sheet not found\n");
                    assert fileAutomationReport.length() > 10000 : strErrorMsg.append("something went wrong with the Automation Report file\n");
                } else {
                    strErrorMsg.append("Automation Report file not found\n");
                }

                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\APIDTOnDirWithSingleWP\\HTMLReport\\Report.html");

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 20000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * run tests in parallel on all data in a specific Datatable file
     */
    @Test
    public void runSpecificDT_InParallel() {
        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTinParallel\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\Tes Datatable.xlsx";

        //Option : classes | instance | methods | tests | none
        TestNGConfig.strParallel = "tests";
        TestNGConfig.intThreadCount = 4;
        TestNGConfig.intThreadThreadDataProvidersCount = 4;

        MyConfig.propertiesTestNG.setProperty("Parallel", TestNGConfig.strParallel);

        mainUnitTest();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTinParallel\\");

            if (fileReport.exists()) {
                File fileScreenshot = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTinParallel\\Tes Datatable\\Screenshot\\");
                File fileHTMLReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTinParallel\\HTMLReport\\Report.html");

                if (fileScreenshot.exists()) {
                    File[] matchingFiles = fileScreenshot.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith("png");
                        }
                    });

                    assert matchingFiles.length == 6 : strErrorMsg.append("Images files aren't equal to 6\n");
                }

                if (fileHTMLReport.exists()) {
                    System.out.println("HTML Report file found");
                    assert fileHTMLReport.length() > 200000 : strErrorMsg.append("something went wrong with the HTML Report file\n");
                } else {
                    strErrorMsg.append("HTML Report file not found\n");
                }

            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * creates a WP report for a single datatable file based on the image files that are in a specific report path
     * !!! to run this make sure the images already exist in strPathReport + {datatable name} + \\Screenshot\\
     */
    @Test
    public void runCreateReportOnly_SingleWP() {
        MyConfig.boolReportOnly = true;

        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\Tes Datatable.xlsx";

        MyConfig.boolCreateWPByData = false;

        WPReportOnly();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\");

            if (fileReport.exists()) {
                File fileWPExcel = new File("C:\\Screens\\UnitTestMainFrameworkReport\\SpecificDTWithSingleWP\\Tes Datatable\\Report\\WP-Tes Datatable.xlsx");

                if (fileWPExcel.exists()) {
                    System.out.println("WP Excel file is exist");

                    assert fileWPExcel.length() > 200000 : strErrorMsg.append("something went wrong with the WP Excel file");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "WP-1-A.1 Step Login Passed") : strErrorMsg.append("WP-1-A.1 Step Login Passed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "WP-2-A.2 Step Login Failed") : strErrorMsg.append("WP-2-A.2 Step Login Failed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel.getPath(), "WP-3-Tes Datatable") : strErrorMsg.append("WP-3-Tes Datatable sheet not found\n");
                } else {
                    strErrorMsg.append("WP Excel file doesn't exist\n");
                }
            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * generate multiple WP report files based on image files of each data number present in a particular report path
     * to run this make sure the images already exist in strPathReport + {datatable name} + \\Screenshot\\
     */
    @Test
    public void runCreateReportOnly_MultiDT_SingleWP() {
        MyConfig.boolReportOnly = true;

        MyConfig.strPathReport = "C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\";
        MyConfig.strPathDatatableDir = "src\\test\\resources\\Datatable\\dir\\";

        MyConfig.boolCreateWPByData = false;

        WPReportOnly();

        try {
            StringBuilder strErrorMsg = new StringBuilder("\nERROR MESSAGE:\n");
            File fileReport = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\");

            if (fileReport.exists()) {
                File fileWPExcel1 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 1\\Report\\WP-Tes Datatable 1.xlsx");

                if (fileWPExcel1.exists()) {
                    System.out.println("WP Excel file (WP-Tes Datatable 1) found");

                    assert fileWPExcel1.length() > 200000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-Tes Datatable 1)");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel1.getPath(), "WP-1-A.1 Step Login Passed") : strErrorMsg.append("WP-1-A.1 Step Login Passed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel1.getPath(), "WP-2-A.2 Step Login Failed") : strErrorMsg.append("WP-2-A.2 Step Login Failed sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel1.getPath(), "WP-3-Tes Datatable 1") : strErrorMsg.append("WP-3-Tes Datatable 1 sheet not found\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel1.getPath(), "WP-4-TestScenarioNameLengthIs31") : strErrorMsg.append("WP-4-TestScenarioNameLengthIs31\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-Tes Datatable 1) not found\n");
                }

                File fileWPExcel2 = new File("C:\\Screens\\UnitTestMainFrameworkReport\\DTOnDirWithSingleWP\\Tes Datatable 2\\Report\\WP-Tes Datatable 2.xlsx");

                if (fileWPExcel2.exists()) {
                    System.out.println("WP Excel file (WP-Tes Datatable 2) found");

                    assert fileWPExcel2.length() > 200000 : strErrorMsg.append("something went wrong with the WP Excel file (WP-Tes Datatable 2)");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel2.getPath(), "WP-5-TestScenarioNameLengthIsMo") : strErrorMsg.append("WP-5-TestScenarioNameLengthIsMo\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel2.getPath(), "WP-6-UserID Kosong") : strErrorMsg.append("WP-6-UserID Kosong\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel2.getPath(), "WP-7-12345678901234567890123456") : strErrorMsg.append("WP-7-12345678901234567890123456\n");
                    assert ExcelFactory.init().sheetIsExist(fileWPExcel2.getPath(), "WP-8-UserID & Password Kosong") : strErrorMsg.append("WP-8-UserID & Password Kosong\n");
                } else {
                    strErrorMsg.append("WP Excel file (WP-Tes Datatable 2) not found\n");
                }
            } else {
                strErrorMsg.append("UnitTestMainFrameworkReport directory not found\n");
            }

            if (strErrorMsg.length() > 16) {
                throw new Exception(strErrorMsg.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create test for unit tests
     */
    public void mainUnitTest() {
        MyConfig.strPathDatatable = FilesUtil.init().getListOfDatatable()[0].getPath();
        MyConfig.intDataCounter = 1;

        log.info("Start Automation");

        try {
            FilesUtil.init().deleteFolder(new File(MyConfig.strPathReport));
            TestDynamicXMLFactory.init().createTest("Test Dynamic XML");
        } catch (NullPointerException e) {
            log.error("Action not found!");
            e.printStackTrace();
        } catch (Exception e) {
            log.error(e.getCause());
            e.printStackTrace();
        }

        log.info("End Automation");
    }

    /**
     * create Excel & PDF report only
     */
    public void WPReportOnly() {
        MainEngine.WPReportOnly();
        System.out.println("Done Create WP");
    }
}
