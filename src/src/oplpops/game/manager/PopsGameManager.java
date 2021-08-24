package oplpops.game.manager;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import javax.imageio.ImageIO;

public class PopsGameManager {

    // <editor-fold defaultstate="collapsed" desc="Private Variables">
    private static final String RELEASE_DATE = "06 January 2018";
    private static final String OLD_APP_NAME = "OPLPOPS-Manager_0.6.jar";
    private static final String CURRENT_APP_NAME = "OPLPOPS-Manager_0.6.1.jar";
    private static final String CURRENT_VERSION_NUMBER = "0.6.1";
    private static final String FORM_TITLE = "OPLPOPS Game Manager - v0.6.1 (Beta)";
    private static final String[] ALL_PREVIOUS_VERSIONS = {"0.0","0.1","0.2","0.3","0.4","0.5","0.6"};
    
    private static boolean DebugMode = false;
    private static boolean TestMode = false;
    private static String userOperatingSystem;
    private static String userOperatingSystemVersion;
    private static String userOSArchitecture;
    private static String userMacAddress;
    
    private static final List<MyListener> LISTENERS = new ArrayList<>();
    private static boolean firstLaunch = false;
    private static String ps2IPAddress = "192.168.0.01";
    private static String oplFolder;
    private static String currentConsole;
    private static String currentMode;
    
    private static boolean emulatorInUsePS1 = false; 
    private static String emulatorPathPS1 = ""; 
    private static boolean emulatorFullScreenPS1 = false;
    private static boolean emulatorInUsePS2 = false; 
    private static String emulatorPathPS2 = ""; 
    private static boolean emulatorFullScreenPS2 = false;
    
    private static String remoteVCDPath = "hdd0:/__.POPS";
    private static String remoteELFPath = "hdd0:/+OPL/APPS";
    private static String remoteOPLPath = "hdd0:/+OPL";
    
    private static boolean gameListRetrievedPS1 = false;
    private static boolean gameListRetrievedPS2 = false;
    
    private static boolean gameCompatabilityPS1 = true;
    private static boolean splitGameDisplayPS2 = true;
    
    private static String gameIDPositionPS1 = "end";
    private static String gameIDPositionPS2 = "start";
    // </editor-fold>
    
    
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Public Functions">   
    public static void callbackToUpdateGUIGameList(String gameID, int listIndex){updateGUIGameList(gameID, listIndex);}     // This provides a callback to the main game screen (To update GUI list)
    
    
    
    
    
    
    
    public static void addListener(MyListener listener) {LISTENERS.add(listener);}                                          // Adds callback LISTENERS to the list
    
    public static void setDebugMode(boolean debugMode) {DebugMode = debugMode;}                                             // Set debug mode on or off (if debug on, most of the exception messages will be printed)
    public static void setTestMode(boolean testMode) {TestMode = testMode;}                                                 // Set test mode on or off (if test mode is on, the application will use the test server)
    public static void setCurrentConsole(String console){currentConsole = console;}                                         // This sets the current console and saves it to the settings.xml file
    public static void setFisrtLaunch(boolean first) {firstLaunch = first;}                                                 // This sets the first launch boolean value
    public static void setPS2IP(String ipAddress) {ps2IPAddress = ipAddress;}                                               // Sets the PS2 IP address  
    public static void setCurrentMode(String mode) {currentMode = mode;}                                                    // This sets the current mode and saves it to the settings.xml file
    public static void setOPLFolder(String oplDirectory) {oplFolder = oplDirectory;}                                        // Sets the OPL folder               
    public static void setEmulatorInUsePS1(boolean useEmulator) {emulatorInUsePS1 = useEmulator;}
    public static void setEmulatorPathPS1(String path) {emulatorPathPS1 = path;}
    public static void setEmulatorFullScreenPS1(boolean fullscreen) {emulatorFullScreenPS1 = fullscreen;}
    public static void setEmulatorInUsePS2(boolean useEmulator) {emulatorInUsePS2 = useEmulator;}
    public static void setEmulatorPathPS2(String path) {emulatorPathPS2 = path;}
    public static void setEmulatorFullScreenPS2(boolean fullscreen) {emulatorFullScreenPS2 = fullscreen;}
    public static void setRemoteVCDPath(String path) {remoteVCDPath = path;}
    public static void setRemoteELFPath(String path) {remoteELFPath = path;}
    public static void setRemoteOPLPath(String path) {remoteOPLPath = path;}
    public static void setGameListRetrievedPS1(boolean listRetrieved) {gameListRetrievedPS1 = listRetrieved;}
    public static void setGameListRetrievedPS2(boolean listRetrieved) {gameListRetrievedPS2 = listRetrieved;}
    public static void setGameCompatabilityPS1(boolean gameCompatability) {gameCompatabilityPS1 = gameCompatability;}
    public static void setSplitGameDisplayPS2(boolean splitGameDisplay) {splitGameDisplayPS2 = splitGameDisplay;}
    public static void setGameIDPositionPS1(String position) {gameIDPositionPS1 = position;}  
    public static void setGameIDPositionPS2(String position) {gameIDPositionPS2 = position;}
    
