package Utilities;

import Core.Toolbar;
import Valids.CursorType;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import javax.imageio.ImageIO;
import javax.swing.JRootPane;

public final class CursorUtils {

    private static final Point _BOX_HOTSPOT_ = new Point(0, 0);
    private static final Point _BUCKET_HOTSPOT_ = new Point(30, 27);
    private static final Point _MAGNIFY_HOTSPOT_ = new Point(10, 10);
    private static final Point _PENCIL_HOTSPOT_ = new Point(4, 27);
    private static final Point _SPRAYCAN_HOTSPOT_ = new Point(30, 3);

    private static final Dimension _CURSOR_SIZE_ = new Dimension(32, 32);

    private static Cursor _DEFAULT_CURSOR_ = Cursor.getDefaultCursor();
    private static Cursor _MOVE_CURSOR_ = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    private static Cursor _TEXT_CURSOR_ = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    private static Cursor _CROSSHAIR_CURSOR_ = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    private static Cursor _RESIZE_E_CURSOR_ = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    private static Cursor _RESIZE_N_CURSOR_ = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    private static Cursor _RESIZE_NE_CURSOR_ = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
    private static Cursor _RESIZE_NW_CURSOR_ = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
    private static Cursor _RESIZE_S_CURSOR_ = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
    private static Cursor _RESIZE_SE_CURSOR_ = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
    private static Cursor _RESIZE_SW_CURSOR_ = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
    private static Cursor _RESIZE_W_CURSOR_ = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);

    private static Cursor _BOX_CURSOR_= _loadCursor("/res/box.png", _BOX_HOTSPOT_, _CURSOR_SIZE_);
    private static Cursor _BUCKET_CURSOR_ = _loadCursor("/res/paintcan.png", _BUCKET_HOTSPOT_, _CURSOR_SIZE_);
    private static Cursor _MAGNIFY_CURSOR_ = _loadCursor("/res/magnify.png", _MAGNIFY_HOTSPOT_, _CURSOR_SIZE_);
    private static Cursor _PENCIL_CURSOR_ = _loadCursor("/res/pencil.png", _PENCIL_HOTSPOT_, _CURSOR_SIZE_);
    private static Cursor _SPRAYCAN_CURSOR_ = _loadCursor("/res/spraycan.png", _SPRAYCAN_HOTSPOT_, _CURSOR_SIZE_);


    private static Cursor _currentCursor = _DEFAULT_CURSOR_;

    public static Cursor getCurrentCursor() {
        return CursorUtils._currentCursor;
    }

    public static void setCurrentCursor(CursorType newCursor) {
        CursorUtils._currentCursor = _getCursorFromType(newCursor);
    }

    public static void applyCursor(JRootPane rootPane) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        if (toolkit != null) {
            rootPane.setCursor(getCurrentCursor());
        }
    }

    public static void clearCursor(JRootPane rootPane) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        if (toolkit != null) {
            rootPane.setCursor(_DEFAULT_CURSOR_);
        }
    }

    public static void setTempCursor(CursorType tempCursor, JRootPane rootPane) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        if (toolkit != null) {
            rootPane.setCursor(_getCursorFromType(tempCursor));
        }
    }

    public static void clearTempCursor(JRootPane rootPane) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        if (toolkit != null) {
            rootPane.setCursor(_currentCursor);
        }
    }

    private static Cursor _loadCursor(String imagePath, Point hotspot, Dimension size) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        if (toolkit != null) {
            try {
                return toolkit.createCustomCursor(_scaleCursorImage(imagePath, size), hotspot, "customCursor");
            } catch (Exception e) {
                return _DEFAULT_CURSOR_;
            }
        }

        return _DEFAULT_CURSOR_;
    }

    private static Image _scaleCursorImage(String imagePath, Dimension size)
    {
        int width = size.width;
        int height = size.height;

        try {
            return ImageIO.read(Toolbar.class.getResource(imagePath)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setBoxCursorSize(int sideLength) {
        boolean switchBack = false;
        if (getCurrentCursor().equals(CursorUtils._BOX_CURSOR_)) {
            switchBack = true;
            setCurrentCursor(CursorType.Default);
        }

        Dimension size = new Dimension(sideLength, sideLength);
        Point hotspot = new Point(sideLength / 2, sideLength / 2);
        CursorUtils._BOX_CURSOR_ = _loadCursor("/res/box.png", hotspot, size);

        if (switchBack) {
            setCurrentCursor(CursorType.Box);
        }
    }

    private static Cursor _getCursorFromType(CursorType cursorType) {
        switch (cursorType) {
            case Box:
                return _BOX_CURSOR_;
            case Bucket:
                return _BUCKET_CURSOR_;
            case Crosshair:
                return _CROSSHAIR_CURSOR_;
            case Magnify:
                return _MAGNIFY_CURSOR_;
            case Move:
                return _MOVE_CURSOR_;
            case Pencil:
                return _PENCIL_CURSOR_;
            case Resize_E:
                return _RESIZE_E_CURSOR_;
            case Resize_N:
                return _RESIZE_N_CURSOR_;
            case Resize_NE:
                return _RESIZE_NE_CURSOR_;
            case Resize_NW:
                return _RESIZE_NW_CURSOR_;
            case Resize_S:
                return _RESIZE_S_CURSOR_;
            case Resize_SE:
                return _RESIZE_SE_CURSOR_;
            case Resize_SW:
                return _RESIZE_SW_CURSOR_;
            case Resize_W:
                return _RESIZE_W_CURSOR_;
            case SprayCan:
                return _SPRAYCAN_CURSOR_;
            case Text:
                return _TEXT_CURSOR_;
            case Default:
            default:
                return _DEFAULT_CURSOR_;
        }
    }
}
