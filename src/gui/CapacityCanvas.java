package gui;

import java.awt.geom.AffineTransform;

import offer.OfferFactory;

import java.awt.*;
import java.awt.event.*;

public class CapacityCanvas extends javax.swing.JPanel {

	/*	Gibt an, ob gerade pausiert wird. */
	private Color color[] = { Color.lightGray, Color.red, Color.cyan, Color.green, Color.orange, Color.blue, 
			Color.magenta, Color.pink, Color.orange, Color.white};

	private OfferFactory offer;
	public CapacityCanvas() {
		super();
	}
	/**
	 * Die Beschreibung der Methode hier eingeben.
	 * Erstellungsdatum: (08.12.2002 15:40:09)
	 * @return wendtris.MySpace
	 */
	public OfferFactory getOffer() {
		return offer;
	}
	public void paintComponent(java.awt.Graphics g) {
		if( offer == null || g == null) return;

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform origTx = g2d.getTransform();
		AffineTransform at = new AffineTransform();

		int x = getSize().width;
		int y = getSize().height;

		int xGap = 25;
		int yGap = 35;

		int xPaint = x-xGap;
		int yPaint = y-yGap;

		int maxRows = offer.maxRows;
		int maxCols = offer.maxCols;

		g2d.setColor( Color.lightGray);
		g2d.clearRect( 0, 0, x, y);

		double xDist = ((double)xPaint)/maxCols, yDist = ((double)yPaint)/maxRows;

		for( int i=0; i<maxCols; i++){ 
			for( int j=0; j<maxRows; j++) {
				if( offer.history[i][j]!=0) {
					g2d.setColor( color[offer.history[i][j]] );
					g2d.fillRect( xGap+(int)(xDist*i),  yGap+yPaint-(int)(yDist*(j+1)), (int)(xDist+1), (int)(yDist+1));
				}
			}
		}
		g2d.setColor(Color.black);
		at.rotate( -Math.PI/2, y/2, y/2);
		g2d.transform(at);
		g2d.drawString("test", 100, 100);

		g2d.setColor(Color.black);
		// Rotate 90 degrees clockwise about the position (x, y)
		at.rotate( Math.PI/2, y/2, y/2);
		g2d.transform(at);

		// Draw the string at the position (x, y)
		g2d.setFont( new Font( "Arial", Font.ITALIC, 15));
		g2d.drawString( "Capacity of resource", 20, 14);

		// Rotate back
		g2d.setTransform(origTx);

		for( int i=0; i<maxRows; i++) {
			g2d.drawLine( xGap, yGap+(int)(yDist*i), xGap+xPaint, yGap+(int)(yDist*i));
			g2d.drawLine( xGap, yGap+(int)(yDist*i+1), xGap+xPaint, yGap+(int)(yDist*i+1));
		}
		g2d.drawLine( xGap, yGap+(int)(yDist*maxRows)-1, xGap+xPaint, yGap+(int)(yDist*maxRows)-1);	
		g2d.drawLine( xGap, yGap+(int)(yDist*maxRows)-2, xGap+xPaint, yGap+(int)(yDist*maxRows)-2);	

		g2d.setFont( new Font( "Dialog", Font.BOLD, 12));
		g2d.drawString( "t = ", 10, 16);
		for( int i=0; i<maxCols; i++) {
			g2d.drawLine( xGap+(int)(xDist*i), yGap, xGap+(int)(xDist*i), y);
			g2d.drawLine( xGap+(int)(xDist*i+1), yGap, xGap+(int)(xDist*i+1), y);
			g2d.drawString( String.valueOf(i+1), xGap+(int)(xDist*i)+(i<9?8:4), 16);
		}
		g2d.drawLine( xGap+(int)(xDist*maxCols)-1, yGap, xGap+(int)(xDist*maxCols)-1, y);
		g2d.drawLine( xGap+(int)(xDist*maxCols)-2, yGap, xGap+(int)(xDist*maxCols)-2, y);

	}
	/**
	 * Update offer object of Capacity Canvas and repaint
	 *
	 * @param newMySpace wendtris.MySpace
	 */
	public void setOffer(OfferFactory newOffer) {
		offer = newOffer;
		repaint();
	}
}
