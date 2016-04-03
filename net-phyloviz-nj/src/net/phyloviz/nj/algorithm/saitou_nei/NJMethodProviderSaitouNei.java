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

package net.phyloviz.nj.algorithm.saitou_nei;

import net.phyloviz.upgmanjcore.AbstractClusteringMethod;
import net.phyloviz.upgmanjcore.ClusteringMethodProvider;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.tree.NJLeafNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aplf
 */
@ServiceProvider(service = ClusteringMethodProvider.class)
public class NJMethodProviderSaitouNei implements ClusteringMethodProvider<NJLeafNode> {

    @Override
    public String toString() {
            return "Neighbor-Joining distance Saitou N. and Nei M. Criterion";
    }

    @Override
    public AbstractClusteringMethod<NJLeafNode> getMethod(TypingData<? extends Profile> td) {
        return new NJMethodSaitouNei();    
    }
}
