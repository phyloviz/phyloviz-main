package net.phyloviz.category.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import net.phyloviz.category.CategoryChangeListener;
import net.phyloviz.category.CategoryProvider;
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
	private CategoryProvider cp;
	private int total;

	public ChartLegendPanel(Dimension _d, CategoryProvider _cp, int _total) {
		this.setLayout(new BorderLayout());
		this.d = _d;
		this.cp = _cp;
		this.total = _total;

		ChartLegend ch = new ChartLegend(d, cp, total);
		add(ch.getChartPie(), BorderLayout.CENTER);
		add(ch.getLegends(), BorderLayout.EAST);

		cp.addCategoryChangeListener(new CategoryChangeListener() {

			@Override
			public void categoryChange(CategoryProvider cp) {
				if (cp.isOn()) {
					ChartLegend ch = new ChartLegend(d, cp, total);
					removeAll();
					add(ch.getChartPie(), BorderLayout.CENTER);
					add(ch.getLegends(), BorderLayout.EAST);
					//requestActive();
					revalidate();
				}
			}
		});
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
