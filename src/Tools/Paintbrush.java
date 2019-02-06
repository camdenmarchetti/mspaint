package Tools;

import Valids.BrushShape;
import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class Paintbrush extends PaintTool {
    private int _oldX = -1, _oldY = -1;

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();
        Point click = _adjustPoint(event.getX(), event.getY());

        this._oldX = click.x;
        this._oldY = click.y;

        source.RequestPaint(_draw(source, _getColor(source, event), this._oldX, this._oldY));
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        this._oldX = -1;
        this._oldY = -1;
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();
        source.RequestPaint(_draw(source, _getColor(source, event), event.getX(), event.getY()));
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        source.RequestPaint(_draw(source, _getColor(source, event), this._oldX, this._oldY, x, y));

        this._oldX = x;
        this._oldY = y;
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return _draw(canvas, drawColor, x, y, x, y);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        Graphics2D graphics = canvas.getGraphics2D();

        if (graphics != null) {
            graphics.setPaint(drawColor);
            int size = ToolSettings.getBrushSize();
            int zoom = ToolSettings.getZoomLevel();
            BrushShape shape = ToolSettings.getBrushShape();

            if (shape == BrushShape.ForeSlash || shape == BrushShape.BackSlash) {
                int dir = shape == BrushShape.BackSlash ? -1 : 1;
                graphics.setStroke(new BasicStroke());

                graphics.drawLine(x1, y1, x2 + (size / zoom) * dir, y2 - (size / zoom));

                if (this._oldX != -1 && this._oldY != -1) {
                    java.awt.Polygon fill = new java.awt.Polygon();
                    fill.addPoint(x2, y2);
                    fill.addPoint(x2 + size * dir, y2 - size);
                    fill.addPoint(x1 + size * dir, y1 - size);
                    fill.addPoint(x1, y1);

                    graphics.fillPolygon(fill);
                }
            } else {
                int cap = (shape == BrushShape.Circle) ? BasicStroke.CAP_ROUND : BasicStroke.CAP_SQUARE;
                graphics.setStroke(new BasicStroke(size, cap, BasicStroke.JOIN_MITER, 10.f, null, 0.f));
                graphics.drawLine(x1, y1, x2, y2);
            }

           // graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            return true;
        }

        return false;
    }
}
