package ari.dnrs.rtk.addon.extensions.price;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;

public class DomainPriceCheckExtensionResponseTest {
    private static final String priceXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                    "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">" +
                    "<response>" +
                    "<result code=\"1000\">" +
                    "<msg lang=\"en\">Command completed successfully</msg>" +
                    "</result>" +
                    "<extension>" +
                    "<chkData xmlns=\"urn:ar:params:xml:ns:price-1.0\">" +
                    "<cd>" +
                    "<name premium=\"1\">premiumdomain.zonewithpermission</name>" +
                    "<period unit=\"y\">3</period>" +
                    "<price>200</price>" +
                    "<renewalPrice>180.0</renewalPrice>" +
                    "</cd>" +
                    "<cd>" +
                    "<name premium=\"0\">normaldomain.zonewithpermission</name>" +
                    "<period unit=\"y\">1</period>" +
                    "<price>10</price>" +
                    "<renewalPrice>9.495</renewalPrice>" +
                    "</cd>" +
                    "<cd>" +
                    "<name>domain.notexistszone</name>" +
                    "<reason>Invalid domain name</reason>" +
                    "</cd>" +
                    "</chkData>" +
                    "</extension>" +
                    "<trID>" +
                    "<clTRID>.20130514.153545.0</clTRID>" +
                    "<svTRID>serverTxnId-1368509745652</svTRID>" +
                    "</trID>" +
                    "</response>" +
                    "</epp>";

    DomainPriceCheckExtensionResponse domainPriceCheckExtensionResponse;

    @Before
    public void setUp() {
        domainPriceCheckExtensionResponse = new DomainPriceCheckExtensionResponse();
        domainPriceCheckExtensionResponse.fromXML(priceXml);
    }

    @Test
    public void testGetCreateAndRenewPriceForPremiumDomain() throws Exception {
        assertEquals(domainPriceCheckExtensionResponse.isPremium("premiumdomain.zonewithpermission"), true);
        assertEquals(domainPriceCheckExtensionResponse.getPeriod("premiumdomain.zonewithpermission").getUnit(),
                epp_DomainPeriodUnitType.YEAR);
        assertEquals(domainPriceCheckExtensionResponse.getPeriod("premiumdomain.zonewithpermission").getValue(), 3);
        assertEquals(domainPriceCheckExtensionResponse.getCreatePrice("premiumdomain.zonewithpermission"),
                BigDecimal.valueOf(200.0));
        assertEquals(domainPriceCheckExtensionResponse.getRenewPrice("premiumdomain.zonewithpermission"),
                BigDecimal.valueOf(180.0));
    }

    @Test
    public void testGetCreateAndRenewPriceForNonPremiumDomain() {
        assertEquals(domainPriceCheckExtensionResponse.isPremium("normaldomain.zonewithpermission"), false);
        assertEquals(domainPriceCheckExtensionResponse.getPeriod("normaldomain.zonewithpermission").getUnit(),
                epp_DomainPeriodUnitType.YEAR);
        assertEquals(domainPriceCheckExtensionResponse.getPeriod("normaldomain.zonewithpermission").getValue(), 1);
        assertEquals(domainPriceCheckExtensionResponse.getCreatePrice("normaldomain.zonewithpermission"),
                BigDecimal.valueOf(10.0));
        assertEquals(domainPriceCheckExtensionResponse.getRenewPrice("normaldomain.zonewithpermission"),
                BigDecimal.valueOf(9.495));
    }

    @Test
    public void testGetErrorReasonForInvalidDomain() {
        assertEquals(domainPriceCheckExtensionResponse.isPremium("domain.notexistszone"), null);
        assertEquals(domainPriceCheckExtensionResponse.getPeriod("domain.notexistszone"), null);
        assertEquals(domainPriceCheckExtensionResponse.getCreatePrice("domain.notexistszone"), null);
        assertEquals(domainPriceCheckExtensionResponse.getRenewPrice("domain.notexistszone"), null);
        assertEquals(domainPriceCheckExtensionResponse.getReason("domain.notexistszone"), "Invalid domain name");

    }

    @Test
    public void testShouldBeAbleToGetValuesUsingPosition() {
        assertEquals(domainPriceCheckExtensionResponse.isPremium(1), true);
        assertEquals(domainPriceCheckExtensionResponse.getPeriod(1).getUnit(), epp_DomainPeriodUnitType.YEAR);
        assertEquals(domainPriceCheckExtensionResponse.getPeriod(1).getValue(), 3);
        assertEquals(domainPriceCheckExtensionResponse.getCreatePrice(2), BigDecimal.valueOf(10.0));
        assertEquals(domainPriceCheckExtensionResponse.getReason(3), "Invalid domain name");
    }

    @Test
    public void testShouldGetNullForInvalidDomains() {
        assertEquals(domainPriceCheckExtensionResponse.isPremium("invalid"), null);
        assertEquals(domainPriceCheckExtensionResponse.getCreatePrice("invalid"), null);
        assertEquals(domainPriceCheckExtensionResponse.getRenewPrice("invalid"), null);
    }
}
