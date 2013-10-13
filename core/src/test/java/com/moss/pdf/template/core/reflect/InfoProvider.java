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

import java.util.HashSet;
import java.util.Set;

@DefaultBinding("info")
@Description("Provides testing info.")
public class InfoProvider {
	
	private final int sockCount;
	private final Sandwich sandwich;
	private final MapProvider<VacuumCleaner> vacuumCleaners;
	
	public InfoProvider() {
		this.sockCount = 7;
		
		this.sandwich = new Sandwich(3);
		
		SimpleMapProvider map = new SimpleMapProvider();
		map.put(VacuumCleanerKeys.GREEN, new VacuumCleaner());
		map.put(VacuumCleanerKeys.BLUE, new VacuumCleaner());
		map.put(VacuumCleanerKeys.YELLOW, new VacuumCleaner());
		map.put(VacuumCleanerKeys.LIGHT_BROWN, new VacuumCleaner());
		map.put(VacuumCleanerKeys.DARK_ORANGE, new VacuumCleaner());
		
		vacuumCleaners = map;
	}

	@Description("The number of socks available.")
	public int getSockCount() {
		return sockCount;
	}
	
	@Description("The currently selected sandwich.")
	public Sandwich getSandwich() {
		return sandwich;
	}
	
	@Keys(provider=VacuumCleanerKeys.class)
	@Description("A map of all known vacuum cleaners, keyed by color.")
	public MapProvider<VacuumCleaner> getVacuumCleaners() {
		return vacuumCleaners;
	}
	
	public static class VacuumCleanerKeys implements KeyProvider {
		
		public static final String GREEN = "green", BLUE = "blue", YELLOW = "yellow", LIGHT_BROWN = "light brown", DARK_ORANGE = "dark.orange";
		
		public Set<String> getKeys() {
			Set<String> s = new HashSet<String>();
			s.add(GREEN);
			s.add(BLUE);
			s.add(YELLOW);
			s.add(LIGHT_BROWN);
			s.add(DARK_ORANGE);
			return s;
		}
	}
	
	public static class Sandwich {
		
		int pickleCount;
		
		public Sandwich(int pickleCount) {
			this.pickleCount = pickleCount;
		}

		public int getPickleCount() {
			return 3;
		}
	}
	
	public static class VacuumCleaner {
		
		public float getBagLevel() {
			return .4f;
		}
	}
}
