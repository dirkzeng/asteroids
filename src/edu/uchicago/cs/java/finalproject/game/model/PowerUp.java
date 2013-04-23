package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

public class PowerUp extends Sprite {

	public static enum Type {MACHINE_GUN, 
							EXPLODE_GUN, 
							NEW_LIFE, 
							INVINCIBILITY, 
							REGENERATE_SHIELD,
							CRUISE};
	private Type type;
	private int nSpin;

	public PowerUp() {

		super();

		ArrayList<Point> pntCs = new ArrayList<Point>();
		// top of ship
		pntCs.add(new Point(5, 5));
		pntCs.add(new Point(4,0));
		pntCs.add(new Point(5, -5));
		pntCs.add(new Point(0,-4));
		pntCs.add(new Point(-5, -5));
		pntCs.add(new Point(-4,0));
		pntCs.add(new Point(-5, 5));
		pntCs.add(new Point(0,4));

		assignPolarPoints(pntCs);

		setExpire(250);
		setRadius(50);
		setColor(Color.BLUE);


		int nX = Game.R.nextInt(10);
		int nY = Game.R.nextInt(10);
		int nS = Game.R.nextInt(5);
		
		//set random DeltaX
		if (nX % 2 == 0)
			setDeltaX(nX);
		else
			setDeltaX(-nX);

		//set random DeltaY
		if (nY % 2 == 0)
			setDeltaX(nY);
		else
			setDeltaX(-nY);
		
		//set random spin
		if (nS % 2 == 0)
			setSpin(nS);
		else
			setSpin(-nS);

		//random point on the screen
		setCenter(new Point(Game.R.nextInt(Game.DIM.width),
				Game.R.nextInt(Game.DIM.height)));

		//random orientation 
		 setOrientation(Game.R.nextInt(360));
	}
	
	public PowerUp(Type type) {
		this();
		this.type = type;
		
		switch (type) {
		case MACHINE_GUN:
			setColor(Color.red);
			setBackgroundColor(Color.magenta);
			break;
		case EXPLODE_GUN:
			setColor(Color.orange);
			setBackgroundColor(Color.red);
			break;
		case NEW_LIFE:
			setColor(Color.green);
			setBackgroundColor(Color.green);
			break;
		case INVINCIBILITY:
			setColor(Color.yellow);
			setBackgroundColor(Color.pink);
			break;
		case REGENERATE_SHIELD:
			setColor(Color.cyan);
			setBackgroundColor(Color.blue);
			break;
		case CRUISE:
			setColor(Color.red);
			setBackgroundColor(Color.black);
			break;
		default:
			break;
	}
		System.out.println(toString());
	}

	public void move() {
		super.move();

		setOrientation(getOrientation() + getSpin());

	}
	
	public String toString() {
		String strReturn = "";
		if (type != null) {
			switch (type) {
				case MACHINE_GUN:
					strReturn += "Machine gun ";
					break;
				case EXPLODE_GUN:
					strReturn += "Explode gun ";
					break;
				case NEW_LIFE:
					strReturn += "New life ";
					break;
				case INVINCIBILITY:
					strReturn += "Invincibility ";
					break;
				case REGENERATE_SHIELD:
					strReturn += "Regenerate shield ";
					break;
				case CRUISE:
					strReturn += "Cruise ";
					break;
				default:
					break;
			}
			strReturn += "powerup";
		}
		
		return strReturn;
	}
	
	public ArrayList<String> getAnnouncement() {
		ArrayList<String> strReturns = new ArrayList<String>();
		if (type != null) {
			switch (type) {
				case MACHINE_GUN:
					strReturns.add("Machine");
					strReturns.add("gun");
					break;
				case EXPLODE_GUN:
					strReturns.add("Burst");
					strReturns.add("gun");
					break;
				case NEW_LIFE:
					strReturns.add("New");
					strReturns.add("life");
					break;
				case INVINCIBILITY:
					strReturns.add("Invincibility");
					break;
				case REGENERATE_SHIELD:
					strReturns.add("Regenerate");
					strReturns.add("shield");
					break;
				case CRUISE:
					strReturns.add("Cruise");
					strReturns.add("missile");
					break;
				default:
					break;
			}
			return strReturns;
		} else {
			return null;
		}
	}
	
	public Point getAnnouncementPoint() {
		return getCenter();
	}

	public int getSpin() {
		return this.nSpin;
	}

	public void setSpin(int nSpin) {
		this.nSpin = nSpin;
	}

	//override the expire method - once an object expires, then remove it from the arrayList.
	@Override
	public void expire() {
		if (getExpire() == 0)
			CommandCenter.movFloaters.remove(this);
		else
			setExpire(getExpire() - 1);
	}
	
	public void explode(Falcon fal) {
		explode(false);
		Sound.playSound("pacman_eatghost.wav");
		switch (type) {
			case MACHINE_GUN:
				fal.enableMachineGun();
				break;
			case EXPLODE_GUN:
				fal.enableExplodeGun();
				break;
			case REGENERATE_SHIELD:
				fal.setMaxShield(fal.getMaxShield() + 200);
				fal.regenerateShield();
				break;
			case NEW_LIFE:
				CommandCenter.setNumFalcons(CommandCenter.getNumFalcons() + 1);
				break;
			case INVINCIBILITY:
				fal.setInvincibility(400);
				break;
			case CRUISE:
				fal.setCruise(10);
			default:
				break;
		}
	}

}
