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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.moss.pdf.template.api.FontName;
import com.moss.pdf.template.api.TextAlignment;

public class TextComponent implements PageComponent {
	
	/*
	 * absolute position
	 */
	private float x, y;
	
	/*
	 * last rendered dimensions
	 */
	private float width, height;
	
	/*
	 * rendering details
	 */
	private FontName font;
	private int fontSize;
	private TextAlignment alignment;
	private String expr;
	private ParserReference ref;
	
	private final List<PageComponentListener> listeners = new ArrayList<PageComponentListener>();
	
	public TextComponent(Point2D p, FontName font, int fontSize, TextAlignment alignment, String expr, ParserReference ref) {
		
		x = (float)p.getX();
		y = (float)p.getY();
		width = 0;
		height = 0;

		this.font = font;
		this.fontSize = fontSize;
		this.alignment = alignment;
		this.expr = expr;
		this.ref = ref;
	}
	
	public Rectangle2D.Float getRect() {
		return new Rectangle2D.Float(x, y, width, height);
	}

	public void moveTo(Point2D p) {
		x = (float)p.getX();
		y = (float)p.getY();
		fireComponentChanged();
	}
	
	public void draw(Graphics2D g, float zoom) {
		
		/*
		 * determine current dimensions
		 */
		
		String family = null;
		int style = Font.PLAIN;
		
		if (FontName.COURIER == this.font) {
			family = "Courier";
		}
		else if (FontName.COURIER_BOLD == this.font) {
			family = "Courier";
		}
		else if (FontName.COURIER_BOLD_OBLIQUE == this.font) {
			family = "Courier";
		}
		else if (FontName.COURIER_OBLIQUE == this.font) {
			family = "Courier";
		}
		else if (FontName.HELVETICA == this.font) {
			family = "Helvetica";
		}
		else if (FontName.HELVETICA_BOLD == this.font) {
			family = "Helvetica";
		}
		else if (FontName.HELVETICA_BOLD_OBLIQUE == this.font) {
			family = "Helvetica";
		}
		else if (FontName.HELVETICA_OBLIQUE == this.font) {
			family = "Helvetica";
		}
		else if (FontName.TIMES_BOLD == this.font) {
			family = "Times-Roman";
		}
		else if (FontName.TIMES_BOLD_ITALIC == this.font) {
			family = "Helvetica";
		}
		else if (FontName.TIMES_ITALIC == this.font) {
			family = "Helvetica";
		}
		else if (FontName.TIMES_ROMAN == this.font) {
			family = "Helvetica";
		}
		
		if (family == null) {
			throw new RuntimeException("Cannot determine the family for this font: " + this.font);
		}
		
		Font font = new Font(family, style, fontSize);
		
		String text;
		
		try {
			Object o = ref.getParser().eval(expr);
			
			if (o == null) {
				text = "NULL";
			}
			else {
				text = o.toString();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			text = "NOT_PARSABLE";
		}

		Font oldFont = g.getFont();
		g.setFont(font);
		width = g.getFontMetrics().charsWidth(text.toCharArray(), 0, text.length());
		height = g.getFontMetrics().getHeight();
		g.setFont(oldFont);
		
		float scaledX = x * zoom;
		float scaledY = y * zoom;
		float scaledWidth = width * zoom;
		float scaledHeight = height * zoom;
		
		/*
		 * draw to buffer
		 */
		
		BufferedImage buffer = new BufferedImage((int)scaledWidth, (int)scaledHeight, ColorSpace.TYPE_RGB);
		Graphics2D bg = (Graphics2D)buffer.getGraphics();
		
		bg.scale(zoom, zoom);
		
		bg.setColor(Color.white);
		bg.fillRect(0, 0, (int)width, (int)height);
		
		bg.setColor(Color.black);
		bg.setFont(font);
		bg.drawString(text, 0, height);
		
		g.drawImage(buffer, (int)scaledX, (int)scaledY, null);
	}

	public void addListener(PageComponentListener l) {
		listeners.add(l);
	}

	public void removeListener(PageComponentListener l) {
		listeners.remove(l);
	}
	
	private void fireComponentChanged() {
		for (PageComponentListener l : listeners) {
			l.componentChanged(this);
		}
	}

	public TextAlignment getAlignment() {
		return alignment;
	}

	public void setAlignment(TextAlignment alignment) {
		this.alignment = alignment;
		fireComponentChanged();
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
		fireComponentChanged();
	}

	public FontName getFont() {
		return font;
	}

	public void setFont(FontName font) {
		this.font = font;
		fireComponentChanged();
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		fireComponentChanged();
	}

	public ParserReference getRef() {
		return ref;
	}

	public void setRef(ParserReference ref) {
		this.ref = ref;
		fireComponentChanged();
	}
}
