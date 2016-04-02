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
package net.phyloviz.core.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.table.TableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileChooserBuilder;

public class DataSaver implements SaveCookie {

    protected DataModel dm;

    public DataSaver(DataModel dm) {
        this.dm = dm;
    }

    @Override
    public void save() throws IOException {

        String title = "Saving " + this.toString() + "...";
        File f = new FileChooserBuilder(DataSaver.class).setTitle(title).showSaveDialog();
        if (f != null) {
            try {
                if (!f.exists()) {
                    if (!f.createNewFile()) {
                        String failMsg = "Unable to create " + f.getName();
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(failMsg));
                        return;
                    }
                } else {
                    String overwriteMessage = "Overwriting " + f.getName();
                    Object userChose = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(overwriteMessage));
                    if (NotifyDescriptor.CANCEL_OPTION.equals(userChose)) {
                        return;
                    }
                }

                save(f.getAbsoluteFile());
            } catch (IOException ioe) {
                String failMsg = "Error: " + ioe.getMessage();
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(failMsg));
            }
        } else {
            throw new FileNotFoundException("Unknown file!");
        }
    }

    private void save(File f) throws FileNotFoundException {

        try (PrintWriter pw = new PrintWriter(f)) {
            pw.write(toSave());
        }
    }

    public String toSave() {
        StringBuilder sb = new StringBuilder();
        TableModel tm = dm.tableModel();

        sb.append(tm.getColumnName(0));
        for (int i = 1; i < tm.getColumnCount(); i++) {
            sb.append("\t" + tm.getColumnName(i));
        }
        sb.append("\n");

        for (int k = 0; k < tm.getRowCount(); k++) {
            sb.append(tm.getValueAt(k, 0));
            for (int i = 1; i < tm.getColumnCount(); i++) {
                sb.append("\t" + tm.getValueAt(k, i));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return dm.toString();
    }
}
