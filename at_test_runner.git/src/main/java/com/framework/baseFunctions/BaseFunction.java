package com.framework.baseFunctions;

import com.framework.DynamicTest;
import com.framework.baseFunctions.baseInterface.BaseFunctionsInterface;
import com.framework.factory.*;
import com.framework.services.BDSService;
import com.framework.services.MyConfig;
import com.framework.services.PropertiesService;
import com.framework.services.ScreenshotService;
import com.framework.utilities.FilesUtil;
import com.framework.utilities.MobileKey;
import com.framework.utilities.RobotKey;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.*;

/**
 * Base Function class implement AssertInterface
 */
public class BaseFunction extends BaseController implements BaseFunctionsInterface {
    public static ThreadLocal<Integer> trdIntTempCounterRow = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdIntTempNoData = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdIntTotalLooping = new ThreadLocal<>();
    public static ThreadLocal<Integer> trdIntCounterLooping = new ThreadLocal<>();
    public static ThreadLocal<String> trdStringAction = new ThreadLocal<>();

    public BaseFunction() {
    }

    public BaseFunction(ThreadLocal<String> thrApplication, ThreadLocal<String> thrKeyword, ThreadLocal<String> thrObjectName, ThreadLocal<String> thrValue, ThreadLocal<WebElement> thrWebElement, ThreadLocal<String> thrDescription) {
        super(thrApplication,thrKeyword, thrObjectName, thrValue, thrWebElement, thrDescription);
    }
    public static List<String> lstWindows = new ArrayList<String>();

    /**
     * Start chrome driver
     */
    @Override
    public void start_chrome_driver() {
        DriverFactory.init().initWebDriver(thrValue.get());
    }

    /**
     * Start mobile driver
     */
    @Override
    public void start_mobile_driver() {
        Properties propertiesMobile = PropertiesService.readProperties("configuration/mobileDriverConfig.properties");
        Properties propertiesDeviceFarm = PropertiesService.readProperties("configuration/deviceFarm.properties");

        if (Boolean.parseBoolean(propertiesMobile.getProperty("useDeviceFarm"))) {
            DriverFactory.init().initMobileDriverKobiton(propertiesDeviceFarm.getProperty("DeviceName"));
        } else {
            DriverFactory.init().initMobileDriver();
        }
    }

    /**
     * Switch current Driver to newest initialized Driver
     * or Web driver <-> Mobile driver
     */
    @Override
    public void switch_driver() {
        DriverFactory.init().switchDriver();
    }

    /**
     * Switch Driver if start_browser more than one by Value
     */
    @Override
    public void switch_driver_to() {
        DriverFactory.init().switchDriverTo(thrValue.get());
    }

    /**
     * Start mobile driver using Kobiton Server
     */
    @Override
    public void start_kobiton_driver() {
        if (thrValue.get().equalsIgnoreCase("")) {
            Properties propertiesDeviceFarm = PropertiesService.readProperties("configuration/deviceFarm.properties");
            DriverFactory.init().initMobileDriverKobiton(propertiesDeviceFarm.getProperty("DeviceName"));
        } else {
            DriverFactory.init().initMobileDriverKobiton(thrValue.get());
        }
    }

    /**
     * Screenshot once
     */
    @Override
    public void screenshot() {
        ScreenshotService.init().screenshot();
    }

    /**
     * Web browser  : screenshot and scroll specific object class
     * Mobile       : screenshot and scroll until find specific element
     */
    @Override
    public void screenshot_by_object() {
        ScreenshotService.init().screenshotByObject(thrObjectName.get());
    }

    /**
     * Screenshot screen into each part
     */
    @Override
    public void screenshot_full_part() {
        ScreenshotService.init().screenshotFullPart();}

    /**
     * Screenshot entire page on web browser
     */
    @Override
    public void screenshot_full_whole() {
        ScreenshotService.init().screenShotFullWhole();
    }

