package net.phyloviz.njviewer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;

import org.freehep.graphics2d.PrintColor;
import org.freehep.graphics2d.VectorGraphics;

import org.freehep.graphicsbase.util.export.ExportDialog;
import org.freehep.graphicsbase.util.export.ExportFileType;
import prefuse.Display;

public class GTExportDialog extends ExportDialog {
	private static final long serialVersionUID = 1L;
	private GView gv;
	public GTExportDialog(GView gv) {
		this.gv = gv;
	}
	@Override
	protected boolean writeFile(Component component, ExportFileType t) throws IOException {
		DisplayImage comp = new DisplayImage((Display) component);
		boolean r = super.writeFile(comp, t);
		return r;
	}
	private class DisplayImage extends Component {
		Display d;
		public DisplayImage(Display d) {
			this.d = d;
			this.setSize(d.getSize());
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
		}
		@Override
		public void print(Graphics g) {

			VectorGraphics vg = VectorGraphics.create(g);
			vg.setColorMode(PrintColor.COLOR);
			vg.setBackground(Color.WHITE);

			// set up the display, render, then revert to normal settings
			boolean q = d.isHighQuality();
			d.setHighQuality(false);
			gv.getVisualization().run("static");
			d.setHighQuality(true);
			d.paintDisplay(vg, d.getSize());
			d.setHighQuality(q);
		}
	}

}
