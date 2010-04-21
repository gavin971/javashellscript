//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8 -Xmx1536M

/**
 * Copyright (c) 2010, Robert Schuster
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


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Properties;
import javax.imageio.ImageIO;
import jsslib.shell.ArgParser;


/**
 *
 * This example will convert a AndNav-Map Store to a single printable
 * png-file.
 */
public class AndNav2Map {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //parse the arguments to a properties object
        Properties arguments = ArgParser.ArgsToProperties(args);

        //show information and exit if no directory is specified
        if (args.length == 0) {
            System.out.println("\nThis example will convert a AndNav map store folder");
            System.out.println("to a single printable png-file.");
            System.out.println();
            System.out.println("Only one zoom level is allowed!");
            System.out.println();
            System.out.println("Usage: (Convert the zoom level 17)");
            System.out.println("    ./AndNav2Map 17");
            System.out.println("Parameters:");
            System.out.println("    -splitx #n   split the output in x-direction");
            System.out.println("    -splity #n   split the output in y-direction");
            System.out.println("    -overlap #n  overlap the slited output tiles");
            return;
        }

        //check if the argument 1 is a directory
        File mapdir = new File(arguments.getProperty("unnamed0"));
        if (!mapdir.isDirectory()) {
            System.out.println("The first argument needs to be the folder with the map tiles!");
            return;
        }

        //split the area in x and y direction
        int splitx = 1;
        int splity = 1;
        int overlap = 0;
        if (arguments.getProperty("splitx") != null) {
            splitx = Integer.parseInt(arguments.getProperty("splitx"));
        }
        if (arguments.getProperty("splity") != null) {
            splity = Integer.parseInt(arguments.getProperty("splity"));
        }
        if (splitx > 1 || splity > 1) {
            System.out.println(String.format("Spliting the area in %d x %d tiles",splitx, splity));
            //add overlapping area
            if (arguments.containsKey("overlap")) {
                overlap = Integer.parseInt(arguments.getProperty("overlap"));
                System.out.println("Add overlapping of "+overlap+" pixel");
            }
        }


        //get a list of subdirectories
        try {
            BufferedImage output = null;
            Graphics2D outputg = null;
            //calculate the size of the images
            File[] tiledirs = mapdir.listFiles();
            File[] maptiles = tiledirs[0].listFiles();
            BufferedImage image = ImageIO.read(maptiles[0]);
            int width = image.getWidth();
            int height = image.getHeight();
            int tilewidth = width*tiledirs.length / splitx;
            int tileheight = height * maptiles.length / splity;
            //loop over all output tiles
            for (int x=0;x<splitx;x++) {
                for (int y=0;y<splity;y++) {
                    output = new BufferedImage(tilewidth+2*overlap, tileheight+2*overlap, BufferedImage.TYPE_INT_RGB);
                    outputg = output.createGraphics();
                    if (overlap > 0) {
                        outputg.setColor(Color.white);
                        outputg.fillRect(0, 0, tilewidth+2*overlap, tileheight+2*overlap);
                    }
                    //loop over all input tiles
                    for (int j=0;j<tiledirs.length;j++) {
                        System.out.println("Working on: ("+j+"/"+tiledirs.length+") " + tiledirs[j]);
                        maptiles = tiledirs[j].listFiles();
                        //copy eche tile into the line
                        for (int i=0;i<maptiles.length;i++) {
                            if (j*width >= x*tilewidth-width-overlap && j*width < (x+1)*tilewidth+overlap) {
                                if (i*height >= y*tileheight-height-overlap && i*height < (y+1)*tileheight+overlap) {
                                    BufferedImage tile = ImageIO.read(maptiles[i]);
                                    outputg.drawImage(tile, j*width-x*tilewidth+overlap, i*height-y*tileheight+overlap, null);
                                }
                            }
                        }
                    
                    }                
                    //write the output to a file
                    System.out.println("\nWriting the output image...\n");
                    ImageIO.write(output, "png", new File("map"+x+"x"+y+".png"));
                }
            }
            System.out.println("done.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
