package ari.dnrs.rtk.addon.examples;

import java.util.List;

import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;

import ari.dnrs.rtk.addon.bean.DSDataBean;
import ari.dnrs.rtk.addon.bean.KeyDataBean;
import ari.dnrs.rtk.addon.extensions.secdns.SECDNSInfoResponseExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;

public class DomainInfoCommandSECDNSExtensionExample {

    private static String USAGE = "Usage: ari.dnrs.rtk.addon.examples.DomainInfoCommandIdnaExtensionExample"
            + " epp_host_name epp_host_port epp_client_id epp_password" + " dns_form";

    private static EPPClient eppClient;

    private static epp_Command commandData;

    private static String clientTrid;

    public static void main(final String[] args) throws Exception {

        if (args.length != 5) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final String eppHostName = args[0];
        final String eppHostPortString = args[1];
        final String eppClientID = args[2];
        final String password = args[3];
        final String dnsForm = args[4];

        int eppHostPort = Integer.parseInt(eppHostPortString);

        eppClient = new EPPClient(eppHostName, eppHostPort, eppClientID, password);

        eppClient.setLang("en");

        // Add the extension name space to tell the EPP server what additional extensions the client supports
        eppClient.setEPPServiceExtensions(new String[] { XMLNamespaces.SEC_DNS_NAMESPACE });

        eppClient.connectAndGetGreeting();

        eppClient.login(eppClientID);

        clientTrid = "ABC:" + eppClientID + ":" + System.currentTimeMillis();

        commandData = new epp_Command();
        commandData.setClientTrid(clientTrid);

        epp_DomainInfoReq domainInfoRequest = new epp_DomainInfoReq();

        domainInfoRequest.setCmd(commandData);
        domainInfoRequest.setName(dnsForm);

        EPPDomainInfo domainInfo = new EPPDomainInfo();
        domainInfo.setRequestData(domainInfoRequest);

        domainInfo = (EPPDomainInfo) eppClient.processAction(domainInfo);

        final epp_DomainInfoRsp domain_info_response = domainInfo.getResponseData();
        final epp_Response response = domain_info_response.getRsp();

        final String[] extensionStrings = response.getExtensionStrings();
        if (extensionStrings != null && extensionStrings.length > 0 && extensionStrings[0].length() > 0) {

            // Assume there is only one extension returned and is the IDNA extension
            SECDNSInfoResponseExtension secDnsDomainInfoResponse = new SECDNSInfoResponseExtension();
            secDnsDomainInfoResponse.fromXML(extensionStrings[0]);

            List<DSDataBean> dsDataList = secDnsDomainInfoResponse.getInfData().getDsDataList();
            if (dsDataList != null) {
                for (int i = 0; i < dsDataList.size(); i++) {
                    DSDataBean dsData = dsDataList.get(i);
                    System.out.println("DS Data Algorithm: " + dsData.getAlg());
                    System.out.println("DS Data Digest: " + dsData.getDigest());
                    System.out.println("DS Data Digest Type: " + dsData.getDigestType());
                    System.out.println("DS Data Key Tag: " + dsData.getKeyTag());
                    KeyDataBean keyData = dsData.getKeyData();
                    if (keyData != null) {
                        System.out.println("Key Data Algorithm: " + keyData.getAlgorithm());
                        System.out.println("Key Data Flags: " + keyData.getFlags());
                        System.out.println("Key Data Protocol: " + keyData.getProtocol());
                        System.out.println("Key Data Public Key: " + keyData.getPubKey());
                    }
                }
            }
            List<KeyDataBean> keyDataList = secDnsDomainInfoResponse.getInfData().getKeyDataList();
            if (keyDataList != null) {
                for (int i = 0; i < keyDataList.size(); i++) {
                    KeyDataBean keyData = keyDataList.get(i);
                    System.out.println("Key Data Algorithm: " + keyData.getAlgorithm());
                    System.out.println("Key Data Flags: " + keyData.getFlags());
                    System.out.println("Key Data Protocol: " + keyData.getProtocol());
                    System.out.println("Key Data Public Key: " + keyData.getPubKey());
                }
            }
        } else {
            System.err.println("Failed to return extension data");
        }

        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }
}
