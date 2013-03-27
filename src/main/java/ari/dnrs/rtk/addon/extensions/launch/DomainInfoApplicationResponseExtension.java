package ari.dnrs.rtk.addon.extensions.launch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;
import ari.dnrs.rtk.addon.utils.XMLUtil;

public class DomainInfoApplicationResponseExtension  extends EPPXMLBase {
    private String applicationId;
    private String phase;
    private List<String> statuses = new ArrayList<String>();

    public void fromXML(String infoResponseXml) {
        try {
            xml_ = infoResponseXml;
            Element infDataNode = getDocumentElement();
            NodeList nodeList = infDataNode.getElementsByTagNameNS(XMLNamespaces.LAUNCH_NAMESPACE, "*");

            for (int count = 0; count < nodeList.getLength(); count++) {
                Node node = nodeList.item(count);
                if (node.getNodeName().equals("launch:id")) {
                    applicationId = node.getFirstChild().getNodeValue();
                } else if (node.getNodeName().equals("launch:phase")) {
                    phase = node.getFirstChild().getNodeValue();
                } else if (node.getNodeName().equals("launch:status")) {
                    statuses.add(node.getAttributes().getNamedItem("s").getNodeValue());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    public boolean isInitialised() {
        return applicationId != null && phase != null && statuses != null;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getPhase() {
        return phase;
    }

    public List<String> getStatuses() {
        return statuses;
    }
}
