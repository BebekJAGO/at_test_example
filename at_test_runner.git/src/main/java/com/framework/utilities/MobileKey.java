package com.framework.utilities;

import com.framework.factory.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;

public class MobileKey {
    /**
     * pressing Android key
     * @param strKey -> key value to press
     */
    public static void key_press_android(String strKey) {
        try {
            Integer.parseInt(strKey);
            AndroidKey androidKeyValue = AndroidKey.valueOf("DIGIT_"+strKey);
            ((AndroidDriver) DriverFactory.init().get()).pressKey(new KeyEvent(androidKeyValue));
        } catch (Exception e) {
            AndroidKey androidKeyValue = AndroidKey.valueOf(strKey.toUpperCase());
            ((AndroidDriver) DriverFactory.init().get()).pressKey(new KeyEvent(androidKeyValue));
        }
    }

    /**
     * pressing iOS keyboard
     * @param strKey key value to press
     */
    public static void key_press_ios(String strKey) {
        if (strKey.equalsIgnoreCase("Return") || strKey.equalsIgnoreCase("Emoji") || strKey.equalsIgnoreCase("Done")) {
            strKey = strKey.toLowerCase();
            strKey = strKey.replace(strKey.charAt(0), Character.toUpperCase(strKey.charAt(0)));
            ((IOSDriver) DriverFactory.init().get()).findElement(By.xpath("//XCUIElementTypeButton[@name='" + strKey + "']")).click();
        } else {
            ((IOSDriver) DriverFactory.init().get()).findElement(By.xpath("//XCUIElementTypeKey[@name='" + strKey + "']")).click();
        }
    }
}
