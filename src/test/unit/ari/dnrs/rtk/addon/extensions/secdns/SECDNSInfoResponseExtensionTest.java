package ari.dnrs.rtk.addon.extensions.secdns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openrtk.idl.epprtk.epp_XMLException;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

public class SECDNSInfoResponseExtensionTest {

    @Test
    public void shouldParseInfDataResponseWithOnlyDSData() throws epp_XMLException {
        SECDNSInfoResponseExtension secDNSInfoResponse = new SECDNSInfoResponseExtension();
        secDNSInfoResponse.fromXML("<infData xmlns=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + " secDNS-1.1.xsd\">"
                + "<dsData><keyTag>1</keyTag><alg>2</alg><digestType>3</digestType><digest>AA</digest></dsData>"
                + "</infData>");

        assertNotNull("No infdata returned", secDNSInfoResponse.getInfData());
        assertNotNull("No DS Data returned", secDNSInfoResponse.getInfData().getDsDataList());
        assertEquals("Should have at least one DS Data", 1, secDNSInfoResponse.getInfData().getDsDataList().size());
        assertEquals("DS Data keytag incorrect", 1, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getKeyTag());
        assertEquals("DS Data algorithm incorrect", 2, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getAlg());
        assertEquals("DS Data digestType incorrect", 3, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getDigestType());
        assertEquals("DS Data digest incorrect", "AA", secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getDigest());
    }

    @Test
    public void shouldParseInfDataResponseWithOnlyMultipleDSData() throws epp_XMLException {
        SECDNSInfoResponseExtension secDNSInfoResponse = new SECDNSInfoResponseExtension();
        secDNSInfoResponse.fromXML("<infData xmlns=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + " secDNS-1.1.xsd\">"
                + "<dsData><keyTag>1</keyTag><alg>2</alg><digestType>3</digestType><digest>AA</digest></dsData>"
                + "<dsData><keyTag>4</keyTag><alg>5</alg><digestType>6</digestType><digest>BB</digest></dsData>"
                + "</infData>");

        assertNotNull("No infdata returned", secDNSInfoResponse.getInfData());
        assertNotNull("No DS Data returned", secDNSInfoResponse.getInfData().getDsDataList());
        assertEquals("Should have two DS Data", 2, secDNSInfoResponse.getInfData().getDsDataList().size());
        assertEquals("DS Data keytag incorrect", 4, secDNSInfoResponse.getInfData().getDsDataList().get(1)
                .getKeyTag());
        assertEquals("DS Data algorithm incorrect", 5, secDNSInfoResponse.getInfData().getDsDataList().get(1)
                .getAlg());
        assertEquals("DS Data digestType incorrect", 6, secDNSInfoResponse.getInfData().getDsDataList().get(1)
                .getDigestType());
        assertEquals("DS Data digest incorrect", "BB", secDNSInfoResponse.getInfData().getDsDataList().get(1)
                .getDigest());
    }

    @Test
    public void shouldParseInfDataResponseWithOnlyDSDataIncludingKeyData() throws epp_XMLException {
        SECDNSInfoResponseExtension secDNSInfoResponse = new SECDNSInfoResponseExtension();
        secDNSInfoResponse.fromXML("<infData xmlns=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + " secDNS-1.1.xsd\">"
                + "<dsData><keyTag>1</keyTag><alg>2</alg><digestType>3</digestType><digest>AA</digest>" //
                + "<keyData><flags>1</flags><protocol>3</protocol><alg>1</alg><pubKey>ABC</pubKey></keyData>"
                + "</dsData>"
                + "</infData>");

        assertNotNull("No infdata returned", secDNSInfoResponse.getInfData());
        assertNotNull("No DS Data returned", secDNSInfoResponse.getInfData().getDsDataList());
        assertEquals("Should have at least one DS Data", 1, secDNSInfoResponse.getInfData().getDsDataList().size());
        assertEquals("DS Data keytag incorrect", 1, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getKeyTag());
        assertEquals("DS Data algorithm incorrect", 2, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getAlg());
        assertEquals("DS Data digestType incorrect", 3, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getDigestType());
        assertEquals("DS Data digest incorrect", "AA", secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getDigest());
        assertNotNull("No Key Data returned", secDNSInfoResponse.getInfData().getDsDataList().get(0).getKeyData());
    }

