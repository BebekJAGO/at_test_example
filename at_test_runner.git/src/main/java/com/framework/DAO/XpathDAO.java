package com.framework.DAO;

/**
 * Xpath Object Class for load sheet XPATH on Datatable
 */
public class XpathDAO {
    private String strObject, strXpath, strDescription;

    public XpathDAO() {
    }

    public XpathDAO(String strObject, String strXpath, String strDescription) {
        this.strObject = strObject;
        this.strXpath = strXpath;
        this.strDescription = strDescription;
    }

    public String getStrObject() {
        return strObject;
    }

    public void setStrObject(String strObject) {
        this.strObject = strObject;
    }

    public String getStrXpath() {
        return strXpath;
    }

    public void setStrXpath(String strXpath) {
        this.strXpath = strXpath;
    }

    public String getStrDescription() {
        return strDescription;
    }

    public void setStrDescription(String strDescription) {
        this.strDescription = strDescription;
    }
}
