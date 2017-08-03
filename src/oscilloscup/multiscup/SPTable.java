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
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

class SPTable<E> extends JTable
{
	private Color bgColor = Color.white;
	private Color aColor = new Color(240, 240, 240);

	private final TableCellRenderer cellRenderer = new DefaultTableCellRenderer()
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object object,
				boolean isSelected, boolean hasFocus, int row, int column)
		{
			// column 0 is the name column, it can't be selected
			if (column == 0)
			{
				Component r = super.getTableCellRendererComponent(table, object, false,
						hasFocus, row, column);
				r.setBackground(bgColor);
				return r;
			}
			else
			{
				Property<E> property = properties.get(column - 1);
				String value = property.getFormattedValue((E) object);
				Component r = super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);

				if ( ! isSelected)
				{
					if ((column + row) % 2 == 0)
					{
						r.setBackground(aColor);
					}
					else
					{
						r.setBackground(bgColor);
					}
				}

				return r;
			}
		}
	};

	private Palette colorPalette;
	private final List<Property<E>> properties;

	public SPTable(List<Property<E>> props, ListSelectionListener tableSelectionHandler)
	{
		this.properties = props;
		getSelectionModel().addListSelectionListener(tableSelectionHandler);
		getColumnModel().getSelectionModel()
				.addListSelectionListener(tableSelectionHandler);

		setAutoCreateRowSorter(true);
		setCellSelectionEnabled(true);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(true);
	}

	public void setModel(List<E> rows)
	{
		setModel(new DefaultTableModel()
		{

			@Override
			public int getColumnCount()
			{
				// adds the name column
				return properties.size() + 1;
			}

			@Override
			public String getColumnName(int index)
			{
				return index == 0 ? "name"
						: properties.get(index - 1).getHumanReadableNameAndUnit();
			}

			@Override
			public boolean isCellEditable(int row, int col)
			{
				return false;
			}

			@Override
			public int getRowCount()
			{
				return rows.size();
			}
		});
	}

	public Palette getColorPalette()
	{
		return colorPalette;
	}

	public void setColorPalette(Palette colorPalette)
	{
		this.colorPalette = colorPalette;
	}

	@Override
	public Dimension getSize()
	{
		return new Dimension(getParent().getSize().width, 300);
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		return cellRenderer;
	}

	JScrollPane createScrollPane()
	{
		return new JScrollPane(this)
		{

			@Override
			public Dimension getPreferredSize()
			{
				Dimension tps = SPTable.this.getPreferredSize();
				return new Dimension(tps.width, tps.height + 25);
			}
		};
	}
}
