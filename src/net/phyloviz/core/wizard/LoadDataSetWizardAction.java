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

package net.phyloviz.core.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;

public final class LoadDataSetWizardAction extends AbstractAction {

	private WizardDescriptor.Panel[] panels;

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		WizardDescriptor wizardDescriptor = new WizardDescriptor((Panel<WizardDescriptor>[]) getPanels());
		// {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
		wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
		wizardDescriptor.setTitle("Load a Dataset");
		wizardDescriptor.putProperty("WizardPanel_contentBackgroundColor", Color.WHITE);
		//wizardDescriptor.putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/core/wizard.gif", true));
		wizardDescriptor.putProperty("WizardPanel_imageAligment", "center");
		Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
		dialog.setVisible(true);
		dialog.toFront();
		boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
		if (!cancelled) {
			// do something
		}
	}

	/**
	 * Initialize panels representing individual wizard's steps and sets
	 * various properties for them influencing wizard appearance.
	 */
	private WizardDescriptor.Panel[] getPanels() {
		if (panels == null) {
			panels = new WizardDescriptor.Panel[]{
					new LoadDataSetWizardPanel1(),
					new LoadDataSetWizardPanel2(),
					new LoadDataSetWizardPanel3()
				};
			String[] steps = new String[panels.length];
			for (int i = 0; i < panels.length; i++) {
				Component c = panels[i].getComponent();
				// Default step name to component name of panel. Mainly useful
				// for getting the name of the target chooser to appear in the
				// list of steps.
				steps[i] = c.getName();
				if (c instanceof JComponent) { // assume Swing components
					JComponent jc = (JComponent) c;
					// Sets step number of a component
					// TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
					jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
					// Sets steps names for a panel
					jc.putClientProperty("WizardPanel_contentData", steps);
					// Turn on subtitle creation on each step
					jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
					// Show steps on the left side with the image on the background
					jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
					// Turn on numbering of all steps
					jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
				}
			}
		}
		return panels;
	}

	public String getName() {
		return "Start Sample Wizard";
	}

	public String iconResource() {
		return null;
	}

	public HelpCtx getHelpCtx() {
		return HelpCtx.DEFAULT_HELP;
	}

	protected boolean asynchronous() {
		return false;
	}
}
