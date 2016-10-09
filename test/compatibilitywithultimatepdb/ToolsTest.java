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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ToolsTest {

    /**
     * Utils to read a file from the resources
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
