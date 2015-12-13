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
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class PhylovizProject implements Project {

    private final FileObject projectDir;
    private Lookup lkp;

    public PhylovizProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                // register your features here
                this,
                new Info(),
                new PhylovizProjectLogicalView(this),
                new CustomizerProvider() {
                    @Override
                    public void showCustomizer() {
                        JOptionPane.showMessageDialog(
                                null,
                                getProjectDirectory().getName());
                    }

                }
            });
        }
        return lkp;
    }

    private final class Info implements ProjectInformation {

        @StaticResource()
        public static final String CUSTOMER_ICON = "net/phyloviz/project/ProjectIcon.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(CUSTOMER_ICON));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public Project getProject() {
            return PhylovizProject.this;
        }
    }
}
