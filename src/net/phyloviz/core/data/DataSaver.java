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

	private DataModel dm;

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
		} else
			throw new FileNotFoundException("Unknown file!");
	}

	private void save(File f) throws FileNotFoundException {

		PrintWriter pw = new PrintWriter(f);
		TableModel tm = dm.tableModel();

		pw.print(tm.getColumnName(0));
		for (int i = 1; i < tm.getColumnCount(); i++)
			pw.print("\t" + tm.getColumnName(i));
		pw.println();

		for (int k = 0; k < tm.getRowCount(); k++) {
			pw.print(tm.getValueAt(k, 0));
			for (int i = 1; i < tm.getColumnCount(); i++)
				pw.print("\t" + tm.getValueAt(k, i));
			pw.println();
		}

		pw.close();
	}

	@Override
	public String toString() {
		return dm.toString();
	}
}
