import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    private int width, height;
    private BufferedImage texture;

    public static final int BOTTOM = 5;
    public static final int WEST = 4;
    public static final int EAST = 3;
    public static final int SOUTH = 2;
    public static final int NORTH = 1;
    public static final int TOP = 0;



    public Texture(String path) {
        try {
            this.texture = upscale(ImageIO.read(new File(path)), 10);
        }
        catch (IOException ignored) { }
        this.height = this.texture.getHeight();
        this.width = this.texture.getWidth();
    }

    public BufferedImage convertImage(double xAngle, double yAngle, int type){
        BufferedImage texture = new BufferedImage(width*2, height*2, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = texture.createGraphics();

        double ref_angle = Math.PI/4;
        switch (type){
            case NORTH -> {
                if (xAngle >= ref_angle && xAngle <= 5*ref_angle) return null;
            }
            case SOUTH -> {
                if (xAngle >= 5*ref_angle || xAngle <= ref_angle) return null;
            }
            case WEST -> {
                if (xAngle >= 7*ref_angle || xAngle <= 3*ref_angle) return null;
            }
            case EAST -> {
                if (xAngle >= 3*ref_angle && xAngle <= 7*ref_angle) return null;
            }
        };

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgba = this.texture.getRGB(x, y);
                double[] coos = switch (type){
                    case TOP -> toScreenIso(x, y, height, xAngle, yAngle);
                    case BOTTOM -> toScreenIso(x, y, 0, xAngle, yAngle);
                    case NORTH -> toScreenIso(y, 0, x, xAngle, yAngle);
                    case SOUTH -> toScreenIso(y, height, x, xAngle, yAngle);
                    case EAST -> toScreenIso(0, x, y, xAngle, yAngle);
                    case WEST -> toScreenIso(height, x, y, xAngle, yAngle);
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                };

                Color color = new Color(rgba);
                g2.setColor(color);
                g2.fillRect((int) (coos[0]), (int) (coos[1]),1,1);
            }
        }
        g2.setColor(Color.RED);
        g2.drawRect(0, 0, texture.getWidth(), texture.getHeight());
        g2.dispose();

        return texture;
    }


    public double[] toScreenIso(double x, double y, double z, double xAngle, double yAngle){
        // 8 = SIZE / 2
        double relativeX = (width / 2d) - x;
        double relativeY = (height / 2d) - y;

        double newAngle = xAngle + ((relativeX == 0d && relativeY == 0d) ? 0d : Math.atan(relativeY / relativeX));
        double originDistance = Math.sqrt(relativeX * relativeX + relativeY * relativeY);

        double finalX = originDistance * Math.cos(newAngle) * (relativeX < 0d ? -1d : 1d) / 2d;
        double finalY = originDistance * Math.sin(newAngle) * (relativeX < 0d ? -1d : 1d) / 2d;

        double heightOffset = height - (Math.sin(Math.PI / 2d - (yAngle * (Math.PI / 2d)))) * z;
        return new double[] {
                height / 2d + (finalX - finalY),
                heightOffset + (yAngle * (finalY + finalX))
        };
    }

    public BufferedImage upscale(BufferedImage image, double scale) {
        int height = (int) (image.getWidth()*scale);
        int width = (int) (image.getHeight()*scale);
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = output.createGraphics();

        for (int y = 0; y < image.getHeight(); y++){
            for (int x = 0; x < image.getWidth(); x++) {
                int rgba = image.getRGB(x, y);

                Color color = new Color(rgba);
                g2.setColor(color);
                g2.fillRect(
                        (int) (x * scale),
                        (int) (y * scale),
                        (int) (1 * scale),
                        (int) (1 * scale)
                );
            }
        }

        g2.dispose();
        return output;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getTexture() {
        return texture;
    }
}
