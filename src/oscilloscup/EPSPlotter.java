package oscilloscup;


import java.io.ByteArrayOutputStream;

import java.io.IOException;

import org.jibble.epsgraphics.EpsGraphics2D;

/**
 * The user may want to get an image object or the data of an
 * image file (PNG, JPEG, SVG...) for, for instance, return it to a 
 * HTTP-client that will have to draw the image on the web page
 * it will show.
 * 
 * @author Luc Hogie
 */
public class EPSPlotter extends DelegPlotter
{

	public byte[] getEPSData( int width, int height )
	{
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			EpsGraphics2D g = new EpsGraphics2D(null, outputStream, 0, 0, width, height);
			g.setClip(0, 0, width, height);
			getGraphics2DPlotter().paint(g);
			g.flush();
			g.close();
			outputStream.close();
			return outputStream.toByteArray();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			throw new IllegalStateException("bug");
		}
	}


}
