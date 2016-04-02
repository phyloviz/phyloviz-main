/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.snp;

import net.phyloviz.core.data.DataSaver;
import net.phyloviz.core.data.TypingData;

/**
 *
 * @author martanascimento
 */
public class SNPoldData extends TypingData{

    public SNPoldData(String[] ha) {
        super(ha);
    }

    public SNPoldData(int nColumns) {
        super(nColumns);
    }

    @Override
    public DataSaver getSaver() {
        if (saver == null) {
            saver = new SNPoldDataSaver(this);
        }

        return saver;
    }
}
