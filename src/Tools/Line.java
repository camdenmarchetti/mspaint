package Tools;

import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

public class Line extends PaintTool {
    private Line2D _line;

    public Line() {
        ToolSettings.setBrushSize(1);
    }

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();
        source.SetupTempPaint();

        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;
        this._line = new Line2D.Float(x, y, x, y);
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        this._line = null;
        source.ClearTempPaint();
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        this._line = null;
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        GenericDrag(source, event);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return this._draw(canvas, drawColor, x, y, -1, -1);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        Graphics2D graphics = canvas.getGraphics2D();

        if (graphics != null) {
            graphics.setPaint(drawColor);

            graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));
            if (this._line != null) {
                this._line.setLine(new Point(x1, y1), this._line.getP2());
            } else {
                return false;
            }

            canvas.ResetTempPaint();
            graphics.draw(this._line);

            return true;
        }

        return false;
    }
}
