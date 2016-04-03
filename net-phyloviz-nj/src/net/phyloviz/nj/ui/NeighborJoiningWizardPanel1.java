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

package net.phyloviz.nj.ui;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.algo.AbstractDistance;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;

public class NeighborJoiningWizardPanel1 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private NeighborJoiningVisualPanel1 component;

	private Node node;

	public NeighborJoiningWizardPanel1(Node node) {
		super();
		this.node = node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getComponent() {
		if (component == null) {
			TypingData<? extends Profile> td = node.getLookup().lookup(TypingData.class);
			component = new NeighborJoiningVisualPanel1(td);
		}
		component.setPreferredSize(new java.awt.Dimension(480,340));
		return component;
	}

	@Override
	public HelpCtx getHelp() {
		return HelpCtx.DEFAULT_HELP;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public final void addChangeListener(ChangeListener l) {
	}

	@Override
	public final void removeChangeListener(ChangeListener l) {
	}

	@Override
	public void readSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/nj/Neighbor-JoiningImage.jpg", true));
	}

	@Override
	public void storeSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("distance", ((NeighborJoiningVisualPanel1) getComponent()).getDistance());
	}

	@Override
	public void validate() throws WizardValidationException {
		AbstractDistance ad = component.getDistance();

		if (ad == null)
			throw new WizardValidationException(null, "Invalid distance", null);
	}
}
