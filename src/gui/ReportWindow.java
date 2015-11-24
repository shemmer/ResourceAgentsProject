package gui;

import java.util.AbstractMap.SimpleEntry;


import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Vector;


public class ReportWindow extends javax.swing.JFrame {
	private javax.swing.JPanel mainPanel = null;
	private Vector columnNames = new Vector();
	private Vector rows= new Vector();
	private JTable table;
	private JScrollPane scrollPane;
	public ReportWindow(){
		initialize();
	}

	private void initialize() {
		columnNames.add("Agent");
		columnNames.add("Resource : Remaining Capacity");
		columnNames.add("Resource : Initial Capacity");
		columnNames.add("Total Profit");
		columnNames.add("Final Baseline Prices");
		columnNames.add("Initial Baseline Prices");
		columnNames.add("Rounds won");
		columnNames.add("Rounds lost");
		setName("Stats");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setFont(new java.awt.Font("dialog",0, 10));
		setSize(800, 200);
		setTitle("Stats");
	}
	private javax.swing.JScrollPane getScrollPane(){
		table = new JTable(rows, columnNames);
		scrollPane = new JScrollPane(table);
		scrollPane.setVisible(true);
//		pack();
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(String.class, new MultiLineCellRenderer());
//		table.setRowHeight(50);
		return scrollPane;
	}
	public void addAgent(String agent, String content) {
		if(scrollPane==null){
			setContentPane(getScrollPane());
		}
		Vector<String> dummy = new Vector<String>();
		dummy.add(agent);
		String[] split = content.split("<<");
		for(int i=0; i < split.length; i++){
			String cellContent="";
			cellContent=split[i];
			dummy.add(cellContent);
		}
		rows.add(dummy);
		table.setVisible(true);
		table.revalidate();
		table.repaint();

		scrollPane.revalidate();
		scrollPane.repaint();
	}
	public void addEmpty(){
		if(table!=null){
			Vector<String> dummy = new Vector<String>();
			dummy.addElement("");dummy.addElement("");dummy.addElement("");
			dummy.addElement("");dummy.addElement("");dummy.addElement("");
			rows.add(dummy);
			table.revalidate();
			table.repaint();
		}
	}
}

class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

	  public MultiLineCellRenderer() {
	    setLineWrap(true);
	    setWrapStyleWord(true);
	    setOpaque(true);
	  }

	  public Component getTableCellRendererComponent(JTable table, Object value,
	      boolean isSelected, boolean hasFocus, int row, int column) {
	    if (isSelected) {
	      setForeground(table.getSelectionForeground());
	      setBackground(table.getSelectionBackground());
	    } else {
	      setForeground(table.getForeground());
	      setBackground(table.getBackground());
	    }
	    setFont(table.getFont());
	    if (hasFocus) {
	      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
	      if (table.isCellEditable(row, column)) {
	        setForeground(UIManager.getColor("Table.focusCellForeground"));
	        setBackground(UIManager.getColor("Table.focusCellBackground"));
	      }
	    } else {
	      setBorder(new EmptyBorder(1, 2, 1, 2));
	    }
	    setText((value == null) ? "" : value.toString());
	    return this;
	  }
	}
