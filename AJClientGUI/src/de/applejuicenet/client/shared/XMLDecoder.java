package de.applejuicenet.client.shared;

import java.io.*;
import javax.xml.parsers.*;

import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public abstract class XMLDecoder {
  private Document document;
  private String filePath;

  protected XMLDecoder(String filePath) {
    reload(filePath);
  }

  protected void reload(String filePath) {
    DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(filePath);
      this.filePath = filePath;
    }
    catch (SAXException sxe) {
      Exception x = sxe;
      if (sxe.getException() != null) {
        x = sxe.getException();
      }
      x.printStackTrace();

    }
    catch (ParserConfigurationException pce) {
      pce.printStackTrace();

    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public String getFirstAttrbuteByTagName(String[] attributePath) {
    NodeList nodes = document.getChildNodes();
    Node rootNode = nodes.item(0);   //Element "root"
    nodes = rootNode.getChildNodes();
    for (int i=0; i<attributePath.length-1; i++){
      for (int x=0; x<nodes.getLength(); x++){
        String test = nodes.item(x).getNodeName();
        if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i])){
          if (i == attributePath.length-2){
            Element e = (Element) nodes.item(x);
            return e.getAttribute(attributePath[attributePath.length-1]);
          }
          else{
            nodes = nodes.item(x).getChildNodes();
            break;
          }
        }
      }
    }
    return "";  //Nicht gefunden
  }

  public void setAttributeByTagName(String[] attributePath, String newValue) {
    Element ele = document.getDocumentElement();
    NodeList nl = ele.getElementsByTagName(attributePath[0]);
    if (attributePath.length > 2) {
      for (int i = 1; i < attributePath.length - 1; i++) {
        nl = ele.getElementsByTagName(attributePath[i]);
      }
    }
    if (nl.getLength() != 0) {
      Element language = (Element) nl.item(0);
      try {
        language.setAttribute(attributePath[attributePath.length - 1], ZeichenErsetzer.korrigiereUmlaute(newValue, true));
        try {
          XMLSerializer xs = new XMLSerializer(new FileWriter(filePath),
                                               new OutputFormat(document,
              "UTF-8", true));
          xs.serialize(document);
        }
        catch (IOException ioE) {
          ioE.printStackTrace();
        }
      }
      catch (DOMException ex) {
        ex.printStackTrace();
      }
    }
  }

}
