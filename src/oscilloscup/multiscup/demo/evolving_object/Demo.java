package oscilloscup.multiscup.demo.evolving_object;

import java.util.Arrays;

import javax.swing.JFrame;

import oscilloscup.data.rendering.DataElementRenderer;
import oscilloscup.data.rendering.figure.BooleansFigureRenderer;
import oscilloscup.data.rendering.figure.ConnectedLineFigureRenderer;
import oscilloscup.multiscup.Multiscope;
import oscilloscup.multiscup.Property;
import oscilloscup.multiscup.PropertyValueFormatter;

public class Demo
{
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setSize(800, 600);

		Property<EvolvingObject> multi = new Property<EvolvingObject>()
		{
			@Override
			public Object getRawValue(EvolvingObject target)
			{
				return target.hashCode() * target.ID;
			}
		};

		Property<EvolvingObject> ds = new Property<EvolvingObject>()
		{
			@Override
			public Object getRawValue(EvolvingObject target)
			{
				return 3;
			}
		};

		Property<EvolvingObject> boolprop = new Property<EvolvingObject>()
		{
			@Override
			public Object getRawValue(EvolvingObject target)
			{
				if (target.ID == 1)
				{
					return Math.random() < 0.5 ? true : false;
				}
				else
				{
					return (int) (Math.random() * 10);
				}

			}
		};
		boolprop.setName("int or bool");

		Property<EvolvingObject> foo = Property.getProperty(EvolvingObject.class, "foo");
		foo.setName("foo");
		foo.setReformatter(new PropertyValueFormatter.PrettyDecimals(2));
		foo.setPlotBounds(0d, null);
		Property<EvolvingObject> bar = Property.getProperty(EvolvingObject.class, "bar");
		bar.setPlotBounds(null, 0d);

		Multiscope<EvolvingObject> c = new Multiscope<EvolvingObject>(
				Arrays.asList(foo, bar, multi, ds, boolprop))
		{

			@Override
			protected String getRowNameFor(EvolvingObject e)
			{
				return String.valueOf(e.ID);
			}

			@Override
			protected int getNbPointsInSlidingWindow(EvolvingObject row,
					Property<EvolvingObject> p)
			{
				return 20;
			}

			@Override
			protected DataElementRenderer getSpecificRenderer(EvolvingObject row,
					Property<EvolvingObject> property)
			{
				if (row.ID == 1)
					return new BooleansFigureRenderer();

				return null;
			}
		};

		c.setRows(Arrays.asList(new EvolvingObject(), new EvolvingObject(),
				new EvolvingObject(), new EvolvingObject()));
		c.setRefreshPeriodMs(1000);
		f.setContentPane(c);
		f.setVisible(true);
	}
}
