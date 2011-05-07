/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.goeburst.cluster;

import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.goeburst.AbstractDistance;

public class Edge implements Comparable<Edge> {
	private AbstractProfile u;
	private AbstractProfile v;
	private AbstractDistance ad;
	private boolean vis;

	public Edge(AbstractProfile u, AbstractProfile v, AbstractDistance ad) {
		
		if (u.getUID() > v.getUID()) {
			AbstractProfile x = u;
			u = v;
			v = x;
		}
		
		this.u = u;
		this.v = v;
		this.ad = ad;
		
		this.vis = false;
	}

	public AbstractProfile getU() {
		return u;
	}
	
	public AbstractProfile getV() {
		return v;
	}
	
	public void setVisible(boolean vis) {
		this.vis = vis;
	}
	
	public boolean visible() {
		return vis;
	}
	
	@Override
	public int compareTo(Edge e) {
		if (u.getUID() - e.getU().getUID() != 0)
			return u.getUID() - e.getU().getUID();
		
		return v.getUID() - e.getV().getUID();
	}

	public boolean equals(Edge e) {
		return compareTo(e) == 0;
	}
	
	@Override
	public String toString() {
		char lv = '?';
		switch (ad.compute(getU(), getV())) {
		case 1:
			lv = 's';
			break;
		case 2:
			lv = 'd';
			break;
		case 3:
			lv = 't';
		}
		return "Edge: " + u.getID() + " -- " + v.getID() + " ( " + lv + "lv level )\n";
	}
}
