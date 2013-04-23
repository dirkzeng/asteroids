package edu.uchicago.cs.java.finalproject.game.view;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.game.model.CommandCenter;

public class StatusFrame extends JFrame {
	private JLabel lblShips;
	private JLabel lblAsteroids;
	private JLabel lblPowerUps;
	private JLabel lblShields;
	private JLabel lblLevel;
	private JPanel pnl;
	
	public StatusFrame() {
		JLabel lblShips = new JLabel();
		JLabel lblAsteroids = new JLabel();
		JLabel lblPowerUps = new JLabel();
		JLabel lblShields = new JLabel();
		JLabel lblLevel = new JLabel();
		pnl = new JPanel();
		
		pnl.setLayout(new GridLayout(5, 1));
		pnl.add(lblShips);
		pnl.add(lblAsteroids);
		pnl.add(lblPowerUps);
		pnl.add(lblShields);
		pnl.add(lblLevel);
		
		add(pnl);
		pack();
		
	}
	
	public void update() {
		lblShips.setText(CommandCenter.getNumFalcons() + " Falcons");
		lblAsteroids.setText(CommandCenter.getFoesNumber() + " Falcons");
		lblPowerUps.setText(CommandCenter.getPowerUpsNumber() + " Falcons");
		lblShields.setText(CommandCenter.getFalcon().getShield() + " Shield");
		lblLevel.setText(CommandCenter.getLevel() + " level");
		pack();
	}
}
