package oplpops.game.manager;

public class Main {

    public static void main(String[] args) {

        // Check the command line parameters and then start the application
        if (args.length > 0){
            if (args[0].toUpperCase().equals("-DEBUG")){PopsGameManager.setDebugMode(true);} else {PopsGameManager.setDebugMode(false);}
            if (args[0].toUpperCase().equals("-TEST")){PopsGameManager.setTestMode(true);} else {PopsGameManager.setTestMode(false);}
        }
        
        PopsGameManager.startApplication();
    }
}