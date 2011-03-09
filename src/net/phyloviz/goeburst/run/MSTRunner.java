package net.phyloviz.goeburst.run;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import net.phyloviz.algo.tree.Edge;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.AbstractDistance;
import net.phyloviz.algo.tree.MSTAlgorithm;
import net.phyloviz.core.data.Profile;
import net.phyloviz.goeburst.tree.GOeBurstEdgeComparator;
import net.phyloviz.goeburst.tree.GOeBurstMSTResult;
import net.phyloviz.goeburst.tree.GOeBurstTreeNode;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.Node;

public class MSTRunner implements Runnable {

	private Node n;
	private OutputPanel op;
	private AbstractDistance ad;
	static int times = 0;

	public MSTRunner(Node n, OutputPanel op, AbstractDistance ad) {
		this.n = n;
		this.op = op;
		this.ad = ad;

	}

	@Override
	public void run() {

		op.appendWithDate("MST algorithm has started\nMST algorithm: computing nodes...\n");
		op.flush();

		DataSet ds = n.getParentNode().getLookup().lookup(DataSet.class);
		Population pop = ds.getLookup().lookup(Population.class);
		TypingData<? extends Profile> td = (TypingData<? extends Profile>) ds.getLookup().lookup(TypingData.class);

		ArrayList<GOeBurstTreeNode> nlst = new ArrayList<GOeBurstTreeNode>();

		Iterator<? extends Profile> ip = td.iterator();
		while (ip.hasNext())
			nlst.add(new GOeBurstTreeNode(ip.next()));

		op.appendWithDate("\nMST algorithm: computing LVs...\n");
		op.flush();

		int maxLV = ad.maximum(td);

		Iterator<GOeBurstTreeNode> in = nlst.iterator();
		while (in.hasNext())
			in.next().updateLVs(nlst, ad, maxLV);

		op.appendWithDate("\nMST algorithm: sorting nodes...\n");
		op.flush();

		Collections.sort(nlst, new Comparator<GOeBurstTreeNode>() {

			@Override
			public int compare(GOeBurstTreeNode u, GOeBurstTreeNode v) {
				return u.diffLV(v);
			}

		});

		op.appendWithDate("\nMST algorithm: computing tree edges...\n");
		op.flush();

		Collection<Edge> tree = null;
		if (nlst.size() > 2) {
			MSTAlgorithm algorithm = new MSTAlgorithm(nlst, new GOeBurstEdgeComparator(ad, maxLV));
			tree = algorithm.getTree();
		}

		op.appendWithDate("\nMST algorithm: printing edges...\n");
		op.flush();

		Iterator<Edge> ei = tree.iterator();
		while (ei.hasNext()) {
			Edge e = ei.next();

			int diff = Math.abs(((GOeBurstTreeNode) e.getU()).diffLV((GOeBurstTreeNode) e.getV()));

			op.append(e.getU().getProfile().getID() + " -- " + e.getV().getProfile().getID() + " (level: " + diff  + ")\n");
		}

		op.appendWithDate("MST algorithm: done.\n");
		op.flush();

		ds.add(new GOeBurstMSTResult(tree, ad, op));
	}
}
