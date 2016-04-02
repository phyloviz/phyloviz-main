/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.snp;

import javax.swing.table.TableModel;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSaver;

/**
 *
 * @author martanascimento
 */
public class SNPoldDataSaver extends DataSaver {

    public SNPoldDataSaver(DataModel dm) {
        super(dm);
    }

    @Override
    public String toSave() {
        StringBuilder sb = new StringBuilder();
        TableModel tm = dm.tableModel();

        sb.append(tm.getColumnName(1));
        for (int i = 2; i < tm.getColumnCount(); i++) {
            sb.append("\t" + tm.getColumnName(i));
        }
        sb.append("\n");

        for (int k = 0; k < tm.getRowCount(); k++) {
            sb.append(tm.getValueAt(k, 1));
            for (int i = 2; i < tm.getColumnCount(); i++) {
                sb.append("\t" + tm.getValueAt(k, i));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
