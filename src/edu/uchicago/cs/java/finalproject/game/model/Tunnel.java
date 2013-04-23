package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Tunnel {
	private ArrayList<Point> leftPoints;
	private ArrayList<Point> rightPoints;
	private final int NUM_PARTITIONS = 6;
	private double dPROB_CLOSE = 0.55;
	private int HORIZONTAL_SHIFT = 10;
	private int VERTICAL_SHIFT;
	private int nSpeed;
	private Color lineColor = Color.white;
	private Color tunnelColor = new Color(210, 105, 30);
	private int nShiftTimer = 0;
	private double dTopDistance;
	private boolean bGuaranteeWiden = false;
	private int nMinimumDistance = 10;
	private int nPoints = 0;
	private int nPointsTimer = 0;
	private int nTopLeftX;
	private int nTopRightX;
	
	int leftXCoords[] = new int[NUM_PARTITIONS + 4];
	int leftYCoords[] = new int[NUM_PARTITIONS + 4];
	int rightXCoords[] = new int[NUM_PARTITIONS + 4];
	int rightYCoords[] = new int[NUM_PARTITIONS + 4];
	
	public Tunnel() {
		reset();
	}
	
	
	
	public int getSpeed() {
		return nSpeed;
	}
	
	public void reset() {
		leftPoints = new ArrayList<Point>(NUM_PARTITIONS+4);
		rightPoints = new ArrayList<Point>(NUM_PARTITIONS+4);
		VERTICAL_SHIFT = Game.DIM.height / NUM_PARTITIONS;
		
		leftPoints.add(new Point(Game.DIM.width/5, 0));
		rightPoints.add(0, new Point(4*Game.DIM.width/5, 0));
		
		for (int i=1; i<=NUM_PARTITIONS+1; i++) {
			Point newLeftPoint = newPoint(leftPoints.get(i-1), true);
			Point newRightPoint = newPoint(rightPoints.get(i-1), false);
			leftPoints.add(newLeftPoint);
			rightPoints.add(newRightPoint);
			dTopDistance = newRightPoint.x-newLeftPoint.x;
		}
		
		leftPoints.add(new Point(0,0));
		leftPoints.add(new Point(0, Game.DIM.height));
		rightPoints.add(new Point(Game.DIM.width, 0));
		rightPoints.add(new Point(Game.DIM.width, Game.DIM.height));
		
		nSpeed = 50;
		
		if (CommandCenter.getFalcon() != null)
			CommandCenter.getFalcon().setDeltaX(0);
	}
	
	private Point newPoint(Point oldPoint, boolean isLeft) {
		Point pntReturn = new Point();
		pntReturn.y = oldPoint.y - VERTICAL_SHIFT;
		dPROB_CLOSE = dTopDistance/Game.DIM.width;
		int nShift = (int)(Game.R.nextDouble()*HORIZONTAL_SHIFT);
		
		if (isLeft) {
			if (Game.R.nextDouble() <= dPROB_CLOSE && !bGuaranteeWiden) {
				//then we make a narrower point
				pntReturn.x = oldPoint.x + nShift;
			} else {
				pntReturn.x = oldPoint.x - nShift;
				if (pntReturn.x < 0) pntReturn.x = 0;
			}
		} else {
			if (Game.R.nextDouble() <= dPROB_CLOSE && !bGuaranteeWiden) {
				pntReturn.x = oldPoint.x - nShift;
			} else {
				pntReturn.x = oldPoint.x + nShift;
				if (pntReturn.x > Game.DIM.width) pntReturn.x = Game.DIM.width;
			}
		}
		
		return pntReturn;
	}
	
	public void update() {
		for (int i=0; i< NUM_PARTITIONS + 1; i++) {
			leftPoints.get(i).y += nSpeed;
			rightPoints.get(i).y += nSpeed;
			leftYCoords[i+2] += nSpeed;
			rightYCoords[i+2] += nSpeed;
		}
		if (leftPoints.get(1).y >= Game.DIM.height) {
			for (int i=0; i<NUM_PARTITIONS+1; i++) {
				leftPoints.set(i, leftPoints.get(i+1));
				rightPoints.set(i, rightPoints.get(i+1));
			}
			
			Point newLeftPoint = newPoint(leftPoints.get(NUM_PARTITIONS), true);
			Point newRightPoint = newPoint(rightPoints.get(NUM_PARTITIONS), false);

			nTopLeftX = newLeftPoint.x;
			nTopRightX = newRightPoint.x;
			
			dTopDistance = newRightPoint.x-newLeftPoint.x;
			bGuaranteeWiden = (dTopDistance <= nMinimumDistance);
			
			leftPoints.set(NUM_PARTITIONS+1, newLeftPoint);
			rightPoints.set(NUM_PARTITIONS+1, newRightPoint);
		}
		nShiftTimer++;
		if (nShiftTimer > 5) {
			nShiftTimer = 0;
			HORIZONTAL_SHIFT += 1;
			nSpeed += 3;
		}
		
		nPointsTimer++;
		if (nPointsTimer == 10) {
			nPoints += HORIZONTAL_SHIFT;
			nPointsTimer = 0;
		}
	}
	
	public int[] getValidEntryRange() {
		int bounds[] = {nTopLeftX, nTopRightX};
		return bounds;
	}
	
	public int getPoints() {
		return nPoints;
	}
	
	public void addPoints(int n) {
		nPoints += n;
	}
	
	public void draw(Graphics g) {
		
		g.setColor(tunnelColor);
		for (int i=0; i<leftPoints.size(); i++) {
			leftXCoords[i] = leftPoints.get(i).x;
			leftYCoords[i] = leftPoints.get(i).y;
			rightXCoords[i] = rightPoints.get(i).x;
			rightYCoords[i] = rightPoints.get(i).y;
		}
			
		g.fillPolygon(leftXCoords, leftYCoords, NUM_PARTITIONS + 4);
		g.fillPolygon(rightXCoords, rightYCoords, NUM_PARTITIONS + 4);
		
		g.setColor(lineColor);
		for (int i=0; i<NUM_PARTITIONS+1; i++) {
			Point leftStart = leftPoints.get(i), leftEnd = leftPoints.get(i+1);
			Point rightStart = rightPoints.get(i), rightEnd = rightPoints.get(i+1);
			
			g.drawLine(leftStart.x, leftStart.y, leftEnd.x, leftEnd.y);
			g.drawLine(rightStart.x, rightStart.y, rightEnd.x, rightEnd.y);
		}
	}
	
	public boolean isCollision(Sprite spr) {
		Point pnt = spr.getCenter();
		int r = spr.getRadius();
		for (int i=0; i<NUM_PARTITIONS + 1; i++) {
			
			//check left side
			if (leftPoints.get(i).y >= pnt.y && leftPoints.get(i+1).y <= pnt.y) {
				if (leftPoints.get(i).x >= pnt.x-r && leftPoints.get(i).x >= pnt.x-r)
					return true;
			}
			//check right side
			if (rightPoints.get(i).y >= pnt.y && rightPoints.get(i+1).y <= pnt.y) {
				if (rightPoints.get(i).x <= pnt.x+r && rightPoints.get(i).x <= pnt.x+r)
					return true;
			}
		}
		return false;
	}

}
