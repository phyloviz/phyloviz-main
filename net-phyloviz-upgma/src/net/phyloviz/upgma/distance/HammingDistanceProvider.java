/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.distance;

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.DistanceProvider;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.upgma.tree.UPGMALeafNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Marta Nascimento
 */
@ServiceProvider(service = DistanceProvider.class)
public class HammingDistanceProvider implements DistanceProvider<UPGMALeafNode> {

    @Override
    public AbstractDistance<UPGMALeafNode> getDistance(TypingData<? extends Profile> td) {
        return new HammingDistance();
    }

    @Override
    public AbstractDistance<UPGMALeafNode> getDistance(TypingData<? extends Profile> td, int maxLevel) {
        return getDistance(td);
    }
}
