package net.phyloviz.njviewer.ui.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import org.openide.util.HelpCtx;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Cátia Vaz
 */
public class ChartLegendPanel extends TopComponent {

	private Dimension d;
	private int total;

	public ChartLegendPanel(EdgeStats edge, Dimension _d) {
		this.setLayout(new GridLayout(1,2));
		this.d = _d;
		this.total = edge.interval;

		ChartLegend ch = new ChartLegend(edge, d);
		add(ch.getChartPie());
		add(ch.getLegends());
                
                
//                this.setLayout(new GridLayout(1,4));
//		this.d = _d;
//		this.total = _total;
//
//		ChartLegend ch = new ChartLegend(view, d, total, c, minEdgeDiff, maxEdgeDiff);
//		add(ch.getChartPie());
//		add(ch.getLegends());
//                
//                ChartLegend ch2 = new ChartLegend(view, d, total, c, minEdgeDiff, maxEdgeDiff);
//		add(ch2.getChartPie());
//		add(ch2.getLegends());
	}

	@Override
	public void open() {
		Mode m = WindowManager.getDefault().findMode("output");
		m.dockInto(this);
		super.open();
	}

	@Override
	public HelpCtx getHelpCtx() {
		return null;
	}

	@Override
	public int getPersistenceType() {
		return PERSISTENCE_NEVER;
	}

	@Override
	protected String preferredID() {
		return "ChartLegend";
	}
}
