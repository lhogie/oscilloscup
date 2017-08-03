package oscilloscup.system;

/**
 * <p>
 * The space object is made of (and itself is) bounded by numeric values.
 * </p>
 * 
 * <p>
 * The mathematical concept of space does not talk about an interval but here we
 * have to render the graphical representation of the space and so it is no
 * possible to consider the whole space: we want to display only the interval
 * required by the user.
 * </p>
 * 
 * <p>
 * A bounded graphical element is a graphical element plus the conception of
 * interval. This is used only for rendering. This class only brings the concept
 * of interval used for painting.
 * </p>
 * 
 * <p>
 * Knowing that the interval may be automatically deduced from the user data or
 * even from the parent bounded graphical elements, we assume that a bounded
 * graphical element can be <i>auto bounded</i>.
 * </p>
 * 
 * <p>
 * Note: the bounds values are inclusive (ex: [-1, 1])
 * </p>
 * 
 * @author Luc Hogie
 */

public class BoundedSpaceElement extends SpaceElement
{
	private double min = - 10, max = 10;
	private boolean minimumIsAutomatic = true;
	private boolean maximumIsAutomatic = true;

	public boolean isMaximumAutomatic()
	{
		return maximumIsAutomatic;
	}

	public void setMaximumIsAutomatic(boolean maximumIsAutomatic)
	{
		this.maximumIsAutomatic = maximumIsAutomatic;
	}

	public boolean isMinimumAutomatic()
	{
		return minimumIsAutomatic;
	}

	public void setMinimumIsAutomatic(boolean minimumIsAutomatic)
	{
		this.minimumIsAutomatic = minimumIsAutomatic;
	}

	/**
	 * @return the minimum value for the bounds. This cannot be lower than the
	 *         maximum value.
	 */
	public double getMin()
	{
		return min;
	}

	/**
	 * @return the maximum value for the bounds. This cannot be greater than the
	 *         minimum value.
	 */
	public double getMax()
	{
		return max;
	}

	/**
	 * Sets the bounds. Obviously, <i>min</i> must be lower than <i>max</i>.
	 */
	public void setBounds(double min, double max)
	{
		if (max == min)
			throw new IllegalArgumentException("max == min");

		if (max < min)
			throw new IllegalArgumentException("max < min");

		this.min = min;
		this.max = max;
	}

	/**
	 * @return if the graphical element is visible at the given position.
	 */
	public boolean isVisibleAt(double position)
	{
		if (isVisible())
		{
			return getMin() <= position && position <= getMax();
		}
		else
		{
			return false;
		}
	}
}