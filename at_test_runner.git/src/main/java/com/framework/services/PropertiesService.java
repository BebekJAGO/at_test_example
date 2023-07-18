package com.framework.services;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesService {
    /**
     * @param strPropertiesName File propertiesName
     * @return properties as Param
     */
    public  static Properties readProperties(String strPropertiesName){
        Properties properties = new Properties();
        try {
            FileReader reader = new FileReader("src\\main\\resources\\"+strPropertiesName);
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Failed load properties "+ strPropertiesName);
        }
        return properties;
    }
}
