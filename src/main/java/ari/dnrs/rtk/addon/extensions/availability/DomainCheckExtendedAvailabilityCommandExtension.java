package ari.dnrs.rtk.addon.extensions.availability;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.bean.DomainCheckExtendedAvailabilityDetails;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;
import ari.dnrs.rtk.addon.utils.XMLUtil;

public class DomainCheckExtendedAvailabilityCommandExtension extends EPPXMLBase implements epp_Extension {
    private Map<String, DomainCheckExtendedAvailabilityDetails> domainExtAvailabilityStateMap = new HashMap<String,
            DomainCheckExtendedAvailabilityDetails>();

    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();
        final Element commandElement = extensionDoc.createElement("check");
        commandElement.setAttribute("xmlns", XMLNamespaces.AVAILABLE_NAMESPACE);

        extensionDoc.appendChild(commandElement);
        String availableExtensionXML = null;
        try {
            availableExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating available extension XML.\n"
                    + e.getMessage());
        }
        return availableExtensionXML;
    }

    @Override
    public void fromXML(String responseXml) throws epp_XMLException {
        try {
            xml_ = responseXml;
            Element infDataNode = getDocumentElement();
            NodeList nodeList = infDataNode.getElementsByTagNameNS(XMLNamespaces.AVAILABLE_NAMESPACE, "*");
            long infoNum;
            int count;
            for (count = 0, infoNum = 1; count < nodeList.getLength();) {
                if (nodeList.item(count).getNodeName().equals("cd")) {
                    count = processNode(nodeList, count + 1, infoNum);
                    infoNum++;
                } else {
                    count++;
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

    private int processNode(NodeList nodeList, int count, long infoNum) throws DatatypeConfigurationException {
        String name = null, state = null, reason = null, phase = null, variantPrimaryDomainName = null;
        GregorianCalendar date = null;
        while (count < nodeList.getLength() && !nodeList.item(count).getNodeName().equals("cd")) {
            Node node = nodeList.item(count);
            switch (AvailabilityTagNames.valueOf(node.getNodeName())) {
                case name:
                    name = node.getFirstChild().getNodeValue();
                    break;
                case state:
                    state = node.getAttributes().getNamedItem("s").getNodeValue();
                    break;
                case reason:
                    reason = node.getFirstChild().getNodeValue();
                    break;
                case phase:
                    phase = node.getFirstChild().getNodeValue();
                    break;
                case primaryDomainName:
                    variantPrimaryDomainName = node.getFirstChild().getNodeValue();
                    break;
                case date:
                    date = XMLUtil.fromXSDateTime(node.getFirstChild().getNodeValue());
            }
            count++;
        }
        DomainCheckExtendedAvailabilityDetails details = new DomainCheckExtendedAvailabilityDetails(state, reason,
                date, phase, variantPrimaryDomainName);
        domainExtAvailabilityStateMap.put(name, details);
        return count;
    }

    public DomainCheckExtendedAvailabilityDetails getStateForDomain(String domainActiveVariant) {
        return domainExtAvailabilityStateMap.get(domainActiveVariant);
    }

    public Map<String, DomainCheckExtendedAvailabilityDetails> getDomainExtAvailabilityStateMap() {
        return domainExtAvailabilityStateMap;
    }

    private enum AvailabilityTagNames {
        name, state, reason, phase, date, primaryDomainName
    }

}
