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

package net.phyloviz.gtview.ui;

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

			//vg.dispose();
		}
	}

}
