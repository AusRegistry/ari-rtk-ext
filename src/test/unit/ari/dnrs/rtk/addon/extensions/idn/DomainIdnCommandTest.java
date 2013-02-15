package ari.dnrs.rtk.addon.extensions.idn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openrtk.idl.epprtk.epp_AuthInfo;
import org.openrtk.idl.epprtk.epp_AuthInfoType;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriod;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

public class DomainIdnCommandTest {

    @Test
    public void shouldGenerateCorrectXmlForDomainCreateCommandWithIdnExtension() {
        EPPDomainCreate cmd = new EPPDomainCreate();
        DomainIdnCommandExtension idnExt = new DomainIdnCommandExtension();
        idnExt.setLanguageTag("test");

        epp_Extension[] extensions = { idnExt };

        epp_DomainCreateReq domainCreateRequest = new epp_DomainCreateReq();
        epp_Command commandData = new epp_Command();
        commandData.setClientTrid("client_trid");
        domainCreateRequest.setCmd(commandData);

        domainCreateRequest.setName("test-domain.com.au");

        domainCreateRequest.setPeriod(new epp_DomainPeriod());
        domainCreateRequest.getPeriod().setUnit(epp_DomainPeriodUnitType.YEAR);
        domainCreateRequest.getPeriod().setValue((short) 2);

        List<String> nameServerList = new ArrayList<String>();
        nameServerList.add("ns1.valid.info");
        nameServerList.add("ns2.valid.info");
        domainCreateRequest.setNameServers(EPPXMLBase.convertListToStringArray(nameServerList));

        epp_AuthInfo domain_auth_info = new epp_AuthInfo();
        domain_auth_info.setValue("123123");

        domain_auth_info.setType(epp_AuthInfoType.PW);
        domainCreateRequest.setAuthInfo(domain_auth_info);

        domainCreateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainCreateRequest);

        try {
            final String xml = cmd.toXML();
            assertEquals(
                    "Incorrect XML produced",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" "
                    		+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
                    		+ "<command><create><domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
                            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
                    		+ "<domain:name>test-domain.com.au</domain:name><domain:period unit=\"y\">2</domain:period>"
                            + "<domain:ns><domain:hostObj>ns1.valid.info</domain:hostObj>"
                    		+ "<domain:hostObj>ns2.valid.info</domain:hostObj></domain:ns>"
                            + "<domain:authInfo><domain:pw>123123</domain:pw></domain:authInfo>"
                    		+ "</domain:create></create>"
                            + "<extension><create xmlns=\"urn:rbp:params:xml:ns:idn-1.0\">"
                    		+ "<languageTag>test</languageTag></create></extension>"
                            + "<clTRID>client_trid</clTRID></command></epp>",
                    xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldParseInfoXMLResponse() throws epp_XMLException {
        DomainIdnCommandExtension domainCreateIdnaCommandExtension = new DomainIdnCommandExtension();
        String domainName = "domainName";
        String languageTag = "test";
        domainCreateIdnaCommandExtension.fromXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">" + "<response>"
                + "<result code=\"1000\">" + "<msg>Command completed successfully</msg>" + "</result>" + "<resData>"
                + "<infData xmlns=\"urn:ietf:params:xml:ns:domain-1.0\""
                + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" + "<name>"
                + domainName
                + "</name>"
                + "<roid>D0000003-AR</roid>"
                + "<status s=\"ok\" language=\"en\"/>"
                + "<registrant>EXAMPLE</registrant>"
                + "<contact type=\"tech\">EXAMPLE</contact>"
                + "<ns>"
                + "<hostObj>ns1.example.com.au</hostObj>"
                + "<hostObj>ns2.example.com.au</hostObj>"
                + "</ns>"
                + "<host>ns1.example.com.au</host>"
                + "<host>ns2.exmaple.com.au</host>"
                + "<clID>Registrar</clID>"
                + "<crID>Registrar</crID>"
                + "<crDate>2006-02-09T15:44:58.0Z</crDate>"
                + "<exDate>2008-02-10T00:00:00.0Z</exDate>"
                + "<authInfo>"
                + "<pw>0192pqow</pw>"
                + "</authInfo>"
                + "</infData>"
                + "</resData>"
                + "<extension>"
                + "<infData xmlns=\"urn:rbp:params:xml:ns:idn-1.0\">"
                + "<languageTag>" + languageTag + "</languageTag>"
                + "</infData>"
                + "</extension>"
                + "<trID>"
                + "<clTRID>ABC-12345</clTRID>" + "<svTRID>54321-XYZ</svTRID>" + "</trID>" + "</response>" + "</epp>");
        assertEquals("Language was incorrect", languageTag, domainCreateIdnaCommandExtension.getLanguageTag());
    }

    @Test
    public void shouldParseInfDataResponse() throws epp_XMLException {
        DomainIdnCommandExtension domainCreateIdnaCommandExtension = new DomainIdnCommandExtension();
        String languageTag = "test";
        domainCreateIdnaCommandExtension.fromXML("<infData xmlns=\"urn:rbp:params:xml:ns:idn-1.0\">"
                + "<languageTag>" + languageTag + "</languageTag></infData>");
        assertEquals("Language was incorrect", languageTag, domainCreateIdnaCommandExtension.getLanguageTag());
    }

}
