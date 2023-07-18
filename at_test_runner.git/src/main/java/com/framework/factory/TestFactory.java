package com.framework.factory;

import com.framework.DAO.XpathDAO;
import com.framework.services.MyConfig;
import com.framework.services.PropertiesService;
import com.framework.utilities.FilesUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.util.*;

public class TestFactory {
    public static ThreadLocal<Class> thrDynamicClass = new ThreadLocal<>();
    public static ThreadLocal<DevToolsFactory> thrDevTools = new ThreadLocal<>();
    private Map<String, Object> mapParam;
    Properties properties = PropertiesService.readProperties("configuration/excelConfig.properties");
    public static Map<String, XpathDAO> mapXpath;

    /**
     * Create DataProvider for Dynamic Testing which total data in Datatable
     *
     * @param context for get Test Group
     * @return DataProvider for methods DynamicTesting
     */
    @DataProvider(name = "init_runner", parallel = true)
    public Iterator<Object[]> testFactory(ITestContext context) {
        Sheet shtMain = null;
        Sheet shtInfo = null;

        File[] listDatatableFile = FilesUtil.init().getListOfDatatable();
        List<Object[]> lstObject = new ArrayList<>();

        mapXpath = ExcelFactory.init().getXpath(listDatatableFile);

        for (File fleDatatable : listDatatableFile) {
            MyConfig.strPathDatatable = fleDatatable.getPath();

            shtMain = ExcelFactory.init().getSheetExcel(fleDatatable.getPath(), MyConfig.strMainSheet);
            shtInfo = ExcelFactory.init().getSheetExcel(fleDatatable.getPath(), MyConfig.strDataInfo);

            String strTestGroupParam = "";

            int intColumnStart = ExcelFactory.init().getColumnByName(shtInfo, "Start Data");
            int intColumnEnd = ExcelFactory.init().getColumnByName(shtInfo, "End Data");
            int intColumnStartData = Integer.parseInt(ExcelFactory.init().getCellValue(shtInfo.getWorkbook(), shtInfo.getRow(1).getCell(intColumnStart)));
            int intColumnEndData = Integer.parseInt(ExcelFactory.init().getCellValue(shtInfo.getWorkbook(), shtInfo.getRow(1).getCell(intColumnEnd)));

            for (int i = intColumnStartData; i <= intColumnEndData; i++) {
                try {
                    strTestGroupParam = context.getCurrentXmlTest().getParameter("testGroup");
                    int intColumnTestGroup = ExcelFactory.init().getColumnByName(shtMain, properties.getProperty("strTestGroup"));
                    String strTestGroup = ExcelFactory.init().getCellValue(shtMain.getWorkbook(), shtMain.getRow(i).getCell(intColumnTestGroup));

                    if (strTestGroupParam.equalsIgnoreCase(strTestGroup)) {
                        int intColumnAction = ExcelFactory.init().getColumnByName(shtMain, properties.getProperty("strColumnMainSheet"));
                        int intColumnScenario = ExcelFactory.init().getColumnByName(shtMain, properties.getProperty("strScenario"));

                        String strAction = ExcelFactory.init().getCellValue(shtMain.getWorkbook(), shtMain.getRow(i).getCell(intColumnAction));
                        String tempStrScrenario = ExcelFactory.init().getCellValue(shtMain.getWorkbook(), shtMain.getRow(i).getCell(intColumnScenario));
                        String strScenario = !tempStrScrenario.equalsIgnoreCase("") ? tempStrScrenario : FilesUtil.init().getDatatableFileName();

                        mapParam = new HashMap<>();
                        mapParam.put("intNo", i);
                        mapParam.put("strAction", strAction);
                        mapParam.put("strScenario", strScenario);
                        mapParam.put("strTestGroup", strTestGroup);
                        mapParam.put("strDatatablePath", fleDatatable.getPath());

                        lstObject.add(new Object[]{mapParam});
                    }

                } catch (NullPointerException e) {
                    System.out.println("Null pointer");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Something wrong.");
                    e.printStackTrace();
                }

            }
        }
        return lstObject.stream().iterator();
    }
}
