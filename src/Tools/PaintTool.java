package Tools;

import Core.PaintCanvas;

import Core.ToolSettings;
import java.awt.Point;
import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public abstract class PaintTool {
    public abstract void MousePress(PaintCanvas source, MouseEvent event);
    public abstract void MouseRelease(PaintCanvas source, MouseEvent event);
    public abstract void MouseClick(PaintCanvas source, MouseEvent event);
    public abstract void MouseDrag(PaintCanvas source, MouseEvent event);

    protected abstract boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y);
    protected abstract boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2);

    protected final Point _adjustPoint(int x, int y) {
        int zoom = ToolSettings.getZoomLevel();

        if (zoom == 1) {
            return new Point(x - 4, y - 4);
        }

        // Translate the point to account for magnification
        Point newPoint = new Point((x - x % zoom) / zoom, (y - y % zoom) / zoom);

        switch (zoom) {
            case 2:
                return  newPoint;
            case 3:
            case 4:
                newPoint.translate(2, 2);
                return  newPoint;
            case 5:
            case 6:
            case 7:
            case 8:
                newPoint.translate(3, 3);
                return  newPoint;
            default:
                return new Point(x - 4, y - 4);
        }
    }

    protected final void GenericDrag(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, x, y));
        }
    }

    protected Color _getColor(PaintCanvas source, MouseEvent event) {
        if (SwingUtilities.isLeftMouseButton(event)) {
            return source.getColor(0);
        } else if (SwingUtilities.isRightMouseButton(event)) {
            return source.getColor(1);
        }

        return null;
    }
}
