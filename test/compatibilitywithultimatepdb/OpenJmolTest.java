package compatibilitywithultimatepdb;

import org.junit.Test;
import ultimatepdb.UltiJmol1462;

import java.io.BufferedReader;
import java.io.FileReader;
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


    @Test
    public void testOpenJmolLoadV3000GetV3000(){

        UltiJmol1462 ultiJmol1462 = new UltiJmol1462();


        URL url = OpenJmolTest.class.getClassLoader().getResource("1di9v3000.mol");

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
        assertTrue(data.contains("M  V30 COUNTS 2798 2863 0 0 0"));
    }

}
