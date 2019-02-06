package Tools;

import Valids.FillMode;
import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class Text extends PaintTool {
    private Rectangle2D _shape;
    private int _oldX = -1, _oldY = -1;
    private boolean _hasEditor = false;

    public Text() {
        ToolSettings.setBrushSize(1);
        ToolSettings.setFillMode(FillMode.BorderNoFill);
    }

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        if (ToolSettings.getZoomLevel() != 1) {
            return;
        }

        if (!this._hasEditor) {
            source.StorePrePaint();
            source.SetupTempPaint();

            Point click = _adjustPoint(event.getX(), event.getY());
            int x = click.x;
            int y = click.y;

            this._oldX = x;
            this._oldY = y;
            this._shape = new Rectangle2D.Float(this._oldX, this._oldY, 50, 20);

            Color paintColor = _getColor(source, event);
            if (paintColor != null) {
                source.RequestPaint(_draw(source, paintColor, x, y));
            }
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        if (ToolSettings.getZoomLevel() != 1) {
            return;
        }

        if (!this._hasEditor) {
            source.ResetTempPaint();
            source.setupEditor(this._shape);
            this._hasEditor = true;

            this._shape = null;
            source.ClearTempPaint();
            source.RequestPaint(true);
        } else {
            source.drawText();
            this._hasEditor = false;
        }
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        if (ToolSettings.getZoomLevel() != 1) {
            return;
        }

        this._shape = null;
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        if (ToolSettings.getZoomLevel() != 1) {
            return;
        }

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

            if (this._shape != null) {
                int x = Math.min(x1, this._oldX);
                int y = Math.min(y1, this._oldY);
                int w = Math.abs(x1 - this._oldX);
                int h = Math.abs(y1 - this._oldY);

                this._shape.setRect(x, y, w, h);
            } else {
                return false;
            }

            canvas.ResetTempPaint();

            graphics.setStroke(new BasicStroke(ToolSettings.getBrushSize()));
            graphics.setPaint(drawColor);
            graphics.draw(this._shape);

            return true;
        }

        return false;
    }
}
