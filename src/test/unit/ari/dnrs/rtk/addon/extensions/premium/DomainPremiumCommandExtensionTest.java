package ari.dnrs.rtk.addon.extensions.premium;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainTransfer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DomainPremiumCommandExtensionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final static String DOMAIN_CREATE_ACK_NO_PRICE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<create>"
            + "<domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>premium.example</domain:name>"
            + "<domain:registrant>JTKCON</domain:registrant><domain:contact type=\"tech\">JTKCON2</domain:contact>"
            + "<domain:authInfo>"
            + "<domain:pw>2fooBAR</domain:pw>"
            + "</domain:authInfo>"
            + "</domain:create>"
            + "</create>"
            + "<extension>"
            + "<create xmlns=\"urn:ar:params:xml:ns:premium-1.0\">"
            + "<ack/>"
            + "</create>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private final static String DOMAIN_CREATE_ACK_CREATE_PRICE_ONLY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<create>"
            + "<domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>premium.example</domain:name>"
            + "<domain:registrant>JTKCON</domain:registrant><domain:contact type=\"tech\">JTKCON2</domain:contact>"
            + "<domain:authInfo>"
            + "<domain:pw>2fooBAR</domain:pw>"
            + "</domain:authInfo>"
            + "</domain:create>"
            + "</create>"
            + "<extension>"
            + "<create xmlns=\"urn:ar:params:xml:ns:premium-1.0\">"
            + "<ack>"
            + "<price>200.0</price>"
            + "</ack>"
            + "</create>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private final static String DOMAIN_CREATE_ACK_RENEW_PRICE_ONLY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<create>"
            + "<domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>premium.example</domain:name>"
            + "<domain:registrant>JTKCON</domain:registrant><domain:contact type=\"tech\">JTKCON2</domain:contact>"
            + "<domain:authInfo>"
            + "<domain:pw>2fooBAR</domain:pw>"
            + "</domain:authInfo>"
            + "</domain:create>"
            + "</create>"
            + "<extension>"
            + "<create xmlns=\"urn:ar:params:xml:ns:premium-1.0\">"
            + "<ack>"
            + "<renewalPrice>95.9</renewalPrice>"
            + "</ack>"
            + "</create>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private final static String DOMAIN_CREATE_ACK_BOTH_PRICE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<create>"
            + "<domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>premium.example</domain:name>"
            + "<domain:registrant>JTKCON</domain:registrant><domain:contact type=\"tech\">JTKCON2</domain:contact>"
            + "<domain:authInfo>"
            + "<domain:pw>2fooBAR</domain:pw>"
            + "</domain:authInfo>"
            + "</domain:create>"
            + "</create>"
            + "<extension>"
            + "<create xmlns=\"urn:ar:params:xml:ns:premium-1.0\">"
            + "<ack>"
            + "<price>200.0</price>"
            + "<renewalPrice>90.95</renewalPrice>"
            + "</ack>"
            + "</create>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";
    private static final String DOMAIN_CHECK_PREMIUM_EXTENSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3" +
            ".org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">" +
            "<command>" +
            "<check>" +
            "<domain:check xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" " +
            "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" +
            "<domain:name>domain1.zone</domain:name>" +
            "<domain:name>domain2.zone</domain:name>" +
            "</domain:check>" +
            "</check>" +
            "<extension>" +
            "<check xmlns=\"urn:ar:params:xml:ns:premium-1.0\"/>" +
            "</extension>" +
            "<clTRID>JTKUTEST.20070101.010101.0</clTRID>" +
            "</command>" +
            "</epp>";

    private final static String DOMAIN_TRANSFER_REQUEST_ACK_NO_PRICE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<transfer op=\"request\">"
            + "<domain:transfer xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>premium.example</domain:name>"
            + "<domain:authInfo>"
            + "<domain:pw>2fooBAR</domain:pw>"
            + "</domain:authInfo>"
            + "</domain:transfer>"
            + "</transfer>"
            + "<extension>"
            + "<transfer xmlns=\"urn:ar:params:xml:ns:premium-1.0\">"
            + "<ack/>"
            + "</transfer>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private final static String DOMAIN_TRANSFER_REQUEST_ACK_RENEW_PRICE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<transfer op=\"request\">"
            + "<domain:transfer xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>premium.example</domain:name>"
            + "<domain:authInfo>"
            + "<domain:pw>2fooBAR</domain:pw>"
            + "</domain:authInfo>"
            + "</domain:transfer>"
            + "</transfer>"
            + "<extension>"
            + "<transfer xmlns=\"urn:ar:params:xml:ns:premium-1.0\">"
            + "<ack>"
            + "<renewalPrice>90.95</renewalPrice>"
            + "</ack>"
            + "</transfer>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";


    private epp_Command commandData;

    private epp_DomainCreateReq domainCreateRequest;
    private  epp_DomainCheckReq epp_domainCheckReq;
    private epp_DomainTransferReq domainTransferReq;

    @Before
    public void setUp() {
        commandData = new epp_Command();
        commandData.setClientTrid("JTKUTEST.20070101.010101.0");

        domainCreateRequest = new epp_DomainCreateReq();
        domainCreateRequest.setName("premium.example");
        domainCreateRequest.setRegistrant("JTKCON");
        domainCreateRequest.setContacts(new epp_DomainContact[] {new epp_DomainContact(epp_DomainContactType.TECH,
                "JTKCON2")});
        domainCreateRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", "2fooBAR"));

        epp_domainCheckReq  = new epp_DomainCheckReq();
        epp_domainCheckReq.setNames(new String[]{"domain1.zone", "domain2.zone"});

        epp_TransferRequest transferRequest = new epp_TransferRequest();
        transferRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", "2fooBAR"));
        transferRequest.setOp(epp_TransferOpType.REQUEST);
        domainTransferReq = new epp_DomainTransferReq();
        domainTransferReq.setName("premium.example");
        domainTransferReq.setTrans(transferRequest);
    }

    @Test
    public void shouldCreateExtensionForDomainCheckCommand() throws epp_XMLException {
        epp_domainCheckReq.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("check");

        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainCheck eppDomainCheck = new EPPDomainCheck();
        eppDomainCheck.setRequestData(epp_domainCheckReq);

        assertEquals(eppDomainCheck.toXML(), DOMAIN_CHECK_PREMIUM_EXTENSION);
    }

    @Test
    public void shouldCreateExtensionForDomainCheckWithoutPriceEvenWhenPriceSpecified() throws epp_XMLException {
        epp_domainCheckReq.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("check", BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(200.00));

        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainCheck eppDomainCheck = new EPPDomainCheck();
        eppDomainCheck.setRequestData(epp_domainCheckReq);

        assertEquals(eppDomainCheck.toXML(), DOMAIN_CHECK_PREMIUM_EXTENSION);
    }

    @Test
    public void shouldCreateDomainCreateXmlWithPremiumAckAndNoPrices() throws Exception {
        domainCreateRequest.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("create", null, null);
        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainCreate eppDomainCreate = new EPPDomainCreate();
        eppDomainCreate.setRequestData(domainCreateRequest);

        assertEquals(eppDomainCreate.toXML(), DOMAIN_CREATE_ACK_NO_PRICE_XML);
    }

    @Test
    public void shouldCreateDomainCreateXmlWithPremiumAckAndCreatePrice() throws Exception {
        domainCreateRequest.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("create",
                BigDecimal.valueOf(200.00), null);
        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainCreate eppDomainCreate = new EPPDomainCreate();
        eppDomainCreate.setRequestData(domainCreateRequest);

        assertEquals(eppDomainCreate.toXML(), DOMAIN_CREATE_ACK_CREATE_PRICE_ONLY_XML);
    }

    @Test
    public void shouldCreateDomainCreateXmlWithPremiumAckAndRenewalPrice() throws Exception {
        domainCreateRequest.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("create",
                null, BigDecimal.valueOf(95.90));
        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainCreate eppDomainCreate = new EPPDomainCreate();
        eppDomainCreate.setRequestData(domainCreateRequest);

        assertEquals(eppDomainCreate.toXML(), DOMAIN_CREATE_ACK_RENEW_PRICE_ONLY_XML);
    }

    @Test
    public void shouldCreateDomainCreateXmlWithPremiumAckAndBothPrices() throws Exception {
        domainCreateRequest.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("create",
                BigDecimal.valueOf(200.00), BigDecimal.valueOf(90.95));
        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainCreate eppDomainCreate = new EPPDomainCreate();
        eppDomainCreate.setRequestData(domainCreateRequest);

        assertEquals(eppDomainCreate.toXML(), DOMAIN_CREATE_ACK_BOTH_PRICE_XML);
    }

    @Test
    public void shouldCreateDomainTransferRequestXmlWithPremiumAckAndNoRenewalPrice() throws Exception {
        domainTransferReq.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("transfer");
        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainTransfer eppDomainTransfer = new EPPDomainTransfer();
        eppDomainTransfer.setRequestData(domainTransferReq);

        assertEquals(eppDomainTransfer.toXML(), DOMAIN_TRANSFER_REQUEST_ACK_NO_PRICE_XML);
    }

    @Test
    public void shouldCreateDomainTransferRequestXmlWithPremiumAckIncludingRenewalPrices() throws Exception {
        domainTransferReq.setCmd(commandData);

        DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("transfer", null,
                BigDecimal.valueOf(90.95));
        commandData.setExtensions(new epp_Extension[]{extension});

        EPPDomainTransfer eppDomainTransfer = new EPPDomainTransfer();
        eppDomainTransfer.setRequestData(domainTransferReq);

        assertEquals(eppDomainTransfer.toXML(), DOMAIN_TRANSFER_REQUEST_ACK_RENEW_PRICE_XML);
    }

    @Test
    public void shouldFailWhenPassingInvalidCommandName() throws epp_XMLException {
        thrown.expect(epp_XMLException.class);

        try {
            DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension("invalid",
                    BigDecimal.valueOf(200.00), BigDecimal.valueOf(90.95));
        } catch (epp_XMLException exception) {
            assertThat(exception.getErrorMessage(), is("Invalid command name for creating premium extension XML. "
                    + "Valid names are: create ; check ; transfer ; "));
            throw exception;
        }
    }

    @Test
    public void shouldFailWhenPassingNullCommandName() throws epp_XMLException {
        thrown.expect(epp_XMLException.class);

        try {
            DomainPremiumCommandExtension extension = new DomainPremiumCommandExtension(null,
                    BigDecimal.valueOf(200.00), BigDecimal.valueOf(90.95));
        } catch (epp_XMLException exception) {
            assertThat(exception.getErrorMessage(), is("Invalid command name for creating premium extension XML. "
                    + "Valid names are: create ; check ; transfer ; "));
            throw exception;
        }
    }
}
