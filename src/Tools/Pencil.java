package Tools;

import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class Pencil extends PaintTool {

    private int _oldX = -1, _oldY = -1;

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();

        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, x, y, x, y));
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        this._oldX = -1;
        this._oldY = -1;
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
        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, this._oldX, this._oldY, x, y));
        }
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return this._draw(canvas, drawColor, x, y, -1, -1);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        this._oldX = x2;
        this._oldY = y2;

        Graphics2D graphics = canvas.getGraphics2D();

        if (graphics != null) {
            graphics.setPaint(drawColor);
            graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));

            if (x2 != -1 && y2 != -1) {
                graphics.drawLine(x1, y1, x2, y2);
            } else {
                graphics.drawLine(x1, y1, x1, y1);
            }

            return true;
        }

        return false;
    }
}
