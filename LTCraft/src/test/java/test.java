import javax.swing.*;
import java.awt.*;

public class test {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("");
        jFrame.setSize(new Dimension(2500, 1400));
        int[] yaws = {45, 145, 250, 360};
        double X;
        double Z;
        int radius = 500;
        for (int i = 0; i < 3; i++) {
            float leftYaw = 90;
            float rightYaw = 180 ;
            System.out.println(leftYaw);
            System.out.println(rightYaw);
            if (i < 1){
                X = -Math.sin(leftYaw / 180 * Math.PI) * radius;//计算X
                Z = Math.cos(leftYaw / 180 * Math.PI) * radius;//计算Z
            }else if(i > 1){
                X = -Math.sin(rightYaw / 180 * Math.PI) * radius;//计算X
                Z = Math.cos(rightYaw / 180 * Math.PI) * radius;//计算Z
            }else{
                X = 0;
                Z = 0;
            }
            JLabel jLabel = new JLabel(i + "");
            jLabel.setBackground(Color.BLACK);
            jLabel.setSize(new Dimension(100, 100));
            jLabel.setLocation(((int)Math.round(X) + jFrame.getWidth() / 2), ((int)Math.round(Z) + jFrame.getHeight() / 2));
            jFrame.add(jLabel);
        }
        jFrame.setVisible(true);
    }
}