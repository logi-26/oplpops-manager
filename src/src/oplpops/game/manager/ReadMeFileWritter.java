package oplpops.game.manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ReadMeFileWritter {

    public ReadMeFileWritter(){
        
    }
    
    // This writes the read me file if it has been deleted or the MD5 is different to the latest version
    public void WriteFile(){

        String fileContent[] = {
            "OPLPOPS Game Manager V0.6.1 Beta By Logi26 (06/01/2018)",
            "Cross platform application for managing PlayStation 1 and PlayStation 2 game, art, config, cheat and save files for Open-PS2-Loader (OPL) and POPSTARTER.",
            " ",
            " ",
            "WARNING/DISCLAIMER:",
            "This is an early beta version of the application and will likely contain bugs.",
            "The application is capable of writing data to the PlayStation 2's internal hard drive, therfore there is a risk of data corruption.",
            " ",
            "Use this application at your own risk!",
            "This Software is provided \"as is,\" without warranty of any kind.",
            "In no event shall the author be liable for any damages or other liability, arising from, or in connection with the use of this software.",
            " ",
            " ",
            "CREDITS:",
            "This application uses cue2pops and pops2cue which were developed by krHACKen, who kindley provided the sources.",
            "This application uses HDL Dump which was developed by The Wizard of Oz, AKuHAK and other contributers.",
            "This application also uses GenVMC which was developed by jimmikaelkael.",
            "This application was inspired by the Official OPL Manager app which was developed by danielb.",
            " ",
            "All of the third party apps have been compiled for Linux and Windows from the same code bases:",
            "cue2pops = version 2.3 - Permission from krHACKen to distribute.",
            "pops2cue = version 1.0 - Permission from krHACKen to distribute.",
            "hdl_dump = version 093 - Licensed under: GNU GENERAL PUBLIC LICENSE Version 2.		https://github.com/ifcaro/Open-PS2-Loader/tree/master/pc/genvmc",
            "genvmc = version 0.1.0 - Licensed under: Academic Free License version 3.0.	        https://bitbucket.org/AKuHAK/hdl-dump",
            " ",
            " ",
            "CURRENT DATABASE FILES AVAILABLE:",
            " ",
            "PS1 - ART = 55,008",
            "PS1 - CFG = 7,365",
            "PS1 - CHT = 372",
            "PS1 - VMC = 152",
            "PS1 - Widescreen Codes = 234",
            "PS2 - ART = 19,067",
            "PS2 - CFG = 2,071",
            "PS2 - CHT = 555",
            "PS2 - VMC = 1",
            "PS2 - Widescreen Codes = 1861",
            "Total available files on server = 86,686",
            " ",
            " ",
            "CURRENT FEATURES V0.6 (BETA):",
            "*********************************************************************************************************************************",
            "SMB/USB MODE (PS1):",
            "Add/batch import PS1 games to your OPL folder (.cue files will be converted to .vcd) The app will determine the games unique ID, rename the file and generate the .elf file.",
            "Delete PS1 game from your OPL folder.",
            "Download/batch download PS1 game art files from server to OPL folder.",
            "Download/batch download PS1 game config files from server to OPL folder.",
            "Share PS1 game art and config files.",
            "Stream PS1 game cheats from server to client application.",
            "Launch PS1 game in emulator.",
            "Generate new .elf files for all PS1 .vcd games in OPL folder.",
            "Generate conf_elm file in OPL folder.",
            "Generate spine ART for all PS1 games.",
            "Perform MD5 check.",
            "Detect games with names greater than 32 characters.",
            "Display game compatability.",
            "Delete all ELF files in SMB folder.",
            "Delete all ART files in SMB folder.",
            "Delete all CFG files in SMB folder.",
            "Delete all CHT files in SMB folder.",
            "Delete unused ART files in SMB folder.",
            "Delete unused CFG files in SMB folder.",
            "Delete unused CHT files in SMB folder.",
            "Delete all pine ART files in SMB folder.",
            "*********************************************************************************************************************************",
            "HDD MODE (PS1):",
            "Add/batch upload PS1 games to your console (.cue files will be converted to .vcd) The app will determine the games unique ID, rename the file and generate the .elf file. (Requires FTP Server Running)",
            "Delete PS1 game from your console. (Requires FTP Server Running)",
            "Download/batch download PS1 game art files from server to OPL/HDD folder.",
            "Download/batch download PS1 game config files from server to OPL/HDD folder.",
            "Share PS1 game art and config files.",
            "Stream PS1 game cheats from server to client application.",
            "Upload game art, config files and cheat files to the console. (Requires FTP Server Running)",
            "Download game art, config files and cheat files from the console. (Requires FTP Server Running)",
            "Browse and delete game art, config files and cheat files that are on the console. (Requires FTP Server Running)",
            "Generate new .elf files for all PS1 .vcd games on the console. (Requires FTP Server Running)",
            "Generate conf_elm file and upload to the console memory card 1. (Requires FTP Server Running)",
            "Display game compatability.",
            "Delete all ART files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete all CFG files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete all CHT files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete unused ART files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete unused CFG files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete unused CHT files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "*********************************************************************************************************************************",
            "SMB/USB MODE (PS2):",
            "Add/batch import PS2 games to your OPL folder. The app will determine the games unique ID and rename the file.",
            "Delete PS2 game from your OPL folder.",
            "Download/batch download PS2 game art files from server to OPL folder.",
            "Download/batch download PS2 game config files from server to OPL folder.",
            "Share PS2 game art and config files.",
            "Stream PS2 game cheats from server to client application.",
            "Launch PS2 game in emulator.",
            "Perform MD5 check.",
            "Detect games with names greater than 32 characters.",
            "Read games from ul.cfg file.",
            "Generate ul.cfg file.",
            "Conver PS2 ISO files to UL format.",
            "Conver PS2 UL format games to ISO.",
            "Generate spine ART.",
            "Delete all ART files in SMB folder.",
            "Delete all CFG files in SMB folder.",
            "Delete all CHT files in SMB folder.",
            "Delete all spine ART folder.",
            "Delete unused ART files in SMB folder.",
            "Delete unused CFG files in SMB folder.",
            "Delete unused CHT files in SMB folder.",
            "*********************************************************************************************************************************",
            "HDD MODE (PS2):",
            "Add/batch upload PS2 games to your console. The app will determine the games unique ID and rename the file. (Requires HDL Server Running)",
            "Download/batch download PS2 game art files from server to OPL/HDD folder.",
            "Download/batch download PS2 game config files from server to OPL/HDD folder.",
            "Share PS2 game art and config files.",
            "Stream PS2 game cheats from server to client application.",
            "Upload game art, config, cheat and vmc files to the console. (Requires FTP Server Running)",
            "Browse and delete game art, config files and cheat files and VMC files that are on the console. (Requires FTP Server Running)",
            "Delete all ART files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete all CFG files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete all CHT files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete unused ART files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete unused CFG files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "Delete unused CHT files in SMB folder and on the consoles internal hard drive. (Requires FTP Server Running)",
            "*********************************************************************************************************************************",
            "",
            "NOTES:",
            "The PS1 .VCD files need to have the games unique ID at the end of their name. (This is required in order to be able to manage the PS1 games remotley and identify the associated art, config and cheat files)",
            "The PS1 .VCD files must follow this naming convention: GAME NAME-GAME ID.VCD. Example: Resident Evil-SLES_123.45.VCD and Resident Evil-SLES_123.45.ELF",
            "",
            "This application will auto name the PS1 .VCD files for you when you use the add game option from the menu.",
            "If you have manually put PS1 .VCD files on your consoles internal hdd and they do not have the games unique ID at the end of their name, this application will not recognise them.",
            "",
            "PS1 Emulator feature was only designed for use with PCSX-Reloaded which supports Windows/Linux - Other emulators may work as long as they end their process when a game window is closed (ePSX does not)",
            "PS2 Emulator feature was only designed for use with PCSX2 which supports Windows/Linux - Other emulators may work as long as they end their process when a game window is closed",
            "",
            "PS2 CD games are not yet supported!"
        };
        
        // Delete previous read me file and then generate a new one
        File localReadMeFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "READ ME.txt");

        //System.out.println(PopsGameManager.PerformQuickHashCheck(localReadMeFile));
        
        if (localReadMeFile.exists() && localReadMeFile.isFile()){
            String latestReadMeMD5 = "3499479286e9a4a929f56983f0af238b";
            String localReadMeMD5 = PopsGameManager.PerformQuickHashCheck(localReadMeFile);
            
            if (!localReadMeMD5.equals(latestReadMeMD5)){
                localReadMeFile.delete();
                List<String> lines = Arrays.asList(fileContent);
                try {Files.write(Paths.get(localReadMeFile.getAbsolutePath()), lines, Charset.forName("UTF-8"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
            }
        }
        else {
            localReadMeFile.delete();
            List<String> lines = Arrays.asList(fileContent);
            try {Files.write(Paths.get(localReadMeFile.getAbsolutePath()), lines, Charset.forName("UTF-8"));} catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
    }
}