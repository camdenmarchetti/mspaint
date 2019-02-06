package Tools;

import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class Eraser extends PaintTool {
    private int _oldX = -1, _oldY = -1;

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();

        Erase(source, event);

        Point click = _adjustPoint(event.getX(), event.getY());
        this._oldX = click.x;
        this._oldY = click.y;
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        // Unused
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();
        Erase(source, event);
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());
        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, click.x, click.y, this._oldX, this._oldY));
        }

        this._oldX = click.x;
        this._oldY = click.y;
    }

    private void Erase(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());
        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, click.x, click.y));
        }
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return this._draw(canvas, drawColor, x, y, x, y);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        Graphics2D graphics = canvas.getGraphics2D();

        if (graphics != null) {
            graphics.setPaint(drawColor);
            int size = ToolSettings.getBrushSize();
            int cap = BasicStroke.CAP_SQUARE;

           // graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setStroke(new BasicStroke(size, cap, BasicStroke.JOIN_MITER, 10.f, null, 0.f));
            graphics.drawLine(x1, y1, x2, y2);

           // graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            return true;
        }

        return false;
    }

    @Override
    protected Color _getColor(PaintCanvas source, MouseEvent event) {
        return source.getColor(1);
    }
}
