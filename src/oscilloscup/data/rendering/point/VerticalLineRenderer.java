package oscilloscup.data.rendering.point;



import oscilloscup.data.DataElement;
import oscilloscup.data.Point;
import oscilloscup.system.Dimension;
import oscilloscup.system.Space;

public class VerticalLineRenderer extends PointRenderer
{

 
    public void drawImpl(DataElement object, Space space)
    {
        Point point = (Point) object;
        Dimension xDimension = space.getXDimension();
        int x = xDimension.convertToGraphicsCoordonateSystem(point.getX());
        space.getFigureGraphics().drawLine(x, 0, x, 250);
    }

    public String getPublicName()
    {
        return "vertical line";
    }

}