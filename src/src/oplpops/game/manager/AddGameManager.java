package oplpops.game.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;


public final class AddGameManager {
    private static final String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
    

    private AddGameManager(){}
    
    // Used to truncate a string
    public static String truncate(String value, int length) {if (value.length() > length) return value.substring(0, length); else return value;}       
    
    
    // This copies the POPSTARTER.ELF file to the POPS directory and renames it to match the newly added VCD file
    public static void generateElf(String fileName, String path){
        
        // Check if POPSTARTER.ELF exists in /Tools/POPSTARTER/POPSTARTER.ELF 
        File popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.elf");
        if (popstarterFile.exists() && !popstarterFile.isDirectory()){
            popstarterFile.renameTo(new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF"));
        }
        
        
        // Check if POPSTARTER.ELF exists in /Tools/POPSTARTER/POPSTARTER.ELF
        popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF");
        
        if (popstarterFile.exists() && popstarterFile.isFile()){
            try {
                FileChannel destination;
                try (FileChannel source = new FileInputStream(popstarterFile).getChannel()) {
                    destination = new FileOutputStream(path + fileName).getChannel();
                    destination.transferFrom(source, 0, source.size());
                }
                destination.close(); 
            } 
            catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
        else {JOptionPane.showMessageDialog(null,"Could not locate POPSTARTER.ELF in the POPSTARTER directory."," Missing POPSTARTER.ELF!",JOptionPane.ERROR_MESSAGE);}
    }
    
    
    // Launch cue2pops to convert the .bin/.cue to a .vcd file
    public static boolean launchCueToPops(File cueFile) throws IOException, InterruptedException{
        
        // Display a message for 3 seconds to inform the user that cue2pops is converting the file
        JOptionPane opt = new JOptionPane("BIN/CUE to POPStarter VCD conversion tool v2.3.    \nSaving the virtual CD-ROM image. Please wait...    \n\n" + cueFile.getName() + "\n", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{});
       
        
        final JDialog timedDialog = opt.createDialog("CUE2POPS");
        new Thread(() -> {
            try{
                Thread.sleep(3000);
                timedDialog.dispose();
            }
            catch (InterruptedException ex){PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }).start();
        timedDialog.setVisible(true);
        
        boolean generatedVCD = false;

        String appFolder = "windows";
        String appName = "cue2pops.exe";
        
        if (PopsGameManager.getOSType().equals("Linux") || PopsGameManager.getOSType().equals("Mac")){
            appFolder = "linux";
            appName = "cue2pops";
        }
        
        List<String> commands = new ArrayList<>();
        commands.add(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName);
        commands.add(cueFile.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(cueFile.getPath().substring(0, cueFile.getPath().length() - cueFile.getName().length())));
        
        // Redirect any CUE2POPS errors
        pb.redirectErrorStream(true);

        // Start the process
        Process process = pb.start();
        
        // String builders to store the output from cue2pops (seperate string builder for general output and error messages)
        StringBuilder processOutput = new StringBuilder();
        StringBuilder processErrorOutput = new StringBuilder();
        try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));){
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null){
                if (!readLine.contains("Error")){processOutput.append(readLine).append(System.lineSeparator());}
                else {processErrorOutput.append(readLine).append(System.lineSeparator());}
            }

            // Wait for the process
            process.waitFor();
        }
        
        // If there was an error with cue2pops this displays a message to the user
        if (processErrorOutput.toString().trim().contains("Error")){
            JOptionPane.showMessageDialog(null,"" + processErrorOutput.toString().trim()," CUE2POPS Error!",JOptionPane.ERROR_MESSAGE);
        }
        
        // File name and path for the newly created .VCD file
        String newFileName = truncate(cueFile.getName(), cueFile.getName().length()-3);
        String newFileFullName = newFileName + "VCD";
        
        //newFileName += "VCD";
        String newFilePath = truncate(cueFile.toString(), cueFile.toString().length()-newFileFullName.length());
        newFilePath += newFileFullName;
        
        if (new File(newFilePath).exists() && new File(newFilePath).isFile()){generatedVCD = true;}
        
        return generatedVCD;
    } 
    
    
    // This searches the VCD file for the games unique identifier string
    public static String getPS1GameIDFromVCD(File vcdfile) throws Exception {

        String theGameID;
        try ( 
            // Read the file
            FileReader fileReader = new FileReader(vcdfile); 
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
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
            }          }
        
        return theGameID;
    }
}