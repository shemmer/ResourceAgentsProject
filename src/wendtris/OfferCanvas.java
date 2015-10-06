package wendtris;

import java.awt.geom.AffineTransform;
import java.awt.*;
import java.awt.event.*;

public class OfferCanvas extends javax.swing.JPanel {
	private Offer offer = null;
	
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
public Offer getOffer() {
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
	int yPaint = y-yGap-5;
	
	int maxRows = offer.getMaxPriceTag();
	int maxCols = offer.maxCols;
	
	g2d.clearRect( 0, 0, x, y);
	
	double xDist = ((double)xPaint)/maxCols, yDist = ((double)yPaint)/maxRows;

	g2d.setColor( color[offer.getActiveObjectID()] );
	if( offer.getActiveObjectState())
	for( int i=0; i<maxCols; i++) if( activeObject[i] != 0) {
		g2d.fillRect( xGap+(int)(xDist*i), yGap+yPaint-(int)(yDist*offer.getActiveObjectPriceTag()+yDist*activeObject[i] ), (int)(xDist+1), (int)(yDist*activeObject[i]));
	}
	
	g2d.setColor(Color.black);
	// Rotate 90 degrees clockwise about the position (x, y)
	at.rotate( -Math.PI/2, y/2, y/2);
	g2d.transform(at);

	// Draw the string at the position (x, y)
	g2d.setFont( new Font( "Arial", Font.ITALIC, 15));
	g2d.drawString( "Price per used resource", 6, 14);

	// Rotate back
	g2d.setTransform(origTx);
	
	for( int i=0; i<maxRows; i++) {
		g2d.drawLine( xGap, yGap+(int)(yDist*i), xGap+xPaint, yGap+(int)(yDist*i));
		g2d.drawLine( xGap, yGap+(int)(yDist*i+1), xGap+xPaint, yGap+(int)(yDist*i+1));
	}
	g2d.drawLine( xGap, yGap+(int)(yDist*maxRows)-1, xGap+xPaint, yGap+(int)(yDist*maxRows)-1);	
	g2d.drawLine( xGap, yGap+(int)(yDist*maxRows), xGap+xPaint, yGap+(int)(yDist*maxRows));	

	for( int i=0; i<maxCols; i++) {
		g2d.drawLine( xGap+(int)(xDist*i), yGap, xGap+(int)(xDist*i), yGap + yPaint);
		g2d.drawLine( xGap+(int)(xDist*i+1), yGap, xGap+(int)(xDist*i+1), yGap + yPaint);
	}
	g2d.drawLine( xGap+(int)(xDist*maxCols)-1, yGap, xGap+(int)(xDist*maxCols)-1, yGap + yPaint);
	g2d.drawLine( xGap+(int)(xDist*maxCols)-2, yGap, xGap+(int)(xDist*maxCols)-2, yGap + yPaint);
		
}
public void setOffer ( Offer ms) {
	offer = ms;
	repaint();
}
}
