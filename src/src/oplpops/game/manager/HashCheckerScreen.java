package oplpops.game.manager;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class HashCheckerScreen extends javax.swing.JDialog {
    
    private final static JProgressBar DOWNLOAD_STATUS_BAR = new JProgressBar(0, 100);
    private final File selectedGameFile;
    private boolean performingHashCheck = false;
    
    public HashCheckerScreen(java.awt.Frame parent, boolean modal, File selectedGameFile) {
        super(parent, modal);
        this.selectedGameFile = selectedGameFile;
        initComponents();
        initialiseGUI();
        overideClose();
    }
    
    
    // Initialise the GUI elements
    private void initialiseGUI(){
        
        jPanelProgress.add(DOWNLOAD_STATUS_BAR);
        DOWNLOAD_STATUS_BAR.setBounds(115, 70, 270, 25);
        DOWNLOAD_STATUS_BAR.setValue(0);
        DOWNLOAD_STATUS_BAR.repaint();
        
        jTextFieldGameName.setText(" " + selectedGameFile.getName());

        if (PopsGameManager.getCurrentConsole().equals("PS1")) {try {jTextFieldGameID.setText(GameListManager.getPS1GameIDFromVCD(selectedGameFile));} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {try {jTextFieldGameID.setText(GameListManager.getPS2GameIDFromArchive(selectedGameFile.getAbsolutePath()));} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
    }
    
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                
                if (performingHashCheck) {JOptionPane.showMessageDialog(null, "You cannot exit this window while a task is being performed."," Performing MD5 Check!",JOptionPane.ERROR_MESSAGE);}
                else {dispose();}
            }
        });
        this.setTitle(" MD5 Checker");
    }
    
    
    // Background worker thread: this performs the MD5 hash check and updates the progress bar in the GUI
    public class BackgroundWorker extends SwingWorker<Object, File> {
        private final File selectedGameFile;
        
        public BackgroundWorker(File selectedGameFile) {
            this.selectedGameFile = selectedGameFile;
        }

        @Override
        protected Object doInBackground() throws Exception {

            // Performs a MD5 hash on a game file!
            FileInputStream fileInputStream = null;
            try {

                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                fileInputStream = new FileInputStream(selectedGameFile);
                byte[] dataBytes = new byte[1024];
                int nread = 0;
                long totalRead = 0;

                DOWNLOAD_STATUS_BAR.setMinimum(0);
                DOWNLOAD_STATUS_BAR.setMaximum((int) (fileInputStream.getChannel().size()/100));

                while ((nread = fileInputStream.read(dataBytes)) != -1) {
                    messageDigest.update(dataBytes, 0, nread);
                    totalRead+= nread;
                    DOWNLOAD_STATUS_BAR.setValue((int) (totalRead/100));
                    DOWNLOAD_STATUS_BAR.repaint();
                }   

                byte[] mdbytes = messageDigest.digest();

                // Convert the byte to hex format
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < mdbytes.length; i++) {sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));}   

                jTextFieldMD5.setText(sb.toString());
            } 
            catch (FileNotFoundException ex) {} catch (IOException | NoSuchAlgorithmException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
            finally {try {if (fileInputStream != null) {fileInputStream.close();}} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
            
            return null;
        }

        @Override
        protected void done(){
            performingHashCheck = false;
            jButtonStart.setEnabled(true);
        }
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonStart = new javax.swing.JButton();
        jTextFieldGameName = new javax.swing.JTextField();
        jTextFieldGameID = new javax.swing.JTextField();
        jPanelProgress = new javax.swing.JPanel();
        jTextFieldMD5 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Game"));

        jButtonStart.setText("Start");
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        jTextFieldGameName.setEditable(false);
        jTextFieldGameName.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameName.setText("Game Name");

        jTextFieldGameID.setEditable(false);
        jTextFieldGameID.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameID.setText("SLES_123.45");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldGameName)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldGameName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonStart))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));

        jTextFieldMD5.setEditable(false);
        jTextFieldMD5.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldMD5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldMD5.setPreferredSize(new java.awt.Dimension(59, 25));
        jTextFieldMD5.setRequestFocusEnabled(false);

        jLabel1.setText("MD5:");

        jLabel2.setText("Progress:");

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProgressLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jTextFieldMD5, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldMD5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">  
    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed
        performingHashCheck = true;
        jButtonStart.setEnabled(false);
        jTextFieldMD5.setText("");
        new BackgroundWorker(selectedGameFile).execute();
    }//GEN-LAST:event_jButtonStartActionPerformed
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JTextField jTextFieldGameID;
    private javax.swing.JTextField jTextFieldGameName;
    private javax.swing.JTextField jTextFieldMD5;
    // End of variables declaration//GEN-END:variables
}// </editor-fold>