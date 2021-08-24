package oplpops.game.manager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.border.EmptyBorder;

public class GameConfigScreen extends javax.swing.JDialog {

    // Constants for the image paths
    private static final String STAR_ACTIVE_IMAGE_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images"  + File.separator + "Star Active.png";
    private static final String STAR_INACTIVE_IMAGE_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "Star Inactive.png";
    private static final String NO_RATING_IMAGE_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "rating" + File.separator + "No_Rating.png";

    private static final String RATING_IMAGE_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "rating" + File.separator;
    private static final String PEGI_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "PEGI_3.png";
    private static final String PEGI_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "PEGI_7.png";
    private static final String PEGI_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "PEGI_12.png";
    private static final String PEGI_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "PEGI_16.png";
    private static final String PEGI_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "PEGI_18.png";
    private static final String ESRB_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "ESRB_Early_Childhood.png";
    private static final String ESRB_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "ESRB_Everyone.png";
    private static final String ESRB_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "ESRB_Everyone_10+.png";
    private static final String ESRB_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "ESRB_Teen.png";
    private static final String ESRB_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "ESRB_Mature.png";
    private static final String ESRB_RATING_6_IMAGE_PATH = RATING_IMAGE_PATH + "ESRB_Adults_Only.png";
    private static final String CERO_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "CERO_A.png";
    private static final String CERO_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "CERO_B.png";
    private static final String CERO_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "CERO_C.png";
    private static final String CERO_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "CERO_D.png";
    private static final String CERO_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "CERO_Z.png";
    private static final String CERO_RATING_6_IMAGE_PATH = RATING_IMAGE_PATH + "CERO_Demo.png";
    private static final String CERO_RATING_7_IMAGE_PATH = RATING_IMAGE_PATH + "CERO_Pending.png";
    private static final String BBFC_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "BBFC_U.png";
    private static final String BBFC_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "BBFC_PG.png";
    private static final String BBFC_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "BBFC_12.png";
    private static final String BBFC_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "BBFC_15.png";
    private static final String BBFC_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "BBFC_18.png";
    private static final String DEJUS_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "DEJUS_L.png";
    private static final String DEJUS_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "DEJUS_10.png";
    private static final String DEJUS_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "DEJUS_12.png";
    private static final String DEJUS_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "DEJUS_14.png";
    private static final String DEJUS_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "DEJUS_16.png";
    private static final String DEJUS_RATING_6_IMAGE_PATH = RATING_IMAGE_PATH + "DEJUS_18.png";
    private static final String ELSPA_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "ELSPA_3.jpg";
    private static final String ELSPA_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "ELSPA_11.jpg";
    private static final String ELSPA_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "ELSPA_15.jpg";
    private static final String ELSPA_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "ELSPA_18.jpg";
    private static final String ESRA_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "ESRA_3.png";
    private static final String ESRA_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "ESRA_7.png";
    private static final String ESRA_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "ESRA_12.png";
    private static final String ESRA_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "ESRA_15.png";
    private static final String ESRA_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "ESRA_18.png";
    private static final String OFLC_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "OFLC_E.png";
    private static final String OFLC_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "OFLC_G.png";
    private static final String OFLC_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "OFLC_PG.png";
    private static final String OFLC_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "OFLC_M.png";
    private static final String OFLC_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "OFLC_15.png";
    private static final String OFLC_RATING_6_IMAGE_PATH = RATING_IMAGE_PATH + "OFLC_R18.png";
    private static final String OFLC_RATING_7_IMAGE_PATH = RATING_IMAGE_PATH + "OFLC_X18.png";
    private static final String USK_RATING_1_IMAGE_PATH = RATING_IMAGE_PATH + "USK_0.png";
    private static final String USK_RATING_2_IMAGE_PATH = RATING_IMAGE_PATH + "USK_6.png";
    private static final String USK_RATING_3_IMAGE_PATH = RATING_IMAGE_PATH + "USK_12.png";
    private static final String USK_RATING_4_IMAGE_PATH = RATING_IMAGE_PATH + "USK_16.png";
    private static final String USK_RATING_5_IMAGE_PATH = RATING_IMAGE_PATH + "USK_18.png";
    
    private final GameConfigFileManager configManager;
    private static final String[] NEW_CONFIG_DATA = new String[24];
    private static List<Game> gameList;

    private int currentListIndex; 
    private boolean userRatingSet = false;
    private int userRatingValue = 0;
    private int parentalRating = 0;
    private boolean guiActive = false;

