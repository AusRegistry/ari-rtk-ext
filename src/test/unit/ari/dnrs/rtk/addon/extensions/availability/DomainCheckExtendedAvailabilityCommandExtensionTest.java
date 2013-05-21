package ari.dnrs.rtk.addon.extensions.availability;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckReq;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;

import ari.dnrs.rtk.addon.bean.DomainCheckExtendedAvailabilityDetails;
import ari.dnrs.rtk.addon.extensions.tmch.DomainClaimsCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DomainCheckExtendedAvailabilityCommandExtensionTest {

    private static final String EXPECTED_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<check>"
            + "<domain:check xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>domain.name</domain:name>"
            + "</domain:check>"
            + "</check>"
            + "<extension>"
            + "<check xmlns=\"urn:ar:params:xml:ns:exAvail-1.0\"/>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private static final String AVAILABLE_DOMAIN = "domain-avail.tld";
    private static final String PENDING_CREATE_DOMAIN = "domain-pending.tld";
    private static final String UNAVAILABLE_DOMAIN = "domain-inuse.tld";
    private static final String INVALID_DOMAIN = "domain-invalid.tld";
    private static final String RESERVED_DOMAIN = "domain-reserved.tld";
    private static final String DOMAIN_APPLICATION = "domain-application.tld";
    private static final String DOMAIN_ACTIVE_VARIANT = "domain-variant.tld";
    private static final String DOMAIN_WITHHELD_VARIANT = "domain-VaRiAnT.tld";
    private static final String DOMAIN_BLOCKED_VARIANT = "DOMAIN-variant.tld";

    private static final String RESPONSE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">"
            + "  <response>"
            + "    <result code=\"1000\">"
            + "      <msg lang=\"en\">Command completed successfully</msg>"
            + "    </result>"
            + "    <extension>"
            + "      <chkData xmlns=\"urn:ar:params:xml:ns:exAvail-1.0\">"
            + "        <cd>"
            + "          <name>" + AVAILABLE_DOMAIN + "</name>"
            + "          <state s=\"available\">"
            + "            <date>2010-04-23T00:00:00Z</date>"
            + "          </state>"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + PENDING_CREATE_DOMAIN + "</name>"
            + "          <state s=\"pendingCreate\" />"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + UNAVAILABLE_DOMAIN + "</name>"
            + "          <state s=\"unavailable\">"
            + "            <reason lang=\"en\">In use</reason>"
            + "          </state>"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + INVALID_DOMAIN + "</name>"
            + "          <state s=\"invalid\">"
            + "            <reason lang=\"en\">invalid char '!'</reason>"
            + "          </state>"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + RESERVED_DOMAIN + "</name>"
            + "          <state s=\"reserved\">"
            + "            <reason lang=\"en\">ICANN Reserved</reason>"
            + "          </state>"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + DOMAIN_APPLICATION + "</name>"
            + "          <state s=\"application\">"
            + "            <phase>sunrise</phase>"
            + "            <date>2010-04-25T00:00:00Z</date>"
            + "          </state>"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + DOMAIN_ACTIVE_VARIANT + "</name>"
            + "          <state s=\"activatedVariant\">"
            + "            <primaryDomainName>domain-VARIANT-one.tld</primaryDomainName>"
            + "          </state>"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + DOMAIN_WITHHELD_VARIANT + "</name>"
            + "          <state s=\"withheldVariant\">"
            + "            <primaryDomainName>domain-VARIANT-two.tld</primaryDomainName>"
            + "          </state>"
            + "        </cd>"
            + "        <cd>"
            + "          <name>" + DOMAIN_BLOCKED_VARIANT + "</name>"
            + "          <state s=\"blockedVariant\">"
            + "            <primaryDomainName>domain-VARIANT-three.tld</primaryDomainName>"
            + "          </state>"
            + "        </cd>"
            + "      </chkData>"
            + "    </extension>"
            + "    <trID>"
            + "      <clTRID>ABC-12345</clTRID>"
            + "      <svTRID>57f39ac6-abd2-4fea-9a80-e791d1af86f7</svTRID>"
            + "    </trID>"
            + "  </response>"
            + "</epp>";

    private epp_Command commandData;

    private epp_DomainCheckReq epp_domainCheckReq;

    private DomainCheckExtendedAvailabilityCommandExtension domainCheckExtendedAvailabilityCommandExtension;

    @Before
    public void setUp() {
        commandData = new epp_Command();
        commandData.setClientTrid("JTKUTEST.20070101.010101.0");

        epp_domainCheckReq = new epp_DomainCheckReq();
        epp_domainCheckReq.setNames(new String[]{"domain.name"});
        domainCheckExtendedAvailabilityCommandExtension = new DomainCheckExtendedAvailabilityCommandExtension();
    }

    @Test
    public void shouldCreateDomainCheckWithAvailabilityExtension() throws epp_XMLException {
        epp_domainCheckReq.setCmd(commandData);

        commandData.setExtensions(new epp_Extension[]{domainCheckExtendedAvailabilityCommandExtension});

        EPPDomainCheck eppDomainCheck = new EPPDomainCheck();
        eppDomainCheck.setRequestData(epp_domainCheckReq);

        assertEquals(eppDomainCheck.toXML(), EXPECTED_REQUEST);
    }

    @Test
    public void shouldParseAllTheDomainsInTheExtendedAvailabilityCheckResponseXml() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        assertEquals(domainCheckExtendedAvailabilityCommandExtension.getDomainExtAvailabilityStateMap().size(), 9);
    }

    @Test
    public void shouldReturnCorrectStateForDomainThatIsAvailable() throws epp_XMLException,
            DatatypeConfigurationException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(AVAILABLE_DOMAIN);
        assertEquals(domainState.getState(), "available");
        assertEquals(domainState.getDate(), XMLUtil.fromXSDateTime("2010-04-23T00:00:00Z"));
        assertNull(domainState.getReason());
        assertNull(domainState.getPhase());
        assertNull(domainState.getVariantPrimaryDomainName());
    }

    @Test
    public void shouldReturnCorrectStateDetailsForDomainThatIsPendingCreate() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(PENDING_CREATE_DOMAIN);
        assertEquals(domainState.getState(), "pendingCreate");
        assertNull(domainState.getDate());
        assertNull(domainState.getReason());
        assertNull(domainState.getPhase());
        assertNull(domainState.getVariantPrimaryDomainName());
    }

    @Test
    public void shouldReturnCorrectStateDetailsForDomainThatIsUnavailable() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(UNAVAILABLE_DOMAIN);
        assertEquals(domainState.getState(), "unavailable");
        assertNull(domainState.getDate());
        assertEquals(domainState.getReason(), "In use");
        assertNull(domainState.getPhase());
        assertNull(domainState.getVariantPrimaryDomainName());
    }


    @Test
    public void shouldReturnCorrectStateDetailsForDomainThatIsInvalid() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(INVALID_DOMAIN);
        assertEquals(domainState.getState(), "invalid");
        assertNull(domainState.getDate());
        assertEquals(domainState.getReason(), "invalid char '!'");
        assertNull(domainState.getPhase());
        assertNull(domainState.getVariantPrimaryDomainName());
    }

    @Test
    public void shouldReturnCorrectStateDetailsForDomainThatIsReserved() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(RESERVED_DOMAIN);
        assertEquals(domainState.getState(), "reserved");
        assertNull(domainState.getDate());
        assertEquals(domainState.getReason(), "ICANN Reserved");
        assertNull(domainState.getPhase());
        assertNull(domainState.getVariantPrimaryDomainName());
    }

    @Test
    public void shouldReturnCorrectStateDetailsForAnApplication() throws epp_XMLException,
            DatatypeConfigurationException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(DOMAIN_APPLICATION);
        assertEquals(domainState.getState(), "application");
        assertEquals(domainState.getPhase(), "sunrise");
        assertEquals(domainState.getDate(), XMLUtil.fromXSDateTime("2010-04-25T00:00:00Z"));
        assertNull(domainState.getReason());
        assertNull(domainState.getVariantPrimaryDomainName());
    }

    @Test
    public void shouldReturnCorrectStateDetailsForAnActiveVariant() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(DOMAIN_ACTIVE_VARIANT);
        assertEquals(domainState.getState(), "activatedVariant");
        assertEquals(domainState.getVariantPrimaryDomainName(), "domain-VARIANT-one.tld");
        assertNull(domainState.getDate());
        assertNull(domainState.getPhase());
        assertNull(domainState.getReason());
    }

    @Test
    public void shouldReturnCorrectStateDetailsForAWithheldVariant() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(DOMAIN_WITHHELD_VARIANT);
        assertEquals(domainState.getState(), "withheldVariant");
        assertEquals(domainState.getVariantPrimaryDomainName(), "domain-VARIANT-two.tld");
        assertNull(domainState.getDate());
        assertNull(domainState.getPhase());
        assertNull(domainState.getReason());
    }

    @Test
    public void shouldReturnCorrectStateDetailsForABlockedVariant() throws epp_XMLException {
        domainCheckExtendedAvailabilityCommandExtension.fromXML(RESPONSE_XML);
        DomainCheckExtendedAvailabilityDetails domainState = domainCheckExtendedAvailabilityCommandExtension
                .getStateForDomain(DOMAIN_BLOCKED_VARIANT);
        assertEquals(domainState.getState(), "blockedVariant");
        assertEquals(domainState.getVariantPrimaryDomainName(), "domain-VARIANT-three.tld");
        assertNull(domainState.getDate());
        assertNull(domainState.getPhase());
        assertNull(domainState.getReason());
    }
}
