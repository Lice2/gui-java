package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.net.URLDecoder;
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/WebXMLParser.java,v 1.14 2003/10/01 07:25:44 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: WebXMLParser.java,v $
 * Revision 1.14  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 * Revision 1.13  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.12  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.11  2003/08/29 19:34:03  maj0r
 * Einige Aenderungen.
 * Version 0.17 Beta
 *
 * Revision 1.10  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
 * Revision 1.9  2003/08/19 12:38:47  maj0r
 * Passworteingabe und md5 korrigiert.
 *
 * Revision 1.8  2003/08/16 17:50:06  maj0r
 * Diverse Farben k�nnen nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.7  2003/08/15 17:53:54  maj0r
 * Tree fuer Shareauswahl fortgefuehrt, aber noch nicht fertiggestellt.
 *
 * Revision 1.6  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.5  2003/08/09 16:47:42  maj0r
 * Diverse �nderungen.
 *
 * Revision 1.4  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.3  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.2  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugef�gt.
 *
 * Revision 1.1  2003/06/22 18:58:53  maj0r
 * Umbenannt und Hostverwendung korrigiert.
 *
 * Revision 1.9  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public abstract class WebXMLParser
        extends XMLDecoder implements DataUpdateListener {
    private String host;
    private String xmlCommand;
    private long timestamp = 0;
    private boolean firstRun = true;
    private boolean useTimestamp = true;
    private String password;
    private Logger logger;

    public WebXMLParser(String xmlCommand, String parameters) {
        super();
        init(xmlCommand);
        PropertiesManager.getOptionsManager().addSettingsListener(this);
    }

    public WebXMLParser(String xmlCommand, String parameters,
                        boolean useTimestamp) {
        super();
        this.useTimestamp = useTimestamp;
        init(xmlCommand);
    }

    private void init(String xmlCommand) {
        logger = Logger.getLogger(getClass());
        ConnectionSettings rc = PropertiesManager.getOptionsManager().getRemoteSettings();
        host = rc.getHost();
        password = rc.getOldPassword();
        if (host == null || host.length() == 0)
            host = "localhost";
        this.xmlCommand = xmlCommand;
        webXML = true;
    }

    public void reload(String parameters) {
        String xmlData = null;
        try
        {
            if (useTimestamp)
            {
                xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                                       xmlCommand + "?password=" + password + "&timestamp=" +
                                                       timestamp + parameters);
            }
            else
            {
                if (parameters.length() != 0)
                {
                    xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                                           xmlCommand + "?password=" + password + "&" + parameters);
                }
                else
                {
                    xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                                           xmlCommand + "?password=" + password);
                }
            }
        }
        catch (WebSiteNotFoundException ex)
        {
            AppleJuiceDialog.closeWithErrormessage(
                    "Die Verbindung zum Core ist abgebrochen.\r\nDas GUI wird beendet.", true);
        }
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlData)));
            if (!firstRun)
            {
                if (useTimestamp)
                {
                    timestamp = Long.parseLong(getFirstAttrbuteByTagName(new String[]{
                        "applejuice", "time"}
                                                                         , true));
                }
            }
            else
            {
                firstRun = !firstRun;
            }
        }
        catch (Exception e)
        {
            Exception x = e;
            if (e instanceof SAXException){
                if (((SAXException)e).getException() != null)
                {
                    x = ((SAXException)e).getException();
                }
            }
            if (logger.isEnabledFor(Level.ERROR)){
                String zeit = Long.toString(System.currentTimeMillis());
                String path = System.getProperty("user.dir") + File.separator +
                    "logs";
                File aFile = new File(path);
                if (!aFile.exists()) {
                  aFile.mkdir();
                }
                FileWriter fileWriter = null;
                String dateiname = path + File.separator + zeit + ".exc";
                try {
                    fileWriter = new FileWriter(dateiname);
                    fileWriter.write(xmlData);
                    fileWriter.close();
                }
                catch (IOException ioE) {
                    ioE.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                logger.error("Unbehandelte SAX-Exception -> Inhalt in " + dateiname, x);
            }
            x.printStackTrace();
        }
    }

    public abstract void update();

    public void setPassword(String password) {
        this.password = password;
    }

    public void fireContentChanged(int type, Object content) {
        if (type==DataUpdateListener.CONNECTION_SETTINGS_CHANGED){
            host = ((ConnectionSettings)content).getHost();
            password = ((ConnectionSettings)content).getOldPassword();
            if (host == null || host.length() == 0)
                host = "localhost";
        }
    }
}