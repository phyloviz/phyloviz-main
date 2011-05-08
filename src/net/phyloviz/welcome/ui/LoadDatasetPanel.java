/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package net.phyloviz.welcome.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.phyloviz.welcome.content.BundleSupport;
import net.phyloviz.welcome.content.Constants;
import net.phyloviz.welcome.content.LinkButton;
import net.phyloviz.welcome.content.Utils;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author S. Aubrecht
 */
class LoadDatasetPanel extends JPanel implements Constants {

    public LoadDatasetPanel( boolean showInstallPlugins ) {
        super( new GridBagLayout() );
        setOpaque(false);
        if( showInstallPlugins ) {
            InstallConfig ic = InstallConfig.getDefault();

            if( ic.isErgonomicsEnabled() ) {
                addLoadDatasets(BundleSupport.getLabel("LoadDatasetsFullIDE"), BundleSupport.getLabel("LoadDatasetsDescrFullIDE"));
            } else {
                addLoadDatasets(BundleSupport.getLabel("LoadDatasets"), BundleSupport.getLabel("LoadDatasetsDescr"));
            }
            
        } else {
            addActivateFeatures( BundleSupport.getLabel("ActivateFeaturesFullIDE"), BundleSupport.getLabel("ActivateFeaturesDescrFullIDE"));
        }
    }

    private void addActivateFeatures( String label, String description ) {
        LinkButton b = new LinkButton(label, Utils.getColor(COLOR_HEADER), true, "ActivateFeatures" ) { //NOI18N

            @Override
            public void actionPerformed(ActionEvent e) {
                logUsage();
               	try {
		    DataObject dobj = DataObject.find(FileUtil.getConfigFile("WelcomePage/GettingStartedLinks/sampleproject.instance"));
		    InstanceCookie.Of instCookie = dobj.getCookie(InstanceCookie.Of.class);
                    if( null != instCookie && instCookie.instanceOf( Action.class ) ) {
                	Action res = (Action) instCookie.instanceCreate();
			res.actionPerformed(e);
		    }
		} catch (Exception ex) {
                    Logger.getLogger(LoadDatasetPanel.class.getName()).log(Level.INFO, null, ex);
		}
            }
        };
        b.setFont(GET_STARTED_FONT);
        add( b, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5), 0, 0));
        add( new JLabel(description), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(20,9,5,5), 0, 0));
        add( new JLabel(), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
    }

    private void addLoadDatasets( String label, String description ) {
        LinkButton b = new LinkButton(label, Utils.getColor(COLOR_HEADER), true, "LoadDatasets") { //NOI18N

            @Override
            public void actionPerformed(ActionEvent e) {
                logUsage();
               	try {
		    DataObject dobj = DataObject.find(FileUtil.getConfigFile("WelcomePage/GettingStartedLinks/sampleproject.instance"));
		    InstanceCookie.Of instCookie = dobj.getCookie(InstanceCookie.Of.class);
                    if( null != instCookie && instCookie.instanceOf( Action.class ) ) {
                	Action res = (Action) instCookie.instanceCreate();
			res.actionPerformed(e);
		    }
		} catch (Exception ex) {
                    Logger.getLogger(LoadDatasetPanel.class.getName()).log(Level.INFO, null, ex);
		}
            }
        };
        b.setFont(GET_STARTED_FONT);
        add( b, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5), 0, 0));
        add( new JLabel(description), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(20,9,5,5), 0, 0));
        add( new JLabel(), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
    }
}
