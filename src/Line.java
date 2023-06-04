import java.awt.*;

public class Line extends Figure{
    private PreviousPoint previousPoint;
    private int size;

    public Line(int x, int y, Color color, PreviousPoint previousPoint, int size) {
        super(x, y, color);
        this.previousPoint = previousPoint;
        this.size = size;
    }


    @Override
    public boolean isPointInside(int x, int y) {
        return (x - previousPoint.getX()) * (super.getY() - previousPoint.getY()) == (y - previousPoint.getY()) * (super.getX() - previousPoint.getX());

    }


    @Override
    public void paint(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setColor(getColor());
        g2d.setStroke(new BasicStroke(size));
        g2d.drawLine(previousPoint.getX(), previousPoint.getY(), getX(), getY());
    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPreviousPoint(PreviousPoint previousPoint) {
        this.previousPoint = previousPoint;
    }
}
