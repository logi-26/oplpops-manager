package oplpops.game.manager;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class GameLongNameScreen extends javax.swing.JDialog {

     ArrayList<Game> longNameList;
     String currentConsole;
     int selectedIndex;
    
    public GameLongNameScreen(java.awt.Frame parent, boolean modal, String console, ArrayList<Game> longNameList) {
        super(parent, modal);
        initComponents();
        this.longNameList = longNameList;
        this.currentConsole = console;
        
        createList();
        
        // Set the screen title
        this.setTitle("Long " + console + " Game Names");

        // Limit the number of chars in the text filed to 32
        jTextFieldGameNewTitle.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) { 
                if (jTextFieldGameNewTitle.getText().length() >= 32){e.consume();}
            }  
        });
    }
    
    
    // Convert the game list to an array and display in the GUI
    private void createList(){
        // Add the game names to the list box in the GUI
        List<String> gameNameList = new ArrayList<>();
        longNameList.stream().forEach((game) -> {gameNameList.add(game.getGameName());});
        createListGUI(gameNameList.toArray(new String[0]));
    }
    
    
    // This creates the list model for the games list in the GUI
    private void createListGUI(String[] games){
        
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
    
    
    // This displays the game details
    private void displayGameDetails(){
        if (selectedIndex != -1){
            jTextFieldGameOldTitle.setText(longNameList.get(jListGameList.getSelectedIndex()).getGameName());
            jTextFieldGameNewTitle.setText(longNameList.get(jListGameList.getSelectedIndex()).getGameName().substring(0, 32)); 
        }
    }
    
    
    // Update the main game list and callback to the main GUI
    private void updateGameList(String console){
        longNameList.remove(selectedIndex);
        createList();

        if (console.equals("PS1")){
            // Re-generate the PS1 game list
            GameListManager.createGameListsPS1();

            // Generate a new conf_elm.cfg file
            GameListManager.writeConfigELM();
        }
        else if (console.equals("PS2")){
            
            // Re-generate the PS2 game list
            GameListManager.createGameListsPS2(false);
        }

        // Callback to update the main game list in the GUI
        PopsGameManager.callbackToUpdateGUIGameList(null, -1); 

        if (!longNameList.isEmpty()){
            jListGameList.setSelectedIndex(0);
            displayGameDetails();
        }
        else {
            jTextFieldGameOldTitle.setText("");
            jTextFieldGameNewTitle.setText("");
        }
    }
    
    
    // Rename a local PS1 VCD and ELF file (SMB or HDD_USB)
    private void renameLocalGamePS1(String prefix){
        
        // Ensure that the VCD and ELF files still exists
        File selectedGame = new File(longNameList.get(selectedIndex).getGamePath());
        if (selectedGame.exists()){

            boolean success;

            // Rename the VCD file
            success = selectedGame.renameTo(new File(selectedGame.getParentFile() + File.separator + jTextFieldGameNewTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".VCD"));

            // Rename the ELF file
            File elfFile = new File(selectedGame.getParent() + File.separator + prefix + selectedGame.getName().substring(0, selectedGame.getName().length()-3) + "ELF");
            if (elfFile.exists()){  
                success = elfFile.renameTo(new File(selectedGame.getParentFile() + File.separator + prefix + jTextFieldGameNewTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".ELF"));
            } 

            // Rename the PS1 game art files
            renameGameART();
            
            // Rename the PS1 config files
            renameConfigFilesPS1();
            
            String gameID = longNameList.get(selectedIndex).getGameID();
            
            // If the game is a multi-disc game, modify the DISCS.TXT file with the new file name
            if (longNameList.get(selectedIndex).getMultiDiscGame()) {renameMultiDiscPS1(longNameList.get(selectedIndex));}
            else {
                // Rename the game folder if it exists
                File gameFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + jTextFieldGameOldTitle.getText() + "-" + gameID);
                if (gameFolder.exists() && gameFolder.isDirectory()){gameFolder.renameTo(new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + jTextFieldGameNewTitle.getText() + "-" + gameID));}
            }
            
            // Remove the game from the list, update main game list and generate a new conf_elm.cfg file
            if (success){updateGameList("PS1");}
        }
    }
    
    
    // Rename a local PS2 ISO (SMB or HDD_USB)
    private void renameLocalGamePS2(){

        // Ensure that the ISO file still exists
        File selectedGame = new File(longNameList.get(selectedIndex).getGamePath());
        if (selectedGame.exists()){
            
            boolean success;
            
            // Rename the ISO file
            success = selectedGame.renameTo(new File(selectedGame.getParentFile() + File.separator + longNameList.get(selectedIndex).getGameID() + "." + jTextFieldGameNewTitle.getText() + ".iso"));

            // Remove the game from the list, update main game list and generate a new conf_elm.cfg file
            if (success){updateGameList("PS2");}
        }
    }
    
    
    // This renames the PS1 ART files if they exist (PS2 ART does not contain the game name)
    private void renameGameART(){
        
        switch (PopsGameManager.getCurrentMode()) {
            case "SMB":
                renameArtPS1("SB.","COV");
                renameArtPS1("SB.","COV2");
                renameArtPS1("SB.","BG");
                renameArtPS1("SB.","ICO");
                renameArtPS1("SB.","SCR");
                renameArtPS1("SB.","SCR2");
                break;
            case "HDD_USB":
                renameArtPS1("XX.","COV");
                renameArtPS1("XX.","COV2");
                renameArtPS1("XX.","BG");
                renameArtPS1("XX.","ICO");
                renameArtPS1("XX.","SCR");
                renameArtPS1("XX.","SCR2");
                break;
            case "HDD":

                break;
        }    
    }
    
    
    // This renames the ART
    private void renameArtPS1(String prefix, String coverType){

        String artFolder = PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator;
        String gameID = longNameList.get(selectedIndex).getGameID();
        
        // Rename art file if it exists
        if (coverType.equals("ICO")){
            File frontCover = new File(artFolder + prefix + jTextFieldGameOldTitle.getText() + "-" + gameID + ".ELF_" + coverType + ".png");
            if (frontCover.exists() && frontCover.isFile()){frontCover.renameTo(new File(artFolder + prefix + jTextFieldGameNewTitle.getText() + "-" + gameID + ".ELF_" + coverType + ".png"));}
        }
        else {
            File frontCover = new File(artFolder + prefix + jTextFieldGameOldTitle.getText() + "-" + gameID + ".ELF_" + coverType + ".jpg");
            if (frontCover.exists() && frontCover.isFile()){frontCover.renameTo(new File(artFolder + prefix + jTextFieldGameNewTitle.getText() + "-" + gameID + ".ELF_" + coverType + ".jpg"));}
        }           
    }
    
    
    // Rename the PS1 config files
    private void renameConfigFilesPS1(){
        
        String gameID = longNameList.get(selectedIndex).getGameID();
        File folder = new File(PopsGameManager.getOPLFolder() + File.separator + "CFG");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals("SB." + jTextFieldGameOldTitle.getText() + "-" + gameID + ".ELF.cfg")){
                    file.renameTo(new File(file.getParentFile() + File.separator + "SB." + jTextFieldGameNewTitle.getText() + "-" + gameID + ".ELF.cfg"));
                }
                
                if (file.getName().equals("XX." + jTextFieldGameOldTitle.getText() + "-" + gameID + ".ELF.cfg")){
                    file.renameTo(new File(file.getParentFile() + File.separator + "XX." + jTextFieldGameNewTitle.getText() + "-" + gameID + ".ELF.cfg"));
                }
            }
        }
    }
    
    
    // Modify the DISCS.TXT file with the new file name
    private void renameMultiDiscPS1(Game selectedGame){

        File discsFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + selectedGame.getGameName() + "-" + selectedGame.getGameID());
        File discsFile = new File(discsFolder + File.separator + "DISCS.TXT");

        ArrayList<File> otherFolderList = new ArrayList<>();
        
        // Check the DISCS.TXT file for the game that is being renamed
        if (discsFile.exists() && discsFile.isFile()){

            // Store each of the game names from the text file into a list
            ArrayList<String> discsInFile = new ArrayList<>();
            
            // Replace the game name that is in the DISCS.TXT file
            try (BufferedReader br = new BufferedReader(new FileReader(discsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                   if (line.equals(selectedGame.getGameName() + "-" + selectedGame.getGameID() + ".VCD")){discsInFile.add(jTextFieldGameNewTitle.getText() + "-" + selectedGame.getGameID() + ".VCD");}
                   else {discsInFile.add(line);}
                }
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            
            // Write the new modified text file using the data in the array list
            if (!discsInFile.isEmpty()){try {Files.write(Paths.get(discsFile.getAbsolutePath()),discsInFile,Charset.defaultCharset());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
            
            String gameID = longNameList.get(selectedIndex).getGameID();
            
            // Rename the folder that contains the DISCS.TXT file
            discsFolder.renameTo(new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + jTextFieldGameNewTitle.getText() + "-" + gameID));
            
            // Using the DISCS.TXT file, determine if there are any other game folders for the other discs in this mult-disc collection
            if (discsInFile.size() > 1){
                discsInFile.stream().filter((arrayItem) -> (!arrayItem.equals(jTextFieldGameNewTitle.getText() + "-" + selectedGame.getGameID() + ".VCD"))).forEachOrdered((arrayItem) -> {
                    otherFolderList.add(new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + arrayItem.substring(0, arrayItem.length()-16)));
                });
            }
            
            // Modify the DISC.TXT file in all of the other game folders so that they all contain the new game name and not the old game name
            if (!otherFolderList.isEmpty()){
                for (File folder : otherFolderList){
                    File otherDiscsFile = new File(folder.getAbsolutePath() + File.separator + "DISCS.TXT");
                    discsInFile.clear();

                    // Replace the game name that is in the DISCS.TXT file
                    try (BufferedReader br = new BufferedReader(new FileReader(otherDiscsFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                           if (line.equals(selectedGame.getGameName() + "-" + selectedGame.getGameID() + ".VCD")){discsInFile.add(jTextFieldGameNewTitle.getText() + "-" + selectedGame.getGameID() + ".VCD");}
                           else {discsInFile.add(line);}
                        }
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                    // Write the new modified text file using the data in the array list
                    if (!discsInFile.isEmpty()){try {Files.write(Paths.get(otherDiscsFile.getAbsolutePath()),discsInFile,Charset.defaultCharset());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
                }
            }
        } 
    }
    
    
    // Rename the selected game
    private void renameGame(){

        if (currentConsole.equals("PS1")){
            
            if (!jTextFieldGameNewTitle.getText().equals("")){
                
                // ensure that a game with the same name is not already in the game list
                boolean gameNameAlreadyUsed = false;
                for (Game game : GameListManager.getGameListPS1()){if (game.getGameName().equals(jTextFieldGameNewTitle.getText())){gameNameAlreadyUsed = true;}}
                
                if (!gameNameAlreadyUsed){
                    switch (PopsGameManager.getCurrentMode()) {
                        case "SMB":
                            renameLocalGamePS1("SB.");
                            break;
                        case "HDD_USB":
                            renameLocalGamePS1("XX.");
                            break;
                        case "HDD":
                            
                            // FTP client
                            MyFTPClient myFTP = new MyFTPClient();

                            // Connect to the PS2 console to rename the VCD file
                            if (myFTP.connectToConsole(PopsGameManager.getPS2IP())) {

                                String remoteDrive = GameListManager.getFormattedVCDDrive();
                                if (remoteDrive.equals("hdd")) {remoteDrive = "pfs";}

                                // Check if the VCD file is still on the console
                                List<String> remoteDirectoryList = myFTP.listRemoteDirectory("/pfs/0/", "__.POPS", true);
                                boolean vcdOnConsole = false;
                                for (String arrayItem : remoteDirectoryList){if (arrayItem.equals(jTextFieldGameOldTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".VCD")){vcdOnConsole = true;}}

                                // Rename the VCD file on the console
                                if (vcdOnConsole) {myFTP.renameFile("/pfs/0/" + jTextFieldGameOldTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".VCD", "/pfs/0/" + jTextFieldGameNewTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".VCD");}
                                    
                                // Disconnect the FTP connection with the console
                                myFTP.disconnectFromConsole();
                            } 
                           
                            // Connect to the PS2 console to rename the ELF file
                            if (myFTP.connectToConsole(PopsGameManager.getPS2IP())) {

                                String remoteDrive = GameListManager.getFormattedELFDrive();
                                if (remoteDrive.equals("hdd")) {remoteDrive = "pfs";}
                                
                                // Check if the ELF file is still on the console
                                List<String> remoteDirectoryList = myFTP.listRemoteDirectory("/pfs/0/APPS/", "+OPL", true);
                                boolean elfOnConsole = false;
                                for (String arrayItem : remoteDirectoryList){if (arrayItem.equals(jTextFieldGameOldTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".ELF")){elfOnConsole = true;}}

                                // Rename the ELF file on the console
                                if (elfOnConsole) {myFTP.renameFile("/pfs/0/" + jTextFieldGameOldTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".ELF", "/pfs/0/" + jTextFieldGameNewTitle.getText() + "-" + longNameList.get(selectedIndex).getGameID() + ".ELF");}
   
                                // Disconnect the FTP connection with the console
                                myFTP.disconnectFromConsole();
                            } 
                            break;
                    }    
                } else {
                    // Display a message that the name is already in the list
                    JOptionPane.showMessageDialog(null,"A game with the same name is already in the game list!"," Game Rename Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (currentConsole.equals("PS2")){
            
            if (!jTextFieldGameNewTitle.getText().equals("")){
                
                // ensure that a game with the same name is not already in the game list
                boolean gameNameAlreadyUsed = false;
                for (Game game : GameListManager.getGameListPS2()){if (game.getGameName().equals(jTextFieldGameNewTitle.getText())){gameNameAlreadyUsed = true;}}
                
                if (!gameNameAlreadyUsed){
                    switch (PopsGameManager.getCurrentMode()) {
                        case "HDD_USB":
                        case "SMB":
                            renameLocalGamePS2();
                            break;
                        case "HDD":
                            JOptionPane.showMessageDialog(null,"The application cannot currently rename a PS2 game in HDD mode!"," Unable To Rename Game",JOptionPane.ERROR_MESSAGE);
                            break;
                    }    
                } else {
                    // Display a message that the name is already in the list
                    JOptionPane.showMessageDialog(null,"A game with the same name is already in the game list!"," Game Rename Error",JOptionPane.ERROR_MESSAGE);
                } 
            } 
        }
    }
 
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListGameList = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jLabelGameTitle1 = new javax.swing.JLabel();
        jTextFieldGameNewTitle = new javax.swing.JTextField();
        jButtonRename = new javax.swing.JButton();
        jTextFieldGameOldTitle = new javax.swing.JTextField();
        jLabelGameTitle2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Title"));

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Rename Game"));

        jLabelGameTitle1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameTitle1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelGameTitle1.setText("New Title:");
        jLabelGameTitle1.setToolTipText("");

        jTextFieldGameNewTitle.setPreferredSize(new java.awt.Dimension(402, 20));

        jButtonRename.setText("Rename");
        jButtonRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRenameActionPerformed(evt);
            }
        });

        jTextFieldGameOldTitle.setEditable(false);
        jTextFieldGameOldTitle.setPreferredSize(new java.awt.Dimension(402, 20));

        jLabelGameTitle2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameTitle2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelGameTitle2.setText("Old Title:");
        jLabelGameTitle2.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelGameTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameNewTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelGameTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameOldTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonRename, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameOldTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameNewTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonRename, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    // <editor-fold defaultstate="collapsed" desc="Button Click Evenets"> 
    private void jButtonRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRenameActionPerformed
        
        if (jListGameList.getSelectedIndex() != -1){
            selectedIndex = jListGameList.getSelectedIndex();
            renameGame();
        }
    }//GEN-LAST:event_jButtonRenameActionPerformed
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRename;
    private javax.swing.JLabel jLabelGameTitle1;
    private javax.swing.JLabel jLabelGameTitle2;
    private javax.swing.JList<String> jListGameList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldGameNewTitle;
    private javax.swing.JTextField jTextFieldGameOldTitle;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}