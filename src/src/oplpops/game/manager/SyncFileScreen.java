package oplpops.game.manager;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class SyncFileScreen extends javax.swing.JDialog {

    private static final String LOCAL_DIRECTORY_ART = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "ART" + File.separator;
    private static final String LOCAL_DIRECTORY_CONFIG = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "CFG" + File.separator;
    private static final String LOCAL_DIRECTORY_CHEAT_PS2 = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "CHT" + File.separator;
    private static final String LOCAL_DIRECTORY_VMC_PS2 = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "VMC" + File.separator;
    private static final String LOCAL_DIRECTORY_PS1 = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "POPS" + File.separator;
    private static String currentLocalDirectory = LOCAL_DIRECTORY_ART;
    private String selectedPartition = "+OPL";
    
    private String REMOTE_DIRECTORY_ART;
    private String REMOTE_DIRECTORY_CONFIG;
    private String REMOTE_DIRECTORY_CHEAT;
    private String REMOTE_DIRECTORY_VMC;

    private MyFTPClient myFTP;

    
    // Constructor
    public SyncFileScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        displayIPAddress();
        overideClose();
        initialiseGUI();
        initialiseFilePaths();
    }
    
    
    // Initialise the GUI elements
    private void initialiseGUI(){
        jButtonDeleteFile.setMargin(new Insets(0,0,0,0));
        jButtonTransferFromConsole.setMargin(new Insets(0,0,0,0));
        jButtonTransferToConsole.setMargin(new Insets(0,0,0,0));   
        jButtonConnectToConsole.setMargin(new Insets(0,0,0,0));
        jButtonSyncFiles.setMargin(new Insets(0,0,0,0));
        jComboBoxPartition.setSelectedItem(PopsGameManager.getRemoteOPLPath());
        jFormattedTextFieldIPAddress.requestFocus();
    }
    
    
    // Initialise the file paths
    private void initialiseFilePaths(){
        String remotePath = null;
        if (GameListManager.getFormattedOPLDrive().equals("hdd")) {remotePath = "/pfs/" + GameListManager.getFormattedOPLPartition() + "/";}
        else if (GameListManager.getFormattedOPLDrive().equals("mass")) {remotePath = "/mass/" + GameListManager.getFormattedOPLPartition() + "/";}

        REMOTE_DIRECTORY_ART = remotePath + "ART/";
        REMOTE_DIRECTORY_CONFIG = remotePath + "CFG/";
        REMOTE_DIRECTORY_CHEAT = remotePath + "CHT/";
        REMOTE_DIRECTORY_VMC = remotePath + "VMC/";
    }
    
    
    // This checks to see if the remote OPL direvtories exist
    public void checkRemoteOPLDirectories(){
        
        myFTP = new MyFTPClient();
        if (myFTP.connectToConsole(PopsGameManager.getPS2IP())) {
            //myFTP.checkOPLDirectoriesOnConsole();
            myFTP.disconnectFromConsole();
        }
    }
    

    // This composes the list of files from the local hdd directories
    public void composeLocalFileList(String directory){
        
        currentLocalDirectory = directory;
        List<String> localFileList = new ArrayList<>();
        
        myFTP.disconnectFromConsole();
        myFTP.connectToConsole(PopsGameManager.getPS2IP());
        myFTP.changeDirectory("hdd/0/" + selectedPartition);

        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) if (file.isFile() && !file.isDirectory()) {
            
            if (currentLocalDirectory.equals(LOCAL_DIRECTORY_ART)){
                
                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                    if (file.getName().length() >=3){
                        
                        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
                            if (file.getName().substring(0, 3).equals(PopsGameManager.getFilePrefix())){localFileList.add(file.getName());}
                        } 
                        else {
                            localFileList.add(file.getName());
                        }
                    }

                }
                else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                    if (file.getName().length() >=3){if (!file.getName().substring(0, 3).equals(PopsGameManager.getFilePrefix())){localFileList.add(file.getName());}} 
                }
            }
            else {localFileList.add(file.getName());}  
        }
        createLocalList(localFileList.stream().toArray(String[]::new));  
    }
    
    
    // This composes the list of files in a remote directory on the console
    public void composeRemoteFileList(String localDirectory){
        
        String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
        List<String> remoteFileList = new ArrayList<>();

        if (localDirectory.equals(LOCAL_DIRECTORY_ART)){
            
            // This prevents PS2 images from being displayed in PS1 mode and vice-versa
            if (myFTP.isFTPConnected()) {
            
                if (PopsGameManager.getCurrentConsole().equals("PS1")){

                    List<String> tempRemoteFileList = myFTP.listRemoteDirectory(REMOTE_DIRECTORY_ART, selectedPartition, true);
                    List<String> ps1RemoteFileList = new ArrayList<>();
                    
                    // If any of the file names start with a region code, the file is removed from the list because it must be for a PS2 game
                    tempRemoteFileList.forEach((remoteFileName) -> {
                        boolean containsRegionCode = false;
                        for (String regionCode : REGION_CODES){if (remoteFileName.substring(0, 5).equals(regionCode)) {containsRegionCode = true;}}
                        if (!containsRegionCode) {ps1RemoteFileList.add(remoteFileName);}
                    });
                    
                    for (String remoteFile : ps1RemoteFileList){remoteFileList.add(remoteFile);} 
                }
                else if (PopsGameManager.getCurrentConsole().equals("PS2")){

                    List<String> tempRemoteFileList = myFTP.listRemoteDirectory(REMOTE_DIRECTORY_ART, selectedPartition, true);
                    List<String> ps2RemoteFileList = new ArrayList<>();

                    // If any of the file names start with a region code, the file is added to the list because it must be for a PS2 game
                    tempRemoteFileList.forEach((remoteFileName) -> {
                        boolean containsRegionCode = false;
                        for (String regionCode : REGION_CODES){if (remoteFileName.substring(0, 5).equals(regionCode)) {containsRegionCode = true;}}
                        if (containsRegionCode) {ps2RemoteFileList.add(remoteFileName);}
                    });
                    
                    for (String remoteFile : ps2RemoteFileList){remoteFileList.add(remoteFile);} 
                }
            }
        }
        else if (localDirectory.equals(LOCAL_DIRECTORY_CONFIG)){if (myFTP.isFTPConnected()) remoteFileList = myFTP.listRemoteDirectory(REMOTE_DIRECTORY_CONFIG, selectedPartition, true);}
        else if (localDirectory.equals(LOCAL_DIRECTORY_CHEAT_PS2)){if (myFTP.isFTPConnected()) remoteFileList = myFTP.listRemoteDirectory(REMOTE_DIRECTORY_CHEAT, selectedPartition, true);}
        else if (localDirectory.equals(LOCAL_DIRECTORY_VMC_PS2)){if (myFTP.isFTPConnected()) remoteFileList = myFTP.listRemoteDirectory(REMOTE_DIRECTORY_VMC, selectedPartition, true);}

        createRemoteList(remoteFileList.stream().toArray(String[]::new));   
    }

    
    // Overide the close operation to ensure that the FTP server gets disconnected
    private void overideClose(){
        
        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                
                // Discount the FTP client from the server on the console
                if (myFTP != null) {myFTP.disconnectFromConsole();}
                
                PopsGameManager.callbackToUpdateGUIGameList(null, -1);
                
                // Close the form
                dispose();
            }
        });
        
        // Form title
        this.setTitle(" Transfer Files");
    }
    
    
    // Display the users IP address in the text field
    private void displayIPAddress(){
        
        if (PopsGameManager.getPS2IP() != null) {
            String[] splitIP = PopsGameManager.getPS2IP().split("\\.");
            splitIP[2] = String.format("%03d", Integer.parseInt(splitIP[2]));
            splitIP[3] = String.format("%03d", Integer.parseInt(splitIP[3]));
            jFormattedTextFieldIPAddress.setText(splitIP[0] + splitIP[1] + splitIP[2] + splitIP[3]);
        }
        
        //if (PopsGameManager.getCurrentConsole().equals("PS1")) {jTextFieldPartition.setText("_.POPS");}
        //else if (PopsGameManager.getCurrentConsole().equals("PS2")) {jTextFieldPartition.setText("+OPL");}
    }
    
    
    // This creates the list model for the local games list in the GUI
    private void createLocalList(String[] games){
        
        // List model
        jListLocalFileList.setModel(new javax.swing.AbstractListModel<String>() { 
            @Override
            public int getSize() {return games.length;}
            @Override
            public String getElementAt(int i) {return games[i];}
        });
        
        // Mouse listener
        jListLocalFileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {}
            @Override
            public void mouseReleased(MouseEvent evt){if (SwingUtilities.isLeftMouseButton(evt)){jListRemoteFileList.clearSelection();}}
        });
        
        // Key listener
        jListLocalFileList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    
    // This creates the list model for the remote games list in the GUI
    private void createRemoteList(String[] games){
        
        // List model
        jListRemoteFileList.setModel(new javax.swing.AbstractListModel<String>() { 
            @Override
            public int getSize() {return games.length;}
            @Override
            public String getElementAt(int i) {return games[i];}
        });
        
        // Mouse listener
        jListRemoteFileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {}
            @Override
            public void mouseReleased(MouseEvent evt){if (SwingUtilities.isLeftMouseButton(evt)){jListLocalFileList.clearSelection();}}
        });
        
        // Key listener
        jListRemoteFileList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    
    // This changes the radio button selection, local directory path and loads the new file list into the GUI
    private void fileFilterChanged(String fileType){
        
        if (myFTP != null && myFTP.isFTPConnected()){
            
            switch (fileType) {
                case "Art":
                    jRadioButtonART.setSelected(true);
                    jRadioButtonCFG.setSelected(false);
                    jRadioButtonCHT.setSelected(false);
                    jRadioButtonVMC.setSelected(false);
                    composeLocalFileList(LOCAL_DIRECTORY_ART);
                    composeRemoteFileList(LOCAL_DIRECTORY_ART);
                    break;
                case "Config":
                    jRadioButtonART.setSelected(false);
                    jRadioButtonCFG.setSelected(true);
                    jRadioButtonCHT.setSelected(false);
                    jRadioButtonVMC.setSelected(false);
                    composeLocalFileList(LOCAL_DIRECTORY_CONFIG);
                    composeRemoteFileList(LOCAL_DIRECTORY_CONFIG);
                    break;
                case "Cheat":
                    jRadioButtonART.setSelected(false);
                    jRadioButtonCFG.setSelected(false);
                    jRadioButtonCHT.setSelected(true);
                    jRadioButtonVMC.setSelected(false);
                    
                    if (PopsGameManager.getCurrentConsole().equals("PS1")){
                        composeLocalCheatListPS1();
                        composeRemoteCheatListPS1();
                    }
                    else {
                        composeLocalFileList(LOCAL_DIRECTORY_CHEAT_PS2);
                        composeRemoteFileList(LOCAL_DIRECTORY_CHEAT_PS2);
                    }
                    break;
                case "VMC":
                    jRadioButtonART.setSelected(false);
                    jRadioButtonCFG.setSelected(false);
                    jRadioButtonCHT.setSelected(false);
                    jRadioButtonVMC.setSelected(true);
                    composeLocalFileList(LOCAL_DIRECTORY_VMC_PS2);
                    composeRemoteFileList(LOCAL_DIRECTORY_VMC_PS2);
                    break;
                default:
                    break;
            } 
        }
        else {
            jRadioButtonART.setSelected(false);
            jRadioButtonCFG.setSelected(false);
            jRadioButtonCHT.setSelected(false);
            jRadioButtonVMC.setSelected(false);
        }  
    }
    
    
    // Creates the list of local PS1 cheat files
    private void composeLocalCheatListPS1(){
        
        List<String> localFileList = new ArrayList<>();
        currentLocalDirectory = LOCAL_DIRECTORY_PS1;
        if (new File(PopsGameManager.getOPLFolder() + "POPS").exists() && new File(PopsGameManager.getOPLFolder() + "POPS").isDirectory()){
            
            // Read all of the directories in the POPS folder
            File[] directories = new File(PopsGameManager.getOPLFolder() + "POPS").listFiles(File::isDirectory);

            // If a directory contains a CHEATS.TXT file, add the directory name to the list
            for (File directory : directories){if (new File(directory + File.separator + "CHEATS.TXT").exists() && new File(directory + File.separator + "CHEATS.TXT").isFile()) {localFileList.add(directory.getName());}}  
        }
        
        createLocalList(localFileList.stream().toArray(String[]::new)); 
    }
    
    
    // Creates the list of remote PS1 cheat files
    private void composeRemoteCheatListPS1(){

        currentLocalDirectory = LOCAL_DIRECTORY_PS1;
        
        myFTP.disconnectFromConsole();
        myFTP.connectToConsole(PopsGameManager.getPS2IP());
       
        String remoteCheatFolder = "";
        if (GameListManager.getFormattedVCDDrive().equals("hdd")) {remoteCheatFolder = "/pfs/" + GameListManager.getFormattedVCDPartition() + "/POPS/";}
        else if (GameListManager.getFormattedVCDDrive().equals("mass")) {remoteCheatFolder = "/mass/" + GameListManager.getFormattedVCDPartition() + "/POPS/";}
        
        //myFTP.changeDirectory("hdd/0/__common");
        myFTP.changeDirectory(GameListManager.getFormattedVCDDrive() + "/" + GameListManager.getFormattedVCDPartition() + "/__common");
        
        //List<String> remoteDirectoryList = myFTP.listRemoteDirectory("/pfs/0/POPS/", "__common", false);
        List<String> remoteDirectoryList = myFTP.listRemoteDirectory(remoteCheatFolder, "__common", false);
        
        List<String> remoteCheatList = new ArrayList<>();
        
        if (remoteDirectoryList != null){
            for (String directory : remoteDirectoryList){
                //List<String> remoteInnerDirectoryList = myFTP.listRemoteDirectory("/pfs/0/POPS/" + directory + "/" , "__common", true);
                List<String> remoteInnerDirectoryList = myFTP.listRemoteDirectory(remoteCheatFolder + directory + "/" , "__common", true);
                if (remoteInnerDirectoryList.contains("CHEATS.TXT")) {remoteCheatList.add(directory);}
            }
        } 
        
        createRemoteList(remoteCheatList.stream().toArray(String[]::new));
    }
    
    
    // This deletes a local or remote file
    private void deleteFile(){
        
        // Try and delete the selected file
        // If an item in the remote list is selected, this deletes the remote file, else if the local list is selected, it deletes the local file
        if (jListRemoteFileList.getSelectedIndex() != -1 && jListLocalFileList.getSelectedIndex() == -1){
            if (currentLocalDirectory.equals(LOCAL_DIRECTORY_ART)){if (myFTP.isFTPConnected()) myFTP.deleteRemoteFile(REMOTE_DIRECTORY_ART + jListRemoteFileList.getSelectedValue());}
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_CONFIG)){if (myFTP.isFTPConnected()) myFTP.deleteRemoteFile(REMOTE_DIRECTORY_CONFIG + jListRemoteFileList.getSelectedValue());}
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_CHEAT_PS2)){if (myFTP.isFTPConnected()) myFTP.deleteRemoteFile(REMOTE_DIRECTORY_CHEAT + jListRemoteFileList.getSelectedValue());} 
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_VMC_PS2)){if (myFTP.isFTPConnected()) myFTP.deleteRemoteFile(REMOTE_DIRECTORY_VMC + jListRemoteFileList.getSelectedValue());} 

            if (currentLocalDirectory.equals(LOCAL_DIRECTORY_PS1)){
                if (myFTP.isFTPConnected()) {
                    
                    String remoteCheatFolder = "";
                    if (GameListManager.getFormattedVCDDrive().equals("hdd")) {remoteCheatFolder = "/pfs/" + GameListManager.getFormattedVCDPartition() + "/POPS/";}
                    else if (GameListManager.getFormattedVCDDrive().equals("mass")) {remoteCheatFolder = "/mass/" + GameListManager.getFormattedVCDPartition() + "/POPS/";}
                    
                    myFTP.deleteRemoteFile(remoteCheatFolder + jListRemoteFileList.getSelectedValue() + "/CHEATS.TXT");
                    composeRemoteCheatListPS1();
                }
            }
            else {composeRemoteFileList(currentLocalDirectory);}  
        }
        else if (jListLocalFileList.getSelectedIndex() != -1 && jListRemoteFileList.getSelectedIndex() == -1){
            
            // Try to delete the local file from the local hdd directory
            if (!currentLocalDirectory.equals(LOCAL_DIRECTORY_PS1)){
                try{new File(currentLocalDirectory + jListLocalFileList.getSelectedValue()).delete();} catch(Exception ex){PopsGameManager.displayErrorMessageDebug(ex.toString());}  
                composeLocalFileList(currentLocalDirectory);
            }
            else {
                try{new File(currentLocalDirectory + jListLocalFileList.getSelectedValue() + File.separator + "CHEATS.TXT").delete();} catch(Exception ex){PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                composeLocalCheatListPS1();
            }
        } 
    }
    
    
    // This copies a file from the local hdd directories to the remote directory on the console
    private void copyFileToConsole(){
        // Try and add the selected local file to the remote server
        if (jListLocalFileList.getSelectedIndex() != -1 && jListRemoteFileList.getSelectedIndex() == -1){
            if (currentLocalDirectory.equals(LOCAL_DIRECTORY_ART)){if (myFTP.isFTPConnected()) myFTP.addFileToPS2(LOCAL_DIRECTORY_ART, jListLocalFileList.getSelectedValue(), REMOTE_DIRECTORY_ART, PopsGameManager.getCurrentConsole().equals("PS1"));}
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_CONFIG)){if (myFTP.isFTPConnected()) myFTP.addFileToPS2(LOCAL_DIRECTORY_CONFIG, jListLocalFileList.getSelectedValue(), REMOTE_DIRECTORY_CONFIG, PopsGameManager.getCurrentConsole().equals("PS1"));}
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_CHEAT_PS2)){if (myFTP.isFTPConnected()) myFTP.addFileToPS2(LOCAL_DIRECTORY_CHEAT_PS2, jListLocalFileList.getSelectedValue(), REMOTE_DIRECTORY_CHEAT, PopsGameManager.getCurrentConsole().equals("PS1"));} 
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_VMC_PS2)){if (myFTP.isFTPConnected()) myFTP.addFileToPS2(LOCAL_DIRECTORY_VMC_PS2, jListLocalFileList.getSelectedValue(), REMOTE_DIRECTORY_VMC, PopsGameManager.getCurrentConsole().equals("PS1"));} 

            String remoteCheatFolder = "";
            if (GameListManager.getFormattedVCDDrive().equals("hdd")) {remoteCheatFolder = "/pfs/" + GameListManager.getFormattedVCDPartition() + "/POPS/";}
            else if (GameListManager.getFormattedVCDDrive().equals("mass")) {remoteCheatFolder = "/mass/" + GameListManager.getFormattedVCDPartition() + "/POPS/";}
            
            // Upload PS1 cheat files to the console
            if (currentLocalDirectory.equals(LOCAL_DIRECTORY_PS1)){
                if (myFTP.remoteFileExists(GameListManager.getFormattedOPLDrive(), remoteCheatFolder + jListLocalFileList.getSelectedValue() + "/", "__common", "CHEATS.TXT", true)){
                    // If a cheat file already exists on the console for the specific game, this deletes the cheat file before uploading the new one
                    myFTP.deleteRemoteFile(remoteCheatFolder + jListLocalFileList.getSelectedValue() + "/CHEATS.TXT");
                    
                    // Upload the cheat file to the specific directory on the console
                    myFTP.addFileToPS2(LOCAL_DIRECTORY_PS1 + jListLocalFileList.getSelectedValue() + File.separator, "CHEATS.TXT", remoteCheatFolder + jListLocalFileList.getSelectedValue() + "/", false);
                }
                else {
                    // Create the remote folder to store the cheat file if it does not already exist
                    if (!myFTP.remoteFileExists(GameListManager.getFormattedOPLDrive(), remoteCheatFolder, "__common", jListLocalFileList.getSelectedValue(), false)){myFTP.createDirectory(remoteCheatFolder + jListLocalFileList.getSelectedValue());}
                    
                    // Upload the cheat file to the specific directory on the console
                    myFTP.addFileToPS2(LOCAL_DIRECTORY_PS1 + jListLocalFileList.getSelectedValue() + File.separator, "CHEATS.TXT", remoteCheatFolder + jListLocalFileList.getSelectedValue() + "/", false); 
                }

                composeRemoteCheatListPS1();
            }
            else {composeRemoteFileList(currentLocalDirectory);}
        }
    }
    

    // This copies a file from the remote server to the local  hdd directory
    private void copyFileFromConsole(){
        
        // If an item in the remote list is selected, this copies the remote file to local directory
        if (jListRemoteFileList.getSelectedIndex() != -1 && jListLocalFileList.getSelectedIndex() == -1){
            
            if (currentLocalDirectory.equals(LOCAL_DIRECTORY_ART)){
                
                // Copy the ART file from the console if a file with the same name is not already in the local ART directory
                List<String> directoryFileList = listOfFilesInDirectory(LOCAL_DIRECTORY_ART);
                if (!directoryFileList.contains(PopsGameManager.getFilePrefix() + jListRemoteFileList.getSelectedValue())){
                    myFTP.getFile(REMOTE_DIRECTORY_ART,jListRemoteFileList.getSelectedValue(), currentLocalDirectory);
                    if (new File(currentLocalDirectory + jListRemoteFileList.getSelectedValue()).exists()){
                        new File(currentLocalDirectory + jListRemoteFileList.getSelectedValue()).renameTo(new File(currentLocalDirectory + PopsGameManager.getFilePrefix() + jListRemoteFileList.getSelectedValue()));
                    } 
                }
                else {JOptionPane.showMessageDialog(null,"This file is already in your local ART directory!"," File Copy Error!",JOptionPane.ERROR_MESSAGE);}
            }
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_CONFIG)){
                
                // Copy the CFG file from the console if a file with the same name is not already in the local ART directory
                List<String> directoryFileList = listOfFilesInDirectory(LOCAL_DIRECTORY_CONFIG);
                if (!directoryFileList.contains(PopsGameManager.getFilePrefix() + jListRemoteFileList.getSelectedValue())){
                    myFTP.getFile(REMOTE_DIRECTORY_CONFIG, jListRemoteFileList.getSelectedValue(), currentLocalDirectory);
                    if (new File(currentLocalDirectory + jListRemoteFileList.getSelectedValue()).exists()){
                        new File(currentLocalDirectory + jListRemoteFileList.getSelectedValue()).renameTo(new File(currentLocalDirectory + PopsGameManager.getFilePrefix() + jListRemoteFileList.getSelectedValue()));
                    } 
                }
                else {JOptionPane.showMessageDialog(null,"This file is already in your local CFG directory!"," File Copy Error!",JOptionPane.ERROR_MESSAGE);} 
            }
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_CHEAT_PS2)) {
                
                // Copy the CHT file from the console if a file with the same name is not already in the local ART directory
                List<String> directoryFileList = listOfFilesInDirectory(LOCAL_DIRECTORY_CHEAT_PS2);
                if (!directoryFileList.contains(jListRemoteFileList.getSelectedValue())){
                    myFTP.getFile(REMOTE_DIRECTORY_CHEAT, jListRemoteFileList.getSelectedValue(), currentLocalDirectory);
                }
                else {JOptionPane.showMessageDialog(null,"This file is already in your local CHT directory!"," File Copy Error!",JOptionPane.ERROR_MESSAGE);} 
            }
            else if (currentLocalDirectory.equals(LOCAL_DIRECTORY_VMC_PS2)) {
                
                // Copy the VMC file from the console if a file with the same name is not already in the local ART directory
                List<String> directoryFileList = listOfFilesInDirectory(LOCAL_DIRECTORY_VMC_PS2);
                if (!directoryFileList.contains(jListRemoteFileList.getSelectedValue())){
                    myFTP.getFile(REMOTE_DIRECTORY_VMC, jListRemoteFileList.getSelectedValue(), currentLocalDirectory);
                }
                else {JOptionPane.showMessageDialog(null,"This file is already in your local VMC directory!"," File Copy Error!",JOptionPane.ERROR_MESSAGE);} 
            }
        
            // Download PS1 cheat files from the console
            if (currentLocalDirectory.equals(LOCAL_DIRECTORY_PS1)){
                
                // Create the local PS1 cheat folder for the specific game if it does not already exists
                File localCheatFolder = new File(LOCAL_DIRECTORY_PS1 + File.separator + jListRemoteFileList.getSelectedValue());
                if (!localCheatFolder.exists() || !localCheatFolder.isDirectory()){localCheatFolder.mkdir();}
                
                myFTP.getFile("/pfs/0/POPS/" + jListRemoteFileList.getSelectedValue() + "/"  ,"CHEATS.TXT", localCheatFolder.getAbsolutePath() + File.separator);
                composeLocalCheatListPS1();
            }
            else {composeLocalFileList(currentLocalDirectory);}
        }
    }
    
    
    // This returns a list of all files in the selected directory
    private List<String> listOfFilesInDirectory(String directoryPath){
        List<String> directoryFileList = new ArrayList<>();
        File[] files = new File(directoryPath).listFiles();
        for (File file : files) {if (file.isFile()) {directoryFileList.add(file.getName());}}
        
        return directoryFileList;
    }
    
    
    // This attempts to create the FTP client and connects to the FTP server running on the console
    private void createFTPClient(){
        
        if (!selectedPartition.equals("") && !jFormattedTextFieldIPAddress.getText().equals("   .   .   .   ")){

            int dialogResult = JOptionPane.showConfirmDialog (null, "Ensure that the FTP server is currently running on your console, then click \"OK\"."," File Sync Requires FTP Connection",JOptionPane.OK_CANCEL_OPTION);
            if(dialogResult == JOptionPane.OK_OPTION){

                // Get the console IP address from the formatted text field
                String ipAddress = jFormattedTextFieldIPAddress.getText();

                // Create the FTP client and attempt to connect to the FTP server that should be running on the console
                myFTP = new MyFTPClient();

                if (myFTP.connectToConsole(ipAddress)) {
                    jRadioButtonART.setSelected(true);
                    
                    // Then display the contents in the list boxes in the GUI
                    composeLocalFileList(LOCAL_DIRECTORY_ART);
                    composeRemoteFileList(LOCAL_DIRECTORY_ART);
                }
            }
        }
    }
    
    
    // Sync all of the files between the console and the PC
    private void syncFiles(){
        
        /*
        if (myFTP != null) {
            myFTP.syncFilesToConsole(LOCAL_DIRECTORY_ART, REMOTE_DIRECTORY_ART);
            myFTP.syncFilesFromConsole(REMOTE_DIRECTORY_ART, LOCAL_DIRECTORY_ART);
            myFTP.listRemoteDirectory(REMOTE_DIRECTORY_ART, jTextFieldPartition.getText());
        }
        */
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jProgressBarTransferProgress = new javax.swing.JProgressBar();
        jButtonSyncFiles = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextFieldIPAddress = new javax.swing.JFormattedTextField();
        jButtonConnectToConsole = new javax.swing.JButton();
        jComboBoxPartition = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonCFG = new javax.swing.JRadioButton();
        jRadioButtonART = new javax.swing.JRadioButton();
        jRadioButtonCHT = new javax.swing.JRadioButton();
        jRadioButtonVMC = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListLocalFileList = new javax.swing.JList<>();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListRemoteFileList = new javax.swing.JList<>();
        jButtonTransferFromConsole = new javax.swing.JButton();
        jButtonTransferToConsole = new javax.swing.JButton();
        jButtonDeleteFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer Files"));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer Status"));

        jProgressBarTransferProgress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonSyncFiles.setText("Sync All");
        jButtonSyncFiles.setEnabled(false);
        jButtonSyncFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSyncFilesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBarTransferProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSyncFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBarTransferProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSyncFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Console Settings"));

        jLabel1.setText(" Partition:");

        jLabel2.setText(" IP:");

        try {
            jFormattedTextFieldIPAddress.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###.###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextFieldIPAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButtonConnectToConsole.setText("Connect");
        jButtonConnectToConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectToConsoleActionPerformed(evt);
            }
        });

        jComboBoxPartition.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "hdd0:/+OPL/", "mass:/+OPL/", "mass1:/+OPL/", "mass2:/+OPL/" }));
        jComboBoxPartition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPartitionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextFieldIPAddress)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonConnectToConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jComboBoxPartition, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxPartition, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFormattedTextFieldIPAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonConnectToConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Files"));

        jRadioButtonCFG.setText("CFG");
        jRadioButtonCFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCFGActionPerformed(evt);
            }
        });

        jRadioButtonART.setText("ART");
        jRadioButtonART.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonARTActionPerformed(evt);
            }
        });

        jRadioButtonCHT.setText("CHT");
        jRadioButtonCHT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCHTActionPerformed(evt);
            }
        });

        jRadioButtonVMC.setText("VMC");
        jRadioButtonVMC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonVMCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonART, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonVMC, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButtonCHT, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                    .addComponent(jRadioButtonCFG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonART)
                    .addComponent(jRadioButtonCFG))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonCHT)
                    .addComponent(jRadioButtonVMC))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Files on PC"));

        jListLocalFileList.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(jListLocalFileList);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Files on PS2"));

        jListRemoteFileList.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jScrollPane2.setViewportView(jListRemoteFileList);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonTransferFromConsole.setText("<<");
        jButtonTransferFromConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTransferFromConsoleActionPerformed(evt);
            }
        });

        jButtonTransferToConsole.setText(">>");
        jButtonTransferToConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTransferToConsoleActionPerformed(evt);
            }
        });

        jButtonDeleteFile.setText("Delete");
        jButtonDeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonTransferFromConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonTransferToConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonDeleteFile, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonTransferFromConsole)
                            .addComponent(jButtonTransferToConsole))
                        .addGap(18, 18, 18)
                        .addComponent(jButtonDeleteFile))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button click events"> 
    private void jRadioButtonARTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonARTActionPerformed
        fileFilterChanged("Art");
    }//GEN-LAST:event_jRadioButtonARTActionPerformed

    private void jRadioButtonCFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCFGActionPerformed
        fileFilterChanged("Config");
    }//GEN-LAST:event_jRadioButtonCFGActionPerformed

    private void jRadioButtonCHTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCHTActionPerformed
        
        fileFilterChanged("Cheat");
    }//GEN-LAST:event_jRadioButtonCHTActionPerformed

    private void jRadioButtonVMCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVMCActionPerformed

        if (!PopsGameManager.getCurrentConsole().equals("PS1")){fileFilterChanged("VMC");}
        else {
            jRadioButtonVMC.setSelected(false);
            if (myFTP != null && myFTP.isFTPConnected()) {JOptionPane.showMessageDialog(null,"This application cannot yet upload PS1 Virtual Memory Card files."," Unable to Upload PS1 VMC File!",JOptionPane.ERROR_MESSAGE);}
        }
    }//GEN-LAST:event_jRadioButtonVMCActionPerformed
	
    private void jButtonSyncFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSyncFilesActionPerformed
        syncFiles();
    }//GEN-LAST:event_jButtonSyncFilesActionPerformed

    private void jButtonConnectToConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectToConsoleActionPerformed
        createFTPClient();
    }//GEN-LAST:event_jButtonConnectToConsoleActionPerformed

    private void jButtonDeleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteFileActionPerformed
        deleteFile();
    }//GEN-LAST:event_jButtonDeleteFileActionPerformed

    private void jButtonTransferToConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTransferToConsoleActionPerformed
        copyFileToConsole();
    }//GEN-LAST:event_jButtonTransferToConsoleActionPerformed

    private void jButtonTransferFromConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTransferFromConsoleActionPerformed
        copyFileFromConsole();
    }//GEN-LAST:event_jButtonTransferFromConsoleActionPerformed

    private void jComboBoxPartitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPartitionActionPerformed
        PopsGameManager.setRemoteOPLPath(jComboBoxPartition.getSelectedItem().toString());
        try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {}
    }//GEN-LAST:event_jComboBoxPartitionActionPerformed
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc="Generated variables">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonConnectToConsole;
    private javax.swing.JButton jButtonDeleteFile;
    private javax.swing.JButton jButtonSyncFiles;
    private javax.swing.JButton jButtonTransferFromConsole;
    private javax.swing.JButton jButtonTransferToConsole;
    private javax.swing.JComboBox<String> jComboBoxPartition;
    private javax.swing.JFormattedTextField jFormattedTextFieldIPAddress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jListLocalFileList;
    private javax.swing.JList<String> jListRemoteFileList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBarTransferProgress;
    private javax.swing.JRadioButton jRadioButtonART;
    private javax.swing.JRadioButton jRadioButtonCFG;
    private javax.swing.JRadioButton jRadioButtonCHT;
    private javax.swing.JRadioButton jRadioButtonVMC;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}// </editor-fold>