package oplpops.game.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JOptionPane;

// Lightweight 7zip library for reading the ISO file contents
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class GameListManager {
    
    // <editor-fold defaultstate="collapsed" desc="Private Variables">
    private static final String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};

    // Encrypted file paths
    private static final File keyFilePS1 = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "data_1");
    private static final File keyFilePS2 = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "data_2");
    private static final File gameListFilePS1 = new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS1");
    private static final File gameListFilePS2 = new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS2");
    
    private static final File badGameListTextFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "invalidGameList.txt");
    private static final File badGameListEncryptedFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "invalidGameList");
    private static final File keyFileBadGames = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "data_5");
    
    // PS1 lists
    private static List<Game> gameListPS1; 
    private static List<String> gameConfigElmListPS1;  
    private static List<File> invalidGameListPS1;
    
    // PS2 lists
    private static List<Game> gameListPS2;
    private static List<File> invalidGameListPS2;

    // Stores the total game sizes for each console
    private static String totalGameSizeDisplayPS1;
    private static String totalGameSizeDisplayPS2;
    private static long totalGameSizeRawPS1;
    private static long totalGameSizeRawPS2;
    
    // Multi-disc PS1 games
    // Metal Gear Solid - 2 disc game (3 discs for Japan special editions)
    private static final String[] METAL_GEAR_SOLID_NTSCU = {"SLUS_005.94","SLUS_007.76"};
    private static final String[] METAL_GEAR_SOLID_PAL_E = {"SLES_013.70","SLES_113.70"};
    private static final String[] METAL_GEAR_SOLID_PAL_F = {"SLES_015.06","SLES_115.06"};
    private static final String[] METAL_GEAR_SOLID_PAL_G = {"SLES_015.07","SLES_115.07"};
    private static final String[] METAL_GEAR_SOLID_PAL_I = {"SLES_015.08","SLES_115.08"};
    private static final String[] METAL_GEAR_SOLID_PAL_S = {"SLES_017.34","SLES_117.34"};
    private static final String[] METAL_GEAR_SOLID_NTSCJ = {"SCPS_453.17","SCPS-453.18"};
    private static final String[] METAL_GEAR_SOLID_NTSCJ_KONAMI = {"SLPM_864.85","SLPM_864.86"};
    private static final String[] METAL_GEAR_SOLID_NTSCJ_B1 = {"SCPS_453.20","SCPS_453.21","SCPS_453.22"};
    private static final String[] METAL_GEAR_SOLID_NTSCJ_B2 = {"SLPM_861.14","SLPM_861.15","SLPM_861.16"};
    private static final String[] METAL_GEAR_SOLID_NTSCJ_20TH = {"SLPM_874.11","SLPM_874.12","SLPM_874.13"};
    
    // Oddworld Abe Exoddus - 2 disc game
    private static final String[] ODDWORLD_ABE_EXODDUS_NTSCU = {"SLUS_007.10","SLUS_007.31"};
    private static final String[] ODDWORLD_ABE_EXODDUS_PAL_E = {"SLES_014.80","SLES_114.80"};
    private static final String[] ODDWORLD_ABE_EXODDUS_PAL_F = {"SLES_015.02","SLES_115.02"};
    private static final String[] ODDWORLD_ABE_EXODDUS_PAL_G = {"SLES_015.03","SLES_115.03"};
    private static final String[] ODDWORLD_ABE_EXODDUS_PAL_I = {"SLES_015.04","SLES_115.04"};
    private static final String[] ODDWORLD_ABE_EXODDUS_PAL_S = {"SLES_015.05","SLES_115.05"};
    
    // Array of all mult-disc PS1 games
    private static final String[][] MULTI_DISC_GAME_LIST = {
        METAL_GEAR_SOLID_NTSCU, 
        METAL_GEAR_SOLID_PAL_E, 
        METAL_GEAR_SOLID_PAL_F, 
        METAL_GEAR_SOLID_PAL_G, 
        METAL_GEAR_SOLID_PAL_I, 
        METAL_GEAR_SOLID_PAL_S, 
        METAL_GEAR_SOLID_NTSCJ,
        METAL_GEAR_SOLID_NTSCJ_KONAMI,
        METAL_GEAR_SOLID_NTSCJ_B1,
        METAL_GEAR_SOLID_NTSCJ_B2,
        METAL_GEAR_SOLID_NTSCJ_20TH,     
        ODDWORLD_ABE_EXODDUS_NTSCU,
        ODDWORLD_ABE_EXODDUS_PAL_E,
        ODDWORLD_ABE_EXODDUS_PAL_F,
        ODDWORLD_ABE_EXODDUS_PAL_G,
        ODDWORLD_ABE_EXODDUS_PAL_I,
        ODDWORLD_ABE_EXODDUS_PAL_S
    };
    
    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Public Functions">   
    
    // PS1 list functions
    public static String[][] getMultiDiscList() {return MULTI_DISC_GAME_LIST;}                                      // Return the multi-disc 2d array  
    
    
    public static void setGameListPS1(List<Game> newGameList) {gameListPS1 = newGameList;}                          // Sets the list containg all of the PS1 games
    public static List<Game> getGameListPS1() {return gameListPS1;}                                                 // Returns a list containg all of the PS1 games
    public static Game getGamePS1(int gameNumber) {return gameListPS1.get(gameNumber);}                             // Returns a single PS1 game
    public static void setGameSizeDisplayTotalPS1(String totalGameSize){totalGameSizeDisplayPS1 = totalGameSize;}   // Sets the total size of all PS1 games converted to readable format
    public static void setGameSizeRawTotalPS1(long totalGameSize) {totalGameSizeRawPS1 = totalGameSize;}            // Sets the total size of all PS1 games in raw format
    public static String getGameSizeDisplayTotalPS1() {return totalGameSizeDisplayPS1;}                             // Returns the total size of all PS1 games converted to readable format
    public static long getGameSizeRawTotalPS1() {return totalGameSizeRawPS1;}                                       // Returns the total size of all PS1 games in raw format
    public static List<String> getConfigListPS1() {return gameConfigElmListPS1;}                                    // Returns a list containg all of the game configs for "conf_apps.cfg" (PS1 games)
    
    // PS2 list functions
    public static void setGameListPS2(List<Game> newGameList) {gameListPS2 = newGameList;}                          // Sets the list containg all of the PS2 games
    public static List<Game> getGameListPS2() {return gameListPS2;}                                                 // Returns a list containg all of the PS2 games
    public static Game getGamePS2(int gameNumber) {return gameListPS2.get(gameNumber);}                             // Returns a single PS2 game
    public static long getGameSizeRawTotalPS2() {return totalGameSizeRawPS2;}                                       // Returns the total size of all PS2 games in raw format        
    public static String getGameSizeDisplayTotalPS2() {return totalGameSizeDisplayPS2;}                             // Returns the total size of all PS2 games converted to readable format
    public static void setGameSizeDisplayTotalPS2(String totalGameSize){totalGameSizeDisplayPS2 = totalGameSize;}   // Sets the total size of all PS2 games converted to readable format
    public static void setGameSizeRawTotalPS2(long totalGameSize) {totalGameSizeRawPS2 = totalGameSize;}            // Sets the total size of all PS2 games in raw format  

    // This creates the game lists for PS1
    public static void createGameListsPS1() {

        gameListPS1 = new ArrayList<>();
        invalidGameListPS1 = new ArrayList<>();
        gameConfigElmListPS1 = new ArrayList<>();
        
        try {
            createGameListPS1();
            PopsGameManager.callbackToUpdateGUIGameList(null, 0);
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // This creates the game lists for PS2
    public static void createGameListsPS2(boolean checkBadGames) {

        gameListPS2 = new ArrayList<>();
        invalidGameListPS2 = new ArrayList<>();

        try {
            createGameListPS2(checkBadGames);                                
            PopsGameManager.callbackToUpdateGUIGameList(null, 0);      
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // Adds a game to the PS2 game list
    public static void addToGameListsPS2(File isoFile){
        
        String gameID = null;

        // Try and get the game ID from the ISO
        try {gameID = getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        // Get the game name without the directory path or file extension and game ID
        String gameName = isoFile.getName();
        if (gameName.contains(".iso")) {gameName = gameName.replace(".iso", "");}
        if (gameName.contains(gameID + ".")) {gameName = gameName.replace(gameID + ".", "");}
        
        gameListPS2.add(new Game(gameName, gameID, isoFile.toString(), PopsGameManager.bytesToHuman(isoFile.length()), isoFile.length()));
    }
    
    
    
    // Adds a game to the PS2 game list
    public static void addToGameListsPS1(File vcdFile){
        
        String gameID = null;

        // Try and get the game ID from the ISO
        try {gameID = getPS1GameIDFromVCD(vcdFile);} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        // Get the game name without the directory path or file extension and game ID
        String gameName = vcdFile.getName();
        if (gameName.contains(".VCD")) {gameName = gameName.replace(".VCD", "");}
        if (gameName.contains("-" + gameID)) {gameName = gameName.replace("-" + gameID, "");}
        
        gameListPS1.add(new Game(gameName, gameID, vcdFile.toString(), PopsGameManager.bytesToHuman(vcdFile.length()), vcdFile.length()));
    }
    
    
    
    // This gets the PS1 game list from the console using FTP
    public static List<Game> getGameListFromConsolePS1(){
        MyFTPClient ftpClient = new MyFTPClient();
        List<Game> gameList = null;
        if (ftpClient.connectToConsole(PopsGameManager.getPS2IP())){gameList = ftpClient.getGameListPS1();}  
        return gameList;
    }
    

    // This copies the over the main list with new lists
    public static void copyLists(List<Game> gameListPS1, List<Game> gameListPS2){

        // Copy over the PS1 list and main variables
        setGameListPS1(gameListPS1);
        setGameSizeDisplayTotalPS1("");
        setGameSizeRawTotalPS1(0);
        int totalRawSizePS1 = 0;
        if (gameListPS1.size() >0){for (Game ps1Game : gameListPS1){totalRawSizePS1 += ps1Game.getGameRawSize();}}
        setGameSizeRawTotalPS1(totalRawSizePS1 *1024);
        setGameSizeDisplayTotalPS1(PopsGameManager.bytesToHuman(totalRawSizePS1 *1024));
        
        // Copy over the PS2 list and main variables
        setGameListPS2(gameListPS2);
        setGameSizeDisplayTotalPS2("");
        setGameSizeRawTotalPS2(0);
        int totalRawSizePS2 = 0;
        if (gameListPS2.size() >0){for (Game ps2Game : gameListPS2){totalRawSizePS2 += ps2Game.getGameRawSize();}}
        setGameSizeRawTotalPS2(totalRawSizePS2 *1024);
        setGameSizeDisplayTotalPS2(PopsGameManager.bytesToHuman(totalRawSizePS2 *1024));
    }
    
    
    // This creates the gameListPS2.dat for storing the list of PS2 games that are currently on the console
    public static boolean writeGameListFilePS2(List<Game> ps2GameList){
        
        if (ps2GameList == null) {ps2GameList = gameListPS2;}

        List<String> lines = new ArrayList<>();
        for (int i = 0; i < ps2GameList.size(); i++) {lines.add(ps2GameList.get(i).getGameID() + "," + ps2GameList.get(i).getGameRawSize() + "," + ps2GameList.get(i).getGameName());}

        // Encrypt and write the data to the PS1 game list file
        FileEncryptor encryptor = new FileEncryptor();
        encryptor.EncryptData(lines, gameListFilePS2.getAbsolutePath(), keyFilePS2.getAbsolutePath());

        PopsGameManager.setGameListRetrievedPS2(true);      
        
        return gameListFilePS2.exists() && gameListFilePS2.isFile();
    }
    
    
    // This creates the gameListPS1.dat for storing the list of PS1 games that are currently on the console
    public static boolean writeGameListFilePS1(List<Game> ps1GameList){
        
        if (ps1GameList == null) {ps1GameList = gameListPS1;}

        List<String> lines = new ArrayList<>();
        for (int i = 0; i < ps1GameList.size(); i++) {lines.add(ps1GameList.get(i).getGameID() + "," + ps1GameList.get(i).getGameRawSize() + "," + ps1GameList.get(i).getGameName());}

        // Encrypt and write the data to the PS1 game list file
        FileEncryptor encryptor = new FileEncryptor();
        encryptor.EncryptData(lines, gameListFilePS1.getAbsolutePath(), keyFilePS1.getAbsolutePath());
         
        PopsGameManager.setGameListRetrievedPS1(true);      
        
        return gameListFilePS1.exists() && gameListFilePS1.isFile();
    }
    
    
    // Creates the game list from a file
    public static void createGameListFromFile(String console, File file) throws IOException {
        
        long totalSize = 0;

        // Clear any previous files from the game arrays
        if (console.equals("PS1")) {gameListPS1 = new ArrayList<>();}
        else if (console.equals("PS2")) {gameListPS2 = new ArrayList<>();}

        // Read and decrypt the game list file
        FileEncryptor encryptor = new FileEncryptor();

        List<String> decryptedList;
        if (console.equals("PS1")) {decryptedList = encryptor.DecryptData(file.getAbsolutePath(), keyFilePS1.getAbsolutePath());}
        else {decryptedList = encryptor.DecryptData(file.getAbsolutePath(), keyFilePS2.getAbsolutePath());}
        
        for (String line : decryptedList){
            String[] splitLines = line.split(",");
            String gameID = splitLines[0];
            String gameName = splitLines[2];
            long gameRawSize = Long.parseLong(splitLines[1]);
            totalSize += gameRawSize;

            if (console.equals("PS1")){
                
                // This sets the PS1 game compatability values from the text file within the resources
                String compatabilityUSB = "0";
                String compatabilityHDD = "0";
                String compatabilitySMB = "0";

                InputStream in = GameListManager.class.getResourceAsStream("/oplpops/game/manager/PS1CompatabilityList.txt"); 
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                String theLine;
                try {
                    while ((theLine = bufferedReader.readLine()) != null) {
                        if(theLine.substring(0, 11).equals(gameID)){
                            compatabilityUSB = theLine.substring(16, 17);
                            compatabilityHDD = theLine.substring(22, 23);
                            compatabilitySMB = theLine.substring(28, 29);
                        }
                    }
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                Game selectedGame = new Game(gameName, gameID, "GAME PATH HERE!!!!", PopsGameManager.bytesToHuman(gameRawSize), gameRawSize);
                selectedGame.setCompatibleHDD(compatabilityUSB);
                selectedGame.setCompatibleUSB(compatabilityHDD);
                selectedGame.setCompatibleSMB(compatabilitySMB);

                gameListPS1.add(selectedGame);
            }
            else if (console.equals("PS2")){gameListPS2.add(new Game(gameName, gameID, "GAME PATH HERE!!!!", PopsGameManager.bytesToHuman(gameRawSize), gameRawSize));}
        } 

        if (console.equals("PS1")){
            totalGameSizeRawPS1 = totalSize;
            totalGameSizeDisplayPS1 = PopsGameManager.bytesToHuman(totalSize);
        }
        else if (console.equals("PS2")){
            totalGameSizeRawPS2 = totalSize;
            totalGameSizeDisplayPS2 = PopsGameManager.bytesToHuman(totalSize);
        }
    }
    
    
    // This searches the ISO file for the games unique identifier file
    public static String getPS2GameIDFromArchive(String archiveFile) throws Exception {
        IInArchive archive;
        RandomAccessFile randomAccessFile;
        randomAccessFile = new RandomAccessFile(archiveFile, "r");
        archive = SevenZip.openInArchive(ArchiveFormat.ISO, new RandomAccessFileInStream(randomAccessFile));

        String theGameID = null;
        
        for (int i = 0; i <archive.getNumberOfItems(); i++){
            String gameID = archive.getStringProperty(i, PropID.PATH);
            for (String regionCode:REGION_CODES) {if (gameID.contains(regionCode)) {theGameID = gameID;}}
        }

        archive.close();
        randomAccessFile.close();

        return theGameID;
    }
    

    // This searches the VCD file for the games unique identifier string
    public static String getPS1GameIDFromVCD(File vcdfile) throws Exception {

        String theGameID;
        try (FileReader fileReader = new FileReader(vcdfile); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            Boolean gameIDFound = false;
            theGameID = null;
            
            // Check each line for the games region code and unique ID
            while((line = bufferedReader.readLine()) != null && !gameIDFound){
                for (String code:REGION_CODES) if (line.contains(code)) {
                    gameIDFound = true;
                    String[] parts = line.split("_");
                    String regionCode = parts[0].substring(parts[0].length() - 4);
                    String idNumber = truncate(parts[1], 6);
                    
                    // If the game ID does not contain a decimal
                    if (!idNumber.contains(".")){
                        idNumber = truncate(idNumber, 5);                                   // Remove empty char at the end of the string
                        String afterDecimal = idNumber.substring(idNumber.length() - 2);    // Get the last 2 digits
                        idNumber = truncate(idNumber, 3);                                   // Get the first 3 digits
                        idNumber += "." + afterDecimal;                                     // Place a decimal between the digits
                    }
                    theGameID = regionCode + "_" + idNumber;
                }
            }          
        }
        
        return theGameID;
    }

    
    // Generate the conf_apps file using data from the smb POPS folder, hdd game list and usb game list
    public static void writeConfigELM() {

        ArrayList<String> configElmList = new ArrayList<>();
        
        
        File popstarterELFFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF");
        

        
        switch (PopsGameManager.getCurrentMode()) {
            case "SMB":
                if (new File(PopsGameManager.getOPLFolder() + File.separator + "POPS").exists()){
                    try {
                        List<File> filesInFolder = Files.walk(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS")).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
                        filesInFolder.stream().filter((file) -> (file.getName().substring(file.getName().length()-3, file.getName().length()).equals("VCD"))).forEachOrdered((file) -> {
                            configElmList.add(file.getName().substring(0, file.getName().length()-16) + "=smb:/POPS/SB." + file.getName().substring(0, file.getName().length()-4) + ".ELF");
                        });
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}  
                }
                break;
            case "HDD_USB":

                if (new File(PopsGameManager.getOPLFolder() + File.separator + "POPS").exists()){
                    try {
                        List<File> filesInFolder = Files.walk(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS")).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
                        String elfFolder = GameListManager.getFormattedELFFolder();
                        if (elfFolder.contains("+OPL/")) {elfFolder = elfFolder.replace("+OPL/", "");}
                        
                        for (File file : filesInFolder){
                            if (file.getName().substring(file.getName().length()-3, file.getName().length()).toUpperCase().equals("VCD")){
                                configElmList.add(file.getName().substring(0, file.getName().length()-16) + "=mass" + GameListManager.getFormattedELFPartition() + ":" + "/" + elfFolder + "/" + "XX." + file.getName().substring(0, file.getName().length()-4) + ".ELF");
                            }
                        }
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}   
                }
                break;
            case "HDD":
                if (new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS1.dat").exists()){

                    try (Stream<String> lines = Files.lines(Paths.get(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS1.dat"), StandardCharsets.UTF_8)){
                        for (String line : (Iterable<String>) lines::iterator){
                            String[] parts = line.split(",");

                            String path = null;
                            String elfFolder = GameListManager.getFormattedELFFolder();
                            if (elfFolder.contains("+OPL/")) {elfFolder = elfFolder.replace("+OPL/", "");}
                            if (GameListManager.getFormattedELFDrive().equals("hdd")) {path = "=pfs" + GameListManager.getFormattedELFPartition() + ":" + "/" + elfFolder + "/";}
                            else if (GameListManager.getFormattedELFDrive().equals("mass")) {path = "=mass" + GameListManager.getFormattedELFPartition() + ":" + "/" + elfFolder + "/";}

                            //configElmList.add(parts[2] + "-" +  parts[0] + path + parts[2] + "-" + parts[0] + ".ELF");
                            configElmList.add(parts[2] + path + parts[2] + "-" + parts[0] + ".ELF"); 
                        }
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                }
                break;
            default:
                break;
        }

        // Write the conf_apps file
        if (configElmList.size() > 0){
            try {Files.write(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "conf_apps.cfg"), configElmList, Charset.forName("UTF-8"));} 
            catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        } 
        
        // If HDD mode, ask user if they want to upload the conf_apps file to the console via FTP
        if (PopsGameManager.getCurrentMode().equals("HDD")){

            int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to upload conf_apps.cfg to your console?\n\nFTP Server must be running on your console in order to perform this task!"," Connect to PlayStation 2",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){

                // FTP to console and upload the conf_apps.cfg file to the +OPL directory
                MyFTPClient myFTP = new MyFTPClient();
                
                // Connect to the PS2 console
                if (myFTP.connectToConsole(PopsGameManager.getPS2IP())) {

                    // Change to the memory card
                    myFTP.changeDirectory("mc/0/");

                    // If a conf_apps.cfg is already on the memory card, delete it
                    if (myFTP.remoteFileExists("mc", "/mc/0/OPL/", "","conf_apps.cfg", true)){ myFTP.deleteRemoteFile("/mc/0/OPL/conf_apps.cfg");}
                 
                    // Upload new conf_apps.cfg to the memory card
                    myFTP.uploadConfElmToConsole(PopsGameManager.getOPLFolder() + "conf_apps.cfg", "conf_apps.cfg", "/mc/0/OPL/");

                    // Disconnect the FTP connection with the console
                    myFTP.disconnectFromConsole();
                } 
            }
        }
    }
    
    
    // This deletes all of the specified files in a directory (remote and local)
    public static void deleteAllFiles(String directory){
        
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            
            // Delete all files in the local folder
            File selectedFolder = new File(PopsGameManager.getOPLFolder() + File.separator + directory);
            if (selectedFolder.exists() && selectedFolder.isDirectory()){
                try {
                    Files.walkFileTree(Paths.get(selectedFolder.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                PopsGameManager.callbackToUpdateGUIGameList(null, -1);
            }
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            
            // Delete all files from the remote directory and also delete all local files in the hdd folder
            MyFTPClient myFTP = new MyFTPClient();

            if (PopsGameManager.getPS2IP() != null){
                if (myFTP.connectToConsole(PopsGameManager.getPS2IP())){

                    String drive = null;
                    if (getFormattedOPLDrive().equals("hdd")) {drive = "pfs";}
                    else if (getFormattedOPLDrive().equals("mass")) {drive = "mass";}
                    
                    //List<String> remoteDirectoryList = myFTP.listRemoteDirectory("/pfs/0/" + directory, "+OPL");
                    List<String> remoteDirectoryList = myFTP.listRemoteDirectory("/" + drive + "/" + getFormattedOPLPartition() + "/" + directory, "+OPL", true);

                    if (remoteDirectoryList != null && remoteDirectoryList.size() >0){
                        //remoteDirectoryList.forEach((file) -> {myFTP.deleteRemoteFile("/pfs/0/" + directory + "/" + file);});
                        for (String file : remoteDirectoryList){myFTP.deleteRemoteFile("/" + drive + "/" + getFormattedOPLPartition() + "/" + directory + "/" + file);}
                        PopsGameManager.callbackToUpdateGUIGameList(null, -1);
                    } 
                    
                    // Disconnect the FTP connection with the console
                    myFTP.disconnectFromConsole();
                }
                
                // Delete all files in the local ART folder
                File selectedFolder = new File(PopsGameManager.getOPLFolder() + File.separator + directory);
                if (selectedFolder.exists() && selectedFolder.isDirectory()){
                    try {
                        Files.walkFileTree(Paths.get(selectedFolder.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                }
                
                PopsGameManager.callbackToUpdateGUIGameList(null, -1);
            }  
        }
    }
    
    
    // This deletes all of the unused files in a directory (remote and local)
    public static void deleteUnusedFiles(String directory){
        
        // Get the PS1 and PS2 game lists from the files
        File ps1GameList = new File(PopsGameManager.getOPLFolder() + File.separator + "gameListPS1");
        File ps2GameList = new File(PopsGameManager.getOPLFolder() + File.separator + "gameListPS2");

        if (ps1GameList.exists() && ps1GameList.isFile()) {try {createGameListFromFile("PS1", new File(PopsGameManager.getOPLFolder() + File.separator + "gameListPS1"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error creating PS1 game list from file!\n\n" + ex.toString());}}
        if (ps2GameList.exists() && ps2GameList.isFile()) {try {createGameListFromFile("PS2", new File(PopsGameManager.getOPLFolder() + File.separator + "gameListPS2"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error creating PS2 game list from file!\n\n" + ex.toString());}}

        // Add all of the game ID's from the PS1 and PS2 game list into a single list
        List<String> allGameList = new ArrayList<>();
        gameListPS1.forEach((game) -> {allGameList.add(game.getGameID());});
        gameListPS2.forEach((game) -> {allGameList.add(game.getGameID());});

        List<String> deleteFileList = new ArrayList<>();

        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){

            // Delete all files in the local folder
            File selectedFolder = new File(PopsGameManager.getOPLFolder() + File.separator + directory);
            if (selectedFolder.exists() && selectedFolder.isDirectory()){
                deleteFileList.clear();
                File[] localDirectoryList = selectedFolder.listFiles();
                
                for (File localFile : localDirectoryList){
                    boolean fileInUse = false;
                    for (String idInList : allGameList){if (idInList != null && localFile.getName() != null && localFile.getName().contains(idInList)) {fileInUse = true;}}
                    if (!fileInUse) {deleteFileList.add(localFile.getAbsolutePath());}
                }
                
                deleteFileList.stream().filter((fileToDelete) -> (new File(fileToDelete).exists())).forEachOrdered((fileToDelete) -> {new File(fileToDelete).delete();});
            } 
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            
            // Delete all files from the remote directory and also delete all local files in the hdd folder
            MyFTPClient myFTP = new MyFTPClient();

            if (PopsGameManager.getPS2IP() != null){
                if (myFTP.connectToConsole(PopsGameManager.getPS2IP())){
                    
                    String drive = null;
                    if (getFormattedOPLDrive().equals("hdd")) {drive = "pfs";}
                    else if (getFormattedOPLDrive().equals("mass")) {drive = "mass";}
                    
                    //List<String> remoteDirectoryList = myFTP.listRemoteDirectory("/pfs/0/" + directory, "+OPL");
                    List<String> remoteDirectoryList = myFTP.listRemoteDirectory("/" + drive + "/" + getFormattedOPLPartition() + "/" + directory, "+OPL", true);

                    if (remoteDirectoryList != null && remoteDirectoryList.size() >0){
                        
                        remoteDirectoryList.forEach((file) -> {
                            boolean fileInUse = false;
                            for (String idInList : allGameList){if (file.contains(idInList)) {fileInUse = true;}}
                            if (!fileInUse) {deleteFileList.add(file);}
                        });
                        //deleteFileList.forEach((fileToDelete) -> {myFTP.deleteRemoteFile("/pfs/0/" + directory + "/" + fileToDelete);}); 
                        for (String fileToDelete : deleteFileList){myFTP.deleteRemoteFile("/" + drive + "/" + getFormattedOPLPartition() + "/" + directory + "/" + fileToDelete);}

                    } 
                    myFTP.disconnectFromConsole();
                }
                
                // Delete all files in the local folder
                File selectedFolder = new File(PopsGameManager.getOPLFolder() + File.separator + directory);
                if (selectedFolder.exists() && selectedFolder.isDirectory()){
                    
                    deleteFileList.clear();
                    File[] localDirectoryList = selectedFolder.listFiles();

                    for (File localFile : localDirectoryList){
                        boolean fileInUse = false;
                        for (String idInList : allGameList){if (localFile.getName().contains(idInList)) {fileInUse = true;}}
                        if (!fileInUse) {deleteFileList.add(localFile.getAbsolutePath());}
                    }
                    deleteFileList.stream().filter((fileToDelete) -> (new File(fileToDelete).exists())).forEachOrdered((fileToDelete) -> {new File(fileToDelete).delete();});
                }
                PopsGameManager.callbackToUpdateGUIGameList(null, -1);
            }   
        }
    }
    
    
    // Returns the OPL Drive formatted
    public static String getFormattedOPLDrive(){
        String remoteDriveOPL = null;
        String[] splitOplPath = PopsGameManager.getRemoteOPLPath().split("/");
        if (splitOplPath.length > 0){remoteDriveOPL = splitOplPath[0].substring(0, splitOplPath[0].length()-2);}
        return remoteDriveOPL;
    }

    // Returns the OPL Partition formatted
    public static String getFormattedOPLPartition(){
        String remotePartitionOPL = null;
        String[] splitOplPath = PopsGameManager.getRemoteVCDPath().split("/");
        if (splitOplPath.length > 0){remotePartitionOPL = splitOplPath[0].substring(splitOplPath[0].length()-2, splitOplPath[0].length()-1);}
        return remotePartitionOPL;
    }

    // Returns the VCD Drive formatted
    public static String getFormattedVCDDrive(){
        String remoteDriveVCD = null;
        String[] splitVcdPath = PopsGameManager.getRemoteVCDPath().split("/");
        if (splitVcdPath.length > 0){remoteDriveVCD = splitVcdPath[0].substring(0, splitVcdPath[0].length()-2);}
        return remoteDriveVCD;
    }
    
    // Returns the VCD Partition formatted
    public static String getFormattedVCDPartition(){
        String remotePartitionVCD = null;
        String[] splitVcdPath = PopsGameManager.getRemoteVCDPath().split("/");
        if (splitVcdPath.length > 0){remotePartitionVCD = splitVcdPath[0].substring(splitVcdPath[0].length()-2, splitVcdPath[0].length()-1);}
        return remotePartitionVCD;
    }
    
    // Returns the VCD Folder formatted
    public static String getFormattedVCDFolder(){
        String remoteFolderVCD = null;
        String[] splitVcdPath = PopsGameManager.getRemoteVCDPath().split("/");
        if (splitVcdPath.length > 0){remoteFolderVCD = splitVcdPath[1];}
        return remoteFolderVCD;
    }

    // Returns the ELF Drive formatted
    public static String getFormattedELFDrive(){
        String remoteDriveELF = null;
        String[] splitElfPath = PopsGameManager.getRemoteELFPath().split("/");
        if (splitElfPath.length > 0){remoteDriveELF = splitElfPath[0].substring(0, splitElfPath[0].length()-2);}
        return remoteDriveELF;
    }
    
    // Returns the ELF Partition formatted
    public static String getFormattedELFPartition(){
        String remotePartitionELF = null;
        String[] splitElfPath = PopsGameManager.getRemoteELFPath().split("/");
        if (splitElfPath.length > 0){remotePartitionELF = splitElfPath[0].substring(splitElfPath[0].length()-2, splitElfPath[0].length()-1);}
        return remotePartitionELF;
    }
    
    // Returns the ELF Folder formatted
    public static String getFormattedELFFolder(){
        String remoteFolderELF = "";
        String[] splitElfPath = PopsGameManager.getRemoteELFPath().split("/");
        if (splitElfPath.length > 0){for (int i = 1; i < splitElfPath.length; i++){if (i!=1) {remoteFolderELF = remoteFolderELF + "/" + splitElfPath[i];}else {remoteFolderELF = remoteFolderELF + splitElfPath[i];}}}
        return remoteFolderELF;
    }
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc="Private Functions"> 

    // This creates the PS1 game lists
    private static void createGameListPS1() throws IOException {

        switch (PopsGameManager.getCurrentMode()) {
            case "HDD_USB":
            case "SMB":
                createPS1ListFromSMB();
                break;
            case "HDD":
                createGameListFromFile("PS1", gameListFilePS1);
                break;
            default:
                break;
        }
    }
    

    // Creates the PS1 game list from SMB (Using the actual files in the folder)
    private static void createPS1ListFromSMB() {
        
        ArrayList<String> storedBadGameList = readBadGameListFile();
        ArrayList<String> badGameListPS1 = new ArrayList<>();
        long totalSize = 0;
        File [] files = null;
        try {files = getVCDFiles();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        if (files != null){
            
            // Loop through the vcd files
            for (File vcdFile : files) {
                
                String gameID = null;
                
                // Try and get the game ID from the VCD
                try {gameID = getPS1GameIDFromVCD(vcdFile);} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                // Split the string to get the game name without the directory path or file extension
                String gamePath = vcdFile.toString();
                String gameName = gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).substring(0,gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).lastIndexOf('.'));
                gameName = gameName.substring(0, gameName.length() -12);

                // Used to calculate the total size of all VCD files combined
                totalSize += vcdFile.length();

                if (gameID != null) {
                    
                    if (gamePath.contains(gameID)) {
                        
                        // This sets the PS1 game compatability values from the text file within the resources
                        String compatabilityUSB = "0";
                        String compatabilityHDD = "0";
                        String compatabilitySMB = "0";
                        
                        InputStream in = GameListManager.class.getResourceAsStream("/oplpops/game/manager/PS1CompatabilityList.txt"); 
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        
                        String line;
                        try {
                            while ((line = bufferedReader.readLine()) != null) {
                                if(line.substring(0, 11).equals(gameID)){
                                    compatabilityUSB = line.substring(16, 17);
                                    compatabilityHDD = line.substring(22, 23);
                                    compatabilitySMB = line.substring(28, 29);
                                }
                            }
                        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                        
                        
                        // This checks if the game is a mult-disc game
                        boolean multiDiscGame = false;
                        String[] multiDiscArray = null;
                        for (String[] selectedArray : MULTI_DISC_GAME_LIST){
                            
                            for (String selectedGame : selectedArray){
                                if (selectedGame.equals(gameID)){
                                    multiDiscGame = true;
                                    multiDiscArray = selectedArray;
                                }
                            }
                        }
                        
                        // Create the game object
                        Game selectedGame = new Game(gameName, gameID, vcdFile.toString(), PopsGameManager.bytesToHuman(vcdFile.length()), vcdFile.length());
                        selectedGame.setCompatibleHDD(compatabilityUSB);
                        selectedGame.setCompatibleUSB(compatabilityHDD);
                        selectedGame.setCompatibleSMB(compatabilitySMB);
                        
                        if (multiDiscGame){
                            selectedGame.setMultiDiscGame(true);
                            if (multiDiscArray != null) {for (String arrayItem : multiDiscArray){selectedGame.addToMultiDiscList(arrayItem);}}

                            File gameFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + selectedGame.getGameName() + "-" + selectedGame.getGameID());

                            // If the game folder does not exist, make the folder and add the DISCS.txt file
                            if (!gameFolder.exists()){
                                gameFolder.mkdir();

                                // Create the DISC.TXT file
                                try(PrintWriter out = new PrintWriter(gameFolder + File.separator + "DISCS.TXT")){
                                    out.println(selectedGame.getGameName() + "-" + selectedGame.getGameID() + ".VCD");
                                } catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                            } 
                        }

                        gameListPS1.add(selectedGame); 
                    }
                    else {invalidGameListPS1.add(vcdFile);}
                }
                else {
                    if (!storedBadGameList.contains(vcdFile.getName())){badGameListPS1.add(vcdFile.getName());}
                } 
            }

            // Make sure that the the DISC.TXT file is up-to-date for any of the mult-disc games
            checkMultiDiscFiles();

            // Display a message to the user, listing any games were the game ID could not be detected
            if (badGameListPS1.size() > 0) {
                StringBuilder badGames = new StringBuilder();
                badGameListPS1.forEach((game) -> {badGames.append(game).append("\n");});
                JOptionPane.showMessageDialog(null,"The system was unable to detect the unique game ID for the following PS1 games:\n" + badGames," Unable to Detect Game ID!",JOptionPane.WARNING_MESSAGE);
            }
            
            totalGameSizeRawPS1 = totalSize;
            totalGameSizeDisplayPS1 = PopsGameManager.bytesToHuman(totalSize);
        }    

        // If any of the games are not correctly named with the game ID, call the invalid games function
        if (!invalidGameListPS1.isEmpty()) {checkInvalidGamesPS1(totalSize);}  
        
        // This reorganizes the PS1 game list in alphaetical order
        Collections.sort(gameListPS1, new ListOrganiser());
    }
    

    
    
    
    
    
    
    
    
    
    
    
 

    // Encrypts the invalidGameList.txt file
    public static void encryptBadGameListFile(){

        try {
            List<String> list = Files.readAllLines(badGameListTextFile.toPath(), Charset.defaultCharset());
            // Encrypt 
            FileEncryptor encryptor = new FileEncryptor();
            encryptor.EncryptData(list, badGameListEncryptedFile.getAbsolutePath(), keyFileBadGames.getAbsolutePath());
            if (badGameListTextFile.exists() && badGameListTextFile.isFile()) {badGameListTextFile.delete();}
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    // Decrypt the invalidGameList.txt file
    private static void decryptBadGameListFile(){
        
        // Ensure that the settings file exists
        if (new File(badGameListEncryptedFile.getAbsolutePath()).exists() && new File(badGameListEncryptedFile.getAbsolutePath()).isFile()){
            try {
                // Decrypt the encrypted settings file
                FileEncryptor encryptor = new FileEncryptor();
                List<String> list = encryptor.DecryptData(badGameListEncryptedFile.getAbsolutePath(), keyFileBadGames.getAbsolutePath());
                Files.write(Paths.get(PopsGameManager.getCurrentDirectory() + File.separator + "invalidGameList.txt"),list,Charset.defaultCharset());
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
    }

    // Read the invalidGameList.txt file
    public static ArrayList<String> readBadGameListFile(){
        
        ArrayList<String> fileList = new ArrayList<>();
        if (badGameListEncryptedFile.exists() && !badGameListEncryptedFile.isDirectory()){
            decryptBadGameListFile();
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(badGameListTextFile.getAbsolutePath()));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {fileList.add(line);}
                bufferedReader.close();   
            } catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            encryptBadGameListFile();
        }
        return fileList;
    }
    
    // Create the invalidGameList.txt file
    public static void createBadGameListFile(){
        
        ArrayList<String> badGameList = new ArrayList<>();
        File [] files = null;
        
        // Loop through all the VCD files and if we are unable to read the gameID from the file, it gets added to the bad game list
        try {files = getVCDFiles();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        if (files != null){
            for (File vcdFile : files) {
                String gameID = null;
                try {gameID = getPS1GameIDFromVCD(vcdFile);} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                if (gameID == null) {badGameList.add(vcdFile.getName());}
            }
        }     
        
        // Loop through all the ISO files and if we are unable to read the gameID from the file, it gets added to the bad game list
        files = null;
        try {files = getISOFiles();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        if (files != null){
            for (File isoFile : files) {
                String gameID = null;
                try {gameID = getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                if (gameID == null) {badGameList.add(isoFile.getName());}
            }
        }     
        
        // Create the bad game list text file
        FileWriter writer; 
        try {
            writer = new FileWriter(badGameListTextFile);
            for(String badGmaeName: badGameList) {writer.write(badGmaeName);}
            writer.close();
            encryptBadGameListFile();
            
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
    }
    
    
    
    // This ensure that the DISCS.TXT file for each multi-disc game contains all of the other avilable multi-discs
    private static void checkMultiDiscFiles(){

        for (Game ps1Game : gameListPS1){

            // If the game is a multi-disc game
            if (ps1Game.getMultiDiscGame()){
                
                File gameFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + ps1Game.getGameName() + "-" + ps1Game.getGameID());
                
                // Check if the game folder exists (it should have been created in the function that calls this function if it did not exist)
                if (gameFolder.exists() && gameFolder.isDirectory()){
                    
                    File discsFile = new File(gameFolder + File.separator + "DISCS.TXT");
                    
                    // Check if the DISCS.TXT file exists (it should have been created in the function that calls this function if it did not exist)
                    if (discsFile.exists() && discsFile.isFile()){
                        
                        // This get a list of the other mult-disc games that are in the game list
                        ArrayList<String> newDiscsList = new ArrayList<>();
                        ArrayList<String> multiDiscList = ps1Game.getMultiDiscList();
                        for (String gameID : multiDiscList){for (Game game : gameListPS1){if (game.getGameID().equals(gameID)) {newDiscsList.add(game.getGameName() + "-" + game.getGameID() + ".VCD");}}}
                        
                        // This writes the DISCS.TXT file to include the other multi-discs that are associated with it
                        if (!newDiscsList.isEmpty()){
                            try {
                                FileWriter fileWriter = new FileWriter(discsFile.getAbsolutePath());
                                for (String arrayItem : newDiscsList){fileWriter.write(arrayItem + "\n");}
                                fileWriter.close();
                            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                        } 
                    }
                }
            }
        }
    }
    
    
    // This enables the user to rename any PS1 games that are not correctly named
    private static void checkInvalidGamesPS1(long totalSize){

        // Ask the user if they want to try and rename the game files
        int dialogResult = JOptionPane.showConfirmDialog (null, "Some of your PS1 games are not correctly named! \n\nDo you want to try and re-name them?"," Incorrect Game Names",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            GameRenamingScreenPS1 gameRenamingScreen = new GameRenamingScreenPS1(null, true, invalidGameListPS1);
            gameRenamingScreen.setLocationRelativeTo(null);
            gameRenamingScreen.setVisible(true);  
        }
        else {
            /*
            for (File isoFile : invalidGameListPS2) {

                // Try and get the game ID from the ISO
                String gameID = null;
                try {gameID = getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {}

                // Split the string to get the game name without the directory path or file extension
                String gamePath = isoFile.toString();
                String gameName = gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).substring(0,gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).lastIndexOf('.'));
                
                // Used to calculate the total size of all ISO files combined
                totalSize += isoFile.length();

                gameListPS2.add(new Game(gameName, gameID, isoFile.toString(), PopsGameManager.bytesToHuman(isoFile.length()), isoFile.length()));
            } 
            totalGameSizeRawPS2 = totalSize;
            totalGameSizeDisplayPS2 = PopsGameManager.bytesToHuman(totalSize);
            */
        } 
    }
    
    
    
    
    
    
    // This creates the PS2 game lists
    private static void createGameListPS2(boolean checkBadGames) throws IOException {

        switch (PopsGameManager.getCurrentMode()) {
            case "HDD_USB":
                createPS2ListFromSMB(checkBadGames);
                break;
            case "SMB":
                createPS2ListFromSMB(checkBadGames);
                readPS2GamesFromULCFG();
                break;
            case "HDD":
                createGameListFromFile("PS2", new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS2"));
                break;
            default:
                break;
        }
    }
    
    
    
    // Creates the PS2 game list from SMB (Using the actual files in the folder)
    private static void createPS2ListFromSMB(boolean checkBadGames) {

        ArrayList<String> storedBadGameList = readBadGameListFile();
        ArrayList<String> badGameListPS2 = new ArrayList<>();
        long totalSize = 0;
        File [] files = null;
        try {files = getISOFiles();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        if (files != null){
            
            // Loop through the iso files
            for (File isoFile : files) {

                // Try and get the game ID from the ISO
                String gameID = null;
                try {gameID = getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                // Split the string to get the game name without the directory path or file extension
                String gamePath = isoFile.toString();

                if (gameID != null) {
                    
                    if (gamePath.contains(gameID)){

                        String gameName = gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).substring(0,gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).lastIndexOf('.'));
                        gameName = gameName.substring(12);

                        // Used to calculate the total size of all ISO files combined
                        totalSize += isoFile.length();
                        gameListPS2.add(new Game(gameName, gameID, isoFile.toString(), PopsGameManager.bytesToHuman(isoFile.length()), isoFile.length()));
                    }
                    else {invalidGameListPS2.add(isoFile);}
                }
                else {if (checkBadGames) {if (!storedBadGameList.contains(isoFile.getName())){badGameListPS2.add(isoFile.getName());}}} 
            }
            totalGameSizeRawPS2 = totalSize;
            totalGameSizeDisplayPS2 = PopsGameManager.bytesToHuman(totalSize);
        }  
        
        // Display a message to the user, listing any games were the game ID could not be detected
        if (badGameListPS2.size() > 0) {
            StringBuilder badGames = new StringBuilder();
            badGameListPS2.forEach((game) -> {badGames.append(game).append("\n");});
            JOptionPane.showMessageDialog(null,"The system was unable to detect the unique game ID for the following PS2 games:\n" + badGames," Unable to Detect Game ID!",JOptionPane.WARNING_MESSAGE);
        }
        
        // If any of the games are not correctly named with the game ID, call the invalid games function
        if (!invalidGameListPS2.isEmpty()) {checkInvalidGamesPS2(totalSize);} 
        
        // This reorganizes the PS2 game list in alphaetical order
        Collections.sort(gameListPS2, new ListOrganiser());
    }
    
    
    
    
    // This adds all PS2 games from the ul.cfg file to the PS2 game list
    private static void readPS2GamesFromULCFG() {
 
        // If the ul.cfg file exists and contains games
        if (!USBUtil.readULCFG().isEmpty()){
            
            // Add each game from the ul.cfg file to the PS2 game list
            USBUtil.readULCFG().forEach((ulGame) -> {gameListPS2.add(ulGame);});
            
            // This reorganizes the PS2 game list in alphaetical order after the UL games have been added
            Collections.sort(gameListPS2, new ListOrganiser());
        }
    }
    
    
    
    
    
    
    // This enables the user to rename any PS2 games that are not correctly named
    private static void checkInvalidGamesPS2(long totalSize){

        // Ask the user if they want to try and rename the game files
        int dialogResult = JOptionPane.showConfirmDialog (null, "Some of your PS2 games are not correctly named! \n\nDo you want to try and re-name them?"," Incorrect Game Names",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            GameRenamingScreenPS2 gameRenamingScreen = new GameRenamingScreenPS2(null, true, invalidGameListPS2);
            gameRenamingScreen.setLocationRelativeTo(null);
            gameRenamingScreen.setVisible(true);  
        }
        else {

            for (File isoFile : invalidGameListPS2) {

                // Try and get the game ID from the ISO
                String gameID = null;
                try {gameID = getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                // Split the string to get the game name without the directory path or file extension
                String gamePath = isoFile.toString();
                String gameName = gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).substring(0,gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).lastIndexOf('.'));
                
                // Used to calculate the total size of all ISO files combined
                totalSize += isoFile.length();

                gameListPS2.add(new Game(gameName, gameID, isoFile.toString(), PopsGameManager.bytesToHuman(isoFile.length()), isoFile.length()));
            } 
            totalGameSizeRawPS2 = totalSize;
            totalGameSizeDisplayPS2 = PopsGameManager.bytesToHuman(totalSize);
        } 
    }
    
    
    // This returns an array containing all of the .ISO files in the directory
    private static File [] getISOFiles() throws IOException {
        File directory = new File(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator);
        File [] files = directory.listFiles((File dir, String name) -> name.endsWith(".ISO") ||  name.endsWith(".iso"));

        return files;
    }
    
    
    // This returns an array containing all of the .VCD files in the directory
    private static File [] getVCDFiles() throws IOException {
        File directory = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator);
        File [] files = directory.listFiles((File dir, String name) -> name.endsWith(".VCD") ||  name.endsWith(".vcd"));
    
        return files;
    }
    
    
    // This truncates a string
    private static String truncate(String value, int length) {if (value.length() > length) return value.substring(0, length); else return value;}
    
    // </editor-fold>
    
    
    // This is used to sort the game list alphabetically
    public static class ListOrganiser implements Comparator<Game> {
        @Override
        public int compare(Game firstGame, Game secondGame) {return firstGame.getGameName().compareTo(secondGame.getGameName());}
    }  
}