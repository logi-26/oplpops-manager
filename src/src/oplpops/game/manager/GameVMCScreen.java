package oplpops.game.manager;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class GameVMCScreen extends javax.swing.JDialog {

    private ArrayList<String> serverVMCList = new ArrayList<>();
    private ArrayList<String> serverVMCDescriptionList = new ArrayList<>();
    private ArrayList<String> gameVMCList = new ArrayList<>();
    
    public GameVMCScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        overideClose();
    }
    
    
    // Initialise the GUI elements
    public void initialisGUI(int gameIndex){
        this.setTitle(" Download Game VMC Files");
        
        // Remove the button margins to enable smaller buttons
        jButtonDownload.setMargin(new Insets(0,0,0,0));
        
        updateList();
        jListGameList1.setSelectedIndex(gameIndex);
        jListGameList1.ensureIndexIsVisible(jListGameList1.getSelectedIndex());
        
        displayGameDetails();
        getServerVMCList();
        readVMCList();
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")){displayAvailableVMC(GameListManager.getGamePS1(jListGameList1.getSelectedIndex()).getGameID());}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){displayAvailableVMC(GameListManager.getGamePS2(jListGameList1.getSelectedIndex()).getGameID());}
    }
    
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                disposeDialog();
            }
        });
    }
    
    
    // This disposes the dialog form and callsback to update the game list in the main GUI
    private void disposeDialog(){
        PopsGameManager.callbackToUpdateGUIGameList(null, jListGameList1.getSelectedIndex());
        dispose();
    }
    
    
    // Get the cheat list from the server
    private void getServerVMCList(){
        
        // Download the cheat file list from the server
        serverVMCList = new ArrayList<>();
        MyTCPClient tcpClient = new MyTCPClient();
        tcpClient.getListFromServer("VMC", PopsGameManager.getCurrentConsole());

        File vmcListFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + PopsGameManager.getCurrentConsole() + "_ServerVMCList.dat");
        
        // If the vmc file list is available, read the file and store the game ID's in a list
        if(vmcListFile.exists() && !vmcListFile.isDirectory()) {try (Stream<String> lines = Files.lines(vmcListFile.toPath(), Charset.defaultCharset())) {lines.forEachOrdered(line -> serverVMCList.add(line));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
    }
    
    
    private void readVMCList(){

        serverVMCList.clear();
        String fileName = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + PopsGameManager.getCurrentConsole() + "_ServerVMCList.dat";
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                
                if (line.length() >= 23){
                    serverVMCList.add(line.substring(0, 21));
                    serverVMCDescriptionList.add(line.substring(22, line.length()));
                }
            }
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // This updates the game list in the MainScreen
    private void updateList(){
        
        if (PopsGameManager.isOPLFolderSet() && PopsGameManager.isCurrentConsoleSet()) {
            
            if (PopsGameManager.getCurrentConsole().equals("PS1")) {
                if (GameListManager.getGameListPS1()!= null) {

                    List<String> gameNameList = new ArrayList<>();
                    GameListManager.getGameListPS1().stream().forEach((game) -> {gameNameList.add(game.getGameName());});
                    createMainGameList(gameNameList.toArray(new String[0])); 
                }
                else {createMainGameList(new String[0]);}
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
                if (GameListManager.getGameListPS2()!= null) {
                    
                    List<String> gameNameList = new ArrayList<>();
                    GameListManager.getGameListPS2().stream().forEach((game) -> {gameNameList.add(game.getGameName());});
                    createMainGameList(gameNameList.toArray(new String[0]));  
                }
                else {createMainGameList(new String[0]);}
            }
        }
    }
    
    
    // This displays the game details in the MainScreen
    private void displayGameDetails(){

        if (jListGameList1.getSelectedIndex() != -1){
            if (PopsGameManager.getCurrentConsole().equals("PS1")){
                if (GameListManager.getGameListPS1() != null){
                    jTextFieldGameTitleDisplay.setText(" " + GameListManager.getGamePS1(jListGameList1.getSelectedIndex()).getGameName());
                    jTextFieldGameIDDisplay.setText(GameListManager.getGamePS1(jListGameList1.getSelectedIndex()).getGameID());
                    displayAvailableVMC(GameListManager.getGamePS1(jListGameList1.getSelectedIndex()).getGameID());
                }
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                if (GameListManager.getGameListPS2() != null){
                    jTextFieldGameTitleDisplay.setText(" " + GameListManager.getGamePS2(jListGameList1.getSelectedIndex()).getGameName());
                    jTextFieldGameIDDisplay.setText(GameListManager.getGamePS2(jListGameList1.getSelectedIndex()).getGameID());
                    displayAvailableVMC(GameListManager.getGamePS2(jListGameList1.getSelectedIndex()).getGameID());
                }
            }  
        }
        else {clearGameDetails();}
    }
    
    
    private void clearGameDetails(){
        jTextFieldGameTitleDisplay.setText("");
        jTextFieldGameIDDisplay.setText("");
    }
    
    
    private void displayAvailableVMC(String gameID){
        
        // Clear any items that are currently in the list
        gameVMCList.clear();
        jListVMCList.clearSelection();
        createGameVMCList(gameVMCList.toArray(new String[0]));
        
        jButtonDownload.setEnabled(false);
        jTextAreaVMCDescription.setText("");
        
        // This checks checks the cureent game ID with the list of VMCs (Should modify to detect multiple VMC's for a single game)
        for (int i = 0; i < serverVMCList.size(); i++){if (serverVMCList.get(i).substring(0, 11).equals(gameID)){gameVMCList.add(serverVMCList.get(i));}}

        // Display the list of VMC's that are available on the server
        if (!gameVMCList.isEmpty()){createGameVMCList(gameVMCList.toArray(new String[0]));}
    }
    
    
    
    
    
    private void downloadVMCFile(){
        MyTCPClient tcpClient = new MyTCPClient();
        tcpClient.getVMCFromServer(PopsGameManager.determineGameRegion(jTextFieldGameIDDisplay.getText().substring(0, 4)), jListVMCList.getSelectedValue(), jTextFieldGameTitleDisplay.getText().trim(), jTextFieldGameIDDisplay.getText());
    }
    
    
    
    
    // This creates the list model for the games list
    private void createMainGameList(String[] games){
        
        // List model
        jListGameList1.setModel(new javax.swing.AbstractListModel<String>() { 
            @Override
            public int getSize() {return games.length;}
            @Override
            public String getElementAt(int i) {return games[i];}
        });
        
        // Mouse listener
        jListGameList1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {}
            @Override
            public void mouseReleased(MouseEvent evt){if (SwingUtilities.isLeftMouseButton(evt)){displayGameDetails();}}
        });
        
        // Key listener
        jListGameList1.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {} 
        });
    }
    
    
    // This creates the list model for the individual game VMC list
    private void createGameVMCList(String[] vmcs){
        
        // List model
        jListVMCList.setModel(new javax.swing.AbstractListModel<String>() { 
            @Override
            public int getSize() {return vmcs.length;}
            @Override
            public String getElementAt(int i) {return vmcs[i];}
        });
        
        // Mouse listener
        jListVMCList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {}
            @Override
            public void mouseReleased(MouseEvent evt){if (SwingUtilities.isLeftMouseButton(evt)){
            
                for (int i = 0; i < serverVMCList.size(); i++){
                    if (serverVMCList.get(i).equals(jListVMCList.getSelectedValue())){
                        jTextAreaVMCDescription.setText(serverVMCDescriptionList.get(i));
                    }
                }
                
                // If a VMC file is selected, enable the download button
                if (jListVMCList.getSelectedIndex() != -1){jButtonDownload.setEnabled(true);}
            }}
        });
        
        // Key listener
        jListVMCList.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {} 
        });
    }
    
    
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListVMCList = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaVMCDescription = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButtonDownload = new javax.swing.JButton();
        jPanelGameList1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListGameList1 = new javax.swing.JList<>();
        jPanelGameInformation = new javax.swing.JPanel();
        jLabelGameTitle = new javax.swing.JLabel();
        jLabelGameID = new javax.swing.JLabel();
        jTextFieldGameTitleDisplay = new javax.swing.JTextField();
        jTextFieldGameIDDisplay = new javax.swing.JTextField();

        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("VMC File Downloader"));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("VMC Files on Server"));
        jPanel3.setPreferredSize(new java.awt.Dimension(380, 451));

        jScrollPane3.setViewportView(jListVMCList);

        jTextAreaVMCDescription.setEditable(false);
        jTextAreaVMCDescription.setColumns(20);
        jTextAreaVMCDescription.setRows(5);
        jScrollPane4.setViewportView(jTextAreaVMCDescription);

        jLabel1.setText("Description");

        jButtonDownload.setText("Download");
        jButtonDownload.setToolTipText("");
        jButtonDownload.setEnabled(false);
        jButtonDownload.setPreferredSize(new java.awt.Dimension(100, 23));
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonDownload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonDownload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelGameList1.setBorder(javax.swing.BorderFactory.createTitledBorder("Game List"));

        jScrollPane2.setViewportView(jListGameList1);

        javax.swing.GroupLayout jPanelGameList1Layout = new javax.swing.GroupLayout(jPanelGameList1);
        jPanelGameList1.setLayout(jPanelGameList1Layout);
        jPanelGameList1Layout.setHorizontalGroup(
            jPanelGameList1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameList1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelGameList1Layout.setVerticalGroup(
            jPanelGameList1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameList1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
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

        jTextFieldGameTitleDisplay.setEditable(false);
        jTextFieldGameTitleDisplay.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameTitleDisplay.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldGameTitleDisplay.setPreferredSize(new java.awt.Dimension(402, 20));

        jTextFieldGameIDDisplay.setEditable(false);
        jTextFieldGameIDDisplay.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameIDDisplay.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldGameIDDisplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameIDDisplay.setPreferredSize(new java.awt.Dimension(104, 20));

        javax.swing.GroupLayout jPanelGameInformationLayout = new javax.swing.GroupLayout(jPanelGameInformation);
        jPanelGameInformation.setLayout(jPanelGameInformationLayout);
        jPanelGameInformationLayout.setHorizontalGroup(
            jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                        .addComponent(jLabelGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameIDDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                        .addComponent(jLabelGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(113, 113, 113))
        );
        jPanelGameInformationLayout.setVerticalGroup(
            jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGameInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGameID, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameIDDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelGameList1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelGameInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanelGameInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE))
                    .addComponent(jPanelGameList1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events"> 
    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownloadActionPerformed
        downloadVMCFile();
    }//GEN-LAST:event_jButtonDownloadActionPerformed
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Generated Variables">     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelGameID;
    private javax.swing.JLabel jLabelGameTitle;
    private javax.swing.JList<String> jListGameList1;
    private javax.swing.JList<String> jListVMCList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelGameInformation;
    private javax.swing.JPanel jPanelGameList1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextAreaVMCDescription;
    private javax.swing.JTextField jTextFieldGameIDDisplay;
    private javax.swing.JTextField jTextFieldGameTitleDisplay;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>  
}