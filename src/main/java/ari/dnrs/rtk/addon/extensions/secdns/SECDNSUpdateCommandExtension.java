package ari.dnrs.rtk.addon.extensions.secdns;

import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ari.dnrs.rtk.addon.bean.ChangeElementBean;
import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.bean.RemoveElementBean;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;
import ari.dnrs.rtk.addon.utils.XMLUtil;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Can be used to set the DNSSEC Domain extension properties for an EPP Domain Update command.
 *
 */
public class SECDNSUpdateCommandExtension extends EPPXMLBase implements epp_Extension {

    private static final long serialVersionUID = 5120692578959989834L;

    private DNSSecDataBean addData;
    private ChangeElementBean chgData;
    private RemoveElementBean remData;
    private boolean urgent;

    @Override
    public void fromXML(String xml) throws epp_XMLException {
        // There is no response extension for domain update, so this method does not need to be implemented
    }

    public final DNSSecDataBean getAddData() {
        return addData;
    }

    public final ChangeElementBean getChgData() {
        return chgData;
    }

    public final RemoveElementBean getRemData() {
        return remData;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public final void setAddData(DNSSecDataBean addData) {
        this.addData = addData;
    }

    public final void setChgData(ChangeElementBean chgData) {
        this.chgData = chgData;
    }

    public final void setRemData(RemoveElementBean remData) {
        this.remData = remData;
    }

    public void setUrgent(final boolean urgentArg) {
        this.urgent = urgentArg;
    }

    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();

        addToCommand(extensionDoc);

        String secDNSExtensionXML = null;
        try {
            secDNSExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating SECDNS extension XML.\n" + e.getMessage());
        }
        return secDNSExtensionXML;
    }

    private void addToCommand(final Document xmlDocument) {

        final Element updateElement = xmlDocument.createElement("update");
        updateElement.setAttribute("xmlns", XMLNamespaces.SEC_DNS_NAMESPACE);

        if (urgent) {
            updateElement.setAttribute("urgent", "true");
        }
        handleRemove(xmlDocument, updateElement);
        handleAdd(xmlDocument, updateElement);
        handleChange(xmlDocument, updateElement);

        xmlDocument.appendChild(updateElement);

    }

    private void handleAdd(final Document xmlDocument, final Element updateElement) {
        if (this.addData == null
                || ((this.addData.getDsDataList() == null || this.addData.getDsDataList().size() == 0) && (this.addData
                        .getKeyDataList() == null || this.addData.getKeyDataList().size() == 0))) {
            return;
        }

        final Element addElement = XMLUtil.appendChildElement(updateElement, "add");
        addData.createXMLElement(addElement);

    }

    private void handleChange(final Document xmlDocument, final Element updateElement) {
        if (chgData != null && chgData.getMaxSigLife() != null && chgData.getMaxSigLife().getMaxSigLife() > 0) {
            final Element changeElement = XMLUtil.appendChildElement(updateElement, "chg");
            chgData.createXMLElement(changeElement);
        }
    }

    private void handleRemove(final Document xmlDocument, final Element updateElement) {
        if (this.remData == null
                || ((remData.getDsDataList() == null || remData.getDsDataList().size() == 0)
                        && (remData.getKeyDataList() == null || remData.getKeyDataList().size() == 0) && !remData
                            .isRemoveAll())) {
            return;
        }

        final Element removeElement = XMLUtil.appendChildElement(updateElement, "rem");
        this.remData.createXMLElement(removeElement);
    }

}
