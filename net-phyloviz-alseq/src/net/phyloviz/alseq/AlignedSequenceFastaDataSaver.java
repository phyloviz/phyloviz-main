/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.alseq;

import javax.swing.table.TableModel;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSaver;

/**
 *
 * @author martanascimento
 */
public class AlignedSequenceFastaDataSaver extends DataSaver {

    public AlignedSequenceFastaDataSaver(DataModel dm) {
        super(dm);
    }

    @Override
    public String toSave() {
        StringBuilder sb = new StringBuilder();
        TableModel tm = dm.tableModel();

        for (int k = 0; k < tm.getRowCount(); k++) {
            sb.append(">").append(tm.getValueAt(k, 0)).append("\n");
            for (int i = 1; i < tm.getColumnCount(); i++) {
                sb.append(tm.getValueAt(k, i));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
