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

import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;

public final class GOeBurstVisualPanel2 extends JPanel {

	/** Creates new form GOeBurstVisualPanel2 */
	public GOeBurstVisualPanel2() {

		initComponents();

		jSlider1.setMajorTickSpacing(1);
		jSlider1.setMinorTickSpacing(1);
		jSlider1.setFont(this.getFont());
		jSlider1.setMinimum(1);
		jSlider1.setMaximum(Math.max(GOeBurstCluster.MAXLV, 3));
		jSlider1.setValue(1);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(Integer.valueOf(1), new JLabel("SLV"));
		labelTable.put(Integer.valueOf(2), new JLabel("DLV"));
		labelTable.put(Integer.valueOf(3), new JLabel("TLV"));
		jSlider1.setLabelTable(labelTable);
		jSlider1.setPaintLabels(true);
		jSlider1.setSnapToTicks(true);
		jSlider1.setPaintTicks(true);
	}

	@Override
	public String getName() {
		return "Level";
	}

	public int getLevel() {
		return jSlider1.getValue();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jPanel2 = new javax.swing.JPanel();
                jPanel3 = new javax.swing.JPanel();
                jLabel1 = new javax.swing.JLabel();
                jPanel4 = new javax.swing.JPanel();
                jSlider1 = new javax.swing.JSlider();
                jPanel5 = new javax.swing.JPanel();
                jPanel1 = new javax.swing.JPanel();
                jLabel3 = new javax.swing.JLabel();

                setLayout(new java.awt.BorderLayout());

                jPanel2.setLayout(new java.awt.BorderLayout());

                jPanel3.setLayout(new java.awt.GridLayout(1, 0, 0, 8));

                org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(GOeBurstVisualPanel2.class, "GOeBurstVisualPanel2.jLabel1.text")); // NOI18N
                jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
                jPanel3.add(jLabel1);

                jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

                jPanel4.setLayout(new java.awt.GridLayout(1, 0, 0, 8));
                jPanel4.add(jSlider1);

                jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);
                jPanel2.add(jPanel5, java.awt.BorderLayout.EAST);

                jPanel1.setLayout(new java.awt.BorderLayout());

                org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(GOeBurstVisualPanel2.class, "GOeBurstVisualPanel2.jLabel3.text")); // NOI18N
                jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
                jPanel1.add(jLabel3, java.awt.BorderLayout.CENTER);

                jPanel2.add(jPanel1, java.awt.BorderLayout.SOUTH);

                add(jPanel2, java.awt.BorderLayout.PAGE_START);
        }// </editor-fold>//GEN-END:initComponents
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JPanel jPanel4;
        private javax.swing.JPanel jPanel5;
        private javax.swing.JSlider jSlider1;
        // End of variables declaration//GEN-END:variables
}
