package de.applejuicenet.client.gui.tables.upload;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.Icon;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.UploadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/upload/Attic/MainNode.java,v 1.9 2004/05/28 15:32:08 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class MainNode
    implements Node, LanguageListener {
    public static final int ROOT_NODE = -1;
    public static final int LOADING_UPLOADS = 0;
    public static final int WAITING_UPLOADS = 1;
    public static final int REST_UPLOADS = 2;
    private static Map uploads = null;

    private String text;

    private int type;

    private MainNode[] children;

    public MainNode() {
        type = ROOT_NODE;
        children = new MainNode[3];
        children[0] = new MainNode(LOADING_UPLOADS);
        children[1] = new MainNode(WAITING_UPLOADS);
        children[2] = new MainNode(REST_UPLOADS);
    }

    public MainNode(int type) {
        super();
        this.type = type;
        if (type == LOADING_UPLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        else if (type == WAITING_UPLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        else if (type == REST_UPLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
        }
    }

    public static void setUploads(Map uploadMap){
        uploads = uploadMap;
    }

    public Icon getConvenientIcon() {
        if (type == LOADING_UPLOADS) {
            return IconManager.getInstance().getIcon("upload");
        }
        else if (type == WAITING_UPLOADS) {
            return IconManager.getInstance().getIcon("cool");
        }
        else if (type == REST_UPLOADS) {
            return IconManager.getInstance().getIcon("eek");
        }
        else {
            return null;
        }
    }

    public String toString() {
        return text;
    }

    public int getType() {
        return type;
    }

    public int getChildCount() {
        Object[] object = getChildren();
        if (object == null) {
            return 0;
        }
        else {
            return object.length;
        }
    }

    public UploadDO[] getChildrenByStatus(int statusToCheck, boolean active){
        if (uploads == null) {
            return null;
        }
        else {
            ArrayList children = new ArrayList();
            UploadDO[] uploadsForThread = (UploadDO[]) uploads.values().
                toArray(new UploadDO[uploads.size()]);
            for (int i = 0; i < uploadsForThread.length; i++) {
                if (uploadsForThread[i].getStatus() ==
                    statusToCheck) {
                    if (active){
                        if (uploadsForThread[i].getDirectState() == UploadDO.STATE_DIREKT_VERBUNDEN){
                            children.add(uploadsForThread[i]);
                        }
                    }
                    else{
                        if (uploadsForThread[i].getDirectState() != UploadDO.STATE_DIREKT_VERBUNDEN){
                            children.add(uploadsForThread[i]);
                        }
                    }
                }
            }
            return (UploadDO[]) children.toArray(new UploadDO[children.
                size()]);
        }
    }

    public Object[] getChildren() {
        if (type == ROOT_NODE) {
            return children;
        }
        else{
            if (getType() == MainNode.LOADING_UPLOADS) {
                return getChildrenByStatus(UploadDO.AKTIVE_UEBERTRAGUNG, true);
            }
            else if (getType() == MainNode.WAITING_UPLOADS) {
                return getChildrenByStatus(UploadDO.WARTESCHLANGE, true);
            }
            else if (getType() == MainNode.REST_UPLOADS) {
                return getChildrenByStatus(UploadDO.WARTESCHLANGE, false);
            }
            else{
                return null;
            }
        }
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        if (type == LOADING_UPLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.uploadform.ladendeuploads"));
        }
        else if (type == WAITING_UPLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.uploadform.wartendeuploads"));
        }
        else if (type == REST_UPLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.uploadform.dreckigerrest"));
        }
    }
}