    public static Boolean getFisrtLaunch() {return firstLaunch;}                                                            // Returns the first launch boolean value
    public static String getFormTitle() {return FORM_TITLE;}
    public static String getApplicationVersionNumber() {return CURRENT_VERSION_NUMBER;}                                     // Returns the application version number
    public static String getApplicationReleaseDate() {return RELEASE_DATE;}                                                 // Returns the application release date
    public static String getPS2IP() {return ps2IPAddress;}                                                                  // Returns the PS2 IP address 
    public static String getOPLFolder() {return oplFolder;}                                                                 // Returns the OPL folder
    public static String getCurrentMode() {return currentMode;}                                                             // Returns current mode
    public static String getCurrentConsole() {return currentConsole;}                                                       // Returns current console
    public static String getOSType() {return userOperatingSystem;}                                                          // Returns the OS type (windows, linux etc)
    public static String getOSVersion() {return userOperatingSystemVersion;}                                                // Returns the OS version (xp, 7, 10, ubuntu etc)
    public static String getOSArchitecture() {return userOSArchitecture;}                                                   // Returns the OS architecture (64bit or 86bit)
    public static String getMacAddress() {return userMacAddress;}                                                           // Returns the screen resolution
    public static Boolean getEmulatorInUsePS1() {return emulatorInUsePS1;}
    public static String getEmulatorPathPS1() {return emulatorPathPS1;}
    public static Boolean getEmulatorFullScreenPS1() {return emulatorFullScreenPS1;}
    public static Boolean getEmulatorInUsePS2() {return emulatorInUsePS2;}
    public static String getEmulatorPathPS2() {return emulatorPathPS2;}
    public static Boolean getEmulatorFullScreenPS2() {return emulatorFullScreenPS2;}
    public static String getRemoteVCDPath() {return remoteVCDPath;}
    public static String getRemoteELFPath() {return remoteELFPath;}
    public static String getRemoteOPLPath() {return remoteOPLPath;}
    public static Boolean getGameListRetrievedPS1() {return gameListRetrievedPS1;}
    public static Boolean getGameListRetrievedPS2() {return gameListRetrievedPS2;}
    public static Boolean getGameCompatabilityPS1() {return gameCompatabilityPS1;}
    public static Boolean getSplitGameDisplayPS2() {return splitGameDisplayPS2;}
    public static String getGameIDPositionPS1() {return gameIDPositionPS1;}
    public static String getGameIDPositionPS2() {return gameIDPositionPS2;}
    
    public static Boolean isOPLFolderSet() {return oplFolder != null;}                                                      // Returns a boolean showing if the OPL folder has been set by user                        
    public static Boolean isCurrentConsoleSet() {return currentConsole != null;}                                            // Returns a boolean showing if the current console has been set by user
    
    
    
    // This is used for displaying the captured exceptions if debug mode is set
    public static void displayErrorMessageDebug(String message){
        if (DebugMode){
            System.out.println("\n**********************************************");
            System.out.println("Exception Caught:");
            System.out.println(message);
            System.out.println("**********************************************\n");
        } 
    }
    
    // This is used for displaying the messages if debug mode is set
    public static void displayMessageDebug(String message){
        if (DebugMode){
            System.out.println("\n**********************************************");
            System.out.println(message);
            System.out.println("**********************************************\n");
        } 
    }
    
    
    // This returns the server address
    public static String getServerAddress(){
        if (TestMode){return "192.168.0.60";} else {return "192.168.0.60";}
    }
    
    // This returns the server port number
    public static int getServerPort(){
        if (TestMode){return 9876;} else {return 6789;}
    }
    
