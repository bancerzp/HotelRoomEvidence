package Panels;

import Events.*;
import Exceptions.PriceMinMaxException;
import Exceptions.RoomDoesntExistsException;
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
import java.util.Properties;
import java.util.Vector;

public class SearchRoomPanel extends Component implements ActionListener, LanguageListener, PlafListener, HotelWasAddedListener, HotelWasSelectedListener, HotelWasDeletedListener, RoomWasAddedListener {

    private static SearchRoomPanel instante = null;
    private static String[] paramsValue;
    private static String[] paramsSearch = {"SearchRoom", "SelectHotel", "RoomSize", "Country", "Resort", "Price", "Bathroom", "Amenities", "LiquirCabinet", "Tv", "Clear", "Results", "Searcher", "MinimumRoomPrice", "MaximumRoomPrice"};
    private File propFileName = new File("config.properties");
    private static String[] paramsRoomTable = {"Id", "Room", "RoomName", "Hotel", "Country", "Resort", "RoomSize", "Price", "Bathroom", "LiquirCabinet", "Tv", "Delete"};
    JPanel search, showRooms;
    JLayeredPane layer;

    static JTabbedPane main = new JTabbedPane();
    private GridBagConstraints c = new GridBagConstraints();
    private GridBagConstraints b = new GridBagConstraints();
    private GridBagLayout gbc, gbb;

    private static JCheckBox chbIsBathroom;
    private static JCheckBox chbIsBar;
    private static JCheckBox chbIsTv;
    private static JLabel title;
    private static JLabel selectTag;
    private static JLabel personTag;
    private static JLabel countryTag;//napis country
    private static JLabel resortTag;//napis resort
    private static JLabel amenitiesTag;//napis udogodnienia
    private static JComboBox countryNameInput;
    private static JComboBox resortNameInput;
    private static JComboBox hotelNameInput;
    private static JLabel priceMinTag;//napis pricemin
    private static JLabel priceMaxTag;//napis pricemax
    private static JTextField priceMinValueInput;
    private static JTextField priceMaxValueInput;
    private static JTable searchResult;

    private static JButton searchButton;
    private static JButton clearButton;
    private static JButton deleteButton;
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
    private String idRoom;

    private SearchRoomPanel(Connection con, Logger logger) throws IOException, SQLException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        idRoom = new String();
        this.logger = logger;
        this.con = con;
        search = new JPanel();
        showRooms = new JPanel();
        layer = new JLayeredPane();
        search.setLayout(gbc = new GridBagLayout());
        layer.setLayout(gbb = new GridBagLayout());

