package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import edu.uchicago.cs.java.finalproject.controller.Game;



public class DebrisChunk extends Sprite {
	private double dLen;
	private int nSpin;
	private Debris debParent;
	boolean isOnScreen = true;
	double dOffsetX;
	double dOffsetY;

	public DebrisChunk(double dLength, int nOrientation, Debris debParent)
	{
		this.dLen = dLength;
		this.nSpin = Game.R.nextInt(10);
		setOrientation(nOrientation);
		this.debParent = debParent;
	}
	
	public void draw(Graphics g)
	{
		if (isOnScreen || true) {
			int startX = (int) (getCenter().x + dLen/2 * Math.cos(Math.toRadians(getOrientation())));
			int startY = (int) (getCenter().y + dLen/2 * Math.sin(Math.toRadians(getOrientation())));
			int endX = (int) (getCenter().x - dLen/2 * Math.cos(Math.toRadians(getOrientation())));
			int endY = (int) (getCenter().y - dLen/2 * Math.sin(Math.toRadians(getOrientation())));
			
			Graphics2D graphics2d = (Graphics2D)g;
			graphics2d.drawLine(startX, startY, endX, endY);
			
			//System.out.printf("DebrisChunk drew a line from (%d, %d) to (%d, %d)\n",startX, startY, endX, endY);
		}
		
		expire();
	}
	
	public String toString()
	{
		return "Length: " + dLen + " orientation: " + getOrientation() + 
				" speed: (" + getDeltaX() + ", " + getDeltaY() + ")";
	}
	
	public void move() {	
		Point p = getCenter();
		updateOffset();
		
		setCenter(new Point((int) (getFieldCenter().x - dOffsetX),(int) (getFieldCenter().y + dOffsetY)));
		
		//we won't draw the sprite if it's not inside the bounds of the frame
		if (p.x > getDim().width || p.x < 0 || p.y > getDim().height || p.y < 0) {
			setIsOnScreen(false);
		}
		
		//update the debris chunk's orientation
		setOrientation(getOrientation() + getSpin());
		
	}
	
	public void updateOffset()
	{
		dOffsetX += getDeltaX();
		dOffsetY += getDeltaY();
	}
	
	public int getSpin() {
		return nSpin;
	}

	public void setSpin(int nSpin) {
		this.nSpin = nSpin;
	}
	
	private Point getFieldCenter()
	{
		return debParent.getCenter();
	}
	
	public void setIsOnScreen(boolean bArg)
	{
		isOnScreen = bArg;
	}
	
	public boolean isOnScreen()
	{
		return isOnScreen;
	}
	
	public void expire(){
 		if (getExpire() == 0) {
 			setIsOnScreen(false);
 		} else 
			setExpire(getExpire() - 1);
	}
	
}
