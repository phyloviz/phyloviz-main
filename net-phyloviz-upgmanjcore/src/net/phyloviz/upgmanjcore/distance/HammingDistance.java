/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.distance;

import net.phyloviz.algo.Edge;
import net.phyloviz.core.data.Profile;

/**
 *
 * @author Marta Nascimento
 */
public class HammingDistance extends ClusteringDistance {

    @Override
    public int level(Profile px, Profile py) {
        int diffs = 0;

        for (int i = 0; i < px.profileLength(); i++) {
            if (px.getValue(i).compareTo(py.getValue(i)) != 0) {
                diffs++;
            }
        }

        return diffs;
    }

    @Override
    public int level(Edge<Profile> e) {
        return level(e.getU(), e.getV());
    }

    @Override
    public String toString() {
        return "Hamming Distance";
    }
}
