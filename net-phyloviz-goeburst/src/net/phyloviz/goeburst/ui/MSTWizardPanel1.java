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

package net.phyloviz.goeburst.ui;

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

public class MSTWizardPanel1 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private MSTVisualPanel1 component;

	private Node node;

	public MSTWizardPanel1(Node node) {
		super();
		this.node = node;
	}

	@Override
	public Component getComponent() {
		if (component == null) {
			TypingData<? extends Profile> td = (TypingData<? extends Profile>) node.getLookup().lookup(TypingData.class);
			component = new MSTVisualPanel1(td);
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
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/goeburst/GOeBurstMSTImage.png", true));
	}

	@Override
	public void storeSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("distance", ((MSTVisualPanel1) getComponent()).getDistance());
	}

	@Override
	public void validate() throws WizardValidationException {
		AbstractDistance ad = component.getDistance();

		if (ad == null)
			throw new WizardValidationException(null, "Invalid distance", null);
	}
}
