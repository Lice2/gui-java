package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODPluginOptionsDialog.java,v 1.5 2004/02/21 18:20:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ODPluginOptionsDialog.java,v $
 * Revision 1.5  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.4  2004/02/05 23:11:26  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2004/01/05 13:21:32  maj0r
 * Logger eingebaut.
 *
 * Revision 1.2  2004/01/04 10:31:43  maj0r
 * PluginOptionenDialog ueberarbeitet.
 *
 * Revision 1.1  2004/01/01 15:30:21  maj0r
 * Plugins koennen nun ein JPanel zB fuer Optionen implementieren.
 * Dieses wird dann im Optionendialog angezeigt.
 *
 *
 */

public class ODPluginOptionsDialog
    extends JDialog {
    private PluginConnector pluginConnector;
    private JButton schliessen = new JButton();
    private Logger logger;

    public ODPluginOptionsDialog(JDialog parent,
                                 PluginConnector pluginConnector) {
        super(parent, true);
        this.pluginConnector = pluginConnector;
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        schliessen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ODPluginOptionsDialog.this.dispose();
            }
        });
        String title = pluginConnector.getTitle() + " - ";
        title += ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.plugins.einstellungen"));
        schliessen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.plugins.schliessen")));
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pluginConnector.getOptionPanel(),
                             BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(schliessen);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
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
