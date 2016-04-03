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

package net.phyloviz.goeburst.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.upgmanjcore.json.JsonSchemaValidator;

/**
 *
 * @author martanascimento
 */
public class GOeBurstSchemaValidator extends JsonSchemaValidator{

    private static final String[] nodes = new String[]{"id", "profile", "graph-lvs", "group-lvs", "slvs", "dlvs"};
    private static final String[] edges = new String[]{"id", "u", "v", "visible", "maxTie"};
    private static final String[] groups = new String[]{"id", "edges", "nodes", "maxLVs", "maxStId"};
    private static final String[] edgeTieStats = new String[]{"group", "ne", "fne", "xLV", "withoutTies"};

    public GOeBurstSchemaValidator() {
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("nodes", nodes);
        dataIds.put("edges", edges);
        dataIds.put("groups", groups);
        dataIds.put("edgeTieStats", edgeTieStats);
        setDataIds(dataIds);
    }

    public boolean validate(String directory, String filename) throws IOException {
        String dfn = org.openide.util.NbBundle.getMessage(this.getClass(), "schema");
        URL url = this.getClass().getResource(dfn);
        InputStream stream = url.openStream();
        return validate(stream, directory, filename);
    }
}
