package ari.dnrs.rtk.addon.extensions.price;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

/**
 * Supports the price extension for domain check, domain create and domain transfer commands
 *
 * @see org.openrtk.idl.epprtk.domain.epp_DomainCheckReq
 * @see org.openrtk.idl.epprtk.domain.epp_DomainCreateReq
 * @see org.openrtk.idl.epprtk.domain.epp_DomainTransferReq
 */
public class DomainPriceCommandExtension extends EPPXMLBase implements epp_Extension {

    private final Integer period;
    private final BigDecimal price;
    private final BigDecimal renewPrice;
    private CommandName command;

    /**
     *
     * @param command The command name. Possible values are check, create and transfer
     * @param period The period for price check
     * @param price The create price
     * @param renewPrice The renew price
     * @throws epp_XMLException
     */
    public DomainPriceCommandExtension(final String command, final Integer period, final BigDecimal price,
                                       final BigDecimal renewPrice) throws epp_XMLException {
        this.period = period;
        this.price = price;
        this.renewPrice = renewPrice;
        assertValidCommandName(command);
    }

    /**
     *
     * @param command The command name. Possible values are check, create and transfer
     * @param price The create price
     * @param renewPrice The renew price
     * @throws epp_XMLException
     */
    public DomainPriceCommandExtension(final String command, final BigDecimal price, final BigDecimal renewPrice)
            throws epp_XMLException {

        this(command, null, price, renewPrice);
    }

    /**
     *
     * @param command The command name. Possible values are check, create and transfer
     * @param period The period for price check
     * @throws epp_XMLException
     */
    public DomainPriceCommandExtension(String command, int period) throws epp_XMLException {
        this(command, period, null, null);
    }


    /**
     *
     * @param command The command name. Possible values are check, create and transfer
     * @throws epp_XMLException
     */
    public DomainPriceCommandExtension(String command) throws epp_XMLException {
        this(command, null, null, null);
    }

    private void assertValidCommandName(String command) throws epp_XMLException {
        try {
            this.command = CommandName.valueOf(command);
        } catch (IllegalArgumentException illegalArguementException) {
            throw new epp_XMLException("Invalid command name for creating price extension XML. Valid names are: "
                    + CommandName.getCommandNames());
        } catch (NullPointerException nullPointerException) {
            throw new epp_XMLException("Invalid command name for creating price extension XML. Valid names are: "
                    + CommandName.getCommandNames());
        }
    }

    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();
        final Element commandElement = extensionDoc.createElement(command.name());
        commandElement.setAttribute("xmlns", XMLNamespaces.PRICE_NAMESPACE);

        processInnerElements(commandElement, extensionDoc);

        extensionDoc.appendChild(commandElement);
        String variantExtensionXML = null;
        try {
            variantExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating price extension XML.\n" + e.getMessage());
        }
        return variantExtensionXML;
    }

    private void processInnerElements(Element commandElement, Document extensionDoc) {
        switch (command) {
            case check:
                if (period != null) {
                    final Element periodElement = extensionDoc.createElement("period");
                    periodElement.setAttribute("unit", "y");
                    periodElement.appendChild(extensionDoc.createTextNode(period.toString()));
                    commandElement.appendChild(periodElement);
                }
                break;
            case create:
            case renew:
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

    /**
     * There is no response extension for Price commands, so this method is not implemented
     *
     * @param responseXml
     * @throws epp_XMLException
     */
    @Override
    public void fromXML(final String responseXml) throws epp_XMLException {
    }

    private enum CommandName {
        create, check, transfer, renew;

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
