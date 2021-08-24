package oplpops.game.manager;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ShareVMCScreen extends javax.swing.JDialog {

    public ShareVMCScreen(java.awt.Frame parent, boolean modal, int listIndex) {
        super(parent, modal);
        initComponents();
        initialiseGUI(listIndex);
        this.setTitle("Share VMC Files");
        
        // Add a key listener to the text field to limit the number of chars that the user can enter
        jTextFieldGameId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {if (jTextFieldGameId.getText().length() >= 11 ){e.consume();}}  
        });
    }
    
    
    // Initialise the GUI textfields if the values are not null
    private void initialiseGUI(int listIndex){
        
        jComboBoxConsole.setSelectedItem(PopsGameManager.getCurrentConsole());
        if (listIndex != -1){
            
            if (PopsGameManager.getCurrentConsole().equals("PS1")){ jTextFieldGameId.setText(GameListManager.getGamePS1(listIndex).getGameID());}
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){ jTextFieldGameId.setText(GameListManager.getGamePS2(listIndex).getGameID());}

            getVMCPath(listIndex);
            jTextAreaVMCDescription.requestFocus();
        }
    }
    
    
    // This attempts to get the VMC path for the selected game if it exists
    private void getVMCPath(int listIndex){
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")){
                
            String gameID = GameListManager.getGamePS1(listIndex).getGameID();
            String gameName = GameListManager.getGamePS1(listIndex).getGameName();
            String vmcPath = PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameName + "-" + gameID + File.separator;
                
            File vmcFile = new File(vmcPath + gameID + ".VMC");
            if(vmcFile.exists() && !vmcFile.isDirectory()) {jTextFieldVMCPath.setText(vmcFile.getAbsolutePath());}
            else {
                for (int i = 0; i <10; i++){
                    vmcFile = new File(vmcPath + gameID + "_oplpops_" + i + ".VMC");
                    if(vmcFile.exists() && !vmcFile.isDirectory()) {
                        jTextFieldVMCPath.setText(vmcFile.getAbsolutePath());
                        i = 10;
                    }
                }
            } 
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){
            
            String gameID = GameListManager.getGamePS2(listIndex).getGameID();
            String vmcPath = PopsGameManager.getOPLFolder() + File.separator + "VMC" + File.separator;
            
            File vmcFile = new File(vmcPath + gameID + "_0.bin");
            if(vmcFile.exists() && !vmcFile.isDirectory()) {jTextFieldVMCPath.setText(vmcFile.getAbsolutePath());}
            else {
                vmcFile = new File(vmcPath + gameID + "_1.bin");
                if(vmcFile.exists() && !vmcFile.isDirectory()) {jTextFieldVMCPath.setText(vmcFile.getAbsolutePath());}
            }
        }
    }
        
        
 
    
    private void browseVMC(){
        
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(PopsGameManager.getOPLFolder()));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle("Select VMC File");
        FileNameExtensionFilter filter = null;
        
        if (jComboBoxConsole.getSelectedItem().equals("PS1")){filter = new FileNameExtensionFilter("PS1 Virtual Memory Card Files", "VMC");}
        else if (jComboBoxConsole.getSelectedItem().equals("PS2")){filter = new FileNameExtensionFilter("PS2 Virtual Memory Card Files", "bin");}
        
        chooser.setFileFilter(filter);
        chooser.showOpenDialog(null);
        File vmcFile = chooser.getSelectedFile();

        // Display the VMC file path in the text filed
        if (vmcFile != null){jTextFieldVMCPath.setText(vmcFile.getAbsolutePath());}
    }
    
    
    private void uploadVMC(){
        
        if (!jTextFieldVMCPath.getText().equals("") && !jTextAreaVMCDescription.getText().equals("") && !jTextFieldGameId.getText().equals("")){
            
            boolean conatinsIdentifier = false;
            String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
            
            // Ensure that the user has entered the games unique identifier
            for (String regionCode:REGION_CODES) {if (jTextFieldGameId.getText().toUpperCase().contains(regionCode)){conatinsIdentifier = true;}}
            
            if (conatinsIdentifier){
                String gameRegion = PopsGameManager.determineGameRegion(jTextFieldGameId.getText().substring(0, 4).toUpperCase());
                MyTCPClient myTCPClient = new MyTCPClient();
                try {
                    myTCPClient.shareVMCWithServer(jComboBoxConsole.getSelectedItem().toString(), gameRegion, jTextFieldGameId.getText(), jTextFieldVMCPath.getText(), jTextAreaVMCDescription.getText());
                    JOptionPane.showMessageDialog(null, "Your VMC file has been uploaded to the server, thank you for your contribution.", " VMC File Uploaded", JOptionPane.PLAIN_MESSAGE); 
                    dispose();
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}   
            }
            else {JOptionPane.showMessageDialog(null, "It appears that you have not entered a correct game ID.", " Incorrect Game ID!", JOptionPane.ERROR_MESSAGE); } 
        }
        else {JOptionPane.showMessageDialog(null, "Please ensure that you have entered all of the correct details for the VMC file.", " Missing Information!", JOptionPane.ERROR_MESSAGE);} 
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextFieldVMCPath = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaVMCDescription = new javax.swing.JTextArea();
        jButtonUpload = new javax.swing.JButton();
        javax.swing.JLabel jLabelDescription = new javax.swing.JLabel();
        javax.swing.JLabel jLabelGameID = new javax.swing.JLabel();
        jTextFieldGameId = new javax.swing.JTextField();
        javax.swing.JLabel jLabelGameID1 = new javax.swing.JLabel();
        jComboBoxConsole = new javax.swing.JComboBox<>();

        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("VMC File"));

        jTextFieldVMCPath.setEditable(false);

        jButtonBrowse.setText("Browse");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jTextAreaVMCDescription.setColumns(20);
        jTextAreaVMCDescription.setRows(5);
        jScrollPane1.setViewportView(jTextAreaVMCDescription);

        jButtonUpload.setText("Upload");
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });

        jLabelDescription.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelDescription.setText("VMC Description:");

        jLabelGameID.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelGameID.setText("Game ID:");

        jTextFieldGameId.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabelGameID1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelGameID1.setText("Console:");

        jComboBoxConsole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PS1", "PS2" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonUpload, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabelGameID1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jComboBoxConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabelGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldGameId, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabelDescription, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextFieldVMCPath, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(412, 412, 412)
                        .addComponent(jButtonBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameID1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldGameId, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldVMCPath, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonBrowse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonUpload)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        browseVMC();
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUploadActionPerformed
        uploadVMC();
    }//GEN-LAST:event_jButtonUploadActionPerformed
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JComboBox<String> jComboBoxConsole;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaVMCDescription;
    private javax.swing.JTextField jTextFieldGameId;
    private javax.swing.JTextField jTextFieldVMCPath;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}