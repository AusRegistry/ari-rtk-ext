package ari.dnrs.rtk.addon.extensions.app;

import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

/**
 * Supports the application extension for domain create, domain delete, domain info and
 * domain update commands.
 *
 * @see org.openrtk.idl.epprtk.domain.epp_DomainCreateReq
 * @see org.openrtk.idl.epprtk.domain.epp_DomainDeleteReq
 * @see org.openrtk.idl.epprtk.domain.epp_DomainInfoReq
 * @see org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq
 */
public class DomainApplicationCommandExtension extends EPPXMLBase implements epp_Extension {
    private final CommandName command;
    private String phaseType;
    private String applicationId;

    /**
     *
     * @param command the name of the command for which application extensions are being created. Possible
     *                values are create, delete, update and info
     */
    public DomainApplicationCommandExtension(String command) {
        this.command = CommandName.valueOf(command);
    }

    /**
     *
     * @return The extension xml
     * @throws epp_XMLException
     */
    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();
        final Element commandElement = extensionDoc.createElement(command.name());
        commandElement.setAttribute("xmlns", XMLNamespaces.APPLICATION_NAMESPACE);

        processInnerElements(commandElement, extensionDoc);

        extensionDoc.appendChild(commandElement);
        String applicationExtensionXML = null;
        try {
            applicationExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating application extension XML.\n"
                    + e.getMessage());
        }
        return applicationExtensionXML;
    }

    private void processInnerElements(Element commandElement, Document extensionDoc) {
        switch (command) {
            case create:
                if (phaseType != null) {
                    Element phase = extensionDoc.createElement("phase");
                    phase.appendChild(extensionDoc.createTextNode(phaseType));
                    commandElement.appendChild(phase);
                    break;
                }
            case delete:
            case update:
            case info:
                Element applicationIdElement = extensionDoc.createElement("id");
                applicationIdElement.appendChild(extensionDoc.createTextNode(applicationId));
                commandElement.appendChild(applicationIdElement);
                break;
        }

    }

    /**
     * This method does not have any implementation because only create and info have extension in
     * response and are handled separately
     *
     * @param responseXml
     * @throws epp_XMLException
     * @see DomainInfoApplicationResponseExtension
     * @see DomainCreateApplicationResponse
     */
    @Override
    public void fromXML(String responseXml) throws epp_XMLException {
    }

    public void setPhaseType(String phaseType) {
        this.phaseType = phaseType;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    private enum CommandName {
        create, delete, update, info
    }
}