        properties = new Properties();
        is = new FileInputStream(propFileName);
        properties.load(is);
        model = new DefaultTableModel();
        deleteButton = new JButton();
        paramsValue = MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), paramsRoomTable, this.logger);
        model.addColumn(paramsValue[0].toString() + paramsValue[1].toString());
        model.addColumn(paramsValue[2]);
        model.addColumn(paramsValue[0] + paramsValue[3]);
        model.addColumn(paramsValue[4]);
        model.addColumn(paramsValue[5]);
        model.addColumn(paramsValue[6]);
        model.addColumn(paramsValue[7]);
        model.addColumn(paramsValue[8]);
        model.addColumn(paramsValue[9]);
        model.addColumn(paramsValue[10]);

        searchResult = new JTable(model);
        searchResult.setModel(model);
        searchResult.add(deleteButton);
        deleteButton.addActionListener(this);

        JScrollPane scrollPane = new JScrollPane(searchResult);

        // tworzenie tabeli do wyświetlenia rekordów
        searchResult.setPreferredScrollableViewportSize(new Dimension(Integer.parseInt(properties.getProperty("ScrollWidth")), Integer.parseInt(properties.getProperty("ScrollHeight"))));
        scrollPane.createHorizontalScrollBar();
        scrollPane.createVerticalScrollBar();
        searchResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        showRooms.add(scrollPane);
        showRooms.setVisible(true);
        showRooms.setPreferredSize(new Dimension(Integer.parseInt(properties.getProperty("ScrollWidth")), Integer.parseInt(properties.getProperty("ScrollHeight"))));
        showRooms.add(deleteButton);
        deleteButton.addActionListener(this);
        paramsValue = MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), paramsSearch, this.logger);
        //tworzenie listy pokoi do wyboru
        int i = 1;
        stmt = con.createStatement();

        treePicture = ImageIO.read(new File(properties.getProperty("TreePicture")));
        seaPicture = ImageIO.read(new File(properties.getProperty("SeaPicture")));
        mountainPicture = ImageIO.read(new File(properties.getProperty("MountainPicture")));
        c.insets.set(0, 0, 20, 0);
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
        c.insets.set(0, 0, 5, 0);
        this.selectTag = new JLabel();
        selectTag.setMaximumSize(new Dimension(100, 20));
        selectTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 0;
        c.gridy = 1;
        layer.add(selectTag, c);
        c.insets.set(0, 0, 5, 100);
        this.personTag = new JLabel();
        personTag.setMaximumSize(new Dimension(100, 20));
        personTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 1;
        c.gridy = 1;
        layer.add(personTag, c);


        c.insets.set(0, 0, 10, 0);
        this.hotelNameInput = new JComboBox();
        c.gridx = 0;
        c.gridy = 2;
        hotelNameInput.setMinimumSize(new Dimension(200, 30));
        hotelNameInput.setMaximumSize(new Dimension(200, 30));
        layer.add(hotelNameInput, c);

        this.spinnerPerson = new JSpinner(value);
        c.gridx = 1;
        c.insets.set(0, 0, 10, 150);
        spinnerPerson.setMaximumSize(new Dimension(50, 30));
        spinnerPerson.setMinimumSize(new Dimension(50, 30));
        layer.add(spinnerPerson, c);


        c.insets.set(0, 100, 5, 0);
        c.ipadx = 100;
        this.countryTag = new JLabel();//country
        countryTag.setMaximumSize(new Dimension(200, 20));
        countryTag.setMinimumSize(new Dimension(200, 20));
        c.gridx = 0;
        c.gridy = 3;
        layer.add(countryTag, c);

        this.resortTag = new JLabel();
        c.gridy = 3;
        c.gridx = 1;
        resortTag.setMaximumSize(new Dimension(250, 20));
        resortTag.setMinimumSize(new Dimension(250, 20));
        layer.add(resortTag, c);


        c.ipadx = 150;
        c.insets.set(0, 0, 45, 70);
        this.countryNameInput = new JComboBox();
        countryNameInput.setMaximumSize(new Dimension(100, 25));
        countryNameInput.setMinimumSize(new Dimension(100, 25));
        c.gridx = 0;
        c.gridy = 4;
        layer.add(countryNameInput, c);

        c.insets.set(0, 0, 45, 170);
        this.resortNameInput = new JComboBox();


        resortNameInput.setMaximumSize(new Dimension(100, 25));
        resortNameInput.setMinimumSize(new Dimension(100, 25));
        c.gridx = 1;
        layer.add(resortNameInput, c);

        priceMinTag = new JLabel();//napis resort
        priceMaxTag = new JLabel();//napis udogodnienia
        priceMinValueInput = new JTextField();
        priceMaxValueInput = new JTextField();

        c.gridy = 5;
        c.gridx = 0;
        c.insets.set(0, 65, 4, 0);
        layer.add(priceMinTag, c);
        c.gridx = 1;
        c.insets.set(0, 0, 4, 25);
        layer.add(priceMaxTag, c);
        c.insets.set(0, 0, 4, 100);
        c.gridy = 6;
        c.gridx = 0;
        layer.add(priceMinValueInput, c);
        c.gridx = 1;
        c.insets.set(0, 0, 4, 150);
        layer.add(priceMaxValueInput, c);
        c.insets.set(0, 0, 30, 0);

        c.insets.set(0, 250, 30, 0);
        amenitiesTag = new JLabel();
        amenitiesTag.setFont(new Font(title.getFont().getFontName(), title.getFont().getStyle(), 16));
        amenitiesTag.setMaximumSize(new Dimension(100, 20));
        amenitiesTag.setMinimumSize(new Dimension(100, 20));
        c.gridx = 0;
        c.gridy = 7;
        layer.add(amenitiesTag, c);

        GridLayout g = new GridLayout();
        h.setLayout(g = new GridLayout());
        g.setColumns(2);
        g.setRows(3);
        this.chbIsBathroom = new JCheckBox();
        this.chbIsBar = new JCheckBox();
        this.chbIsTv = new JCheckBox();
        c.gridwidth = 500;
        chbIsBathroom.setMinimumSize(new Dimension(100, 20));
        chbIsBar.setMinimumSize(new Dimension(100, 20));
        chbIsTv.setMinimumSize(new Dimension(100, 20));
        h.setPreferredSize(new Dimension(300, 100));
        h.add(chbIsBathroom);
        h.add(chbIsBar);
        h.add(chbIsTv);
        c.gridy = 12;
        c.gridx = 0;
        c.insets.set(0, 0, 30, 150);


        c.gridy = 13;
        c.gridx = 0;
        this.searchButton = new JButton();
        searchButton.setPreferredSize(new Dimension(60, 20));
        this.searchButton.addActionListener(this);
        layer.add(searchButton, c);

        b.gridx = 1;
        b.gridy = 1;

        search.add(layer, b);
        search.add(picLabel, b);

        clearButton = new JButton();
        c.gridy = 14;
        c.gridx = 0;
        clearButton.setPreferredSize(new Dimension(60, 20));
        layer.add(clearButton, c);
        clearButton.addActionListener(this);
        layer.setBackground(Color.MAGENTA);
        main.add(search);
        deleteButton.setEnabled(false);
        main.add(showRooms);
        searchResult.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = searchResult.rowAtPoint(evt.getPoint());
                int col = searchResult.columnAtPoint(evt.getPoint());
                deleteButton.setEnabled(false);
                if (col == 0) {
                    deleteButton.setEnabled(true);
                    setIdRoom(searchResult.getModel().getValueAt(row, col).toString());
                }
            }
        });
        SwingWorker<Void, Void> worker2 = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                setHotelCountryResort();
                return null;
            }

            @Override
            protected void done() {
            }
        };
        worker2.execute();
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == searchButton) {
            try {
                if (areDataValide()) {
                    SwingWorker<Void, Void> worker1 = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            displayRooms();
                            return null;
                        }

                        @Override
                        protected void done() {
                        }
                    };
                    worker1.execute();

                    main.setSelectedIndex(1);
                    this.logger.info("Wyswietlanie wynikow wyszukiwania!");
                }
            } catch (PriceMinMaxException e1) {
                this.logger.error(e1);
            } catch (IOException e1) {
                this.logger.error(e1);
            }
        } else if (source == clearButton) {
            clearData();
            this.logger.info("Czyszczenie formularzy!");
        } else if (source == deleteButton) {


            if (deleteButton.isEnabled()) {
                this.logger.info("Usuwanie pokoju");
                SwingWorker<Void, Void> workerr = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws RoomDoesntExistsException {
                        try {
                            deleteRoom();
                        } catch (IOException e1) {
                            logger.error(e1);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {

                    }
                };
                workerr.execute();
                deleteButton.setEnabled(false);
            }
        }
    }

    //SINGLETON
    public static synchronized SearchRoomPanel createSearchRoomPanel(Connection con, Logger logger) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (instante == null) {
            try {
                logger.info("Próba utworzenia instancji SearchRoomPanel");
                instante = new SearchRoomPanel(con, logger);
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

    //sprawdzanie poprawności wprowadzonych danych--> czy cena jest liczbą, i czy min nie jest >niż max
    private boolean areDataValide() throws IOException, PriceMinMaxException {
        String[] takeIt = {"NumberFormat"};
        boolean areValidate = true;
        String min = priceMinValueInput.getText().replace(" ", "");
        String max = priceMaxValueInput.getText().replace(" ", "");

        if (min.length() > 0) {
            try {
                this.logger.info("Sprawdzanie formatu ceny minimalnej");
                Integer.parseInt(min);
            } catch (Exception e) {
                this.logger.error("Niepoprawny format ceny minimalnej");
                areValidate = false;
                properties = new Properties();
                try {
                    is = new FileInputStream(propFileName);
                    properties.load(is);
                    JOptionPane.showMessageDialog(null, MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt, this.logger)[0]);
                } catch (FileNotFoundException e1) {
                    this.logger.error(e1);
                } catch (IOException e1) {
                    this.logger.error(e1);
                }
            }
            if (areValidate && max.length() > 0) {
                try {
                    Integer.parseInt(max);
                    this.logger.info("Sprawdzanie formatu ceny maksymalnej");
                } catch (Exception e1) {
                    this.logger.error("Niepoprawny format ceny maksymalnej");
                    areValidate = false;
                    properties = new Properties();
                    try {
                        is = new FileInputStream(propFileName);
                        properties.load(is);
                        JOptionPane.showMessageDialog(null, MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt, this.logger)[0]);
                    } catch (FileNotFoundException e2) {
                        this.logger.error(e1);
                    } catch (IOException e2) {
                        this.logger.error(e1);
                    }
                }
                if (areValidate) {
                    if (Integer.parseInt(min) > Integer.parseInt(max))
                        throw new PriceMinMaxException(this.logger);
                }
            }
        }
        if (areValidate && max.replace(" ", "").length() > 0) {
            try {
                Integer.parseInt(max);
                this.logger.info("Sprawdzanie formatu ceny maksymalnej");
            } catch (Exception e) {
                this.logger.error("Niepoprawny format ceny maksymalnej");
                areValidate = false;
                properties = new Properties();
                try {
                    is = new FileInputStream(propFileName);
                    properties.load(is);
                    JOptionPane.showMessageDialog(null, MainPanel.ReadXmlLanguage(properties.getProperty("DefaultLanguage"), takeIt, this.logger)[0]);
                } catch (FileNotFoundException e1) {
                    this.logger.error(e1);
                } catch (IOException e1) {
                    this.logger.error(e1);
                }
            }
        }
        return areValidate;
    }

    //wyszukiwanie pokoi
    public void displayRooms() throws SQLException {

        String idHotel;
        String query = "select a.idRoom,a.name,a.idHotel,b.name,b.country,b.resort,a.roomSize,a.price,a.bathroom,a.liquorCabinet,a.TV from room a,hotel b where a.idHotel=b.idHotel ";
        String queryPart = "";

        if (hotelNameInput.getSelectedItem() != "") {
            idHotel = hotelNameInput.getSelectedItem().toString().split(" ")[0];
            queryPart += " and b.idHotel=" + idHotel + " ";
        }

        if (Integer.parseInt(spinnerPerson.getValue().toString()) > 0) {

            queryPart += " and roomSize=" + spinnerPerson.getValue().toString() + " ";
        }

        if (countryNameInput.getSelectedItem().toString().replace(" ", "").length() > 1) {
            queryPart += " and country='" + countryNameInput.getSelectedItem().toString() + "' ";
        }

        if (resortNameInput.getSelectedItem().toString().replace(" ", "").length() > 1) {
            queryPart += " and resort='" + resortNameInput.getSelectedItem().toString() + "' ";
        }

        if (chbIsBathroom.isSelected()) {
            queryPart += " and bathroom='+'";
        }

        if (chbIsBar.isSelected()) {
            queryPart += " and liquorCabinet='+'";
        }

        if (chbIsTv.isSelected()) {
            queryPart += " and tv='+'";
        }

        if (priceMinValueInput.getText().replace(" ", "").length() > 0) {
            queryPart += " and price>=" + Integer.parseInt(priceMinValueInput.getText().replace(" ", ""));
        }
        if (priceMaxValueInput.getText().replace(" ", "").length() > 0) {
            queryPart += " and price<=" + Integer.parseInt(priceMaxValueInput.getText().replace(" ", ""));
        }
        query += queryPart;
        try {
            this.rs = this.stmt.executeQuery(query);
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }
            while (rs.next()) {
                model.addRow(new String[]{rs.getString(1), rs.getString(2), rs.getString(3) + " " + rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11)});
            }
            searchResult.setModel(model);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    //czyszczenie formatek
    private void clearData() {
        countryNameInput.setSelectedItem("");
        resortNameInput.setSelectedItem("");
        chbIsBathroom.setSelected(false);
        chbIsBar.setSelected(false);
        chbIsTv.setSelected(false);
        priceMinValueInput.setText("");
        priceMaxValueInput.setText("");
        spinnerPerson.setValue(0);
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        hotelNameInput.setSelectedItem("");
    }


    //ustalanie rozmiaru poszczególnych kolumn
    void setColumnsSize() {
        searchResult.getColumn(searchResult.getColumnName(0)).setPreferredWidth(60);
        searchResult.getColumn(searchResult.getColumnName(1)).setPreferredWidth(100);
        searchResult.getColumn(searchResult.getColumnName(2)).setPreferredWidth(100);
        searchResult.getColumn(searchResult.getColumnName(3)).setPreferredWidth(60);
        searchResult.getColumn(searchResult.getColumnName(4)).setPreferredWidth(60);
        searchResult.getColumn(searchResult.getColumnName(5)).setPreferredWidth(60);
        searchResult.getColumn(searchResult.getColumnName(6)).setPreferredWidth(60);
        searchResult.getColumn(searchResult.getColumnName(7)).setPreferredWidth(60);
        searchResult.getColumn(searchResult.getColumnName(8)).setPreferredWidth(60);
        searchResult.getColumn(searchResult.getColumnName(9)).setPreferredWidth(60);
    }

    //nadawanie pokojowi id--> zeby w przypadku usuwania pokoju, było wiadomo który usunąć
    private void setIdRoom(String id) {
        this.idRoom = id;
    }

    private void setHotelCountryResort() throws SQLException {

        rs = stmt.executeQuery("select idHotel,name from hotel where name<>'' and name is not null;");
        this.hotelNameInput.addItem("");
        while (rs.next()) {
            this.hotelNameInput.addItem(rs.getString(1) + " " + rs.getString(2));
        }
        this.countryNameInput.addItem("");

        rs = stmt.executeQuery("select distinct country from hotel;");
        while (rs.next()) {
            this.countryNameInput.addItem(rs.getString(1));
        }

        rs = stmt.executeQuery("select distinct resort from hotel;");
        this.resortNameInput.addItem("");
        while (rs.next()) {
            this.resortNameInput.addItem(rs.getString(1));
        }

    }


    private boolean deleteRoom() throws IOException, RoomDoesntExistsException {
        try {
            this.logger.info("Próba wykonania zapytania: " + "select count(1) from room where idRoom=" + this.idRoom);
            this.rs = this.stmt.executeQuery("select count(1) from room where idRoom=" + this.idRoom);
            rs.next();
            if (this.rs.getInt(1) != 0) {
                String query = "delete from room where idRoom=" + this.idRoom;
                this.logger.info("Próba wykonania zapytania: " + "delete from room where idRoom=" + this.idRoom);
                stmt.executeUpdate(query);
                int i = 0;
                while (i < model.getRowCount()) {
                    if (model.getValueAt(i, 0).equals(this.idRoom)) {
                        model.removeRow(i);
                        i--;
                    }
                    i++;
                }
                searchResult.validate();
                return true;
            } else {
                throw new RoomDoesntExistsException(this.logger);
            }
        } catch (SQLException e) {
            this.logger.error(e);
        }
        return false;
    }


    //zmiana skórki
    @Override
    public void changePlaf(PlafEvent event) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        int plaf = event.getPlaf();
        switch (plaf) {
            case 1:
                b.gridx = 1;
                b.gridy = 1;
                picLabel = new JLabel(new ImageIcon(seaPicture));
                search.remove(0);
                search.remove(0);
                search.add(layer, b);
                search.add(picLabel, b);
                UIManager.setLookAndFeel(properties.getProperty("Plaf1"));
                SwingUtilities.updateComponentTreeUI(main);
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

    //zmiana języka
    @Override
    public void changeLanguage(LanguageEvent event) {
        Vector<String> data = model.getDataVector();
        Vector<String> par = new Vector<String>();
        paramsValue = MainPanel.ReadXmlLanguage(event.getLanguage(), paramsSearch, this.logger);
        //kolejny panel
        model = new DefaultTableModel();
        title.setText(paramsValue[0]);
        selectTag.setText(paramsValue[1]);
        personTag.setText(paramsValue[2]);
        countryTag.setText(paramsValue[3]);
        amenitiesTag.setText(paramsValue[7] + ":");
        searchButton.setText(paramsValue[0]);
        resortTag.setText(paramsValue[4]);
        priceMinTag.setText(paramsValue[13]);
        priceMaxTag.setText(paramsValue[14]);

        chbIsBathroom.setText(paramsValue[6]);
        chbIsBar.setText(paramsValue[8]);
        chbIsTv.setText(paramsValue[9]);
        clearButton.setText(paramsValue[10]);

        main.setTitleAt(0, paramsValue[12]);
        main.setTitleAt(1, paramsValue[11]);

        paramsValue = MainPanel.ReadXmlLanguage(event.getLanguage(), paramsRoomTable, this.logger);
        model.addColumn(paramsValue[0].toString() + paramsValue[1].toString());
        model.addColumn(paramsValue[2]);
        model.addColumn(paramsValue[0] + paramsValue[3]);
        model.addColumn(paramsValue[4]);
        model.addColumn(paramsValue[5]);
        model.addColumn(paramsValue[6]);
        model.addColumn(paramsValue[7]);
        model.addColumn(paramsValue[8]);
        model.addColumn(paramsValue[9]);
        model.addColumn(paramsValue[10]);
        deleteButton.setText(paramsValue[11]);

        for (int i = 0; i < model.getColumnCount(); i++) {
            par.add(model.getColumnName(i));
        }
        model.setDataVector(data, par);
        searchResult.setModel(model);
        setColumnsSize();
        this.logger.info("Zmieniono jezyk w tym panelu!");
    }


    //reakcja na dodanie hotelu
    @Override
    public void hotelWasAdded(HotelWasAddedEvent event) {
        if (event.getWasAdded()) {
            try {
                this.logger.info("select idHotel,name from hotel where name<>'' and name is not null;");
                this.logger.info("select idHotel,name from hotel where name<>'' and name is not null;");
                rs = stmt.executeQuery("select idHotel,name from hotel where name<>'' and name is not null;");
                hotelNameInput.removeAllItems();
                hotelNameInput.addItem("");
                while (rs.next()) {
                    hotelNameInput.addItem(rs.getString(1) + " " + rs.getString(2));
                }
            } catch (SQLException e) {
                this.logger.error("Błąd zapytania");
            }
            try {
                rs = stmt.executeQuery("select distinct country from hotel;");
                countryNameInput.removeAllItems();
                countryNameInput.addItem("");
                while (rs.next()) {
                    countryNameInput.addItem(rs.getString(1));
                }
            } catch (SQLException e) {
                this.logger.error(e);
            }
        }

        try {
            this.logger.info("Próba wykonania zapytania: " + "select distinct resort from hotel;");
            rs = stmt.executeQuery("select distinct resort from hotel;");
            resortNameInput.removeAllItems();
            resortNameInput.addItem("");
            while (rs.next()) {
                resortNameInput.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            this.logger.error(e);
        }
    }


    //reakcja na wybranie hotelu
    @Override
    public void hotelWasSelected(HotelWasSelectedEvent event) {
        hotelNameInput.setSelectedItem(event.getWasSelected());
    }

    //reakcja na usunięcie hotelu
    @Override
    public void hotelWasDeleted(HotelWasDeletedEvent event) {
        if (event.getWasDeleted()) {
            int i = 0;
            try {
                this.logger.info("Próba wykonania zapytania: " + "select count(name) from hotel;");
                rs = stmt.executeQuery("select count(name) from hotel;");
                rs.next();
                rs = stmt.executeQuery("select idHotel,name from hotel where name<>'' and name is not null;");
                hotelNameInput.removeAllItems();
                hotelNameInput.addItem("");
                while (rs.next()) {
                    hotelNameInput.addItem(rs.getString(1) + " " + rs.getString(2));
                    i++;
                }
                i = 0;
                while (i < model.getRowCount()) {
                    if (model.getValueAt(i, 2).toString().split(" ")[0].equals(event.getIdHotel())) {
                        model.removeRow(i);
                        i--;
                    }
                    i++;
                }
                searchResult.validate();
            } catch (SQLException e) {
                this.logger.error(e);
            }
            try {
                this.logger.info("Próba wykonania zapytania: " + "select count(country) from hotel;");
                rs = stmt.executeQuery("select count(country) from hotel;");
                rs.next();
                rs = stmt.executeQuery("select distinct country from hotel;");
                countryNameInput.removeAllItems();
                countryNameInput.addItem("");
                while (rs.next()) {
                    countryNameInput.addItem(rs.getString(1));
                    i++;
                }
            } catch (SQLException e) {
                this.logger.error(e);
            }
        }

        int i = 0;
        try {
            this.logger.info("Próba wykonania zapytania: " + "select count(resort) from hotel;");
            rs = stmt.executeQuery("select count(resort) from hotel;");
            rs.next();
            rs = stmt.executeQuery("select distinct resort from hotel;");
            resortNameInput.removeAllItems();
            resortNameInput.addItem("");
            while (rs.next()) {
                resortNameInput.addItem(rs.getString(1));
                i++;
            }

        } catch (SQLException e) {
            this.logger.error(e);
        }
    }

    //reakcja na dodanie pokoju
    @Override
    public void roomWasAdded(RoomWasAddedEvent event) throws SQLException {
        if (event.getWasAdded()) {
            displayRooms();
        }
    }
}