package edu.uchicago.cs.java.finalproject.controller;

import sun.audio.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;

import edu.uchicago.cs.java.finalproject.game.model.*;
import edu.uchicago.cs.java.finalproject.game.view.*;
import edu.uchicago.cs.java.finalproject.sounds.MidiPlayer;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

	public static final Dimension DIM = new Dimension(1000, 750); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nLevel = 1;
	private int nTick = 0;
	private boolean bMuted = true;
	private MidiPlayer mp;

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			LEFT = 37, // rotate left; left arrow
			RIGHT = 39, // rotate right; right arrow
			UP = 38, // thrust; up arrow
			DOWN = 40, //brake; down arrow
			START = 83, // s key
			FIRE = 32, // space key
			MUTE = 77, // m-key mute
			SHIELD = 65, //a-key shield
			EXPLODE_GUN = 88, //x-key "explode gun"
			//these are some "cheat keys" for testing
			TOGGLE_MACHINE_GUN = 79, //o key
			TOGGLE_EXPLODE_GUN = 73, //i key
			L_NEW_LIFE = 76,
			SPAWN_POWER_UP = 85, // u key
			SPAWN_EXPLODING_ASTEROID = 69, //e key
			DO_HYPERSPACE = 72,//h key
			MODE_SWITCH = 93,// ] key

	// for possible future use
			MACHINE_GUN = 68, 					// d key
	// NUM_ENTER = 10, 				// hyp
			SPECIAL = 70; 					// fire special weapon;  F key

	public static enum Mode {START, 
							 ASTEROIDS,  
							 TUNNEL, 
							 GAME_OVER, 
							 PAUSED,
							 HYPERSPACE
							 };
	public static Mode mode = Mode.START;
	public static Mode nextMode;
	public static Mode tempNextMode;
	
	private Clip clpThrust;
	private Clip clpMusicBackground;
	private Clip clpMachineGun;

	private static final int SPAWN_NEW_POWERUP = 300;
	private static final int SPAWN_NEW_EXPLODING_ASTEROID = 300;
	private static final double SPAWN_NEW_TUNNEL_POWERUP = 1.5;
	private static final double SPAWN_NEW_TUNNEL_ASTEROID = 0.5;
	private static int spawnPowerUpThreshold = 0;
	private static final double SPAWN_WORMHOLE = 0.1;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {

		gmpPanel = new GamePanel(DIM);
		gmpPanel.addKeyListener(this);
		mp = new MidiPlayer();
		MidiPlayer.setMusic(MidiPlayer.Music.MAIN_MENU);
		Thread musicThread = new Thread(mp);
		musicThread.start();
		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
		
	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			if (CommandCenter.isPlaying()) {
				tick();
				if (mode == Mode.ASTEROIDS) {
					spawnRandomPowerUp();
					spawnNewExplodingAsteroid();
					//spawnWormhole();
		
					//this might be a good place to check for collisions
					checkCollisionsAsteroids();
					//this might be a god place to check if the level is clear (no more foes)
					//if the level is clear then spawn some big asteroids -- the number of asteroids 
					//should increase with the level. 
					checkNewLevel();
				} else if (mode == Mode.TUNNEL) {
					spawnTunnelPowerUp();
					spawnTunnelAsteroid();
					checkCollisionsTunnel();
				}
			}

			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must 
			// surround the sleep() in a try/catch block
			// this simply controls delay time between 
			// the frames of the animation
			
			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run

	private void checkCollisionsTunnel() {
		//check collisions for the falcon3
		Tunnel tun = gmpPanel.getTunnel();
		Falcon fal = CommandCenter.getFalcon();
		if (tun.isCollision(fal)) {
			CommandCenter.addScore(tun.getPoints());
			setMode(Mode.ASTEROIDS);
			fal.explode();
			CommandCenter.spawnFalcon(true);
		}
		
		for (Movable mov : CommandCenter.movFriends)
			for (Movable tpo : CommandCenter.movTunnelPowerUps) {
				if (tun.isCollision((Sprite)tpo)) {
					((Sprite)tpo).reverseDeltaX();
				}
				
				Point pntFloaterCenter = tpo.getCenter();
				int nFloaterRadiux = tpo.getRadius();
	
				//detect collision
				if (mov.getCenter().distance(pntFloaterCenter) < (mov.getRadius() + nFloaterRadiux)) {
					if (tpo instanceof TunnelPowerUp) {
						((TunnelPowerUp)tpo).explode();
						gmpPanel.makePowerUpAnnouncement((TunnelPowerUp)tpo);
					} else if (tpo instanceof TunnelAsteroid) {
						((Sprite)tpo).explode();
						CommandCenter.addScore(tun.getPoints());
						if (mov instanceof Falcon) {
							setMode(Mode.ASTEROIDS);
							fal.explode();
							CommandCenter.spawnFalcon(true);
						}
					}
	
				}
		}
	}
	private void checkCollisionsAsteroids() {

		
		//@formatter:off
		//for each friend in movFriends
			//for each foe in movFoes
				//if the distance between the two centers is less than the sum of their radii
					//mark it for removal
		
		//for each mark-for-removal
			//remove it
		//for each mark-for-add
			//add it
		//@formatter:on
		
		//we use this ArrayList to keep pairs of movMovables/movTarget for either
		//removal or insertion into our arrayLists later on
		Point pntFriendCenter, pntFoeCenter;
		boolean bCollisionDetected = false;
		int nScoreAdded = 0;
		int nFriendRadiux, nFoeRadiux;
		
		//check for collisions between friend and foe
		for (Movable movFriend : CommandCenter.movFriends) {
			for (Movable movFoe : CommandCenter.movFoes) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision
				if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {
					
					bCollisionDetected = true;
					//falcon
					if ((movFriend instanceof Falcon) ){
						if (!CommandCenter.getFalcon().getProtected()){
							if (!CommandCenter.getFalcon().isProtectedByShield()) {
//								tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
								//add a debris object
//								tupMarkForAdds.add(new Tuple(CommandCenter.movDebris, 
//										new Debris((Falcon)movFriend)));
								((Sprite)movFriend).explode();
								CommandCenter.spawnFalcon(false);
							} else if (movFriend instanceof Falcon && movFoe instanceof Asteroid){
								((Falcon)movFriend).absorbHit(((Asteroid)movFoe).getSize());
								CommandCenter.addScore(((Sprite)movFoe).getPrize());
								nScoreAdded = ((Sprite)movFoe).getPrize();
							}
							killFoe(movFoe);
						}
					}
					//not the falcon
					else {
//						tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
						CommandCenter.addScore(((Sprite)movFoe).getPrize());
						nScoreAdded = ((Sprite)movFoe).getPrize();
						if (!(movFriend instanceof Cruise)) ((Sprite)movFriend).explode();
						killFoe(movFoe);
					}//end else 

					//explode/remove foe
				
				}//end if 
			}//end inner for
		}//end outer for
		
		//check for collisions between falcon and floaters
		if (CommandCenter.getFalcon() != null) {
			Point pntFalCenter = CommandCenter.getFalcon().getCenter();
			int nFalRadiux = CommandCenter.getFalcon().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : CommandCenter.movFloaters) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
					bCollisionDetected = true;
					if (movFloater instanceof PowerUp) {
						((PowerUp)movFloater).explode(CommandCenter.getFalcon());
						gmpPanel.makePowerUpAnnouncement((PowerUp)movFloater);
					} else { 
						((Sprite)movFloater).explode();
					}
					CommandCenter.addScore(((Sprite)movFloater).getPrize());
					nScoreAdded = ((Sprite)movFloater).getPrize();
				}//end if 
			}//end inner for
		}//end if not null
		
		spawnPowerUpThreshold += nScoreAdded;
		if (spawnPowerUpThreshold > 1000) {
			spawnRandomPowerUpNow();
			spawnPowerUpThreshold = 0;
		}
		
		//call garbage collection
		System.gc();
		
	}//end meth

	private void killFoe(Movable movFoe) {
		
		((Sprite)movFoe).explode();
		
	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public int getTick() {
		return nTick;
	}

	private void spawnWormholeNow() {
		if (!CommandCenter.hasWormhole()) 
			CommandCenter.movFloaters.add(new Wormhole());
	}
	
	public void spawnTunnelPowerUp() {
		if (Game.R.nextDouble() * 100 <= SPAWN_NEW_TUNNEL_POWERUP) {
			TunnelPowerUp tpo = new TunnelPowerUp(gmpPanel.getTunnel());
			CommandCenter.movTunnelPowerUps.add(tpo);
		}
	}
	
	public void spawnTunnelAsteroid() {
		if (Game.R.nextDouble() * 100 <= SPAWN_NEW_TUNNEL_ASTEROID) {
			TunnelAsteroid ta = new TunnelAsteroid(gmpPanel.getTunnel());
			CommandCenter.movTunnelPowerUps.add(ta);
		}
	}
	
	private void spawnRandomPowerUp() {
		if (nTick % (SPAWN_NEW_POWERUP - nLevel * 7) == 0) {
			int nSelection = new Random().nextInt(PowerUp.Type.values().length);
			PowerUp newPowerUp = new PowerUp(PowerUp.Type.values()[nSelection]);
			CommandCenter.movFloaters.add(newPowerUp);
			System.out.println("Spawned " + newPowerUp);
		}
	}
	
	private void spawnWormhole() {
		if (Game.R.nextDouble()*100 <= SPAWN_WORMHOLE && !CommandCenter.hasWormhole()) {
			spawnWormholeNow();
		}
	}
	
	private void spawnRandomPowerUpNow() {
		int nSelection = new Random().nextInt(PowerUp.Type.values().length);
		PowerUp newPowerUp = new PowerUp(PowerUp.Type.values()[nSelection]);
		CommandCenter.movFloaters.add(newPowerUp);
		System.out.println("Spawned " + newPowerUp);
	}
	
	private void spawnNewExplodingAsteroidNow() {
		ExplodingAsteroid ea = new ExplodingAsteroid();
		gmpPanel.getStarMap().track(ea);
		CommandCenter.movFoes.add(ea);
	}
	
	private void spawnNewExplodingAsteroid() {
		if (nTick % (SPAWN_NEW_EXPLODING_ASTEROID - nLevel * 7) == 0) {
			ExplodingAsteroid ea = new ExplodingAsteroid();
			gmpPanel.getStarMap().track(ea);
			CommandCenter.movFoes.add(ea);
		}
	}
	
	public static void toggleMode() {
		if (mode == Mode.ASTEROIDS) {
			tempNextMode = mode;
			mode = Mode.TUNNEL;
		} else {
			tempNextMode = mode;
			mode = Mode.ASTEROIDS;
		}
	}
	
	public static void setMode(Mode m) {
		switch (m) {
			case ASTEROIDS:
				CommandCenter.clearAllButFalcon();
			case TUNNEL:
				if (CommandCenter.getFalcon() != null) {
					CommandCenter.getFalcon().moveToMiddle();
					CommandCenter.getFalcon().setUpwardsOrientation();
				}
			default:
				break;
		}
		mode = m;
	}
	
	public static void setNextMode(Mode m) {
		nextMode = m;
	}
	
	public static Mode getNextMode() {
		return nextMode;
	}

	// Called when user presses 's'
	private void startGame() {
		setMode(Mode.ASTEROIDS);
		CommandCenter.clearAll();
		CommandCenter.initGame();
		CommandCenter.setLevel(0);
		CommandCenter.setPlaying(true);
		CommandCenter.setPaused(false);
		if (!bMuted)
		    clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
	}

	//this method spawns new asteroids
	private void spawnAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			//Asteroids with size of zero are big
			CommandCenter.movFoes.add(new Asteroid(0));
		}
	}
	
	
	private boolean isLevelClear(){
		if (mode == Mode.ASTEROIDS) {
			//returns false if the game isn't running
			if (!CommandCenter.isPlaying())
				return false;
			
			//if there are no more Asteroids on the screen
			
			boolean bAsteroidFree = true;
			for (Movable movFoe : CommandCenter.movFoes) {
				if (movFoe instanceof Asteroid){
					bAsteroidFree = false;
					break;
				}
			}
			
			return bAsteroidFree;
		} else
			return false;

		
	}
	
	private void checkNewLevel(){
		
		if (isLevelClear() ){
			if (CommandCenter.getFalcon() !=null)
				CommandCenter.getFalcon().setProtected(true);
			
			spawnAsteroids(CommandCenter.getLevel() + 2);
			CommandCenter.setLevel(CommandCenter.getLevel() + 1);
			gmpPanel.makeLevelAnnouncement(CommandCenter.getLevel());
			if (CommandCenter.getLevel() % 3 == 0)
				spawnWormholeNow();
			CommandCenter.getFalcon().setShield(CommandCenter.getFalcon().getMaxShield());
			CommandCenter.getFalcon().setInvincibility(0);
//			gmpPanel.getStarMap().hyperspace();
		}
	}
	
	
	

	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (nKey == START && !CommandCenter.isPlaying())
			startGame();
		if (nKey == QUIT)
			System.exit(0);
		
		if (fal != null) {

			switch (nKey) {
			case PAUSE:
				if (Mode.PAUSED == mode) {
					setMode(nextMode);
					if (!bMuted) clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
				} else {
					setNextMode(mode);
					setMode(Mode.PAUSED);
					stopLoopingSounds(clpMusicBackground, clpThrust);
				}
				break;
			case QUIT:
				System.exit(0);
				break;
			case UP:
				fal.thrustOn();
				if (!CommandCenter.isPaused())
					clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case LEFT:
				fal.rotateLeft();
				break;
			case RIGHT:
				fal.rotateRight();
				break;
			case DOWN:
				fal.brakeOn();
				break;
			case SHIELD:
				fal.shieldOn();
				break;
			case MACHINE_GUN:
				if (fal.hasMachineGun())
//					clpMachineGun.loop(Clip.LOOP_CONTINUOUSLY);
				fal.machineGunOn();
				break;
			case EXPLODE_GUN:
				if (fal.hasExplodeGun()) Sound.playSound("laserblast.wav");
				fal.fireExplodeGun();
				break;
			case TOGGLE_EXPLODE_GUN:
				CommandCenter.getTunnel().reset();
				break;
			case TOGGLE_MACHINE_GUN:
				fal.enableMachineGun();
				break;
			case SPAWN_POWER_UP:
				System.out.println("spawning a power up");
				spawnRandomPowerUpNow();
				break;
			case SPAWN_EXPLODING_ASTEROID:
				spawnWormholeNow();
				break;
			case L_NEW_LIFE:
				CommandCenter.setNumFalcons(CommandCenter.getNumFalcons() + 1);
				break;
			case SPECIAL:
				fal.fireSpecial();
				break;

			// possible future use
			// case KILL:
			// case SHIELD:
			// case NUM_ENTER:

			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
//		 System.out.println(nKey);

		if (fal != null) {
			switch (nKey) {
			case FIRE:
				CommandCenter.movFriends.add(new Bullet(fal));
				Sound.playSound("laser.wav");
				break;
				
			//special is a special weapon, current it just fires the cruise missile. 
			case SPECIAL:
				fal.stopFiringSpecial();
				//Sound.playSound("laser.wav");
				break;
				
			case LEFT:
				fal.stopRotating();
				break;
			case RIGHT:
				fal.stopRotating();
				break;
			case UP:
				fal.thrustOff();
				clpThrust.stop();
				break;
			case DOWN:
				fal.brakeOff();
				break;
			case SHIELD:
				fal.shieldOff();
				break;
			case MACHINE_GUN:
				fal.machineGunOff();
				break;
			case DO_HYPERSPACE:
				gmpPanel.getStarMap().softHyperspace();
				break;
			case MODE_SWITCH:
				toggleMode();
				fal.setUpwardsOrientation();
				break;
				
				
			case MUTE:
				if (!bMuted){
					stopLoopingSounds(clpMusicBackground);
					bMuted = !bMuted;
				} 
				else {
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
					bMuted = !bMuted;
				}
				break;
				
				
			default:
				break;
			}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}
	

	
}