package oscilloscup.ui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import oscilloscup.system.Arrow;
import oscilloscup.system.Axis;
import oscilloscup.system.AxisLine;
import oscilloscup.system.Dimension;
import oscilloscup.system.Graduation;
import oscilloscup.system.Legend;
import oscilloscup.system.Space;



public class UpTreeCellRenderer extends DefaultTreeCellRenderer
{
	static Map iconMap;
	
	public UpTreeCellRenderer()
	{
		if (iconMap == null)
		{
			iconMap = new HashMap();
			iconMap.put(Arrow.class, new ImageIcon(Object.class.getResource("/res/arrow.gif")));
			iconMap.put(Space.class, new ImageIcon(Object.class.getResource("/res/aim.gif")));
			iconMap.put(Dimension.class, new ImageIcon(Object.class.getResource("/res/redBall.gif")));
			iconMap.put(AxisLine.class, new ImageIcon(Object.class.getResource("/res/greenBall.gif")));
			iconMap.put(Axis.class, new ImageIcon(Object.class.getResource("/res/blueBall.gif")));
			iconMap.put(Graduation.class, new ImageIcon(Object.class.getResource("/res/blueDiamond.gif")));
			iconMap.put(Legend.class, new ImageIcon(Object.class.getResource("/res/spiral.gif")));
		}
	}
	
	
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object node,
		boolean selected,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus)
	{
		Object object = ((DefaultMutableTreeNode) node).getUserObject();
		Icon icon = (Icon) iconMap.get(object.getClass());
		JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, node, selected, expanded, leaf, row, hasFocus);
		component.setIcon(icon);
		return component;
	}

}
