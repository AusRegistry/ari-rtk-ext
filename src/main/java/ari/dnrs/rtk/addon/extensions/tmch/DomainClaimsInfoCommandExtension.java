package ari.dnrs.rtk.addon.extensions.tmch;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;
import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * <p>Supports Domain Name Trademark Clearing House extension Extension for the EPP Domain Info request and response.</p>
 *
 * <p>Use this to access domain Trademark Clearing House data for a domain as provided in an EPP Domain Info response.
 * Such a service element is sent by a compliant EPP server in response to a valid Domain Info command
 * with the Trademark Clearing House extension.</p>
 *
 * @see org.openrtk.idl.epprtk.domain.epp_DomainInfoReq
 * @see <a href="http://ausregistry.github.io/doc/tmch-1.0/tmch-1.0.html">Domain Name Trademark Clearing House
 * Extension Mapping for the Extensible Provisioning Protocol (EPP)</a>
 */
public class DomainClaimsInfoCommandExtension extends EPPXMLBase implements epp_Extension {
    private String encodedSignedMarkData;

    @Override
    public String toXML() throws epp_XMLException {
        DocumentImpl extensionDocument = new DocumentImpl();
        Element extensionElement = extensionDocument.createElement("info");
        extensionElement.setAttribute("xmlns", XMLNamespaces.TMCH_NAMESPACE);

        extensionDocument.appendChild(extensionElement);
        String claimsInfoExtensionXml = null;
        try {
            claimsInfoExtensionXml = createXMLSnippetFromDoc(extensionDocument);
        } catch (IOException e) {
            throw new epp_XMLException("IOException occurred while creating tmch info extension XML.\n"
                    + e.getMessage());
        }

        return claimsInfoExtensionXml;
    }

    @Override
    public void fromXML(String responseXml) throws epp_XMLException {

        if (responseXml == null || responseXml.length() == 0) {
            return;
        }

        xml_ = responseXml;

        try {
            Element infDataNode = getDocumentElement();
            NodeList extensionNodes = infDataNode.getElementsByTagNameNS(XMLNamespaces.TMCH_NAMESPACE, "*");

            if (extensionNodes.getLength() == 0) {
                return;
            }

            for (int count = 0; count < extensionNodes.getLength(); count++) {
                Node childNode = extensionNodes.item(count);
                if (childNode.getNodeName().equals("smd")) {
                    encodedSignedMarkData = childNode.getFirstChild().getNodeValue();
                }
            }
        } catch (IOException e) {
            throw new epp_XMLException("unable to parse xml [" + e.getClass().getName() + "] [" + e.getMessage() + "]");
        } catch (SAXException e) {
            throw new epp_XMLException("unable to parse xml [" + e.getClass().getName() + "] [" + e.getMessage() + "]");
        }
    }

    public String getEncodedSignedMarkData() {
        return encodedSignedMarkData;
    }
}
