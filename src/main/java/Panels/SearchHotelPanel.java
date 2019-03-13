package Panels;

import Events.*;
import Exceptions.HotelDoesntExistException;
import com.mysql.jdbc.Connection;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

public class SearchHotelPanel extends JComponent implements ActionListener, LanguageListener, PlafListener, HotelWasAddedListener, HotelWasDeletedListener {

    private static SearchHotelPanel instante = null;
    static String[] paramsValue;
    static String[] paramsSearch = {"SearchHotel", "SelectHotel", "Stars", "Country", "Resort", "EnterCountry", "EnterResort", "Amenities", "SwimmingPool", "Restaurant", "Pets", "Parking", "WiFi", "Clear", "Results", "Searcher", "Elevator"};

    private File propFile = new File("config.properties");
    static String[] paramsHotel = {"Id", "Hotel", "Country", "Resort", "Stars", "SwimmingPool", "Restaurant", "Pets", "Parking", "WiFi", "Description", "Delete", "Elevator", "Address"};
    JPanel search, showHotels;
    private JLayeredPane layer, layer2;
    private String idHotel;
    static JTabbedPane main = new JTabbedPane();
    private GridBagConstraints b = new GridBagConstraints();
    private GridBagConstraints c = new GridBagConstraints();
    private GridBagConstraints d = new GridBagConstraints();
    private GridBagLayout gbc, gbb, gbd;

    //components
    private static JCheckBox swimmingPoolChb;
    private static JCheckBox restaurantChb;
    private static JCheckBox petsChb;
    private static JCheckBox parkingChb;
    private static JCheckBox wifiChb;
    private static JCheckBox elevatorChb;
    private static JLabel lbl;
    private static JLabel hotelNameTag;
    private static JLabel starTag;
    private static JLabel countryTag;//napis countryTag
    private static JLabel resortTag;//napis resort
    private static JLabel amenitiesTag;//napis udogodnienia
    private static JComboBox countryNameInput;
    private static JComboBox resortNameInput;
    private static JComboBox hotelNameInput;

    private JTextPane descriptionArea;

    private static JButton searchButton;
    private static JButton clearButton;
    private static JButton deleteButton;
    private static JSpinner stars;
    private static JTable searchResult;
    private static SpinnerModel valueStar =
            new SpinnerNumberModel(0, //initial valueStar
                    0, //minimum value
                    5, //maximum value
                    1); //step

    //Połączanie z db
    private Connection con;
    private Statement stmt;
    private ResultSet resultSet;
    private static Logger logger;
    private static DefaultTableModel modelPolish;
    private static DefaultTableModel modelEnglish;
    private static Properties properties;
    private InputStream inputPropertiesStream;

    private ArrayList selectedHotelListener;
    private ArrayList deleteHotelListener;

    private static String previoush;
    private static String previousc;
    private static String previousr;

    private BufferedImage seaPicture;
    private BufferedImage treePicture;
    private BufferedImage mountainPicture;
    private ImageIcon icon = new ImageIcon();
    private JLabel picLabel;

    static int[] rowCol;

