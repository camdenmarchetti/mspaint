package Tools;

import Valids.FillMode;
import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

public class Polygon extends PaintTool {
    private java.awt.Polygon _shape;
    private Line2D _line;

    public Polygon() {
        ToolSettings.setBrushSize(1);
    }

    private void initialize(PaintCanvas source, int x, int y) {
        source.StorePrePaint();

        this._shape = new java.awt.Polygon();
        this._line = new Line2D.Float(x, y, x, y);

        this._shape.addPoint(x, y);
    }

    private void resetInstance(PaintCanvas source) {
        source.ClearTempPaint();

        this._shape.invalidate();
        this._shape.reset();

        this._shape = null;
        this._line = null;
    }

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.SetupTempPaint();

        if (this._shape == null) {
            initialize(source, event.getX(), event.getY());
        } else {
            MouseDrag(source, event);
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        if (this._shape == null) {
            return;
        }

        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        Color paintColor = _getColor(source, event);
        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, x, y));
        }

        source.ClearTempPaint();
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) { }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());
        int x = click.x;
        int y = click.y;

        Color paintColor = _getColor(source, event);
        if (paintColor != null) {
            source.RequestPaint(_draw(source, paintColor, -1, -1, x, y));
        }
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return this._draw(canvas, drawColor, x, y, -1, -1);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        Graphics2D graphics = canvas.getGraphics2D();

        if (graphics == null) {
            return false;
        }

        if (x1 == -1 && y1 == -1) {
            // Have not committed; draw temp line
            graphics.setPaint(drawColor);

            graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));
            if (this._line != null) {
                this._line.setLine(this._line.getP1(), new Point(x2, y2));
            } else {
                return false;
            }

            canvas.ResetTempPaint();
            graphics.draw(this._line);

            return true;
        }

        if (this._shape == null) {
            return false;
        }

        int deltaX = Math.abs(this._shape.xpoints[0] - x1);
        int deltaY = Math.abs(this._shape.ypoints[0] - y1);
        boolean closed = false;

        int tolerance = (3 + ToolSettings.getBrushSize());

        if (this._shape.npoints >= 3 && ((deltaX * deltaX) + (deltaY * deltaY) < ((tolerance * tolerance) + 1))) {
            this._shape.addPoint(this._shape.xpoints[0], this._shape.ypoints[0]);
            this._line = null;
            closed = true;
        } else if (this._shape.npoints > 1 || !this._shape.contains(x1, y1)) {
            this._shape.addPoint(x1, y1);

            if (this._line != null) {
                this._line.setLine(x1, y1, x1, y1);
            } else {
                this._line = new Line2D.Float(x1, y1, x1, y1);
            }
        }

        canvas.ResetTempPaint();
        FillMode fillMode = ToolSettings.getFillMode();

        if (closed) {
            if (fillMode == FillMode.FillNoBorder || fillMode == FillMode.Both) {
                graphics.setStroke(new BasicStroke());

                if (drawColor == canvas.getColor(0)) {
                    graphics.setPaint(canvas.getColor(1));
                } else {
                    graphics.setPaint(canvas.getColor(0));
                }

                graphics.fillPolygon(this._shape);
            }

            if (fillMode == FillMode.BorderNoFill || fillMode == FillMode.Both) {
                graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));
                graphics.setPaint(drawColor);
                graphics.drawPolygon(this._shape);
            }

            resetInstance(canvas);
        } else {
            int prevX = this._shape.xpoints[0], prevY = this._shape.ypoints[0];

            graphics.setPaint(drawColor);
            graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));

            for (int index = 1; index < this._shape.npoints; index++) {
                graphics.drawLine(prevX, prevY, this._shape.xpoints[index], this._shape.ypoints[index]);
                prevX = this._shape.xpoints[index];
                prevY = this._shape.ypoints[index];
            }
        }

        return true;
    }
}
