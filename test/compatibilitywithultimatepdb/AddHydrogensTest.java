package compatibilitywithultimatepdb;

import org.jmol.minimize.Minimizer;
import org.junit.Test;
import ultimatepdb.UltiJmol1462;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 08/10/16.
 */
public class AddHydrogensTest {

    @Test
    public void testAreHydrogenAdded() {

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

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String script = getScriptAddHydrogens();
        ultiJmol1462.jmolPanel.evalString(script);

        boolean convergenceReached = false;

        Float energy = 1E8f;
        int countIteration = 0;
        int maxIteration = 20;
        boolean goAhead = true;
        while (countIteration <= maxIteration && goAhead == true && ultiJmol1462.jmolPanel.getViewer().areHydrogenAdded() == false) {

            try {
                Thread.sleep(4000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            countIteration += 1;
            // Energy there is a relative indicator
            // Only relates to what is unfixed in the minimization
            float currentEnergy = getEnergyBiojavaJmolNewCode(ultiJmol1462);

            System.out.println("currentEnergy = " + currentEnergy);

            // when too high then I should give up
            if (currentEnergy > 1E8) {
                //System.out.println("Minimization is aborted as energy is > 1E8 ");
                //return null;
            }

            if (Math.abs(currentEnergy - energy) < 5.0) {
                goAhead = false;
            }
            energy = currentEnergy;
        }

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {

        }

        String data = ultiJmol1462.jmolPanel.getViewer().getData("*", "V3000");

        assertTrue(data.contains("M  V30 COUNTS 5593 5658 0 0 0"));

        System.out.println();
    }


    private Float getEnergyBiojavaJmolNewCode(UltiJmol1462 ultiJMol) {

        Float energy = waitMinimizationEnergyAvailable(2, ultiJMol);
        return energy;
    }



    private Float waitMinimizationEnergyAvailable(int waitTimeSeconds, UltiJmol1462 ultiJMol) {

        int maxIteration = 20;
        int countIteration = 0;

        Minimizer minimizer = ultiJMol.jmolPanel.getViewer().getMinimizer(true);

        while (minimizer == null || minimizer.getMinimizationEnergy() == null) {
            try {
                Thread.sleep(waitTimeSeconds * 1000);
                countIteration += 1;
                System.out.println(countIteration);
                //System.out.println(countIteration);
                if (countIteration > maxIteration) {
                    String message = "Wait for Minimization Energy to be available failed because too many iterations :  ";
                }
            } catch (InterruptedException e) {
                String message = "Wait for Minimization Energy to be available failed because of Exception";
            }
            minimizer = ultiJMol.jmolPanel.getViewer().getMinimizer(true);
        }
        return minimizer.getMinimizationEnergy();
    }


    public static String getScriptAddHydrogens() {

        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 20\n");
        sb.append("minimize energy ADDHYDROGENS\n");

        String script = sb.toString();
        return script;
    }
}
