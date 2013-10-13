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

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.moss.pdf.template.core.reflect.InfoProvider.VacuumCleanerKeys;

public class TestParser {
	
	private ReflectionParser p;
	private InfoProvider info;
	
	@Before
	public void before() {
		info = new InfoProvider();
		p = new ReflectionParser();
		p.bind("info", info);
	}
	
	@Test
	public void rootPath() throws ParseException {
		Assert.assertEquals(info, p.eval("info"));
	}

	@Test
	public void value() throws ParseException {
		Assert.assertEquals(info.getSockCount(), p.eval("info.sockCount"));
	}
	
	@Test
	public void object() throws ParseException {
		Assert.assertEquals(info.getSandwich().getPickleCount(), p.eval("info.sandwich.pickleCount"));
	}
	
	@Test
	public void map() throws ParseException {
		Assert.assertEquals(info.getVacuumCleaners().get(VacuumCleanerKeys.BLUE).getBagLevel(), p.eval("info.vacuumCleaners[blue].bagLevel"));
	}
	
	@Test
	public void mapSpaceInKey() throws ParseException {
		Assert.assertEquals(info.getVacuumCleaners().get(VacuumCleanerKeys.LIGHT_BROWN).getBagLevel(), p.eval("info.vacuumCleaners[light brown].bagLevel"));
	}
	
	@Test
	public void mapDotInKey() throws ParseException {
		Assert.assertEquals(info.getVacuumCleaners().get(VacuumCleanerKeys.DARK_ORANGE).getBagLevel(), p.eval("info.vacuumCleaners[dark.orange].bagLevel"));
	}
}
