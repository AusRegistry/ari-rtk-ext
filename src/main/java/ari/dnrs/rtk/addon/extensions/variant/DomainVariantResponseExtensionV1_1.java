package ari.dnrs.rtk.addon.extensions.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.bean.IdnDomainVariant;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

/**
 * Extension of the domain mapping of the EPP create/info response, as defined
 * in RFC5730 and RFC5731, to domain name variants, the specification of which
 * are in the XML schema definition urn:X-ar:params:xml:ns:variant-1.1.
 *
 * Instances of this class provide an interface to access create and info data
 * for a domain as provided in an EPP domain create response. Such a service
 * element is sent by a EPP server in response to a valid domain create/info
 * command as implemented by the domain create and domain info, optionally with
 * the variant extension.
 *
 * @see ari.dnrs.rtk.addon.extensions.variant.DomainUpdateVariantCommandExtensionV1_1
 */
public class DomainVariantResponseExtensionV1_1 extends EPPXMLBase {
    private List<IdnDomainVariant> variants;
    private final CommandName commandName;

    public DomainVariantResponseExtensionV1_1(String commandName) {
        this.commandName = CommandName.valueOf(commandName);
        variants = new ArrayList<IdnDomainVariant>();
    }

    public void fromXml(String responseXml) {
        try {
            xml_ = responseXml;
            Element dataNode = getDocumentElement();

            if (dataNode.getElementsByTagNameNS(XMLNamespaces.VARIANT_V1_1_NAMESPACE,
                    commandName.getTagName()).getLength() == 0) {
                return;
            }
            NodeList nodeList = dataNode.getElementsByTagNameNS(XMLNamespaces.VARIANT_V1_1_NAMESPACE, "*");

            for (int count = 0; count < nodeList.getLength(); count++) {
                if (nodeList.item(count).getNodeName().equals("variant")) {
                    variants.add(new IdnDomainVariant(nodeList.item(count).getFirstChild().getNodeValue()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    public boolean isInitialised() {
        return variants.size() > 0;
    }

    public List<IdnDomainVariant> getVariants() {
        return variants;
    }

    private enum CommandName {
        create("creData"), info("infData");

        private String tagName;

        CommandName(String tagName) {
            this.tagName = tagName;
        }

        String getTagName() {
            return tagName;
        }
    }
}
