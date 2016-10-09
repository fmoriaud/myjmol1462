/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package compatibilitywithultimatepdb;

import org.junit.Test;
import jmolgui.UltiJmol1462;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;


public class OpenJmolTest {
    // -------------------------------------------------------------------
    // Junit tests
    // -------------------------------------------------------------------

    /**
     * Test opening a V3000 mol file and protonate the structure in the Jmol GUI.
     */
    @Test
    public void testOpenJmolLoadV3000GetV3000(){

        UltiJmol1462 ultiJmol1462 = new UltiJmol1462();


        URL url = OpenJmolTest.class.getClassLoader().getResource("2n0uV3000.mol");

        String filecontent = "";
        try {
            Path path = Paths.get(url.toURI());
            filecontent = ToolsTest.readFile(path.toString());
        } catch (URISyntaxException | IOException e1) {
            assertTrue(false);
        }
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        ultiJmol1462.openStringInline(filecontent);
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }

        String data = ultiJmol1462.jmolPanel.getViewer().getData("*", "V3000");
        assertTrue(data.contains("M  V30 COUNTS 750 767 0 0 0"));
    }
}
