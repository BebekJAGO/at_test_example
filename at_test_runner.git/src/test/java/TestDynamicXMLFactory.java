
import com.framework.DynamicTest;
import com.framework.factory.ExcelFactory;
import com.framework.listeners.DynamicListeners;
import com.framework.services.MyConfig;
import com.framework.utilities.FilesUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TestDynamicXMLFactory {
    private static volatile TestDynamicXMLFactory instance = null;

    private TestDynamicXMLFactory() {}

    public static TestDynamicXMLFactory init() {
        if (instance == null) {
            synchronized (TestDynamicXMLFactory.class) {
                if (instance == null)
                    instance = new TestDynamicXMLFactory();
            }
        }
        return instance;
    }

    /**
     * Create dynamic XML for testNG
     * @param strSuiteName for Name of test
     */
    public void createTest(String strSuiteName) {
        TestNG testNG = new TestNG();
        XmlSuite mySuite = createSuite(strSuiteName);

        createTestGroup(mySuite);

        //Creating XML Suite in the form of ArrayList and adding the list of Suites defined
        List<XmlSuite> mySuitesList = new ArrayList<>();
        mySuitesList.add(mySuite);

        //Create XML
        createXML(mySuite);

        //Adding the XMLSuites selected to the TestNG defined
        testNG.setXmlSuites(mySuitesList);

        testNG.run();
    }

    /**
     * Create Test Group for Parallel run
     * @param mySuite XML Suite
     */
    public void createTestGroup(XmlSuite mySuite) {
        File[] fleDatatables = FilesUtil.init().getListOfDatatable();
        List<String> lstTestGroup = new ArrayList<>();

        for (File fleDatatable: fleDatatables) {
            Sheet shtMain = ExcelFactory.init().getSheetExcel(fleDatatable.getPath(), MyConfig.strMainSheet);
            Sheet shtInfo = ExcelFactory.init().getSheetExcel(fleDatatable.getPath(), MyConfig.strDataInfo);

            int intColumnStart = ExcelFactory.init().getColumnByName(shtInfo, "Start Data");
            int intColumnEnd = ExcelFactory.init().getColumnByName(shtInfo, "End Data");
            int intColumnTestGroup = ExcelFactory.init().getColumnByName(shtMain, "TEST_GROUP");
            int intColumnStartData = Integer.parseInt(ExcelFactory.init().getCellValue(shtInfo.getWorkbook(), shtInfo.getRow(1).getCell(intColumnStart)));
            int intColumnEndData = Integer.parseInt(ExcelFactory.init().getCellValue(shtInfo.getWorkbook(), shtInfo.getRow(1).getCell(intColumnEnd)));

            for (int i = intColumnStartData; i <= intColumnEndData; i++) {
                String strTestGroup = ExcelFactory.init().getCellValue(shtMain.getWorkbook(), shtMain.getRow(i).getCell(intColumnTestGroup));
                lstTestGroup.add(strTestGroup);
            }
        }

        lstTestGroup = lstTestGroup.stream()
                .distinct()
                .collect(Collectors.toList());

        lstTestGroup.forEach(testGroup -> {
            XmlTest myTest = createTest(mySuite, testGroup);
            XmlClass myClass = createClass(DynamicTest.class, "DynamicTesting");
            myTest.getClasses().add(myClass);

        });
    }

    /**
     * Create Dynamic TestNG into .xml file
     * @param mySuite XML Suite
     */
    public void createXML(XmlSuite mySuite) {
        File file = new File("DynamicXMLTestNG.xml");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(mySuite.toXml());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Create configuration Suite Test
     * @param strXMLSuiteName String name for Suite Name
     * @return XmlSuite for init
     */
    public XmlSuite createSuite(String strXMLSuiteName) {
        //Creating XML Suite
        XmlSuite mySuite = new XmlSuite();

        //Setting the name for XML Suite
        mySuite.setName(strXMLSuiteName);

        //Setting the Preserve Order for XML Test to True to execute the Test in Order
        mySuite.setPreserveOrder(TestNGConfig.boolPreserveOrder);

        //Setting the Verbose Count for Console Logs
        mySuite.setVerbose(TestNGConfig.intVerbose);
        //Setting the XML Suite Parallel execution mode as Methods
        switch (TestNGConfig.strParallel) {
            case "methods":
                mySuite.setParallel(XmlSuite.ParallelMode.METHODS);
                break;
            case "classes":
                mySuite.setParallel(XmlSuite.ParallelMode.CLASSES);
                break;
            case "instance":
                mySuite.setParallel(XmlSuite.ParallelMode.INSTANCES);
                break;
            case "tests":
                mySuite.setParallel(XmlSuite.ParallelMode.TESTS);
                break;
            default:
                mySuite.setParallel(XmlSuite.ParallelMode.NONE);
        }

        int intDataProvider = (mySuite.getParallel() == XmlSuite.ParallelMode.NONE ? 1 : TestNGConfig.intThreadThreadDataProvidersCount);
        //Setting the execution Thread Count for Parallel Execution
        mySuite.setThreadCount(TestNGConfig.intThreadCount);
        mySuite.setDataProviderThreadCount(intDataProvider);

        //Adding the Listener class to the XML Suite
        mySuite.addListener(DynamicListeners.class.getCanonicalName());

        return mySuite;


    }

    /**
     * Create Dynamic Test
     * @param mySuite XML Suite
     * @param strTestGroup Which group test
     * @return XML Test
     */
    public XmlTest createTest(XmlSuite mySuite, String strTestGroup) {
        //Creating XML Test and add the Test to the Suite
        XmlTest myTest = new XmlTest(mySuite);

        //Setting the name for XML Test
        myTest.setName("Group Test " + strTestGroup);

        //Creating HashMap for setting the Parameters for the XML Test
        HashMap<String, String> testngParams = new HashMap<String, String>();
        testngParams.put("testGroup", strTestGroup);
        myTest.setParameters(testngParams);

        //Getting the Classes and adding it to the XML Test defined
        return myTest;
    }

    /**
     * Create class just DynamicTest.java and choose the method DynamicTesting
     * @param cls cls Dynamic Testing (Just Only One)
     * @param strTestMethods methods Dynamic Testing (Just Only One)
     * @return XML Class
     */
    public XmlClass createClass(Class cls, String strTestMethods) {
        //Creating XML Class
        XmlClass myClass = new XmlClass(cls);

        //Creating XML Include in the form of ArrayList to add Multiple Methods which i need to run from the Class
        List<XmlInclude> myMethods = new ArrayList<>();
        myMethods.add(new XmlInclude(strTestMethods));

        //Adding the Methods selected to the my XML Class defined
        myClass.setIncludedMethods(myMethods);
        return myClass;
    }

}
