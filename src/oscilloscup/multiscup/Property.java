/////////////////////////////////////////////////////////////////////////////////////////
// 
//                 Université de Nice Sophia-Antipolis  (UNS) - 
//                 Centre National de la Recherche Scientifique (CNRS)
//                 Copyright © 2015 UNS, CNRS All Rights Reserved.
// 
//     These computer program listings and specifications, herein, are
//     the property of Université de Nice Sophia-Antipolis and CNRS
//     shall not be reproduced or copied or used in whole or in part as
//     the basis for manufacture or sale of items without written permission.
//     For a license agreement, please contact:
//     <mailto: licensing@sattse.com> 
//
//
//
//     Author: Luc Hogie – Laboratoire I3S - luc.hogie@unice.fr
//
//////////////////////////////////////////////////////////////////////////////////////////

package oscilloscup.multiscup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oscilloscup.data.Point;
import oscilloscup.data.rendering.DataElementRenderer;
import oscilloscup.data.rendering.figure.BSplineFigureRenderer;

public abstract class Property<E>
{
	public static String NULL_VALUE_VISIBLE_NAME = "null";

	private String name;
	private String unit;
	private PropertyValueFormatter formatter;
	private Double min, max;
	private Clock clock = Clock.systemClockInSeconds.createElapsedClock();

	private DataElementRenderer renderer;

	public Property()
	{
	}

	public Property(String name, String unit)
	{
		setName(name);
		setUnit(unit);
	}

	public PropertyValueFormatter getFormatter()
	{
		return formatter;
	}

	public void setFormatter(PropertyValueFormatter formatter)
	{
		this.formatter = formatter;
	}

	public Double getMin()
	{
		return min;
	}

	public void setPlotBounds(Double min, Double max)
	{
		if (min != null && max != null && min >= max)
			throw new IllegalArgumentException("min >= max");

		this.min = min;
		this.max = max;
	}

	public Double getMax()
	{
		return max;
	}

	public Clock getClock()
	{
		return clock;
	}

	public void setClock(Clock clock)
	{
		if (clock == null)
			clock = Clock.systemClockInSeconds.createElapsedClock();

		this.clock = clock;
	}

	public PropertyValueFormatter getReformatter()
	{
		return formatter;
	}

	public void setReformatter(PropertyValueFormatter reformatter)
	{
		this.formatter = reformatter;
	}

	public String getHumanReadableNameAndUnit()
	{
		return getHumanReadableName() + (unit == null ? "" : " (" + unit + ")");
	}

	public String getHumanReadableName()
	{
		if (name == null)
			return "No name";

		String r = name.substring(0, 1).toLowerCase();

		for (int i = 1; i < name.length(); ++i)
		{
			char c = name.charAt(i);
			char p = name.charAt(i - 1);
			boolean sep = false;

			if (Character.isLetter(p) && Character.isLetter(c))
			{
				if (Character.isLowerCase(p) && Character.isUpperCase(c))
				{
					sep = true;
				}
			}
			else if (Character.isLetter(p) && Character.isDigit(c))
			{
				sep = true;
			}
			else if (Character.isDigit(p) && Character.isLetter(c))
			{
				sep = true;
			}

			if (sep)
			{
				r += ' ';
			}

			r += Character.toLowerCase(c);
		}

		return r;
	}

	public final String getFormattedValue(E target)
	{
		Object rawValue = getRawValue(target);

		if (formatter == null)
		{
			return rawValue.toString();
		}
		else
		{
			if (rawValue == null)
			{
				return NULL_VALUE_VISIBLE_NAME;
			}
			else
			{
				return formatter.format(rawValue);
			}
		}
	}

	public abstract Object getRawValue(E target);

	public static List<String> getNames(List<? extends Property> props)
	{
		List<String> r = new ArrayList<>();

		for (Property p : props)
		{
			r.add(p.name);
		}

		return r;
	}

	public Point getPoint(E target)
	{
		Object value = getRawValue(target);

		if (value == null)
		{
			return null;
		}
		else
		{
			try
			{
				double v = convertToDouble(value.toString());
				return new Point(getClock().getTime(), v);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
	}

	protected double convertToDouble(String s)
	{
		try
		{
			return Double.valueOf(s);
		}
		catch (NumberFormatException e)
		{
			if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes"))
			{
				return 1;
			}
			else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("no"))
			{
				return 0;
			}
		}

		throw new NumberFormatException(s);
	}

	public static <E> Set<String> findUnits(List<Property<E>> props)
	{
		Set<String> r = new HashSet<>();

		for (Property<E> p : props)
		{
			r.add(p.unit);
		}

		return r;
	}

	public static <E> Set<Set<Property<E>>> findPropertyByUnitAndClock(
			List<Property<E>> props)
	{
		Map<String, Set<Property<E>>> m = new HashMap<>();

		for (Property<E> p : props)
		{
			String hash = p.getClock().hashCode() + ":"
					+ (p.getUnit() == null ? "" : p.getUnit().hashCode());
			Set<Property<E>> set = m.get(hash);

			if (set == null)
			{
				m.put(hash, set = new HashSet<>());
			}

			set.add(p);
		}

		return new HashSet<Set<Property<E>>>(m.values());
	}

	public static <E> Property<E> findPropertyByName(List<Property<E>> props,
			String propertyName)
	{
		for (Property<E> p : props)
		{
			if ((p.getName() == null && propertyName == null)
					|| (p.getName() != null && p.getName().equals(propertyName)))
			{
				return p;
			}
		}

		return null;
	}

	public static <E> void addProperty(List<Property<E>> props, Property<E> p)
	{
		if (getNames(props).contains(p.name))
			throw new IllegalArgumentException(
					"property " + p.getName() + " is already in");

		props.add(p);
	}

	public static <E> void removeProperty(List<Property<E>> props, String propertyName)
	{
		props.remove(Property.findPropertyByName(props, propertyName));
	}

	public String getName()
	{
		return name;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String u)
	{
		this.unit = u;
	}

	static Set<Class> acceptedTypes = new HashSet<>(Arrays.asList(byte.class, Byte.class,
			int.class, Integer.class, long.class, Long.class, double.class, Double.class,
			Float.class, float.class, Short.class, short.class));

	public static <E> List<Property<E>> findAllProperties(Class<E> c)
	{
		List<Property<E>> r = new ArrayList<>();

		for (InFieldProperty<E> p : InFieldProperty.findInFieldPropertiesIn(c))
		{
			Property.addProperty(r, p);
		}

		for (InMethodProperty<E> p : InMethodProperty.findInMethodPropertiesIn(c))
		{
			Property.addProperty(r, p);
		}

		return r;
	}

	public static <E> Property<E> getProperty(Class<E> c, String name)
	{
		for (Property<E> p : findAllProperties(c))
		{
			if (p.getName().equals(name))
			{
				return p;
			}
		}

		return null;
	}

	public Double getMinYDisplayed()
	{
		return min;
	}

	public Double getMaxYDisplayed()
	{
		return max;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public DataElementRenderer createRenderer()
	{
		return new BSplineFigureRenderer();
	}

}
