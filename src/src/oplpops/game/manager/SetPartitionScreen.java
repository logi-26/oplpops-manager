package oplpops.game.manager;

import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultComboBoxModel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class SetPartitionScreen extends javax.swing.JDialog {

    public SetPartitionScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        overideClose();
    }
    
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                dispose();
            }
        });
        this.setTitle(" Set Remote File Locations");
    }
    
    
    // Initialise the GUI components with data
    public void initialiseGUI(){
        
        if (!PopsGameManager.getRemoteOPLPath().equals("") && !PopsGameManager.getRemoteOPLPath().equals("") && !PopsGameManager.getRemoteOPLPath().equals("")){
            
            // Load the stored OPL path values into the GUI
            jComboBoxFolderOPL.setSelectedItem(PopsGameManager.getRemoteOPLPath());

            // Load the stored VCD path values into the GUI
            String[] splitVCDPath = PopsGameManager.getRemoteVCDPath().split("/");
            jComboBoxFolderVCD1.setSelectedItem(splitVCDPath[0] + "/");
            jComboBoxFolderVCD2.setSelectedItem(splitVCDPath[1]);

            // Load the stored ELF path values into the GUI
            String[] splitElfPath = PopsGameManager.getRemoteELFPath().split("/");
            if (splitElfPath.length > 0){
                jComboBoxFolderELF1.setSelectedItem(splitElfPath[1] + "/");
                for (int i = 2; i < splitElfPath.length; i++){jTextFieldElfFolderELF2.setText(jTextFieldElfFolderELF2.getText() + splitElfPath[i]);} 
            }
        }
        else {
            // Load the default OPL path values into the GUI
            jComboBoxFolderOPL.setSelectedItem("hdd0:/+OPL");
            
            // Load the default VCD path values into the GUI
            jComboBoxFolderVCD1.setSelectedItem("hdd0:/");
            jComboBoxFolderVCD2.setSelectedItem("__.POPS");
            
            // Load the default ELF path values into the GUI
            jComboBoxFolderELF1.setSelectedItem("hdd0:/+OPL/");
            jTextFieldElfFolderELF2.setText("APPS");
        }
        
        // Display the IP address in the textfield
        jFormattedTextFieldIPAddress.setText(PopsGameManager.getPS2IP()); 
        
        // Remove the padding from the buttons
        jButtonSet.setMargin(new Insets(0,0,0,0));
        jButtonCancel.setMargin(new Insets(0,0,0,0));       
    }
    
    
    // Save the remote file paths that the user has set
    private void saveValues(){

        PopsGameManager.setRemoteOPLPath(jComboBoxFolderOPL.getSelectedItem().toString());
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
        dispose();
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelUpload = new javax.swing.JPanel();
        jLabelIp = new javax.swing.JLabel();
        jFormattedTextFieldIPAddress = new javax.swing.JFormattedTextField();
        jButtonCancel = new javax.swing.JButton();
        jLabelPartitionVCD = new javax.swing.JLabel();
        jComboBoxFolderVCD1 = new javax.swing.JComboBox<>();
        jComboBoxFolderVCD2 = new javax.swing.JComboBox<>();
        jLabelPartitionELF = new javax.swing.JLabel();
        jComboBoxFolderELF1 = new javax.swing.JComboBox<>();
        jTextFieldElfFolderELF2 = new javax.swing.JTextField();
        jLabelPartitionOPL = new javax.swing.JLabel();
        jComboBoxFolderOPL = new javax.swing.JComboBox<>();
        jButtonSet = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelUpload.setBorder(javax.swing.BorderFactory.createTitledBorder("File Partitions"));

        jLabelIp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelIp.setText("Console IP:");
        jLabelIp.setPreferredSize(new java.awt.Dimension(80, 25));

        try {
            jFormattedTextFieldIPAddress.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###.###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextFieldIPAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextFieldIPAddress.setPreferredSize(new java.awt.Dimension(235, 25));

        jButtonCancel.setText("Cancel");
        jButtonCancel.setPreferredSize(new java.awt.Dimension(80, 25));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabelPartitionVCD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPartitionVCD.setText("VCD Folder:");
        jLabelPartitionVCD.setPreferredSize(new java.awt.Dimension(80, 25));

        jComboBoxFolderVCD1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "hdd0:/", "mass:/", "mass1:/", "mass2:/" }));
        jComboBoxFolderVCD1.setPreferredSize(new java.awt.Dimension(105, 25));
        jComboBoxFolderVCD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFolderVCD1ActionPerformed(evt);
            }
        });

        jComboBoxFolderVCD2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "__.POPS", "__.POPS0", "__.POPS1", "__.POPS2", "__.POPS3", "__.POPS4", "__.POPS5", "__.POPS6", "__.POPS7", "__.POPS8", "__.POPS9" }));
        jComboBoxFolderVCD2.setPreferredSize(new java.awt.Dimension(117, 25));

        jLabelPartitionELF.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPartitionELF.setText("ELF Folder:");
        jLabelPartitionELF.setPreferredSize(new java.awt.Dimension(80, 25));

        jComboBoxFolderELF1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "+OPL/", "__.POPS/", "__.POPS0/", "__.POPS1/", "__.POPS2/", "__.POPS3/", "__.POPS4/", "__.POPS5/", "__.POPS6/", "__.POPS7/", "__.POPS8/", "__.POPS9/" }));
        jComboBoxFolderELF1.setPreferredSize(new java.awt.Dimension(105, 25));

        jTextFieldElfFolderELF2.setPreferredSize(new java.awt.Dimension(117, 25));

        jLabelPartitionOPL.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPartitionOPL.setText("OPL Folder:");
        jLabelPartitionOPL.setPreferredSize(new java.awt.Dimension(80, 25));

        jComboBoxFolderOPL.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "hdd0:/+OPL", "mass:/+OPL", "mass1:/+OPL", "mass2:/+OPL" }));
        jComboBoxFolderOPL.setPreferredSize(new java.awt.Dimension(105, 25));

        jButtonSet.setText("Set");
        jButtonSet.setPreferredSize(new java.awt.Dimension(80, 25));
        jButtonSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelUploadLayout = new javax.swing.GroupLayout(jPanelUpload);
        jPanelUpload.setLayout(jPanelUploadLayout);
        jPanelUploadLayout.setHorizontalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUploadLayout.createSequentialGroup()
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPartitionOPL, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(jLabelPartitionVCD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelPartitionELF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelIp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextFieldIPAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelUploadLayout.createSequentialGroup()
                        .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBoxFolderELF1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxFolderVCD1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldElfFolderELF2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxFolderVCD2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jComboBoxFolderOPL, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelUploadLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButtonSet, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89))
        );
        jPanelUploadLayout.setVerticalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUploadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUploadLayout.createSequentialGroup()
                        .addComponent(jLabelPartitionOPL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelPartitionVCD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelPartitionELF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelIp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelUploadLayout.createSequentialGroup()
                        .addComponent(jComboBoxFolderOPL, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelUploadLayout.createSequentialGroup()
                                .addComponent(jComboBoxFolderVCD1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxFolderELF1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                            .addGroup(jPanelUploadLayout.createSequentialGroup()
                                .addComponent(jComboBoxFolderVCD2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldElfFolderELF2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextFieldIPAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSet, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelUpload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelUpload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">  
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetActionPerformed
        saveValues();
    }//GEN-LAST:event_jButtonSetActionPerformed

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
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSet;
    private javax.swing.JComboBox<String> jComboBoxFolderELF1;
    private javax.swing.JComboBox<String> jComboBoxFolderOPL;
    private javax.swing.JComboBox<String> jComboBoxFolderVCD1;
    private javax.swing.JComboBox<String> jComboBoxFolderVCD2;
    private javax.swing.JFormattedTextField jFormattedTextFieldIPAddress;
    private javax.swing.JLabel jLabelIp;
    private javax.swing.JLabel jLabelPartitionELF;
    private javax.swing.JLabel jLabelPartitionOPL;
    private javax.swing.JLabel jLabelPartitionVCD;
    private javax.swing.JPanel jPanelUpload;
    private javax.swing.JTextField jTextFieldElfFolderELF2;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}