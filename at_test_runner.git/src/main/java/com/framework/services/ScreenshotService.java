package com.framework.services;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.framework.DynamicTest;
import com.framework.baseFunctions.BaseController;
import com.framework.factory.DriverFactory;
import com.framework.factory.ExtentReportsFactory;
import com.framework.factory.TestFactory;
import com.framework.utilities.FilesUtil;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

public class ScreenshotService {
    private static volatile ScreenshotService instance = null;
    private ThreadLocal<Integer> trdIntCounterData = new ThreadLocal<>();
    private ThreadLocal<String> trdStrFullPath = new ThreadLocal<String>();
    public static ThreadLocal<String> trdScreenshotPath = ThreadLocal.withInitial(() -> {
            return MyConfig.strPathReport + FilesUtil.init().getDatatableFileName() + "\\Screenshot\\";});

    private ScreenshotService() {
        trdScreenshotPath.set(MyConfig.strPathReport + FilesUtil.init().getDatatableFileName() + "\\Screenshot\\");
        initFolderDefault(trdScreenshotPath.get());
    }

    public static ScreenshotService init() {
        if (instance == null) {
            synchronized (ScreenshotService.class) {
                if (instance == null) {
                    instance = new ScreenshotService();
                }
            }
        }
        return instance;
    }

    public synchronized Integer getCounterData() {
        if (trdIntCounterData.get() == null)
            setCounterData(1);

        return trdIntCounterData.get();
    }

    public synchronized void setCounterData(Integer intCounterData) {
        trdIntCounterData.set(intCounterData);
    }

