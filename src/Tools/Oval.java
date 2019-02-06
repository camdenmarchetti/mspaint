package Tools;

import Valids.FillMode;
import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class Oval extends PaintTool {
    private Ellipse2D _shape;
    private int _oldX = -1, _oldY = -1;

    public Oval() {
        ToolSettings.setBrushSize(1);
        ToolSettings.setFillMode(FillMode.BorderNoFill);
    }

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();
        source.SetupTempPaint();

        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        this._oldX = x;
        this._oldY = y;

        this._shape = new Ellipse2D.Float(x, y, 0, 0);

        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, x, y));
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        this._shape = null;
        source.ClearTempPaint();
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        this._shape = null;
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());

        Color paintColor = _getColor(source, event);

        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, click.x, click.y));
        }
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return this._draw(canvas, drawColor, x, y, -1, -1);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        Graphics2D graphics = canvas.getGraphics2D();

        if (graphics != null) {

            if (this._shape != null) {
                int x = Math.min(x1, this._oldX);
                int y = Math.min(y1, this._oldY);
                int w = Math.abs(x1 - this._oldX);
                int h = Math.abs(y1 - this._oldY);

                this._shape.setFrame(x, y, w, h);
            } else {
                return false;
            }

            canvas.ResetTempPaint();

            FillMode fillMode = ToolSettings.getFillMode();
            graphics.setStroke(new BasicStroke());

            if (fillMode == FillMode.FillNoBorder || fillMode == FillMode.Both) {

                if (drawColor == canvas.getColor(0)) {
                    graphics.setPaint(canvas.getColor(1));
                } else {
                    graphics.setPaint(canvas.getColor(0));
                }

                graphics.fill(this._shape);
            }

            if (fillMode == FillMode.BorderNoFill || fillMode == FillMode.Both) {
                graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));
                graphics.setPaint(drawColor);
                graphics.draw(this._shape);
            }

            return true;
        }

        return false;
    }
}
