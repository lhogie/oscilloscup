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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oscilloscup.SwingPlotter;
import oscilloscup.data.Figure;
import oscilloscup.data.Point;
import oscilloscup.data.rendering.DataElementRenderer;
import toools.thread.Threads;

public abstract class Multiscope<E> extends JPanel
{
	private Palette palette = Palette.defaultPalette;
	private List<E> rows;
	private final List<Property<E>> displayedProperties;

	private final Map<E, Map<Property<E>, Figure>> row_figures = new HashMap<>();
	private final Map<Property<E>, PanePlotter> property_plotter = new HashMap<>();
	private final Map<String, PanePlotter> unit_plotter = new HashMap<>();

	private final SPTable<E> table;

	private final PlotterSetPanel propertiesPlottersPanel = new PlotterSetPanel();
	private final PlotterSetPanel unitsPlottersPanel = new PlotterSetPanel();
	private RefreshThread refreshThread;

	private int refreshPeriodMs = - 1;

	public Multiscope(Class<E> c)
	{
		this(Property.findAllProperties(c));
	}

	public Multiscope(List<Property<E>> props)
	{
		super();

		if (props == null)
			throw new NullPointerException("null property list");

		if (props.contains(null))
			throw new NullPointerException(
					"I found a null property in the property list");

		this.displayedProperties = new ArrayList<>(props);

		table = new SPTable<E>(props, new TableSelectionHandler());
		table.setColorPalette(palette);

		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, table.createScrollPane());

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Properties", propertiesPlottersPanel);
		tabbedPane.addTab("Units", unitsPlottersPanel);
		tabbedPane.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateVisiblePlotPanels();
			}
		});

		add(BorderLayout.CENTER, tabbedPane);
	}

	public int getRefreshPeriodMs()
	{
		return refreshPeriodMs;
	}

	public void setRefreshPeriodMs(int rp)
	{
		if (rp != refreshPeriodMs)
		{
			this.refreshPeriodMs = rp;

			// ensureRefreshThreadIsStopped
			if (refreshThread != null)
			{
				refreshThread.shouldStop = true;
				refreshThread = null;
			}

			// if the refresh period is valid
			if (refreshPeriodMs >= 0)
			{
				// start refresh tread
				this.refreshThread = new RefreshThread();
				Threads.sleepMs(refreshPeriodMs);
				this.refreshThread.start();
			}
		}
	}

	public void setRows(E... rows)
	{
		setRows(Arrays.asList(rows));
	}

	public void setRows(List<E> rows)
	{
		this.rows = new ArrayList<>(rows);
		table.setModel(rows);

		for (int rowindex = 0; rowindex < rows.size(); ++rowindex)
		{
			E row = rows.get(rowindex);

			// set the row labels in the table
			table.getModel().setValueAt(getRowNameFor(row), rowindex, 0);

			// fills the table with the row. The renderer will extract the
			// appropriate value for each cell
			for (int propertyIndex = 0; propertyIndex < displayedProperties
					.size(); ++propertyIndex)
			{
				table.getModel().setValueAt(row, rowindex, propertyIndex + 1);
			}
		}

		for (Property<E> p : displayedProperties)
		{
			PanePlotter plotter = new PanePlotter();

			double min = p.getMinYDisplayed() == null
					? plotter.getGraphics2DPlotter().getSpace().getYDimension().getMin()
					: p.getMinYDisplayed();
			plotter.getGraphics2DPlotter().getSpace().getYDimension()
					.setMinimumIsAutomatic(p.getMinYDisplayed() == null);

			double max = p.getMaxYDisplayed() == null
					? plotter.getGraphics2DPlotter().getSpace().getYDimension().getMax()
					: p.getMaxYDisplayed();
			plotter.getGraphics2DPlotter().getSpace().getYDimension()
					.setMaximumIsAutomatic(p.getMaxYDisplayed() == null);

			plotter.getGraphics2DPlotter().getSpace().getYDimension().setBounds(min, max);

			plotter.getGraphics2DPlotter().getSpace().getLegend()
					.setText(p.getHumanReadableNameAndUnit());
			plotter.getGraphics2DPlotter().getSpace().getYDimension().getLegend()
					.setText(p.getHumanReadableNameAndUnit());
			plotter.getGraphics2DPlotter().getSpace().getXDimension().getLegend()
					.setText("time (" + p.getClock().getTimeUnit() + ")");
			property_plotter.put(p, plotter);
		}

		for (Set<Property<E>> set : Property
				.findPropertyByUnitAndClock(displayedProperties))
		{
			Property<E> aProperty = set.iterator().next();
			String unit = aProperty.getUnit();
			Clock clock = aProperty.getClock();

			PanePlotter plotter = new PanePlotter();
			plotter.getGraphics2DPlotter().getSpace().getLegend().setText(null);
			plotter.getGraphics2DPlotter().getSpace().getYDimension().getLegend()
					.setText(unit);
			plotter.getGraphics2DPlotter().getSpace().getXDimension().getLegend()
					.setText("time " + (clock.getTimeUnit() == null ? ""
							: "(" + clock.getTimeUnit() + ")"));
			unit_plotter.put(unit, plotter);
		}

		for (E row : rows)
		{
			row_figures.put(row, new HashMap<Property<E>, Figure>());

			for (Property<E> p : displayedProperties)
			{
				Figure columnFigure = new Figure();

				columnFigure.addRenderer(p.createRenderer());
				// columnFigure.addRenderer(new ConnectedLineFigureRenderer());
				row_figures.get(row).put(p, columnFigure);
			}
		}
	}

	protected abstract String getRowNameFor(E row);

	synchronized public void newStep()
	{
		for (E row : rows)
		{
			Map<Property<E>, Figure> prop_fig = row_figures.get(row);

			for (Property<E> property : displayedProperties)
			{
				Figure f = prop_fig.get(property);
				Point point = property.getPoint(row);

				if (point != null)
				{
					if (f.getPointCount() == 0)
					{
						f.addPoint(point);
					}
					else
					{
						Point lastPoint = f.getPointAt(f.getPointCount() - 1);

						if ( ! point.equals(lastPoint))
						{
							f.addPoint(point);

							f.retainsOnlyLastPoints(
									getNbPointsInSlidingWindow(row, property));
						}
					}
				}
			}
		}

		table.repaint();

		if (propertiesPlottersPanel.isVisible())
		{
			for (SwingPlotter p : property_plotter.values())
			{
				p.repaint();
			}
		}

		if (unitsPlottersPanel.isVisible())
		{
			for (SwingPlotter p : unit_plotter.values())
			{
				p.repaint();
			}
		}
	}

	protected int getNbPointsInSlidingWindow(E row, Property<E> p)
	{
		return 20;
	}

	class TableSelectionHandler implements ListSelectionListener
	{
		@Override
		synchronized public void valueChanged(ListSelectionEvent e)
		{
			// if ( ! e.getValueIsAdjusting())
			{
				updateVisiblePlotPanels();
			}
		}
	}

	private class RefreshThread extends Thread
	{
		public boolean shouldStop;

		@Override
		public void run()
		{
			setName("Multiscope refresh");

			while ( ! shouldStop)
			{
				newStep();
				Threads.sleepMs(refreshPeriodMs);
			}
		}
	}

	public void updateVisiblePlotPanels()
	{
		if (propertiesPlottersPanel.isVisible())
		{
			propertiesPlottersPanel.setPlotters(getPropertiesPlottersToShow());
			propertiesPlottersPanel.doLayout();
		}

		if (unitsPlottersPanel.isVisible())
		{
			unitsPlottersPanel.setPlotters(getUnitsPlottersToShow());
			unitsPlottersPanel.doLayout();
		}
	}

	private Set<SwingPlotter> getPropertiesPlottersToShow()
	{
		Set<SwingPlotter> plottersToShow = new HashSet<>();

		for (int columnindex : table.getSelectedColumns())
		{
			if (columnindex > 0)
			{
				Property<E> property = displayedProperties.get(columnindex - 1);
				SwingPlotter plotter = property_plotter.get(property);
				plottersToShow.add(plotter);
				plotter.getGraphics2DPlotter().getFigure().removeAllFigures();

				for (int rowindex : table.getSelectedRows())
				{
					E row = rows.get(rowindex);
					Map<Property<E>, Figure> property_figure = row_figures.get(row);
					Figure propertyFigure = property_figure.get(property);
					propertyFigure.setName(getRowNameFor(row));

					DataElementRenderer sr = getSpecificRenderer(row, property);

					if (sr != null)
					{
						propertyFigure.removeAllRenderers();
						propertyFigure.addRenderer(sr);
					}
					
					propertyFigure.setColor(palette.getColor(
							plotter.getGraphics2DPlotter().getFigure().getFigureCount()
									% palette.getNumberOfColors()));
					plotter.getGraphics2DPlotter().getFigure().addFigure(propertyFigure);
				}
			}
		}
		return plottersToShow;

	}

	protected abstract DataElementRenderer getSpecificRenderer(
			E row, Property<E> property);

	private Set<SwingPlotter> getUnitsPlottersToShow()
	{
		Set<SwingPlotter> plottersToShow = new HashSet<>();

		for (PanePlotter plotter : unit_plotter.values())
		{
			plotter.getGraphics2DPlotter().getFigure().removeAllFigures();
		}

		for (int columnindex : table.getSelectedColumns())
		{
			if (columnindex > 0)
			{
				Property<E> property = displayedProperties.get(columnindex - 1);

				SwingPlotter plotter = unit_plotter.get(property.getUnit());
				plottersToShow.add(plotter);

				for (int r : table.getSelectedRows())
				{
					E n = rows.get(r);
					Map<Property<E>, Figure> property_figure = row_figures.get(n);
					Figure f = property_figure.get(property);
					f.setColor(palette.getColor(
							plotter.getGraphics2DPlotter().getFigure().getFigureCount()
									% palette.getNumberOfColors()));
					f.setName(getRowNameFor(n) + ":" + property.getHumanReadableName());
					plotter.getGraphics2DPlotter().getFigure().addFigure(f);
				}
			}
		}

		return plottersToShow;
	}

	public List<E> getRows()
	{
		return rows;
	}
}
