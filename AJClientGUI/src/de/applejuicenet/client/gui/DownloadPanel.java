package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.gui.tables.download.DownloadTableCellRenderer;
import de.applejuicenet.client.gui.tables.download.DownloadNode;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.JTreeTable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPanel.java,v 1.30 2003/08/05 05:11:59 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadPanel.java,v $
 * Revision 1.30  2003/08/05 05:11:59  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.29  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.28  2003/07/04 15:25:38  maj0r
 * Version erh�ht.
 * DownloadModel erweitert.
 *
 * Revision 1.27  2003/07/04 06:43:51  maj0r
 * Diverse �nderungen am DownloadTableModel.
 *
 * Revision 1.26  2003/07/03 19:11:16  maj0r
 * DownloadTable �berarbeitet.
 *
 * Revision 1.25  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett �berarbeitet.
 *
 * Revision 1.24  2003/07/01 18:49:03  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.23  2003/07/01 18:41:39  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.22  2003/07/01 18:34:28  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.21  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class DownloadPanel
    extends JPanel
    implements LanguageListener, RegisterI, DataUpdateListener {
  private JTextField downloadLink = new JTextField();
  private JButton btnStartDownload = new JButton("Download");
  private PowerDownloadPanel powerDownloadPanel;
  private JTreeTable downloadTable;
  private JTable actualDlOverviewTable = new JTable();
  private JLabel linkLabel = new JLabel("ajfsp-Link hinzuf�gen");
  private DownloadModel downloadModel;
  private JLabel label4 = new JLabel("Vorhanden");
  private JLabel label3 = new JLabel("Nicht vorhanden");
  private JLabel label2 = new JLabel("In Ordnung");
  private JLabel label1 = new JLabel("�berpr�ft");
  private JPopupMenu popup = new JPopupMenu();
  JMenuItem item1;
  JMenuItem item2;
  JMenuItem item3;
  JMenuItem item4;
  JMenuItem item5;
  JMenuItem item6;

  public DownloadPanel() {
    powerDownloadPanel = new PowerDownloadPanel(this);
    try {
      jbInit();
      LanguageSelector.getInstance().addLanguageListener(this);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new GridBagLayout());
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());

    item1 = new JMenuItem("Abbrechen");
    item2 = new JMenuItem("Pause/Fortsetzen");
    item3 = new JMenuItem("Powerdownload");
    item4 = new JMenuItem("Umbenennen");
    item5 = new JMenuItem("Zielordner �ndern");
    item6 = new JMenuItem("Fertige �bertragungen entfernen");
    popup.add(item1);
    popup.add(item2);
    popup.add(item3);
    popup.add(item4);
    popup.add(item5);
    popup.add(item6);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(new BorderLayout());
    tempPanel.add(linkLabel, BorderLayout.WEST);
    tempPanel.add(downloadLink, BorderLayout.CENTER);
    tempPanel.add(btnStartDownload, BorderLayout.EAST);
    topPanel.add(tempPanel, constraints);
    constraints.gridwidth = 3;
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    downloadModel = new DownloadModel();
    downloadTable = new JTreeTable(downloadModel);

    DownloadTableCellRenderer renderer = new DownloadTableCellRenderer();
    for (int i=1; i<downloadTable.getColumnModel().getColumnCount(); i++){
        downloadTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }

    btnStartDownload.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            String link = downloadLink.getText();
            if (link.length()!=0){
                DataManager.getInstance().processLink(link);
            }
        }
    });
    downloadTable.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (e.getClickCount() == 2 && downloadTable.columnAtPoint(p) != 0) {
          TreeTableModelAdapter model = (TreeTableModelAdapter) downloadTable.
              getModel();
          int selectedRow = downloadTable.getSelectedRow();
          ( (TreeTableModelAdapter) downloadTable.getModel()).expandOrCollapseRow(selectedRow);
        }
      }

      public void mousePressed(MouseEvent me) {
        Point p = me.getPoint();
        int iRow = downloadTable.rowAtPoint(p);
        int iCol = downloadTable.columnAtPoint(p);
        downloadTable.setRowSelectionInterval(iRow, iRow);
        downloadTable.setColumnSelectionInterval(iCol, iCol);
        maybeShowPopup(me);
      }

      public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        maybeShowPopup(e);
      }

      private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popup.show(downloadTable, e.getX(), e.getY());
        }
      }
    });

    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(downloadTable);
    topPanel.add(aScrollPane, constraints);

    bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);
    JPanel tempPanel1 = new JPanel();
    tempPanel1.setLayout(new FlowLayout());
    JLabel blau = new JLabel("     ");
    blau.setOpaque(true);
    blau.setBackground(Color.blue);
    tempPanel1.add(blau);
    tempPanel1.add(label4);

    JLabel red = new JLabel("     ");
    red.setOpaque(true);
    red.setBackground(Color.red);
    tempPanel1.add(red);
    tempPanel1.add(label3);

    JLabel black = new JLabel("     ");
    black.setOpaque(true);
    black.setBackground(Color.black);
    tempPanel1.add(black);
    tempPanel1.add(label2);

    JLabel green = new JLabel("     ");
    green.setOpaque(true);
    green.setBackground(Color.green);
    tempPanel1.add(green);
    tempPanel1.add(label1);

    JPanel tempPanel2 = new JPanel();
    tempPanel2.setLayout(new BorderLayout());
    tempPanel2.add(tempPanel1, BorderLayout.NORTH);
    tempPanel2.add(actualDlOverviewTable, BorderLayout.CENTER);
    bottomPanel.add(tempPanel2, BorderLayout.CENTER);

    add(topPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
    DataManager.getInstance().addDataUpdateListener(this, DataUpdateListener.DOWNLOAD_CHANGED);
  }

  public Object[] getSelectedDownloadItems(){
/*      int[] indizes = downloadTable.getSelectedRows();
      ((DownloadModel)downloadTable.getModel()).get*/
      return null;
  }

  public void registerSelected() {
    //    nix zu tun
      //test
/*      Version version = new Version("0.01.34", Version.LINUX);
      Version version2 = new Version("0.01.36", Version.WIN32);
      DownloadDO download = new DownloadDO("12", "24", "kjhh", "13138657", DownloadDO.SUCHEN_LADEN, "test1.rar", "filme", 33);
      DownloadSourceDO source = new DownloadSourceDO("13", DownloadSourceDO.UEBERTRAGUNG, DownloadSourceDO.INDIREKTE_VERBINDUNG,
              new Integer(100000), new Integer(747520), new Integer(340756), new Integer(738), version, 4, 33, "test1.rar", "nickname");
      download.addOrAlterSource(source);
      DownloadDO download2 = new DownloadDO("16", "14", "kjhh", "44223423406", DownloadDO.SUCHEN_LADEN, "test2.rar", "", 490);
      DownloadSourceDO source2 = new DownloadSourceDO("18", DownloadSourceDO.UEBERTRAGUNG, DownloadSourceDO.DIREKTE_VERBINDUNG,
              new Integer(100), new Integer(300), new Integer(230), new Integer(44206), version, 4, 490, "test2.rar", "maj0r");
      DownloadSourceDO source3 = new DownloadSourceDO("17", DownloadSourceDO.UNGEFRAGT, DownloadSourceDO.UNBEKANNT,
              new Integer(430), new Integer(670), new Integer(430), new Integer(0), version2, 4, 32, "test2.rar", "seppel");
      DownloadDO download3 = new DownloadDO("5", "11", "kjhh", "34523423406", DownloadDO.FERTIGSTELLEN, "fertigeDatei.rar", "", 0);
      download2.addOrAlterSource(source2);
      download2.addOrAlterSource(source3);
      DownloadNode node3 = new DownloadNode(download3);
      DownloadNode node = new DownloadNode(download);
      DownloadNode node2 = new DownloadNode(download2);
      downloadTable.updateUI();  */
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    String text = languageSelector.getFirstAttrbuteByTagName(new String[] {
        "mainform", "Label14", "caption"});
    linkLabel.setText(ZeichenErsetzer.korrigiereUmlaute(text));
    btnStartDownload.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "downlajfsp",
                                  "caption"})));
    btnStartDownload.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "downlajfsp", "hint"})));
    String[] tableColumns = new String[10];
    tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col0caption"}));
    tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col1caption"}));
    tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col2caption"}));
    tableColumns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col3caption"}));
    tableColumns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col4caption"}));
    tableColumns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col5caption"}));
    tableColumns[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col6caption"}));
    tableColumns[7] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col7caption"}));
    tableColumns[8] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col8caption"}));
    tableColumns[9] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "queue",
                                  "col9caption"}));

    TableColumnModel tcm = downloadTable.getColumnModel();
    for (int i = 0; i < tcm.getColumnCount(); i++) {
      tcm.getColumn(i).setHeaderValue(tableColumns[i]);
    }

    label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label4", "caption"})));
    label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label3", "caption"})));
    label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label2", "caption"})));
    label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label1", "caption"})));

    item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "canceldown",
                                  "caption"})));
    String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "pausedown",
                                  "caption"}));
    temp += "/" +
        ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                          getFirstAttrbuteByTagName(new String[] {
        "mainform", "resumedown", "caption"}));
    item2.setText(temp);
    item3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "powerdownload",
                                  "caption"})));
    item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "renamefile",
                                  "caption"})));
    item5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "changetarget",
                                  "caption"})));
    item6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform",
                                  "Clearfinishedentries1", "caption"})));
  }

    public void fireContentChanged(int type, Object content) {
        if (type==DataUpdateListener.DOWNLOAD_CHANGED){
            HashMap downloads = (HashMap) content;
            if (downloads!=null && downloads.size()!=0){
                Iterator it = downloads.values().iterator();
                DownloadDO downloadDO = null;
                DownloadNode node = null;
                while (it.hasNext()){
                    downloadDO = (DownloadDO)it.next();
                    node = new DownloadNode(downloadDO);

                }
                downloadTable.updateUI();
            }
        }
    }
}