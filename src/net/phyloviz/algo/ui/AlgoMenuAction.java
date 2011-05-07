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

package net.phyloviz.algo.ui;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.phyloviz.algo.AlgorithmProvider;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

public class AlgoMenuAction extends AbstractAction implements Presenter.Popup {

	@Override
	public void actionPerformed(ActionEvent e) {
		// No action.
	}

	@Override
	public JMenuItem getPopupPresenter() {
		JMenu result = new JMenu("Compute");

		Collection<? extends AlgorithmProvider> lookres = Lookup.getDefault().lookupAll(AlgorithmProvider.class);
		Iterator<? extends AlgorithmProvider> ir = lookres.iterator();
		while (ir.hasNext()) {
			JMenuItem mi = new JMenuItem();
			AlgorithmProvider ap = ir.next();
			mi.setText(ap.toString());
			mi.addActionListener(ap.getAction());
			result.add(mi);
		}

		if (lookres.isEmpty())
			result.setEnabled(false);

		return result;
	}


}
