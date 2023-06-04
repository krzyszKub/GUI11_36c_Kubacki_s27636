import java.awt.*;

public class Circle extends Figure{
    private int rad;

    public Circle(int x, int y, Color color, int rad) {
        super(x, y, color);
        this.rad = rad;
    }


    @Override
    public boolean isPointInside(int x, int y) {
        double distance = Math.sqrt(Math.pow((x - super.getX()), 2) + Math.pow((y - super.getY()), 2));
        return distance <= rad;
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(super.getColor());
        int diam = rad + rad;
        graphics.fillOval(super.getX() - rad, super.getY() - rad, diam, diam);

    }

}
