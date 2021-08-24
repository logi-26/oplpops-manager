package oplpops.game.manager;

import static java.lang.Math.toIntExact;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class MyFTPClient {

    private static FTPClient ftpClient;

    // Booleans to determine if the OPL directories are present on the console
    private static final boolean folderExistsPOPS = false;
    private String consoleIP = null;
    
    
    public MyFTPClient() {ftpClient = new FTPClient();}
    
    
    // Return the FTP connection status
    public boolean isFTPConnected(){return ftpClient.isConnected();}
    
    
    public void createDirectory(String directoryPath){
        try {ftpClient.makeDirectory(directoryPath);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }

    
    // Upload the conf_elm.cfg file to the console via FTP
    public void uploadConfElmToConsole(String localFileDirectory, String localFileName, String remoteDirectory){
        
        InputStream inputStream = null;
        File localConfElmFile = new File(localFileDirectory);

        if (localConfElmFile.exists()){
            
            try {inputStream = new FileInputStream(localConfElmFile);} catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            try {ftpClient.setFileType(FTP.BINARY_FILE_TYPE);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            try {
                try (OutputStream outputStream = ftpClient.storeFileStream(remoteDirectory + localFileName)) {
                    byte[] bytesIn = new byte[4096];
                    int read = 0;
                    while (-1 != (read = inputStream.read(bytesIn))) {outputStream.write(bytesIn, 0, read);}
                    inputStream.close();
                }

                ftpClient.completePendingCommand();
            }
            catch (IOException ex) {
                disconnectFromConsole();
                PopsGameManager.displayErrorMessageDebug(ex.toString());
            }

            try {if (inputStream != null) {inputStream.close();}} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        } 
    }
    
    
    // This adds a PS1 game to the console using FTP in a background thread
    public void addGameToPS2(AddGameHDDScreenPS1 addGameScreen, List<File> fileList){
        new BackgroundWorker(addGameScreen, fileList).execute();
    }
    
    
    // This attempts to upload a file to PS2 console
    public void addFileToPS2(String localFileDirectory, String localFileName, String remoteDirectory, boolean removePrefix){

        // Upload file using an InputStream
        File firstLocalFile = new File(localFileDirectory + localFileName);
        
        // If it is a PS1 game, remove the SB. prefix
        String remoteFileName = localFileName;
        //if (removePrefix) {remoteFileName = remoteFileName.substring(3, remoteFileName.length());}
        
        String firstRemoteFile;
        firstRemoteFile = remoteDirectory + remoteFileName;

        InputStream inputStream = null;
        try {inputStream = new FileInputStream(firstLocalFile);} catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        try {ftpClient.setFileType(FTP.BINARY_FILE_TYPE);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        try {
            try (OutputStream outputStream = ftpClient.storeFileStream(firstRemoteFile)) {
                if (inputStream!= null){
                    if (outputStream != null){
                        byte[] bytesIn = new byte[4096];
                        int read = 0;
                        while (-1 != (read = inputStream.read(bytesIn))) {outputStream.write(bytesIn, 0, read);}
                        inputStream.close();
                    }  
                }     
            }

            ftpClient.completePendingCommand();
        }
        catch (IOException ex) {
            disconnectFromConsole();
            PopsGameManager.displayErrorMessageDebug(ex.toString());
        }

        try {if (inputStream != null) {inputStream.close();}} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // Rename a file on the console using FTP
    public void renameFile(String originalName, String newName){
        try {ftpClient.rename(originalName, newName);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}  
    }
    
    
    // This gets a file from the console
    public void getFile(String remoteDirectory, String fileName, String localDirectory){
        getFileFromConsole(ftpClient, remoteDirectory, fileName, localDirectory);
    }
    
    
    // This downloads a file from the PS2 console to a specified directory
    private void getFileFromConsole(FTPClient ftpClient, String remotePath, String remoteFileName, String destinationPath){
        try {
            String remoteFile = remotePath + remoteFileName;
            File downloadFile = new File(destinationPath + remoteFileName);
            try (OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile))) {ftpClient.retrieveFile(remoteFile, outputStream1);}
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    

    // This lists all of the files in a remote directory on the PS2 console
    public List<String> listRemoteDirectory(String remoteDirectory, String partition, boolean listFiles){
        
        List<String> remoteDirectoryList = new ArrayList<>();
        
        try {
            // First change the working directory to the correct partition. example - ("mass/0/" + partition") or ("hdd/0/" + partition)
            changeDirectory(GameListManager.getFormattedOPLDrive() + "/" + GameListManager.getFormattedOPLPartition() + "/" + partition);

            // Then set the current working directory (ART, CFG, CHT)
            changeDirectory(remoteDirectory);
 
            FTPFile[] files;
            if (listFiles) {files = ftpClient.listFiles(remoteDirectory);} else {files = ftpClient.listDirectories(remoteDirectory);}
            if (files.length > 0){for (FTPFile file : files) {if (listFiles && file.isFile() && !file.isDirectory()) {remoteDirectoryList.add(file.getName());} else if (!listFiles && !file.isFile() && file.isDirectory() &&!file.getName().equals(".") && !file.getName().equals("..")) {remoteDirectoryList.add(file.getName());}}}
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        
        return remoteDirectoryList;
    }
    
    
    // This checks if a file or folder exists in a remote directory
    public boolean remoteFileExists(String drive, String remoteDirectory, String partition, String remoteFileName, boolean isFile){
        
        boolean fileOnConsole = false;

        try {
            // First change the working directory to the correct partition. example - ("mass/0/" + partition") or ("hdd/0/" + partition)
            changeDirectory(drive + "/" + GameListManager.getFormattedOPLPartition() + "/" + partition);

            // Then set the current working directory (ART, CFG, CHT, VMC, APPS)
            changeDirectory(remoteDirectory);
 
            // Get the list of files from the remote directory and check if any of the file names match what we are looking for
            FTPFile[] files = ftpClient.listFiles(remoteDirectory);
            if (files.length > 0){
                for (FTPFile file : files) {
                    if (isFile) {if (file.getName().equals(remoteFileName) && file.isFile() && !file.isDirectory()) {fileOnConsole = true;}}
                    else {if (file.getName().equals(remoteFileName) && !file.isFile() && file.isDirectory()) {fileOnConsole = true;}}       
                }
            }
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        
        return fileOnConsole;
    }

    // This syncs all of the game art from the PS2 console to the local hdd directory on the PC
    public void syncFilesFromConsole(String remoteDirectory, String localDirectory){
        
        List<String> remoteDirectoryList = new ArrayList<>();
        List<String> localDirectoryList = new ArrayList<>();
        
        // Lists all of the remoteArtFiles in the ART directory on the console
        FTPFile[] remoteArtFiles = null;
        try {remoteArtFiles = ftpClient.listFiles(remoteDirectory);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            
        // Add the file names to a list
        if (remoteArtFiles != null) {for (FTPFile file : remoteArtFiles) {if (file.isFile() && !file.isDirectory()) {remoteDirectoryList.add(file.getName());}}}

        // Get the list of files in the local hdd folder on the PC
        File folder = new File(localDirectory);
        File[] localArtFiles = folder.listFiles();
        
        // Add the file names to a list
        if (localArtFiles != null) {for (File file : localArtFiles) {if (file.isFile() && !file.isDirectory()) {localDirectoryList.add(file.getName());}}}
         
        // If there is a file on the console that is not also in the local directory, this downloads the file from the console
        if (!remoteDirectoryList.isEmpty()) {remoteDirectoryList.stream().filter((fileName) -> (!localDirectoryList.contains(fileName))).forEach((fileName) -> {getFileFromConsole(ftpClient, remoteDirectory, fileName, localDirectory);});}
    }
    

    // This syncs all of the game art from the local hdd directory to the PS2 console
    public void syncFilesToConsole(String localDirectory, String remoteDirectory){
        
        List<String> remoteDirectoryList = new ArrayList<>();
        List<String> localDirectoryList = new ArrayList<>();
        
        // Lists all of the remoteArtFiles in the ART directory on the console
        FTPFile[] remoteArtFiles = null;
        try {remoteArtFiles = ftpClient.listFiles(remoteDirectory);} 
        catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            
        // Add the file names to a list
        if (remoteArtFiles != null) {for (FTPFile file : remoteArtFiles) {if (file.isFile() && !file.isDirectory()) {remoteDirectoryList.add(file.getName());}}}

        // Get the list of files in the local hdd folder on the PC
        File folder = new File(localDirectory);
        File[] localArtFiles = folder.listFiles();
        
        // Add the file names to a list
        if (localArtFiles != null) {for (File file : localArtFiles) {if (file.isFile() && !file.isDirectory()) {localDirectoryList.add(file.getName());}}}
         
        // If there is a file in the local directory that is not on the console, this downloads the file from the console
        if (!localDirectoryList.isEmpty()) {localDirectoryList.stream().filter((fileName) -> (!remoteDirectoryList.contains(fileName))).forEach((fileName) -> {addFileToPS2(localDirectory, fileName, remoteDirectory, PopsGameManager.getCurrentConsole().equals("PS1"));});}
    }
    
    
    // This attempts to establish an FTP connection with the PS2 console
    public boolean connectToConsole(String consoleIP){

        boolean connectionEstablished = false;
        this.consoleIP = consoleIP;
        
        if (!ftpClient.isConnected()){
            
            try {
                ftpClient.connect(consoleIP, 21);

                if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {JOptionPane.showMessageDialog(null, "Unable to establish a connection with the FTP server!", " FTP Connection Error!", JOptionPane.ERROR_MESSAGE);}
                else {connectionEstablished = true;}

                if (!ftpClient.login("anonymous", "")) {JOptionPane.showMessageDialog(null, "Unable to login to FTP server!", " FTP Login Error!", JOptionPane.ERROR_MESSAGE);} 
            } 
            catch (IOException ex) {JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage() + ".", " FTP Connection Error!", JOptionPane.ERROR_MESSAGE);}  
        }
        
        return connectionEstablished;
    }
    
    
    // This disconnects from the consoles FTP server
    public void disconnectFromConsole(){
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error terminating the FTP connection with the console!\n\n" + ex.toString());}
    }
    
    
    // This deletes a file on the console using FTP
    public void deleteRemoteFile(String fullPath){
        try {ftpClient.deleteFile(fullPath);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error deleting remote FTP file!\n\n" + ex.toString());}
    }
    
    
    // Change the FTP remote directory
    public void changeDirectory(String newDirectory){
        
        try {
            ftpClient.changeWorkingDirectory(newDirectory);
            //System.out.println("Working directory changed to :  " + newDirectory);
        } 
        catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error changing the FTP working directory!\n\n" + ex.toString());}  
    }
    
    
    // Get the list of PS1 games from the console
    public List<Game> getGameListPS1(){
        
        List<Game> gameList = new ArrayList<>();
        String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
        boolean conatinsIdentifier = false;
        String remoteDriveVCD = GameListManager.getFormattedVCDDrive();
        String remotePartitionVCD = GameListManager.getFormattedVCDPartition(); 
        String remoteFolderVCD = GameListManager.getFormattedVCDFolder();
        String remotePath = null;
        
        if (remoteDriveVCD.equals("hdd")) {remotePath = "/pfs/" + remotePartitionVCD;} else if (remoteDriveVCD.equals("mass")) {remotePath = "/mass/" + remotePartitionVCD;}
        changeDirectory(remoteDriveVCD + "/" + remotePartitionVCD + "/" + remoteFolderVCD);

        FTPFile[] files;
        try {
            files = ftpClient.listFiles(remotePath);
            
            if (files.length > 0){
                for (FTPFile file : files) {
                    if (file.getName().length() >= 6){
                        if (file.getName().substring(file.getName().lastIndexOf("."), file.getName().length()).equals(".VCD")){
                            long rawFileSize = file.getSize();
                            String gameID = null;
   
                            // If name is long enough to hold a game unique ID and file extension
                            if (file.getName().length() >= 15){
                                
                                String possibleID = "";
                                if (file.getName().contains("-") && file.getName().contains("_")){possibleID = file.getName().substring(file.getName().lastIndexOf("-")+1, file.getName().lastIndexOf("."));}
                                // Ensure that the file name contains the unique identifier
                                for (String regionCode:REGION_CODES) {if (possibleID.contains(regionCode)){conatinsIdentifier = true;}}
                                if (conatinsIdentifier){
                                    String gameName = file.getName().substring(0, file.getName().lastIndexOf("-"));
                                    gameID = possibleID;

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

                                    Game selectedGame = new Game(gameName,gameID,"PATH HERE!!",PopsGameManager.bytesToHuman(rawFileSize),rawFileSize);
                                    selectedGame.setCompatibleHDD(compatabilityUSB);
                                    selectedGame.setCompatibleUSB(compatabilityHDD);
                                    selectedGame.setCompatibleSMB(compatabilitySMB);
        
                                    gameList.add(selectedGame);
                                }  
                            } 
                        }
                    }  
                }
            } 
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        return gameList;
    }
    

    
    // Background worker thread: this uploads the file to the console and updates the progress bar in the GUI
    public class BackgroundWorker extends SwingWorker<Object, File> {

        AddGameHDDScreenPS1 addGameScreen;
        JProgressBar progressBar;
        JLabel timeRemainingLabel;
        JLabel uploadSpeedLabel;
        JTextField textFieldGameName;
        JTextField textFieldGameCounter;
        String localFileDirectory;
        String localFileName;
        List<File> fileList;

        public BackgroundWorker(AddGameHDDScreenPS1 addGameScreen, List<File> fileList) {

            this.addGameScreen = addGameScreen;
            this.progressBar = addGameScreen.getProgressBar();
            this.timeRemainingLabel = addGameScreen.getTimeRemainingLabel();
            this.uploadSpeedLabel = addGameScreen.getUploadSpeedLabel();
            this.fileList = fileList;
        }

        @Override
        protected Object doInBackground() throws Exception {

            String remoteDriveVCD = GameListManager.getFormattedVCDDrive();
            String remotePartitionVCD = GameListManager.getFormattedVCDPartition(); 
            String remoteFolderVCD = GameListManager.getFormattedVCDFolder();
            String remoteDriveELF = GameListManager.getFormattedELFDrive();
            String remotePartitionELF = GameListManager.getFormattedELFPartition();
            String remoteFolderELF = GameListManager.getFormattedELFFolder();
            
            String[] splitElfFolder = remoteFolderELF.split("/");

            String firstRemotePath = "";
            if (remoteDriveVCD.equals("hdd")) {firstRemotePath = "/pfs/" + remotePartitionVCD + "/";}
            else if (remoteDriveVCD.equals("mass")) {firstRemotePath = "/mass/" + remotePartitionVCD + "/POPS/";}
            
            // Upload each game in the file list
            int count = 0;
            for (File currentFile : fileList){

                count++;
                localFileDirectory = currentFile.getParent() + File.separator;
                localFileName = currentFile.getName().substring(0, currentFile.getName().length()-4);
                
                // Display the current game name and game counter in the labels in the GUI
                addGameScreen.getGameNameLabel().setText(" " + currentFile.getName());  
                if (fileList.size() > 1){addGameScreen.getGameCounterLabel().setText(count + "/" + fileList.size());}
                
                // Upload file using an InputStream
                File firstLocalFile = new File(localFileDirectory + localFileName + ".VCD");
                String firstRemoteFile = firstRemotePath + localFileName + ".VCD";
                String secondRemotePath = "";
                String elfPath = "";
                for (int i = 1; i < splitElfFolder.length; i++){elfPath = elfPath + "/" + splitElfFolder[i];} 

                if (remoteDriveELF.equals("hdd")) {secondRemotePath = "/pfs/" + remotePartitionELF + elfPath + "/";}
                else if (remoteDriveELF.equals("mass")) {secondRemotePath = "/mass/" + remotePartitionELF + "/POPS/";}

                File secondLocalFile = new File(localFileDirectory + localFileName + ".ELF");
                String secondRemoteFile = secondRemotePath + localFileName + ".ELF";
                long firstFileSize = firstLocalFile.length();
                long secondFileSize = secondLocalFile.length();
                long totalBytesToUpload = firstFileSize + secondFileSize;
                int totalUploadedBytes = 0;
                long start = System.nanoTime();

                // Connect to the console
                if (consoleIP != null) {connectToConsole(consoleIP);} else if (PopsGameManager.getPS2IP() != null) {connectToConsole(PopsGameManager.getPS2IP());}

                // Change the remote directory and set the file type to binary
                changeDirectory(remoteDriveVCD + "/" + remotePartitionVCD + "/" + remoteFolderVCD);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                // Upload the VCD file
                uploadFileToConsole(firstLocalFile, firstRemoteFile, totalUploadedBytes, totalBytesToUpload, start, progressBar, timeRemainingLabel, uploadSpeedLabel);

                // *****************************************************************************************************************
                // The easiest way to change between the _.POPS and +OPL partitions was to quickly disconnect and re-connect
                disconnectFromConsole();
                // *****************************************************************************************************************
                
                // If the user also wants to upload the ELF file with the VCD file
                if (addGameScreen.includeElfFile()){

                    // Connect to the console
                    if (consoleIP != null) {connectToConsole(consoleIP);} else if (PopsGameManager.getPS2IP() != null) {connectToConsole(PopsGameManager.getPS2IP());}
                    
                    // Change the remote directory and set the file type to binary
                    changeDirectory(remoteDriveELF + "/" + remotePartitionELF + "/" + remoteFolderELF);
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    // Upload the ELF file
                    uploadFileToConsole(secondLocalFile, secondRemoteFile, totalUploadedBytes, totalBytesToUpload, start, progressBar, timeRemainingLabel, uploadSpeedLabel);
                }
                
                // Disconnect from console once the ELF file has been uploaded
                disconnectFromConsole();
            }
            
            return null;
        }

        @Override
        protected void done(){
            
            // Ensure that the console is disconnected and close the dialog window
            disconnectFromConsole();
            addGameScreen.setUploadInProgress(false);
            addGameScreen.closeDialog();

            // Delete the .ELF and .VCD files after they have been fully transfered to the console (in non-batch mode)
            if (fileList.size() == 1){
                if (new File(localFileDirectory + localFileName + ".ELF").exists() && new File(localFileDirectory + localFileName + ".ELF").isFile()) {new File(localFileDirectory + localFileName + ".ELF").delete();}
                if (new File(localFileDirectory + localFileName + ".VCD").exists() && new File(localFileDirectory + localFileName + ".VCD").isFile()) {new File(localFileDirectory + localFileName + ".VCD").delete();}
            }
            
            // Try and get the PS1 game list from the console, write the game list.dat file, callback to update the main gui
            List<Game> gameList = GameListManager.getGameListFromConsolePS1();
            if (gameList != null && gameList.size() >0){

                // Write the PS1 game list file
                GameListManager.writeGameListFilePS1(gameList);

                // Try and load the game data from the PS1 game list file
                try {GameListManager.createGameListFromFile("PS1", new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS1"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error reading the PS1 game list from file!\n\n" + ex.toString());}
            }

            if (gameList != null && gameList.size()>0){PopsGameManager.callbackToUpdateGUIGameList(null, gameList.size()-1);}
        }
    }
    

    // This upload a VCD or ELF file to the console
    private void uploadFileToConsole(File localFile, String remoteFile, int totalUploadedBytes, long totalBytesToUpload, long start, JProgressBar progressBar, JLabel timeRemainingLabel, JLabel uploadSpeedLabel){
        
        final double NANOS_PER_SECOND = 1000000000.0;
        final double BYTES_PER_MIB = 1024 * 1024;
        double downloadSpeed;
        InputStream inputStream;
        
        try {
            inputStream = new FileInputStream(localFile);
            try {
                try (OutputStream outputStream = ftpClient.storeFileStream(remoteFile)) {
                    byte[] bytesIn = new byte[4096];
                    int read = 0;
                    totalUploadedBytes = 0;
                    progressBar.setMinimum(0);
                    progressBar.setMaximum(toIntExact(totalBytesToUpload));

                    while (-1 != (read = inputStream.read(bytesIn))) {

                        outputStream.write(bytesIn, 0, read);
                        totalUploadedBytes += read;
                        progressBar.setValue(totalUploadedBytes);
                        progressBar.repaint();

                        downloadSpeed = NANOS_PER_SECOND / BYTES_PER_MIB * totalUploadedBytes / (System.nanoTime() - start + 1);
                        long remainingBytesToUpload = totalBytesToUpload - totalUploadedBytes;
                        remainingBytesToUpload = Math.round(remainingBytesToUpload/downloadSpeed);
                        String timeRemaining = String.valueOf(remainingBytesToUpload);

                        switch(timeRemaining.length()) {
                            case 10 :
                                timeRemaining = timeRemaining.substring(0,4);
                                 break;
                            case 9 :
                                timeRemaining = timeRemaining.substring(0,3);
                                break;
                            case 8 :
                                timeRemaining = timeRemaining.substring(0,2);
                                break;
                            case 7 :
                                timeRemaining = timeRemaining.substring(0,1);
                                break;
                        }

                        String formattedDownloadSpeed = Double.toString(downloadSpeed);
                        if (formattedDownloadSpeed.length()>5){formattedDownloadSpeed = formattedDownloadSpeed.substring(2, 5);}

                        if (timeRemaining.length() <= 4) {
                            timeRemainingLabel.setText(getDurationString(Integer.parseInt(timeRemaining), false));
                            uploadSpeedLabel.setText(formattedDownloadSpeed + "KB/sec");
                        }
                    }
                    inputStream.close();
                }
            }catch (IOException ex) {
                disconnectFromConsole();
                PopsGameManager.displayErrorMessageDebug(ex.toString());
            }
            
            try {inputStream.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // This converts seconds into hh:mm:ss
    private String getDurationString(int seconds, boolean usingHours) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (usingHours) {return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);}
        else {return twoDigitString(minutes) + ":" + twoDigitString(seconds);}
    }

    private String twoDigitString(int number) {
        if (number == 0) {return "00";}
        if (number / 10 == 0) {return "0" + number;}
        return String.valueOf(number);
    }
}