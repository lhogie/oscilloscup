package oscilloscup.multiscup.demo.mortgage;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import oscilloscup.data.rendering.DataElementRenderer;
import oscilloscup.multiscup.Clock;
import oscilloscup.multiscup.Multiscope;
import oscilloscup.multiscup.Property;

public class Demo
{
	static int mois = 0;

	public static void main(String[] args)
	{
		Clock clock = new Clock()
		{
			@Override
			public double getTime()
			{
				return mois;
			}

			@Override
			public String getTimeUnit()
			{
				return "month";
			}
		};

		List<Property<BankSimulation>> props = Property
				.findAllProperties(BankSimulation.class);
		System.out.println(Property.getNames(props));
		Property.removeProperty(props, "prixDuBien");
		Property.findPropertyByName(props, "enBanque").setUnit("€");
		Property.findPropertyByName(props, "enBanque").setClock(clock);
		Property.findPropertyByName(props, "resteARembourser").setUnit("€");
		Property.findPropertyByName(props, "apport").setUnit("€");
		Property.findPropertyByName(props, "mensualite").setUnit("€");
		Property.findPropertyByName(props, "tauxEpargne").setUnit("%");
		Property.findPropertyByName(props, "gainEpargneTotal").setUnit("€");

		List<BankSimulation> simulations = new ArrayList<>();
		simulations.add(new BankSimulation("BNP", 129000, 1170, 900, 527));
		simulations.add(new BankSimulation("Banque postale", 123000, 896, 0, 800));
		simulations.add(new BankSimulation("caisse d'épargne", 123000, 921, 300, 0));

		Multiscope<BankSimulation> pane = new Multiscope<BankSimulation>(props)
		{
			@Override
			protected String getRowNameFor(BankSimulation e)
			{
				return e.name;
			}

			@Override
			protected int getNbPointsInSlidingWindow(BankSimulation row,
					Property<BankSimulation> p)
			{
				return 20000;
			}

			@Override
			protected DataElementRenderer getSpecificRenderer(BankSimulation row,
					Property<BankSimulation> property)
			{
				return null;
			}

		};

		pane.setRows(simulations);

		JFrame f = new JFrame();
		f.setSize(f.getMaximumSize());

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{

					if (mois == 86)
						break;

					// Threads.sleepMs(100);

					for (BankSimulation s : simulations)
					{
						s.change(clock.getTime());
					}

					pane.newStep();
					++mois;
					System.out.println("mois=" + mois);
				}

				System.out.println("done");
			}
		}).start();

		f.setContentPane(pane);
		f.setVisible(true);
	}
}
