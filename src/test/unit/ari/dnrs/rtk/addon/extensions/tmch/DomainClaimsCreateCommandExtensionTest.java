package ari.dnrs.rtk.addon.extensions.tmch;

import org.junit.Before;
import org.junit.Test;
import org.openrtk.idl.epprtk.epp_XMLException;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DomainClaimsCreateCommandExtensionTest {

    private static String noticeID = "49FD46E6C4B45C55D4AC";
    private static String notAfterDateTimeString = "2007-01-01T01:01:01.000Z";
    private static String acceptedDateTimeString = "2007-02-02T02:02:02.000Z";
    private static String encodedSignedMarkData = "ZW5jb2RlZFNpZ25lZE1hcmtEYXRh";

    private final static String DOMAIN_CREATE_WITH_TMCH_NOTICE_ID_XML =
            "<create xmlns=\"urn:ar:params:xml:ns:tmch-1.0\">"
            + "<noticeID>" + noticeID + "</noticeID>"
            + "<notAfter>" + notAfterDateTimeString + "</notAfter>"
            + "<accepted>"+ acceptedDateTimeString + "</accepted>"
            + "</create>";

    private final static String DOMAIN_CREATE_WITH_TMCH_SMD_XML = "<create xmlns=\"urn:ar:params:xml:ns:tmch-1.0\">"
            + "<smd>" + encodedSignedMarkData  + "</smd>"
            + "</create>";

    private DomainClaimsCreateCommandExtension createCommandExtension;

    @Before
    public void setUp() throws Exception {
        createCommandExtension = new DomainClaimsCreateCommandExtension();
    }

    @Test
    public void shouldCreateValidXmlWhenSupplyingTmchExtensionWithNoticeId() throws epp_XMLException {
        createCommandExtension.setNoticeId(noticeID);
        createCommandExtension.setNotAfterDateTimeString(notAfterDateTimeString);
        createCommandExtension.setAcceptedDateTimeString(acceptedDateTimeString);

        assertThat(createCommandExtension.toXML(), is(DOMAIN_CREATE_WITH_TMCH_NOTICE_ID_XML));
    }

    @Test
    public void shouldCreateValidXmlWhenSupplyingTmchExtensionWithSMD() throws epp_XMLException {
        createCommandExtension.setEncodedSignedMarkData(encodedSignedMarkData);

        assertThat(createCommandExtension.toXML(), is(DOMAIN_CREATE_WITH_TMCH_SMD_XML));
    }
}
