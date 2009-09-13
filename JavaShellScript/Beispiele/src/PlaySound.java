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
 * This Example use JLayer to Play mp3-Files
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import jsslib.shell.ArgParser;

/**
 *
 * @author robert schuster
 */
public class PlaySound {
    public static void main(String[] args) {
        //get the arguments
        Properties arguments = ArgParser.ArgsToProperties(args);

        //any arguments?
        if (arguments == null || arguments.size() == 0) {
            ShowDiscription();
            return;
        }

        //get the filename
        String filename = arguments.getProperty("unnamed0");
        File file = new File(filename);
        
        //Create an InputStream
        InputStream is;
        try {
            is = new FileInputStream(filename);
        } catch (FileNotFoundException ex) {
            System.out.println("File " + filename + " not found!");
            return;
        }

        //Create the Player
        try {
            final Player player = new Player(is);
            //Start the Playback in a seperate Thread
            new Thread() {
                @Override
                public void run() {
                    try {
                        player.play();
                    } catch (JavaLayerException ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
            
            System.out.print("Press ENTER to exit!");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            in.readLine();
            player.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void ShowDiscription() {
        System.out.println();
        System.out.println("This Example use JLayer to play a mp3-File!");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("PlaySound file.mp3");
    }
}
