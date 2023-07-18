package com.application.example_app;

import com.framework.baseFunctions.BaseFunction;
import org.openqa.selenium.WebElement;

public class SpecificFunction extends BaseFunction {

    public SpecificFunction(ThreadLocal<String> thrApplication, ThreadLocal<String> thrKeyword, ThreadLocal<String> thrObjectName, ThreadLocal<String> thrValue, ThreadLocal<WebElement> thrWebElement, ThreadLocal<String> thrDescription) {
        super(thrApplication, thrKeyword,thrObjectName,thrValue,thrWebElement,thrDescription);
    }

}
