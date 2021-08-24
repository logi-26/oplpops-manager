package oplpops.game.manager;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class SplitMergeScreen extends javax.swing.JDialog {

    private final static JProgressBar PROGRESS_BAR = new JProgressBar(0, 100);
    private final Game selectedGame;
    private final String mode;
    private boolean performingTask = false;
    
    
    public SplitMergeScreen(java.awt.Frame parent, boolean modal, Game selectedGame, String mode) {
        super(parent, modal);
        this.selectedGame = selectedGame;
        this.mode = mode;
        initComponents();
        initialiseGUI();
        overideClose();
    }

    
    // Initialise the GUI elements
    private void initialiseGUI(){
        
        // Set the dialog title and game name label
        this.setTitle(mode + " PS2 Game");
        jLabelGameName.setText(selectedGame.getGameName());
        
        // Create the progress bar
        jPanelProgress.add(PROGRESS_BAR);
        PROGRESS_BAR.setBounds(100, 80, 343, 25);
        PROGRESS_BAR.setValue(0);
        PROGRESS_BAR.repaint();
        
        jButtonProcess.setText(mode);
    }
    
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                if (performingTask){JOptionPane.showMessageDialog(null,"This window will automatically close once the current operation has completed."," Currently Performing Operation!",JOptionPane.WARNING_MESSAGE);} 
                else {dispose();}
            }
        });
    }
    
    
    // Set the label text for the number of game parts
    public void setGamePartsText(String text){jLabelGameParts.setText(text);}
    
    // Close the dialog window
    public void closeDialog(){dispose();}
    
    // This returns the progress bar
    public JProgressBar getProgressBar(){return PROGRESS_BAR;}
    
    // Split the selected game file using the USBUtil class
    public void splitFile(){try {USBUtil.splitFile(this, selectedGame);} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
    
    // Merges the selected game file using the USBUtil class
    public void mergeFile(){try {USBUtil.joinFiles(this, selectedGame.getGameID());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelProgress = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabelGameName = new javax.swing.JLabel();
        jLabelParts = new javax.swing.JLabel();
        jLabelGameParts = new javax.swing.JLabel();
        jButtonProcess = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Progress:");
        jLabel5.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Game:");
        jLabel7.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabelGameName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelGameName.setText("Game Name");

        jLabelParts.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelParts.setText("Parts:");
        jLabelParts.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabelGameParts.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelGameParts.setText("0/0");

        jButtonProcess.setText("Split");
        jButtonProcess.setPreferredSize(new java.awt.Dimension(53, 25));
        jButtonProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProcessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProgressLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelGameName, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
                    .addGroup(jPanelProgressLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelProgressLayout.createSequentialGroup()
                        .addComponent(jLabelParts, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelGameParts, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProgressLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGameName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelParts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGameParts, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">    
    private void jButtonProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProcessActionPerformed
        performingTask = true;
        jButtonProcess.setEnabled(false);
        if (mode.equals("Split")) {splitFile();} else {mergeFile();}
    }//GEN-LAST:event_jButtonProcessActionPerformed
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Generated Variables">    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonProcess;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelGameName;
    private javax.swing.JLabel jLabelGameParts;
    private javax.swing.JLabel jLabelParts;
    private javax.swing.JPanel jPanelProgress;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>  
}