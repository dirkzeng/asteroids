package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;


public class Bullet extends Sprite {

	private final double FIRE_POWER = 35.0;
	private boolean isVerbose = false;
	private int nColorStep = 10;
	private int nSize;
	
	public int getSize() {
		return nSize;
	}

	public void setSize(int nSize) {
		this.nSize = nSize;
	}

	public void makeVerbose() {
		isVerbose = true;
	}

	public Bullet(Sprite spr){
		super();
	
		//defined the points on a cartesean grid
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		pntCs.add(new Point(0,3)); //top point
		
		pntCs.add(new Point(1,-1));
		pntCs.add(new Point(0,-2));
		pntCs.add(new Point(-1,-1));
		
		assignPolarPoints(pntCs);
		
		//a bullet expires after 20 frames
		setExpire( 20 );
		setRadius(6);


		//everything is relative to the falcon ship that fired the bullet
		setDeltaX( spr.getDeltaX() +
		           Math.cos( Math.toRadians( spr.getOrientation() ) ) * FIRE_POWER );
		setDeltaY( spr.getDeltaY() +
		           Math.sin( Math.toRadians( spr.getOrientation() ) ) * FIRE_POWER );
		setCenter( spr.getCenter() );

		//set the bullet orientation to the falcon (ship) orientation
		setOrientation(spr.getOrientation());
	}
	
	public Bullet(Sprite spr, Color col) {
		this(spr);
		setColor(col);
	}
	
	public void makeTwoDirectional(Sprite spr, Color col) {

	}
	
	public void makeStarBullet() {
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		pntCs.add(new Point(0,3)); //top point
		pntCs.add(new Point(1,1));
		pntCs.add(new Point(3,0));
		pntCs.add(new Point(1,-1));
		pntCs.add(new Point(0,-3));
		pntCs.add(new Point(-1,-1));
		pntCs.add(new Point(-1,1));
		
		assignPolarPoints(pntCs);
	}
	
	public void fadeInOut() {
		Color col = getColor();
		int red = col.getRed();
		int green = col.getGreen();
		int blue = col.getBlue();
		
		if (red-nColorStep>0) red -= nColorStep;
		if (green-nColorStep>0) green -= nColorStep;
		if (blue-nColorStep>0) blue -= nColorStep;
			
		setColor(new Color(red, green, blue));
	}

    //override the expire method - once an object expires, then remove it from the arrayList. 
	public void expire(){
		if (isVerbose) System.out.println(getExpire());
 		if (getExpire() == 0)
 			setExploded();
		 else 
			setExpire(getExpire() - 1);
	}
	
	//we'll override the explode method so it doesn't make debris when it explodes.
	public void explode() {
		setExploded();
	}

}
