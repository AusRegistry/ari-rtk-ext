package ari.dnrs.rtk.addon.extensions.variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openrtk.idl.epprtk.epp_XMLException;

import ari.dnrs.rtk.addon.bean.DomainVariantBean;

/**
 * Unit test for request elements in {@link ari.dnrs.rtk.addon.extensions.variant.DomainVariantCommandExtension}.
 */
public class DomainVariantExtensionTest {

    /** The Expected Exception Rule. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DomainVariantCommandExtension domainVariantExtension;
    private String zoneName;

    @Before
    public void setup() {
        domainVariantExtension = new DomainVariantCommandExtension();
        zoneName = "zoneName";
    }

    /**
     * Should successfully parse domain info XML response.
     *
     * @throws epp_XMLException if the test throws an epp_XMLException
     */
    @Test
    public void shouldSuccessfullyParseInfoXMLResponse() throws epp_XMLException {
        final String expectedUserForm = "userform." + zoneName;
        final String expectedDNSForm = "dnsform." + zoneName;
        final String expectedUserForm2 = "userform2." + zoneName;
        final String expectedDNSForm2 = "dnsform2." + zoneName;

        final String domainInfoWithTwoVariantsResponseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
                + "<response><result code=\"1000\"><msg lang=\"en\">Command completed successfully</msg></result>"
                + "<resData>" + "<extension><varInfData xmlns=\"urn:X-ar:params:xml:ns:variant-1.0\""
                + " xsi:schemaLocation=\"urn:X-ar:params:xml:ns:variant-1.0 variant-1.0.xsd\">"
                + "<variant userForm=\"" + expectedUserForm + "\">" + expectedDNSForm + "</variant><variant userForm=\""
                + expectedUserForm2 + "\">" + expectedDNSForm2 + "</variant></varInfData></extension></resData>"
                + "<trID><clTRID>ABC-12345</clTRID><svTRID>54322-XYZ</svTRID></trID>" + "</response>" + "</epp>";

        domainVariantExtension.fromXML(domainInfoWithTwoVariantsResponseXml);
        List<DomainVariantBean> variants = domainVariantExtension.getInfoVariantList();

        assertEquals("Should return expected domain name user form", expectedUserForm, variants.get(0).getUserForm());
        assertEquals("Should return expected domain name DNS form", expectedDNSForm, variants.get(0).getName());

        assertEquals("Should return second expected domain name user form", expectedUserForm2, variants.get(1)
                .getUserForm());
        assertEquals("Should return second expected domain name DNS form", expectedDNSForm2, variants.get(1).getName());
    }

    /**
     * Should create extension XML for a domain update with variants to add.
     *
     * @throws epp_XMLException if the test throws an epp_XMLException
     */
    @Test
    public void shouldCreateExtensionXMLForDomainUpdateWithVariantsToAdd() throws epp_XMLException {
        final String expectedAdd = "dnsAdd." + zoneName;
        final String expectedAddUserForm = "userAdd." + zoneName;
        final String expectedAdd2 = "dnsAdd2." + zoneName;
        final String expectedAddUserForm2 = "userAdd2." + zoneName;

        domainVariantExtension.addToVariantsToRemoveList(expectedAdd, expectedAddUserForm);
        domainVariantExtension.addToVariantsToRemoveList(expectedAdd2, expectedAddUserForm2);

        final String variantUpdateExtensionString = domainVariantExtension.toXML();
        assertNotNull("Should create XML string", variantUpdateExtensionString);

        final String expectedXML = "<update xmlns=\"urn:X-ar:params:xml:ns:variant-1.0\">" //
                + "<rem>" //
                + "<variant userForm=\"" + expectedAddUserForm + "\">" + expectedAdd + "</variant>" //
                + "<variant userForm=\"" + expectedAddUserForm2 + "\">" + expectedAdd2 + "</variant>" //
                + "</rem></update>";

        assertEquals("toXML output should match expected XML string", expectedXML, variantUpdateExtensionString);
    }

