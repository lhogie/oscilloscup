package oscilloscup.data.event;




import oscilloscup.data.Figure;
import oscilloscup.data.Point;


/**
 * @author Luc Hogie
 */

public interface FigureListener extends DataElementListener
{
	void pointInserted( Figure figure, Point point, int position );
	void pointRemoved( Figure figure, Point point, int position );
	void figureInserted( Figure figure, Figure subFigure, int position );
	void figureRemoved( Figure figure, Figure subFigure, int position );
}
