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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package net.phyloviz.welcome;

import java.util.Set;
import org.openide.modules.ModuleInstall;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Checks the feedback survey.
 */
public class Installer extends ModuleInstall implements Runnable {

    @Override public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(this);
    }

    @Override
    public boolean closing() {
        WelcomeComponent topComp = null;
        Set<TopComponent> tcs = TopComponent.getRegistry().getOpened();
        for (TopComponent tc: tcs) {
            if (tc instanceof WelcomeComponent) {                
                topComp = (WelcomeComponent) tc;               
                break;
            }
        }
        if( WelcomeOptions.getDefault().isShowOnStartup() ) {
            if(topComp == null){            
                topComp = WelcomeComponent.findComp();
            }
            //activate welcome screen at shutdown to avoid editor initialization
            //before the welcome screen is activated again at startup
            topComp.open();
            topComp.requestActive();
        } else if( topComp != null ) {
            topComp.close();
        }
        return super.closing();
    }

    public void run() {
        FeedbackSurvey.start();
    }
}