    public GameConfigScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();   
        configManager =  new GameConfigFileManager();
        overideClose();
    }

    
    // <editor-fold defaultstate="collapsed" desc="Public Functions">     
    // This initialises the GUI
    public void initialiseGUI(int gameIndex, boolean configExists){
        
        if (gameIndex < 0) gameIndex = 0;
        
        getGameLists();
        currentListIndex = gameIndex;
        clearGUI();
        displayGameName();
        displayGameNumber();
        initialiseRatingStars();

        // Load the data from the config file if it exists
        if (configExists){
            
            guiEnabled(true);
            
            if (PopsGameManager.getCurrentConsole().equals("PS1")){
                try {displayGameConfigDetails(configManager.readGameConfigFormatted(GameListManager.getGamePS1(gameIndex).getGameID(),GameListManager.getGamePS1(gameIndex).getGameName()));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error reading the game config file!\n\n" + ex.toString());}
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                try {displayGameConfigDetails(configManager.readGameConfigFormatted(GameListManager.getGamePS2(gameIndex).getGameID(),GameListManager.getGamePS1(gameIndex).getGameName()));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error reading the game config file!\n\n" + ex.toString());}
            }
        }
        else {guiEnabled(false);}
        
        this.setTitle(" Manage Game Config");
        
        jButtonSaveConfig.setEnabled(false);
        
    }
    // </editor-fold>
    
        
    // <editor-fold defaultstate="collapsed" desc="Private Functions"> 
    

    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                
                // Need to check if config data has changed, if so prompt the user if they want to save the changes
                composeGameConfigData();
                
                if (configManager.gameConfigExists(gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName())){
                    if (!compareConfigData()) {
                        int dialogResult = JOptionPane.showConfirmDialog (null, "The config data has been modified. \n\nDo you want to save the file?"," Config data not saved!",JOptionPane.YES_NO_OPTION);
                        if(dialogResult == JOptionPane.YES_OPTION){
                            writeGameConfigFile();
                            disposeDialog();
                        }  
                        else {disposeDialog();}
                    }
                    else {disposeDialog();}
                } 
                else {disposeDialog();}
            }
        });
    }
    
    
    // This disposes the dialog form and callsback to update the game list in the main GUI
    private void disposeDialog(){
        PopsGameManager.callbackToUpdateGUIGameList(null, currentListIndex);
        dispose();
    }

    // This displays the game name in the GUI
    private void displayGameName(){jTextFieldGameTitle.setText(" " + gameList.get(currentListIndex).getGameName());}
    
    
    // This displays the game ID in the GUI
    private void displayGameNumber(){
        jTextFieldGameID.setText(gameList.get(currentListIndex).getGameID());
        jTextFieldGameNumber.setText("[" + (currentListIndex +1) + "/" + gameList.size() + "]");
    }
    
    
    // This truncates a string
    private static String truncate(String value, int length) {if (value.length() > length) return value.substring(0, length); else return value;}
    
    
    // This loads the game lists
    private void getGameLists(){
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {gameList = new ArrayList<>(GameListManager.getGameListPS1());}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {gameList = new ArrayList<>(GameListManager.getGameListPS2());}     
    }
    
    
    // This reverses a string
    private String reverseString(String source) {
        int i, len = source.length();
        StringBuilder dest = new StringBuilder(len);
        for (i = (len - 1); i >= 0; i--) {dest.append(source.charAt(i));}

        return dest.toString();
    }
    
    
    // This deletes the selected config file
    private void deleteConfigFile(){

        int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete the current config file?"," Delete Config!",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            new File(PopsGameManager.getOPLFolder() + "CFG" + File.separator + gameList.get(currentListIndex).getGameID() + ".cfg").delete();
            clearGUI();
            initialiseGUI(currentListIndex, configManager.gameConfigExists(gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName()));
        }   
    }
    
    
    // This generates the VMC file (Uses genvmc for PS2 memory cards)
    private  void generateVMC(String vmcName){

        if (PopsGameManager.getCurrentConsole().equals("PS1")){
            
            // This creates the game's folder if it does not already exist
            //File gameFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameList.get(currentListIndex).getGameName());
            //if (!gameFolder.exists() || !gameFolder.isDirectory()) {gameFolder.mkdir();}
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){
            
            // This creates the VMC folder if it does not already exist
            File vmcFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "VMC");
            if (!vmcFolder.exists() || !vmcFolder.isDirectory()) {vmcFolder.mkdir();}

            // Check the users operating system to determine which version of the app to execute 
            String appFolder = "windows";
            String appName = "genvmc.exe";

            if (PopsGameManager.getOSType().equals("Linux") || PopsGameManager.getOSType().equals("Mac")){
                appFolder = "linux";
                appName = "genvmc";
            }

            List<String> commands = new ArrayList<>();
            commands.add(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName);
            commands.add("8");
            commands.add(PopsGameManager.getOPLFolder() + File.separator + "VMC" + File.separator + vmcName + ".bin");

            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.directory(new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder));
            try {
                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug("Error launching genvmc!\n\n" + ex.toString());}
        }  
    }
    
    // Initialise the star rating images
    private void initialiseRatingStars(){
        
        jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
        jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
        jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
        jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
        jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
        
        // Mouse listener for star 1 label
        jLabelStar1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {mouseEnterStar(1);}
            @Override
            public void mouseExited(MouseEvent e) {mouseExistStar(1);}
            @Override
            public void mouseClicked(MouseEvent evt) {mouseClickStar(1);}
        });
        
        // Mouse listener for star 2 label
        jLabelStar2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {mouseEnterStar(2);}
            @Override
            public void mouseExited(MouseEvent e) {mouseExistStar(2);}
            @Override
            public void mouseClicked(MouseEvent evt) {mouseClickStar(2);}
        });
        
        // Mouse listener for star 3 label
        jLabelStar3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {mouseEnterStar(3);}
            @Override
            public void mouseExited(MouseEvent e) {mouseExistStar(3);}
            @Override
            public void mouseClicked(MouseEvent evt) {mouseClickStar(3);}
        });
        
        // Mouse listener for star 4 label
        jLabelStar4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {mouseEnterStar(4);}
            @Override
            public void mouseExited(MouseEvent e) {mouseExistStar(4);}
            @Override
            public void mouseClicked(MouseEvent evt) {mouseClickStar(4);}
        });
        
        // Mouse listener for star 5 label
        jLabelStar5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {mouseEnterStar(5);}
            @Override
            public void mouseExited(MouseEvent e) {mouseExistStar(5);}
            @Override
            public void mouseClicked(MouseEvent evt) {mouseClickStar(5);}
        });
    }
    
    
    // This updates the parental rating image in the GUI
    private void updateParentalRatingImage(){
        
        switch (jComboBoxRatingSystem.getSelectedItem().toString()) {
            case "BBFC":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(BBFC_RATING_1_IMAGE_PATH).getImage())); 
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(BBFC_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(BBFC_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(BBFC_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(BBFC_RATING_5_IMAGE_PATH).getImage()));
                        break;
                }
                break;
            case "CERO":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(CERO_RATING_1_IMAGE_PATH).getImage()));
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(CERO_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(CERO_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(CERO_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(CERO_RATING_5_IMAGE_PATH).getImage()));
                        break;
                    case 6:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(CERO_RATING_6_IMAGE_PATH).getImage()));
                        break;
                    case 7:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(CERO_RATING_7_IMAGE_PATH).getImage()));
                        break;
                }
                break;
            case "DEJUS":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(DEJUS_RATING_1_IMAGE_PATH).getImage()));
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(DEJUS_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(DEJUS_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(DEJUS_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(DEJUS_RATING_5_IMAGE_PATH).getImage()));
                        break;
                    case 6:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(DEJUS_RATING_6_IMAGE_PATH).getImage()));
                        break;    
                }
                break; 
            case "ELSPA":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ELSPA_RATING_1_IMAGE_PATH).getImage())); 
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ELSPA_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ELSPA_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ELSPA_RATING_4_IMAGE_PATH).getImage()));
                        break;
                }
                break; 
            case "ESRA":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRA_RATING_1_IMAGE_PATH).getImage()));
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRA_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRA_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRA_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRA_RATING_5_IMAGE_PATH).getImage()));
                        break;
                }
                break; 
            case "ESRB":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRB_RATING_1_IMAGE_PATH).getImage()));
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRB_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRB_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRB_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRB_RATING_5_IMAGE_PATH).getImage()));
                        break;
                    case 6:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(ESRB_RATING_6_IMAGE_PATH).getImage()));
                        break;    
                }
                break; 
            case "OFLC":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(OFLC_RATING_1_IMAGE_PATH).getImage()));
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(OFLC_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(OFLC_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(OFLC_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(OFLC_RATING_5_IMAGE_PATH).getImage()));
                        break;
                    case 6:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(OFLC_RATING_6_IMAGE_PATH).getImage()));
                        break;
                    case 7:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(OFLC_RATING_7_IMAGE_PATH).getImage()));
                        break;
                }
                break; 
            case "PEGI":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(PEGI_RATING_1_IMAGE_PATH).getImage())); 
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(PEGI_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(PEGI_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(PEGI_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(PEGI_RATING_5_IMAGE_PATH).getImage()));
                        break;
                }
            break; 
            case "USK":
                switch (parentalRating) {
                    case 1:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(USK_RATING_1_IMAGE_PATH).getImage()));
                        break;
                    case 2:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(USK_RATING_2_IMAGE_PATH).getImage()));
                        break;
                    case 3:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(USK_RATING_3_IMAGE_PATH).getImage()));
                        break;
                    case 4:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(USK_RATING_4_IMAGE_PATH).getImage()));
                        break;
                    case 5:
                        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(USK_RATING_5_IMAGE_PATH).getImage()));
                        break;
                }
            break;      
        } 
    }
    
    
    // This converts the rating value that has been read from the config file
    private void switchParentRatingValue(String ratingSystem, String ratingValue){
        
        switch (ratingSystem) {
                case "bbfc":
                    switch (ratingValue) {
                        case "pg":
                            parentalRating = 1;
                            break;
                        case "u":
                            parentalRating = 2;
                            break;
                        case "12":
                            parentalRating = 3;
                            break;
                        case "15":
                            parentalRating = 4;
                            break;
                        case "16":
                            parentalRating = 5;    
                            break;
                    }
                    break;
                case "cero":
                    switch (ratingValue) {
                        case "a":
                            parentalRating = 1;
                            break;
                        case "b":
                            parentalRating = 2;
                            break;
                        case "c":
                            parentalRating = 3;
                            break;
                        case "d":
                            parentalRating = 4;
                            break;
                        case "z":
                            parentalRating = 5;    
                            break;
                        case "demo":
                            parentalRating = 6;    
                            break;
                        case "pending":
                            parentalRating = 7;    
                            break;
                    }
                    break;
                case "dejus":
                    switch (ratingValue) {
                        case "l":
                            parentalRating = 1;
                            break;
                        case "10":
                            parentalRating = 2;
                            break;
                        case "12":
                            parentalRating = 3;
                            break;
                        case "14":
                            parentalRating = 4;
                            break;
                        case "16":
                            parentalRating = 5;    
                            break;
                        case "18":
                            parentalRating = 6;    
                            break;
                    }
                    break;
                case "elspa":
                    switch (ratingValue) {
                        case "3":
                        case "3a":
                            parentalRating = 1;
                            break;
                        case "11":
                        case "11a":
                            parentalRating = 2;
                            break;
                        case "15":
                        case "15a":
                            parentalRating = 3;
                            break;
                        case "18":
                        case "18a":
                            parentalRating = 4;
                            break;
                    }
                    break;
                case "esra":
                    switch (ratingValue) {
                        case "3":
                            parentalRating = 1;
                            break;
                        case "7":
                            parentalRating = 2;
                            break;
                        case "12":
                            parentalRating = 3;
                            break;
                        case "15":
                            parentalRating = 4;
                            break;
                        case "18":
                            parentalRating = 5;    
                            break;
                    }
                    break;   
                case "esrb":
                    switch (ratingValue) {
                        case "3":
                            parentalRating = 1;
                            break;
                        case "e":
                            parentalRating = 2;
                            break;
                        case "10":
                            parentalRating = 3;
                            break;
                        case "teen":
                            parentalRating = 4;
                            break;
                        case "17":
                            parentalRating = 5;    
                            break;
                        case "18":
                        case "m":
                            parentalRating = 6;    
                            break;
                    }
                    break;
                case "oflc":
                    switch (ratingValue) {
                        case "e":
                            parentalRating = 1;
                            break;
                        case "g":
                            parentalRating = 2;
                            break;
                        case "pg":
                            parentalRating = 3;
                            break;
                        case "m":
                            parentalRating = 4;
                            break;
                        case "ma":
                            parentalRating = 5;    
                            break;
                        case "r":
                            parentalRating = 6;    
                            break;
                        case "x":
                            parentalRating = 7;    
                            break;
                    }
                    break;
                case "pegi":
                    switch (ratingValue) {
                        case "3":
                            parentalRating = 1;
                            break;
                        case "7":
                            parentalRating = 2;
                            break;
                        case "12":
                            parentalRating = 3;
                            break;
                        case "16":
                            parentalRating = 4;
                            break;
                        case "18":
                            parentalRating = 5;    
                            break;
                    }
                    break;
                case "usk":
                    switch (ratingValue) {
                        case "0":
                            parentalRating = 1;
                            break;
                        case "6":
                            parentalRating = 2;
                            break;
                        case "12":
                            parentalRating = 3;
                            break;
                        case "16":
                            parentalRating = 4;
                            break;
                        case "18":
                            parentalRating = 5;    
                            break;
                    }
                    break;  
            }
    }
    
    
    // This displays the details from the config file in the GUI
    private void displayGameConfigDetails(String configData[]){
 
        //if (configData[1] != null) jTextFieldGameTitle.setText(configData[1]);                                        // Game title
        if (configData[2] != null) {jComboBoxGenre.setSelectedItem(configData[2]);}                                     // Game genre combo
        if (configData[3] != null) {jTextFieldDeveloper.setText(configData[3]);}                                        // Developer text
        if (configData[4] != null) {jTextFieldReleaseDate.setText(configData[4]);}                                      // Release date text
        if (configData[5] != null) {jComboBoxNoOfPlayers.setSelectedIndex(Integer.parseInt(configData[5]));}            // Number of players combo
        
        // User game rating
        if (configData[6] != null) {mouseClickStar(Integer.parseInt(configData[6]));} else {mouseClickStar(0);}

        // Game description and notes
        if (configData[7] != null) {jTextAreaGameDescription.setText(configData[7]);}                             
        if (configData[8] != null) {jTextFieldNotes.setText(configData[8]);}                                      

        // VMC0
        if (configData[9] != null) {
            jCheckBoxVMC0.setSelected(true);
            jTextFieldVMC0.setText(configData[9]);
        }

        // VMC1
        if (configData[10] != null) {
            jCheckBoxVMC1.setSelected(true);
            jTextFieldVMC1.setText(configData[10]);
        }
        
        if (configData[11] != null) {jComboBoxCheat.setSelectedItem(configData[11]);}                                   // Cheat type
        if (configData[12] != null) {jCheckBoxCheatEnabled.setSelected(true);}                                          // Cheat enabled
        else {jCheckBoxCheatEnabled.setSelected(false);}
        
        if (configData[13] != null) {checkCompatibleDevices(configData[13]);} else {jComboBoxDMA.setEnabled(false);}    // Device compatibility   
        if (configData[14] != null) {jComboBoxVMode.setSelectedItem(configData[14].toUpperCase());}                     // V-mode
        if (configData[15] != null) {checkAspectRatio(configData[15]);}                                                 // Aspect
        if (configData[16] != null) {checkScan(configData[16]);}                                                        // Scan
        if (configData[17] != null) {readCompatibilityMode(configData[17]);}                                            // Compatibility Mode
        
        // Enable GSM
        if (configData[18] != null) {
            jCheckBoxGSMEnabled.setSelected(true);
            jComboBoxGSMVMode.setEnabled(true);
            jCheckBoxGSMSkipVideos.setEnabled(true);
            jSpinnerHPos.setEnabled(true);
            jSpinnerVPos.setEnabled(true);
        }
        else {
            jCheckBoxGSMEnabled.setSelected(false);
            jComboBoxGSMVMode.setEnabled(false);
            jCheckBoxGSMSkipVideos.setEnabled(false);
            jSpinnerHPos.setEnabled(false);
            jSpinnerVPos.setEnabled(false);
        }

        if (configData[19] != null) {jComboBoxGSMVMode.setSelectedIndex(Integer.parseInt(configData[19]));}             // GSM V-mode
        if (configData[20] != null) {jSpinnerHPos.setValue(Integer.parseInt(configData[20]));}                          // V-mode H-pos
        if (configData[21] != null) {jSpinnerVPos.setValue(Integer.parseInt(configData[21]));}                          // V-mode V-pos
        if (configData[22] != null) {jCheckBoxGSMSkipVideos.setSelected(true);}                                         // GSM Skip Videos

        if (configData[23] != null) {
            String[] splitParentalRating = configData[23].split("/");
            String ratingSystem = splitParentalRating[0];                 
            String ratingValue = splitParentalRating[1];                  
            
            if (ratingSystem.equals("ofcl")) {ratingSystem = "oflc";}
            
            jComboBoxRatingSystem.setSelectedItem(ratingSystem.toUpperCase(Locale.ENGLISH));
            switchParentRatingValue(ratingSystem, ratingValue);
            updateParentalRatingImage(); 
        }
    }
    

    // This converts the compatability mode
    private String convertCompatabilityMode(){
        
        int[] checkboxBinary = {0,0,0,0,0,0,0,0};
        
        // If the checkbox is checked, set the value in the array to 1
        if (jCheckBoxMode1.isSelected()) {checkboxBinary[0] = 1;}
        if (jCheckBoxMode2.isSelected()) {checkboxBinary[1] = 1;}
        if (jCheckBoxMode3.isSelected()) {checkboxBinary[2] = 1;}
        if (jCheckBoxMode4.isSelected()) {checkboxBinary[3] = 1;}
        if (jCheckBoxMode5.isSelected()) {checkboxBinary[4] = 1;}
        if (jCheckBoxMode6.isSelected()) {checkboxBinary[5] = 1;}
        if (jCheckBoxMode7.isSelected()) {checkboxBinary[6] = 1;}
        if (jCheckBoxMode8.isSelected()) {checkboxBinary[7] = 1;}
        
        // Convert the array to a string and then reverse it
        String binaryValue = "";
        for (int number:checkboxBinary) {binaryValue += number;}
        binaryValue = reverseString(binaryValue);
        
        // Convert the reversed binary string to a decimal value, then convert that to a string
        int decimalValue = Integer.parseInt(binaryValue, 2);
        String compatabilityMode = Integer.toString(decimalValue);
        
        return compatabilityMode;
    }
    
    
    // This determines if any of the compatibility mode checkboxes are selected
    private boolean compatabilityModeSelected(){
        return jCheckBoxMode1.isSelected() || jCheckBoxMode2.isSelected() || jCheckBoxMode3.isSelected() || jCheckBoxMode4.isSelected() || jCheckBoxMode5.isSelected() || jCheckBoxMode6.isSelected() || jCheckBoxMode7.isSelected() || jCheckBoxMode8.isSelected();  
    }
    
    
    // This writes the config file for the game
    private void composeGameConfigData(){

        // Clear the array
        Arrays.fill(NEW_CONFIG_DATA, null);
        
        // Compose the config data array using the information in the GUI
        NEW_CONFIG_DATA[0] = "CfgVersion=5";
        NEW_CONFIG_DATA[1] = "Title=" + gameList.get(currentListIndex).getGameName();
        
        if (jComboBoxGenre.getSelectedIndex() != 0 || !jComboBoxGenre.getSelectedItem().equals(" ")) {NEW_CONFIG_DATA[2] = "Genre=" + jComboBoxGenre.getSelectedItem().toString();}
        if (!"".equals(jTextFieldDeveloper.getText())) {NEW_CONFIG_DATA[3] = "Developer=" + jTextFieldDeveloper.getText();}
        if (!"".equals(jTextFieldReleaseDate.getText())) {NEW_CONFIG_DATA[4] = "Release=" + jTextFieldReleaseDate.getText();}
        if (jComboBoxNoOfPlayers.getSelectedIndex() != 0) {NEW_CONFIG_DATA[5] = "Players=players/" + truncate(jComboBoxNoOfPlayers.getSelectedItem().toString(), 1);}
        if (userRatingValue != 0) {NEW_CONFIG_DATA[6] = "Rating=rating/" + userRatingValue;}
        if (!"".equals(jTextAreaGameDescription.getText())) {NEW_CONFIG_DATA[7] = "Description=" + jTextAreaGameDescription.getText();}
        if (!"".equals(jTextFieldNotes.getText())) {NEW_CONFIG_DATA[8] = "Notes=" + jTextFieldNotes.getText();}
        if (jCheckBoxVMC0.isSelected()) {NEW_CONFIG_DATA[9] = "$VMC_0=" + jTextFieldVMC0.getText();}
        if (jCheckBoxVMC1.isSelected()) {NEW_CONFIG_DATA[10] = "$VMC_1=" + jTextFieldVMC1.getText();}
        
        if (jComboBoxCheat.getSelectedIndex() != 0 || !jComboBoxCheat.getSelectedItem().equals(" ")){NEW_CONFIG_DATA[11] = "Cheat=" + jComboBoxCheat.getSelectedItem().toString();}
        if (jCheckBoxCheatEnabled.isSelected()) {NEW_CONFIG_DATA[12] = "$EnableCheat=1";}
        
        if (jCheckBoxUSB.isSelected() || jCheckBoxETH.isSelected() || jCheckBoxHDD.isSelected()) {NEW_CONFIG_DATA[13] = "Device=device/" + convertCompatibleDevices();}
        if (jComboBoxVMode.getSelectedIndex() != 0) {NEW_CONFIG_DATA[14] = "Vmode=vmode/" + jComboBoxVMode.getSelectedItem().toString().toLowerCase();}
        if (jComboBoxAspectRatio.getSelectedIndex() != 0) {NEW_CONFIG_DATA[15] = "Aspect=aspect/" + convertAspectRation();}
        if (jComboBoxScan.getSelectedIndex() != 0) {NEW_CONFIG_DATA[16] = "Scan=scan/" + convertScanRate();}
        if (compatabilityModeSelected()) {NEW_CONFIG_DATA[17] = "$Compatibility=" + convertCompatabilityMode();}
        if (jCheckBoxGSMEnabled.isSelected()) {NEW_CONFIG_DATA[18] = "$EnableGSM=1";}
        if (jComboBoxGSMVMode.getSelectedIndex() != 0) {NEW_CONFIG_DATA[19] = "$GSMVMode=" + jComboBoxGSMVMode.getSelectedIndex();}
        if (!jSpinnerHPos.getValue().toString().equals("0")) {NEW_CONFIG_DATA[20] = "$GSMXOffset=" + jSpinnerHPos.getValue().toString();}
        if (!jSpinnerVPos.getValue().toString().equals("0")) {NEW_CONFIG_DATA[21] = "$GSMYOffset=" + jSpinnerVPos.getValue().toString();}
        if (jCheckBoxGSMSkipVideos.isSelected()) {NEW_CONFIG_DATA[22] = "$GSMSkipVideos=1";} 
        if (parentalRating != 0) {NEW_CONFIG_DATA[23] = convertParentRatingValue();} 
    }
    

    // This writes the config file for the game
    private void writeGameConfigFile(){
        configManager.writeGameConfigFile(NEW_CONFIG_DATA, gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName());
        checkForVMC();
    }
    

    // This checks if a config file already exists and compares the data to determine if any changes have been made by the user
    private boolean compareConfigData(){
        
        // Try and compare the current config data with the config data stored in the file
        boolean configIdentical = false;
        try {
            String[] storedConfigFile = configManager.readGameConfigRaw(gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName());
            if (storedConfigFile != null){if (NEW_CONFIG_DATA.length == storedConfigFile.length){configIdentical = configManager.compareGameConfig(NEW_CONFIG_DATA, storedConfigFile);}}
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
      
        return configIdentical;
    }
    

    // This checks generates a virtual memory card file if one does not already exist
    private void checkForVMC(){
        
        String memoryCard1 = null;
        String memoryCard2 = null;
        
        // Check if the new config data contains any virtual memory cards
        for (String arrayItem : NEW_CONFIG_DATA){
            if (arrayItem != null && arrayItem.length() > 7){
                if (arrayItem.substring(0,6).equals("$VMC_0")) {memoryCard1 = arrayItem.substring(7,arrayItem.length());}
                if (arrayItem.substring(0,6).equals("$VMC_1")) {memoryCard2 = arrayItem.substring(7,arrayItem.length());}
            }
        }
        
        // Generate virtual memory card 1
        if (memoryCard1 != null){if (!memeoryCardExists(memoryCard1)){generateVMC(memoryCard1);}}
        
        // Generate virtual memory card 2
        if (memoryCard2 != null){if (!memeoryCardExists(memoryCard2)){generateVMC(memoryCard2);}}
    }
    

    // This checks if a virtual memory card already exists
    private boolean memeoryCardExists(String memoryCardName){
        
        boolean memeoryCardExists = false;
        String memoryCardLocation = null;
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")){memoryCardLocation = PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameList.get(currentListIndex).getGameName() + File.separator + memoryCardName + ".bin";}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){memoryCardLocation = PopsGameManager.getOPLFolder() + File.separator + "VMC" + File.separator + memoryCardName + ".bin";}
        
        if (memoryCardLocation != null){if (new File(memoryCardLocation).exists() && !new File(memoryCardLocation).isDirectory()){memeoryCardExists = true;}}

        return memeoryCardExists;
    }
    
    
    // This converts the rating value before it is written to the new config file
    private String convertParentRatingValue(){
        
        String newRatingValue = "";
        
        switch (jComboBoxRatingSystem.getSelectedItem().toString()) {
                case "BBFC":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=bbfc/pg";
                            break;
                        case 2:
                            newRatingValue = "Parental=bbfc/u";
                            break;
                        case 3:
                            newRatingValue = "Parental=bbfc/12";
                            break;
                        case 4:
                            newRatingValue = "Parental=bbfc/15";
                            break;
                        case 5:
                            newRatingValue = "Parental=bbfc/18";
                            break;
                    }
                    break;
                case "CERO":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=cero/a";
                            break;
                        case 2:
                            newRatingValue = "Parental=cero/b";
                            break;
                        case 3:
                            newRatingValue = "Parental=cero/c";
                            break;
                        case 4:
                            newRatingValue = "Parental=cero/d";
                            break;
                        case 5:
                            newRatingValue = "Parental=cero/z";
                            break;
                        case 6:
                            newRatingValue = "Parental=cero/demo";
                            break;
                        case 7:
                            newRatingValue = "Parental=cero/pending";
                            break;
                    }
                    break;
                case "DEJUS":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=dejus/l";
                            break;
                        case 2:
                            newRatingValue = "Parental=dejus/10";
                            break;
                        case 3:
                            newRatingValue = "Parental=dejus/12";
                            break;
                        case 4:
                            newRatingValue = "Parental=dejus/14";
                            break;
                        case 5:
                            newRatingValue = "Parental=dejus/16";
                            break;
                        case 6:
                            newRatingValue = "Parental=dejus/18";
                            break;
                    }
                    break;
                case "ELSPA":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=elspa/3";
                            break;
                        case 2:
                            newRatingValue = "Parental=elspa/11";
                            break;
                        case 3:
                            newRatingValue = "Parental=elspa/15";
                            break;
                        case 4:
                            newRatingValue = "Parental=elspa/18";
                            break;
                    }
                    break;
                case "ESRA":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=esra/3";
                            break;
                        case 2:
                            newRatingValue = "Parental=esra/7";
                            break;
                        case 3:
                            newRatingValue = "Parental=esra/12";
                            break;
                        case 4:
                            newRatingValue = "Parental=esra/15";
                            break;
                        case 5:
                            newRatingValue = "Parental=esra/18";
                            break;
                    }
                    break;   
                case "ESRB":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=esrb/3";
                            break;
                        case 2:
                            newRatingValue = "Parental=esrb/e";
                            break;
                        case 3:
                            newRatingValue = "Parental=esrb/10";
                            break;
                        case 4:
                            newRatingValue = "Parental=esrb/teen";
                            break;
                        case 5:
                            newRatingValue = "Parental=esrb/17";
                            break;
                        case 6:
                            newRatingValue = "Parental=esrb/18";
                            break;
                    }
                    break;
                case "OFLC":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=ofcl/e";
                            break;
                        case 2:
                            newRatingValue = "Parental=ofcl/g";
                            break;
                        case 3:
                            newRatingValue = "Parental=ofcl/pg";
                            break;
                        case 4:
                            newRatingValue = "Parental=ofcl/m";
                            break;
                        case 5:
                            newRatingValue = "Parental=ofcl/ma";
                            break;
                        case 6:
                            newRatingValue = "Parental=ofcl/r";
                            break;
                        case 7:
                            newRatingValue = "Parental=ofcl/x";
                            break;
                    }
                    break;
                case "PEGI":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=pegi/3";
                            break;
                        case 2:
                            newRatingValue = "Parental=pegi/7";
                            break;
                        case 3:
                            newRatingValue = "Parental=pegi/12";
                            break;
                        case 4:
                            newRatingValue = "Parental=pegi/16";
                            break;
                        case 5:
                            newRatingValue = "Parental=pegi/18";
                            break;
                    }
                    break;
                case "USK":
                    switch (parentalRating) {
                        case 1:
                            newRatingValue = "Parental=usk/0";
                            break;
                        case 2:
                            newRatingValue = "Parental=usk/6";
                            break;
                        case 3:
                            newRatingValue = "Parental=usk/12";
                            break;
                        case 4:
                            newRatingValue = "Parental=usk/16";
                            break;
                        case 5:
                            newRatingValue = "Parental=usk/18";
                            break;
                    }
                    break;  
            }
        
        return newRatingValue;
    }
    
    
    // This detemines the code depending on which compatible device checkboxes have been selected
    private String convertCompatibleDevices(){

        String code = null;
        
        if (jCheckBoxUSB.isSelected() && jCheckBoxETH.isSelected() && jCheckBoxHDD.isSelected()) {code = "all";}          // ETH, USB, HDD
        else if (!jCheckBoxUSB.isSelected() && !jCheckBoxETH.isSelected() && jCheckBoxHDD.isSelected()) {code = "6";}     // HDD
        else if (!jCheckBoxUSB.isSelected() && jCheckBoxETH.isSelected() && !jCheckBoxHDD.isSelected()) {code = "5";}     // ETH
        else if (!jCheckBoxUSB.isSelected() && jCheckBoxETH.isSelected() && jCheckBoxHDD.isSelected()) {code = "4";}      // ETH, HDD
        else if (jCheckBoxUSB.isSelected() && !jCheckBoxETH.isSelected() && jCheckBoxHDD.isSelected()) {code = "3";}      // USB, HDD
        else if (jCheckBoxUSB.isSelected() && jCheckBoxETH.isSelected() && !jCheckBoxHDD.isSelected()) {code = "2";}      // USB, ETH
        else if (jCheckBoxUSB.isSelected() && !jCheckBoxETH.isSelected() && !jCheckBoxHDD.isSelected()) {code = "1";}     // USB
        
        return code;
    }
    
    
    // This is used to determine which compatibility mode checkboxes should be selected
    private void readCompatibilityMode(String compatibilityMode){

        int mode = Integer.parseInt(compatibilityMode);     // Convert the number from a string to and int
        String binaryMode = Integer.toString(mode,2);       // Convert the int to a binary string
        binaryMode = reverseString(binaryMode);             // Reverse the binary string
        String[] binaryModeArray = binaryMode.split("");    // Convert the reversed binary string to an array
        
        // Loop through the array and determine which checkboxes should be selected
        for (int i = 0; i < binaryModeArray.length; i++) {if (binaryModeArray[i].equals("1")) {setCompatibilityModeCheckBox(i+1);}}
    }
    

    // This determines the index for the scan mode combo box
    private void checkScan(String scanCode){

        switch (scanCode) {
            case "240p":
                jComboBoxScan.setSelectedIndex(1);
                break;
            case "240p1":
                jComboBoxScan.setSelectedIndex(2);
                break;
            case "480i":
                jComboBoxScan.setSelectedIndex(3);
                break;
            case "480p":
                jComboBoxScan.setSelectedIndex(4);
                break;
            case "480p1":
                jComboBoxScan.setSelectedIndex(5);
                break;
            case "480p2":
                jComboBoxScan.setSelectedIndex(6);
                break;
            case "480p3":
                jComboBoxScan.setSelectedIndex(7);
                break;
            case "480p4":
                jComboBoxScan.setSelectedIndex(8);
                break;
            case "480p5":
                jComboBoxScan.setSelectedIndex(9);
                break;
            case "576i":
                jComboBoxScan.setSelectedIndex(10);
                break;
            case "576p":
                jComboBoxScan.setSelectedIndex(11);
                break;
            case "720p":
                jComboBoxScan.setSelectedIndex(12);
                break;
            case "1080i":
                jComboBoxScan.setSelectedIndex(13);
                break;
            case "1080i2":
                jComboBoxScan.setSelectedIndex(14);
                break;
            case "1080p":
                jComboBoxScan.setSelectedIndex(15);
                break;
            default:
                break;
        }
    }
    
    
    // This determines the scan code using the index of the scan mode combo box
    private String convertScanRate(){
        
        String code = null;
        switch (jComboBoxScan.getSelectedIndex()) {
            case 1:
                code = "240p";
                break;
            case 2:
                code = "240p1";
                break;
            case 3:
                code = "480i";
                break;
            case 4:
                code = "480p";
                break;
            case 5:
                code = "480p1";
                break;
            case 6:
                code = "480p2";
                break;
            case 7:
                code = "480p3";
                break;
            case 8:
                code = "480p4";
                break;
            case 9:
                code = "480p5";
                break;
            case 10:
                code = "576i";
                break;
            case 11:
                code = "576p";
                break;
            case 12:
                code = "720p";
                break;
            case 13:
                code = "1080i";
                break;
            case 14:
                code = "1080i2";
                break;
            case 15:
                code = "1080p";
                break;
            default:
                break;
        }
        
        return code;
    }
    
    
    // This sets the compatibility mode checkboxes
    private void setCompatibilityModeCheckBox(int checkBoxNumber){

        switch (checkBoxNumber) {
            case 1:
                jCheckBoxMode1.setSelected(true);
                break;
            case 2:
                jCheckBoxMode2.setSelected(true);
                break;
            case 3:
                jCheckBoxMode3.setSelected(true);
                break;
            case 4:
                jCheckBoxMode4.setSelected(true);
                break;
            case 5:
                jCheckBoxMode5.setSelected(true);
                break;
            case 6:
                jCheckBoxMode6.setSelected(true);
                break;
            case 7:
                jCheckBoxMode7.setSelected(true);
                break;
            case 8:
                jCheckBoxMode8.setSelected(true);
                break;
            default:
                break;
        }
    }

    
    // This detemines which compatible device checkboxes should be selected
    private void checkCompatibleDevices(String deviceCode){
        
        switch (deviceCode) {
            case "1":
                jCheckBoxUSB.setSelected(true);
                break;
            case "2":
                jCheckBoxUSB.setSelected(true);
                jCheckBoxETH.setSelected(true);
                break;
            case "3":
                jCheckBoxUSB.setSelected(true);
                jCheckBoxHDD.setSelected(true);
                jComboBoxDMA.setEnabled(true);
                break;
            case "4":
                jCheckBoxHDD.setSelected(true);
                jCheckBoxETH.setSelected(true);
                jComboBoxDMA.setEnabled(true);
                break;
            case "5":
                jCheckBoxETH.setSelected(true);
                break;
            case "6":
                jCheckBoxHDD.setSelected(true);
                jComboBoxDMA.setEnabled(true);
                break;
            case "all":
                jCheckBoxUSB.setSelected(true);
                jCheckBoxHDD.setSelected(true);
                jCheckBoxETH.setSelected(true);
                jComboBoxDMA.setEnabled(true);
                break;
            default:
                break;
        }
    }

   
    // This determines the index for the aspect ratio combo box
    private void checkAspectRatio(String aspectCode){
        
        switch (aspectCode) {
            case "s":
                jComboBoxAspectRatio.setSelectedIndex(1);
                break;
            case "w":
                jComboBoxAspectRatio.setSelectedIndex(2);
                break;
            case "w1":
                jComboBoxAspectRatio.setSelectedIndex(3);
                break;
            case "w2":
                jComboBoxAspectRatio.setSelectedIndex(4);
                break;
            default:
                break;
        }
    }
    
    
    // This detemines the code depending combob box selection
    private String convertAspectRation(){
        
        String code = null;

        switch (jComboBoxAspectRatio.getSelectedIndex()) {
            case 1:
                code = "s";
                break;
            case 2:
                code = "w";
                break;
            case 3:
                code = "w1";
                break;
            case 4:
                code = "w2";
                break;
            default:
                break;
        }
        
        return code;
    }
    
    
    // This enables or disables the GUI controls
    private void guiEnabled(boolean enabled){
        
        jComboBoxVMode.setEnabled(enabled);
        jComboBoxAspectRatio.setEnabled(enabled);
        jComboBoxScan.setEnabled(enabled);
        jComboBoxGenre.setEnabled(enabled);
        jComboBoxNoOfPlayers.setEnabled(enabled);
        jComboBoxCheat.setEnabled(enabled);
        jTextFieldDeveloper.setEnabled(enabled);
        jTextFieldReleaseDate.setEnabled(enabled);
        jTextFieldNotes.setEnabled(enabled);
        jTextAreaGameDescription.setEnabled(enabled);
        jCheckBoxMode1.setEnabled(enabled);
        jCheckBoxMode2.setEnabled(enabled);
        jCheckBoxMode3.setEnabled(enabled);
        jCheckBoxMode4.setEnabled(enabled);
        jCheckBoxMode5.setEnabled(enabled);
        jCheckBoxMode6.setEnabled(enabled);
        jCheckBoxMode7.setEnabled(enabled);
        jCheckBoxMode8.setEnabled(enabled);
        jCheckBoxVMC0.setEnabled(enabled);
        jCheckBoxVMC1.setEnabled(enabled);
        jCheckBoxETH.setEnabled(enabled);
        jCheckBoxUSB.setEnabled(enabled);
        jCheckBoxHDD.setEnabled(enabled);
        jButtonSaveConfig.setEnabled(enabled);
        jButtonDeleteConfig.setEnabled(enabled);
        jCheckBoxGSMEnabled.setEnabled(enabled);
        jComboBoxRatingSystem.setEnabled(enabled);
        jButtonRatingDecrease.setEnabled(enabled);
        jButtonRatingIncrease.setEnabled(enabled);
        guiActive = enabled;
        jLabelStar1.setEnabled(enabled);
        jLabelStar2.setEnabled(enabled);
        jLabelStar3.setEnabled(enabled);
        jLabelStar4.setEnabled(enabled);
        jLabelStar5.setEnabled(enabled);
        jCheckBoxCheatEnabled.setEnabled(enabled);
    }
    
    
    // This clears all of the controls in the GUI
    private void clearGUI(){
        
        jComboBoxVMode.setSelectedIndex(0);
        jComboBoxAspectRatio.setSelectedIndex(0);
        jComboBoxScan.setSelectedIndex(0);
        jComboBoxGenre.setSelectedIndex(0);
        jComboBoxNoOfPlayers.setSelectedIndex(0);
        jComboBoxDMA.setSelectedIndex(7);
        jComboBoxGSMVMode.setSelectedIndex(0);
        jSpinnerHPos.setValue(0);
        jSpinnerVPos.setValue(0);
        jComboBoxCheat.setSelectedIndex(0);
        jTextFieldDeveloper.setText("");
        jTextFieldReleaseDate.setText("");
        jTextFieldNotes.setText("");
        jTextAreaGameDescription.setText("");
        jTextFieldVMC0.setText("");
        jTextFieldVMC1.setText("");
        jCheckBoxMode1.setSelected(false);
        jCheckBoxMode2.setSelected(false);
        jCheckBoxMode3.setSelected(false);
        jCheckBoxMode4.setSelected(false);
        jCheckBoxMode5.setSelected(false);
        jCheckBoxMode6.setSelected(false);
        jCheckBoxMode7.setSelected(false);
        jCheckBoxMode8.setSelected(false);
        jCheckBoxVMC0.setSelected(false);
        jCheckBoxVMC1.setSelected(false);
        jTextFieldVMC0.setEditable(false);
        jTextFieldVMC1.setEditable(false);
        jCheckBoxETH.setSelected(false);
        jCheckBoxUSB.setSelected(false);
        jCheckBoxHDD.setSelected(false);
        jCheckBoxGSMEnabled.setSelected(false);     
        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(NO_RATING_IMAGE_PATH).getImage()));
        jCheckBoxCheatEnabled.setSelected(false);
    }
    
    
    // This is called when the mouse click event is called on a star label
    private void mouseClickStar(int starNumber){
        
        if (guiActive){
            switch (starNumber) {
                case 0:
                    jLabelPersonalRating.setText("Personal Rating: 0");
                    break;
                case 1:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelPersonalRating.setText("Personal Rating: 1");
                    break;
                case 2:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelPersonalRating.setText("Personal Rating: 2");
                    break;
                case 3:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelPersonalRating.setText("Personal Rating: 3");
                    break;
                case 4:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelPersonalRating.setText("Personal Rating: 4");
                    break;
                case 5:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelPersonalRating.setText("Personal Rating: 5");
                    break;
                default:
                    break;
            } 
        }

        userRatingSet = starNumber != 0;
        userRatingValue = starNumber;
    }

    
    // This is called when the mouse exit event is called on a star label
    private void mouseExistStar(int starNumber){

        if (guiActive){
            switch (starNumber) {
                case 1:
                    if (userRatingSet){switchStarImage();}
                    else {
                        jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    }
                    break;
                case 2:
                    if (userRatingSet){switchStarImage();}
                    else {
                        jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    }
                    break;
                case 3:
                    if (userRatingSet){switchStarImage();}
                    else {
                        jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    }
                    break;
                case 4:
                    if (userRatingSet){switchStarImage();}
                    else {
                        jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    }
                    break;
                case 5:
                    if (userRatingSet){switchStarImage();}
                    else {
                        jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                        jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    }
                    break;
                default:
                    break;
            }
        } 
    }
    
    
    // This is called when the mouse enter event is called on a star label
    private void mouseEnterStar(int starNumber){
        
        if (guiActive){
            switch (starNumber) {
                case 1:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 2:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 3:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 4:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 5:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    break;
                default:
                    break;
            } 
        } 
    }
    

    // This switches the star rating depending on which star has been clicked
    private void switchStarImage(){
        
        if (guiActive){
            switch (userRatingValue) {
                case 1:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 2:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 3:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 4:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_INACTIVE_IMAGE_PATH).getImage()));
                    break;
                case 5:
                    jLabelStar1.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar2.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar3.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar4.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    jLabelStar5.setIcon(new ImageIcon(new ImageIcon(STAR_ACTIVE_IMAGE_PATH).getImage()));
                    break;
                default:
                    break;
            } 
        }
    }
    // </editor-fold>
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanelGameDetails = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxGenre = new javax.swing.JComboBox<>();
        jTextFieldDeveloper = new javax.swing.JTextField();
        jTextFieldReleaseDate = new javax.swing.JTextField();
        jTextFieldNotes = new javax.swing.JTextField();
        jComboBoxNoOfPlayers = new javax.swing.JComboBox<>();
        jTextFieldGameTitle = new javax.swing.JTextField();
        jTextFieldGameID = new javax.swing.JTextField();
        jPanelGameSettings = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jComboBoxVMode = new javax.swing.JComboBox<>();
        jComboBoxAspectRatio = new javax.swing.JComboBox<>();
        jComboBoxScan = new javax.swing.JComboBox<>();
        jComboBoxCheat = new javax.swing.JComboBox<>();
        jCheckBoxCheatEnabled = new javax.swing.JCheckBox();
        jPanelCompatibilityMode = new javax.swing.JPanel();
        jCheckBoxMode1 = new javax.swing.JCheckBox();
        jCheckBoxMode2 = new javax.swing.JCheckBox();
        jCheckBoxMode3 = new javax.swing.JCheckBox();
        jCheckBoxMode4 = new javax.swing.JCheckBox();
        jCheckBoxMode5 = new javax.swing.JCheckBox();
        jCheckBoxMode6 = new javax.swing.JCheckBox();
        jCheckBoxMode7 = new javax.swing.JCheckBox();
        jCheckBoxMode8 = new javax.swing.JCheckBox();
        jPanelCompatibleDevices = new javax.swing.JPanel();
        jCheckBoxETH = new javax.swing.JCheckBox();
        jCheckBoxUSB = new javax.swing.JCheckBox();
        jCheckBoxHDD = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jComboBoxDMA = new javax.swing.JComboBox<>();
        jPanelVirtualMemoryCard = new javax.swing.JPanel();
        jCheckBoxVMC0 = new javax.swing.JCheckBox();
        jCheckBoxVMC1 = new javax.swing.JCheckBox();
        jTextFieldVMC0 = new javax.swing.JTextField();
        jTextFieldVMC1 = new javax.swing.JTextField();
        jPanelGSM = new javax.swing.JPanel();
        jCheckBoxGSMEnabled = new javax.swing.JCheckBox();
        jSpinnerHPos = new javax.swing.JSpinner();
        jSpinnerVPos = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jCheckBoxGSMSkipVideos = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        jComboBoxGSMVMode = new javax.swing.JComboBox<>();
        jPanelGameRating = new javax.swing.JPanel();
        jLabelPersonalRating = new javax.swing.JLabel();
        jLabelStar1 = new javax.swing.JLabel();
        jLabelStar2 = new javax.swing.JLabel();
        jLabelStar3 = new javax.swing.JLabel();
        jLabelStar4 = new javax.swing.JLabel();
        jLabelStar5 = new javax.swing.JLabel();
        jLabelRatingImage = new javax.swing.JLabel();
        jButtonRatingIncrease = new javax.swing.JButton();
        jButtonRatingDecrease = new javax.swing.JButton();
        jComboBoxRatingSystem = new javax.swing.JComboBox<>();
        jPanelGameDescription = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaGameDescription = new javax.swing.JTextArea();
        jButtonSaveConfig = new javax.swing.JButton();
        jButtonNewConfig = new javax.swing.JButton();
        jButtonDownloadConfig = new javax.swing.JButton();
        jButtonNextGame = new javax.swing.JButton();
        jButtonPreviousGame = new javax.swing.JButton();
        jButtonDeleteConfig = new javax.swing.JButton();
        jTextFieldGameNumber = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel1.setText("Game Title:");
        jLabel1.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel2.setText("Game ID:");
        jLabel2.setPreferredSize(new java.awt.Dimension(54, 25));

        jPanelGameDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Details"));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Developer:");
        jLabel8.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Release Date:");
        jLabel9.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Notes:");
        jLabel10.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("No of Players:");
        jLabel3.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Genre:");
        jLabel7.setPreferredSize(new java.awt.Dimension(54, 25));

        jComboBoxGenre.setEditable(true);
        jComboBoxGenre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Action", "Adventure", "Arcade", "Board", "Cards", "Compilation", "Dance", "Exercise", "Fighting", "First-Person Shooter", "Horror", "Hunting", "Miscellaneous", "Music", "Open World", "Pinball", "Platform", "Puzzle", "Racing", "RPG", "Sci-Fi", "Shooter", "Simulation", "Sport", "Strategy", "Survival", "Third-Person Action", "Third-Person Shooter", "Turn-Based Strategy", "Visual Novel" }));
        jComboBoxGenre.setEnabled(false);
        jComboBoxGenre.setPreferredSize(new java.awt.Dimension(127, 25));

        jTextFieldDeveloper.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDeveloper.setEnabled(false);
        jTextFieldDeveloper.setPreferredSize(new java.awt.Dimension(198, 25));

        jTextFieldReleaseDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldReleaseDate.setEnabled(false);
        jTextFieldReleaseDate.setPreferredSize(new java.awt.Dimension(198, 25));

        jTextFieldNotes.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldNotes.setEnabled(false);
        jTextFieldNotes.setPreferredSize(new java.awt.Dimension(198, 25));

        jComboBoxNoOfPlayers.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Not Set", "1 Player", "2 Player", "3 Player", "4 Player" }));
        jComboBoxNoOfPlayers.setEnabled(false);
        jComboBoxNoOfPlayers.setPreferredSize(new java.awt.Dimension(64, 25));

        javax.swing.GroupLayout jPanelGameDetailsLayout = new javax.swing.GroupLayout(jPanelGameDetails);
        jPanelGameDetails.setLayout(jPanelGameDetailsLayout);
        jPanelGameDetailsLayout.setHorizontalGroup(
            jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDetailsLayout.createSequentialGroup()
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxNoOfPlayers, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxGenre, 0, 158, Short.MAX_VALUE)
                    .addComponent(jTextFieldDeveloper, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(jTextFieldReleaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelGameDetailsLayout.setVerticalGroup(
            jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxGenre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDeveloper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldReleaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxNoOfPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextFieldGameTitle.setEditable(false);
        jTextFieldGameTitle.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameTitle.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldGameTitle.setPreferredSize(new java.awt.Dimension(656, 25));

        jTextFieldGameID.setEditable(false);
        jTextFieldGameID.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameID.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldGameID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameID.setPreferredSize(new java.awt.Dimension(160, 25));

        jPanelGameSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Settings"));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("V-Mode:");
        jLabel4.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Aspect:");
        jLabel5.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Scan:");
        jLabel6.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Cheat:");
        jLabel11.setPreferredSize(new java.awt.Dimension(54, 25));

        jComboBoxVMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Not Set", "PAL", "NTSC", "MULTI" }));
        jComboBoxVMode.setEnabled(false);
        jComboBoxVMode.setPreferredSize(new java.awt.Dimension(260, 25));

        jComboBoxAspectRatio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Not Set", "Standard - (4:3)", "Widescreen - (16:9)", "Widescreen - (ps2rd Hack)", "Widescreen - (HEX ISO HACK)" }));
        jComboBoxAspectRatio.setEnabled(false);
        jComboBoxAspectRatio.setPreferredSize(new java.awt.Dimension(260, 25));

        jComboBoxScan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Not Set", "240p - (Default For Some NTSC Games)", "240p - (HEX ISO HACK)", "480i - (Default For NTSC Games)", "480p - (Can Be Set in Game's Settings)", "480p - (Press Triangle and Cross)", "480p - (Press Circle and Cross)", "480p - (GSM Settings)", "480p - (ps2rd Hack)", "480p - (HEX ISO HACK)", "576i - (Default For PAL Games)", "576p - (GSM Setting)", "720p - (GSM Setting)", "1080i - (Can Be Set in Game's Settings)", "1080i - (GSM Settings)", "1080p - (GSM Settings)" }));
        jComboBoxScan.setEnabled(false);
        jComboBoxScan.setMinimumSize(new java.awt.Dimension(214, 27));
        jComboBoxScan.setPreferredSize(new java.awt.Dimension(260, 25));

        jComboBoxCheat.setEditable(true);
        jComboBoxCheat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Codebreaker", "ps2rd" }));
        jComboBoxCheat.setPreferredSize(new java.awt.Dimension(120, 20));

        jCheckBoxCheatEnabled.setText("Enable Cheats");

        javax.swing.GroupLayout jPanelGameSettingsLayout = new javax.swing.GroupLayout(jPanelGameSettings);
        jPanelGameSettings.setLayout(jPanelGameSettingsLayout);
        jPanelGameSettingsLayout.setHorizontalGroup(
            jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameSettingsLayout.createSequentialGroup()
                .addGroup(jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxAspectRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelGameSettingsLayout.createSequentialGroup()
                        .addComponent(jComboBoxCheat, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBoxCheatEnabled))
                    .addComponent(jComboBoxVMode, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameSettingsLayout.setVerticalGroup(
            jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameSettingsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxVMode, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxAspectRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxScan, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelGameSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxCheat, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCheckBoxCheatEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11))
        );

        jPanelCompatibilityMode.setBorder(javax.swing.BorderFactory.createTitledBorder("Compatibility Mode"));

        jCheckBoxMode1.setText("Mode 1");
        jCheckBoxMode1.setEnabled(false);

        jCheckBoxMode2.setText("Mode 2");
        jCheckBoxMode2.setEnabled(false);

        jCheckBoxMode3.setText("Mode 3");
        jCheckBoxMode3.setEnabled(false);

        jCheckBoxMode4.setText("Mode 4");
        jCheckBoxMode4.setEnabled(false);

        jCheckBoxMode5.setText("Mode 5");
        jCheckBoxMode5.setEnabled(false);

        jCheckBoxMode6.setText("Mode 6");
        jCheckBoxMode6.setEnabled(false);

        jCheckBoxMode7.setText("Mode 7");
        jCheckBoxMode7.setEnabled(false);

        jCheckBoxMode8.setText("Mode 8");
        jCheckBoxMode8.setEnabled(false);

        javax.swing.GroupLayout jPanelCompatibilityModeLayout = new javax.swing.GroupLayout(jPanelCompatibilityMode);
        jPanelCompatibilityMode.setLayout(jPanelCompatibilityModeLayout);
        jPanelCompatibilityModeLayout.setHorizontalGroup(
            jPanelCompatibilityModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompatibilityModeLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanelCompatibilityModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxMode4)
                    .addComponent(jCheckBoxMode3)
                    .addComponent(jCheckBoxMode2)
                    .addComponent(jCheckBoxMode1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelCompatibilityModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxMode8)
                    .addComponent(jCheckBoxMode7)
                    .addComponent(jCheckBoxMode6)
                    .addComponent(jCheckBoxMode5))
                .addGap(28, 28, 28))
        );
        jPanelCompatibilityModeLayout.setVerticalGroup(
            jPanelCompatibilityModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompatibilityModeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCompatibilityModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCompatibilityModeLayout.createSequentialGroup()
                        .addComponent(jCheckBoxMode5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxMode6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxMode7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxMode8))
                    .addGroup(jPanelCompatibilityModeLayout.createSequentialGroup()
                        .addComponent(jCheckBoxMode1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxMode2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxMode3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxMode4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelCompatibleDevices.setBorder(javax.swing.BorderFactory.createTitledBorder("Compatible Devices"));

        jCheckBoxETH.setText("ETH");
        jCheckBoxETH.setEnabled(false);

        jCheckBoxUSB.setText("USB");
        jCheckBoxUSB.setEnabled(false);

        jCheckBoxHDD.setText("HDD");
        jCheckBoxHDD.setEnabled(false);
        jCheckBoxHDD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxHDDActionPerformed(evt);
            }
        });

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("DMA:");
        jLabel12.setPreferredSize(new java.awt.Dimension(54, 25));

        jComboBoxDMA.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MDMA0", "MDMA1", "MDMA2", "UDMA0", "UDMA1", "UDMA2", "UDMA3", "UDMA4" }));
        jComboBoxDMA.setEnabled(false);
        jComboBoxDMA.setPreferredSize(new java.awt.Dimension(61, 25));

        javax.swing.GroupLayout jPanelCompatibleDevicesLayout = new javax.swing.GroupLayout(jPanelCompatibleDevices);
        jPanelCompatibleDevices.setLayout(jPanelCompatibleDevicesLayout);
        jPanelCompatibleDevicesLayout.setHorizontalGroup(
            jPanelCompatibleDevicesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompatibleDevicesLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jComboBoxDMA, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCompatibleDevicesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBoxETH)
                .addGap(33, 33, 33)
                .addComponent(jCheckBoxUSB)
                .addGap(32, 32, 32)
                .addComponent(jCheckBoxHDD)
                .addGap(21, 21, 21))
        );
        jPanelCompatibleDevicesLayout.setVerticalGroup(
            jPanelCompatibleDevicesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCompatibleDevicesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCompatibleDevicesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxETH)
                    .addComponent(jCheckBoxUSB)
                    .addComponent(jCheckBoxHDD))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanelCompatibleDevicesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxDMA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        jPanelVirtualMemoryCard.setBorder(javax.swing.BorderFactory.createTitledBorder("Virtual Memory Cards"));

        jCheckBoxVMC0.setText("MC0");
        jCheckBoxVMC0.setEnabled(false);
        jCheckBoxVMC0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxVMC0ActionPerformed(evt);
            }
        });

        jCheckBoxVMC1.setText("MC1");
        jCheckBoxVMC1.setEnabled(false);
        jCheckBoxVMC1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxVMC1ActionPerformed(evt);
            }
        });

        jTextFieldVMC0.setEditable(false);
        jTextFieldVMC0.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldVMC0.setPreferredSize(new java.awt.Dimension(197, 25));

        jTextFieldVMC1.setEditable(false);
        jTextFieldVMC1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldVMC1.setPreferredSize(new java.awt.Dimension(197, 25));

        javax.swing.GroupLayout jPanelVirtualMemoryCardLayout = new javax.swing.GroupLayout(jPanelVirtualMemoryCard);
        jPanelVirtualMemoryCard.setLayout(jPanelVirtualMemoryCardLayout);
        jPanelVirtualMemoryCardLayout.setHorizontalGroup(
            jPanelVirtualMemoryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVirtualMemoryCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVirtualMemoryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelVirtualMemoryCardLayout.createSequentialGroup()
                        .addComponent(jCheckBoxVMC1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldVMC1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelVirtualMemoryCardLayout.createSequentialGroup()
                        .addComponent(jCheckBoxVMC0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldVMC0, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelVirtualMemoryCardLayout.setVerticalGroup(
            jPanelVirtualMemoryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVirtualMemoryCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVirtualMemoryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxVMC0)
                    .addComponent(jTextFieldVMC0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelVirtualMemoryCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxVMC1)
                    .addComponent(jTextFieldVMC1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelGSM.setBorder(javax.swing.BorderFactory.createTitledBorder("GSM"));

        jCheckBoxGSMEnabled.setText("Enabled");
        jCheckBoxGSMEnabled.setEnabled(false);
        jCheckBoxGSMEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxGSMEnabledActionPerformed(evt);
            }
        });

        jSpinnerHPos.setEnabled(false);
        jSpinnerHPos.setPreferredSize(new java.awt.Dimension(29, 25));

        jSpinnerVPos.setEnabled(false);
        jSpinnerVPos.setPreferredSize(new java.awt.Dimension(29, 25));

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("H-Pos");
        jLabel14.setPreferredSize(new java.awt.Dimension(54, 20));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("V-Pos");
        jLabel15.setPreferredSize(new java.awt.Dimension(54, 20));

        jCheckBoxGSMSkipVideos.setText("Skip Videos");
        jCheckBoxGSMSkipVideos.setEnabled(false);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("V-Mode:");
        jLabel13.setPreferredSize(new java.awt.Dimension(54, 20));

        jComboBoxGSMVMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NTSC", "NTSC Non Interlaced", "PAL", "PAL Non Interlaced", "PAL @60hz", "PAL @60hz Non Interlaced", "PS1 NTSC (HDTV 480p @60hz)", "PS1 PAL (HDTV 576p @50hz)", "HDTV 480p @60hz", "HDTV 576p @50hz", "HDTV 720p @60hz", "HDTV 1080i @60hz", "HDTV 1080i @60hz Non Interlaced", "HDTV 1080p @60hz", "VGA 640x480p @60hz", "VGA 640x480p @72hz", "VGA 640x480p @75hz", "VGA 640x480p @85hz", "VGA 640x480i @60hz" }));
        jComboBoxGSMVMode.setEnabled(false);
        jComboBoxGSMVMode.setPreferredSize(new java.awt.Dimension(190, 25));

        javax.swing.GroupLayout jPanelGSMLayout = new javax.swing.GroupLayout(jPanelGSM);
        jPanelGSM.setLayout(jPanelGSMLayout);
        jPanelGSMLayout.setHorizontalGroup(
            jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGSMLayout.createSequentialGroup()
                .addGroup(jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanelGSMLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxGSMEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxGSMSkipVideos))
                        .addGap(45, 45, 45)
                        .addGroup(jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jSpinnerHPos, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinnerVPos, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelGSMLayout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxGSMVMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGSMLayout.setVerticalGroup(
            jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGSMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGSMLayout.createSequentialGroup()
                        .addGroup(jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelGSMLayout.createSequentialGroup()
                                .addComponent(jCheckBoxGSMEnabled)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jCheckBoxGSMSkipVideos)
                                .addGap(6, 6, 6))
                            .addGroup(jPanelGSMLayout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinnerVPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanelGSMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxGSMVMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14))
                    .addGroup(jPanelGSMLayout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerHPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))))
        );

        jPanelGameRating.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Rating"));

        jLabelPersonalRating.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPersonalRating.setText("Personal Rating: 0");

        jLabelRatingImage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabelRatingImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRatingImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonRatingIncrease.setText("+");
        jButtonRatingIncrease.setPreferredSize(new java.awt.Dimension(50, 23));
        jButtonRatingIncrease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRatingIncreaseActionPerformed(evt);
            }
        });

        jButtonRatingDecrease.setText("-");
        jButtonRatingDecrease.setPreferredSize(new java.awt.Dimension(50, 23));
        jButtonRatingDecrease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRatingDecreaseActionPerformed(evt);
            }
        });

        jComboBoxRatingSystem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BBFC", "CERO", "DEJUS", "ELSPA", "ESRA", "ESRB", "OFLC", "PEGI", "USK" }));
        jComboBoxRatingSystem.setPreferredSize(new java.awt.Dimension(56, 25));
        jComboBoxRatingSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxRatingSystemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGameRatingLayout = new javax.swing.GroupLayout(jPanelGameRating);
        jPanelGameRating.setLayout(jPanelGameRatingLayout);
        jPanelGameRatingLayout.setHorizontalGroup(
            jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRatingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPersonalRating, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelGameRatingLayout.createSequentialGroup()
                        .addComponent(jLabelRatingImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxRatingSystem, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelGameRatingLayout.createSequentialGroup()
                                .addComponent(jButtonRatingDecrease, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonRatingIncrease, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanelGameRatingLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabelStar1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelStar2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelStar3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelStar4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelStar5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameRatingLayout.setVerticalGroup(
            jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRatingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameRatingLayout.createSequentialGroup()
                        .addComponent(jComboBoxRatingSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonRatingIncrease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonRatingDecrease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabelRatingImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelStar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelStar3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameRatingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelStar4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelStar5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPersonalRating, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelGameDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Description"));

        jTextAreaGameDescription.setColumns(20);
        jTextAreaGameDescription.setLineWrap(true);
        jTextAreaGameDescription.setRows(5);
        jTextAreaGameDescription.setWrapStyleWord(true);
        jTextAreaGameDescription.setAutoscrolls(false);
        jTextAreaGameDescription.setEnabled(false);
        jScrollPane1.setViewportView(jTextAreaGameDescription);

        javax.swing.GroupLayout jPanelGameDescriptionLayout = new javax.swing.GroupLayout(jPanelGameDescription);
        jPanelGameDescription.setLayout(jPanelGameDescriptionLayout);
        jPanelGameDescriptionLayout.setHorizontalGroup(
            jPanelGameDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDescriptionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanelGameDescriptionLayout.setVerticalGroup(
            jPanelGameDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDescriptionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jButtonSaveConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Save Icon.png"))); // NOI18N
        jButtonSaveConfig.setContentAreaFilled(false);
        jButtonSaveConfig.setEnabled(false);
        jButtonSaveConfig.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonSaveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveConfigActionPerformed(evt);
            }
        });

        jButtonNewConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/New Folder Icon.png"))); // NOI18N
        jButtonNewConfig.setContentAreaFilled(false);
        jButtonNewConfig.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonNewConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewConfigActionPerformed(evt);
            }
        });

        jButtonDownloadConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonDownloadConfig.setContentAreaFilled(false);
        jButtonDownloadConfig.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonDownloadConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadConfigActionPerformed(evt);
            }
        });

        jButtonNextGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Right Icon.png"))); // NOI18N
        jButtonNextGame.setContentAreaFilled(false);
        jButtonNextGame.setPreferredSize(new java.awt.Dimension(70, 23));
        jButtonNextGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextGameActionPerformed(evt);
            }
        });

        jButtonPreviousGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Left Icon.png"))); // NOI18N
        jButtonPreviousGame.setContentAreaFilled(false);
        jButtonPreviousGame.setPreferredSize(new java.awt.Dimension(70, 23));
        jButtonPreviousGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousGameActionPerformed(evt);
            }
        });

        jButtonDeleteConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Delete Icon.png"))); // NOI18N
        jButtonDeleteConfig.setToolTipText("");
        jButtonDeleteConfig.setContentAreaFilled(false);
        jButtonDeleteConfig.setEnabled(false);
        jButtonDeleteConfig.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonDeleteConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteConfigActionPerformed(evt);
            }
        });

        jTextFieldGameNumber.setEditable(false);
        jTextFieldGameNumber.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameNumber.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldGameNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameNumber.setText("[1/12]");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelGameRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonNewConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonDeleteConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(108, 108, 108)
                                .addComponent(jButtonDownloadConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonSaveConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanelGameDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanelGameSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextFieldGameID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jTextFieldGameNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanelGameDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addComponent(jPanelCompatibleDevices, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelGSM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelVirtualMemoryCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelCompatibilityMode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldGameID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldGameNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanelGameSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanelGameDetails, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelCompatibilityMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanelVirtualMemoryCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelCompatibleDevices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jPanelGSM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelGameRating, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelGameDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonSaveConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDownloadConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNewConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDeleteConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">     
    private void jCheckBoxVMC0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxVMC0ActionPerformed

        if (PopsGameManager.getCurrentConsole().equals("PS2")){
            if (jCheckBoxVMC0.isSelected()){
                jTextFieldVMC0.setEditable(true);
                jTextFieldVMC0.setText(gameList.get(currentListIndex).getGameID() + "_0");
            } 
            else {
                jTextFieldVMC0.setEditable(false);
                jTextFieldVMC0.setText("");
            }
        } 
        else {
            jCheckBoxVMC0.setSelected(false);
            JOptionPane.showMessageDialog(null,"This application cannot yet generate PS1 Virtual Memory Cards."," Unable to Upload PS1 Cheat File!",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jCheckBoxVMC0ActionPerformed

    private void jCheckBoxVMC1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxVMC1ActionPerformed
        
        if (PopsGameManager.getCurrentConsole().equals("PS2")){
            if (jCheckBoxVMC1.isSelected()){
                jTextFieldVMC1.setEditable(true);
                jTextFieldVMC1.setText(gameList.get(currentListIndex).getGameID() + "_1");
            } 
            else {
                jTextFieldVMC1.setEditable(false);
                jTextFieldVMC1.setText("");
            }
        }
        else {
            jCheckBoxVMC1.setSelected(false);
            JOptionPane.showMessageDialog(null,"This application cannot yet generate PS1 Virtual Memory Cards."," Unable to Upload PS1 Cheat File!",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jCheckBoxVMC1ActionPerformed

    private void jButtonSaveConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveConfigActionPerformed
        composeGameConfigData();
        writeGameConfigFile();
    }//GEN-LAST:event_jButtonSaveConfigActionPerformed

    private void jButtonDownloadConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadConfigActionPerformed
        
        // Send the UDP request for the specific image file
        String[] splitName = gameList.get(currentListIndex).getGameID().split("_");
        MyTCPClient tcpClient = new MyTCPClient();
        tcpClient.getConfigFromServer(PopsGameManager.determineGameRegion(splitName[0]),gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName(),false);

        if (PopsGameManager.getCurrentConsole().equals("PS1")) {initialiseGUI(currentListIndex,configManager.gameConfigExists(GameListManager.getGamePS1(currentListIndex).getGameID(),GameListManager.getGamePS1(currentListIndex).getGameName()));}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {initialiseGUI(currentListIndex,configManager.gameConfigExists(GameListManager.getGamePS2(currentListIndex).getGameID(),GameListManager.getGamePS1(currentListIndex).getGameName()));} 
    }//GEN-LAST:event_jButtonDownloadConfigActionPerformed

    private void jButtonNextGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextGameActionPerformed

        // Need to check if config data has changed, if so prompt the user if they want to save the changes
        composeGameConfigData();

        if (configManager.gameConfigExists(gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName())){
            if (!compareConfigData()) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "The config data has been modified. \n\nDo you want to save the file?"," Config data not saved!",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){writeGameConfigFile();}  
            }
        }

        if (currentListIndex < gameList.size()-1) {
            currentListIndex +=1;
            initialiseGUI(currentListIndex, configManager.gameConfigExists(gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName()));
        }
    }//GEN-LAST:event_jButtonNextGameActionPerformed

    private void jButtonPreviousGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousGameActionPerformed

        // Need to check if config data has changed, if so prompt the user if they want to save the changes
        composeGameConfigData();

        if (configManager.gameConfigExists(gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName())){
            if (!compareConfigData()) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "The config data has been modified. \n\nDo you want to save the file?"," Config data not saved!",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){writeGameConfigFile();}  
            }
        }
        
        if (currentListIndex > 0) {
            currentListIndex -=1;
            initialiseGUI(currentListIndex, configManager.gameConfigExists(gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName()));
        }
    }//GEN-LAST:event_jButtonPreviousGameActionPerformed

    private void jCheckBoxGSMEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxGSMEnabledActionPerformed
        jCheckBoxGSMSkipVideos.setEnabled(jCheckBoxGSMEnabled.isSelected());
        jComboBoxGSMVMode.setEnabled(jCheckBoxGSMEnabled.isSelected());
        jSpinnerHPos.setEnabled(jCheckBoxGSMEnabled.isSelected());
        jSpinnerVPos.setEnabled(jCheckBoxGSMEnabled.isSelected());
    }//GEN-LAST:event_jCheckBoxGSMEnabledActionPerformed

    private void jCheckBoxHDDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxHDDActionPerformed
        jComboBoxDMA.setEnabled(jCheckBoxHDD.isSelected());
    }//GEN-LAST:event_jCheckBoxHDDActionPerformed

    private void jButtonNewConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewConfigActionPerformed

        PrintWriter writer = null;
        try {writer = new PrintWriter(PopsGameManager.getOPLFolder() + "CFG" + File.separator + gameList.get(currentListIndex).getGameID() + ".cfg", "UTF-8");} 
        catch (FileNotFoundException | UnsupportedEncodingException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        
        if (writer != null){
            writer.println("CfgVersion=5");
            writer.println("Title=" + gameList.get(currentListIndex).getGameName());
            writer.close();
        }
        
        guiEnabled(true);
    }//GEN-LAST:event_jButtonNewConfigActionPerformed

    private void jButtonRatingIncreaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRatingIncreaseActionPerformed
        
        switch (jComboBoxRatingSystem.getSelectedItem().toString()) {
            case "BBFC":
                if (parentalRating <5) {parentalRating += 1;}
                break;
            case "CERO":
                if (parentalRating <7) {parentalRating += 1;}
                break;
            case "DEJUS":
                if (parentalRating <6) {parentalRating += 1;}
                break;
            case "ELSPA":
                if (parentalRating <4) {parentalRating += 1;}
                break;
            case "ESRA":
                if (parentalRating <5) {parentalRating += 1;}
                break;   
            case "ESRB":
                if (parentalRating <6) {parentalRating += 1;} 
                break;
            case "OFLC":
                if (parentalRating <7) {parentalRating += 1;}
                break;
            case "PEGI":
                if (parentalRating <5) {parentalRating += 1;} 
                break;
            case "USK":
                if (parentalRating <5) {parentalRating += 1;}
                break;   
        }

        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(NO_RATING_IMAGE_PATH).getImage())); 
        jLabelRatingImage.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        updateParentalRatingImage();
    }//GEN-LAST:event_jButtonRatingIncreaseActionPerformed

    private void jButtonRatingDecreaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRatingDecreaseActionPerformed

        if (parentalRating > 1) {parentalRating -= 1;}
        updateParentalRatingImage(); 
    }//GEN-LAST:event_jButtonRatingDecreaseActionPerformed

    private void jComboBoxRatingSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxRatingSystemActionPerformed
        
        parentalRating = 0;
        jLabelRatingImage.setIcon(new ImageIcon(new ImageIcon(NO_RATING_IMAGE_PATH).getImage())); 
    }//GEN-LAST:event_jComboBoxRatingSystemActionPerformed

    private void jButtonDeleteConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteConfigActionPerformed
        deleteConfigFile();
    }//GEN-LAST:event_jButtonDeleteConfigActionPerformed
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Variables">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeleteConfig;
    private javax.swing.JButton jButtonDownloadConfig;
    private javax.swing.JButton jButtonNewConfig;
    private javax.swing.JButton jButtonNextGame;
    private javax.swing.JButton jButtonPreviousGame;
    private javax.swing.JButton jButtonRatingDecrease;
    private javax.swing.JButton jButtonRatingIncrease;
    private javax.swing.JButton jButtonSaveConfig;
    private javax.swing.JCheckBox jCheckBoxCheatEnabled;
    private javax.swing.JCheckBox jCheckBoxETH;
    private javax.swing.JCheckBox jCheckBoxGSMEnabled;
    private javax.swing.JCheckBox jCheckBoxGSMSkipVideos;
    private javax.swing.JCheckBox jCheckBoxHDD;
    private javax.swing.JCheckBox jCheckBoxMode1;
    private javax.swing.JCheckBox jCheckBoxMode2;
    private javax.swing.JCheckBox jCheckBoxMode3;
    private javax.swing.JCheckBox jCheckBoxMode4;
    private javax.swing.JCheckBox jCheckBoxMode5;
    private javax.swing.JCheckBox jCheckBoxMode6;
    private javax.swing.JCheckBox jCheckBoxMode7;
    private javax.swing.JCheckBox jCheckBoxMode8;
    private javax.swing.JCheckBox jCheckBoxUSB;
    private javax.swing.JCheckBox jCheckBoxVMC0;
    private javax.swing.JCheckBox jCheckBoxVMC1;
    private javax.swing.JComboBox<String> jComboBoxAspectRatio;
    private javax.swing.JComboBox<String> jComboBoxCheat;
    private javax.swing.JComboBox<String> jComboBoxDMA;
    private javax.swing.JComboBox<String> jComboBoxGSMVMode;
    private javax.swing.JComboBox<String> jComboBoxGenre;
    private javax.swing.JComboBox<String> jComboBoxNoOfPlayers;
    private javax.swing.JComboBox<String> jComboBoxRatingSystem;
    private javax.swing.JComboBox<String> jComboBoxScan;
    private javax.swing.JComboBox<String> jComboBoxVMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelPersonalRating;
    private javax.swing.JLabel jLabelRatingImage;
    private javax.swing.JLabel jLabelStar1;
    private javax.swing.JLabel jLabelStar2;
    private javax.swing.JLabel jLabelStar3;
    private javax.swing.JLabel jLabelStar4;
    private javax.swing.JLabel jLabelStar5;
    private javax.swing.JPanel jPanelCompatibilityMode;
    private javax.swing.JPanel jPanelCompatibleDevices;
    private javax.swing.JPanel jPanelGSM;
    private javax.swing.JPanel jPanelGameDescription;
    private javax.swing.JPanel jPanelGameDetails;
    private javax.swing.JPanel jPanelGameRating;
    private javax.swing.JPanel jPanelGameSettings;
    private javax.swing.JPanel jPanelVirtualMemoryCard;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerHPos;
    private javax.swing.JSpinner jSpinnerVPos;
    private javax.swing.JTextArea jTextAreaGameDescription;
    private javax.swing.JTextField jTextFieldDeveloper;
    private javax.swing.JTextField jTextFieldGameID;
    private javax.swing.JTextField jTextFieldGameNumber;
    private javax.swing.JTextField jTextFieldGameTitle;
    private javax.swing.JTextField jTextFieldNotes;
    private javax.swing.JTextField jTextFieldReleaseDate;
    private javax.swing.JTextField jTextFieldVMC0;
    private javax.swing.JTextField jTextFieldVMC1;
    // End of variables declaration//GEN-END:variables
}// </editor-fold>