    /**
     * Screenshot current page using method getScreenshotAs from WebDriver
     * Can be used for 'Mobile' and 'Web'
     *
     * @return path of Screenshot
     */
    public synchronized String screenshot() {
        getScreenshotAs();
        putIntoReportByThread();
        DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);
        return trdScreenshotPath.get();
    }



    public synchronized String screenshotDesktop(){
       String strImageFileName = trdScreenshotPath.get() + createFileName();
        try {
            Robot rbtRobotCapture = new Robot();
            java.awt.Rectangle recCapture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage buiImage = rbtRobotCapture.createScreenCapture(recCapture);
            try {
                ImageIO.write(buiImage, "PNG", new File(strImageFileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        return trdScreenshotPath.get();
    }



    /**
     * Mobile   : screenshot until find the specific object
     * Web      : screenshot until end of specific object size by object class
     *
     * @param strObjName given object name
     * @return path of Screenshot
     */
    public synchronized String screenshotByObject(String strObjName) {
        String strDescriptionReport = "Keyword: screenshot_by_object<br/>Object Name : " + strObjName + "<br/>Value :<br/>Description : Screenshot";
        int intSSNumb = 1;
        getScreenshotAs();
        putIntoReportByDescription(strDescriptionReport);
        DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);

        //for Android and iOS
        if (!DriverFactory.init().get().toString().contains("Chrome")) {
            Boolean boolElementNotExist = true;

            while (boolElementNotExist) {
                try {
                    String strXpath = TestFactory.mapXpath.get(strObjName).getStrXpath();

                    //WebDriverWait webDriverWait = new WebDriverWait(DriverFactory.init().get(), Duration.ofSeconds(3));
                    //webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(strXpath)));

                    DriverFactory.init().get().findElement(By.xpath(strXpath));

                    boolElementNotExist = false;
                } catch (Exception e) {
                    swipeUpMobile();
                    getScreenshotAs();
                    putIntoReportByDescription(strDescriptionReport + intSSNumb);
                    intSSNumb++;
                    DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);
                }
            }
        }
        //for web browser
        else {
            swipeUpWeb_SS(strObjName, strDescriptionReport);
        }

        return trdScreenshotPath.get();
    }

    /**
     * Screenshot whole page for Web browser only
     *
     * @return path of Screenshot
     */
    public synchronized String screenShotFullWhole() {
        try {
            String strFileName = createFileName();
            trdStrFullPath.set(trdScreenshotPath.get() + strFileName);
            Shutterbug.shootPage(DriverFactory.init().get(), ScrollStrategy.WHOLE_PAGE, 500).withName(strFileName.replace(".png","")).save(trdScreenshotPath.get());
            putIntoReportByThread();
            DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trdScreenshotPath.get();
    }

    /**
     * Screenshot per section by window/screen size
     * for iPhone still on develop
     *
     * @return path of Screenshot
     */
    public synchronized String screenshotFullPart() {
        String strDescriptionReport = "Keyword: screenshot_full_part<br/>Object Name :<br/>Value :<br/>Description : Screenshot";
        String instanceWebDriver = DriverFactory.init().get().toString();

        getScreenshotAs();
        putIntoReportByDescription(strDescriptionReport);
        DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);

        //for Mobile driver
        if (!instanceWebDriver.contains("Chrome")) {

            String strXpath = "";

            if (instanceWebDriver.contains("Android")) { //for Android driver
                strXpath = "//android.widget.ScrollView[@content-desc=\"page content\"]";
            } else if (instanceWebDriver.contains("IOS")) { //for IOS driver
                strXpath = "//XCUIElementTypeScrollView[@index=0]";
            }
            try {
                Boolean assertLayer = false;

                WebDriverWait webDriverWait = new WebDriverWait(DriverFactory.init().get(), Duration.ofSeconds(10));
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(strXpath)));

                int intSSNumb = 1;

                while (!assertLayer) {
                    List<WebElement> currentChilds = DriverFactory.init().get().findElements(By.xpath(strXpath + "//*"));
                    swipeUpMobile();
                    List<WebElement> afterSwipeChilds = DriverFactory.init().get().findElements(By.xpath(strXpath + "//*"));
                    if (currentChilds.size() != afterSwipeChilds.size()) {
                        getScreenshotAs();
                        putIntoReportByDescription(strDescriptionReport + intSSNumb);
                        DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);
                    } else {
                        int intCountNotSame = 0;
                        for (int i = 1; i <= currentChilds.size(); i++) {
                            if (!currentChilds.get(i).toString().split("xpath: ")[1].equalsIgnoreCase(afterSwipeChilds.get(i).toString().split("xpath: ")[1])) {
                                intCountNotSame++;
                            } else {
                                break;
                            }
                            if (intCountNotSame != 0) {
                                break;
                            }
                        }

                        getScreenshotAs();
                        putIntoReportByDescription(strDescriptionReport + intSSNumb);
                        DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);

                        if (intCountNotSame == 0) {
                            assertLayer = true;
                        }
                    }
                    intSSNumb++;
                }

                if (intSSNumb >= 1) {
                    for (int i = 1; i < intSSNumb; i++) {
                        swipeDownMobile();
                    }
                }

            } catch (Exception e) {
            }
        } else {
            //for web browser
            swipeUpWeb_SS("", strDescriptionReport);
        }
        return trdScreenshotPath.get();
    }

    public synchronized String screenshotAHK(String strValue) {
        try {
            String strImagePath = FilesUtil.init().captureAutoHotKey(strValue);
            trdStrFullPath.set(strImagePath);
            putIntoReportByThread();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return trdScreenshotPath.get();
    }

    /**
     * Change image into Base64
     *
     * @param strPathScreenShot Full path file screenshot
     * @return encodedString
     */
    private String changeImageToBase64(String strPathScreenShot) {
        String encodedString = "";
        try {
            Thread.sleep(1000);
            byte[] fileContent = FileUtils.readFileToByteArray(new File(strPathScreenShot));
            encodedString = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return encodedString;
    }

    /**
     * Create folder for put the result screenshot
     *
     * @param strPathFolderResultTesting Path Folder file Screenshot
     */
    private void initFolderDefault(String strPathFolderResultTesting) {
        File file = new File(strPathFolderResultTesting);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * Calling screenshot function using method getScreenshotAs of WebDriver
     */
    public void getScreenshotAs() {
        String strFileName = createFileName();
        trdStrFullPath.set(trdScreenshotPath.get() + strFileName);
        try {
            Thread.sleep(500);
            File scrFile = ((TakesScreenshot) DriverFactory.init().get()).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(trdStrFullPath.get()));
            //compressImage(trdStrFullPath.get());

        } catch (IOException e) {
            ExtentReportsFactory.init().get().log(Status.FAIL, "Path Not Found." + trdStrFullPath.get());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * swipe up current page on mobile
     */
    public void swipeUpMobile() {
        Dimension mobileDimension = DriverFactory.init().get().manage().window().getSize();
        int intDimensionHeight = mobileDimension.height;
        int intDimensionWidth = mobileDimension.width;
        TouchAction actionSwipeUP = null;

        if (DriverFactory.init().get().toString().contains("Android")) {
            actionSwipeUP = new TouchAction((AndroidDriver) DriverFactory.init().get());
        } else if (DriverFactory.init().get().toString().contains("IOS")) {
            actionSwipeUP = new TouchAction((IOSDriver) DriverFactory.init().get());
        }

        actionSwipeUP.press(PointOption.point(intDimensionWidth / 2, intDimensionHeight * 3 / 5)) // (x,y) --> (Middle point of window, 3/4 of window height)
                .waitAction(new WaitOptions().withDuration(Duration.ofMillis(500)))
                .moveTo(PointOption.point(intDimensionWidth / 2, intDimensionHeight / 4)) // (x,y) --> (Middle point of window, 1/4 of window height)
                .release()
                .perform();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * swipe up current page on mobile
     */
    public void swipeDownMobile() {
        Dimension mobileDimension = DriverFactory.init().get().manage().window().getSize();
        int intDimensionHeight = mobileDimension.height;
        int intDimensionWidth = mobileDimension.width;
        TouchAction actionSwipeUP = null;

        if (DriverFactory.init().get().toString().contains("Android")) {
            actionSwipeUP = new TouchAction((AndroidDriver) DriverFactory.init().get());
        } else if (DriverFactory.init().get().toString().contains("IOS")) {
            actionSwipeUP = new TouchAction((IOSDriver) DriverFactory.init().get());
        }

        actionSwipeUP.press(PointOption.point(intDimensionWidth / 2, intDimensionHeight / 4)) // (x,y) --> (Middle point of window, 3/4 of window height)
                .waitAction(new WaitOptions().withDuration(Duration.ofMillis(2000))) //you can change wait durations as per your requirement
                .moveTo(PointOption.point(intDimensionWidth / 2, intDimensionHeight * 3 / 4)) // (x,y) --> (Middle point of window, 1/4 of window height)
                .release()
                .perform();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void swipeUpWeb_SS(String strObjectName, String strDescriptionReport) {
        DriverFactory.init().get().switchTo().defaultContent();
        JavascriptExecutor JsEx = (JavascriptExecutor) DriverFactory.init().get();

        JsEx.executeScript("document.scrollingElement.scrollTo(0,-document.scrollingElement.scrollHeight);");

        String strScriptClassToExecute = "";
        String strObjectClassName = "";

        if (strObjectName.equalsIgnoreCase("")) {
            strScriptClassToExecute = "return document.body.scrollHeight";
        } else {
            String replaceQuotation = (TestFactory.mapXpath.get(strObjectName).getStrXpath()).replace("\'", "\"");
            String[] splitXpathByQuotes = replaceQuotation.split("\"");
            strObjectClassName = splitXpathByQuotes[1];

            strScriptClassToExecute = "return document.getElementsByClassName('" + strObjectClassName + "')[0].scrollHeight";
        }

        long longWindowSize = ((long) JsEx.executeScript("return window.innerHeight"));
        long longClassHeightSize = ((long) JsEx.executeScript(strScriptClassToExecute));

        for (int i = 1; i <= longClassHeightSize / longWindowSize; i++) {
            if (longClassHeightSize < longWindowSize) {
                break;
            }

            if (strObjectClassName.equalsIgnoreCase("")) {
                JsEx.executeScript("window.scrollBy(0,window.innerHeight-50)");
            } else {
                JsEx.executeScript("document.getElementsByClassName('" + strObjectClassName + "')[0].scrollBy(0,window.innerHeight-10)");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            getScreenshotAs();
            putIntoReportByDescription(strDescriptionReport + i);
            DynamicTest.trdIntSSCounter.set(DynamicTest.trdIntSSCounter.get() + 1);
        }
        JsEx.executeScript("document.scrollingElement.scrollTo(0,-document.scrollingElement.scrollHeight);");
    }

    /**
     * Put image into Report using Media Thread and delete it
     */
    public void putIntoReportByThread() {
        //BaseController.mediaThreadLocal.set(MediaEntityBuilder.createScreenCaptureFromPath(trdStrFullPath.get()).build());

        //displaying image as a base64
        String encodedString = changeImageToBase64(trdStrFullPath.get());
        BaseController.mediaThreadLocal.set(MediaEntityBuilder.createScreenCaptureFromBase64String(encodedString).build());
    }

    /**
     * Put image into Report for multiple times using description value and delete it
     *
     * @param strDescriptionReport description value that will be put in a Report
     */
    public void putIntoReportByDescription(String strDescriptionReport) {
        //ExtentReportsFactory.init().get().log(Status.INFO, strDescriptionReport, MediaEntityBuilder.createScreenCaptureFromPath(trdStrFullPath.get()).build());

        //displaying image as a base64
        String encodedString = changeImageToBase64(trdStrFullPath.get());
        BaseController.mediaThreadLocal.set(MediaEntityBuilder.createScreenCaptureFromBase64String(encodedString).build());

        BaseController.thrInitExtentReports.set(false);
    }

    /**
     * Create default filename for screenshot Working Papers
     */
    public String createFileName() {
        String strFileName =
                DynamicTest.trdTempDataCounter.get() + "-"
                        + DynamicTest.trdScenario.get() + "_"
                        + ("000" + DynamicTest.trdTestcaseCount.get()).substring(("000" + DynamicTest.trdTestcaseCount.get()).length() - 3) + "_"
                        + ("000" + DynamicTest.trdIntSSCounter.get()).substring(("000" + DynamicTest.trdIntSSCounter.get()).length() - 3)
                        + ".png";

        return strFileName;
    }

    /**
     * compressing image file size, adjust the "setCompressionQuality"
     * @param strImgSource
     */
    public void compressImage(String strImgSource) {
        BufferedImage image;
        IIOMetadata metadata;

        try {
            ImageInputStream in = ImageIO.createImageInputStream(Files.newInputStream(Paths.get(strImgSource)));
            ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
            reader.setInput(in, true, false);
            image = reader.read(0);
            metadata = reader.getImageMetadata(0);
            reader.dispose();

            ImageOutputStream out = ImageIO.createImageOutputStream(Files.newOutputStream(Paths.get(trdScreenshotPath.get() + "test.png")));
            ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
            ImageWriter writer = ImageIO.getImageWriters(type, "png").next();

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.0f);
            }

            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, metadata), param);
            writer.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetInstance() {
        instance = null;
    }
}
