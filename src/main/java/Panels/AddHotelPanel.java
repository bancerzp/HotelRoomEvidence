package Panels;

import Events.*;
import Exceptions.CharLimitException;
import Exceptions.NoInternetConnection;
import com.mysql.jdbc.Connection;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class AddHotelPanel implements ActionListener, LanguageListener, PlafListener, HotelWasAddedListener {

    private static AddHotelPanel instante = null;//zmienna przechowujaca instancje klasy
    static String[] paramsValue;//zmienna do zapisywania wartości pobranych z xml
    static String[] params = {"AddHotel", "Hotel", "Stars", "Country", "Resort", //tablica zmiennych zależnych od języka
            "EnterCountry", "EnterResort", "Amenities", "SwimmingPool", "Restaurant",
            "Pets", "Parking", "WiFi", "Elevator", "Description", "Clear",
            "Searcher", "Address", "Translate"};

    private File propFile = new File("config.properties");//plik z properties
    JPanel main;//panel główny
    JLayeredPane layer;//panel warstwowy
    GridBagConstraints c, b;//
    GridBagLayout gbc, gbb;

    //components
    static JCheckBox swimmingPoolChb;
    static JCheckBox restaurantChb;
    static JCheckBox petsChb;
    static JCheckBox parkingChb;
    static JCheckBox wifiChb;
    static JCheckBox elevatorChb;
    static JLabel lbl;//napis dodaj hotel
    static JLabel hotelNameTag;
    static JLabel starTag;
    static JLabel addressTag;
    static JLabel countryTag;//napis country
    static JLabel resortTag;//napis resort
    static JLabel amenitiesTag;//napis udogodnienia
    static JTextField countryNameInput;
    static JTextField resortNameInput;
    static JTextField hotelNameInput;
    JTextArea descriptionInput;
    JTextField addrInput;
    static JLabel descTag;


    static JButton addButton, checkButton;
    static JButton clearButton;
    static JSpinner stars;
    static SpinnerModel value =
            new SpinnerNumberModel(0, //initial value
                    0, //minimum value
                    5, //maximum value
                    1); //step

    //Połączanie z db
    Connection con;
    Statement stmt;
    ResultSet rs;
    static Logger logger;
    private ArrayList addingHotelListeners;

    BufferedImage seaPicture;
    BufferedImage treePicture;
    BufferedImage mountainPicture;
    ImageIcon icon = new ImageIcon();
    JLabel picLabel;

    Properties properties;
    InputStream is;

    private AddHotelPanel(Connection con, Logger logger) throws IOException, SQLException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        addingHotelListeners = new ArrayList();
        this.logger = logger;
        this.con = con;
        main = new JPanel();
        layer = new JLayeredPane();
        c = new GridBagConstraints();
        b = new GridBagConstraints();
        layer.setLayout(gbb = new GridBagLayout());
        main.setLayout(gbc = new GridBagLayout());
        properties = new Properties();
        is = new FileInputStream(propFile);
        properties.load(is);


        this.stmt = con.createStatement();
        UIManager.setLookAndFeel(properties.getProperty("DefaultPlafName"));
        //napis dodaj hotel
        c.insets.set(0, 0, 10, 0);
        this.lbl = new JLabel();
        lbl.setFont(new Font(lbl.getFont().getFontName(), lbl.getFont().getStyle(), 20));
        lbl.setMaximumSize(new Dimension(400, 30));
        lbl.setMinimumSize(new Dimension(400, 30));
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        layer.add(lbl, c);

        //napis hotel
        c.insets.set(0, 0, 3, 0);
        this.hotelNameTag = new JLabel();
        hotelNameTag.setMaximumSize(new Dimension(150, 40));
        hotelNameTag.setMinimumSize(new Dimension(150, 40));
        c.gridx = 0;
        c.gridy = 1;
        layer.add(hotelNameTag, c);

        //napis gwiazdki
        this.starTag = new JLabel();
        starTag.setMaximumSize(new Dimension(100, 20));
        starTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 1;
        c.gridy = 1;
        layer.add(starTag, c);

        //pole do wpisania nazwy hotelu
        c.ipadx = 150;
        c.insets.set(0, 0, 10, 0);
        this.hotelNameInput = new JTextField();
        c.gridx = 0;
        c.gridy = 2;
        hotelNameInput.setMinimumSize(new Dimension(110, 25));
        hotelNameInput.setMaximumSize(new Dimension(110, 25));
        layer.add(hotelNameInput, c);

        //spinner do gwiazdek
        c.ipadx = 0;
        this.stars = new JSpinner(value);
        c.gridx = 1;
        stars.setMaximumSize(new Dimension(50, 30));
        stars.setMinimumSize(new Dimension(50, 30));
        layer.add(stars, c);

        //napis kraj
        c.insets.set(0, 0, 5, 0);
        this.countryTag = new JLabel();//country
        countryTag.setMaximumSize(new Dimension(200, 25));
        countryTag.setMinimumSize(new Dimension(200, 25));
        c.gridx = 0;
        c.gridy = 3;
        layer.add(countryTag, c);

        //napis miasto
        this.resortTag = new JLabel();
        c.gridy = 3;
        c.gridx = 1;
        resortTag.setMaximumSize(new Dimension(250, 25));
        resortTag.setMinimumSize(new Dimension(250, 25));
        layer.add(resortTag, c);

        //pole na kraj
        c.ipadx = 150;
        c.insets.set(0, 0, 10, 0);
        this.countryNameInput = new JTextField();
        countryNameInput.setMaximumSize(new Dimension(100, 28));
        countryNameInput.setMinimumSize(new Dimension(100, 28));
        c.gridx = 0;
        c.gridy = 4;
        layer.add(countryNameInput, c);

        //pole na miasto
        this.resortNameInput = new JTextField();
        resortNameInput.setMaximumSize(new Dimension(100, 28));
        resortNameInput.setMinimumSize(new Dimension(100, 28));
        c.gridx = 1;
        layer.add(resortNameInput, c);

        //napis udogodnienia
        c.insets.set(0, 0, 10, 0);
        amenitiesTag = new JLabel();
        amenitiesTag.setFont(new Font(lbl.getFont().getFontName(), lbl.getFont().getStyle(), 16));
        amenitiesTag.setMaximumSize(new Dimension(100, 25));
        amenitiesTag.setMinimumSize(new Dimension(100, 25));
        c.gridx = 0;
        c.gridy = 5;
        layer.add(amenitiesTag, c);

        //panel z checkboxami
        JPanel h = new JPanel();
        GridLayout g;
        h.setLayout(g = new GridLayout());
        g.setColumns(2);
        g.setRows(3);
        this.swimmingPoolChb = new JCheckBox();
        this.restaurantChb = new JCheckBox();
        this.petsChb = new JCheckBox();
        this.parkingChb = new JCheckBox();
        this.wifiChb = new JCheckBox();
        this.elevatorChb = new JCheckBox();
        c.gridwidth = 500;
        swimmingPoolChb.setMinimumSize(new Dimension(100, 25));
        restaurantChb.setMinimumSize(new Dimension(100, 25));
        petsChb.setMinimumSize(new Dimension(100, 25));
        parkingChb.setMinimumSize(new Dimension(100, 25));
        wifiChb.setMinimumSize(new Dimension(100, 25));
        elevatorChb.setMinimumSize(new Dimension(100, 25));
        h.add(swimmingPoolChb);
        h.add(restaurantChb);
        h.add(petsChb);
        h.add(parkingChb);
        h.add(wifiChb);
        h.add(elevatorChb);
        c.gridy = 6;
        c.gridx = 0;
        layer.add(h, c);

        addressTag = new JLabel();
        c.gridx = 0;
        c.gridy = 7;
        layer.add(addressTag, c);

        c.ipadx = 400;
        addrInput = new JTextField();
        c.gridx = 0;
        c.gridy = 8;
        layer.add(addrInput, c);

        c.ipadx = 0;
        c.insets.set(0, 0, 10, 0);
        c.gridx = 0;
        c.gridy = 9;
        descTag = new JLabel();
        descTag.setMaximumSize(new Dimension(100, 20));
        descTag.setMinimumSize(new Dimension(100, 20));
        layer.add(descTag, c);

        JScrollPane scrollDescription = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        c.gridx = 0;
        c.gridy = 10;
        descriptionInput = new JTextArea();
        descriptionInput.setWrapStyleWord(true);
        descriptionInput.setMaximumSize(new Dimension(200, 20));
        descriptionInput.setMinimumSize(new Dimension(200, 20));
        descriptionInput.setLineWrap(true);
        scrollDescription.add(descriptionInput);
        scrollDescription.setViewportView(descriptionInput);

        c.ipadx = 400;
        c.ipady = 60;
        layer.add(scrollDescription, c);

        c.ipadx = 100;
        c.ipady = 10;
        c.gridy = 11;
        c.gridx = 0;
        this.checkButton = new JButton();
        this.addButton = new JButton();
        addButton.setMinimumSize(new Dimension(100, 10));
        addButton.setMinimumSize(new Dimension(100, 10));

        this.addButton.addActionListener(this);
        layer.add(addButton, c);

        clearButton = new JButton();
        c.gridy = 12;
        c.gridx = 0;
        clearButton.setMinimumSize(new Dimension(100, 10));
        clearButton.setMinimumSize(new Dimension(100, 10));
        layer.add(clearButton, c);
        clearButton.addActionListener(this);

        c.gridy = 13;
        c.gridx = 0;
        checkButton.setMinimumSize(new Dimension(100, 10));
        checkButton.setMinimumSize(new Dimension(100, 10));
        checkButton.addActionListener(this);
        layer.add(checkButton, c);

        treePicture = ImageIO.read(new File(properties.getProperty("TreePicture")));
        seaPicture = ImageIO.read(new File(properties.getProperty("SeaPicture")));
        mountainPicture = ImageIO.read(new File(properties.getProperty("MountainPicture")));

        picLabel = new JLabel(icon = new ImageIcon());
        b.gridx = 1;
        b.gridy = 1;
        main.add(layer, b);
        main.add(picLabel, b);
        main.setVisible(true);

        //nasłuchuje zmian w polu opisu
        descriptionInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    //sprawdza czy nie został przekroczony limit
                    checkLimit(properties.getProperty("CharLimit"), 1);
                } catch (CharLimitException e1) {
                } catch (IOException e1) {
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        //nasłuchuje zmian w polu miejscowosc
        resortNameInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    properties = new Properties();
                    is = new FileInputStream(propFile);
                    properties.load(is);
                    //sprawdza czy nie został przekroczony limit
                    checkLimit(properties.getProperty("ResortLimit"), 2);
                } catch (CharLimitException e1) {
                } catch (IOException e1) {
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        //nasłuchuje zmian w polu kraj
        countryNameInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    properties = new Properties();
                    is = new FileInputStream(propFile);
                    properties.load(is);
                    //sprawdza czy nie został przekroczony limit
                    checkLimit(properties.getProperty("CountryLimit"), 3);
                } catch (CharLimitException e1) {
                } catch (IOException e1) {
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        //nasłuchuje zmian w polu adres
        addrInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    properties = new Properties();
                    is = new FileInputStream(propFile);
                    properties.load(is);
                    //sprawdza czy nie został przekroczony limit
                    checkLimit(properties.getProperty("AddresLimit"), 4);
                } catch (CharLimitException e1) {
                } catch (IOException e1) {
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        //nasłuchuje zmian w polu nazwa hotelu
        hotelNameInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    properties = new Properties();
                    is = new FileInputStream(propFile);
                    properties.load(is);
                    //sprawdza czy nie został przekroczony limit
                    checkLimit(properties.getProperty("NameLimit"), 5);
                } catch (CharLimitException e1) {
                } catch (IOException e1) {
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == addButton) {
//przycisk dodaj=dodawanie hotelu
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fireAddingHotelEvent(addHotel());
                    return null;
                }

                @Override
                protected void done() {

                }
            };
            worker.execute();

        } else if (source == clearButton) {
            clearData();
            this.logger.info("Wyczyszczono formularze");
        } else if (source == checkButton) {
            //"podglad" opisu w drugim jezyku
            if (descriptionInput.getText().length() > 0) {
                try {
                    if (!Translator.isConnection()) {
                        new NoInternetConnection(this.logger);
                    } else {
                        JTextArea j = null;
                        try {
                            j = new JTextArea(translate());
                        } catch (IOException e1) {
                            this.logger.error(e1);
                        } catch (NoInternetConnection noInternetConnection) {
                            this.logger.error(noInternetConnection);
                        }
                        if (j.getText() != null) {
                            JFrame translateFrame = new JFrame();
                            j.setLineWrap(true);
                            j.setWrapStyleWord(true);
                            translateFrame.add(j);
                            translateFrame.setSize(300, 200);
                            translateFrame.setVisible(true);
                        }
                    }
                } catch (IOException e1) {
                    this.logger.error(e1);
                }
            }
        }
    }


    //czysci formatki
    private void clearData() {
        countryNameInput.setText("");
        resortNameInput.setText("");
        swimmingPoolChb.setSelected(false);
        restaurantChb.setSelected(false);
        petsChb.setSelected(false);
        parkingChb.setSelected(false);
        wifiChb.setSelected(false);
        elevatorChb.setSelected(false);
        descriptionInput.setText("");
        stars.setValue(0);
        hotelNameInput.setText("");
        addrInput.setText("");
    }


    //funkcja dodająca hotel
    public boolean addHotel() throws IOException {
        if (Translator.isConnection()) {
            if (checkIsNotEmpty() && Translator.isConnection()) {
                properties = new Properties();
                is = new FileInputStream(propFile);
                properties.load(is);
                String desc2;
                String descRet;
                String countryPl;
                String countryEn;
                String resortPl;
                String resortEn;
                String query = "insert into hotel values (null,'" + hotelNameInput.getText() + "','";
                if (properties.getProperty("DefaultLanguage").toString().equals("Polish")) {
                    descRet = descriptionInput.getText().replaceAll("\n", "");
                    countryPl = countryNameInput.getText().replaceAll("\n", "");
                    resortPl = resortNameInput.getText().replaceAll("\n", "");
                    countryEn = (String) Translator.translate("pl", "en", countryPl.replaceAll("\n", ""), this.logger);
                    resortEn = (String) Translator.translate("pl", "en", resortPl.replaceAll("\n", ""), this.logger);
                    desc2 = (String) Translator.translate("pl", "en", descRet.replaceAll("\n", ""), this.logger);

                } else {
                    countryEn = countryNameInput.getText().replaceAll("\n", "");
                    resortEn = resortNameInput.getText().replaceAll("\n", "");
                    countryPl = (String) Translator.translate("en", "pl", countryEn.replaceAll("\n", ""), this.logger);
                    resortPl = (String) Translator.translate("en", "pl", resortEn.replaceAll("\n", ""), this.logger);
                    descRet = (String) Translator.translate("en", "pl", descriptionInput.getText().replaceAll("\n", ""), this.logger);
                    desc2 = descriptionInput.getText().replaceAll("\n", "");
                }

                if ((countryPl + "/" + countryEn).length() > 2 * Integer.parseInt(properties.getProperty("CountryLimit"))) {
                    query += (countryPl + "/" + countryEn).substring(0, Integer.parseInt(properties.getProperty("CountryLimit")) * 2 - 1) + "','";
                } else {
                    query += countryPl + "/" + countryEn + "','";
                }

                if ((resortPl + "/" + resortEn).length() > 2 * Integer.parseInt(properties.getProperty("ResortLimit"))) {
                    query += (resortPl + "/" + resortEn).substring(0, Integer.parseInt(properties.getProperty("ResortLimit")) * 2 - 1) + "','";
                } else {
                    query += resortPl + "/" + resortEn + "','";
                }

                query += addrInput.getText() + "','" + stars.getValue().toString() + "','";
                if (descRet.length() > Integer.parseInt(properties.getProperty("CharLimit"))) {
                    descRet = descRet.substring(0, Integer.parseInt(properties.getProperty("CharLimit")) - 1);
                }
                query += descRet + "',";
                if (swimmingPoolChb.isSelected()) {
                    query += "'+',";
                } else {
                    query += "'-',";
                }
                if (petsChb.isSelected()) {
                    query += "'+',";
                } else {
                    query += "'-',";
                }
                if (restaurantChb.isSelected()) {
                    query += "'+',";
                } else {
                    query += "'-',";
                }
                if (parkingChb.isSelected()) {
                    query += "'+',";
                } else {
                    query += "'-',";
                }
                if (elevatorChb.isSelected()) {
                    query += "'+',";
                } else {
                    query += "'-',";
                }
                if (wifiChb.isSelected()) {
                    query += "'+','";
                } else {
                    query += "'-','";
                }
                if (desc2.length() > Integer.parseInt(properties.getProperty("CharLimit"))) {
                    query += (desc2.substring(0, Integer.parseInt(properties.getProperty("CountryLimit")))) + "');";
                } else {
                    query += desc2 + "');";
                }
                try {
                    this.logger.info("Próba wykonania zapytania: " + query);
                    stmt.executeUpdate(query);
                    return true;
                } catch (SQLException e) {
                    this.logger.error("Blad zapytania!\n" + e);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            new NoInternetConnection(this.logger);
            return false;
        }
    }

    //funkcja tłumacząca opis
    private String translate() throws IOException, NoInternetConnection {
        String descRet;
        properties = new Properties();
        is = new FileInputStream(propFile);
        properties.load(is);
        if (properties.getProperty("DefaultLanguage").toString().equals("Polish")) {
            descRet = (String) Translator.translate("pl", "en", descriptionInput.getText().replaceAll("\n", ""), this.logger);
        } else {
            descRet = (String) Translator.translate("en", "pl", descriptionInput.getText().replaceAll("\n", ""), this.logger);
        }
        return descRet;
    }

    //sprawdzanie czy przy dodawaniu hotelu wszystkie formatki są wypełnione
    private boolean checkIsNotEmpty() {
        boolean notEmpty = true;
        if (notEmpty && hotelNameInput.getText().replace(" ", "").length() == 0) {
            notEmpty = false;
            this.logger.info("hotelNameInput");
        }
        if (notEmpty && countryNameInput.getText().replace(" ", "").length() == 0) {
            notEmpty = false;
            this.logger.info("countryNameInput");
        }
        if (notEmpty && resortNameInput.getText().replace(" ", "").length() == 0) {
            notEmpty = false;
            this.logger.info("ResortNameInput");
        }
        if (notEmpty && addrInput.getText().replace(" ", "").length() == 0) {
            notEmpty = false;
            this.logger.info("AddrInput");
        }
        if (notEmpty && descriptionInput.getText().replace(" ", "").length() == 0) {
            notEmpty = false;
            this.logger.info("descriptionInput");
        }
        if (notEmpty && stars.getValue().toString().equals("0")) {
            notEmpty = false;
            this.logger.info("stars");
        }
        return notEmpty;
    }

    //sprawdza czy nie został przekroczony limit znaków
    private void checkLimit(String limit, int i) throws CharLimitException, IOException {
        switch (i) {
            case 1:
                if (descriptionInput.getText().length() >= Integer.parseInt(limit)) {
                    descriptionInput.setText(descriptionInput.getText().substring(0, Integer.parseInt(limit) - 1));
                    throw new CharLimitException(this.logger, limit);
                }
                break;
            case 2:
                if (resortNameInput.getText().length() >= Integer.parseInt(limit)) {
                    resortNameInput.setText(resortNameInput.getText().substring(0, Integer.parseInt(limit) - 1));
                    throw new CharLimitException(this.logger, limit);
                }
                break;
            case 3:
                if (countryNameInput.getText().length() >= Integer.parseInt(limit)) {
                    countryNameInput.setText(countryNameInput.getText().substring(0, Integer.parseInt(limit) - 1));
                    throw new CharLimitException(this.logger, limit);
                }
                break;
            case 4:
                if (addrInput.getText().length() >= Integer.parseInt(limit)) {
                    addrInput.setText(addrInput.getText().substring(0, Integer.parseInt(limit) - 1));
                    throw new CharLimitException(this.logger, limit);
                }
                break;
            case 5:
                if (hotelNameInput.getText().length() >= Integer.parseInt(limit)) {
                    hotelNameInput.setText(hotelNameInput.getText().substring(0, Integer.parseInt(limit) - 1));
                    throw new CharLimitException(this.logger, limit);
                }
                break;
        }
    }

    //nadpisane funkcje
    //zmiana języka panelu
    @Override
    public void changeLanguage(LanguageEvent event) {
        paramsValue = MainPanel.ReadXmlLanguage(event.getLanguage(), params, this.logger);
        //kolejny panel
        lbl.setText(paramsValue[0]);
        hotelNameTag.setText(paramsValue[1]);
        starTag.setText(paramsValue[2]);
        countryTag.setText(paramsValue[3]);
        amenitiesTag.setText(paramsValue[7] + ":");
        addButton.setText(paramsValue[0]);
        resortTag.setText(paramsValue[4]);
        descTag.setText(paramsValue[14]);
        String[] s = {"Address"};
        addressTag.setText(MainPanel.ReadXmlLanguage(event.getLanguage(), s, this.logger)[0]);
        swimmingPoolChb.setText(paramsValue[8]);
        restaurantChb.setText(paramsValue[9]);
        petsChb.setText(paramsValue[10]);
        parkingChb.setText(paramsValue[11]);
        wifiChb.setText(paramsValue[12]);
        elevatorChb.setText(paramsValue[13]);
        clearButton.setText(paramsValue[15]);
        checkButton.setText(paramsValue[18]);
        this.logger.info("Nastapila zmiana jezyka w tym panelu!");
    }

    //funkcja posępowania po odpaleniu eventu dodania hotelu
    @Override
    public void hotelWasAdded(HotelWasAddedEvent event) {
        if (event.getWasAdded()) {
            try {
                properties = new Properties();
                is = new FileInputStream(propFile);
                properties.load(is);
            } catch (IOException e) {
                this.logger.error(e);
            }
            String[] takeIt = {"Hotel", "Adding"};
            takeIt = MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage").toString(), takeIt, this.logger);
            this.logger.info("Dodano hotel");
            JOptionPane.showMessageDialog(null, takeIt[0] + " " + takeIt[1]);
            clearData();
        } else {
            String[] takeIt = {"Warning"};
            try {
                properties = new Properties();
                is = new FileInputStream(propFile);
                properties.load(is);
                JOptionPane.showMessageDialog(null, MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage").toString(), takeIt, this.logger)[0]);
            } catch (IOException e) {
                this.logger.error(e);
            }
        }
    }


    //funkcja zmiany PLAF
    @Override
    public void changePlaf(PlafEvent event) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException {
        int plaf = event.getPlaf();
        properties = new Properties();
        is = new FileInputStream(propFile);
        properties.load(is);
        switch (plaf) {
            case 1:
                UIManager.setLookAndFeel(properties.getProperty("Plaf1"));
                SwingUtilities.updateComponentTreeUI(main);
                b.gridx = 1;
                b.gridy = 1;
                picLabel = new JLabel(new ImageIcon(seaPicture));
                main.remove(0);
                main.remove(0);
                main.add(layer, b);
                main.add(picLabel, b);
                break;
            case 2:
                UIManager.setLookAndFeel(properties.getProperty("Plaf2"));
                SwingUtilities.updateComponentTreeUI(main);
                b.gridx = 1;
                b.gridy = 1;
                picLabel = new JLabel(new ImageIcon(treePicture));
                main.remove(0);
                main.remove(0);
                main.add(layer, b);
                main.add(picLabel, b);
                break;
            case 3:
                UIManager.setLookAndFeel(properties.getProperty("Plaf3"));
                SwingUtilities.updateComponentTreeUI(main);
                b.gridx = 1;
                b.gridy = 1;
                picLabel = new JLabel(new ImageIcon(mountainPicture));
                main.remove(0);
                main.remove(0);
                main.add(layer, b);
                main.add(picLabel, b);
                break;
        }
        main.revalidate();
    }

    //Własne Eventy
    //dodawanie słuchaczy do listy addingHotelListeners
    public synchronized void addAddingHotelListener(HotelWasAddedListener l) {
        addingHotelListeners.add(l);
    }

    //usuwanie słuchaczy z listy addingHotelListeners
    public synchronized void removeAddingHotelListener(HotelWasAddedListener l) {
        addingHotelListeners.remove(l);
    }

    //wywołanie wydarzenia dodawania hotelu
    private synchronized void fireAddingHotelEvent(boolean wasAdded) {
        HotelWasAddedEvent addedEv = new HotelWasAddedEvent(this, wasAdded);
        Iterator listenersIt = addingHotelListeners.iterator();
        while (listenersIt.hasNext()) {
            ((HotelWasAddedListener) listenersIt.next()).hotelWasAdded(addedEv);
        }
    }

    //SINGLETON
    public static synchronized AddHotelPanel createAddHotelPanel(Connection con, Logger logger) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (instante == null) {
            try {
                logger.info("Próba utworzenia instancji klasy AddHotelPanel");
                instante = new AddHotelPanel(con, logger);
            } catch (IOException e) {
                logger.error(e);
            } catch (SQLException e) {
                logger.error(e);
            }
            return instante;
        } else {
            logger.error("AddRoomPanel już został wcześniej utworzony!!!1");
            return null;
        }
    }
}