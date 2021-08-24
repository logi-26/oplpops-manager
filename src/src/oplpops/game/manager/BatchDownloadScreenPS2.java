package oplpops.game.manager;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class BatchDownloadScreenPS2 extends javax.swing.JDialog {

    private static final String NO_IMAGE_PS2_COVER_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Cover PS2.png";
    private static final String NO_IMAGE_BACKGROUND_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Background.png";
    private static final String NO_IMAGE_SCREENSHOT_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Screenshot.png";
    private static final String NO_IMAGE_DISC_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Disc.png";
    
    final static JProgressBar DOWNLOAD_STATUS_BAR = new JProgressBar(0, 100);
    private static List<Game> gameList;
    private static List<String> artList; 
    private static List<String> configList; 
    private static List<String> processedGameList;
    
    // Count the missing files (Used for the progress bar)
    private int missingFrontCovers = 0;
    private int missingRearCovers = 0;
    private int missingDiscImages = 0;
    private int missingScreenshots = 0;
    private int missingBackgrounds = 0;
    private int missingConfigss = 0;
    
    private int totalFilesToDownload = 0;
    private int totalFilesProcessed = 0;
    private int threadCount = 0;
    
    public BatchDownloadScreenPS2(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initialiseGUI();
        overideClose();
        getGameLists();
        getArtList();
        getConfigList();
        detectMissingFiles(); 
    }

    
    // This sets the dialog title and creates the progress bar
    private void initialiseGUI(){
        this.setTitle(" Batch Downloads");
        jPanelProgress.add(DOWNLOAD_STATUS_BAR);
        DOWNLOAD_STATUS_BAR.setBounds(16, 35, 185, 30);
        DOWNLOAD_STATUS_BAR.setValue(0);
        DOWNLOAD_STATUS_BAR.repaint();
    }
  
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                PopsGameManager.callbackToUpdateGUIGameList(null, -1);
                dispose();
            }
        });
    }
    
    
    // This gets the game lists for the current console
    private void getGameLists(){gameList = new ArrayList<>(GameListManager.getGameListPS2());}
    
    
    // Get a list of all the files in the OPL ART directory
    private void getArtList(){
        artList = new ArrayList<>();
        File[] files = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator).listFiles();
        for (File file : files) if (file.isFile()) {artList.add(file.getName());}
    }
    
    
    // Get a list of all the files in the OPL CFG directory
    private void getConfigList(){
        configList = new ArrayList<>();
        File[] files = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator).listFiles();
        for (File file : files) if (file.isFile()) {configList.add(file.getName());}
    }
    

    // This counts the number of files
    private void detectMissingFiles(){
        
        gameList.stream().map((game) -> {
            if (!artList.contains(game.getGameID() + "_COV.jpg") && !artList.contains(game.getGameID() + "_COV.png")){missingFrontCovers++;}
            return game;
        }).map((game) -> {
            if (!artList.contains(game.getGameID() + "_COV2.jpg") && !artList.contains(game.getGameID() + "_COV2.png")){missingRearCovers++;}
            return game;
        }).map((game) -> {
            if (!artList.contains(game.getGameID() + "_ICO.jpg") && !artList.contains(game.getGameID() + "_ICO.png")){missingDiscImages++;}
            return game;
        }).map((game) -> {
            if (!artList.contains(game.getGameID() + "_SCR.jpg") && !artList.contains(game.getGameID() + "_SCR.png")){missingScreenshots++;}
            return game;
        }).map((game) -> {
            if (!artList.contains(game.getGameID() + "_SCR2.jpg") && !artList.contains(game.getGameID() + "_SCR2.png")){missingScreenshots++;}
            return game;
        }).map((game) -> {
            if (!artList.contains(game.getGameID() + "_BG.jpg") && !artList.contains(game.getGameID() + "_BG.png")){missingBackgrounds++;}
            return game;
        }).filter((game) -> (!configList.contains(game.getGameID() + ".cfg"))).forEach((_item) -> {
            missingConfigss++;
        });
    }
    

    // This determines which files need to be downloaded and starts the download thread for each file
    private void downloadMissingFiles(){
        
        processedGameList = new ArrayList<>();
        
        // First check to see if the server is responding to prevent wasting time sending many un-answered requests
        MyTCPClient tcpClient = new MyTCPClient();

        if (tcpClient.sendMessageToServer("RESPOND").equals("RESPONSE")){
            
            // First determine the total number of files to be downloaded (For the progress bar)
            totalFilesToDownload = 0;
            
            if (jCheckBoxFrontCover.isSelected()) {totalFilesToDownload += missingFrontCovers;}
            if (jCheckBoxRearCover.isSelected()) {totalFilesToDownload += missingRearCovers;} 
            if (jCheckBoxDiscImage.isSelected()) {totalFilesToDownload += missingDiscImages;} 
            if (jCheckBoxScreenshot.isSelected()) {totalFilesToDownload += missingScreenshots;}
            if (jCheckBoxBackground.isSelected()) {totalFilesToDownload += missingBackgrounds;}
            if (jCheckBoxConfigFile.isSelected()) {totalFilesToDownload += missingConfigss;}

            totalFilesProcessed = 0;
            DOWNLOAD_STATUS_BAR.setMinimum(0);
            DOWNLOAD_STATUS_BAR.setMaximum(totalFilesToDownload);

            // Downloads the missing files using the background worker thread (So the progress bar can be updated)
            if (jCheckBoxFrontCover.isSelected()) {for (int i = 0; i < gameList.size(); i++) if (!artList.contains(gameList.get(i).getGameID() + "_COV.jpg") && !artList.contains(gameList.get(i).getGameID() + "_COV.png")) new BackgroundWorker(gameList.get(i).getGameID(), "_COV").execute();}
            if (jCheckBoxRearCover.isSelected()) {for (int i = 0; i < gameList.size(); i++) if (!artList.contains(gameList.get(i).getGameID() + "_COV2.jpg") && !artList.contains(gameList.get(i).getGameID() + "_COV2.png")) new BackgroundWorker(gameList.get(i).getGameID(), "_COV2").execute();}
            if (jCheckBoxDiscImage.isSelected()) {for (int i = 0; i < gameList.size(); i++) if (!artList.contains(gameList.get(i).getGameID() + "_ICO.jpg") && !artList.contains(gameList.get(i).getGameID() + "_ICO.png")) new BackgroundWorker(gameList.get(i).getGameID(), "_ICO").execute();}
            if (jCheckBoxScreenshot.isSelected()) {
                for (int i = 0; i < gameList.size(); i++) {if (!artList.contains(gameList.get(i).getGameID() + "_SCR.jpg") && !artList.contains(gameList.get(i).getGameID() + "_SCR.png")) new BackgroundWorker(gameList.get(i).getGameID(), "_SCR").execute();}
                for (int i = 0; i < gameList.size(); i++) {if (!artList.contains(gameList.get(i).getGameID() + "_SCR2.jpg") && !artList.contains(gameList.get(i).getGameID() + "_SCR2.png")) new BackgroundWorker(gameList.get(i).getGameID(), "_SCR2").execute();}
            }
            if (jCheckBoxBackground.isSelected()) {for (int i = 0; i < gameList.size(); i++) if (!artList.contains(gameList.get(i).getGameID() + "_BG.jpg") && !artList.contains(gameList.get(i).getGameID() + "_BG.png")) new BackgroundWorker(gameList.get(i).getGameID(), "_BG").execute();}
            if (jCheckBoxConfigFile.isSelected()) {for (int i = 0; i < gameList.size(); i++) if (!configList.contains(gameList.get(i).getGameID() + ".cfg")) new BackgroundWorker(gameList.get(i).getGameID(), "CONFIG").execute();}
        }
        else {JOptionPane.showMessageDialog(null,"The server is not responding at the moment!"," Server Connection Error!",JOptionPane.WARNING_MESSAGE);} 
    }
    
    
    // This displays the game images in the GUI
    private void displayGameImages(String gameID, String fileType){

        // Front cover
        if (fileType.equals("_COV")){
            File frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_COV" + ".jpg");
            if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_COV" + ".png");
                if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS2_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));} 
            } 
        }
       
        // Rear cover
        if (fileType.equals("_COV2")){
            File rearCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_COV2" + ".jpg");
            if(rearCover.exists() && !rearCover.isDirectory()) {jLabelGameRearCover.setIcon(new ImageIcon(new ImageIcon(rearCover.toString()).getImage().getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                rearCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_COV2" + ".png");
                if(rearCover.exists() && !rearCover.isDirectory()) {jLabelGameRearCover.setIcon(new ImageIcon(new ImageIcon(rearCover.toString()).getImage().getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameRearCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS2_COVER_PATH).getImage().getScaledInstance(jLabelGameRearCover.getWidth(), jLabelGameRearCover.getHeight(), Image.SCALE_DEFAULT)));}
            }   
        }
        
        // Background image
        if (fileType.equals("_BG")){
            File backgroundImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_BG" + ".jpg");
            if(backgroundImage.exists() && !backgroundImage.isDirectory()) {jLabelGameBackgroundImage.setIcon(new ImageIcon(new ImageIcon(backgroundImage.toString()).getImage().getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                backgroundImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_BG" + ".png");
                if(backgroundImage.exists() && !backgroundImage.isDirectory()) {jLabelGameBackgroundImage.setIcon(new ImageIcon(new ImageIcon(backgroundImage.toString()).getImage().getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameBackgroundImage.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_BACKGROUND_PATH).getImage().getScaledInstance(jLabelGameBackgroundImage.getWidth(), jLabelGameBackgroundImage.getHeight(), Image.SCALE_DEFAULT)));} 
            }   
        }

        // Disc image
        if (fileType.equals("_ICO")){
            File discImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_ICO" + ".jpg");
            if(discImage.exists() && !discImage.isDirectory()) {jLabelGameDiscImage.setIcon(new ImageIcon(new ImageIcon(discImage.toString()).getImage().getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                discImage = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_ICO" + ".png");
                if(discImage.exists() && !discImage.isDirectory()) {jLabelGameDiscImage.setIcon(new ImageIcon(new ImageIcon(discImage.toString()).getImage().getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameDiscImage.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_DISC_PATH).getImage().getScaledInstance(jLabelGameDiscImage.getWidth(), jLabelGameDiscImage.getHeight(), Image.SCALE_DEFAULT)));} 
            }  
        }

        // Screenshot 1 image
        if (fileType.equals("_SCR")){
            File screenshot1Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_SCR" + ".jpg");
            if(screenshot1Image.exists() && !screenshot1Image.isDirectory()) {jLabelGameScreenshot1.setIcon(new ImageIcon(new ImageIcon(screenshot1Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                screenshot1Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_SCR" + ".png");
                if(screenshot1Image.exists() && !screenshot1Image.isDirectory()) {jLabelGameScreenshot1.setIcon(new ImageIcon(new ImageIcon(screenshot1Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameScreenshot1.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_SCREENSHOT_PATH).getImage().getScaledInstance(jLabelGameScreenshot1.getWidth(), jLabelGameScreenshot1.getHeight(), Image.SCALE_DEFAULT)));} 
            }  
        }
        
        // Screenshot 2 image
        if (fileType.equals("_SCR2")){
            File screenshot2Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_SCR2" + ".jpg");
            if(screenshot2Image.exists() && !screenshot2Image.isDirectory()) {jLabelGameScreenshot2.setIcon(new ImageIcon(new ImageIcon(screenshot2Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                screenshot2Image = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + "_SCR2" + ".png");
                if(screenshot2Image.exists() && !screenshot2Image.isDirectory()) {jLabelGameScreenshot2.setIcon(new ImageIcon(new ImageIcon(screenshot2Image.toString()).getImage().getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameScreenshot2.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_SCREENSHOT_PATH).getImage().getScaledInstance(jLabelGameScreenshot2.getWidth(), jLabelGameScreenshot2.getHeight(), Image.SCALE_DEFAULT)));} 
            }  
        }
    }
    
    
    // This checks the titles of the games in the config files with the users game names
    // This is required because the user may name the game file slightly differently
    private void renameConfigGameTitles(){
        
        try(Stream<Path> paths = Files.walk(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    
                    gameList.stream().filter((ps2Game) -> (ps2Game.getGameID().equals(filePath.getFileName().toString().substring(0, filePath.getFileName().toString().length()-4)))).forEachOrdered((ps2Game) -> {
                        try {
                            List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + ps2Game.getGameID() + ".cfg"), StandardCharsets.UTF_8));
                            if (fileContent.get(1).substring(0, 5).equals("Title")) {fileContent.set(1, "Title=" + ps2Game.getGameName());}
                            Files.write(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + ps2Game.getGameID() + ".cfg"), fileContent, StandardCharsets.UTF_8);
                        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    });
                }
            });
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
    }

    
    // Background worker thread: this downloads the file from the server and updates the progress bar in the GUI
    public class BackgroundWorker extends SwingWorker<Object, File> {
        private final String gameID;
        private final String fileType;

        public BackgroundWorker(String gameID, String fileType) {
            this.gameID = gameID;
            this.fileType = fileType;
        }

        @Override
        protected Object doInBackground() throws Exception {

            // This gets the game name and game size for the current game
            String gameSize = null;
            String gameName = null;
            Game selectedGame = null;
            long gameSizeValue = 0;   

            for (int i = 0; i < gameList.size(); i++) {
                if (gameList.get(i).getGameID().equals(gameID)) {
                    gameSize = String.valueOf(gameList.get(i).getGameRawSize());
                    gameName = gameList.get(i).getGameName();
                    selectedGame = gameList.get(i);
                }
            }

            if (gameSize != null) {gameSizeValue = Long.valueOf(gameSize);}    
            
            DOWNLOAD_STATUS_BAR.setValue(totalFilesProcessed);
            DOWNLOAD_STATUS_BAR.repaint();

            // This ensures the game size is greater than 5mb (Used to help prevent batch downloads using dummy files)
            if (gameSizeValue > 5000000){

                // Send the UDP request for the specific image file
                String[] splitName = gameID.split("_");

                MyTCPClient tcpClient = new MyTCPClient();
                switch (fileType) {
                    case "CONFIG":
                        tcpClient.getConfigFromServer(PopsGameManager.determineGameRegion(splitName[0]), gameID, gameName, true); 
                        break;
                    case "_SCR2":
                        tcpClient.getImageFromServer(selectedGame, PopsGameManager.determineGameRegion(splitName[0]), gameID, gameName, fileType, fileType, 1, true);
                        break;
                    default:
                        tcpClient.getImageFromServer(selectedGame, PopsGameManager.determineGameRegion(splitName[0]), gameID, gameName, fileType, fileType, 0, true);
                        break;
                }

                // Display the game name for the current file being downloaded and add it to the list
                if (gameName != null) {
                    jTextFieldGameName.setText(" " + gameName + " : " + gameID);
                    if (!processedGameList.contains(gameName)) {processedGameList.add(gameName);}
                    createList(processedGameList.toArray(new String[0]));
                }
                displayGameImages(gameID,fileType);
            }

            // Decrement the total number of files left to download
            totalFilesProcessed +=1;
            
            return null;
        }

        @Override
        protected void done(){
            
            // Count the threads when they close and ensure progress bar always = 100% when all threads have ended
            threadCount +=1;
            if (threadCount == totalFilesToDownload) {
                DOWNLOAD_STATUS_BAR.setValue(totalFilesToDownload);
                renameConfigGameTitles();
            }
        }
    }
    

    // This creates the list model for the games list
    private void createList(String[] games){
        
        // List model
        jListGameList.setModel(new javax.swing.AbstractListModel<String>() { 
            @Override
            public int getSize() {return games.length;}
            @Override
            public String getElementAt(int i) {return games[i];}
        });
       
        jScrollPane1.setViewportView(jListGameList);
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelGameDiscImage = new javax.swing.JPanel();
        jLabelGameDiscImage = new javax.swing.JLabel();
        jTextFieldGameName = new javax.swing.JTextField();
        jPanelDownloads = new javax.swing.JPanel();
        jPanelFiles = new javax.swing.JPanel();
        jCheckBoxFrontCover = new javax.swing.JCheckBox();
        jCheckBoxRearCover = new javax.swing.JCheckBox();
        jCheckBoxScreenshot = new javax.swing.JCheckBox();
        jCheckBoxBackground = new javax.swing.JCheckBox();
        jCheckBoxConfigFile = new javax.swing.JCheckBox();
        jCheckBoxDiscImage = new javax.swing.JCheckBox();
        jPanelProgress = new javax.swing.JPanel();
        jButtonDownload = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListGameList = new javax.swing.JList<>();
        jPanelGameScreenshots = new javax.swing.JPanel();
        jLabelGameScreenshot1 = new javax.swing.JLabel();
        jLabelGameScreenshot2 = new javax.swing.JLabel();
        jPanelGameBackgroundImage = new javax.swing.JPanel();
        jLabelGameBackgroundImage = new javax.swing.JLabel();
        jPanelGameRearCover = new javax.swing.JPanel();
        jLabelGameRearCover = new javax.swing.JLabel();
        jPanelGameFrontCover = new javax.swing.JPanel();
        jLabelGameFrontCover = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelGameDiscImage.setBorder(javax.swing.BorderFactory.createTitledBorder("Disc Image"));

        jLabelGameDiscImage.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameDiscImage.setToolTipText("");
        jLabelGameDiscImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanelGameDiscImageLayout = new javax.swing.GroupLayout(jPanelGameDiscImage);
        jPanelGameDiscImage.setLayout(jPanelGameDiscImageLayout);
        jPanelGameDiscImageLayout.setHorizontalGroup(
            jPanelGameDiscImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDiscImageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGameDiscImage, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameDiscImageLayout.setVerticalGroup(
            jPanelGameDiscImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDiscImageLayout.createSequentialGroup()
                .addComponent(jLabelGameDiscImage, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jTextFieldGameName.setEditable(false);
        jTextFieldGameName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextFieldGameName.setText("Game name - Game ID");
        jTextFieldGameName.setBorder(null);
        jTextFieldGameName.setPreferredSize(new java.awt.Dimension(630, 25));

        jPanelDownloads.setBorder(javax.swing.BorderFactory.createTitledBorder("Batch Downloads"));

        jPanelFiles.setBorder(javax.swing.BorderFactory.createTitledBorder("Files"));

        jCheckBoxFrontCover.setText("Front Covers");
        jCheckBoxFrontCover.setBorder(null);
        jCheckBoxFrontCover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFrontCoverActionPerformed(evt);
            }
        });

        jCheckBoxRearCover.setText("Rear Covers");
        jCheckBoxRearCover.setBorder(null);
        jCheckBoxRearCover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRearCoverActionPerformed(evt);
            }
        });

        jCheckBoxScreenshot.setText("Screenshots");
        jCheckBoxScreenshot.setBorder(null);
        jCheckBoxScreenshot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxScreenshotActionPerformed(evt);
            }
        });

        jCheckBoxBackground.setText("Background Images");
        jCheckBoxBackground.setBorder(null);
        jCheckBoxBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBackgroundActionPerformed(evt);
            }
        });

        jCheckBoxConfigFile.setText("Config Files");
        jCheckBoxConfigFile.setToolTipText("");
        jCheckBoxConfigFile.setBorder(null);
        jCheckBoxConfigFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxConfigFileActionPerformed(evt);
            }
        });

        jCheckBoxDiscImage.setText("Disc Images");
        jCheckBoxDiscImage.setBorder(null);
        jCheckBoxDiscImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDiscImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFilesLayout = new javax.swing.GroupLayout(jPanelFiles);
        jPanelFiles.setLayout(jPanelFilesLayout);
        jPanelFilesLayout.setHorizontalGroup(
            jPanelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFilesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBoxFrontCover, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(jCheckBoxRearCover, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxDiscImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxScreenshot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxConfigFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFilesLayout.setVerticalGroup(
            jPanelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxFrontCover)
                .addGap(4, 4, 4)
                .addComponent(jCheckBoxRearCover)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDiscImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxScreenshot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxBackground)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxConfigFile)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 203, Short.MAX_VALUE)
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 52, Short.MAX_VALUE)
        );

        jButtonDownload.setText("Download");
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jListGameList);

        javax.swing.GroupLayout jPanelDownloadsLayout = new javax.swing.GroupLayout(jPanelDownloads);
        jPanelDownloads.setLayout(jPanelDownloadsLayout);
        jPanelDownloadsLayout.setHorizontalGroup(
            jPanelDownloadsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDownloadsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDownloadsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addComponent(jButtonDownload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 215, Short.MAX_VALUE)
                    .addComponent(jPanelProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelDownloadsLayout.setVerticalGroup(
            jPanelDownloadsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDownloadsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDownload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanelGameScreenshots.setBorder(javax.swing.BorderFactory.createTitledBorder("Screenshots"));

        jLabelGameScreenshot1.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameScreenshot1.setToolTipText("");
        jLabelGameScreenshot1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabelGameScreenshot2.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameScreenshot2.setToolTipText("");
        jLabelGameScreenshot2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanelGameScreenshotsLayout = new javax.swing.GroupLayout(jPanelGameScreenshots);
        jPanelGameScreenshots.setLayout(jPanelGameScreenshotsLayout);
        jPanelGameScreenshotsLayout.setHorizontalGroup(
            jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelGameScreenshot1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGameScreenshot2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanelGameScreenshotsLayout.setVerticalGroup(
            jPanelGameScreenshotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameScreenshotsLayout.createSequentialGroup()
                .addComponent(jLabelGameScreenshot1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelGameScreenshot2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelGameBackgroundImage.setBorder(javax.swing.BorderFactory.createTitledBorder("Background Image"));

        jLabelGameBackgroundImage.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameBackgroundImage.setToolTipText("");
        jLabelGameBackgroundImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabelGameBackgroundImage.setPreferredSize(new java.awt.Dimension(375, 2));

        javax.swing.GroupLayout jPanelGameBackgroundImageLayout = new javax.swing.GroupLayout(jPanelGameBackgroundImage);
        jPanelGameBackgroundImage.setLayout(jPanelGameBackgroundImageLayout);
        jPanelGameBackgroundImageLayout.setHorizontalGroup(
            jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameBackgroundImageLayout.setVerticalGroup(
            jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                .addComponent(jLabelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jPanelGameRearCover.setBorder(javax.swing.BorderFactory.createTitledBorder("Rear Cover"));
        jPanelGameRearCover.setPreferredSize(new java.awt.Dimension(192, 290));

        jLabelGameRearCover.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameRearCover.setToolTipText("");
        jLabelGameRearCover.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanelGameRearCoverLayout = new javax.swing.GroupLayout(jPanelGameRearCover);
        jPanelGameRearCover.setLayout(jPanelGameRearCoverLayout);
        jPanelGameRearCoverLayout.setHorizontalGroup(
            jPanelGameRearCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRearCoverLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelGameRearCover, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelGameRearCoverLayout.setVerticalGroup(
            jPanelGameRearCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameRearCoverLayout.createSequentialGroup()
                .addComponent(jLabelGameRearCover, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelGameFrontCover.setBorder(javax.swing.BorderFactory.createTitledBorder("Font Cover"));
        jPanelGameFrontCover.setPreferredSize(new java.awt.Dimension(192, 290));

        jLabelGameFrontCover.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameFrontCover.setToolTipText("");
        jLabelGameFrontCover.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanelGameFrontCoverLayout = new javax.swing.GroupLayout(jPanelGameFrontCover);
        jPanelGameFrontCover.setLayout(jPanelGameFrontCoverLayout);
        jPanelGameFrontCoverLayout.setHorizontalGroup(
            jPanelGameFrontCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameFrontCoverLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameFrontCoverLayout.setVerticalGroup(
            jPanelGameFrontCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameFrontCoverLayout.createSequentialGroup()
                .addComponent(jLabelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextFieldGameName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jPanelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanelGameRearCover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelGameScreenshots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelGameDiscImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelDownloads, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelDownloads, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldGameName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanelGameDiscImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelGameScreenshots, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanelGameFrontCover, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                    .addComponent(jPanelGameRearCover, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Event"> 
    private void jCheckBoxFrontCoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxFrontCoverActionPerformed

    }//GEN-LAST:event_jCheckBoxFrontCoverActionPerformed

    private void jCheckBoxRearCoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRearCoverActionPerformed

    }//GEN-LAST:event_jCheckBoxRearCoverActionPerformed

    private void jCheckBoxScreenshotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxScreenshotActionPerformed

    }//GEN-LAST:event_jCheckBoxScreenshotActionPerformed

    private void jCheckBoxBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBackgroundActionPerformed

    }//GEN-LAST:event_jCheckBoxBackgroundActionPerformed

    private void jCheckBoxConfigFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxConfigFileActionPerformed

    }//GEN-LAST:event_jCheckBoxConfigFileActionPerformed

    private void jCheckBoxDiscImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDiscImageActionPerformed

    }//GEN-LAST:event_jCheckBoxDiscImageActionPerformed

    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed
        downloadMissingFiles();
    }//GEN-LAST:event_jButtonDownloadActionPerformed
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JCheckBox jCheckBoxBackground;
    private javax.swing.JCheckBox jCheckBoxConfigFile;
    private javax.swing.JCheckBox jCheckBoxDiscImage;
    private javax.swing.JCheckBox jCheckBoxFrontCover;
    private javax.swing.JCheckBox jCheckBoxRearCover;
    private javax.swing.JCheckBox jCheckBoxScreenshot;
    private javax.swing.JLabel jLabelGameBackgroundImage;
    private javax.swing.JLabel jLabelGameDiscImage;
    private javax.swing.JLabel jLabelGameFrontCover;
    private javax.swing.JLabel jLabelGameRearCover;
    private javax.swing.JLabel jLabelGameScreenshot1;
    private javax.swing.JLabel jLabelGameScreenshot2;
    private javax.swing.JList<String> jListGameList;
    private javax.swing.JPanel jPanelDownloads;
    private javax.swing.JPanel jPanelFiles;
    javax.swing.JPanel jPanelGameBackgroundImage;
    private javax.swing.JPanel jPanelGameDiscImage;
    private javax.swing.JPanel jPanelGameFrontCover;
    private javax.swing.JPanel jPanelGameRearCover;
    private javax.swing.JPanel jPanelGameScreenshots;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldGameName;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>  
}