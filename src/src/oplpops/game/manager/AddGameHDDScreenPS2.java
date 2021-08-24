package oplpops.game.manager;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class AddGameHDDScreenPS2 extends javax.swing.JDialog {

    private boolean batchMode;
    private JTextField textFieldGamePath;
    private JTextField textFieldGameName;
    private JTextField textFieldGameCounter;
    
    private final static String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
    private final static JProgressBar UPLOAD_STATUS_BAR = new JProgressBar(0, 100);
    private HDLDumpManager hdlDump;
    private boolean uploadInProgress = false;
    private File gameFile = null;
   
    // Public methods for accessing the UI elements from a seperate background thread
    public JLabel getTimeRemainingLabel(){return jLabelTimeRemaining;}
    public JLabel getUploadSpeedLabel(){return jLabelUploadSpeed;}
    public JProgressBar getProgressBar(){return UPLOAD_STATUS_BAR;}
    public JTextField getGameNameLabel(){return textFieldGameName;}
    public JTextField getGameCounterLabel(){return textFieldGameCounter;}
    
    public void setUploadInProgress(boolean uploading){uploadInProgress = uploading;}
    private String selectedPath;
    

    public AddGameHDDScreenPS2(java.awt.Frame parent, boolean modal, boolean batchMode, String userSelectedPath, File selectedFile) {
        super(parent, modal);
        this.batchMode = batchMode;
        this.gameFile = selectedFile;
        this.selectedPath = userSelectedPath;
        initComponents();
        overideClose();
        layoutGUI();
        initialiseGUI();
    }


    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                if (uploadInProgress){JOptionPane.showMessageDialog(null,"This window will automatically close once the current operation has completed."," Currently Performing Operation!",JOptionPane.WARNING_MESSAGE);}
                else {dispose();}
            }
        });
    }
    
    
    // Initilaise the GUI layout for the current mode (normal/batch)
    private void layoutGUI(){
        
        // Game path text field
        textFieldGamePath = new JTextField();
        textFieldGamePath.setSize(385, 25);
        textFieldGamePath.setLocation(100, 35);
        textFieldGamePath.setVisible(true);
        textFieldGamePath.setEditable(false);
        textFieldGamePath.setBackground(Color.WHITE);
        
        textFieldGameName = new JTextField();
        if (!batchMode){
            
            this.setTitle("Add PS2 Game to HDD");
            
            // Game name text field
            textFieldGameName.setSize(385, 25);
            textFieldGameName.setLocation(100, 70);
            textFieldGameName.setVisible(true);
        }
        else {
            
            this.setTitle("Add PS2 Game to HDD - Batch Mode");
            
            // Game name text field
            textFieldGameName.setSize(300, 25);
            textFieldGameName.setLocation(100, 70);
            textFieldGameName.setVisible(true);
            
            // Game counter text field
            textFieldGameCounter = new JTextField();
            textFieldGameCounter.setSize(75, 25);
            textFieldGameCounter.setLocation(410, 70);
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
    
    
    // Initialise the text in the GUI
    private void initialiseGUI(){

        jButtonUpload.setMargin(new Insets(0,0,0,0));
        
        // Display the game path and formatted game name in the text fields
        textFieldGamePath.setText(" " + gameFile.getPath());
        
        if (!batchMode) {
            
            // Game name from the selected file with the extension removed
            String gameName = gameFile.getName().substring(0, gameFile.getName().length() - 4);

            // If the game name contains the game ID/Region code, this removes it
            for (String regionCode:REGION_CODES) if (gameName.contains(regionCode)) gameName = gameName.substring(12, gameName.length());
        
            textFieldGameName.setText(gameName);
        }
        else {
            
            // Get all of the ISO files from the directory
            File[] files = new File(selectedPath).listFiles();
            List<String> isoFilesInDirectory = new ArrayList<>();
            for (File file : files) {if (file.isFile() && file.getName().length() > 4 && file.getName().substring(file.getName().length()-3, file.getName().length()).toUpperCase().equals("ISO")) {isoFilesInDirectory.add(file.getName());}}

            textFieldGameName.setText("         " + isoFilesInDirectory.size() + " games will be uploaded to the console!");
            textFieldGameName.setEditable(false);
            textFieldGameName.setBackground(Color.WHITE);
            textFieldGameCounter.setText("0/" + isoFilesInDirectory.size());
        }

        // Display the console IP address if it is already available
        if (PopsGameManager.getPS2IP() != null) jFormattedTextFieldIPAddress.setText(PopsGameManager.getPS2IP());
        
        // Create the progress bar
        jPanelProgress.add(UPLOAD_STATUS_BAR);
        UPLOAD_STATUS_BAR.setBounds(100, 30, 382, 25);
        UPLOAD_STATUS_BAR.setValue(0);
        UPLOAD_STATUS_BAR.repaint();
    }
    
    
    // Lock the UI after a game has been uploaded
    public void setUIControlsEnabled(boolean enabled){
        textFieldGameName.setEditable(enabled);
        jFormattedTextFieldIPAddress.setEditable(enabled);
        jButtonUpload.setEnabled(enabled);
    }
    
    
    public void closeDialog(){
        dispose();
    }
    

    // Uploads a PS2 game using HDL Dump
    private void uploadGamePS2(){

        if (!batchMode){
            if (gameFile.getPath() != null){
                setUIControlsEnabled(false);
                uploadInProgress = true;
                hdlDump = new HDLDumpManager();
                try {hdlDump.hdlDumpUploadGame(this, jFormattedTextFieldIPAddress.getText(), textFieldGameName.getText(), gameFile.getPath());} 
                catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
            }
            else {JOptionPane.showMessageDialog(null,"You need to select a game to upload."," No game selected!",JOptionPane.WARNING_MESSAGE);}
        }
        else{

            // Read all of the ISO files from the directory into an array list
            ArrayList<Path> ps2GamesInDirectory = new ArrayList<>();
            try(Stream<Path> paths = Files.walk(Paths.get(selectedPath))) {
                paths.forEach(filePath -> {
                    if (Files.isRegularFile(filePath) && filePath.getFileName().toString().length() > 4) {
                        if (filePath.getFileName().toString().substring(filePath.getFileName().toString().length()-4, filePath.getFileName().toString().length()).toLowerCase().equals(".iso")){
                            ps2GamesInDirectory.add(filePath);
                        }
                    }
                });
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
            
            // If the list is not empty, call HDL_Dump_Manager in batch mode
            if (!ps2GamesInDirectory.isEmpty()){
                setUIControlsEnabled(false);
                uploadInProgress = true;
                hdlDump = new HDLDumpManager();
                try {hdlDump.hdlDumpUploadGameBatch(this, jFormattedTextFieldIPAddress.getText(), ps2GamesInDirectory);} 
                catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
            }  
        }
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
        jPanelProgress = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelTimeRemaining = new javax.swing.JLabel();
        jLabelUploadSpeed = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Add PS2 Game"));

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
                .addContainerGap(402, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanelUploadLayout = new javax.swing.GroupLayout(jPanelUpload);
        jPanelUpload.setLayout(jPanelUploadLayout);
        jPanelUploadLayout.setHorizontalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelUploadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIp, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFormattedTextFieldIPAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonUpload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelUploadLayout.setVerticalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUploadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonUpload, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(jFormattedTextFieldIPAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelIp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelUpload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">  
    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed
        if (!"".equals(jFormattedTextFieldIPAddress.getText())) {uploadGamePS2();}
        else {JOptionPane.showMessageDialog(null,"You need to enter the IP address of your PS2 console into the text field."," No IP Address Entered!",JOptionPane.WARNING_MESSAGE);}
    }//GEN-LAST:event_jButtonUploadActionPerformed
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JFormattedTextField jFormattedTextFieldIPAddress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelIp;
    private javax.swing.JLabel jLabelTimeRemaining;
    private javax.swing.JLabel jLabelUploadSpeed;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelGame;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelUpload;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>  
}