    private SearchHotelPanel(Connection con, Logger logger) throws IOException, SQLException {

        this.rowCol = new int[2];
        idHotel = new String();
        idHotel = "";
        selectedHotelListener = new ArrayList();
        deleteHotelListener = new ArrayList();
        properties = new Properties();
        inputPropertiesStream = new FileInputStream(propFile);
        properties.load(inputPropertiesStream);
        this.logger = logger;
        this.paramsValue = MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), this.paramsSearch, this.logger);
        stmt = con.createStatement();

        this.con = con;
        //budowa panelu
        search = new JPanel();
        layer = new JLayeredPane();
        layer2 = new JLayeredPane();
        showHotels = new JPanel();
        search.setLayout(gbc = new GridBagLayout());
        layer.setLayout(gbb = new GridBagLayout());
        layer2.setLayout(gbd = new GridBagLayout());

        //tabela z zakładki results
        modelPolish = new DefaultTableModel();
        modelEnglish = new DefaultTableModel();

        paramsValue = MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), this.paramsHotel, this.logger);
        if (properties.getProperty("DefaultLanguage") == "Polish") {
            modelPolish.addColumn(this.paramsValue[0].toString());
            modelPolish.addColumn(this.paramsValue[1].toString());
            modelPolish.addColumn(this.paramsValue[2].toString());
            modelPolish.addColumn(this.paramsValue[3].toString());
            modelPolish.addColumn(this.paramsValue[13].toString());
            modelPolish.addColumn(this.paramsValue[4].toString());
            modelPolish.addColumn(this.paramsValue[5].toString());
            modelPolish.addColumn(this.paramsValue[6].toString());
            modelPolish.addColumn(this.paramsValue[7].toString());
            modelPolish.addColumn(this.paramsValue[8].toString());
            modelPolish.addColumn(this.paramsValue[9].toString());
            modelPolish.addColumn(this.paramsValue[12].toString());
            modelPolish.addColumn(this.paramsValue[10].toString());
            searchResult = new JTable(modelPolish);
            searchResult.setModel(modelPolish);
        } else {
            modelEnglish.addColumn(this.paramsValue[0].toString());
            modelEnglish.addColumn(this.paramsValue[1].toString());
            modelEnglish.addColumn(this.paramsValue[2].toString());
            modelEnglish.addColumn(this.paramsValue[3].toString());
            modelEnglish.addColumn(this.paramsValue[13].toString());
            modelEnglish.addColumn(this.paramsValue[4].toString());
            modelEnglish.addColumn(this.paramsValue[5].toString());
            modelEnglish.addColumn(this.paramsValue[6].toString());
            modelEnglish.addColumn(this.paramsValue[7].toString());
            modelEnglish.addColumn(this.paramsValue[8].toString());
            modelEnglish.addColumn(this.paramsValue[9].toString());
            modelEnglish.addColumn(this.paramsValue[12].toString());
            modelEnglish.addColumn(this.paramsValue[10].toString());
            searchResult = new JTable(modelEnglish);
            searchResult.setModel(modelEnglish);
        }
        JScrollPane scrollPane = new JScrollPane(searchResult);
        deleteButton = new JButton();
        deleteButton.setEnabled(false);
        this.deleteButton.addActionListener(this);
        descriptionArea = new JTextPane();
        descriptionArea.setEnabled(false);
        searchResult.setPreferredScrollableViewportSize(new Dimension(Integer.parseInt(properties.getProperty("ScrollWidth")), Integer.parseInt(properties.getProperty("ScrollHeight"))));
        scrollPane.createHorizontalScrollBar();
        scrollPane.createVerticalScrollBar();
        searchResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        searchResult.setLayout(gbd);
        layer2.add(scrollPane);

        scrollPane.setPreferredSize(new Dimension(Integer.parseInt(properties.getProperty("ScrollWidth")), Integer.parseInt(properties.getProperty("ScrollHeight"))));
        descriptionArea.setFont(new Font(descriptionArea.getFont().getFontName(), descriptionArea.getFont().getStyle(), 15));
        descriptionArea.setSelectedTextColor(Color.BLACK);
        descriptionArea.setPreferredSize(new Dimension(350, 190));

        layer2.setVisible(true);
        layer2.setPreferredSize(new Dimension(Integer.parseInt(properties.getProperty("ScrollWidth")), Integer.parseInt(properties.getProperty("ScrollHeight"))));

        //napis
        c.insets.set(0, 0, 20, 0);
        this.lbl = new JLabel();
        lbl.setFont(new Font(lbl.getFont().getFontName(), lbl.getFont().getStyle(), 20));
        lbl.setMaximumSize(new Dimension(400, 40));
        lbl.setMinimumSize(new Dimension(400, 40));
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        layer.add(lbl, c);

        c.insets.set(0, 0, 5, 0);
        this.hotelNameTag = new JLabel();
        hotelNameTag.setMaximumSize(new Dimension(100, 20));
        hotelNameTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 0;
        c.gridy = 1;
        layer.add(hotelNameTag, c);

        c.insets.set(0, 0, 5, 250);
        this.starTag = new JLabel();
        starTag.setMaximumSize(new Dimension(100, 20));
        starTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 1;
        c.gridy = 1;
        layer.add(starTag, c);

        c.insets.set(0, 0, 10, 0);
        this.hotelNameInput = new JComboBox();
        c.gridx = 0;
        c.gridy = 2;
        hotelNameInput.setMinimumSize(new Dimension(200, 30));
        hotelNameInput.setMaximumSize(new Dimension(200, 30));
        layer.add(hotelNameInput, c);

        this.stars = new JSpinner(valueStar);
        c.gridx = 1;
        c.insets.set(0, 0, 10, 300);
        stars.setMaximumSize(new Dimension(50, 30));
        stars.setMinimumSize(new Dimension(50, 30));
        layer.add(stars, c);

        c.insets.set(0, 190, 5, 0);
        c.ipadx = 200;
        this.countryTag = new JLabel();//countryTag
        countryTag.setMaximumSize(new Dimension(200, 20));
        countryTag.setMinimumSize(new Dimension(200, 20));
        c.gridx = 0;
        c.gridy = 3;
        layer.add(countryTag, c);

        this.resortTag = new JLabel();
        c.gridy = 3;
        c.gridx = 1;
        c.insets.set(0, 70, 5, 0);
        resortTag.setMaximumSize(new Dimension(250, 20));
        resortTag.setMinimumSize(new Dimension(250, 20));
        layer.add(resortTag, c);

        c.ipadx = 150;
        c.insets.set(0, 0, 30, 0);
        this.countryNameInput = new JComboBox();
        countryNameInput.setMaximumSize(new Dimension(100, 25));
        countryNameInput.setMinimumSize(new Dimension(100, 25));
        c.gridx = 0;
        c.gridy = 4;
        layer.add(countryNameInput, c);

        c.insets.set(0, 0, 30, 400);
        this.resortNameInput = new JComboBox();
        resortNameInput.setMaximumSize(new Dimension(100, 25));
        resortNameInput.setMinimumSize(new Dimension(100, 25));
        c.gridx = 1;
        layer.add(resortNameInput, c);

        c.insets.set(0, 250, 30, 0);
        amenitiesTag = new JLabel();
        amenitiesTag.setFont(new Font(lbl.getFont().getFontName(), lbl.getFont().getStyle(), 16));
        amenitiesTag.setMaximumSize(new Dimension(100, 20));
        amenitiesTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 0;
        c.gridy = 5;
        layer.add(amenitiesTag, c);

