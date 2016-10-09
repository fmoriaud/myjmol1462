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

import org.jmol.minimize.Minimizer;
import org.junit.Test;
import jmolgui.UltiJmol1462;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class NewMethodsAddedToJmolTest {

    // -------------------------------------------------------------------
    // Junit tests
    // -------------------------------------------------------------------

    /**
     * This test protonates a structure in the viewer
     * It uses the two methods that were introduced in Jmol in this project:
     * minimizer.getMinimizationEnergy()
     * viewer.areHydrogenAdded()
     */
    @Test
    public void testAreHydrogenAdded() {

        UltiJmol1462 ultiJmol1462 = new UltiJmol1462();

        loadStructure(ultiJmol1462, "2n0uV3000.mol");

        addHydrogens(ultiJmol1462);

        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String data = ultiJmol1462.jmolPanel.getViewer().getData("*", "V3000");

        assertTrue(data.contains("M  V30 COUNTS 1508 1525 0 0 0"));

        ultiJmol1462.frame.dispose();
    }


    /**
     * This test minimize a structure with a forcefield in the viewer
     * It uses the two methods that were introduced in Jmol in this project:
     * minimizer.getMinimizationEnergy()
     * viewer.areHydrogenAdded()
     */
    @Test
    public void testMinimize() {

        UltiJmol1462 ultiJmol1462 = new UltiJmol1462();
        loadStructure(ultiJmol1462, "complex.mol");

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String script = getScriptMinimizationLigandInTarget();

        ultiJmol1462.jmolPanel.evalString(script);

        boolean convergenceReached = false;

        Float energy = 1E8f;
        int countIteration = 0;
        int maxIteration = 20;
        boolean goAhead = true;
        while (countIteration <= maxIteration && goAhead == true) {

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

            if (Math.abs(currentEnergy - energy) < 5.0) {
                goAhead = false;
            }
            energy = currentEnergy;
        }

        System.out.println("did " + countIteration + " iterations");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (countIteration <= maxIteration == false) {
            convergenceReached = false;
        } else {
            convergenceReached = true;
        }
        Float finalEnergy = waitMinimizationEnergyAvailable(2, ultiJmol1462);


        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // as i dont know what is does in jmol, I take the energy before stoping
        ultiJmol1462.jmolPanel.evalString("minimize stop");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("convergence reached " + convergenceReached);
        System.out.println("final energy = " + finalEnergy);

        //String structureV3000 = ultiJmol1462.jmolPanel.getViewer().getData("*", "V3000");

        ultiJmol1462.jmolPanel.evalString("minimize clear");
        ultiJmol1462.frame.dispose();
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------

    /**
     * Returns a script to minimize a ligand within a target
     * The parameters atomCountTarget and atomIds were determined running ultimatepdb on a test case.
     * @return
     */
    public static String getScriptMinimizationLigandInTarget() {

        int atomCountTarget = 573;
        List<Integer> atomIds = new ArrayList<Integer>(Arrays.asList(17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 90, 91, 92, 93, 95, 96, 97, 98, 99, 100, 101, 122, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 191, 192, 200, 201, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 215, 216, 219, 250, 257, 260, 261, 262, 286, 290, 291, 292, 293, 294, 295, 296, 298, 300, 301, 302, 304, 305, 306, 310, 311, 312, 313, 314, 316, 317, 318, 321, 323, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 353, 357, 358, 359, 360, 361, 362, 363, 365, 366, 369, 370, 371, 372, 374, 376, 377, 378, 379, 380, 381, 383, 384, 385, 386, 387, 389, 390, 391, 392, 393, 394, 395, 396, 399, 526, 527, 533, 534, 535, 536, 537, 540, 541, 542, 544, 545, 546, 547, 548, 549, 550, 551, 552, 553, 564));

        // Build script
        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 50\n");
        sb.append("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");
        String selectStringTargetNonHydrogens = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and not { hydrogen }";
        String selectStringTargetHydrogens = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and { hydrogen }";

        StringBuilder sbAtomIds = new StringBuilder();
        for (int i = 0; i < atomIds.size(); i++) {
            sbAtomIds.append("atomno = " + atomIds.get(i));
            if (i != atomIds.size() - 1) {
                sbAtomIds.append(" or ");
            }
        }

        String selectStringTargetNotToFix = sbAtomIds.toString();
        String selectStringTargetToFix = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and not { " + selectStringTargetNotToFix + " }";

        if (atomIds.isEmpty()) {
            selectStringTargetToFix = "atomno > 0 and atomno < " + (atomCountTarget + 1);
            selectStringTargetNotToFix = null;
        }

        String selectLigand = "{atomno > " + (atomCountTarget) + "}";
        sb.append("select {" + selectStringTargetToFix + "}\n");
        sb.append("spacefill 50\n");
        sb.append("select {" + selectLigand + "}\n");
        sb.append("spacefill 400\n");
        if (selectStringTargetNotToFix != null) {
            sb.append("select {" + selectStringTargetNotToFix + "}\n");
            sb.append("spacefill 400\n");
        }
        sb.append("minimize FIX {" + selectStringTargetToFix + "} select {*}\n");

        String script = sb.toString();

        return script;
    }


    /**
     * Returns the energy of the unfix atoms in the viewer
     *
     * @param ultiJMol
     * @return
     */
    private Float getEnergyBiojavaJmolNewCode(UltiJmol1462 ultiJMol) {

        Float energy = waitMinimizationEnergyAvailable(2, ultiJMol);
        return energy;
    }


    /**
     * Returns the energy of the unfix atoms in the viewer and waits that the energy is available
     *
     * @param waitTimeSeconds
     * @param ultiJMol
     * @return
     */
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


    /**
     * Returns the Jmol script to add hydrogens
     *
     * @return
     */
    private String getScriptAddHydrogens() {

        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 20\n");
        sb.append("minimize energy ADDHYDROGENS\n");

        String script = sb.toString();
        return script;
    }



    /**
     * Load a test PDB file from resources
     *
     * @param ultiJmol1462
     */
    private void loadStructure(UltiJmol1462 ultiJmol1462, String fileName) {
        URL url = OpenJmolTest.class.getClassLoader().getResource(fileName);

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
    }

    /**
     * Adds hydrogens in Jmol
     *
     * @param ultiJmol1462
     */
    private void addHydrogens(UltiJmol1462 ultiJmol1462) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String script = getScriptAddHydrogens();
        ultiJmol1462.jmolPanel.evalString(script);

        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

            if (Math.abs(currentEnergy - energy) < 5.0) {
                goAhead = false;
            }
            energy = currentEnergy;
        }

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {

        }
    }
}