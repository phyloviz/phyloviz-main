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

package net.phyloviz.njviewer.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.ForcePair;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;

public class ForceViewControlAction implements ActionListener {

	private GraphView gv;
	private JDialog forceDialog;

	public ForceViewControlAction(GView gv) {
		this.gv =(GraphView) gv;
		gv.addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorAdded(AncestorEvent event) {
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				if (forceDialog != null) {
					forceDialog.setVisible(false);
					forceDialog.dispose();
				}
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}
		});
	}

	public JMenuItem getMenuItem() {
		JMenuItem mi = new JMenuItem("Control");
		mi.setToolTipText("Rendering engine control");
		mi.setMnemonic(KeyEvent.VK_C);
                mi.setEnabled(!gv.isRadial());
		mi.addActionListener(this);
		return mi;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (forceDialog != null && forceDialog.isShowing())
			forceDialog.requestFocus();
		else
			enableViewControl();
	}

	public void enableViewControl() {

		forceDialog = new JDialog();
		forceDialog.setName("Force Control");
		forceDialog.setTitle("Force Control");
		forceDialog.setIconImage(ImageUtilities.loadImage("net/phyloviz/core/DataSetIcon.png"));
		forceDialog.add(gv.getForcePanel(), BorderLayout.CENTER);
		JPanel p = new JPanel();
		JButton bSave = new JButton("Save");
		JButton bLoad = new JButton("Load");
		p.add(bLoad);
		p.add(bSave);
		p.setBackground(Color.white);
		bLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser() {

					private static final long serialVersionUID = 1L;

					@Override
					protected JDialog createDialog(Component parent) throws HeadlessException {
						JDialog dialog = super.createDialog(parent);
						dialog.setTitle("Choose file");
						return dialog;
					}
				};

				int ww = fileChooser.showDialog(forceDialog, "Load");
				if (ww == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						BufferedReader br = new BufferedReader(new FileReader(file));
						String line;
						ForceSimulator ff = gv.getForceLayout().getForceSimulator();
						Force[] array = ff.getForces();
						System.out.println(Arrays.toString(array));
						int i = 0;
						while (i < array.length) {
							int ni = array[i].getParameterCount();
							System.out.println(ni);
							for (int j = 0; j < ni; j++) {
								line = br.readLine();
								ForcePair p = ForcePair.valueOf(line);
								array[i].setParameter(j, p.value);
							}
							i++;

						}
						gv.getForceLayout().cancel();
						gv.getForceLayout().reset();
						forceDialog.setVisible(false);
						forceDialog.dispose();


					} catch (Exception f) {
						System.out.println(f.getMessage());
						JOptionPane.showMessageDialog(forceDialog, "Unable to load control parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		bSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser() {

					private static final long serialVersionUID = 1L;

					@Override
					protected JDialog createDialog(Component parent) throws HeadlessException {
						JDialog dialog = super.createDialog(parent);
						dialog.setTitle("Choose file");
						return dialog;
					}
				};

				int ret = fileChooser.showDialog(forceDialog, "Save");
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					try {

						ArrayList<ForcePair> pair = gv.getForces();
						PrintWriter pw = new PrintWriter(file);
						for (int i = 0; i < pair.size(); i++) {
							System.out.println(pair.get(i).toString());
							pw.println(pair.get(i).toString());

						}


						pw.close();
						forceDialog.setVisible(false);
						forceDialog.dispose();

					} catch (Exception f) {
						JOptionPane.showMessageDialog(forceDialog, "Unable to save control parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		forceDialog.add(p, BorderLayout.SOUTH);
		forceDialog.setLocationRelativeTo(TopComponent.getRegistry().getActivated());
		forceDialog.pack();
		forceDialog.setVisible(true);
		forceDialog.setResizable(false);
	}
}
