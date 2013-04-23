package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

public class StarMap {
	
	ArrayList<double[]> dStarPoints;
	ArrayList<double[]> dMidPoints;
	double deltaX;
	double deltaY;
	private Color colStarColor;
	private int nStarNumber;
	private final double DEFAULT_SUBDIVISION = 32.0;
	private double dSubdivision;
	private int nHyperspaceX, nHyperspaceY;
	private boolean bHyperspace;
	private int nHyperspaceDrawCount;
	private boolean bSoftHyperspace;
	private int nSoftHyperSpaceDrawLimit;
	private boolean isTracking = false;
	private boolean bTrackedObjectHasExploded = false;
	private Sprite sprToTrack;
	
	public StarMap (int nStars) {
		this.nStarNumber = nStars;
		dStarPoints = new ArrayList<double[]>(nStarNumber);
		for (int i=0; i<nStars; i++) {
			int x = Game.R.nextInt(Game.DIM.width);
			int y = Game.R.nextInt(Game.DIM.height);
			double newPoint[] = new double[2];
			newPoint[0] = (double)x;
			newPoint[1] = (double)y;
			dStarPoints.add(newPoint);
		}
		
		deltaX = Game.R.nextDouble();
		deltaY = Game.R.nextDouble();
		setColor(new Color(190, 190, 190));
		bHyperspace = false;
		bSoftHyperspace = false;
		nHyperspaceDrawCount = 0;
		dSubdivision = DEFAULT_SUBDIVISION;
	}
	
	public void draw(Graphics g) {
		g.setColor(getColor());
		
		if (isTracking) {
			nHyperspaceX = sprToTrack.getCenter().x;
			nHyperspaceY = sprToTrack.getCenter().y;
			if (sprToTrack.hasExploded()) {
				isTracking = false;
				bHyperspace = false;
				bTrackedObjectHasExploded = true;
				sprToTrack = null;
			}
		}
		if (bHyperspace) {
			for (int i = 0; i<dStarPoints.size(); i++) {
				double starPoint[] = dStarPoints.get(i);
//				double oldMidPoint[] = dMidPoints.get(i);
				double newMidPoint[] = new double[2];
				newMidPoint[0] = starPoint[0] + nHyperspaceDrawCount * 
						(nHyperspaceX - starPoint[0])/dSubdivision;
				newMidPoint[1] = starPoint[1] + nHyperspaceDrawCount * 
						(nHyperspaceY - starPoint[1])/dSubdivision;
				g.drawLine((int)(starPoint[0]), (int)(starPoint[1]), 
							(int)(newMidPoint[0]), (int)(newMidPoint[1]));
				dMidPoints.add(newMidPoint);
			}
			if (!bSoftHyperspace || nHyperspaceDrawCount < nSoftHyperSpaceDrawLimit) {
				nHyperspaceDrawCount++;
				updateColor();
			}
		} else {
			for (double starPoint[] : dStarPoints) {
				g.drawRect((int)(starPoint[0]), (int)(starPoint[1]), 1, 1);
			}
		}
	}
	
	public void softHyperspace() {
		int x = Game.R.nextInt(Game.DIM.width);
		int y = Game.R.nextInt(Game.DIM.height);
		dSubdivision *= 4;
		hyperspace(x,y,true);
	}
	
	private void updateColor() {
		Color col = getColor();
		if (col.getRed() < 255) {
			setColor(new Color(col.getRed() + 1, col.getRed() + 1, col.getRed() + 1));
		}
	}
	
	public void update() {
		if (Game.mode == Game.Mode.ASTEROIDS) {
			update(deltaX, deltaY);
		} else {
			int nSpeed = CommandCenter.getTunnel().getSpeed()/10;
			update(0, nSpeed);
		}
	}
	
	public void setColor(Color c) {
		colStarColor = c;
	}
	
	public Color getColor() {
		return colStarColor;
	}
	
	public void track(Sprite sprite) {
		if (!isTracking) {
			sprToTrack = sprite;
			isTracking = true;
			bTrackedObjectHasExploded = false;
			softHyperspace();
		}
	}
	
	public void setDirection(double deltaX, double deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}
	
	public void update(double deltaX, double deltaY) {
		for (double starPoint[] : dStarPoints) {
			starPoint[0] = (starPoint[0] + deltaX);// % Game.DIM.width;
			starPoint[1] = (starPoint[1] + deltaY);// % Game.DIM.height;
			if (starPoint[0] <= 0)
				starPoint[0] += Game.DIM.width;
			if (starPoint[1] <= 0)
				starPoint[1] += Game.DIM.height;
			if (starPoint[0] > Game.DIM.width)
				starPoint[0] -= Game.DIM.width;
			if (starPoint[1] > Game.DIM.height)
				starPoint[1] -= Game.DIM.height;
		}
	}
	
	public void hyperspace() {
		int x, y;
		if (isTracking) {
			x = sprToTrack.getCenter().x;
			y = sprToTrack.getCenter().y;
			isTracking = false;
		} else {
			x = Game.R.nextInt(Game.DIM.width);
			y = Game.R.nextInt(Game.DIM.height);
		}
		hyperspace(x,y, false);
	}
	
	public int getHyperspaceDrawCount() {
		return nHyperspaceDrawCount;
	}
	
	public boolean isHyperspaceDone() {
		return nHyperspaceDrawCount >= dSubdivision;
	}
	
	private Game.Mode nextMode;
	
	public Game.Mode getNextMode() {
		return nextMode;
	}
	
	public void setNextMode(Game.Mode mode) {
		nextMode = mode;
	}
	
	public void hyperspace(int x, int y, boolean bSoft) {
		dMidPoints = new ArrayList<double[]>(nStarNumber);
		nHyperspaceX = x;
		nHyperspaceY = y;
		bHyperspace = true;
		nHyperspaceDrawCount = 0;
		if (!bSoft) {
			Sound.playSound("hyperspace.wav");
			bSoftHyperspace = false;
			dSubdivision = 32;
		} else {
			bSoftHyperspace = true;
			nSoftHyperSpaceDrawLimit = 3;
		}
	}
}
