/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

package net.phyloviz.category.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JOptionPane;
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

					if (cp.getCategories().size() == 1) {
						JOptionPane.showMessageDialog(getParent(),
							"Your selection returns only one category!",
							"Warning", JOptionPane.WARNING_MESSAGE);
					} else if (cp.getCategories().size() == 0) {
						JOptionPane.showMessageDialog(getParent(),
							"Your selection does not return any category!",
							"Warning", JOptionPane.WARNING_MESSAGE);
					}
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
