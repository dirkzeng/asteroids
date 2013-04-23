package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.Clip;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;


public class Falcon extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================
	
	private final double THRUST = .65;

	final int DEGREE_STEP = 14;
	
	private boolean bShield = false;
	private boolean bFlame = false;
	private boolean bProtected; //for fade in and out
	
	private boolean bThrusting = false;
	private boolean bTurningRight = false;
	private boolean bTurningLeft = false;
	private boolean bBraking = false;
	
	//special weapons
	private boolean bHasMachineGun = false;
	private boolean bIsFiringMachineGun = false;
	private int nMachineGunRounds = 100;
	private int nExplodeGunRounds = 0;
	private int nInvincibility = 0;
	private int nCruise = 0;
	private int nShield;
	
	public static Clip clpMachineGun = Sound.clipForLoopFactory("machinegun.wav");
			
	private final double[] FLAME = { 23 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];
	
	int nMaxShield = 1000;

	
	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Falcon() {
		super();

		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// top of ship
		pntCs.add(new Point(0, 18)); 
		
		//right points
		pntCs.add(new Point(3, 3)); 
		pntCs.add(new Point(12, 0)); 
		pntCs.add(new Point(13, -2)); 
		pntCs.add(new Point(13, -4)); 
		pntCs.add(new Point(11, -2)); 
		pntCs.add(new Point(4, -3)); 
		pntCs.add(new Point(2, -10)); 
		pntCs.add(new Point(4, -12)); 
		pntCs.add(new Point(2, -13)); 

		//left points
		pntCs.add(new Point(-2, -13)); 
		pntCs.add(new Point(-4, -12));
		pntCs.add(new Point(-2, -10)); 
		pntCs.add(new Point(-4, -3)); 
		pntCs.add(new Point(-11, -2));
		pntCs.add(new Point(-13, -4));
		pntCs.add(new Point(-13, -2)); 
		pntCs.add(new Point(-12, 0)); 
		pntCs.add(new Point(-3, 3)); 
		

		assignPolarPoints(pntCs);

		setColor(Color.white);
		
		reset();
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================

	public void reset() {
		//put falcon in the middle.
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));
		
		//with random orientation
		setOrientation(270);
		
		//this is the size of the falcon
		setRadius(35);

		//these are falcon specific
		setProtected(true);
		setFadeValue(0);
		setShield(nMaxShield);
	}
	public void setMaxShield (int n) {
		nMaxShield = n;
	}
	
	public int getMaxShield() {
		return nMaxShield;
	}
	public void move() {
		super.move();
		
		if (Game.Mode.ASTEROIDS == Game.mode) {
			if (bThrusting) {
				bFlame = true;
				double dAdjustX = Math.cos(Math.toRadians(getOrientation()))
						* THRUST;
				double dAdjustY = Math.sin(Math.toRadians(getOrientation()))
						* THRUST;
				setDeltaX(getDeltaX() + dAdjustX);
				setDeltaY(getDeltaY() + dAdjustY);
			}
			
			if (bBraking) {
				setDeltaX(3*getDeltaX()/4);
				setDeltaY(3*getDeltaY()/4);
			}
			
			if (bTurningLeft) {
	
				if (getOrientation() <= 0 && bTurningLeft) {
					setOrientation(360);
				}
				setOrientation(getOrientation() - DEGREE_STEP);
			} 
			if (bTurningRight) {
				if (getOrientation() >= 360 && bTurningRight) {
					setOrientation(0);
				}
				setOrientation(getOrientation() + DEGREE_STEP);
			}
		} 
		
		else if (Game.Mode.TUNNEL == Game.mode) {
			if (bThrusting) {
				bFlame = true;
				setDeltaY(getDeltaY() - THRUST);
			}
			
			if (bBraking) {
				setDeltaY(getDeltaY() + THRUST);
			}
			
			if (bTurningLeft) {
				setDeltaX(getDeltaX() - THRUST);
			}
			
			if (bTurningRight) {
				setDeltaX(getDeltaX() + THRUST);
			}
		}
		
		//MACHINE GUN
		
		if (bIsFiringMachineGun && bHasMachineGun) {
			Bullet bltMachineGunBullet = new Bullet(this, Color.red);
			Bullet bltBackBullet = new Bullet(this, Color.green);
			bltBackBullet.setDeltaX(bltBackBullet.getDeltaX() * (-1));
			bltBackBullet.setDeltaY(bltBackBullet.getDeltaY() * (-1));
			CommandCenter.movFriends.add(bltBackBullet);
			CommandCenter.movFriends.add(bltMachineGunBullet);
			decrementMachineGunRounds();
		}
	} //end move

	public void moveToMiddle() {
		Point c = getCenter();
		setCenter(new Point(Game.DIM.width/2, c.y));
	}
	public void rotateLeft() {
		bTurningLeft = true;
	}

	public void rotateRight() {
		bTurningRight = true;
	}

	public void stopRotating() {
		bTurningRight = false;
		bTurningLeft = false;
	}
	
	public void setRightOrientation() {
		setOrientation(180);
	}
	
	public void setUpwardsOrientation() {
		setOrientation(270);
	}

	public void thrustOn() {
		bThrusting = true;
	}

	public void thrustOff() {
		bThrusting = false;
		bFlame = false;
	}
	
	public void brakeOn() {
		bBraking = true;
	}

	public void brakeOff() {
		bBraking = false;
	}

	private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}
	
	public void setExplodeGunRounds(int nRounds) {
		nExplodeGunRounds = nRounds;
	}
	
	public int getExplodeGunRounds() {
		return nExplodeGunRounds;
	}
	
	//MACHINE GUN METHODS
	public void enableMachineGun() {
		bHasMachineGun = true;
		nMachineGunRounds += 100;
	}
	
	public void disableMachineGun() {
		bHasMachineGun = false;
	}
	
	public void machineGunOn() {
		bIsFiringMachineGun = true;
		clpMachineGun.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void machineGunOff() {
		bIsFiringMachineGun = false;
		clpMachineGun.stop();
	}
	
	public boolean hasMachineGun() {
		return bHasMachineGun;
	}
	
	public void decrementMachineGunRounds() {
		System.out.println(nMachineGunRounds);
		nMachineGunRounds--;
		if (nMachineGunRounds <= 0)
			disableMachineGun();
	}
	//END MACHINE GUN METHODS
	
	public void regenerateShield() {
		setShield(getMaxShield());
	}
	
	//EXPLODE GUN METHODS
	public void enableExplodeGun() {
		nExplodeGunRounds += 20;
	}
	public void fireExplodeGun() {
		if (getExplodeGunRounds() > 0) {
			final int NUMBER_OF_BULLETS_TO_FIRE = 20;
			final int BULLET_SPEED = 20;
			for (int i=0; i<NUMBER_OF_BULLETS_TO_FIRE; i++) {
				int nOrientation = (int) (360.0/NUMBER_OF_BULLETS_TO_FIRE * i);
				Bullet newBullet = new Bullet(this, Color.orange);
				newBullet.makeStarBullet();
				newBullet.setDeltaX(BULLET_SPEED * Math.cos(Math.toRadians(nOrientation + getOrientation())));
				newBullet.setDeltaY(BULLET_SPEED * Math.sin(Math.toRadians(nOrientation + getOrientation())));
				newBullet.setOrientation(nOrientation);
				CommandCenter.movFriends.add(newBullet);
			}
			//decrement rounds
			setExplodeGunRounds(getExplodeGunRounds()-1);
		}
	}
	//END EXPLODE GUN METHODS
	
	public void draw(Graphics g) {

		//does the fading at the beginning or after hyperspace
		Color colShip;
		if (getFadeValue() == 255) {
			colShip = Color.white;
		} else {
			colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(
					getFadeValue(), 175), getFadeValue());
		}

		//shield on
		if (bShield && nShield > 0) {

			setShield(getShield() - 1);

			g.setColor(Color.cyan);
			g.drawOval(getCenter().x - getRadius(),
					getCenter().y - getRadius(), getRadius() * 2,
					getRadius() * 2);

		} //end if shield
		
		//if invincible
		
		if (nInvincibility > 0) {
			g.setColor(Color.yellow);
			for (int i= 1; i<=5; i++) {
				g.drawOval(getCenter().x - getRadius(i),
						getCenter().y - getRadius(i), getRadius(i) * 2,
						getRadius(i) * 2);
			}
			
		}

		//thrusting
		if (bFlame) {
			g.setColor(colShip);
			//the flame
			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame

		drawShipWithColor(g, colShip);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}

	public void fadeInOut() {
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}
		decrementInvincibility();
	}
	
	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}
	
	public void fireSpecial() {
		if (bHasMachineGun) {
			machineGunOn();
		} else if (nExplodeGunRounds > 0) {
			fireExplodeGun();
		} else if (nCruise > 0){
			fireCruise();
		}
	}
	
	public void stopFiringSpecial() {
		if (bIsFiringMachineGun) {
			machineGunOff();
		}
	}
	
	public void setCruise(int n) {
		nCruise = n;
	}
	
	public void fireCruise() {
		CommandCenter.movFriends.add(new Cruise(this));
		nCruise--;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void shieldOn() {
		bShield = true;
	}
	
	public void shieldOff() {
		bShield = false;
	}
	
	public void setInvincibility(int nInvincibility) {
		this.nInvincibility = nInvincibility;
	}
	
	private void decrementInvincibility() {
		if (nInvincibility > 0)
			nInvincibility--;
	}
	
	public int getRadius() {
		int rad = super.getRadius();
		if (nInvincibility > 0) {
			return 5 * rad;
		} else
			return rad;
	}
	
	public int getRadius(int nTimes) {
		return nTimes * super.getRadius();
	}
	
	public boolean hasExplodeGun() {
		return (nExplodeGunRounds>0);
	}
	
	public boolean isProtectedByShield() {
		return (bShield && nShield > 0) || nInvincibility > 0;
	}
	public boolean getProtected() {
		return bProtected;
	}
	public void absorbHit(int nSize) {
		switch (nSize) {
			case 0:
				nShield -= 50;
			case 1:
				nShield -= 50;
			case 2:
				nShield -= 50;
			default:
				break;
		}
	}
	public void setShield(int n) {nShield = n;}
	public int getShield() {return nShield;}	
	
} //end class