    /**
     * Should create extension XML for domain update with variants to remove.
     *
     * @throws epp_XMLException if the test throws an epp_XMLException
     */
    @Test
    public void shouldCreateExtensionXMLForDomainUpdateWithVariantsToRemove() throws epp_XMLException {
        final String expectedRemove = "dnsRem." + zoneName;
        final String expectedRemoveUserForm = "userRem." + zoneName;
        final String expectedRemove2 = "dnsRem2." + zoneName;
        final String expectedRemoveUserForm2 = "userRem2." + zoneName;

        domainVariantExtension.addToVariantsToRemoveList(expectedRemove, expectedRemoveUserForm);
        domainVariantExtension.addToVariantsToRemoveList(expectedRemove2, expectedRemoveUserForm2);

        final String variantUpdateExtensionString = domainVariantExtension.toXML();
        assertNotNull("Should create XML string", variantUpdateExtensionString);

        final String expectedXML = "<update xmlns=\"urn:X-ar:params:xml:ns:variant-1.0\">" //
                + "<rem>" //
                + "<variant userForm=\"" + expectedRemoveUserForm + "\">" + expectedRemove + "</variant>" //
                + "<variant userForm=\"" + expectedRemoveUserForm2 + "\">" + expectedRemove2 + "</variant>" //
                + "</rem></update>";

        assertEquals("toXML output should match expected XML string", expectedXML, variantUpdateExtensionString);
    }

    /**
     * Should create extension XML for domain update with variants to add and remove.
     *
     * @throws epp_XMLException if the test throws an epp_XMLException
     */
    @Test
    public void shouldCreateExtensionXMLForDomainUpdateWithVariantsToAddAndRemove() throws epp_XMLException {
        //Variants to add
        final String expectedAdd = "dnsAdd." + zoneName;
        final String expectedAddUserForm = "userAdd." + zoneName;
        final String expectedAdd2 = "dnsAdd2." + zoneName;
        final String expectedAddUserForm2 = "userAdd2." + zoneName;

        //Variants to remove
        final String expectedRemove = "dnsRem." + zoneName;
        final String expectedRemoveUserForm = "userRem." + zoneName;
        final String expectedRemove2 = "dnsRem2." + zoneName;
        final String expectedRemoveUserForm2 = "userRem2." + zoneName;

        domainVariantExtension.addToVariantsToAddList(expectedAdd, expectedAddUserForm);
        domainVariantExtension.addToVariantsToAddList(expectedAdd2, expectedAddUserForm2);
        domainVariantExtension.addToVariantsToRemoveList(expectedRemove, expectedRemoveUserForm);
        domainVariantExtension.addToVariantsToRemoveList(expectedRemove2, expectedRemoveUserForm2);

        final String variantUpdateExtensionString = domainVariantExtension.toXML();
        assertNotNull("Should create XML string", variantUpdateExtensionString);

        final String expectedXML = "<update xmlns=\"urn:X-ar:params:xml:ns:variant-1.0\">" //
                + "<add>" //
                + "<variant userForm=\"" + expectedAddUserForm + "\">" + expectedAdd + "</variant>" //
                + "<variant userForm=\"" + expectedAddUserForm2 + "\">" + expectedAdd2 + "</variant>" //
                + "</add>" //
                + "<rem>" //
                + "<variant userForm=\"" + expectedRemoveUserForm + "\">" + expectedRemove + "</variant>" //
                + "<variant userForm=\"" + expectedRemoveUserForm2 + "\">" + expectedRemove2 + "</variant>" //
                + "</rem></update>";

        assertEquals("toXML output should match expected XML string", expectedXML, variantUpdateExtensionString);
    }

    /**
     * Should throw an exception when attempting to convert a domain variant extension with no variants provided.
     *
     * @throws epp_XMLException the epp_XMLException expected to be thrown by this test
     */
    @Test
    public void shouldThrowExceptionWhenAttemptingToConvertAnExtensionWithNoVariantsAdded() throws epp_XMLException {

        thrown.expect(epp_XMLException.class);
        try {
            domainVariantExtension.toXML();
        } catch (final epp_XMLException e) {
            assertEquals("The error message is not what is expected", "Should provide at least one variant to add" //
                    + " or remove to construct variant extension XML", e.getErrorMessage());
            throw e;
        }

    }
}
