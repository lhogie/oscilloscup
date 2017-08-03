package oscilloscup.data.rendering.point;



import oscilloscup.data.*;
import oscilloscup.system.Dimension;
import oscilloscup.system.Space;

public class XDimensionOriginAxisConnectedPointRenderer extends PointRenderer
{

    public void drawImpl(DataElement object, Space space)
    {
        Point point = (Point) object;
        Dimension xDimension = space.getXDimension();
        Dimension yDimension = space.getYDimension();

        int x = xDimension.convertToGraphicsCoordonateSystem(point.getX());
        int y = yDimension.convertToGraphicsCoordonateSystem(point.getY());
        space.getFigureGraphics().drawLine(x, y, x, (int) space.getOriginPoint().getY());
    }

    public String getPublicName()
    {
        return "connected to Xaxis";
    }

}