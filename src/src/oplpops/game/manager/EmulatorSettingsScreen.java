package oplpops.game.manager;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class EmulatorSettingsScreen extends javax.swing.JDialog {
    private boolean emulatorInUse = false;
    private boolean fullScreenMode = false;
    private String emulatorPath = null;
    private String console = null;
    private String emulatorName;

    public EmulatorSettingsScreen(java.awt.Frame parent, boolean modal, String currentConsole) {
        super(parent, modal);
        console = currentConsole;
        if (console.equals("PS1")){emulatorName = "PCSXR";} 
        else if (console.equals("PS2")){emulatorName = "PCSX2";}
        initComponents();
        initialiseGUI();
    }

    
    // This initialises the GUI elements 
    private void initialiseGUI(){
        
        this.setTitle(" " + emulatorName + " Settings");
        jCheckBoxUseEmulator.setText("Use " + emulatorName);

        if (console.equals("PS1")){emulatorInUse = PopsGameManager.getEmulatorInUsePS1();}
        else if (console.equals("PS2")){emulatorInUse = PopsGameManager.getEmulatorInUsePS2();}
        
        if (emulatorInUse){
            jCheckBoxUseEmulator.setSelected(true);
            jCheckBoxFullScreenMode.setEnabled(true);
            jTextFieldPathPCSX2.setEnabled(true);
            jButtonBrowse.setEnabled(true);

            if (console.equals("PS1")){emulatorPath = PopsGameManager.getEmulatorPathPS1();}
            else if (console.equals("PS2")){emulatorPath = PopsGameManager.getEmulatorPathPS2();}

            if (emulatorPath != null) {jTextFieldPathPCSX2.setText(emulatorPath);}
            
            if (console.equals("PS1")){fullScreenMode = PopsGameManager.getEmulatorFullScreenPS1();}
            else if (console.equals("PS2")){fullScreenMode = PopsGameManager.getEmulatorFullScreenPS2();}

            if (fullScreenMode) {jCheckBoxFullScreenMode.setSelected(true);} else {jCheckBoxFullScreenMode.setSelected(false);}
        }
        else {
            jCheckBoxUseEmulator.setSelected(false);
            jCheckBoxFullScreenMode.setEnabled(false);
            jTextFieldPathPCSX2.setEnabled(false);
            jButtonBrowse.setEnabled(false);
        }
        
        this.pack();
        
        if (console.equals("PS1")){jCheckBoxFullScreenMode.setVisible(false);}
        else if (console.equals("PS2")){jCheckBoxFullScreenMode.setVisible(true);}
    }
    
    
    // This finds the emulators executable/binary files
    private void findEmulatorExecutable(){
        JFileChooser chooser = new JFileChooser();
        
        if (PopsGameManager.isOPLFolderSet()) {chooser.setCurrentDirectory(new java.io.File(PopsGameManager.getOPLFolder()));} else {chooser.setCurrentDirectory(new java.io.File("."));}

        chooser.setDialogTitle("Set " +  emulatorName + " Path");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(emulatorName + " Executable", "EXE");
        chooser.setFileFilter(filter);
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            emulatorPath = chooser.getSelectedFile().toString();
            jTextFieldPathPCSX2.setText(emulatorPath);
        }
    }
    

    // This saves the emulator settings in the settings.xml file
    private void saveEmulatorSettings(){
        
        if (jCheckBoxUseEmulator.isSelected()){
            if (emulatorPath != null){
                
                if (console.equals("PS1")){
                    PopsGameManager.setEmulatorInUsePS1(true);
                    PopsGameManager.setEmulatorPathPS1(emulatorPath);
                    if (jCheckBoxFullScreenMode.isSelected()) {PopsGameManager.setEmulatorFullScreenPS1(true);} else {PopsGameManager.setEmulatorFullScreenPS1(false);}
                }
                else if (console.equals("PS2")){
                    PopsGameManager.setEmulatorInUsePS2(true);
                    PopsGameManager.setEmulatorPathPS2(emulatorPath);
                    if (jCheckBoxFullScreenMode.isSelected()) {PopsGameManager.setEmulatorFullScreenPS2(true);} else {PopsGameManager.setEmulatorFullScreenPS2(false);}
                }

                try {XMLFileManager.writeSettingsXML();} 
                catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                this.setVisible(false);
            }
            else {JOptionPane.showMessageDialog(null,"You need to set the " + emulatorName + " path."," No path set!",JOptionPane.WARNING_MESSAGE);}
        }
        else {
            if (console.equals("PS1")){
                PopsGameManager.setEmulatorInUsePS1(false);
                PopsGameManager.setEmulatorPathPS1("");
                PopsGameManager.setEmulatorFullScreenPS1(false);
            }
            else if (console.equals("PS2")){
                PopsGameManager.setEmulatorInUsePS2(false);
                PopsGameManager.setEmulatorPathPS2("");
                PopsGameManager.setEmulatorFullScreenPS2(false);
            }
            
            try {XMLFileManager.writeSettingsXML();} 
            catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            this.setVisible(false);
        }  
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelEmulatorPanel = new javax.swing.JPanel();
        jCheckBoxUseEmulator = new javax.swing.JCheckBox();
        jTextFieldPathPCSX2 = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jCheckBoxFullScreenMode = new javax.swing.JCheckBox();
        jLabelPathSMB = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelEmulatorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Emulator Settings"));

        jCheckBoxUseEmulator.setText("Use PCSX2");
        jCheckBoxUseEmulator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxUseEmulatorActionPerformed(evt);
            }
        });

        jTextFieldPathPCSX2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPathPCSX2.setToolTipText("");
        jTextFieldPathPCSX2.setPreferredSize(new java.awt.Dimension(335, 25));

        jButtonBrowse.setText("Browse");
        jButtonBrowse.setPreferredSize(new java.awt.Dimension(90, 25));
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jCheckBoxFullScreenMode.setText("Fullscreen Mode");

        jLabelPathSMB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPathSMB.setText("Path:");
        jLabelPathSMB.setPreferredSize(new java.awt.Dimension(35, 25));

        javax.swing.GroupLayout jPanelEmulatorPanelLayout = new javax.swing.GroupLayout(jPanelEmulatorPanel);
        jPanelEmulatorPanel.setLayout(jPanelEmulatorPanelLayout);
        jPanelEmulatorPanelLayout.setHorizontalGroup(
            jPanelEmulatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEmulatorPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanelEmulatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxUseEmulator, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxFullScreenMode, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelEmulatorPanelLayout.createSequentialGroup()
                .addGroup(jPanelEmulatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelEmulatorPanelLayout.createSequentialGroup()
                        .addComponent(jLabelPathSMB, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldPathPCSX2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 21, Short.MAX_VALUE))
        );
        jPanelEmulatorPanelLayout.setVerticalGroup(
            jPanelEmulatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEmulatorPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jCheckBoxUseEmulator)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxFullScreenMode)
                .addGap(18, 18, 18)
                .addGroup(jPanelEmulatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPathPCSX2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPathSMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonSave.setText("Save");
        jButtonSave.setPreferredSize(new java.awt.Dimension(44, 25));
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.setPreferredSize(new java.awt.Dimension(60, 25));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelEmulatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jPanelEmulatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events"> 
    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        findEmulatorExecutable();  
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        saveEmulatorSettings();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jCheckBoxUseEmulatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxUseEmulatorActionPerformed
        
        if (jCheckBoxUseEmulator.isSelected()){
            jCheckBoxFullScreenMode.setEnabled(true);
            jTextFieldPathPCSX2.setEnabled(true);
            jButtonBrowse.setEnabled(true);
        }
        else {
            jCheckBoxFullScreenMode.setEnabled(false);
            jTextFieldPathPCSX2.setEnabled(false);
            jButtonBrowse.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxUseEmulatorActionPerformed
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxFullScreenMode;
    private javax.swing.JCheckBox jCheckBoxUseEmulator;
    private javax.swing.JLabel jLabelPathSMB;
    private javax.swing.JPanel jPanelEmulatorPanel;
    private javax.swing.JTextField jTextFieldPathPCSX2;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}