/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of pdf-template.
 *
 * pdf-template is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * pdf-template is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pdf-template; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.pdf.template.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;

import com.moss.pdf.genericrenderer.GenericPdfException;
import com.moss.pdf.genericrenderer.RenderedPage;

public class PagePanel extends JPanel implements Scrollable {

	private static final long serialVersionUID = 1L;

	private Page page;
	private float zoom = 1;
	private RenderedPage rendered;
	
	private PageComponent selected = null;
	private PageComponent componentUnderMouse; // not-null when mouse is over a component
	private Point mouseOffset; // mouse's offset from top left corner of component rect
	
	private ComponentEditor componentEditor;
	
	private Rectangle2D.Float scale(Rectangle2D.Float r) {
		return new Rectangle2D.Float(
			r.x * zoom,
			r.y * zoom,
			r.width * zoom,
			r.height * zoom
		);
	}
	
	public PagePanel() {
		setLayout(new BorderLayout());
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			public void mouseMoved(MouseEvent e) {
				
				Point p = e.getPoint();
				
				
				Rectangle2D.Float scaledRect = null;
				
				for (PageComponent c : page) {
					
					scaledRect = scale(c.getRect());
					
					if (scaledRect.contains(p)) {
						selected = c;
						break;
					}
				}
				
				if (selected != null) {
					componentUnderMouse = selected;
					
					int x = -(p.x - (int)scaledRect.x);
					int y = -(p.y - (int)scaledRect.y);
					
					mouseOffset = new Point(x, y);
				}
				else {
					componentUnderMouse = null;
					mouseOffset = null;
				}

				repaint();
			}

			public void mouseDragged(MouseEvent e) {
				if (componentUnderMouse != null) {
					
					// where the cursor is at in visible coordinates
					Point p = new Point(e.getPoint());
					
					// where the top left corner of the components box should be in real coordinates
					Point2D.Float real = new Point2D.Float();
					real.x = (p.x + mouseOffset.x) / zoom;
					real.y = (p.y + mouseOffset.y) / zoom;
					
					componentUnderMouse.moveTo(real);
					repaint();
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				
				if (componentUnderMouse == null || e.getButton() <= 1) {
					return;
				}
				
				final PageComponent comp = componentUnderMouse;
				
				JMenuItem item = new JMenuItem("Delete");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						deleteComponent(comp);
					}
				});
				
				JMenuItem edit = new JMenuItem("Edit");
				edit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(componentEditor!=null)
							componentEditor.edit(comp);
					}
				});
				
				JPopupMenu menu = new JPopupMenu();
				menu.add(item);
				menu.add(edit);
				
				menu.show(PagePanel.this, e.getPoint().x, e.getPoint().y);
			}
		});
		
		
		setFocusable(true);
		requestFocusInWindow();
		addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
			}
				public void keyTyped(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()){
				case KeyEvent.VK_UP:
					shiftFocusUp();
					break;
				case KeyEvent.VK_DOWN:
					shiftFocusDown();
					break;
				case KeyEvent.VK_LEFT:
					shiftFocusLeft();
					break;
				case KeyEvent.VK_RIGHT:
					shiftFocusRight();
					break;
				}
				e.consume();
			}
		});
		requestFocusInWindow();
	}
	
	
	private void shiftFocusUp(){
		System.out.println("Up");
		if(selected!=null){
			System.out.println("Moving " + selected);
			Point2D location = new Point2D.Double(selected.getRect().x, selected.getRect().y-1);
			selected.moveTo(location);
			repaint();
		}
//			selected.
	}
	private void shiftFocusDown(){
		System.out.println("Down");
		if(selected!=null){
			System.out.println("Moving " + selected);
			Point2D location = new Point2D.Double(selected.getRect().x, selected.getRect().y+1);
			selected.moveTo(location);
			repaint();
		}

	}
	private void shiftFocusLeft(){
		System.out.println("Left");
		if(selected!=null){
			System.out.println("Moving " + selected);
			Point2D location = new Point2D.Double(selected.getRect().x-1, selected.getRect().y);
			selected.moveTo(location);
			repaint();
		}
	}
	private void shiftFocusRight(){
		System.out.println("Right");
		if(selected!=null){
			System.out.println("Moving " + selected);
			Point2D location = new Point2D.Double(selected.getRect().x+1, selected.getRect().y);
			selected.moveTo(location);
			repaint();
		}
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(final Page page) {
		PagePanel.this.page = page;
		rendered = null;
		refreshPreferredSize();
	}
	
	public void addComponent(final PageComponent component) {
		page.add(component);
		refreshPreferredSize();
	}
	
	public void deleteComponent(final PageComponent component) {
		page.remove(component);
		refreshPreferredSize();
	}
	public float getZoom() {
		return zoom;
	}

	public void setZoom(final float zoom) {
		PagePanel.this.zoom = zoom;
		rendered = null;
		refreshPreferredSize();
		requestFocusInWindow();
	}
	
	public void reRender(){
		rendered = null;
		repaint();
	}

	public void paint(Graphics g1) {
		
		Graphics2D g = (Graphics2D)g1;
		
		if (page == null) {
			setPreferredSize(new Dimension(0, 0));
			return;
		}
		
		// paint page
		
		float width = page.width();
		float height = page.height();
		
		width = width * zoom;
		height = height * zoom;
		
		Dimension thisDimension = new Dimension((int)width, (int)height);
		
		if (rendered == null 
			|| 
			page.pageNumber() != rendered.page 
			|| 
			!thisDimension.equals(rendered.dimension)
			||
			zoom != rendered.zoom)
		{
			
			Rectangle2D clip = null;
			ImageObserver observer = this;
			boolean drawBg = true;
			boolean wait = true;
			
			
//			BufferedImage image = (BufferedImage)page.renderer().getImage((int)width, (int)height, clip, observer, drawBg, wait);
			
			try {
				rendered = page.render(thisDimension, zoom);
			} catch (GenericPdfException e) {
				throw new RuntimeException(e);
			}
//			rendered.page = page.renderer().getPageNumber();
//			rendered.zoom = zoom;
//			rendered.image = image;
//			rendered.dimension = thisDimension;
			
			setPreferredSize(rendered.dimension);
		}
		
		g.drawImage(rendered.image, 0, 0, (int)width, (int)height, this);
		
		// paint non-selected components
		
		g.setColor(Color.BLUE);
		
		for (PageComponent c : page) {
			
			if (c == componentUnderMouse) {
				continue;				
			}
			
			c.draw(g, zoom);
			
			Rectangle2D r = scale(c.getRect());
			g.draw(r);
		}
		
		// paint selected component

		if (componentUnderMouse != null) {
			g.setColor(Color.GREEN);
			
			componentUnderMouse.draw(g, zoom);
			
			Rectangle2D r = scale(componentUnderMouse.getRect());
			g.draw(r);
		}
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 48;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 48;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	
	private void refreshPreferredSize() {
		
		if (page != null) {

			float width = page.width();
			float height = page.height();

			width = width * zoom;
			height = height * zoom;

			Dimension thisDimension = new Dimension((int)width, (int)height);
			setPreferredSize(thisDimension);
		}
		else {
			setPreferredSize(new Dimension(0, 0));
		}
		
		invalidate();
		getParent().validate();
		getParent().repaint();
	}
	public final void setComponentEditor(ComponentEditor componentEditor) {
		this.componentEditor = componentEditor;
	}
}
