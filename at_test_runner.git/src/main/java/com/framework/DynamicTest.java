package com.framework;

import com.aventstack.extentreports.Status;
import com.framework.baseFunctions.BaseController;
import com.framework.factory.ExcelFactory;
import com.framework.factory.ExtentReportsFactory;
import com.framework.factory.TestFactory;
import com.framework.services.MyConfig;
import com.framework.services.PropertiesService;
import com.framework.utilities.FilesUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DynamicTest implements ITest {
    public static ThreadLocal<Integer> trdIntNoData = new ThreadLocal<>();
    public static ThreadLocal<String> trdStrAction = new ThreadLocal<>();
    public static ThreadLocal<String> trdScenario = new ThreadLocal<>();
    public static ThreadLocal<String> trdDatatableName = new ThreadLocal<>();
    public static ThreadLocal<String> trdTestGroup = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdIntCounterRow = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdIntCurrentRow = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdIntSSCounter = new ThreadLocal<>();
    public static ThreadLocal<String> trdTestName = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdTestcaseCount = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdTempDataCounter = new ThreadLocal<>();
    public static ThreadLocal<Map<Integer, String>> trdMapTestCaseList = new ThreadLocal<>();
    public static boolean isRunning = false;

    public DynamicTest() {
    }


    @BeforeMethod()
    public void setUp(Method method, ITestContext ctx, Object[] objects) {
        Map<String, Object> mapParam = (HashMap) objects[0];

        MyConfig.strPathDatatable = (String) mapParam.get("strDatatablePath");
        int intNo = (int) mapParam.get("intNo");
        String strAction = (String) mapParam.get("strAction");
        String strDatatableName = FilesUtil.init().getDatatableFileName();
        String strScenario = (String) mapParam.get("strScenario");
        String strTestGroup = (String) mapParam.get("strTestGroup");
        String strTestName = intNo + "-" + strAction;

        trdDatatableName.set(strDatatableName);
        trdTestName.set(strTestName);
        trdIntNoData.set(intNo);
        trdTempDataCounter.set(intNo);
        trdTestcaseCount.set(0);
        trdStrAction.set(strAction);
        trdTestGroup.set(strTestGroup);
        trdScenario.set(strScenario);
        trdIntSSCounter.set(1);
        trdMapTestCaseList.set(new HashMap<>());

        TestFactory.thrDynamicClass.set(this.getClass());

    }


    @Test(dataProvider = "init_runner",
            dataProviderClass = TestFactory.class)
    public void DynamicTesting(Map<String, Object> mapParam) {
        execute((String) mapParam.get("strAction"));
    }


    /**
     * Execute the engine Action Sheet.
     *
     * @param strAction which Action
     */
    public static void execute(String strAction) {
        Sheet shtAction = ExcelFactory.init().getSheetExcel(MyConfig.strPathDatatable, strAction);

        if(shtAction==null){
            ExtentReportsFactory.init().get().log(Status.FAIL,"Sheet action "+ strAction +" doesn't exist!");
            throw new NullPointerException("Sheet action "+ strAction +" doesn't exist!");
        }

        int intColumnTestCase = ExcelFactory.init().getColumnByName(shtAction, "Testcase");
        int intColumnIsRunning = ExcelFactory.init().getColumnByName(shtAction, "Is_Running");
        int intColumnKeyword = ExcelFactory.init().getColumnByName(shtAction, "Keyword");
        int intColumnObjectName = ExcelFactory.init().getColumnByName(shtAction, "ObjectName");
        int intColumnValue = ExcelFactory.init().getColumnByName(shtAction, "Value");
        int intColumnApplication = ExcelFactory.init().getColumnByName(shtAction, "Application");
        String strTestcaseName = "";

        String strApplication = null;

        trdIntCounterRow.set(1);

        for (trdIntCurrentRow.set(1); trdIntCurrentRow.get() <= shtAction.getLastRowNum(); trdIntCurrentRow.set(trdIntCurrentRow.get() + 1)) {

            strTestcaseName = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(trdIntCurrentRow.get()).getCell(intColumnTestCase));
            if (!strTestcaseName.equals("")) {
                trdTestcaseCount.set(trdTestcaseCount.get() + 1);
                trdMapTestCaseList.get().put(trdTestcaseCount.get(), strTestcaseName);
            }

            if (trdIntCounterRow.get().equals(trdIntCurrentRow.get())) {
                String strIsRunning = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(trdIntCounterRow.get()).getCell(intColumnIsRunning));
                //before use : intColumnApplication
                String strTempApplication = PropertiesService.readProperties("configuration/runningConfig.properties").getProperty("Application");
                strApplication = (strTempApplication != "" ? strTempApplication : strApplication);

                isRunning = checkIsRunning(strIsRunning, isRunning);

                if (isRunning) {
                    if (strIsRunning.isBlank()) {
                        String strKeyword = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(trdIntCounterRow.get()).getCell(intColumnKeyword));
                        String strObjectName = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(trdIntCounterRow.get()).getCell(intColumnObjectName));
                        String strValue = ExcelFactory.init().getCellValue(shtAction.getWorkbook(), shtAction.getRow(trdIntCounterRow.get()).getCell(intColumnValue), trdIntNoData.get());

                        trdIntCounterRow.set(trdIntCurrentRow.get() + 1);
                        new BaseController(strKeyword.toLowerCase(), strObjectName, strValue, strApplication);
                    } else {
                        trdIntCounterRow.set(trdIntCurrentRow.get() + 1);
                    }
                } else {
                    trdIntCounterRow.set(trdIntCurrentRow.get() + 1);
                }
            }
        }
    }

    /**
     * Check is running or no
     *
     * @param strIsRunning isRunning Yes or No
     * @param isRunning    parameter by Ref
     * @return
     */
    public static boolean checkIsRunning(String strIsRunning, boolean isRunning) {
        if (strIsRunning.equalsIgnoreCase("YES"))
            return true;
        else if (strIsRunning.equalsIgnoreCase("NO"))
            return false;
        return isRunning;
    }

    @Override
    public String getTestName() {
        return trdTestName.get();
    }

}
