package de.applejuicenet.client;

import java.io.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.*;
import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.25 2003/09/07 09:29:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AppleJuiceClient.java,v $
 * Revision 1.25  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.24  2003/09/06 14:57:55  maj0r
 * Splashscreenausblendung verlagert.
 *
 * Revision 1.23  2003/09/05 12:07:28  maj0r
 * Logdateinamen auf 24 Stunden korrigiert.
 *
 * Revision 1.22  2003/08/29 19:34:03  maj0r
 * Einige Aenderungen.
 * Version 0.17 Beta
 *
 * Revision 1.21  2003/08/25 08:01:21  maj0r
 * SplashScreen-Bild geaendert.
 *
 * Revision 1.20  2003/08/24 19:43:23  maj0r
 * Splashscreen eingefuegt.
 *
 * Revision 1.19  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
 * Revision 1.18  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.17  2003/07/01 14:52:45  maj0r
 * Wenn kein Core gefunden wird, k�nnen nun die entsprechenden Einstellungen beim Start der GUI angepasst werden.
 *
 * Revision 1.16  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingef�gt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.15  2003/06/24 12:06:49  maj0r
 * log4j eingef�gt (inkl. Bedienung �ber Einstellungsdialog).
 *
 * Revision 1.14  2003/06/22 20:03:54  maj0r
 * Konsolenausgaben hinzugef�gt.
 *
 * Revision 1.13  2003/06/22 19:54:45  maj0r
     * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugef�gt.
 *
 * Revision 1.12  2003/06/22 19:02:58  maj0r
 * Fehlernachricht bei nicht erreichbarem Core ge�ndert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class AppleJuiceClient {
  private static Logger logger;

  public static void main(String[] args) {
    Logger rootLogger = Logger.getRootLogger();
    logger = Logger.getLogger(AppleJuiceClient.class.getName());

    String datum = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date(
        System.currentTimeMillis()));
    String dateiName;
    dateiName = datum + ".html";
    HTMLLayout layout = new HTMLLayout();
    layout.setTitle("appleJuice-Core-GUI-Log " + datum);
    layout.setLocationInfo(true);

    try {
      String path = System.getProperty("user.dir") + File.separator +
          "logs";
      File aFile = new File(path);
      if (!aFile.exists()) {
        aFile.mkdir();
      }
      FileAppender fileAppender = new FileAppender(layout,
          path + File.separator + dateiName);
      rootLogger.addAppender(fileAppender);
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    rootLogger.setLevel(OptionsManager.getInstance().getLogLevel());

    try {
      String nachricht = "appleJuice-Core-GUI Version " + ApplejuiceFassade.GUI_VERSION + " wird gestartet...";
      if (logger.isEnabledFor(Level.INFO))
        logger.info(nachricht);
      System.out.println(nachricht);

      Frame dummyFrame = new Frame();
      Image img = IconManager.getInstance().getIcon("applejuice").getImage();
      dummyFrame.setIconImage(img);
      String titel = null;
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      QuickConnectionSettingsDialog remoteDialog = null;
      while (!ApplejuiceFassade.istCoreErreichbar()) {
        titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "caption"}));
        nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
              getFirstAttrbuteByTagName(new String[] {"javagui", "startup",
                                        "fehlversuch"}));
        JOptionPane.showMessageDialog(dummyFrame, nachricht, titel,
                                      JOptionPane.ERROR_MESSAGE);
        remoteDialog = new QuickConnectionSettingsDialog(dummyFrame);
        remoteDialog.show();
        if (remoteDialog.getResult()==QuickConnectionSettingsDialog.ABGEBROCHEN){
            nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "startup",
                                          "verbindungsfehler"}));
            nachricht = nachricht.replaceFirst("%s",
                                               OptionsManager.getInstance().
                                               getRemoteSettings().
                                               getHost());
            JOptionPane.showMessageDialog(dummyFrame, nachricht, titel,
                                          JOptionPane.OK_OPTION);
            logger.fatal(nachricht);
            System.out.println("Fehler: " + nachricht);
            System.exit( -1);
        }
      }
      Splash splash = new Splash(IconManager.getInstance().getIcon("splashscreen").getImage());
      splash.show();
      PositionManager lm = PositionManager.getInstance();
      AppleJuiceDialog theApp = new AppleJuiceDialog();
      if (lm.isLegal()){
          theApp.setLocation(lm.getMainXY());
          theApp.setSize(lm.getMainDimension());
      }
      else{
          Dimension appDimension = theApp.getSize();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          theApp.setLocation( (screenSize.width - appDimension.width) / 2,
                             (screenSize.height - appDimension.height) / 2);
      }
      theApp.show();
      nachricht = "appleJuice-Core-GUI l�uft...";
      if (logger.isEnabledFor(Level.INFO))
        logger.info(nachricht);
      System.out.println(nachricht);
      splash.dispose();
    }
    catch (Exception e) {
      if (logger.isEnabledFor(Level.FATAL))
        logger.fatal("Programmabbruch", e);
      System.exit( -1);
    }
  }
}