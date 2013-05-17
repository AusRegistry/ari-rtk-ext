package ari.dnrs.rtk.addon.extensions.tmch;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrtk.idl.epprtk.domain.epp_DomainCheckReq;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCheck;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class DomainClaimsCommandExtensionTest {
    private static final String TMCH_CLAIMS_CHECK_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\"><command><check><domain:check xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\"><domain:name>domain-with-claim.tld</domain:name><domain:name>domain-without-claim.tld</domain:name><domain:name>domain.not-a-tld</domain:name><domain:name>just-a-label</domain:name></domain:check></check><extension><check xmlns=\"urn:ar:params:xml:ns:tmch-1.0\"/></extension><clTRID>.20130313.182817.2</clTRID></command></epp>";
    private static final String TMCH_CLAIMS_CHECK_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\"><response><result code=\"1000\"><msg lang=\"en\">Command completed successfully</msg></result><extension><chkData xmlns=\"urn:ar:params:xml:ns:tmch-1.0\"><cd><name claim=\"1\">domain-with-claim.tld</name><key>claimsKey1</key></cd><cd><name claim=\"0\">domain-wihtout-claim.tld</name></cd><cd><name claim=\"0\">domain.not-a-tld</name></cd><cd><name claim=\"1\">just-a-label</name><key>claimsKey2</key></cd></chkData></extension><trID><clTRID>ABC-12345</clTRID><svTRID>57f39ac6-abd2-4fea-9a80-e791d1af86f7</svTRID></trID></response></epp>";
    private epp_Command commandData;

    private epp_DomainCheckReq epp_domainCheckReq;

    private DomainClaimsCommandExtension domainClaimsCommandExtension;

    @Before
    public void setUp() {
        commandData = new epp_Command();
        commandData.setClientTrid(".20130313.182817.2");

        epp_domainCheckReq  = new epp_DomainCheckReq();
        epp_domainCheckReq.setNames(new String[]{"domain-with-claim.tld", "domain-without-claim.tld",
                "domain.not-a-tld", "just-a-label"});

        domainClaimsCommandExtension = new DomainClaimsCommandExtension();
    }

    @Test
    public void shouldGenerateTmchClaimsExtension() throws epp_XMLException {
        epp_domainCheckReq.setCmd(commandData);

        commandData.setExtensions(new epp_Extension[]{domainClaimsCommandExtension});

        EPPDomainCheck eppDomainCheck = new EPPDomainCheck();
        eppDomainCheck.setRequestData(epp_domainCheckReq);

        assertEquals(eppDomainCheck.toXML(), TMCH_CLAIMS_CHECK_REQUEST);
    }

    @Test
    public void shouldGetKeyValuesFromResponseBasedOnDomainLabels() throws epp_XMLException {
        domainClaimsCommandExtension.fromXML(TMCH_CLAIMS_CHECK_RESPONSE);

        assertEquals("claimsKey1", domainClaimsCommandExtension.getClaimsKey("domain-with-claim.tld"));
        assertNull(domainClaimsCommandExtension.getClaimsKey("domain-wihtout-claim.tld"));
        assertNull(domainClaimsCommandExtension.getClaimsKey("domain.not-a-tld"));
        assertEquals("claimsKey2", domainClaimsCommandExtension.getClaimsKey("just-a-label"));
        assertNull(domainClaimsCommandExtension.getClaimsKey("example.tld"));

    }

    @Test
    public void shouldReturnClaimsKeyFromResponseUsingPosition() throws epp_XMLException {
        domainClaimsCommandExtension.fromXML(TMCH_CLAIMS_CHECK_RESPONSE);

        Assert.assertEquals("claimsKey1", domainClaimsCommandExtension.getClaimsKey(1L));
        assertNull(domainClaimsCommandExtension.getClaimsKey(2L));
        assertNull(domainClaimsCommandExtension.getClaimsKey(3L));
        Assert.assertEquals("claimsKey2", domainClaimsCommandExtension.getClaimsKey(4L));
        assertNull(domainClaimsCommandExtension.getClaimsKey(5L));
    }

    @Test
    public void shouldReturnExistsFlagFromResponse() throws epp_XMLException {
        domainClaimsCommandExtension.fromXML(TMCH_CLAIMS_CHECK_RESPONSE);

        Assert.assertEquals(Boolean.TRUE, domainClaimsCommandExtension.claim("domain-with-claim.tld"));
        Assert.assertEquals(Boolean.FALSE, domainClaimsCommandExtension.claim("domain-wihtout-claim.tld"));
        Assert.assertEquals(Boolean.FALSE, domainClaimsCommandExtension.claim("domain.not-a-tld"));
        Assert.assertEquals(Boolean.TRUE, domainClaimsCommandExtension.claim("just-a-label"));
        assertNull(domainClaimsCommandExtension.claim("example3.tld"));
    }

    @Test
    public void shouldReturnExistsFlagFromResponseUsingPosition() throws epp_XMLException {
        domainClaimsCommandExtension.fromXML(TMCH_CLAIMS_CHECK_RESPONSE);

        Assert.assertEquals(Boolean.TRUE, domainClaimsCommandExtension.claim(1L));
        Assert.assertEquals(Boolean.FALSE, domainClaimsCommandExtension.claim(2L));
        Assert.assertEquals(Boolean.FALSE, domainClaimsCommandExtension.claim(3L));
        Assert.assertEquals(Boolean.TRUE, domainClaimsCommandExtension.claim(4L));
        assertNull(domainClaimsCommandExtension.claim(5L));
    }
}
