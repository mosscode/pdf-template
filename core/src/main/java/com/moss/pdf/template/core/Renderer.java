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
package com.moss.pdf.template.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.moss.pdf.template.api.FontName;
import com.moss.pdf.template.api.PropertyMapping;
import com.moss.pdf.template.api.TextAlignment;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class Renderer {
	
	private static final float POINTS_IN_A_CM = 28.3464567f;
	
	private final Parser p;
	
	public Renderer(Parser p) {
		this.p = p;
	}
	
	public void render(InputStream in, List<? extends PropertyMapping> fields, OutputStream out) throws Exception {
		
		PdfReader reader = new PdfReader(in);
		
		Document document = new Document(reader.getPageSizeWithRotation(1));
		
		PdfWriter writer = PdfWriter.getInstance(document, out);
		
		document.open();
		
		for (int i=1; i<=reader.getNumberOfPages(); i++) {
			
			PdfContentByte cb = writer.getDirectContent();
			
			PdfImportedPage customPage = writer.getImportedPage(reader, i);
			
			/*
			 * add the page to our new document, turning this page to its 
			 * original rotation
			 */
			int pageRotation = reader.getPageRotation(i);
			
			if (pageRotation > 0) {

				System.out.println("page rotation found: " + pageRotation);

				double angle = -((2 * Math.PI) * pageRotation / 360);
				//			double angle = -(Math.PI / 2);

				cb.addTemplate(
						customPage, 
						(float)Math.cos(angle),
						(float)Math.sin(angle), 
						(float)-Math.sin(angle), 
						(float)Math.cos(angle),  
						0f, // x
						document.top() + document.topMargin() // y
				);
			}
			else {
				cb.addTemplate(customPage, 0f, 0f);
			}
			
			Map<FontName, BaseFont> fonts = new HashMap<FontName, BaseFont>();
			
			for (PropertyMapping field : fields) {
				
				if (field.getPageNumber() != i) {
					continue;
				}
					
				/*
				 * Only builtin fonts are supported at the moment
				 */
				BaseFont font;
				int fontSize;
				int alignment;
				String text;
				float x, y;
				float rotation;
				
				{
					font = fonts.get(field.getFontName());

					if (font == null) {
						
						FontName e = field.getFontName();
						String name = null;
						
						if (FontName.COURIER == e) {
							name = BaseFont.COURIER;
						}
						else if (FontName.COURIER_BOLD == e) {
							name = BaseFont.COURIER_BOLD;
						}
						else if (FontName.COURIER_BOLD_OBLIQUE == e) {
							name = BaseFont.COURIER_BOLDOBLIQUE;
						}
						else if (FontName.COURIER_OBLIQUE == e) {
							name = BaseFont.COURIER_OBLIQUE;
						}
						else if (FontName.HELVETICA == e) {
							name = BaseFont.HELVETICA;
						}
						else if (FontName.HELVETICA_BOLD == e) {
							name = BaseFont.HELVETICA_BOLD;
						}
						else if (FontName.HELVETICA_BOLD_OBLIQUE == e) {
							name = BaseFont.HELVETICA_BOLDOBLIQUE;
						}
						else if (FontName.HELVETICA_OBLIQUE == e) {
							name = BaseFont.HELVETICA_OBLIQUE;
						}
						else if (FontName.TIMES_BOLD == e) {
							name = BaseFont.TIMES_BOLD;
						}
						else if (FontName.TIMES_BOLD_ITALIC == e) {
							name = BaseFont.TIMES_BOLDITALIC;
						}
						else if (FontName.TIMES_ITALIC == e) {
							name = BaseFont.TIMES_ITALIC;
						}
						else if (FontName.TIMES_ROMAN == e) {
							name = BaseFont.TIMES_ROMAN;
						}
						
						if (name == null) {
							throw new RuntimeException("Unknown font type: " + e);
						}
						
						font = BaseFont.createFont(name, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
						fonts.put(field.getFontName(), font);
					}
					
					fontSize = field.getFontSize();

					if (TextAlignment.LEFT == field.getAlignment()) {
						alignment = PdfContentByte.ALIGN_LEFT;
					}
					else if (TextAlignment.CENTER == field.getAlignment()) {
						alignment = PdfContentByte.ALIGN_CENTER;
					}
					else if (TextAlignment.RIGHT == field.getAlignment()) {
						alignment = PdfContentByte.ALIGN_RIGHT;
					}
					else {
						alignment = PdfContentByte.ALIGN_LEFT;
					}

					Object value = p.eval(field.getExpr());

					if (value == null) {
						text = "";
					}
					else {
						text = value.toString();
					}

					x = field.getX() * POINTS_IN_A_CM;
					y = field.getY() * POINTS_IN_A_CM;
					
					rotation = 0;
				}

				cb.beginText();

				cb.setFontAndSize(font, fontSize);

				cb.showTextAligned(alignment, text, x, y, rotation);

				cb.endText();
			}
			
			document.newPage();
		}
		
		reader.close();
		document.close();
	}
}
