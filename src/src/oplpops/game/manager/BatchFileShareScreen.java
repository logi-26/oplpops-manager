package oplpops.game.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class BatchFileShareScreen extends javax.swing.JDialog {

    //private static final String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
    private static List<String> serverArtList;
    private static List<ServerFile> artNotOnServerList;
    private static List<String> uploadedArtFileList;
    
    private static List<String> serverConfigList;
    private static List<ServerFile> configNotOnServerList;
    private static List<String> uploadedConfigFileList;
    
    final static JProgressBar DOWNLOAD_STATUS_BAR = new JProgressBar(0, 100);
    private int totalFilesShared = 0;

    public BatchFileShareScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initialiseGUI();
        getServerArtList();
        getServerConfigList();
    }

    
    // Class to hold the image file info (The info is used to make it very easy for the server to locate the image without much processing)
    class ServerFile {
        
        public String filePath;
        public String console;
        public String gameID;
        public String coverType;
        public String gameRegion;
        
        public ServerFile(String filePath, String console, String gameID, String coverType, String gameRegion){
            this.filePath = filePath;
            this.console = console;
            this.gameID = gameID;
            this.coverType = coverType;
            this.gameRegion = gameRegion;  
        }
    }
    
    
    // This sets the dialog title and creates the progress bar
    private void initialiseGUI(){
        this.setTitle(" Share Files");
        jPanelProgress.add(DOWNLOAD_STATUS_BAR);
        DOWNLOAD_STATUS_BAR.setBounds(16, 35, 288, 30);
        DOWNLOAD_STATUS_BAR.setValue(0);
        DOWNLOAD_STATUS_BAR.repaint();
    }
    
    
    // Get the art lists from the server
    private void getServerArtList(){
        
        // Download the art file list from the server
        serverArtList = new ArrayList<>();
        MyTCPClient tcpClient = new MyTCPClient();
        
        // This gets all of the art lists from the server
        File serverArtFile;
        tcpClient.getListFromServer("ART", "PS1");
        serverArtFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + "PS1_ServerArtList.dat");
        if(serverArtFile.exists() && !serverArtFile.isDirectory()) {try (Stream<String> lines = Files.lines(serverArtFile.toPath(), Charset.defaultCharset())) {lines.forEachOrdered(line -> serverArtList.add(line));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error getting the PS1 ART list from the server!\n\n" + ex.toString());}}
        
        tcpClient.getListFromServer("ART", "PS2");
        serverArtFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + "PS2_ServerArtList.dat");
        if(serverArtFile.exists() && !serverArtFile.isDirectory()) {try (Stream<String> lines = Files.lines(serverArtFile.toPath(), Charset.defaultCharset())) {lines.forEachOrdered(line -> serverArtList.add(line));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error getting the PS2 ART list from the server!\n\n" + ex.toString());}}
    }
    
    
    // Get the config lists from the server
    private void getServerConfigList(){
        
        // Download the config file list from the server
        serverConfigList = new ArrayList<>();
        MyTCPClient tcpClient = new MyTCPClient();
        
        // This gets all of the config lists from the server
        File serverConfigFile;
        tcpClient.getListFromServer("CONFIG", "PS1");
        serverConfigFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + "PS1_ServerConfigList.dat");
        if(serverConfigFile.exists() && !serverConfigFile.isDirectory()) {try (Stream<String> lines = Files.lines(serverConfigFile.toPath(), Charset.defaultCharset())) {lines.forEachOrdered(line -> serverConfigList.add(line));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error getting the PS1 CFG list from the server!\n\n" + ex.toString());}}
        
        tcpClient.getListFromServer("CONFIG", "PS2");
        serverConfigFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + "PS2_ServerConfigList.dat");
        if(serverConfigFile.exists() && !serverConfigFile.isDirectory()) {try (Stream<String> lines = Files.lines(serverConfigFile.toPath(), Charset.defaultCharset())) {lines.forEachOrdered(line -> serverConfigList.add(line));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error getting the PS2 CFG list from the server!\n\n" + ex.toString());}}
    }
    
    
    // Check the extension of the image file
    private boolean checkImageFileExtension(String coverType, String extension){
        
        boolean badExtension = true;
        if (coverType.equals("_ICO") && extension.contains("png")) {badExtension = false;} else if (coverType.equals("_ICO") && extension.contains("jpg")) {badExtension = true;}

        if ((coverType.equals("_COV") || coverType.equals("_COV2") || coverType.equals("_BG") || coverType.equals("_SCR") || coverType.equals("_SCR2")) && extension.contains("png")) {badExtension = true;}
        else if ((coverType.equals("_COV") || coverType.equals("_COV2") || coverType.equals("_BG") || coverType.equals("_SCR") || coverType.equals("_SCR2")) && extension.contains("jpg")) {badExtension = false;}

        return badExtension;
    }
    
    
    // Check the art files
    private void checkArtFiles(){
        
        MyTCPClient tcpClient = new MyTCPClient();
        
        // Check if the server is responding before performing the batch image upload
        String serverResponse = tcpClient.sendMessageToServer("RESPOND");

        if (serverResponse.equals("RESPONSE")){

            // First determine which ART folders are available
            artNotOnServerList = new ArrayList<>();
            uploadedArtFileList = new ArrayList<>();
            
            String smbFolder = null;
            String hddFolder = null; 

            File smbLocalDirectory = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator);
            if (smbLocalDirectory.exists() && smbLocalDirectory.isDirectory()) {smbFolder = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator;}

            File hddLocalDirectory = new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "ART" + File.separator);
            if (hddLocalDirectory.exists() && hddLocalDirectory.isDirectory()) {hddFolder = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "ART" + File.separator;}

            // This checks all of the image files in the SMB and HDD directories
            if (smbFolder != null) {checkArtFilesSMB(smbLocalDirectory);}
            if (hddFolder != null) {checkArtFilesHDD(hddLocalDirectory);}

            shareArtFiles();
        } 
    }
    

    // This checks all of the art files in the OPL/ART folder
    private void checkArtFilesSMB(File smbLocalDirectory){

        File[] smbART = smbLocalDirectory.listFiles();
        if (smbART != null) {

            for (File localArtFile : smbART) {

                // This gets the file extension
                String imageFileName = localArtFile.getName();
                int index = imageFileName.lastIndexOf(".");
                String extension = imageFileName.substring(index, imageFileName.length());

                if (extension.contains("jpg") || extension.contains("png")){

                    // If the image size is smaller than 500Kb
                    if (localArtFile.length() < 4000000) {

                        // If the images is a PS1 image
                        if (localArtFile.getName().contains(".ELF_")){

                            // This removes the file extension from the image file name
                            imageFileName = AddGameManager.truncate(imageFileName, imageFileName.length() -4);

                            // This gets the cover type and removes the "SB." from the start of the string and the ".ELF_COV" from the end of the string
                            index = imageFileName.lastIndexOf(".");
                            String coverType = imageFileName.substring(index + 4, imageFileName.length());
                            imageFileName = imageFileName.substring(3, index);
                            String gameID = imageFileName.substring(imageFileName.length()-11, imageFileName.length());
                            imageFileName = imageFileName.substring(0,imageFileName.length()-14);

                            // Check the file extension of the image file. _ICO files should be .png all other image files should be .jpg
                            if (!checkImageFileExtension(coverType, extension)){

                                // Determine the game region
                                String[] splitName = gameID.split("_");
                                String gameRegion = PopsGameManager.determineGameRegion(splitName[0]);
                                
                                // All screenshots on the server have the _SCR name
                                if (coverType.equals("_SCR2")) {coverType = "_SCR";}
                                
                                // Generate an MD5 checksum of the local art file
                                String localArtMD5 = PerformQuickHashCheck(localArtFile);
                                
                                // Check the server art list to see if it contains the MD5 of the local file
                                boolean artOnServer = false;
                                for (String artFile : serverArtList){if (artFile.substring(artFile.length()-33, artFile.length()).contains(localArtMD5)){artOnServer = true;}}

                                // If the MD5 of the local file does not match any file on the server, this adds the file to the upload list
                                if (!artOnServer) {artNotOnServerList.add(new ServerFile(localArtFile.getAbsolutePath(), "PS1", gameID, coverType, gameRegion));}
                            }   
                        }
                        // If the image file name does not contain ".ELF" it must be a PS2 image file
                        else {

                            // This removes the file extension from the image file name
                            imageFileName = AddGameManager.truncate(imageFileName, imageFileName.length() -4);

                            // This gets the cover type "_COV"
                            index = imageFileName.lastIndexOf("_");
                            String coverType = imageFileName.substring(index, imageFileName.length());

                            // Check the file extension of the image file. _ICO files should be .png all other image files should be .jpg
                            if (!checkImageFileExtension(coverType, extension)){

                                // This gets the game ID from the image file name
                                String gameID = imageFileName.substring(0, index);
                                String[] splitName = gameID.split("_");

                                // This determines the game region
                                String gameRegion = PopsGameManager.determineGameRegion(splitName[0]);

                                // All screenshots on the server have the _SCR name
                                if (coverType.equals("_SCR2")) {coverType = "_SCR";}
                                
                                // Generate an MD5 checksum of the local art file
                                String localArtMD5 = PerformQuickHashCheck(localArtFile);

                                // Check the server art list to see if it contains the MD5 of the local file
                                boolean artOnServer = false;
                                for (String artFile : serverArtList){if (artFile.substring(artFile.length()-33, artFile.length()).contains(localArtMD5)){artOnServer = true;}}

                                // If the MD5 of the local file does not match any file on the server, this adds the file to the upload list
                                if (!artOnServer) {artNotOnServerList.add(new ServerFile(localArtFile.getAbsolutePath(), "PS2", gameID, coverType, gameRegion));}
                            }
                        } 
                    }
                }
            }
        }  
    }
    
    
    // This checks all of the art files in the HDD/ART folder
    private void checkArtFilesHDD(File hddLocalDirectory){
        
        File[] hddART = hddLocalDirectory.listFiles();

        for (File localArtFile : hddART) {

            // This gets the file extension
            String imageFileName = localArtFile.getName();
            int index = imageFileName.lastIndexOf(".");
            String extension = imageFileName.substring(index, imageFileName.length());

            if (extension.contains("jpg") || extension.contains("png")){

                // If the image size is smaller than 500Kb
                if (localArtFile.length() < 4000000) {

                    // If the images is a PS1 image
                    if (localArtFile.getName().contains(".ELF_")){

                        // This removes the file extension from the image file name
                        imageFileName = AddGameManager.truncate(imageFileName, imageFileName.length() -4);

                        // This gets the cover type and removes the "SB." from the start of the string and the ".ELF_COV" from the end of the string
                        index = imageFileName.lastIndexOf(".");
                        String coverType = imageFileName.substring(index + 4, imageFileName.length());
                        imageFileName = imageFileName.substring(3, index);
                        String gameID = imageFileName.substring(imageFileName.length()-12, imageFileName.length()-1);
                        imageFileName = imageFileName.substring(0,imageFileName.length()-14);
                        
                        // Check the file extension of the image file. _ICO files should be .png all other image files should be .jpg
                        if (!checkImageFileExtension(coverType, extension)){

                            // Determine the game region
                            String[] splitName = gameID.split("_");
                            String gameRegion = PopsGameManager.determineGameRegion(splitName[0]);
                            
                            // Check if the config file is on the server
                            boolean artOnServer = false;
                            for (String artFile : serverArtList){if (artFile.substring(0, 11).equals(gameID) && artFile.contains(coverType)) {artOnServer = true;}}

                            if (!artOnServer) {artNotOnServerList.add(new ServerFile(localArtFile.getAbsolutePath(),"PS1",gameID,coverType,gameRegion));}
                            else {

                                // Generate an MD5 checksum of the backup config file
                                String localArtMD5 = PerformQuickHashCheck(localArtFile);
 
                                // Check if the MD5 of the local art file is the same as the version on the server
                                for (String artFile : serverArtList){  

                                    // If the MD5 does not match, add the file to the list
                                    if (artFile.substring(0, 11).equals(gameID)){
                                        if (!localArtMD5.equals(artFile.substring(artFile.length()-32, artFile.length()))){artNotOnServerList.add(new ServerFile(localArtFile.getAbsolutePath(),"PS1",gameID,coverType,gameRegion));}
                                    }
                                }
                            }
                            
                        }  
                    }
                    // If the image file name does not contain ".ELF" it must be a PS2 image file
                    else {

                        // This removes the file extension from the image file name
                        imageFileName = AddGameManager.truncate(imageFileName, imageFileName.length() -4);

                        // This gets the cover type "_COV"
                        index = imageFileName.lastIndexOf("_");
                        String coverType = imageFileName.substring(index, imageFileName.length());

                        // Check the file extension of the image file. _ICO files should be .png all other image files should be .jpg
                        if (!checkImageFileExtension(coverType, extension)){

                            // This gets the game ID from the image file name
                            String gameID = imageFileName.substring(0, index);
                            String[] splitName = gameID.split("_");

                            // This determines the game region
                            String gameRegion = PopsGameManager.determineGameRegion(splitName[0]);

                             // Check if the config file is on the server
                            boolean artOnServer = false;
                            for (String artFile : serverArtList){if (artFile.substring(0, 11).equals(gameID) && artFile.contains(coverType)) {artOnServer = true;}}
                            
                            if (!artOnServer) {artNotOnServerList.add(new ServerFile(localArtFile.getAbsolutePath(),"PS2",gameID,coverType,gameRegion));}
                            else {

                                // Generate an MD5 checksum of the backup config file
                                String localArtMD5 = PerformQuickHashCheck(localArtFile);
 
                                // Check if the MD5 of the local art file is the same as the version on the server
                                for (String artFile : serverArtList){  

                                    // If the MD5 does not match, add the file to the list
                                    if (artFile.substring(0, 11).equals(gameID)){
                                        if (!localArtMD5.equals(artFile.substring(artFile.length()-32, artFile.length()))){artNotOnServerList.add(new ServerFile(localArtFile.getAbsolutePath(),"PS2",gameID,coverType,gameRegion));}
                                    }
                                }
                            }
                        }
                    } 
                }
            }
        }
    }
    

    // Check the config files
    private void checkConfigFiles(){
        
        MyTCPClient tcpClient = new MyTCPClient();
        
        // Check if the server is responding before performing the batch image upload
        String serverResponse = tcpClient.sendMessageToServer("RESPOND");

        if (serverResponse.equals("RESPONSE")){

            // First determine which CONFIG folders are available
            configNotOnServerList = new ArrayList<>();
            uploadedConfigFileList = new ArrayList<>();
            
            String smbFolder = null;
            String hddFolder = null;

            File smbLocalDirectory = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator);

            if (smbLocalDirectory.exists() && smbLocalDirectory.isDirectory()) {smbFolder = PopsGameManager.getOPLFolder() + "CFG" + File.separator;}

            File hddLocalDirectory = new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "CFG" + File.separator);
            if (hddLocalDirectory.exists() && hddLocalDirectory.isDirectory()) {hddFolder = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "CFG" + File.separator;}

            // This checks all of the image files in the SMB, HDD and HDD_USB directories
            if (smbFolder != null) {checkConfigFilesSMB(smbLocalDirectory);}
            if (hddFolder != null) {checkConfigFilesHDD(hddLocalDirectory);}
            
            shareConfigFiles();
        } 
    }
    

    // This checks to see if the config file is already in the server config file list
    private void checkConfigFilesSMB(File smbLocalDirectory){

        File[] smbCFG = smbLocalDirectory.listFiles();
        for (File localConfigFile : smbCFG) {
            
            // This gets the file extension
            String configFileName = localConfigFile.getName();
            int index = configFileName.lastIndexOf(".");
            String extension = configFileName.substring(index, configFileName.length());

            // Ensure that the file is a config file
            if (extension.contains("cfg")){

                // If the filename length is greater than 15 it is a PS1 config file, otherwise it is a PS2 config file
                if (localConfigFile.getName().length() >15){
                    
                    String gameID = localConfigFile.getName().substring(localConfigFile.getName().length()-19, localConfigFile.getName().length()-8);
                    String gameRegion = gameID.substring(0, gameID.length()-7);

                    // Check if the config file is on the server
                    boolean configOnServer = false;
                    for (String configFile : serverConfigList){if (configFile.substring(0, 11).equals(gameID)) {configOnServer = true;}}
                    
                    // If the config file is not on the server, add it to the list, If the config is on the server, perform MD5 and compare
                    if (!configOnServer){configNotOnServerList.add(new ServerFile(localConfigFile.getAbsolutePath(),"PS1",gameID,null,PopsGameManager.determineGameRegion(gameRegion)));}
                    else {
                        
                        // Generate a backup config file which does not contain the (CfgVersion and Title) lines
                        File backupConfigFile = generateBackupConfigFile(localConfigFile);

                        // Generate an MD5 checksum of the backup config file
                        String localConfigMD5 = PerformQuickHashCheck(backupConfigFile);

                        // Check if the MD5 of the local config file is the same as the version on the server
                        for (String configFile : serverConfigList){  

                            // If the MD5 does not match, add the file to the list
                            if (configFile.substring(0, 11).equals(gameID)){
                                if (!localConfigMD5.equals(configFile.substring(configFile.length()-32, configFile.length()))){configNotOnServerList.add(new ServerFile(localConfigFile.getAbsolutePath(),"PS1",gameID,null,PopsGameManager.determineGameRegion(gameRegion)));}
                            }
                        }

                        // Delete the backup file
                        backupConfigFile.delete();
                    }
                }
                // If the filename length = 15, it is a PS2 config file
                else if (localConfigFile.getName().length() == 15){
                    String gameID = localConfigFile.getName().substring(0, localConfigFile.getName().length()-4);
                    String gameRegion = gameID.substring(0, gameID.length()-7);
                    
                    // Check if the config file is on the server
                    boolean configOnServer = false;
                    for (String configFile : serverConfigList){if (configFile.substring(0, 11).equals(gameID)) {configOnServer = true;}}
                    
                    if (!configOnServer) {
                        configNotOnServerList.add(new ServerFile(localConfigFile.getAbsolutePath(),"PS2",gameID,null,PopsGameManager.determineGameRegion(gameRegion)));
                    }
                    else {
                        
                        // Generate a backup config file which does not contain the (CfgVersion and Title) lines
                        File backupConfigFile = generateBackupConfigFile(localConfigFile);

                        // Generate an MD5 checksum of the backup config file
                        String localConfigMD5 = PerformQuickHashCheck(backupConfigFile);

                        // Check if the MD5 of the local config file is the same as the version on the server
                        for (String configFile : serverConfigList){  

                            // If the MD5 does not match, add the file to the list
                            if (configFile.substring(0, 11).equals(gameID)){
                                if (!localConfigMD5.equals(configFile.substring(configFile.length()-32, configFile.length()))){configNotOnServerList.add(new ServerFile(localConfigFile.getAbsolutePath(),"PS2",gameID,null,PopsGameManager.determineGameRegion(gameRegion)));}
  
                            }
                        }

                        // Delete the backup file
                        backupConfigFile.delete();  
                    }
                }
            }  
        }
    }
    
    
    // This generates the backup config file which does not contain the CfgVersion and Title values (first 2 lines removed from the config file)
    private static File generateBackupConfigFile(File originalConfigFile){

        File backupConfigFile = new File(originalConfigFile.getAbsolutePath().substring(0, originalConfigFile.getAbsolutePath().length()-4) + "_backup.cfg");

        // Read the config file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(originalConfigFile))) {
            String line;
            int lineNumber = 0;

            // Create a copy of the file with the appended name "_backup" (Ignores the first 2 lines of the config file)
            try{
                PrintWriter writer = new PrintWriter(backupConfigFile.getAbsolutePath(), "UTF-8");

                // Read each line of the original config file, but ignore the first 2 lines (which contain: CfgVersion and Title)
                while ((line = br.readLine()) != null) {
                    lineNumber ++;

                    // Write the lines to the new backup config file
                    if (lineNumber > 2){writer.println(line);} 
                }
                
                writer.close();

            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        } catch (FileNotFoundException ex) {} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}  

        return backupConfigFile;
    }
    

    // This returns an Md5 hash of the selected file
    private String PerformQuickHashCheck(File selectedFile){

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
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {stringBuilder.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));}   
            fileMD5 = stringBuilder.toString();
        } 
        catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} catch (IOException | NoSuchAlgorithmException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
        finally {try {if (fileInputStream != null) {fileInputStream.close();}} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}} 
        
        return fileMD5;
    }
    
    
    // This checks all of the config files in the HDD/ART folder
    private void checkConfigFilesHDD(File hddLocalDirectory){}
    

    
    // This shares the art files with the server
    private void shareArtFiles(){
        
        if (artNotOnServerList.isEmpty()) JOptionPane.showMessageDialog(null,"You do not have any ART files to share with the server."," No ART!",JOptionPane.INFORMATION_MESSAGE); 
        else {
            DOWNLOAD_STATUS_BAR.setMinimum(0);
            DOWNLOAD_STATUS_BAR.setMaximum(artNotOnServerList.size()); 
            artNotOnServerList.stream().forEach((imageFile) -> {new BackgroundWorker(imageFile.console, imageFile.gameRegion, imageFile.gameID, imageFile.coverType, imageFile.filePath).execute();});  
        }
    }
    
    
    // This shares the config files with the server
    private void shareConfigFiles(){
        
        if (configNotOnServerList.isEmpty()) JOptionPane.showMessageDialog(null,"You do not have any Config files to share with the server."," No CFG!",JOptionPane.INFORMATION_MESSAGE); 
        else {
            DOWNLOAD_STATUS_BAR.setMinimum(0);
            DOWNLOAD_STATUS_BAR.setMaximum(configNotOnServerList.size()); 
            configNotOnServerList.stream().forEach((file) -> {new BackgroundWorker(file.console, file.gameRegion, file.gameID, null, file.filePath).execute();});  
        }
    }
    

    // Background worker thread: this downloads the file from the server and updates the progress bar in the GUI
    public class BackgroundWorker extends SwingWorker<Object, File> {

        private final String console;
        private final String gameRegion;
        private final String gameID;
        private final String coverType;
        private final String filePath;

        public BackgroundWorker(String console, String gameRegion, String gameID, String coverType, String filePath) {
            
            this.console = console;
            this.gameRegion = gameRegion;
            this.gameID = gameID;
            this.coverType = coverType;
            this.filePath = filePath;
        }

        @Override
        protected Object doInBackground() throws Exception {

            MyTCPClient tcpClient = new MyTCPClient();
            
            if (coverType!= null) {tcpClient.shareImageWithServer(console, gameRegion, gameID, coverType, filePath);}
            else {tcpClient.shareConfigWithServer(console, gameRegion, gameID, filePath);}     
            
            totalFilesShared +=1;
            DOWNLOAD_STATUS_BAR.setValue(totalFilesShared);
            DOWNLOAD_STATUS_BAR.repaint();
            
            if (coverType!= null){uploadedArtFileList.add(filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length()));} 
            else {uploadedConfigFileList.add(filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length()));}
            
            return null;
        }

        @Override
        protected void done(){

            DOWNLOAD_STATUS_BAR.setValue(DOWNLOAD_STATUS_BAR.getMaximum());
            DOWNLOAD_STATUS_BAR.repaint();        
            
            ArrayList<String> completeUploadedList = new ArrayList<>();
            uploadedArtFileList.forEach((uploadedFile) -> {if (!completeUploadedList.contains(uploadedFile)){completeUploadedList.add(uploadedFile);}});
            uploadedConfigFileList.forEach((uploadedFile) -> {if (!completeUploadedList.contains(uploadedFile)){completeUploadedList.add(uploadedFile);}});
            createList(completeUploadedList.toArray(new String[0]));
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListGameList = new javax.swing.JList<>();
        jPanelProgress = new javax.swing.JPanel();
        jButtonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Share Files"));

        jScrollPane1.setViewportView(jListGameList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));

        jButtonStart.setText("Start");
        jButtonStart.setToolTipText("");
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProgressLayout.createSequentialGroup()
                .addContainerGap(219, Short.MAX_VALUE)
                .addComponent(jButtonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProgressLayout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addComponent(jButtonStart)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events"> 
    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed
        checkArtFiles();
        checkConfigFiles();
    }//GEN-LAST:event_jButtonStartActionPerformed
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonStart;
    private javax.swing.JList<String> jListGameList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}