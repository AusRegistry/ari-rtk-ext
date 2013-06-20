package ari.dnrs.rtk.addon.extensions.tmch;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;
import org.junit.Before;
import org.junit.Test;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DomainClaimsInfoCommandExtensionTest {

    private DomainClaimsInfoCommandExtension claimsInfoCommandExtension;

    @Before
    public void setUp() throws Exception {
        claimsInfoCommandExtension = new DomainClaimsInfoCommandExtension();
    }

    @Test
    public void shouldGenerateCorrectXmlForTmchExtensionToDomainInfoRequestCommand() throws epp_XMLException {
        String requestXml = claimsInfoCommandExtension.toXML();

        String expectedRequestXml = "<info xmlns=\"urn:ar:params:xml:ns:tmch-1.0\"/>";
        assertThat(requestXml, is(expectedRequestXml));
    }

    @Test
    public void shouldAppendExtensionXmlCorrectlyToDomainInfoRequestCommand() throws epp_XMLException {
        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\"><command><info>"
                + "<domain:info xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
                + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
                + "<domain:name hosts=\"all\">domain.name</domain:name>"
                + "</domain:info></info>"
                + "<extension><info xmlns=\"urn:ar:params:xml:ns:tmch-1.0\"/></extension>"
                + "<clTRID>.20130313.182817.2</clTRID></command></epp>";
        epp_Command commandData = new epp_Command();
        commandData.setClientTrid(".20130313.182817.2");
        commandData.setExtensions(new epp_Extension[]{claimsInfoCommandExtension});

        epp_DomainInfoReq epp_domainInfoReq = new epp_DomainInfoReq();
        epp_domainInfoReq.setName("domain.name");
        epp_domainInfoReq.setCmd(commandData);

        EPPDomainInfo eppDomainInfo = new EPPDomainInfo();
        eppDomainInfo.setRequestData(epp_domainInfoReq);

        assertThat(expectedXml, is(eppDomainInfo.toXML()));
    }

    @Test
    public void shouldReturnTheCorrectSmdForADomainNameFromInfoResponse() throws epp_XMLException {
        String expectedEncodedSmdString = "ZW5jb2RlZFNpZ25lZE1hcmtEYXRh";

        String infoResponseXml = "<infData xmlns=\"urn:ar:params:xml:ns:tmch-1.0\">"
                + "<smd>" + expectedEncodedSmdString + "</smd>"
                + "</infData>";

        claimsInfoCommandExtension.fromXML(infoResponseXml);

        String encodedSmdString = claimsInfoCommandExtension.getEncodedSignedMarkData();

        assertThat(encodedSmdString, is(expectedEncodedSmdString));
    }

    @Test
    public void shouldReturnSafelyWhenInputIsNull() throws epp_XMLException {
        claimsInfoCommandExtension.fromXML(null);
    }

    @Test
    public void shouldReturnSafelyWhenInputIsEmpty() throws epp_XMLException {
        claimsInfoCommandExtension.fromXML("");
    }
}
