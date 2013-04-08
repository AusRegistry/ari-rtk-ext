package ari.dnrs.rtk.addon.extensions.app;

import org.junit.Before;
import org.junit.Test;
import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;

import com.tucows.oxrs.epprtk.rtk.xml.*;

import static org.junit.Assert.assertEquals;

public class DomainApplicationCommandExtensionTest {

    String CREATE_REQUEST_WITH_APP_EXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command><create><domain:create xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>jtkutest.com.au</domain:name><domain:authInfo><domain:pw>jtkUT3st</domain:pw>"
            + "</domain:authInfo></domain:create></create>"
            + "<extension><create xmlns=\"urn:ar:params:xml:ns:application-1.0\"><phase>sunrise</phase></create>"
            + "</extension><clTRID>JTKUTEST.20070101.010101.0</clTRID></command></epp>";

    String DELETE_REQUEST_WITH_APP_EXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command><delete><domain:delete xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>jtkutest.com.au</domain:name></domain:delete></delete>" + "<extension>"
            + "<delete xmlns=\"urn:ar:params:xml:ns:application-1.0\"><id>sunrise-application-id</id></delete>"
            + "</extension><clTRID>JTKUTEST.20070101.010101.0</clTRID></command></epp>";

    String INFO_REQUEST_WITH_APP_EXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command><info><domain:info xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name hosts=\"all\">jtkutest.com.au</domain:name></domain:info></info>" + "<extension>"
            + "<info xmlns=\"urn:ar:params:xml:ns:application-1.0\"><id>sunrise-application-id</id></info>"
            + "</extension><clTRID>JTKUTEST.20070101.010101.0</clTRID></command></epp>";

    String UPDATE_REQUEST_WITH_APP_EXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command><update><domain:update xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>jtkutest.com.au</domain:name></domain:update></update>" + "<extension>"
            + "<update xmlns=\"urn:ar:params:xml:ns:application-1.0\"><id>sunrise-application-id</id></update>"
            + "</extension><clTRID>JTKUTEST.20070101.010101.0</clTRID></command></epp>";

    private epp_Command commandData;
    private DomainApplicationCommandExtension domainApplicationCommandExtension;
    private epp_DomainCreateReq domainCreateRequest;
    private epp_DomainDeleteReq domainDeleteReq;
    private epp_DomainUpdateReq domainUpdateReq;
    private epp_DomainInfoReq domainInfoReq;

    @Before
    public void setUp() {
        commandData = new epp_Command();
        commandData.setClientTrid("JTKUTEST.20070101.010101.0");

        domainCreateRequest = new epp_DomainCreateReq();
        domainCreateRequest.setName("jtkutest.com.au");
        domainCreateRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", "jtkUT3st"));

        domainDeleteReq = new epp_DomainDeleteReq();
        domainDeleteReq.setName("jtkutest.com.au");

        domainInfoReq = new epp_DomainInfoReq();
        domainInfoReq.setName("jtkutest.com.au");

        domainUpdateReq = new epp_DomainUpdateReq();
        domainUpdateReq.setName("jtkutest.com.au");
    }

    @Test
    public void shouldHaveValidXmlForCreateApplicationExtension() throws epp_XMLException {
        domainCreateRequest.setCmd(commandData);

        domainApplicationCommandExtension = new DomainApplicationCommandExtension("create");
        domainApplicationCommandExtension.setPhaseType("sunrise");

        commandData.setExtensions(new epp_Extension[]{domainApplicationCommandExtension});

        EPPDomainCreate eppDomainCreate = new EPPDomainCreate();
        eppDomainCreate.setRequestData(domainCreateRequest);

        assertEquals(eppDomainCreate.toXML(), CREATE_REQUEST_WITH_APP_EXT);
    }

    @Test
    public void shouldHaveValidXmlForDeleteApplicationExtension() throws epp_XMLException {
        domainDeleteReq.setCmd(commandData);

        domainApplicationCommandExtension = new DomainApplicationCommandExtension("delete");
        domainApplicationCommandExtension.setApplicationId("sunrise-application-id");

        commandData.setExtensions(new epp_Extension[]{domainApplicationCommandExtension});

        EPPDomainDelete eppDomainDelete = new EPPDomainDelete();
        eppDomainDelete.setRequestData(domainDeleteReq);

        assertEquals(eppDomainDelete.toXML(), DELETE_REQUEST_WITH_APP_EXT);
    }

    @Test
    public void shouldHaveValidXmlForUpdateApplicationExtension() throws epp_XMLException {
        domainUpdateReq.setCmd(commandData);

        domainApplicationCommandExtension = new DomainApplicationCommandExtension("update");
        domainApplicationCommandExtension.setApplicationId("sunrise-application-id");

        commandData.setExtensions(new epp_Extension[]{domainApplicationCommandExtension});

        EPPDomainUpdate eppDomainUpdate = new EPPDomainUpdate();
        eppDomainUpdate.setRequestData(domainUpdateReq);

        assertEquals(eppDomainUpdate.toXML(), UPDATE_REQUEST_WITH_APP_EXT);
    }

    @Test
    public void shouldHaveValidXmlForInfoApplicationExtension() throws epp_XMLException {
        domainInfoReq.setCmd(commandData);

        domainApplicationCommandExtension = new DomainApplicationCommandExtension("info");
        domainApplicationCommandExtension.setApplicationId("sunrise-application-id");

        commandData.setExtensions(new epp_Extension[]{domainApplicationCommandExtension});

        EPPDomainInfo eppDomainInfo = new EPPDomainInfo();
        eppDomainInfo.setRequestData(domainInfoReq);

        assertEquals(eppDomainInfo.toXML(), INFO_REQUEST_WITH_APP_EXT);
    }
}
