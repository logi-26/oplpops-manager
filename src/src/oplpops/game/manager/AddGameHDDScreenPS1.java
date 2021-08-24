package oplpops.game.manager;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

// Form for adding PS1 or PS2 games to the consoles internal HDD
public class AddGameHDDScreenPS1 extends javax.swing.JDialog {
    
    private final static String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
    private final static JProgressBar UPLOAD_STATUS_BAR = new JProgressBar(0, 100);
    private HDLDumpManager hdlDump;
    private boolean uploadInProgress = false;
    private File gameFile = null;

    public void setUploadInProgress(boolean uploading){uploadInProgress = uploading;}
    private boolean batchMode = false;
    private JTextField textFieldGamePath;
    private JTextField textFieldGameName;
    private JTextField textFieldGameCounter;
    private String selectedPath;
    
    
    private List<File> vcdFilesInDirectory = new ArrayList<>();
    private List<File> cueFilesInDirectory = new ArrayList<>();
    
    
    
    // Public methods for accessing the UI elements from a seperate background thread
    public JLabel getTimeRemainingLabel(){return jLabelTimeRemaining;}
    public JLabel getUploadSpeedLabel(){return jLabelUploadSpeed;}
    public JProgressBar getProgressBar(){return UPLOAD_STATUS_BAR;}
    public boolean includeElfFile(){return jCheckBoxIncludeElf.isSelected();}
    public JTextField getGameNameLabel(){return textFieldGameName;}
    public JTextField getGameCounterLabel(){return textFieldGameCounter;}
    public void closeDialog(){dispose();}
    
    
    
