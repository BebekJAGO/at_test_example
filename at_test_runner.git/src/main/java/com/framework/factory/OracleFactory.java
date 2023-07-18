package com.framework.factory;

import com.framework.services.PropertiesService;

import java.sql.*;
import java.util.Properties;

public class OracleFactory {

    private static volatile OracleFactory instance = null;
    Properties propertiesOracle = PropertiesService.readProperties("configuration/oracleConfig.properties");

    ThreadLocal<Connection> thrConnection = new ThreadLocal<>();

    private OracleFactory() {
        createConnection();
    }

    public static OracleFactory init() {
        if (instance == null) {
            synchronized (OracleFactory.class) {
                if (instance == null)
                    instance = new OracleFactory();
            }
        }
        return instance;
    }
    public Connection getConnection(){
        return thrConnection.get();
    }


    /**
     * Create Connection into Oracle Database
     */
    public Connection createConnection() {
        if (thrConnection.get() == null) {
            try {
                String strUsername = propertiesOracle.getProperty("username");
                String strPassword = propertiesOracle.getProperty("password");
                String strHostname = propertiesOracle.getProperty("hostname");
                int intPort = Integer.parseInt(propertiesOracle.getProperty("port"));
                String strService = propertiesOracle.getProperty("serviceName");

                thrConnection.set(DriverManager.getConnection("jdbc:oracle:thin:@" + strHostname + ":" + intPort + ":" + strService, strUsername, strPassword));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return thrConnection.get();

    }

    /**
     * Close connection Database
     */
    public void tearDown(){
        if(thrConnection.get()!=null){
            try {
                thrConnection.get().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * getAllDatas on table API_INTERFACE with source_deal_id
     * @param strSourceDealID
     * @return
     */
    public ResultSet getDataAPIInterface(String strSourceDealID){
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String strQuery = "select * from MSSAPI.API_INTERFACE  where source_deal_id = ?";
            statement = thrConnection.get().prepareStatement(strQuery);
            statement.setString(1,strSourceDealID);
            resultSet = statement.executeQuery();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }

}
