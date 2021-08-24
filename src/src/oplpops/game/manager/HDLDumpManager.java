package oplpops.game.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class HDLDumpManager {
    private String timeRemaining = "00:00";
    private int percentDownloaded = 0;
    private boolean uploadInProgress;
    
    public HDLDumpManager() {}
    
 
    // Get/Set methods for the download time/percentage
    public String getTimeRemaining() {return timeRemaining;}
    public int getPercentDownloaded() {return percentDownloaded;}
    public boolean getUploadnInProgress() {return uploadInProgress;}
    
    
    // Upload a PS2 game to the console using HDL_Dump
    public void hdlDumpUploadGame(AddGameHDDScreenPS2 addGameScreen, String destination, String gameName, String gamePath) throws IOException, InterruptedException{
        timeRemaining = "0:00";
        new BackgroundWorker(destination, gameName, gamePath, addGameScreen).execute();
    }
    
    
    // Batch upload PS2 games to the console using HDL_Dump
    public void hdlDumpUploadGameBatch(AddGameHDDScreenPS2 addGameScreen, String destination, ArrayList<Path> gamePathList) throws IOException, InterruptedException{
        timeRemaining = "0:00";
        new BatchBackgroundWorker(destination, gamePathList, addGameScreen).execute();
    }
    

    // This switches the OPL directory to the local hdd folder (Used when in HDD mode)
    public static void switchToLocalHDLDirectory(){
        
        // Ensure that the local hdd directory exists, if not try to create it and set it as the OPL directory
        if (createLocalHDLDirectory("hdd")){
            
            // Set the OPL directory to the local hdd directory
            PopsGameManager.setOPLFolder(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator);

            // Try and save the selected OPL directory to the settings.xml file
            try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug("Error saving the settings!\n\n" + ex.toString());} 
        }
    }

    
    // This switches the OPL directory to the local hdd_usb folder (Used when in USB mode)
    public static void switchToLocalHDLUSBDirectory(){
        
        // Ensure that the local hdd_usb directory exists, if not try to create it and set it as the OPL directory
        if (createLocalHDLDirectory("hdd_usb")){
            
            // Set the OPL directory to the local hdd_usb directory
            PopsGameManager.setOPLFolder(PopsGameManager.getCurrentDirectory() + File.separator + "hdd_usb" + File.separator);

            // Try and save the selected OPL directory to the settings.xml file
            try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug("Error saving the settings!\n\n" + ex.toString());} 
        }
    }
    
    
    // This gets the game list from the console using hdd_dump
    public List<Game> hdlDumpGetTOC(String destination) throws IOException, InterruptedException{

        List<Game> gameListPS2 = null;
        boolean errorConnecting = false;
        
        // Create a local directory to store the file for transfering to the console (If the local directory doesnt already exist)
        if (!createLocalHDLDirectory("hdd")) {JOptionPane.showMessageDialog(null,"There was a problem creating the local hdd folder in the same directory as this Jar file."," Unable to Create Directory!",JOptionPane.WARNING_MESSAGE);}

        // Check the users operating system to determine which version of the app to execute 
        String appFolder = "windows";
        String appName = "hdl_dump.exe";

        // If the users operating system is linux or mac
        if (PopsGameManager.getOSType().equals("Linux") || PopsGameManager.getOSType().equals("Mac")){
            appFolder = "linux";
            appName = "hdl_dump";
        }

        // Ensure that the HDL_Dump executable/binary is available before trying to launch it
        if (new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName).exists()){
            
            List<String> commands = new ArrayList<>();
            commands.add(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName);
            commands.add("hdl_toc");
            commands.add(destination);

            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.directory(new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder));
            Process process = processBuilder.start();

            // Buffers for storing the command line output from hdl_dump
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read the output from the command
            String line = null;

            while ((line = stdError.readLine()) != null) {errorConnecting = true;}

            // Try and store the HDD game list to an XML file
            if (!errorConnecting){
                gameListPS2 = new ArrayList<>();
                while ((line = stdInput.readLine()) != null) if (line.contains("DVD")) {addGameToList(gameListPS2, line);}
            }

            // Wait for HDL_Dump
            process.waitFor(); 

            if (errorConnecting) {JOptionPane.showMessageDialog(null, "HDL_Dump reported an error! \n\nPlease ensure that you have HDL_Server running on your PlayStation 2 console. \nAlso make sure that you have enetered the correct IP address.", " HDL_Dump Error!", JOptionPane.ERROR_MESSAGE);} 
        }

        return gameListPS2;
    } 
    
    
    // This determines information about the game before adding it to the list which is passed in
    private static void addGameToList(List<Game> gameListPS2, String line){

        String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};

        // Split the line using whitespace as the deliminator
        String[] splitLine = line.split("\\s+");
        String gameName = "";
        String gameID = null;
        String gameRawSize = null;
        
        // Get the game size without the KB prefix
        gameRawSize = splitLine[1].substring(0,splitLine[1].length()-2);

        // Determine the location of the game unique ID/Region code, within the string array
        int gameIDPosition = 0;
        boolean gameIDFound = false;
        
        for (String regionCode:REGION_CODES) {

            if (splitLine.length >2 && !gameIDFound){
                if (splitLine[2].contains(regionCode)) {
                    gameIDFound = true;
                    gameIDPosition = 2;
                }
            }
            
            if (splitLine.length >3 && !gameIDFound) {
                if (splitLine[3].contains(regionCode)) {
                    gameIDFound = true;
                    gameIDPosition = 3;
                }
            }
            
            if (splitLine.length >4 && !gameIDFound) {
                if (splitLine[4].contains(regionCode)) {
                    gameIDFound = true;
                    gameIDPosition = 4;
                }
            }
            
            if (splitLine.length >5 && !gameIDFound) {
                if (splitLine[5].contains(regionCode)) {
                    gameIDFound = true;
                    gameIDPosition = 5;
                }
            }
        }

        gameID = splitLine[gameIDPosition];
        
        // This gets the game name (does not know how many white spaces are in the game name)
        for (int i = gameIDPosition+1; i < splitLine.length; i++) if (i != splitLine.length -1) {gameName += (splitLine[i] + " ");} else {gameName += (splitLine[i]);} 

        // Add the values to the lists
        if (!"".equals(gameName)) {gameListPS2.add(new Game(gameName, gameID, "PATH HERE!!", PopsGameManager.bytesToHuman(Long.parseLong(gameRawSize)*1024), Long.parseLong(gameRawSize)*1024));}
    }
    

    // If the local hdd folder does not already exist, this attempts to create it
    private static boolean createLocalHDLDirectory(String directoryName){

        File hdlLocalDirectory = new File(PopsGameManager.getCurrentDirectory() + File.separator + directoryName + File.separator);
        boolean directoriesCreated = false;
        
        if (!hdlLocalDirectory.exists()) {
            if (hdlLocalDirectory.mkdir()) {
                directoriesCreated = (new File(PopsGameManager.getCurrentDirectory() + File.separator + directoryName + File.separator + "ART" + File.separator)).mkdirs();
                directoriesCreated = (new File(PopsGameManager.getCurrentDirectory() + File.separator + directoryName + File.separator + "CFG" + File.separator)).mkdirs();
                directoriesCreated = (new File(PopsGameManager.getCurrentDirectory() + File.separator + directoryName + File.separator + "CHT" + File.separator)).mkdirs();
                directoriesCreated = (new File(PopsGameManager.getCurrentDirectory() + File.separator + directoryName + File.separator + "POPS" + File.separator)).mkdirs();
                directoriesCreated = (new File(PopsGameManager.getCurrentDirectory() + File.separator + directoryName + File.separator + "VMC" + File.separator)).mkdirs();
            } 
        }
        return hdlLocalDirectory.exists() || directoriesCreated;
    }
    
    
    // Background worker thread: this uploads multiple games to the console and updates the progress bar in the GUI
    public class BatchBackgroundWorker extends SwingWorker<Object, File> {
    
        String destination;
        String name;
        String path;
        String downloadSpeed = null;
        boolean errorConnecting = false;
        JLabel labelTimeRemaining; 
        JLabel labelDownloadSpeed; 
        JProgressBar progressBar;
        JTextField gameNameDisplayText;
        JTextField gameCounterDisplayText;
        AddGameHDDScreenPS2 addGameScreen;
        ArrayList<Path> gamePathList;
        String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
        
        public BatchBackgroundWorker(String destination, ArrayList<Path> gamePathList, AddGameHDDScreenPS2 addGameScreen) {
            this.destination = destination;
            this.labelTimeRemaining = addGameScreen.getTimeRemainingLabel();
            this.labelDownloadSpeed = addGameScreen.getUploadSpeedLabel();
            this.progressBar = addGameScreen.getProgressBar();
            this.gameNameDisplayText = addGameScreen.getGameNameLabel();
            this.gameCounterDisplayText = addGameScreen.getGameCounterLabel();
            
            this.addGameScreen = addGameScreen;
            this.gamePathList = gamePathList;
        }

        @Override
        protected Object doInBackground() throws Exception {

            // Check the users operating system to determine which version of the app to execute 
            String appFolder = "windows";
            String appName = "hdl_dump.exe";

            if (PopsGameManager.getOSType().equals("Linux") || PopsGameManager.getOSType().equals("Mac")){
                appFolder = "linux";
                appName = "hdl_dump";
            }

            
            // Loop through all of the games
            int count = 0;
            for (Path game : gamePathList){

                count += 1;
                
                // Get the game name and path
                name = game.getFileName().toString().substring(0, game.getFileName().toString().length()-4);
                path = game.toString();
                
                // If the game name contains the region code at the start of the name, this removes it
                if (name.length() > 4){for (String regionCode : REGION_CODES){if (name.substring(0, 5).equals(regionCode)) {name = name.substring(12, name.length());}}}

                // Set the game name in the text field
                gameNameDisplayText.setText(" " + name);
                
                // Set the game ptrocessed counter in the text field
                gameCounterDisplayText.setText(count + "/" + gamePathList.size());
                
                List<String> commands = new ArrayList<>();
                commands.add(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName);
                commands.add("inject_dvd");
                commands.add(destination);
                commands.add(name);
                commands.add(path);
                commands.add("*u4");

                ProcessBuilder processBuilder = new ProcessBuilder(commands);
                processBuilder.directory(new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder));
                Process process = processBuilder.start();

                uploadInProgress = true;

                // Buffers for storing the command line output from hdl_dump
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                // Read the output from the command
                String line = null;

                while ((line = stdInput.readLine()) != null) {

                    // Remove all whitespace from the string
                    line = line.replaceAll(" ", "");

                    if (line.contains(",")) {

                        String[] splitLine = line.split(",");
                        boolean isFirstDigit = false;
                        boolean isSecondDigit = false;

                        int minutes = 0;
                        int seconds = 0;

                        // This hideous bit of code determines the number of digits in the string and calculates the percent downloaded and time remaining
                        if (splitLine.length == 4){

                            if (splitLine[0].length() >= 1){isFirstDigit = (splitLine[0].charAt(0) >= '0' && splitLine[0].charAt(0) <= '9');}
                            if (splitLine[0].length() >= 2){isSecondDigit = (splitLine[0].charAt(1) >= '0' && splitLine[0].charAt(1) <= '9');}

                            if (isSecondDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,2));}
                            else if (isFirstDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,1));}

                            if (splitLine[1].length() >= 1){isFirstDigit = (splitLine[1].charAt(0) >= '0' && splitLine[1].charAt(0) <= '9');}
                            if (splitLine[1].length() >= 2){isSecondDigit = (splitLine[1].charAt(1) >= '0' && splitLine[1].charAt(1) <= '9');}

                            if (isSecondDigit){minutes = Integer.valueOf(splitLine[1].substring(0,2));}
                            else if (isFirstDigit){minutes = Integer.valueOf(splitLine[1].substring(0,1));}

                            isFirstDigit = false;
                            isSecondDigit = false;

                            if (splitLine[2].length() >= 1){isFirstDigit = (splitLine[2].charAt(0) >= '0' && splitLine[2].charAt(0) <= '9');}
                            if (splitLine[2].length() >= 2){isSecondDigit = (splitLine[2].charAt(1) >= '0' && splitLine[2].charAt(1) <= '9');}

                            if (isSecondDigit){seconds = Integer.valueOf(splitLine[2].substring(0,2));}
                            else if (isFirstDigit){seconds = Integer.valueOf(splitLine[2].substring(0,1));}

                            timeRemaining = Integer.toString(minutes) + ":" + Integer.toString(seconds);
                            downloadSpeed = splitLine[3];
                        }
                        else if (splitLine.length == 3){

                            if (splitLine[0].length() >= 1){isFirstDigit = (splitLine[0].charAt(0) >= '0' && splitLine[0].charAt(0) <= '9');}
                            if (splitLine[0].length() >= 2){isSecondDigit = (splitLine[0].charAt(1) >= '0' && splitLine[0].charAt(1) <= '9');}

                            if (isSecondDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,2));}
                            else if (isFirstDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,1));}

                            isFirstDigit = false;
                            isSecondDigit = false;

                            if (splitLine[1].length() >= 1){isFirstDigit = (splitLine[1].charAt(0) >= '0' && splitLine[1].charAt(0) <= '9');}
                            if (splitLine[1].length() >= 2){isSecondDigit = (splitLine[1].charAt(1) >= '0' && splitLine[1].charAt(1) <= '9');}

                            if (isSecondDigit){seconds = Integer.valueOf(splitLine[1].substring(0,2));}
                            else if (isFirstDigit){seconds = Integer.valueOf(splitLine[1].substring(0,1));}

                            if (splitLine[1].contains("secremaining")){timeRemaining = "0:" + Integer.toString(seconds);}
                            else if (splitLine[1].contains("minremaining")){timeRemaining = Integer.toString(seconds) + ":00";}

                            downloadSpeed = splitLine[2];
                        }
                    }
                    else {
                        if (line.length() ==4){percentDownloaded = Integer.valueOf(line.substring(0, 3));}
                        else{percentDownloaded = Integer.valueOf(line.substring(0, 1));}
                    }

                    if (downloadSpeed != null){
                        labelTimeRemaining.setText(timeRemaining);
                        labelDownloadSpeed.setText(downloadSpeed);
                    }

                    progressBar.setValue(percentDownloaded);
                    progressBar.repaint();
                }

                // Wait for HDL_Dump
                process.waitFor(); 

                while ((line = stdError.readLine()) != null) {errorConnecting = true;}

                if (errorConnecting) JOptionPane.showMessageDialog(null, "HDL_Dump reported an error! \n\nPlease ensure that you have HDL_Server running on your PlayStation 2 console. \nAlso make sure that you have enetered the correct IP address.", " HDL_Dump Error!", JOptionPane.ERROR_MESSAGE); 
            
            // END OF LOOP!!
            }
            
            return null;
        }

        @Override
        protected void done(){
            
            if (!errorConnecting) {
                labelTimeRemaining.setText("00:00");
                labelDownloadSpeed.setText("0MB/sec");
                progressBar.setValue(100);
                progressBar.repaint();
            }
            
            uploadInProgress = false;
            addGameScreen.setUploadInProgress(false);
            addGameScreen.closeDialog();
            
            List<Game> gameList = null;

            // Try and get the PS2 game list from the console, write the game list.dat file, callback to update the main gui
            HDLDumpManager hdlDump = new HDLDumpManager();
            try {
                gameList = hdlDump.hdlDumpGetTOC(PopsGameManager.getPS2IP()); 

                if (gameList != null && gameList.size() >0){
                    GameListManager.writeGameListFilePS2(gameList);

                    // Try and load the game data from the PS2 game list file
                    try {GameListManager.createGameListFromFile("PS2", new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS2"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error creating the PS2 game list from file!\n\n" + ex.toString());}
                }
            } 
            catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            
            if (gameList != null && gameList.size()>0){PopsGameManager.callbackToUpdateGUIGameList(null, gameList.size()-1);}
        }
    }
    
    
    // Background worker thread: this uploads a game to the console and updates the progress bar in the GUI
    public class BackgroundWorker extends SwingWorker<Object, File> {
    
        String destination;
        String name;
        String path;
        String downloadSpeed = null;
        boolean errorConnecting = false;
        JLabel labelTimeRemaining; 
        JLabel labelDownloadSpeed; 
        JProgressBar progressBar;
        AddGameHDDScreenPS2 addGameScreen;
        
        public BackgroundWorker(String destination, String name, String path, AddGameHDDScreenPS2 addGameScreen) {
            this.destination = destination;
            this.name = name;
            this.path = path;
            this.labelTimeRemaining = addGameScreen.getTimeRemainingLabel();
            this.labelDownloadSpeed = addGameScreen.getUploadSpeedLabel();
            this.progressBar = addGameScreen.getProgressBar();
            this.addGameScreen = addGameScreen;
        }

        @Override
        protected Object doInBackground() throws Exception {

            // Check the users operating system to determine which version of the app to execute 
            String appFolder = "windows";
            String appName = "hdl_dump.exe";

            if (PopsGameManager.getOSType().equals("Linux") || PopsGameManager.getOSType().equals("Mac")){
                appFolder = "linux";
                appName = "hdl_dump";
            }

            List<String> commands = new ArrayList<>();
            commands.add(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName);
            commands.add("inject_dvd");
            commands.add(destination);
            commands.add(name);
            commands.add(path);
            commands.add("*u4");

            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.directory(new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder));
            Process process = processBuilder.start();
            
            uploadInProgress = true;

            // Buffers for storing the command line output from hdl_dump
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read the output from the command
            String line = null;

            while ((line = stdInput.readLine()) != null) {

                // Remove all whitespace from the string
                line = line.replaceAll(" ", "");
                
                if (line.contains(",")) {
 
                    String[] splitLine = line.split(",");
                    boolean isFirstDigit = false;
                    boolean isSecondDigit = false;

                    int minutes = 0;
                    int seconds = 0;
                    
                    // This hideous bit of code determines the number of digits in the string and calculates the percent downloaded and time remaining
                    if (splitLine.length == 4){

                        if (splitLine[0].length() >= 1){isFirstDigit = (splitLine[0].charAt(0) >= '0' && splitLine[0].charAt(0) <= '9');}
                        if (splitLine[0].length() >= 2){isSecondDigit = (splitLine[0].charAt(1) >= '0' && splitLine[0].charAt(1) <= '9');}
                        
                        if (isSecondDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,2));}
                        else if (isFirstDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,1));}

                        if (splitLine[1].length() >= 1){isFirstDigit = (splitLine[1].charAt(0) >= '0' && splitLine[1].charAt(0) <= '9');}
                        if (splitLine[1].length() >= 2){isSecondDigit = (splitLine[1].charAt(1) >= '0' && splitLine[1].charAt(1) <= '9');}
                        
                        if (isSecondDigit){minutes = Integer.valueOf(splitLine[1].substring(0,2));}
                        else if (isFirstDigit){minutes = Integer.valueOf(splitLine[1].substring(0,1));}
                        
                        isFirstDigit = false;
                        isSecondDigit = false;
                        
                        if (splitLine[2].length() >= 1){isFirstDigit = (splitLine[2].charAt(0) >= '0' && splitLine[2].charAt(0) <= '9');}
                        if (splitLine[2].length() >= 2){isSecondDigit = (splitLine[2].charAt(1) >= '0' && splitLine[2].charAt(1) <= '9');}
                        
                        if (isSecondDigit){seconds = Integer.valueOf(splitLine[2].substring(0,2));}
                        else if (isFirstDigit){seconds = Integer.valueOf(splitLine[2].substring(0,1));}
                        
                        timeRemaining = Integer.toString(minutes) + ":" + Integer.toString(seconds);
                        downloadSpeed = splitLine[3];
                    }
                    else if (splitLine.length == 3){
                        
                        if (splitLine[0].length() >= 1){isFirstDigit = (splitLine[0].charAt(0) >= '0' && splitLine[0].charAt(0) <= '9');}
                        if (splitLine[0].length() >= 2){isSecondDigit = (splitLine[0].charAt(1) >= '0' && splitLine[0].charAt(1) <= '9');}
                        
                        if (isSecondDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,2));}
                        else if (isFirstDigit){percentDownloaded = Integer.valueOf(splitLine[0].substring(0,1));}

                        isFirstDigit = false;
                        isSecondDigit = false;
                                            
                        if (splitLine[1].length() >= 1){isFirstDigit = (splitLine[1].charAt(0) >= '0' && splitLine[1].charAt(0) <= '9');}
                        if (splitLine[1].length() >= 2){isSecondDigit = (splitLine[1].charAt(1) >= '0' && splitLine[1].charAt(1) <= '9');}
                        
                        if (isSecondDigit){seconds = Integer.valueOf(splitLine[1].substring(0,2));}
                        else if (isFirstDigit){seconds = Integer.valueOf(splitLine[1].substring(0,1));}
                        
                        if (splitLine[1].contains("secremaining")){timeRemaining = "0:" + Integer.toString(seconds);}
                        else if (splitLine[1].contains("minremaining")){timeRemaining = Integer.toString(seconds) + ":00";}

                        downloadSpeed = splitLine[2];
                    }
                }
                else {
                    if (line.length() ==4){percentDownloaded = Integer.valueOf(line.substring(0, 3));}
                    else{percentDownloaded = Integer.valueOf(line.substring(0, 1));}
                }

                if (downloadSpeed != null){
                    labelTimeRemaining.setText(timeRemaining);
                    labelDownloadSpeed.setText(downloadSpeed);
                }

                progressBar.setValue(percentDownloaded);
                progressBar.repaint();
            }

            // Wait for HDL_Dump
            process.waitFor(); 

            while ((line = stdError.readLine()) != null) {errorConnecting = true;}

            if (errorConnecting) JOptionPane.showMessageDialog(null, "HDL_Dump reported an error! \n\nPlease ensure that you have HDL_Server running on your PlayStation 2 console. \nAlso make sure that you have enetered the correct IP address.", " HDL_Dump Error!", JOptionPane.ERROR_MESSAGE); 

            return null;
        }

        @Override
        protected void done(){
            
            if (!errorConnecting) {
                labelTimeRemaining.setText("00:00");
                labelDownloadSpeed.setText("0MB/sec");
                progressBar.setValue(100);
                progressBar.repaint();
            }
            
            uploadInProgress = false;
            addGameScreen.setUploadInProgress(false);
            
            List<Game> gameList = null;
            
            // Try and get the PS2 game list from the console, write the game list.dat file, callback to update the main gui
            HDLDumpManager hdlDump = new HDLDumpManager();
            try {
                gameList = hdlDump.hdlDumpGetTOC(PopsGameManager.getPS2IP()); 

                if (gameList != null && gameList.size() >0){
                    GameListManager.writeGameListFilePS2(gameList);

                    // Try and load the game data from the PS2 game list file
                    try {GameListManager.createGameListFromFile("PS2", new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS2"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error creating the PS2 game list from file!\n\n" + ex.toString());}
                }
            } 
            catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            
            if (gameList != null && gameList.size()>0){PopsGameManager.callbackToUpdateGUIGameList(null, gameList.size()-1);}
        }
    }
    


    // This launches HDL_Dump for a locally connected USB drive
    public static boolean launchHDLDumpLocal(String destination, String command1, String command2, String command3) throws IOException, InterruptedException{

        List<Game> gameListPS2 = new ArrayList<>();
        boolean errorConnecting = false;
        boolean ps2HDDFound = false;
        
        // Create a local directory to store the file for transfering to the console (If the local directory doesnt already exist)
        if (!createLocalHDLDirectory("hdd_usb")) {JOptionPane.showMessageDialog(null,"There was a problem creating the local hdd_usb folder in the same directory as this Jar file."," Unable to Create Directory!",JOptionPane.WARNING_MESSAGE);}

        // Check the users operating system to determine which version of the app to execute 
        String appFolder = "windows";
        String appName = "hdl_dump.exe";

        if (PopsGameManager.getOSType().equals("Linux") || PopsGameManager.getOSType().equals("Mac")){
            appFolder = "linux";
            appName = "hdl_dump";
        }

        List<String> commands = new ArrayList<>();
        commands.add(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName);
        commands.add(command1);
        commands.add(destination);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder));
        Process process = processBuilder.start();
        
        // Buffers for storing the command line output from hdl_dump
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // Read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            
            if (s.contains("DVD")) {

                // Split the line using whitespace as the deliminator
                String[] splitString = s.split("\\s+");
                
                // Remove the KB prefif from the game size
                splitString[1] = splitString[1].substring(0,splitString[1].length()-2);
                
                // Add the values to the lists
                gameListPS2.add(new Game(splitString[3], splitString[2], "PATH HERE!!", PopsGameManager.bytesToHuman(Long.parseLong(splitString[1])*1024), Long.parseLong(splitString[1])));
            }
        }
        
        // If any games have been found on the PS2 hard drive, calculate the total size
        if (!gameListPS2.isEmpty()){
            JOptionPane.showMessageDialog(null, "Games list has been retrived from your PS2 HDD.", " Connection Successful", JOptionPane.INFORMATION_MESSAGE);
            ps2HDDFound = true;
        }
        
        // Read any errors from the attempted command and display a message to the user
        while ((s = stdError.readLine()) != null) {errorConnecting = true;}

        // If HDL_Dump reported an error
        if (errorConnecting) {ps2HDDFound = false;}
        else {
            
            // Try and store the HDD game list to an XMF file for next time
            if (!gameListPS2.isEmpty()) {GameListManager.writeGameListFilePS2(gameListPS2);}
            else {JOptionPane.showMessageDialog(null, "Games list has been retrived from your PS2 console (No Games Found).", " Connection Successful", JOptionPane.INFORMATION_MESSAGE);}
        }

        // Wait for HDL_Dump
        process.waitFor();    
        
        return ps2HDDFound;
    } 
}