package oplpops.game.manager;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class SetModeScreen extends javax.swing.JDialog {
    private String currentlySelectedMode = null;
    private String oplFolder = null;
    private String selectedConsole = "PS2";
    
    // Temporary game lists
    private List<Game> gameListPS1 = new ArrayList<>();
    private List<Game> gameListPS2 = new ArrayList<>();
    
    public SetModeScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        overideClose();
    }
    
    
    // This initialises the GUI controls
    public void initialiseGUI(){
        
        if (PopsGameManager.getFisrtLaunch()) {
            currentlySelectedMode = "SMB";
            PopsGameManager.setCurrentConsole("PS2");
            PopsGameManager.setFisrtLaunch(false);
        }
        else if (currentlySelectedMode == null) {currentlySelectedMode = PopsGameManager.getCurrentMode();}

        jFormattedTextPathHDD.setText("");
        jTextFieldPathSMB.setText("");
        jTextFieldPathUSB.setText("");
        
        // Remove the button margins to enable smaller buttons
        jButtonBrowseSMB.setMargin(new Insets(0,0,0,0));
        jButtonConnectHDD.setMargin(new Insets(0,0,0,0));
        jButtonBrowseUSB.setMargin(new Insets(0,0,0,0));
        jButtonSaveMode.setMargin(new Insets(0,0,0,0));
        jButtonCancel.setMargin(new Insets(0,0,0,0));
        
        Color colourGray = new Color(240, 240, 240, 255);
        
        switch (currentlySelectedMode) {
            case "SMB":
                jRadioModeSelectSMB.setSelected(true);
                jRadioModeSelectUSB.setSelected(false);
                jRadioModeSelectHDD.setSelected(false);
                jButtonBrowseSMB.setEnabled(true);
                jTextFieldPathSMB.setEditable(true);
                if (PopsGameManager.isOPLFolderSet() && PopsGameManager.getCurrentMode().equals(currentlySelectedMode)) {jTextFieldPathSMB.setText(PopsGameManager.getOPLFolder());}
                jButtonConnectHDD.setEnabled(false);
                jFormattedTextPathHDD.setEditable(false);
                jButtonBrowseUSB.setEnabled(false);
                jLabelPathSMB.setEnabled(true);
                jLabelIP.setEnabled(false);
                jLabelIPathUSB.setEnabled(false);
                jTextFieldPathUSB.setBackground(colourGray);
                break;
            case "HDD_USB":
                jRadioModeSelectUSB.setSelected(true);
                jRadioModeSelectSMB.setSelected(false);
                jRadioModeSelectHDD.setSelected(false);
                jButtonBrowseUSB.setEnabled(true);
                jButtonConnectHDD.setEnabled(false);
                jFormattedTextPathHDD.setEditable(false);
                jButtonBrowseSMB.setEnabled(false);
                jTextFieldPathSMB.setEditable(false);
                jLabelPathSMB.setEnabled(false);
                jLabelIP.setEnabled(false);
                jLabelIPathUSB.setEnabled(true);
                jTextFieldPathUSB.setBackground(Color.white);
                if (PopsGameManager.isOPLFolderSet() && PopsGameManager.getCurrentMode().equals(currentlySelectedMode)) {jTextFieldPathUSB.setText(PopsGameManager.getOPLFolder());}
                break;
            case "HDD":
                jRadioModeSelectUSB.setSelected(false);
                jRadioModeSelectSMB.setSelected(false);
                jRadioModeSelectHDD.setSelected(true);
                jButtonConnectHDD.setEnabled(true);
                jFormattedTextPathHDD.setEditable(true);
                
                if (PopsGameManager.getPS2IP() != null) {
                    String[] splitIP = PopsGameManager.getPS2IP().split("\\.");
                    splitIP[2] = String.format("%03d", Integer.parseInt(splitIP[2]));
                    splitIP[3] = String.format("%03d", Integer.parseInt(splitIP[3]));
                    jFormattedTextPathHDD.setText(splitIP[0] + splitIP[1] + splitIP[2] + splitIP[3]);
                }

                jButtonBrowseSMB.setEnabled(false);
                jTextFieldPathSMB.setEditable(false);
                jButtonBrowseUSB.setEnabled(false);
                jLabelPathSMB.setEnabled(false);
                jLabelIP.setEnabled(true);
                jLabelIPathUSB.setEnabled(false);
                jTextFieldPathUSB.setBackground(colourGray);
                break;
        }   
    }
    
    
    // Try to connect to the PS2 console using the HDL_Dump application
    private void connectToPS2(){

        jButtonSaveMode.setEnabled(false);
        PopsGameManager.setPS2IP(jFormattedTextPathHDD.getText());
        
        // Get the list of PS2 games from the console using HDL Dump
        int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to get the list of PS2 games on your console?\n\nHDL Server must be running on your console in order to perform this task!"," Connect to PlayStation 2",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){

            selectedConsole = "PS2";
            HDLDumpManager hdlDump = new HDLDumpManager();
            try {
                gameListPS2 = hdlDump.hdlDumpGetTOC(jFormattedTextPathHDD.getText()); 
                if (gameListPS2 != null){GameListManager.writeGameListFilePS2(gameListPS2);}
            } 
            catch (IOException | InterruptedException ex) {
                JOptionPane.showMessageDialog(null,"There was a problem launching HDL_Dump!\nEnsure that you have admin privileges in order to run HDL_Dump."," HDL_Dump Error!",JOptionPane.ERROR_MESSAGE);
                PopsGameManager.displayErrorMessageDebug("Error launching hdl_dump!\n\n" + ex.toString());
            } 
        }
        
        dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to get the list of PS1 games on your console?\n\nThe FTP Server must be running on your console in order to perform this task!"," Connect to PlayStation 2",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION) {

            selectedConsole = "PS1";
            
            // Try and get the list of PS1 games from the console and write the data to the PS1_List.dat file
            gameListPS1 = GameListManager.getGameListFromConsolePS1();
            if (gameListPS1 != null){GameListManager.writeGameListFilePS1(gameListPS1);}
        }  

        // Ensure that the local hdd folder exists and then switch to it
        File hdlLocalDirectory = new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator);
        if (hdlLocalDirectory.exists() && hdlLocalDirectory.isDirectory()) {oplFolder = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator;}
        else {
            hdlLocalDirectory.mkdir();
            oplFolder = oplFolder = PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator;
        }
        
        jButtonSaveMode.setEnabled(true);
    }
    
    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                
                if (!PopsGameManager.isOPLFolderSet()){

                    int dialogResult = JOptionPane.showConfirmDialog (null, "You have not set the OPL directory. \n\nDo you want to exit the application?"," OPL Directory Not Set!",JOptionPane.YES_NO_OPTION);
                    if(dialogResult == JOptionPane.YES_OPTION){Runtime.getRuntime().exit(0);} 
                } 
                else {dispose();}
            }
        });
        this.setTitle(" Select Mode");
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelModeSelect = new javax.swing.JPanel();
        jPanelmodeSMB = new javax.swing.JPanel();
        jRadioModeSelectSMB = new javax.swing.JRadioButton();
        jLabelPathSMB = new javax.swing.JLabel();
        jTextFieldPathSMB = new javax.swing.JTextField();
        jButtonBrowseSMB = new javax.swing.JButton();
        jPanelmodeHDD = new javax.swing.JPanel();
        jRadioModeSelectHDD = new javax.swing.JRadioButton();
        jLabelIP = new javax.swing.JLabel();
        jButtonConnectHDD = new javax.swing.JButton();
        jFormattedTextPathHDD = new javax.swing.JFormattedTextField();
        jPanelmodeUSB = new javax.swing.JPanel();
        jRadioModeSelectUSB = new javax.swing.JRadioButton();
        jLabelIPathUSB = new javax.swing.JLabel();
        jTextFieldPathUSB = new javax.swing.JTextField();
        jButtonBrowseUSB = new javax.swing.JButton();
        jPanelmodeUSBHDD = new javax.swing.JPanel();
        jRadioModeSelectUSBHDD = new javax.swing.JRadioButton();
        jLabelIPathUSBHDD = new javax.swing.JLabel();
        jTextFieldPathUSBHDD = new javax.swing.JTextField();
        jButtonBrowseUSBHDD = new javax.swing.JButton();
        jButtonSaveMode = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelModeSelect.setBorder(javax.swing.BorderFactory.createTitledBorder("Mode Select"));

        jPanelmodeSMB.setBorder(javax.swing.BorderFactory.createTitledBorder("SMB Mode"));
        jPanelmodeSMB.setPreferredSize(new java.awt.Dimension(425, 140));

        jRadioModeSelectSMB.setSelected(true);
        jRadioModeSelectSMB.setText("OPL folder is on this PC.");
        jRadioModeSelectSMB.setActionCommand("");
        jRadioModeSelectSMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioModeSelectSMBActionPerformed(evt);
            }
        });

        jLabelPathSMB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPathSMB.setText("Path:");
        jLabelPathSMB.setPreferredSize(new java.awt.Dimension(35, 25));

        jTextFieldPathSMB.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPathSMB.setToolTipText("");
        jTextFieldPathSMB.setPreferredSize(new java.awt.Dimension(335, 25));

        jButtonBrowseSMB.setText("Browse");
        jButtonBrowseSMB.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonBrowseSMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseSMBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelmodeSMBLayout = new javax.swing.GroupLayout(jPanelmodeSMB);
        jPanelmodeSMB.setLayout(jPanelmodeSMBLayout);
        jPanelmodeSMBLayout.setHorizontalGroup(
            jPanelmodeSMBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeSMBLayout.createSequentialGroup()
                .addGroup(jPanelmodeSMBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelmodeSMBLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jRadioModeSelectSMB))
                    .addGroup(jPanelmodeSMBLayout.createSequentialGroup()
                        .addComponent(jLabelPathSMB, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldPathSMB, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonBrowseSMB, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanelmodeSMBLayout.setVerticalGroup(
            jPanelmodeSMBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeSMBLayout.createSequentialGroup()
                .addComponent(jRadioModeSelectSMB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelmodeSMBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPathSMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPathSMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowseSMB, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelmodeHDD.setBorder(javax.swing.BorderFactory.createTitledBorder("HDD Mode"));
        jPanelmodeHDD.setPreferredSize(new java.awt.Dimension(425, 140));

        jRadioModeSelectHDD.setSelected(true);
        jRadioModeSelectHDD.setText("OPL folder is on internal PS2 HDD.");
        jRadioModeSelectHDD.setActionCommand("");
        jRadioModeSelectHDD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioModeSelectHDDActionPerformed(evt);
            }
        });

        jLabelIP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelIP.setText("PS2 IP:");
        jLabelIP.setPreferredSize(new java.awt.Dimension(35, 25));

        jButtonConnectHDD.setText("Connect");
        jButtonConnectHDD.setPreferredSize(new java.awt.Dimension(92, 23));
        jButtonConnectHDD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectHDDActionPerformed(evt);
            }
        });

        try {
            jFormattedTextPathHDD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###.###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextPathHDD.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextPathHDD.setPreferredSize(new java.awt.Dimension(335, 25));

        javax.swing.GroupLayout jPanelmodeHDDLayout = new javax.swing.GroupLayout(jPanelmodeHDD);
        jPanelmodeHDD.setLayout(jPanelmodeHDDLayout);
        jPanelmodeHDDLayout.setHorizontalGroup(
            jPanelmodeHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeHDDLayout.createSequentialGroup()
                .addGroup(jPanelmodeHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelmodeHDDLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jRadioModeSelectHDD))
                    .addGroup(jPanelmodeHDDLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabelIP, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextPathHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonConnectHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanelmodeHDDLayout.setVerticalGroup(
            jPanelmodeHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeHDDLayout.createSequentialGroup()
                .addComponent(jRadioModeSelectHDD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelmodeHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonConnectHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextPathHDD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelmodeUSB.setBorder(javax.swing.BorderFactory.createTitledBorder("USB Mode"));
        jPanelmodeUSB.setPreferredSize(new java.awt.Dimension(425, 140));

        jRadioModeSelectUSB.setText("OPL folder is on USB drive.");
        jRadioModeSelectUSB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioModeSelectUSBActionPerformed(evt);
            }
        });

        jLabelIPathUSB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelIPathUSB.setText("Path:");
        jLabelIPathUSB.setPreferredSize(new java.awt.Dimension(35, 25));

        jTextFieldPathUSB.setEditable(false);
        jTextFieldPathUSB.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPathUSB.setToolTipText("");
        jTextFieldPathUSB.setPreferredSize(new java.awt.Dimension(335, 25));

        jButtonBrowseUSB.setText("Browse");
        jButtonBrowseUSB.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonBrowseUSB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseUSBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelmodeUSBLayout = new javax.swing.GroupLayout(jPanelmodeUSB);
        jPanelmodeUSB.setLayout(jPanelmodeUSBLayout);
        jPanelmodeUSBLayout.setHorizontalGroup(
            jPanelmodeUSBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeUSBLayout.createSequentialGroup()
                .addGroup(jPanelmodeUSBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelmodeUSBLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jRadioModeSelectUSB))
                    .addGroup(jPanelmodeUSBLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabelIPathUSB, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldPathUSB, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonBrowseUSB, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanelmodeUSBLayout.setVerticalGroup(
            jPanelmodeUSBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeUSBLayout.createSequentialGroup()
                .addComponent(jRadioModeSelectUSB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelmodeUSBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIPathUSB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPathUSB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowseUSB, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanelmodeUSBHDD.setBorder(javax.swing.BorderFactory.createTitledBorder("USB - HDD Mode"));
        jPanelmodeUSBHDD.setPreferredSize(new java.awt.Dimension(425, 140));

        jRadioModeSelectUSBHDD.setText("OPL folder is on PS2 formatted external HDD.");
        jRadioModeSelectUSBHDD.setToolTipText("");
        jRadioModeSelectUSBHDD.setActionCommand("OPL folder is on external HDD connected via USB.");
        jRadioModeSelectUSBHDD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioModeSelectUSBHDDActionPerformed(evt);
            }
        });

        jLabelIPathUSBHDD.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelIPathUSBHDD.setText("Path:");
        jLabelIPathUSBHDD.setPreferredSize(new java.awt.Dimension(35, 25));

        jTextFieldPathUSBHDD.setEditable(false);
        jTextFieldPathUSBHDD.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPathUSBHDD.setToolTipText("");
        jTextFieldPathUSBHDD.setPreferredSize(new java.awt.Dimension(335, 25));

        jButtonBrowseUSBHDD.setText("Browse");
        jButtonBrowseUSBHDD.setPreferredSize(new java.awt.Dimension(90, 23));
        jButtonBrowseUSBHDD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseUSBHDDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelmodeUSBHDDLayout = new javax.swing.GroupLayout(jPanelmodeUSBHDD);
        jPanelmodeUSBHDD.setLayout(jPanelmodeUSBHDDLayout);
        jPanelmodeUSBHDDLayout.setHorizontalGroup(
            jPanelmodeUSBHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeUSBHDDLayout.createSequentialGroup()
                .addGroup(jPanelmodeUSBHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelmodeUSBHDDLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jRadioModeSelectUSBHDD))
                    .addGroup(jPanelmodeUSBHDDLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabelIPathUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldPathUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonBrowseUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanelmodeUSBHDDLayout.setVerticalGroup(
            jPanelmodeUSBHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelmodeUSBHDDLayout.createSequentialGroup()
                .addComponent(jRadioModeSelectUSBHDD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelmodeUSBHDDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIPathUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPathUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowseUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelModeSelectLayout = new javax.swing.GroupLayout(jPanelModeSelect);
        jPanelModeSelect.setLayout(jPanelModeSelectLayout);
        jPanelModeSelectLayout.setHorizontalGroup(
            jPanelModeSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelModeSelectLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelModeSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelModeSelectLayout.createSequentialGroup()
                        .addComponent(jPanelmodeUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelModeSelectLayout.createSequentialGroup()
                        .addGroup(jPanelModeSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelmodeSMB, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelmodeHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelmodeUSB, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanelModeSelectLayout.setVerticalGroup(
            jPanelModeSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelModeSelectLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelmodeSMB, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelmodeHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelmodeUSB, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanelmodeUSBHDD, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButtonSaveMode.setText("Save");
        jButtonSaveMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveModeActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
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
                .addContainerGap()
                .addComponent(jPanelModeSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonSaveMode, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(220, 220, 220))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelModeSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSaveMode, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button click events"> 
    private void jRadioModeSelectSMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioModeSelectSMBActionPerformed
        currentlySelectedMode = "SMB";
        initialiseGUI();
    }//GEN-LAST:event_jRadioModeSelectSMBActionPerformed

    private void jRadioModeSelectUSBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioModeSelectUSBActionPerformed
        currentlySelectedMode = "HDD_USB";
        initialiseGUI();
    }//GEN-LAST:event_jRadioModeSelectUSBActionPerformed

    private void jRadioModeSelectHDDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioModeSelectHDDActionPerformed
        currentlySelectedMode = "HDD";
        initialiseGUI();
    }//GEN-LAST:event_jRadioModeSelectHDDActionPerformed

    private void jButtonSaveModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveModeActionPerformed
        
        if (oplFolder != null && currentlySelectedMode != null){
            
            PopsGameManager.setOPLFolder(oplFolder);
            PopsGameManager.setCurrentMode(currentlySelectedMode);
            
            // Create any missing folders
            if (!new File(oplFolder + File.separator + "ART").exists()) {new File(oplFolder + File.separator + "ART").mkdir();}
            if (!new File(oplFolder + File.separator + "CFG").exists()) {new File(oplFolder + File.separator + "CFG").mkdir();}
            if (!new File(oplFolder + File.separator + "CHT").exists()) {new File(oplFolder + File.separator + "CHT").mkdir();}
            if (!new File(oplFolder + File.separator + "VMC").exists()) {new File(oplFolder + File.separator + "VMC").mkdir();}
            if (!new File(oplFolder + File.separator + "POPS").exists()) {new File(oplFolder + File.separator + "POPS").mkdir();}
            if (!new File(oplFolder + File.separator + "THM").exists()) {new File(oplFolder + File.separator + "THM").mkdir();}
            
            if (!currentlySelectedMode.equals("HDD_USB")) {
                if (!new File(oplFolder + File.separator + "CD").exists()) {new File(oplFolder + File.separator + "CD").mkdir();}
                if (!new File(oplFolder + File.separator + "DVD").exists()) {new File(oplFolder + File.separator + "DVD").mkdir();}
            }

            
            // Store the console IP address
            if (currentlySelectedMode.equals("HDD")){PopsGameManager.setPS2IP(jFormattedTextPathHDD.getText());}

            switch (currentlySelectedMode) {
                case "HDD":
                    try {GameListManager.createGameListFromFile("PS1", new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS1"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error creating the PS1 game list from file!\n\n" + ex.toString());}
                    try {GameListManager.createGameListFromFile("PS2", new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd" + File.separator + "gameListPS2"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug("Error creating the PS2 game list from file!\n\n" + ex.toString());}
                    break;
                case "HDD_USB":
                case "SMB":
                    try {
                        GameListManager.createGameListsPS1();
                        GameListManager.createGameListsPS2(true);
                    } 
                    catch(NullPointerException ex){PopsGameManager.displayErrorMessageDebug(ex.toString());}
                    break;
            }

            if (currentlySelectedMode.equals("HDD")) {
                PopsGameManager.setCurrentConsole(selectedConsole);
                if (selectedConsole.equals("PS1")) {if (gameListPS1 != null) {GameListManager.setGameListPS1(gameListPS1);}}
                if (selectedConsole.equals("PS2")) {if (gameListPS2 != null) {GameListManager.setGameListPS2(gameListPS2);}}
            }
            
            try {XMLFileManager.writeSettingsXML();} catch (TransformerException | ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug("Error saving the settings!\n\n" + ex.toString());}
            
            PopsGameManager.callbackToUpdateGUIGameList(null, 0);
            this.setVisible(false);
        }  
    }//GEN-LAST:event_jButtonSaveModeActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        
        if (!PopsGameManager.isOPLFolderSet()){

            int dialogResult = JOptionPane.showConfirmDialog (null, "You have not set the OPL directory. \n\nDo you want to exit the application?"," OPL Directory Not Set!",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){Runtime.getRuntime().exit(0);} 
        } 
        else {this.setVisible(false);}
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonConnectHDDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectHDDActionPerformed
        
        if (!"".equals(jFormattedTextPathHDD.getText())) {connectToPS2();}
        else {JOptionPane.showMessageDialog(null,"You need to enter the IP address of your PS2 console into the text field."," No IP address entered!",JOptionPane.WARNING_MESSAGE);}
    }//GEN-LAST:event_jButtonConnectHDDActionPerformed

    private void jButtonBrowseSMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseSMBActionPerformed
        
        // Display the folder browser and try and get the selected path
        String folder = PopsGameManager.manuallySetOPLDirectory();

        if (folder != null){
            oplFolder = folder;
            jTextFieldPathSMB.setText(folder);
        } 
    }//GEN-LAST:event_jButtonBrowseSMBActionPerformed

    private void jButtonBrowseUSBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseUSBActionPerformed

        JFileChooser chooser = new JFileChooser();

        /*
        // This tries to detect the removable drive letter
        String driveLetter = "";
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] f = File.listRoots();
        for (int i = 0; i < f.length; i++) {
          String drive = f[i].getPath();
          String type = fsv.getSystemTypeDescription(f[i]);
          boolean isDrive = fsv.isDrive(f[i]);
          boolean isFloppy = fsv.isFloppyDrive(f[i]);
          boolean canRead = f[i].canRead();
          boolean canWrite = f[i].canWrite();

          if (canRead && canWrite && !isFloppy && isDrive && (type.toLowerCase().contains("removable") || type.toLowerCase().contains("rimovibile"))) {
            driveLetter = drive;
            break;
          }						
        }

        // If USB Drive found
        if (!driveLetter.equals("")) {
          File usbDrive = new File(driveLetter);
          chooser.setCurrentDirectory(usbDrive);
        } 
        else{chooser.setCurrentDirectory(new java.io.File("."));}
        */
        
        chooser.setCurrentDirectory(new java.io.File("."));
        
        chooser.setDialogTitle("Set OPL Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            oplFolder = chooser.getSelectedFile().getAbsolutePath();
            jTextFieldPathUSB.setText(oplFolder);

            // Ensure that the local hdd_usb folder exists
            //File hdlLocalDirectory = new File(PopsGameManager.getCurrentDirectory() + File.separator + "hdd_usb" + File.separator);
            //if (!hdlLocalDirectory.exists() && hdlLocalDirectory.isDirectory()) {hdlLocalDirectory.mkdir();}

            jButtonSaveMode.setEnabled(true);
        }
    }//GEN-LAST:event_jButtonBrowseUSBActionPerformed

    private void jRadioModeSelectUSBHDDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioModeSelectUSBHDDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioModeSelectUSBHDDActionPerformed

    private void jButtonBrowseUSBHDDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseUSBHDDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonBrowseUSBHDDActionPerformed
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated variables">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseSMB;
    private javax.swing.JButton jButtonBrowseUSB;
    private javax.swing.JButton jButtonBrowseUSB1;
    private javax.swing.JButton jButtonBrowseUSBHDD;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonConnectHDD;
    private javax.swing.JButton jButtonSaveMode;
    private javax.swing.JFormattedTextField jFormattedTextPathHDD;
    private javax.swing.JLabel jLabelIP;
    private javax.swing.JLabel jLabelIPathUSB;
    private javax.swing.JLabel jLabelIPathUSB1;
    private javax.swing.JLabel jLabelIPathUSBHDD;
    private javax.swing.JLabel jLabelPathSMB;
    private javax.swing.JPanel jPanelModeSelect;
    private javax.swing.JPanel jPanelmodeHDD;
    private javax.swing.JPanel jPanelmodeSMB;
    private javax.swing.JPanel jPanelmodeUSB;
    private javax.swing.JPanel jPanelmodeUSB1;
    private javax.swing.JPanel jPanelmodeUSBHDD;
    private javax.swing.JRadioButton jRadioModeSelectHDD;
    private javax.swing.JRadioButton jRadioModeSelectSMB;
    private javax.swing.JRadioButton jRadioModeSelectUSB;
    private javax.swing.JRadioButton jRadioModeSelectUSB1;
    private javax.swing.JRadioButton jRadioModeSelectUSBHDD;
    private javax.swing.JTextField jTextFieldPathSMB;
    private javax.swing.JTextField jTextFieldPathUSB;
    private javax.swing.JTextField jTextFieldPathUSB1;
    private javax.swing.JTextField jTextFieldPathUSBHDD;
    // End of variables declaration//GEN-END:variables
}// </editor-fold>