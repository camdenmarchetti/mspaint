package Core;

import Utilities.CursorUtils;
import Valids.CursorType;
import Valids.SizeUnit;
import Valids.ZoomLevel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.border.Border;

public class ResizableCanvasBorder implements Border, MouseListener, MouseMotionListener {

    private final int _NUB_SIZE_ = 4;

    private Rectangle[] _nubs = new Rectangle[3];
    private PaintCanvas _parent;

    private int _startNub = -1;
    private Point _dragStartPoint;
    private Dimension _dragStartSize;

    public ResizableCanvasBorder(PaintCanvas parent) {
        parent.addMouseListener(this);
        parent.addMouseMotionListener(this);

        this._parent = parent;

        int width = parent.getWidth();
        int height = parent.getHeight();

        _nubs[0] = new Rectangle(width - _NUB_SIZE_, height / 2 - _NUB_SIZE_ / 2, _NUB_SIZE_, _NUB_SIZE_);
        _nubs[1] = new Rectangle(width - _NUB_SIZE_, height - _NUB_SIZE_, _NUB_SIZE_, _NUB_SIZE_);
        _nubs[2] = new Rectangle(width / 2 - _NUB_SIZE_ / 2, height - _NUB_SIZE_, _NUB_SIZE_, _NUB_SIZE_);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        x = 4;
        y = 4;

        // Don't allow resize when zoomed in
        if (ToolSettings.getZoomLevel() != ZoomLevel.x1.getValue()) {
            int zoomLevel = ToolSettings.getZoomLevel();
            Insets borderInsets = getBorderInsets(c);

            int w = width - (borderInsets.left + borderInsets.right) * zoomLevel - _NUB_SIZE_;
            int h = height - (borderInsets.top + borderInsets.bottom) * zoomLevel - _NUB_SIZE_;

            g.setColor(Color.BLACK);
            g.drawRect(x + _NUB_SIZE_, y + _NUB_SIZE_, w + 4 * (zoomLevel - 1), h + 4 * (zoomLevel - 1));

            return;
        }

        g.setColor(Color.BLACK);

        Insets borderInsets = getBorderInsets(c);
        int w = width - borderInsets.left + borderInsets.right;
        int h = height - borderInsets.top + borderInsets.bottom;

        g.drawRect(x + _NUB_SIZE_, y + _NUB_SIZE_, w - x - _NUB_SIZE_ * 2, h - y - _NUB_SIZE_ * 2);

        g.setColor(Color.BLUE);

        int x0 = x;
        int y0 = y;

        int x1 = w / 2;
        int y1 = h / 2;

        int x2 = w - _NUB_SIZE_;
        int y2 = h - _NUB_SIZE_;


        // Draw initial squares
        g.fillRect(x0, y0, _NUB_SIZE_, _NUB_SIZE_);
        g.fillRect(x0, y1, _NUB_SIZE_, _NUB_SIZE_);
        g.fillRect(x0, y2, _NUB_SIZE_, _NUB_SIZE_);
        g.fillRect(x1, y0, _NUB_SIZE_, _NUB_SIZE_);
        g.fillRect(x1, y2, _NUB_SIZE_, _NUB_SIZE_);
        g.fillRect(x2, y0, _NUB_SIZE_, _NUB_SIZE_);
        g.fillRect(x2, y1, _NUB_SIZE_, _NUB_SIZE_);
        g.fillRect(x2, y2, _NUB_SIZE_, _NUB_SIZE_);


        // Cover unusable squares
        g.setColor(Color.WHITE);
        g.fillRect(x0 + 1, y0 + 1, _NUB_SIZE_ - 1, _NUB_SIZE_ - 1);
        g.fillRect(x0 + 1, y1 + 1, _NUB_SIZE_ - 1, _NUB_SIZE_ - 1);
        g.fillRect(x0 + 1, y2 + 1, _NUB_SIZE_ - 1, _NUB_SIZE_ - 1);
        g.fillRect(x1 + 1, y0 + 1, _NUB_SIZE_ - 1, _NUB_SIZE_ - 1);
        g.fillRect(x2 + 1, y0 + 1, _NUB_SIZE_ - 1, _NUB_SIZE_ - 1);


        // Designate resize hot spots
        _nubs[0] = new Rectangle(x2, y1, _NUB_SIZE_, _NUB_SIZE_);
        _nubs[1] = new Rectangle(x2, y2, _NUB_SIZE_, _NUB_SIZE_);
        _nubs[2] = new Rectangle(x1, y2, _NUB_SIZE_, _NUB_SIZE_);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(_NUB_SIZE_, _NUB_SIZE_, _NUB_SIZE_, _NUB_SIZE_);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    private int _getTargetNub(MouseEvent e) {
        Point target = e.getPoint();

        if (_nubs[0].contains(target)) {
            return 0;
        } else if (_nubs[1].contains(target)) {
            return 1;
        } else if (_nubs[2].contains(target)) {
            return 2;
        }

        return -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (_dragStartPoint == null || _startNub < 0 || _startNub > 2) {
            return;
        }

        if (_dragStartSize == null) {
            _dragStartSize = new Dimension(_parent.getWidth(), _parent.getHeight());
        }

        int width = _dragStartSize.width;
        int height = _dragStartSize.height;

        int dx = (e.getLocationOnScreen().x - _dragStartPoint.x);
        int dy = (e.getLocationOnScreen().y - _dragStartPoint.y);

        if (_startNub == 0) {
            _parent.Resize(SizeUnit.Pels, width + dx, height);
        } else if (_startNub == 1) {
            _parent.Resize(SizeUnit.Pels, width + dx, height + dy);
        } else if (_startNub == 2) {
            _parent.Resize(SizeUnit.Pels, width, height + dy);
        }

        _parent.getParent().revalidate();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int targetNub = _getTargetNub(e);

        if (targetNub != -1) {
            if (_dragStartPoint == null || _startNub == -1) {
                _dragStartPoint = e.getLocationOnScreen();
                _dragStartSize = new Dimension(_parent.getWidth(), _parent.getHeight());
                _startNub = targetNub;
            }

            _parent.ForbidDraw();
            switch (targetNub) {
                case 0:
                    CursorUtils.setTempCursor(CursorType.Resize_E, _parent.getRootPane());
                    break;
                case 1:
                    CursorUtils.setTempCursor(CursorType.Resize_SE, _parent.getRootPane());
                    break;
                case 2:
                    CursorUtils.setTempCursor(CursorType.Resize_S, _parent.getRootPane());
                    break;
                default:
                    _parent.AllowDraw();
                    CursorUtils.clearTempCursor(_parent.getRootPane());
            }
        } else {
            _dragStartPoint = null;
            _dragStartSize = null;
            _startNub = -1;

            _parent.AllowDraw();
            CursorUtils.clearTempCursor(_parent.getRootPane());
        }
    }
}
