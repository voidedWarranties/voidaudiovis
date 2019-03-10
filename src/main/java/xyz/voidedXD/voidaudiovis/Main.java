package xyz.voidedXD.voidaudiovis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

public class Main extends JPanel {

    private Point initialClick;
    private JFrame parent;

    public Main(JFrame parent) {
        init();
        this.parent = parent;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame parent = new JFrame("voidaudiovis");
            Main main = new Main(parent);

            parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            parent.setSize(512, 480);
            parent.setLayout(new BorderLayout());
            parent.setUndecorated(true);
            parent.setBackground(new Color(0, 0, 0, 0));

            parent.add(main);
            parent.setVisible(true);
        });
    }

    private void init() {
        FileDialog fd = new FileDialog(parent, "Choose a file", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setFile("*.mp3");
        fd.setVisible(true);
        Player player = new Player(new File(fd.getFile()));
        setLayout(new BorderLayout());
        setSize(512, 480);
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);
        add(player);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                // get location of Window
                int thisX = parent.getLocation().x;
                int thisY = parent.getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                parent.setLocation(X, Y);
            }
        });
    }
}
