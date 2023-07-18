package com.framework.DAO;

public class ReportSummaryDAO {
    private String strNo;
    private String strDatatableName;
    private String strAction;
    private String strScenario;
    private String strStatus;
    private String strDescription;
    private String strDuration;

    public ReportSummaryDAO() {
    }

    public ReportSummaryDAO(String strNo, String strDatatableName, String strAction, String strScenario, String strStatus, String strDescription, String strDuration) {
        this.setStrNo(strNo);
        this.setStrDatatableName(strDatatableName);
        this.setStrAction(strAction);
        this.setStrScenario(strScenario);
        this.setStrStatus(strStatus);
        this.setStrDescription(strDescription);
        this.setStrDuration(strDuration);
    }


    public String getStrNo() {return strNo;}

    public void setStrNo(String strNo) {this.strNo = strNo;}

    public String getStrDatatableName() {return strDatatableName;}

    public void setStrDatatableName(String strDatatableName) {this.strDatatableName = strDatatableName;}

    public String getStrAction() {return strAction;}

    public void setStrAction(String strAction) {this.strAction = strAction;}

    public String getStrScenario() {return strScenario;}

    public void setStrScenario(String strScenario) {this.strScenario = strScenario;}

    public String getStrStatus() {return strStatus;}

    public void setStrStatus(String strStatus) {this.strStatus = strStatus;}

    public String getStrDescription() {return strDescription;}

    public void setStrDescription(String strDescription) {this.strDescription = strDescription;}

    public String getStrDuration() {return strDuration;}

    public void setStrDuration(String strDuration) {this.strDuration = strDuration;}
}
