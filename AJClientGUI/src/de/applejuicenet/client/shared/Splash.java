package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Splash.java,v 1.8 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
 *
 */

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;

public class Splash
    extends JDialog {
    private static final long serialVersionUID = -919746311487300858L;
	private Image image;
    private JProgressBar progress;
    private JLayeredPane panel;

    public Splash(Frame parent, Image image, int progressMin, int progressMax) {
        super(parent);
        this.image = image;
        progress = new JProgressBar(progressMin, progressMax);
        progress.setStringPainted(true);
        init();
    }

    public void setProgress(int position, String text){
        if (position >= progress.getMinimum()
            && position <= progress.getMaximum() ){
            progress.setValue(position);
            progress.setString(text);
        }
    }

    private void init(){
        setUndecorated(true);
        int w = image.getWidth(this);
        int h = image.getHeight(this);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Image back = null;
        setBounds( (d.width - w) / 2, (d.height - h) / 3, w, h);
        try {
            back = new Robot().createScreenCapture(getBounds());
        }
        catch (AWTException e) {
            ;
        }
        Graphics g = back.getGraphics();
        g.drawImage(image, 0, 0, this);
        JLabel label = new JLabel(new ImageIcon(back));
        label.setBounds(0, 0, w, h);
        progress.setBounds(160, 61, 180, 15);
        JLayeredPane panel = new JLayeredPane();
        panel.add(progress, JLayeredPane.DEFAULT_LAYER);
        panel.add(label, JLayeredPane.DEFAULT_LAYER);
        getContentPane().add(panel);
    }

    public void dispose() {
        super.dispose();
        image = null;
    }
}