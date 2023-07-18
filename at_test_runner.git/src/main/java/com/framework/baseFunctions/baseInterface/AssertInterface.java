package com.framework.baseFunctions.baseInterface;

/**
 * Interface for AssertClass
 */
public interface AssertInterface {
    void assert_web_element_not_exist();
    void assert_web_element_exist();

    void assert_web_element_text_true();
    void assert_web_element_text_false();

    void assert_web_element_text_contains_true();
    void assert_web_element_text_contains_false();

    void assert_mobile_element_exist();

//    void assertObjectNull(Object strObject, String strDescription);
//    void assertObjectNotNull(Object strObject, String strDescription);
//
//    void assertSame(Object strObject, Object strObjectCompare, String strDescription);
//    void assertNotSame(Object strObject, Object strObjectCompare, String strDescription);

}
