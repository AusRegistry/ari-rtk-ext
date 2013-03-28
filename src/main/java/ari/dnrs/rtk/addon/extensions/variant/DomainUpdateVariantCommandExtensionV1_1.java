package ari.dnrs.rtk.addon.extensions.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.bean.IdnDomainVariant;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

public class DomainUpdateVariantCommandExtensionV1_1  extends EPPXMLBase implements epp_Extension {

    private final List<IdnDomainVariant> addVariants = new ArrayList<IdnDomainVariant>();
    private final List<IdnDomainVariant> remVariants = new ArrayList<IdnDomainVariant>();

    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();
        final Element commandElement = extensionDoc.createElement("update");
        commandElement.setAttribute("xmlns", XMLNamespaces.VARIANT_V1_1_NAMESPACE);

        processVariants(commandElement, extensionDoc, "rem", remVariants);
        processVariants(commandElement, extensionDoc, "add", addVariants);

        extensionDoc.appendChild(commandElement);
        String variantExtensionXML = null;
        try {
            variantExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating premium extension XML.\n" + e.getMessage());
        }
        return variantExtensionXML;
    }

    private void processVariants(Element commandElement, Document extensionDoc, String elementName,
                                 List<IdnDomainVariant> variants) {
        Element variantParentElement = extensionDoc.createElement(elementName);
        for (IdnDomainVariant idnDomainVariant : variants) {
            Element variantElement = extensionDoc.createElement("variant");
            variantElement.appendChild(extensionDoc.createTextNode(idnDomainVariant.getName()));
            variantParentElement.appendChild(variantElement);
        }
        if (variants.size() > 0) {
            commandElement.appendChild(variantParentElement);
        }
    }

    @Override
    public void fromXML(String s) throws epp_XMLException {

    }

    public void addVariant(IdnDomainVariant... idnDomainVariant) throws epp_XMLException {
        addVariantsToList(idnDomainVariant, addVariants);
    }

    public void removeVariant(IdnDomainVariant... idnDomainVariant) throws epp_XMLException {
        addVariantsToList(idnDomainVariant, remVariants);
    }

    private void addVariantsToList(IdnDomainVariant[] idnDomainVariant, List<IdnDomainVariant> variantsList) throws
            epp_XMLException {
        for (IdnDomainVariant domainVariant : idnDomainVariant) {
            if (domainVariant == null) {
                throw new epp_XMLException("The domain variant is a required parameter.");
            }
            if(domainVariant.getName() == null) {
                throw new epp_XMLException("The domain variant DNS form is a required parameter.");
            }
            variantsList.add(domainVariant);
        }
    }

}
