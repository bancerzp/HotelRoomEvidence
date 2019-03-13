package Panels;

import Exceptions.NoInternetConnection;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static Panels.MainPanel.logger;

public class Translator {
    private static final String CLIENT_ID = "pati123-14@o2.pl";
    private static final String CLIENT_SECRET = "05159eebb7f2447fb0002d090846f6f0";
    private static final String ENDPOINT = "http://api.whatsmate.net/v1/translation/translate";

    public static Object translate(String fromLang, String toLang, String text, Logger logger1) throws IOException {

        FileReader fileReader = new FileReader("webserviceData");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int n = 0;
        String textLine = bufferedReader.readLine();
        String[] data=new String[3];
        do {
            data[n] = textLine;
            n++;
            textLine = bufferedReader.readLine();
        } while (textLine != null);

        bufferedReader.close();
        String textRet = new String();
        Logger logger = logger1;
        String jsonPayload = new StringBuilder()
                .append("{")
                .append("\"fromLang\":\"")
                .append(fromLang)
                .append("\",")
                .append("\"toLang\":\"")
                .append(toLang)
                .append("\",")
                .append("\"text\":\"")
                .append(text)
                .append("\"")
                .append("}")
                .toString();

        URL url = null;
        try {
            logger.info("Próba utworzenia urla do usługi webowej");
            url = new URL(data[0]);
        } catch (MalformedURLException e) {
            logger.error("Błąd tworzenia urla do usługi webowej"+e);
            return null;
        }
        HttpURLConnection conn = null;
        try {
            logger.info("Proba polaczenia z webserviceData");
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            logger.error("Blad polaczenia z webserviceData!\n" + e);
            new NoInternetConnection(logger);
        }
        conn.setDoOutput(true);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            logger.error(e);
            return null;
        }
        conn.setRequestProperty("X-WM-CLIENT-ID", data[1]);
        conn.setRequestProperty("X-WM-CLIENT-SECRET", data[2]);
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStream os = null;
        try {
            os = conn.getOutputStream();
        } catch (IOException e) {
            logger.error(e);
            return new NoInternetConnection(logger);
        }
        try {
            os.write(jsonPayload.getBytes());
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
        try {
            os.flush();
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
        try {
            logger.info("Proba zamkniecia strumienia");
            os.close();
        } catch (IOException e) {
            logger.error("Zamkniecie strumienia webserviceData zakonczone bledem!\n" + e);
        }

        int statusCode = 0;
        try {
            statusCode = conn.getResponseCode();
            logger.info("Pobieranie kodu!");
        } catch (IOException e) {
            logger.error(e);
        }
        BufferedReader br = null;
        if(statusCode!=200){
            return null;
        }
        try {
            br = new BufferedReader(new InputStreamReader(
                    (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()
            ));
            logger.info("Pobieranie strumienia");
        } catch (IOException e) {
            logger.error("Blad strumienia!\n" + e);
        }
        String output;
        try {
            logger.info("Pobieranie tekstu\n");
            while ((output = br.readLine()) != null) {
                textRet += output;//System.out.println(output);
            }
        } catch (IOException e) {
            logger.info("Blad przy pobieraniu tekstu!\n" + e);
        }
        conn.disconnect();
       // System.out.println(textRet);
        return textRet;
    }

    static boolean isConnection() throws IOException {
        URL url = null;
        try {
            logger.info("Próba utworzenia urla do usługi webowej");
            url = new URL(ENDPOINT);
        } catch (MalformedURLException e) {
            logger.error("Błąd tworzenia urla do usługi webowej"+e);
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            logger.info("Proba polaczenia z webserviceData");
        } catch (IOException e) {
            new NoInternetConnection(logger);
            logger.error("Blad polaczenia z webserviceData!\n" + e);
        }
        conn.setDoOutput(true);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            logger.error(e);
        }
        conn.setRequestProperty("X-WM-CLIENT-ID", CLIENT_ID);
        conn.setRequestProperty("X-WM-CLIENT-SECRET", CLIENT_SECRET);
        conn.setRequestProperty("Content-Type", "application/json");
        OutputStream os = null;
        try {
            os = conn.getOutputStream();
        } catch (IOException e) {
            logger.error(e);
            return false;
        }
        return true;
    }
}