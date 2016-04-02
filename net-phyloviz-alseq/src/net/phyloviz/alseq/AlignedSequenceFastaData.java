/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.alseq;

import net.phyloviz.core.data.DataSaver;
import net.phyloviz.core.data.TypingData;

/**
 *
 * @author martanascimento
 */
public class AlignedSequenceFastaData extends TypingData{

    public AlignedSequenceFastaData(String[] ha) {
        super(ha);
    }

    public AlignedSequenceFastaData(int nColumns) {
        super(nColumns);
    }

    @Override
    public DataSaver getSaver() {
        if (saver == null) {
            saver = new AlignedSequenceFastaDataSaver(this);
        }

        return saver;
    }
}
