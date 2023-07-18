package com.framework.services;

import com.framework.utilities.DateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyConfig {
    public static Properties propertiesTestNG = PropertiesService.readProperties("configuration/testNGConfig.properties");

    public static String strPathDatatable = "";
    public static String strPathDatatableDir = PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("strPathDatatableDir");
    public static String strMainSheet = PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("strMainSheet");
    public static String strDataInfo = PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("strInfoSheet");
    public static String strColumnMainSheet = PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("strColumnMainSheet");
    public static String strDatePathReport = DateUtil.getSimpleDateNow();
    public static String strPathReport = PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("strPathReport") + strDatePathReport + "\\";
    public static boolean boolCreateWPByData = Boolean.valueOf(PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("boolCreateWPByData"));
    public static boolean boolReportOnly = Boolean.valueOf(PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("ReportOnly"));
    public static boolean boolReplaceReportExisting = Boolean.valueOf(PropertiesService.readProperties("configuration/excelConfig.properties").getProperty("ReplaceReportExisting"));
    public static Map<String, String> mapSaveData = new HashMap();
    public static Integer intDataCounter = 1;

    public static String strTotalTestDuration = "00:00:00.000";
    public static String strPathSourceZip = "";
    public static String strDestinationFTP = "";
    public static String strTrack = "";

}
