package oplpops.game.manager;

import java.awt.image.BufferedImage;
import java.io.*; 
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class MyTCPClient {
    
    //private static final String SERVER_ADDRESS = "oplpops-manager.myftp.org"; 
    //private static final int SERVER_PORT = 6789;
    
    //private static final String SERVER_ADDRESS = "oplpops-test.myftp.org"; 
    //private static final int SERVER_PORT = 9876;

    
    public MyTCPClient() {} 
  
    // This shares an image file with the server
    public void shareImageWithServer(String console, String gameRegion, String gameID, String coverType, String imagePath) throws IOException{

        Socket serverSocket = getServerSocket();
        
        if (serverSocket != null){      
            OutputStream outputStream = createOutputStream(serverSocket); 
            
            if (outputStream != null){
                String outMessage = "UPLOAD_ART," + console + "," + gameRegion + "," + coverType + "," + gameID + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);

                int messageLength = outMessage.length() + 3;
                String finalMessage = messageLength + "," + outMessage;
                byte[] bufferedMessage = finalMessage.getBytes();

                // Load the selected image into a buffer
                File selectedFile = new File(imagePath);
                if(selectedFile.exists() && !selectedFile.isDirectory()) {

                    // Read the image file into the imageBuffer
                    BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
                    ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", byteOutStream);
                    byteOutStream.flush();
                    byte[] imageBuffer = byteOutStream.toByteArray();

                    // Create a new buffer and place the message string and the buffered image into it
                    byte[] fullMessage = new byte[bufferedMessage.length + imageBuffer.length];
                    System.arraycopy(bufferedMessage, 0, fullMessage, 0, bufferedMessage.length);
                    System.arraycopy(imageBuffer, 0, fullMessage, bufferedMessage.length, imageBuffer.length);

                    try {
                        dataOutStream.writeInt(fullMessage.length);
                        if (fullMessage.length > 0) try {dataOutStream.write(fullMessage, 0, fullMessage.length);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                }
            }
        }
    }
    
    
    // This shares a config file with the server
    public void shareConfigWithServer(String console, String gameRegion, String gameID, String configPath) throws IOException{

        Socket serverSocket = getServerSocket();
        
        if (serverSocket != null){      
            OutputStream outputStream = createOutputStream(serverSocket); 
            
            if (outputStream != null){
                String outMessage = "UPLOAD_CFG," + console + "," + gameRegion + "," + gameID + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);

                int messageLength = outMessage.length() + 3;
                String finalMessage = messageLength + "," + outMessage;
                byte[] bufferedMessage = finalMessage.getBytes();

                // Load the selected config file into a buffer
                File selectedFile = new File(configPath);
                if(selectedFile.exists() && !selectedFile.isDirectory()) {
                    
                    // Load the config file into a buffer
                    byte[] configFileBuffer = Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
                    
                    // Create a new buffer and place the message string and the buffered config file into it
                    byte[] fullMessage = new byte[bufferedMessage.length + configFileBuffer.length];
                    System.arraycopy(bufferedMessage, 0, fullMessage, 0, bufferedMessage.length);
                    System.arraycopy(configFileBuffer, 0, fullMessage, bufferedMessage.length, configFileBuffer.length);

                    try {
                        dataOutStream.writeInt(fullMessage.length);
                        if (fullMessage.length > 0) try {dataOutStream.write(fullMessage, 0, fullMessage.length);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                }
            }
        }
    }
    
    
    // This shares a vmc file with the server
    public void shareVMCWithServer(String console, String gameRegion, String gameID, String vmcPath, String vmcDescription) throws IOException{
        
        Socket serverSocket = getServerSocket();
        
        if (serverSocket != null){      
            OutputStream outputStream = createOutputStream(serverSocket); 
            
            if (outputStream != null){
                String outMessage = "UPLOAD_VMC," + console + "," + gameRegion + "," + gameID + "," + vmcDescription + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);

                int messageLength = outMessage.length() + 3;
                String finalMessage = messageLength + "," + outMessage;
                byte[] bufferedMessage = finalMessage.getBytes();

                // Load the selected VMC file into a buffer
                File selectedFile = new File(vmcPath);
                if(selectedFile.exists() && !selectedFile.isDirectory()) {
                    
                    // Load the VMC file into a buffer
                    byte[] vmcFileBuffer = Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
                    
                    // Create a new buffer and place the message string and the buffered VMC file into it
                    byte[] fullMessage = new byte[bufferedMessage.length + vmcFileBuffer.length];
                    System.arraycopy(bufferedMessage, 0, fullMessage, 0, bufferedMessage.length);
                    System.arraycopy(vmcFileBuffer, 0, fullMessage, bufferedMessage.length, vmcFileBuffer.length);

                    try {
                        dataOutStream.writeInt(fullMessage.length);
                        if (fullMessage.length > 0) try {dataOutStream.write(fullMessage, 0, fullMessage.length);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                }
            }
        }
    }
    

    // This gets the latest update from the server
    public void getJarFileFromServer(String newVersionNumber){

        Socket serverSocket = getServerSocket();

        if (serverSocket != null){
            String outMessage = "UPDATE," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";  
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);
            
            if (outputStream != null && inputStream != null){
                String jarPath = PopsGameManager.getCurrentDirectory() + File.separator + "OPLPOPS-Manager_" + newVersionNumber + ".jar";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
   
                byte[] recievedData = null;
                int fileLength = 0;
                int byteCount = 0;

                try {
                    dataOutStream.writeInt(outMessage.length());
                    if (outMessage.length() > 0) try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    
                    fileLength = dataInputStream.readInt();
                    byteCount = dataInputStream.available();
                    recievedData = new byte[fileLength];
                    
                    if (fileLength > 0) {dataInputStream.readFully(recievedData);}
                    
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                if (recievedData != null && recievedData.length > 0) {try {Files.write(Paths.get(jarPath), recievedData);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                if (fileLength > 0) {checkJarFile(jarPath, fileLength);}
            } 
        }
    }
    
    
    // This checks if the JAR file has been downloaded and the size matches
    private void checkJarFile(String filePath, int fileLength){

        File jarFile = new File(filePath);

        if(jarFile.exists() && jarFile.length() == fileLength) {
            
            long jarFileSize = jarFile.length();
            int dialogResult = JOptionPane.showConfirmDialog (null, "The update was successfully downloaded!  (size = " + PopsGameManager.bytesToHuman(jarFileSize) + "). \n\nDo you want to launch the new version now?"," Update Downloaded",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
          
                int endIndex = filePath.lastIndexOf(File.separator);
                String jarName = null;
                String jarPath = null;

                if (endIndex != -1) {
                    jarPath = filePath.substring(0, endIndex +1);
                    jarName = filePath.substring(endIndex +1, filePath.length());
                }

                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath + jarName);
                pb.directory(new File(PopsGameManager.getCurrentDirectory()));
                try {
                    Process p = pb.start();
                    System.exit(0);
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            }  
        }
        else {JOptionPane.showMessageDialog(null,"There was a problem downloading the update, please try again later!."," Update Failed!",JOptionPane.ERROR_MESSAGE);}
    } 
    
    
    // This gets the latest Cue2Pops application from the server
    public void getCue2PopsFromServer(String cue2popsPath, String cue2popsMD5){

        Socket serverSocket = getServerSocket();

        if (serverSocket != null){
            String outMessage = "CUE2POPS," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";  
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);

            if (outputStream != null && inputStream != null){
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
   
                byte[] recievedData = null;
                int fileLength = 0;
                int byteCount = 0;

                // Download the new cue2pops file
                try {
                    dataOutStream.writeInt(outMessage.length());
                    if (outMessage.length() > 0) try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    
                    fileLength = dataInputStream.readInt();
                    byteCount = dataInputStream.available();
                    recievedData = new byte[fileLength];
                    
                    if (fileLength > 0) {dataInputStream.readFully(recievedData);}
                    
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                if (recievedData != null && recievedData.length > 0) {try {Files.write(Paths.get(cue2popsPath), recievedData);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}

                if (new File(cue2popsPath).exists() && new File(cue2popsPath).isFile()){
                    if (PopsGameManager.PerformQuickHashCheck(new File(cue2popsPath)).equals(cue2popsMD5)){

                        // Delete the original cue2pops application
                        new File(new File(cue2popsPath).getParent().substring(0, new File(cue2popsPath).getParent().lastIndexOf(File.separator)+1) + new File(cue2popsPath).getName()).delete();

                        // Move the new cue2pops application    
                        new File(cue2popsPath).renameTo(new File(new File(cue2popsPath).getParent().substring(0, new File(cue2popsPath).getParent().lastIndexOf(File.separator)+1) + new File(cue2popsPath).getName()));

                        // Delete the backup folder
                        new File(new File(cue2popsPath).getParent()).delete();
                    }
                }
            } 
        }
    }
    
    
    
    // This tries to connect to the server to retrieve the selected image file
    public void getImageFromServer(Game selectedGame, String gameRegion, String gameID, String gameName, String coverType, String coverPath, int gameNumber, boolean batchMode){
        
        Socket serverSocket = getServerSocket();
        String number = "_(" + Integer.toString(gameNumber) + ")"; 

        if (coverType.equals("_SCR2")) {coverType = "_SCR";}
        
        if (serverSocket != null){
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);

            if (outputStream != null && inputStream != null){
                String outMessage = "ART," + PopsGameManager.getCurrentConsole() + "," + gameRegion + "," + coverType + "," + gameID + "," + number + "," +PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                byte[] recievedData = null;
                int fileLength;

                //String imagePrefix = PopsGameManager.getFilePrefix();
                //if (imagePrefix == null) {imagePrefix = "";}
                
                String localImagePath = null;
                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                    if (coverType.equals("_ICO")){localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverPath + ".png";}
                    else {localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverPath + ".jpg";}
                } else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                    if (coverType.equals("_ICO")){localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverPath + ".png";}
                    else {localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverPath + ".jpg";}
                }
                
                try {
                    dataOutStream.writeInt(outMessage.length());
                    if (outMessage.length() > 0) try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    
                    fileLength = dataInputStream.readInt();
                    recievedData = new byte[fileLength];
                    if (fileLength > 0) {dataInputStream.readFully(recievedData);}
                    
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                if (recievedData != null && recievedData.length > 0 && localImagePath != null){
                    if (recievedData.length > 20) {try {Files.write(Paths.get(localImagePath), recievedData);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                    //else if (new String(recievedData).contains("NO_IMAGE")) {if (!batchMode) {JOptionPane.showMessageDialog(null, "There is no image file available in the database for this game.", " No ART Available!", JOptionPane.WARNING_MESSAGE);}}
                } 
                
            }
        }
        else {JOptionPane.showMessageDialog(null,"The server does not seem to be responding at the moment, please try again later."," Server Not Responding!",JOptionPane.WARNING_MESSAGE);} 
    }
    
    
    
    // This gets the number of files that are available on the server for a specific cover type (returns zero if no files available)
    public int getImagesAvailableOnServer(Game selectedGame, String gameRegion, String gameID, String gameName, String coverType, boolean batchMode){
        
        Socket serverSocket = getServerSocket();
        int numberOfFiles = 0;

        if (serverSocket != null){
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);

            if (outputStream != null && inputStream != null){
                String outMessage = "ART_NUM," + PopsGameManager.getCurrentConsole() + "," + gameRegion + "," + coverType + "," + gameID + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                byte[] recievedData = null;
                int fileLength;
                
                String localImagePath = null;
                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                    if (coverType.equals("_ICO")){localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverType + ".png";}
                    else {localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF" + coverType + ".jpg";}
                } else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                    if (coverType.equals("_ICO")){localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverType + ".png";}
                    else {localImagePath = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + gameID + coverType + ".jpg";}
                }
                
                String recievedMessage = null;
                
                // Try and send the request message to the server
                try {
                    serverSocket.setSoTimeout(2000); 
                    dataOutStream.writeInt(outMessage.length());
                    if (outMessage.length() > 0) {try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}

                    fileLength = dataInputStream.readInt();
                    byte[] data = new byte[fileLength];
                    if (fileLength > 0) {
                        dataInputStream.readFully(data);
                        recievedMessage = new String(data, "UTF-8");
                        recievedMessage = recievedMessage.trim();
                    }  
                } 
                catch (IOException ex) {JOptionPane.showMessageDialog(null,"The server does not seem to be responding at the moment, please try again later."," Server Not Responding!",JOptionPane.WARNING_MESSAGE);}
                

                // Close the socket
                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}  

                // Print the message
                if (recievedMessage != null) {
                    numberOfFiles = Integer.parseInt(recievedMessage);
                }
                    
            }
        }
        
        return numberOfFiles;
    }
    
    
    
    // This tries to connect to the server to retrieve the selected config file
    public void getConfigFromServer(String gameRegion, String gameID, String gameName, boolean batchMode){
        
        Socket serverSocket = getServerSocket();
        
        if (serverSocket != null){
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);

            if (outputStream != null && inputStream != null){
                String outMessage = "CONFIG," + PopsGameManager.getCurrentConsole() + "," + gameRegion + "," + gameID + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                byte[] recievedData = null;
                int fileLength;
                
                String localConfigPath = null;
                
                if (PopsGameManager.getCurrentConsole().equals("PS1")) {
                    
                    //switch (PopsGameManager.getCurrentMode()) {
                        //case "SMB":

                        localConfigPath = PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF.cfg";

                            //break;
                        //case "USB":
                            //localConfigPath = PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + "XX." + gameName + "-" + gameID + "ELF.cfg";
                            //break;
                        //case "HDD":
                            //localConfigPath = PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + gameName + "-" + gameID + "ELF.cfg";
                            //break;
                        //default:
                            //break;
                    //}
                    
                }
                else {localConfigPath = PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + gameID + ".cfg";}
                

                try {
                    dataOutStream.writeInt(outMessage.length());
                    if (outMessage.length() > 0) {try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                    
                    fileLength = dataInputStream.readInt();
                    recievedData = new byte[fileLength];
                    if (fileLength > 0) {dataInputStream.readFully(recievedData);}
                    
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                if (recievedData != null && recievedData.length > 0 && localConfigPath != null){
                    if (recievedData.length > 20) {try {Files.write(Paths.get(localConfigPath), recievedData);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                    else if (new String(recievedData).contains("NO_CONFIG")) {if (!batchMode) {JOptionPane.showMessageDialog(null, "There is no config file available in the database for this game.", " No CFG Available!", JOptionPane.WARNING_MESSAGE);}}  
                } 

                // This updates the config version number in the text file (This was done to prevent having to update all of the config files on the server everytime there is an update)
                // It also replaces the game name in the config file to match the name of the users game
                if (new File(localConfigPath).exists()){
                    try {
                        List<String> cfgFileContent = new ArrayList<>(Files.readAllLines(Paths.get(localConfigPath), StandardCharsets.UTF_8));
                        
                        for (int i = 0; i < cfgFileContent.size(); i++) {
                            if (cfgFileContent.get(i).equals("CfgVersion=3")) {cfgFileContent.set(i, "CfgVersion=5");}
                            if (cfgFileContent.get(i).substring(0, 5).equals("Title")){cfgFileContent.set(i, "Title=" + gameName);}
                        }

                        Files.write(Paths.get(localConfigPath), cfgFileContent, StandardCharsets.UTF_8);
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                } 
            }
        }
        else {JOptionPane.showMessageDialog(null,"The server does not seem to be responding at the moment, please try again later."," Server Not Responding!",JOptionPane.WARNING_MESSAGE);} 
    }
    
    
    // This tries to connect to the server to retrieve the selected VMC file
    public void getVMCFromServer(String gameRegion, String vmcName, String gameName, String gameID){
        
        Socket serverSocket = getServerSocket();

        if (serverSocket != null){
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);

            if (outputStream != null && inputStream != null){
                String outMessage = "VMC," + PopsGameManager.getCurrentConsole() + "," + gameRegion + "," + vmcName + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                byte[] recievedData = null;
                int fileLength;
                String localVMCPath = null;
                
                if (PopsGameManager.getCurrentConsole().equals("PS2")) {localVMCPath = PopsGameManager.getOPLFolder() + File.separator + "VMC" + File.separator + vmcName + ".bin";}
                else if (PopsGameManager.getCurrentConsole().equals("PS1")) {
                    localVMCPath = PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameName + "-" + gameID + File.separator + vmcName + ".VMC";
                    
                    // Create the game folder if it does not already exist
                    File gameFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameName + "-" + gameID + File.separator);
                    if (!gameFolder.exists()) {gameFolder.mkdir();}
                }
                
                try {
                    dataOutStream.writeInt(outMessage.length());
                    if (outMessage.length() > 0) {try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                    
                    fileLength = dataInputStream.readInt();
                    recievedData = new byte[fileLength];
                    if (fileLength > 0) {dataInputStream.readFully(recievedData);}
                    
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                if (recievedData != null && recievedData.length > 0 && localVMCPath != null){
                    if (recievedData.length > 20) {try {Files.write(Paths.get(localVMCPath), recievedData);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                } 
                
                if (new File(localVMCPath).exists() && new File(localVMCPath).isFile()) {JOptionPane.showMessageDialog(null,"The VMC file has been successfully downloaded."," VMC File Downloaded",JOptionPane.PLAIN_MESSAGE);}
            }
        }
        else {JOptionPane.showMessageDialog(null,"The server does not seem to be responding at the moment, please try again later."," Server Not Responding!",JOptionPane.WARNING_MESSAGE);}
    }
    
    
    // This tries to connect to the server to retrieve the selected cheat file
    public String getCheatFromServer(String gameRegion, String game){
        
        String cheatFromServer = null;
        Socket serverSocket = getServerSocket();

        if (serverSocket != null){
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);

            if (outputStream != null && inputStream != null){
                String outMessage = "CHEAT," + PopsGameManager.getCurrentConsole() + "," + gameRegion + "," + game + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                byte[] recievedData = null;
                int fileLength;
                
                try {
                    dataOutStream.writeInt(outMessage.length());
                    if (outMessage.length() > 0) {try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                    
                    fileLength = dataInputStream.readInt();
                    recievedData = new byte[fileLength];
                    if (fileLength > 0) {dataInputStream.readFully(recievedData);}
                    
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                if (recievedData != null && recievedData.length > 0) {cheatFromServer = new String(recievedData);}
            }
        }    
        return cheatFromServer;
    }
    

    // This tries to connect to the server to retrieve the cheat/art lists
    public void getListFromServer(String listType, String console){

        Socket serverSocket = getServerSocket();

        if (serverSocket != null){
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);

            if (outputStream != null && inputStream != null){
                String outMessage = null;
                switch (listType) {
                    case "CHEAT":
                        outMessage = "CHEAT_LIST," + console + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                        break;
                    case "ART":
                        outMessage = "ART_LIST," + console + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                        break;
                    case "CONFIG":
                        outMessage = "CONFIG_LIST," + console + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                        break;
                    case "VMC":
                        outMessage = "VMC_LIST," + console + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber() + "," + PopsGameManager.getOSType() + "_" + PopsGameManager.getOSArchitecture() + "," + "0";
                        break;
                    default:
                        break;
                }
                    
                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                byte[] recievedData = null;
                int fileLength;
                String localConfigPath = null; 
                
                switch (listType) {
                    case "CHEAT":
                        localConfigPath = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + console + "_ServerCheatList.dat";
                        break;
                    case "ART":
                        localConfigPath = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + console + "_ServerArtList.dat";
                        break;
                    case "CONFIG":
                        localConfigPath = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + console + "_ServerConfigList.dat";
                        break; 
                    case "VMC":
                        localConfigPath = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + console + "_ServerVMCList.dat";
                        break;  
                    default:
                        break;
                }

                try {
                    if (outMessage != null){
                        dataOutStream.writeInt(outMessage.length());
                        if (outMessage.length() > 0) {try {dataOutStream.write(outMessage.getBytes(), 0, outMessage.length());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}

                        fileLength = dataInputStream.readInt();
                        recievedData = new byte[fileLength];
                        if (fileLength > 0) {dataInputStream.readFully(recievedData);}
                    } 
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                if (recievedData != null && !new String(recievedData).contains("NO_LIST")){try {Files.write(Paths.get(localConfigPath), recievedData);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}  
            }
        }
    }
    

    // This is used to send a message to the server to check that it is responding (usually used before performing batch actions that involve the server)
    public String sendMessageToServer(String serverMessage){

        //DatagramSocket clientSocket = null;
        String message = serverMessage + ",0";
        String response = null;
        Socket serverSocket = getServerSocket();
        
        if (serverSocket != null){
            byte[] sendData = message.getBytes();
            OutputStream outputStream = createOutputStream(serverSocket); 
            InputStream inputStream = createInputStream(serverSocket);
            
            if (outputStream != null && inputStream != null){

                DataOutputStream dataOutStream = new DataOutputStream(outputStream);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                String recievedMessage = null;

                // Try and send the request message to the server
                try {
                    serverSocket.setSoTimeout(2000); 
                    dataOutStream.writeInt(sendData.length);
                    if (sendData.length > 0) {try {dataOutStream.write(sendData, 0, sendData.length);} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}

                    int fileLength = dataInputStream.readInt();
                    byte[] data = new byte[fileLength];
                    if (fileLength > 0) {
                        dataInputStream.readFully(data);
                        recievedMessage = new String(data, "UTF-8");
                        recievedMessage = recievedMessage.trim();
                    }  
                } 
                catch (IOException ex) {JOptionPane.showMessageDialog(null,"The server does not seem to be responding at the moment, please try again later."," Server Not Responding!",JOptionPane.WARNING_MESSAGE);}

                // Close the socket
                try {serverSocket.close();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}  

                // Return string to show if response was recieved or not
                switch (serverMessage) {
                    case "RESPOND":
                        if (recievedMessage != null) {response = "RESPONSE";} else  {response = "NO_RESPONSE";}
                        break;
                    case "VERSION":
                        if (recievedMessage != null) {response = recievedMessage;} else  {response = "NO_RESPONSE";}
                        break;
                }   
            }
        }
        else {response = "NO_RESPONSE";}
        
         return response;   
    }
        
        
    // Get the server socket
    private Socket getServerSocket(){
        Socket serverSocket = null;
        InetAddress IPAddress = null;
        try {IPAddress = InetAddress.getByName(PopsGameManager.getServerAddress());} catch (UnknownHostException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        if (IPAddress != null) {try {serverSocket = new Socket(IPAddress, PopsGameManager.getServerPort());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
        return serverSocket;
    }
    
    
    // Create an output stream
    private OutputStream createOutputStream(Socket serverSocket){
        OutputStream out = null; 
        try {out = serverSocket.getOutputStream();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        return out;
    }
    
    
    // Create an input stream
    private InputStream createInputStream(Socket serverSocket){
        InputStream in = null;
        try {in = serverSocket.getInputStream();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        return in;
    }
}