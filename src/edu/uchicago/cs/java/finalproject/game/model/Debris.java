package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

public class Debris extends Sprite {
	
	private int nChunkNumber;
	private int nDebrisChunkSpeed = 5;
	private ArrayList<DebrisChunk> arlFirstExplosion;
	private ArrayList<DebrisChunk> arlSecondExplosion;
	private boolean hasSecondExplosion = false;
	private boolean firstExplosionDisappeared = false;
	private boolean secondExplosionDisappeared = false;
	private double dSourceDegrees[];
	private double dSourceLengths[];
	private int nColorStep = 10;
	
	
	public Debris(Sprite sprSource)
	{
		setColor(Color.white);
		//we'll put this in a try/catch in case the sprite gets deleted in a parallel thread.
		try {
			nChunkNumber = sprSource.getChunkNumber();
			//get position and velocity from source
			this.setCenter(sprSource.getCenter());
			this.setDeltaX(sprSource.getDeltaX());
			this.setDeltaY(sprSource.getDeltaY());
			
	//		System.out.printf("Debris created with center (%d, %d) and velocity (%g, %g)\n", getCenter().x, 
	//				getCenter().y, getDeltaX(), getDeltaY());
	//		
			//create debris (initially, static)
//			this.nChunkNumber = sprSource.getDegrees().length;
			this.dSourceDegrees = sprSource.getDegrees();
			this.dSourceLengths = sprSource.getLengths(); 
		} catch (Exception e) {
			Sprite spr = new Asteroid(0);
			this.setCenter(spr.getCenter());
			this.setDeltaX(spr.getDeltaX());
			this.setDeltaY(spr.getDeltaY());
			
	//		System.out.printf("Debris created with center (%d, %d) and velocity (%g, %g)\n", getCenter().x, 
	//				getCenter().y, getDeltaX(), getDeltaY());
	//		
			//create debris (initially, static)
			this.nChunkNumber = spr.getDegrees().length;
			this.dSourceDegrees = spr.getDegrees();
			this.dSourceLengths = spr.getLengths();
		}
		
		
		this.arlFirstExplosion = new ArrayList<DebrisChunk>(nChunkNumber);
		this.arlSecondExplosion = new ArrayList<DebrisChunk>(nChunkNumber);
		
		spawnDebrisChunks();
	}
	
	private void spawnDebrisChunks()
	{
		Point pntCenter;
		double dOrientation;
		for (int i=0; i<nChunkNumber; i++) {
			// Make a debris chunk and set it with a random orientation
			// and an angle proportional to i
			DebrisChunk toAdd = new DebrisChunk(Game.R.nextDouble()*20+15, Game.R.nextInt(360), this);
			
			dOrientation = 360.0/nChunkNumber * i;
			
			pntCenter = new Point(this.getCenter().x + (int)(dSourceLengths[i] * Math.cos(dSourceDegrees[i])),
								  this.getCenter().y + (int)(dSourceLengths[i] * Math.sin(dSourceDegrees[i])));
			
			toAdd.setCenter(pntCenter);
			toAdd.setDeltaX(nDebrisChunkSpeed * Math.cos(Math.toRadians(dOrientation)));
			toAdd.setDeltaY(nDebrisChunkSpeed * Math.sin(Math.toRadians(dOrientation)));
			
			toAdd.setExpire(10);
			
			arlFirstExplosion.add(toAdd);
			//System.out.println("Added debris chunk: " + toAdd);
			
			DebrisChunk toAddSecond = new DebrisChunk(Game.R.nextDouble()*20+15, Game.R.nextInt(360), this);
			
			dOrientation = 360.0/nChunkNumber * i;
			
			pntCenter = new Point(this.getCenter().x + (int)(dSourceLengths[i] * Math.cos(dSourceDegrees[i])),
								  this.getCenter().y + (int)(dSourceLengths[i] * Math.sin(dSourceDegrees[i])));
			
			toAddSecond.setCenter(getCenter());
			toAddSecond.setDeltaX(nDebrisChunkSpeed * Math.cos(Math.toRadians(dOrientation)));
			toAddSecond.setDeltaY(nDebrisChunkSpeed * Math.sin(Math.toRadians(dOrientation)));
			
			toAddSecond.setExpire(255/nColorStep);
			
			arlSecondExplosion.add(toAddSecond);
		}
	}
	
	public void draw(Graphics g)
	{
		g.setColor(getColor());
		if (!firstExplosionDisappeared)
			for (DebrisChunk dc: arlFirstExplosion) {
				dc.draw(g);
			}
		if (!secondExplosionDisappeared)
			for (DebrisChunk dc : arlSecondExplosion) {
				dc.draw(g);
			}
	}
	
	public void fadeInOut() {
		Color col = getColor();
		int red = col.getRed();
		int green = col.getGreen();
		int blue = col.getBlue();
		
		if (red-nColorStep>0) red -= nColorStep;
		if (green-nColorStep>0) green -= nColorStep;
		if (blue-nColorStep>0) blue -= nColorStep;
		if (red-nColorStep>0)
			setColor(new Color(red, green, blue));
		else
			setExploded();
	}
	
	public void move()
	{
		super.move();
		//then iterate over all of the chunks and call move on them
		if (!firstExplosionDisappeared)
			if (!isExplosionGone(arlFirstExplosion)) {
				for (DebrisChunk dc: arlFirstExplosion)
					dc.move();
			} else {
				firstExplosionDisappeared = true;
			}
			
		if (!secondExplosionDisappeared)
			if (!isExplosionGone(arlSecondExplosion)) {
				for (DebrisChunk dc : arlSecondExplosion) {
					dc.move();
				}
			} else {
				secondExplosionDisappeared = true;
			}
	}
	
	private boolean isExplosionGone(ArrayList<DebrisChunk> arlExplosion)
	{
		boolean bResult = true;
		for (DebrisChunk dc : arlExplosion) {
			if (dc.isOnScreen())
				bResult = false;
		}
		return bResult;
	}
	
}
