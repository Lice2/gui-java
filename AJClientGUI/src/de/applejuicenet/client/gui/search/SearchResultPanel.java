package de.applejuicenet.client.gui.search;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.shared.FileType;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.search.table.SearchNode;
import de.applejuicenet.client.gui.search.table.SearchNodeComparator;
import de.applejuicenet.client.gui.search.table.SearchResultTableModel;
import de.applejuicenet.client.gui.search.table.SearchResultTreeTableCellRenderer;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.SoundPlayer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/search/SearchResultPanel.java,v 1.13 2006/10/26 13:34:06 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class SearchResultPanel extends JPanel
{
   private static String          offeneSuchen       = "%i offene Suchen";
   private static String          gefundeneDateien   = "%i gefundene Dateien";
   private static String          durchsuchteClients = "%i durchsuchte Clients";
   private static String          linkLaden          = "Link";
   private static String          sucheStoppen       = "Suche stoppen";
   private static String          alreadyLoaded;
   private static String          invalidLink;
   private static String          linkFailure;
   private static String          dialogTitel;
   private static String[]        columns;
   private Logger                 logger;
   private JTreeTable             searchResultTable;
   private SearchResultTableModel tableModel;
   private Search                 search;
   private JButton                sucheAbbrechen     = new JButton();
   private JLabel                 label1             = new JLabel();
   private JLabel                 label2             = new JLabel();
   private JLabel                 label3             = new JLabel();
   private JPopupMenu             popup              = new JPopupMenu();
   private JMenuItem              item1              = new JMenuItem();
   private JToggleButton[]        filterButtons;
   private TableColumn[]          tableColumns       = new TableColumn[3];

   public SearchResultPanel(Search aSearch)
   {
      search = aSearch;
      logger = Logger.getLogger(getClass());
      try
      {
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public Search getSearch()
   {
      return search;
   }

   private void init() throws Exception
   {
      IconManager im = IconManager.getInstance();

      item1.setText(linkLaden);
      item1.setIcon(im.getIcon("download"));
      sucheAbbrechen.setText(sucheStoppen);
      sucheAbbrechen.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               sucheAbbrechen.setEnabled(false);
               try
               {
                  AppleJuiceClient.getAjFassade().cancelSearch(search);
               }
               catch(IllegalArgumentException e)
               {
                  logger.error(e);
               }
            }
         });
      popup.add(item1);
      setLayout(new BorderLayout());
      updateZahlen();
      tableModel = new SearchResultTableModel(search);
      SearchResultTreeTableCellRenderer searchResultTreeTableCellRenderer = new SearchResultTreeTableCellRenderer(tableModel);

      searchResultTable = new JTreeTable(tableModel, searchResultTreeTableCellRenderer);
      searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      JPanel  buttonPanel = new JPanel(new FlowLayout());
      JButton all = new JButton(IconManager.getInstance().getIcon("abbrechen"));

      buttonPanel.add(all);

      FileType[] allTypes = FileType.values();

      filterButtons = new JToggleButton[allTypes.length];
      for(int i = 0; i < allTypes.length; i++)
      {
         filterButtons[i] = new JToggleButton(IconManager.getInstance().getIcon(allTypes[i].toString()));
         filterButtons[i].addActionListener(new FilterAdapter(allTypes[i]));
         buttonPanel.add(filterButtons[i]);
      }

      all.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               for(int i = 0; i < filterButtons.length; i++)
               {
                  filterButtons[i].setSelected(false);
               }

               search.clearFilter();
               ((SearchNode) tableModel.getRoot()).updateFilter();
               searchResultTable.updateUI();
            }
         });

      add(buttonPanel, BorderLayout.NORTH);
      add(new JScrollPane(searchResultTable), BorderLayout.CENTER);
      JPanel southPanel = new JPanel(new BorderLayout());
      JPanel textPanel = new JPanel(new FlowLayout());

      textPanel.add(label1);
      textPanel.add(label2);
      textPanel.add(label3);

      JPanel panel1 = new JPanel(new FlowLayout());

      panel1.add(sucheAbbrechen);
      southPanel.add(panel1, BorderLayout.WEST);
      southPanel.add(textPanel, BorderLayout.CENTER);
      add(southPanel, BorderLayout.SOUTH);
      MouseAdapter popupMouseAdapter = new MouseAdapter()
      {
         public void mousePressed(MouseEvent me)
         {
            if(SwingUtilities.isRightMouseButton(me))
            {
               Point p    = me.getPoint();
               int   iRow = searchResultTable.rowAtPoint(p);
               int   iCol = searchResultTable.columnAtPoint(p);

               if(iRow != -1 && iCol != -1)
               {
                  searchResultTable.setRowSelectionInterval(iRow, iRow);
                  searchResultTable.setColumnSelectionInterval(iCol, iCol);
               }
            }

            maybeShowPopup(me);
         }

         public void mouseReleased(MouseEvent e)
         {
            super.mouseReleased(e);
            maybeShowPopup(e);
         }

         private void maybeShowPopup(MouseEvent e)
         {
            if(e.isPopupTrigger())
            {
               int sel = searchResultTable.getSelectedRow();

               if(sel != -1)
               {
                  Object result = ((TreeTableModelAdapter) searchResultTable.getModel()).nodeForRow(sel);

                  if(result.getClass() == SearchNode.class || result instanceof FileName)
                  {
                     popup.show(searchResultTable, e.getX(), e.getY());
                  }
               }
            }
         }
      };

      searchResultTable.addMouseListener(popupMouseAdapter);
      item1.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               int sel = searchResultTable.getSelectedRow();

               if(sel != -1)
               {
                  Object result = ((TreeTableModelAdapter) searchResultTable.getModel()).nodeForRow(sel);

                  if(result.getClass() == SearchNode.class)
                  {
                     SearchEntry  entry  = (SearchEntry) ((SearchNode) result).getValueObject();
                     StringBuffer toCopy = new StringBuffer();

                     toCopy.append("ajfsp://file|");
                     toCopy.append(searchResultTable.getValueAt(sel, 0) + "|" + entry.getChecksumme() + "|" + entry.getGroesse() +
                                   "/");
                     String link = toCopy.toString();

                     processLink(link);
                  }
                  else if(result instanceof FileName)
                  {
                     String dateiname = ((FileName) result).getDateiName();
                     int    i    = sel;
                     Object temp = null;

                     while(i > 0)
                     {
                        i--;
                        temp = ((TreeTableModelAdapter) searchResultTable.getModel()).nodeForRow(i);
                        if(temp.getClass() == SearchNode.class)
                        {
                           break;
                        }
                     }

                     if(temp != null && temp.getClass() == SearchNode.class)
                     {
                        SearchEntry  entry  = (SearchEntry) ((SearchNode) temp).getValueObject();
                        StringBuffer toCopy = new StringBuffer();

                        toCopy.append("ajfsp://file|");
                        toCopy.append(dateiname + "|" + entry.getChecksumme() + "|" + entry.getGroesse() + "/");
                        String link = toCopy.toString();

                        processLink(link);
                     }
                  }
               }
            }
         });

      TableColumnModel   model    = searchResultTable.getColumnModel();
      SortButtonRenderer renderer = new SortButtonRenderer();

      for(int i = 0; i < tableColumns.length; i++)
      {
         tableColumns[i] = model.getColumn(i);
         tableColumns[i].setHeaderRenderer(renderer);
      }

      JTableHeader header = searchResultTable.getTableHeader();

      header.addMouseListener(new SortMouseAdapter(header, renderer));
      if(!search.isRunning())
      {
         sucheAbbrechen.setEnabled(false);
      }
   }

   private void processLink(final String link)
   {
      new Thread()
         {
            public void run()
            {
               try
               {
                  final String result = AppleJuiceClient.getAjFassade().processLink(link, "");

                  SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
                  if(result.indexOf("ok") != 0)
                  {
                     SwingUtilities.invokeLater(new Runnable()
                        {
                           public void run()
                           {
                              String message = null;

                              if(result.indexOf("already downloaded") != -1)
                              {
                                 message = alreadyLoaded.replaceAll("%s", link);
                              }
                              else if(result.indexOf("incorrect link") != -1)
                              {
                                 message = invalidLink.replaceAll("%s", link);
                              }
                              else if(result.indexOf("failure") != -1)
                              {
                                 message = linkFailure;
                              }

                              if(message != null)
                              {
                                 JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message, dialogTitel,
                                                               JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                              }
                           }
                        });
                  }
               }
               catch(IllegalArgumentException e)
               {
                  logger.error(e);
               }
            }
         }.start();
   }

   public static void setTexte(String[] texte, String[] tableColumns)
   {
      offeneSuchen       = texte[0];
      gefundeneDateien   = texte[1];
      durchsuchteClients = texte[2];
      linkLaden          = texte[3];
      sucheStoppen       = texte[4];
      alreadyLoaded      = texte[5];
      invalidLink        = texte[6];
      linkFailure        = texte[7];
      dialogTitel        = texte[8];
      columns            = tableColumns;
   }

   public void updateSearchContent()
   {
      try
      {
         if(search.isChanged())
         {
            ((SearchNode) tableModel.getRoot()).refresh();
            searchResultTable.updateUI();
            updateZahlen();
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private void updateZahlen()
   {
      SearchEntry[] searchEntries = search.getAllSearchEntries();

      label1.setText(offeneSuchen.replaceFirst("%i", Integer.toString(search.getOffeneSuchen())));
      label2.setText(gefundeneDateien.replaceFirst("%i", Integer.toString(searchEntries.length)));
      label3.setText(durchsuchteClients.replaceFirst("%i", Integer.toString(search.getDurchsuchteClients())));
   }

   public void aendereSprache()
   {
      try
      {
         item1.setText(linkLaden);
         sucheAbbrechen.setText(sucheStoppen);
         label1.setText(offeneSuchen.replaceFirst("%i", Integer.toString(search.getOffeneSuchen())));
         label2.setText(gefundeneDateien.replaceFirst("%i", Long.toString(search.getEntryCount())));
         label3.setText(durchsuchteClients.replaceFirst("%i", Integer.toString(search.getDurchsuchteClients())));
         TableColumnModel tcm = searchResultTable.getColumnModel();

         for(int i = 0; i < tcm.getColumnCount(); i++)
         {
            tcm.getColumn(i).setHeaderValue(columns[i]);
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   class SortMouseAdapter extends MouseAdapter
   {
      private JTableHeader       header;
      private SortButtonRenderer renderer;

      public SortMouseAdapter(JTableHeader header, SortButtonRenderer renderer)
      {
         this.header   = header;
         this.renderer = renderer;
         renderer.setSelectedColumn(0);
         renderer.setSelectedColumn(0);
         header.repaint();
      }

      public void mouseClicked(MouseEvent e)
      {
         if(e.getButton() != MouseEvent.BUTTON1)
         {
            return;
         }

         int col = header.columnAtPoint(e.getPoint());

         if(col == -1)
         {
            return;
         }

         TableColumn pressedColumn = searchResultTable.getColumnModel().getColumn(col);

         renderer.setPressedColumn(col);
         renderer.setSelectedColumn(col);
         header.repaint();

         if(header.getTable().isEditing())
         {
            header.getTable().getCellEditor().stopCellEditing();
         }

         boolean isAscent;

         if(SortButtonRenderer.UP == renderer.getState(col))
         {
            isAscent = true;
         }
         else
         {
            isAscent = false;
         }

         SearchNode rootNode = ((SearchNode) tableModel.getRoot());

         if(pressedColumn == tableColumns[0])
         {
            rootNode.setSortCriteria(SearchNodeComparator.SORT_TYPE.SORT_FILENAME, isAscent);
         }
         else if(pressedColumn == tableColumns[1])
         {
            rootNode.setSortCriteria(SearchNodeComparator.SORT_TYPE.SORT_GROESSE, isAscent);
         }
         else if(pressedColumn == tableColumns[2])
         {
            rootNode.setSortCriteria(SearchNodeComparator.SORT_TYPE.SORT_ANZAHL, isAscent);
         }

         searchResultTable.updateUI();
         renderer.setPressedColumn(-1);
         header.repaint();
      }
   }


   private class FilterAdapter implements ActionListener
   {
      private FileType filter;

      public FilterAdapter(FileType newFilter)
      {
         filter = newFilter;
      }

      public void actionPerformed(ActionEvent ae)
      {
         JToggleButton source = (JToggleButton) ae.getSource();

         if(source.isSelected())
         {
            search.removeFilter(filter);
         }
         else
         {
            search.addFilter(filter);
         }

         ((SearchNode) tableModel.getRoot()).updateFilter();
         searchResultTable.updateUI();
      }
   }
}