    // This loads the settings from the settings.xml file
    public static void loadSettings(){
        XMLFileManager.readSettingsXML();
    }     
    
    
    // This returns the file prefix for SMB and USB mode
    public static String getFilePrefix(){
        String filePrefix = null;
        switch (getCurrentMode()) {
            case "SMB":
                filePrefix = "SB.";
                break;
            case "HDD_USB":
                filePrefix = "XX.";
                break;
            default:
                filePrefix = "";
                break;
        }
        
        return filePrefix;
    }
    
    
    // This sets the OPL directory
    public static String manuallySetOPLDirectory(){
        
        JFileChooser chooser = new JFileChooser();
        
        if (isOPLFolderSet()) {chooser.setCurrentDirectory(new java.io.File(oplFolder));}
        else {chooser.setCurrentDirectory(new java.io.File("."));}

        chooser.setDialogTitle("Set OPL Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {return chooser.getSelectedFile().toString();} else {return null;}
    }
    
    // This opens a directory in explorer
    public static void openDirectory(String directory){
        
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(directory);
            desktop.open(dirToOpen);
        } 
        catch (IllegalArgumentException | IOException ex) {displayErrorMessageDebug("Error opening directory!\n\n" + ex.toString());}
    }
    
    // This determines the game region based on the games ID
    public static String determineGameRegion(String gameID){
        
        String gameRegion;
        switch (gameID) {
            case "SLES":
            case "SCES":
                gameRegion = "PAL";
                break;
            case "SLUS":
            case "SCUS":
                gameRegion = "NTSCU";
                break;
            case "SLPM":
            case "SLKA": 
            case "SCPS":
            case "SLPS":
            case "ESPM": 
            case "CPCS":
            case "SCAJ":
            case "GUST":
            case "SCCS":
            case "SLAJ":
                gameRegion = "NTSCJ";
                break;
            default:
                gameRegion = "NTSCJ";
                break;
        }
        return gameRegion;
    }
    
    // This returns the current directory of the .Jar file
    public static String getCurrentDirectory(){
        
        CodeSource codeSource = PopsGameManager.class.getProtectionDomain().getCodeSource();
        File jarFile;
        String jarDirectory = null;
        try {
            jarFile = new File(codeSource.getLocation().toURI().getPath());
            jarDirectory = jarFile.getParentFile().getPath();
        } 
        catch (URISyntaxException ex) {displayErrorMessageDebug("Unable to detect the current directory of the jar file!\n\n" + ex.toString());}  

      return jarDirectory;
    }
    
