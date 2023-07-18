
import com.framework.services.PropertiesService;

public class TestNGConfig {
    public static String strParallel = PropertiesService.readProperties("configuration/testNGConfig.properties").getProperty("Parallel");
    public static Integer intThreadCount = Integer.parseInt(PropertiesService.readProperties("configuration/testNGConfig.properties").getProperty("ThreadCount"));
    public static Integer intThreadThreadDataProvidersCount = Integer.parseInt(PropertiesService.readProperties("configuration/testNGConfig.properties").getProperty("ThreadDataProvidersCount"));
    public static Integer intVerbose = Integer.parseInt(PropertiesService.readProperties("configuration/testNGConfig.properties").getProperty("Verbose"));
    public static boolean boolPreserveOrder = Boolean.valueOf(PropertiesService.readProperties("configuration/testNGConfig.properties").getProperty("PreserveOrder"));
}
