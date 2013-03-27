package ari.dnrs.rtk.addon.extensions.launch;

import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;
import ari.dnrs.rtk.addon.utils.XMLUtil;

public class DomainCreateApplicationResponse extends EPPXMLBase {
    private String applicationId;
    private String name;
    private GregorianCalendar createDate;

    public void fromXML(String responseXml) {
        try {
            xml_ = responseXml;
            Element creDataNode = getDocumentElement();
            NodeList nodeList = creDataNode.getElementsByTagNameNS(XMLNamespaces.LAUNCH_NAMESPACE, "*");

            for (int count = 0; count < nodeList.getLength(); count++) {
                Node node = nodeList.item(count);
                if (node.getNodeName().equals("id")) {
                    applicationId = node.getFirstChild().getNodeValue();
                } else if (node.getNodeName().equals("name")) {
                    name = node.getFirstChild().getNodeValue();
                } else if (node.getNodeName().equals("crDate")) {
                    createDate = XMLUtil.fromXSDateTime(node.getFirstChild().getNodeValue());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

    }

    public GregorianCalendar getCreateDate() {
        return createDate;
    }

    public String getName() {
        return name;
    }

    public String getApplicationId() {
        return applicationId;
    }
}
