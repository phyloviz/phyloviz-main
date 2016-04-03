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
