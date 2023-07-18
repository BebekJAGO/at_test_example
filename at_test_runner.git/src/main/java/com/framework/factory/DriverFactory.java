package com.framework.factory;

import com.framework.listeners.DynamicListeners;
import com.framework.services.PropertiesService;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DriverFactory {
    private static volatile DriverFactory instance = null;
    public static Properties propertiesWeb = PropertiesService.readProperties("configuration/webDriverConfig.properties");
    private Properties propertiesMobile = PropertiesService.readProperties("configuration/mobileDriverConfig.properties");
    private Properties propertiesDeviceFarm = PropertiesService.readProperties("configuration/deviceFarm.properties");
    private Logger log = Logger.getLogger("FILE");
    public static ThreadLocal<Map<String, WebDriver>> trdMapDriver = ThreadLocal.withInitial(() -> {
        return new HashMap<String, WebDriver>();
    });

    private DriverFactory() {
    }

    public static DriverFactory init() {
        if (instance == null) {
            synchronized (DriverFactory.class) {
                if (instance == null)
                    instance = new DriverFactory();
            }
        }
        return instance;
    }

    ThreadLocal<WebDriver> trdMainDriver = new ThreadLocal<>();
    ThreadLocal<WebDriver> trdWebDriver = new ThreadLocal<>();
    ThreadLocal<WebDriver> trdMobileDriver = new ThreadLocal<>();

    public WebDriver get() {
        return trdMainDriver.get();
    }

    public void set(WebDriver webDriver) {
        trdMainDriver.set(webDriver);
    }

    public void remove() {
        trdMainDriver.remove();
    }

    /**
     * Initialize web driver using configuratipn on webDriverProperties
     * Improve webDriver more than one, and put all into HashMap
     *
     * @return Return web driver
     */
    public void initWebDriver(String... strKeyWebDriver) {
        DynamicListeners.trdDriverProperties.set(propertiesWeb);

        String strBrowser = propertiesWeb.getProperty("Browser");
        String strSource = propertiesWeb.getProperty("Source");
        WebDriver webDriver = null;
        useLocalorDefault(strSource, strBrowser);

        switch (strBrowser) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
                chromeOptions.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
                chromeOptions.addArguments("--remote-allow-origins=*");

                if (propertiesWeb.getProperty("Maximize").equalsIgnoreCase("true"))
                    chromeOptions.addArguments("--start-maximized");

                if (propertiesWeb.getProperty("Headless").equalsIgnoreCase("true")) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--window-size=" + propertiesWeb.getProperty("WindowSize"));
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
                    chromeOptions.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
                }

                if (propertiesWeb.getProperty("Incognito").equalsIgnoreCase("true"))
                    chromeOptions.addArguments("--incognito");

                if (propertiesWeb.getProperty("Proxy").equalsIgnoreCase("true")) {
                    String strProxy = propertiesWeb.getProperty("ProxyHost");
                    chromeOptions.addArguments("--proxy-server= " + strProxy);
                }

                if (propertiesWeb.getProperty("bypassSSLCertification").equalsIgnoreCase("true")) {
                    chromeOptions.addArguments("--ignore-ssl-errors=yes");
                    chromeOptions.addArguments("--ignore-certificate-errors");
                }

                int intCounter = 0;
                while (webDriver == null && intCounter < 10) {
                    try {
                        webDriver = new ChromeDriver(chromeOptions);
                    } catch (Exception e) {
                        webDriver = null;
                        e.printStackTrace();
                    }
                    intCounter++;
                }

                break;
            case "firefox":
                webDriver = new FirefoxDriver();
                break;
            case "ie":
                webDriver = new InternetExplorerDriver();
                break;
        }

        Long ObjectWaitTime = Long.parseLong(propertiesWeb.getProperty("WaitTime"));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ObjectWaitTime));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ObjectWaitTime * 2));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(ObjectWaitTime * 2));

        trdWebDriver.set(webDriver);
        trdMapDriver.get().put((!strKeyWebDriver[0].equalsIgnoreCase("") ? strKeyWebDriver[0] : "WebDriverDefault"), webDriver);

        set(webDriver);


    }

    /**
     * Initialize mobile driver using configuration on mobileDriverProperties
     *
     * @return Return mobile driver
     */
    public void initMobileDriver() {
        DynamicListeners.trdDriverProperties.set(propertiesMobile);

        String strDevice = propertiesMobile.getProperty("Device");
        String strApp = propertiesMobile.getProperty("app");
        String strAppPakage = propertiesMobile.getProperty("appPackage");
        String strAppActivity = propertiesMobile.getProperty("appActivity");
        String strURL = propertiesMobile.getProperty("URL");

        Long newCommandTimeout = Long.parseLong(propertiesMobile.getProperty("newCommandTimeout"));
        Boolean boolNoReset = Boolean.valueOf(propertiesMobile.getProperty("noReset"));
        Boolean boolForBrowser = Boolean.valueOf(propertiesMobile.getProperty("forBrowser"));

        WebDriver mobileDriver = null;

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("noReset", boolNoReset);
        capabilities.setCapability("newCommandTimeout", newCommandTimeout);

        if (!boolForBrowser) {
            //set Capability using app sourcepath or appPackage-appActivity
            if (strApp.equalsIgnoreCase("null")) {
                capabilities.setCapability("appPackage", strAppPakage);
                capabilities.setCapability("appActivity", strAppActivity);
            } else {
                capabilities.setCapability("app", strApp);
            }
        } else {
            //set Capability for mobile browser
            switch (strDevice) {
                case "android":
                    String strChromeDriver = propertiesMobile.getProperty("chromeDriver");
                    capabilities.setCapability("chromedriverExecutable", System.getProperty("user.dir") + "\\src\\main\\resources\\driverLocal\\" + strChromeDriver);
                    capabilities.setCapability("browserName", "Chrome");
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setExperimentalOption("w3c", false);
                    capabilities.merge(chromeOptions);
                    break;
                case "iphone":
                    capabilities.setCapability("browserName", "Safari");
                    break;
            }
        }

        switch (strDevice) {
            case "android":
                try {
                    capabilities.setCapability("platformName", "Android");
                    mobileDriver = new AndroidDriver(new URL(strURL), capabilities);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "iphone":
                capabilities.setCapability("platformName", "iOS");
                capabilities.setCapability("deviceName", propertiesMobile.getProperty("deviceName"));
                capabilities.setCapability("automationName", propertiesMobile.getProperty("automationName"));
                capabilities.setCapability("platformVersion", propertiesMobile.getProperty("platformVersion"));
                capabilities.setCapability("udid", propertiesMobile.getProperty("udid"));
                capabilities.setCapability("xcode0grId", propertiesMobile.getProperty("xcode0grId"));
                capabilities.setCapability("xcodeSigningID", propertiesMobile.getProperty("xcodeSigningID"));
                capabilities.setCapability("autoAcceptAlerts", Boolean.valueOf(propertiesMobile.getProperty("autoAcceptAlerts")));
                capabilities.setCapability("wdaLocalPort", propertiesMobile.getProperty("wdaLocalPort"));
                capabilities.setCapability("forceAppLaunch", propertiesMobile.getProperty("forceAppLaunch"));
                capabilities.setCapability("bundleId", propertiesMobile.getProperty("bundleId"));
                try {
                    mobileDriver = new IOSDriver(new URL(strURL), capabilities);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        Long ObjectWaitTime = Long.parseLong(propertiesMobile.getProperty("WaitTimeMobile"));
        mobileDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ObjectWaitTime));

        trdMobileDriver.set(mobileDriver);
        trdMapDriver.get().put("MobileDriverDefault", mobileDriver);
        set(mobileDriver);
    }

    /**
     * Initialize mobile driver with Kobiton Server
     *
     * @param strDevice device name which available on Kobiton
     * @Return mobile driver
     */
    public void initMobileDriverKobiton(String strDevice) {
        DynamicListeners.trdDriverProperties.set(propertiesDeviceFarm);

        strDevice = strDevice.toUpperCase();
        WebDriver kobitonDriver = null;

        String kobitonServerUrl = propertiesDeviceFarm.getProperty("Server");
        Boolean boolRunBrowser = Boolean.valueOf(propertiesDeviceFarm.getProperty("runBrowser"));
        Boolean boolNoReset = Boolean.valueOf(propertiesDeviceFarm.getProperty("noReset"));
        Boolean boolFullReset = Boolean.valueOf(propertiesDeviceFarm.getProperty("fullReset"));
        Boolean boolcaptureScreenshots = Boolean.valueOf(propertiesDeviceFarm.getProperty("captureScreenshots"));

        DesiredCapabilities capabilities = createKobitonCapabilites();

        Long newCommandTimeout = Long.parseLong(propertiesDeviceFarm.getProperty("newCommandTimeout"));

        capabilities.setCapability("sessionName", propertiesDeviceFarm.getProperty("sessionName"));
        capabilities.setCapability("sessionDescription", propertiesDeviceFarm.getProperty("sessionDescription"));
        capabilities.setCapability("deviceOrientation", propertiesDeviceFarm.getProperty("deviceOrientation"));
        capabilities.setCapability("noReset", boolNoReset);
        capabilities.setCapability("fullReset", boolFullReset);
        capabilities.setCapability("captureScreenshots", boolcaptureScreenshots);
        capabilities.setCapability("groupId", 1);
        capabilities.setCapability("deviceGroup", propertiesDeviceFarm.getProperty("deviceGroup"));
        capabilities.setCapability("tagName", propertiesDeviceFarm.getProperty("tagName"));
        capabilities.setCapability("newCommandTimeout", newCommandTimeout);

        try {
            switch (strDevice) {

                case "GALAXY TAB S4":
                    if (boolRunBrowser) {
                        capabilities.setCapability("useConfiguration", propertiesDeviceFarm.getProperty("useConfiguration"));
                        capabilities.setCapability("autoWebview", Boolean.parseBoolean(propertiesDeviceFarm.getProperty("autoWebview")));
                        capabilities.setCapability("browserName", "chrome");
                    } else {
                        //run mobile App
                        if (propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("null") || propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("")) {
                            capabilities.setCapability("appPackage", propertiesDeviceFarm.getProperty("appPackage"));
                            capabilities.setCapability("appActivity", propertiesDeviceFarm.getProperty("appActivity"));
                        } else {
                            capabilities.setCapability("app", propertiesDeviceFarm.getProperty("appURL"));
                        }
                    }

                    capabilities.setCapability("deviceName", "Galaxy Tab S4");
                    capabilities.setCapability("platformVersion", "9");
                    capabilities.setCapability("platformName", "Android");

                    try {
                        kobitonDriver = new AndroidDriver(new URL(kobitonServerUrl), capabilities);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }

                    break;

                case "GALAXY TAB A (2017)":
                    if (boolRunBrowser) {
                        capabilities.setCapability("useConfiguration", propertiesDeviceFarm.getProperty("useConfiguration"));
                        capabilities.setCapability("autoWebview", Boolean.parseBoolean(propertiesDeviceFarm.getProperty("autoWebview")));
                        capabilities.setCapability("browserName", "chrome");
                    } else {
                        //run mobile App
                        if (propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("null") || propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("")) {
                            capabilities.setCapability("appPackage", propertiesDeviceFarm.getProperty("appPackage"));
                            capabilities.setCapability("appActivity", propertiesDeviceFarm.getProperty("appActivity"));
                        } else {
                            capabilities.setCapability("app", propertiesDeviceFarm.getProperty("appURL"));
                        }
                    }

                    capabilities.setCapability("deviceName", "Galaxy Tab A (2017)");
                    capabilities.setCapability("platformVersion", "9");
                    capabilities.setCapability("platformName", "Android");

                    try {
                        kobitonDriver = new AndroidDriver(new URL(kobitonServerUrl), capabilities);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }

                    break;

                case "GALAXY J8":
                    if (boolRunBrowser) {
                        capabilities.setCapability("useConfiguration", propertiesDeviceFarm.getProperty("useConfiguration"));
                        capabilities.setCapability("autoWebview", Boolean.parseBoolean(propertiesDeviceFarm.getProperty("autoWebview")));
                        capabilities.setCapability("browserName", "chrome");
                    } else {
                        //run mobile App
                        if (propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("null") || propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("")) {
                            capabilities.setCapability("appPackage", propertiesDeviceFarm.getProperty("appPackage"));
                            capabilities.setCapability("appActivity", propertiesDeviceFarm.getProperty("appActivity"));
                        } else {
                            capabilities.setCapability("app", propertiesDeviceFarm.getProperty("appURL"));
                        }
                    }

                    capabilities.setCapability("deviceName", "Galaxy J8");
                    capabilities.setCapability("platformVersion", "10");
                    capabilities.setCapability("platformName", "Android");

                    try {
                        kobitonDriver = new AndroidDriver(new URL(kobitonServerUrl), capabilities);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }

                    break;

                case "IPHONE 6":
                    if (boolRunBrowser) {
                        capabilities.setCapability("useConfiguration", propertiesDeviceFarm.getProperty("useConfiguration"));
                        capabilities.setCapability("autoWebview", Boolean.parseBoolean(propertiesDeviceFarm.getProperty("autoWebview")));
                        capabilities.setCapability("browserName", "safari");
                    } else {
                        //run mobile App
                        if (propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("null") || propertiesDeviceFarm.getProperty("appURL").equalsIgnoreCase("")) {
                            capabilities.setCapability("appPackage", propertiesDeviceFarm.getProperty("appPackage"));
                            capabilities.setCapability("appActivity", propertiesDeviceFarm.getProperty("appActivity"));
                        } else {
                            capabilities.setCapability("app", propertiesDeviceFarm.getProperty("appURL"));
                        }
                    }

                    capabilities.setCapability("deviceName", "iPhone 6");
                    capabilities.setCapability("platformVersion", "12.5.5");
                    capabilities.setCapability("platformName", "iOS");

                    try {
                        kobitonDriver = new IOSDriver(new URL(kobitonServerUrl), capabilities);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                default:
                    System.out.println("Device not found on Device Farm");
            }

            Long ObjectWaitTime = 120L;
            kobitonDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ObjectWaitTime));

            trdMobileDriver.set(kobitonDriver);
            trdMapDriver.get().put("KobitonMobileDriverDefault", kobitonDriver);
            set(kobitonDriver);
        } catch (NullPointerException np) {
            np.printStackTrace();
            log.error("Device not found on Device Farm");
        }
    }

    /**
     * switch to another driver which already start before
     */
    public void switchDriver() {
        if ((trdMainDriver.get() != trdMobileDriver.get()) && trdWebDriver != null) {
            trdMainDriver = trdMobileDriver;
        } else if ((trdMainDriver.get() != trdWebDriver.get()) && trdMobileDriver != null) {
            trdMainDriver = trdWebDriver;
        }
    }

    /**
     * Switch driver to specific WebDriver
     */
    public void switchDriverTo(String strKey) {
        trdWebDriver.set(trdMapDriver.get().get(strKey));
        set(trdMapDriver.get().get(strKey));
    }

    /**
     * Close the driver
     */
    public void tearDown() {
        if (TestFactory.thrDevTools.get() != null)
            TestFactory.thrDevTools.get().tearDown();
        if (trdMapDriver.get().size() > 0) {
            if (trdMapDriver.get().size() > 1) {
                trdMapDriver.get().forEach((key, value) -> {
                    trdMainDriver.set(value);
                    trdMainDriver.get().quit();
                    remove();
                });
            } else {
                trdMainDriver.get().quit();
                remove();
            }
        }
    }

    /**
     * create DesiredCapabilities with configuration on deviceFarm properties
     *
     * @return DesiredCapabilities object
     */
    private DesiredCapabilities createKobitonCapabilites() {
        DesiredCapabilities desCapabilities = new DesiredCapabilities();

        desCapabilities.setCapability("sessionName", propertiesDeviceFarm.getProperty("sessionName"));
        desCapabilities.setCapability("sessionDescription", "");
        desCapabilities.setCapability("deviceOrientation", propertiesDeviceFarm.getProperty("deviceOrientation"));
        desCapabilities.setCapability("noReset", Boolean.parseBoolean(propertiesDeviceFarm.getProperty("noReset")));
        desCapabilities.setCapability("fullReset", Boolean.parseBoolean(propertiesDeviceFarm.getProperty("fullReset")));
        desCapabilities.setCapability("captureScreenshots", Boolean.parseBoolean(propertiesDeviceFarm.getProperty("captureScreenshots")));
        desCapabilities.setCapability("groupId", 1);
        desCapabilities.setCapability("deviceGroup", propertiesDeviceFarm.getProperty("deviceGroup"));
        desCapabilities.setCapability("tagName",propertiesDeviceFarm.getProperty("tagName"));

        return desCapabilities;
    }

    /**
     * @param strSource  Source use local driver or webdrivermanager, because bca use proxy, sometimes we can't connect chromedriver
     * @param strBrowser What browser for the setup
     */
    private void useLocalorDefault(String strSource, String strBrowser) {
        if (strSource.equalsIgnoreCase("driverLocal")) {
            switch (strBrowser) {
                case "chrome":
                    getChromeVersion();
                    String strdriverLocal = propertiesWeb.getProperty("DriverLocal");
                    System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\driverLocal\\" + strdriverLocal);
                    break;
                case "firefox":
                    break;
                case "ie":
                    break;
            }
        } else {
            switch (strBrowser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    break;
                case "ie":
                    WebDriverManager.iedriver().setup();
                    break;
            }
        }
    }

    /**
     * get registry query of chrome from registry editor
     * put the registry value into each line
     * read which line that contains version value to get chrome version
     * set the DriverLocal property of webDriverConfig.properties
     */
    private void getChromeVersion() {
        String chromeRegistry = null;
        String strChromeDriver = "";

        //Find Chrome version in Registry Editor
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec("reg query " + "HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon " + "/v version");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedReader regData = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        try {
            while ((chromeRegistry = regData.readLine()) != null) {
                if (chromeRegistry.contains("version")) {
                    //Split values from Chrome registry to get Chrome Vers. (E.g:"   version    REG_SZ    103.0.5060.114")
                    String[] strChromeVersion = chromeRegistry.split("\\s+");
                    //Split Chrome version by the dot
                    String[] splitVersion = strChromeVersion[3].split("\\.");
                    //check if the second value equal to 0 (xxx.0.xxxx.xxx)
                    if (splitVersion[1].equalsIgnoreCase("0")) {
                        strChromeDriver = splitVersion[0];
                    }
                }
            }
            propertiesWeb.setProperty("DriverLocal", "chromedriver" + strChromeDriver + ".exe");
        } catch (Exception e) {
            try {
                throw new Exception("Chrome version not found in Registry Editor");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
