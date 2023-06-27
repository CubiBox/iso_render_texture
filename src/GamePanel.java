import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    public static int scale;
    public static int width, height;
    public static double xAngle = 0;
    public static double yAngle = 0;

    public final JFrame window = new JFrame();
    private final Thread renderThread = new Thread(this, "RENDER_THREAD");

    public GamePanel(String title, int width, int height) {
        window.setTitle(title);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel.height = height;
        GamePanel.width = width;
        scale = width / height * 4;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void start() {
        this.renderThread.start();
    }

    @Override
    public void run() {
        while (renderThread.isAlive()) {
            repaint();

            try {
                Thread.sleep(6);
                xAngle = (xAngle+0.002f) % (Math.PI*2);
                yAngle = (yAngle+0.001f) % 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        /*
        ArrayList<Texture> textures = new ArrayList<>(List.of(
                new Texture("temp/bottom.png"),
                new Texture("temp/west.png"),
                new Texture("temp/east.png"),
                new Texture("temp/south.png"),
                new Texture("temp/north.png"),
                new Texture("temp/top.png")
        ));
        */

        ArrayList<Texture> textures = new ArrayList<>(List.of(
                new Texture("dirt.png"),
                new Texture("dirt.png"),
                new Texture("dirt.png"),
                new Texture("dirt.png"),
                //new Texture("dirt.png"),
                new Texture("dirt.png")
        ));

        int face = 4;
        int index = 0;
        for (Texture texture : textures) {
            BufferedImage curr_text = texture.convertImage(xAngle, yAngle, face--);
            if (curr_text != null) {
                g2.drawImage(curr_text, null, 0, 0);
                System.out.println(++index);
            }
        }

        graphics.dispose();
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
