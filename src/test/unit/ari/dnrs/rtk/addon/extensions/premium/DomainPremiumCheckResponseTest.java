package ari.dnrs.rtk.addon.extensions.premium;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DomainPremiumCheckResponseTest {
    private static final String premiumXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                    "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">" +
                    "<response><result code=\"1000\"><msg>Command completed successfully</msg></result>" +
                    "<resData>" +
                    "<chkData xmlns=\"urn:ar:params:xml:ns:premium-1.0\">" +
                    "<cd>" +
                    "<name premium=\"1\">premiumdomain1.zone</name>" +
                    "<price>100.00</price>" +
                    "<renewalPrice>150.00</renewalPrice>" +
                    "</cd>" +
                    "<cd>" +
                    "<name premium=\"1\">premiumdomain2.zone</name>" +
                    "<price>500.00</price>" +
                    "<renewalPrice>550.00</renewalPrice>" +
                    "</cd>" +
                    "<cd>" +
                    "<name premium=\"0\">nonpremiumdomain1.zone</name>" +
                    "<price>500.00</price>" +
                    "<renewalPrice>550.00</renewalPrice>" +
                    "</cd>" +
                    "</chkData>" +
                    "</resData>" +
                    "<trID><clTRID>ABC-12345</clTRID><svTRID>54322-XYZ</svTRID>" +
                    "</trID></response></epp>";

    DomainPremiumCheckResponse domainPremiumCheckResponse;

    @Before
    public void setUp() {
        domainPremiumCheckResponse = new DomainPremiumCheckResponse();
        domainPremiumCheckResponse.fromXML(premiumXml);
    }

    @Test
    public void testGetCreateAndRenewPriceForPremiumDomain() throws Exception {
        assertEquals(domainPremiumCheckResponse.isPremium("premiumdomain1.zone"), true);
        assertEquals(domainPremiumCheckResponse.getCreatePrice("premiumdomain1.zone"), BigDecimal.valueOf(100.0));
        assertEquals(domainPremiumCheckResponse.getRenewPrice("premiumdomain1.zone"), BigDecimal.valueOf(150.0));

        assertEquals(domainPremiumCheckResponse.isPremium("premiumdomain2.zone"), true);
        assertEquals(domainPremiumCheckResponse.getCreatePrice("premiumdomain2.zone"), BigDecimal.valueOf(500.0));
        assertEquals(domainPremiumCheckResponse.getRenewPrice("premiumdomain2.zone"), BigDecimal.valueOf(550.0));
    }

    @Test
    public void testShouldNotHavePriceForNonPremiumDomains() {
        assertEquals(domainPremiumCheckResponse.isPremium("nonpremiumdomain1.zone"), false);
        assertEquals(domainPremiumCheckResponse.getCreatePrice("nonpremiumdomain1.zone"), null);
        assertEquals(domainPremiumCheckResponse.getRenewPrice("nonpremiumdomain1.zone"), null);
    }

    @Test
    public void testShouldBeAbleToGetValuesUsingPosition() {
        assertEquals(domainPremiumCheckResponse.isPremium(1), true);
        assertEquals(domainPremiumCheckResponse.getCreatePrice(2), BigDecimal.valueOf(500.0));
        assertEquals(domainPremiumCheckResponse.getRenewPrice(3), null);
    }

    @Test
    public void testShouldGetNullForInvalidDomains() {
        assertEquals(domainPremiumCheckResponse.isPremium("invalid"), false);
        assertEquals(domainPremiumCheckResponse.getCreatePrice("invalid"), null);
        assertEquals(domainPremiumCheckResponse.getRenewPrice("invalid"), null);

        assertEquals(domainPremiumCheckResponse.isPremium("invalid"), false);
        assertEquals(domainPremiumCheckResponse.getCreatePrice("invalid"), null);
        assertEquals(domainPremiumCheckResponse.getRenewPrice("invalid"), null);
    }
}
