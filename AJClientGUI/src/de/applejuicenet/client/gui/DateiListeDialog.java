package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dnd.DndTargetAdapter;
import de.applejuicenet.client.gui.tables.dateiliste.DateiListeTableModel;
import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;
import java.io.File;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DateiListeDialog.java,v 1.2 2003/08/28 06:11:02 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DateiListeDialog.java,v $
 * Revision 1.2  2003/08/28 06:11:02  maj0r
 * DragNDrop vervollstaendigt.
 *
 * Revision 1.1  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 *
 */

public class DateiListeDialog extends JDialog {
    private JLabel speicherTxt = new JLabel();
    private JLabel speicherHtml = new JLabel();
    private JTable table = new JTable();
    private JLabel text = new JLabel();

    public DateiListeDialog(Frame parent, boolean modal) {
        super(parent, modal);
        init();
    }

    private void init() {
        table.setModel(new DateiListeTableModel());
        getContentPane().setLayout(new GridBagLayout());
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        IconManager im = IconManager.getInstance();
        speicherTxt.setIcon(im.getIcon("speichern"));
        speicherTxt.addMouseListener(new SpeichernMouseAdapter());
        speicherHtml.setIcon(im.getIcon("web"));
        speicherHtml.addMouseListener(new SpeichernMouseAdapter());
        panel1.add(speicherTxt);
        panel1.add(speicherHtml);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        getContentPane().add(panel1, constraints);
        constraints.weightx = 0;
        constraints.gridy = 1;
        getContentPane().add(text, constraints);
        constraints.gridy = 2;
        constraints.weighty = 1;
        JScrollPane scroll = new JScrollPane(table);
        scroll.setDropTarget(new DropTarget(scroll, new DndTargetAdapter() {
            protected Object getTarget( Point point ){
                return this;
             }

            public void drop(DropTargetDropEvent event) {
                Transferable tr = event.getTransferable();
                if (tr.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "ShareNodesTransferer"))){
                    try{
                        event.acceptDrop(DnDConstants.ACTION_COPY);
                        Object[] transfer = (Object[])tr.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "ShareNodesTransferer"));
                        if (transfer != null && transfer.length!=0){
                            ShareNode node = null;
                            DateiListeTableModel model = (DateiListeTableModel)table.getModel();
                            for (int i=0; i<transfer.length; i++){
                                model.addNodes((ShareNode)transfer[i]);
                            }
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        event.getDropTargetContext().dropComplete(false);
                    }
                }
                event.getDropTargetContext().dropComplete(true);
            }
        }));
        getContentPane().add(scroll, constraints);
        constraints.weighty = 0;
        initLanguage();
        pack();
    }

    public void initLanguage() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                 getFirstAttrbuteByTagName(new String[]{"linklist",
                                                                                                        "caption"})));
        text.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                 getFirstAttrbuteByTagName(new String[]{"linklist", "Label1",
                                                                                                        "caption"})));
        String[] tableColumns = new String[2];
        tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"linklist", "files",
                                                                                                   "col0caption"}));
        tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"linklist", "files",
                                                                                                   "col1caption"}));
        TableColumnModel tcm = table.getColumnModel();
        for (int i = 0; i < tableColumns.length; i++)
        {
            tcm.getColumn(i).setHeaderValue(tableColumns[i]);
        }
    }

    class SpeichernMouseAdapter
            extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            if (source==speicherTxt)
                fileChooser.setFileFilter(new TxtFileFilter());
            else
                fileChooser.setFileFilter(new HtmlFileFilter());
            int i = fileChooser.showSaveDialog(DateiListeDialog.this);
            if (i==JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                //todo
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }

    class TxtFileFilter extends FileFilter{
        public boolean accept(File file) {
            if (!file.isFile())
                return true;
            else{
                String name = file.getName();
                if(name.toLowerCase().endsWith(".txt"))
                    return true;
                else
                    return false;
            }
        }

        public String getDescription() {
            return "*.txt";
        }
    }

    class HtmlFileFilter extends FileFilter{
        public boolean accept(File file) {
            if (!file.isFile())
                return true;
            else{
                String name = file.getName();
                if(name.toLowerCase().endsWith(".htm") || name.toLowerCase().endsWith(".html"))
                    return true;
                else
                    return false;
            }
        }

        public String getDescription() {
            return "*.htm | *.html";
        }
    }
}
