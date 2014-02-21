package net.phyloviz.loadmlst.wizard;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import net.phyloviz.loadmlst.io.XMLParser;

public final class LoadMLSTVisualPanel2 extends JPanel {

	private int iIndex;
	private Task task;
	private String sData;
	private boolean bEmpty;
	private boolean bHasComplex;

	/** Creates new form LoadMLSTVisualPanel2 */
	public LoadMLSTVisualPanel2() {
		initComponents();

		jToggleButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				JToggleButton tb = (JToggleButton) ae.getSource();
				if (tb.isSelected()) {
					if (XMLParser.hasConnection()) {
						jProgressBar1.setString(null);
						task = new Task();
						task.addPropertyChangeListener(new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent pce) {
								if (pce.getPropertyName().equals("progress")) {
									int progress = (Integer) pce.getNewValue();
									jProgressBar1.setValue(progress);
								}
							}
						});
						task.execute();
					} else {
						jProgressBar1.setString(org.openide.util.NbBundle.getMessage(
								LoadMLSTVisualPanel2.class, "Connection.offline"));
						jToggleButton1.setSelected(false);
					}
				} else {
					task.cancel(true);
				}
			}
		});

		try {
			URL url = LoadMLSTVisualPanel2.class.getResource("LoadMLSTVisualPanel2.html");
			jEditorPane1.setEditorKit(new HTMLEditorKit());
			jEditorPane1.setPage(url);
			Font font = UIManager.getFont("Label.font");
			String bodyRule = "body { font-family: " + font.getFamily() + "; "
					+ "font-size: " + font.getSize() + "pt; width: " + jEditorPane1.getSize().width + "px;}";
			((HTMLDocument) jEditorPane1.getDocument()).getStyleSheet().addRule(bodyRule);
		} catch (IOException e) {
			Logger.getLogger(LoadMLSTVisualPanel2.class.getName()).log(Level.WARNING,
					e.getLocalizedMessage());
		}

	}

	public void setDatabase(int iDBIndex, String sDBName) {
		// Form initialization
		iIndex = iDBIndex;
		jlDatasetName.setText(sDBName);
		jlSize.setText(XMLParser.getParser().getProfileCount(iIndex) + " STs");
		String s = "";
		for (String locus : XMLParser.getParser().getLoci(iIndex)) {
			s += locus + "  ";
		}
		jlProfile.setText(s);
		bEmpty = true;

		// ProgressBar initialization
		jProgressBar1.setMinimum(0);
		jProgressBar1.setMaximum(100);
		jProgressBar1.setStringPainted(true);
		jProgressBar1.setString(null);
		jProgressBar1.setValue(0);
		jToggleButton1.setSelected(false);
		jToggleButton1.setEnabled(true);
	}

	@Override
	public String getName() {
		return "Typing Data";
	}

	public StringReader getTypingData() {
		return new StringReader(sData);
	}

	public boolean isEmpty() {
		return bEmpty;
	}

	private String filterHeader(String header) {
		if (header.endsWith("clonal_complex")) {
			bHasComplex = true;
			return header.replaceAll("\tclonal_complex$", "");
		}
		bHasComplex = false;
		return header;
	}

	private String filterData(String line) {
		if (bHasComplex) {
			return line.replaceAll("\t[^\t]*$", "");
		}
		return line;
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
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        jlDatasetName = new javax.swing.JLabel();
        jlProfile = new javax.swing.JLabel();
        jlSize = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jEditorPane1 = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridLayout(4, 0, 0, 8));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(LoadMLSTVisualPanel2.class, "LoadMLSTVisualPanel2.jLabel2.text")); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel2);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(LoadMLSTVisualPanel2.class, "LoadMLSTVisualPanel2.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(LoadMLSTVisualPanel2.class, "LoadMLSTVisualPanel2.jLabel3.text")); // NOI18N
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel3);

        org.openide.awt.Mnemonics.setLocalizedText(jToggleButton1, org.openide.util.NbBundle.getMessage(LoadMLSTVisualPanel2.class, "LoadMLSTVisualPanel2.jToggleButton1.text")); // NOI18N
        jToggleButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jToggleButton1);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.GridLayout(4, 0, 0, 8));

        org.openide.awt.Mnemonics.setLocalizedText(jlDatasetName, org.openide.util.NbBundle.getMessage(LoadMLSTVisualPanel2.class, "LoadMLSTVisualPanel2.jlDatasetName.text")); // NOI18N
        jPanel4.add(jlDatasetName);

        org.openide.awt.Mnemonics.setLocalizedText(jlProfile, org.openide.util.NbBundle.getMessage(LoadMLSTVisualPanel2.class, "LoadMLSTVisualPanel2.jlProfile.text")); // NOI18N
        jPanel4.add(jlProfile);

        org.openide.awt.Mnemonics.setLocalizedText(jlSize, org.openide.util.NbBundle.getMessage(LoadMLSTVisualPanel2.class, "LoadMLSTVisualPanel2.jlSize.text")); // NOI18N
        jPanel4.add(jlSize);

        jProgressBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel4.add(jProgressBar1);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jEditorPane1.setBackground(jPanel2.getBackground());
        jEditorPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jEditorPane1.setEditable(false);
        jEditorPane1.setMaximumSize(new java.awt.Dimension(200, 200));
        add(jEditorPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel jlDatasetName;
    private javax.swing.JLabel jlProfile;
    private javax.swing.JLabel jlSize;
    // End of variables declaration//GEN-END:variables

	class Task extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			try {
				URL url = new URL(XMLParser.getParser().getProfileURL(iIndex));
				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));
				sData = filterHeader(in.readLine());
				String line;
				int i = 1;
				int iMax = XMLParser.getParser().getProfileCount(iIndex);
				while ((line = in.readLine()) != null) {
					if (isCancelled()) {
						bEmpty = true;
						jProgressBar1.setString("Canceled!");
						return null;
					}
					sData += filterData("\n" + line);
					int percent = i * 100 / iMax;
					if (i <= iMax) {
						setProgress(percent);
						jProgressBar1.setString(i + " of " + iMax + " STs (" + percent + "%)");
					}
					i++;
				}
				bEmpty = false;
				jToggleButton1.setEnabled(false);
				jProgressBar1.setString("Done!");
			} catch (IOException e) {
				Logger.getLogger(LoadMLSTVisualPanel2.class.getName()).log(Level.WARNING,
						e.getLocalizedMessage());
			}
			return null;
		}

		@Override
		public void done() {
			setCursor(null); //turn off the wait cursor
		}
	}
}