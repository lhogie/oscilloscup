package oscilloscup.multiscup;

public abstract class Clock
{
	public abstract double getTime();

	public abstract String getTimeUnit();

	public static final Clock systemClockInSeconds = new Clock()
	{

		@Override
		public double getTime()
		{
			return System.currentTimeMillis() / 1000;
		}

		@Override
		public String getTimeUnit()
		{
			return "s";
		}
	};

	public Clock createElapsedClock()
	{
		final double startDate = getTime();

		return new Clock()
		{
			@Override
			public String getTimeUnit()
			{
				return Clock.this.getTimeUnit();
			}

			@Override
			public double getTime()
			{
				return Clock.this.getTime() - startDate;
			}
		};
	}

	@Override
	public String toString()
	{
		return getTime() + "" + getTimeUnit();
	};

}
