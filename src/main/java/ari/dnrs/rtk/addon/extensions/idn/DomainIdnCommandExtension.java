package ari.dnrs.rtk.addon.extensions.idn;

import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Sets the IDN Domain extension properties for an EPP Domain Create command or
 * retrieve the IDN Domain extension properties from an EPP Domain Info command.
 */
public final class DomainIdnCommandExtension extends EPPXMLBase implements epp_Extension {

    private static final long serialVersionUID = -8945007354471832288L;
    private String languageTag;

    public void setLanguageTag(final String languageTag) {
        if (languageTag == null || languageTag.isEmpty()) {
            throw new IllegalArgumentException("Language must not be null or empty");
        }
        this.languageTag = languageTag;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    @Override
    public String toXML() throws epp_XMLException {
        Document extensionDoc = new DocumentImpl();

        Element commandElement = extensionDoc.createElement("create");
        commandElement.setAttribute("xmlns", XMLNamespaces.IDN_NAMESPACE);

        Element languageTagElement = extensionDoc.createElement("languageTag");
        languageTagElement.appendChild(extensionDoc.createTextNode(languageTag));
        commandElement.appendChild(languageTagElement);

        extensionDoc.appendChild(commandElement);

        String idnExtensionXML = null;
        try {
            idnExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating idn extension XML.\n" + e.getMessage());
        }
        return idnExtensionXML;
    }

    @Override
    public void fromXML(String xml) throws epp_XMLException {

        if (xml == null || xml.length() == 0) {
            return;
        }

        try {
            xml_ = xml;

            Element idnNode = getDocumentElement();
            NodeList extensionNodes = idnNode.getElementsByTagNameNS(XMLNamespaces.IDN_NAMESPACE, "*");

            if (extensionNodes.getLength() == 0) {
                return;
            }

            for (int count = 0; count < extensionNodes.getLength(); count++) {
                Node childNode = extensionNodes.item(count);
                if (childNode.getNodeName().equals("languageTag")) {
                    languageTag = childNode.getFirstChild().getNodeValue();
                }
            }
        } catch (DOMException xcp) {
            throw new epp_XMLException("unable to parse xml [" + xcp.getClass().getName() + "] [" + xcp.getMessage()
                    + "]");
        } catch (SAXException xcp) {
            throw new epp_XMLException("unable to parse xml [" + xcp.getClass().getName() + "] [" + xcp.getMessage()
                    + "]");
        } catch (IOException xcp) {
            throw new epp_XMLException("unable to parse xml [" + xcp.getClass().getName() + "] [" + xcp.getMessage()
                    + "]");
        }
    }
}
