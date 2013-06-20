package ari.dnrs.rtk.addon.extensions.tmch;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;
import com.tucows.oxrs.epp02.rtk.xml.EPPXMLBase;
import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * <p>Supports the Domain Name Trademark Clearing House extension for the EPP Domain Create command</p>
 *
 * <p>Use this to supply the domain's encoded signed mark data or notice id and its related info as part of an EPP
 * Domain Create command compliant with RFC5730 and RFC5731.
 * The response expected from a server should be handled by a Domain Create Response.</p>
 *
 * @see org.openrtk.idl.epprtk.domain.epp_DomainCreateReq
 * @see <a href="http://ausregistry.github.io/doc/tmch-1.0/tmch-1.0.html">Domain Name Trademark Clearing House
 * Extension Mapping for the Extensible Provisioning Protocol (EPP)</a>
 */
public class DomainClaimsCreateCommandExtension extends EPPXMLBase implements epp_Extension {
    private String noticeId;
    private String notAfterDateTimeString;
    private String acceptedDateTimeString;
    private String encodedSignedMarkData;

    @Override
    public String toXML() throws epp_XMLException {
        Document extensionDoc = new DocumentImpl();
        Element commandElement = extensionDoc.createElement("create");
        commandElement.setAttribute("xmlns", XMLNamespaces.TMCH_NAMESPACE);

        if (noticeId != null) {
            Element noticeIdElement = extensionDoc.createElement("noticeID");
            noticeIdElement.appendChild(extensionDoc.createTextNode(noticeId));
            commandElement.appendChild(noticeIdElement);
        }

        if (notAfterDateTimeString != null) {
            Element notAfterElement = extensionDoc.createElement("notAfter");
            notAfterElement.appendChild(extensionDoc.createTextNode(notAfterDateTimeString));
            commandElement.appendChild(notAfterElement);
        }

        if (acceptedDateTimeString != null) {
            Element acceptedElement = extensionDoc.createElement("accepted");
            acceptedElement.appendChild(extensionDoc.createTextNode(acceptedDateTimeString));
            commandElement.appendChild(acceptedElement);
        }

        if (encodedSignedMarkData != null) {
            Element noticeIdElement = extensionDoc.createElement("smd");
            noticeIdElement.appendChild(extensionDoc.createTextNode(encodedSignedMarkData));
            commandElement.appendChild(noticeIdElement);
        }

        extensionDoc.appendChild(commandElement);

        String claimsCreateExtensionXML = null;
        try {
            claimsCreateExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating tmch extension XML.\n"
                    + e.getMessage());
        }
        return claimsCreateExtensionXML;
    }

    /**
     * This method does not have any implementation because create does not have any response extension
     * @param responseXml
     */
    @Override
    public void fromXML(String responseXml) {
    }

    /**
     *
     * @param noticeId the notice ID
     */
    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    /**
     * Use this setter to pass the notAfter datetime as a String
     * @param notAfterDateTimeString in the format "yyyy-MM-dd'T'HH:mm:ss.0'Z'"
     */
    public void setNotAfterDateTimeString(String notAfterDateTimeString) {
        this.notAfterDateTimeString = notAfterDateTimeString;
    }

    /**
     * Use this setter to pass the accepted datetime as a String
     * @param acceptedDateTimeString in the format "yyyy-MM-dd'T'HH:mm:ss.0'Z'"
     */
    public void setAcceptedDateTimeString(String acceptedDateTimeString) {
        this.acceptedDateTimeString = acceptedDateTimeString;
    }

    /**
     *
     * @param encodedSignedMarkData the encoded signed mark data
     */
    public void setEncodedSignedMarkData(String encodedSignedMarkData) {
        this.encodedSignedMarkData = encodedSignedMarkData;
    }
}
