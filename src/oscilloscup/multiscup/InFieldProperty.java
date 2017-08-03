package oscilloscup.multiscup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InFieldProperty<E> extends Property<E>
{
	private final Field field;

	public InFieldProperty(Field field)
	{
		this.field = field;
		setName(field.getName());
	}

	@Override
	public Object getRawValue(E target)
	{
		try
		{
			return field.get(target);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public static <E> List<InFieldProperty<E>> findInFieldPropertiesIn(Class<E> c)
	{
		List<InFieldProperty<E>> r = new ArrayList<>();

		for (Field f : c.getDeclaredFields())
		{
			if (Property.acceptedTypes.contains(f.getType()))
			{
				f.setAccessible(true);
				r.add(new InFieldProperty<>(f));
			}
		}

		return r;
	}

}
