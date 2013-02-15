package ari.dnrs.rtk.addon.extensions.secdns;

import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;


/**
 * Can be used to set the DNSSEC Domain extension properties for an EPP Domain Create command.
 *
 */
public final class SECDNSCreateCommandExtension extends EPPXMLBase implements epp_Extension {

    private static final long serialVersionUID = 3450085965276921900L;

    private DNSSecDataBean createData;

    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();

        final Element createElement = extensionDoc.createElement("create");
        createElement.setAttribute("xmlns", XMLNamespaces.SEC_DNS_NAMESPACE);

        if (this.createData == null
                || ((this.createData.getDsDataList() == null || this.createData.getDsDataList().size() == 0) && (this.createData
                        .getKeyDataList() == null || this.createData.getKeyDataList().size() == 0))) {
            throw new epp_XMLException("No DS data or Key data was provided for the extension.");
        }

        createData.createXMLElement(createElement);

        extensionDoc.appendChild(createElement);

        String secDNSExtensionXML = null;
        try {
            secDNSExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating SECDNS extension XML.\n" + e.getMessage());
        }
        return secDNSExtensionXML;
    }

    public final DNSSecDataBean getCreateData() {
        return createData;
    }

    public final void setCreateData(DNSSecDataBean createData) {
        this.createData = createData;
    }

    @Override
    public void fromXML(String xml) throws epp_XMLException {
        // There is no response extension for domain create, so this method does not need to be implemented
    }

}
