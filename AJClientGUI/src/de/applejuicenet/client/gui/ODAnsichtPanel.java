package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODAnsichtPanel.java,v 1.11 2004/02/21 18:20:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ODAnsichtPanel.java,v $
 * Revision 1.11  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.10  2004/02/05 23:11:26  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.9  2004/01/29 15:52:33  maj0r
 * Bug #153 umgesetzt (Danke an jr17)
 * Verbindungsdialog kann nun per Option beim naechsten GUI-Start erzwungen werden.
 *
 * Revision 1.8  2004/01/25 10:16:42  maj0r
 * Optionenmenue ueberarbeitet.
 *
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/10/31 16:24:58  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt.
 *
 * Revision 1.5  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.4  2003/09/02 16:08:11  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.3  2003/08/25 18:02:10  maj0r
 * Sprachberuecksichtigung und Tooltipps eingebaut.
 *
 * Revision 1.2  2003/08/16 17:49:56  maj0r
 * Diverse Farben k�nnen nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.1  2003/08/15 18:31:36  maj0r
 * Farbdialog in Optionen eingebaut.
 *
 *
 */

public class ODAnsichtPanel
    extends JPanel
    implements OptionsRegister {
    private JLabel farbeFertigerDownload = new JLabel("      ");
    private JLabel farbeQuelle = new JLabel("      ");
    private Settings settings;
    JCheckBox cmbAktiv = new JCheckBox();
    JCheckBox cmbDownloadUebersicht = new JCheckBox();
    private JCheckBox cmbStartscreenZeigen = new JCheckBox();
    private Logger logger;
    private Icon menuIcon;
    private String menuText;
    private boolean dirty = false;

    public ODAnsichtPanel() {
        logger = Logger.getLogger(getClass());
        try {
            settings = Settings.getSettings();
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
        IconManager im = IconManager.getInstance();
        menuIcon = im.getIcon("opt_ansicht");
        cmbDownloadUebersicht.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.downloadansicht")));
        cmbAktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.aktiv")));
        cmbStartscreenZeigen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.zeigestartscreen")));
        setLayout(new BorderLayout());
        farbeFertigerDownload.setOpaque(true);
        farbeFertigerDownload.setBackground(settings.
                                            getDownloadFertigHintergrundColor());
        farbeFertigerDownload.addMouseListener(new ColorChooserMouseAdapter());
        farbeQuelle.setOpaque(true);
        farbeQuelle.setBackground(settings.getQuelleHintergrundColor());
        farbeQuelle.addMouseListener(new ColorChooserMouseAdapter());
        cmbAktiv.setSelected(settings.isFarbenAktiv());
        cmbDownloadUebersicht.setSelected(settings.isDownloadUebersicht());
        cmbDownloadUebersicht.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                settings.setDownloadUebersicht(cmbDownloadUebersicht.isSelected());
            }
        });
        cmbStartscreenZeigen.setSelected(PropertiesManager.getOptionsManager().
                                         shouldShowConnectionDialogOnStartup());
        cmbStartscreenZeigen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbAktiv.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                settings.setFarbenAktiv(cmbAktiv.isSelected());
            }
        });
        ImageIcon icon = im.getIcon("hint");
        JLabel hint1 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        JLabel hint2 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        String tooltipp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.ttipp_farbewaehlen"));
        hint1.setToolTipText(tooltipp);
        hint2.setToolTipText(tooltipp);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.bottom = 5;
        panel1.setBorder(BorderFactory.createTitledBorder(ZeichenErsetzer.
            korrigiereUmlaute(languageSelector.
                              getFirstAttrbuteByTagName(".root.javagui.options.ansicht.hintergrundfarben"))));
        panel1.add(cmbAktiv, constraints);
        constraints.gridy = 1;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        panel1.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.fertigerdownload"))), constraints);
        constraints.gridy = 2;
        panel1.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.quelle"))), constraints);
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.caption"));
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel1.add(farbeFertigerDownload, constraints);
        constraints.gridy = 2;
        panel1.add(farbeQuelle, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        panel1.add(hint1, constraints);
        constraints.gridy = 2;
        panel1.add(hint2, constraints);
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(panel1, BorderLayout.NORTH);
        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(cmbStartscreenZeigen, BorderLayout.NORTH);
        panel3.add(cmbDownloadUebersicht, BorderLayout.SOUTH);
        panel2.add(panel3, BorderLayout.SOUTH);
        add(panel2, BorderLayout.WEST);
    }

    public boolean save() {
        try {
            settings.save();
            return true;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return false;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean shouldShowStartcreen() {
        return cmbStartscreenZeigen.isSelected();
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }

    class ColorChooserMouseAdapter
        extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            JLabel source = (JLabel) e.getSource();
            JColorChooser jColorChooserBackground = new JColorChooser();
            Color newColor = jColorChooserBackground.showDialog(null,
                ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.options.ansicht.hintergrundfarbewaehlen")),
                source.getBackground());
            if (newColor != null &&
                newColor.getRGB() != source.getBackground().getRGB()) {
                source.setBackground(newColor);
                if (source == farbeQuelle) {
                    settings.setQuelleHintergrundColor(newColor);
                }
                else {
                    settings.setDownloadFertigHintergrundColor(newColor);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }
}
