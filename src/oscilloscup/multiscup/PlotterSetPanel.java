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

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Set;

import javax.swing.JPanel;

import oscilloscup.SwingPlotter;

class PlotterSetPanel extends JPanel
{
	public PlotterSetPanel()
	{
		setBackground(Color.white);
		setOpaque(true);
	}

	public void setPlotters(Set<SwingPlotter> plottersToShow)
	{
		setVisible(false);
		removeAll();
		int sz = plottersToShow.size();

		if (sz > 0)
		{
			final int nbCols = (int) Math.sqrt(sz);
			int nbRows = nbCols;

			if (nbCols * nbRows < sz)
			{
				++nbRows;
			}

			setLayout(new GridLayout(nbRows, nbCols));

			for (SwingPlotter p : plottersToShow)
			{
				add(p);
			}
		}

		setVisible(true);
		doLayout();
	}

}
