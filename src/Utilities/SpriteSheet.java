package Utilities;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class SpriteSheet {

    private static BufferedImage _spriteSheet;
    private static int _squareSize = 0;
    private static boolean _initialized = false;

    public static void Initialize() {

        if (_initialized) {
            return;
        }

        _initialized = true;
        _squareSize = 17;

        try {
            _spriteSheet = ImageIO.read(SpriteSheet.class.getResource("/res/sprite_sheet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Icon GetIcon(int index) {
        if (_spriteSheet == null) {
            return null;
        }

        int row = index / ((_spriteSheet.getWidth() + 1) / _squareSize);
        return GetIcon(row, index - (row * (_spriteSheet.getWidth() + 1) / _squareSize), 1.0);
    }

    public static Icon GetIcon(int index, double span) {
        if (_spriteSheet == null) {
            return null;
        }

        int row = index / ((_spriteSheet.getWidth() + 1) / _squareSize);
        return GetIcon(row, index - (row * (_spriteSheet.getWidth() + 1) / _squareSize), span);
    }

    public static Icon GetIcon(int row, int col, double span) {
        if (_spriteSheet == null) {
            return null;
        }

        int height = _spriteSheet.getHeight();
        int width = _spriteSheet.getWidth();
        int x = col * _squareSize;
        int y = row * _squareSize;

        if (row < 0 || col < 0 || x > width || y > height) {
            return null;
        }

        return new ImageIcon(_spriteSheet.getSubimage(x, y, (int)((_squareSize - 1) * span), _squareSize - 1));
    }
}
