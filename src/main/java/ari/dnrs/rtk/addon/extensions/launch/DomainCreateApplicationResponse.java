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
                switch (TagNames.valueOf(node.getNodeName())) {
                    case id:
                        applicationId = node.getFirstChild().getNodeValue();
                        break;
                    case name:
                        name = node.getFirstChild().getNodeValue();
                        break;
                    case crDate:
                        createDate = XMLUtil.fromXSDateTime(node.getFirstChild().getNodeValue());
                        break;
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

    private enum TagNames {
        id, name, crDate, creData
    }
}
