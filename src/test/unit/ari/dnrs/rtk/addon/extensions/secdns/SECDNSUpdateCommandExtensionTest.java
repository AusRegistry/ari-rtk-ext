package ari.dnrs.rtk.addon.extensions.secdns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;

import ari.dnrs.rtk.addon.bean.ChangeElementBean;
import ari.dnrs.rtk.addon.bean.DSDataBean;
import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.bean.KeyDataBean;
import ari.dnrs.rtk.addon.bean.MaxSigLifeBean;
import ari.dnrs.rtk.addon.bean.RemoveElementBean;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

public class SECDNSUpdateCommandExtensionTest {

    @Test
    public void shouldUpdateCorrectXMLWhenAddingDSData() throws epp_XMLException {
        EPPDomainUpdate cmd = new EPPDomainUpdate();
        SECDNSUpdateCommandExtension secDNSExtension = new SECDNSUpdateCommandExtension();
        DNSSecDataBean addData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        addData.addToDsData(dsData);

        secDNSExtension.setAddData(addData);

        epp_Extension[] extensions = {secDNSExtension};

        epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        epp_Command command_data = new epp_Command();
        command_data.setClientTrid("client_trid");
        domainUpdateRequest.setCmd(command_data);

        domainUpdateRequest.setName("test-domain.com.au");

        domainUpdateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainUpdateRequest);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"//
                    + "<command>" //
                    + "<update>"//
                    + "<domain:update"//
                    + " xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"" //
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" //
                    + "<domain:name>test-domain.com.au</domain:name>"//
                    + "</domain:update>"//
                    + "</update>" //
                    + "<extension>"//
                    + "<update xmlns=\"urn:ietf:params:xml:ns:secDNS-1.1\">"
                    + "<add>" //
                    + "<dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType><digest>AA</digest></dsData>"
                    + "</add>" //
                    + "</update></extension><clTRID>client_trid</clTRID></command></epp>", xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldUpdateCorrectXMLWhenAddingDsAndKeyData() throws epp_XMLException {
        EPPDomainUpdate cmd = new EPPDomainUpdate();
        SECDNSUpdateCommandExtension secDNSExtension = new SECDNSUpdateCommandExtension();
        DNSSecDataBean addData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        KeyDataBean keyData = new KeyDataBean(1, 3, 1, "ABC");
        dsData.setKeyData(keyData);
        dsData.setKeyData(keyData);
        addData.addToDsData(dsData);

        secDNSExtension.setAddData(addData);
        secDNSExtension.setUrgent(true);

        epp_Extension[] extensions = {secDNSExtension};

        epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        epp_Command command_data = new epp_Command();
        command_data.setClientTrid("client_trid");
        domainUpdateRequest.setCmd(command_data);

        domainUpdateRequest.setName("test-domain.com.au");
        domainUpdateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainUpdateRequest);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"//
                    + "<command>" //
                    + "<update>"//
                    + "<domain:update"//
                    + " xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"" //
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" //
                    + "<domain:name>test-domain.com.au</domain:name>"//
                    + "</domain:update>"//
                    + "</update>" //
                    + "<extension>"//
                    + "<update urgent=\"true\" xmlns=\"urn:ietf:params:xml:ns:secDNS-1.1\">"
                    + "<add>" //
                    + "<dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType><digest>AA</digest>"
                    + "<keyData><flags>1</flags><protocol>3</protocol><alg>1</alg><pubKey>ABC</pubKey></keyData>"
                    + "</dsData></add>" //
                    + "</update></extension><clTRID>client_trid</clTRID></command></epp>", xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldUpdateCorrectXMLWhenAddingKeyData() {
        EPPDomainUpdate cmd = new EPPDomainUpdate();
        SECDNSUpdateCommandExtension secDNSExtension = new SECDNSUpdateCommandExtension();
        DNSSecDataBean addData = new DNSSecDataBean();
        KeyDataBean keyData = new KeyDataBean(1, 3, 1, "ABC");
        addData.addToKeyData(keyData);

        secDNSExtension.setAddData(addData);

        epp_Extension[] extensions = {secDNSExtension};

        epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        epp_Command command_data = new epp_Command();
        command_data.setClientTrid("client_trid");
        domainUpdateRequest.setCmd(command_data);

        domainUpdateRequest.setName("test-domain.com.au");
        domainUpdateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainUpdateRequest);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"//
                    + "<command>" //
                    + "<update>"//
                    + "<domain:update"//
                    + " xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"" //
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" //
                    + "<domain:name>test-domain.com.au</domain:name>"//
                    + "</domain:update>"//
                    + "</update>" //
                    + "<extension>"//
                    + "<update xmlns=\"urn:ietf:params:xml:ns:secDNS-1.1\">"
                    + "<add>" //
                    + "<keyData><flags>1</flags><protocol>3</protocol><alg>1</alg><pubKey>ABC</pubKey></keyData>"
                    + "</add>" //
                    + "</update></extension><clTRID>client_trid</clTRID></command></epp>", xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldUpdateCorrectXMLWhenRemovingDsAndKeyData() throws epp_XMLException {
        EPPDomainUpdate cmd = new EPPDomainUpdate();
        SECDNSUpdateCommandExtension secDNSExtension = new SECDNSUpdateCommandExtension();
        DNSSecDataBean addData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        KeyDataBean keyData = new KeyDataBean(1, 3, 1, "ABC");
        dsData.setKeyData(keyData);
        dsData.setKeyData(keyData);
        addData.addToDsData(dsData);

        RemoveElementBean remType = new RemoveElementBean();
        remType.addToDsData(dsData);
        secDNSExtension.setRemData(remType);

        epp_Extension[] extensions = {secDNSExtension};

        epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        epp_Command command_data = new epp_Command();
        command_data.setClientTrid("client_trid");
        domainUpdateRequest.setCmd(command_data);

        domainUpdateRequest.setName("test-domain.com.au");
        domainUpdateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainUpdateRequest);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"//
                    + "<command>" //
                    + "<update>"//
                    + "<domain:update"//
                    + " xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"" //
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" //
                    + "<domain:name>test-domain.com.au</domain:name>"//
                    + "</domain:update>"//
                    + "</update>" //
                    + "<extension>"//
                    + "<update xmlns=\"urn:ietf:params:xml:ns:secDNS-1.1\">"
                    + "<rem>" //
                    + "<dsData><keyTag>1</keyTag><alg>1</alg><digestType>1</digestType><digest>AA</digest>"
                    + "<keyData><flags>1</flags><protocol>3</protocol><alg>1</alg><pubKey>ABC</pubKey></keyData>"
                    + "</dsData></rem>" //
                    + "</update></extension><clTRID>client_trid</clTRID></command></epp>", xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldUpdateCorrectXMLWhenRemovingAll() {
        EPPDomainUpdate cmd = new EPPDomainUpdate();
        SECDNSUpdateCommandExtension secDNSExtension = new SECDNSUpdateCommandExtension();

        RemoveElementBean remType = new RemoveElementBean();
        remType.setRemoveAll(true);
        secDNSExtension.setRemData(remType);

        epp_Extension[] extensions = {secDNSExtension};

        epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        epp_Command command_data = new epp_Command();
        command_data.setClientTrid("client_trid");
        domainUpdateRequest.setCmd(command_data);

        domainUpdateRequest.setName("test-domain.com.au");
        domainUpdateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainUpdateRequest);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"//
                    + "<command>" //
                    + "<update>"//
                    + "<domain:update"//
                    + " xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"" //
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" //
                    + "<domain:name>test-domain.com.au</domain:name>"//
                    + "</domain:update>"//
                    + "</update>" //
                    + "<extension>"//
                    + "<update xmlns=\"urn:ietf:params:xml:ns:secDNS-1.1\">"
                    + "<rem><all>true</all></rem>" //
                    + "</update></extension><clTRID>client_trid</clTRID></command></epp>", xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldUpdateCorrectXMLWhenChangingDSData() throws epp_XMLException {
        EPPDomainUpdate cmd = new EPPDomainUpdate();
        SECDNSUpdateCommandExtension secDNSExtension = new SECDNSUpdateCommandExtension();

        DNSSecDataBean chgData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest("AA");
        dsData.setDigestType(1);
        dsData.setKeyTag(1);
        KeyDataBean keyData = new KeyDataBean(1, 3, 1, "ABC");
        dsData.setKeyData(keyData);
        dsData.setKeyData(keyData);
        chgData.addToDsData(dsData);

        ChangeElementBean chgType = new ChangeElementBean();
        MaxSigLifeBean maxSigLife = new MaxSigLifeBean(10);
        chgType.setMaxSigLife(maxSigLife);
        secDNSExtension.setChgData(chgType);

        epp_Extension[] extensions = {secDNSExtension};

        epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        epp_Command command_data = new epp_Command();
        command_data.setClientTrid("client_trid");
        domainUpdateRequest.setCmd(command_data);

        domainUpdateRequest.setName("test-domain.com.au");
        domainUpdateRequest.getCmd().setExtensions(extensions);

        cmd.setRequestData(domainUpdateRequest);

        try {
            final String xml = cmd.toXML();
            assertEquals("Incorrect XML produced", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"//
                    + "<command>" //
                    + "<update>"//
                    + "<domain:update"//
                    + " xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\"" //
                    + " xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">" //
                    + "<domain:name>test-domain.com.au</domain:name>"//
                    + "</domain:update>"//
                    + "</update>" //
                    + "<extension>"//
                    + "<update xmlns=\"urn:ietf:params:xml:ns:secDNS-1.1\">"
                    + "<chg><maxSigLife>10</maxSigLife></chg>" //
                    + "</update></extension><clTRID>client_trid</clTRID></command></epp>", xml);
        } catch (epp_XMLException e) {
            fail(e.getMessage());
        }
    }
}
