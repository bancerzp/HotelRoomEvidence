package Exceptions;

import Panels.MainPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class NoInternetConnection extends Exception {
    public NoInternetConnection(Logger logger) throws IOException {
        super("Brak połączenia z internetem");
        logger.error(this);
        String[] takeIt = new String[]{"InternetConnection"};
        Properties properties = new Properties();
        FileInputStream is = new FileInputStream("config.properties");
        properties.load(is);
        JOptionPane.showMessageDialog(null, MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt, logger)[0]);
    }
}
//wyjatek rzucany gdy jest probelm z webserisem

