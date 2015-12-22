package net.phyloviz.njviewer.ui.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

	public ChartLegendPanel(Dimension _d, int _total, Color[] c, double maxEdgeDiff) {
		this.setLayout(new BorderLayout());
		this.d = _d;
		this.total = _total;

		ChartLegend ch = new ChartLegend(d, total, c, maxEdgeDiff);
		add(ch.getChartPie(), BorderLayout.CENTER);
		add(ch.getLegends(), BorderLayout.EAST);
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
