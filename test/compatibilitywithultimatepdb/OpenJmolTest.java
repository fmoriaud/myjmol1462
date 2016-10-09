/*
*                    ultimatepdb development code
*
* This code may be freely distributed and modified under the
* terms of the GNU Lesser General Public Licence.  This should
* be distributed with the code.  If you do not have a copy,
* see:
*
*      http://www.gnu.org/copyleft/lesser.html
*
* Copyright for this code is held jointly by the individual
* authors.  These should be listed in @author doc comments.
*
* For more information on the BioJava project and its aims,
* or to join the biojava-l mailing list, visit the home page
* at:
*
*      http://www.ultimatepdb.org/
*
* Created on Oct 8, 2016
* @author Fabrice Moriaud
*
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

/**
 * Created by Fabrice on 10/09/16.
 */


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