    // Public constructor (Requires the game file which will be added)
    public AddGameHDDScreenPS1(java.awt.Frame parent, boolean modal, Boolean batchMode, String userSelectedPath, File selectedFile) {
        super(parent, modal);
        this.gameFile = selectedFile;
        this.batchMode = batchMode;
        this.selectedPath = userSelectedPath;
        initComponents();
        layoutGUI();
        initialiseGUI();
        overideClose(); 
    }

    
    // Overide the form close event
    private void overideClose(){

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                
                // Ensure that there is no upload currently being performed before enabling this window to close
                if (!uploadInProgress){dispose();} 
                else {JOptionPane.showMessageDialog(null, "This window cannot be closed until the current upload has been completed.", " Upload in Progress", JOptionPane.INFORMATION_MESSAGE);}
            }
        });
    }
    
    
    
    
    
    
    
    
    
    
    
    // Initilaise the GUI layout for the current mode (normal/batch)
    private void layoutGUI(){
        
        // Game path text field
        textFieldGamePath = new JTextField();
        textFieldGamePath.setSize(375, 25);
        textFieldGamePath.setLocation(100, 35);
        textFieldGamePath.setVisible(true);
        textFieldGamePath.setEditable(false);
        textFieldGamePath.setBackground(Color.WHITE);
        
        textFieldGameName = new JTextField();
        if (!batchMode){
            
            this.setTitle("Add PS1 Game to HDD");
            
            // Game name text field
            textFieldGameName.setSize(375, 25);
            textFieldGameName.setLocation(100, 70);
            textFieldGameName.setVisible(true);
        }
        else {
            
            this.setTitle("Add PS1 Game to HDD - Batch Mode");
            
            // Game name text field
            textFieldGameName.setSize(290, 25);
            textFieldGameName.setLocation(100, 70);
            textFieldGameName.setVisible(true);
            
            // Game counter text field
            textFieldGameCounter = new JTextField();
            textFieldGameCounter.setSize(75, 25);
            textFieldGameCounter.setLocation(400, 70);
            textFieldGameCounter.setVisible(true);
            textFieldGameCounter.setHorizontalAlignment(SwingConstants.CENTER);
            textFieldGameCounter.setEditable(false);
            textFieldGameCounter.setBackground(Color.WHITE);
            
            jPanelGame.add(textFieldGameCounter);
        }
        
        jPanelGame.add(textFieldGamePath);
        jPanelGame.add(textFieldGameName);

        jPanelGame.revalidate();
        jPanelGame.repaint();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    // Initialise the GUI components
    private void initialiseGUI(){
        
        jButtonUpload.setMargin(new Insets(0,0,0,0));
        
        // Display the game path and formatted game name in the text fields
        textFieldGamePath.setText(" " + gameFile.getPath());
        
        
        
        if (!batchMode) {
            
            // Game name from the selected file with the extension removed
            String gameName = gameFile.getName().substring(0, gameFile.getName().length() - 4);

            // If the game name contains the game ID/Region code, this removes it
            if (gameName.length() > 12){
                
                ArrayList<String> regionCodeList = new ArrayList<>();
                regionCodeList.addAll(Arrays.asList(REGION_CODES));
                if (regionCodeList.contains(gameName.substring(0, 5))) {gameName = gameName.substring(0, gameName.length()-12);}
                else if (regionCodeList.contains(gameName.substring(gameName.length()-11, gameName.length()-6))) {gameName = gameName.substring(0, gameName.length()-12);}
            }

            textFieldGameName.setText(gameName);
        }
        else {

            // Get all of the VCD and BIN/CUE files from the directory
            File[] files = new File(selectedPath).listFiles();
            
            for (File file : files) {if (file.isFile() && file.getName().length() > 4 && file.getName().substring(file.getName().length()-3, file.getName().length()).toUpperCase().equals("VCD")) {vcdFilesInDirectory.add(file);}}
            for (File file : files) {if (file.isFile() && file.getName().length() > 4 && file.getName().substring(file.getName().length()-3, file.getName().length()).toUpperCase().equals("CUE")) {cueFilesInDirectory.add(file);}}

            textFieldGameName.setText("         " + (vcdFilesInDirectory.size() + cueFilesInDirectory.size()) + " games will be uploaded to the console!");
            textFieldGameName.setEditable(false);
            textFieldGameName.setBackground(Color.WHITE);
            textFieldGameCounter.setText("0/" + (vcdFilesInDirectory.size() + cueFilesInDirectory.size()));
        }

        // Display the console IP address if it is already available
        if (PopsGameManager.getPS2IP() != null) jFormattedTextFieldIPAddress.setText(PopsGameManager.getPS2IP());
        
        // Create the progress bar
        jPanelProgress.add(UPLOAD_STATUS_BAR);
        UPLOAD_STATUS_BAR.setBounds(100, 30, 374, 25);
        UPLOAD_STATUS_BAR.setValue(0);
        UPLOAD_STATUS_BAR.repaint();

        jCheckBoxIncludeElf.setSelected(true);

        // Load the VCD path values into the GUI
        String[] splitVCDPath = PopsGameManager.getRemoteVCDPath().split("/");
        jComboBoxFolderVCD1.setSelectedItem(splitVCDPath[0] + "/");
        jComboBoxFolderVCD2.setSelectedItem(splitVCDPath[1]);

        // Load the ELF path values into the GUI
        String[] splitElfPath = PopsGameManager.getRemoteELFPath().split("/");
        if (splitElfPath.length > 0){
            jComboBoxFolderELF1.setSelectedItem(splitElfPath[1] + "/");
            for (int i = 2; i < splitElfPath.length; i++){jTextFieldElfFolderELF2.setText(jTextFieldElfFolderELF2.getText() + splitElfPath[i]);} 
        }

        // Display the IP address in the textfield
        jFormattedTextFieldIPAddress.setText(PopsGameManager.getPS2IP()); 
    }
    

    // Lock the UI after a game has been uploaded
    public void setUIControlsEnabled(boolean enabled){
        textFieldGameName.setEditable(enabled);
        jFormattedTextFieldIPAddress.setEditable(enabled);
        jCheckBoxIncludeElf.setEnabled(enabled);
        jButtonUpload.setEnabled(enabled);
        
        jComboBoxFolderVCD1.setEnabled(enabled);
        jComboBoxFolderVCD2.setEnabled(enabled);
        jComboBoxFolderELF1.setEnabled(enabled);
        jTextFieldElfFolderELF2.setEnabled(enabled); 
    }
    

    // Uploads a PS1 games using the FTP Client
    private void uploadGamePS1(){

        // Check if POPSTARTER.ELF exists in /Tools/POPSTARTER/POPSTARTER.ELF 
        File popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.elf");
        if (popstarterFile.exists() && !popstarterFile.isDirectory()){
            popstarterFile.renameTo(new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF"));
        }
        
        // Check if POPSTARTER.ELF exists in POPSTARTER/POPSTARTER.ELF
        popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF");
        if (popstarterFile.exists() && popstarterFile.isFile()){
            
            JOptionPane.showMessageDialog(null,"Ensure that the FTP server is currently running on your console, then click \"OK\".  "," PS1 Game Transfers Require an FTP Connection",JOptionPane.WARNING_MESSAGE);

            // Save the remote file paths and IP address to the XML settings file
            PopsGameManager.setRemoteVCDPath(jComboBoxFolderVCD1.getSelectedItem().toString() + jComboBoxFolderVCD2.getSelectedItem().toString());

            // Determine which drive the ELF folder is on, based on weather the ELF folder is in +OPL or __.POPS
            String elfPath = null;
            if (jComboBoxFolderELF1.getSelectedItem().toString().substring(0, 1).equals("+")){
                String[] splitOPLPath = PopsGameManager.getRemoteOPLPath().split("/");
                elfPath = splitOPLPath[0] + "/" + jComboBoxFolderELF1.getSelectedItem().toString() + jTextFieldElfFolderELF2.getText();
            }
            else if (jComboBoxFolderELF1.getSelectedItem().toString().substring(0, 1).equals("_")){
                String[] splitVCDPath = PopsGameManager.getRemoteVCDPath().split("/");
                elfPath = splitVCDPath[0] + "/" + jComboBoxFolderELF1.getSelectedItem().toString() + jTextFieldElfFolderELF2.getText(); 
            }

            PopsGameManager.setRemoteELFPath(elfPath);
            PopsGameManager.setPS2IP(jFormattedTextFieldIPAddress.getText());

            try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug("Error saving the settings!\n\n" + ex.toString());}

            // Get the console IP address
            String ipAddress = PopsGameManager.getPS2IP();

            // Create the FTP client and attempt to connect to the FTP server that should be running on the console
            MyFTPClient myFTP = new MyFTPClient();

            if (myFTP.connectToConsole(ipAddress)) {

                setUIControlsEnabled(false);
                uploadInProgress = true;
 
                // Upload the game using eith batch mode or single mode
                if (!batchMode){uploadSingleMode(myFTP);} else {uploadBatchMode(myFTP);}
            } 
            
            
        }
        else {
            JOptionPane.showMessageDialog(null,"Could not locate POPSTARTER.ELF in the POPSTARTER directory."," Missing POPSTARTER.ELF!",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    
    
    
    
    
    
    
    // Upload a single PS1 game
    private void uploadSingleMode(MyFTPClient myFTP){
        
        String originalVCDName = null;
        String newVCDName = null;
        String filePath = textFieldGamePath.getText();
        String originalGameFileName = gameFile.getName().substring(0, gameFile.getName().length()-4);
        String newGameFileName = textFieldGameName.getText();
        String fileExtension = textFieldGamePath.getText().substring(textFieldGamePath.getText().lastIndexOf("."), textFieldGamePath.getText().length());

        if (fileExtension.equals(".cue")){

            // Check for bin file in directory
            String binPath = filePath.substring(1, filePath.lastIndexOf(File.separator)+1) + originalGameFileName + ".bin";
            File binFile = new File(binPath);

            if (binFile.exists() && binFile.isFile()){

                // Try and convert cue to vcd
                boolean vcdGenerated = false;
                try {vcdGenerated = AddGameManager.launchCueToPops(gameFile);} catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                if (vcdGenerated){
                    try {
                        // Get game ID from vcd file
                        String gameID = AddGameManager.getPS1GameIDFromVCD(new File(filePath.substring(1, filePath.lastIndexOf(File.separator)+1) + originalGameFileName + ".VCD"));

                        if (gameID != null){

                            // Generate .elf
                            AddGameManager.generateElf(newGameFileName + "-" + gameID + ".ELF", filePath.substring(1, filePath.lastIndexOf(File.separator)+1));

                            if (new File(filePath.substring(1, filePath.lastIndexOf(File.separator)+1) + newGameFileName + "-" + gameID + ".ELF").exists() && new File(filePath.substring(1, filePath.lastIndexOf(File.separator)+1) + newGameFileName + "-" + gameID + ".ELF").isFile()){

                                // Rename VCD file with game ID and name from text field
                                originalVCDName = gameFile.getAbsolutePath().substring(0, gameFile.getAbsolutePath().lastIndexOf(".")+1) + "VCD";
                                newVCDName = gameFile.getAbsolutePath().substring(0, gameFile.getAbsolutePath().lastIndexOf(File.separator)+1) + newGameFileName + "-" + gameID + ".VCD";
                                new File(originalVCDName).renameTo(new File(newVCDName));

                                // Add the single file to the list (this is to prevent having to use aseperate backgroundworker for batch mode)
                                List<File> vcdFileList = new ArrayList<>();
                                vcdFileList.add(new File(newVCDName));
                                
                                // Upload vcd file and elf file
                                myFTP.addGameToPS2(this, vcdFileList);
                            }
                            else {
                                uploadInProgress = false;
                                new File(gameFile.getAbsolutePath().substring(0, gameFile.getAbsolutePath().lastIndexOf(".")+1) + "VCD").delete();
                                setUIControlsEnabled(true);
                            }  
                        }
                    } catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                } 
            }  
            else {JOptionPane.showMessageDialog(null,"Unable to locate the .bin file that is associated with this .cue file!"," File Not Found",JOptionPane.ERROR_MESSAGE);}
        
        }
        else if (fileExtension.equals(".vcd") || fileExtension.equals(".VCD")){

            // Get game ID from vcd file
            try {
                String gameID = AddGameManager.getPS1GameIDFromVCD(new File(filePath.substring(1, filePath.lastIndexOf(File.separator)+1) + originalGameFileName + ".VCD"));

                if (gameID != null){

                    // Generate .elf
                    AddGameManager.generateElf(newGameFileName + "-" + gameID + ".ELF", filePath.substring(1, filePath.lastIndexOf(File.separator)+1));

                    if (new File(filePath.substring(1, filePath.lastIndexOf(File.separator)+1) + newGameFileName + "-" + gameID + ".ELF").exists() && new File(filePath.substring(1, filePath.lastIndexOf(File.separator)+1) + newGameFileName + "-" + gameID + ".ELF").isFile()){

                        // Rename VCD file with game ID and name from text field
                        originalVCDName = gameFile.getAbsolutePath().substring(0, gameFile.getAbsolutePath().lastIndexOf(".")+1) + "VCD";
                        newVCDName = gameFile.getAbsolutePath().substring(0, gameFile.getAbsolutePath().lastIndexOf(File.separator)+1) + newGameFileName + "-" + gameID + ".VCD";
                        new File(originalVCDName).renameTo(new File(newVCDName));

                        // Add the single file to the list (this is to prevent having to use aseperate backgroundworker for batch mode)
                        List<File> vcdFileList = new ArrayList<>();
                        vcdFileList.add(new File(newVCDName));

                        // Upload vcd file and elf file
                        myFTP.addGameToPS2(this, vcdFileList);
                    }
                }
            } catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }   
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Batch upload multiple PS1 games
    private void uploadBatchMode(MyFTPClient myFTP){

        // First convert any BIN/CUE files to VCD files and add them to the VCD file list
        cueFilesInDirectory.forEach((cueFile) -> {
            String originalGameFileName = cueFile.getName().substring(0, cueFile.getName().length()-4);
            
            // Check for bin file in directory
            String binPath = cueFile.getAbsolutePath().substring(0, cueFile.getAbsolutePath().lastIndexOf(File.separator)+1) + originalGameFileName + ".bin";
            File binFile = new File(binPath);

            if (binFile.exists() && binFile.isFile()){

                // Try and convert cue to vcd
                boolean vcdGenerated = false;
                try {vcdGenerated = AddGameManager.launchCueToPops(cueFile);} catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug("Error launching cue2pops!\n\n" + ex.toString());}

                // Add the VCD file to the list
                if (vcdGenerated){
                    File newVCDFile = new File(cueFile.getAbsolutePath().substring(0, cueFile.getAbsolutePath().lastIndexOf(File.separator)+1) + originalGameFileName + ".VCD");
                    if (newVCDFile.exists() && newVCDFile.isFile()) {vcdFilesInDirectory.add(newVCDFile);}
                }
            }
            else {JOptionPane.showMessageDialog(null,"Unable to locate the .bin file that is associated with this .cue file!"," File Not Found",JOptionPane.ERROR_MESSAGE);}
        });

        

        // Loop through all of the VCD files, rename the VCD file to include the game ID, generate the ELF file and upload the VCD file to the console
        for (File vcdFile : vcdFilesInDirectory){
            
            String originalGameFileName = vcdFile.getName().substring(0, vcdFile.getName().length()-4);
            
            // Get game ID from vcd file
            try {
                String gameID = AddGameManager.getPS1GameIDFromVCD(new File(vcdFile.getAbsolutePath().substring(0, vcdFile.getAbsolutePath().lastIndexOf(File.separator)+1) + originalGameFileName + ".VCD"));

                if (gameID != null){

                    // Generate .elf
                    AddGameManager.generateElf(originalGameFileName + "-" + gameID + ".ELF", vcdFile.getAbsolutePath().substring(0, vcdFile.getAbsolutePath().lastIndexOf(File.separator)+1));

                    if (new File(vcdFile.getAbsolutePath().substring(0, vcdFile.getAbsolutePath().lastIndexOf(File.separator)+1) + originalGameFileName + "-" + gameID + ".ELF").exists() && new File(vcdFile.getAbsolutePath().substring(0, vcdFile.getAbsolutePath().lastIndexOf(File.separator)+1) + originalGameFileName + "-" + gameID + ".ELF").isFile()){

                        // Rename VCD file with game ID if it does not already contain it
                        String originalVCDName = vcdFile.getName().substring(0, vcdFile.getName().lastIndexOf("."));
                        if (!originalVCDName.contains(gameID)){
                            String newVCDName = vcdFile.getAbsolutePath().substring(0, vcdFile.getAbsolutePath().lastIndexOf(File.separator)+1) + originalVCDName + "-" + gameID + ".VCD";
                            vcdFile.renameTo(new File(newVCDName));
                        }
                    }
                }
            } catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }  
        
        
        
        // Get all of the VCD files from the directory now that some of them may have been renamed
        vcdFilesInDirectory.clear();
        File[] files = new File(selectedPath).listFiles();
        for (File file : files) {if (file.isFile() && file.getName().length() > 4 && file.getName().substring(file.getName().length()-3, file.getName().length()).toUpperCase().equals("VCD")) {vcdFilesInDirectory.add(file);}}
        myFTP.addGameToPS2(this, vcdFilesInDirectory);
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanelGame = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanelUpload = new javax.swing.JPanel();
        jLabelIp = new javax.swing.JLabel();
        jFormattedTextFieldIPAddress = new javax.swing.JFormattedTextField();
        jButtonUpload = new javax.swing.JButton();
        jCheckBoxIncludeElf = new javax.swing.JCheckBox();
        jLabelPartitionVCD = new javax.swing.JLabel();
        jComboBoxFolderVCD1 = new javax.swing.JComboBox<>();
        jComboBoxFolderVCD2 = new javax.swing.JComboBox<>();
        jLabelPartitionELF = new javax.swing.JLabel();
        jComboBoxFolderELF1 = new javax.swing.JComboBox<>();
        jTextFieldElfFolderELF2 = new javax.swing.JTextField();
        jPanelProgress = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelTimeRemaining = new javax.swing.JLabel();
        jLabelUploadSpeed = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Add PS1 Game"));

        jPanelGame.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Game"));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Name:");
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 25));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Path:");
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout jPanelGameLayout = new javax.swing.GroupLayout(jPanelGame);
        jPanelGame.setLayout(jPanelGameLayout);
        jPanelGameLayout.setHorizontalGroup(
            jPanelGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameLayout.createSequentialGroup()
                .addGroup(jPanelGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameLayout.setVerticalGroup(
            jPanelGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelUpload.setBorder(javax.swing.BorderFactory.createTitledBorder("Destination"));

        jLabelIp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelIp.setText("Console IP:");
        jLabelIp.setPreferredSize(new java.awt.Dimension(80, 25));

        try {
            jFormattedTextFieldIPAddress.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###.###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextFieldIPAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextFieldIPAddress.setPreferredSize(new java.awt.Dimension(208, 25));

        jButtonUpload.setText("Upload");
        jButtonUpload.setPreferredSize(new java.awt.Dimension(80, 25));
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });

        jCheckBoxIncludeElf.setText("Upload ELF");
        jCheckBoxIncludeElf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCheckBoxIncludeElf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxIncludeElfActionPerformed(evt);
            }
        });

        jLabelPartitionVCD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPartitionVCD.setText("VCD Folder:");
        jLabelPartitionVCD.setPreferredSize(new java.awt.Dimension(80, 25));

        jComboBoxFolderVCD1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "hdd0:/", "mass:/", "mass1:/", "mass2:/" }));
        jComboBoxFolderVCD1.setPreferredSize(new java.awt.Dimension(80, 25));
        jComboBoxFolderVCD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFolderVCD1ActionPerformed(evt);
            }
        });

        jComboBoxFolderVCD2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "__.POPS", "__.POPS0", "__.POPS1", "__.POPS2", "__.POPS3", "__.POPS4", "__.POPS5", "__.POPS6", "__.POPS7", "__.POPS8", "__.POPS9" }));
        jComboBoxFolderVCD2.setPreferredSize(new java.awt.Dimension(80, 25));

        jLabelPartitionELF.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPartitionELF.setText("ELF Folder:");
        jLabelPartitionELF.setPreferredSize(new java.awt.Dimension(80, 25));

        jComboBoxFolderELF1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "+OPL/", "__.POPS/", "__.POPS0/", "__.POPS1/", "__.POPS2/", "__.POPS3/", "__.POPS4/", "__.POPS5/", "__.POPS6/", "__.POPS7/", "__.POPS8/", "__.POPS9/" }));
        jComboBoxFolderELF1.setPreferredSize(new java.awt.Dimension(80, 25));

        jTextFieldElfFolderELF2.setPreferredSize(new java.awt.Dimension(117, 25));

        javax.swing.GroupLayout jPanelUploadLayout = new javax.swing.GroupLayout(jPanelUpload);
        jPanelUpload.setLayout(jPanelUploadLayout);
        jPanelUploadLayout.setHorizontalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelUploadLayout.createSequentialGroup()
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPartitionELF, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelIp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPartitionVCD, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUploadLayout.createSequentialGroup()
                        .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxFolderELF1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxFolderVCD1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldElfFolderELF2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxFolderVCD2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jFormattedTextFieldIPAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonUpload, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxIncludeElf, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );
        jPanelUploadLayout.setVerticalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUploadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUploadLayout.createSequentialGroup()
                        .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelUploadLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jLabelPartitionELF, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelUploadLayout.createSequentialGroup()
                                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBoxFolderVCD1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelPartitionVCD, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxFolderELF1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelUploadLayout.createSequentialGroup()
                                .addComponent(jComboBoxFolderVCD2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldElfFolderELF2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelIp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelUploadLayout.createSequentialGroup()
                        .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxIncludeElf, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelUploadLayout.createSequentialGroup()
                                .addGap(72, 72, 72)
                                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButtonUpload, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jFormattedTextFieldIPAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer Status"));
        jPanelProgress.setPreferredSize(new java.awt.Dimension(477, 90));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Progress:");
        jLabel5.setPreferredSize(new java.awt.Dimension(80, 25));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Remaining:");
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 25));

        jLabelTimeRemaining.setText("00:00");
        jLabelTimeRemaining.setMinimumSize(new java.awt.Dimension(34, 20));
        jLabelTimeRemaining.setPreferredSize(new java.awt.Dimension(70, 25));

        jLabelUploadSpeed.setText("0MB/sec");
        jLabelUploadSpeed.setMinimumSize(new java.awt.Dimension(34, 20));
        jLabelUploadSpeed.setPreferredSize(new java.awt.Dimension(70, 25));

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabelTimeRemaining, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelUploadSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTimeRemaining, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelUploadSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelUpload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelUpload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">   
    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed

        if (!"".equals(jFormattedTextFieldIPAddress.getText())) {uploadGamePS1();}
        else {JOptionPane.showMessageDialog(null,"You need to enter the IP address of your PS2 console into the text field."," No IP Address Entered!",JOptionPane.WARNING_MESSAGE);}
    }//GEN-LAST:event_jButtonUploadActionPerformed

    private void jCheckBoxIncludeElfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxIncludeElfActionPerformed

        if(jCheckBoxIncludeElf.isSelected()){
            jComboBoxFolderELF1.setEnabled(true);
            jTextFieldElfFolderELF2.setEnabled(true);
        }
        else {
            jComboBoxFolderELF1.setEnabled(false);
            jTextFieldElfFolderELF2.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxIncludeElfActionPerformed

    private void jComboBoxFolderVCD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFolderVCD1ActionPerformed
        
        String[] vcdFullFolderArray = {"__.POPS", "__.POPS0", "__.POPS1", "__.POPS2", "__.POPS3", "__.POPS4", "__.POPS5", "__.POPS6", "__.POPS7", "__.POPS8", "__.POPS9"};
        String[] vcdMutedFolderArray = {"POPS"};
        String[] elfFullFolderArray = {"+OPL/", "__.POPS/", "__.POPS0/", "__.POPS1/", "__.POPS2/", "__.POPS3/", "__.POPS4/", "__.POPS5/", "__.POPS6/", "__.POPS7/", "__.POPS8/", "__.POPS9/"};
        String[] elfMutedFolderArray = {"POPS/"};
        
        // When a user chooses to store the VCD files on a mass drive the VCD path is disabled (When using mass VCD path must equal "POPS")
        if (jComboBoxFolderVCD1.getSelectedItem().toString().substring(0, 1).equals("m")){
            
            DefaultComboBoxModel modelVCD = new DefaultComboBoxModel<>(vcdMutedFolderArray);
            jComboBoxFolderVCD2.setModel(modelVCD);
            
            DefaultComboBoxModel modelELF = new DefaultComboBoxModel<>(elfMutedFolderArray);
            jComboBoxFolderELF1.setModel(modelELF);
        }
        else {
            
            DefaultComboBoxModel modelVCD = new DefaultComboBoxModel<>(vcdFullFolderArray);
            jComboBoxFolderVCD2.setModel(modelVCD);
            
            DefaultComboBoxModel modelELF = new DefaultComboBoxModel<>(elfFullFolderArray);
            jComboBoxFolderELF1.setModel(modelELF);
        }
        
    }//GEN-LAST:event_jComboBoxFolderVCD1ActionPerformed
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Generated Variables">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JCheckBox jCheckBoxIncludeElf;
    private javax.swing.JComboBox<String> jComboBoxFolderELF1;
    private javax.swing.JComboBox<String> jComboBoxFolderVCD1;
    private javax.swing.JComboBox<String> jComboBoxFolderVCD2;
    private javax.swing.JFormattedTextField jFormattedTextFieldIPAddress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelIp;
    private javax.swing.JLabel jLabelPartitionELF;
    private javax.swing.JLabel jLabelPartitionVCD;
    private javax.swing.JLabel jLabelTimeRemaining;
    private javax.swing.JLabel jLabelUploadSpeed;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelGame;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelUpload;
    private javax.swing.JTextField jTextFieldElfFolderELF2;
    // End of variables declaration//GEN-END:variables
}// </editor-fold>