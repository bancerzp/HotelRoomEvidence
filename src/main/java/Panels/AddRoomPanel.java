package Panels;

import Events.*;
import Exceptions.CharLimitException;
import com.mysql.jdbc.Connection;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;


class AddRoomPanel extends Component implements ActionListener, LanguageListener, PlafListener, HotelWasAddedListener, RoomWasAddedListener, HotelWasSelectedListener, HotelWasDeletedListener {

    private static AddRoomPanel instante = null;
    private static String[] paramsValue;
    private static String[] paramsSearch = {"AddRoom", "SelectHotel", "RoomSize", "Price", "Bathroom", "Amenities", "LiquirCabinet", "Tv", "Clear", "Country", "Resort", "RoomName"};
    private static File propFileName;
    JPanel main;
    private ArrayList addingRoomListeners;
    private JLayeredPane layer;

    private GridBagConstraints c = new GridBagConstraints();
    private GridBagConstraints b = new GridBagConstraints();
    private GridBagLayout gbc, gbb;

    private static JCheckBox chbIsBathroom;
    private static JCheckBox chbIsBar;
    private static JCheckBox chbIsTv;
    private static JLabel title;
    private static JLabel nameTag;
    private static JLabel countryResortTag;
    private static JLabel selectTag;
    private static JLabel personTag;
    private static JLabel amenitiesTag;//napis udogodnienia
    private static JLabel priceTag;//napis pricemin
    private static JComboBox hotelNameInput;
    private ItemListener changeHotelListener;

    private static JTextField priceValueInput;
    private static JTextField nameInput;
    private static JTextArea countryResortShow;

    private static JButton addButton;
    private static JButton clearButton;
    private static JSpinner spinnerPerson;

    private static SpinnerModel value =
            new SpinnerNumberModel(0, //initial value
                    0, //minimum value
                    10, //maximum value
                    1); //step

    //Połączanie z db
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private static DefaultTableModel model;
    private static Logger logger;

    private BufferedImage seaPicture;
    private BufferedImage treePicture;
    private BufferedImage mountainPicture;
    private ImageIcon icon = new ImageIcon();
    private JLabel picLabel;

    Properties properties;
    InputStream is;


