package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.wizard.*;
import de.applejuicenet.client.gui.listener.LanguageListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/WizardDialog.java,v 1.3 2003/09/09 06:37:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: WizardDialog.java,v $
 * Revision 1.3  2003/09/09 06:37:36  maj0r
 * Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.2  2003/09/08 14:55:15  maj0r
 * Wizarddialog weitergefuehrt.
 *
 * Revision 1.1  2003/09/08 06:27:11  maj0r
 * Um Wizard erweitert, aber noch nicht fertiggestellt.
 *
 *
 */


public class WizardDialog extends JDialog implements LanguageListener {
    private Logger logger;
    private WizardPanel aktuellesPanel;
    private WizardPanel schritt1 = new Schritt1Panel();
    private WizardPanel schritt2 = new Schritt2Panel();
    private WizardPanel schritt3 = new Schritt3Panel();
    private WizardPanel schritt4 = new Schritt4Panel();
    private WizardPanel schritt5 = new Schritt5Panel();
    private JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JButton zurueck = new JButton();
    private JButton weiter = new JButton();
    private JButton ende = new JButton();

    public WizardDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        try{
            init();
            LanguageSelector ls = LanguageSelector.getInstance();
            ls.addLanguageListener(this);
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());
        ImageIcon icon1 = IconManager.getInstance().getIcon("wizardbanner");
        JLabel label1 = new JLabel(icon1);

        schritt1.setVorherigesPanel(null);
        schritt1.setNaechstesPanel(schritt2);
        schritt2.setVorherigesPanel(schritt1);
        schritt2.setNaechstesPanel(schritt3);
        schritt3.setVorherigesPanel(schritt2);
        schritt3.setNaechstesPanel(schritt4);
        schritt4.setVorherigesPanel(schritt3);
        schritt4.setNaechstesPanel(schritt5);
        schritt5.setVorherigesPanel(schritt4);
        schritt5.setNaechstesPanel(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                close();
            }
        });

        ende.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                WizardDialog.this.dispose();
            }
        });
        zurueck.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                if (aktuellesPanel.getVorherigesPanel()!=null){
                    getContentPane().remove(aktuellesPanel);
                    aktuellesPanel = aktuellesPanel.getVorherigesPanel();
                    getContentPane().add(aktuellesPanel, BorderLayout.CENTER);
                    weiter.setEnabled(true);
                    if (aktuellesPanel.getVorherigesPanel()==null){
                        zurueck.setEnabled(false);
                    }
                    else{
                        zurueck.setEnabled(true);
                    }
                    WizardDialog.this.validate();
                    WizardDialog.this.repaint();
                }
            }
        });
        weiter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                if (aktuellesPanel.getNaechstesPanel()!=null){
                    getContentPane().remove(aktuellesPanel);
                    aktuellesPanel = aktuellesPanel.getNaechstesPanel();
                    getContentPane().add(aktuellesPanel, BorderLayout.CENTER);
                    zurueck.setEnabled(true);
                    if (aktuellesPanel.getNaechstesPanel()==null){
                        weiter.setEnabled(false);
                    }
                    else{
                        weiter.setEnabled(true);
                    }
                    WizardDialog.this.validate();
                    WizardDialog.this.repaint();
                }
            }
        });
        ende.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                close();
            }
        });

        buttons.add(zurueck);
        buttons.add(weiter);
        buttons.add(ende);
        buttons.setBackground(Color.WHITE);
        zurueck.setEnabled(false);

        getContentPane().add(label1, BorderLayout.NORTH);
        getContentPane().add(schritt1, BorderLayout.CENTER);
        aktuellesPanel = schritt1;
        getContentPane().add(buttons, BorderLayout.SOUTH);
        setSize(icon1.getIconWidth(), icon1.getIconHeight() + 180 + buttons.getPreferredSize().height);
        setResizable(false);
        fireLanguageChanged();
    }

    private void close(){
        LanguageSelector.getInstance().removeLanguageListener(this);
        dispose();
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "titel"})));
        zurueck.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "zurueck"})));
        weiter.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "weiter"})));
        ende.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "ende"})));
        schritt1.fireLanguageChanged();
        schritt2.fireLanguageChanged();
        schritt3.fireLanguageChanged();
        schritt4.fireLanguageChanged();
        schritt5.fireLanguageChanged();
        repaint();
    }
}