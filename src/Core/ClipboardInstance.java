package Core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

public class ClipboardInstance implements ClipboardOwner {
    public ClipboardInstance(BufferedImage image) {
        _TransferableImage tempImage = new _TransferableImage(image);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(tempImage, this);
    }

    public void lostOwnership(Clipboard clip, Transferable trans ) { }

    // To transfer we need an image that implements the Transferable interface
    private class _TransferableImage implements Transferable {
        BufferedImage _source;
        _TransferableImage(BufferedImage image) {
            this._source = image;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.imageFlavor);
        }

        public Object getTransferData(DataFlavor flavor) throws NullPointerException, UnsupportedFlavorException {
            if (this._source == null) {
                throw new NullPointerException("Source image not provided.");
            }

            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            return this._source;
        }
    }
}
