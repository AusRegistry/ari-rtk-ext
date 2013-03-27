package ari.dnrs.rtk.addon.extensions.premium;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

public class DomainPremiumCommandExtension extends EPPXMLBase implements epp_Extension {

    private final BigDecimal price;
    private final BigDecimal renewPrice;
    private CommandName command;

    public DomainPremiumCommandExtension(final String command, BigDecimal price, BigDecimal renewPrice) throws
            epp_XMLException {
        this.price = price;
        this.renewPrice = renewPrice;
        assertValidCommandName(command);
    }

    public DomainPremiumCommandExtension(String command) throws epp_XMLException {
        this(command, null, null);
    }

    private void assertValidCommandName(String command) throws epp_XMLException {
        try {
            this.command = CommandName.valueOf(command);
        } catch (IllegalArgumentException illegalArguementException) {
            throw new epp_XMLException("Invalid command name for creating premium extension XML. Valid names are: "
                    + CommandName.getCommandNames());
        } catch (NullPointerException nullPointerException) {
            throw new epp_XMLException("Invalid command name for creating premium extension XML. Valid names are: "
                    + CommandName.getCommandNames());
        }
    }

    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();
        final Element commandElement = extensionDoc.createElement(command.name());
        commandElement.setAttribute("xmlns", XMLNamespaces.PREMIUM_NAMESPACE);

        processInnerElements(commandElement, extensionDoc);

        extensionDoc.appendChild(commandElement);
        String variantExtensionXML = null;
        try {
            variantExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating premium extension XML.\n" + e.getMessage());
        }
        return variantExtensionXML;
    }

    private void processInnerElements(Element commandElement, Document extensionDoc) {
        switch (command) {
            case check:
                break;
            case create:
            case transfer:
                final Element ack = extensionDoc.createElement("ack");
                processPrice(ack, "price", price, extensionDoc);
                processPrice(ack, "renewalPrice", renewPrice, extensionDoc);
                commandElement.appendChild(ack);
                break;
            default:
        }
    }

    private void processPrice(Element element, String priceElementName, BigDecimal priceValue, Document extensionDoc) {
        if (priceValue == null) {
            return;
        }
        Element priceElement = extensionDoc.createElement(priceElementName);
        priceElement.appendChild(extensionDoc.createTextNode(priceValue.toPlainString()));
        element.appendChild(priceElement);
    }

    @Override
    public void fromXML(final String s) throws epp_XMLException {
        /* There is no response extension for Premium commands, so this method does not need to be implemented */
    }

    private enum CommandName {
        create, check, transfer;

        private static String getCommandNames() {
            StringBuilder builder = new StringBuilder();
            for (CommandName name : CommandName.values()) {
                builder.append(name.name());
                builder.append(" ; ");
            }
            return builder.toString();
        }
    }
}
