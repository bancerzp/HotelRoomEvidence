package Panels;

import Events.LanguageEvent;
import Events.LanguageListener;
import Events.PlafEvent;
import Events.PlafListener;
import com.mysql.jdbc.Connection;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class MainPanel extends JFrame implements ActionListener, LanguageListener {
    JFrame frame;
    static CardLayout cardLayout;
    private static MainPanel instante = null;
    static JPanel card = new JPanel();
    JMenuBar mb;
    String plaf;
    static Logger logger;
    private ArrayList languageListeners = new ArrayList();
    private ArrayList plafListeners = new ArrayList();

    private static File propFile = new File("config.properties");

    public static int getMainPanel() {
        return mainPanel;
    }

    public static void setMainPanel(int mainPanel) {
        MainPanel.mainPanel = mainPanel;
    }

    static int mainPanel = 1;
    JMenu menu1, menu2, menu3, menu4, menu5;//wyszukiwarka,Hotel,Pokój,skorki,jezyk
    JMenuItem i1, i2, i3, i5, sea, tree, mountain, i10, i11;//wysz_pokoj,wysz_hotel,dodaj_hotel,usuń_hotel,dodaj_pokoj,usun_pokoj,skorka1,skorka2,skorka3,jezyk1,jezyk2
    SearchHotelPanel searchHotelPanel;
    LanguageListener sH;
    SearchRoomPanel searchRoomPanel;
    AddHotelPanel addHotelPanel;
    AddRoomPanel addRoomPanel;

    Connection con;
    static String[] params = {"FrameTitle", "Searcher", "Hotel", "Room", "SearchHotel", "SearchRoom",
            "AddHotel", "AddRoom",
            "Plaf", "Plaf1", "Plaf2", "Plaf3", "Language", "Language1", "Language2"};
    static String[] paramsValue = new String[params.length];
    Properties properties;

    private MainPanel(Connection con, Logger logger) throws Exception {
        this.logger = logger;
        //przypisanie wartosci, zeby wiedziec, ktora ramka jest wyswietlana
        this.mainPanel = 1;
        this.con = con;
        frame = new JFrame("");
        //------------------------------------------
        //---------MENU------------------------------
        //---------------------------------------
        mb = new JMenuBar();
        //5 zakładki menu
        menu1 = new JMenu();
        menu2 = new JMenu();
        menu3 = new JMenu();
        menu4 = new JMenu();
        menu5 = new JMenu();
        i1 = new JMenuItem(paramsValue[2]);
        i1.setAccelerator(KeyStroke.getKeyStroke("ctrl H"));
        i2 = new JMenuItem();
        i2.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        i3 = new JMenuItem();
        i3.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));

        i5 = new JMenuItem();
        i5.setAccelerator(KeyStroke.getKeyStroke("alt A"));

        sea = new JMenuItem();
        sea.setAccelerator(KeyStroke.getKeyStroke("alt 1"));
        tree = new JMenuItem();
        tree.setAccelerator(KeyStroke.getKeyStroke("alt 2"));
        mountain = new JMenuItem();
        mountain.setAccelerator(KeyStroke.getKeyStroke("alt 3"));
        i10 = new JMenuItem();
        i10.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
        i11 = new JMenuItem();
        i11.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
        //listenery
        i1.addActionListener(this);
        i2.addActionListener(this);
        i3.addActionListener(this);
        i5.addActionListener(this);
        sea.addActionListener(this);
        tree.addActionListener(this);
        mountain.addActionListener(this);
        i10.addActionListener(this);
        i11.addActionListener(this);

        //wyszukiwarka
        menu1.add(i1);
        menu1.add(i2);
        i2.setMnemonic(KeyEvent.VK_O);
        //hotel
        menu2.add(i3);
        //pokoj
        menu3.add(i5);
        //plafy
        menu4.add(sea);
        menu4.add(tree);
        menu4.add(mountain);
        //jezyk
        menu5.add(i10);
        menu5.add(i11);
        //
        mb.add(menu1);
        mb.add(menu2);
        mb.add(menu3);
        mb.add(menu4);
        mb.add(menu5);
        frame.setJMenuBar(mb);

        ////////////////////////////
        //--------------------------------
        //---------Panele-----------------
        //--------------------------------
        //tworze panel, który jest panelem utworzonej ramki
        JPanel contentPane = (JPanel) frame.getContentPane();
        card.setLayout(cardLayout = new CardLayout());
        //kolejny panel
        //nadanie identyfikatora naszemu panelowi

        this.searchHotelPanel = SearchHotelPanel.createSearchHotelPanel(con, this.logger);
        this.searchRoomPanel = SearchRoomPanel.createSearchRoomPanel(con, this.logger);
        this.addHotelPanel = AddHotelPanel.createAddHotelPanel(con, this.logger);
        this.addRoomPanel = AddRoomPanel.createAddRoomPanel(con, this.logger);
        //  this.addRoomPanel2 = AddRoomPanel.createAddRoomPanel(con, logger);

        //listenery języka
        addLanguageListener(searchHotelPanel);
        addLanguageListener(this);
        addLanguageListener(searchRoomPanel);
        addLanguageListener(addHotelPanel);
        addLanguageListener(addRoomPanel);

        //listenery dodawania hotelu
        addHotelPanel.addAddingHotelListener(addHotelPanel);
        addHotelPanel.addAddingHotelListener(searchRoomPanel);
        addHotelPanel.addAddingHotelListener(searchHotelPanel);
        addHotelPanel.addAddingHotelListener(addRoomPanel);

        //listenery dodawania pokoju
        addRoomPanel.addAddingRoomListener(addRoomPanel);

        //listenery wybrania hotelu
        searchHotelPanel.addSelectedHotelListener(searchRoomPanel);
        searchHotelPanel.addSelectedHotelListener(addRoomPanel);

        //listenery  usuwania hotelu
        searchHotelPanel.addDeleteHotelListener(searchRoomPanel);
        searchHotelPanel.addDeleteHotelListener(searchRoomPanel);
        searchHotelPanel.addDeleteHotelListener(addRoomPanel);

        //listenery zmiany skórki
        addPlafListener(searchHotelPanel);
        addPlafListener(searchRoomPanel);
        addPlafListener(addHotelPanel);
        addPlafListener(addRoomPanel);

