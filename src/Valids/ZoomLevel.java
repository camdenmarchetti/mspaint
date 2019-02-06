package Valids;

public enum ZoomLevel {
    x1(1), x2(2), x4(4), x6(6), x8(8);

    private final int _zoom;
    ZoomLevel(int zoom) {
        this._zoom = zoom;
    }

    public int getValue() {
        return this._zoom;
    }
}
