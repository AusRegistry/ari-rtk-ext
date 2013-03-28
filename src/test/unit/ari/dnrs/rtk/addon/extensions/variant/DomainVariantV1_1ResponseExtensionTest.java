package ari.dnrs.rtk.addon.extensions.variant;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ari.dnrs.rtk.addon.bean.IdnDomainVariant;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;

public class DomainVariantV1_1ResponseExtensionTest {

    @Test
    public void testGetVariantsV1_1ForInfo() throws Exception {
        final String dnsForm = "xn--xha91b83h.com";
        final String variantDnsForm = "xn--lga31c50h.com";
        final DomainVariantResponseExtensionV1_1 variantsExtension =
                new DomainVariantResponseExtensionV1_1("info");

        variantsExtension.fromXml(getCreateResponseExpectedXml(dnsForm, "infData", variantDnsForm, "another",
                "yetAnother"));

        assertTrue("Variant extension should have been initialised",
                variantsExtension.isInitialised());
        final List<IdnDomainVariant> variantList = variantsExtension.getVariants();
        assertThat("Incorrect number of variants returned", variantList.size(), is(3));
        assertThat(variantList, hasItems(new IdnDomainVariant(variantDnsForm), new IdnDomainVariant("another"),
                new IdnDomainVariant("yetAnother")));
    }

    @Test
    public void testGetVariantsV1_1ForCreate() throws Exception {
        final String dnsForm = "xn--xha91b83h.com";
        final String variantDnsForm = "xn--lga31c50h.com";
        final DomainVariantResponseExtensionV1_1 variantsExtension =
                new DomainVariantResponseExtensionV1_1("create");

        variantsExtension.fromXml(getCreateResponseExpectedXml(dnsForm, "creData", variantDnsForm, "this", "that"));

        assertTrue("Variant extension should have been initialised",
                variantsExtension.isInitialised());
        final List<IdnDomainVariant> variantList = variantsExtension.getVariants();
        assertThat("Incorrect number of variants returned", variantList.size(), is(3));
        assertThat(variantList, hasItems(new IdnDomainVariant(variantDnsForm), new IdnDomainVariant("this"),
                new IdnDomainVariant("that")));
    }

    @Test
    public void testGetNoVariantsV1_1ForCreateWithInfoInExtension() throws Exception {
        final String dnsForm = "xn--xha91b83h.com";
        final String variantDnsForm = "xn--lga31c50h.com";
        final DomainVariantResponseExtensionV1_1 variantsExtension =
                new DomainVariantResponseExtensionV1_1("create");

        variantsExtension.fromXml(getCreateResponseExpectedXml(dnsForm, "infData", variantDnsForm));

        assertFalse("Variant extension should not have been initialised",
                variantsExtension.isInitialised());
        final List<IdnDomainVariant> variantList = variantsExtension.getVariants();
        assertThat("Incorrect number of variants returned", variantList.size(), is(0));
    }

    @Test
    public void testGetNoVariantsV1_1ForInfoWithCreateInExtension() throws Exception {
        final String dnsForm = "xn--xha91b83h.com";
        final String variantDnsForm = "xn--lga31c50h.com";
        final DomainVariantResponseExtensionV1_1 variantsExtension =
                new DomainVariantResponseExtensionV1_1("info");

        variantsExtension.fromXml(getCreateResponseExpectedXml(dnsForm, "creData", variantDnsForm));

        assertFalse("Variant extension should not have been initialised",
                variantsExtension.isInitialised());
        final List<IdnDomainVariant> variantList = variantsExtension.getVariants();
        assertThat("Incorrect number of variants returned", variantList.size(), is(0));
    }

    private static String getCreateResponseExpectedXml(final String domainName,
                                                       final String tagType, final String... variantDnsForm) {
        final StringBuilder result = new StringBuilder();
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        result.append("<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\"");
        result.append(    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        result.append(    " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">");
        result.append(    "<response>");
        result.append(        "<result code=\"1000\">");
        result.append(            "<msg>Command completed successfully</msg>");
        result.append(        "</result>");
        result.append(        "<resData>");
        result.append(            "<domain:creData xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"");
        result.append(                " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">");
        result.append(                "<domain:name>" + domainName + "</domain:name>");
        result.append(                "<domain:crDate>1999-04-03T22:00:00.0Z</domain:crDate>");
        result.append(                "<domain:exDate>2001-04-03T22:00:00.0Z</domain:exDate>");
        result.append(            "</domain:creData>");
        result.append(        "</resData>");

        if (variantDnsForm != null) {
            result.append("<extension>");
            result.append("<" + tagType +" xmlns=\"urn:ar:params:xml:ns:variant-1.1\"");
            result.append(" xsi:schemaLocation=\"urn:ar:params:xml:ns:variant-1.1 variant-1.1.xsd\">");
            for (String variant : variantDnsForm) {
                result.append("<variant>" + variant + "</variant>");
            }
            result.append("</" + tagType + ">");
            result.append("</extension>");
        }

        result.append(        "<trID>");
        result.append(            "<clTRID>ABC-12345</clTRID>");
        result.append(            "<svTRID>54321-XYZ</svTRID>");
        result.append(        "</trID>");
        result.append(    "</response>");
        result.append("</epp>");
        return result.toString();
    }
}
