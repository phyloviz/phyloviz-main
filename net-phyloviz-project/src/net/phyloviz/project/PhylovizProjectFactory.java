/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    public static final String PROJECT_PROPFILE = "config.properties";

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
