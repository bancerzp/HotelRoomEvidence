package Exceptions;

import Panels.MainPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PriceMinMaxException extends Exception {
    public PriceMinMaxException(Logger logger) throws IOException {
        super("Cena maxymalna nie może być mniejsza od minimalnej");
       String[] takeIt= new String[]{"MinMaxPrice"};
        Properties properties = new Properties();
        FileInputStream is = new FileInputStream( "config.properties");
        properties.load(is);
        logger.error(this);
        JOptionPane.showMessageDialog(null, MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt,logger)[0]);
    }
}

//wyjątek rzucany w przypadku gdy cena minimalna wyszukiwanego hotelu jest większa od maksymalnej
