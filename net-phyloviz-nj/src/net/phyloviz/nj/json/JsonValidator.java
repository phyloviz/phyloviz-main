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

package net.phyloviz.nj.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.upgmanjcore.json.JsonSchemaValidator;

/**
 *
 * @author Adriano
 */
public class JsonValidator extends JsonSchemaValidator{

    private static final String[] leafIds = new String[]{"id", "profile", "x", "y"};
    private static final String[] unionIds = new String[]{"id", "left", "distanceLeft", "right", "distanceRight", "x", "y"};
    private static final String[] rootIds = new String[]{"distance", "left", "right"};
    
    public JsonValidator(){
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("leaf", leafIds);
        dataIds.put("union", unionIds);
        dataIds.put("root", rootIds);
        setDataIds(dataIds);
    }

    public boolean validate(String directory, String filename) throws IOException {
        String dfn = org.openide.util.NbBundle.getMessage(this.getClass(), "schema");
        URL url = this.getClass().getResource(dfn);
        InputStream stream = url.openStream();
        return validate(stream, directory, filename);
    }
}
