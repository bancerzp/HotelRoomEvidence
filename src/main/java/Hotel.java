
import Panels.MainPanel;
import com.mysql.jdbc.Connection;
import org.apache.log4j.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


class Hotel {
    static Connection con;
    static MainPanel m;
    static Layout lay1;
    static Appender app1;
    static Logger logger;

    public static void main(String args[]) throws Exception {
        lay1 = new PatternLayout("[%p] %C:%L - %m - Data wpisu: %d %n");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            app1 = new FileAppender(lay1, "logname." + dateFormat.format(new java.util.Date()), true);
        } catch (IOException ex) {

        }
        logger = Logger.getRootLogger();
        BasicConfigurator.configure(app1);
        con = Hotel.ConnectToDatabase(logger);
        if (con != null) {
            m = MainPanel.createMainClass(con, logger);
        }
    }

    private static Connection ConnectToDatabase(Logger logger) throws IOException {
        String[] data = new String[3];
        Logger logge = logger;
        logger.info("--------------------------------------Zaczynam logowanie--------------------------------------");
        FileReader fileReader = new FileReader("connectionData");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int n = 0;
        String textLine = bufferedReader.readLine();
        do {
            data[n] = textLine;
            n++;
            textLine = bufferedReader.readLine();
        } while (textLine != null);
        bufferedReader.close();
        logge.debug("Pobrane zostały dane logowania");
        try {
            logge.info("Próba połączenia z bazą danych");
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(
                    data[0], data[1], data[2]);
        } catch (Exception e) {
            logge.error("Próba połączenia z bazą danych zakończona niepowodzeniem!" + e);
            return null;
        }
        logger.info("Próba połączenia z bazą danych zakończona powodzeniem :)");
        return con;
    }
}