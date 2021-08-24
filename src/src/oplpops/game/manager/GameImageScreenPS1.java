package oplpops.game.manager;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class GameImageScreenPS1 extends javax.swing.JDialog implements ImageSelectListener, ImageChangedListener {
    
    private static final String NO_IMAGE_PS1_COVER_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Cover.png";
    private static final String NO_IMAGE_BACKGROUND_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Background.png";
    private static final String NO_IMAGE_SCREENSHOT_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Screenshot.png";
    private static final String NO_IMAGE_DISC_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Disc.png";
    
    private static List<Game> gameList;
    private int currentListIndex; 
    private final java.awt.Frame parent;
    private GameImageSelectorScreenPS1 imageSelectorScreen = null;
    
    public GameImageScreenPS1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        overideClose(); 
    }

    
    // Initialise the GUI elements
    public void initialiseGUI(int gameIndex){
        currentListIndex = gameIndex;
        getGameLists();
        displayGameNumber();
        displayGameName();
        displayGameImages();
        this.setTitle(" Manage PlayStation 1 Game ART");
    }

    
    // Overide the close operation
    private void overideClose(){

        // Callback to update the main GUI when this window is closed
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                PopsGameManager.callbackToUpdateGUIGameList(null, currentListIndex);
                dispose();
            }
        });
        //this.setTitle(" Select Mode");
    }
    

    // Callback from image selector screen (called when a user selects the image from the image selector screen) 
    @Override
    public void imageSelected(String coverType, File image) {

        if (coverType.equals("_COV")) if(image.exists() && !image.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
        if (coverType.equals("_COV2")) if(image.exists() && !image.isDirectory()) {jLabelGameRearCover.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), Image.SCALE_DEFAULT)));}
        if (coverType.equals("_ICO")) if(image.exists() && !image.isDirectory()) {jLabelGameDiscImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), Image.SCALE_DEFAULT)));}
        if (coverType.equals("_BG")) if(image.exists() && !image.isDirectory()) {jLabelGameBackgroundImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), Image.SCALE_DEFAULT)));}
        if (coverType.equals("_SCR")) if(image.exists() && !image.isDirectory()) {jLabelGameScreenshot1.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), Image.SCALE_DEFAULT)));}
        if (coverType.equals("_SCR2")) if(image.exists() && !image.isDirectory()) {jLabelGameScreenshot2.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), Image.SCALE_DEFAULT)));} 
    }
    
    
    // Callback from image selector screen (called when a user changes the image from the image selector screen)
    @Override
    public void imageChanged(String coverType, int currentImageNumber) {
        getNextImageFromServer(coverType, currentImageNumber);
    }
    

    // This gets the next image when there are multiple images available
    public void getNextImageFromServer(String coverType, int currentImageNumber){
        
        // Send the TCP request for the specific image file
        String[] splitName = gameList.get(currentListIndex).getGameID().split("_");
        
        String coverPath = coverType;
        
        MyTCPClient tcpClient = new MyTCPClient();
        tcpClient.getImageFromServer(gameList.get(currentListIndex), PopsGameManager.determineGameRegion(splitName[0]),gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName(),coverType, coverPath, currentImageNumber-1, false);

        File image; 
        if (coverType.equals("_ICO")) {image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + coverType + ".png");}
        else {image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + coverType + ".jpg");}

        if(image.exists() && !image.isDirectory()) {if (imageSelectorScreen != null){
            imageSelectorScreen.updateImage(image);}
        }
    }
    

    // This tries to get the image file from the server
    public void getImageFromServer(String coverType, String coverPath, int currentImageNumber){
        
        // Send the TCP request for the specific image file
        String[] splitName = gameList.get(currentListIndex).getGameID().split("_");
        int numberOfFiles = 0;

        MyTCPClient tcpClient = new MyTCPClient();
        numberOfFiles = tcpClient.getImagesAvailableOnServer(gameList.get(currentListIndex), PopsGameManager.determineGameRegion(splitName[0]),gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName(),coverType, false);
        
        if (numberOfFiles > 0){

            tcpClient.getImageFromServer(gameList.get(currentListIndex), PopsGameManager.determineGameRegion(splitName[0]),gameList.get(currentListIndex).getGameID(),gameList.get(currentListIndex).getGameName(),coverType, coverPath, currentImageNumber, false);

            File image; 
            if (coverType.equals("_ICO")) {image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + coverPath + ".png");}
            else {image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + coverPath + ".jpg");}

            if (coverPath.equals("_COV")) if(image.exists() && !image.isDirectory()) {displayImageSelectorScreen("_COV", image, numberOfFiles, currentImageNumber+1, gameList.get(currentListIndex).getGameID());}
            if (coverPath.equals("_COV2")) if(image.exists() && !image.isDirectory()) {displayImageSelectorScreen("_COV2", image, numberOfFiles, currentImageNumber+1, gameList.get(currentListIndex).getGameID());}
            if (coverPath.equals("_ICO")) if(image.exists() && !image.isDirectory()) {displayImageSelectorScreen("_ICO", image, numberOfFiles, currentImageNumber+1, gameList.get(currentListIndex).getGameID());}
            if (coverPath.equals("_BG")) if(image.exists() && !image.isDirectory()) {displayImageSelectorScreen("_BG", image, numberOfFiles, currentImageNumber+1, gameList.get(currentListIndex).getGameID());}
            if (coverPath.equals("_SCR")) if(image.exists() && !image.isDirectory()) {displayImageSelectorScreen("_SCR", image, numberOfFiles, currentImageNumber+1, gameList.get(currentListIndex).getGameID());}
            if (coverPath.equals("_SCR2")) if(image.exists() && !image.isDirectory()) {displayImageSelectorScreen("_SCR2", image, numberOfFiles, currentImageNumber+1, gameList.get(currentListIndex).getGameID());}
        }
        else {
            JOptionPane.showMessageDialog(null, "There is no image file available in the database for this game.", " No ART Available!", JOptionPane.WARNING_MESSAGE);
        }
        
    }

    
    // Display the game image selector screen
    private void displayImageSelectorScreen(String coverType, File image, int numberOfFiles, int currentImageNumber, String gameID){
        imageSelectorScreen = new GameImageSelectorScreenPS1(parent, true, this, this, currentImageNumber, coverType, image, numberOfFiles, gameID, currentListIndex);
        imageSelectorScreen.setLocationRelativeTo(parent);
        imageSelectorScreen.setVisible(true);
    }
    

    // This displays the game name in the GUI
    private void displayGameName(){jTextFieldGameName.setText(gameList.get(currentListIndex).getGameName() + "  :  " + gameList.get(currentListIndex).getGameID());}
    
    
    // This displays the game ID in the GUI
    private void displayGameNumber(){jTextFieldGameNumber.setText("[" + (currentListIndex+1) + "/" + gameList.size() + "]");}
    
    
    // This loads the game lists
    private void getGameLists(){gameList = new ArrayList<>(GameListManager.getGameListPS1());}
    

    // This loads an image file that the user has selected
    private void loadImageFromDirectory(String coverType) {

        Image img = null;
        
        try {
            switch (coverType) {
                case "_COV":
                    try{img = PopsGameManager.manualImageSelection(coverType, gameList.get(currentListIndex).getGameName(), gameList.get(currentListIndex).getGameID()).getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), jLabelGameFrontCover.getHeight()); }
                    catch (NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    if (img != null) {jLabelGameFrontCover.setIcon(new javax.swing.ImageIcon(img));}
                    break;
                case "_COV2":
                    try{img = PopsGameManager.manualImageSelection(coverType, gameList.get(currentListIndex).getGameName(), gameList.get(currentListIndex).getGameID()).getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), jLabelGameRearCover.getHeight()); }
                    catch (NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    if (img != null) {jLabelGameRearCover.setIcon(new javax.swing.ImageIcon(img));}
                    break;
                case "_SCR":
                    try{img = PopsGameManager.manualImageSelection(coverType, gameList.get(currentListIndex).getGameName(), gameList.get(currentListIndex).getGameID()).getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), jLabelGameScreenshot1.getHeight()); }
                    catch (NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    if (img != null) {jLabelGameScreenshot1.setIcon(new javax.swing.ImageIcon(img));}
                    break;
                case "_SCR2":
                    try{img = PopsGameManager.manualImageSelection(coverType, gameList.get(currentListIndex).getGameName(), gameList.get(currentListIndex).getGameID()).getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), jLabelGameScreenshot2.getHeight()); }
                    catch (NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    if (img != null) {jLabelGameScreenshot2.setIcon(new javax.swing.ImageIcon(img));}
                    break;
                case "_BG":
                    try{img = PopsGameManager.manualImageSelection(coverType, gameList.get(currentListIndex).getGameName(), gameList.get(currentListIndex).getGameID()).getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), jLabelGameBackgroundImage.getHeight()); }
                    catch (NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    if (img != null) {jLabelGameBackgroundImage.setIcon(new javax.swing.ImageIcon(img));}
                    break;
                case "_ICO":
                    try{img = PopsGameManager.manualImageSelection(coverType, gameList.get(currentListIndex).getGameName(), gameList.get(currentListIndex).getGameID()).getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), jLabelGameDiscImage.getHeight()); }
                    catch (NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    if (img != null) {jLabelGameDiscImage.setIcon(new javax.swing.ImageIcon(img));}
                    break;
            }
        } 
        catch (HeadlessException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
    }
    

    // This displays the game images in the GUI
    private void displayGameImages(){
        
        // Front cover
        File frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_COV" + ".jpg");
        if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
        else {
            frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_COV" + ".png");
            if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
            else {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS1_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));} 
        } 

        // Rear cover
        File rearCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_COV2" + ".jpg");
        if(rearCover.exists() && !rearCover.isDirectory()) {jLabelGameRearCover.setIcon(new ImageIcon(new ImageIcon(rearCover.toString()).getImage().getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), Image.SCALE_DEFAULT)));}
        else {
            rearCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_COV2" + ".png");
            if(rearCover.exists() && !rearCover.isDirectory()) {jLabelGameRearCover.setIcon(new ImageIcon(new ImageIcon(rearCover.toString()).getImage().getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), Image.SCALE_DEFAULT)));}
            else {jLabelGameRearCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS1_COVER_PATH).getImage().getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), Image.SCALE_DEFAULT))); }
        }   

        // Background image
        File backgroundImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_BG" + ".jpg");
        if(backgroundImage.exists() && !backgroundImage.isDirectory()) {jLabelGameBackgroundImage.setIcon(new ImageIcon(new ImageIcon(backgroundImage.toString()).getImage().getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), Image.SCALE_DEFAULT)));}
        else {
            backgroundImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_BG" + ".png");
            if(backgroundImage.exists() && !backgroundImage.isDirectory()) {jLabelGameBackgroundImage.setIcon(new ImageIcon(new ImageIcon(backgroundImage.toString()).getImage().getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), Image.SCALE_DEFAULT)));}
            else {jLabelGameBackgroundImage.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_BACKGROUND_PATH).getImage().getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), Image.SCALE_DEFAULT)));}
        }   

        // Disc image
        File discImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_ICO" + ".jpg");
        if(discImage.exists() && !discImage.isDirectory()) {jLabelGameDiscImage.setIcon(new ImageIcon(new ImageIcon(discImage.toString()).getImage().getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), Image.SCALE_DEFAULT)));}
        else {
            discImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_ICO" + ".png");
            if(discImage.exists() && !discImage.isDirectory()) {jLabelGameDiscImage.setIcon(new ImageIcon(new ImageIcon(discImage.toString()).getImage().getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), Image.SCALE_DEFAULT)));}
            else {jLabelGameDiscImage.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_DISC_PATH).getImage().getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), Image.SCALE_DEFAULT)));}
        }  
        
        // Screenshot 1 image
        File screenshot1Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_SCR" + ".jpg");
        if(screenshot1Image.exists() && !screenshot1Image.isDirectory()) {jLabelGameScreenshot1.setIcon(new ImageIcon(new ImageIcon(screenshot1Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), Image.SCALE_DEFAULT)));}
        else {
            screenshot1Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_SCR" + ".png");
            if(screenshot1Image.exists() && !screenshot1Image.isDirectory()) {jLabelGameScreenshot1.setIcon(new ImageIcon(new ImageIcon(screenshot1Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), Image.SCALE_DEFAULT)));}
            else {jLabelGameScreenshot1.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_SCREENSHOT_PATH).getImage().getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), Image.SCALE_DEFAULT)));} 
        }  
        
        // Screenshot 2 image
        File screenshot2Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_SCR2" + ".jpg");
        if(screenshot2Image.exists() && !screenshot2Image.isDirectory()) {jLabelGameScreenshot2.setIcon(new ImageIcon(new ImageIcon(screenshot2Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), Image.SCALE_DEFAULT)));}
        else {
            screenshot2Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + "_SCR2" + ".png");
            if(screenshot2Image.exists() && !screenshot2Image.isDirectory()) {jLabelGameScreenshot2.setIcon(new ImageIcon(new ImageIcon(screenshot2Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), Image.SCALE_DEFAULT)));}
            else {jLabelGameScreenshot2.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_SCREENSHOT_PATH).getImage().getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), Image.SCALE_DEFAULT)));} 
        }  
    }
    

    // This deletes an image file
    private void deleteImage(String coverType){

        // Delete the file if it is a jpg 
        File imageFile = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + coverType + ".jpg");
        if(imageFile.exists() && !imageFile.isDirectory()) {imageFile.delete();}
        
        // Delete the file if it is a png 
        imageFile = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + ".ELF" + coverType + ".png");
        if(imageFile.exists() && !imageFile.isDirectory()) {imageFile.delete();}

        displayGameImages();
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldGameNumber = new javax.swing.JTextField();
        jTextFieldGameName = new javax.swing.JTextField();
        jButtonPreviousGame = new javax.swing.JButton();
        jButtonNextGame = new javax.swing.JButton();
        jPanelGameDiscImage = new javax.swing.JPanel();
        jLabelGameDiscImage = new javax.swing.JLabel();
        jButtonDiscImageManual = new javax.swing.JButton();
        jButtonDiscImageAuto = new javax.swing.JButton();
        jButtonDiscImageDelete = new javax.swing.JButton();
        jPanelGameRearCover = new javax.swing.JPanel();
        jLabelGameRearCover = new javax.swing.JLabel();
        jPanelGameRearCoverButtons = new javax.swing.JPanel();
        jButtonRearCoverManual = new javax.swing.JButton();
        jButtonRearCoverAuto = new javax.swing.JButton();
        jButtonRearCoverDelete = new javax.swing.JButton();
        jPanelGameFrontCover = new javax.swing.JPanel();
        jLabelGameFrontCover = new javax.swing.JLabel();
        jPanelGameFrontCoverButtons = new javax.swing.JPanel();
        jButtonFrontCoverDelete = new javax.swing.JButton();
        jButtonFrontCoverManual = new javax.swing.JButton();
        jButtonFrontCoverAuto = new javax.swing.JButton();
        jPanelGameBackgroundImage = new javax.swing.JPanel();
        jLabelGameBackgroundImage = new javax.swing.JLabel();
        jButtonBackgroundImageManual = new javax.swing.JButton();
        jButtonBackgroundImageAuto = new javax.swing.JButton();
        jButtonBackgroundImageDelete = new javax.swing.JButton();
        jPanelGameScreenshots = new javax.swing.JPanel();
        jLabelGameScreenshot1 = new javax.swing.JLabel();
        jLabelGameScreenshot2 = new javax.swing.JLabel();
        jButtonDiscImageManual1 = new javax.swing.JButton();
        jButtonDiscImageAuto1 = new javax.swing.JButton();
        jButtonDiscImageManual2 = new javax.swing.JButton();
        jButtonDiscImageAuto2 = new javax.swing.JButton();
        jButtonDiscImageDelete1 = new javax.swing.JButton();
        jButtonDiscImageDelete2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jTextFieldGameNumber.setEditable(false);
        jTextFieldGameNumber.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextFieldGameNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameNumber.setText("[1/12]");
        jTextFieldGameNumber.setBorder(null);

        jTextFieldGameName.setEditable(false);
        jTextFieldGameName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextFieldGameName.setText("Game name - Game ID");
        jTextFieldGameName.setBorder(null);
        jTextFieldGameName.setPreferredSize(new java.awt.Dimension(630, 25));

        jButtonPreviousGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Left Icon.png"))); // NOI18N
        jButtonPreviousGame.setToolTipText("Previous game");
        jButtonPreviousGame.setContentAreaFilled(false);
        jButtonPreviousGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousGameActionPerformed(evt);
            }
        });

        jButtonNextGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Right Icon.png"))); // NOI18N
        jButtonNextGame.setToolTipText("Next game");
        jButtonNextGame.setContentAreaFilled(false);
        jButtonNextGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextGameActionPerformed(evt);
            }
        });

        jPanelGameDiscImage.setBorder(javax.swing.BorderFactory.createTitledBorder("Disc Image"));

        jLabelGameDiscImage.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameDiscImage.setToolTipText("");
        jLabelGameDiscImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonDiscImageManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Folder Icon.png"))); // NOI18N
        jButtonDiscImageManual.setToolTipText("");
        jButtonDiscImageManual.setContentAreaFilled(false);
        jButtonDiscImageManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageManualActionPerformed(evt);
            }
        });

        jButtonDiscImageAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonDiscImageAuto.setToolTipText("");
        jButtonDiscImageAuto.setContentAreaFilled(false);
        jButtonDiscImageAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageAutoActionPerformed(evt);
            }
        });

        jButtonDiscImageDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Delete Icon.png"))); // NOI18N
        jButtonDiscImageDelete.setToolTipText("");
        jButtonDiscImageDelete.setContentAreaFilled(false);
        jButtonDiscImageDelete.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonDiscImageDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGameDiscImageLayout = new javax.swing.GroupLayout(jPanelGameDiscImage);
        jPanelGameDiscImage.setLayout(jPanelGameDiscImageLayout);
        jPanelGameDiscImageLayout.setHorizontalGroup(
            jPanelGameDiscImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameDiscImageLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(jLabelGameDiscImage, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonDiscImageManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonDiscImageAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonDiscImageDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelGameDiscImageLayout.setVerticalGroup(
            jPanelGameDiscImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameDiscImageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelGameDiscImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelGameDiscImage, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelGameDiscImageLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanelGameDiscImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonDiscImageDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelGameDiscImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonDiscImageAuto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonDiscImageManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(77, 77, 77))
        );

        jPanelGameRearCover.setBorder(javax.swing.BorderFactory.createTitledBorder("Rear Cover"));
        jPanelGameRearCover.setPreferredSize(new java.awt.Dimension(192, 290));

        jLabelGameRearCover.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameRearCover.setToolTipText("");
        jLabelGameRearCover.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonRearCoverManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Folder Icon.png"))); // NOI18N
        jButtonRearCoverManual.setToolTipText("");
        jButtonRearCoverManual.setContentAreaFilled(false);
        jButtonRearCoverManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRearCoverManualActionPerformed(evt);
            }
        });

        jButtonRearCoverAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonRearCoverAuto.setToolTipText("");
        jButtonRearCoverAuto.setContentAreaFilled(false);
        jButtonRearCoverAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRearCoverAutoActionPerformed(evt);
            }
        });

        jButtonRearCoverDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Delete Icon.png"))); // NOI18N
        jButtonRearCoverDelete.setToolTipText("");
        jButtonRearCoverDelete.setContentAreaFilled(false);
        jButtonRearCoverDelete.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonRearCoverDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRearCoverDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGameRearCoverButtonsLayout = new javax.swing.GroupLayout(jPanelGameRearCoverButtons);
        jPanelGameRearCoverButtons.setLayout(jPanelGameRearCoverButtonsLayout);
        jPanelGameRearCoverButtonsLayout.setHorizontalGroup(
            jPanelGameRearCoverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRearCoverButtonsLayout.createSequentialGroup()
                .addComponent(jButtonRearCoverManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jButtonRearCoverAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonRearCoverDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelGameRearCoverButtonsLayout.setVerticalGroup(
            jPanelGameRearCoverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRearCoverButtonsLayout.createSequentialGroup()
                .addGroup(jPanelGameRearCoverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonRearCoverAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRearCoverManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRearCoverDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelGameRearCoverLayout = new javax.swing.GroupLayout(jPanelGameRearCover);
        jPanelGameRearCover.setLayout(jPanelGameRearCoverLayout);
        jPanelGameRearCoverLayout.setHorizontalGroup(
            jPanelGameRearCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRearCoverLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameRearCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameRearCoverLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelGameRearCover, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelGameRearCoverButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelGameRearCoverLayout.setVerticalGroup(
            jPanelGameRearCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRearCoverLayout.createSequentialGroup()
                .addComponent(jLabelGameRearCover, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelGameRearCoverButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelGameFrontCover.setBorder(javax.swing.BorderFactory.createTitledBorder("Font Cover"));
        jPanelGameFrontCover.setPreferredSize(new java.awt.Dimension(192, 290));

        jLabelGameFrontCover.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameFrontCover.setToolTipText("");
        jLabelGameFrontCover.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonFrontCoverDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Delete Icon.png"))); // NOI18N
        jButtonFrontCoverDelete.setToolTipText("");
        jButtonFrontCoverDelete.setContentAreaFilled(false);
        jButtonFrontCoverDelete.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonFrontCoverDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFrontCoverDeleteActionPerformed(evt);
            }
        });

        jButtonFrontCoverManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Folder Icon.png"))); // NOI18N
        jButtonFrontCoverManual.setToolTipText("");
        jButtonFrontCoverManual.setContentAreaFilled(false);
        jButtonFrontCoverManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFrontCoverManualActionPerformed(evt);
            }
        });

        jButtonFrontCoverAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonFrontCoverAuto.setToolTipText("");
        jButtonFrontCoverAuto.setContentAreaFilled(false);
        jButtonFrontCoverAuto.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonFrontCoverAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFrontCoverAutoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGameFrontCoverButtonsLayout = new javax.swing.GroupLayout(jPanelGameFrontCoverButtons);
        jPanelGameFrontCoverButtons.setLayout(jPanelGameFrontCoverButtonsLayout);
        jPanelGameFrontCoverButtonsLayout.setHorizontalGroup(
            jPanelGameFrontCoverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameFrontCoverButtonsLayout.createSequentialGroup()
                .addComponent(jButtonFrontCoverManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jButtonFrontCoverAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jButtonFrontCoverDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelGameFrontCoverButtonsLayout.setVerticalGroup(
            jPanelGameFrontCoverButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButtonFrontCoverDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jButtonFrontCoverManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jButtonFrontCoverAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanelGameFrontCoverLayout = new javax.swing.GroupLayout(jPanelGameFrontCover);
        jPanelGameFrontCover.setLayout(jPanelGameFrontCoverLayout);
        jPanelGameFrontCoverLayout.setHorizontalGroup(
            jPanelGameFrontCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameFrontCoverLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameFrontCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameFrontCoverLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanelGameFrontCoverButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelGameFrontCoverLayout.setVerticalGroup(
            jPanelGameFrontCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameFrontCoverLayout.createSequentialGroup()
                .addComponent(jLabelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jPanelGameFrontCoverButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelGameBackgroundImage.setBorder(javax.swing.BorderFactory.createTitledBorder("Background Image"));

        jLabelGameBackgroundImage.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameBackgroundImage.setToolTipText("");
        jLabelGameBackgroundImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabelGameBackgroundImage.setPreferredSize(new java.awt.Dimension(375, 2));

        jButtonBackgroundImageManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Folder Icon.png"))); // NOI18N
        jButtonBackgroundImageManual.setToolTipText("");
        jButtonBackgroundImageManual.setContentAreaFilled(false);
        jButtonBackgroundImageManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackgroundImageManualActionPerformed(evt);
            }
        });

        jButtonBackgroundImageAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonBackgroundImageAuto.setToolTipText("");
        jButtonBackgroundImageAuto.setContentAreaFilled(false);
        jButtonBackgroundImageAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackgroundImageAutoActionPerformed(evt);
            }
        });

        jButtonBackgroundImageDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Delete Icon.png"))); // NOI18N
        jButtonBackgroundImageDelete.setToolTipText("");
        jButtonBackgroundImageDelete.setContentAreaFilled(false);
        jButtonBackgroundImageDelete.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonBackgroundImageDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackgroundImageDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGameBackgroundImageLayout = new javax.swing.GroupLayout(jPanelGameBackgroundImage);
        jPanelGameBackgroundImage.setLayout(jPanelGameBackgroundImageLayout);
        jPanelGameBackgroundImageLayout.setHorizontalGroup(
            jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                        .addComponent(jLabelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                        .addComponent(jButtonBackgroundImageManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141)
                        .addComponent(jButtonBackgroundImageAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonBackgroundImageDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameBackgroundImageLayout.setVerticalGroup(
            jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonBackgroundImageManual, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBackgroundImageAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBackgroundImageDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelGameScreenshots.setBorder(javax.swing.BorderFactory.createTitledBorder("Screenshots"));

        jLabelGameScreenshot1.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameScreenshot1.setToolTipText("");
        jLabelGameScreenshot1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelGameScreenshot2.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameScreenshot2.setToolTipText("");
        jLabelGameScreenshot2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonDiscImageManual1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Folder Icon.png"))); // NOI18N
        jButtonDiscImageManual1.setToolTipText("");
        jButtonDiscImageManual1.setContentAreaFilled(false);
        jButtonDiscImageManual1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageManual1ActionPerformed(evt);
            }
        });

        jButtonDiscImageAuto1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonDiscImageAuto1.setToolTipText("");
        jButtonDiscImageAuto1.setContentAreaFilled(false);
        jButtonDiscImageAuto1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageAuto1ActionPerformed(evt);
            }
        });

        jButtonDiscImageManual2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Folder Icon.png"))); // NOI18N
        jButtonDiscImageManual2.setToolTipText("");
        jButtonDiscImageManual2.setContentAreaFilled(false);
        jButtonDiscImageManual2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageManual2ActionPerformed(evt);
            }
        });

        jButtonDiscImageAuto2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonDiscImageAuto2.setToolTipText("");
        jButtonDiscImageAuto2.setContentAreaFilled(false);
        jButtonDiscImageAuto2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageAuto2ActionPerformed(evt);
            }
        });

        jButtonDiscImageDelete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Delete Icon.png"))); // NOI18N
        jButtonDiscImageDelete1.setToolTipText("");
        jButtonDiscImageDelete1.setContentAreaFilled(false);
        jButtonDiscImageDelete1.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonDiscImageDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageDelete1ActionPerformed(evt);
            }
        });

        jButtonDiscImageDelete2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Delete Icon.png"))); // NOI18N
        jButtonDiscImageDelete2.setToolTipText("");
        jButtonDiscImageDelete2.setContentAreaFilled(false);
        jButtonDiscImageDelete2.setPreferredSize(new java.awt.Dimension(40, 40));
        jButtonDiscImageDelete2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscImageDelete2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGameScreenshotsLayout = new javax.swing.GroupLayout(jPanelGameScreenshots);
        jPanelGameScreenshots.setLayout(jPanelGameScreenshotsLayout);
        jPanelGameScreenshotsLayout.setHorizontalGroup(
            jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                        .addComponent(jLabelGameScreenshot2, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButtonDiscImageManual2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonDiscImageAuto2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonDiscImageDelete2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                        .addComponent(jLabelGameScreenshot1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonDiscImageManual1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonDiscImageAuto1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonDiscImageDelete1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameScreenshotsLayout.setVerticalGroup(
            jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                        .addComponent(jButtonDiscImageManual1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jButtonDiscImageAuto1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDiscImageDelete1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelGameScreenshot1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelGameScreenshot2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                        .addComponent(jButtonDiscImageManual2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jButtonDiscImageAuto2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDiscImageDelete2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldGameNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameName, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanelGameRearCover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanelGameScreenshots, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelGameDiscImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldGameName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanelGameFrontCover, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                            .addComponent(jPanelGameRearCover, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                        .addComponent(jPanelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanelGameDiscImage, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanelGameScreenshots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">  
    private void jButtonPreviousGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousGameActionPerformed
        if (currentListIndex > 0) {
            currentListIndex -=1;
            displayGameName();
            displayGameNumber();
            displayGameImages();
        }
    }//GEN-LAST:event_jButtonPreviousGameActionPerformed

    private void jButtonNextGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextGameActionPerformed
        if (currentListIndex < gameList.size()-1) {
            currentListIndex +=1;
            displayGameName();
            displayGameNumber();
            displayGameImages();
        }
    }//GEN-LAST:event_jButtonNextGameActionPerformed

    private void jButtonDiscImageManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageManualActionPerformed
        loadImageFromDirectory("_ICO");
    }//GEN-LAST:event_jButtonDiscImageManualActionPerformed

    private void jButtonDiscImageAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageAutoActionPerformed
        getImageFromServer("_ICO", "_ICO", 0);
    }//GEN-LAST:event_jButtonDiscImageAutoActionPerformed

    private void jButtonDiscImageDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageDeleteActionPerformed
        deleteImage("_ICO");
    }//GEN-LAST:event_jButtonDiscImageDeleteActionPerformed

    private void jButtonRearCoverManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRearCoverManualActionPerformed
        loadImageFromDirectory("_COV2");
    }//GEN-LAST:event_jButtonRearCoverManualActionPerformed

    private void jButtonRearCoverAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRearCoverAutoActionPerformed
        getImageFromServer("_COV2", "_COV2", 0);
    }//GEN-LAST:event_jButtonRearCoverAutoActionPerformed

    private void jButtonRearCoverDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRearCoverDeleteActionPerformed
        deleteImage("_COV2");
    }//GEN-LAST:event_jButtonRearCoverDeleteActionPerformed

    private void jButtonFrontCoverDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFrontCoverDeleteActionPerformed
        deleteImage("_COV");
    }//GEN-LAST:event_jButtonFrontCoverDeleteActionPerformed

    private void jButtonFrontCoverManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFrontCoverManualActionPerformed
        loadImageFromDirectory("_COV");
    }//GEN-LAST:event_jButtonFrontCoverManualActionPerformed

    private void jButtonFrontCoverAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFrontCoverAutoActionPerformed
        getImageFromServer("_COV", "_COV", 0);
    }//GEN-LAST:event_jButtonFrontCoverAutoActionPerformed

    private void jButtonBackgroundImageManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackgroundImageManualActionPerformed
        loadImageFromDirectory("_BG");
    }//GEN-LAST:event_jButtonBackgroundImageManualActionPerformed

    private void jButtonBackgroundImageAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackgroundImageAutoActionPerformed
        getImageFromServer("_BG", "_BG", 0);
    }//GEN-LAST:event_jButtonBackgroundImageAutoActionPerformed

    private void jButtonDiscImageManual1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageManual1ActionPerformed
        loadImageFromDirectory("_SCR");
    }//GEN-LAST:event_jButtonDiscImageManual1ActionPerformed

    private void jButtonDiscImageAuto1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageAuto1ActionPerformed
        getImageFromServer("_SCR", "_SCR", 0);
    }//GEN-LAST:event_jButtonDiscImageAuto1ActionPerformed

    private void jButtonDiscImageManual2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageManual2ActionPerformed
        loadImageFromDirectory("_SCR2");
    }//GEN-LAST:event_jButtonDiscImageManual2ActionPerformed

    private void jButtonDiscImageAuto2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageAuto2ActionPerformed
        getImageFromServer("_SCR", "_SCR2", 1);
    }//GEN-LAST:event_jButtonDiscImageAuto2ActionPerformed

    private void jButtonBackgroundImageDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackgroundImageDeleteActionPerformed
        deleteImage("_BG");
    }//GEN-LAST:event_jButtonBackgroundImageDeleteActionPerformed

    private void jButtonDiscImageDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageDelete1ActionPerformed
        deleteImage("_SCR");
    }//GEN-LAST:event_jButtonDiscImageDelete1ActionPerformed

    private void jButtonDiscImageDelete2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscImageDelete2ActionPerformed
        deleteImage("_SCR2");
    }//GEN-LAST:event_jButtonDiscImageDelete2ActionPerformed
    // </editor-fold> 
   
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBackgroundImageAuto;
    private javax.swing.JButton jButtonBackgroundImageDelete;
    private javax.swing.JButton jButtonBackgroundImageManual;
    private javax.swing.JButton jButtonDiscImageAuto;
    private javax.swing.JButton jButtonDiscImageAuto1;
    private javax.swing.JButton jButtonDiscImageAuto2;
    private javax.swing.JButton jButtonDiscImageDelete;
    private javax.swing.JButton jButtonDiscImageDelete1;
    private javax.swing.JButton jButtonDiscImageDelete2;
    private javax.swing.JButton jButtonDiscImageManual;
    private javax.swing.JButton jButtonDiscImageManual1;
    private javax.swing.JButton jButtonDiscImageManual2;
    private javax.swing.JButton jButtonFrontCoverAuto;
    private javax.swing.JButton jButtonFrontCoverDelete;
    private javax.swing.JButton jButtonFrontCoverManual;
    private javax.swing.JButton jButtonNextGame;
    private javax.swing.JButton jButtonPreviousGame;
    private javax.swing.JButton jButtonRearCoverAuto;
    private javax.swing.JButton jButtonRearCoverDelete;
    private javax.swing.JButton jButtonRearCoverManual;
    private javax.swing.JLabel jLabelGameBackgroundImage;
    private javax.swing.JLabel jLabelGameDiscImage;
    private javax.swing.JLabel jLabelGameFrontCover;
    private javax.swing.JLabel jLabelGameRearCover;
    private javax.swing.JLabel jLabelGameScreenshot1;
    private javax.swing.JLabel jLabelGameScreenshot2;
    javax.swing.JPanel jPanelGameBackgroundImage;
    private javax.swing.JPanel jPanelGameDiscImage;
    private javax.swing.JPanel jPanelGameFrontCover;
    private javax.swing.JPanel jPanelGameFrontCoverButtons;
    private javax.swing.JPanel jPanelGameRearCover;
    private javax.swing.JPanel jPanelGameRearCoverButtons;
    private javax.swing.JPanel jPanelGameScreenshots;
    private javax.swing.JTextField jTextFieldGameName;
    private javax.swing.JTextField jTextFieldGameNumber;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}