package edu.rpi.phil.legup.newgui;

import edu.rpi.phil.legup.newgui.DynamicViewer;
import edu.rpi.phil.legup.BoardDrawingHelper;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Legup;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.newgui.LEGUP_Gui;
import edu.rpi.phil.legup.Selection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.util.Vector;

public class CaseRuleSelectionHelper extends DynamicViewer implements ActionListener
{
	public int mode = MODE_TILE;
	public static final int MODE_TILE = 0;
	public static final int MODE_COL_ROW = 1;
	public static final int MODE_TILETYPE = 2;
	Vector<Integer> tileTypes = null; //whitelist of allowed tiles for MODE_TILETYPE
	public Point pointSelected = new Point(-5,-5);
	public boolean allowLabels = Legup.getInstance().getPuzzleModule().hasLabels();
	private LEGUP_Gui parent = null;
	
	CaseRuleSelectionHelper(LEGUP_Gui gui)
	{
		parent = gui;
		setPreferredSize(new Dimension(600,400));
		setBackground(new Color(0xE0E0E0));
		setSize(getProperSize());
		zoomFit();
		zoomTo(1.0);
		tileTypes = null;
		pointSelected.x = -5;
		pointSelected.y = -5;
	}

	private Dimension getProperSize()
	{
		Dimension rv = new Dimension();
		Selection selection = Legup.getInstance().getSelections().getFirstSelection();

		if (selection.isState())
		{
			BoardState state = selection.getState();
			PuzzleModule pz = Legup.getInstance().getPuzzleModule();

			if (pz != null)
			{
				Dimension d = pz.getImageSize();
				int w  = state.getWidth();
				int h = state.getHeight();

				rv.width = d.width * (w + 2);
				rv.height = d.height * (h + 2);
			}
		}

		return rv;
	}
	
	protected void draw( Graphics2D g )
	{
		BoardDrawingHelper.draw(g,pointSelected,mode);
	}
	
	protected void mousePressedAt(Point p, MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			BoardState state = Legup.getCurrentState();
			PuzzleModule pm = Legup.getInstance().getPuzzleModule();
			Dimension d = pm.getImageSize();
			
			int imW = d.width;
			int imH = d.height;
			int w  = state.getWidth();
			int h = state.getHeight();

			p.x -= imW;
			p.y -= imH;

			p.x = (int)(Math.floor((double)p.x/imW));
			p.y = (int)(Math.floor((double)p.y/imH));
			
			if((p.x < -1)||(p.x > w)||(p.y < -1)||(p.y > h)) //don't allow out of bounds
			{
				p.x = -5;
				p.y = -5;
			}
			if(((p.x == -1)||(p.x == w))&&((p.y == -1)||(p.y == h))) //don't allow corners (with no label)
			{
				p.x = -5;
				p.y = -5;
			}
			if((!allowLabels)||(mode != MODE_COL_ROW))
			{
				if((p.x == -1)||(p.x == w)||(p.y == -1)||(p.y == h))
				{
					p.x = -5;
					p.y = -5;
				}
			}
			if(!((p.x == -5)&&(p.y == -5)))
			{
				if(mode == MODE_TILE)
				{
					if(!state.isModifiableCell(p.x,p.y))
					{
						p.x = -5;
						p.y = -5;
					}
				}
				if(mode == MODE_TILETYPE)
				{
					if(tileTypes != null)
					{
						int current_cell = state.getCellContents(p.x,p.y); 
						if(!tileTypes.contains(current_cell))
						{
							p.x = -5;
							p.y = -5;
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null,"The tile type whitelist is null.");
						p.x = -5;
						p.y = -5;
					}
				}
			}
			if(p.x == w)p.x = -1;
			if(p.y == h)p.y = -1;
			if(mode == MODE_COL_ROW)
			{
				if((p.x != -1)&&(p.y != -1))
				{
					p.x = -5;
					p.y = -5;
				}
			}
			
			pointSelected.x = p.x;
			pointSelected.y = p.y;
		}
		repaint();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		
	}
}