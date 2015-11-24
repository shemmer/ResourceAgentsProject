package gui;
import offer.Resource;
import java.awt.geom.AffineTransform;

import offer.OfferFactory;

import java.awt.*;
import java.awt.event.*;

public class OfferCanvas extends javax.swing.JPanel {
	private OfferFactory offer = null;

	/*	Gibt an, ob gerade pausiert wird. */
	private Color color[] = { Color.lightGray, Color.red, Color.cyan, Color.green, Color.orange, Color.blue, 
			Color.magenta, Color.pink, Color.orange, Color.white};

	public OfferCanvas() {
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
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform origTx = g2d.getTransform();
		AffineTransform at = new AffineTransform();

		int x = getSize().width;
		int y = getSize().height;

		byte[] activeObject = offer.getActiveObject();

		int xGap = 25;
		int yGap = 5;

		int xPaint = x-xGap;
		int yPaint = y-yGap-30;

		int maxRows = offer.getMaxPriceTag();
		int maxCols =Resource.allValuesAsList().size() - 3;

		g2d.clearRect( 0, 0, x, y);

		double xDist = ((double)xPaint)/maxCols, yDist = ((double)yPaint)/maxRows;

		g2d.setColor( color[offer.getShapeID()] );
		if( offer.getActiveObjectState())
			for( int i=0; i<maxCols; i++) if( activeObject[i] != 0) {
				g2d.fillRect( xGap+(int)(xDist*i), yGap+yPaint-(int)(yDist*offer.getActiveObjectPriceTag()+yDist*activeObject[i] ),
						(int)(xDist+1), (int)(yDist*activeObject[i]));

			}

		g2d.setColor(Color.black);
		// Rotate 90 degrees clockwise about the position (x, y)
		at.rotate( -Math.PI/2, y/2, y/2);
		g2d.transform(at);

		// Draw the string at the position (x, y)
		g2d.setFont( new Font( "Arial", Font.ITALIC, 15));
		g2d.drawString( "Requested capacity per resource", 35, 14);

		// Rotate back
		g2d.setTransform(origTx);

		for( int i=0; i<maxRows; i++) {
			//oben
			g2d.drawLine( xGap, yGap+(int)(yDist*i), xGap+xPaint, yGap+(int)(yDist*i));
			g2d.drawLine( xGap, yGap+(int)(yDist*i+1), xGap+xPaint, yGap+(int)(yDist*i+1));
		}
		//unten
		g2d.drawLine( xGap, yGap+(int)(yDist*maxRows)-1, xGap+xPaint, yGap+(int)(yDist*maxRows)-1);	
		g2d.drawLine( xGap, yGap+(int)(yDist*maxRows), xGap+xPaint, yGap+(int)(yDist*maxRows));	

		for( int i=0; i<maxCols; i++) {
			g2d.drawLine( xGap+(int)(xDist*i), yGap, xGap+(int)(xDist*i), yGap + yPaint);
			g2d.drawLine( xGap+(int)(xDist*i+1), yGap, xGap+(int)(xDist*i+1), yGap + yPaint);
		}


		g2d.setFont( new Font( "Dialog", Font.PLAIN, 10));
		g2d.drawString( "Res", 5, yPaint+20);

		for( int i=0; i<maxCols; i++) {
			g2d.drawString( Resource.allValuesAsList().get(i).toString(), xGap+(int)(xDist*i)+(i<9?8:4), yPaint+20);
		}
		g2d.drawLine( xGap+(int)(xDist*maxCols)-1, yGap, xGap+(int)(xDist*maxCols)-1, yGap + yPaint);
		g2d.drawLine( xGap+(int)(xDist*maxCols)-2, yGap, xGap+(int)(xDist*maxCols)-2, yGap + yPaint);

	}
	public void setOffer ( OfferFactory ms) {
		offer = ms;
		repaint();
	}
}
