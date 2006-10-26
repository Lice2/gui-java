package de.applejuicenet.client.gui.download.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.tree.TreeModel;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.download.table.DownloadMainNode.MainNodeType;
import de.applejuicenet.client.shared.Settings;

public class DownloadTreeTableCellRenderer extends DefaultTreeTableCellRenderer implements DataUpdateListener
{
   private Settings settings;

   public DownloadTreeTableCellRenderer(TreeModel model)
   {
      super(model);
      settings = Settings.getSettings();
      OptionsManagerImpl.getInstance().addSettingsListener(this);
   }

   /**
    * Ueberschreiben, um den IconNodeRenderer anzupassen
    */
   protected DefaultIconNodeRenderer getIconNodeRenderer()
   {
      return new DownloadIconNodeRenderer(treeTable);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Object node = ((TreeTableModelAdapter) table.getModel()).nodeForRow(row);

      if(isSelected)
      {
         setBackground(table.getSelectionBackground());
      }
      else
      {
         if(settings.isFarbenAktiv())
         {
            if(node instanceof DownloadSource)
            {
               setBackground(settings.getQuelleHintergrundColor());
            }
            else if(node.getClass() == DownloadMainNode.class && ((DownloadMainNode) node).getType() == MainNodeType.ROOT_NODE &&
                       ((DownloadMainNode) node).getDownload().getStatus() == Download.FERTIG)
            {
               setBackground(settings.getDownloadFertigHintergrundColor());
            }
            else
            {
               setBackground(table.getBackground());
            }
         }
         else
         {
            setBackground(table.getBackground());
         }
      }

      visibleRow = row;
      return this;
   }

   public void fireContentChanged(DATALISTENER_TYPE type, Object content)
   {
      if(type == DATALISTENER_TYPE.SETTINGS_CHANGED)
      {
         settings = (Settings) content;
      }
   }
}
