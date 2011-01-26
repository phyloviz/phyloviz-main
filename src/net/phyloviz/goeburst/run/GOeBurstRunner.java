package net.phyloviz.goeburst.run;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Isolate;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.goeburst.algorithm.GOeBurstWithStats;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.Node;

public class GOeBurstRunner implements Runnable {

	private Node n;
	private DataSet ds;
	private Population pop;
	private OutputPanel op;
	private HashMap<String, String> st2cl;
	private int level;
	static int times = 0;

	public GOeBurstRunner(Node n, OutputPanel op, int level) {
		this.n = n;
		this.op = op;
		this.level = level;
		ds = n.getParentNode().getLookup().lookup(DataSet.class);
		pop = ds.getLookup().lookup(Population.class);
	}

	@Override
	public void run() {

		op.appendWithDate("goeBURST started\ngoeBURST algorithm: computing groups...\n\n");
		op.flush();
		
		TypingData<? extends AbstractProfile> td = n.getLookup().lookup(TypingData.class);

		GOeBurstWithStats algorithm = new GOeBurstWithStats(level);
		Collection<GOeBurstClusterWithStats> groups = algorithm.getClustering(td);

		// Integrate with isolate data, if it exists.
		if (pop != null) {
			op.appendWithDate("goeBURST algorithm: integrating data...\n\n");
			op.flush();

			st2cl = new HashMap<String, String>();
			Iterator<? extends GOeBurstCluster> igo = groups.iterator();
			while (igo.hasNext()) {
				GOeBurstCluster cluster = igo.next();
				String id = "" + cluster.getID();
				Iterator<AbstractProfile> itr = cluster.getSTs().iterator();
				while (itr.hasNext()) {
					st2cl.put(itr.next().getID(), id);
				}
			}
			pop.addColumn("goeBURST[" + (times++) + "]", new Population.ColumnFiller() {

				@Override
				public String getValue(Isolate i) {
					return st2cl.get(i.get(ds.getPopKey()));
				}
			});

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					pop.tableModel().fireTableStructureChanged();
				}
			});
		}

		Iterator<GOeBurstClusterWithStats> ig = groups.iterator();
		op.append("#CC = " + groups.size() + "\n\n");
		op.flush();
		while (ig.hasNext()) {
			GOeBurstClusterWithStats g = ig.next();

			op.append("CC " + g.getID() + " has " + g.size() + " STs:\n");

			Iterator<AbstractProfile> is = g.getSTs().iterator();
			while (is.hasNext()) {
				AbstractProfile st = is.next();

				op.append("ST " + st.getID() + " (" + st.getFreq() + ") "
					+ g.getXLV(st, 0) + " " + g.getXLV(st, 1) + " " + g.getXLV(st, 2) + " " + g.getXLV(st, 3) + " ("
					+ algorithm.getSTxLV(st, 0) + " " + algorithm.getSTxLV(st, 1) + " " + algorithm.getSTxLV(st, 2) + " " + algorithm.getSTxLV(st, 3)
					+ ")" + (g.isFounder(st) ? " *" : "  ") + "\n");

			}

			ArrayList<Edge> elst = g.getEdges();
			if (g.size() >= 2) {
				op.append("\nCC " + g.getID() + " has " + (g.size() - 1) + "/" + elst.size() + " selected edges:\n");
			} else {
				op.append("\nCC " + g.getID() + " has " + (g.size() - 1) + "/" + elst.size() + " selected edges\n");
			}
			Iterator<Edge> ie = elst.iterator();
			while (ie.hasNext()) {
				Edge e = ie.next();
				if (e.visible()) {
					op.append(e.getU().getID() + " - " + e.getV().getID() + "\n");
				}
			}
			op.append("\n");
		}

		op.appendWithDate("goeBURST algorithm: computing statistics...\n");
		op.flush();
		Iterator<GOeBurstClusterWithStats> iter = groups.iterator();
		while (iter.hasNext()) {
			iter.next().computeStatistics();
		}
		op.appendWithDate("goeBURST algorithm: done.\n");
		op.flush();

		ds.add(new GOeBurstResult(groups, level, op));
	}
}
