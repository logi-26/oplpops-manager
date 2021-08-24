package oplpops.game.manager;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class GameRenamingScreenPS2 extends javax.swing.JDialog {
    
    private static final String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
    private static List<File> invalidGameListPS2;
    private static List<Game> gameList;
    
    public GameRenamingScreenPS2(java.awt.Frame parent, boolean modal, List<File> invalidGameList) {
        super(parent, modal);
        initComponents();
        initialiseGUI();
        createGameLists(invalidGameList);
    }
    
    
    // Initialise the GUI elements
    private void initialiseGUI(){
        this.setTitle(" Rename Games");
        
        // Remove the padding from the buttons
        jButtonRename.setMargin(new Insets(0,0,0,0));
        jButtonRenameAll.setMargin(new Insets(0,0,0,0));
        jButtonExit.setMargin(new Insets(0,0,0,0));
    }
    
    
    // This creates the list model for the games list in the MainScreen
    private void createList(String[] games){
        
        // List model
        jListGameList.setModel(new javax.swing.AbstractListModel<String>() { 
            @Override
            public int getSize() {return games.length;}
            @Override
            public String getElementAt(int i) {return games[i];}
        });
        
        // Mouse listener
        jListGameList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt)){displayGameDetails();}
            }
        });
        
        // Key listener
        jListGameList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {displayGameDetails();}
        });
        
        jScrollPane1.setViewportView(jListGameList);
    }
    
    
    // Creat the game lists
    private void createGameLists(List<File> invalidGameList){
        
        gameList = new ArrayList<>();
        invalidGameListPS2 = new ArrayList<>(invalidGameList);

        invalidGameListPS2.stream().forEach((isoFile) -> {
            String gameID = null;

            // Try and get the game ID from the ISO
            try {gameID = getPS2GameIDFromArchive(isoFile.getAbsolutePath());} 
            catch (Exception ex) {PopsGameManager.displayErrorMessageDebug("Error reading the PS2 game ID from the ISO file!\n\n" + ex.toString());}

            // Split the string to get the game name without the directory path or file extension
            String gamePath = isoFile.toString();
            String gameName = gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).substring(0,gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).lastIndexOf('.'));
            
            gameList.add(new Game(gameName, gameID, isoFile.toString(), PopsGameManager.bytesToHuman(isoFile.length()), isoFile.length()));
        });

        // Add the game names to the list box in the GUI
        List<String> gameNameList = new ArrayList<>();
        gameList.stream().forEach((game) -> {gameNameList.add(game.getGameName());});
        createList(gameNameList.toArray(new String[0]));
    }
    
    
    // This displays the game details in the MainScreen
    private void displayGameDetails(){

        if (jListGameList.getSelectedIndex() != -1){

            if (gameList != null){
                jTextFieldGameTitleDisplay.setText(" " + gameList.get(jListGameList.getSelectedIndex()).getGameName());
                jTextFieldGameIDDisplay.setText(gameList.get(jListGameList.getSelectedIndex()).getGameID());
                jTextFieldGameNumberDisplay.setText((jListGameList.getSelectedIndex()+1) + "/" + gameList.size());
                jTextFieldGameSizeDisplay.setText(gameList.get(jListGameList.getSelectedIndex()).getGameReadableSize());
                File originalFile = invalidGameListPS2.get(jListGameList.getSelectedIndex());
                jTextFieldGameOldTitleDisplay.setText(originalFile.getName());
                jTextFieldGameNewTitleDisplay.setText(gameList.get(jListGameList.getSelectedIndex()).getGameID() + "." + gameList.get(jListGameList.getSelectedIndex()).getGameName() + ".iso");
            }
        }
    }
    
    
    // Clear all of the game details in the GUI
    private void clearGameDetails(){
        jTextFieldGameTitleDisplay.setText("");
        jTextFieldGameNumberDisplay.setText("");        
        jTextFieldGameIDDisplay.setText("");
        jTextFieldGameSizeDisplay.setText("");
        jTextFieldGameOldTitleDisplay.setText("");
        jTextFieldGameNewTitleDisplay.setText("");
    }
    
    
    // This searches the ISO file for the games unique identifier file
    private String getPS2GameIDFromArchive(String archiveFile) throws Exception {
        IInArchive archive;
        RandomAccessFile randomAccessFile;
        randomAccessFile = new RandomAccessFile(archiveFile, "r");
        archive = SevenZip.openInArchive(ArchiveFormat.ISO, new RandomAccessFileInStream(randomAccessFile));
        
        String theGameID = null;
        
        for (int i = 0; i <archive.getNumberOfItems(); i++){
            String gameID = archive.getStringProperty(i, PropID.PATH);
            for (String regionCode:REGION_CODES) {if (gameID.contains(regionCode)) {theGameID = gameID;}}
        }
        
        archive.close();
        randomAccessFile.close();
        
        return theGameID;
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonExit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListGameList = new javax.swing.JList<>();
        jPanelGameInformation = new javax.swing.JPanel();
        jLabelGameTitle = new javax.swing.JLabel();
        jLabelGameID = new javax.swing.JLabel();
        jLabelGameSize = new javax.swing.JLabel();
        jTextFieldGameTitleDisplay = new javax.swing.JTextField();
        jTextFieldGameIDDisplay = new javax.swing.JTextField();
        jTextFieldGameSizeDisplay = new javax.swing.JTextField();
        jTextFieldGameNumberDisplay = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabelGameTitle1 = new javax.swing.JLabel();
        jTextFieldGameNewTitleDisplay = new javax.swing.JTextField();
        jButtonRename = new javax.swing.JButton();
        jTextFieldGameOldTitleDisplay = new javax.swing.JTextField();
        jLabelGameTitle2 = new javax.swing.JLabel();
        jButtonRenameAll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Rename Games"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Game List"));

        jScrollPane1.setViewportView(jListGameList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanelGameInformation.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Information"));
        jPanelGameInformation.setPreferredSize(new java.awt.Dimension(600, 101));

        jLabelGameTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelGameTitle.setText("Title:");
        jLabelGameTitle.setToolTipText("");

        jLabelGameID.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelGameID.setText("ID:");
        jLabelGameID.setToolTipText("");

        jLabelGameSize.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameSize.setText("Size:");
        jLabelGameSize.setToolTipText("");

        jTextFieldGameTitleDisplay.setEditable(false);
        jTextFieldGameTitleDisplay.setPreferredSize(new java.awt.Dimension(402, 20));

        jTextFieldGameIDDisplay.setEditable(false);
        jTextFieldGameIDDisplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameIDDisplay.setPreferredSize(new java.awt.Dimension(104, 20));

        jTextFieldGameSizeDisplay.setEditable(false);
        jTextFieldGameSizeDisplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameSizeDisplay.setPreferredSize(new java.awt.Dimension(80, 20));

        jTextFieldGameNumberDisplay.setEditable(false);
        jTextFieldGameNumberDisplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameNumberDisplay.setPreferredSize(new java.awt.Dimension(109, 20));

        javax.swing.GroupLayout jPanelGameInformationLayout = new javax.swing.GroupLayout(jPanelGameInformation);
        jPanelGameInformation.setLayout(jPanelGameInformationLayout);
        jPanelGameInformationLayout.setHorizontalGroup(
            jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                        .addComponent(jLabelGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameIDDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelGameSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameSizeDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                        .addComponent(jLabelGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGameNumberDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameInformationLayout.setVerticalGroup(
            jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameNumberDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameSize, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameSizeDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameIDDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Rename Game"));

        jLabelGameTitle1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameTitle1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelGameTitle1.setText("New Title:");
        jLabelGameTitle1.setToolTipText("");

        jTextFieldGameNewTitleDisplay.setPreferredSize(new java.awt.Dimension(402, 20));

        jButtonRename.setText("Rename");
        jButtonRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRenameActionPerformed(evt);
            }
        });

        jTextFieldGameOldTitleDisplay.setEditable(false);
        jTextFieldGameOldTitleDisplay.setPreferredSize(new java.awt.Dimension(402, 20));

        jLabelGameTitle2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameTitle2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelGameTitle2.setText("Old Title:");
        jLabelGameTitle2.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelGameTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameNewTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelGameTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameOldTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonRename, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameOldTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameNewTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonRename, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButtonRenameAll.setText("Rename All");
        jButtonRenameAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRenameAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelGameInformation, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jButtonRenameAll, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanelGameInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRenameAll, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // <editor-fold defaultstate="collapsed" desc="Button Click Events">  
    
    private void jButtonRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRenameActionPerformed

        // Do nothing if the text field is empty
        String newName = jTextFieldGameNewTitleDisplay.getText();
        if (!newName.equals("")){
            
            if (newName.length() > 4) {
                String fileExtension = newName.substring(newName.length() - 4);

                if (fileExtension.equals(".iso") || fileExtension.equals(".ISO")){

                    File originalFile = invalidGameListPS2.get(jListGameList.getSelectedIndex());
                    String originalPath = originalFile.getAbsolutePath().replace(originalFile.getName(), "");
                    String newPath = originalPath + newName;

                    // If a file with the same name does not already exist, rename the file
                    File newFile = new File(newPath);
                    if (!newFile.exists()){
                    
                        if (!originalFile.renameTo(newFile)) {JOptionPane.showMessageDialog (null, "The file could not be renamed!"," Problem Renaming File",JOptionPane.ERROR_MESSAGE);} 
                        else {
                            invalidGameListPS2.remove(originalFile);
                            GameListManager.addToGameListsPS2(newFile);
                            createGameLists(invalidGameListPS2);
                            clearGameDetails();
                        }
                    }
                    else {JOptionPane.showMessageDialog (null, "There is already a game with that name in the directory!"," Problem Renaming File",JOptionPane.ERROR_MESSAGE);} 
                }
                else {JOptionPane.showMessageDialog (null, "You cannot change the games file extension!"," Incorrect Game Name",JOptionPane.ERROR_MESSAGE);}
            }
            else {JOptionPane.showMessageDialog (null, "The new name appears to be too short!"," Incorrect Game Name",JOptionPane.ERROR_MESSAGE);}  
        }  
    }//GEN-LAST:event_jButtonRenameActionPerformed

    private void jButtonRenameAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRenameAllActionPerformed

        // List to hold any games that are unable to be re-named
        List<File> stillInvalidGameListPS2 = new ArrayList<>();
        
        // Attempt to rename each game in the list using the games current name and the games ID from the ISO file
        invalidGameListPS2.stream().forEach((isoFile) -> {
            
            // Try and get the game ID from the ISO
            String gameID = null;
            try {gameID = getPS2GameIDFromArchive(isoFile.getAbsolutePath());} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug("Error reading the PS2 game ID from the ISO file!\n\n" + ex.toString());}
            
            String originalPath = isoFile.getAbsolutePath().replace(isoFile.getName(), "");
            String newPath = originalPath + gameID + "." + isoFile.getName();
            
            // If a file with the same name does not already exist, rename the file
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                boolean success = isoFile.renameTo(newFile);
                if (success) {GameListManager.addToGameListsPS2(newFile);} else {stillInvalidGameListPS2.add(isoFile);}
            }
        });
        
        if (!stillInvalidGameListPS2.isEmpty()) {invalidGameListPS2 = stillInvalidGameListPS2;}
        else invalidGameListPS2 = new ArrayList<>();
        
        createGameLists(invalidGameListPS2);
        clearGameDetails(); 
        
        if (!invalidGameListPS2.isEmpty()) {JOptionPane.showMessageDialog (null, "There was a problem whilst attempting to rename some of the games!"," Problem Renaming Games",JOptionPane.WARNING_MESSAGE);}
        else {
            JOptionPane.showMessageDialog (null, "All of the files have been succesfully renamed!"," Files Renamed",JOptionPane.INFORMATION_MESSAGE);
            jButtonRenameAll.setEnabled(false);
            jButtonRename.setEnabled(false);
            jTextFieldGameNewTitleDisplay.setEnabled(false);
        } 
    }//GEN-LAST:event_jButtonRenameAllActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonExitActionPerformed
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonRename;
    private javax.swing.JButton jButtonRenameAll;
    private javax.swing.JLabel jLabelGameID;
    private javax.swing.JLabel jLabelGameSize;
    private javax.swing.JLabel jLabelGameTitle;
    private javax.swing.JLabel jLabelGameTitle1;
    private javax.swing.JLabel jLabelGameTitle2;
    private javax.swing.JList<String> jListGameList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelGameInformation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldGameIDDisplay;
    private javax.swing.JTextField jTextFieldGameNewTitleDisplay;
    private javax.swing.JTextField jTextFieldGameNumberDisplay;
    private javax.swing.JTextField jTextFieldGameOldTitleDisplay;
    private javax.swing.JTextField jTextFieldGameSizeDisplay;
    private javax.swing.JTextField jTextFieldGameTitleDisplay;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>  
}