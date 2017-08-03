package oscilloscup.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import oscilloscup.data.event.FigureListener;
import oscilloscup.data.rendering.DataElementRenderer;
import oscilloscup.data.rendering.figure.FigureRenderer;
import oscilloscup.data.rendering.point.PointRenderer;
import oscilloscup.system.Space;

/**
 * <p>
 * A figure is a container. Basically a figure in a sequence of points. A figure
 * can model what you want. For example if you have to display a curve (with
 * data that come for a multiple measurement of a physical event), you can
 * create a figure and add it each point to it. If you want each point to be
 * connected to the previous and next points, you have to set a
 * ConnectedPointFigureRenderer to the figure.
 * </p>
 * 
 * <p>
 * A figure also contains children figure: this is a recursive data structure.
 * This allow you to build a well organized data structure and display complex
 * stuff. For example, if you have to display the map of your house, you can
 * create a houseAndGarden figure that will contain garden and a house figure.
 * The house figure may contain a kitchen figure, a livingRoom figure, a
 * parentsRoom figure, a childrenRoom figure and a garage figure... Each figure
 * deleguates its painting to a dedicated (or shared, depends on what you want)
 * renderer.
 * </p>
 * 
 * <p>
 * The deep of the tree is not limited.
 * </p>
 * 
 * 
 * @author Luc Hogie
 */
public class Figure extends DataElement
{
	private final List<Point> pointList = new ArrayList<Point>();
	private final List<Figure> figureList = new ArrayList<Figure>();
	private String name = "figure-" + hashCode();

	/**
	 * @see oscilloscup.data.DataElement#addRenderer(DataElementRenderer)
	 */
	public void addRenderer(DataElementRenderer renderer)
	{
		if (renderer instanceof FigureRenderer)
		{
			super.addRenderer(renderer);
		}
		else if (renderer instanceof PointRenderer)
		{
			for (Point p : pointList)
			{
				p.addRenderer(renderer);
			}
		}
		else
		{
			throw new IllegalArgumentException(
					"this is not a FigureRenderer nor a PointRenderer");
		}
	}

	/**
	 * Recursively adds a renderer to the figure and its subfigure.
	 * 
	 * @param renderer
	 */
	public void addRendererRecursively(DataElementRenderer renderer)
	{
		addRenderer(renderer);
		int figureCount = getFigureCount();

		for (int i = 0; i < figureCount; ++i)
		{
			getFigureAt(i).addRendererRecursively(renderer);
		}
	}

	/**
	 * Gets the number of point in this figure.
	 * 
	 * @return int
	 */
	public int getPointCount()
	{
		return pointList.size();
	}

	/**
	 * Gets the point at the given position.
	 * 
	 * @param index
	 * @return Point
	 */
	public Point getPointAt(int index)
	{
		return pointList.get(index);
	}

	/**
	 * Insert the given point at the given position.
	 * 
	 * @param p
	 * @param position
	 */
	public void insertPoint(Point point, int position)
	{
		if (point == null)
			throw new NullPointerException("trying to insert a null point");

		pointList.add(position, point);
		firePointInserted(this, point, position);
	}

	/**
	 * Appends the given point to the figure.
	 * 
	 * @param p
	 */
	public void addPoint(Point p)
	{
		insertPoint(p, getPointCount());
	}

	/**
	 * Removes the point at the given position.
	 * 
	 * @param i
	 * @return Point
	 */
	public Point removePointAt(int i)
	{
		Point point = (Point) pointList.remove(i);
		firePointRemoved(this, point, i);
		return point;
	}

	/**
	 * Remove all the points that figure contain.
	 */
	public void removeAllPoints()
	{
		while (getPointCount() > 0)
		{
			removePointAt(getPointCount() - 1);
		}
	}

	/**
	 * Inserts the given figure at the given position.
	 * 
	 * @param figure
	 * @param position
	 */
	public void insertFigure(Figure figure, int position)
	{
		if (figure == null)
			throw new IllegalArgumentException("null figure");

		figureList.add(position, figure);
		fireFigureInserted(this, figure, position);
	}

	/**
	 * Append the given figure to this figure.
	 * 
	 * @param figure
	 */
	public void addFigure(Figure figure)
	{
		insertFigure(figure, getFigureCount());
	}

	/**
	 * Removes the figure at the given position.
	 * 
	 * @param i
	 * @return Point
	 */
	public Figure removeFigureAt(int i)
	{
		Figure figure = figureList.remove(i);
		fireFigureRemoved(this, figure, i);
		return figure;
	}

	/**
	 * Remove all the figures that figure contain.
	 */
	public void removeAllFigures()
	{
		while (getFigureCount() > 0)
		{
			removeFigureAt(getFigureCount() - 1);
		}
	}

	/**
	 * Gets the figure at the given index.
	 * 
	 * @param index
	 * @return Figure
	 */
	public Figure getFigureAt(int index)
	{
		return figureList.get(index);
	}

	/**
	 * Gets the number of subfigure in this figure.
	 * 
	 * @return int
	 */
	public int getFigureCount()
	{
		return figureList.size();
	}

