package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class PropertiesUtils {

    public static Properties load(String path) throws IOException {
        Properties logPropeties = new Properties();
        FileInputStream fis = new FileInputStream(path);
        logPropeties.load(fis);
        fis.close();
        return logPropeties;
    }
}
