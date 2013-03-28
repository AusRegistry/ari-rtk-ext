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
            System.out.println(xml_);
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
