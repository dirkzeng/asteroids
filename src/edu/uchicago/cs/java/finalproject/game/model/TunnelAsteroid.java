package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

public class TunnelAsteroid extends Asteroid {

	private int nLastShiftedCount=0;
	private Tunnel tun;
	
	public TunnelAsteroid(Tunnel tun) {
		super(2);
		this.tun = tun;
		int range[] = tun.getValidEntryRange();
		setColor(new Color(200, 200, 200));
		setBackgroundColor(new Color(50,50,50));
		int nRange = Math.abs(range[1]-range[0]);
		
		int nXCoord;
		if (nRange > 0) {
			nXCoord = Game.R.nextInt(nRange) + range[0];
		} else {
			nXCoord = Game.DIM.width/2;
		}
		//guarantee a positive deltay
		setDeltaY(Game.R.nextDouble() * 30);
		setCenter(new Point(nXCoord, 0));
		System.out.println("new tunnel asteroid");
		setPrize(500);
	}
	
	public ArrayList<String> getAnnouncement() {
		ArrayList<String> strReturns = new ArrayList<String>();
		strReturns.add("" + getPrize());
		return strReturns;
	}
	
	public void reverseDeltaX() {
		if (nLastShiftedCount > 10) {
			setDeltaX(getDeltaX() * (-1));
			nLastShiftedCount = 0;
		}
	}
	
	public void explode() {
		tun.addPoints(getPrize());
		Debris d = new Debris(this);
		CommandCenter.movDebris.add(d);
		setExploded();
		Sound.playSound("grenade.wav");
	}
	
	public void move() {
		Point pnt = getCenter();
		double dX = pnt.x + getDeltaX();
		double dY = pnt.y + getDeltaY();
		
		
		//we'll delete the powerup if it's off the screen
		if (pnt.x > getDim().width || pnt.x < 0 || pnt.y < 0 || pnt.y > getDim().height) {
			setExploded();
		} else {
			setCenter(new Point((int) dX, (int) dY));
		}
		nLastShiftedCount++;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		System.out.println("tunnel asteroid drew");
	}
}