//pobieranie tła
        treePicture = ImageIO.read(new File(properties.getProperty("TreePicture")));
        seaPicture = ImageIO.read(new File(properties.getProperty("SeaPicture")));
        mountainPicture = ImageIO.read(new File(properties.getProperty("MountainPicture")));

        c.insets.set(0, 0, 30, 0);
        picLabel = new JLabel(icon = new ImageIcon());
        d.ipady = 1;
        d.ipadx = 1;
        showHotels.add(layer2, d);
        showHotels.add(picLabel, d);
        showHotels.add(descriptionArea);
        showHotels.add(deleteButton);
        //koniec tła
        JLayeredPane h = new JLayeredPane();
        GridLayout g;
        c.gridx = 0;
        c.gridy = 1;
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
        swimmingPoolChb.setMinimumSize(new Dimension(100, 20));
        restaurantChb.setMinimumSize(new Dimension(100, 20));
        petsChb.setMinimumSize(new Dimension(100, 20));
        parkingChb.setMinimumSize(new Dimension(100, 20));
        wifiChb.setMinimumSize(new Dimension(100, 20));
        elevatorChb.setMinimumSize(new Dimension(100, 20));
        h.setPreferredSize(new Dimension(300, 100));
        h.add(swimmingPoolChb);
        h.add(restaurantChb);
        h.add(petsChb);
        h.add(parkingChb);
        h.add(wifiChb);
        h.add(elevatorChb);
        c.gridy = 8;
        c.gridx = 0;
        c.insets.set(0, 0, 30, 250);
        layer.add(h, c);

        c.gridy = 11;
        c.gridx = 0;
        this.searchButton = new JButton();
        searchButton.setPreferredSize(new Dimension(60, 20));
        this.searchButton.addActionListener(this);
        layer.add(searchButton, c);

        clearButton = new JButton();
        c.gridy = 12;
        c.gridx = 0;
        clearButton.setPreferredSize(new Dimension(60, 20));
        layer.add(clearButton, c);
        clearButton.addActionListener(this);

        b.gridx = 1;
        b.gridy = 1;
        search.add(layer, b);
        search.add(picLabel, b);

        searchResult.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                int row = searchResult.rowAtPoint(evt.getPoint());
                int col = searchResult.columnAtPoint(evt.getPoint());
                deleteButton.setEnabled(false);
                if (searchResult.getModel().getValueAt(row, col) != null)
                    descriptionArea.setText(searchResult.getModel().getValueAt(row, col).toString());
                if (col == 0) {
                    fireSelectedHotelEvent(searchResult.getModel().getValueAt(row, col).toString() + " " + searchResult.getModel().getValueAt(row, col + 1).toString());
                    deleteButton.setEnabled(true);
                    descriptionArea.setText("");
                    setIdHotel(searchResult.getModel().getValueAt(row, col).toString());
                }
            }

        });

        setColumnsSize();
        refreshComboBoxLists();
        main.add(search);
        main.add(showHotels);
    }


    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == searchButton) {
            descriptionArea.setText("");
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    displayHotels();
                    return null;
                }

                @Override
                protected void done() {

                }
            };
            worker.execute();
            deleteButton.setEnabled(false);
            main.setSelectedIndex(1);
            this.logger.info("Wyswietlono liste hoteli");

        } else if (source == clearButton) {
            clearData();
            this.logger.info("Wyczyszczono formularze");
        } else if (source == deleteButton) {

            this.logger.info("Usuwanie hotelu");
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fireDeleteHotelEvent(deleteHotel(), getIdHotel());
                    refreshComboBoxLists();
                    return null;
                }

                @Override
                protected void done() {

                }
            };
            worker.execute();

            deleteButton.setEnabled(false);
        }

    }

    //funkcja czyszczaca formatki
    private void clearData() {
        countryNameInput.setSelectedItem("");
        resortNameInput.setSelectedItem("");
        swimmingPoolChb.setSelected(false);
        restaurantChb.setSelected(false);
        petsChb.setSelected(false);
        parkingChb.setSelected(false);
        wifiChb.setSelected(false);
        elevatorChb.setSelected(false);
        stars.setValue(0);
        while (modelPolish.getRowCount() > 0) {
            modelPolish.removeRow(0);
        }
        while (modelEnglish.getRowCount() > 0) {
            modelEnglish.removeRow(0);
        }
        hotelNameInput.setSelectedItem("");
        descriptionArea.setText("");

    }

    //zmiana języka
    @Override
    public void changeLanguage(LanguageEvent event) {
        Vector<String> par = new Vector<String>();
        this.paramsValue = MainPanel.ReadXmlLanguage(event.getLanguage(), this.paramsSearch, this.logger);
        //kolejny panel
        lbl.setText(paramsValue[0]);
        hotelNameTag.setText(paramsValue[1]);
        starTag.setText(paramsValue[2]);
        countryTag.setText(paramsValue[3]);
        amenitiesTag.setText(paramsValue[7] + ":");
        searchButton.setText(paramsValue[0]);
        resortTag.setText(paramsValue[4]);
        swimmingPoolChb.setText(paramsValue[8]);
        restaurantChb.setText(paramsValue[9]);
        petsChb.setText(paramsValue[10]);
        parkingChb.setText(paramsValue[11]);
        wifiChb.setText(paramsValue[12]);
        elevatorChb.setText(paramsValue[16]);
        clearButton.setText(paramsValue[13]);
        main.setTitleAt(0, paramsValue[15]);
        main.setTitleAt(1, paramsValue[14]);
        paramsValue = MainPanel.ReadXmlLanguage(event.getLanguage(), this.paramsHotel, this.logger);
        if (event.getLanguage() == "English") {
            Vector<String> data = modelEnglish.getDataVector();
            modelEnglish = new DefaultTableModel();
            modelEnglish.addColumn(paramsValue[0].toString());
            modelEnglish.addColumn(paramsValue[1].toString());
            modelEnglish.addColumn(paramsValue[2].toString());
            modelEnglish.addColumn(paramsValue[3].toString());
            modelEnglish.addColumn(paramsValue[13].toString());
            modelEnglish.addColumn(paramsValue[4].toString());
            modelEnglish.addColumn(paramsValue[5].toString());
            modelEnglish.addColumn(paramsValue[6].toString());
            modelEnglish.addColumn(paramsValue[7].toString());
            modelEnglish.addColumn(paramsValue[8].toString());
            modelEnglish.addColumn(paramsValue[9].toString());
            modelEnglish.addColumn(paramsValue[12].toString());
            modelEnglish.addColumn(paramsValue[10].toString());
            for (int i = 0; i < modelEnglish.getColumnCount(); i++) {
                par.add(modelEnglish.getColumnName(i));
            }
            modelEnglish.setDataVector(data, par);
            searchResult.setModel(modelEnglish);
        } else {
            Vector<String> data = modelPolish.getDataVector();
            modelPolish = new DefaultTableModel();
            modelPolish.addColumn(paramsValue[0].toString());
            modelPolish.addColumn(paramsValue[1].toString());
            modelPolish.addColumn(paramsValue[2].toString());
            modelPolish.addColumn(paramsValue[3].toString());
            modelPolish.addColumn(paramsValue[13].toString());
            modelPolish.addColumn(paramsValue[4].toString());
            modelPolish.addColumn(paramsValue[5].toString());
            modelPolish.addColumn(paramsValue[6].toString());
            modelPolish.addColumn(paramsValue[7].toString());
            modelPolish.addColumn(paramsValue[8].toString());
            modelPolish.addColumn(paramsValue[9].toString());
            modelPolish.addColumn(paramsValue[12].toString());
            modelPolish.addColumn(paramsValue[10].toString());

            for (int i = 0; i < modelPolish.getColumnCount(); i++) {
                par.add(modelPolish.getColumnName(i));
            }
            modelPolish.setDataVector(data, par);
            deleteButton.setText(paramsValue[11]);
            searchResult.setModel(modelPolish);
        }
        setColumnsSize();
        this.logger.info("Nastapila zmiana jezyka w tym panelu!");
        descriptionArea.setText("");
    }


    //ustawianie rozmiaru kolumn
    void setColumnsSize() {
        searchResult.getColumn(searchResult.getColumnName(0)).setPreferredWidth(30);
        searchResult.getColumn(searchResult.getColumnName(1)).setPreferredWidth(80);
        searchResult.getColumn(searchResult.getColumnName(2)).setPreferredWidth(80);
        searchResult.getColumn(searchResult.getColumnName(3)).setPreferredWidth(80);
        searchResult.getColumn(searchResult.getColumnName(4)).setPreferredWidth(80);
        searchResult.getColumn(searchResult.getColumnName(5)).setPreferredWidth(40);
        searchResult.getColumn(searchResult.getColumnName(6)).setPreferredWidth(40);
        searchResult.getColumn(searchResult.getColumnName(7)).setPreferredWidth(40);
        searchResult.getColumn(searchResult.getColumnName(8)).setPreferredWidth(40);
        searchResult.getColumn(searchResult.getColumnName(9)).setPreferredWidth(40);
        searchResult.getColumn(searchResult.getColumnName(10)).setPreferredWidth(40);
        searchResult.getColumn(searchResult.getColumnName(11)).setPreferredWidth(40);
        searchResult.getColumn(searchResult.getColumnName(12)).setPreferredWidth(200);
    }

    //odswiezanie listy hoteli,miast,krajów
    public void refreshComboBoxLists() {
        String queryPart = "";
        //tworzenie zapytania--> pobranie
        if (hotelNameInput.getSelectedItem() != null) { //zeby nie zostalo to wykonane przy pierwszym razie bo nullpointer exception;
            previoush = hotelNameInput.getSelectedItem().toString();
            previousc = countryNameInput.getSelectedItem().toString();
            previousr = resortNameInput.getSelectedItem().toString();
        }
        //czyszcze liste
        hotelNameInput.removeAllItems();
        hotelNameInput.addItem("");
        try {
            this.logger.info("Próba wykonania zapytania:" + "select distinct name from hotel where name is not null " + queryPart);
            this.resultSet = this.stmt.executeQuery("select distinct name from hotel where name is not null " + queryPart);
            while (resultSet.next()) {
                hotelNameInput.addItem(resultSet.getString(1));
                if (resultSet.getString(1) == this.previoush) {
                    hotelNameInput.setSelectedItem(this.previoush);
                }
            }
        } catch (SQLException e) {
            this.logger.error("Błąd pobierania nazw hotelu!");
        }

        //COUNTRY
        countryNameInput.removeAllItems();
        countryNameInput.addItem("");
        try {
            this.logger.info("Próba wykonania zapytania:" + "select distinct country from hotel where country is not null" + queryPart);
            this.resultSet = this.stmt.executeQuery("select distinct country from hotel where country is not null" + queryPart);
            while (resultSet.next()) {
                countryNameInput.addItem(resultSet.getString(1));
                if (resultSet.getString(1) == this.previousc) {
                    countryNameInput.setSelectedItem(this.previousc);
                }
            }
        } catch (SQLException e) {
            this.logger.error("Błąd pobierania nazw krajów!"+e);
        }
        //RESORT
        resortNameInput.removeAllItems();
        resortNameInput.addItem("");
        try {
            this.logger.info("Próba wykonania zapytania:" + "select distinct resort from hotel where resort is not null " + queryPart);
            this.resultSet = this.stmt.executeQuery("select distinct resort from hotel where resort is not null " + queryPart);
            while (resultSet.next()) {
                resortNameInput.addItem(resultSet.getString(1));
                if (resultSet.getString(1) == this.previousr) {
                    resortNameInput.setSelectedItem(this.previousr);
                }
            }
        } catch (SQLException e) {
            this.logger.error("Błąd pobierania nazw miejscowości!");
        }
    }


    //wyszukiwanie hoteli
    public void displayHotels() {
        boolean isEnd = false;
        String query = "select idHotel,name,country,resort,stars,swimmingPool,restaurant,pets,parking,wifi,elevator,description,description2,address from hotel  ";
        String queryPart = "where ";
        if (hotelNameInput.getSelectedItem() != "") {
            queryPart += "name='" + hotelNameInput.getSelectedItem() + "'";
            isEnd = true;
        }
        if (Integer.parseInt(stars.getValue().toString()) > 0) {
            if (!isEnd) {
                queryPart += "stars=" + stars.getValue().toString() + " ";
                isEnd = true;
            } else {
                queryPart += "and stars=" + stars.getValue().toString() + " ";
            }
        }
        if (countryNameInput.getSelectedItem() != "") {
            if (!isEnd) {
                queryPart += "country='" + countryNameInput.getSelectedItem().toString() + "' ";
                isEnd = true;
            } else {
                queryPart += "and country='" + countryNameInput.getSelectedItem().toString() + "' ";
            }
        }
        if (resortNameInput.getSelectedItem() != "") {
            if (!isEnd) {
                queryPart += "resort='" + resortNameInput.getSelectedItem().toString() + "' ";
                isEnd = true;
            } else {
                queryPart += "and resort='" + resortNameInput.getSelectedItem().toString() + "' ";
            }
        }

        if (swimmingPoolChb.isSelected()) {
            if (!isEnd) {
                queryPart += "swimmingPool='+'";
                isEnd = true;
            } else {
                queryPart += "and swimmingPool='+'";
            }
        }
        if (restaurantChb.isSelected()) {
            if (!isEnd) {
                queryPart += "restaurant='+'";
                isEnd = true;
            } else {
                queryPart += "and restaurant='+'";
            }
        }
        if (petsChb.isSelected()) {
            if (!isEnd) {
                queryPart += "pets='+'";
                isEnd = true;
            } else {
                queryPart += "and pets='+'";
            }
        }
        if (parkingChb.isSelected()) {
            if (!isEnd) {
                queryPart += "parking='+'";
                isEnd = true;
            } else {
                queryPart += "and parking='+'";
            }
        }
        if (wifiChb.isSelected()) {
            if (!isEnd) {
                queryPart += "wifi='+'";
                isEnd = true;
            } else {
                queryPart += "and wifi='+'";
            }
        }
        if (elevatorChb.isSelected()) {
            if (!isEnd) {
                queryPart += "elevator='+'";
                isEnd = true;
            } else {
                queryPart += "and elevator='+'";
            }
        }

        if (queryPart == "where ") {
            queryPart = "";
        }
        query += queryPart;
        try {
            this.resultSet = this.stmt.executeQuery(query);
            this.logger.info("Zapytanie\n" + query + "\n zostalo wykonane");
            while (modelPolish.getRowCount() > 0) {
                modelPolish.removeRow(0);
            }
            while (modelEnglish.getRowCount() > 0) {
                modelEnglish.removeRow(0);
            }
            while (resultSet.next()) {
                modelPolish.addRow(new String[]{resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(14), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9), resultSet.getString(10), resultSet.getString(11), resultSet.getString(12)});
                modelEnglish.addRow(new String[]{resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(14), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9), resultSet.getString(10), resultSet.getString(11), resultSet.getString(13)});
            }
            properties = new Properties();
            inputPropertiesStream = new FileInputStream(propFile);
            properties.load(inputPropertiesStream);

            if (properties.getProperty("DefaultLanguage").toString().equals("English")) {
                searchResult.setModel(modelEnglish);
            } else {
                searchResult.setModel(modelPolish);
            }
            setColumnsSize();

        } catch (SQLException e1) {
            this.logger.error("Blad przy wykonywaniu zapytania!\n" + e1);
        } catch (FileNotFoundException e) {
            this.logger.error(e);
        } catch (IOException e) {
            this.logger.error(e);
        }
    }

    //SINGLETON
    public static synchronized SearchHotelPanel createSearchHotelPanel(Connection con, Logger logger1) {
        Logger logger = logger1;
        if (instante == null) {
            try {
                logger.info("Próba utworzenia panelu SearchHotelPanel");
                instante = new SearchHotelPanel(con, logger);
            } catch (IOException e) {
                logger.error(e);
            } catch (SQLException e) {
               logger.error(e);
            }
            return instante;
        } else {
            logger.error("SearchRoomPanel już został wcześniej utworzony!!!1");

            return null;
        }

    }

    public synchronized void addSelectedHotelListener(HotelWasSelectedListener l) {
        selectedHotelListener.add(l);
    }

    public synchronized void removeSelectedHotelListener(HotelWasDeletedListener l) {
        selectedHotelListener.remove(l);
    }

    public synchronized void addDeleteHotelListener(HotelWasDeletedListener l) {
        deleteHotelListener.add(l);
    }

    public synchronized void removeDeleteHotelListener(HotelWasSelectedListener l) {
        deleteHotelListener.remove(l);
    }


    //wyzwalanie eventu, ze hotel został wybrany
    private synchronized void fireSelectedHotelEvent(final String wasSelected) {

        HotelWasSelectedEvent addedEv = new HotelWasSelectedEvent(this, wasSelected);
        Iterator listenersIt = selectedHotelListener.iterator();
        while (listenersIt.hasNext()) {
            ((HotelWasSelectedListener) listenersIt.next()).hotelWasSelected(addedEv);
        }

    }

    //wyzwalanie eventu, ze hotel zostal usunięty
    private synchronized void fireDeleteHotelEvent(final boolean wasDeleted, final String idHotel) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                HotelWasDeletedEvent addedEv = new HotelWasDeletedEvent(this, wasDeleted, idHotel);
                Iterator listenersIt = deleteHotelListener.iterator();
                while (listenersIt.hasNext()) {
                    ((HotelWasDeletedListener) listenersIt.next()).hotelWasDeleted(addedEv);
                }
                return null;
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
    }

    //zmiana plafa
    @Override
    public void changePlaf(final PlafEvent event) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        int plaf = event.getPlaf();
        switch (plaf) {
            case 1:
                UIManager.setLookAndFeel(properties.getProperty("Plaf1"));
                SwingUtilities.updateComponentTreeUI(main);
                b.gridx = 1;
                b.gridy = 1;
                picLabel = new JLabel(new ImageIcon(seaPicture));
                search.remove(0);
                search.remove(0);
                search.add(layer, b);
                search.add(picLabel, b);
                break;

            case 2:
                UIManager.setLookAndFeel(properties.getProperty("Plaf2"));
                SwingUtilities.updateComponentTreeUI(main);
                b.gridx = 1;
                b.gridy = 1;
                picLabel = new JLabel(new ImageIcon(treePicture));
                search.remove(0);
                search.remove(0);
                search.add(layer, b);
                search.add(picLabel, b);
                break;

            case 3:
                UIManager.setLookAndFeel(properties.getProperty("Plaf3"));
                SwingUtilities.updateComponentTreeUI(main);
                b.gridx = 1;
                b.gridy = 1;
                picLabel = new JLabel(new ImageIcon(mountainPicture));
                search.remove(0);
                search.remove(0);
                search.add(layer, b);
                search.add(picLabel, b);
                break;

        }


    }

    //reakcja na dodanie hotelu
    @Override
    public void hotelWasAdded(final HotelWasAddedEvent event) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (event.getWasAdded()) {
                    refreshComboBoxLists();
                }
                return null;
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
    }

    //reakcja na usniecie hotelu
    @Override
    public void hotelWasDeleted(final HotelWasDeletedEvent event) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (event.getWasDeleted()) {
                    refreshComboBoxLists();
                }
                return null;
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
    }

    public void setIdHotel(String idHotel) {
        this.idHotel = idHotel;
    }

    public String getIdHotel() {
        return this.idHotel;
    }

    //usuwanie hotelu
    private boolean deleteHotel() throws HotelDoesntExistException, IOException {
        boolean ifExists = false;
        try {
            this.logger.info("Próba usunięcia hotelu");
            this.resultSet = this.stmt.executeQuery("select count(1) from hotel where idHotel=" + this.idHotel);
            resultSet.next();
            if (this.resultSet.getInt(1) == 0) {
                ifExists = false;
                throw new HotelDoesntExistException(this.logger);
            } else {
                ifExists = true;
                String query = "delete from room where idHotel=" + this.idHotel;
                stmt.executeUpdate(query);
                query = "delete from hotel where idHotel=" + this.idHotel;
                stmt.executeUpdate(query);

                int i = 0;
                while (i < modelPolish.getRowCount()) {
                    if (modelPolish.getValueAt(i, 0) == this.idHotel) {
                        modelPolish.removeRow(i);
                        i = modelPolish.getRowCount();
                    }
                    searchResult.validate();
                    i++;
                }
                i = 0;
                while (i < modelEnglish.getRowCount()) {
                    if (modelEnglish.getValueAt(i, 0) == this.idHotel) {
                        modelEnglish.removeRow(i);
                        i = modelEnglish.getRowCount();
                        i--;
                    }
                    i++;
                    searchResult.validate();
                }
            }
        } catch (SQLException e) {
            this.logger.error("Błąd usuwania hotelu");
        }
        return ifExists;
    }
}