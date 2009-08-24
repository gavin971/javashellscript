//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8 -Djava.library.path=/Applications/Mathematica.app/SystemFiles/Links/JLink/SystemFiles/Libraries/MacOSX-x86-64 -cp /Applications/Mathematica.app/SystemFiles/Links/JLink/JLink.jar
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
