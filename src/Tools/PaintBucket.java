package Tools;

import Core.PaintCanvas;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class PaintBucket extends PaintTool {

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();

        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, x, y));
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();

        Point click = _adjustPoint(event.getX(), event.getY());
        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, click.x, click.y));
        }
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return this._draw(canvas, drawColor, x, y, -1, -1);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        // Work with the image used for drawing to color by pixels
        BufferedImage raster = (BufferedImage)canvas.getImage();
        int width = raster.getWidth();
        int height = raster.getHeight();

        // Track where work has been done
        byte[] markedSet = new byte[width * height];

        // FIFO list of points to check
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x1, y1));

        // Only fill where the color matches
        int clickedColor = raster.getRGB(x1, y1);
        int drawColorRGB = drawColor.getRGB();

        while (!queue.isEmpty()) {
            Point center = queue.remove();

            // Check if filling is needed
            if (floodFill(raster, markedSet, clickedColor, drawColorRGB, center.x, center.y, width, height)) {
                // Add above
                queue.add(new Point(center.x, center.y - 1));

                // Add right
                queue.add(new Point(center.x + 1, center.y));

                // Add below
                queue.add(new Point(center.x, center.y + 1));

                // Add left
                queue.add(new Point(center.x - 1, center.y));
            }
        }

        return true;
    }

    private static boolean floodFill(BufferedImage image, byte[] markedSet, int oldColor, int newColor, int x, int y, int width, int height)
    {
        // Make sure we're in the bounds of the image (prevent out-of-bounds markedSet checks)
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }

        // Make sure we didn't already check this pixel
        if (markedSet[width * y + x] == 1) {
            return false;
        }

        // Make sure we're only working within our (sorta) source color
       // double colorDist = colorDistance(image.getRGB(x, y), oldColor);
        if (image.getRGB(x, y) != oldColor) {
           // System.out.println("Distance: " + colorDist);
            return false;
        }

        // Paint the pixel and mark it as painted
        image.setRGB(x, y, newColor);
        markedSet[width * y + x] = 1;

        return true;
    }

    // TODO: Figure this out?
    private static double colorDistance(int colorA, int colorB) {
        float mean = (colorA & 0xFF) / 2;

        int red = ((colorA) & 0xFF) - ((colorB) & 0xFF);
        int green = ((colorA >> 8) & 0xFF) - ((colorB >> 8) & 0xFF);
        int blue = ((colorA >> 16) & 0xFF) - ((colorB >> 16) & 0xFF);

        return Math.sqrt(  (int)((512 + mean) * red * red ) >> 8 +
                         + (4 * green * green) +
                         + ((int)((767 - mean) * blue * blue) >> 8));
    }
}
