package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.File;

import javax.imageio.ImageIO;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Wormhole extends Sprite {
	private static Image img;
	static {
		try {
			img = ImageIO.read(new File("wormhole.png"));
		} catch (Exception e) {
			System.out.println("error: wormhole image file not found");
			System.exit(0);
	}
	}
	public Wormhole() {
		super();
		setRandomLocation();
		CommandCenter.getGamePanel().getStarMap().track(this);
		CommandCenter.setHasWormhole(true);
		setRadius(50);
	}
	private void setRandomLocation() {
		int x = Game.R.nextInt(Game.DIM.width);
		int y = Game.R.nextInt(Game.DIM.height);
		setCenter(new Point(x,y));
		int theta = Game.R.nextInt(360);
		double speed = Game.R.nextDouble() * 10;
		setDeltaX(speed*Math.cos(Math.toRadians(theta)));
		setDeltaY(speed*Math.sin(Math.toRadians(theta)));
	}
	
	public void draw(Graphics g) {
		g.drawImage(img, getCenter().x, getCenter().y, 100, 100, null, null);
	}
	
	public void explode() {
		CommandCenter.goThroughWormhole();
		CommandCenter.setHasWormhole(false);
		setExploded();
	}

}
