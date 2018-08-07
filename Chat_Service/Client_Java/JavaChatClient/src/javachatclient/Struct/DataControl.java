/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachatclient.Struct;

import javachatclient.Struct.MessageStruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javachatclient.GlobalStatic;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ubuntu
 */
public class DataControl {

    /* Public variables */
    public String desUsr;
    public String curUsr;
    public String Message;
    public int numberOfElement;
    private Map<String, MessageStruct> datamap= new HashMap<String, MessageStruct>(); 
    private String Path;
    
    public DataControl() {
    }
    
    private void BuildPath(String NameOfFile){
        Path = System.getProperty("user.dir") + "/data/" + GlobalStatic.myUserName  + NameOfFile + ".xml";
    }
    
    /*
 
    *Function : CreateXMLFile(String desUsr)
    *Description: Create xml file with root is desUsr. This file contain
                  all message from desUsr
    *Argument: String destination user
    *Return: Error
    Note:  

    */
    public int CreateXMLFile(String NameOfFile){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("PrivateMessage");
            doc.appendChild(rootElement);
            
            // block elements
            Element block = doc.createElement("block");
            rootElement.appendChild(block);
            
            // set attribute to staff element
            Attr attr = doc.createAttribute("id");
            attr.setValue(String.valueOf(numberOfElement));
            block.setAttributeNode(attr);
            
            // current user  elements
            Element currentUsr = doc.createElement("CurrentUser");
            currentUsr.appendChild(doc.createTextNode(curUsr));
            block.appendChild(currentUsr);
            
            // destination user elements
            Element destinationUsr = doc.createElement("DestinationUser");
            destinationUsr.appendChild(doc.createTextNode(desUsr));
            block.appendChild(destinationUsr);
            
            // message elements
            Element mess = doc.createElement("Message");
            mess.appendChild(doc.createTextNode(Message));
            block.appendChild(mess);
            
            WriteXML(doc, NameOfFile);
            
            return 1;
            
        } catch (Exception e) {
            return 0;
        }
        
        //return 0;
    }
    
    private void WriteXML(Document doc, String NameOfFile){
        try {
            BuildPath(NameOfFile);
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(Path));
            
            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
        } catch (Exception e) {
        }
    }
    
    /*
 
    *Function : CreateXMLFile(String desUsr)
    *Description: Create xml file with root is desUsr. This file contain
                  all message from desUsr
    *Argument: String destination user
    *Return: Error
    Note:  

    */
    public int CheckExistXMLFile(String NameOfFile){
        try {
            BuildPath(NameOfFile);
            File f = new File(Path);
            System.out.println(Path);
            if (f.exists()){
                System.out.println("file existed");
                return 1;
            }
            else {
                System.out.println("file not found");
                return 0;
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    /*
 
    *Function : CreateXMLFile(String desUsr)
    *Description: Create xml file with root is desUsr. This file contain
                  all message from desUsr
    *Argument: String destination user
    *Return: Error
    Note:  

    */
    public int AppendXMLFile(String NameOfFile){
        try {
            BuildPath(NameOfFile);
            
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            // root elements
            Document doc = docBuilder.parse(Path);
            Element rootElement = doc.getDocumentElement();
            //doc.appendChild(rootElement);
            
            // block elements
            Element block = doc.createElement("block");
            rootElement.appendChild(block);
            
            // set attribute to staff element
            Attr attr = doc.createAttribute("id");
            attr.setValue(String.valueOf(numberOfElement));
            block.setAttributeNode(attr);
            
            // current user  elements
            Element currentUsr = doc.createElement("CurrentUser");
            currentUsr.appendChild(doc.createTextNode(curUsr));
            block.appendChild(currentUsr);
            
            // destination user elements
            Element destinationUsr = doc.createElement("DestinationUser");
            destinationUsr.appendChild(doc.createTextNode(desUsr));
            block.appendChild(destinationUsr);
            
            // message elements
            Element mess = doc.createElement("Message");
            mess.appendChild(doc.createTextNode(Message));
            block.appendChild(mess);
            
            WriteXML(doc, NameOfFile);
            
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
    
     /*
 
    *Function : CreateXMLFile(String desUsr)
    *Description: Create xml file with root is desUsr. This file contain
                  all message from desUsr
    *Argument: String destination user
    *Return: Error
    Note:  

    */
    public int CountXMLElement(String NameOfFile){
            try {
                BuildPath(NameOfFile);
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(Path);
                
                NodeList list = doc.getElementsByTagName("block");
                System.out.println(list.getLength());
                return list.getLength();
            } catch (Exception e) {
                return 0;
            }
    }
    
    /*
 
    *Function : CreateXMLFile(String desUsr)
    *Description: Create xml file with root is desUsr. This file contain
                  all message from desUsr
    *Argument: String destination user
    *Return: Error
    Note:  

    */
    public Map<String, MessageStruct> GetList(String NameOfFile){
        try {
            datamap.clear();
            BuildPath(NameOfFile);
            
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            // root elements
            Document doc = docBuilder.parse(Path);
            Element rootElement = doc.getDocumentElement();
            
            NodeList nodeList = rootElement.getChildNodes();
            if (nodeList != null){
                int length = nodeList.getLength();
                for (int i = 0; i < length; i++){
                    if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                        Element el = (Element) nodeList.item(i);
                        if (el.getNodeName().contains("block")){
                            String id;
                            MessageStruct ms = new MessageStruct();
                            id = el.getAttribute("id");
                            ms.curUsr = el.getElementsByTagName("CurrentUser").item(0).getTextContent();
                            ms.desUsr = el.getElementsByTagName("DestinationUser").item(0).getTextContent();
                            ms.Message = el.getElementsByTagName("Message").item(0).getTextContent();
                            datamap.put(id, ms);
                        }
                    }
                }
            }
            return datamap;
        } catch (Exception e) {
            return null;
        }
    }
}