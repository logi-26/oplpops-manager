package oplpops.game.manager;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

public class GameImageSelectorScreenPS2 extends javax.swing.JDialog {

    private static final String NO_IMAGE_PS2_COVER_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Cover PS2.png";
    private static final String NO_IMAGE_BACKGROUND_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Background.png";
    private static final String NO_IMAGE_SCREENSHOT_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Screenshot.png";
    private static final String NO_IMAGE_DISC_PATH = PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "images" + File.separator + "No Image Disc.png";
    
    private final ImageSelectListener imageSelectListener;
    private final ImageChangedListener imageChangedListener;
    private final String imageType;
    private File image;
    private boolean imageSaved = false;
    private int numberOfImages = 0;
    private int currentImageNumber = 1;
    private final java.awt.Frame parent;
    private final String gameID;
    private final int currentListIndex;
    
    public GameImageSelectorScreenPS2(java.awt.Frame parent, boolean modal, ImageSelectListener imageSelectListener, ImageChangedListener imageChangedListener, int currentImageNumber, String imageType, File image, int numberOfFiles, String gameID, int currentListIndex) {
        super(parent, modal);
        this.imageType = imageType;
        this.image = image;
        this.numberOfImages = numberOfFiles;
        this.currentImageNumber = currentImageNumber;
        this.imageSelectListener = imageSelectListener;
        this.imageChangedListener = imageChangedListener;
        this.parent = parent;
        this.gameID = gameID;
        this.currentListIndex = currentListIndex;
        initComponents();
        overideClose();
        initialiseGUI();
    }

    
    // Overide the close operation
    private void overideClose(){

        // Prevent the window from being closed using the X
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                
               if (!imageSaved){
                   image.delete();
                   
                   switch (imageType) {
                       case "_COV":
                       case "_COV2":
                           imageSelectListener.imageSelected(imageType, new File(NO_IMAGE_PS2_COVER_PATH));
                           break;
                       case "_SCR":
                       case "_SCR2":
                           imageSelectListener.imageSelected(imageType, new File(NO_IMAGE_SCREENSHOT_PATH));
                           break;
                       case "_BG":
                           imageSelectListener.imageSelected(imageType, new File(NO_IMAGE_BACKGROUND_PATH));
                           break;
                       case "_ICO":
                           imageSelectListener.imageSelected(imageType, new File(NO_IMAGE_DISC_PATH));
                           break;
                       default:
                           break;
                   }  
               }
               dispose();
            }
        });
        this.setTitle("Images Selector");
    }
    
    
    private void initialiseGUI(){
        
        updateNumberLabel();
        
        switch (imageType) {
            case "_COV":
                jPanelGameBackgroundImage.setBorder(BorderFactory.createTitledBorder("Front Cover"));
                jLabelGameImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(160, 210, Image.SCALE_DEFAULT)));
                break;
            case "_COV2":
                jPanelGameBackgroundImage.setBorder(BorderFactory.createTitledBorder("Rear Cover"));
                jLabelGameImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(160, 210, Image.SCALE_DEFAULT)));
                break;
            case "_ICO":
                jPanelGameBackgroundImage.setBorder(BorderFactory.createTitledBorder("Disc Image"));
                jLabelGameImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT)));
                break;
            case "_BG":
                jPanelGameBackgroundImage.setBorder(BorderFactory.createTitledBorder("Background Image"));
                jLabelGameImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(375, 200, Image.SCALE_DEFAULT)));
                break;
            case "_SCR":
                jPanelGameBackgroundImage.setBorder(BorderFactory.createTitledBorder("Screenshot 1"));
                jLabelGameImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(225, 170, Image.SCALE_DEFAULT)));
                break;
            case "_SCR2":
                jPanelGameBackgroundImage.setBorder(BorderFactory.createTitledBorder("Screenshot 2"));
                jLabelGameImage.setIcon(new ImageIcon(new ImageIcon(image.toString()).getImage().getScaledInstance(225, 170, Image.SCALE_DEFAULT)));
                break;
            default:
                break;
        }
    }
    
    
    private void updateNumberLabel(){
        jTextFieldImageNumber.setText(String.valueOf(currentImageNumber) + "/" + String.valueOf(numberOfImages));
    }
    
    
    public void updateImage(File image){
        this.image = image;
        initialiseGUI();
        imageSaved = false;
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelGameBackgroundImage = new javax.swing.JPanel();
        jLabelGameImage = new javax.swing.JLabel();
        jButtonBackgroundImageAuto = new javax.swing.JButton();
        jButtonNextGame = new javax.swing.JButton();
        jButtonPreviousGame = new javax.swing.JButton();
        jButtonReportFile = new javax.swing.JButton();
        jTextFieldImageNumber = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelGameBackgroundImage.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabelGameImage.setBackground(new java.awt.Color(153, 153, 153));
        jLabelGameImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGameImage.setToolTipText("");
        jLabelGameImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabelGameImage.setPreferredSize(new java.awt.Dimension(375, 2));

        jButtonBackgroundImageAuto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Download Icon.png"))); // NOI18N
        jButtonBackgroundImageAuto.setToolTipText("");
        jButtonBackgroundImageAuto.setContentAreaFilled(false);
        jButtonBackgroundImageAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackgroundImageAutoActionPerformed(evt);
            }
        });

        jButtonNextGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Right Icon.png"))); // NOI18N
        jButtonNextGame.setToolTipText("Next game");
        jButtonNextGame.setContentAreaFilled(false);
        jButtonNextGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextGameActionPerformed(evt);
            }
        });

        jButtonPreviousGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oplpops/game/manager/images/buttons/Left Icon.png"))); // NOI18N
        jButtonPreviousGame.setToolTipText("Previous game");
        jButtonPreviousGame.setContentAreaFilled(false);
        jButtonPreviousGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousGameActionPerformed(evt);
            }
        });

        jButtonReportFile.setText("Report");
        jButtonReportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReportFileActionPerformed(evt);
            }
        });

        jTextFieldImageNumber.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextFieldImageNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldImageNumber.setText("1/1");
        jTextFieldImageNumber.setMinimumSize(new java.awt.Dimension(6, 25));

        javax.swing.GroupLayout jPanelGameBackgroundImageLayout = new javax.swing.GroupLayout(jPanelGameBackgroundImage);
        jPanelGameBackgroundImage.setLayout(jPanelGameBackgroundImageLayout);
        jPanelGameBackgroundImageLayout.setHorizontalGroup(
            jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                        .addComponent(jLabelGameImage, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                        .addComponent(jButtonBackgroundImageAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(jButtonReportFile, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldImageNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGameBackgroundImageLayout.setVerticalGroup(
            jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGameBackgroundImageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelGameImage, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelGameBackgroundImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButtonBackgroundImageAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonNextGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonReportFile, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldImageNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPreviousGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelGameBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Button Click Events">   
    private void jButtonBackgroundImageAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackgroundImageAutoActionPerformed
        imageSaved = true;
        imageSelectListener.imageSelected(imageType, image);
    }//GEN-LAST:event_jButtonBackgroundImageAutoActionPerformed

    private void jButtonNextGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextGameActionPerformed
        
        if (currentImageNumber < numberOfImages){
            currentImageNumber ++;
            updateNumberLabel();
            imageChangedListener.imageChanged(imageType, currentImageNumber);
        }
        
    }//GEN-LAST:event_jButtonNextGameActionPerformed

    private void jButtonPreviousGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousGameActionPerformed
        
        if (currentImageNumber > 1){
            currentImageNumber --;
            updateNumberLabel();
            imageChangedListener.imageChanged(imageType, currentImageNumber);
        }
    }//GEN-LAST:event_jButtonPreviousGameActionPerformed

    private void jButtonReportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReportFileActionPerformed
        FileReportScreen reportScreen = new FileReportScreen(parent, true, currentListIndex, imageType);
        reportScreen.setLocationRelativeTo(this);
        reportScreen.setVisible(true); 
    }//GEN-LAST:event_jButtonReportFileActionPerformed
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc="Generated Variables">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBackgroundImageAuto;
    private javax.swing.JButton jButtonNextGame;
    private javax.swing.JButton jButtonPreviousGame;
    private javax.swing.JButton jButtonReportFile;
    private javax.swing.JLabel jLabelGameImage;
    javax.swing.JPanel jPanelGameBackgroundImage;
    private javax.swing.JTextField jTextFieldImageNumber;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
