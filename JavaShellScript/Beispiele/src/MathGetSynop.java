//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8 -Djava.library.path=/Applications/Mathematica.app/SystemFiles/Links/JLink/SystemFiles/Libraries/MacOSX-x86-64 -cp /Applications/Mathematica.app/SystemFiles/Links/JLink/JLink.jar

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

/*
 * This Example use Mathematica to get Synop-Data
 */

import com.wolfram.jlink.*;
import java.util.Properties;
import jsslib.shell.ArgParser;
import jsslib.util.TimePeriod;

/**
 *
 * @author robert schuster
 */
public class MathGetSynop {

    /**
     * The Link to the Mathematica-Kernel
     */
    private static KernelLink ml;

    public static void main(String[] args) {
        //Parse the arguments to a Properties object
        Properties arguments = ArgParser.ArgsToProperties(args);

        //if there are no arguments, print the discription
        if (arguments == null || arguments.size() == 0) {
            ShowDiscription();
            return;
        }

        //Arguments for the Mathematica-Kernellink:
        String mathargs[] = new String[5];
        mathargs[0] = "-linkmode";
        mathargs[1] = "launch";
        mathargs[2] = "-linkname";
        mathargs[3] = "/Applications/Mathematica.app/Contents/MacOS/MathKernel";
        mathargs[4] = "-mathlink";

        //Connect to Mathematica
		try {
			ml = MathLinkFactory.createKernelLink(mathargs);
		} catch (MathLinkException e) {
			System.out.println("Fatal error opening link to Mathematica: " + e.getMessage());
            ShowDiscription();
			return;
		}

        
		try {
			// Get rid of the initial InputNamePacket the kernel will send
			// when it is launched.
			ml.discardAnswer();

            //if there is station-name inside the arguments?
            String station = arguments.getProperty("station");
            if (station != null) {
                getLatestStationData(station);
            }
		} catch (MathLinkException e) {
			System.out.println("MathLinkException occurred: " + e.getMessage());
		} finally {
			ml.close();
		}

    }

    private static void ShowDiscription() {
        System.out.println();
        System.out.println("This Example use Mathematica to get Synop-Data!");
        System.out.println("IMPORTANT: Set the path to jour Mathematica-Installation");
        System.out.println("           in the #options line of this file.");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("./MathGetSynop -station WMO10513");
    }

    private static String[] property = {"Memberships",
                                        "CloudCoverFraction",
                                        "CloudHeight",
                                        "CloudTypes",
                                        "DewPoint",
                                        "Humidity",
                                        "PrecipitationRate",
                                        "Pressure",
                                        "PressureTendency",
                                        "SnowAccumulationRate",
                                        "SnowDepth",
                                        "StationPressure",
                                        "Temperature",
                                        "Visibility",
                                        "WindChill",
                                        "WindDirection",
                                        "WindGusts",
                                        "WindSpeed",
                                        "Conditions",
                                        "PrecipitationAmount",
                                        "PrecipitationTypes",
                                        "SnowAccumulation",
                                        "MaxTemperature",
                                        "MaxWindSpeed",
                                        "MeanDewPoint",
                                        "MeanHumidity",
                                        "MeanPressure",
                                        "MeanStationPressure",
                                        "MeanTemperature",
                                        "MeanVisibility",
                                        "MeanWindChill",
                                        "MeanWindSpeed",
                                        "MinTemperature",
                                        "TotalPrecipitation",
                                        "WindGusts",
                                        "Coordinates",
                                        "DateRange",
                                        "Elevation",
                                        "Latitude",
                                        "Longitude"};

    private static void getLatestStationData(String station) throws MathLinkException {
        System.out.println("Waiting for data for the station " + station);
        String strResult;

        //is there a station with this name?
        strResult = ml.evaluateToInputForm("WeatherData[\""+station+"\"]", 50);
        if (strResult.contains("WeatherData")) {
            System.out.println("Station not found: " + station);
            return;
        }
        //Ask for the Date-Range of the station
        ml.evaluate("WeatherData[\""+station+"\", \"DateRange\"]");
        ml.waitForAnswer();
        int[][] daterange = ml.getIntArray2();
        TimePeriod tp = new TimePeriod();
        tp.from.set(daterange[0][0],
                    daterange[0][1]-1,
                    daterange[0][2],
                    daterange[0][3],
                    daterange[0][4]);
        tp.to.set(daterange[1][0],
                  daterange[1][1]-1,
                  daterange[1][2],
                  daterange[1][3],
                  daterange[1][4]);
        System.out.println("Data available from: " + tp.getFromString() + " to: " + tp.getToString());

        for (int i=0;i<property.length;i++) {
            strResult = ml.evaluateToInputForm("WeatherData[\""+station+"\", \""+property[i]+"\"]", 50);
            System.out.println(String.format("%25s:  %s", property[i],strResult));
        }
    }
}
