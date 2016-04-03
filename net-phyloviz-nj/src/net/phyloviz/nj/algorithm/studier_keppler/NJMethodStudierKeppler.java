/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

package net.phyloviz.nj.algorithm.studier_keppler;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;

public class NJMethodStudierKeppler implements AgglomerativeClusteringMethod {
    
    @Override
    public String toString() {
        return "Studier-Keppler Criterion";
    }

    @Override
    public NJ getClusteringMethod(TypingData<? extends Profile> inTd, ClusteringDistance<NJLeafNode> inAd, OutputPanel op) {
        return new NJStudierKeppler(inTd, inAd, op);
    }
}
