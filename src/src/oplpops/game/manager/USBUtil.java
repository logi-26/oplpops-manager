package oplpops.game.manager;

import static java.lang.Math.toIntExact;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

public final class USBUtil {

    private static final int[] CRC_TABLE = new int[0x400];
    private static final File UL_BACKUP_TXT = new File(PopsGameManager.getCurrentDirectory() + File.separator + "ul-backup.txt");
    private static final File UL_BACKUP_FILE = new File(PopsGameManager.getCurrentDirectory() + File.separator + "ul-backup");
    private static final File KEY_FILE_BACKUP = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "data_4");
    
    
    public USBUtil() {}
    
    
    // Local class to store the data from the ul-backup file
    static class BackupULGame{
        String hexName;
        String originalName;
        String gameID;
        int numberOfFragments;
        long gameRawSize;
        
        public BackupULGame(String hexName, String originalName, String gameID, int numberOfFragments, long gameRawSize){
            this.hexName = hexName;
            this.originalName = originalName;
            this.gameID = gameID;
            this.numberOfFragments = numberOfFragments;
            this.gameRawSize = gameRawSize;
        } 
    }
    
    
    // This joins the split files back into a single ISO file using a background worker task
    public static void joinFiles(SplitMergeScreen splitMergeScreen, String gameID) throws Exception {
        new BackgroundWorkerMergeGame(splitMergeScreen, gameID).execute();  
    }
    
    
    // This splits the file into 1GB chunks using a background worker task
    public static void splitFile(SplitMergeScreen SplitMergeScreen, Game game) throws Exception{
        new BackgroundWorkerSplitGame(SplitMergeScreen, game).execute();
    }
    
    
    // This reads the ul.cfg file and adds each game to a local list
    public static ArrayList<Game> readULCFG(){

        ArrayList<Game> ulGameList = new ArrayList<>();
        
        // Get the ul.cfg file from the OPL directory
        File cfgFile = new File(PopsGameManager.getOPLFolder() + File.separator + "ul.cfg");
        Path path = Paths.get(cfgFile.getAbsolutePath());
        
        // If the ul.cfg file exists
        if (cfgFile.exists() && cfgFile.isFile()){
            try {
                byte[] data = Files.readAllBytes(path);
                byte[] gameName = new byte[32];          
                byte[] gameID = new byte[14];               
                byte[] gameParts = new byte[1];             
                int charNumber = 0;
                
                for (int i = 0; i < data.length; i++){

                    if (charNumber < 32){gameName[charNumber] = data[i];} 
                    else if (charNumber < 46){gameID[charNumber-32] = data[i];}
                    else if (charNumber == 47){gameParts[0] = data[i];}

                    charNumber++;
                    if (charNumber == 64) { 

                        // List all of the UL Game fragments that are in the DVD folder
                        ArrayList<File> ulGameFragments = listULFragments();

                        // Check for each of the game fragments in the DVD folder in order to determine the game size
                        long totalGameSize = 0;
                        int filesFound = 0;
                        for (int count = 0; count < gameParts[0]; count++){
                            for (File gameFragment : ulGameFragments){
                                if (gameFragment.getName().contains(new String(gameID).substring(3, new String(gameID).length())) &&  gameFragment.getName().substring(gameFragment.getName().length()-2, gameFragment.getName().length()).equals("0" + count)){
                                    totalGameSize += gameFragment.length();
                                    filesFound ++;
                                }
                            }
                        }

                        // If any of the fragments are missing, this sets the game size to zero (game size would be inaccurate if a single fragment is missing)
                        if (filesFound != gameParts[0]) {totalGameSize = 0;}

                        // Get the game name and the game ID without the leading "ul." and with the trailing whitespace removed
                        String name = new String(gameName).trim();
                        String id = new String(gameID).substring(3, new String(gameID).length()).trim();

                        // If the game name contains the game ID, this removes the game Id from the start of the string
                        if (name.contains(id)){name = name.substring(12, name.length());}

                        // Create a new UL Game object and specify the number of fragments that the game contains
                        Game newULGame = new Game(name, id, "ul.cfg", PopsGameManager.bytesToHuman(totalGameSize), totalGameSize);
                        newULGame.setULGame(true);
                        newULGame.setNumberOfParts(gameParts[0]);

                        // Add the new UL Game to the list
                        ulGameList.add(newULGame);
                        charNumber = 0;
                    } 
                }

            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
        } 
        
        // Return the list of UL Game objects
        return ulGameList;
    }
    
    
    // This generates the ul.cfg file in the OPL directory
    public static void writeULCFG(ArrayList<Game> ulGameList){

        // Generate the ul.cfg file
        OutputStream outputStream;
        try {
            // Create the ul.cfg file in the OPL directory
            outputStream = new FileOutputStream(PopsGameManager.getOPLFolder() + File.separator + "ul.cfg");

            // Loop through all of the UL games
            for (Game ulGame : ulGameList){

                // Get the game name and pad it if the length is less than 32 bytes
                String gameName = ulGame.getGameName();

                if (gameName.length() <= 32){
                    gameName = padName(gameName);

                    // Add the additional 32 bytes, which consists of Desc:0, Parts:NumOfParts, Media:14, Info:4x0, 1x8, 10x0
                    String confLine = gameName + "ul." + ulGame.getGameID() + (char)0 + (char)ulGame.getNumberOfParts() + (char)20;
                    for (int i = 0; i < 15; i++){if (i == 4){confLine += (char)8;} else {confLine += (char)0;}}

                    // Write the line to the ul.cfg file
                    for (char ch : confLine.toCharArray()){
                        int num = (int)ch;
                        String text = String.valueOf(num);
                        byte value = Byte.parseByte(text);
                        outputStream.write(value);
                    }
                }
                else {}

            }
            // Close the ul.cfg file once all of the UL Games have been written
            outputStream.close();
        } catch (FileNotFoundException ex) {} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // Pad the game name with NUL chars if the length is less than 32 bytes
    static public String padName(String name){
        //char nullChar = 0;
        for (int i = name.length(); i < 32; i++){name = name + (char)0;}
        return name;
    }
    
    
    // This read/write the files
    private static void readWrite(RandomAccessFile randomAccessFile, BufferedOutputStream bufferedOutputStream, long numBytes) throws IOException {
        byte[] buffer = new byte[(int) numBytes];
        int value = randomAccessFile.read(buffer);
        if (value != -1) {bufferedOutputStream.write(buffer);}
    }
    
    
    // This returns the number of split files that will need to be created
    private static int getNumberOfFiles(RandomAccessFile randomAccessFile){
        
        int numberOfOutputFiles = 0;
        try {
            double isoSize = randomAccessFile.length();
            double spliSize = isoSize/1073741824;
            String fullFragments = String.valueOf(spliSize).substring(0, 1);
            int numOfFullFragments = Integer.parseInt(fullFragments);
            numberOfOutputFiles = numOfFullFragments + 1;  
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        return numberOfOutputFiles;
    }
    
    
    // This returns a list of all the UL Game fragments in the OPL directory
    private static ArrayList<File> listULFragments(){
        
        // List all of the UL Game fragments that are in the DVD folder
        ArrayList<File> ulGameFragments = new ArrayList<>();
        File dvdFolder = new File(PopsGameManager.getOPLFolder());
        if (dvdFolder.exists() && dvdFolder.isDirectory()){
            File[] listOfFiles = dvdFolder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().length() > 3 && file.getName().contains(".")) {
                    String extension = file.getName().substring(file.getName().lastIndexOf('.')+1, file.getName().length());
                    if (extension.length() == 2 && extension.chars().allMatch(Character::isDigit)){ulGameFragments.add(file);}   
                }
            }
        }
        
        return ulGameFragments;
    }
    
    
    // This converts the game name string into a HEX format for UL Games
    public static String gameNameToULHex(byte[] originalName) {

        int count, table, crc = 0;
        for(table = 0; table < 256; table++) {
            crc = table << 24;
            for(count = 8; count > 0; count--) {if (crc < 0) {crc = crc << 1;} else {crc = crc << 1 ^ 0x04C11DB7;}}
            CRC_TABLE[255-table] = crc;
        }
        
        // Loop through all of the bytes in the string
        for (byte singleByte : originalName){crc = CRC_TABLE[singleByte ^ ((crc >>> 24) & 0xFF)] ^ ((crc << 8) & 0xFFFFFF00);}
        
        // Add the zero byte at the end
        crc = CRC_TABLE[0 ^ ((crc >>> 24) & 0xFF)] ^ ((crc << 8) & 0xFFFFFF00);
        
        return String.format("%08X", crc);
    }
    
    
    // This writes the UL Backup file with the games original name and the games HEX name
    private static void writeULBackupFile(byte[] originalName, String hexName, String gameID, int numberOfFragments, long gameRawSize){

        // Decrypt the ul-backup file
        decryptBackupFile();
        
        // Create the ul-backup file if it does not already exist
        if (!UL_BACKUP_TXT.exists() || !UL_BACKUP_TXT.isFile()) {try {UL_BACKUP_TXT.createNewFile();} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}

        // Read the ul-backup file to make sure the data is not already in the file
        boolean gameInBackupFile = false;
        try (BufferedReader br = new BufferedReader(new FileReader(UL_BACKUP_TXT))) {
            String line;
            while ((line = br.readLine()) != null) {if (line.contains(hexName) && line.contains(gameID)){gameInBackupFile = true;}}
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

        // Append the new game to the ul-backup file
        if (!gameInBackupFile){try(PrintWriter output = new PrintWriter(new FileWriter(UL_BACKUP_TXT,true))) {output.printf("%s-%s*%s(%s)-%s\n", hexName, new String(originalName), gameID, numberOfFragments, gameRawSize);} catch (Exception ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}}
    
        // Enrypt the ul-backup file
        encryptBackupFile();
    }
    
    
    // This tries to generate the ul.cfg file using the game parts in the DVD directory
    public static void regenerateULCFG(){
        
        ArrayList<File> ulGameFragments = listULFragments();
        ArrayList<Game> ulCompleteGames = new ArrayList();
        ArrayList<BackupULGame> backupGameList = new ArrayList();

        // Decrypt the ul-backup file
        decryptBackupFile();
        
        // Check if the ul-backup file exists and read the data from it
        if (UL_BACKUP_TXT.exists() && UL_BACKUP_TXT.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(UL_BACKUP_TXT))) {
                String line;
                while ((line = br.readLine()) != null) {if (!line.equals("")){backupGameList.add(new BackupULGame(line.substring(0, 8), line.substring(9, line.lastIndexOf('*')), line.substring(line.lastIndexOf('*')+1, line.lastIndexOf("(")), Integer.parseInt(line.substring(line.lastIndexOf("(")+1, line.lastIndexOf("(")+2)), Long.parseLong(line.substring(line.lastIndexOf("-")+1, line.length()))));}}
            } catch (FileNotFoundException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
        
        // Loop through all of the game data in the ul-backup file to try and determine the games original name and the number of fragments
        backupGameList.forEach((backupGame) -> {
            // Check for each of the game fragments in the DVD folder in order to determine the game size
            int filesFound = 0;
            for (int count = 0; count < backupGame.numberOfFragments; count++) {
                for (File gameFragment : ulGameFragments){if (gameFragment.getName().contains(backupGame.gameID) && gameFragment.getName().substring(gameFragment.getName().length()-2, gameFragment.getName().length()).equals("0" + count)){filesFound ++;}}
            }
            // If all of the fragments are available, this adds the game to the UL Complete Game List
            if (filesFound == backupGame.numberOfFragments) {
                Game completeBackupGame = new Game(backupGame.originalName, backupGame.gameID, "PATH", PopsGameManager.bytesToHuman(backupGame.gameRawSize), backupGame.gameRawSize);
                completeBackupGame.setULGame(true);
                completeBackupGame.setNumberOfParts(backupGame.numberOfFragments);
                ulCompleteGames.add(completeBackupGame);
            }
        });
        
        // Encrypt the ul-backup file
        encryptBackupFile();
        
        // Create the ul.cfg file
        writeULCFG(ulCompleteGames);
    }
    
    
    // Encrypt the ul-backup file
    private static void encryptBackupFile(){
        
        try {
            List<String> list = Files.readAllLines(UL_BACKUP_TXT.toPath(), Charset.defaultCharset());
            // Encrypt 
            FileEncryptor encryptor = new FileEncryptor();
            encryptor.EncryptData(list, UL_BACKUP_FILE.getAbsolutePath(), KEY_FILE_BACKUP.getAbsolutePath());
            if (UL_BACKUP_TXT.exists() && UL_BACKUP_TXT.isFile()) {UL_BACKUP_TXT.delete();}
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // Decrypt the ul-backup file
    private static void decryptBackupFile(){
        
        // Ensure that the ul-backup file exists
        if (new File(UL_BACKUP_FILE.getAbsolutePath()).exists() && new File(UL_BACKUP_FILE.getAbsolutePath()).isFile()){
            try {
                // Decrypt the encrypted settings file
                FileEncryptor encryptor = new FileEncryptor();
                List<String> list = encryptor.DecryptData(UL_BACKUP_FILE.getAbsolutePath(), KEY_FILE_BACKUP.getAbsolutePath());
                Files.write(Paths.get(PopsGameManager.getCurrentDirectory() + File.separator + "ul-backup.txt"),list,Charset.defaultCharset());
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
    }
    
    
    
    // Background worker thread which split the PS2 ISO
    public static class BackgroundWorkerSplitGame extends SwingWorker<Object, File> {
        SplitMergeScreen SplitMergeScreen;
        Game selectedGame;

        public BackgroundWorkerSplitGame(SplitMergeScreen SplitMergeScreen, Game selectedGame) {
            this.SplitMergeScreen = SplitMergeScreen;
            this.selectedGame = selectedGame;
        }

        @Override
        protected Object doInBackground() throws Exception {

            // Rename the ISO file and game path to remove the game ID
            File selectedISO = new File(selectedGame.getGamePath());
            File renamedISO = new File(selectedGame.getGamePath().substring(0, selectedGame.getGamePath().lastIndexOf(File.separator)+1) + selectedGame.getGameName() + ".ISO");

            if (selectedISO.exists() && selectedISO.isFile()){
                selectedISO.renameTo(renamedISO);
                selectedGame.setGamePath(renamedISO.getAbsolutePath());
            }
                
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(selectedGame.getGamePath(), "r")) {
                
                long bytesPerSplit = 1073741824; // 1GB chunks
                double isoSize = randomAccessFile.length();
                double spliSize = isoSize/1073741824;
                long remainder = Long.parseLong(   String.valueOf(spliSize).substring(2, String.valueOf(spliSize).length())   );
                long remainingBytes = remainder;
                int numberOfOutputFiles = getNumberOfFiles(randomAccessFile);
                int maxReadBufferSize = 8 * 1024; // 8KB
                long totalBytesProcessed = 0;
                
                for (int destIx = 1; destIx <= numberOfOutputFiles; destIx++) {
                    
                    SplitMergeScreen.setGamePartsText(String.valueOf(destIx) + "/" + String.valueOf(numberOfOutputFiles));
                    
                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(PopsGameManager.getOPLFolder() + File.separator + "ul." + gameNameToULHex(selectedGame.getGameName().getBytes()) + "." + selectedGame.getGameID() + "." + "0" + (destIx-1)))) {
                        if (bytesPerSplit > maxReadBufferSize) {
                            long numReads = bytesPerSplit / maxReadBufferSize;
                            long numRemainingRead = bytesPerSplit % maxReadBufferSize;

                            SplitMergeScreen.getProgressBar().setMinimum(0);
                            SplitMergeScreen.getProgressBar().setMaximum(toIntExact(numReads*numberOfOutputFiles));

                            for (int i = 0; i < numReads; i++) {
                                readWrite(randomAccessFile, bufferedOutputStream, maxReadBufferSize);
                                totalBytesProcessed++;
                                SplitMergeScreen.getProgressBar().setValue(toIntExact(totalBytesProcessed));
                                SplitMergeScreen.getProgressBar().repaint();
                            }

                            if (numRemainingRead > 0) {readWrite(randomAccessFile, bufferedOutputStream, numRemainingRead);}
                        } else {
                            readWrite(randomAccessFile, bufferedOutputStream, bytesPerSplit);
                        }
                    }
                }

                selectedGame.setULGame(true);
                selectedGame.setNumberOfParts(numberOfOutputFiles);

                // Add this game to the ul-backup file
                writeULBackupFile(selectedGame.getGameName().getBytes(), gameNameToULHex(selectedGame.getGameName().getBytes()), selectedGame.getGameID(), numberOfOutputFiles, selectedGame.getGameRawSize());

                // This often throws an error?
                if (remainingBytes > 0) {
                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("split." + numberOfOutputFiles + 1))) {
                        readWrite(randomAccessFile, bufferedOutputStream, remainingBytes);
                    }
                }
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());} 
            
            return null;
        }
        
        @Override
        protected void done(){

            // Delete the original ISO file
            File file = new File(selectedGame.getGamePath());
            file.delete();  

            // Create the ul.cfg file
            ArrayList<Game> ulGameList = new ArrayList<>();
            GameListManager.getGameListPS2().stream().filter((ps2Game) -> (ps2Game.getULGame())).forEachOrdered((ps2Game) -> {ulGameList.add(ps2Game);});
            writeULCFG(ulGameList);
            
            // update the main game list in the GUI and close the split/merge dialog screen
            GameListManager.createGameListsPS2(false);
            PopsGameManager.callbackToUpdateGUIGameList(selectedGame.getGameID(), -1); 
            SplitMergeScreen.closeDialog();
        }
    }   
    
    
    // Background worker thread which merges the PS2 ISO
    public static class BackgroundWorkerMergeGame extends SwingWorker<Object, File> {
        SplitMergeScreen SplitMergeScreen;
        String gameID;

        public BackgroundWorkerMergeGame(SplitMergeScreen SplitMergeScreen, String gameID) {
            this.SplitMergeScreen = SplitMergeScreen;
            this.gameID = gameID;
        }

        @Override
        protected Object doInBackground() throws Exception {

            int maxReadBufferSize = 8 * 1024;
            String gameName = gameID + ".GAMENAME";

            // Try and get the actual game name from the ul.cfg file
            if (!readULCFG().isEmpty()){for (Game ulGame : readULCFG()){if (ulGame.getGameID().equals(gameID)) {gameName = gameID + "." + ulGame.getGameName();}}}

            // List all of the split files for the selected game
            File[] allFiles = new File(PopsGameManager.getOPLFolder()).listFiles();
            ArrayList<File> splitFiles = new ArrayList<>();
            for (File ulSplitFile : allFiles){if (ulSplitFile.getName().contains(gameID) && ulSplitFile.getName().substring(ulSplitFile.getName().length()-2, ulSplitFile.getName().length()-1).equals("0")) {splitFiles.add(ulSplitFile);}}

            // Join the files to create the original ISO file
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + gameName + ".iso"))) {
                RandomAccessFile randomAccessFile = null;

                int fileCount = 0;
                long totalBytesProcessed = 0;
                for (File file : splitFiles) {

                    fileCount++;
                    SplitMergeScreen.setGamePartsText(String.valueOf(fileCount) + "/" + splitFiles.size());

                    randomAccessFile = new RandomAccessFile(file, "r");
                    long numReads = randomAccessFile.length() / maxReadBufferSize;
                    long numRemainingRead = randomAccessFile.length() % maxReadBufferSize;

                    SplitMergeScreen.getProgressBar().setMinimum(0);
                    SplitMergeScreen.getProgressBar().setMaximum(toIntExact(numReads*splitFiles.size()));

                    for (int i = 0; i < numReads; i++) {
                        readWrite(randomAccessFile, bufferedOutputStream, maxReadBufferSize);
                        totalBytesProcessed++;
                        SplitMergeScreen.getProgressBar().setValue(toIntExact(totalBytesProcessed));
                        SplitMergeScreen.getProgressBar().repaint();
                    }

                    if (numRemainingRead > 0) {readWrite(randomAccessFile, bufferedOutputStream, numRemainingRead);}
                    randomAccessFile.close();
                    file.delete();
                }

                // Set the games UL value to false now that the game has been merged back into a single ISO file
                for (Game gameFromList : GameListManager.getGameListPS2()){
                    if (gameFromList.getGameID().equals(gameID)) {
                        gameFromList.setULGame(false);
                        gameFromList.setNumberOfParts(0);
                        gameFromList.setGamePath(PopsGameManager.getOPLFolder() + File.separator + "DVD" + File.separator + gameName + ".iso");
                    }
                }
            }
            
            return null;
        }
        
        @Override
        protected void done(){

            // Create the ul.cfg file
            ArrayList<Game> ulGameList = new ArrayList<>();
            GameListManager.getGameListPS2().stream().filter((ps2Game) -> (ps2Game.getULGame())).forEachOrdered((ps2Game) -> {ulGameList.add(ps2Game);});
            writeULCFG(ulGameList);
            
            // update the main game list in the GUI and close the split/merge dialog screen
            GameListManager.createGameListsPS2(false);
            PopsGameManager.callbackToUpdateGUIGameList(gameID, -1); 
            SplitMergeScreen.closeDialog();
        }
    }   
}