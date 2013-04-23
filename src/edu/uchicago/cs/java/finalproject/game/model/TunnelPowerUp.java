package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

public class TunnelPowerUp extends PowerUp {
	Tunnel tun;
	private int nLastShiftedCount=0;
	
	public TunnelPowerUp(Tunnel tun) {
		super();
		this.tun = tun;
		int range[] = tun.getValidEntryRange();
		setColor(Color.yellow);
		setBackgroundColor(Color.yellow);
		int nRange = Math.abs(range[1]-range[0]);
		
		int nXCoord;
		if (nRange > 0) {
			nXCoord = Game.R.nextInt(nRange) + range[0];
		} else {
			nXCoord = Game.DIM.width/2;
		}
		//guarantee a positive deltay
		setDeltaY(Game.R.nextDouble() * 100);
		setCenter(new Point(nXCoord, 0));
		System.out.println("new tunnel powerup");
		setPrize(1000);
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
		d.setColor(getColor());
		CommandCenter.movDebris.add(d);
		setExploded();
		Sound.playSound("coin.wav");
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
		System.out.println("tunnel power up drew");
	}
}