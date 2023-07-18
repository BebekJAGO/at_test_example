package com.framework.factory;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DevToolsFactory {
    private Logger log = Logger.getLogger("NETWORK");
    private String thrCDPSessionsDevTools = "";
    public static String strDevToolsResponse = "";
    public DevTools devToolsThreadLocal;
    public ExtentTest extentTest;
    public String strTestName;


    public DevToolsFactory() {

    }

    public static String getStrDevToolsResponse() {
        return strDevToolsResponse;
    }

    public static void setStrDevToolsResponse(String strDevToolsResponse) {
        DevToolsFactory.strDevToolsResponse = strDevToolsResponse;
    }

    public String getStrTestName() {
        return strTestName;
    }

    public void setStrTestName(String strTestName) {
        this.strTestName = strTestName;
    }


    public ExtentTest getExtentTest() {
        return extentTest;
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest = extentTest;
    }

    public DevTools getDevToolsThreadLocal() {
        return devToolsThreadLocal;
    }

    public void setDevToolsThreadLocal(DevTools devToolsThreadLocal) {
        this.devToolsThreadLocal = devToolsThreadLocal;
    }



    public String getSessionDevTools() {
        if (devToolsThreadLocal != null)
            return thrCDPSessionsDevTools;
        else
            return null;
    }

    public synchronized String getLogMessageBySessionDevTools() {
        try {
            if (getSessionDevTools() != null) {
                String strSessionDevToolsID = devToolsThreadLocal.getCdpSession().toString();
                String fileName = "C:\\Screens\\Report\\logNetwork.log";
                String strResult = "";

                File file = new File(fileName);
                BufferedReader br = new BufferedReader(new FileReader(file));
                List<String> allLines = br.lines().collect(Collectors.toList());

                for (int i = 0; i < allLines.size(); i++) {
                    if (allLines.get(i).contains(strSessionDevToolsID)) {
                        int intTemp = i;
                        for (int j = 0; j < 6; j++) {
                            strResult += allLines.get(intTemp) + "</br>";
                            intTemp++;
                        }
                        i += 5;
                    }
                }
                br.close();
                return strResult;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }


    public void initDevTools() {
        ChromeDriver chromeDriver = (ChromeDriver) DriverFactory.init().get();
        devToolsThreadLocal = chromeDriver.getDevTools();
        devToolsThreadLocal.createSession();

        thrCDPSessionsDevTools = devToolsThreadLocal.getCdpSession().toString();

        devToolsThreadLocal.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devToolsThreadLocal.addListener(Network.requestWillBeSent(), request -> {
            Request req = request.getRequest();
            try {
                if (req.getHeaders().get("Content-Type") != null || req.getHeaders().get("content-type") != null) {
                    String strType = (req.getHeaders().get("Content-Type") == null ? req.getHeaders().get("content-type").toString() : req.getHeaders().get("Content-Type").toString());
                    if (strType.equalsIgnoreCase("application/json")) {
                        String strTemp = "";
//                        strTemp += "============Request============\r\n";
                        strTemp += "URL : " + req.getUrl() + "\r\n";
                        strTemp += "Headers : " + req.getHeaders() + "\r\n";
                        strTemp += "Method : " + req.getMethod() + "\r\n";
                        if (req.getHasPostData().isEmpty() == false) {
                            strTemp += "Post Data : " + req.getPostData();
                        }
                        extentTest.log(Status.INFO,"Get Request XHR </br>" + strTemp.replace("\r\n","</br>"));
                        log.info("Data : "+ strTestName +"\r\nDev Session ID : " + devToolsThreadLocal.getCdpSession() + "\r\n" + strTemp);
                        strDevToolsResponse = strDevToolsResponse + strTemp;
                    }
                }
            } catch (Exception exception) {
            }

        });
    }

    public void tearDown() {
        if (devToolsThreadLocal != null){
            devToolsThreadLocal.clearListeners();
            devToolsThreadLocal.close();

        }

    }

}
