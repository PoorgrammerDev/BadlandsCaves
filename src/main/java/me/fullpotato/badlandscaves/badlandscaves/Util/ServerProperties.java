package me.fullpotato.badlandscaves.badlandscaves.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerProperties {
    public static String getSetting (File propertiesFile, String setting) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(propertiesFile);

        properties.load(inputStream);
        return properties.getProperty(setting);
    }

    public static void setSetting (File propertiesFile, String setting, String value) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(propertiesFile);

        properties.load(inputStream);
        properties.setProperty(setting, value);
        properties.store(new FileOutputStream(propertiesFile), null);
    }
}
