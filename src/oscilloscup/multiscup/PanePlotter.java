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

import oscilloscup.SwingPlotter;
import oscilloscup.data.Figure;
import oscilloscup.system.Space;

class PanePlotter extends SwingPlotter
{
	PanePlotter()
	{
		setSize(getPreferredSize());
		Space s = getGraphics2DPlotter().getSpace();

		// does not allow negative values
		// s.getXDimension().setMin(0);
		// s.getYDimension().setMin(0);

		// hide arrows

		//
		s.getXDimension().getOriginAxis().setVisible(false);
		s.getYDimension().getOriginAxis().setVisible(false);
		s.getYDimension().getLowerBoundAxis().getLine().getArrow().setVisible(false);
		s.getYDimension().getUpperBoundAxis().getLine().getArrow().setVisible(false);
		s.getXDimension().getUpperBoundAxis().getLine().getArrow().setVisible(false);
		s.getXDimension().getLowerBoundAxis().getLine().getArrow().setVisible(false);

		// p.getGraphics2DPlotter().getSpace().getYDimension().getLowerBoundAxis().setVisible(false);

		getGraphics2DPlotter().getSpace().getYDimension().getLegend().setText("");
		getGraphics2DPlotter().getSpace().getXDimension().getLegend().setText("");

		getGraphics2DPlotter().setFigure(new Figure());
	}
}
