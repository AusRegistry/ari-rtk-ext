package ari.dnrs.rtk.addon.extensions.kv;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openrtk.idl.epprtk.epp_AuthInfo;
import org.openrtk.idl.epprtk.epp_AuthInfoType;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainContact;
import org.openrtk.idl.epprtk.domain.epp_DomainContactType;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;

import ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

public class DomainKVCommandExtensionTest {

    /** The Expected Exception Rule. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private epp_DomainCreateReq domainCreateRequest;

    private epp_Command commandData;

    @Before
    public void setUp() {
        commandData = new epp_Command();
        commandData.setClientTrid("JTKUTEST.20070101.010101.0");

        domainCreateRequest = new epp_DomainCreateReq();
        domainCreateRequest.setCmd(commandData);
        domainCreateRequest.setName("jtkutest.com.ae");
        domainCreateRequest.setRegistrant("JTKCON");
        domainCreateRequest.setContacts(new epp_DomainContact[] {new epp_DomainContact(epp_DomainContactType.TECH,
                "JTKCON2")});
        domainCreateRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", "jtkUT3st"));
    }

    @Test
    public void shouldCreateKVExtensionForDomainCreateCommand() throws epp_XMLException {
        final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("create");
        kvExtension.addKeyValuePairToList("ae", "eligibilityType", "Trademark");
        kvExtension.addKeyValuePairToList("ae", "policyReason", "1");
        kvExtension.addKeyValuePairToList("ae", "registrantName", "AusRegistry");

        final epp_Extension[] extensions = {kvExtension};
        domainCreateRequest.getCmd().setExtensions(extensions);

        final EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        final String xml = domainCreate.toXML();
        assertEquals("Generated XML should be correct",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" "
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">" + "<command><create>"
                        + "<domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
                        + "<domain:name>jtkutest.com.ae</domain:name>"
                        + "<domain:registrant>JTKCON</domain:registrant>"
                        + "<domain:contact type=\"tech\">JTKCON2</domain:contact>" + "<domain:authInfo>"
                        + "<domain:pw>jtkUT3st</domain:pw>" + "</domain:authInfo>" + "</domain:create></create>"
                        + "<extension>" + "<create xmlns=\"urn:X-ar:params:xml:ns:kv-1.0\">" + "<kvlist name=\"ae\">"
                        + "<item key=\"eligibilityType\">Trademark</item>" + "<item key=\"policyReason\">1</item>"
                        + "<item key=\"registrantName\">AusRegistry</item>" + "</kvlist></create>" + "</extension>"
                        + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>" + "</command></epp>", xml);
    }

    @Test
    public void shouldCreateKVExtensionForDomainCreateCommandWithDuplicateValues() throws epp_XMLException {
        final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("create");
        kvExtension.addKeyValuePairToList("ae", "eligibilityType", "Trademark");
        kvExtension.addKeyValuePairToList("ae", "policyReason", "1");
        kvExtension.addKeyValuePairToList("ae", "registrantName", "AusRegistry");
        kvExtension.addKeyValuePairToList("ae", "registrantName", "AusRegistry");

        final epp_Extension[] extensions = {kvExtension};
        domainCreateRequest.getCmd().setExtensions(extensions);

        final EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        final String xml = domainCreate.toXML();
        assertEquals(
                "Generated XML should be correct",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" "
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
                        + "<command><create>"
                        + "<domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
                        + "<domain:name>jtkutest.com.ae</domain:name>"
                        + "<domain:registrant>JTKCON</domain:registrant>"
                        + "<domain:contact type=\"tech\">JTKCON2</domain:contact>"
                        + "<domain:authInfo>"
                        + "<domain:pw>jtkUT3st</domain:pw>"
                        + "</domain:authInfo>"
                        + "</domain:create></create>"
                        + "<extension>"
                        + "<create xmlns=\"urn:X-ar:params:xml:ns:kv-1.0\">"
                        + "<kvlist name=\"ae\">"
                        + "<item key=\"eligibilityType\">Trademark</item>"
                        + "<item key=\"policyReason\">1</item>"
                        + "<item key=\"registrantName\">AusRegistry</item>"
                        + "<item key=\"registrantName\">AusRegistry</item>"
                        + "</kvlist></create>" + "</extension>" + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
                        + "</command></epp>", xml);
    }

    @Test
    public void shouldCreateKVExtensionWithMultipleKVListsForDomainCreateCommand() throws epp_XMLException {
        final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("create");
        kvExtension.addKeyValuePairToList("ae", "eligibilityType", "TrademarkAE");
        kvExtension.addKeyValuePairToList("ae", "policyReason", "1");
        kvExtension.addKeyValuePairToList("ae", "registrantName", "AusRegistryAE");

        kvExtension.addKeyValuePairToList("au", "eligibilityType", "TrademarkAU");
        kvExtension.addKeyValuePairToList("au", "policyReason", "2");
        kvExtension.addKeyValuePairToList("au", "registrantName", "AusRegistryAU");

        final epp_Extension[] extensions = {kvExtension};
        domainCreateRequest.getCmd().setExtensions(extensions);

        final EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        final String xml = domainCreate.toXML();
        assertEquals("Generated XML should be correct",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" "
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">" + "<command><create>"
                        + "<domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
                        + "<domain:name>jtkutest.com.ae</domain:name>"
                        + "<domain:registrant>JTKCON</domain:registrant>"
                        + "<domain:contact type=\"tech\">JTKCON2</domain:contact>" + "<domain:authInfo>"
                        + "<domain:pw>jtkUT3st</domain:pw>" + "</domain:authInfo>" + "</domain:create></create>"
                        + "<extension>" + "<create xmlns=\"urn:X-ar:params:xml:ns:kv-1.0\">" + "<kvlist name=\"ae\">"
                        + "<item key=\"eligibilityType\">TrademarkAE</item>" + "<item key=\"policyReason\">1</item>"
                        + "<item key=\"registrantName\">AusRegistryAE</item>" + "</kvlist>" + "<kvlist name=\"au\">"
                        + "<item key=\"eligibilityType\">TrademarkAU</item>" + "<item key=\"policyReason\">2</item>"
                        + "<item key=\"registrantName\">AusRegistryAU</item>" + "</kvlist>" + "</create>"
                        + "</extension>" + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>" + "</command></epp>", xml);
    }

    @Test
    public void shouldCreateKVExtensionWithMultipleKVListsForDomainUpdateCommand() throws epp_XMLException {
        final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("update");
        kvExtension.addKeyValuePairToList("ae", "eligibilityType", "TrademarkAE");
        kvExtension.addKeyValuePairToList("ae", "policyReason", "1");
        kvExtension.addKeyValuePairToList("ae", "registrantName", "AusRegistryAE");

        kvExtension.addKeyValuePairToList("au", "eligibilityType", "TrademarkAU");
        kvExtension.addKeyValuePairToList("au", "policyReason", "2");
        kvExtension.addKeyValuePairToList("au", "registrantName", "AusRegistryAU");

        final epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        domainUpdateRequest.setCmd(commandData);
        domainUpdateRequest.setName("jtkutest.com.ae");

        final epp_Extension[] extensions = {kvExtension};
        domainUpdateRequest.getCmd().setExtensions(extensions);

        final EPPDomainUpdate domainUpdate = new EPPDomainUpdate();
        domainUpdate.setRequestData(domainUpdateRequest);

        final String xml = domainUpdate.toXML();

        assertEquals("Generated XML should be correct",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" "
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\"><command><update>"
                        + "<domain:update xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
                        + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
                        + "<domain:name>jtkutest.com.ae</domain:name>" + "</domain:update>" + "</update>"
                        + "<extension>" + "<update xmlns=\"urn:X-ar:params:xml:ns:kv-1.0\">" + "<kvlist name=\"ae\">"
                        + "<item key=\"eligibilityType\">TrademarkAE</item>" + "<item key=\"policyReason\">1</item>"
                        + "<item key=\"registrantName\">AusRegistryAE</item></kvlist>" + "<kvlist name=\"au\">"
                        + "<item key=\"eligibilityType\">TrademarkAU</item>" + "<item key=\"policyReason\">2</item>"
                        + "<item key=\"registrantName\">AusRegistryAU</item></kvlist>"
                        + "</update></extension><clTRID>JTKUTEST.20070101.010101.0</clTRID></command></epp>", xml);
    }

    @Test
    public void shouldFailKVExtensionWhenCommandNotProvided() throws epp_XMLException {
        final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("");
        kvExtension.addKeyValuePairToList("ae", "eligibilityType", "Trademark");
        kvExtension.addKeyValuePairToList("ae", "policyReason", "1");
        kvExtension.addKeyValuePairToList("ae", "registrantName", "AusRegistry");

        final epp_Extension[] extensions = {kvExtension};
        domainCreateRequest.getCmd().setExtensions(extensions);

        final EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        thrown.expect(epp_XMLException.class);
        try {
            domainCreate.toXML();
        } catch (final epp_XMLException e) {
            assertEquals("The error message is not what is expected",
                    "Should provide command and at least one kvlist to construct kv extension XML", e.getErrorMessage());
            throw e;
        }

    }

    @Test
    public void shouldFailKVExtensionWhenKeyValueDataNotProvided() throws epp_XMLException {
        final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("create");

        final epp_Extension[] extensions = {kvExtension};
        domainCreateRequest.getCmd().setExtensions(extensions);

        final EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        thrown.expect(epp_XMLException.class);
        try {
            domainCreate.toXML();
        } catch (final epp_XMLException e) {
            assertEquals("The error message is not what is expected",
                    "Should provide command and at least one kvlist to construct kv extension XML", e.getErrorMessage());
            throw e;
        }

    }
}
