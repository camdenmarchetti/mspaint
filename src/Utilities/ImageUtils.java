package Utilities;

import Valids.Direction;
import Valids.RotateAmount;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import static java.awt.image.AffineTransformOp.TYPE_BICUBIC;

public final class ImageUtils {

    private static final double _DEG_TO_RAD_ = Math.PI / 180;

    public static BufferedImage RotateImage(BufferedImage image, RotateAmount amount) {
        int width = image.getWidth();
        int height = image.getHeight();

        int quadrantCount;
        if (amount == RotateAmount.R90) {
            quadrantCount = 1;
        } else if (amount == RotateAmount.R180) {
            quadrantCount = 2;
        } else if (amount == RotateAmount.R270) {
            quadrantCount = 3;
        } else {
            return image;
        }

        AffineTransform transform = AffineTransform.getQuadrantRotateInstance(quadrantCount, width / 2, height / 2);
        AffineTransformOp execute = new AffineTransformOp(transform, TYPE_BICUBIC);
        BufferedImage transformed = execute.createCompatibleDestImage(image, null);

        return execute.filter(image, transformed);
    }

    public static BufferedImage FlipImage(BufferedImage image, Direction direction) {
        int sx = direction == Direction.Horizontal ? -1 : 1;
        int sy = direction == Direction.Horizontal ? 1 : -1;
        int tx = direction == Direction.Horizontal ? -image.getWidth() : 0;
        int ty = direction == Direction.Horizontal ? 0 : -image.getHeight();

        AffineTransform transform = AffineTransform.getScaleInstance(sx, sy);
        transform.translate(tx, ty);

        AffineTransformOp execute = new AffineTransformOp(transform, TYPE_BICUBIC);
        BufferedImage transformed = execute.createCompatibleDestImage(image, null);

        return execute.filter(image, transformed);
    }

    public static BufferedImage ScaleImage(BufferedImage image, double scaleX, double scaleY) {
        double tx = scaleX > 1 ? image.getWidth() / scaleX : 0;
        double ty = scaleY > 1 ? image.getHeight() / scaleY : 0;

        AffineTransform transform = AffineTransform.getScaleInstance(scaleX, scaleY);
        transform.translate(tx, ty);

        AffineTransformOp execute = new AffineTransformOp(transform, TYPE_BICUBIC);
        BufferedImage transformed = execute.createCompatibleDestImage(image, null);

        return execute.filter(image, transformed);
    }

    public static BufferedImage SkewImage(BufferedImage image, Direction direction, double amount) {
        int width = image.getWidth();
        int height = image.getHeight();
        int delta;

        if (direction == Direction.Horizontal) {
            delta = (int)(Math.tan(amount * _DEG_TO_RAD_) * height);
            width += Math.abs(delta);
        } else {
            delta = (int)(Math.tan(amount * _DEG_TO_RAD_) * width);
            height += Math.abs(delta);
        }

        if (delta == 0) {
            return image;
        }

        BufferedImage skewed = new BufferedImage(width, height, image.getType());

        Raster oldData = image.getData();
        WritableRaster newData = skewed.getRaster();

        if (direction == Direction.Horizontal) {
            if (delta >= 0) {
                for (int row = 0; row < height; row++) {
                    int rowDelta = (int)(Math.tan(amount * _DEG_TO_RAD_) * (height - row));
                    for (int col = 0; col < image.getWidth(); col++) {
                        newData.setPixel(col + rowDelta, row, oldData.getPixel(col, row, (int[]) null));
                    }
                }
            } else {
                amount = -amount;
                for (int row = 0; row < height; row++) {
                    int rowDelta = (int)(Math.tan(amount * _DEG_TO_RAD_) * row);
                    for (int col = 0; col < image.getWidth(); col++) {
                        newData.setPixel(col + rowDelta, row, oldData.getPixel(col, row, (int[]) null));
                    }
                }
            }
        } else {
            if (delta < 0) {
                for (int col = 0; col < width; col++) {
                    int colDelta = (int)(Math.tan(amount * _DEG_TO_RAD_) * (width - col));
                    for (int row = 0; row < image.getHeight(); row++) {
                        newData.setPixel(col, row + colDelta, oldData.getPixel(col, row, (int[])null));
                    }
                }
            } else {
                amount = -amount;
                for (int col = 0; col < width; col++) {
                    int colDelta = (int)(Math.tan(amount * _DEG_TO_RAD_) * col);
                    for (int row = 0; row < image.getHeight(); row++) {
                        newData.setPixel(col, row + colDelta, oldData.getPixel(col, row, (int[])null));
                    }
                }
            }
        }

        return skewed;
    }
}
