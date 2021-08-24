package oplpops.game.manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLFileManager {
    
    private static final File settingsXMLFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "settings.xml");
    private static final File settingsFile = new File(PopsGameManager.getCurrentDirectory() + File.separator + "oplpops-settings");
    private static final File keyFileSettings = new File(PopsGameManager.getCurrentDirectory() + File.separator + "lib" + File.separator + "data" + File.separator + "data_3");
    
    public  XMLFileManager() {}
  
    
    // This writes the settings to the settings.xml file
    public static void writeSettingsXML() throws TransformerException, ParserConfigurationException{

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("settings");
        doc.appendChild(rootElement);
        
        // Consoleaddress element
        Element consoleIP = doc.createElement("consoleaddress");
        consoleIP.appendChild(doc.createTextNode(PopsGameManager.getPS2IP()));
        rootElement.appendChild(consoleIP);
        
        // Oplfolder element
        Element oplFolder = doc.createElement("oplfolder");
        oplFolder.appendChild(doc.createTextNode(PopsGameManager.getOPLFolder()));
        rootElement.appendChild(oplFolder);

        // Currentconsole element
        Element currentConsole = doc.createElement("currentconsole");
        currentConsole.appendChild(doc.createTextNode(PopsGameManager.getCurrentConsole()));
        rootElement.appendChild(currentConsole);

        // Currentmode element
        Element currentMode = doc.createElement("currentmode");
        currentMode.appendChild(doc.createTextNode(PopsGameManager.getCurrentMode()));
        rootElement.appendChild(currentMode);

        // Useemulator ps2 element
        Element useEmulatorPS2 = doc.createElement("useemulatorps2");
        useEmulatorPS2.appendChild(doc.createTextNode(PopsGameManager.getEmulatorInUsePS2().toString()));
        rootElement.appendChild(useEmulatorPS2);
        
        // Emulatorpath ps2 element
        Element emulatorPathPS2 = doc.createElement("emulatorpathps2");
        emulatorPathPS2.appendChild(doc.createTextNode(PopsGameManager.getEmulatorPathPS2()));
        rootElement.appendChild(emulatorPathPS2);
        
        // Emulatorfull ps2 element
        Element emulatorFullPS2 = doc.createElement("emulatorfullps2");
        emulatorFullPS2.appendChild(doc.createTextNode(PopsGameManager.getEmulatorFullScreenPS2().toString()));
        rootElement.appendChild(emulatorFullPS2);

        // Useemulator ps1 element
        Element useEmulatorPS1 = doc.createElement("useemulatorps1");
        useEmulatorPS1.appendChild(doc.createTextNode(PopsGameManager.getEmulatorInUsePS1().toString()));
        rootElement.appendChild(useEmulatorPS1);

        // Emulatorpath ps1 element
        Element emulatorPathPS1 = doc.createElement("emulatorpathps1");
        emulatorPathPS1.appendChild(doc.createTextNode(PopsGameManager.getEmulatorPathPS1()));
        rootElement.appendChild(emulatorPathPS1);
        
        // Emulatorfull ps1 element
        Element emulatorFullPS1 = doc.createElement("emulatorfullps1");
        emulatorFullPS1.appendChild(doc.createTextNode(PopsGameManager.getEmulatorFullScreenPS1().toString()));
        rootElement.appendChild(emulatorFullPS1);

        // PS2 HDD VCD partition
        Element remoteVCDPath = doc.createElement("remotevcdpath");
        remoteVCDPath.appendChild(doc.createTextNode(PopsGameManager.getRemoteVCDPath()));
        rootElement.appendChild(remoteVCDPath);
        
        // PS2 HDD ELF partition
        Element remoteELFPath = doc.createElement("remoteelfpath");
        remoteELFPath.appendChild(doc.createTextNode(PopsGameManager.getRemoteELFPath()));
        rootElement.appendChild(remoteELFPath);

        // PS2 HDD OPL partition
        Element remoteOPLPath = doc.createElement("remoteoplpath");
        remoteOPLPath.appendChild(doc.createTextNode(PopsGameManager.getRemoteOPLPath()));
        rootElement.appendChild(remoteOPLPath);
        
        // PS1 Compatability coloured list
        Element compatabilityPS1 = doc.createElement("compatabilityps1");
        compatabilityPS1.appendChild(doc.createTextNode(PopsGameManager.getGameCompatabilityPS1().toString()));
        rootElement.appendChild(compatabilityPS1);
        
        // PS2 UL Game coloured list
        Element ulGamePS2 = doc.createElement("splitgamesps2");
        ulGamePS2.appendChild(doc.createTextNode(PopsGameManager.getSplitGameDisplayPS2().toString()));
        rootElement.appendChild(ulGamePS2);
        
        // Write the contents to the xml file
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.STANDALONE, "yes");
            tr.transform(new DOMSource(doc), new StreamResult(settingsXMLFile));
            
            encryptSettingsFile();
            
        } catch (TransformerException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    

    // This gets the values from the settings.xml file
    public static void readSettingsXML(){

        decryptSettingsFile();
        
        if (settingsXMLFile.exists() && settingsXMLFile.isFile()){
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {dBuilder = dbFactory.newDocumentBuilder();} 
            catch (ParserConfigurationException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

            if (dBuilder != null) {

                Document doc = null;
                try {doc = dBuilder.parse(settingsXMLFile);} 
                catch (SAXException ex) {JOptionPane.showMessageDialog(null, "The oplpops-settings file appears to have been modified or moved!\n\nYou will need to set the Mode again.", " Error Loading Settings!", JOptionPane.ERROR_MESSAGE);} 
                catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}

                if (doc == null){PopsGameManager.setFisrtLaunch(true);}
                else {
                    doc.getDocumentElement().normalize();
                    NodeList nList = doc.getElementsByTagName("settings");

                    if (nList != null){

                        for (int i = 0; i < nList.getLength(); i++) {
                            Node nNode = nList.item(i);

                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElement = (Element) nNode;
                                try{
                                    // General settings
                                    PopsGameManager.setPS2IP(eElement.getElementsByTagName("consoleaddress").item(0).getTextContent());
                                    PopsGameManager.setOPLFolder(eElement.getElementsByTagName("oplfolder").item(0).getTextContent());
                                    PopsGameManager.setCurrentMode(eElement.getElementsByTagName("currentmode").item(0).getTextContent());
                                    PopsGameManager.setCurrentConsole(eElement.getElementsByTagName("currentconsole").item(0).getTextContent());

                                    // PS2 emulator settings
                                    if (eElement.getElementsByTagName("useemulatorps2").item(0).getTextContent().equals("false")) PopsGameManager.setEmulatorInUsePS2(false);
                                    else if (eElement.getElementsByTagName("useemulatorps2").item(0).getTextContent().equals("true")) PopsGameManager.setEmulatorInUsePS2(true);
                                    PopsGameManager.setEmulatorPathPS2(eElement.getElementsByTagName("emulatorpathps2").item(0).getTextContent());
                                    if (eElement.getElementsByTagName("emulatorfullps2").item(0).getTextContent().equals("false")) PopsGameManager.setEmulatorFullScreenPS2(false);
                                    else if (eElement.getElementsByTagName("emulatorfullps2").item(0).getTextContent().equals("true")) PopsGameManager.setEmulatorFullScreenPS2(true);

                                    // PS1 emulator settings
                                    if (eElement.getElementsByTagName("useemulatorps1").item(0).getTextContent().equals("false")) PopsGameManager.setEmulatorInUsePS1(false);
                                    else if (eElement.getElementsByTagName("useemulatorps1").item(0).getTextContent().equals("true")) PopsGameManager.setEmulatorInUsePS1(true);
                                    PopsGameManager.setEmulatorPathPS1(eElement.getElementsByTagName("emulatorpathps1").item(0).getTextContent());
                                    if (eElement.getElementsByTagName("emulatorfullps1").item(0).getTextContent().equals("false")) PopsGameManager.setEmulatorFullScreenPS1(false);
                                    else if (eElement.getElementsByTagName("emulatorfullps1").item(0).getTextContent().equals("true")) PopsGameManager.setEmulatorFullScreenPS1(true);
                                
                                    // Remote PS2 file paths
                                    PopsGameManager.setRemoteVCDPath(eElement.getElementsByTagName("remotevcdpath").item(0).getTextContent());
                                    PopsGameManager.setRemoteELFPath(eElement.getElementsByTagName("remoteelfpath").item(0).getTextContent());
                                    PopsGameManager.setRemoteOPLPath(eElement.getElementsByTagName("remoteoplpath").item(0).getTextContent());
                                    
                                    // PS1 Compatability mode
                                    if (eElement.getElementsByTagName("compatabilityps1").item(0).getTextContent().equals("false")) {PopsGameManager.setGameCompatabilityPS1(false);}
                                    else if (eElement.getElementsByTagName("compatabilityps1").item(0).getTextContent().equals("true")) {PopsGameManager.setGameCompatabilityPS1(true);}
                                    
                                    // PS2 Split game highlight
                                    if (eElement.getElementsByTagName("splitgamesps2").item(0).getTextContent().equals("false")) {PopsGameManager.setSplitGameDisplayPS2(false);}
                                    else if (eElement.getElementsByTagName("splitgamesps2").item(0).getTextContent().equals("true")) {PopsGameManager.setSplitGameDisplayPS2(true);}
                                }
                                catch(NullPointerException ex){
                                    JOptionPane.showMessageDialog(null, "The oplpops-settings file appears to have been modified or moved!\n\nYou will need to set the Mode again.", " Error Loading Settings!", JOptionPane.ERROR_MESSAGE);
                                    PopsGameManager.setFisrtLaunch(true);
                                }
                            }
                        }
                    }
                }
            }
            encryptSettingsFile();
        }
        else {PopsGameManager.setFisrtLaunch(true);}
    }
    
    
    // Encrypt the setting .xml file
    private static void encryptSettingsFile(){
        try {
            List<String> list = Files.readAllLines(settingsXMLFile.toPath(), Charset.defaultCharset());
            // Encrypt 
            FileEncryptor encryptor = new FileEncryptor();
            encryptor.EncryptData(list, settingsFile.getAbsolutePath(), keyFileSettings.getAbsolutePath());
            if (settingsXMLFile.exists() && settingsXMLFile.isFile()) {settingsXMLFile.delete();}
        } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    
    // Decrypt the settings .xml file
    private static void decryptSettingsFile(){
        
        // Ensure that the settings file exists
        if (new File(settingsFile.getAbsolutePath()).exists() && new File(settingsFile.getAbsolutePath()).isFile()){
            try {
                // Decrypt the encrypted settings file
                FileEncryptor encryptor = new FileEncryptor();
                List<String> list = encryptor.DecryptData(settingsFile.getAbsolutePath(), keyFileSettings.getAbsolutePath());
                Files.write(Paths.get(PopsGameManager.getCurrentDirectory() + File.separator + "settings.xml"),list,Charset.defaultCharset());
            } catch (IOException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        }
    }
}