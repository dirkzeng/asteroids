package edu.uchicago.cs.java.finalproject.game.view;

import java.awt.*;
import java.awt.event.*;

import javax.management.MXBean;
import javax.swing.*;

import edu.uchicago.cs.java.finalproject.game.model.CommandCenter;
import edu.uchicago.cs.java.finalproject.game.model.Falcon;

public class GameFrame extends JFrame implements MouseMotionListener {

	private JPanel contentPane;
	private BorderLayout borderLayout1 = new BorderLayout();

	public GameFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Component initialization
	private void initialize() throws Exception {
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
	}

	@Override
	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
