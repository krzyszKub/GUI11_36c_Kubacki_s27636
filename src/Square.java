import java.awt.*;

public class Square extends Figure{
    private int width;
    public Square(int x, int y, Color color, int width) {
        super(x, y, color);
        this.width = width;
    }


    @Override
    public boolean isPointInside(int x, int y) {
        return x >= super.getX() && x <= super.getX() + width && y >= super.getY() && y <= super.getY() + width;
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(super.getColor());
        graphics.fillRect(super.getX() - width/2, super.getY() - width/2, width, width);
    }
}
