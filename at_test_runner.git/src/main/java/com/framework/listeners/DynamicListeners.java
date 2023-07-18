package com.framework.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.framework.DynamicTest;
import com.framework.baseFunctions.BaseController;
import com.framework.factory.DriverFactory;
import com.framework.factory.ExtentReportsFactory;
import com.framework.factory.OracleFactory;
import com.framework.factory.TestFactory;
import com.framework.services.BDSService;
import com.framework.services.ReportService;
import com.framework.services.ScreenshotService;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Properties;

public class DynamicListeners implements ITestListener {
    protected static ThreadLocal<StopWatch> trdStopwatchTest = new ThreadLocal<>();
    public static ThreadLocal<Properties> trdDriverProperties = new ThreadLocal<>();
    public static ThreadLocal<String> trdTestDuration = new ThreadLocal<>();
    public static ThreadLocal<String> trdStrDescription = new ThreadLocal<>();
    private Logger log = Logger.getLogger("FILE");
    private Logger logNetwork = Logger.getLogger("NETWORK");

    ExtentReports extentReports;

    @Override
    public synchronized void onTestStart(ITestResult result) {
        trdStopwatchTest.set(new StopWatch());
        trdStopwatchTest.get().start();

        trdStrDescription.set("");

        Class cls = TestFactory.thrDynamicClass.get();
        try {
            Integer intNo = (int) ((ThreadLocal) cls.getDeclaredField("trdIntNoData").get(null)).get();
            String strAction = (String) ((ThreadLocal) cls.getDeclaredField("trdStrAction").get(null)).get();
            String strTestGroup = (String) ((ThreadLocal) cls.getDeclaredField("trdTestGroup").get(null)).get();
            String strScenario = (String) ((ThreadLocal) cls.getDeclaredField("trdScenario").get(null)).get();
            String strDatatableName = (String) ((ThreadLocal) cls.getDeclaredField("trdDatatableName").get(null)).get();
            String strTestName = "[" + strDatatableName + "] " + intNo + "-" + strAction;

            ExtentReportsFactory.init().createTest(extentReports, strTestName, strScenario, strTestGroup);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (DriverFactory.init().get() != null) {
            DriverFactory.init().tearDown();
        }

        if (trdStopwatchTest.get() != null) {
            trdStopwatchTest.get().stop();
            trdTestDuration.set(trdStopwatchTest.get().toString());
            trdStopwatchTest.get().reset();
        }

        ExtentReportsFactory.init().get().log(Status.PASS, "Test Passed");

        ReportService.createExcelPDFReport();
        ReportService.appendStatusToReport("PASSED", trdStrDescription.get());

        String strResult = "PASSED";

        if (ExtentReportsFactory.trdAPIExtentSuccessStatus.get() != null ) {
            strResult = ExtentReportsFactory.trdAPIExtentSuccessStatus.get().toString().equalsIgnoreCase("Pass") ? "PASSED" : "FAILED";
        }

        log.info("Data : " + DynamicTest.trdTestName.get() + " [" + strResult + "]");
        logNetwork.info("Data : " + DynamicTest.trdTestName.get() + " [" + strResult + "]");

    }

    @Override
    public void onTestFailure(ITestResult result) {
        BDSService.closeBDSIsExist();

        if (DriverFactory.init().get() != null) {
            if (DriverFactory.init().get().toString().contains("Chrome") && !DriverFactory.init().get().toString().contains("Android")) {
                ScreenshotService.init().screenShotFullWhole();
            } else {
                ScreenshotService.init().screenshot();
            }
            DriverFactory.init().tearDown();
        }

        if (trdStopwatchTest.get() != null) {
            trdStopwatchTest.get().stop();
            trdTestDuration.set(trdStopwatchTest.get().toString());
            trdStopwatchTest.get().reset();
        }

        ExtentReportsFactory.init().get().log(Status.FAIL,
                "Test Failed. </br>" +
                        "Last Screen : ", BaseController.mediaThreadLocal.get());

        ReportService.createExcelPDFReport();
        ReportService.appendStatusToReport("FAILED",result.getThrowable().getMessage());

        log.error("Data : " + DynamicTest.trdTestName.get() + " [FAILED]");
        log.error("Detail : ", result.getThrowable());

        logNetwork.error("Data : " + DynamicTest.trdTestName.get() + " [FAILED]");
        logNetwork.error("Detail : ", result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        BDSService.closeBDSIsExist();

        if (DriverFactory.init().get() != null) {
            if (!DriverFactory.init().get().toString().contains("Chrome")) {
                ScreenshotService.init().screenShotFullWhole();
            } else {
                ScreenshotService.init().screenshot();
            }
            DriverFactory.init().tearDown();
        }

        ExtentReportsFactory.init().get().log(Status.SKIP, "Test Skipped");
        log.warn("STATUS Data : " + DynamicTest.trdTestName.get() + " [SKIPPED]");

        ReportService.createExcelPDFReport();
        ReportService.appendStatusToReport("SKIPPED", result.getThrowable().getMessage());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
        if (extentReports == null) {
            extentReports = ExtentReportsFactory.init().setupExtentReports();
        }
        BDSService.closeBDSIsExist();
    }

    @Override
    public void onFinish(ITestContext context) {
        BDSService.closeBDSIsExist();
        OracleFactory.init().tearDown();
        extentReports.flush();
    }


}
