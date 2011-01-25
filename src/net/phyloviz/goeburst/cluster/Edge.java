package net.phyloviz.goeburst.cluster;

import net.phyloviz.core.data.AbstractType;
import net.phyloviz.goeburst.algorithm.HammingDistance;

public class Edge implements Comparable<Edge> {
	private AbstractType u;
	private AbstractType v;
	private boolean vis;

	public Edge(AbstractType u, AbstractType v) {
		
		if (u.getUID() > v.getUID()) {
			AbstractType x = u;
			u = v;
			v = x;
		}
		
		this.u = u;
		this.v = v;
		
		this.vis = false;
	}

	public AbstractType getU() {
		return u;
	}
	
	public AbstractType getV() {
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
		switch (HammingDistance.compute(getU(), getV())) {
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
