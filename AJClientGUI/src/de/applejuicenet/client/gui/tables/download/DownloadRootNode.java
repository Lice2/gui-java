package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.tables.Node;

import javax.swing.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadRootNode.java,v 1.6 2003/10/10 15:12:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadRootNode.java,v $
 * Revision 1.6  2003/10/10 15:12:26  maj0r
 * Sortieren im Downloadbereich eingefuegt.
 *
 * Revision 1.5  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
 * Revision 1.4  2003/10/04 15:31:07  maj0r
 * Erste Version des Versteckens.
 *
 * Revision 1.3  2003/10/02 15:01:00  maj0r
 * Erste Version den Versteckens eingebaut.
 *
 * Revision 1.2  2003/09/02 19:29:26  maj0r
 * Einige Stellen synchronisiert und Nullpointer behoben.
 * Version 0.21 beta.
 *
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public class DownloadRootNode implements Node, DownloadNode {
    public static int SORT_NO_SORT = -1;
    public static int SORT_DOWNLOADNAME = 0;
    public static int SORT_GROESSE = 1;
    public static int SORT_BEREITS_GELADEN = 2;
    public static int SORT_RESTZEIT = 3;
    public static int SORT_PROZENT = 4;
    public static int SORT_PWDL = 5;
    public static int SORT_REST_ZU_LADEN = 6;
    public static int SORT_GESCHWINDIGKEIT = 7;

    private HashMap downloads;

    private HashMap childrenPath = new HashMap();
    private ArrayList children = new ArrayList();
    private HashMap versteckteNodes = new HashMap();
    private boolean versteckt = false;

    private int sort = SORT_NO_SORT;
    private boolean isAscent = true;

    public void alterVerstecke(DownloadMainNode downloadMainNode){
        if (downloadMainNode.getType()==DownloadMainNode.ROOT_NODE){
            MapSetStringKey key = new MapSetStringKey(downloadMainNode.getDownloadDO().getId());
            if (!versteckteNodes.containsKey(key)){
                versteckteNodes.put(key, downloadMainNode);
            }
            else{
                Object node = versteckteNodes.get(key);
                versteckteNodes.remove(key);
                if (!children.contains(node)){
                    children.add(node);
                }
            }
        }
    }

    public void enableVerstecke(boolean verstecke){
        versteckt = verstecke;
        if (!versteckt){
            Iterator it = versteckteNodes.values().iterator();
            Object node = null;
            while (it.hasNext()){
                node = it.next();
                if (!children.contains(node)){
                    children.add(node);
                }
            }
        }
    }

    public boolean isVerstecktEnabled(){
        return versteckt;
    }

    public Object[] getChildren(){
        if (downloads==null)
            return null;
        DownloadDO downloadDO;
        HashMap targetDirs = new HashMap();
        MapSetStringKey key;
        DownloadDirectoryNode newNode;
        synchronized(this){
            int childCount = children.size();   //alte Downloads entfernen
            Object obj;
            for (int i=childCount-1; i>=0; i--){
                obj = children.get(i);
                if (obj.getClass()==DownloadMainNode.class){
                    key = new MapSetStringKey(((DownloadMainNode)obj).getDownloadDO().getId());
                    if (!downloads.containsKey(key) || (versteckt && versteckteNodes.containsKey(key))){
                        children.remove(i);
                    }
                }
            }
            Iterator it = downloads.values().iterator();
            String pfad;
            PathEntry pathEntry;
            MapSetStringKey mapKey;
            while (it.hasNext()){
                downloadDO = (DownloadDO)it.next();
                mapKey = new MapSetStringKey(downloadDO.getId());
                if (versteckt && versteckteNodes.containsKey(mapKey)){
                    continue;
                }
                pfad = downloadDO.getTargetDirectory();
                pathEntry = (PathEntry)childrenPath.get(mapKey);
                if (pathEntry!=null){
                    if (pathEntry.getPfad().compareToIgnoreCase(pfad)!=0){ //geaenderter Download
                        children.remove(pathEntry.getObj());
                        childrenPath.remove(mapKey);
                    }
                    else{
                        continue;
                    }
                }
                if (pathEntry==null ){ //neuer Download
                    if (pfad==null || pfad.length()==0){
                        childrenPath.put(mapKey, new PathEntry("", downloadDO));
                        children.add(new DownloadMainNode(downloadDO));
                    }
                    else{
                        key = new MapSetStringKey(downloadDO.getTargetDirectory());
                        if (!targetDirs.containsKey(key)){
                            childrenPath.put(mapKey, new PathEntry(downloadDO.getTargetDirectory(), downloadDO));
                            newNode = new DownloadDirectoryNode(downloadDO.getTargetDirectory());
                            targetDirs.put(key, newNode);
                            children.add(newNode);
                        }
                    }
                }
            }
        }
        return sort((DownloadMainNode[])children.toArray(new DownloadMainNode[children.size()]));
    }

    public void setSortCriteria(int sortCriteria, boolean isAscent){
        sort = sortCriteria;
        this.isAscent = isAscent;
    }

    private Object[] sort(DownloadMainNode[] childNodes){
        if (sort==SORT_NO_SORT){
            return childNodes;
        }
        else{
            int n = childNodes.length;
            DownloadMainNode tmp;
            for (int i = 0; i < n - 1; i++) {
              int k = i;
              for (int j = i + 1; j < n; j++) {
                if (isAscent) {
                  if (compare(childNodes, j, k) < 0) {
                    k = j;
                  }
                }
                else {
                  if (compare(childNodes, j, k) > 0) {
                    k = j;
                  }
                }
              }
              tmp = childNodes[i];
              childNodes[i] = childNodes[k];
              childNodes[k] = tmp;
            }
            return childNodes;
        }
    }

    private int compare(DownloadMainNode[] childNodes, int row1, int row2) {
      Object o1 = null;
      Object o2 = null;
        if (sort==SORT_DOWNLOADNAME){
            o1 = childNodes[row1].getDownloadDO().getFilename();
            o2 = childNodes[row2].getDownloadDO().getFilename();
        }
        else if (sort==SORT_GROESSE){
            o1 = new Long(childNodes[row1].getDownloadDO().getGroesse());
            o2 = new Long(childNodes[row2].getDownloadDO().getGroesse());
        }
        else if (sort==SORT_BEREITS_GELADEN){
            o1 = new Long(childNodes[row1].getDownloadDO().getBereitsGeladen());
            o2 = new Long(childNodes[row2].getDownloadDO().getBereitsGeladen());
        }
        else if (sort==SORT_RESTZEIT){
            o1 = new Long(childNodes[row1].getDownloadDO().getRestZeit());
            o2 = new Long(childNodes[row2].getDownloadDO().getRestZeit());
        }
        else if (sort==SORT_PROZENT){
            o1 = new Double(childNodes[row1].getDownloadDO().getProzentGeladen());
            o2 = new Double(childNodes[row2].getDownloadDO().getProzentGeladen());
        }
        else if (sort==SORT_PWDL){
            o1 = new Integer(childNodes[row1].getDownloadDO().getPowerDownload());
            o2 = new Integer(childNodes[row2].getDownloadDO().getPowerDownload());
        }
        else if (sort==SORT_REST_ZU_LADEN){
            o1 = new Long(childNodes[row1].getDownloadDO().getGroesse()-childNodes[row1].getDownloadDO().getBereitsGeladen());
            o2 = new Long(childNodes[row2].getDownloadDO().getGroesse()-childNodes[row2].getDownloadDO().getBereitsGeladen());
        }
        else if (sort==SORT_GESCHWINDIGKEIT){
            o1 = new Long(childNodes[row1].getDownloadDO().getSpeedInBytes());
            o2 = new Long(childNodes[row2].getDownloadDO().getSpeedInBytes());
        }

      if (o1 == null && o2 == null) {
        return 0;
      }
      else if (o1 == null) {
        return -1;
      }
      else if (o2 == null) {
        return 1;
      }
      else {
          if (o1.getClass().getSuperclass() == Number.class) {
            return compare( (Number) o1, (Number) o2);
          }
          else if (o1.getClass() == String.class) {
            return ( (String) o1).compareTo( (String) o2);
          }
          else if (o1.getClass() == Boolean.class) {
            return compare( (Boolean) o1, (Boolean) o2);
          }
          else {
            return ( (String) o1).compareToIgnoreCase( (String) o2);
          }
      }
    }

  public int compare(Number o1, Number o2) {
    double n1 = o1.doubleValue();
    double n2 = o2.doubleValue();
    if (n1 < n2) {
      return -1;
    }
    else if (n1 > n2) {
      return 1;
    }
    else {
      return 0;
    }
  }

  public int compare(Boolean o1, Boolean o2) {
    boolean b1 = o1.booleanValue();
    boolean b2 = o2.booleanValue();
    if (b1 == b2) {
      return 0;
    }
    else if (b1) {
      return 1;
    }
    else {
      return -1;
    }
  }

    public void setDownloadMap(HashMap downloadMap){
        if (downloads==null){
            downloads = downloadMap;
        }
    }

    public int getChildCount(){
        Object[] obj = getChildren();
        if (obj==null)
            return 0;
        return getChildren().length;
    }

    public boolean isLeaf(){
        return false;
    }

    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("tree");
    }

    class PathEntry{
        private String pfad;
        private Object obj;

        public PathEntry(String pfad, Object obj){
            this.pfad = pfad;
            this.obj = obj;
        }

        public String getPfad() {
            return pfad;
        }

        public Object getObj() {
            return obj;
        }
    }
}
