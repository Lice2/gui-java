/* Generated by Together */

package de.applejuicenet.client.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.JTableHeader;

/* $id$ */

public class DownloadDataTableModel extends AbstractTableModel {
    final static String[] COL_NAMES =
        {"Dateiname", "Status", "Gr��e", "Bereits geladen", "Prozent geladen", "Noch zu laden", "Geschwindigkeit", "Restliche Zeit", "Powerdownload", "Client"};

    Object[] downloads;

    public DownloadDataTableModel() {
        super();
    }

    public Object getRow(int row) {
        if ((downloads != null) && (row < downloads.length)) {
            return downloads[row];
        }
		return null;
    }

    public Object getValueAt(int row, int column) {
        if ((downloads == null) || (row >= downloads.length)) {
            return "";
        }

        Object download = downloads[row];
        if (download == null) {
            return "";
        }

        String s = new String("");
        switch (column) {
            default:
                s = new String("Fehler");
        }
        if (s == null)
            s = "";
        return s;
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public int getRowCount() {
        if (downloads == null) {
            return 0;
        }
        return downloads.length;
    }

    public Class getClass(int index) {
        return String.class;
    }

    public void setTable(Object[] downloadArray) {
        downloads = downloadArray;
        this.fireTableDataChanged();
    }

    public void clearTable() {
        downloads = null;
        this.fireTableDataChanged();
    }
}