    private AddRoomPanel(Connection con, Logger logger) throws IOException, SQLException {
        this.propFileName = new File("config.properties");
        addingRoomListeners = new ArrayList();
        this.logger = logger;
        this.con = con;
        main = new JPanel();
        layer = new JLayeredPane();
        main.setLayout(gbc = new GridBagLayout());
        layer.setLayout(gbb = new GridBagLayout());
        this.properties = new Properties();
        this.is = new FileInputStream(propFileName);
        this.properties.load(this.is);
        paramsValue = MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), paramsSearch, this.logger);
        stmt = con.createStatement();

        treePicture = ImageIO.read(new File(properties.getProperty("TreePicture")));
        seaPicture = ImageIO.read(new File(properties.getProperty("SeaPicture")));
        mountainPicture = ImageIO.read(new File(properties.getProperty("MountainPicture")));
        c.insets.set(0, 150, 20, 0);
        picLabel = new JLabel(icon = new ImageIcon());
        JLayeredPane h = new JLayeredPane();

        this.title = new JLabel();
        title.setFont(new Font(title.getFont().getFontName(), title.getFont().getStyle(), 20));
        title.setMaximumSize(new Dimension(400, 40));
        title.setMinimumSize(new Dimension(400, 40));
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        layer.add(title, c);

        c.insets.set(0, 0, 5, 100);
        this.nameTag = new JLabel();
        nameTag.setMaximumSize(new Dimension(150, 20));
        nameTag.setMinimumSize(new Dimension(150, 20));
        c.gridx = 0;
        c.gridy = 1;
        layer.add(nameTag, c);

        c.insets.set(0, 0, 5, 150);
        this.personTag = new JLabel();
        personTag.setMaximumSize(new Dimension(150, 20));
        personTag.setMinimumSize(new Dimension(150, 20));
        c.gridx = 1;
        c.gridy = 1;
        layer.add(personTag, c);

        this.nameInput = new JTextField();
        c.gridy = 2;
        c.gridx = 0;
        c.insets.set(0, 0, 10, 0);
        nameInput.setMaximumSize(new Dimension(150, 30));
        nameInput.setMinimumSize(new Dimension(150, 30));
        layer.add(nameInput, c);

        this.spinnerPerson = new JSpinner(value);
        c.gridx = 1;
        c.insets.set(0, 0, 10, 200);
        spinnerPerson.setMaximumSize(new Dimension(70, 30));
        spinnerPerson.setMinimumSize(new Dimension(70, 30));
        layer.add(spinnerPerson, c);

        this.selectTag = new JLabel();
        c.insets.set(0, 100, 10, 0);
        selectTag.setMaximumSize(new Dimension(100, 20));
        selectTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 0;
        c.gridy = 5;
        layer.add(selectTag, c);

        c.gridx = 1;
        c.insets.set(0, 0, 10, 420);
        countryResortTag = new JLabel();
        countryResortTag.setMaximumSize(new Dimension(150, 20));
        countryResortTag.setMinimumSize(new Dimension(150, 20));
        layer.add(countryResortTag, c);


        c.insets.set(0, 150, 10, 0);
        this.hotelNameInput = new JComboBox();
        c.gridx = 0;
        c.gridy = 6;
        hotelNameInput.setMinimumSize(new Dimension(200, 30));
        hotelNameInput.setMaximumSize(new Dimension(200, 30));
        layer.add(hotelNameInput, c);

        c.gridx = 1;
        c.insets.set(0, 0, 10, 400);
        countryResortShow = new JTextArea();
       countryResortShow.setLineWrap(true);
        countryResortShow.setWrapStyleWord(true);
        //countryResortShow.setMaximumSize(new Dimension(300, 25));
        countryResortShow.setMinimumSize(new Dimension(300, 60));
        layer.add(countryResortShow, c);

        priceTag = new JLabel();//napis resort

        c.gridy = 7;
        c.gridx = 0;
        c.insets.set(0, 300, 4, 0);
        layer.add(priceTag, c);

        c.insets.set(0, 300, 60, 0);
        priceValueInput = new JTextField();
        priceValueInput.setMaximumSize(new Dimension(150, 25));
        priceValueInput.setMinimumSize(new Dimension(150, 25));

        c.gridy = 8;
        c.gridx = 0;
        layer.add(priceValueInput, c);

        c.insets.set(0, 110, 20, 0);
        amenitiesTag = new JLabel();
        amenitiesTag.setFont(new Font(title.getFont().getFontName(), title.getFont().getStyle(), 16));
        c.gridx = 0;
        c.gridy = 9;
        layer.add(amenitiesTag, c);

        GridLayout g = new GridLayout();
        h.setLayout(g = new GridLayout());
        g.setColumns(2);
        g.setRows(3);
        this.chbIsBathroom = new JCheckBox();
        this.chbIsBar = new JCheckBox();
        this.chbIsTv = new JCheckBox();
        c.gridwidth = 500;
        chbIsBathroom.setMinimumSize(new Dimension(200, 20));
        chbIsBar.setMinimumSize(new Dimension(200, 20));
        chbIsTv.setMinimumSize(new Dimension(200, 20));
        h.setPreferredSize(new Dimension(500, 120));
        h.add(chbIsBathroom);
        h.add(chbIsBar);
        h.add(chbIsTv);
        c.gridy = 12;
        c.gridx = 0;
        c.insets.set(0, 0, 30, 250);
        layer.add(h, c);

        c.gridy = 13;
        c.gridx = 0;
        c.insets.set(0, 0, 30, 260);
        this.addButton = new JButton();
        addButton.setPreferredSize(new Dimension(100, 20));
        this.addButton.addActionListener(this);
        layer.add(addButton, c);
        b.gridx = 1;
        b.gridy = 1;

        this.changeHotelListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                if (hotelNameInput.getSelectedItem() != null) {
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            refreshCountryResort(hotelNameInput.getSelectedItem().toString());
                            return null;
                        }

                        @Override
                        protected void done() {
                        }
                    };
                    worker.execute();


                }
            }
        };
        hotelNameInput.addItemListener(changeHotelListener);

        main.add(layer, b);
        main.add(picLabel, b);

        clearButton = new JButton();
        c.gridy = 14;
        c.gridx = 0;
        clearButton.setPreferredSize(new Dimension(100, 20));
        layer.add(clearButton, c);
        clearButton.addActionListener(this);
        nameInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    checkLimit(properties.getProperty("NameLimit"), 1);
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

        priceValueInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    checkLimit(properties.getProperty("PriceLimit"), 2);
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


        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                selectHotels();
                return null;
            }

            @Override
            protected void done() {

            }
        };
        worker.execute();

    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == addButton) {

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fireAddingRoomEvent(checkIsNotEmpty());
                    return null;
                }

                @Override
                protected void done() {
                }
            };
            worker.execute();


            this.logger.info("Proba dodania hotelu");
        } else if (source == clearButton) {
            clearData();
            this.logger.info("Wyczyszczono formularze");
        }

    }

    public static synchronized AddRoomPanel createAddRoomPanel(Connection con, Logger logger) {
        if (instante == null) {
            try {
                logger.info("Próba utworzenia instancji klasy AddRoomPanel");
                instante = new AddRoomPanel(con, logger);
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

    //funkcja dodająca pokój
    public void addRoom() {

        String query = "insert into room values (null,'" + hotelNameInput.getSelectedItem().toString().split(" ")[0] + "','" + spinnerPerson.getValue().toString() + "'," + Integer.parseInt(priceValueInput.getText()) + ",";

        if (chbIsBathroom.isSelected()) {
            query += "'+',";
        } else {
            query += "'-',";
        }
        if (chbIsBar.isSelected()) {
            query += "'+',";
        } else {
            query += "'-',";
        }
        if (chbIsTv.isSelected()) {
            query += "'+',";
        } else {
            query += "'-',";
        }
        query += "'" + nameInput.getText() + "');";
        try {
            this.logger.info("Zapytanie\n" + query + "\n jest wykonanywane");
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            this.logger.error("Blad zapytania!\n" + e);
        }
    }

    //czyszczenie formatek
    private void clearData() {
        chbIsBathroom.setSelected(false);
        chbIsBar.setSelected(false);
        chbIsTv.setSelected(false);
        spinnerPerson.setValue(0);
        hotelNameInput.setSelectedItem("");
        priceValueInput.setText("");
        nameInput.setText("");
    }

    //odświeżanie pola z automatycznie wyświetlanym krajem i państwem
    public void refreshCountryResort(String hotel) {
        if (hotel != "") {
            String query = "select country,resort from hotel where idHotel=" + hotel.split(" ")[0] + ";";
            try {
                logger.info("Próba wykonania zapytania: " + query);
                this.rs = this.stmt.executeQuery(query);
                rs.next();
                countryResortShow.setText(rs.getString(1) + "/" + rs.getString(2));
                this.logger.info("Zapytanie\n" + query + "\n zostalo wykonane");
            } catch (SQLException e) {
                this.logger.error("Błąd zapytania!!!:" + query);
            }
        } else {
            countryResortShow.setText("");
        }
    }

    //funkcja uzupełniająca listę hoteli
    private void selectHotels() {
        try {
            this.logger.info("Próba wykonania zapytania: " + "select idHotel,name from hotel where name<>'' and name is not null;");
            rs = stmt.executeQuery("select idHotel,name from hotel where name<>'' and name is not null;");
            hotelNameInput.removeAllItems();
            hotelNameInput.addItem("");
            while (rs.next()) {
                hotelNameInput.addItem(rs.getString(1) + " " + rs.getString(2));
            }
        } catch (SQLException e) {
            this.logger.error("Błąd pobierania hoteli!!");
        }
    }

    //funkcja sprawdzająca czy wszystkie formatki są wypełnione
    private boolean checkIsNotEmpty() {
        boolean correctData = true;

        if (correctData && (hotelNameInput.getSelectedItem().toString() == "")) {
            correctData = false;
            logger.debug("Pusta nazwa hotelu");
        }
        if (correctData && priceValueInput.getText().replace(" ", "").length() == 0) {
            correctData = false;
            logger.debug("Pusta cena");
        } else if (correctData && priceValueInput.getText().replace(" ", "").length() > 0) {
            try {
                this.logger.info("Sprawdzanie typu wprowadzanej ceny");
                Integer.parseInt(priceValueInput.getText());
                correctData = true;
            } catch (Exception e) {
                this.logger.error("Niepoprawny format danych!!");
                correctData = false;
            }
        }
        if (correctData && Integer.parseInt(spinnerPerson.getValue().toString()) == 0) {
            correctData = false;
        }
        if (correctData && nameInput.getText().replace(" ", "").length() == 0) {
            correctData = false;
            logger.debug("Pusta nazwa pokoju");
        }
        return correctData;
    }

    //sprawdzanie czy nie został przekroczony limit znaków
    private void checkLimit(String limit, int which) throws CharLimitException, IOException {
        switch (which) {
            case 1:
                if (nameInput.getText().length() >= Integer.parseInt(limit)) {
                    nameInput.setText(nameInput.getText().substring(0, Integer.parseInt(limit) - 1));
                    throw new CharLimitException(this.logger, limit);
                }
                break;
            case 2:
                if (priceValueInput.getText().length() >= Integer.parseInt(limit)) {
                    priceValueInput.setText(priceValueInput.getText().substring(0, Integer.parseInt(limit) - 1));
                    throw new CharLimitException(this.logger, limit);
                }
                break;
        }
    }

    //funkcja wywołująca wydarzenie dodawania pokoju
    private synchronized void fireAddingRoomEvent(boolean wasAdded) throws SQLException {
        RoomWasAddedEvent addedEv = new RoomWasAddedEvent(this, wasAdded);
        Iterator listenersIt = addingRoomListeners.iterator();
        while (listenersIt.hasNext()) {
            ((RoomWasAddedListener) listenersIt.next()).roomWasAdded(addedEv);
        }
    }

    //funkcja dodająca słuchaczy dodawania pokoju
    public synchronized void addAddingRoomListener(RoomWasAddedListener l) {
        addingRoomListeners.add(l);
    }

    public synchronized void removeAddingRoomListeners(RoomWasAddedListener l) {
        addingRoomListeners.remove(l);
    }

    @Override
    public void hotelWasAdded(HotelWasAddedEvent event) {
        if (event.getWasAdded()) {
            try {
                this.logger.info("Próba wykonania zapytania: " + "select idHotel,name from hotel where name<>'' and name is not null;");
                rs = stmt.executeQuery("select idHotel,name from hotel where name<>'' and name is not null;");
                hotelNameInput.removeAllItems();
                hotelNameInput.addItem("");
                while (rs.next()) {
                    hotelNameInput.addItem(rs.getString(1) + " " + rs.getString(2));
                }
            } catch (SQLException e) {
                this.logger.error(e);
            }
        }
    }

    @Override
    public void roomWasAdded(RoomWasAddedEvent event) {
        if (event.getWasAdded()) {
            try {
                properties = new Properties();
                is = new FileInputStream(propFileName);
                properties.load(is);
            } catch (IOException e) {
                this.logger.error(e);
            }
            addRoom();
            String[] takeIt = {"Room", "Adding"};
            takeIt = MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt, this.logger);
            this.logger.info("Dodano pokój");
            JOptionPane.showMessageDialog(null, takeIt[0] + " " + takeIt[1]);
            clearData();
        } else {
            this.logger.info("Niepoprawne dane!");
            String[] takeIt = {"Warning"};
            try {
                properties = new Properties();
                is = new FileInputStream(propFileName);
                properties.load(is);
                JOptionPane.showMessageDialog(null, MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt, this.logger)[0]);
            } catch (IOException e) {
                this.logger.error(e);
            }
        }
    }

    @Override
    public void hotelWasSelected(HotelWasSelectedEvent event) {
        hotelNameInput.setSelectedItem(event.getWasSelected());
    }

    @Override
    public void hotelWasDeleted(HotelWasDeletedEvent event) {
        if (event.getWasDeleted()) {
            clearData();
            try {
                this.logger.info("Próba wykonania zapytania: " + "select idHotel,name from hotel where name<>'' and name is not null;");
                rs = stmt.executeQuery("select idHotel,name from hotel where name<>'' and name is not null;");
                hotelNameInput.addItem("");
                hotelNameInput.removeAllItems();
                hotelNameInput.addItem("");
                while (rs.next()) {
                    hotelNameInput.addItem(rs.getString(1) + " " + rs.getString(2));
                }
            } catch (SQLException e) {
                this.logger.error(e);
            }
        }
    }

    @Override
    public void changeLanguage(LanguageEvent event) {

        this.paramsValue = MainPanel.ReadXmlLanguage(event.getLanguage(), this.paramsSearch, this.logger);
        //kolejny panel
        title.setText(paramsValue[0]);
        selectTag.setText(paramsValue[1]);
        personTag.setText(paramsValue[2]);
        priceTag.setText(paramsValue[3]);
        amenitiesTag.setText(paramsValue[5] + ":");
        addButton.setText(paramsValue[0]);

        chbIsBathroom.setText(paramsValue[4]);
        chbIsBar.setText(paramsValue[6]);
        chbIsTv.setText(paramsValue[7]);

        clearButton.setText(paramsValue[8]);
        countryResortTag.setText(paramsValue[9] + "/" + paramsValue[10]);
        nameTag.setText(paramsValue[11]);

        this.logger.info("Nastapila zmiana jezyka w tym panelu!");
    }

    @Override
    public void changePlaf(PlafEvent event) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        int plaf = event.getPlaf();
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
    }
}