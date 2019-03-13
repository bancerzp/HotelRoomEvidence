package Exceptions;

import Panels.MainPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CharLimitException extends Exception {
    public CharLimitException(Logger logger,String numberChar) throws IOException {
        super("Limit znaków został przekroczony"+numberChar);
        String[] takeIt = new String[]{"CharLimit",numberChar};
        Properties properties = new Properties();
        FileInputStream is = new FileInputStream("config.properties");
        logger.error(this);
        properties.load(is);
        takeIt= MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt,logger);
        JOptionPane.showMessageDialog(null, takeIt[0]+numberChar+")");
    }
}
//wyjątek rzucany w przypadku przekroczenia limitu znaków