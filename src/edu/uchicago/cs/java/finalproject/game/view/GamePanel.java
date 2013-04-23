package edu.uchicago.cs.java.finalproject.game.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.game.model.CommandCenter;
import edu.uchicago.cs.java.finalproject.game.model.Falcon;
import edu.uchicago.cs.java.finalproject.game.model.Movable;
import edu.uchicago.cs.java.finalproject.game.model.PowerUp;
import edu.uchicago.cs.java.finalproject.game.model.Sprite;
import edu.uchicago.cs.java.finalproject.game.model.StarMap;
import edu.uchicago.cs.java.finalproject.game.model.StarMap;
import edu.uchicago.cs.java.finalproject.game.model.Tunnel;


 public class GamePanel extends Panel {
	
	// ==============================================================
	// FIELDS 
	// ============================================================== 
	 
	// The following "off" vars are used for the off-screen double-bufferred image. 
	private Dimension dimOff;
	private Image imgOff;
	private Graphics grpOff;
	
	private GameFrame gmf;
	private Font fnt = new Font("SansSerif", Font.BOLD, 12);
	private Font fntBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
	private FontMetrics fmt; 
	private int nFontWidth;
	private int nFontHeight;
	private String strDisplay = "";
	private StarMap smap;
	public ArrayList<Announcement> announcements;
	private Tunnel tun;
	
	private static Image imgSplash; 
	static {
		try {
			imgSplash = ImageIO.read(new File("asteroids_splash.png"));
		}catch (Exception e) {
			System.out.println("Error: splash screen image not found.");
		}
	}

	//=============================================
	//ANNOUNCEMENT STUFF
	//=============================================
	public class Announcement {
		private ArrayList<String> strAnnouncements;
		private Color colAnnouncement;
		private Font fntAnnouncement = new Font("SansSerif", Font.BOLD, 60);
		private Font fntBigAnnouncement = new Font("SansSerif", Font.BOLD + Font.ITALIC, 100);
		private FontMetrics fmtAnnouncement; 
		private int nAnnouncementFontWidth;
		private int nAnnouncementFontHeight;
		
		private boolean isDisplayed = false;
		private int nCount;
		private int nFade;
		
		
		private int centerX, centerY;
		
		public Announcement (int nAnnouncementNumber) {
			strAnnouncements = new ArrayList<String>(nAnnouncementNumber);
			initAnnouncementView();
			setDisplayed(50);
			setCenter(Game.DIM.width/2, Game.DIM.height/2);
			setColor(Color.green);
			nFade = 10;
		}
		
		public Announcement (String... strAnnouncements) {
			this(strAnnouncements.length);
			for (String str : strAnnouncements) {
				addAnnouncement(str);
			}
		}
		
		public Announcement(int nFontSize, Color col, String... strAnnouncements) {
			
		}
		
		public void fade() {
			Color c = getColor();
			int alpha = c.getAlpha();
			if (alpha-nFade >= 0)
				alpha -= nFade;
			setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
		}
	
		public void setCount(int nCount) {
			this.nCount = nCount;
		}
		
		public void countDown() {
			nCount--;
			if (nCount <= 0) {
				setDisplayed(false);
			}
		}
		
		public void setFont(Font fnt) {
			fntAnnouncement = fnt;
		}
		
		public Font getFont() {
			if  (fntAnnouncement == null)
				fntAnnouncement = new Font("SansSerif", Font.BOLD, 12);
			return fntAnnouncement;
		}
		
		public void setAnnouncementBigFont(Font fnt) {
			fntBigAnnouncement = fnt;
		}
		
		public Font getAnnouncementBigFont() {
			if (fntBigAnnouncement == null)
				fntBigAnnouncement = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
			return fntBigAnnouncement;
		}
		
		public void setDisplayed (boolean b) {
			isDisplayed = b;
		}
		
		public void setDisplayed (int nCount) {
			setDisplayed(true);
			setCount(nCount);
		}
		
		public boolean isDisplayed() {
			return isDisplayed;
		}
		
		public void setCenter(int x, int y) {
			centerX = x;
			centerY = y;
		}
		
		public void setCenter(Point p) {
			setCenter(p.x, p.y);
		}
		
		public void makePeripheral() {
			setFont(new Font("SansSerif", Font.BOLD, 15));
			initAnnouncementView();
			setCenter(fmtAnnouncement.stringWidth(strAnnouncements.get(0))/2 + 5, 
					Game.DIM.height - 3*fmtAnnouncement.getHeight());
		}
		
		public void addAnnouncement(String strAnnouncement) {
			strAnnouncements.add(strAnnouncement);
		}
		
		public void setColor(Color c) {
			colAnnouncement = c;
		}
		
		public Color getColor() {
			return colAnnouncement;
		}
		
		public void drawAnnouncements(Graphics g) {
			g.setColor(getColor());
			g.setFont(getFont());
			//calculate number of lines to draw
			//for each linecalculate th
			int nLinesToDraw = strAnnouncements.size();
			int nLineHeight = fmtAnnouncement.getHeight();
			int nMaxHeight = (centerY + (int) (nLinesToDraw/2.0 * nLineHeight));
			for (int i = 0; i<strAnnouncements.size(); i++) {
				g.drawString(strAnnouncements.get(i),
						centerX - (fmtAnnouncement.stringWidth(strAnnouncements.get(i))) / 2, 
						nMaxHeight - nLineHeight * (nLinesToDraw - i));
			}
			
			countDown();
			fade();
		}
		
		private void initAnnouncementView() {
			Graphics g = getGraphics();			// get the graphics context for the panel
			g.setFont(getFont());						// take care of some simple font stuff
			fmtAnnouncement = g.getFontMetrics();
			nAnnouncementFontWidth = fmtAnnouncement.getMaxAdvance();
			nAnnouncementFontHeight = fmtAnnouncement.getHeight();
			g.setFont(getAnnouncementBigFont());					// set font info
		}
	}
	//=============================================
	// END ANNOUNCEMENT STUFF
	//=============================================
	
	
	//status bars
	private int nStatusBarHeight = 20;
	private int nStatusBarWidth = 100;
	private int nStatusBarX = Game.DIM.width - 120;
	private int nStatusBarY = 20;
	private int nStatusBarOffset = 5;
	
	
	private final int NUMBER_OF_STARS = 400;

	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public GamePanel(Dimension dim){
	    gmf = new GameFrame();
		gmf.getContentPane().add(this);
		
		gmf.pack();
		initView();
		
		smap = new StarMap(NUMBER_OF_STARS);
		
		announcements = new ArrayList<Announcement>();
		
//		announcements.add(new Announcement("WELCOME TO", "ASTEROIDS"));
		
		CommandCenter.setGamePanel(this);
		
		gmf.setSize(dim);
		gmf.setTitle("Game Base");
		gmf.setResizable(false);
		gmf.setVisible(true);
		this.setFocusable(true);
		tun = new Tunnel();
		CommandCenter.setTunnel(tun);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================
	
	public Tunnel getTunnel() {
		return tun;
	}
	
	public void setStarDirection(double dx, double dy) {
		smap.setDirection(dx, dy);
	}
	
	private void drawScore(Graphics g) {
		g.setColor(Color.white);
		g.setFont(fnt);
		if (CommandCenter.getScore() != 0) {
			g.drawString("SCORE :  " + CommandCenter.getScore(), nFontWidth, nFontHeight);
		} else {
			g.drawString("NO SCORE", nFontWidth, nFontHeight);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void update(Graphics g) {
		if (grpOff == null || Game.DIM.width != dimOff.width
				|| Game.DIM.height != dimOff.height) {
			dimOff = Game.DIM;
			imgOff = createImage(Game.DIM.width, Game.DIM.height);
			grpOff = imgOff.getGraphics();
		}
		// Fill in background with black.
		grpOff.setColor(Color.black);
		//turn on antialiasing
		Graphics2D g2 = (Graphics2D) grpOff;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);
		
		// Draw the stars.
		smap.update();
		if (smap.isHyperspaceDone()) {
//			if ()
			Game.setMode(smap.getNextMode());
			CommandCenter.getTunnel().reset();
			smap = new StarMap(NUMBER_OF_STARS);
		}
		smap.draw(grpOff);

		drawScore(grpOff);
		
		if (Game.mode == Game.Mode.START || CommandCenter.isGameOver()) {
			displaySplashScreen();
		} else if (Game.mode == Game.Mode.PAUSED) {
			strDisplay = "Game Paused";
			grpOff.drawString(strDisplay,
					(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
		}
		
		//playing and not paused!
		else {

			if (Game.Mode.ASTEROIDS == Game.mode) {
				//draw them in decreasing level of importance
				//friends will be on top layer and debris on the bottom
				iterateMovables(grpOff, 
						   CommandCenter.movDebris,
				           CommandCenter.movFloaters, 
				           CommandCenter.movFoes,
				           CommandCenter.movFriends);
				
				
				drawNumberShipsLeft(grpOff);
				drawStatusBars(grpOff);
				if (CommandCenter.isGameOver()) {
					CommandCenter.setPlaying(false);
					//bPlaying = false;
				}
			} else if (Game.mode == Game.Mode.TUNNEL){ //then we're in tunnel mode
				tun.update();
				tun.draw(grpOff);
				iterateMovables(grpOff, CommandCenter.movDebris, 
						CommandCenter.movFriends, 
						CommandCenter.movTunnelPowerUps);
				drawTunnelScore(grpOff);
			}
		}
		//draw the double-Buffered Image to the graphics context of the panel
		//announcements will always be drawn if they're there.
		for (Announcement ann : announcements) {
			if (ann.isDisplayed())
				ann.drawAnnouncements(grpOff);
		}
		g.drawImage(imgOff, 0, 0, this);
	} 
	
	public void makeLevelAnnouncement(int nLevel) {
		announcements.add(new Announcement("Level", "" + nLevel));
	}
	
	public StarMap getStarMap() {
		return smap;
	}
	
	public void makePowerUpAnnouncement(PowerUp p) {
		Announcement ann = new Announcement();
		for (String str : p.getAnnouncement()) {
			ann.addAnnouncement(str);
		}
		
		ann.setCount(30);
		ann.makePeripheral();
		ann.setCenter(p.getCenter());
		announcements.add(ann);
	}


	public void clearAnnouncements() {
		for (Announcement ann : announcements) {
			ann.setDisplayed(false);
		}
	}
	
	//for each movable array, process it.
	private void iterateMovables(Graphics g, CopyOnWriteArrayList<Movable>...movMovz){
		
		for (CopyOnWriteArrayList<Movable> movMovs : movMovz) {
			for (Movable mov : movMovs) {

				mov.move();
				mov.draw(g);
				mov.fadeInOut();
				mov.expire();
				if (((Sprite)mov).hasExploded())
					movMovs.remove(mov);
			}
		}
		
		if (CommandCenter.getFalcon() != null)
			setStarDirection((-1)*CommandCenter.getFalcon().getDeltaX(), (-1)*CommandCenter.getFalcon().getDeltaY());
	}
	

	// Draw the number of falcons left on the bottom-right of the screen. 
	private void drawNumberShipsLeft(Graphics g) {
		Falcon fal = CommandCenter.getFalcon();
		if (fal != null) {
			double[] dLens = fal.getLengths();
			int nLen = fal.getDegrees().length;
			Point[] pntMs = new Point[nLen];
			int[] nXs = new int[nLen];
			int[] nYs = new int[nLen];
		
			//convert to cartesean points
			for (int nC = 0; nC < nLen; nC++) {
				pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
						.toRadians(90) + fal.getDegrees()[nC])),
						(int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
								+ fal.getDegrees()[nC])));
			}
			
			//set the color to white
			g.setColor(Color.white);
			//for each falcon left (not including the one that is playing)
			for (int nD = 1; nD < CommandCenter.getNumFalcons(); nD++) {
				//create x and y values for the objects to the bottom right using cartesean points again
				for (int nC = 0; nC < fal.getDegrees().length; nC++) {
					nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
					nYs[nC] = pntMs[nC].y + Game.DIM.height - 40;
				}
				g.drawPolygon(nXs, nYs, nLen);
			}
		} 
	}
	
	public void drawTunnelScore(Graphics g) {
		int x = Game.DIM.width/2;
		int y = 5*Game.DIM.height/6;
		String strPoints = "" + tun.getPoints();
		int nPointsWidth = fmt.stringWidth(strPoints);
		g.setColor(Color.white);
		g.setFont(new Font("SansSerif", Font.BOLD, 36));
		int nheight = fmt.getHeight();
		g.drawString(strPoints, x - nPointsWidth, y + nheight);
	}
	
	public void drawStatusBars(Graphics g){
		int nShield = CommandCenter.getFalcon().getShield();
		double dMaxShield = (double) CommandCenter.getFalcon().getMaxShield();
		int nShieldBarWidth = (int) (nShield/dMaxShield * nStatusBarWidth);
		
		String strShieldStatus = "Shields: " + nShield + "/" + (int)dMaxShield + "  ";
		int nShieldStatusWidth = fmt.stringWidth(strShieldStatus);
		
		g.setColor(Color.cyan);
		g.fillRect(nStatusBarX, nStatusBarY, nShieldBarWidth, nStatusBarHeight);
		g.setColor(Color.white);
		g.drawRect(nStatusBarX, nStatusBarY, nStatusBarWidth, nStatusBarHeight);
		g.drawString(strShieldStatus, nStatusBarX - nShieldStatusWidth, nStatusBarY + nFontHeight);
		
	}
	
	private void initView() {
		Graphics g = getGraphics();			// get the graphics context for the panel
		g.setFont(fnt);						// take care of some simple font stuff
		fmt = g.getFontMetrics();
		nFontWidth = fmt.getMaxAdvance();
		nFontHeight = fmt.getHeight();
		g.setFont(fntBig);					// set font info
	}
	
	// This method draws some text to the middle of the screen before/after a game
	private void displaySplashScreen() {
		
		grpOff.drawImage(imgSplash, 0, 0, Game.DIM.width, Game.DIM.height, null);
		if (CommandCenter.getScore() != 0) {
			//print score on screen
			grpOff.setFont(new Font("SansSerif", Font.BOLD, 80));
			FontMetrics f = grpOff.getFontMetrics();
			String strGameOver = "GAME OVER";
			String strScore = "Score: " + CommandCenter.getScore();
			grpOff.setColor(Color.black);
			grpOff.drawString(strGameOver, (Game.DIM.width - f.stringWidth(strGameOver) + 20)/2, 110);
			grpOff.drawString(strScore, (Game.DIM.width - f.stringWidth(strScore) + 20)/2, 200);
			grpOff.setColor(Color.green);
			grpOff.drawString(strGameOver, (Game.DIM.width - f.stringWidth(strGameOver))/2, 100);
			grpOff.drawString(strScore, (Game.DIM.width - f.stringWidth(strScore))/2, 190);
			
		}
	}

	
	public GameFrame getFrm() {return this.gmf;}
	public void setFrm(GameFrame frm) {this.gmf = frm;}	
}