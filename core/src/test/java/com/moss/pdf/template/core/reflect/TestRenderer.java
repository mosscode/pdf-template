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
package com.moss.pdf.template.core.reflect;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.moss.pdf.template.api.FontName;
import com.moss.pdf.template.api.PropertyMapping;
import com.moss.pdf.template.api.TextAlignment;
import com.moss.pdf.template.core.Renderer;

public class TestRenderer {

	@Test
	public void basic() throws Exception {
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("com/moss/pdf/template/core/reflect/template.pdf");
		
		PropertyMapping sockCount = new PropertyMapping();
		sockCount.setPageNumber(1);
		sockCount.setX(4.15f);
		sockCount.setY(24.45f);
		sockCount.setFontName(FontName.TIMES_ROMAN);
		sockCount.setFontSize(8);
		sockCount.setAlignment(TextAlignment.LEFT);
		sockCount.setExpr("info.sockCount");
		
		PropertyMapping pickleCount = new PropertyMapping();
		pickleCount.setPageNumber(1);
		pickleCount.setX(19.6f);
		pickleCount.setY(23.55f);
		pickleCount.setFontName(FontName.COURIER_BOLD);
		pickleCount.setFontSize(12);
		pickleCount.setAlignment(TextAlignment.RIGHT);
		pickleCount.setExpr("info.sandwich.pickleCount");
		
		PropertyMapping bagLevel = new PropertyMapping();
		bagLevel.setPageNumber(1);
		bagLevel.setX(14.11f);
		bagLevel.setY(22.5f);
		bagLevel.setFontName(FontName.HELVETICA);
		bagLevel.setFontSize(14);
		bagLevel.setAlignment(TextAlignment.CENTER);
		bagLevel.setExpr("info.vacuumCleaners[blue].bagLevel");

		List<PropertyMapping> mappings = new ArrayList<PropertyMapping>();
		mappings.add(sockCount);
		mappings.add(pickleCount);
		mappings.add(bagLevel);
		
		OutputStream out = new FileOutputStream("target/out.pdf");
		
		Renderer r;
		{
			InfoProvider info = new InfoProvider();
			
			ReflectionParser p = new ReflectionParser();
			p.bind("info", info);
			
			r = new Renderer(p);
		}
		
		r.render(in, mappings, out);
		
//		Runtime.getRuntime().exec("evince target/out.pdf");
	}
}
