package de.applejuicenet.client.gui;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.plugins.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODPluginPanel.java,v 1.8 2003/09/04 10:13:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODPluginPanel.java,v $
 * Revision 1.8  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.7  2003/09/04 06:27:53  maj0r
 * Muell entfernt.
 *
 * Revision 1.6  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ODPluginPanel
        extends JPanel {
    private JList pluginList;
    private JEditorPane beschreibung = new JEditorPane();
    private JLabel label1 = new JLabel();
    private AppleJuiceDialog theApp;
    private String name;
    private String version;
    private String autor;
    private String erlaeuterung;
    private Logger logger;

    public ODPluginPanel(JFrame parent) {
        logger = Logger.getLogger(getClass());
        try
        {
            theApp = (AppleJuiceDialog) parent;
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() throws Exception {
        PluginConnector[] plugins = theApp.getPlugins();
        Vector v = new Vector();
        if (plugins.length != 0)
        {
            for (int i = 0; i < plugins.length; i++)
            {
                v.add(new PluginContainer(plugins[i]));
            }
        }
        Dimension parentSize = theApp.getSize();
        beschreibung.setBackground(label1.getBackground());
        beschreibung.setPreferredSize(new Dimension(parentSize.width / 3,
                                                    beschreibung.getPreferredSize().
                                                    height));
        beschreibung.setEditable(false);
        pluginList = new JList(v);
        pluginList.setPreferredSize(new Dimension(190,
                                                  pluginList.getPreferredSize().
                                                  height));
        pluginList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                pluginList_valueChanged(e);
            }
        });
        setLayout(new BorderLayout());
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"einstform", "Label11",
                                                                                                "caption"})) + ":");
        name = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
                                                         String[]{"javagui", "options", "plugins", "name"}));
        version = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                    getFirstAttrbuteByTagName(new
                                                            String[]{"javagui", "options", "plugins", "version"}));
        erlaeuterung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"javagui", "options", "plugins",
                                                                                                "beschreibung"}));
        autor = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                  getFirstAttrbuteByTagName(new
                                                          String[]{"javagui", "options", "plugins", "autor"}));

        add(label1, BorderLayout.NORTH);
        add(pluginList, BorderLayout.WEST);
        JScrollPane sp = new JScrollPane(beschreibung);
        add(sp, BorderLayout.CENTER);
    }

    class PluginContainer {
        private PluginConnector plugin;

        public PluginContainer(PluginConnector plugin) {
            this.plugin = plugin;
        }

        public String toString() {
            return plugin.getTitle();
        }

        public String getBeschreibung() {
            String text;
            text = name + ":\r\n" + plugin.getTitle() + "\r\n\r\n" + autor + ":\r\n" +
                    plugin.getAutor()
                    + "\r\n\r\n" + version + ":\r\n" + plugin.getVersion()
                    + "\r\n\r\n" + erlaeuterung + ":\r\n" + plugin.getBeschreibung();
            return text;
        }
    }

    void pluginList_valueChanged(ListSelectionEvent e) {
        PluginContainer selected = (PluginContainer) ((JList) e.getSource()).
                getSelectedValue();
        beschreibung.setText(selected.getBeschreibung());
    }
}