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

package net.phyloviz.project;

/**
 *
 * @author Marta Nascimento
 */
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ProjectFactory.class)
public class PhylovizProjectFactory implements ProjectFactory {

    public static final String PROJECT_PROPFILE = "config.properties.pviz";

    //Specifies when a project is a project, i.e.,
    //if ".properties" is present in a folder:
    @Override
    public boolean isProject(FileObject projectDirectory) {
        return projectDirectory.getFileObject(PROJECT_PROPFILE) != null;
    }

    //Specifies when the project will be opened, i.e., if the project exists:
    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new PhylovizProject(dir, state) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {

    /*    FileObject projectRoot = project.getProjectDirectory();
        if (projectRoot.getFileObject("testproj") == null) {
            throw new IOException("Project dir " + projectRoot.getPath() + " deleted,"
                    + " cannot save project");
        }

    //Force creation of the scenes/ dir if it was deleted
        //((PhylovizProject) project).getScenesFolder(true);
    //Find the Properties file pvproject/project.properties,
        //creating it if necessary
        String propsPath = "testproj" + "/" + PROJECT_PROPFILE;
        FileObject propertiesFile = projectRoot.getFileObject(propsPath);
        if (propertiesFile == null) {
            //Recreate the Properties file if needed
            propertiesFile = projectRoot.createData(propsPath);
        }

        Properties properties = (Properties) project.getLookup().lookup(Properties.class);

        File f = FileUtil.toFile(propertiesFile);
        properties.store(new FileOutputStream(f), "NetBeans Phyloviz Project Properties");
    */
    }

}
