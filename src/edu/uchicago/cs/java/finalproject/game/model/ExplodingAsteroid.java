package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;

public class ExplodingAsteroid extends Asteroid {
	private int nCountDown;
	private int nNumberOfChunks;
	private int nChunkSpeed;

	public ExplodingAsteroid() {
		super(0);
		setColor(Color.red);
		setBackgroundColor(Color.red);
		nCountDown = 80;
		nNumberOfChunks = 5*getChunkNumber();
		nChunkSpeed = 20;
	}
	
	public void move() {
		super.move();
		nCountDown--;
		if (nCountDown <= 0 && !hasExploded()) {
			explodeDangerous();
		}
	}
	
	public void explodeDangerous() {
		
		for (int i=0; i<nNumberOfChunks; i++) {
			int nOrientation = (int) (360.0/nNumberOfChunks * i);
			Bullet newChunk = new Bullet(this, Color.red);
			newChunk.makeStarBullet();
			newChunk.setDeltaX(nChunkSpeed * Math.cos(Math.toRadians(nOrientation + getOrientation())));
			newChunk.setDeltaY(nChunkSpeed * Math.sin(Math.toRadians(nOrientation + getOrientation())));
			newChunk.setOrientation(nOrientation);
			newChunk.setSize(1);
			CommandCenter.movFoes.add(newChunk);
		}
		
		setExploded();
	}
}
