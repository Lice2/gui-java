package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.dac.PartListDO.Part;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/DownloadPartListPanel.java,v 1.13 2004/02/09 17:46:11 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadPartListPanel.java,v $
 * Revision 1.13  2004/02/09 17:46:11  maj0r
 * Partliste ueberarbeitet.
 *
 * Revision 1.12  2004/02/05 23:11:26  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.11  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.10  2003/12/19 14:26:34  maj0r
 * Neuzeichnen korrigiert.
 *
 * Revision 1.9  2003/12/19 13:35:40  maj0r
 * Bug in der Partliste behoben.
 *
 * Revision 1.8  2003/12/16 09:28:04  maj0r
 * NullPointer behoben.
 *
 * Revision 1.7  2003/10/15 09:12:34  maj0r
 * Beim Deaktivieren der Partliste wird diese nun auch zurueck gesetzt,
 *
 * Revision 1.6  2003/10/04 15:30:26  maj0r
 * Userpartliste hinzugefuegt.
 *
 * Revision 1.5  2003/09/07 12:11:59  maj0r
 * Anzeige korrigiert, da in der aktuellen Core auch Verfuegbarkeitswerte > 10 vorkommen koennen.
 *
 * Revision 1.4  2003/09/04 10:14:44  maj0r
 * NullPointer behoben.
 *
 * Revision 1.3  2003/09/04 09:29:18  maj0r
 * Anpassung an die Namenskonvention.
 *
 * Revision 1.2  2003/09/04 09:27:25  maj0r
 * DownloadPartListe fertiggestellt.
 *
 *
 */

public class DownloadPartListPanel
    extends JPanel {
    private PartListDO partListDO;
    private Logger logger;
    private BufferedImage image = null;
    private int width;
    private int height;
    private long fertigSeit = -1;

    public DownloadPartListPanel() {
        super(new BorderLayout());
        logger = Logger.getLogger(getClass());
    }

    public void paintComponent(Graphics g) {
        if (partListDO != null && image != null) {
            if (height != (int) getVisibleRect().getHeight() ||
                width != (int) getVisibleRect().getWidth()) {
                setPartList(partListDO);
            }
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, width, height);
            g.drawImage(image, 0, 0, null);
        }
        else {
            super.paintComponent(g);
        }
    }

    public void setPartList(PartListDO partListDO) {
        try {
            this.partListDO = partListDO;
            height = (int) getVisibleRect().getHeight();
            width = (int) getVisibleRect().getWidth();
            if (partListDO != null) {
                int zeilenHoehe = 15;
                int zeilen = height / zeilenHoehe;
                int pixelSize = (int) (partListDO.getGroesse() / (zeilen * width) );
                BufferedImage tempImage = new BufferedImage(width * zeilen, 15,
                                          BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = tempImage.getGraphics();
                int obenLinks = 0;
                int obenRechts = 0;
                Part[] parts = partListDO.getParts();
                for (int i=0; i<parts.length-1; i++){
                    obenRechts = obenLinks + (int)((parts[i+1].getFromPosition() - parts[i].getFromPosition()) / pixelSize);
                    if (partListDO.getPartListType()==PartListDO.MAIN_PARTLIST){
                        if (isGeprueft(partListDO, i)){
                            graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
                        }
                        else{
                            graphics.setColor(getColorByType(parts[i].getType()));
                        }
                    }
                    else{
                        graphics.setColor(getColorByType(parts[i].getType()));
                    }
                    graphics.fillRect(obenLinks, 0, obenRechts, zeilenHoehe);
                    obenLinks = obenRechts + 1;
                }
                obenRechts = obenLinks + (int)((partListDO.getGroesse() - parts[parts.length-1].getFromPosition()) / pixelSize);
                if (isGeprueft(partListDO, parts.length-1)){
                    graphics.setColor(PartListDO.COLOR_TYPE_UEBERPRUEFT);
                }
                else{
                    graphics.setColor(getColorByType(parts[parts.length-1].getType()));
                }
                fertigSeit = -1;
                graphics.fillRect(obenLinks, 0, obenRechts, zeilenHoehe);
                image = new BufferedImage(width, height,
                                          BufferedImage.TYPE_INT_ARGB);
                Graphics g = image.getGraphics();
                g.setColor(Color.WHITE);
                g.drawRect(0, 0, width, height);
                int x = 0;
                for (int i=0; i<zeilen; i++){
                    g.drawImage(tempImage.getSubimage(x, 0, width, zeilenHoehe), 0, i*zeilenHoehe, null);
                    x += width;
                }
            }
            updateUI();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private boolean isGeprueft(PartListDO partListDO, int index){
        if (partListDO.getPartListType()==PartListDO.MAIN_PARTLIST){
            Part[] parts = partListDO.getParts();
            if (parts[index].getType() == -1) {
                if (fertigSeit == -1) {
                    fertigSeit = parts[index].getFromPosition();
                }
                if ( (index < parts.length-1) && (parts[index + 1].getFromPosition() - fertigSeit) >= 1048576) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                fertigSeit = -1;
                return false;
            }
        }
        else{
            return false;
        }
    }

    private Color getColorByType(int type) {
        switch (type) {
            case -1:
                return PartListDO.COLOR_TYPE_OK;
            case 0:
                return PartListDO.COLOR_TYPE_0;
            case 1:
                return PartListDO.COLOR_TYPE_1;
            case 2:
                return PartListDO.COLOR_TYPE_2;
            case 3:
                return PartListDO.COLOR_TYPE_3;
            case 4:
                return PartListDO.COLOR_TYPE_4;
            case 5:
                return PartListDO.COLOR_TYPE_5;
            case 6:
                return PartListDO.COLOR_TYPE_6;
            case 7:
                return PartListDO.COLOR_TYPE_7;
            case 8:
                return PartListDO.COLOR_TYPE_8;
            case 9:
                return PartListDO.COLOR_TYPE_9;
            case 10:
                return PartListDO.COLOR_TYPE_10;
            default:
                return PartListDO.COLOR_TYPE_10;
        }
    }
}
