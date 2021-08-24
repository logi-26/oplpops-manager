package oplpops.game.manager;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.border.TitledBorder;

public class AddGameSMBScreen extends javax.swing.JDialog {
    //private static final String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
    private final static JProgressBar DOWNLOAD_STATUS_BAR = new JProgressBar(0, 100);
    
    private String fileExtension = null;
    private File selectedFile = null;
    private boolean batchMode = false;
    private List<File> badCueFileList = new ArrayList<>();
    
    public AddGameSMBScreen(java.awt.Frame parent, boolean modal, Boolean batchMode, String userSelectedPath, String fileExtension, File selectedFile, int listIndex) {
        super(parent, modal);
        this.batchMode = batchMode;
        this.fileExtension = fileExtension;
        this.selectedFile = selectedFile;
        
        initComponents();
        initialiseGUI();
        overideClose();
    }
    
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                JOptionPane.showMessageDialog(null,"Please wait for the process to complete, this window should close automatically."," Performing Operation!",JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    
    // Initialise the GUI elements
    private void initialiseGUI(){
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {this.setTitle(" Add PlayStation Game");}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {this.setTitle(" Add PlayStation 2 Game");}
        
        if (selectedFile != null) {
            TitledBorder border = new TitledBorder(selectedFile.getName());
            jPanel1.setBorder(border);
        }
      
        jPanelProgress.add(DOWNLOAD_STATUS_BAR);
        DOWNLOAD_STATUS_BAR.setBounds(100, 50, 340, 25);
        DOWNLOAD_STATUS_BAR.setValue(0);
        DOWNLOAD_STATUS_BAR.repaint();
        jLabelSize.setText("0 Kb");
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")) { if (!batchMode) {addGame(1);} else {batchAddGame(1);}}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) { if (!batchMode) {addGame(2);} else {batchAddGame(2);}} 
    }
    
    
    // This enables the user to add PS1 and PS2 games to their OPL folder (PS1 - VCD or CUE)  (PS2 - ISO)
    private void addGame(int playstation){

        if (selectedFile != null){

            switch (fileExtension.toLowerCase()) {
                case "cue":
                    if (playstation ==1){
                        try {launchCueToPops(selectedFile);} catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug("Error launching cue2pops!\n\n" + ex.toString());}    
                    }
                    break;
                case "vcd":
                    if (playstation ==1){
                        File vcdFile = selectedFile;
                        String newFileName = AddGameManager.truncate(vcdFile.getName(), vcdFile.getName().length()-3);
                        String newFileFullName = newFileName + "VCD";
                        
                        if (alreadyInGameListPS1(vcdFile)) {
                            JOptionPane.showMessageDialog(null,"This game is already in your game list."," No Games to Add!",JOptionPane.INFORMATION_MESSAGE);
                            new BackgroundWorker(false, vcdFile.getAbsolutePath(), PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + newFileFullName, newFileName, false).execute();
                        }
                        else {
                            new BackgroundWorker(true, vcdFile.getAbsolutePath(), PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + newFileFullName, newFileName, false).execute();
                        } 
                    }
                    break;
                case "iso":
                    if (playstation ==2){
                        File isoFile = selectedFile;

                        // Try and get the game ID from the ISO
                        String gameID = null;
                        try {gameID = GameListManager.getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                        // If the game ID was retrieved, copy the file to the OPL directory, otherwise tell the user that the ISO file is not valid
                        if (gameID != null){
                            
                            if (!alreadyInGameListPS2(isoFile)){
                                
                                if (PopsGameManager.getCurrentMode().equals("SMB")){
                                    String newFileName = AddGameManager.truncate(isoFile.getName(), isoFile.getName().length()-3);
                                    String newFileFullName = newFileName + "iso";
                                    new BackgroundWorker(true, isoFile.getAbsolutePath(), PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + newFileFullName, newFileName, false).execute();
                                }
                                else if (PopsGameManager.getCurrentMode().equals("HDD_USB")){
                                    
                                    // If the ISO file is bigger than 4GB it will need to be converted to UL format
                                    if (isoFile.length() > 4294967296L){
                                        System.out.println("PS2 ISO File greater than 4GB!");
                                    }
                                    else {
                                        System.out.println("PS2 ISO File smaller than 4GB!\nAdding to USB!");
                                        
                                        String newFileName = AddGameManager.truncate(isoFile.getName(), isoFile.getName().length()-3);
                                        String newFileFullName = newFileName + "iso";
                                        
                                        // Copy the file to the USB drive without using the background worker task (much faster but no user feedback)
                                        isoFile.renameTo(new File(PopsGameManager.getOPLFolder() + File.separator + newFileFullName));
                                        
                                        //new BackgroundWorker(true, isoFile.getAbsolutePath(), PopsGameManager.getOPLFolder() + File.separator + newFileFullName, newFileName, false).execute();
                                    }
                                    
                                    
                                    
                                    
                                    
                                }
                                
                                
                                
                           
                            
                            
                            }
                            else{
                                JOptionPane.showMessageDialog(null,"This game is already in your game list."," No Games to Add!",JOptionPane.INFORMATION_MESSAGE);
                                new BackgroundWorker(false, isoFile.getAbsolutePath(), PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + null, null, false).execute();
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"The unique ID for this game could not be identified."," Invalid Game!",JOptionPane.ERROR_MESSAGE);
                            new BackgroundWorker(false, isoFile.getAbsolutePath(), PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + null, null, false).execute();
                        }
                    }
                    break;
            }
        }  
    }
    
    
    // This enables the user to batch add PS1 and PS2 games to their OPL folder (PS1 - VCD or CUE)  (PS2 - ISO)
    private void batchAddGame(int playstation){

        if (playstation == 1){
            
            // Get a list of all the cue files in the directory
            List<File> cueFileList = new ArrayList<>();
            File[] files = selectedFile.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().substring(file.getName().length()-3, file.getName().length()).equals("cue")) {

                    // Only add the cue file to the list if the associated bin file exists and a vcd file with the same name does not already exist
                    File associatedBin = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-3) + "bin");
                    File associatedVcd = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-3) + "VCD");
                    if (associatedBin.exists() && associatedBin.isFile() && !associatedVcd.exists()){cueFileList.add(file);}
                }
            }

            launchCueToPopsBatch(cueFileList); 
        }
        else if (playstation == 2){

            // Get a list of all the iso files in the directory
            List<File> isoFileList = new ArrayList<>();
            File[] files = selectedFile.listFiles();
            for (File file : files) {if (file.isFile() && file.getName().toLowerCase().substring(file.getName().length()-3, file.getName().length()).equals("iso")) {
                if (!alreadyInGameListPS2(file)){isoFileList.add(file);}}
            }
            
            if (isoFileList.isEmpty()){JOptionPane.showMessageDialog(null,"Could not locate any new games to add."," No Games to Add!",JOptionPane.INFORMATION_MESSAGE);}
            new BatchBackgroundWorker(isoFileList).execute();
        }
    }
    
    
    // This generates the .VCD file from the .CUE file using cue2pops
    public void launchCueToPops(File cueFile) throws IOException, InterruptedException{
        
        // File name and path for the newly created .VCD file
        String newFileName = AddGameManager.truncate(cueFile.getName(), cueFile.getName().length()-3);
        String newFileFullName = newFileName + "VCD";
        
        //newFileName += "VCD";
        String newFilePath = AddGameManager.truncate(cueFile.toString(), cueFile.toString().length()-newFileFullName.length());
        newFilePath += newFileFullName;
        
        // Try and convert cue to vcd
        boolean vcdGenerated = false;
        try {vcdGenerated = AddGameManager.launchCueToPops(cueFile);} catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        if (alreadyInGameListPS1(new File(cueFile.getAbsolutePath().substring(0, cueFile.getAbsolutePath().length()-3) + "VCD"))) {
            JOptionPane.showMessageDialog(null,"This game is already in your game list."," No Games to Add!",JOptionPane.INFORMATION_MESSAGE);
            vcdGenerated = false;
        }

        // Start background worker task
        new BackgroundWorker(vcdGenerated, newFilePath, PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + newFileFullName, newFileName, true).execute();
    } 
        
    
    // This batch generates the .VCD file from the .CUE file using cue2pops
    public void launchCueToPopsBatch(List<File> cueFileList) {

        badCueFileList.clear();
        
        // Loop through all of the available cue files, generating the vcd file for each one
        cueFileList.forEach((cueFile) -> {try {if (!AddGameManager.launchCueToPops(cueFile)){badCueFileList.add(cueFile);}} catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}});  
        
        // Get a list of all the vcd files in the directory
        List<File> vcdFileList = new ArrayList<>();
        File[] files = selectedFile.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().substring(file.getName().length()-3, file.getName().length()).toLowerCase().equals("vcd")) {
            
                // Check to ensure that the vcd file has not already been added to the game list
                if (!alreadyInGameListPS1(new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-3) + "VCD"))) {vcdFileList.add(file);}
            }
        }

        if (vcdFileList.isEmpty()){JOptionPane.showMessageDialog(null,"Could not locate any new games to add."," No Games to Add!",JOptionPane.INFORMATION_MESSAGE);}
        new BatchBackgroundWorker(vcdFileList).execute();
    } 
    
    
    // This checks if the vcd file is already in the PS1 game list
    private boolean alreadyInGameListPS1(File vcdFile){
        
        String vcdGameID = null;
        Boolean gameAlreadyInList = false;
        try {vcdGameID = GameListManager.getPS1GameIDFromVCD(vcdFile);} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        if (vcdGameID != null){
            List<Game> gameListPS1 = GameListManager.getGameListPS1();
            for (Game ps1Game : gameListPS1){if (ps1Game.getGameID().equals(vcdGameID)) {gameAlreadyInList = true;}}
        }
        
        return gameAlreadyInList;
    }
    
    
    // This checks if the iso file is already in the PS2 game list
    private boolean alreadyInGameListPS2(File isoFile){
        
        String isoGameID = null;
        Boolean gameAlreadyInList = false;
        try {isoGameID = GameListManager.getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        if (isoGameID != null){
            List<Game> gameListPS2 = GameListManager.getGameListPS2();
            for (Game ps2Game : gameListPS2){if (ps2Game.getGameID().equals(isoGameID)) {gameAlreadyInList = true;}}
        }
        
        return gameAlreadyInList;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Background Worker Threads">   
    // Background worker thread: this moves the newly created .VCD or .ISO file and updates the progress bar in the GUI
    public class BatchBackgroundWorker extends SwingWorker<Object, File> {

        List<File> fileList = new ArrayList<>();
        String gameID;

        public BatchBackgroundWorker(List<File> cueFileList) {
            this.fileList = cueFileList;
        }

        @Override
        protected Object doInBackground() throws Exception {

            if (!fileList.isEmpty()){

                // Try and move the newly created .VCD file to the OPL directory
                InputStream inStream = null;
                OutputStream outStream = null;

                for (File selectedFile : fileList){

                    String newFileName = null;
                    String newFileFullName = null;
                    String inPath = null;
                    String outPath = null;
                    gameID = null;
                    
                    if (PopsGameManager.getCurrentConsole().equals("PS1")){
                        // File name and path for the newly created .VCD file
                        newFileName = AddGameManager.truncate(selectedFile.getName(), selectedFile.getName().length()-3);
                        newFileFullName = newFileName + "VCD";
                        inPath = AddGameManager.truncate(selectedFile.toString(), selectedFile.toString().length()-newFileFullName.length());
                        inPath += newFileFullName;
                        outPath = PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + newFileFullName;
                    }
                    else {
                        // File name and path for the .ISO file
                        newFileName = AddGameManager.truncate(selectedFile.getName(), selectedFile.getName().length()-3);
                        newFileFullName = newFileName + "iso";
                        inPath = AddGameManager.truncate(selectedFile.toString(), selectedFile.toString().length()-newFileFullName.length());
                        inPath += newFileFullName;
                        outPath = PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + newFileFullName;
                    }
                    
                    try{
                        
                        if (PopsGameManager.getCurrentConsole().equals("PS1")){gameID = GameListManager.getPS1GameIDFromVCD(new File(inPath));}
                        else if (PopsGameManager.getCurrentConsole().equals("PS2")){gameID = GameListManager.getPS2GameIDFromArchive(inPath);}
                        
                        if (gameID != null){
                            
                            jLabelGameName.setText(" " + selectedFile.getName().substring(0, selectedFile.getName().length()-4) + " - (" + gameID + ")");
                            
                            if (PopsGameManager.getCurrentConsole().equals("PS1")){outPath = outPath.substring(0, outPath.length()-4) + "-" + gameID + ".VCD";}
                            else if (PopsGameManager.getCurrentConsole().equals("PS2")){outPath = outPath.substring(0, outPath.lastIndexOf(File.separator)+1) + gameID + "." + newFileFullName;}
                            
                            File originalPath = new File(inPath);
                            File oplPath = new File(outPath);

                            int longDiveder = 0;
                            if (originalPath.length() < 1460000000) {longDiveder = 100000;} else if (originalPath.length() > 1460000000) {longDiveder = 1000000;}

                            DOWNLOAD_STATUS_BAR.setMinimum(0);
                            DOWNLOAD_STATUS_BAR.setMaximum((int) (originalPath.length()/longDiveder));

                            inStream = new FileInputStream(originalPath);
                            outStream = new FileOutputStream(oplPath);
                            byte[] buffer = new byte[1024];
                            long byteCounter = 0;

                            int length;
                            while ((length = inStream.read(buffer)) > 0){
                                outStream.write(buffer, 0, length);
                                byteCounter += length;
                                jLabelSize.setText(PopsGameManager.bytesToHuman(byteCounter));
                                DOWNLOAD_STATUS_BAR.setValue((int) (byteCounter/longDiveder));
                            }

                            inStream.close();
                            outStream.close();

                            // If the VCD file has been created, this generates the ELF file
                            if (PopsGameManager.getCurrentConsole().equals("PS1")){                  
                                if (new File(outPath).exists() && new File(outPath).isFile()){
                                    AddGameManager.generateElf(PopsGameManager.getFilePrefix() + newFileName.substring(0, newFileName.length()-1) + "-" + gameID + ".ELF", PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator);
                                }
                            }
                            
                            // Delete the original vcd file
                            originalPath.delete();
                        }
                        else {JOptionPane.showMessageDialog(null,"The unique ID for this game could not be identified."," Invalid Game!",JOptionPane.ERROR_MESSAGE);}

                    } catch(IOException ex){PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                }
            }
            else {done();}

            return null;
        }
        
        
        @Override
        protected void done(){

            if (PopsGameManager.getCurrentConsole().equals("PS1")) {
                GameListManager.createGameListsPS1();
                GameListManager.writeConfigELM();
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")) {GameListManager.createGameListsPS2(true);}
            
            PopsGameManager.callbackToUpdateGUIGameList(null, -1); 
            
            // Display a message to the user listing any files that could not be converted
            if (!badCueFileList.isEmpty()){
                
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to convert the following files to VCD:    \n\n");
                badCueFileList.forEach((file) -> {stringBuilder.append(file.getName().substring(0, file.getName().length()-4)).append("\n");});
                stringBuilder.append("\n");
                
                JOptionPane.showMessageDialog(null,stringBuilder.toString()," Unable to Convert Files",JOptionPane.ERROR_MESSAGE);
            }
            
            dispose();
        }
    }
    
    
    // Background worker thread: this moes the newly created .VCD or .ISO file and updates the progress bar in the GUI
    public class BackgroundWorker extends SwingWorker<Object, File> {

        Boolean vcdGenerated;
        String gameID;
        String inPath;
        String outPath;
        String fileName;
        boolean deleteOriginal;

        public BackgroundWorker(Boolean vcdGenerated, String inPath, String outPath, String fileName, boolean deleteOriginal) {
            this.vcdGenerated = vcdGenerated;
            this.inPath = inPath;
            this.outPath = outPath;
            this.fileName = fileName;
            this.deleteOriginal = deleteOriginal;
        }

        @Override
        protected Object doInBackground() throws Exception {

            if (vcdGenerated){

                // Try and move the newly created .VCD file to the OPL directory
                InputStream inStream = null;
                OutputStream outStream = null;

                try{
                    
                    String newFileName = AddGameManager.truncate(new File(inPath).getName(), new File(inPath).getName().length()-3);

                    if (PopsGameManager.getCurrentConsole().equals("PS1")){gameID = GameListManager.getPS1GameIDFromVCD(new File(inPath));}
                    else if (PopsGameManager.getCurrentConsole().equals("PS2")){gameID = GameListManager.getPS2GameIDFromArchive(inPath);}

                    if (gameID != null){
                        
                        jLabelGameName.setText(" " + selectedFile.getName().substring(0, selectedFile.getName().length()-4) + " - (" + gameID + ")");
                        
                        if (PopsGameManager.getCurrentConsole().equals("PS1")){
                            if (!outPath.contains(gameID)){outPath = outPath.substring(0, outPath.length()-4) + "-" + gameID + ".VCD";}
                        }
                        else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                            if (!outPath.contains(gameID)){outPath = outPath.substring(0, outPath.lastIndexOf(File.separator)+1) + gameID + "." + newFileName + ".iso";}
                        }
                        
                        File originalPath = new File(inPath);
                        File oplPath = new File(outPath);

                        int longDiveder = 0;
                        if (originalPath.length() < 1460000000) {longDiveder = 100000;} else if (originalPath.length() > 1460000000) {longDiveder = 1000000;}

                        DOWNLOAD_STATUS_BAR.setMinimum(0);
                        DOWNLOAD_STATUS_BAR.setMaximum((int) (originalPath.length()/longDiveder));

                        inStream = new FileInputStream(originalPath);
                        outStream = new FileOutputStream(oplPath);
                        byte[] buffer = new byte[1024];
                        long byteCounter = 0;

                        int length;
                        while ((length = inStream.read(buffer)) > 0){
                            outStream.write(buffer, 0, length);
                            byteCounter += length;
                            jLabelSize.setText(PopsGameManager.bytesToHuman(byteCounter));
                            DOWNLOAD_STATUS_BAR.setValue((int) (byteCounter/longDiveder));
                        }

                        inStream.close();
                        outStream.close();

                        // If the VCD file has been created, this generates the ELF file
                        if (PopsGameManager.getCurrentConsole().equals("PS1")){                  
                            if (new File(outPath).exists() && new File(outPath).isFile()){
                                AddGameManager.generateElf(PopsGameManager.getFilePrefix() + newFileName.substring(0, newFileName.length()-1) + "-" + gameID + ".ELF", PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator);
                            }
                        }

                        if (deleteOriginal) {originalPath.delete();}
                    }
                    else {JOptionPane.showMessageDialog(null,"The unique ID for this game could not be identified."," Invalid Game!",JOptionPane.ERROR_MESSAGE);}

                } catch(IOException ex){PopsGameManager.displayErrorMessageDebug(ex.toString());} 
            }
            else {
                done();
            }
            
            return null;
        }
        
        
        @Override
        protected void done(){

            if (PopsGameManager.getCurrentConsole().equals("PS1")) {
                GameListManager.createGameListsPS1();
                GameListManager.writeConfigELM();
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")) {GameListManager.createGameListsPS2(true);}
            
            PopsGameManager.callbackToUpdateGUIGameList(null, -1); 
            dispose();
        }
    }     
    // </editor-fold> 
    

   
  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanelProgress = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelSize = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabelGameName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Game"));

        jPanelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer Status"));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Progress:");
        jLabel5.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Size:");
        jLabel6.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabelSize.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelSize.setText("0 Kb");
        jLabelSize.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Game:");
        jLabel7.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabelGameName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelGameName.setText("Game Name");

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProgressLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelGameName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelProgressLayout.createSequentialGroup()
                        .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelProgressLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelSize, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 234, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGameName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">      

    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelGameName;
    private javax.swing.JLabel jLabelSize;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelProgress;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}