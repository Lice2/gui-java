package de.applejuicenet.client.gui.shared;

import java.awt.*;
import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/shared/Attic/BlankIcon.java,v 1.1 2003/06/24 14:32:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: BlankIcon.java,v $
 * Revision 1.1  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingef�gt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 *
 *
 */

public class BlankIcon
    implements Icon {
  private Color fillColor;
  private int size;

  public BlankIcon() {
    this(null, 11);
  }

  public BlankIcon(Color color, int size) {
    fillColor = color;
    this.size = size;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (fillColor != null) {
      g.setColor(fillColor);
      g.drawRect(x, y, size - 1, size - 1);
    }
  }

  public int getIconWidth() {
    return size;
  }

  public int getIconHeight() {
    return size;
  }
}