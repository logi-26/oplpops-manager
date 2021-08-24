package oplpops.game.manager;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GameCheatScreen extends javax.swing.JDialog {

    private List<String> clipBoardCheats = new ArrayList<>();
    private static List<Game> gameList;
    private static List<String> serverCheatList;
    private int currentListIndex; 
    
    String[] igrArray = {
        "IGR - In Game Reset Codes",
        "NOIGR - (L1+L2+R1+R2+Select+Start) - Disable IGR Menu",
        "IGR0 - (L1+L2+R1+R2+X+DPad-Down) - IGR Menu",
        "IGR1 - (Select+Start) - IGR Menu",
        "IGR2 - (L1+L2+R1+R2+Select+Start) - IGR Menu",
        "IGR3 - (L1+L2+R1+R2+X+DPad-Down) - No IGR Menu",
        "IGR4 - (Select+Start) - No IGR Menu",
        "IGR5 - (L1+L2+R1+R2+Select+Start) - No IGR Menu"
    };
    
    
    public GameCheatScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        overideClose();
    }
    
    
    // Initialise the GUI elements
    public void initialisGUI(int gameIndex){
        currentListIndex = gameIndex;
        getGameLists();
        displayGameNumber();
        displayGameName();
        loadCheatContent();
        getServerCheatList();
        getCheatsFromServer();
        this.setTitle(" Manage Game Cheats");
        
        // Remove the button margins to enable smaller buttons
        jButtonSaveCheatFile.setMargin(new Insets(0,0,0,0));
        jButtonClear.setMargin(new Insets(0,0,0,0));
        jButtonDelete.setMargin(new Insets(0,0,0,0));
        
        // Mouse listener for the server cheat list
        jTextPaneCheatList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {}
            @Override
            public void mouseReleased(MouseEvent evt){
                
                // If left mouse button is released, this copies the highlighted text to the clipboard cheat list
                if (SwingUtilities.isLeftMouseButton(evt)){
                    if (jTextPaneCheatList.getSelectedText()!= null){
                        clipBoardCheats.clear();
                        String[] lines = jTextPaneCheatList.getSelectedText().split("\n");
                        
                        if (PopsGameManager.getCurrentConsole().equals("PS1")){
                            for (String cheat : lines){
                                
                                // Check if it is an IGR code
                                boolean igrCode = false;
                                for (String igr : igrArray){if (cheat.equals(igr) && !cheat.equals("IGR - In Game Reset Codes")) {igrCode = true;}}
                                
                                
                                if (igrCode) {
                                    if (cheat.substring(0, 1).equals("N")) {clipBoardCheats.add("$" + cheat.substring(0, 5));}
                                    else {clipBoardCheats.add("$" + cheat.substring(0, 4));}
                                }
                                else if (cheat.equals("IGR - In Game Reset Codes")) {clipBoardCheats.add(cheat);}
                                else if (checkIfCheatTitle(cheat)) {clipBoardCheats.add(cheat);}
                                else{clipBoardCheats.add("$" + cheat);}
                            }  
                        } 
                        else {
                            for (String cheat : lines){clipBoardCheats.add(cheat);}
                        }
                    }
                }
                
                // If right mouse button is released, this copies the clipboard cheat list into the local cheat composer window
                if (SwingUtilities.isRightMouseButton(evt)){if (clipBoardCheats.size() > 0){clipBoardCheats.forEach((cheat) -> {jTextAreaCheatContent.setText(jTextAreaCheatContent.getText() + cheat + "\n");});}}   
            }
        });
    }
    
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                compareCheatFile();
                disposeDialog();
            }
        });
    }
    
    
    // This disposes the dialog form and callsback to update the game list in the main GUI
    private void disposeDialog(){
        PopsGameManager.callbackToUpdateGUIGameList(null, currentListIndex);
        dispose();
    }
    
    
    // Get the cheat list from the server
    private void getServerCheatList(){
        
        // Download the cheat file list from the server
        serverCheatList = new ArrayList<>();
        MyTCPClient tcpClient = new MyTCPClient();
        tcpClient.getListFromServer("CHEAT", PopsGameManager.getCurrentConsole());

        File cheatListFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "lists" + File.separator + PopsGameManager.getCurrentConsole() + "_ServerCheatList.dat");
        
        // If the cheat file list is available, read the file and store the game ID's in a list
        if(cheatListFile.exists() && !cheatListFile.isDirectory()) {try (Stream<String> lines = Files.lines(cheatListFile.toPath(), Charset.defaultCharset())) {lines.forEachOrdered(line -> serverCheatList.add(line));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error reading the cheat list file!\n\n" + ex.toString());}}
    }
    
 
    // This displays the game name in the GUI
    private void displayGameName(){jTextFieldGameName.setText(gameList.get(currentListIndex).getGameName() + "  :  " + gameList.get(currentListIndex).getGameID());}
    
    
    // This displays the game ID in the GUI
    private void displayGameNumber(){jTextFieldGameNumber.setText("[" + (currentListIndex+1) + "/" + gameList.size() + "]");}
    
    
    // This loads the cheat data from the cheat file
    private void loadCheatContent(){
        
        jTextAreaCheatContent.setText("");
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")){
        
            String fullPath = PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + File.separator + "CHEATS.TXT";
            List<String> fileContentList = new ArrayList<>();

            if(new File(fullPath).exists() && !new File(fullPath).isDirectory()) { 
                try {
                    try (BufferedReader inFile = new BufferedReader(new FileReader(fullPath))) {
                        String line;
                        while ((line = inFile.readLine()) != null){fileContentList.add(line);}   
                    }
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                // Load the content from the CHEAT.TXT into the text area
                if (!fileContentList.isEmpty()) {
                    jTextAreaCheatContent.setText("");
                    fileContentList.stream().forEach((line) -> {jTextAreaCheatContent.setText(jTextAreaCheatContent.getText() + line + "\n");});
                }
            }
            //else {jTextAreaCheatContent.setText("\"" + gameList.get(currentListIndex).getGameName() + " /ID " + gameList.get(currentListIndex).getGameID() + "\"\n" + "POPS Enable Code\n");}
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){
        
            String fullPath = PopsGameManager.getOPLFolder() + File.separator + "CHT" + File.separator + gameList.get(currentListIndex).getGameID() + ".cht";
            List<String> fileContentList = new ArrayList<>();

            File cheatFile = new File(fullPath);
            if(cheatFile.exists() && !cheatFile.isDirectory()) { 
                try {
                    try (BufferedReader inFile = new BufferedReader(new FileReader(fullPath))) {
                        String line;
                        while ((line = inFile.readLine()) != null){fileContentList.add(line);}   
                    }
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                // Load the content from the .cht file into the text area
                if (!fileContentList.isEmpty()) {
                    jTextAreaCheatContent.setText("");
                    fileContentList.stream().forEach((line) -> {jTextAreaCheatContent.setText(jTextAreaCheatContent.getText() + line + "\n");});
                }
            }
            else {jTextAreaCheatContent.setText("\"" + gameList.get(currentListIndex).getGameName() + " /ID " + gameList.get(currentListIndex).getGameID() + "\"\n" + "Mastercode\n");}
        } 
    }
    
    
    // This saves the cheat file
    private void saveCheatFile(){
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")){

            File path = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID());
            File cheatFile = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + File.separator + "CHEATS.TXT");

            // Create the directory and file if they do not already exist
            if (path.exists() && path.isDirectory()) {if (!cheatFile.exists()) {try {cheatFile.createNewFile();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}}
            else{
                cheatFile.getParentFile().mkdirs();
                if (!cheatFile.exists()) {try {cheatFile.createNewFile();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
            }

            // Write the data from the text area to the file
            try {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(cheatFile))) {
                    bufferedWriter.write(jTextAreaCheatContent.getText());
                    bufferedWriter.flush();
                }
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){

            // Create the PS2 cheat file if it does not already exist
            File cheatFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CHT" + File.separator + gameList.get(currentListIndex).getGameID() + ".cht");
            if (!cheatFile.exists() || cheatFile.isDirectory()) {try {cheatFile.createNewFile();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
            
            // Write the data from the text area to the file
            try {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(cheatFile))) {
                    bufferedWriter.write(jTextAreaCheatContent.getText());
                    bufferedWriter.flush();
                }
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
        }
    }
        
    
    // This compares the current cheat data from the text area with the data in the cheat file
    private void compareCheatFile(){

        File cheatFile = null;
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")){cheatFile = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + File.separator + "CHEATS.TXT");}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){cheatFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CHT" + File.separator + gameList.get(currentListIndex).getGameID() + ".cht");}
        
        if (cheatFile != null){

            // Check if a cheat file already exists
            if (cheatFile.exists() && !cheatFile.isDirectory()) {

                ArrayList<String> currentCheatFile = new ArrayList<>();
                ArrayList<String> newCheatData = new ArrayList<>();
                Scanner scanner = null;
                try {scanner = new Scanner(cheatFile);} catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                // Read the contents of the cheat file into a list
                if (scanner != null){
                    while (scanner.hasNextLine()){currentCheatFile.add(scanner.nextLine());}
                    scanner.close();  
                }

                // Read the contents of the text area into a list
                newCheatData.addAll(Arrays.asList(jTextAreaCheatContent.getText().split("\\n")));

                // If the cheat data has changed, ask the user if they want to save the file
                if (!currentCheatFile.equals(newCheatData)){
                    int dialogResult = JOptionPane.showConfirmDialog(this, "The cheat data has changed, do you want to save the changes?", " Cheat Data Changed", JOptionPane.YES_NO_OPTION);
                    if(dialogResult == 0) {saveCheatFile();} 
                }
            }
            else {
                
                // If a cheat file does not exist, ask the user if they want to create one
                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                    //if (!jTextAreaCheatContent.getText().equals("\"" + gameList.get(currentListIndex).getGameName() + " /ID " + gameList.get(currentListIndex).getGameID() + "\"\n" + "POPS Enable Code\n")){
                    if (!jTextAreaCheatContent.getText().equals("")){   
                        // Ask the user if they want to create the cheat file
                        int dialogResult = JOptionPane.showConfirmDialog(this, "The cheat data has changed, do you want to create the cheat file?", " Cheat Data Changed", JOptionPane.YES_NO_OPTION);
                        if(dialogResult == 0) { saveCheatFile();} 
                    }
                }
                else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                    if (!jTextAreaCheatContent.getText().equals("\"" + gameList.get(currentListIndex).getGameName() + " /ID " + gameList.get(currentListIndex).getGameID() + "\"\n" + "Mastercode\n")){
                        // Ask the user if they want to create the cheat file
                        int dialogResult = JOptionPane.showConfirmDialog(this, "The cheat data has changed, do you want to create the cheat file?", " Cheat Data Changed", JOptionPane.YES_NO_OPTION);
                        if(dialogResult == 0) { saveCheatFile();} 
                    }
                }  
            }
        }
    }
    
    
    // This loads the game lists
    private void getGameLists(){
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {gameList = new ArrayList<>(GameListManager.getGameListPS1());}    
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {gameList = new ArrayList<>(GameListManager.getGameListPS2());}  
    }
    
    
    // Try and get the cheats from the server if they exist
    private void getCheatsFromServer(){

        List<String> splitCheatsList = new ArrayList<>();
        
        // If the game ID is in the server cheat list, this sends a message to the server in order to request the specific cheat file
        if (!serverCheatList.isEmpty() && serverCheatList.contains(gameList.get(currentListIndex).getGameID())){
            
            MyTCPClient tcpClient = new MyTCPClient();
            String splitCheats[] = null;
            List<String> cheatList = new ArrayList<>();
            String[] splitName = gameList.get(currentListIndex).getGameID().split("_");       
            String cheat = tcpClient.getCheatFromServer(PopsGameManager.determineGameRegion(splitName[0]), gameList.get(currentListIndex).getGameID());
            jTextPaneCheatList.setText("");
            
            if (cheat != null && !cheat.equals("NO_CHEAT")) {

                // Split the cheat string using newLine as the deliminator
                splitCheats = cheat.split("\\r?\\n");

                // If a line contains more than 3 chars it is added to the list
                for (String splitCheat : splitCheats) if (splitCheat.length() > 3) cheatList.add(splitCheat);

                // Copy the list back to an array, then use the array as the jList model
                splitCheats = cheatList.stream().toArray(String[]::new);
                
                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                    
                    for (String arrayItem : igrArray){splitCheatsList.add(arrayItem);}
                    
                    // Check for PS1 widescreen codes
                    InputStream in = GameListManager.class.getResourceAsStream("/oplpops/game/manager/WidescreenListPS1.txt"); 
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    try {
                        while ((line = bufferedReader.readLine()) != null) {

                            if(line.substring(1, line.length()).equals(gameList.get(currentListIndex).getGameID())){
                                splitCheatsList.add("Widescreen 16:9");
                                
                                boolean endOfCode = false;
                                while((line = bufferedReader.readLine()) != null && !endOfCode){
                                    if (!line.substring(0, 1).equals("*")) {splitCheatsList.add(line);} else {endOfCode = true;}
                                }
                            }
                        }
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    
                }
                
                
                // Check for PS2 widescreen codes
                if (PopsGameManager.getCurrentConsole().equals("PS2")){
                    InputStream in = GameListManager.class.getResourceAsStream("/oplpops/game/manager/WidescreenListPS2.txt"); 
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    try {
                        while ((line = bufferedReader.readLine()) != null) {

                            if(line.substring(1, line.length()).equals(gameList.get(currentListIndex).getGameID())){
                                splitCheatsList.add("Widescreen 16:9");
                                
                                boolean endOfCode = false;
                                while((line = bufferedReader.readLine()) != null && !endOfCode){
                                    if (!line.substring(0, 1).equals("*")) {splitCheatsList.add(line);} else {endOfCode = true;}
                                }
                            }
                        }
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                }
                
                
                for (String cht:splitCheats){splitCheatsList.add(cht);}
                
                jTextPaneCheatList.setContentType("text/html");
                StyledDocument doc = jTextPaneCheatList.getStyledDocument();
                
                // Define a colour attribute (Blue) - IGR code title
                SimpleAttributeSet colourBlue = new SimpleAttributeSet();
                StyleConstants.setForeground(colourBlue, new Color(38,120,190));
                StyleConstants.setBold(colourBlue, true);
                
                // Define a colour attribute (Orange) - Cheat code title
                SimpleAttributeSet colourOrange = new SimpleAttributeSet();
                StyleConstants.setForeground(colourOrange, new Color(226,149,15));
                StyleConstants.setBold(colourOrange, true);

                // Define a colour attribute (Green) - Widescreen code title
                SimpleAttributeSet colourGreen = new SimpleAttributeSet();
                StyleConstants.setForeground(colourGreen, new Color(55,170,20));
                StyleConstants.setBold(colourGreen, true);
                
                // Define a colour attribute (Red) - Enable code title
                SimpleAttributeSet colourRed = new SimpleAttributeSet();
                StyleConstants.setForeground(colourRed, new Color(190,35,25));
                StyleConstants.setBold(colourRed, true);
                
                // This removes the PS2 cheat key (not much use in displaying the cheat key)
                if (PopsGameManager.getCurrentConsole().equals("PS2")){
                    for (int i = 0; i < splitCheatsList.size(); i++){
                        if (splitCheatsList.get(i).length() > 3){
                            if (splitCheatsList.get(i).substring(0, 4).equals("Key:") || splitCheatsList.get(i).substring(0, 4).equals("key:")){splitCheatsList.remove(i);}
                        }
                        
                        if (splitCheatsList.get(i).length() > 6){
                            if (splitCheatsList.get(i).substring(0, 7).equals("Disc 1:")){splitCheatsList.remove(i);}
                            if (splitCheatsList.get(i).substring(0, 7).equals("Disc 2:")){splitCheatsList.remove(i);}
                        }
                    }
                }
                        
                // Add the formatted text to the text pane
                try {
                    for (String code:splitCheatsList){
                        
             
                            
                            
                            if (PopsGameManager.getCurrentConsole().equals("PS1") && code.equals(splitCheatsList.get(0))){doc.insertString(0, code + "\n", colourBlue);}
                            else {
                                if (code.equals("Widescreen 16:9")){doc.insertString(doc.getLength(), code + "\n", colourGreen);}
                                else if (code.equals("Enable Code") || code.equals("POPS Enable Code")){
                                    
                                    if (splitCheatsList.contains("Widescreen 16:9") || splitCheatsList.contains("IGR - In Game Reset Codes")) {doc.insertString(doc.getLength(), "\n" + code + "\n", colourRed);} else {doc.insertString(doc.getLength(), code + "\n", colourRed);}
                                    
                                    
                                    
                                    
                                }
                                else if (checkIfCheatTitle(code)) {
                                    if (code.equals(splitCheatsList.get(0))) {doc.insertString(doc.getLength(), code + "\n", colourOrange);}
                                    else {doc.insertString(doc.getLength(), "\n" + code + "\n", colourOrange);}
                                }
                                else {doc.insertString(doc.getLength(), code + "\n", null);} 
                            }
                            
                            
                 
                        
                        
                        
                        
                    }
                }
                catch(BadLocationException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            }
        }
        else {
            
            if (PopsGameManager.getCurrentConsole().equals("PS1")){
                
                jTextPaneCheatList.setText("");

                for (String arrayItem : igrArray){splitCheatsList.add(arrayItem);}
                
                jTextPaneCheatList.setContentType("text/html");
                StyledDocument doc = jTextPaneCheatList.getStyledDocument();
                
                // Define a colour attribute (Blue)
                SimpleAttributeSet colourBlue = new SimpleAttributeSet();
                StyleConstants.setForeground(colourBlue, new Color(38,120,190));
                StyleConstants.setBold(colourBlue, true);
                
                try {
                    for (String code:splitCheatsList){
                        if (code.equals(splitCheatsList.get(0))){doc.insertString(doc.getLength(), code + "\n", colourBlue);}
                        else{doc.insertString(doc.getLength(), code + "\n", null);}
                    }
                }catch(BadLocationException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            }
            else{jTextPaneCheatList.setText("");} 
        }
        
        jTextPaneCheatList.setCaretPosition(0);
    }
    
    
    
    
    // Determine if the selected list item is a cheat string
    private boolean checkIfCheatTitle(String text){
        
        boolean cheatTitle = false;

        // Count the number of chars and digits in the string
        int numOfChars = 0;
        int numOfNumbers = 0;
        for(char character : text.toCharArray()){if(Character.isDigit(character)) {numOfNumbers +=1;}else {numOfChars +=1;}}

        // Algorithm to determine if the string is a cheat code or a cheat code title
        if (numOfChars > numOfNumbers && Character.isLetter(text.charAt(0))) {cheatTitle = true;}
        else if (text.length() > 9 && numOfChars > numOfNumbers && !text.substring(8, 9).equals(" ")) {cheatTitle = true;}
        else if (numOfChars > numOfNumbers && countWhiteSpace(text) > 1) {cheatTitle = true;}
        
        if (text.length() == 17 && text.substring(8, 9).equals(" ") && countWhiteSpace(text) == 1 && text.matches(".*\\d.*")){cheatTitle = false;}

        for (String arrayItem : igrArray){if (text.equals(arrayItem)) {cheatTitle = false;}}
        
        return cheatTitle;
    }
    

    
    
    
    
    
    // This counts the white spaces in a string
    private int countWhiteSpace(String word){
        
        int i = 0;
        int spaceCount = 0;

        while(i < word.length()){
          if( word.charAt(i) == ' ' ) {spaceCount++;}
          i++;
        }
        
        return spaceCount;
    }
    
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanelCreateCheat = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaCheatContent = new javax.swing.JTextArea();
        jButtonClear = new javax.swing.JButton();
        jButtonSaveCheatFile = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPaneCheatList = new javax.swing.JScrollPane();
        jTextPaneCheatList = new javax.swing.JTextPane();
        jTextFieldGameNumber = new javax.swing.JTextField();
        jTextFieldGameName = new javax.swing.JTextField();
        jButtonPreviousGame = new javax.swing.JButton();
        jButtonNextGame = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cheat File Creator"));

        jPanelCreateCheat.setBorder(javax.swing.BorderFactory.createTitledBorder("Cheat File"));

        jTextAreaCheatContent.setColumns(2);
        jTextAreaCheatContent.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jTextAreaCheatContent.setRows(50);
        jScrollPane1.setViewportView(jTextAreaCheatContent);

        jButtonClear.setText("Undo");
        jButtonClear.setToolTipText("");
        jButtonClear.setPreferredSize(new java.awt.Dimension(80, 23));
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jButtonSaveCheatFile.setText("Save");
        jButtonSaveCheatFile.setToolTipText("");
        jButtonSaveCheatFile.setPreferredSize(new java.awt.Dimension(80, 23));
        jButtonSaveCheatFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveCheatFileActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.setToolTipText("");
        jButtonDelete.setPreferredSize(new java.awt.Dimension(80, 23));
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCreateCheatLayout = new javax.swing.GroupLayout(jPanelCreateCheat);
        jPanelCreateCheat.setLayout(jPanelCreateCheatLayout);
        jPanelCreateCheatLayout.setHorizontalGroup(
            jPanelCreateCheatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCreateCheatLayout.createSequentialGroup()
                .addGroup(jPanelCreateCheatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCreateCheatLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelCreateCheatLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jButtonSaveCheatFile, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelCreateCheatLayout.setVerticalGroup(
            jPanelCreateCheatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCreateCheatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelCreateCheatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSaveCheatFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Cheats From Server"));
        jPanel3.setPreferredSize(new java.awt.Dimension(380, 451));

        jTextPaneCheatList.setEditable(false);
        jScrollPaneCheatList.setViewportView(jTextPaneCheatList);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneCheatList, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneCheatList, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextFieldGameNumber.setEditable(false);
        jTextFieldGameNumber.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextFieldGameNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameNumber.setText("[1/12]");
        jTextFieldGameNumber.setBorder(null);

        jTextFieldGameName.setEditable(false);
        jTextFieldGameName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextFieldGameName.setText("Game name - Game ID");
        jTextFieldGameName.setBorder(null);

        jButtonPreviousGame.setText("<<");
        jButtonPreviousGame.setToolTipText("");
        jButtonPreviousGame.setPreferredSize(new java.awt.Dimension(80, 23));
        jButtonPreviousGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousGameActionPerformed(evt);
            }
        });

        jButtonNextGame.setText(">>");
        jButtonNextGame.setToolTipText("");
        jButtonNextGame.setPreferredSize(new java.awt.Dimension(80, 23));
        jButtonNextGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextGameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldGameNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGameName)
                        .addGap(40, 40, 40)
                        .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanelCreateCheat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldGameName)
                        .addComponent(jTextFieldGameNumber)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelCreateCheat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">  
    private void jButtonPreviousGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousGameActionPerformed

        compareCheatFile();
        if (currentListIndex > 0) {
            currentListIndex -=1;
            displayGameName();
            displayGameNumber();
            loadCheatContent();
            getCheatsFromServer();
        }
    }//GEN-LAST:event_jButtonPreviousGameActionPerformed

    private void jButtonNextGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextGameActionPerformed

        compareCheatFile();
        if (currentListIndex < gameList.size()-1) {
            currentListIndex +=1;
            displayGameName();
            displayGameNumber();
            loadCheatContent();
            getCheatsFromServer();
        }
    }//GEN-LAST:event_jButtonNextGameActionPerformed

    private void jButtonSaveCheatFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveCheatFileActionPerformed
        if (!jTextAreaCheatContent.getText().equals("")) {saveCheatFile();} 
    }//GEN-LAST:event_jButtonSaveCheatFileActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        loadCheatContent();
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        
        File cheatFile = null;
        if (PopsGameManager.getCurrentConsole().equals("PS1")){cheatFile = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameList.get(currentListIndex).getGameName() + "-" + gameList.get(currentListIndex).getGameID() + File.separator + "CHEATS.TXT");}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){cheatFile = new File(PopsGameManager.getOPLFolder() + File.separator + "CHT" + File.separator + gameList.get(currentListIndex).getGameID() + ".cht");}

        if (cheatFile != null && cheatFile.exists() && cheatFile.isFile()) {
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete this cheat file?"," Delete Cheat File!",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){cheatFile.delete();}

            if (PopsGameManager.getCurrentConsole().equals("PS1")){jTextAreaCheatContent.setText("");}
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){jTextAreaCheatContent.setText("\"" + gameList.get(currentListIndex).getGameName() + " /ID " + gameList.get(currentListIndex).getGameID() + "\"\n" + "Mastercode\n");}
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Variables">  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonNextGame;
    private javax.swing.JButton jButtonPreviousGame;
    private javax.swing.JButton jButtonSaveCheatFile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelCreateCheat;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneCheatList;
    private javax.swing.JTextArea jTextAreaCheatContent;
    private javax.swing.JTextField jTextFieldGameName;
    private javax.swing.JTextField jTextFieldGameNumber;
    private javax.swing.JTextPane jTextPaneCheatList;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}