package com.framework.factory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.framework.services.MyConfig;

public class ExtentReportsFactory {
    public static ThreadLocal<Status> trdAPIExtentSuccessStatus = new ThreadLocal<>();
    private static volatile ExtentReportsFactory instance = null;

    private ExtentReportsFactory() {
    }

    public static ExtentReportsFactory init() {
        if (instance == null) {
            synchronized (ExtentReportsFactory.class) {
                if (instance == null)
                    instance = new ExtentReportsFactory();
            }
        }
        return instance;
    }

    volatile ThreadLocal<ExtentTest> trdExtentTest = new ThreadLocal<>();

    public ExtentTest get() {
        return trdExtentTest.get();
    }

    public void set(ExtentTest extentTest) {
        trdExtentTest.set(extentTest);
    }

    public void remove() {
        trdExtentTest.remove();
    }

    /**
     * @return setup ExtentReports
     */
    public ExtentReports setupExtentReports() {
//        String strTitle = FilesUtil.getDatatableFileName();
        ExtentSparkReporter extentSparkReporter = new ExtentSparkReporter(MyConfig.strPathReport + "\\HTMLReport\\Report.html");
        ExtentReports extentReports = new ExtentReports();

        extentReports.attachReporter(extentSparkReporter);

        //Setting config the spark
        extentSparkReporter.config().setTheme(Theme.DARK);


        //Setting config the spark with offline mode (local css, js, & logo image)
        extentSparkReporter.config().setOfflineMode(true);

        extentSparkReporter.config().setDocumentTitle("Dynamic Automation Testing Extent Report");
        extentSparkReporter.config().setReportName("Dynamic Automation Testing Report");

        //Setting config ExtentReport
        extentReports.setSystemInfo("Execute by ", "Automation Team");
        extentReports.setSystemInfo("Execute on ", "DynamicAutomationTest Framework");

        return extentReports;
    }

    /**
     * Create test and put into Thread Local
     *
     * @param extentReports  which ExtentReports
     * @param strTestName    strTestName for the report
     * @param strDescription strDescription for the report
     * @param strCategory    strCategory is TAG Test Group for the report
     */
    public void createTest(ExtentReports extentReports, String strTestName, String strDescription, String strCategory) {
        set(
                extentReports.createTest(strTestName, strDescription)
                        .assignCategory(strCategory)
        );
    }


}