    // This enables the user to select an image file using a dialog (Image gets copied to OPL directory and re-sized)
    public static Image manualImageSelection(String coverType, String gameName, String gameID){
        
        Image image = null;
        
        if (!isOPLFolderSet()) {JOptionPane.showMessageDialog(null, "OPL directory is not set.");}
        else {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(oplFolder));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setDialogTitle("Select Image File");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("IMAGE FILES", "PNG", "JPG");
            chooser.setFileFilter(filter);
            chooser.showOpenDialog(null);
            File imageFile = chooser.getSelectedFile();

            // Attempts to load, re-size and copy the image file to the OPL ART directory
            try {
                if (imageFile != null){
                    
                    image = ImageIO.read(imageFile);
                
                    // Re-scale the image (Different scales depending on the image)
                    BufferedImage scaledImage = null;
                    switch (coverType) {
                        case "_ICO":
                            scaledImage = scaleImage(image, 64, 64);                                                      // Disc image
                            break;
                        case "_BG":
                            scaledImage = scaleImage(image, 640, 480);                                                    // Background image
                            break;
                        case "_SCR":
                        case "_SCR2":
                            scaledImage = scaleImage(image, 250, 188);                                                    // Screenshot images
                            break;
                        case "_COV":
                            if (getCurrentConsole().equals("PS1")) {scaledImage = scaleImage(image, 140, 140);}           // PS1 front cover image
                            else if (getCurrentConsole().equals("PS2")) {scaledImage = scaleImage(image, 140, 200);}      // PS2 front cover image
                            break;
                            case "_COV2":
                            if (getCurrentConsole().equals("PS1")) {scaledImage = scaleImage(image, 140, 140);}           // PS1 rear cover image
                            else if (getCurrentConsole().equals("PS2")) {scaledImage = scaleImage(image, 242, 344);}      // PS2 rear cover image
                            break;
                        default:
                            break;
                    }

                    // Get the file extension for the image file
                    String imageExtension = null;
                    int i = imageFile.getPath().lastIndexOf('.');
                    if (i > 0) {imageExtension = imageFile.getPath().substring(i+1);}

                    // Copy the re-scaled image file to the OPL ART directory
                    if (imageExtension != null) {

                        if (getCurrentConsole().equals("PS1")){
                            
                            if (imageExtension.equals("png")) {

                                // If a jpg file with the same name already exists in the directory, delete it
                                File jpgFile = new File(getOPLFolder() + File.separator + "ART" + File.separator + getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverType + ".jpg");
                                if(jpgFile.exists() && !jpgFile.isDirectory()) {jpgFile.delete();}

                                ImageIO.write(scaledImage, "png",new File(getOPLFolder() + File.separator + "ART" + File.separator + getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverType + ".png"));
                            }
                            else if (imageExtension.equals("jpg")) {

                                // If a png file with the same name already exists in the directory, delete it
                                File pngFile = new File(getOPLFolder() + File.separator + "ART" + File.separator + getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverType + ".png");
                                if(pngFile.exists() && !pngFile.isDirectory()) {pngFile.delete();}

                                ImageIO.write(scaledImage, "jpg",new File(getOPLFolder() + File.separator + "ART" + File.separator + getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverType + ".jpg")); 
                            }
                        }
                        else if (getCurrentConsole().equals("PS2")){
                            
                            if (imageExtension.equals("png")) {

                                // If a jpg file with the same name already exists in the directory, delete it
                                File jpgFile = new File(getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverType + ".jpg");
                                if(jpgFile.exists() && !jpgFile.isDirectory()) {jpgFile.delete();}

                                ImageIO.write(scaledImage, "png",new File(getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverType + ".png"));
                            }
                            else if (imageExtension.equals("jpg")) {

                                // If a png file with the same name already exists in the directory, delete it
                                File pngFile = new File(getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverType + ".png");
                                if(pngFile.exists() && !pngFile.isDirectory()) {pngFile.delete();}

                                ImageIO.write(scaledImage, "jpg",new File(getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverType + ".jpg")); 
                            }
                        }
                    } 
                }
            } 
            catch (IOException ex) {displayErrorMessageDebug("Error processing the image file!\n\n" + ex.toString());}
        }
        return image;
    }
    
    // This formats a double value to a float
    private static String doubleToFloat(double d){return new DecimalFormat("#.##").format(d);}

    // This converts a bytes value to human readable form
    public static String bytesToHuman(long size){
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return doubleToFloat(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return doubleToFloat((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return doubleToFloat((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return doubleToFloat((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return doubleToFloat((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return doubleToFloat((double)size / Pb) + " Pb";
        if (size >= Eb)                 return doubleToFloat((double)size / Eb) + " Eb";

        return "???";
    }
    // </editor-fold>
    

    // <editor-fold defaultstate="collapsed" desc="Private Functions">   
    private static void updateGUIGameList(String gameID, int listIndex){LISTENERS.stream().forEach((listener) -> {listener.updateGameList(gameID, listIndex);});}                                    // Callback to update the games list in the MainScreen

    
    // This determines what Operating system 
    private static void determineOSVersion() {

        // Determine the users operating system and architecture
        String operatingSystemName = System.getProperty("os.name");
        String OperatingSystemArchitecture = System.getProperty("os.arch");

        if (operatingSystemName.contains("Windows")) {
            userOperatingSystemVersion = (String) operatingSystemName.subSequence(8, operatingSystemName.length());
            operatingSystemName = "Windows";
        }
        else if (operatingSystemName.contains("Linux")) {
            userOperatingSystemVersion = (String) operatingSystemName.subSequence(5, operatingSystemName.length());
            operatingSystemName = "Linux";
        }
        else if (operatingSystemName.contains("Mac")) {
            userOperatingSystemVersion = (String) operatingSystemName.subSequence(5, operatingSystemName.length());
            operatingSystemName = "Mac";
        }
        
        if (OperatingSystemArchitecture.substring(OperatingSystemArchitecture.length()-2, OperatingSystemArchitecture.length()).equals("64")) {OperatingSystemArchitecture = "64bit";}
        else if (OperatingSystemArchitecture.substring(OperatingSystemArchitecture.length()-2, OperatingSystemArchitecture.length()).equals("86")) {OperatingSystemArchitecture = "32bit";}
        
        userOperatingSystem = operatingSystemName;
        userOSArchitecture = OperatingSystemArchitecture;
        
        // Store the users MAC address
        determineMacAddress();
    }                                                              
    

    // Gets the users MAC address
    private static void determineMacAddress(){

        boolean firstAddressFound = false;
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            
            while(networks.hasMoreElements()) {
                
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();
                
                if(mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));}
                    if (!firstAddressFound) {userMacAddress = sb.toString();}
                    
                    firstAddressFound = true;
                }
            }
        } catch (SocketException ex) {displayErrorMessageDebug(ex.toString());}
    }

    
    // This scales an image file
    private static BufferedImage scaleImage(Image originalImage, int width, int height) {
        
        BufferedImage buffImage = null;
        
        try {   
            buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D)buffImage.createGraphics();
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY));
            boolean b = g2d.drawImage(originalImage, 0, 0, width, height, null);
        }
        catch (Exception ex){displayErrorMessageDebug("Error scaling the image file!\n\n" + ex.toString());}
        
        return buffImage;
    }
    
    
    // Delete jar file for previous version of the app
    private static void deletePreviousAppVersion(){
 
        // Delete all previous versions of the application if they exist in the directory
        for (String previousVersion : ALL_PREVIOUS_VERSIONS){
            File previousApp = new File(getCurrentDirectory() + File.separator + OLD_APP_NAME.substring(0, OLD_APP_NAME.length()-7) + previousVersion + ".jar");
            if (previousApp.exists() && previousApp.isFile()) {previousApp.delete();}  
        }
        
        // Delete previous .bat and .sh files
        File windowsBatFile = new File(getCurrentDirectory() + File.separator + "start-windows.bat");
        File linuxBatFile = new File(getCurrentDirectory() + File.separator + "start-linux.sh");
        if (windowsBatFile.exists() && windowsBatFile.isFile()) {windowsBatFile.delete();}
        if (linuxBatFile.exists() && linuxBatFile.isFile()) {linuxBatFile.delete();}
        
        // Generate the new Windows .bat file
        String dataString = "@echo off\n" + "set root=" + getCurrentDirectory() + "\nCD /D %root%\njava -jar " + CURRENT_APP_NAME;
        byte[] data = dataString.getBytes();
        Path file = Paths.get(getCurrentDirectory() + File.separator + "start-oplpops.bat");
        try {Files.write(file, data);} catch (IOException ex) {displayErrorMessageDebug("Error creating the .bat file!\n\n" + ex.toString());}
        data = null;
        
        // Generate the new Linux .sh file
        dataString = "#!/bin/sh\njava -jar $(dirname \"$0\")/" + CURRENT_APP_NAME;
        data = dataString.getBytes();
        file = Paths.get(getCurrentDirectory() + File.separator + "start-oplpops.sh");
        try {Files.write(file, data);} catch (IOException ex) {displayErrorMessageDebug("Error creating the .sh file!\n\n" + ex.toString());}
        
        // Generate the latest read me file
        ReadMeFileWritter readMeWritter = new ReadMeFileWritter();
        readMeWritter.WriteFile();
    }
    

    // This checks if the current version of cue2pops is the latest version, otherwise this replaces it
    private static void checkCue2Pops(){
        
        if (userOperatingSystem.equals("Windows")){
            
            String cue2popsMD5 = "29429ee96127be3f24568b1a9db65d4c";
            File currentCue2PopsFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + "windows" + File.separator + "cue2pops.exe");
            
            if (currentCue2PopsFile.exists() && currentCue2PopsFile.isFile()){
                String currentCue2PopsMD5 = PerformQuickHashCheck(currentCue2PopsFile);

                // If the MD5 does not match the version on the server, this downloads the latest version
                if (!currentCue2PopsMD5.equals(cue2popsMD5)){
                    MyTCPClient tcpClient = new MyTCPClient();
                    
                    // Create the temporary cue2pops backup folder
                    new File(currentCue2PopsFile.getParent() + File.separator + "backup").mkdir();
                    
                    // Get the latest version of cue2pops
                    tcpClient.getCue2PopsFromServer(currentCue2PopsFile.getParent() + File.separator + "backup" + File.separator + currentCue2PopsFile.getName(), cue2popsMD5); 
                }
            }  
        }
        else if (userOperatingSystem.equals("Linux") || userOperatingSystem.equals("Mac")){
            
            /*
            String cue2popsMD5 = "84cc4ac0849875a8b1c25f68ead357b7";
            File currentCue2PopsFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + "linux" + File.separator + "cue2pops");
            
            if (currentCue2PopsFile.exists() && currentCue2PopsFile.isFile()){
                String currentCue2PopsMD5 = PerformQuickHashCheck(currentCue2PopsFile);
            
                // If the MD5 does not match the version on the server, this downloads the latest version
                if (!currentCue2PopsMD5.equals(cue2popsMD5)){
                    MyTCPClient tcpClient = new MyTCPClient();
                    
                    // Create the temporary cue2pops backup folder
                    new File(currentCue2PopsFile.getParent() + File.separator + "backup").mkdir();

                    // Get the latest version of cue2pops
                    tcpClient.getCue2PopsFromServer(currentCue2PopsFile.getParent() + File.separator + "backup" + File.separator + currentCue2PopsFile.getName(), cue2popsMD5);
                }
            }
            */
        }
    }
    
    
    // This returns an Md5 hash of the selected file
    public static String PerformQuickHashCheck(File selectedFile){

        // Performs a MD5 hash on a file!
        String fileMD5 = null;
        FileInputStream fileInputStream = null;
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(selectedFile);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fileInputStream.read(dataBytes)) != -1) {messageDigest.update(dataBytes, 0, nread);}   
            byte[] mdbytes = messageDigest.digest();

            // Convert the byte to hex format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));}   
            fileMD5 = sb.toString();
        } 
        catch (FileNotFoundException ex) {displayErrorMessageDebug(ex.toString());} catch (IOException | NoSuchAlgorithmException ex) {displayErrorMessageDebug(ex.toString());} 
        finally {try {if (fileInputStream != null) {fileInputStream.close();}} catch (IOException ex) {}} 
        
        return fileMD5;
    }
    

    // Ensure that all of the required Java libraries have not been moved and are available
    private static boolean checkJavaLibraries(){
        boolean allLibrariesAvailable = true;
        if (!new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "commons-net-3.5.jar").exists()) {allLibrariesAvailable = false;}  
        if (!new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "sevenzipjbinding.jar").exists()) {allLibrariesAvailable = false;}
        if (!new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "sevenzipjbinding-AllPlatforms.jar").exists()){allLibrariesAvailable = false;}
        return allLibrariesAvailable;
    }
    
    // Display message and exit the application if there is a problem
    private static void exitAppWithWarning(){
        Console console = System.console();

        if (console != null) {
            console.format("Unable to locate the required libraries in the \"lib\" directory!%n");
            console.format("The application will exit in 5 seconds.%n");
            // Put the thread to sleep for 5 seconds before exiting
            try {Thread.sleep(5000);}catch (InterruptedException ex){displayErrorMessageDebug(ex.toString());}  

        } else if (!GraphicsEnvironment.isHeadless()) {JOptionPane.showMessageDialog(null,"Unable to locate the required libraries in the \"lib\" directory!\nThe application will now close."," Error Locating Libraries!",JOptionPane.ERROR_MESSAGE);}
    }
    
    // </editor-fold>
    
    
    // Application start
    public static void startApplication(){
        
        // Print the application details in the console
        System.out.println("OPLPOPS Game Manager v" + CURRENT_VERSION_NUMBER + " (" + RELEASE_DATE + ") - by Logi26");

        // This prevents the application from launching if the required Java libraries are not available
        if (!checkJavaLibraries()) {exitAppWithWarning();} 
        else {
            
            PopsGameManager.determineOSVersion();
            
            if (!DebugMode) {System.out.println("");} 
            else {
                System.out.println("\nDEBUG MODE Enabled!\n");
                System.out.println("Detected OS: " + getOSType() + " " + getOSVersion() + " " + getOSArchitecture() + "\n");
            }
            
            PopsGameManager.deletePreviousAppVersion();
            PopsGameManager.checkCue2Pops();
            PopsGameManager.loadSettings();

            if (PopsGameManager.isOPLFolderSet()) {
                try {
                    GameListManager.createGameListsPS1();
                    GameListManager.createGameListsPS2(true);
                    GameListManager.createBadGameListFile();
                } catch(NullPointerException ex){displayErrorMessageDebug(ex.toString());}
            }

            MainScreen gui = new MainScreen();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        }
    }
}