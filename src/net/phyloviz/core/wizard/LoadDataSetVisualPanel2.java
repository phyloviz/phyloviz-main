/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.core.wizard;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public final class LoadDataSetVisualPanel2 extends JPanel {

	/** Creates new form LoadDataSetVisualPanel2 */
	public LoadDataSetVisualPanel2() {
		initComponents();
	}

	@Override
	public String getName() {
		return "Typing Data";
	}

	public String getFilename() {
		return jTextField1.getText();
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
                jTextField1 = new javax.swing.JTextField();
                jPanel5 = new javax.swing.JPanel();
                jButton1 = new javax.swing.JButton();

                setLayout(new java.awt.BorderLayout());

                jPanel2.setLayout(new java.awt.BorderLayout());

                jPanel3.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

                org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel2.class, "LoadDataSetVisualPanel2.jLabel1.text")); // NOI18N
                jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
                jPanel3.add(jLabel1);

                jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

                jPanel4.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

                jTextField1.setText(org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel2.class, "LoadDataSetVisualPanel2.jTextField1.text")); // NOI18N
                jPanel4.add(jTextField1);

                jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

                jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 0));
                jPanel5.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

                org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel2.class, "LoadDataSetVisualPanel2.jButton1.text")); // NOI18N
                jButton1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton1ActionPerformed(evt);
                        }
                });
                jPanel5.add(jButton1);

                jPanel2.add(jPanel5, java.awt.BorderLayout.EAST);

                add(jPanel2, java.awt.BorderLayout.PAGE_START);
        }// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		if (fc == null)
			fc = new JFileChooser();
		fc.showDialog(this, "Open");
		if (fc.getSelectedFile() != null)
			jTextField1.setText(fc.getSelectedFile().getAbsolutePath());
	}//GEN-LAST:event_jButton1ActionPerformed

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton jButton1;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JPanel jPanel4;
        private javax.swing.JPanel jPanel5;
        private javax.swing.JTextField jTextField1;
        // End of variables declaration//GEN-END:variables

	private JFileChooser fc;
}