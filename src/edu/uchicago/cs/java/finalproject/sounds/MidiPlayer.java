package edu.uchicago.cs.java.finalproject.sounds;

import javax.sound.midi.*;

import java.awt.EventQueue;
import java.io.*;

/** Plays a midi file provided on command line */
public class MidiPlayer implements Runnable {
	public static Sequencer sequencer;
	public static File fileAsteroidsMusic;
	public static File fileTunnelMusic;
	public static File fileMainMenuMusic;
	public static File toPlay;
	
	static {
		fileAsteroidsMusic = new File("main.mid");
		fileTunnelMusic = new File("tunnel.mid");
		fileMainMenuMusic = new File("mainmenu.mid");
		
        if(!fileAsteroidsMusic.exists() || fileAsteroidsMusic.isDirectory() || !fileAsteroidsMusic.canRead()) {
            printError("main.mid");
        }
        if(!fileTunnelMusic.exists() || fileTunnelMusic.isDirectory() || !fileTunnelMusic.canRead()) {
            printError("tunnel.mid");
        }
        if(!fileMainMenuMusic.exists() || fileMainMenuMusic.isDirectory() || !fileMainMenuMusic.canRead()) {
            printError("mainmenu.mid");
        }
        
        setMusic(Music.ASTEROIDS);
	}
	
	public static enum Music {MAIN_MENU, ASTEROIDS, TUNNEL};
    
    public static void setMusic(Music music) {
    	switch (music) {
	    	case MAIN_MENU:
	    		toPlay = fileMainMenuMusic;
	    		break;
	    	case TUNNEL:
	    		toPlay = fileTunnelMusic;
	    		break;
	    	case ASTEROIDS:
	    		toPlay = fileAsteroidsMusic;
	    		break;
    		default:
    			toPlay = fileAsteroidsMusic;
    			break;
    	}
    }
    
    public static void start() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.setSequence(MidiSystem.getSequence(toPlay));
            sequencer.open();
            sequencer.start();
            while(!Thread.currentThread().isInterrupted()) {
                if(sequencer.isRunning()) {
                    try {
                        Thread.sleep(1000); // Check every second
                    } catch(InterruptedException ignore) {
                        break;
                    }
                } else {
                    break;
                }
            }
            // Close the MidiDevice & free resources
            sequencer.stop();
            sequencer.close();
        } catch(MidiUnavailableException mue) {
            System.out.println("Midi device unavailable!");
        } catch(InvalidMidiDataException imde) {
            System.out.println("Invalid Midi data!");
        } catch(IOException ioe) {
            System.out.println("I/O Error!");
        }
    }
    
    public static void stop() {
    	sequencer.stop();
    }
    
    public static Sequencer getSequencer() {
    	return sequencer;
    }

    /** Provides help message and exits the program */
    private static void printError(String fileName) {
        System.out.println("Error: " + fileName + " not found.");
    }
    
    public void run() {
    	start();
    }
}