    /**
     * Init Dev tools just for CHROME, for analytics network (API)
     */
    @Override
    public void activated_dev_tools(){
        DevToolsFactory devToolsFactory = new DevToolsFactory();
        devToolsFactory.initDevTools();
        devToolsFactory.setExtentTest(ExtentReportsFactory.init().get());
        devToolsFactory.setStrTestName(DynamicTest.trdTestName.get());
        TestFactory.thrDevTools.set(devToolsFactory);
    }

    /**
     * Click the WebElement
     */
    @Override
    public void click() {
        loadElement();
        thrWebElement.get().click();
    }

    /**
     * Double-click the WebElement
     */
    @Override
    public void double_click(){
        loadElement();

        Actions actions = new Actions(DriverFactory.init().get());
        actions.doubleClick(thrWebElement.get()).perform();
    }

    /**
     * Right Click on the WebElement
     */
    @Override
    public void right_clicK() {
        loadElement();
        Actions actions = new Actions(DriverFactory.init().get());
        actions.contextClick(thrWebElement.get()).perform();
    }

    /**
     * tap object (web element) by their coordinate
     * temporarily has been tried for Android
     */
    public void tap_by_coordinate() {
        String strMobileDriver = DriverFactory.init().get().toString();
        loadElement();

        int intElementWidth = thrWebElement.get().getSize().getWidth();
        int intElementHeight = thrWebElement.get().getSize().getHeight();

        int intX = thrWebElement.get().getLocation().getX();
        int intY = thrWebElement.get().getLocation().getY();

        int intMiddleXElement = intX + (intElementWidth / 2);
        int intMiddleYElement = intY + (intElementHeight / 2);

        if (strMobileDriver.contains("Android")) {
            TouchAction touchActTap = new TouchAction((AndroidDriver) DriverFactory.init().get());
            touchActTap.tap(PointOption.point(intMiddleXElement, intMiddleYElement)).perform();
        } else if (strMobileDriver.contains("IOS")) {
            TouchAction touchActTap = new TouchAction((IOSDriver) DriverFactory.init().get());
            touchActTap.tap(PointOption.point(intMiddleXElement, intMiddleYElement)).perform();
        }
    }

    public void tap_element_corner() {
        loadElement();

        String strMobileDriver = DriverFactory.init().get().toString();

        int intElementWidth = thrWebElement.get().getSize().getWidth();
        int intElementHeight = thrWebElement.get().getSize().getHeight();

        int intX = thrWebElement.get().getLocation().getX();
        int intY = thrWebElement.get().getLocation().getY();

        int intMiddleXElement = intX + (intElementWidth / 2);
        int intMiddleYElement = intY + (intElementHeight / 2);
        int intLeftElement = intX + 5;
        int intRightElement = intX + intElementWidth - 5;
        int intTopElement = intY + 5;
        int intBottomElement = intY +intElementHeight - 5;

        TouchAction touchActTap = null;

        if (strMobileDriver.contains("Android")) {
            touchActTap = new TouchAction((AndroidDriver) DriverFactory.init().get());
        } else if (strMobileDriver.contains("IOS")) {
            touchActTap = new TouchAction((IOSDriver) DriverFactory.init().get());
        }

        switch (thrValue.get().toString()) {
            case "left-top":
                touchActTap.tap(PointOption.point(intLeftElement, intTopElement)).perform();
                break;
            case "left-mid":
                touchActTap.tap(PointOption.point(intLeftElement, intMiddleYElement)).perform();
                break;
            case "left-bottom":
                touchActTap.tap(PointOption.point(intLeftElement, intBottomElement)).perform();
                break;
            case "top-mid":
                touchActTap.tap(PointOption.point(intMiddleXElement, intTopElement)).perform();
                break;
            case "bottom-mid":
                touchActTap.tap(PointOption.point(intMiddleXElement, intBottomElement)).perform();
                break;
            case "right-top":
                touchActTap.tap(PointOption.point(intRightElement, intTopElement)).perform();
                break;
            case "right-mid":
                touchActTap.tap(PointOption.point(intRightElement, intMiddleYElement)).perform();
                break;
            case "right-bottom":
                touchActTap.tap(PointOption.point(intRightElement, intBottomElement)).perform();
                break;
        }
    }

