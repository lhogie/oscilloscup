package oscilloscup.multiscup;

public interface PropertyValueFormatter
{

	String format(Object rawValue);

	public static final PropertyValueFormatter noFormatting = new PropertyValueFormatter()
	{
		@Override
		public String format(Object rawValue)
		{
			return rawValue.toString();
		}
	};

	public static class PrettyDecimals implements PropertyValueFormatter
	{
		private final int nbDecimals;

		public PrettyDecimals(int nbDecimals)
		{
			this.nbDecimals = nbDecimals;
		}

		@Override
		public String format(Object rawValue)
		{
			if (rawValue instanceof Number)
			{
				double d = ((Number) rawValue).doubleValue();
				double factor = Math.pow(10, nbDecimals);
				d = ((int) (d * factor)) / factor;

				if (d == (int) d)
				{
					return String.valueOf((int) d);
				}
				else
				{
					return String.valueOf(d);
				}
			}
			else
			{
				return rawValue.toString();
			}
		}

	}

	public static class Kilos implements PropertyValueFormatter
	{
		private final int nbDecimals;

		public Kilos(int nbDecimals)
		{
			this.nbDecimals = nbDecimals;
		}

		@Override
		public String format(Object rawValue)
		{
			double d = ((Number) rawValue).doubleValue();

			return String.valueOf(d / 1000);
		}
	}
}
