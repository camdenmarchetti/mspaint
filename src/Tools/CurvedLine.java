package Tools;

import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;

public class CurvedLine extends PaintTool {
    private Line2D _line;
    private Shape _curvedLine;
    private Point[] _controls = { null, null };

    public CurvedLine() {
        ToolSettings.setBrushSize(1);
    }

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());

        if (this._line == null) {
            source.StorePrePaint();
            source.SetupTempPaint();

            this._line = new Line2D.Float(click.x, click.y, click.x, click.y);
        } else {
            if (this._controls[0] == null) {
                this._controls[0] = click;
            } else {
                this._controls[1] = click;
            }

            Color paintColor = _getColor(source, event);
            _correctLineCurve(source, paintColor);
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        if (this._controls[1] != null) {
            this._line = null;
            this._curvedLine = null;
            this._controls[0] = null;
            this._controls[1] = null;

            source.ClearTempPaint();
        }
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        this._line = null;
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());
        Color paintColor = _getColor(source, event);

        if (this._controls[0] == null) {
            if (paintColor != null) {
                source.RequestPaint(_draw(source, paintColor, click.x, click.y));
            }
        } else if (this._controls[1] == null) {
            this._controls[0].x = click.x;
            this._controls[0].y = click.y;
            _correctLineCurve(source, paintColor);
        } else {
            this._controls[1].x = click.x;
            this._controls[1].y = click.y;
            _correctLineCurve(source, paintColor);
        }
    }

    private void _correctLineCurve(PaintCanvas canvas, Color drawColor) {
        if (this._line == null) {
            return;
        }

        double x1 = this._line.getX1();
        double y1 = this._line.getY1();
        double x2 = this._line.getX2();
        double y2 = this._line.getY2();

        if (this._controls[0] != null) {
            int ctrx1 = this._controls[0].x;
            int ctry1 = this._controls[0].y;

            if (this._controls[1] != null) {
                int ctrx2 = this._controls[1].x;
                int ctry2 = this._controls[1].y;

                this._curvedLine = new CubicCurve2D.Double(x1, y1, ctrx2, ctry2, ctrx1, ctry1, x2, y2);
            } else {
                this._curvedLine = new QuadCurve2D.Double(x1, y1, ctrx1, ctry1, x2, y2);
            }
        }

        canvas.RequestPaint(_draw(canvas, drawColor, -1, -1));
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return this._draw(canvas, drawColor, x, y, -1, -1);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        Graphics2D graphics = canvas.getGraphics2D();

        if (graphics != null && this._line != null) {
            graphics.setPaint(drawColor);

            graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));

            if (x1 == -1 && y1 == -1 && x2 == -1 && y2 == -1) {
                if (this._curvedLine == null) {
                    return false;
                }

                canvas.ResetTempPaint();
                graphics.draw(this._curvedLine);
            } else {
                this._line.setLine(new Point(x1, y1), this._line.getP2());

                canvas.ResetTempPaint();
                graphics.draw(this._line);
            }

            return true;
        }

        return false;
    }
}
