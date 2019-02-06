package Tools;

import Core.PaintCanvas;
import Core.ToolSettings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class SprayCan extends PaintTool {

    private volatile boolean _painting = false;
    private volatile int _mouseX, _mouseY;
    private Thread _paintLoop;

    public SprayCan() {
        ToolSettings.setBrushSize(5);
    }

    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();

        Point click = _adjustPoint(event.getX(), event.getY());

        this._painting = true;
        this._mouseX = click.x;
        this._mouseY = click.y;

        if (this._paintLoop == null || !this._paintLoop.isAlive()) {

            // Clean up old thread first
            if (this._paintLoop != null) {
                this._paintLoop.interrupt();
                this._paintLoop = null;
            }

            // Start the new thread to color the spray paint
            this._paintLoop = new Thread(() -> {

                Color paintColor = _getColor(source, event);

                while (SprayCan.this._painting && !Thread.currentThread().isInterrupted()) {

                    // Drawing needs another thread?
                    //new Thread(() ->
                    source.RequestPaint(_draw(source, paintColor, SprayCan.this._mouseX, SprayCan.this._mouseY));
                    //).start();

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException interruption) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            this._paintLoop.start();
        }
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
        this._painting = false;
        if (this._paintLoop != null) {
            this._paintLoop.interrupt(); // ensure the thread dies
        }
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
        source.StorePrePaint();

        Point click = _adjustPoint(event.getX(), event.getY());

        _draw(source, _getColor(source, event), click.x, click.y);
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
        Point click = _adjustPoint(event.getX(), event.getY());

        this._mouseX = click.x;
        this._mouseY = click.y;
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {

        Graphics2D graphics = canvas.getGraphics2D();
        if (graphics != null) {

            graphics.setPaint(drawColor);
            graphics.setStroke(new BasicStroke());

            int radius = ToolSettings.getBrushSize();

            // Declare top-left point of bounding rectangle
            int x1 = x - radius;
            int y1 = y - radius;

            // Declare width/height of bounding rectangle
            int width = radius * 2;
            int height = radius * 2;
            int rSquared = radius * radius;

            // Iterate the bounding rectangle
            for (int row = y1; row < y1 + height; row++) {
                for (int col = x1; col < x1 + width; col++) {

                    // Check that the euclidean distance from the center
                    // to the current point is l/e the squared radius
                    if ((((row - y) * (row - y)) + ((col - x) * (col - x))) < rSquared) {

                        // We're in the circle. What are the odds we color the pixel?
                        if ((int)(Math.random() * 100) < 8) {
                            graphics.drawLine(col, row, col, row);
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        return false;
    }
}
