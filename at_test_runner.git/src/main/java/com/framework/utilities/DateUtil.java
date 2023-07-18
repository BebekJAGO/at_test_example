package com.framework.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * @return string as format date ddmmyyyy-hhmm
     */
    public static String getSimpleDateNow(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy-HHmm");
        return simpleDateFormat.format(date);
    }
}
