//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8 -Djava.library.path=/Applications/Mathematica.app/SystemFiles/Links/JLink/SystemFiles/Libraries/MacOSX-x86-64 -cp /Applications/Mathematica.app/SystemFiles/Links/JLink/JLink.jar
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