	/**
	 * Gets the extremums of the figure.
	 * 
	 * @return Extremi
	 */
	public Extremi computeExtremums()
	{
		Extremi extrems = new Extremi();
		fillExtrems(extrems);
		return extrems;
	}

	private void fillExtrems(Extremi extrems)
	{
		int nbPoints = getPointCount();

		for (int i = 0; i < nbPoints; ++i)
		{
			Point p = getPointAt(i);

			if (extrems.nbPoints == 0)
			{
				extrems.minX = extrems.maxX = p.getX();
				extrems.minY = extrems.maxY = p.getY();
			}
			else
			{
				extrems.minX = Math.min(extrems.minX, p.getX());
				extrems.minY = Math.min(extrems.minY, p.getY());
				extrems.maxX = Math.max(extrems.maxX, p.getX());
				extrems.maxY = Math.max(extrems.maxY, p.getY());
			}

			++extrems.nbPoints;
		}

		for (Figure f : figureList)
		{
			f.fillExtrems(extrems);
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Figure clone()
	{
		Figure figure = new Figure();

		for (Point p : pointList)
		{
			figure.addPoint(p.clone());
		}

		for (Figure f : figureList)
		{
			figure.addFigure(f.clone());
		}

		int rendererCount = getRendererCount();

		for (int i = 0; i < rendererCount; ++i)
		{
			figure.addRenderer(getRendererAt(i));
		}

		return figure;
	}

	/**
	 * @see oscilloscup.data.DataElement#translate(double, double)
	 */
	public void translate(double x, double y)
	{
		for (Point p : pointList)
		{
			p.translate(x, y);
		}

		for (Figure p : figureList)
		{
			p.translate(x, y);
		}
	}

	/**
	 * @see oscilloscup.data.DataElement#draw(Space)
	 */
	public void draw(Space space)
	{
		super.draw(space);
		int pointCount = getPointCount();

		for (int i = 0; i < pointCount; ++i)
		{
			getPointAt(i).draw(space);
		}

		int figureCount = getFigureCount();

		for (int i = 0; i < figureCount; ++i)
		{
			getFigureAt(i).draw(space);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("# points: " + pointList.toString());
		buf.append(", figures: " + figureList.toString() + '#');
		return buf.toString();
	}

	public static class Extremi
	{

		public double minX, minY, maxX, maxY;
		public int nbPoints = 0;

		@Override
		public String toString()
		{
			return "Extremi [minX=" + minX + ", minY=" + minY + ", maxX=" + maxX
					+ ", maxY=" + maxY + "]";
		}

		public Object clone()
		{
			Extremi clone = new Extremi();
			clone.minX = minX;
			clone.minY = minY;
			clone.maxX = maxX;
			clone.maxY = maxY;
			return clone;
		}
	}

	private void firePointInserted(Figure figure, Point point, int position)
	{
		Collection<?> listeners = getListeners();

		if (listeners != null)
		{
			Iterator<?> i = listeners.iterator();

			while (i.hasNext())
			{
				((FigureListener) i.next()).pointInserted(figure, point, position);
			}
		}
	}

	private void firePointRemoved(Figure figure, Point point, int position)
	{
		Collection<?> listeners = getListeners();

		if (listeners != null)
		{
			Iterator<?> i = listeners.iterator();

			while (i.hasNext())
			{
				((FigureListener) i.next()).pointRemoved(figure, point, position);
			}
		}
	}

	private void fireFigureInserted(Figure figure, Figure subFigure, int position)
	{
		Collection listeners = getListeners();

		if (listeners != null)
		{
			Iterator i = listeners.iterator();

			while (i.hasNext())
			{
				((FigureListener) i.next()).figureInserted(figure, subFigure, position);
			}
		}
	}

	private void fireFigureRemoved(Figure figure, Figure subFigure, int position)
	{
		Collection listeners = getListeners();

		if (listeners != null)
		{
			Iterator i = listeners.iterator();

			while (i.hasNext())
			{
				((FigureListener) i.next()).figureRemoved(figure, subFigure, position);
			}
		}
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param string
	 */
	public void setName(String string)
	{
		if (name == null)
			throw new NullPointerException("null name is not allowed");

		name = string;
	}

	public List<Figure> findAllFiguresRecursively()
	{
		List<Figure> list = new ArrayList<Figure>();
		list.add(this);

		// the list grows all along the looping
		for (int i = 0; i < list.size(); ++i)
		{
			list.addAll(list.get(i).figureList);
		}

		return list;
	}

	public List<Point> findAllPointsRecursively()
	{
		List<Point> points = new ArrayList<Point>();

		for (Figure f : findAllFiguresRecursively())
		{
			points.addAll(f.pointList);
		}

		return points;
	}

	public void sortX()
	{
		Collections.sort(pointList, new Comparator()
		{
			public int compare(Object p1, Object p2)
			{
				return Double.compare(((Point) p1).getX(), ((Point) p2).getX());
			}
		});
	}

	public void retainsOnlyLastPoints(int n)
	{
		while (getPointCount() > n)
		{
			removePointAt(0);
		}
	}

	public void setColor(Color c)
	{
		int sz = getRendererCount();

		for (int i = 0; i < sz; ++i)
		{
			getRendererAt(i).setColor(c);
		}
	}
}
