package net.phyloviz.core.wizard;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import net.phyloviz.core.util.TypingFactory;
import org.openide.util.Lookup;

public final class LoadDataSetVisualPanel1 extends JPanel {

	private DefaultComboBoxModel typeListModel;

	/** Creates new form LoadDataSetVisualPanel1 */
	public LoadDataSetVisualPanel1() {

		typeListModel = new DefaultComboBoxModel();
		Collection<? extends TypingFactory> result = Lookup.getDefault().lookupAll(TypingFactory.class);
		Iterator<? extends TypingFactory> ir = result.iterator();
		while (ir.hasNext()) {
			typeListModel.addElement(ir.next());
		}

		initComponents();
                String[] initString =
                { "This is an editable JTextPane, ",            //regular
                  "another ",                                   //italic
                  "styled ",                                    //bold
                  "text ",                                      //small
                  "component, ",                                //large
                  "which supports embedded components..." + "\n",//regular
                  "...and embedded icons..." + "\n",         //regular
                  " ",                                          //icon
                  "\n" + "JTextPane is a subclass of JEditorPane that " +
                    "uses a StyledEditorKit and StyledDocument, and provides " +
                    "cover methods for interacting with those objects."
                 };

        String[] initStyles =
                { "regular", "italic", "bold", "small", "large",
                  "regular", "regular", "icon",
                  "regular"
                };


        StyledDocument doc = this.jTextPane1.getStyledDocument();
        addStylesToDocument(doc);

        try {
            for (int i=0; i < initString.length; i++) {
                doc.insertString(doc.getLength(), initString[i],
                                 doc.getStyle(initStyles[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }

	}

         protected void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);

        s = doc.addStyle("icon", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon pigIcon = createImageIcon("images/Pig.gif",
                                            "a cute pig");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }
       
        this.jScrollPane1.setBorder(new EmptyBorder(8,8,8,8));
 /*       s = doc.addStyle("button", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon soundIcon = createImageIcon("images/sound.gif",
                                              "sound icon");
  * *
  */
     /*   JButton button = new JButton();
        if (soundIcon != null) {
            button.setIcon(soundIcon);
        } else {
            button.setText("BEEP");
        }
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.setActionCommand(buttonString);
        button.addActionListener(this);
        StyleConstants.setComponent(s, button);
      *
      */
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = LoadDataSetVisualPanel1.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }



	@Override
	public String getName() {
		return "Dataset";
	}

	public String getDatasetName() {
		return jTextField1.getText();
	}

	public TypingFactory getTypingFactory() {
		return (TypingFactory) jComboBox1.getSelectedItem();
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
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel1.class, "LoadDataSetVisualPanel1.jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel1.class, "LoadDataSetVisualPanel1.jLabel2.text")); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 12, 2, 8));
        jPanel3.add(jLabel2);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.GridLayout(2, 0, 0, 8));

        jTextField1.setText(org.openide.util.NbBundle.getMessage(LoadDataSetVisualPanel1.class, "LoadDataSetVisualPanel1.jTextField1.text")); // NOI18N
        jPanel4.add(jTextField1);

        jComboBox1.setModel(typeListModel);
        jPanel4.add(jComboBox1);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);
        jPanel2.add(jPanel5, java.awt.BorderLayout.EAST);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextPane1.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jScrollPane1.setViewportView(jTextPane1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

}