    /**
     * hover to specific object
     */
    @Override
    public void hover_to_element() {
        loadElement();
        Actions actions = new Actions(DriverFactory.init().get());
        actions.moveToElement(thrWebElement.get());
    }

    /**
     * hover to specific object, then click it
     */
    @Override
    public void hover_and_click() {
        loadElement();
        Actions actions = new Actions(DriverFactory.init().get());
        actions.moveToElement(thrWebElement.get());
        actions.click().build().perform();
    }

    /**
     * Replaced # on xpath element by value and Click
     */
    @Override
    public void click_replace() {
        if (!thrValue.get().equalsIgnoreCase("")) {
            WebDriverWait webDriverWait = new WebDriverWait(DriverFactory.init().get(), Duration.ofSeconds(15));
            String strReplacedElement = getCurrentXpath().replace("#", thrValue.get());

            webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(strReplacedElement)));

            WebElement webElement = DriverFactory.init().get().findElement(By.xpath(strReplacedElement));
            String strDescription = TestFactory.mapXpath.get(thrObjectName.get()).getStrDescription();

            thrWebElement.set(webElement);
            thrDescription.set(strDescription);

            thrWebElement.get().click();

            //JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.init().get();
            //jse.executeScript("arguments[0].click();", webElement);
        }
    }

    /**
     * Set Text the WebElement
     */
    @Override
    public void set_text() {
        try {
            loadElement();
            thrWebElement.get().clear();
            thrWebElement.get().sendKeys(thrValue.get());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Text from WebElement
     */
    @Override
    public void get_text() {
        try {
            loadElement();
            String strGetText = thrWebElement.get().getText().trim();
            MyConfig.mapSaveData.put(thrValue.get(), strGetText);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Value from WebElement
     */
    @Override
    public void get_value(){
        try {
            loadElement();
            String strGetValue = thrWebElement.get().getAttribute("value").trim();
            MyConfig.mapSaveData.put(thrValue.get(), strGetValue);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * Select the WebElement
     */
    @Override
    public void select() {
        try {
            loadElement();
            String strList;
            Select dropdown = new Select(thrWebElement.get());
            //Get all options
            List<WebElement> lstDropdown = dropdown.getOptions();
            //Get the length
            // Loop to print one by one
            for (int j = 0; j < lstDropdown.size(); j++) {
                strList = lstDropdown.get(j).getText();
                if (strList.trim().equals(thrValue.get().trim())) {
                    dropdown.selectByIndex(j);
                    break;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Put url on WebDriver
     */
    @Override
    public void go_to_url() {
        Boolean boolTry = true;
        do {
            try{
                DriverFactory.init().get().get(thrValue.get());
                boolTry = false;
            } catch (TimeoutException t) {}
        } while (boolTry);
    }

    /**
     * Change IFrame
     */
    @Override
    public void change_iframe() {
        loadElement();
        DriverFactory.init().get().switchTo().frame(thrWebElement.get());
    }

    /**
     * Change IFrame to default
     */
    @Override
    public void change_iframe_default() {
        DriverFactory.init().get().switchTo().defaultContent();
    }

    /**
     * Wait for Seconds
     *
     * @throws InterruptedException
     */
    @Override
    public void wait_for_seconds(){
        try {
            Thread.sleep(Integer.parseInt(thrValue.get()) * 1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Wait until Element Exist
     */
    @Override
    public void wait_until_web_element_exist() {
        WebDriverWait webDriverWait = new WebDriverWait(DriverFactory.init().get(), Duration.ofSeconds(30));
        webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(TestFactory.mapXpath.get(thrObjectName.get()).getStrXpath())));
    }

    /**
     * wait until the specific web element disappears from the page
     */
    public void wait_until_web_element_gone() {
        int intCounterGone;
        String strXpath = getCurrentXpath();
        do {
            intCounterGone = DriverFactory.init().get().findElements(By.xpath(strXpath)).size();
        } while (intCounterGone > 0);
    }

    /**
     * Save data for process in other keyword
     */
    @Override
    public void save_data() {
        MyConfig.mapSaveData.put(thrObjectName.get(), thrValue.get());
        thrDescription.set("Save data" +
                "<br/>Key = " + thrObjectName.get() +
                "<br/>Value = " + thrValue.get());
    }

    /**
     * Go To Row when found the anchorGoToValue, Row will be intCounterRow + 1
     * Example, if anchor on row 10, so the next loop intCounterRow will be 11.
     */
    @Override
    public void go_to_value() {
        int intCounterRow = ExcelFactory.getRowFromCurrentRowByValue(thrValue.get().trim(), "anchor");
        if (intCounterRow != -1)
            DynamicTest.trdIntCounterRow.set(intCounterRow);
    }

    /**
     * Scroll vertically to element
     */
    @Override
    public void scroll_to_element() {
        loadElement();
        JavascriptExecutor jse = (JavascriptExecutor) DriverFactory.init().get();
        jse.executeScript("arguments[0].scrollIntoView(true);", thrWebElement.get());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Anchor function GoToValue
     */
    @Override
    public void anchor_go_to_value() {
        System.out.println("Jump into -> " + thrValue.get());
    }

    /**
     * Go To the other sheet by Value.
     * Value is a Sheet Name which is the program will switch the Action Sheet into Value Sheet
     */
    @Override
    public void go_to_sheet() {
        trdIntTempCounterRow.set(DynamicTest.trdIntCounterRow.get());
        trdStringAction.set(DynamicTest.trdStrAction.get());
        DynamicTest.trdStrAction.set(thrValue.get());

        DynamicTest.execute(thrValue.get());

        DynamicTest.trdIntCounterRow.set(trdIntTempCounterRow.get());
//        DynamicTest.trdIntCounterRow.set(intTempCounterRow);
        DynamicTest.trdStrAction.set(trdStringAction.get());
    }

    /**
     * Looping data for specific sheet
     * Data will counter by intNoData to specific sheet
     *
     * intDataLooping containing [(start_index_datasheet), (amount of data looped)]
     *
     * trdIntTempCounterRow = save row index, for back if data sheet more than 1
     * trdIntTempNoData = save no datatable, after loop ended no datatable will returned
     * DynamicTest.trdIntNoData = set started index data sheet for looped
     */
    @Override
    public void for_data_by_sheet() {
        //intDataLooping containing [(start_index_datasheet), (amount of data looped)]
        int[] intDataLooping = ExcelFactory.getDataForDataBySheet(DynamicTest.trdIntNoData.get(), thrValue.get());
        if (intDataLooping[1] == 0) {
            if (thrValue.get().contains(";")) {
                DynamicTest.trdIntCounterRow.set(ExcelFactory.getRowFromCurrentRowByValue(thrValue.get().split(";")[1], "end_data_for_sheet" ));
            } else {
                DynamicTest.trdIntCounterRow.set(ExcelFactory.getRowFromCurrentRowByValue("", "end_data_for_sheet"));
            }
            thrDescription.set("No data found");

        } else {
            trdIntTotalLooping.set(intDataLooping[1]);
            trdIntCounterLooping.set(0);
            trdIntTempCounterRow.set(DynamicTest.trdIntCounterRow.get());
            trdIntTempNoData.set(DynamicTest.trdIntNoData.get());
            DynamicTest.trdIntNoData.set(intDataLooping[0]);
            thrDescription.set("Looping for " + intDataLooping[1] + " datas");
        }
    }

    /**
     * Anchor for End For Data By Sheet
     * 1. Set Counter Row to back to anchor for data by sheet
     * 2. Reset after looping end
     */
    @Override
    public void end_data_for_sheet() {
        trdIntCounterLooping.set(trdIntCounterLooping.get() + 1);
        thrDescription.set("End looping for data - " + (trdIntCounterLooping.get()));

        if (trdIntCounterLooping.get() < trdIntTotalLooping.get()) {
            DynamicTest.trdIntCounterRow.set(trdIntTempCounterRow.get());
            DynamicTest.trdIntNoData.set(DynamicTest.trdIntNoData.get() + 1);
        } else {
            DynamicTest.trdIntNoData.set(trdIntTempNoData.get());
            trdIntTempCounterRow.set(null);
            trdIntTotalLooping.set(null);
            trdIntTempNoData.set(null);
            trdIntCounterLooping.set(null);
        }
    }

    /**
     * press specific key on Mobile device (Android/iOS)
     */
    @Override
    public void key_press_mobile() {
        String strMobileDriver = DriverFactory.init().get().toString();

        if (strMobileDriver.contains("Android")) {
            MobileKey.key_press_android(thrValue.get());
        } else if (strMobileDriver.contains("IOS")) {
            MobileKey.key_press_ios(thrValue.get());
        }
    }

    /**
     * input text using robot
     */
    @Override
    public void input_text() {
        if (thrValue.get().equalsIgnoreCase("")) {
            RobotKey.keyPress("TAB");
        } else if (thrValue.get().contains("[")) {
            thrValue.set(MyConfig.mapSaveData.get(thrValue.get()));
            if (thrValue.get().contains(".")) {
                String strVal = thrValue.get().replace(".", "").replace(",", "");
                RobotKey.inputText(strVal);
            } else {
                RobotKey.inputText(thrValue.get());
            }
        } else {
            RobotKey.inputText(thrValue.get());
        }
    }

    /**
     * send specific key using robot
     */
    @Override
    public void send_key() {
        RobotKey.keyPress(thrValue.get());
    }

    /**
     * take Screenshot using robot Auto Hot Key (AHK)
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public void screenshot_ahk() {
        ScreenshotService.init().screenshotAHK(thrValue.get());
    }

    /**
     * take Screenshot on Desktop
     */
    public void screenshot_desktop() {ScreenshotService.init().screenshotDesktop();}


    /**
     * open path and running bdsi
     * @throws IOException
     */
    @Override
    public void activate_bds() throws IOException, InterruptedException {
        String strLines;
        String strPIDInfo = "";

        Process process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
        BufferedReader inputt = new BufferedReader(new InputStreamReader(process.getInputStream()));

        while ((strLines = inputt.readLine()) != null) {
            strPIDInfo += strLines;
        }
        inputt.close();

        if (!strPIDInfo.contains("ppdsql.exe")) {
            RobotKey.keyPressWith("WINDOWS", "r");
            Thread.sleep(100);
            RobotKey.keyPress("DELETE");
            Thread.sleep(100);
            input_text();
            RobotKey.keyPress("ENTER");

            while (!FilesUtil.init().getRunningTaskLists().contains("ppdsql.exe")) {
                Thread.sleep(7000);
            }
        } else {
            FilesUtil.init().extractFileAHK("BDSI-Activated.exe");
            Runtime.getRuntime().exec(FilesUtil.init().sourcePath() + "AutoHotKey\\BDSI-Activated.exe");
        }
    }

    /**
     * Go to Login Menu BDS 6200
     */
    @Override
    public void login_bds() {
        BDSService.loginMenuBDS();
    }

    /**
     * logut user on BDS using txn 6300
     */
    @Override
    public void logout_bds(){
        try {
            BDSService.logoutBDS();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reading how many tabs that are opened on Browser
     */
    public void get_windows_pop_up() {
        Set<String> handles = DriverFactory.init().get().getWindowHandles(); // get all window handles
        Iterator<String> iterator = handles.iterator();
        while (iterator.hasNext()) {
            lstWindows.add(iterator.next());
        }
    }

    /**
     * Move to another tab on Browser wheter it's LAST or FIRST
     */

    public void change_windows_pop_up() {
        if (thrValue.get().equalsIgnoreCase("LAST")) {
            DriverFactory.init().get().switchTo().window(lstWindows.get(lstWindows.size() - 1)); // switch to popup last window
        } else if (thrValue.get().equalsIgnoreCase("FIRST")) {
            DriverFactory.init().get().switchTo().window(lstWindows.get(0)); // switch to popup  first window
        } else {
            DriverFactory.init().get().switchTo().window(lstWindows.get(Integer.parseInt(thrValue.get()) - 1)); // switch to popup window
        }
    }

}
