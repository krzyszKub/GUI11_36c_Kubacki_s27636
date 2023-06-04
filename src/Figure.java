import java.awt.*;

abstract class Figure {
    private int x;
    private int y;
    private Color color;

    public Figure(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public abstract void paint(Graphics graphics);
    public int getX() {
        return x;
    }
    public abstract boolean isPointInside(int x, int y);

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