//dodawanie kart
        card.add("searchHotelPanel", searchHotelPanel.main);
        card.add("searchRoomPanel", searchRoomPanel.main);
        card.add("addHotelPanel", addHotelPanel.main);//dodaj hotel
        card.add("addRoomPanel", addRoomPanel.main);//dodaj pokoj

        this.paramsValue = paramsValue;

        this.properties = new Properties();

        InputStream is = new FileInputStream(propFile);
        properties.load(is);
        fireLanguageEvent(properties.getProperty("DefaultLanguage").toString());
        firePlafEvent(Integer.parseInt(properties.getProperty("DefaultPlaf")));
        cardLayout.show(card, properties.getProperty("Card"));
        //dodanie do panelu ogólnego, utworzonego przez nas panel
        contentPane.add(card);
        frame.setVisible(Boolean.parseBoolean(properties.getProperty("FrameVisible")));
        frame.setResizable(Boolean.parseBoolean(properties.getProperty("FrameResizable")));
        frame.setSize(new Dimension(Integer.parseInt(properties.getProperty("PanelWidth")), Integer.parseInt(properties.getProperty("PanelHeight"))));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.logger.debug("Panel główny utworzony poprawnie");

        //------------
    }

    //------------------------------------------
//-----------------Funkcje-------------------
    //------------------------------------------

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        plaf = new String();
        if (source == i1) {
            cardLayout.show(card, "searchHotelPanel");
            this.logger.info("Zmiana panelu na wyszukiwarke hoteli");
            setMainPanel(1);
        } else if (source == i2) {
            cardLayout.show(card, "searchRoomPanel");
            this.logger.info("Zmiana panelu na wyszukiwarke pokoi");
            setMainPanel(2);
        } else if (source == i3) {
            cardLayout.show(card, "addHotelPanel");
            this.logger.info("Zmiana panelu na dodawanie hoteli");
            setMainPanel(3);
        } else if (source == i5) {
            cardLayout.show(card, "addRoomPanel");
            this.logger.info("Zmiana panelu na dodawanie pokoi");
            setMainPanel(5);
        } else if (source == sea) {
            plaf = properties.getProperty("Plaf1");
            MainPanel.changeProperties("DefaultPlaf", "1", this.logger);
            MainPanel.changeProperties("DefaultPlafName", plaf, this.logger);

            try {
                firePlafEvent(1);
            } catch (ClassNotFoundException e1) {
                this.logger.error(e1);
            } catch (UnsupportedLookAndFeelException e1) {
                this.logger.error(e1);
            } catch (InstantiationException e1) {
                this.logger.error(e1);
            } catch (IllegalAccessException e1) {
                this.logger.error(e1);
            } catch (IOException e1) {
                this.logger.error(e1);
            }


            try {
                this.logger.info("Proba zmiany skorki na:" + plaf);
                UIManager.setLookAndFeel(plaf);
                SwingUtilities.updateComponentTreeUI(this);

            } catch (ClassNotFoundException e1) {
                this.logger.error(e1);
            } catch (InstantiationException e1) {
                this.logger.error(e1);
            } catch (IllegalAccessException e1) {
                this.logger.error(e1);
            } catch (UnsupportedLookAndFeelException e1) {
                this.logger.error(e1);
            }
        } else if (source == tree) {
            plaf = properties.getProperty("Plaf2");
            MainPanel.changeProperties("DefaultPlaf", "2", this.logger);
            MainPanel.changeProperties("DefaultPlafName", plaf, this.logger);

            try {
                firePlafEvent(2);
            } catch (ClassNotFoundException e1) {
                this.logger.error(e1);
            } catch (UnsupportedLookAndFeelException e1) {
                this.logger.error(e1);
            } catch (InstantiationException e1) {
                this.logger.error(e1);
            } catch (IllegalAccessException e1) {
                this.logger.error(e1);
            } catch (IOException e1) {
                this.logger.error(e1);
            }

            try {
                UIManager.setLookAndFeel(plaf);
                this.logger.info("Proba zmiany skorki na:" + plaf);
                SwingUtilities.updateComponentTreeUI(this);
                this.frame.revalidate();
            } catch (ClassNotFoundException e1) {
                this.logger.error(e1);
            } catch (InstantiationException e1) {
                this.logger.error(e1);
            } catch (IllegalAccessException e1) {
                this.logger.error(e1);
            } catch (UnsupportedLookAndFeelException e1) {
                this.logger.error(e1);
            }

        } else if (source == mountain) {
            plaf = properties.getProperty("Plaf3");
            MainPanel.changeProperties("DefaultPlaf", "3", this.logger);
            MainPanel.changeProperties("DefaultPlafName", plaf, this.logger);

            try {
                firePlafEvent(3);
            } catch (ClassNotFoundException e1) {
                this.logger.error(e1);
            } catch (UnsupportedLookAndFeelException e1) {
                this.logger.error(e1);
            } catch (InstantiationException e1) {
                this.logger.error(e1);
            } catch (IllegalAccessException e1) {
                this.logger.error(e1);
            } catch (IOException e1) {
                this.logger.error(e1);
            }

            try {
                this.logger.info("Proba zmiany skorki na:" + plaf);
                UIManager.setLookAndFeel(plaf);
                SwingUtilities.updateComponentTreeUI(this);
            } catch (ClassNotFoundException e1) {
                this.logger.error(e1);
            } catch (InstantiationException e1) {
                this.logger.error(e1);
            } catch (IllegalAccessException e1) {
                this.logger.error(e1);
            } catch (UnsupportedLookAndFeelException e1) {
                this.logger.error(e1);
            }

        } else if (source == i10) {
            MainPanel.changeProperties("DefaultLanguage", "Polish", this.logger);

            try {
                fireLanguageEvent("Polish");
            } catch (IOException e1) {
                this.logger.error(e1);
            }

        } else if (source == i11) {
            MainPanel.changeProperties("DefaultLanguage", "English", this.logger);

            try {
                fireLanguageEvent("English");
            } catch (IOException e1) {
                this.logger.error(e1);
            }
        }
    }

    //parsowanie xml
    public static String[] ReadXmlLanguage(String language, String[] parametres, Logger logger1) {
        String[] parm = new String[parametres.length];
        Logger logger = logger1;
        try {
            File file = new File("xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName(language);
            Node fstNode = nodeLst.item(0);
            int i = 0;
            while (i < parametres.length) {
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    NodeList fstNmElmntLst = fstElmnt
                            .getElementsByTagName(parametres[i]);
                    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                    NodeList fstNm = fstNmElmnt.getChildNodes();

                    parm[i] = ((Node) fstNm.item(0)).getNodeValue();
                }
                i++;
            }
        } catch (Exception e) {
            logger.error("Problem z parsowaniem xmla!\n" + e);
        }
        return parm;
    }

    //zmiana w pliku properties
    public static void changeProperties(String key, String value, Logger logger) {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            logger.debug("Otwieranie pliku properties");
            is = new FileInputStream(propFile);
        } catch (FileNotFoundException e1) {
            logger.error("Problem z otwarciem pliku properties!\n" + e1);
        }
        try {
            properties.load(is);
            logger.debug("Ladowanie pliku properties");
        } catch (IOException e1) {
            logger.error("Problem z ladowaniem pliku properties!\n" + e1);
        }
        OutputStream os;
        try {
            logger.debug("Proba zmiany wartosci " + key + "na: " + value);
            os = new FileOutputStream(propFile);
            properties.setProperty(key, value);
            properties.store(os, null);
        } catch (FileNotFoundException e) {
            logger.error("Problem z plikiem! " + e);
        } catch (IOException e) {
            logger.debug(e);
        }
    }

    //listenery języka
    public synchronized void addLanguageListener(LanguageListener l) {
        languageListeners.add(l);
    }

    public synchronized void removeLanguageListener(LanguageListener l) {
        languageListeners.remove(l);
    }

    //"odpalanie" funkcja zmiany języka
    private synchronized void fireLanguageEvent(String language) throws IOException {
        LanguageEvent languageEv = new LanguageEvent(this, language);
        Iterator listenersIt = languageListeners.iterator();
        while (listenersIt.hasNext()) {
            ((LanguageListener) listenersIt.next()).changeLanguage(languageEv);
        }
    }

    //zmiana plaf
    public synchronized void addPlafListener(PlafListener l) {
        plafListeners.add(l);
    }

    public synchronized void removePlafListener(PlafListener l) {
        plafListeners.remove(l);
    }

    private synchronized void firePlafEvent(int plaf) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException {
        PlafEvent plafEv = new PlafEvent(this, plaf);
        Iterator listenersIt = plafListeners.iterator();
        while (listenersIt.hasNext()) {
            ((PlafListener) listenersIt.next()).changePlaf(plafEv);
        }
    }

    //zmiana języka
    @Override
    public void changeLanguage(LanguageEvent event) {

        this.paramsValue = ReadXmlLanguage(event.getLanguage(), params, this.logger);
        setTitle(paramsValue[0]);
        menu1.setText(paramsValue[1]);
        menu2.setText(paramsValue[2]);
        menu3.setText(paramsValue[3]);
        menu4.setText(paramsValue[8]);
        menu5.setText(paramsValue[12]);
        i1.setText(paramsValue[4]);
        i2.setText(paramsValue[5]);
        i3.setText(paramsValue[6]);
        i5.setText(paramsValue[7]);
        sea.setText(paramsValue[9]);
        tree.setText(paramsValue[10]);
        mountain.setText(paramsValue[11]);
        i10.setText(paramsValue[13]);
        i11.setText(paramsValue[14]);
    }

    //SINGLETON
    public static synchronized MainPanel createMainClass(Connection con, Logger logger) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (instante == null) {
            try {
                logger.info("Próba utworzenia instancji SearchRoomPanel");
                instante = new MainPanel(con, logger);
            } catch (Exception e) {
                logger.error(e);
            }
            return instante;
        } else {
            new JOptionPane("Koniec");
            logger.error("MainPanel już został wcześniej utworzony!!!1");
            return null;
        }

    }
}
