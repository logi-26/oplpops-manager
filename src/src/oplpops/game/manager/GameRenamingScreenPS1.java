package oplpops.game.manager;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class GameRenamingScreenPS1 extends javax.swing.JDialog {

    private static final String[] REGION_CODES = {"SCES_","SLES_","SCUS_","SLUS_","SLPS_","SCAJ_","SLKA_","SLPM_","SCPS_"};
    private static List<File> invalidGameListPS1;
    private static List<Game> gameList;


    public GameRenamingScreenPS1(java.awt.Frame parent, boolean modal, List<File> invalidGameList) {
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
        invalidGameListPS1 = new ArrayList<>(invalidGameList);

        invalidGameListPS1.stream().forEach((vcdFile) -> {
            String gameID = null;

            // Try and get the game ID from the VCD
            try {gameID = getPS1GameIDFromVCD(vcdFile);} 
            catch (Exception ex) {PopsGameManager.displayErrorMessageDebug("Error reading the PS1 game ID from the VCD file!\n\n" + ex.toString());}

            // Split the string to get the game name without the directory path or file extension
            String gamePath = vcdFile.toString();
            String gameName = gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).substring(0,gamePath.substring(gamePath.lastIndexOf(File.separator) + 1).lastIndexOf('.'));
            
            gameList.add(new Game(gameName, gameID, vcdFile.toString(), PopsGameManager.bytesToHuman(vcdFile.length()), vcdFile.length()));
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
                File originalFile = invalidGameListPS1.get(jListGameList.getSelectedIndex());
                jTextFieldGameOldTitleDisplay.setText(originalFile.getName());
                jTextFieldGameNewTitleDisplay.setText(gameList.get(jListGameList.getSelectedIndex()).getGameName() + "-" + gameList.get(jListGameList.getSelectedIndex()).getGameID() + ".VCD");
                jTextFieldElfNameDisplay.setText(PopsGameManager.getFilePrefix() + gameList.get(jListGameList.getSelectedIndex()).getGameName() + "-" + gameList.get(jListGameList.getSelectedIndex()).getGameID() + ".ELF");
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
        jTextFieldElfNameDisplay.setText("");
    }
    
    
    // This searches the VCD file for the games unique identifier string
    public static String getPS1GameIDFromVCD(File vcdfile) throws Exception {

        String theGameID;
        try (FileReader fileReader = new FileReader(vcdfile); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            Boolean gameIDFound = false;
            theGameID = null;
            
            // Check each line for the games region code and unique ID
            while((line = bufferedReader.readLine()) != null && !gameIDFound){
                for (String code:REGION_CODES) if (line.contains(code)) {
                    gameIDFound = true;
                    String[] parts = line.split("_");
                    String regionCode = parts[0].substring(parts[0].length() - 4);
                    String idNumber = truncate(parts[1], 6);
                    
                    // If the game ID does not contain a decimal
                    if (!idNumber.contains(".")){
                        idNumber = truncate(idNumber, 5);                                   // Remove empty char at the end of the string
                        String afterDecimal = idNumber.substring(idNumber.length() - 2);    // Get the last 2 digits
                        idNumber = truncate(idNumber, 3);                                   // Get the first 3 digits
                        idNumber += "." + afterDecimal;                                     // Place a decimal between the digits
                    }
                    theGameID = regionCode + "_" + idNumber;
                }
            }          
        }
        
        return theGameID;
    }
    
    
    // This truncates a string
    private static String truncate(String value, int length) {if (value.length() > length) return value.substring(0, length); else return value;}
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jLabelGameTitle3 = new javax.swing.JLabel();
        jTextFieldElfNameDisplay = new javax.swing.JTextField();
        jButtonRenameAll = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

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

        jLabelGameTitle3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelGameTitle3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelGameTitle3.setText("ELF File:");
        jLabelGameTitle3.setToolTipText("");

        jTextFieldElfNameDisplay.setPreferredSize(new java.awt.Dimension(402, 20));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelGameTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldGameNewTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelGameTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldGameOldTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonRename, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelGameTitle3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldElfNameDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameTitle3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldElfNameDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                            .addComponent(jPanelGameInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
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

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events"> 
    private void jButtonRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRenameActionPerformed

        // Do nothing if the text field is empty
        String newName = jTextFieldGameNewTitleDisplay.getText();
        String newElfName = jTextFieldElfNameDisplay.getText();
        if (!newName.equals("")){

            if (newName.length() > 4) {
                String fileExtension = newName.substring(newName.length() - 4);

                if (fileExtension.equals(".vcd") || fileExtension.equals(".VCD")){

                    File originalFile = invalidGameListPS1.get(jListGameList.getSelectedIndex());
                    String originalPath = originalFile.getAbsolutePath().replace(originalFile.getName(), "");
                    String newPath = originalPath + newName;

                    // If a file with the same name does not already exist, rename the file
                    File newFile = new File(newPath);
                    if (!newFile.exists()){

                        if (!originalFile.renameTo(newFile)) {JOptionPane.showMessageDialog (null, "The file could not be renamed!"," Problem Renaming File",JOptionPane.ERROR_MESSAGE);}
                        else {
                            invalidGameListPS1.remove(originalFile);
                            GameListManager.addToGameListsPS1(newFile);
                            createGameLists(invalidGameListPS1);
                            clearGameDetails();
                        }
                        
                        // Delete any associated ELF file
                        File elfFile = new File(originalFile.getAbsolutePath().substring(0, originalFile.getAbsolutePath().lastIndexOf(File.separator)+1) + PopsGameManager.getFilePrefix()  + originalFile.getName().substring(0, originalFile.getName().length()-4) + ".ELF");
                        if (elfFile.exists() && elfFile.isFile()) {elfFile.delete();}

                        elfFile = new File(originalFile.getAbsolutePath().substring(0, originalFile.getAbsolutePath().length()-4) + ".ELF");
                        if (elfFile.exists() && elfFile.isFile()) {elfFile.delete();}
                        
                        elfFile = new File(originalFile.getAbsolutePath().substring(0, originalFile.getAbsolutePath().lastIndexOf(File.separator)+1) + PopsGameManager.getFilePrefix()  + newName.substring(0, newName.length()-4) + ".ELF");
                        if (elfFile.exists() && elfFile.isFile()) {elfFile.delete();}

                        elfFile = new File(originalFile.getAbsolutePath().substring(0, originalFile.getAbsolutePath().lastIndexOf(File.separator)+1) + newName.substring(0, newName.length()-4) + ".ELF");
                        if (elfFile.exists() && elfFile.isFile()) {elfFile.delete();}
                        
                        // Generate a new ELF file
                        AddGameManager.generateElf(newElfName, originalFile.getAbsolutePath().substring(0, originalFile.getAbsolutePath().lastIndexOf(File.separator)+1)); 
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
        List<File> stillInvalidGameListPS1 = new ArrayList<>();

        // Attempt to rename each game in the list using the games current name and the games ID from the VCD file
        invalidGameListPS1.stream().forEach((vcdFile) -> {

            // Try and get the game ID from the VCD
            String gameID = null;
            try {gameID = getPS1GameIDFromVCD(vcdFile);} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug("Error reading the PS1 game ID from the VCD file!\n\n" + ex.toString());}

            String originalVCDPath = vcdFile.getAbsolutePath().replace(vcdFile.getName(), "");
            String newVCDPath = originalVCDPath + vcdFile.getName().substring(0, vcdFile.getName().length()-4) + "-" + gameID + ".VCD";

            // If a file with the same name does not already exist, rename the file
            File newVCDFile = new File(newVCDPath);
            
            if (!newVCDFile.exists()) {
                boolean success = vcdFile.renameTo(newVCDFile);
                if (success) {GameListManager.addToGameListsPS1(newVCDFile);} else {stillInvalidGameListPS1.add(vcdFile);}
            }
            
            // Delete any associated ELF file
            File elfFile = new File(newVCDFile.getAbsolutePath().substring(0, newVCDFile.getAbsolutePath().lastIndexOf(File.separator)+1) + PopsGameManager.getFilePrefix() + newVCDFile.getName().substring(0, newVCDFile.getName().length()-4) + ".ELF");
            if (elfFile.exists() && elfFile.isFile()) {elfFile.delete();}

            elfFile = new File(newVCDFile.getAbsolutePath().substring(0, newVCDFile.getAbsolutePath().length()-4) + ".ELF");
            if (elfFile.exists() && elfFile.isFile()) {elfFile.delete();}

            // Generate a new ELF file
            AddGameManager.generateElf(PopsGameManager.getFilePrefix() + vcdFile.getName().substring(0, vcdFile.getName().length()-4) + "-" + gameID + ".ELF", newVCDFile.getParent() + File.separator); 
        });

        if (!stillInvalidGameListPS1.isEmpty()) {invalidGameListPS1 = stillInvalidGameListPS1;}
        else invalidGameListPS1 = new ArrayList<>();

        createGameLists(invalidGameListPS1);
        clearGameDetails();

        if (!invalidGameListPS1.isEmpty()) {JOptionPane.showMessageDialog (null, "There was a problem whilst attempting to rename some of the games!"," Problem Renaming Games",JOptionPane.WARNING_MESSAGE);}
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
    private javax.swing.JLabel jLabelGameTitle3;
    private javax.swing.JList<String> jListGameList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelGameInformation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldElfNameDisplay;
    private javax.swing.JTextField jTextFieldGameIDDisplay;
    private javax.swing.JTextField jTextFieldGameNewTitleDisplay;
    private javax.swing.JTextField jTextFieldGameNumberDisplay;
    private javax.swing.JTextField jTextFieldGameOldTitleDisplay;
    private javax.swing.JTextField jTextFieldGameSizeDisplay;
    private javax.swing.JTextField jTextFieldGameTitleDisplay;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>  
}