    @Test
    public void shouldParseInfDataResponseWithOnlyKeyData() throws epp_XMLException {
        SECDNSInfoResponseExtension secDNSInfoResponse = new SECDNSInfoResponseExtension();
        secDNSInfoResponse.fromXML("<infData xmlns=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + " secDNS-1.1.xsd\">"
                + "<keyData><flags>1</flags><protocol>2</protocol><alg>3</alg><pubKey>ABC</pubKey></keyData>"
                + "</infData>");

        assertNotNull("No infdata returned", secDNSInfoResponse.getInfData());
        assertNotNull("No Key Data returned", secDNSInfoResponse.getInfData().getKeyDataList());
        assertEquals("Should have at least one Key Data", 1, secDNSInfoResponse.getInfData().getKeyDataList().size());
        assertNull("Should have at no DS Data", secDNSInfoResponse.getInfData().getDsDataList());
        assertEquals("Key Data flags incorrect", 1, secDNSInfoResponse.getInfData().getKeyDataList().get(0)
                .getFlags());
        assertEquals("Key Data protocol incorrect", 2, secDNSInfoResponse.getInfData().getKeyDataList().get(0)
                .getProtocol());
        assertEquals("Key Data alg incorrect", 3, secDNSInfoResponse.getInfData().getKeyDataList().get(0)
                .getAlgorithm());
        assertEquals("Key Data pubKey incorrect", "ABC", secDNSInfoResponse.getInfData().getKeyDataList().get(0)
                .getPubKey());
    }

    @Test
    public void shouldParseInfDataResponseWithOnlyMultipleKeyData() throws epp_XMLException {
        SECDNSInfoResponseExtension secDNSInfoResponse = new SECDNSInfoResponseExtension();
        secDNSInfoResponse.fromXML("<infData xmlns=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + " secDNS-1.1.xsd\">"
                + "<keyData><flags>1</flags><protocol>2</protocol><alg>3</alg><pubKey>ABC</pubKey></keyData>"
                + "<keyData><flags>4</flags><protocol>5</protocol><alg>6</alg><pubKey>DEF</pubKey></keyData>"
                + "</infData>");

        assertNotNull("No infdata returned", secDNSInfoResponse.getInfData());
        assertNotNull("No Key Data returned", secDNSInfoResponse.getInfData().getKeyDataList());
        assertEquals("Should have two Key Data", 2, secDNSInfoResponse.getInfData().getKeyDataList().size());
        assertNull("Should have at no DS Data", secDNSInfoResponse.getInfData().getDsDataList());
        assertEquals("Key Data flags incorrect", 4, secDNSInfoResponse.getInfData().getKeyDataList().get(1)
                .getFlags());
        assertEquals("Key Data protocol incorrect", 5, secDNSInfoResponse.getInfData().getKeyDataList().get(1)
                .getProtocol());
        assertEquals("Key Data alg incorrect", 6, secDNSInfoResponse.getInfData().getKeyDataList().get(1)
                .getAlgorithm());
        assertEquals("Key Data pubKey incorrect", "DEF", secDNSInfoResponse.getInfData().getKeyDataList().get(1)
                .getPubKey());
    }

    @Test
    public void shouldParseInfDataResponseWithMaxSigLifeElement() throws epp_XMLException {
        SECDNSInfoResponseExtension secDNSInfoResponse = new SECDNSInfoResponseExtension();
        secDNSInfoResponse.fromXML("<infData xmlns=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.SEC_DNS_NAMESPACE + " secDNS-1.1.xsd\">"
                + "<maxSigLife>10</maxSigLife>"
                + "<dsData><keyTag>1</keyTag><alg>2</alg><digestType>3</digestType><digest>AA</digest></dsData>"
                + "</infData>");

        assertNotNull("No infdata returned", secDNSInfoResponse.getInfData());
        assertNotNull("No DS Data returned", secDNSInfoResponse.getInfData().getDsDataList());
        assertEquals("Should have at least one DS Data", 1, secDNSInfoResponse.getInfData().getDsDataList().size());
        assertEquals("DS Data keytag incorrect", 1, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getKeyTag());
        assertEquals("DS Data algorithm incorrect", 2, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getAlg());
        assertEquals("DS Data digestType incorrect", 3, secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getDigestType());
        assertEquals("DS Data digest incorrect", "AA", secDNSInfoResponse.getInfData().getDsDataList().get(0)
                .getDigest());
        assertEquals("Max Sig Life was incorrect", 10, secDNSInfoResponse.getInfData().getMaxSigLife().getMaxSigLife());
    }
}
