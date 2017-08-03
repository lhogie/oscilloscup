package oscilloscup.multiscup.demo.evolving_object;

import toools.thread.Threads;

class EvolvingObject
{
	static int n = 0;
	final int ID = n++;
	private double foo = 0;
	private double bar = 0;

	public EvolvingObject()
	{
		new Thread()
		{

			@Override
			public void run()
			{
				setName(getClass().getName());
				
				while (true)
				{
					foo += Math.random() - 0.5;

					if ((bar += 10 * Math.random()) > 100)
					{
						bar = - bar;
					}

					Threads.sleepMs(100);
				}

			}
		}.start();
	}
}