package Tools;

import Core.ColorBar;
import Core.PaintCanvas;
import Core.Toolbar;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public class ColorPicker extends PaintTool {

    private Toolbar _toolbar;
    public ColorPicker(Toolbar toolbar) {
        this._toolbar = toolbar;
    }

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        BufferedImage raster = (BufferedImage)source.getImage();
        Point click = _adjustPoint(event.getX(), event.getY());

        Color clickedColor = new Color(raster.getRGB(click.x, click.y));

        if (this._toolbar != null) {
            this._toolbar.FillBox(clickedColor);
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        this._toolbar.FillBox(Color.LIGHT_GRAY);

        BufferedImage raster = (BufferedImage)source.getImage();
        Point click = _adjustPoint(event.getX(), event.getY());
        Color clickedColor = new Color(raster.getRGB(click.x, click.y));

        if (SwingUtilities.isLeftMouseButton(event)) {
            source.setColor(0, clickedColor);
            ColorBar.RepaintNewColors(clickedColor, 0);
        } else if (SwingUtilities.isRightMouseButton(event)) {
            source.setColor(1, clickedColor);
            ColorBar.RepaintNewColors(clickedColor, 1);
        }
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {

    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        BufferedImage raster = (BufferedImage)source.getImage();
        Point click = _adjustPoint(event.getX(), event.getY());

        Color clickedColor = new Color(raster.getRGB(click.x, click.y));

        if (this._toolbar != null) {
            this._toolbar.FillBox(clickedColor);
        }
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return false;
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        return false;
    }
}
