package net.phyloviz.goeburst.cluster;

import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.goeburst.algorithm.AbstractDistance;

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
