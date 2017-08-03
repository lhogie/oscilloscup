package oscilloscup.multiscup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InMethodProperty<E> extends Property<E>
{
	private final Method method;

	public InMethodProperty(Method method)
	{
		this.method = method;
		setName(methodName2propertyName(method.getName()));
	}

	@Override
	public Object getRawValue(E target)
	{
		try
		{
			return method.invoke(target);
		}
		catch (IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public static <E> List<InMethodProperty<E>> findInMethodPropertiesIn(Class<E> c)
	{
		List<InMethodProperty<E>> r = new ArrayList<>();

		for (Method m : c.getDeclaredMethods())
		{
			if (m.getParameterCount() == 0
					&& (Property.acceptedTypes.contains(m.getReturnType())))
			{
				m.setAccessible(true);
				r.add(new InMethodProperty<>(m));
			}
		}

		return r;
	}

	private static String methodName2propertyName(String m)
	{
		if (m.startsWith("get"))
		{
			m = m.substring(3);
		}

		if (Character.isUpperCase(m.charAt(0)))
		{
			m = Character.toLowerCase(m.charAt(0)) + m.substring(1);
		}

		return m;
	}
}
