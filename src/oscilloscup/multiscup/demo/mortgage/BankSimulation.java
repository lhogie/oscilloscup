package oscilloscup.multiscup.demo.mortgage;

import oscilloscup.multiscup.Changeable;

public class BankSimulation implements Changeable
{
	final static double prixDuBien = 223000;
	double enBanque = 230000;
	final double garantie;
	final double fraisDeDossier;

	double resteARembourser;
	final double apport;

	final double mensualite;
	double tauxEpargne = 2.5 / 100d;
	double revenusMensuels = 3000 + 840 + 129;
	double chargesMensuelles = 2200;

	double gainEpargneTotal = 0;
	final String name;

	public BankSimulation(String name, double apport, double mensualite,
			double fraisDeDossier, double garantie)
	{
		this.name = name;
		this.apport = apport;
		this.mensualite = mensualite;
		this.fraisDeDossier = fraisDeDossier;
		this.garantie = garantie;

		resteARembourser = prixDuBien - apport;
		enBanque -= apport;
		enBanque -= fraisDeDossier;
		enBanque -= garantie;
	}

	@Override
	public void change(double time)
	{
		if (time == 84)
		{
			enBanque -= resteARembourser;
			resteARembourser = 0;
		}
		else
		{
			enBanque += revenusMensuels;
			enBanque -= chargesMensuelles;

			double cetteMensualite = Math.min(mensualite, resteARembourser);
		//	enBanque -= cetteMensualite;

			resteARembourser -= cetteMensualite;

			double gainEpargneCeMois = enBanque * (tauxEpargne / 12d);
			gainEpargneTotal += gainEpargneCeMois;
			enBanque += gainEpargneCeMois;
		}
	}
}
