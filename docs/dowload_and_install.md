# Download and install

PHYLOViZ core and several plugins are free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. 

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.

Certain source files distributed by the PHYLOViZ Team are under the terms of the GNU General Public License Version 3 with the following clarification and special exception, but only where PHYLOViZ Team has expressly included it in the particular source file's header.

```text
Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent modules,
and to copy and distribute the resulting executable under terms of your
choice, provided that you also meet, for each linked independent module,
the terms and conditions of the license of that module.  An independent
module is a module which is not derived from or based on this library.
If you modify this library, you may extend this exception to your version
of the library, but you are not obligated to do so.  If you do not wish
to do so, delete this exception statement from your version.
```

Code licensed under this license may be reused in commercial products provided that changes made directly in the sources - bug fixes or enhancements - must be contributed back to PHYLOViZ, but new source files (as in new plugins) which you write that link to PHYLOViZ code do not need to be.

Choose the appropriate version for your operating system or the .jar file. The OS specific versions already contain some memory specific parameters to enhance the software performance when using large datasets.

See details about available plugins and the licenses under which they are covered.

## Binaries

A cross-platform zip distribution package is [available](https://bitbucket.org/phyloviz/phyloviz-main/downloads).

Just unzip the package, enter the created directory and in the sub-directory 'bin/' run 'phyloviz.exe' or 'phyloviz64.exe' (Windows) or 'phyloviz' (Linux/MacOS) accordingly to your operating system.

*NOTE*: You may need to adjust some parameters in 'etc/phyloviz.conf' with respect to memory usage. These settings have a strong impact on visualization features. For instance, in Windows, you may achieve better results with:

```text
default_options="--branding phyloviz -J-Xss8M -J-Xms32m -J-Xmx1024M --laf javax.swing.plaf.metal.MetalLookAndFeel"
```

*IMPORTANTE NOTICE*: After installing always go to the "Help" menu and "Check for updates" to install any novel plugins or latest updates to PHYLOViZ software. The SNP analysis plugin is installed in this way to demonstrate the plugin capability.

## Source

All the Source code is available in the new code repository for in bitbucket.org. Check it out at <https://bitbucket.org/phyloviz/phyloviz-main>.

PHYLOViZ is built on top of the NetBeans Platform, thus we recommend NetBeans for the development of new plugins.

