package ari.dnrs.rtk.addon.extensions.secdns;

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

import ari.dnrs.rtk.addon.bean.DSDataBean;
import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.bean.KeyDataBean;
import ari.dnrs.rtk.addon.bean.MaxSigLifeBean;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

public class SECDNSCreateCommmandExtensionTest {

    @Test
    public void shouldCreateCorrectXMLWhenUsingDSData() throws epp_XMLException {
        EPPDomainCreate cmd = new EPPDomainCreate();
        DNSSecDataBean createData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        createData.addToDsData(dsData);

        setupDomainCreateCommand(cmd, createData);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced",
                    getCreateXML("<dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType>"
                            + "<digest>AA</digest></dsData>"),
                xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCreateCorrectXMLWhenUsingDSDataAndKeyData() throws epp_XMLException {
        EPPDomainCreate cmd = new EPPDomainCreate();
        DNSSecDataBean createData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        KeyDataBean keyData = new KeyDataBean(1, 3, 1, "ABC");
        dsData.setKeyData(keyData);
        createData.addToDsData(dsData);

        setupDomainCreateCommand(cmd, createData);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced",
                    getCreateXML("<dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType><digest>AA</digest>"
                            + "<keyData><flags>1</flags><protocol>3</protocol><alg>1</alg><pubKey>ABC</pubKey></keyData>"
                            + "</dsData>"), xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCreateCorrectXMLWhenUsingDSDataAndKeyDataAndMaxSiglife() throws epp_XMLException {
        EPPDomainCreate cmd = new EPPDomainCreate();
        DNSSecDataBean createData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        KeyDataBean keyData = new KeyDataBean(1, 3, 1, "ABC");
        dsData.setKeyData(keyData);
        createData.addToDsData(dsData);
        MaxSigLifeBean maxSigLife = new MaxSigLifeBean(10);
        createData.setMaxSigLife(maxSigLife);

        setupDomainCreateCommand(cmd, createData);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced", 
                    getCreateXML("<maxSigLife>10</maxSigLife>" //
                    + "<dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType><digest>AA</digest>"
                    + "<keyData><flags>1</flags><protocol>3</protocol><alg>1</alg><pubKey>ABC</pubKey></keyData>"
                    + "</dsData>"), xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCreateCorrectXMLWhenUsingMultipleDSData() throws epp_XMLException {
        EPPDomainCreate cmd = new EPPDomainCreate();
        DNSSecDataBean createData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        createData.addToDsData(dsData);

        DSDataBean dsData2 = new DSDataBean();
        dsData2.setAlgorithm(1);
        dsData2.setDigest("BB");
        dsData2.setDigestType(1);
        dsData2.setKeyTag(1);
        createData.addToDsData(dsData2);

        setupDomainCreateCommand(cmd, createData);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced",
                    getCreateXML("<dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType><digest>AA</digest>"
                            + "</dsData><dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType>"
                            + "<digest>BB</digest></dsData>"), xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCreateCorrectXMLWhenUsingKeyData() {
        EPPDomainCreate cmd = new EPPDomainCreate();
        DNSSecDataBean createData = new DNSSecDataBean();
        KeyDataBean keyData = new KeyDataBean(1, 3, 1, "ABC");
        createData.addToKeyData(keyData);

        setupDomainCreateCommand(cmd, createData);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced",
                    getCreateXML("<keyData><flags>1</flags><protocol>3</protocol><alg>1</alg><pubKey>ABC</pubKey>"
                            + "</keyData>"), xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }

    private void setupDomainCreateCommand(EPPDomainCreate cmd, DNSSecDataBean createData) {
        SECDNSCreateCommandExtension secDNSExtension = new SECDNSCreateCommandExtension();
        secDNSExtension.setCreateData(createData);
        epp_Extension[] extensions = {secDNSExtension};

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

        epp_AuthInfo domainAuthInfo = new epp_AuthInfo();
        domainAuthInfo.setValue("123123");

        domainAuthInfo.setType(epp_AuthInfoType.PW);
        domainCreateRequest.setAuthInfo(domainAuthInfo);

        domainCreateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainCreateRequest);
    }

    private String getCreateXML(String extensionString) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"//
                + "<command>" //
                + "<create>"//
                + "<domain:create"//
                + " xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"" //
                + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" //
                + "<domain:name>test-domain.com.au</domain:name>"//
                + "<domain:period unit=\"y\">2</domain:period>" //
                + "<domain:ns>"//
                + "<domain:hostObj>ns1.valid.info</domain:hostObj>"
                + "<domain:hostObj>ns2.valid.info</domain:hostObj>"//
                + "</domain:ns>"//
                + "<domain:authInfo>"
                + "<domain:pw>123123</domain:pw>"//
                + "</domain:authInfo>"//
                + "</domain:create>"//
                + "</create>" //
                + "<extension>"//
                + "<create xmlns=\"urn:ietf:params:xml:ns:secDNS-1.1\">" + extensionString
                + "</create></extension><clTRID>client_trid</clTRID></command></epp>";
    }
}
