import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

public class Qwe extends JFrame {

    public static void main(String[] args) {
        final Qwe qwe = new Qwe();

        qwe.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() == ICONIFIED) {
                    try {
                        final TrayIcon trayIcon = new TrayIcon(new ImageIcon("xx.png").getImage());
                        trayIcon.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                qwe.setVisible(true);
                                SystemTray.getSystemTray().remove(trayIcon);
                            }
                        });
                        SystemTray.getSystemTray().add(trayIcon);
                        System.out.println("Aa");
//                        qwe.setVisible(false);
                    } catch (AWTException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        qwe.setSize(200,200);
        qwe.setVisible(true);
    }

}