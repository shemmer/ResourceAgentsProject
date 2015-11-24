package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;

import offer.Resource;

public class AgentCapacityCanvas extends JPanel {

	private HashMap<String, HashMap<Resource, byte[]>> agentResCapacityMap;
	private Color color[] = { Color.LIGHT_GRAY, Color.red, Color.cyan, Color.green, Color.orange, Color.blue, 
			Color.magenta, Color.pink, Color.orange, Color.white};
	public AgentCapacityCanvas(){
		super();
		agentResCapacityMap = new HashMap<String, HashMap<Resource, byte[]>>();
	}
	public void paintComponent(java.awt.Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform origTx = g2d.getTransform();
		AffineTransform at = new AffineTransform();

		int x = getSize().width - 1;
		int y = getSize().height - 40;

		int xGap = 25;
		int yGap = 35;

		int xPaint = x-xGap;
		int yPaint = y-yGap;


		int maxRows = 1;
		int maxCols = 0;

		for(String agent : agentResCapacityMap.keySet()){
			HashMap<Resource, byte[]> map = this.agentResCapacityMap.get(agent);
			for(Resource r : map.keySet()){
				maxCols++;
				byte[] usedCapacity= map.get(r);
				if(usedCapacity.length> maxRows) maxRows = usedCapacity.length; 
			}
		}
		
		g2d.setColor( Color.LIGHT_GRAY);
		g2d.clearRect( 0, 0, x, y+40);

		double xDist = ((double)xPaint)/maxCols, yDist = ((double)yPaint)/maxRows;
		if(maxCols==0) xDist = ((double)xPaint)/1;
		int i =0;
		//Column
		for(String agent : agentResCapacityMap.keySet()){
			HashMap<Resource, byte[]> map = this.agentResCapacityMap.get(agent);
			//Another column
			for(Resource r : map.keySet()){
				byte[] usedCapacity = map.get(r);
				for(int j=0; j< usedCapacity.length; j++){
					g2d.setColor( color[usedCapacity[j]] );
					g2d.fillRect( xGap+(int)(xDist*i),  yGap+yPaint-(int)(yDist*(j+1)), (int)(xDist+1), (int)(yDist+1));
					g2d.setColor(Color.black);
					g2d.setFont( new Font( "Dialog", Font.PLAIN, 10));
					g2d.drawString(agent +":", xGap+(int)(xDist*i), y+20);
					g2d.drawString(r.toString(), xGap+(int)(xDist*i), y +20 + g.getFontMetrics().getHeight());
				}
				i++;
			}
		}



		g2d.setColor(Color.black);
		// Rotate 90 degrees clockwise about the position (x, y)
		at.rotate( -Math.PI/2, y/2, y/2);
		g2d.transform(at);

		// Draw the string at the position (x, y)
		g2d.setFont( new Font( "Arial", Font.ITALIC, 15));
		g2d.drawString( "Used capacity per Agent & Resource",  20, 14);

		// Rotate back
		g2d.setTransform(origTx);

		g2d.setColor(Color.black);

		for( int j=0; j<maxRows; j++) {
			//Oben
			g2d.drawLine( xGap, yGap+(int)(yDist*j), xGap+xPaint, yGap+(int)(yDist*j));
			g2d.drawLine( xGap, yGap+(int)(yDist*j+1), xGap+xPaint, yGap+(int)(yDist*j+1));
		}
		//Dickere Linie... Unten
		g2d.drawLine( xGap, yGap+(int)(yDist*maxRows)-1, xGap+xPaint, yGap+(int)(yDist*maxRows)-1);	
		g2d.drawLine( xGap, yGap+(int)(yDist*maxRows)-2, xGap+xPaint, yGap+(int)(yDist*maxRows)-2);	

		for( int j=0; j<maxCols; j++) {
			g2d.drawLine( xGap+(int)(xDist*j), yGap, xGap+(int)(xDist*j), y);
			g2d.drawLine( xGap+(int)(xDist*j+1), yGap, xGap+(int)(xDist*j+1), y);
		}
		if(maxCols==0){
			//rechts
			g2d.drawLine( xGap+(int)(xDist*1), yGap, xGap+(int)(xDist*1), y);
			g2d.drawLine( xGap+(int)(xDist*2), yGap, xGap+(int)(xDist*2), y);
			//Links
			g2d.drawLine( xGap+(int)(xDist*maxCols)-1, yGap, xGap+(int)(xDist*maxCols)-1, y);
			g2d.drawLine( xGap+(int)(xDist*maxCols)-2, yGap, xGap+(int)(xDist*maxCols)-2, y);			

		}else{
			g2d.drawLine( xGap+(int)(xDist*maxCols)-1, yGap, xGap+(int)(xDist*maxCols)-1, y);
			g2d.drawLine( xGap+(int)(xDist*maxCols)-2, yGap, xGap+(int)(xDist*maxCols)-2, y);			
		}

	}
	public void addAgent(String agent, Resource r, int totalCapacity){
		byte[] usedCapacity = new byte[totalCapacity];
		HashMap<Resource, byte[]> map= new HashMap<Resource, byte[]>();
		map.put(r, usedCapacity);
		this.agentResCapacityMap.put(agent, map);
	}
	public void addAgentResource(String agent, Resource r, int newUsedCapacity, byte color){
		if(!this.agentResCapacityMap.containsKey(agent)){
			this.addAgent(agent, r, 0);
		}else{
			if(!this.agentResCapacityMap.get(agent).containsKey(r)) this.agentResCapacityMap.get(agent).put(r, new byte[1]);
		}
		int tmpj =0;
		byte[] usedCapacity = this.agentResCapacityMap.get(agent).get(r);
		while(tmpj<usedCapacity.length){
			if(usedCapacity[tmpj]==0) break;
			tmpj++;
		}
		System.out.println(agent + " ." +tmpj + "new " + newUsedCapacity + "usedCapacity " +  usedCapacity.length);
		if(this.agentResCapacityMap.get(agent).get(r).length< tmpj + newUsedCapacity){
			byte[] tmp = new byte[this.agentResCapacityMap.get(agent).get(r).length + newUsedCapacity];
			for(int i=0; i<this.agentResCapacityMap.get(agent).get(r).length ;i++){
				tmp[i] = usedCapacity[i];
			}
			usedCapacity=tmp;
		}
		for(int i = 0; i < usedCapacity.length; i++)
		{
			if(usedCapacity[i]==0){
				for(int j=i; j<i+newUsedCapacity;j++)
					usedCapacity[j]= color;
				break;
			}
		}
		this.agentResCapacityMap.get(agent).put(r, usedCapacity);
	}
	public void print(){
		for(String agent : this.agentResCapacityMap.keySet()){
			System.err.print(agent + "\t");
			for(Resource r : this.agentResCapacityMap.get(agent).keySet()){
				System.err.print(r+ "\t");
				for(int i=0; i<this.agentResCapacityMap.get(agent).get(r).length;i++){
					System.err.print(this.agentResCapacityMap.get(agent).get(r)[i] +"\t");
				}
			}
			System.err.print("\n");
		}
	}
	public void reset(){
		agentResCapacityMap = new HashMap<String, HashMap<Resource, byte[]>>();
		repaint();
	}
}
