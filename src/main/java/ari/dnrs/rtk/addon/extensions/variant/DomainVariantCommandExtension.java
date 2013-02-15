package ari.dnrs.rtk.addon.extensions.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ari.dnrs.rtk.addon.bean.DomainVariantBean;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Use this Class to set Domain variant extension properties to an EPP Domain Update
 * command (toXML) and to retrieve Domain variant extension properties from a Domain Create response (fromXML) 
 * and a Domain Info responses (fromXML).
 *
 */
public final class DomainVariantCommandExtension extends EPPXMLBase implements epp_Extension {

    private static final long serialVersionUID = -9027549077606975091L;

    private List<DomainVariantBean> updateVariantsToAdd;
    private List<DomainVariantBean> updateVariantsToRemove;
    private List<DomainVariantBean> responseVariants;

    @Override
    public String toXML() throws epp_XMLException {
        final boolean isVariantsToAddProvided = (updateVariantsToAdd != null && updateVariantsToAdd.size() > 0);
        final boolean isVariantsToRemoveProvided = (updateVariantsToRemove != null && updateVariantsToRemove.size() > 0);

        if (!isVariantsToAddProvided && !isVariantsToRemoveProvided) {
            throw new epp_XMLException("Should provide at least one variant to add or remove to construct " //
                    + "variant extension XML");
        }

        final Document extensionDoc = new DocumentImpl();
        final Element extensionElement = extensionDoc.createElement("update");
        extensionElement.setAttribute("xmlns", XMLNamespaces.VARIANT_NAMESPACE);

        if (isVariantsToAddProvided) {
            appendVariantList(updateVariantsToAdd, extensionDoc, extensionElement, "add");
        }

        if (isVariantsToRemoveProvided) {
            appendVariantList(updateVariantsToRemove, extensionDoc, extensionElement, "rem");
        }

        extensionDoc.appendChild(extensionElement);
        String variantExtensionXML = null;
        try {
            variantExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating variant extension XML.\n" + e.getMessage());
        }
        return variantExtensionXML;
    }

    @Override
    public void fromXML(final String xml) throws epp_XMLException {

        if (xml == null || xml.length() == 0) {
            return;
        }

        try {
            xml_ = xml;

            final Element variantNode = getDocumentElement();
            final NodeList extensionNodes = variantNode.getElementsByTagNameNS(XMLNamespaces.VARIANT_NAMESPACE, "*");

            if (extensionNodes.getLength() == 0) {
                return;
            }

            for (int count = 0; count < extensionNodes.getLength(); count++) {
                final Node childNode = extensionNodes.item(count);
                if (childNode.getNodeName().equals("variant")) {
                    final String dnsForm = childNode.getFirstChild().getNodeValue();
                    final NamedNodeMap attributes = childNode.getAttributes();
                    final String userForm = attributes.getNamedItem("userForm").getNodeValue();
                    addVariantToInfoVariantList(dnsForm, userForm);
                }
            }

        } catch (final SAXException e) {
            throw new epp_XMLException("unable to parse xml [" + e.getClass().getName() + "] [" + e.getMessage()
                    + "]");
        } catch (final IOException e) {
            throw new epp_XMLException("unable to parse xml [" + e.getClass().getName() + "] [" + e.getMessage()
                    + "]");
        }
    }

    /**
     * Add a variant to the list of variants to be added in a Domain Update command. If this is the
     * first variant added then the list is instantiated before the addition.
     *
     * @param dnsForm the DNS form of the variant to be added
     * @param userForm the user form of the variant to added
     */
    public void addToVariantsToAddList(final String dnsForm, final String userForm) {
        if (updateVariantsToAdd == null) {
            updateVariantsToAdd = new ArrayList<DomainVariantBean>();
        }
        updateVariantsToAdd.add(new DomainVariantBean(dnsForm, userForm));
    }

    /**
     * Add a variant to the list of variants to be removed in a Domain Update command. If this is the
     * first variant added then the list is instantiated before the addition.
     *
     * @param dnsForm the DNS form of the variant to be removed
     * @param userForm the user form of the variant to removed
     */
    public void addToVariantsToRemoveList(final String dnsForm, final String userForm) {
        if (updateVariantsToRemove == null) {
            updateVariantsToRemove = new ArrayList<DomainVariantBean>();
        }
        updateVariantsToRemove.add(new DomainVariantBean(dnsForm, userForm));
    }

    /**
     * Add a variant to the list of variants that are returned by a successful Domain Create or Info command.
     *
     * @param dnsForm the DNS form of the variant returned
     * @param userForm the user form of the variant returned
     */
    public void addVariantToInfoVariantList(final String dnsForm, final String userForm) {
        if (responseVariants == null) {
            responseVariants = new ArrayList<DomainVariantBean>();
        }
        responseVariants.add(new DomainVariantBean(dnsForm, userForm));
    }

    /**
     * Gets the list of variants to be added in a domain update command.
     *
     * @return the list of domain variant beans
     */
    public List<DomainVariantBean> getUpdateVariantsToAddList() {
        return updateVariantsToAdd;
    }

    /**
     * Gets the list of variants to be removed in a domain update command.
     *
     * @return the list of domain variant beans
     */
    public List<DomainVariantBean> getUpdateVariantsToRemoveList() {
        return updateVariantsToRemove;
    }

    /**
     * Gets the variant list returned by the domain info and domain create commands.
     *
     * @return the list of domain variant beans
     */
    public List<DomainVariantBean> getInfoVariantList() {
        return responseVariants;
    }
    
    /**
     * Append list of variants inside an element with the given tag name to the extension element
     *
     * @param variantList the list of variant beans provided as input
     * @param extensionDoc the XML document representing the EPP extensions
     * @param extensionElement the extension element created for the variant extension
     * @param tagName the 'add' or 'rem' tag name denoting which addRemType the variant list belongs
     */
    private void appendVariantList(final List<DomainVariantBean> variantList, final Document extensionDoc,
            final Element extensionElement, final String tagName) {
        // Create new variant list element tag
        final Element variantListElement = extensionDoc.createElement(tagName);

        // Create and append variant tags for each variant to variant list
        for (final DomainVariantBean variant : variantList) {
            final Element variantElement = extensionDoc.createElement("variant");
            variantElement.setAttribute("userForm", variant.getUserForm());
            variantElement.appendChild(extensionDoc.createTextNode(variant.getName()));

            variantListElement.appendChild(variantElement);
        }

        // Append the variant list element to the extension element of the XML
        extensionElement.appendChild(variantListElement);
    }
}
