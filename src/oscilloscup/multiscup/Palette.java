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

import toools.gui.ListColorPalette;

public class Palette extends ListColorPalette
{
	public static Palette defaultPalette = new Palette();

	int counter = 0;

	public Palette()
	{
		getColorList().add(Color.blue);
		getColorList().add(Color.red);
		getColorList().add(Color.green);
		getColorList().add(Color.pink);
		getColorList().add(Color.black);
		getColorList().add(Color.cyan);
		getColorList().add(Color.magenta);
		getColorList().add(Color.gray);
		getColorList().add(Color.yellow);
	}

	public void reset()
	{
		counter = 0;
	}

	public Color getNextColor()
	{
		return getColor(counter++ % getNumberOfColors());
	}

}
