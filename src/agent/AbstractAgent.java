package agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public abstract class AbstractAgent extends Agent{
	//Type of agent
	protected String[] service;
	// Directory-Facilitator Description of the agent
	protected DFAgentDescription dfAgentDescr;
	//Service Name
	protected String serviceName;
	
	//Path to History Information of agents
	protected String path;
	//XML Doc
	protected Document doc;
	
	//History Information Root
	protected Element root;
	
	protected DocumentBuilder db;
	
	
	protected File logFile;
	/**
	 * Setup Method
	 */
	protected void setup(){
		//Printout a welcome message
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Clean-up operations
	 */
	protected void takeDown(){
		try {
			DFService.deregister(this);
		}
		catch(Exception e){
			System.err.println("Could not deregister agent " + getAID().getName());
		}
	}
	/**
	 * Registers each agent at the df that is can be found via the given type
	 * @param service - the name under which the agent can be searched at the df
	 */
	protected void registerAtDF(){
		String serv = "";
		dfAgentDescr = new DFAgentDescription();
		dfAgentDescr.setName(this.getAID());
		for(int i = 0; i<service.length; i++){
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(service[i]);
			dfAgentDescr.addServices(sd);	
			serv = serv + service[i];
		}
		try {
			DFService.register(this, dfAgentDescr);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Send a message to a single agent
	 */
	protected ACLMessage createMessage(int performative, String content, AID agent){
		ACLMessage msg = new ACLMessage(performative);
		msg.setSender(this.getAID());
		msg.addReceiver(agent);
		msg.setLanguage("Java");
		msg.setContent(content);
		msg.setOntology("");
		return msg;
	}
	
	/**
	 * Write History Information to a xml file
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 * @throws FileNotFoundException 
	 */
	protected void writeHistoryToXML() {
		Transformer tr;
		try {
			tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(
					path)));
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {			
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	private void readHistoryInfoMemory() {
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(this.path);
			Element doc = dom.getDocumentElement();
			NodeList shapeNodes = doc.getElementsByTagName("shape");
			for (int i = 0; i < shapeNodes.getLength(); i++) {
				Element shapeElement = (Element) shapeNodes.item(i);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not find memory file: "
					+ path + ".");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
