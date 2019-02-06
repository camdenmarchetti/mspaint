package Tools;

import Core.PaintCanvas;

import java.awt.Color;
import java.awt.event.MouseEvent;

public class MagnifyingGlass extends PaintTool {
    @Override
    public void MousePress(PaintCanvas source, MouseEvent event) {
    }

    @Override
    public void MouseRelease(PaintCanvas source, MouseEvent event) {
    }

    @Override
    public void MouseClick(PaintCanvas source, MouseEvent event) {
    }

    @Override
    public void MouseDrag(PaintCanvas source, MouseEvent event) {
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x, int y) {
        return _draw(canvas, drawColor, x, y, -1, -1);
    }

    @Override
    protected boolean _draw(PaintCanvas canvas, Color drawColor, int x1, int y1, int x2, int y2) {
        return false;
    }
}
