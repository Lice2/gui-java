package de.applejuicenet.client.gui.tablerenderer;

import javax.swing.tree.*;

public interface TreeTableModel
    extends TreeModel {
  public int getColumnCount();

  public String getColumnName(int column);

  public Class getColumnClass(int column);

  public Object getValueAt(Object node, int column);

  public boolean isCellEditable(Object node, int column);

  public void setValueAt(Object aValue, Object node, int column);

  public void fillTree();
}
