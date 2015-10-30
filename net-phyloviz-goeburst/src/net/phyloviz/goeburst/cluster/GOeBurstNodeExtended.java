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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.Profile;
import net.phyloviz.goeburst.tree.GOeBurstNode;

public class GOeBurstNodeExtended extends GOeBurstNode implements Profile {

	private LinkedList<GOeBurstNodeExtended> slvList;
	private LinkedList<GOeBurstNodeExtended> dlvList;

	public GOeBurstNodeExtended(Profile p) {
		super(p);
		slvList = new LinkedList<GOeBurstNodeExtended>();
		dlvList = new LinkedList<GOeBurstNodeExtended>();
                
                lv = new int[GOeBurstClusterWithStats.MAXLV + 1];
	}

	public void addSLV(GOeBurstNodeExtended p) {
		slvList.add(p);
	}

	public void addDLV(GOeBurstNodeExtended p) {
		dlvList.add(p);
	}

	public Collection<GOeBurstNodeExtended> getSLVs() {
		return slvList;
	}

	public Collection<GOeBurstNodeExtended> getDLVs() {
		return dlvList;
	}
	
	public void updateLVsExtended(Collection<GOeBurstNodeExtended> nlst, AbstractDistance<GOeBurstNodeExtended> ad, int maxLen) {
		lv = new int[maxLen + 1];

		Iterator<GOeBurstNodeExtended> vIter = nlst.iterator();
		while (vIter.hasNext()) {
			GOeBurstNodeExtended v = vIter.next();

			if (this.equals(v)) {
				continue;
			}

			int diff = ad.level(this, v);

			if (diff == 1) {
				slvList.add(v);
				v.slvList.add(this);
			}

			if (diff == 2) {
				dlvList.add(v);
				v.dlvList.add(this);
			}

			if (diff <= maxLen) {
				lv[diff - 1]++;
				v.lv[diff - 1]++;
			} else {
				lv[maxLen]++;
				v.lv[maxLen]++;
			}
		}
	}

	@Override
	public int getLV(int idx) {

		if (idx < 0 || idx >= lv.length)
			return -1;

		return lv[idx];
	}

	public void setLV(int idx, int nlv) {

		if (idx < 0 || idx >= lv.length)
			return ;

		lv[idx] = nlv;
	}

	
	@Override
	public int diffLV(GOeBurstNode v) {
		int ret, i;

		for (ret = i = 0; ret == 0 && i < lv.length - 1; i++)
			ret = getLV(i) - v.getLV(i);

		return Integer.signum(ret)*i;
	}

	@Override
	public int getUID() {
		return p.getUID();
	}

	@Override
	public String getID() {
		return p.getID();
	}

	@Override
	public int profileLength() {
		return p.profileLength();
	}

	@Override
	public String getValue(int idx) {
		return p.getValue(idx);
		
	}

	@Override
	public int getFreq() {
		return p.getFreq();
	}

	@Override
	public void setFreq(int freq) {
		p.setFreq(freq);
	}

	@Override
	public String get(int idx) {
		return p.get(idx);
	}

	@Override
	public int length() {
		return p.length();
	}

	@Override
	public int weight() {
		return p.weight();
	}
}
