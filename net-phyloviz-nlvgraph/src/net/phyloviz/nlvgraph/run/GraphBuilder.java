package net.phyloviz.nlvgraph.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.tree.GOeBurstNode;
import net.phyloviz.nlvgraph.Result;
import net.phyloviz.nlvgraph.ui.OutputPanel;
import org.openide.nodes.Node;

public class GraphBuilder implements Runnable {

	static int times = 0;

	private final Node n;
	private final OutputPanel op;
	private final AbstractDistance<GOeBurstNode> ad;
	private final int min, max;

	public GraphBuilder(Node n, OutputPanel op, AbstractDistance<GOeBurstNode> ad, int min, int max) {
		this.n = n;
		this.op = op;
		this.ad = ad;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void run() {
		op.appendWithDate("MST algorithm has started\nMST algorithm: computing nodes...\n");
		op.flush();

		DataSet ds = n.getParentNode().getLookup().lookup(DataSet.class);
		Population pop = ds.getLookup().lookup(Population.class);
		TypingData<? extends Profile> td = (TypingData<? extends Profile>) n.getLookup().lookup(TypingData.class);

		ArrayList<GOeBurstNode> nlst = new ArrayList<GOeBurstNode>();

		Iterator<? extends Profile> ip = td.iterator();
		while (ip.hasNext())
			nlst.add(new GOeBurstNode(ip.next()));

		op.appendWithDate("\nnLV Graph: computing LVs...\n");
		op.flush();

		int maxLV = ad.maxLevel();

		Iterator<GOeBurstNode> in = nlst.iterator();
		while (in.hasNext())
			in.next().updateLVs(nlst, ad, maxLV);

		op.appendWithDate("\nnLV Graph: sorting nodes...\n");
		op.flush();

		Collections.sort(nlst, ad.getProfileComparator());

		op.appendWithDate("\nnLV Graph: computing edges...\n");
		op.flush();

		op.append("Min level: " + max + "\n");
		op.append("Max level: " + max + "\n");

		ArrayList<Edge<GOeBurstNode>> edges = new ArrayList<Edge<GOeBurstNode>>();
		Iterator<GOeBurstNode> i = nlst.iterator();
		while (i.hasNext()) {
			GOeBurstNode xi = i.next();
			Iterator<GOeBurstNode> j = nlst.iterator();
			while (j.hasNext()) {
				GOeBurstNode xj = j.next();

				if (xi.getUID() < xj.getUID()) {
					int level = ad.level(xi, xj); 
					if (level >= min && level <= max)
						edges.add(new Edge<GOeBurstNode>(xi, xj));
				}
			}
		}

		op.append("#Nodes: " + nlst.size() + "\n");
		op.append("#Edges: " + edges.size() + "\n");
		
		op.appendWithDate("nLV Graph done.\n");
		op.flush();

		td.add(new Result(ds, edges, ad, op, min, max));
	
	}
}
