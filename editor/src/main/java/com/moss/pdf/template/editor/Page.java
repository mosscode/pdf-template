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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.moss.pdf.genericrenderer.GenericPdfException;
import com.moss.pdf.genericrenderer.GenericPdfPage;
import com.moss.pdf.genericrenderer.GenericPdfRenderer;
import com.moss.pdf.genericrenderer.RenderedPage;

public class Page implements Iterable<PageComponent>, PageComponentListener {
	
	private final int pageNum;
//	private final byte[] pdf;
	private final GenericPdfPage page;
	private final List<PageComponent> components = new ArrayList<PageComponent>();
	private final List<PageListener> listeners = new ArrayList<PageListener>();

	public Page(GenericPdfPage page, int pageNum) throws GenericPdfException {
		this.page = page;
		this.pageNum = pageNum;
	}
	
	public void add(PageComponent component) {
		component.addListener(this);
		components.add(component);
		fireComponentAdded(component);
	}
	
	public void remove(PageComponent component) {
		component.removeListener(this);
		components.remove(component);
		fireComponentRemoved(component);
	}
	
	public void clear() {
		for (PageComponent component : components) {
			remove(component);
		}
	}
	
	public int pageNumber() {
		return pageNum;
	}
	
	public Iterator<PageComponent> iterator() {
		return components.iterator();
	}
	
	public void addListener(PageListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PageListener l) {
		listeners.remove(l);
	}
	
	public void componentChanged(PageComponent component) {
		fireComponentChanged(component);
	}
	
	private void fireComponentAdded(PageComponent component) {
		for (PageListener l : listeners) {
			l.componentAdded(this, component);
		}
	}
	
	private void fireComponentRemoved(PageComponent component) {
		for (PageListener l : listeners) {
			l.componentRemoved(this, component);
		}
	}
	
	private void fireComponentChanged(PageComponent component) {
		for (PageListener l : listeners) {
			l.componentChanged(this, component);
		}
	}
	
	public float width() {
		try {
			return (float) page.width();
		} catch (GenericPdfException e) {
			throw new RuntimeException(e);
		}
	}
	public float height() {
		try{
			return (float) page.height();
		} catch (GenericPdfException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public RenderedPage render(Dimension size, float zoom) throws GenericPdfException {
		return page.render(size, zoom);
	}
	
}
