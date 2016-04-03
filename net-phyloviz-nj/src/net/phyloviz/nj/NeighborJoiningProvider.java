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

package net.phyloviz.nj;

import net.phyloviz.algo.AlgorithmProvider;
import net.phyloviz.nj.ui.NeighborJoiningWizardAction;
import org.openide.util.actions.NodeAction;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = AlgorithmProvider.class)
public class NeighborJoiningProvider implements AlgorithmProvider {

	private static final String customName = "Neighbor-Joining";
	private NodeAction action;

	@Override
	public String toString() {
		return customName;
	}

	public boolean asynchronous() {
		return false;
	}

        /**
         * 
         * @return 
         */
	@Override
	public NodeAction getAction() {
		if (action == null)
			action = new NeighborJoiningWizardAction();
		return action;
	}
}
