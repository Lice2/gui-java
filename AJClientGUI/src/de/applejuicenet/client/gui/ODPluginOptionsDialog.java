package de.applejuicenet.client.gui;

import de.applejuicenet.client.gui.plugins.PluginConnector;
import java.awt.BorderLayout;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import javax.swing.JFrame;
import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODPluginOptionsDialog.java,v 1.1 2004/01/01 15:30:21 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ODPluginOptionsDialog.java,v $
 * Revision 1.1  2004/01/01 15:30:21  maj0r
 * Plugins koennen nun ein JPanel zB fuer Optionen implementieren.
 * Dieses wird dann im Optionendialog angezeigt.
 *
 *
 */

public class ODPluginOptionsDialog extends JDialog{
    private PluginConnector pluginConnector;

    public ODPluginOptionsDialog(JDialog parent, PluginConnector pluginConnector) {
        super(parent, true);
        this.pluginConnector = pluginConnector;
        init();
    }

    private void init(){
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String title = pluginConnector.getTitle() + " - ";
        title += ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(new
                                      String[] {"javagui", "options", "plugins",
                                      "einstellungen"}));
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pluginConnector.getOptionPanel(), BorderLayout.CENTER);
        pack();
        Dimension appDimension = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        setLocation( (screenSize.width -
                                   appDimension.width) / 2,
                                 (screenSize.height -
                                  appDimension.height) / 2);
    }
}