package oplpops.game.manager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


public class GenerateSpineART {
    
    public GenerateSpineART(){}
    
    
    // Generate all PS2 spine ART
    public void generateForPS2(){
        
        List<Game> gameListPS2 = GameListManager.getGameListPS2();
        
        if (!gameListPS2.isEmpty()){
            
            // Loop through the PS2 game list
            gameListPS2.forEach((game) -> {
                // Get the game name and the game region
                String gameName = game.getGameName();
                String gameRegion = PopsGameManager.determineGameRegion(game.getGameID());
                
                // Generate the spine ART
                try {

                    InputStream in = GameListManager.class.getResourceAsStream("/oplpops/game/manager/PS2SpineTemplate.png"); 
                    BufferedImage bufferedImage = ImageIO.read(in);
                    Graphics2D graphics = bufferedImage.createGraphics();

                    // Draw the game name on the image
                    graphics.setColor(Color.BLACK);

                    if (gameName.length() <= 27){
                        graphics.setFont(new Font("Tahoma", Font.BOLD, 10));
                        graphics.drawString(gameName, 80, 12);

                        // Draw the game region on the image
                        //Font font = new Font("Tahoma", Font.PLAIN, 5);    
                        //AffineTransform affineTransform = new AffineTransform();
                        //affineTransform.rotate(Math.toRadians(-90), 0, 0);
                        //Font rotatedFont = font.deriveFont(affineTransform);
                        //graphics.setFont(rotatedFont);
                        //if (gameRegion.equals("PAL")){graphics.drawString(gameRegion,235,14);}else {graphics.drawString(gameRegion,235,17);}
                    }
                    else {
                        graphics.setFont(new Font("Tahoma", Font.PLAIN, 9));
                        graphics.drawString(gameName, 80, 12);
                    }

                    // Rotate the image 90 degress
                    AffineTransform tx = new AffineTransform();
                    tx.translate(bufferedImage.getHeight()/2, bufferedImage.getWidth()/2);
                    tx.rotate(Math.PI/2);
                    graphics.setTransform(tx);

                    tx.translate(-bufferedImage.getWidth()/2, -bufferedImage.getHeight()/2);
                    AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
                    bufferedImage = op.filter(bufferedImage, null);

                    // Write the image
                    ImageIO.write(bufferedImage, "png", new File(  PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + game.getGameID() + "_LAB.png"));
                    
                } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            });  
            
            JOptionPane.showMessageDialog(null,"All of the PS2 spine ART has been generated."," Spine ART Generated",JOptionPane.PLAIN_MESSAGE);
        } 
    }
    
    
    // Delete all PS2 spine ART
    public void deleteForPS2(){
        File folder = new File(PopsGameManager.getOPLFolder() + File.separator + "ART");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {if (file.isFile()) {if (file.getName().substring(file.getName().length()-8, file.getName().length()-4).equals("_LAB")){file.delete();}}} 
    }
    
    
    
    
    
    
    // Generate all PS1 spine ART
    public void generateForPS1(){

        List<Game> gameListPS1 = GameListManager.getGameListPS1();

        if (!gameListPS1.isEmpty()){
            
            // Loop through the PS1 game list
            gameListPS1.forEach((game) -> {
                // Get the game name and the game region
                String gameName = game.getGameName();
                String gameRegion = PopsGameManager.determineGameRegion(game.getGameID());

                if (gameName.length() <= 32){

                    // Generate the spine ART
                    try {
                        InputStream in = GameListManager.class.getResourceAsStream("/oplpops/game/manager/PS1SpineTemplate.png"); 
                        BufferedImage bufferedImage = ImageIO.read(in);
                        Graphics2D graphics = bufferedImage.createGraphics();

                        // Draw the game name on the image
                        graphics.setColor(Color.WHITE);

                        if (gameName.length() <= 27){
                            graphics.setFont(new Font("Tahoma", Font.BOLD, 10));
                            graphics.drawString(gameName, 40, 12);

                            // Draw the game region on the image
                            //Font font = new Font("Tahoma", Font.PLAIN, 5);    
                            //AffineTransform affineTransform = new AffineTransform();
                            //affineTransform.rotate(Math.toRadians(-90), 0, 0);
                            //Font rotatedFont = font.deriveFont(affineTransform);
                            //graphics.setFont(rotatedFont);
                            //if (gameRegion.equals("PAL")){graphics.drawString(gameRegion,235,14);}else {graphics.drawString(gameRegion,235,17);}
                        }
                        else {
                            graphics.setFont(new Font("Tahoma", Font.BOLD, 10));
                            graphics.drawString(gameName, 30, 12);
                        }

                        // Rotate the image 90 degress
                        AffineTransform tx = new AffineTransform();
                        tx.translate(bufferedImage.getHeight()/2, bufferedImage.getWidth()/2);
                        tx.rotate(Math.PI/2);
                        graphics.setTransform(tx);

                        tx.translate(-bufferedImage.getWidth()/2, -bufferedImage.getHeight()/2);
                        AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
                        bufferedImage = op.filter(bufferedImage, null);

                        // Write the image
                        ImageIO.write(bufferedImage, "png", new File(PopsGameManager.getOPLFolder() + File.separator + "ART" + File.separator + game.getGameID() + "_LAB.png"));

                    } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
                }
            });  
            
            JOptionPane.showMessageDialog(null,"All of the PS1 spine ART has been generated."," Spine ART Generated",JOptionPane.PLAIN_MESSAGE);
        } 
    }
    
    
 
    // Delete all PS1 spine ART
    public void deleteForPS1(){
        
        
    }
    
}