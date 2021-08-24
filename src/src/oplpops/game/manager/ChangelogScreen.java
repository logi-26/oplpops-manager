package oplpops.game.manager;

import java.awt.Color;
import java.util.Date;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class ChangelogScreen extends javax.swing.JDialog {

    public ChangelogScreen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setTitle("Changelog");
        displayChangelog();
    }
    
    
    private void displayChangelog(){

        String s = new Date().toString();
        StyledDocument doc = (StyledDocument) jTextPaneChangelog.getDocument();

        // Title text styling
        SimpleAttributeSet title = new SimpleAttributeSet();
        StyleConstants.setBold(title, true);
        StyleConstants.setFontFamily(title, "Ubuntu");
        StyleConstants.setFontSize(title, 12);
        StyleConstants.setItalic(title, true);
        Color colour = new Color(218,145,30);
        StyleConstants.setForeground(title, colour);
        
        // Normal text styling
        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setFontFamily(normal, "Ubuntu");
        StyleConstants.setFontSize(normal, 12);

        // Special text styling (Just bold for the initial beta comment)
        SimpleAttributeSet special = new SimpleAttributeSet(normal);
        StyleConstants.setFontFamily(special, "Ubuntu");
        StyleConstants.setFontSize(special, 12);
        StyleConstants.setBold(special, true);

        // Create the changelog
        try {
            
            doc.insertString(doc.getLength(), "Version 0.6.1 (Beta)  -  06 January 2018" + "\n", title);
            doc.insertString(doc.getLength(), "    * Quick bugfix to resolve an issue when uploading PS1 games." + "\n", normal);
            
            doc.insertString(doc.getLength(), "" + "\n", normal);
            doc.insertString(doc.getLength(), "Version 0.6 (Beta)  -  06 January 2018" + "\n", title);
            doc.insertString(doc.getLength(), "    * Improved the cheat display algorithm." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Improved the file report screen (the current console and selected gameID are automatically inserted)." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Improved the share VMC screen (the current console and selected gameID are automatically inserted)." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Improved some other bits of code." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Fixed a bug were the PS1 game directories were not being named with the game ID." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Changed the PS1 game list to be ordered alphabeticaly." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Changed the CFG-DEV folder back to CFG." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Changed the conf_elm.cfg to conf_apps.cfg." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to batch upload PS1 games in HDD mode (over network)." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to batch upload PS2 games in HDD mode (over network)." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to read a ul.cfg file." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added a feature to generate a ul.cfg file." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to convert PS2 games to UL format." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to convert PS2 games from UL format back to ISO format." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added a feature to highlight UL format games in the game list." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to generate PS1 spine ART for all PS1 games." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to delete all PS1 spine ART from the ART folder." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to store any invalid games so that the user is not notified about any specific game more than once." + "\n", normal);

            doc.insertString(doc.getLength(), "" + "\n", normal);
            doc.insertString(doc.getLength(), "Version 0.5 (Beta)  -  05 April 2017" + "\n", title);
            doc.insertString(doc.getLength(), "    * Changed the PS2 game list to be ordered alphabeticaly." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Changed the background colour of the game detail labels to white in the main screen." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to detect and rename games with long file names." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added auto re-generate conf_elm.cfg when adding PS1 games." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added auto re-generate conf_elm.cfg when batch adding PS1 games." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added auto re-generate conf_elm.cfg when deleting PS1 games." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the game name to the progress window when importing a game." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added a message to display any games that could not be converted during the batch import process." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to batch delete all ELF files." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to generate PS2 spine ART for all PS2 games." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to delete all PS2 spine ART from the ART folder." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added PS1 compatability display (coloured list)." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added multi-disc support (limited number of games at the moment)." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added 2000+ widescreen codes." + "\n", normal);

            doc.insertString(doc.getLength(), "" + "\n", normal);
            doc.insertString(doc.getLength(), "Version 0.4 (Beta)  -  28 March 2017" + "\n", title);
            doc.insertString(doc.getLength(), "    * Added the ability to detect and rename any incorrectly named VCD files." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the USB mode for users who store their OPL directory on a USB drive." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added a feature to keep the read me file up-to date with the latest info." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added an extra 30,000+ ART files to the database (mostly alternative screenshots and covers)." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Fixed a bug were the background images where being incorrectly resized." + "\n", normal);
            
            doc.insertString(doc.getLength(), "" + "\n", normal);
            doc.insertString(doc.getLength(), "Version 0.3 (Beta)  -  25 March 2017" + "\n", title);
            doc.insertString(doc.getLength(), "    * Added the ability to share config files if they are not identical to the version on the server." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to share art files if they are not identical to the version on the server." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added the ability to download alternative art for each of the avaialble art files." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added checks to ensure that the third party apps are up to date with the versions on the server." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Fixed a bug that was preventing files from being shared if the folder contained both PS1 and PS2 config files." + "\n", normal);
            
            doc.insertString(doc.getLength(), "" + "\n", normal);
            doc.insertString(doc.getLength(), "Version 0.2 (Beta)  -  17 March 2017" + "\n", title);
            doc.insertString(doc.getLength(), "    * Changed the game import window so that it now auto closes when it has completed." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Changed the CFG folder for CFG-DEV." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Fixed PS1 CFG files, had no \"SB.\" prefix in SMB mode." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Fixed PS1 CFG files, had no \".ELF\" in the file extension." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Fixed file share function, was looking in the wrong directory for the lists." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added batch import for PS1 games." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added batch import for PS2 games." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added checks to prevent games from being added that are already in the list." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added checks to prevent files from being transfered if a file with the same name is already in the directory." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added a message dialog box that is displayed when cue2pops is running in the background." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added a message dialog box that displays any cue2pops error messages." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added PS1 IGR codes to the cheat screen." + "\n", normal);
            doc.insertString(doc.getLength(), "    * Added this changelog screen." + "\n", normal);
            
            doc.insertString(doc.getLength(), "" + "\n", normal);
            doc.insertString(doc.getLength(), "Version 0.1 (Beta)  -  25 February 2017" + "\n", title);
            doc.insertString(doc.getLength(), "    Initial Beta release." + "\n", special);

        } catch (BadLocationException ex) {PopsGameManager.displayErrorMessageDebug("Error generating the changelog!\n\n" + ex.toString());}
        
        jTextPaneChangelog.setCaretPosition(0);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneChangelog = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Changelog"));

        jTextPaneChangelog.setEditable(false);
        jScrollPane1.setViewportView(jTextPaneChangelog);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addContainerGap())
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

    // <editor-fold defaultstate="collapsed" desc="Generated Variables">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPaneChangelog;
    // End of variables declaration//GEN-END:variables
    // </editor-fold> 
}
