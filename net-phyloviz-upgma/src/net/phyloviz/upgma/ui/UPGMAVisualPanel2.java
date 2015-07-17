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

package net.phyloviz.upgma.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import net.phyloviz.upgmanjcore.AbstractClusteringMethod;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.upgmanjcore.ClusteringMethodProvider;
import net.phyloviz.upgma.HierarchicalClusteringMethod;
import org.openide.util.Lookup;

public final class UPGMAVisualPanel2 extends JPanel {

	private DefaultComboBoxModel typeListModel;

	/** Creates new form GOeBurstVisualPanel1 */
	public UPGMAVisualPanel2(TypingData<? extends Profile> td) {

		typeListModel = new DefaultComboBoxModel();
		Collection<? extends ClusteringMethodProvider> result = Lookup.getDefault().lookupAll(ClusteringMethodProvider.class);
		Iterator<? extends ClusteringMethodProvider> ir = result.iterator();
		while (ir.hasNext()) {
                    AbstractClusteringMethod cm = ir.next().getMethod(td);
                    if (cm != null && cm instanceof HierarchicalClusteringMethod){
                    	typeListModel.addElement(cm);
                    }
		}

		initComponents();
		String dfn = org.openide.util.NbBundle.getMessage(typeListModel.getSelectedItem().getClass(), "AbstractMethod.description");
		URL url = typeListModel.getSelectedItem().getClass().getResource(dfn);
		if (url != null) {
					try {
			jEditorPane1.setPage(url);
			} catch (IOException e) {
				// Do nothing...
				System.err.println(e.getMessage());
			}

			Font font = UIManager.getFont("Label.font");
			String bodyRule = "body { font-family: " + font.getFamily() + "; "
				+ "font-size: " + font.getSize() + "pt; width: " + jPanel2.getParent().getSize().width + "px;}";
			((HTMLDocument) jEditorPane1.getDocument()).getStyleSheet().addRule(bodyRule);
		}
		
		jComboBox1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String dfn = org.openide.util.NbBundle.getMessage(typeListModel.getSelectedItem().getClass(), "AbstractMethod.description");
				URL url = typeListModel.getSelectedItem().getClass().getResource(dfn);
				if (url != null) {
					try {
						jEditorPane1.setPage(url);
					} catch (IOException e) {
						// Do nothing...
						System.err.println(e.getMessage());
					}

					Font font = UIManager.getFont("Label.font");
					String bodyRule = "body { font-family: " + font.getFamily() + "; "
						+ "font-size: " + font.getSize() + "pt; width: " + jPanel2.getParent().getSize().width + "px;}";
					((HTMLDocument) jEditorPane1.getDocument()).getStyleSheet().addRule(bodyRule);
				}
			}
		});

		
	}

	@Override
	public String getName() {
		return "Method";
	}

	public AbstractClusteringMethod getMethod() {
		return (AbstractClusteringMethod) jComboBox1.getSelectedItem();
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
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridLayout(1, 0, 0, 8));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(UPGMAVisualPanel2.class, "UPGMAVisualPanel2.jLabel2.text")); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel2);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.GridLayout(1, 0, 0, 8));

        jComboBox1.setModel(typeListModel);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel4.add(jComboBox1);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);
        jPanel2.add(jPanel5, java.awt.BorderLayout.EAST);

        jPanel1.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(UPGMAVisualPanel2.class, "UPGMAVisualPanel2.jLabel3.text")); // NOI18N
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jPanel1.add(jLabel3, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel1, java.awt.BorderLayout.SOUTH);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jEditorPane1.setEditable(false);
        jEditorPane1.setBackground(new java.awt.Color(240, 240, 240));
        jEditorPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jEditorPane1.setContentType(org.openide.util.NbBundle.getMessage(UPGMAVisualPanel2.class, "UPGMAVisualPanel1.jEditorPane1.contentType")); // NOI18N
        jEditorPane1.setMaximumSize(new java.awt.Dimension(200, 200));
        jScrollPane1.setViewportView(jEditorPane1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
