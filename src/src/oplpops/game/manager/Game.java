package oplpops.game.manager;

import java.util.ArrayList;

public class Game {
    
    private String name;
    private final String id;
    private String path;
    private final String humanReadableSize;
    private final long rawSize;
    
    private String compatibleUSB;
    private String compatibleHDD;
    private String compatibleSMB;
    
    private boolean frontCover;
    private boolean rearCover;
    private boolean discCover;
    private boolean screenshot1Cover;
    private boolean screenshot2Cover;
    private boolean backgroundCover;
    
    private boolean cheatFile;
    private boolean configFile;
    
    private boolean isMultiDiscGame;
    private ArrayList<String> multiDiscGames;
    
    private boolean isULGame = false;
    private int numberOfParts = 0;

    
    public Game(String gameName, String gameID, String gamePath, String readableGameSize, long rawGameSize){
        name = gameName;
        id = gameID;
        path = gamePath;
        humanReadableSize = readableGameSize;
        rawSize = rawGameSize;
        isMultiDiscGame = false;
        multiDiscGames = new ArrayList<>();
    }


    public String getGameName(){return name;}
    public void setGameName(String newName){name = newName;}
    public String getGameID(){return id;}
    public String getGamePath(){return path;}
    public String getGameReadableSize(){return humanReadableSize;}
    public long getGameRawSize(){return rawSize;}


    public void setGamePath(String newPath) {path = newPath;}
    
    
    // These booleans are used to get/set the game multi-disc value 
    public void setMultiDiscGame(boolean multiDisc){isMultiDiscGame = multiDisc;}
    public boolean getMultiDiscGame(){return isMultiDiscGame;}
    
    public void addToMultiDiscList(String gameID){multiDiscGames.add(gameID);}
    public ArrayList<String> getMultiDiscList(){return multiDiscGames;}
    
    
    // These booleans are used to determine the PS1 game compatability
    public void setCompatibleUSB(String compatible){compatibleUSB = compatible;}
    public String getCompatibleUSB(){return compatibleUSB;}
    
    public void setCompatibleHDD(String compatible){compatibleHDD = compatible;}
    public String getCompatibleHDD(){return compatibleHDD;}
    
    public void setCompatibleSMB(String compatible){compatibleSMB = compatible;}
    public String getCompatibleSMB(){return compatibleSMB;}
    
    

    // Get/Set the UL Game value
    public Boolean getULGame(){return isULGame;}
    public void setULGame(boolean ulGame){isULGame = ulGame;}
    
    // Get/Set the number of game fragments for a UL Game
    public int getNumberOfParts(){return numberOfParts;}
    public void setNumberOfParts(int number){numberOfParts = number;}
    

    // These booleans are used to determine which files are associated with the game
    public void setFrontCover(boolean cover){frontCover = cover;}
    public boolean getFrontCover(){return frontCover;}
    
    public void setRearCover(boolean cover){rearCover = cover;}
    public boolean getRearCover(){return rearCover;}
    
    public void setDiscCover(boolean cover){discCover = cover;}
    public boolean getDiscCover(){return discCover;}
    
    public void setScreenshot1Cover(boolean cover){screenshot1Cover = cover;}
    public boolean getScreenshot1Cover(){return screenshot1Cover;}
    
    public void setScreenshot2Cover(boolean cover){screenshot2Cover = cover;}
    public boolean getScreenshot2Cover(){return screenshot2Cover;}
    
    public void setBackgroundCover(boolean cover){backgroundCover = cover;}
    public boolean getBackgroundCover(){return backgroundCover;}
    
    public void setCheatFile(boolean file){cheatFile = file;}
    public boolean getCheatFile(){return cheatFile;}
    
    public void setConfigFile(boolean file){configFile = file;}
    public boolean setConfigFile(){return configFile;}
}