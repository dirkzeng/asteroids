package edu.uchicago.cs.java.finalproject.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Announcement extends JPanel {
	private ArrayList<String> strAnnouncements;
	private Color colAnnouncement;
	private Font fntAnnouncement = new Font("SansSerif", Font.BOLD, 12);
	private Font fntBigAnnouncement = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
	private FontMetrics fmtAnnouncement; 
	private int nAnnouncementFontWidth;
	private int nAnnouncementFontHeight;
	
	private boolean isAnnouncementDisplayed = false;
	private int nAnnouncementCount;
	
	public void makeAnnouncement (int nAnnouncementNumber) {
		strAnnouncements = new ArrayList<String>(nAnnouncementNumber);
		setOpaque(false);
		initAnnouncementView();
		setAnnouncementDisplayed(50);
	}
	
	public void makeAnnouncement (String... strAnnouncements) {
		makeAnnouncement(strAnnouncements.length);
		for (String str : strAnnouncements) {
			addAnnouncement(str);
		}
	}

	public void setCount(int nCount) {
		this.nAnnouncementCount = nCount;
	}
	
	public void countDown() {
		nAnnouncementCount--;
		if (nAnnouncementCount <= 0) {
			setAnnouncementDisplayed(false);
		}
	}
	
	public void setAnnouncementFont(Font fnt) {
		fntAnnouncement = fnt;
	}
	
	public Font getAnnouncementFont() {
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
	
	public void setAnnouncementDisplayed (boolean b) {
		isAnnouncementDisplayed = b;
	}
	
	public void setAnnouncementDisplayed (int nCount) {
		setAnnouncementDisplayed(true);
		setCount(nCount);
	}
	
	public boolean isAnnouncementDisplayed() {
		return isAnnouncementDisplayed;
	}
	
	public void addAnnouncement(String strAnnouncement) {
		strAnnouncements.add(strAnnouncement);
	}
	
	public void drawAnnouncements(Graphics g) {
		if (isAnnouncementDisplayed()) {
			g.setFont(getFont());
			//calculate number of lines to draw
			//for each linecalculate th
			
			for (String str : strAnnouncements) {
				g.drawString(str,
						(Game.DIM.width - fmtAnnouncement.stringWidth(str)) / 2, Game.DIM.height / 4);
			}
			
			countDown();
		}
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
