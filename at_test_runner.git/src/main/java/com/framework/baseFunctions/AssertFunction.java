package com.framework.baseFunctions;

import com.framework.baseFunctions.baseInterface.AssertInterface;
import com.framework.factory.DriverFactory;
import com.framework.factory.TestFactory;
import com.framework.services.ScreenshotService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Assert Function class implement AssertInterface
 */
public class AssertFunction extends BaseController implements AssertInterface {


    public AssertFunction() {
    }

    public AssertFunction(ThreadLocal<String> thrApplication, ThreadLocal<String> thrKeyword, ThreadLocal<String> thrObjectName, ThreadLocal<String> thrValue, ThreadLocal<WebElement> thrWebElement, ThreadLocal<String> thrDescription) {
        super(thrApplication, thrKeyword, thrObjectName, thrValue, thrWebElement, thrDescription);
    }

    /**
     * Assert Web Element Exist
     */
    @Override
    public void assert_web_element_exist() {
        try {
            loadElement();
            if (thrWebElement.get().isDisplayed()) {
                ScreenshotService.init().screenshot();
            }

        } catch (Exception ex) {
            throw new AssertionError("WebElement : " + thrObjectName.get() + " NOT FOUND");
        }
    }

    /**
     * Assert Web Element not Exist
     */
    @Override
    public void assert_web_element_not_exist() {
        try {
            loadElement();
            WebElement webElement = DriverFactory.init().get().findElement(By.xpath(TestFactory.mapXpath.get(thrObjectName.get()).getStrXpath()));
            String strDescription = TestFactory.mapXpath.get(thrObjectName.get()).getStrDescription();
            thrWebElement.set(webElement);
            thrDescription.set(strDescription);

            if (thrWebElement.get().isDisplayed()) {
                throw new AssertionError("WebElement : " + thrObjectName.get() + " FOUND");
            }
        } catch (Exception ex) {
            ScreenshotService.init().screenshot();
        }

    }

    /**
     * Assert Web Element Text is exact same
     */
    @Override
    public void assert_web_element_text_true() {
        loadElement();
        String strObject = thrWebElement.get().getText();
        String strCompare = thrValue.get();
        String strDescription = thrDescription.get();

        ScreenshotService.init().screenshot();
        Assert.assertEquals(strObject, strCompare, strDescription);


    }

    /**
     * Assert Web Element Text is exact not same
     */
    @Override
    public void assert_web_element_text_false() {
        loadElement();
        String strObject = thrWebElement.get().getText();
        String strCompare = thrValue.get();
        String strDescription = thrDescription.get();

        ScreenshotService.init().screenshot();
        Assert.assertNotEquals(strObject, strCompare, strDescription);
    }

    /**
     * Assert Web Element Text is contains text
     */
    @Override
    public void assert_web_element_text_contains_true() {
        loadElement();
        String strObject = thrWebElement.get().getText();
        String strCompare = thrValue.get();
        String strDescription = thrDescription.get();

        if (strObject.contains(strCompare)) {
            ScreenshotService.init().screenshot();
        } else {
            throw new AssertionError("Object Name = " + thrObjectName.get() + "</br>Contains text : " + strCompare + " Not Contains " + strObject + "</br>Description : " + strDescription);
        }
    }

    /**
     * Assert Web Element Text is not contains text
     */
    @Override
    public void assert_web_element_text_contains_false() {
        loadElement();
        String strObject = thrWebElement.get().getText();
        String strCompare = thrValue.get();
        String strDescription = thrDescription.get();

        if (!strObject.contains(strCompare)) {
            ScreenshotService.init().screenshot();
        } else {
            throw new AssertionError("Object Name = " + thrObjectName.get() + "</br>Not Contains text : " + strCompare + " Contains " + strObject + "</br>Description : " + strDescription);
        }
    }

    /**
     * Assert Mobile Element Exist
     */
    @Override
    public void assert_mobile_element_exist() {
        try {
            loadElement();
            if (thrWebElement.get().isDisplayed()) {
                ScreenshotService.init().screenshot();
            }

        } catch (Exception ex) {
            throw new AssertionError("WebElement : " + thrObjectName.get() + " NOT FOUND");
        }
    }

}

//    @Override
//    public void assertObjectNull(Object strObject, String strDescription) {
//        Assert.assertNull(strObject, strDescription);
//    }
//
//    @Override
//    public void assertObjectNotNull(Object strObject, String strDescription) {
//        Assert.assertNotNull(strObject, strDescription);
//    }
//
//    @Override
//    public void assertSame(Object objObject, Object objObjectCompare, String strDescription) {
//        Assert.assertSame(objObject, objObjectCompare, strDescription);
//    }
//
//    @Override
//    public void assertNotSame(Object objObject, Object objObjectCompare, String strDescription) {
//        Assert.assertNotSame(objObject, objObjectCompare, strDescription);
//    }