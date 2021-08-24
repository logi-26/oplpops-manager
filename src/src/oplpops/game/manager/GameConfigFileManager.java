package oplpops.game.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class GameConfigFileManager {
    
    public GameConfigFileManager(){}
    
   
    // This checks if their is a config file for the specific game
    public Boolean gameConfigExists(String gameID, String gameName){
        
        boolean configExists = false;
        
        File cfgFile = null;
        if (gameID != null) {
            if (PopsGameManager.getCurrentConsole().equals("PS1")){
                cfgFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF.cfg");
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                cfgFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + gameID + ".cfg");
            }
        }

        if (cfgFile != null){configExists = cfgFile.exists() && !cfgFile.isDirectory();}

        return configExists;
    }

    
    // This reads the data from a game config file and returns an array containing the formatted data
    public String[] readGameConfigFormatted(String gameID, String gameName) throws IOException {

        String configData[] = new String[24];
        File cfgFile = null;
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {cfgFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF.cfg");}
        else {cfgFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + gameID + ".cfg");}
        
        if (cfgFile.exists() && !cfgFile.isDirectory()){

            try (BufferedReader br = new BufferedReader(new FileReader(cfgFile))) {

                String line;
                while ((line = br.readLine()) != null) {

                    if (!line.equals("")){

                        if (line.contains("CfgVersion")) {configData[0] = line.substring(11);}
                        else if (line.contains("Title")) {configData[1] = line.substring(6);}
                        else if (line.contains("Genre")) {configData[2] = line.substring(6);}
                        else if (line.contains("Developer")) {configData[3] = line.substring(10);}
                        else if (line.contains("Release")) {configData[4] = line.substring(8);}
                        else if (line.contains("Players")) {configData[5] = line.substring(16);}
                        else if (line.contains("Rating")) {configData[6] = line.substring(14);}
                        else if (line.contains("Description")) {configData[7] = line.substring(12);}
                        else if (line.contains("Notes")) {configData[8] = line.substring(6);}
                        else if (line.contains("$VMC_0")) {configData[9] = line.substring(7);}
                        else if (line.contains("$VMC_1")) {configData[10] = line.substring(7);}
                        else if (line.contains("Cheat") && !line.contains("$EnableCheat")) {configData[11] = line.substring(6);}
                        else if (line.contains("$EnableCheat")) {configData[12] = line.substring(13);}
                        else if (line.contains("Device")) {configData[13] = line.substring(14);}
                        else if (line.contains("Vmode")) {configData[14] = line.substring(12);}
                        else if (line.contains("Aspect")) {configData[15] = line.substring(14);}
                        else if (line.contains("Scan")) {configData[16] = line.substring(10);}
                        else if (line.contains("$Compatibility")) {configData[17] = line.substring(15);}
                        else if (line.contains("$EnableGSM")) {configData[18] = line.substring(11);}
                        else if (line.contains("$GSMVMode")) {configData[19] = line.substring(10);}
                        else if (line.contains("$GSMXOffset")) {configData[20] = line.substring(12);}
                        else if (line.contains("$GSMYOffset")) {configData[21] = line.substring(12);}
                        else if (line.contains("$GSMSkipVideos")) {configData[22] = line.substring(15);}
                        else if (line.contains("Parental")) {configData[23] = line.substring(9);}
                    }
                }
            }
        } 
        return configData; 
    }
    
    
     // This reads the data from a game config file and returns an array containing the formatted data
    public String[] readGameConfigRaw(String gameID, String gameName) throws IOException {
        
        String configData[] = new String[24];
        File cfgFile = null;
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {cfgFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF.cfg");}
        else {cfgFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + gameID + ".cfg");}

        if (cfgFile.exists() && !cfgFile.isDirectory()){

            try (BufferedReader br = new BufferedReader(new FileReader(cfgFile))) {

                String line;
                while ((line = br.readLine()) != null) {

                    if (!line.equals("")){

                        if (line.contains("CfgVersion")) {configData[0] = line;}
                        else if (line.contains("Title")) {configData[1] = line;}
                        else if (line.contains("Genre")) {configData[2] = line;}
                        else if (line.contains("Developer")) {configData[3] = line;}
                        else if (line.contains("Release")) {configData[4] = line;}
                        else if (line.contains("Players")) {configData[5] = line;}
                        else if (line.contains("Rating")) {configData[6] = line;}
                        else if (line.contains("Description")) {configData[7] = line;}
                        else if (line.contains("Notes")) {configData[8] = line;}
                        else if (line.contains("$VMC_0")) {configData[9] = line;}
                        else if (line.contains("$VMC_1")) {configData[10] = line;}
                        else if (line.contains("Cheat") && !line.contains("$EnableCheat")) {configData[11] = line;}
                        else if (line.contains("$EnableCheat")) {configData[12] = line;}
                        else if (line.contains("Device")) {configData[13] = line;}
                        else if (line.contains("Vmode")) {configData[14] = line;}
                        else if (line.contains("Aspect")) {configData[15] = line;}
                        else if (line.contains("Scan")) {configData[16] = line;}
                        else if (line.contains("$Compatibility")) {configData[17] = line;}
                        else if (line.contains("$EnableGSM")) {configData[18] = line;}
                        else if (line.contains("$GSMVMode")) {configData[19] = line;}
                        else if (line.contains("$GSMXOffset")) {configData[20] = line;}
                        else if (line.contains("$GSMYOffset")) {configData[21] = line;}
                        else if (line.contains("$GSMSkipVideos")) {configData[22] = line;}
                        else if (line.contains("Parental")) {configData[23] = line;}
                    }
                }
            }
        } 
        return configData; 
    }
    

    // This writes a config file for the specific game
    public void writeGameConfigFile(String newConfigData[], String gameID, String gameName){
        
        // Try and write the config file
        PrintWriter writer = null;
        try {
            if (PopsGameManager.getCurrentConsole().equals("PS1")) {writer = new PrintWriter(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + PopsGameManager.getFilePrefix() + gameName + "-" + gameID + ".ELF.cfg", "UTF-8");}
            else {if (PopsGameManager.getCurrentConsole().equals("PS2")) {writer = new PrintWriter(PopsGameManager.getOPLFolder() + File.separator + "CFG" + File.separator + gameID + ".cfg", "UTF-8");}}
        } 
        catch (FileNotFoundException | UnsupportedEncodingException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        
        if (writer != null){
            for (String configData : newConfigData) if (configData != null) writer.println(configData);
            writer.close();
        } 
    }
    

    // This compares 2 sets of config data to determine if they are identical (the order can be different but the actual content must be identical)
    public boolean compareGameConfig(String firstConfigData[], String secondConfigData[]){
        
        ArrayList<String> newList = new ArrayList<>();
        ArrayList<String> storedList = new ArrayList<>();

        // Store the first array elements in a list if they do not equal null
        for (String configData1 : firstConfigData){if (configData1 != null) {newList.add(configData1);}}
        
        // Store the second array elements in a list if they do not equal null
        for (String configData2 : secondConfigData){if (configData2 != null) {storedList.add(configData2);}}
        
        // Sort the lists
        Collections.sort(newList);
        Collections.sort(storedList);  
        
        return newList.equals(storedList);
    }  
}