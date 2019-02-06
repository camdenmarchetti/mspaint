package Tools;

import Core.PaintCanvas;

import Valids.SelectMode;
import Core.ToolSettings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FreeSelect extends PaintTool {

    private int _oldX = -1, _oldY = -1;
    private Polygon _shape = new Polygon();

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();
        source.SetupTempPaint();

        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        Color paintColor = _getColor(source, event);

        if (this._shape == null) {
            this._shape = new Polygon();
        }

        this._shape.addPoint(x, y);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, x, y, x, y));
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        if (this._shape == null) {
            this._shape = new Polygon();
            return;
        }

        if (this._shape.npoints > 0) {
            int x = this._shape.xpoints[0];
            int y = this._shape.ypoints[0];

            this._shape.addPoint(x, y);
            source.RequestPaint(_draw(source, null, this._oldX, this._oldY, x, y));

            Color ignoreColor = null;
            if (ToolSettings.getSelectMode() == SelectMode.IgnoreBackground) {
                ignoreColor = source.getColor(1);
            }

            BufferedImage floatingLayer = source.setSelection(this._shape, ignoreColor);

            if (floatingLayer != null) {
                int locX = this._shape.getBounds().x + 4;
                int locY = this._shape.getBounds().y + 4;

                source.setFloatingLayer(floatingLayer, new Point(locX, locY), this._shape, true);
            }

            //this._shape = null;
            source.ClearTempPaint();
        }

        this._oldX = -1;
        this._oldY = -1;
        this._shape = new Polygon();
        source.ClearTempPaint();
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        // Do nothing
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        if (this._shape == null) {
            this._shape = new Polygon();
            return;
        }

        if (this._shape.npoints == 0) {
            return;
        }

        int x = event.getX();
        int y = event.getY();
        Color paintColor = _getColor(source, event);

        this._shape.addPoint(x, y);

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
            graphics.setPaint(Color.BLACK);
            graphics.setStroke(new BasicStroke(1));

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
