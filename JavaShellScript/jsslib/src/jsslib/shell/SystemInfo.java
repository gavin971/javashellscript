/**
 * Copyright (c) 2009, Robert Schuster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * - Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * - Neither the name of Robert Schuster nor the names of his
 *   contributors may be used to endorse or promote products
 *   derived from this software without specific prior written
 *   permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jsslib.shell;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * This class includes simply function to get Information about the system
 * your program is running on.
 *
 * @author robert schuster
 */
public class SystemInfo {

    public static double getCPUload() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        return os.getSystemLoadAverage();
    }

    /**
     * This Function will return A file Object for the config-dir specified by
     * the name parameter.
     * On Linux this will be /home/USERNAME/.name
     * @param name
     * @return
     */
    public static File getConfigDir(String name) {
        File result;
        String osname = System.getProperty("os.name");
        String ergebnis;
        //Windows Vista
        if (osname.contains("Windows")) {
            String appdata = System.getenv("APPDATA");
            ergebnis = appdata + "/"+name;
        //Mac OS
        } else if (osname.contains("Mac OS X")) {
            String home = System.getProperty("user.home");
            ergebnis = home + "/Library/Application Support/"+name;
        //Alles andere, zum Beispiel Linux
        } else {
            String home = System.getProperty("user.home");
            ergebnis = home + "/."+name;
        }
        result = new File(ergebnis);
        return result;
    }
}
