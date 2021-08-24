package oplpops.game.manager;

import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

public class FileReportScreen extends javax.swing.JDialog {
    
    public FileReportScreen(java.awt.Frame parent, boolean modal, int listIndex, String imageType) {
        super(parent, modal);
        initComponents();
        initialiseGUI(listIndex, imageType);
        
        jButtonSendReport.setMargin(new Insets(0,0,0,0));
        
        jTextFieldFileIdentifier.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) { 
                
                if (!jComboBoxFileTypes.getSelectedItem().equals("VMC")) {if (jTextFieldFileIdentifier.getText().length() >= 11 ){e.consume();}}
                else {if (jTextFieldFileIdentifier.getText().length() >= 21 ){e.consume();}}
            }  
        });
    }

    
    
    
    
    
    // Initialise the GUI textfields if the values are not null
    private void initialiseGUI(int listIndex, String imageType){
        
        jComboBoxConsole.setSelectedItem(PopsGameManager.getCurrentConsole());
        if (listIndex != -1){
            if (PopsGameManager.getCurrentConsole().equals("PS1")){ jTextFieldFileIdentifier.setText(GameListManager.getGamePS1(listIndex).getGameID());}
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){ jTextFieldFileIdentifier.setText(GameListManager.getGamePS2(listIndex).getGameID());}
            jTextAreaReport.requestFocus();
        }
        
        if (imageType != null){jComboBoxFileTypes.setSelectedItem(imageType.substring(1,imageType.length()));}
    }
   
    
    // Send the report to the server
    private void sendReport(){

        String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
        boolean conatinsIdentifier = false;
        String fileName = null;
        String errorDescription = null;
         
        if (!jTextFieldFileIdentifier.getText().equals("")) {fileName = jTextFieldFileIdentifier.getText();}
        if (!jTextAreaReport.getText().equals("")) {errorDescription = jTextAreaReport.getText();}
        
        if (fileName != null && errorDescription != null){
            
            // Ensure that the file name contains the unique identifier
            for (String regionCode:REGION_CODES) {if (fileName.toUpperCase().contains(regionCode)){conatinsIdentifier = true;}}

            // Ensure that the users message does not contain any "," or it will mess up our TCP servers split string method
            if (errorDescription.contains(",")){errorDescription = errorDescription.replace(",", "");}
            
            // Ensure that the user has provided a description of the problem, then send the report to the server
            if (conatinsIdentifier){

                // Detremine game region and console here using the unique game ID!!!!
                String gameRegion = PopsGameManager.determineGameRegion(fileName.substring(0, 4));
                new MyTCPClient().sendMessageToServer("REPORT," + fileName + "," + jComboBoxConsole.getSelectedItem() + "," + jComboBoxFileTypes.getSelectedItem() + "," + gameRegion + "," + errorDescription + "," + PopsGameManager.getMacAddress() + "," + PopsGameManager.getApplicationVersionNumber());
                JOptionPane.showMessageDialog(null, "Your message has been sent, thank you for your contribution.", " Message Sent", JOptionPane.PLAIN_MESSAGE); 
                dispose();
            }
            else {JOptionPane.showMessageDialog(null, "Make sure that you have enetered the correct file indentifier and description of the problem.", " Information Error!", JOptionPane.ERROR_MESSAGE);} 
        }
        else {JOptionPane.showMessageDialog(null, "Make sure that you have enetered the correct file indentifier and description of the problem.", " Information Error!", JOptionPane.ERROR_MESSAGE);}   
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextFieldFileIdentifier = new javax.swing.JTextField();
        jComboBoxFileTypes = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxConsole = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaReport = new javax.swing.JTextArea();
        jButtonSendReport = new javax.swing.JButton();

        setTitle("Report Bad File");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Report File"));

        jTextFieldFileIdentifier.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldFileIdentifier.setToolTipText("");

        jComboBoxFileTypes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CFG", "CHT", "VMC", "COV", "COV2", "SCR", "BG", "ICO" }));
        jComboBoxFileTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFileTypesActionPerformed(evt);
            }
        });

        jLabel1.setText("File Identifier:");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jComboBoxConsole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PS1", "PS2" }));
        jComboBoxConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxConsoleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldFileIdentifier, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jComboBoxConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jComboBoxFileTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFileIdentifier, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxFileTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxConsole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jTextAreaReport.setColumns(20);
        jTextAreaReport.setLineWrap(true);
        jTextAreaReport.setRows(5);
        jScrollPane1.setViewportView(jTextAreaReport);

        jButtonSendReport.setText("Send");
        jButtonSendReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSendReport, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonSendReport)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">   
    private void jButtonSendReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendReportActionPerformed
        sendReport();
    }//GEN-LAST:event_jButtonSendReportActionPerformed

    private void jComboBoxFileTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFileTypesActionPerformed
        //jTextFieldFileIdentifier.setText("");
    }//GEN-LAST:event_jComboBoxFileTypesActionPerformed

    private void jComboBoxConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxConsoleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxConsoleActionPerformed
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSendReport;
    private javax.swing.JComboBox<String> jComboBoxConsole;
    private javax.swing.JComboBox<String> jComboBoxFileTypes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaReport;
    private javax.swing.JTextField jTextFieldFileIdentifier;
    // End of variables declaration//GEN-END:variables
}// </editor-fold>