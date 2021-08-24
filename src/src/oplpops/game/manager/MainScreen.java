package oplpops.game.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


public final class MainScreen extends javax.swing.JFrame implements MyListener {
    
    private final GameConfigFileManager configManager;
    private static final String NO_IMAGE_PS2_COVER_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Cover PS2.png";
    private static final String NO_IMAGE_PS1_COVER_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Cover.png";
    private GameImageScreenPS1 gameImageScreenPS1;
    private GameImageScreenPS2 gameImageScreenPS2;
    private GameConfigScreen gameConfigScreen;
    private SyncFileScreen syncFileScreen;
    private GameCheatScreen cheatScreen;
    private BatchDownloadScreenPS1 batchDownloadScreenPS1;
    private BatchDownloadScreenPS2 batchDownloadScreenPS2;
    private EmulatorSettingsScreen emulatorSettingsScreen; 
    private long buttonLastReleased = 0;
    private Process processPCSX2 = null;
    private File selectedGameFolder = null;
    
    
    @Override
    // Callback function
    public void updateGameList(String gameID, int listIndex) {
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {
            switchConsole("PS1");
            jMenuItemPS1Emulator.setVisible(true);
            jMenuItemPS2Emulator.setVisible(false);
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
            switchConsole("PS2");
            jMenuItemPS1Emulator.setVisible(false);
            jMenuItemPS2Emulator.setVisible(true);
        }
        
        if (gameID == null){
            if (listIndex == -1) {listIndex = 0;}
            if (listIndex == -2) {listIndex = jListGameList.getSelectedIndex();}
            
            clearGameMainDetails();
            updateList();  

            if (PopsGameManager.getCurrentConsole().equals("PS1")){if (GameListManager.getGameListPS1() != null && !GameListManager.getGameListPS1().isEmpty()) {jListGameList.setSelectedIndex(listIndex);}}
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){if (GameListManager.getGameListPS2() != null && !GameListManager.getGameListPS2().isEmpty()) {jListGameList.setSelectedIndex(listIndex);}}
        }
        else {
            if (PopsGameManager.getCurrentConsole().equals("PS1")){if (GameListManager.getGameListPS1() != null) { 
                    for (int i = 0; i < GameListManager.getGameListPS1().size(); i++){if (GameListManager.getGameListPS1().get(i).getGameID().equals(gameID)){jListGameList.setSelectedIndex(i);}}
                }
            } 
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){if (GameListManager.getGameListPS2() != null) {
                    for (int i = 0; i < GameListManager.getGameListPS2().size(); i++){if (GameListManager.getGameListPS2().get(i).getGameID().equals(gameID)){jListGameList.setSelectedIndex(i);}}
                }
            }
        }
        
        updateGameStats();
        updateMainLabel();
        updateMenuItems();
        displayGameDetails();
        jListGameList.ensureIndexIsVisible(jListGameList.getSelectedIndex());
        
        try {
            if (PopsGameManager.getCurrentConsole().equals("PS1")){if (GameListManager.getGameListPS1() != null && GameListManager.getGameListPS1().size() > 0){displayGameConfigDetails(configManager.readGameConfigFormatted(GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID() ,GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName()));}}
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){if (GameListManager.getGameListPS2() != null && GameListManager.getGameListPS2().size() > 0){displayGameConfigDetails(configManager.readGameConfigFormatted(GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID(),GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameName()));}} 
        } 
        catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
    }
    
    
    public MainScreen() {
        configManager =  new GameConfigFileManager();
        initComponents();
        PopsGameManager.addListener(this);
        jCheckBoxMenuPS1Compatability.setSelected(PopsGameManager.getGameCompatabilityPS1());
        jCheckBoxMenuItemPS2ULCFG.setSelected(PopsGameManager.getSplitGameDisplayPS2());
        jMenuItemReportCompatability.setVisible(false);
        initialiseGUI(0);
    }
    
    
    // Initialise the GUI elements
    private void initialiseGUI(int listIndex){
        
        this.setTitle(PopsGameManager.getFormTitle());

        if (PopsGameManager.getFisrtLaunch()){
            SetModeScreen setModeScreen = new SetModeScreen(this, true);
            setModeScreen.initialiseGUI();
            setModeScreen.setLocationRelativeTo(this);
            setModeScreen.setVisible(true);
        }
        else {
            try {
                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                    jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS1_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));
                    jRadioButtonMenuItemPlaystation1.setSelected(true);
                    jRadioButtonMenuItemPlaystation2.setSelected(false);
                    jMenuItemPS1Emulator.setVisible(true);
                    jMenuItemPS2Emulator.setVisible(false);
                    updateCoverLayoutPS1();
                }
                else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                    jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS2_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));
                    jRadioButtonMenuItemPlaystation1.setSelected(false);
                    jRadioButtonMenuItemPlaystation2.setSelected(true);
                    jMenuItemPS1Emulator.setVisible(false);
                    jMenuItemPS2Emulator.setVisible(true);
                    updateCoverLayoutPS2();
                }  
                updateMainLabel(); 
            } catch(NullPointerException ex){PopsGameManager.displayErrorMessageDebug(ex.toString());}
            
            updateList();
            jListGameList.setSelectedIndex(listIndex);
            updateGameStats();
            
            // Check if the PS1 compatability setting is checked
            PopsGameManager.setGameCompatabilityPS1(jCheckBoxMenuPS1Compatability.isSelected());
            
            // Check if the PS2 split game display is checked
            PopsGameManager.setSplitGameDisplayPS2(jCheckBoxMenuItemPS2ULCFG.isSelected());

            // This alters the text colour of the jList items if the PS1 compatability setting is enabled
            jListGameList.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index,boolean isSelected, boolean cellHasFocus) {

                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    Color colourRed = new Color(190,35,25);
                    Color colourGreen = new Color(55,170,20);
                    Color colourOrange = new Color(218,145,30);
                    
                    if (PopsGameManager.getCurrentConsole().equals("PS1")){

                        // If the display compatibility setting has been selected
                        if (PopsGameManager.getGameCompatabilityPS1()){

                           switch (PopsGameManager.getCurrentMode()) {
                               case "HDD":
                                   switch (GameListManager.getGameListPS1().get(index).getCompatibleHDD()) {
                                       case "0":
                                           if (GameListManager.getGameListPS1().get(index).getMultiDiscGame()){setForeground(colourOrange);} else {setForeground(Color.BLACK);}
                                           break;
                                       case "1":
                                           setForeground(colourGreen);
                                           break;
                                       case "2":
                                           setForeground(colourRed);
                                           break;
                                   }
                                break;
                               case "HDD_USB":
                                   switch (GameListManager.getGameListPS1().get(index).getCompatibleHDD()) {
                                       case "0":
                                           if (GameListManager.getGameListPS1().get(index).getMultiDiscGame()){setForeground(colourOrange);} else {setForeground(Color.BLACK);}
                                           break;
                                       case "1":
                                           setForeground(colourGreen);
                                           break;
                                       case "2":
                                           setForeground(colourRed);
                                           break;
                                   }
                                   break;
                               case "SMB":
                                   switch (GameListManager.getGameListPS1().get(index).getCompatibleHDD()) {
                                       case "0":
                                           if (GameListManager.getGameListPS1().get(index).getMultiDiscGame()){setForeground(colourOrange);} else {setForeground(Color.BLACK);}
                                           break;
                                       case "1":
                                           setForeground(colourGreen);
                                           break;
                                       case "2":
                                           setForeground(colourRed);
                                           break;
                                   }
                                   break;
                           }  
                        }
                        else {if (GameListManager.getGameListPS1().get(index).getMultiDiscGame()){setForeground(colourOrange);} else {setForeground(Color.BLACK);} }
                    }
                    else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                        if (PopsGameManager.getSplitGameDisplayPS2()){
                            if (GameListManager.getGameListPS2().get(index).getULGame()){setForeground(colourOrange);}}else {setForeground(Color.BLACK);
                        }
                    }

                     return c;  
                }
           });
            
            try {displayGameDetails();} catch(NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
        
        // Remove the button margins to enable smaller buttons
        jButtonVMC.setMargin(new Insets(0,0,0,0));
        jButtonCreateConfig.setMargin(new Insets(0,0,0,0));
        jButtonEditCheat.setMargin(new Insets(0,0,0,0));
        jButtonGameArt.setMargin(new Insets(0,0,0,0));

        // Update the menu items
        if (PopsGameManager.isCurrentConsoleSet()) {updateMenuItems();}
    }
    

    // This launches the current PS2 game with PCSX2 if the emulator option is enables and the emulator path has been specified
    private void launchEmulatorPS2(){
        
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            if (PopsGameManager.getEmulatorInUsePS2() && PopsGameManager.getEmulatorPathPS2() != null){
            
                ProcessBuilder pb;
                int index = PopsGameManager.getEmulatorPathPS2().lastIndexOf(File.separator); 

                //String driveLetter = PopsGameManager.getEmulatorPath().substring(0, 1);
                String exePath = PopsGameManager.getEmulatorPathPS2().substring(0, index);
                String exeName = PopsGameManager.getEmulatorPathPS2().substring(index +1);
                
                String gameID = GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID();
                String gameName = GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameName();

                File directory = new File(exePath);

                List<String> commands = new ArrayList<>();
                commands.add(exePath + File.separator + exeName);
                commands.add(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + gameID + "." + gameName + ".iso");
                if (PopsGameManager.getEmulatorFullScreenPS2()) {commands.add("--fullscreen");}
                
                pb = new ProcessBuilder(commands);
                
                pb.directory(directory);
                processPCSX2 = null;

                if (processPCSX2 != null && processPCSX2.isAlive()) {return;}

                try {
                    // Start and wait for PCSX2
                    processPCSX2 = pb.start();
                    if (processPCSX2!= null) try {processPCSX2.waitFor();} catch (InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                } 
                
                catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            }
        }
        else {JOptionPane.showMessageDialog(null,"PS2 emulator can only be used in \"SMB\" mode."," Cannot Use Emulator!",JOptionPane.ERROR_MESSAGE);}
    }
    
    
    // This launches the current PS1 game with ePSX if the emulator option is enables and the emulator path has been specified
    private void launchEmulatorPS1(){
        
        File tempFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "EMU_TEMP");

        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            if (PopsGameManager.getEmulatorInUsePS1() && PopsGameManager.getEmulatorPathPS1() != null){

                int index = PopsGameManager.getEmulatorPathPS1().lastIndexOf(File.separator); 
                String gameName = GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName();
                String gameID = GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID();
                String exePath = PopsGameManager.getEmulatorPathPS1().substring(0, index);
                String exeName = PopsGameManager.getEmulatorPathPS1().substring(index +1);
                String gamePath = PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameName + "-" + gameID + ".VCD";

                // Create a temp folder to store the newly created .bin and .cue files
                new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "EMU_TEMP").mkdirs();
                boolean success = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "EMU_TEMP").exists();
                if (success) {

                    // Use pops2cue to convert the .vcd file to .bin/.cue files for use with an emulator
                    File vcdFile = new File(gamePath);

                    // Check the users operating system to determine which version of the app to execute 
                    String appFolder = "windows";
                    String appName = "pops2cue.exe";

                    // If the users operating system is linux or mac
                    if (PopsGameManager.getOSType().equals("Linux") || PopsGameManager.getOSType().equals("Mac")){
                        appFolder = "linux";
                        appName = "pops2cue";
                    }

                    List<String> commands = new ArrayList<>();
                    commands.add(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder + File.separator + appName);
                    commands.add(vcdFile.toString());
                    
                    ProcessBuilder processBuilder = new ProcessBuilder(commands);
                    processBuilder.directory(new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "tools" + File.separator + appFolder));
                    Process process;
                    try {
                        process = processBuilder.start();

                        // Wait for the .vcd to be converted to a .bin/.cue by pops2cue
                        process.waitFor();    

                        Files.move(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameName + "-" + gameID + ".bin"), Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "EMU_TEMP" + File.separator + gameName + "-" + gameID + ".bin"));
                        Files.move(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + gameName + "-" + gameID + ".cue"), Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "EMU_TEMP" + File.separator + gameName + "-" + gameID + ".cue"));

                    } catch (IOException | InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                    File binFile = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "EMU_TEMP" + File.separator + gameName + "-" + gameID + ".bin");
            
                    // If the .bin file has been created
                    if (binFile.exists() && !binFile.isDirectory()){

                        // Launch the .bin file with the emulator
                        commands = new ArrayList<>();
                        commands.add(exePath + File.separator + exeName);
                        commands.add("-nogui");
                        commands.add("-cdfile");
                        commands.add(binFile.getAbsolutePath());

                        ProcessBuilder processBuilderEPSX = new ProcessBuilder(commands);
                        processBuilder.directory(new File(exePath));
                        
                        Process processEPSX = null;
                        
                        try {processEPSX = processBuilderEPSX.start();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                        // Wait for the PS1 emulator process to end
                        if (processEPSX!= null) try {processEPSX.waitFor();} catch (InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                        // Delete the tempoarary emulator folder and all of its contents (Should just be a .bin file and a .cue file for a PS1 game)
                        if (tempFolder.exists() && tempFolder.isDirectory()){
                            try {
                                Files.walkFileTree(Paths.get(tempFolder.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                                    @Override
                                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                        Files.delete(file);
                                        return FileVisitResult.CONTINUE;
                                    }

                                    @Override
                                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                        Files.delete(dir);
                                        return FileVisitResult.CONTINUE;
                                    }
                                });
                            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                        } 
                    }
                }
            }
        }
        else {JOptionPane.showMessageDialog(null,"PS1 emulator can only be used in \"SMB\" mode."," Cannot Use Emulator!",JOptionPane.ERROR_MESSAGE);}
    }
    
    
    // This updates the menu items depending on the current console and current mode
    private void updateMenuItems(){

        if (!PopsGameManager.getFisrtLaunch()){

            // Hide/Show certain menu items depening on the selected console and the selected mode
            
            if (PopsGameManager.getCurrentMode().equals("SMB")) {jMenuItemMD5.setVisible(true);} else {jMenuItemMD5.setVisible(false);}
            if (PopsGameManager.getCurrentMode().equals("HDD")) {
                jMenuConsoleFileTransfer.setVisible(true);
                jMenuItemRefreshGameList.setVisible(true);
            } 
            else {
                jMenuConsoleFileTransfer.setVisible(false);
                jMenuItemRefreshGameList.setVisible(false);
            }
            
            if (PopsGameManager.getCurrentConsole().equals("PS1")){
                jMenuItemAddPS1Game.setVisible(true); 
                jMenuItemAddPS2Game.setVisible(false); 
                jMenuItemAddPS1Game.setVisible(true);
                jMenuItemAddPS2Game.setVisible(false);
                jMenuItemBatchAddPS1Game.setVisible(true); 
                jMenuItemBatchAddPS2Game.setVisible(false); 
                jMenuItemBatchAddPS1Game.setVisible(true);
                jMenuItemBatchAddPS2Game.setVisible(false);
                jMenuItemGenerateConfElm.setVisible(true);
                jMenuBatchPS1Elf.setVisible(true);
                jRadioButtonMenuItemPlaystation1.setSelected(true);
                jRadioButtonMenuItemPlaystation2.setSelected(false);
                jMenuItemDeleteAllELF.setVisible(true);
                //jMenuItemGenerateSpine.setVisible(false);
                //jMenuItemDeleteAllSpineART.setVisible(false);
                jMenuItemGenerateULConf.setVisible(false);
                jMenuItemMergePS2Game.setVisible(false);
                jMenuItemSplitPS2Game.setVisible(false);
                
                if (PopsGameManager.getGameIDPositionPS1().equals("start")){
                    jRadioButtonMenuItemGameIDPositionBeginningPS1.setSelected(true);
                    jRadioButtonMenuItemGameIDPositionEndPS1.setSelected(false);
                }
                else if (PopsGameManager.getGameIDPositionPS1().equals("end")){
                    jRadioButtonMenuItemGameIDPositionBeginningPS1.setSelected(false);
                    jRadioButtonMenuItemGameIDPositionEndPS1.setSelected(true);
                }
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                jMenuItemAddPS1Game.setVisible(false); 
                jMenuItemAddPS2Game.setVisible(true); 
                jMenuItemAddPS1Game.setVisible(false);
                jMenuItemAddPS2Game.setVisible(true);
                jMenuItemBatchAddPS1Game.setVisible(false); 
                jMenuItemBatchAddPS2Game.setVisible(true); 
                jMenuItemBatchAddPS1Game.setVisible(false);
                jMenuItemBatchAddPS2Game.setVisible(true);
                jMenuItemGenerateConfElm.setVisible(false);
                jMenuBatchPS1Elf.setVisible(false);
                jRadioButtonMenuItemPlaystation1.setSelected(false);
                jRadioButtonMenuItemPlaystation2.setSelected(true);  
                jMenuItemDeleteAllELF.setVisible(false);
                //jMenuItemGenerateSpine.setVisible(true);
                //jMenuItemDeleteAllSpineART.setVisible(true);
                
                if (PopsGameManager.getGameIDPositionPS2().equals("start")){
                    jRadioButtonMenuItemGameIDPositionBeginningPS2.setSelected(true);
                    jRadioButtonMenuItemGameIDPositionEndPS2.setSelected(false);
                }
                else if (PopsGameManager.getGameIDPositionPS2().equals("end")){
                    jRadioButtonMenuItemGameIDPositionBeginningPS2.setSelected(false);
                    jRadioButtonMenuItemGameIDPositionEndPS2.setSelected(true);
                }
                
                if (PopsGameManager.getCurrentMode().equals("SMB")){jMenuItemGenerateULConf.setVisible(true);} else {jMenuItemGenerateULConf.setVisible(false);}
                if (PopsGameManager.getCurrentMode().equals("SMB")){jMenuItemMergePS2Game.setVisible(true);} else {jMenuItemMergePS2Game.setVisible(false);}
                if (PopsGameManager.getCurrentMode().equals("SMB")){jMenuItemSplitPS2Game.setVisible(true);} else {jMenuItemSplitPS2Game.setVisible(false);}
            }
        }
    }
    
    
    // This updates the game list in the MainScreen
    private void updateList(){
        
        if (PopsGameManager.isOPLFolderSet() && PopsGameManager.isCurrentConsoleSet()) {
            
            if (PopsGameManager.getCurrentConsole().equals("PS1")) {
                if (GameListManager.getGameListPS1()!= null) {

                    List<String> gameNameList = new ArrayList<>();
                    GameListManager.getGameListPS1().stream().forEach((game) -> {gameNameList.add(game.getGameName());});
                    createList(gameNameList.toArray(new String[0])); 
                }
                else {createList(new String[0]);}
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
                if (GameListManager.getGameListPS2()!= null) {
                    
                    List<String> gameNameList = new ArrayList<>();
                    GameListManager.getGameListPS2().stream().forEach((game) -> {gameNameList.add(game.getGameName());});
                    createList(gameNameList.toArray(new String[0]));  
                }
                else {createList(new String[0]);}
            }
        }
    }
    
    
    // Update the main label with the currently selected mode
    private void updateMainLabel(){
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {
            if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD")){jLabelCurrentConsoleDisplay.setText("PlayStation 1  -  " + PopsGameManager.getCurrentMode());}
            else {jLabelCurrentConsoleDisplay.setText("PlayStation 1  -  USB");} 
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
            if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD")){jLabelCurrentConsoleDisplay.setText("PlayStation 2  -  " + PopsGameManager.getCurrentMode());}
            else {jLabelCurrentConsoleDisplay.setText("PlayStation 2  -  USB");}
        }
    }
    
    
    // Changes the MainScreen layout slightly for PS1 or PS2 games
    private void switchConsole(String console){
        
        if (console.equals("PS1")) {updateCoverLayoutPS1();} else if (console.equals("PS2")) {updateCoverLayoutPS2();}
        
        PopsGameManager.setCurrentConsole(console);
        updateMainLabel();
        updateList();
        updateMenuItems();

        try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // Update the cover layout for PS1 games
    private void updateCoverLayoutPS1(){
        
        jPanelGameCover.setLayout(null);
        jPanelGameCover.setPreferredSize(new Dimension(175,260));
        jLabelGameFrontCover.setSize(new Dimension(140,140));
        
        if (new File(NO_IMAGE_PS1_COVER_PATH).exists() && new File(NO_IMAGE_PS1_COVER_PATH).isFile()){
            jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS1_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));
        }

        this.pack();
    }
    
    
    // Update the cover layout for PS2 games
    private void updateCoverLayoutPS2(){
        
        jPanelGameCover.setLayout(null);
        jPanelGameCover.setPreferredSize(new Dimension(175,260));
        jLabelGameFrontCover.setSize(new Dimension(140,200));
        
        if (new File(NO_IMAGE_PS2_COVER_PATH).exists() && new File(NO_IMAGE_PS2_COVER_PATH).isFile()){
            jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS2_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));
        }

        this.pack();
    }
    
    
    // This displays the game details in the MainScreen
    private void displayGameDetails(){

        if (jListGameList.getSelectedIndex() != -1){

            if (PopsGameManager.getCurrentConsole().equals("PS1")){

                displayGameImages(); 
                
                // Display the game details
                if (GameListManager.getGameListPS1() != null){
                    jTextFieldGameTitleDisplay.setText(" " + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName());
                    jTextFieldGameIDDisplay.setText(GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID());
                    jTextFieldGameNumberDisplay.setText((jListGameList.getSelectedIndex()+1) + "/" + GameListManager.getGameListPS1().size());
                    jTextFieldGameSizeDisplay.setText(GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameReadableSize());
                }

                // Display the game config details
                if (configManager.gameConfigExists(GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID(), GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName())){
                    try {displayGameConfigDetails(configManager.readGameConfigFormatted(GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID(), GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName()));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                }
                else {clearGameConfigDetails();}
                
                // Check if the game folder exists and if it contains any virtual memory cards
                File gameFolder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName() + "-" + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID());
                
                if (gameFolder.exists() && gameFolder.isDirectory()){

                    ArrayList<String> vmcList = new ArrayList<>();
                    File[] listOfFiles = gameFolder.listFiles();
                    
                    // Get just the files with .VMC extensions
                    for (File file : listOfFiles) {
                        if (file.isFile() && file.getName().length() > 4) {
                            if (file.getName().substring(file.getName().length()-4, file.getName().length()).toUpperCase().equals(".VMC")){
                                if (vmcList.size() <= 1){vmcList.add(file.getName());}
                            }
                        }
                    }

                    // If the VMC list is not empty, display the VMC details in the labels in the GUI
                    if (!vmcList.isEmpty()){
                        if (vmcList.size() == 1) {jTextFieldGameVMC0.setText(vmcList.get(0));}
                        else {
                            jTextFieldGameVMC0.setText(vmcList.get(0));
                            jTextFieldGameVMC1.setText(vmcList.get(1));
                        } 
                    }
                }
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                
                displayGameImages();

                if (GameListManager.getGameListPS2() != null){
                    jTextFieldGameTitleDisplay.setText(" " + GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameName());
                    jTextFieldGameIDDisplay.setText(GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID());
                    jTextFieldGameNumberDisplay.setText((jListGameList.getSelectedIndex()+1) + "/" + GameListManager.getGameListPS2().size());
                    jTextFieldGameSizeDisplay.setText(GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameReadableSize());
                }

                if (GameListManager.getGameListPS2() != null){

                    if (configManager.gameConfigExists(GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID(), GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameName())){
                        try {displayGameConfigDetails(configManager.readGameConfigFormatted(GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID(), GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameName()));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    }
                    else {clearGameConfigDetails();}
                }
            }  
            updateGameStats();
        }
        else {clearGameConfigDetails();}
    }
    
    
    // This displays the game config details in the text fields in the GUI
    public void displayGameConfigDetails(String configData[]){

        if (configData[2] != null) {jTextFieldGameGenre.setText(configData[2]);} else {jTextFieldGameGenre.setText("");}                   // Genre
        if (configData[3] != null) {jTextFieldGameDeveloper.setText(configData[3]);} else {jTextFieldGameDeveloper.setText("");}           // Developer
        if (configData[4] != null) {jTextFieldGameReleaseDate.setText(configData[4]);} else {jTextFieldGameReleaseDate.setText("");}       // Release Date
        if (configData[5] != null) {jTextFieldGamePlayerNumber.setText(configData[5]);} else {jTextFieldGamePlayerNumber.setText("");}     // No of Players
        if (configData[9] != null) {jTextFieldGameVMC0.setText(configData[9]);} else {jTextFieldGameVMC0.setText("");}                     // VMC0
        if (configData[10] != null) {jTextFieldGameVMC1.setText(configData[10]);} else {jTextFieldGameVMC1.setText("");}                   // VMC1
        
        // Device compatibility
        if (configData[13] != null) {
            switch (configData[13]) {
                case "1":
                    jTextFieldGameDeviceCompatibility.setText("USB");
                    break;
                case "5":
                    jTextFieldGameDeviceCompatibility.setText("ETH");
                    break;
                case "6":
                    jTextFieldGameDeviceCompatibility.setText("HDD");
                    break;
                case "2":
                    jTextFieldGameDeviceCompatibility.setText("USB, ETH");
                    break;
                case "3":
                    jTextFieldGameDeviceCompatibility.setText("USB, HDD");
                    break;
                case "4":
                    jTextFieldGameDeviceCompatibility.setText("HDD, ETH");
                    break;
                case "all":
                    jTextFieldGameDeviceCompatibility.setText("USB, HDD, ETH");
                    break;
                default:
                    break;
            }
        } else {jTextFieldGameDeviceCompatibility.setText("");}    
    }
    
    
    // This clears the game config details from the text fields in the GUI
    public void clearGameConfigDetails(){
        jButtonCreateConfig.setText("CFG");
        jTextFieldGameGenre.setText("");
        jTextFieldGameDeveloper.setText("");
        jTextFieldGameReleaseDate.setText("");
        jTextFieldGamePlayerNumber.setText("");  
        jTextFieldGameDeviceCompatibility.setText(""); 
        jTextFieldGameVMC0.setText("");
        jTextFieldGameVMC1.setText("");
    }
    
    
    // This clears the game main details from the text fields in the GUI
    private void clearGameMainDetails(){
        jTextFieldGameTitleDisplay.setText("");
        jTextFieldGameNumberDisplay.setText("0/0");
        jTextFieldGameIDDisplay.setText("");
        jTextFieldGameSizeDisplay.setText("");      
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS1_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS2_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));} 
    }
    
    
    // This updates the game stats display
    public void updateGameStats(){
        
        String gameNumberDisplay = null;

        try {
            int totalGamesPS1 = GameListManager.getGameListPS1().size();
            gameNumberDisplay = totalGamesPS1 + "";
            jTextFieldPS1GameCount.setText(gameNumberDisplay + "  -  Total Size  -  " + GameListManager.getGameSizeDisplayTotalPS1());

            int totalGamesPS2 = GameListManager.getGameListPS2().size();
            gameNumberDisplay = totalGamesPS2 + "";
            jTextFieldPS2GameCount.setText(gameNumberDisplay + "  -  Total Size  -  " + GameListManager.getGameSizeDisplayTotalPS2());
        } catch(NullPointerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    

    // This displays the game images in the MainScreen
    private void displayGameImages(){
        
        // Displays the game images depending if the user is viewing PS1 or PS2 games
        if (PopsGameManager.getCurrentConsole().equals("PS1")){

            // Front cover
            File frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName() + "-" + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID() + ".ELF_COV" + ".jpg");
            if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + PopsGameManager.getFilePrefix() + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName() + "-" + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID() + ".ELF_COV" + ".png");
                if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS1_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
            } 
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){
            
            // Front cover
            File frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID() + "_COV" + ".jpg");
            if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
            else {
                frontCover = new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID() + "_COV" + ".png");
                if(frontCover.exists() && !frontCover.isDirectory()) {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(frontCover.toString()).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
                else {jLabelGameFrontCover.setIcon(new ImageIcon(new ImageIcon(NO_IMAGE_PS2_COVER_PATH).getImage().getScaledInstance(jLabelGameFrontCover.getWidth(), jLabelGameFrontCover.getHeight(), Image.SCALE_DEFAULT)));}
            }   
        } 
    }
    
    
    // This displays the add game screen
    private void displayAddGameScreen(){
        
        // Check if POPSTARTER.ELF exists in POPSTARTER/POPSTARTER.ELF
        File popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF");
        if (PopsGameManager.getCurrentConsole().equals("PS1") && !popstarterFile.exists()){
            JOptionPane.showMessageDialog(null,"Could not locate POPSTARTER.ELF in the POPSTARTER directory."," Missing POPSTARTER.ELF!",JOptionPane.ERROR_MESSAGE);
        }
        else {

            // Display a message if the OPL directory has not been set by the user
            if (!PopsGameManager.isOPLFolderSet()) {JOptionPane.showMessageDialog(null, "OPL directory is not set.");}
            else {

                JFileChooser chooser = new JFileChooser();
                if (selectedGameFolder != null) {chooser.setCurrentDirectory(selectedGameFolder);}
                else {chooser.setCurrentDirectory(new java.io.File(PopsGameManager.getOPLFolder()));}
                
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = null;

                // Apply different file filters depending if the user is loading a PS1 or a PS2 game
                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                    chooser.setDialogTitle("Select PS1 Game");
                    filter = new FileNameExtensionFilter("PS1 GAMES", "VCD", "CUE");
                }
                else {
                    chooser.setDialogTitle("Select PS2 Game");
                    filter = new FileNameExtensionFilter("PS2 GAMES", "ISO");
                }

                chooser.setFileFilter(filter);

                // If the user selects a file
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                    String fileExtension = chooser.getSelectedFile().toString().substring(chooser.getSelectedFile().toString().length() - 3);
                    selectedGameFolder = new File(chooser.getSelectedFile().getPath().substring(0, chooser.getSelectedFile().getPath().lastIndexOf(File.separator)));

                    //if (chooser.getSelectedFile().getName().length()-4 < 32 ){
                        AddGameSMBScreen addGameSMBScreen;
                        AddGameHDDScreenPS1 addGameHDDScreen;
                        AddGameHDDScreenPS2 addGameHDDScreenPS2;

                        switch (PopsGameManager.getCurrentMode()) {
                            case "HDD":

                                if (PopsGameManager.getCurrentConsole().equals("PS1")){
                                    addGameHDDScreen = new AddGameHDDScreenPS1(this, true, false, null, chooser.getSelectedFile());
                                    addGameHDDScreen.setLocationRelativeTo(this);
                                    addGameHDDScreen.setVisible(true);
                                }
                                else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                                    addGameHDDScreenPS2 = new AddGameHDDScreenPS2(this, true, false, null, chooser.getSelectedFile());
                                    addGameHDDScreenPS2.setLocationRelativeTo(this);
                                    addGameHDDScreenPS2.setVisible(true);
                                }
    
                                break;
                            case "HDD_USB":
                            case "SMB":

                                // Works
                                addGameSMBScreen = new AddGameSMBScreen(this, true, false, chooser.getSelectedFile().getPath(), fileExtension, chooser.getSelectedFile(), jListGameList.getSelectedIndex());
                                addGameSMBScreen.setLocationRelativeTo(this);
                                addGameSMBScreen.setVisible(true);   

                                break;
                            default:
                                break;
                        } 
                    //} 
                    //else {
                        //JOptionPane.showMessageDialog(null,"The file name of this game contains more than 32 characters.\nPlease rename the game and try again."," Game Name Too Long!",JOptionPane.ERROR_MESSAGE);
                    //}
                }     
            } 
        }  
    }
    
    
    
    
    // This displays the batch add game screen
    private void displayBatchAddGameScreen(){
        
        // Check if POPSTARTER.ELF exists in POPSTARTER/POPSTARTER.ELF
        File popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF");
        if (PopsGameManager.getCurrentConsole().equals("PS1") && !popstarterFile.exists()){
            JOptionPane.showMessageDialog(null,"Could not locate POPSTARTER.ELF in the POPSTARTER directory."," Missing POPSTARTER.ELF!",JOptionPane.ERROR_MESSAGE);
        }
        else {

            // Display a message if the OPL directory has not been set by the user
            if (!PopsGameManager.isOPLFolderSet()) {JOptionPane.showMessageDialog(null, "OPL directory is not set.");}
            else {

                JFileChooser chooser = new JFileChooser();
                if (selectedGameFolder != null) {chooser.setCurrentDirectory(selectedGameFolder);}
                else {chooser.setCurrentDirectory(new java.io.File(PopsGameManager.getOPLFolder()));}
                
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                
                // If the user selects a file
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selectedGameFolder = new File(chooser.getSelectedFile().toString());

                    AddGameHDDScreenPS1 addGameHDDScreenPS1;
                    AddGameHDDScreenPS2 addGameHDDScreenPS2;
                    AddGameSMBScreen addGameSMBScreen;
                    
                    switch (PopsGameManager.getCurrentMode()) {
                        case "HDD":

                            if (PopsGameManager.getCurrentConsole().equals("PS1")){
                                
                                // Ensure that the directory contains VCD files
                                boolean folderContainsVCDFiles = false;
                                File[] filesInFolder = selectedGameFolder.listFiles();
                                for (File file : filesInFolder) {if (file.isFile() && file.getAbsolutePath().toUpperCase().endsWith("VCD")) {folderContainsVCDFiles = true;}}
                                
                                // Display the batch add PS1 game scrren
                                if (folderContainsVCDFiles){
                                    addGameHDDScreenPS1 = new AddGameHDDScreenPS1(this, true, true, chooser.getSelectedFile().getPath(),chooser.getSelectedFile());
                                    addGameHDDScreenPS1.setLocationRelativeTo(this);
                                    addGameHDDScreenPS1.setVisible(true);
                                }
                                else{JOptionPane.showMessageDialog(null,"This directory does not appear to contain any VCD files!"," No Games Detected!",JOptionPane.WARNING_MESSAGE);}
                            }
                            else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                                
                                // Ensure that the directory contains ISO files
                                boolean folderContainsISOFiles = false;
                                File[] filesInFolder = selectedGameFolder.listFiles();
                                for (File file : filesInFolder) {if (file.isFile() && file.getAbsolutePath().toUpperCase().endsWith("ISO")) {folderContainsISOFiles = true;}}
                                
                                // Display the batch add PS2 game scrren
                                if (folderContainsISOFiles){
                                    addGameHDDScreenPS2 = new AddGameHDDScreenPS2(this, true, true, chooser.getSelectedFile().getPath(),chooser.getSelectedFile());
                                    addGameHDDScreenPS2.setLocationRelativeTo(this);
                                    addGameHDDScreenPS2.setVisible(true);
                                }
                                else{JOptionPane.showMessageDialog(null,"This directory does not appear to contain any ISO files!"," No Games Detected!",JOptionPane.WARNING_MESSAGE);}
                            }
                            
                            break;
                        case "HDD_USB":
                        case "SMB":

                            addGameSMBScreen = new AddGameSMBScreen(this, true, true, chooser.getSelectedFile().getPath(), null, chooser.getSelectedFile(), jListGameList.getSelectedIndex());
                            addGameSMBScreen.setLocationRelativeTo(this);
                            addGameSMBScreen.setVisible(true);   
    
                            break;
                        default:
                            break;
                    }    
                }     
            } 
        }
    }
    

    // This deletes all of the PS1 ELF files and then generates new ones (Useful when a new POPSTARTER.ELF is released)
    private void generateNewElfFiles(){
        
        // Generate new >ELF files for all of the .VCD files in the local POPS folder
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            
            ArrayList<String> vcdFilesInDirectory = new ArrayList<>();

            // Try and add all .VCD file names to the list of VCD files   
            try(Stream<Path> paths = Files.walk(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator))) {
                paths.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        if (filePath.toString().substring(filePath.toString().length()-4, filePath.toString().length()).equals(".VCD")){vcdFilesInDirectory.add(filePath.toString());}  
                    }
                });
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 

            // Loop through the list of VCD files, delete the corrosponding .ELF file and then generate a new .ELF file with the same name
            if (!vcdFilesInDirectory.isEmpty()){
                
                boolean elfFilesGenerated = true;
                
                // Check if POPSTARTER.ELF exists in /Tools/POPSTARTER/POPSTARTER.ELF 
                File popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.elf");
                if (popstarterFile.exists() && !popstarterFile.isDirectory()){
                    popstarterFile.renameTo(new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF"));
                }
                
                popstarterFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "POPSTARTER" + File.separator + "POPSTARTER.ELF");
                
                if (popstarterFile.exists() && !popstarterFile.isDirectory()){
                    for (String vcdFilePath : vcdFilesInDirectory){
                        String path = vcdFilePath.substring(0, vcdFilePath.lastIndexOf(File.separator) +1);
                        String vcdFileName = vcdFilePath.substring(vcdFilePath.lastIndexOf(File.separator) +1, vcdFilePath.length());

                        String elfFileName = PopsGameManager.getFilePrefix() + vcdFileName.substring(0, vcdFileName.length() -3) + "ELF";
                        
                        File elfFile = new File(path + elfFileName);
                        if (elfFile.exists() && !elfFile.isDirectory()){elfFile.delete();}
                        AddGameManager.generateElf(elfFileName, PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator);
                        
                        if (!new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + elfFileName).exists()) {elfFilesGenerated = false;}
                    }
                } 
                else {JOptionPane.showMessageDialog(null,"Cannot find POPSTARTER.ELF in the POPSTARTER directory!\n\nPut the POPSTARTER.ELF in the directory and try again."," Error Generating .ELF!",JOptionPane.ERROR_MESSAGE);} 

                if (elfFilesGenerated) {JOptionPane.showMessageDialog(null,"The new ELF files have been generated!."," ELF Files Generated",JOptionPane.PLAIN_MESSAGE);} 
            }
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){

            int dialogResult = JOptionPane.showConfirmDialog(null, "FTP server must be running on your console in order to perform this task."," Generate New ELF Files",JOptionPane.OK_CANCEL_OPTION);
            if (dialogResult == JOptionPane.OK_OPTION){
                
                // Create a temporary folder and generate a new .ELF file for each of the remote games
                if (new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "ELF_TEMP").mkdir()){
                    GameListManager.getGameListFromConsolePS1().forEach((currentGame) -> {AddGameManager.generateElf(currentGame.getGameName() + "-" + currentGame.getGameID() + ".ELF", PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "ELF_TEMP" + File.separator);});
                }
                
                // Connect to the console, delete each game .ELf file and upload a new .ELF file
                List<File> filesInFolder = null;
                try {filesInFolder = Files.walk(Paths.get(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "ELF_TEMP")).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                
                MyFTPClient myFTP = new MyFTPClient();

                if (myFTP.connectToConsole(PopsGameManager.getPS2IP())){
                    myFTP.changeDirectory("hdd/0/+OPL");
                    
                    if (filesInFolder != null && filesInFolder.size() >0){
                        
                        // Delete the .ELF file from the console and upload the new .ELF file
                        for (File file : filesInFolder){
                            myFTP.deleteRemoteFile("/pfs/0/APPS/" + file.getName());
                            myFTP.addFileToPS2(PopsGameManager.getOPLFolder() + "POPS" + File.separator + "ELF_TEMP" + File.separator, file.getName(), "/pfs/0/APPS/", PopsGameManager.getCurrentConsole().equals("PS1"));
                        }
                    }
                    myFTP.disconnectFromConsole();
                }
                
                // Delete the ELF_TEMP folder from the local POPS directory and all of its contents
                File localTempFile = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + "ELF_TEMP");
                if (localTempFile.exists() && localTempFile.isDirectory()){
                    try {
                        Files.walkFileTree(Paths.get(localTempFile.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
                }
            }
        }
    }
    
    
    // This checks to see if there is an update available on the server and if there is an update, the user has the option to download it
    private void checkForUpdate(){

        MyTCPClient tcpClient = new MyTCPClient();
        
        // Send a message to the server asking for the latest version number
        String serverResponse = tcpClient.sendMessageToServer("VERSION");
        
        if (serverResponse != null){
            
            // Display message if the server is not responding
            if (serverResponse.equals("NO_RESPONSE")) {JOptionPane.showMessageDialog (null, "The server is currently not responding or the connection is being blocked by your firewall!"," Server Not Responding",JOptionPane.WARNING_MESSAGE);}
            else {

                // If a message is recieved try and split the message in order to seperate the version number and the build date
                String[] newVersionInfo = null;
                if (serverResponse.contains(",")) {newVersionInfo = serverResponse.split(",");}

                // If the message was successfully split
                if (newVersionInfo != null && newVersionInfo.length > 1) {
                    String newVersionNumber = newVersionInfo[0];
                    String newBuildDate = newVersionInfo[1];

                    // Display the message to the user
                    if (PopsGameManager.getApplicationVersionNumber().equals(newVersionNumber)) {
                        JOptionPane.showMessageDialog (null, "There are currently no updates available!  \n\nApplication Version : " + PopsGameManager.getApplicationVersionNumber() + "\nServer Version : " + newVersionNumber," No Update Available",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if (!PopsGameManager.getApplicationVersionNumber().equals("NO_RESPONSE")){
                        int dialogResult = JOptionPane.showConfirmDialog (null, "There is an update available for this software!  \n\nCurrent Version : " + PopsGameManager.getApplicationVersionNumber() + "  -  (" + PopsGameManager.getApplicationReleaseDate() + ")  \nLatest Version   : " + newVersionNumber +  "  -  (" + newBuildDate + ")  \n\nDo you want to download the update?"," Update Available",JOptionPane.YES_NO_OPTION);
                        if(dialogResult == JOptionPane.YES_OPTION){tcpClient.getJarFileFromServer(newVersionNumber);} 
                    }
                }
            }
        }
        else {JOptionPane.showMessageDialog (null, "The server is currently not responding or the connection is being blocked by your firewall!"," Server Not Responding",JOptionPane.WARNING_MESSAGE);} 
    }
    
    
    // This deletes the selected game
    private void deleteGame(){
        
        String selectedGame = null;
        String selectedGameID = null;
        int dialogResult;
     
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {
            
            selectedGame = GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName();
            selectedGameID = GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID();

            switch (PopsGameManager.getCurrentMode()) {
                case "HDD_USB": 
                case "SMB": 
                    dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete - " + selectedGame + " ?"," Delete Game",JOptionPane.YES_NO_OPTION);
                    if(dialogResult == JOptionPane.YES_OPTION){
                        if (new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + selectedGame + "-" + selectedGameID + ".VCD").exists()){
                            
                            // Delete the .VCD and the .ELF files
                            new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + selectedGame + "-" + selectedGameID + ".VCD").delete();
                            new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + PopsGameManager.getFilePrefix() + selectedGame + "-" + selectedGameID + ".ELF").delete();

                            // Update game list
                            List<Game> gameListPS1 = GameListManager.getGameListPS1();
                            gameListPS1.remove(jListGameList.getSelectedIndex());
                            GameListManager.setGameListPS1(gameListPS1);
                            updateGameList(null, jListGameList.getSelectedIndex() -1);
                            
                            // Generate a new conf_apps.cfg file
                            GameListManager.writeConfigELM();
                        }
                    }
                    break;
                case "HDD": 
                    dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete - " + selectedGame + " ?\n\nFTP Server must be running on your console in order to perform this task!"," Connect to PlayStation 2",JOptionPane.YES_NO_OPTION);
                    if(dialogResult == JOptionPane.YES_OPTION){

                        // FTP to console and delete PS1 .VCD and PS1 .ELF files
                        MyFTPClient myFTP = new MyFTPClient();
                        
                        String gameName = GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName();
                        String gameID = GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID();

                        // Connect to the PS2 console
                        if (myFTP.connectToConsole(PopsGameManager.getPS2IP())) {

                            // Change to the __.POPS/ partition and delete the remote VCD file. Example - ("hdd/0/__.POPS") and then ("/pfs/0/")    
                            String remoteVcdPath = null;
                            if (GameListManager.getFormattedVCDDrive().equals("hdd")) {remoteVcdPath = "/pfs/" + GameListManager.getFormattedVCDPartition() + "/";}
                            else if (GameListManager.getFormattedVCDDrive().equals("mass")) {remoteVcdPath = "/mass/" + GameListManager.getFormattedVCDPartition() + "/";}
                            
                            myFTP.changeDirectory(GameListManager.getFormattedVCDDrive() + "/" + GameListManager.getFormattedVCDPartition() + "/" + GameListManager.getFormattedVCDFolder());
                            myFTP.deleteRemoteFile(remoteVcdPath + gameName + "-" + gameID + ".VCD");

                            // Disconnect and re-connect to the FTP (Easiest way to change to a different partition without causing hassle)
                            myFTP.disconnectFromConsole();
                            myFTP.connectToConsole(PopsGameManager.getPS2IP());
                            
                            // Change to the +OPL/APPS/ directory and delete the remote ELF file. Example - ("hdd/0/+OPL") and then ("/pfs/0/APPS/")
                            // This removes the root folder (+OPL) from the ELF folder string. (+OPL is not needed because it is the partition name, not the root folder)
                            String elfFolder = "";
                            String elfPartitionName = "";
                            String[] splitELFFolder = null;
                            if (GameListManager.getFormattedELFFolder().contains("/")) {splitELFFolder = GameListManager.getFormattedELFFolder().split("/");}
                            if (splitELFFolder != null && splitELFFolder.length > 1){
                                elfPartitionName = splitELFFolder[0];
                                for (int i = 1; i < splitELFFolder.length; i++){
                                    if (i!=1) {elfFolder = elfFolder + "/" + splitELFFolder[i];} else {elfFolder = elfFolder + splitELFFolder[i];}
                                }
                            }
                            else {elfFolder = GameListManager.getFormattedELFFolder();}

                            // Change to the +OPL/APPS/ directory and delete the remote ELF file. Example - ("hdd/0/+OPL") and then ("/pfs/0/APPS/")
                            String remoteElfPath = null;
                            if (GameListManager.getFormattedELFDrive().equals("hdd")) {remoteElfPath = "/pfs/" + GameListManager.getFormattedELFPartition() + "/" + elfFolder + "/";}
                            else if (GameListManager.getFormattedELFDrive().equals("mass")) {remoteElfPath = "/mass/" + GameListManager.getFormattedELFPartition() + "/" + elfFolder + "/";}

                            myFTP.changeDirectory(GameListManager.getFormattedELFDrive() + "/" + GameListManager.getFormattedELFPartition() + "/" + elfPartitionName);
                            myFTP.changeDirectory(remoteElfPath);
                            myFTP.deleteRemoteFile(remoteElfPath + gameName + "-" + gameID + ".ELF");
                            
                            // Disconnect the FTP connection with the console
                            myFTP.disconnectFromConsole();
                            
                            // Try and get the PS1 game list from the console, write the game list.dat file, callback to update the main gui
                            List<Game> gameList = GameListManager.getGameListFromConsolePS1();
                            // Write the PS1 game list file
                            GameListManager.writeGameListFilePS1(gameList);

                            // Try and load the game data from the PS1 game list file
                            try {GameListManager.createGameListFromFile("PS1", new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS1"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                            updateGameList(null, jListGameList.getSelectedIndex() -1); 
                        }
                    }
                    break;
            }
        }       
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
            
            selectedGame = GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameName();
            selectedGameID = GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID();

            switch (PopsGameManager.getCurrentMode()) {
                case "HDD_USB": 
                case "SMB": 
                    dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete - " + selectedGame + " ?"," Delete Game",JOptionPane.YES_NO_OPTION);
                    if(dialogResult == JOptionPane.YES_OPTION){
                        if (new File(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + selectedGameID + "." + selectedGame + ".iso").exists()){
                            new File(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + selectedGameID + "." + selectedGame + ".iso").delete();
                            // Update game list here!!!
                            GameListManager.createGameListsPS2(false);
                            updateGameList(null, jListGameList.getSelectedIndex()-1);
                        }
                    }
                    break;
                case "HDD": 
                    //dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete - " + selectedGame + " ?\n\nHDL Server must be running on your console in order to perform this task!"," Connect to PlayStation 2",JOptionPane.YES_NO_OPTION);
                    //if(dialogResult == JOptionPane.YES_OPTION){

                        // Delete PS2 game using HDL_Dump
                        JOptionPane.showMessageDialog(null, "This application cannot yet delete PS2 games from the consoles internal hdd." ," Delete Game",JOptionPane.ERROR_MESSAGE);

                    //}
                    break;
            } 
        } 
    }
    
    // Switch the current console mode to PS1
    private void switchConsolePS1(){
        if (PopsGameManager.getCurrentConsole().equals("PS2")) {
            switchConsole("PS1");
            if (!GameListManager.getGameListPS1().isEmpty()) {jListGameList.setSelectedIndex(0);}
            updateGameStats();
            displayGameDetails();
            jMenuItemPS1Emulator.setVisible(true);
            jMenuItemPS2Emulator.setVisible(false);
        }  
        jRadioButtonMenuItemPlaystation1.setSelected(true);
    }
    
    // Switch the current console mode to PS2
    private void switchConsolePS2(){
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {
            switchConsole("PS2");
            if (!GameListManager.getGameListPS2().isEmpty()) {jListGameList.setSelectedIndex(0);}
            updateGameStats();
            displayGameDetails();
            jMenuItemPS1Emulator.setVisible(false);
            jMenuItemPS2Emulator.setVisible(true);
        }
        jRadioButtonMenuItemPlaystation2.setSelected(true);
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
            public void mouseClicked(MouseEvent evt) {}
            @Override
            public void mouseReleased(MouseEvent evt){if (SwingUtilities.isLeftMouseButton(evt)){
                displayGameDetails();
                
                if (jListGameList.getSelectedIndex() != -1) {
                    
                    if (PopsGameManager.getCurrentConsole().equals("PS2")){
                        if (GameListManager.getGameListPS2().get(jListGameList.getSelectedIndex()).getULGame()){
                            jMenuItemMD5.setEnabled(false);
                            jMenuItemSplitPS2Game.setEnabled(false);
                            if (GameListManager.getGameListPS2().get(jListGameList.getSelectedIndex()).getGameRawSize() >0) {jMenuItemMergePS2Game.setEnabled(true);} else {jMenuItemMergePS2Game.setEnabled(false);}
                        }
                        else {
                            jMenuItemMD5.setEnabled(true);
                            jMenuItemSplitPS2Game.setEnabled(true);
                            jMenuItemMergePS2Game.setEnabled(false);
                        } 
                    }
                }
            }}
        });
        
        // Key listener
        jListGameList.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                
                // If the delete key is released
                if (e.getKeyCode() == 127){
                    
                    if (buttonLastReleased == 0){
                        if (jListGameList.getSelectedIndex() != -1) {deleteGame();}
                        else {JOptionPane.showMessageDialog(null,"You need to select a game before you can perform this action."," No game selected!",JOptionPane.WARNING_MESSAGE);}
                        buttonLastReleased = System.currentTimeMillis();
                    }
                    
                    if (System.currentTimeMillis()-500 >= buttonLastReleased){
                        if (jListGameList.getSelectedIndex() != -1) {deleteGame();}
                        else {JOptionPane.showMessageDialog(null,"You need to select a game before you can perform this action."," No game selected!",JOptionPane.WARNING_MESSAGE);}
                        buttonLastReleased = System.currentTimeMillis();
                    }
                }
                
                // If the enter key is released
                if (e.getKeyCode() == 10){

                    // The key released method was being called multiple times, as a temporary solution the system time will be checked
                    if (buttonLastReleased == 0){
                        
                        if (PopsGameManager.getCurrentConsole().equals("PS1")){
                            launchEmulatorPS1();
                            buttonLastReleased = System.currentTimeMillis();
                        }
                        else if (PopsGameManager.getCurrentConsole().equals("PS2")){
                            launchEmulatorPS2();
                            buttonLastReleased = System.currentTimeMillis();
                        }
                    }

                    if (System.currentTimeMillis()-3000 >= buttonLastReleased){
                        
                        if (PopsGameManager.getCurrentConsole().equals("PS1")){
                            launchEmulatorPS1();
                            buttonLastReleased = System.currentTimeMillis();
                        }
                        if (PopsGameManager.getCurrentConsole().equals("PS2")){
                            launchEmulatorPS2();
                            buttonLastReleased = System.currentTimeMillis();
                        }
                    }  
                } 
                
                // If the up/down arrow key is released
                if (e.getKeyCode() == 38 || e.getKeyCode() == 40){displayGameDetails();}
            } 
        });
        jScrollPane1.setViewportView(jListGameList);
    }
    
    
    
    // This renames the games and all associated files by moving the position of the gameID postion to either before or after the game name
    private void gameIDPositionChanged(){
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")){

        }
        if (PopsGameManager.getCurrentConsole().equals("PS2")){

        } 
    }

    
    

    // This checks for games with names longer than 32 characters
    private void checkLongGameNames(){
        
        ArrayList<Game> longNameList = new ArrayList<>();

        // List the PS1 and PS2games that have names with more than 32 characters
        if (PopsGameManager.getCurrentConsole().equals("PS1")){GameListManager.getGameListPS1().stream().filter((game) -> (game.getGameName().length() > 32)).forEachOrdered((game) -> {longNameList.add(game);});}
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){GameListManager.getGameListPS2().stream().filter((game) -> (game.getGameName().length() > 32)).forEachOrdered((game) -> {longNameList.add(game);});}

        // If the list is not empty, display the game long name screen
        if (!longNameList.isEmpty()){
            GameLongNameScreen longNameScreen = new GameLongNameScreen(this, true, PopsGameManager.getCurrentConsole() ,longNameList);
            longNameScreen.setLocationRelativeTo(this);
            longNameScreen.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(null,"You do not have any " + PopsGameManager.getCurrentConsole() + " games with names greater than 32 characters in length."," No Games To Rename",JOptionPane.PLAIN_MESSAGE);
        } 
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelGameInformation = new javax.swing.JPanel();
        jLabelGameTitle = new javax.swing.JLabel();
        jLabelGameID = new javax.swing.JLabel();
        jLabelGameSize = new javax.swing.JLabel();
        jTextFieldGameTitleDisplay = new javax.swing.JTextField();
        jTextFieldGameIDDisplay = new javax.swing.JTextField();
        jTextFieldGameSizeDisplay = new javax.swing.JTextField();
        jTextFieldGameNumberDisplay = new javax.swing.JTextField();
        jPanelGameCover = new javax.swing.JPanel();
        jLabelGameFrontCover = new javax.swing.JLabel();
        jLabelCurrentConsoleDisplay = new javax.swing.JLabel();
        jPanelGameTools = new javax.swing.JPanel();
        jButtonGameArt = new javax.swing.JButton();
        jButtonCreateConfig = new javax.swing.JButton();
        jButtonEditCheat = new javax.swing.JButton();
        jButtonVMC = new javax.swing.JButton();
        jPanelGameList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListGameList = new javax.swing.JList<>();
        jPanelGameDetails = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldGameGenre = new javax.swing.JTextField();
        jTextFieldGameDeveloper = new javax.swing.JTextField();
        jTextFieldGameReleaseDate = new javax.swing.JTextField();
        jTextFieldGamePlayerNumber = new javax.swing.JTextField();
        jTextFieldGameDeviceCompatibility = new javax.swing.JTextField();
        jTextFieldGameVMC0 = new javax.swing.JTextField();
        jTextFieldGameVMC1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldPS1GameCount = new javax.swing.JTextField();
        jTextFieldPS2GameCount = new javax.swing.JTextField();
        jMenuBarMainMenu = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemOpenOPLDirectory = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();
        jMenuItemChangeLog = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jCheckBoxMenuPS1Compatability = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemPS2ULCFG = new javax.swing.JCheckBoxMenuItem();
        jMenuGameIDPositionPS1 = new javax.swing.JMenu();
        jRadioButtonMenuItemGameIDPositionBeginningPS1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemGameIDPositionEndPS1 = new javax.swing.JRadioButtonMenuItem();
        jMenuGameIDPositionPS2 = new javax.swing.JMenu();
        jRadioButtonMenuItemGameIDPositionBeginningPS2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemGameIDPositionEndPS2 = new javax.swing.JRadioButtonMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemAddPS1Game = new javax.swing.JMenuItem();
        jMenuItemAddPS2Game = new javax.swing.JMenuItem();
        jMenuItemGenerateConfElm = new javax.swing.JMenuItem();
        jMenuItemGenerateULConf = new javax.swing.JMenuItem();
        jMenuItemSplitPS2Game = new javax.swing.JMenuItem();
        jMenuItemMergePS2Game = new javax.swing.JMenuItem();
        jMenuItemPS1Emulator = new javax.swing.JMenuItem();
        jMenuItemPS2Emulator = new javax.swing.JMenuItem();
        jMenuItemMD5 = new javax.swing.JMenuItem();
        jMenuItemShareVMC = new javax.swing.JMenuItem();
        jMenuItemReportFile = new javax.swing.JMenuItem();
        jMenuItemReportCompatability = new javax.swing.JMenuItem();
        jMenuItemRefreshGameList = new javax.swing.JMenuItem();
        jMenuItemCheckUpdate = new javax.swing.JMenuItem();
        jMenuBatchTools = new javax.swing.JMenu();
        jMenuItemBatchAddPS1Game = new javax.swing.JMenuItem();
        jMenuItemBatchAddPS2Game = new javax.swing.JMenuItem();
        jMenuBatchDownload = new javax.swing.JMenuItem();
        jMenuItemBatchFileShare = new javax.swing.JMenuItem();
        jMenuItemCheckGameNames = new javax.swing.JMenuItem();
        jMenuBatchPS1Elf = new javax.swing.JMenuItem();
        jMenuItemGenerateSpine = new javax.swing.JMenuItem();
        jMenuDeleteFiles = new javax.swing.JMenu();
        jMenuItemDeleteUnusedArt = new javax.swing.JMenuItem();
        jMenuItemDeleteUnusedConfig = new javax.swing.JMenuItem();
        jMenuItemDeleteUnusedCheat = new javax.swing.JMenuItem();
        jMenuItemDeleteAllArt = new javax.swing.JMenuItem();
        jMenuItemDeleteAllConfig = new javax.swing.JMenuItem();
        jMenuItemDeleteAllCheat = new javax.swing.JMenuItem();
        jMenuItemDeleteAllELF = new javax.swing.JMenuItem();
        jMenuItemDeleteAllSpineART = new javax.swing.JMenuItem();
        jMenuConsole = new javax.swing.JMenu();
        jRadioButtonMenuItemPlaystation1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemPlaystation2 = new javax.swing.JRadioButtonMenuItem();
        jMenuModeSelect = new javax.swing.JMenu();
        jMenuItemChangeMode = new javax.swing.JMenuItem();
        jMenuConsoleFileTransfer = new javax.swing.JMenu();
        jMenuItemConsolePartitions = new javax.swing.JMenuItem();
        jMenuItemTransferFilesToConsole = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

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
        jTextFieldGameTitleDisplay.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameTitleDisplay.setPreferredSize(new java.awt.Dimension(402, 20));

        jTextFieldGameIDDisplay.setEditable(false);
        jTextFieldGameIDDisplay.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameIDDisplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameIDDisplay.setPreferredSize(new java.awt.Dimension(104, 20));

        jTextFieldGameSizeDisplay.setEditable(false);
        jTextFieldGameSizeDisplay.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameSizeDisplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameSizeDisplay.setPreferredSize(new java.awt.Dimension(80, 20));

        jTextFieldGameNumberDisplay.setEditable(false);
        jTextFieldGameNumberDisplay.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameNumberDisplay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameNumberDisplay.setPreferredSize(new java.awt.Dimension(109, 20));

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
                        .addComponent(jTextFieldGameIDDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelGameSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameSizeDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGameInformationLayout.createSequentialGroup()
                        .addComponent(jLabelGameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldGameTitleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGameNumberDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32))
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

        jLabelGameTitle.getAccessibleContext().setAccessibleName("lblGameTitle");
        jLabelGameID.getAccessibleContext().setAccessibleName("");

        jPanelGameCover.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Front Cover"));
        jPanelGameCover.setPreferredSize(new java.awt.Dimension(243, 341));

        jLabelGameFrontCover.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameFrontCover.setToolTipText("");
        jLabelGameFrontCover.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabelGameFrontCover.setPreferredSize(new java.awt.Dimension(207, 295));

        javax.swing.GroupLayout jPanelGameCoverLayout = new javax.swing.GroupLayout(jPanelGameCover);
        jPanelGameCover.setLayout(jPanelGameCoverLayout);
        jPanelGameCoverLayout.setHorizontalGroup(
            jPanelGameCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameCoverLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanelGameCoverLayout.setVerticalGroup(
            jPanelGameCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameCoverLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGameFrontCover, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabelCurrentConsoleDisplay.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelCurrentConsoleDisplay.setText("Console");

        jPanelGameTools.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Tools"));

        jButtonGameArt.setText("ART");
        jButtonGameArt.setToolTipText("");
        jButtonGameArt.setPreferredSize(new java.awt.Dimension(100, 23));
        jButtonGameArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGameArtActionPerformed(evt);
            }
        });

        jButtonCreateConfig.setText("CFG");
        jButtonCreateConfig.setToolTipText("");
        jButtonCreateConfig.setPreferredSize(new java.awt.Dimension(100, 23));
        jButtonCreateConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateConfigActionPerformed(evt);
            }
        });

        jButtonEditCheat.setText("CHT");
        jButtonEditCheat.setToolTipText("");
        jButtonEditCheat.setPreferredSize(new java.awt.Dimension(100, 23));
        jButtonEditCheat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditCheatActionPerformed(evt);
            }
        });

        jButtonVMC.setText("VMC");
        jButtonVMC.setToolTipText("");
        jButtonVMC.setPreferredSize(new java.awt.Dimension(100, 23));
        jButtonVMC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVMCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGameToolsLayout = new javax.swing.GroupLayout(jPanelGameTools);
        jPanelGameTools.setLayout(jPanelGameToolsLayout);
        jPanelGameToolsLayout.setHorizontalGroup(
            jPanelGameToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameToolsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelGameToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonGameArt, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonEditCheat, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanelGameToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonVMC, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCreateConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addGap(137, 137, 137))
        );
        jPanelGameToolsLayout.setVerticalGroup(
            jPanelGameToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGameToolsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonGameArt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCreateConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGameToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEditCheat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonVMC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanelGameList.setBorder(javax.swing.BorderFactory.createTitledBorder("Game List"));

        jScrollPane1.setViewportView(jListGameList);

        javax.swing.GroupLayout jPanelGameListLayout = new javax.swing.GroupLayout(jPanelGameList);
        jPanelGameList.setLayout(jPanelGameListLayout);
        jPanelGameListLayout.setHorizontalGroup(
            jPanelGameListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelGameListLayout.setVerticalGroup(
            jPanelGameListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameListLayout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanelGameDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Details"));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Genre:");
        jLabel1.setToolTipText("");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Release Date:");
        jLabel2.setToolTipText("");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Developer:");
        jLabel3.setToolTipText("");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("No of Players:");
        jLabel4.setToolTipText("");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Compatibility:");
        jLabel5.setToolTipText("");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("VMC0:");
        jLabel6.setToolTipText("");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("VMC1:");
        jLabel7.setToolTipText("");

        jTextFieldGameGenre.setEditable(false);
        jTextFieldGameGenre.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameGenre.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameGenre.setPreferredSize(new java.awt.Dimension(209, 20));

        jTextFieldGameDeveloper.setEditable(false);
        jTextFieldGameDeveloper.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameDeveloper.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameDeveloper.setPreferredSize(new java.awt.Dimension(209, 20));

        jTextFieldGameReleaseDate.setEditable(false);
        jTextFieldGameReleaseDate.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameReleaseDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameReleaseDate.setPreferredSize(new java.awt.Dimension(209, 20));

        jTextFieldGamePlayerNumber.setEditable(false);
        jTextFieldGamePlayerNumber.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGamePlayerNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGamePlayerNumber.setPreferredSize(new java.awt.Dimension(209, 20));

        jTextFieldGameDeviceCompatibility.setEditable(false);
        jTextFieldGameDeviceCompatibility.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameDeviceCompatibility.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameDeviceCompatibility.setPreferredSize(new java.awt.Dimension(209, 20));

        jTextFieldGameVMC0.setEditable(false);
        jTextFieldGameVMC0.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameVMC0.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameVMC0.setPreferredSize(new java.awt.Dimension(209, 20));

        jTextFieldGameVMC1.setEditable(false);
        jTextFieldGameVMC1.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldGameVMC1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGameVMC1.setPreferredSize(new java.awt.Dimension(209, 20));

        javax.swing.GroupLayout jPanelGameDetailsLayout = new javax.swing.GroupLayout(jPanelGameDetails);
        jPanelGameDetails.setLayout(jPanelGameDetailsLayout);
        jPanelGameDetailsLayout.setHorizontalGroup(
            jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextFieldGameDeveloper, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameVMC0, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameDeviceCompatibility, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGamePlayerNumber, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameReleaseDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameVMC1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGameGenre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameDetailsLayout.setVerticalGroup(
            jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelGameDetailsLayout.createSequentialGroup()
                        .addComponent(jTextFieldGameGenre, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelGameDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelGameDetailsLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(jTextFieldGameReleaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldGameDeveloper, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGamePlayerNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGameDeviceCompatibility, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGameVMC0, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGameVMC1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGameDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Stats"));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("PS1 Games:");
        jLabel8.setToolTipText("");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("PS2 Games:");
        jLabel9.setToolTipText("");

        jTextFieldPS1GameCount.setEditable(false);
        jTextFieldPS1GameCount.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldPS1GameCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPS1GameCount.setPreferredSize(new java.awt.Dimension(211, 20));

        jTextFieldPS2GameCount.setEditable(false);
        jTextFieldPS2GameCount.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldPS2GameCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPS2GameCount.setPreferredSize(new java.awt.Dimension(211, 20));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldPS2GameCount, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPS1GameCount, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPS1GameCount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPS2GameCount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenuFile.setText("File");

        jMenuItemOpenOPLDirectory.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpenOPLDirectory.setText("Open OPL Directory");
        jMenuItemOpenOPLDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenOPLDirectoryActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpenOPLDirectory);

        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemAbout);

        jMenuItemChangeLog.setText("Changelog");
        jMenuItemChangeLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangeLogActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemChangeLog);

        jMenuSettings.setText("Settings");

        jCheckBoxMenuPS1Compatability.setSelected(true);
        jCheckBoxMenuPS1Compatability.setText("Display PS1 Compatability");
        jCheckBoxMenuPS1Compatability.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuPS1CompatabilityActionPerformed(evt);
            }
        });
        jMenuSettings.add(jCheckBoxMenuPS1Compatability);

        jCheckBoxMenuItemPS2ULCFG.setSelected(true);
        jCheckBoxMenuItemPS2ULCFG.setText("Highlight PS2 UL Games");
        jCheckBoxMenuItemPS2ULCFG.setToolTipText("");
        jCheckBoxMenuItemPS2ULCFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemPS2ULCFGActionPerformed(evt);
            }
        });
        jMenuSettings.add(jCheckBoxMenuItemPS2ULCFG);

        jMenuGameIDPositionPS1.setText("PS1 Game ID Position");

        jRadioButtonMenuItemGameIDPositionBeginningPS1.setSelected(true);
        jRadioButtonMenuItemGameIDPositionBeginningPS1.setText("Before Game Name");
        jRadioButtonMenuItemGameIDPositionBeginningPS1.setToolTipText("");
        jRadioButtonMenuItemGameIDPositionBeginningPS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemGameIDPositionBeginningPS1ActionPerformed(evt);
            }
        });
        jMenuGameIDPositionPS1.add(jRadioButtonMenuItemGameIDPositionBeginningPS1);

        jRadioButtonMenuItemGameIDPositionEndPS1.setSelected(true);
        jRadioButtonMenuItemGameIDPositionEndPS1.setText("After Game Name");
        jRadioButtonMenuItemGameIDPositionEndPS1.setToolTipText("");
        jRadioButtonMenuItemGameIDPositionEndPS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemGameIDPositionEndPS1ActionPerformed(evt);
            }
        });
        jMenuGameIDPositionPS1.add(jRadioButtonMenuItemGameIDPositionEndPS1);

        jMenuSettings.add(jMenuGameIDPositionPS1);

        jMenuGameIDPositionPS2.setText("PS2 Game ID Position");
        jMenuGameIDPositionPS2.setToolTipText("");

        jRadioButtonMenuItemGameIDPositionBeginningPS2.setSelected(true);
        jRadioButtonMenuItemGameIDPositionBeginningPS2.setText("Before Game Name");
        jRadioButtonMenuItemGameIDPositionBeginningPS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemGameIDPositionBeginningPS2ActionPerformed(evt);
            }
        });
        jMenuGameIDPositionPS2.add(jRadioButtonMenuItemGameIDPositionBeginningPS2);

        jRadioButtonMenuItemGameIDPositionEndPS2.setSelected(true);
        jRadioButtonMenuItemGameIDPositionEndPS2.setText("After Game Name");
        jRadioButtonMenuItemGameIDPositionEndPS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemGameIDPositionEndPS2ActionPerformed(evt);
            }
        });
        jMenuGameIDPositionPS2.add(jRadioButtonMenuItemGameIDPositionEndPS2);

        jMenuSettings.add(jMenuGameIDPositionPS2);

        jMenuFile.add(jMenuSettings);

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBarMainMenu.add(jMenuFile);

        jMenuTools.setText("Tools");
        jMenuTools.setActionCommand("Batch");

        jMenuItemAddPS1Game.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItemAddPS1Game.setText("Add PS1 Game");
        jMenuItemAddPS1Game.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddPS1GameActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemAddPS1Game);

        jMenuItemAddPS2Game.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItemAddPS2Game.setText("Add PS2 Game");
        jMenuItemAddPS2Game.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddPS2GameActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemAddPS2Game);

        jMenuItemGenerateConfElm.setText("Generate conf_apps.cfg");
        jMenuItemGenerateConfElm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateConfElmActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateConfElm);

        jMenuItemGenerateULConf.setText("Generate ul.cfg");
        jMenuItemGenerateULConf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateULConfActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateULConf);

        jMenuItemSplitPS2Game.setText("Convert ISO to UL Format");
        jMenuItemSplitPS2Game.setEnabled(false);
        jMenuItemSplitPS2Game.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSplitPS2GameActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemSplitPS2Game);

        jMenuItemMergePS2Game.setText("Convert UL Format to ISO");
        jMenuItemMergePS2Game.setToolTipText("");
        jMenuItemMergePS2Game.setEnabled(false);
        jMenuItemMergePS2Game.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMergePS2GameActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemMergePS2Game);

        jMenuItemPS1Emulator.setText("PS1 Emulator Settings");
        jMenuItemPS1Emulator.setToolTipText("");
        jMenuItemPS1Emulator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPS1EmulatorActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemPS1Emulator);

        jMenuItemPS2Emulator.setText("PS2 Emulator Settings");
        jMenuItemPS2Emulator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPS2EmulatorActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemPS2Emulator);

        jMenuItemMD5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemMD5.setText("Perform MD5 Hash");
        jMenuItemMD5.setToolTipText("");
        jMenuItemMD5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMD5ActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemMD5);

        jMenuItemShareVMC.setText("Share VMC Files");
        jMenuItemShareVMC.setToolTipText("");
        jMenuItemShareVMC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemShareVMCActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemShareVMC);

        jMenuItemReportFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemReportFile.setText("Report a File");
        jMenuItemReportFile.setToolTipText("");
        jMenuItemReportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReportFileActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemReportFile);

        jMenuItemReportCompatability.setText("PS1 Compatability Report");
        jMenuItemReportCompatability.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReportCompatabilityActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemReportCompatability);

        jMenuItemRefreshGameList.setText("Refresh Game List");
        jMenuItemRefreshGameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRefreshGameListActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemRefreshGameList);

        jMenuItemCheckUpdate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCheckUpdate.setText("Check for Updates");
        jMenuItemCheckUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCheckUpdateActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemCheckUpdate);

        jMenuBarMainMenu.add(jMenuTools);

        jMenuBatchTools.setText("Batch Tools");

        jMenuItemBatchAddPS1Game.setText("Add PS1 Games");
        jMenuItemBatchAddPS1Game.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBatchAddPS1GameActionPerformed(evt);
            }
        });
        jMenuBatchTools.add(jMenuItemBatchAddPS1Game);

        jMenuItemBatchAddPS2Game.setText("Add PS2 Games");
        jMenuItemBatchAddPS2Game.setToolTipText("");
        jMenuItemBatchAddPS2Game.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBatchAddPS2GameActionPerformed(evt);
            }
        });
        jMenuBatchTools.add(jMenuItemBatchAddPS2Game);

        jMenuBatchDownload.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuBatchDownload.setText("Batch Download");
        jMenuBatchDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuBatchDownloadActionPerformed(evt);
            }
        });
        jMenuBatchTools.add(jMenuBatchDownload);

        jMenuItemBatchFileShare.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemBatchFileShare.setText("Share Files");
        jMenuItemBatchFileShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBatchFileShareActionPerformed(evt);
            }
        });
        jMenuBatchTools.add(jMenuItemBatchFileShare);

        jMenuItemCheckGameNames.setText("Check Long Game Names");
        jMenuItemCheckGameNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCheckGameNamesActionPerformed(evt);
            }
        });
        jMenuBatchTools.add(jMenuItemCheckGameNames);

        jMenuBatchPS1Elf.setText("Generate PS1 ELF Files");
        jMenuBatchPS1Elf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuBatchPS1ElfActionPerformed(evt);
            }
        });
        jMenuBatchTools.add(jMenuBatchPS1Elf);

        jMenuItemGenerateSpine.setText("Generate Spine ART");
        jMenuItemGenerateSpine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateSpineActionPerformed(evt);
            }
        });
        jMenuBatchTools.add(jMenuItemGenerateSpine);

        jMenuDeleteFiles.setText("Delete Files");

        jMenuItemDeleteUnusedArt.setText("Delete Unused ART Files");
        jMenuItemDeleteUnusedArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteUnusedArtActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteUnusedArt);

        jMenuItemDeleteUnusedConfig.setText("Delete Unused CFG Files");
        jMenuItemDeleteUnusedConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteUnusedConfigActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteUnusedConfig);

        jMenuItemDeleteUnusedCheat.setText("Delete Unused CHT Files");
        jMenuItemDeleteUnusedCheat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteUnusedCheatActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteUnusedCheat);

        jMenuItemDeleteAllArt.setText("Delete All ART Files");
        jMenuItemDeleteAllArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteAllArtActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteAllArt);

        jMenuItemDeleteAllConfig.setText("Delete All CFG Files");
        jMenuItemDeleteAllConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteAllConfigActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteAllConfig);

        jMenuItemDeleteAllCheat.setText("Delete All CHT Files");
        jMenuItemDeleteAllCheat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteAllCheatActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteAllCheat);

        jMenuItemDeleteAllELF.setText("Delete All ELF Files");
        jMenuItemDeleteAllELF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteAllELFActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteAllELF);

        jMenuItemDeleteAllSpineART.setText("Delete All Spine ART");
        jMenuItemDeleteAllSpineART.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteAllSpineARTActionPerformed(evt);
            }
        });
        jMenuDeleteFiles.add(jMenuItemDeleteAllSpineART);

        jMenuBatchTools.add(jMenuDeleteFiles);

        jMenuBarMainMenu.add(jMenuBatchTools);

        jMenuConsole.setText("Console");

        jRadioButtonMenuItemPlaystation1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        jRadioButtonMenuItemPlaystation1.setSelected(true);
        jRadioButtonMenuItemPlaystation1.setText("PlayStation 1");
        jRadioButtonMenuItemPlaystation1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemPlaystation1ActionPerformed(evt);
            }
        });
        jMenuConsole.add(jRadioButtonMenuItemPlaystation1);

        jRadioButtonMenuItemPlaystation2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        jRadioButtonMenuItemPlaystation2.setText("PlayStation 2");
        jRadioButtonMenuItemPlaystation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemPlaystation2ActionPerformed(evt);
            }
        });
        jMenuConsole.add(jRadioButtonMenuItemPlaystation2);

        jMenuBarMainMenu.add(jMenuConsole);

        jMenuModeSelect.setText("Mode");

        jMenuItemChangeMode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemChangeMode.setText("Set Mode");
        jMenuItemChangeMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangeModeActionPerformed(evt);
            }
        });
        jMenuModeSelect.add(jMenuItemChangeMode);

        jMenuBarMainMenu.add(jMenuModeSelect);

        jMenuConsoleFileTransfer.setText("Transfer Files");
        jMenuConsoleFileTransfer.setToolTipText("");

        jMenuItemConsolePartitions.setText("Set Remote File Locations");
        jMenuItemConsolePartitions.setToolTipText("");
        jMenuItemConsolePartitions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConsolePartitionsActionPerformed(evt);
            }
        });
        jMenuConsoleFileTransfer.add(jMenuItemConsolePartitions);

        jMenuItemTransferFilesToConsole.setText("Transfer Files to Console");
        jMenuItemTransferFilesToConsole.setToolTipText("");
        jMenuItemTransferFilesToConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTransferFilesToConsoleActionPerformed(evt);
            }
        });
        jMenuConsoleFileTransfer.add(jMenuItemTransferFilesToConsole);

        jMenuBarMainMenu.add(jMenuConsoleFileTransfer);

        setJMenuBar(jMenuBarMainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelGameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanelGameCover, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                    .addComponent(jPanelGameTools, javax.swing.GroupLayout.PREFERRED_SIZE, 175, Short.MAX_VALUE))
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanelGameDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jPanelGameInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 549, Short.MAX_VALUE)))
                    .addComponent(jLabelCurrentConsoleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelCurrentConsoleDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelGameInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanelGameCover, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelGameDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanelGameTools, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanelGameList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelGameInformation.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">    
    private void jMenuItemOpenOPLDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenOPLDirectoryActionPerformed
        if (!PopsGameManager.isOPLFolderSet()) {JOptionPane.showMessageDialog(null, "OPL directory has not been set.");}
        else {PopsGameManager.openDirectory(PopsGameManager.getOPLFolder());}
    }//GEN-LAST:event_jMenuItemOpenOPLDirectoryActionPerformed

    private void jRadioButtonMenuItemPlaystation1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemPlaystation1ActionPerformed
        switchConsolePS1();
    }//GEN-LAST:event_jRadioButtonMenuItemPlaystation1ActionPerformed

    private void jRadioButtonMenuItemPlaystation2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemPlaystation2ActionPerformed
        switchConsolePS2();
    }//GEN-LAST:event_jRadioButtonMenuItemPlaystation2ActionPerformed

    private void jButtonGameArtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGameArtActionPerformed
        
        GameCheatScreenNew gameCheatScreenNew = new GameCheatScreenNew(this, true);
        gameCheatScreenNew.setLocationRelativeTo(this);
        gameCheatScreenNew.setVisible(true);
        
        /*
        if (jListGameList.getSelectedIndex() != -1) {
            if (PopsGameManager.getCurrentConsole().equals("PS1")) {
                gameImageScreenPS1 = new GameImageScreenPS1(this, true);
                gameImageScreenPS1.initialiseGUI(jListGameList.getSelectedIndex());
                gameImageScreenPS1.setLocationRelativeTo(this);
                gameImageScreenPS1.setVisible(true);
            }
            else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
                gameImageScreenPS2 = new GameImageScreenPS2(this, true);
                gameImageScreenPS2.initialiseGUI(jListGameList.getSelectedIndex());
                gameImageScreenPS2.setLocationRelativeTo(this);
                gameImageScreenPS2.setVisible(true);
            }
        }
        else {JOptionPane.showMessageDialog(null,"You need to select a game before you can manage the game ART."," No game selected!",JOptionPane.WARNING_MESSAGE);}
        */
        
    }//GEN-LAST:event_jButtonGameArtActionPerformed

    private void jButtonCreateConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateConfigActionPerformed
        
        if (jListGameList.getSelectedIndex() != -1) {
            gameConfigScreen = new GameConfigScreen(this, true);
            if (PopsGameManager.getCurrentConsole().equals("PS1")) {gameConfigScreen.initialiseGUI(jListGameList.getSelectedIndex(),configManager.gameConfigExists(GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID(),GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName()));}
            else if (PopsGameManager.getCurrentConsole().equals("PS2")) {gameConfigScreen.initialiseGUI(jListGameList.getSelectedIndex(),configManager.gameConfigExists(GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID(),GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName()));}            
            gameConfigScreen.setLocationRelativeTo(this);
            gameConfigScreen.setVisible(true); 
            
            // Update displayed config details incase they have been modified
            displayGameDetails();
        }
        else {JOptionPane.showMessageDialog(null,"You need to select a game before you can create/edit the config file."," No game selected!",JOptionPane.WARNING_MESSAGE);}
    }//GEN-LAST:event_jButtonCreateConfigActionPerformed

    private void jButtonEditCheatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditCheatActionPerformed

        if (jListGameList.getSelectedIndex() != -1) {
            cheatScreen = new GameCheatScreen(this, true);
            cheatScreen.setLocationRelativeTo(this);
            cheatScreen.initialisGUI(jListGameList.getSelectedIndex());
            cheatScreen.setVisible(true);  
        }
        else {JOptionPane.showMessageDialog(null,"You need to select a game before you can create/edit the cheat file."," No game selected!",JOptionPane.WARNING_MESSAGE);}
    }//GEN-LAST:event_jButtonEditCheatActionPerformed

    private void jButtonVMCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVMCActionPerformed

        if (jListGameList.getSelectedIndex() != -1) {
            GameVMCScreen vmcScreen = new GameVMCScreen(this, true);
            vmcScreen.setLocationRelativeTo(this);
            vmcScreen.initialisGUI(jListGameList.getSelectedIndex());
            vmcScreen.setVisible(true);  
        }
        else {JOptionPane.showMessageDialog(null,"You need to select a game before you can download any VMC files."," No game selected!",JOptionPane.WARNING_MESSAGE);}

    }//GEN-LAST:event_jButtonVMCActionPerformed

    private void jMenuItemChangeModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangeModeActionPerformed
        SetModeScreen setModeScreen = new SetModeScreen(this, true);
        setModeScreen.initialiseGUI();
        setModeScreen.setLocationRelativeTo(this);
        setModeScreen.setVisible(true);
    }//GEN-LAST:event_jMenuItemChangeModeActionPerformed

    private void jMenuItemGenerateConfElmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateConfElmActionPerformed
        GameListManager.writeConfigELM();
    }//GEN-LAST:event_jMenuItemGenerateConfElmActionPerformed

    private void jMenuItemAddPS2GameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddPS2GameActionPerformed
        displayAddGameScreen();
    }//GEN-LAST:event_jMenuItemAddPS2GameActionPerformed

    private void jMenuItemAddPS1GameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddPS1GameActionPerformed
        displayAddGameScreen();
    }//GEN-LAST:event_jMenuItemAddPS1GameActionPerformed

    private void jMenuItemTransferFilesToConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTransferFilesToConsoleActionPerformed
        syncFileScreen = new SyncFileScreen(this, true);
        syncFileScreen.setLocationRelativeTo(this);
        syncFileScreen.setVisible(true);
    }//GEN-LAST:event_jMenuItemTransferFilesToConsoleActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuBatchDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuBatchDownloadActionPerformed

        // Display the batch download dialog screen
        if (PopsGameManager.getCurrentConsole().equals("PS1")){
            batchDownloadScreenPS1 = new BatchDownloadScreenPS1(this, true);
            batchDownloadScreenPS1.setLocationRelativeTo(this);
            batchDownloadScreenPS1.setVisible(true);    
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){
            batchDownloadScreenPS2 = new BatchDownloadScreenPS2(this, true);
            batchDownloadScreenPS2.setLocationRelativeTo(this);
            batchDownloadScreenPS2.setVisible(true);    
        }
    }//GEN-LAST:event_jMenuBatchDownloadActionPerformed

    private void jMenuItemPS2EmulatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPS2EmulatorActionPerformed
        emulatorSettingsScreen = new EmulatorSettingsScreen(this, true, "PS2");
        emulatorSettingsScreen.setLocationRelativeTo(this);
        emulatorSettingsScreen.setVisible(true);    
    }//GEN-LAST:event_jMenuItemPS2EmulatorActionPerformed

    private void jMenuItemCheckUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCheckUpdateActionPerformed
        checkForUpdate();
    }//GEN-LAST:event_jMenuItemCheckUpdateActionPerformed

    private void jMenuBatchPS1ElfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuBatchPS1ElfActionPerformed
        generateNewElfFiles();
    }//GEN-LAST:event_jMenuBatchPS1ElfActionPerformed

    private void jMenuItemBatchFileShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBatchFileShareActionPerformed
        BatchFileShareScreen batchArtShareScreen = new BatchFileShareScreen(this, true);
        batchArtShareScreen.setLocationRelativeTo(this);
        batchArtShareScreen.setVisible(true);    
    }//GEN-LAST:event_jMenuItemBatchFileShareActionPerformed

    private void jMenuItemPS1EmulatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPS1EmulatorActionPerformed
        emulatorSettingsScreen = new EmulatorSettingsScreen(this, true, "PS1");
        emulatorSettingsScreen.setLocationRelativeTo(this);
        emulatorSettingsScreen.setVisible(true);    
    }//GEN-LAST:event_jMenuItemPS1EmulatorActionPerformed

    private void jMenuItemReportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReportFileActionPerformed
        FileReportScreen reportScreen = new FileReportScreen(this, true, jListGameList.getSelectedIndex(), null);
        reportScreen.setLocationRelativeTo(this);
        reportScreen.setVisible(true);     
    }//GEN-LAST:event_jMenuItemReportFileActionPerformed

    private void jMenuItemMD5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMD5ActionPerformed

        // Display the MD5 Checker screen and pass in the selected file
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {
            String selectedGame = GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameName();
            
            if (new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + selectedGame + "-" + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID() + ".VCD").exists()){
                HashCheckerScreen hashCheckScreen = new HashCheckerScreen(this, true, new File(PopsGameManager.getOPLFolder() + File.separator + "POPS" + File.separator + selectedGame + "-" + GameListManager.getGamePS1(jListGameList.getSelectedIndex()).getGameID() + ".VCD"));
                hashCheckScreen.setLocationRelativeTo(this);
                hashCheckScreen.setVisible(true);
            }  
        }          
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
            String selectedGame = GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameName();
            String selectedGameID = GameListManager.getGamePS2(jListGameList.getSelectedIndex()).getGameID();
  
            if (new File(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + selectedGameID + "." + selectedGame + ".iso").exists()){
                HashCheckerScreen hashCheckScreen = new HashCheckerScreen(this, true, new File(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + selectedGameID + "." + selectedGame + ".iso"));
                hashCheckScreen.setLocationRelativeTo(this);
                hashCheckScreen.setVisible(true);
            }
        } 
    }//GEN-LAST:event_jMenuItemMD5ActionPerformed

    private void jMenuItemDeleteAllArtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteAllArtActionPerformed

        // Deletes all of the ART files
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the ART files?"," Delete ART",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteAllFiles("ART");}
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the ART files on your console?\n\nFTP server must be running on your console in order to perform this task."," Delete ART",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteAllFiles("ART");}
        } 
    }//GEN-LAST:event_jMenuItemDeleteAllArtActionPerformed

    private void jMenuItemDeleteAllConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteAllConfigActionPerformed
        
        // Deletes all of the CFG files
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the CFG files?"," Delete CFG",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteAllFiles("CFG");}
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the CFG files on your console?\n\nFTP server must be running on your console in order to perform this task."," Delete CFG",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteAllFiles("CFG");}
        } 
    }//GEN-LAST:event_jMenuItemDeleteAllConfigActionPerformed

    private void jMenuItemDeleteAllCheatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteAllCheatActionPerformed
        
        // Deletes all of the CHT files
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the CHT files?"," Delete CHT",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteAllFiles("CHT");}
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the CHT files on your console?\n\nFTP server must be running on your console in order to perform this task."," Delete CHT",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteAllFiles("CHT");}
        } 
    }//GEN-LAST:event_jMenuItemDeleteAllCheatActionPerformed

    private void jMenuItemDeleteUnusedArtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteUnusedArtActionPerformed

        // Deletes all of the un-used ART files
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the unused ART files?"," Delete Unused ART",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteUnusedFiles("ART");}
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the unused ART files on your console?\n\nFTP server must be running on your console in order to perform this task."," Delete Unused ART",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteUnusedFiles("ART");}
        } 
    }//GEN-LAST:event_jMenuItemDeleteUnusedArtActionPerformed

    private void jMenuItemDeleteUnusedConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteUnusedConfigActionPerformed
        
        // Deletes all of the un-used CFG files
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the unused CFG files?"," Delete Unused CFG",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteUnusedFiles("CFG");}
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the unused CFG files on your console?\n\nFTP server must be running on your console in order to perform this task."," Delete Unused CFG",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteUnusedFiles("CFG");}
        } 
    }//GEN-LAST:event_jMenuItemDeleteUnusedConfigActionPerformed

    private void jMenuItemDeleteUnusedCheatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteUnusedCheatActionPerformed
        
        // Deletes all of the un-used CHT files
        if (PopsGameManager.getCurrentMode().equals("SMB") || PopsGameManager.getCurrentMode().equals("HDD_USB")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the unused CHT files?"," Delete Unused CHT",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteUnusedFiles("CHT");}
        }
        else if (PopsGameManager.getCurrentMode().equals("HDD")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the unused CHT files on your console?\n\nFTP server must be running on your console in order to perform this task."," Delete Unused CHT",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){GameListManager.deleteUnusedFiles("CHT");}
        } 
    }//GEN-LAST:event_jMenuItemDeleteUnusedCheatActionPerformed

    private void jMenuItemConsolePartitionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConsolePartitionsActionPerformed

        SetPartitionScreen setPartitionScreen = new SetPartitionScreen(this, true);
        setPartitionScreen.initialiseGUI();
        setPartitionScreen.setLocationRelativeTo(this);
        setPartitionScreen.setVisible(true);
        
    }//GEN-LAST:event_jMenuItemConsolePartitionsActionPerformed

    private void jMenuItemRefreshGameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRefreshGameListActionPerformed
       
        // This gets the latest PS1 or PS2 game list from the console and updates the list in the GUI
        if (PopsGameManager.getCurrentConsole().equals("PS1")){
            int dialogResult = JOptionPane.showConfirmDialog (null, "FTP server must be running on your console in order to perform this task!"," Refresh PS1 Game List",JOptionPane.OK_CANCEL_OPTION);
            if(dialogResult == JOptionPane.OK_OPTION){
                
                // Try and get the list of PS1 games from the console and write the data to the PS1_List.dat file
                List<Game> gameListPS1 = GameListManager.getGameListFromConsolePS1();
                if (gameListPS1 != null){
                    GameListManager.writeGameListFilePS1(gameListPS1);
                    GameListManager.setGameListPS1(gameListPS1);
                    updateGameList(null, 0);
                }
            }  
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")){

            int dialogResult = JOptionPane.showConfirmDialog (null, "HDL Server must be running on your console in order to perform this task!"," Refresh PS2 Game List",JOptionPane.OK_CANCEL_OPTION);
            if(dialogResult == JOptionPane.OK_OPTION){
                
                HDLDumpManager hdlDump = new HDLDumpManager();
                try {
                    List<Game> gameListPS2 = hdlDump.hdlDumpGetTOC(PopsGameManager.getPS2IP()); 
                    if (gameListPS2 != null){
                        GameListManager.writeGameListFilePS2(gameListPS2);
                        GameListManager.setGameListPS2(gameListPS2);
                        updateGameList(null, 0);
                    }
                } 
                catch (IOException ex) {JOptionPane.showMessageDialog(null,"There was a problem launching HDL_Dump!"," HDL_Dump Error!",JOptionPane.ERROR_MESSAGE);} 
                catch (InterruptedException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            } 
        } 
    }//GEN-LAST:event_jMenuItemRefreshGameListActionPerformed

    private void jMenuItemShareVMCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemShareVMCActionPerformed
        ShareVMCScreen shareVMCScreen = new ShareVMCScreen(this, true, jListGameList.getSelectedIndex());
        shareVMCScreen.setLocationRelativeTo(this);
        shareVMCScreen.setVisible(true);
    }//GEN-LAST:event_jMenuItemShareVMCActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        AboutScreen aboutScreen = new AboutScreen(this, true, PopsGameManager.getFormTitle(), PopsGameManager.getApplicationReleaseDate());
        aboutScreen.setLocationRelativeTo(this);
        aboutScreen.setVisible(true);   
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemBatchAddPS1GameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBatchAddPS1GameActionPerformed
        displayBatchAddGameScreen();
    }//GEN-LAST:event_jMenuItemBatchAddPS1GameActionPerformed

    private void jMenuItemBatchAddPS2GameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBatchAddPS2GameActionPerformed
        displayBatchAddGameScreen();
    }//GEN-LAST:event_jMenuItemBatchAddPS2GameActionPerformed

    private void jMenuItemChangeLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangeLogActionPerformed
        ChangelogScreen changelogScreen = new ChangelogScreen(this, true);
        changelogScreen.setLocationRelativeTo(this);
        changelogScreen.setVisible(true);   
    }//GEN-LAST:event_jMenuItemChangeLogActionPerformed

    private void jCheckBoxMenuPS1CompatabilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuPS1CompatabilityActionPerformed
        initialiseGUI(jListGameList.getSelectedIndex());
        try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug("Error saving the settings!\n\n" + ex.toString());}
    }//GEN-LAST:event_jCheckBoxMenuPS1CompatabilityActionPerformed

    private void jMenuItemCheckGameNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCheckGameNamesActionPerformed
        checkLongGameNames();
    }//GEN-LAST:event_jMenuItemCheckGameNamesActionPerformed

    private void jMenuItemReportCompatabilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReportCompatabilityActionPerformed
        
    }//GEN-LAST:event_jMenuItemReportCompatabilityActionPerformed

    private void jMenuItemDeleteAllELFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteAllELFActionPerformed

        switch (PopsGameManager.getCurrentMode()) {
            case "HDD_USB":
            case "SMB":
                
                int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to delete all of the ELF files in the POPS directory?"," Delete All ELF Files",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    
                    // Delete all of the ELF files in the POPS directory
                    File folder = new File(PopsGameManager.getOPLFolder() + File.separator + "POPS");
                    File[] listOfFiles = folder.listFiles();
                    for (File file : listOfFiles) {if (file.isFile()) {if (file.getName().substring(file.getName().length()-3, file.getName().length()).equals("ELF")){file.delete();}}}
                }
                break;
            case "HDD":
                
                break;
        } 
    }//GEN-LAST:event_jMenuItemDeleteAllELFActionPerformed

    private void jMenuItemGenerateSpineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateSpineActionPerformed

        GenerateSpineART generateSpineART = new GenerateSpineART();
        
        if (PopsGameManager.getCurrentConsole().equals("PS1")) {
            generateSpineART.generateForPS1();
        }
        else if (PopsGameManager.getCurrentConsole().equals("PS2")) {
            generateSpineART.generateForPS2();
        }
        
        
        
    }//GEN-LAST:event_jMenuItemGenerateSpineActionPerformed

    private void jMenuItemDeleteAllSpineARTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteAllSpineARTActionPerformed
        GenerateSpineART generateSpineART = new GenerateSpineART();
        generateSpineART.deleteForPS2();
    }//GEN-LAST:event_jMenuItemDeleteAllSpineARTActionPerformed

    private void jMenuItemGenerateULConfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateULConfActionPerformed

        int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure that you want to re-generate the ul.cfg file?\nThis will overwite the file if it already exists!"," Generate ul.cfg File",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            USBUtil.regenerateULCFG();
            GameListManager.createGameListsPS2(false);
            initialiseGUI(jListGameList.getSelectedIndex());
        }
    }//GEN-LAST:event_jMenuItemGenerateULConfActionPerformed

    private void jMenuItemSplitPS2GameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSplitPS2GameActionPerformed

        if (GameListManager.getGameListPS2().get(jListGameList.getSelectedIndex()).getGameName().length()<=32){
            SplitMergeScreen splitMergerScreen = new SplitMergeScreen(this, true, GameListManager.getGameListPS2().get(jListGameList.getSelectedIndex()), "Split");
            splitMergerScreen.setLocationRelativeTo(this);
            splitMergerScreen.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(null,"You cannot split games that have names greater than 32 characters in length."," Game Name is Too Long!",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItemSplitPS2GameActionPerformed

    private void jMenuItemMergePS2GameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMergePS2GameActionPerformed
        SplitMergeScreen splitMergerScreen = new SplitMergeScreen(this, true, GameListManager.getGameListPS2().get(jListGameList.getSelectedIndex()), "Merge");
        splitMergerScreen.setLocationRelativeTo(this);
        splitMergerScreen.setVisible(true);
    }//GEN-LAST:event_jMenuItemMergePS2GameActionPerformed

    private void jCheckBoxMenuItemPS2ULCFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemPS2ULCFGActionPerformed
        initialiseGUI(jListGameList.getSelectedIndex());
        
        try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug("Error saving the settings!\n\n" + ex.toString());}
    }//GEN-LAST:event_jCheckBoxMenuItemPS2ULCFGActionPerformed

    private void jRadioButtonMenuItemGameIDPositionBeginningPS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemGameIDPositionBeginningPS1ActionPerformed
        if (jRadioButtonMenuItemGameIDPositionBeginningPS1.isSelected()) {
            jRadioButtonMenuItemGameIDPositionEndPS1.setSelected(false);
            PopsGameManager.setGameIDPositionPS1("start");
            gameIDPositionChanged();
        }
    }//GEN-LAST:event_jRadioButtonMenuItemGameIDPositionBeginningPS1ActionPerformed

    private void jRadioButtonMenuItemGameIDPositionEndPS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemGameIDPositionEndPS1ActionPerformed
        if (jRadioButtonMenuItemGameIDPositionEndPS1.isSelected()) {
            jRadioButtonMenuItemGameIDPositionBeginningPS1.setSelected(false);
            PopsGameManager.setGameIDPositionPS1("end");
            gameIDPositionChanged();
        }
    }//GEN-LAST:event_jRadioButtonMenuItemGameIDPositionEndPS1ActionPerformed

    private void jRadioButtonMenuItemGameIDPositionBeginningPS2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemGameIDPositionBeginningPS2ActionPerformed
        if (jRadioButtonMenuItemGameIDPositionBeginningPS2.isSelected()) {
            jRadioButtonMenuItemGameIDPositionEndPS2.setSelected(false);
            PopsGameManager.setGameIDPositionPS2("start");
            gameIDPositionChanged();
        }
    }//GEN-LAST:event_jRadioButtonMenuItemGameIDPositionBeginningPS2ActionPerformed

    private void jRadioButtonMenuItemGameIDPositionEndPS2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemGameIDPositionEndPS2ActionPerformed
        if (jRadioButtonMenuItemGameIDPositionEndPS2.isSelected()) {
            jRadioButtonMenuItemGameIDPositionBeginningPS2.setSelected(false);
            PopsGameManager.setGameIDPositionPS2("end");
            gameIDPositionChanged();
        }
    }//GEN-LAST:event_jRadioButtonMenuItemGameIDPositionEndPS2ActionPerformed
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="Generated variables">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCreateConfig;
    private javax.swing.JButton jButtonEditCheat;
    private javax.swing.JButton jButtonGameArt;
    private javax.swing.JButton jButtonVMC;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemPS2ULCFG;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuPS1Compatability;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCurrentConsoleDisplay;
    private javax.swing.JLabel jLabelGameFrontCover;
    private javax.swing.JLabel jLabelGameID;
    private javax.swing.JLabel jLabelGameSize;
    private javax.swing.JLabel jLabelGameTitle;
    private javax.swing.JList<String> jListGameList;
    private javax.swing.JMenuBar jMenuBarMainMenu;
    private javax.swing.JMenuItem jMenuBatchDownload;
    private javax.swing.JMenuItem jMenuBatchPS1Elf;
    private javax.swing.JMenu jMenuBatchTools;
    private javax.swing.JMenu jMenuConsole;
    private javax.swing.JMenu jMenuConsoleFileTransfer;
    private javax.swing.JMenu jMenuDeleteFiles;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuGameIDPositionPS1;
    private javax.swing.JMenu jMenuGameIDPositionPS2;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemAddPS1Game;
    private javax.swing.JMenuItem jMenuItemAddPS2Game;
    private javax.swing.JMenuItem jMenuItemBatchAddPS1Game;
    private javax.swing.JMenuItem jMenuItemBatchAddPS2Game;
    private javax.swing.JMenuItem jMenuItemBatchFileShare;
    private javax.swing.JMenuItem jMenuItemChangeLog;
    private javax.swing.JMenuItem jMenuItemChangeMode;
    private javax.swing.JMenuItem jMenuItemCheckGameNames;
    private javax.swing.JMenuItem jMenuItemCheckUpdate;
    private javax.swing.JMenuItem jMenuItemConsolePartitions;
    private javax.swing.JMenuItem jMenuItemDeleteAllArt;
    private javax.swing.JMenuItem jMenuItemDeleteAllCheat;
    private javax.swing.JMenuItem jMenuItemDeleteAllConfig;
    private javax.swing.JMenuItem jMenuItemDeleteAllELF;
    private javax.swing.JMenuItem jMenuItemDeleteAllSpineART;
    private javax.swing.JMenuItem jMenuItemDeleteUnusedArt;
    private javax.swing.JMenuItem jMenuItemDeleteUnusedCheat;
    private javax.swing.JMenuItem jMenuItemDeleteUnusedConfig;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGenerateConfElm;
    private javax.swing.JMenuItem jMenuItemGenerateSpine;
    private javax.swing.JMenuItem jMenuItemGenerateULConf;
    private javax.swing.JMenuItem jMenuItemMD5;
    private javax.swing.JMenuItem jMenuItemMergePS2Game;
    private javax.swing.JMenuItem jMenuItemOpenOPLDirectory;
    private javax.swing.JMenuItem jMenuItemPS1Emulator;
    private javax.swing.JMenuItem jMenuItemPS2Emulator;
    private javax.swing.JMenuItem jMenuItemRefreshGameList;
    private javax.swing.JMenuItem jMenuItemReportCompatability;
    private javax.swing.JMenuItem jMenuItemReportFile;
    private javax.swing.JMenuItem jMenuItemShareVMC;
    private javax.swing.JMenuItem jMenuItemSplitPS2Game;
    private javax.swing.JMenuItem jMenuItemTransferFilesToConsole;
    private javax.swing.JMenu jMenuModeSelect;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelGameCover;
    private javax.swing.JPanel jPanelGameDetails;
    private javax.swing.JPanel jPanelGameInformation;
    private javax.swing.JPanel jPanelGameList;
    private javax.swing.JPanel jPanelGameTools;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemGameIDPositionBeginningPS1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemGameIDPositionBeginningPS2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemGameIDPositionEndPS1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemGameIDPositionEndPS2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemPlaystation1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemPlaystation2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldGameDeveloper;
    private javax.swing.JTextField jTextFieldGameDeviceCompatibility;
    private javax.swing.JTextField jTextFieldGameGenre;
    private javax.swing.JTextField jTextFieldGameIDDisplay;
    private javax.swing.JTextField jTextFieldGameNumberDisplay;
    private javax.swing.JTextField jTextFieldGamePlayerNumber;
    private javax.swing.JTextField jTextFieldGameReleaseDate;
    private javax.swing.JTextField jTextFieldGameSizeDisplay;
    private javax.swing.JTextField jTextFieldGameTitleDisplay;
    private javax.swing.JTextField jTextFieldGameVMC0;
    private javax.swing.JTextField jTextFieldGameVMC1;
    private javax.swing.JTextField jTextFieldPS1GameCount;
    private javax.swing.JTextField jTextFieldPS2GameCount;
    // End of variables declaration//GEN-END:variables
// </editor-fold>
}