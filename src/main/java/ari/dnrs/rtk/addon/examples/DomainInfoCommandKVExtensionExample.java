package ari.dnrs.rtk.addon.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;

import ari.dnrs.rtk.addon.bean.DomainKeyValueBean;
import ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;

public class DomainInfoCommandKVExtensionExample {

    private static String USAGE = "Usage: ari.dnrs.rtk.addon.examples.DomainInfoCommandKVExtensionExample"
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

        final int eppHostPort = Integer.parseInt(eppHostPortString);

        eppClient = new EPPClient(eppHostName, eppHostPort, eppClientID, password);

        eppClient.setLang("en");

        // Add the extension name space to tell the EPP server what additional extensions the client supports
        eppClient.setEPPServiceExtensions(new String[] { XMLNamespaces.KVLIST_NAMESPACE });

        eppClient.connectAndGetGreeting();

        eppClient.login(eppClientID);

        clientTrid = "ABC:" + eppClientID + ":" + System.currentTimeMillis();

        commandData = new epp_Command();
        commandData.setClientTrid(clientTrid);

        final epp_DomainInfoReq domainInfoRequest = new epp_DomainInfoReq();

        domainInfoRequest.setCmd(commandData);
        domainInfoRequest.setName(dnsForm);

        EPPDomainInfo domainInfo = new EPPDomainInfo();
        domainInfo.setRequestData(domainInfoRequest);

        domainInfo = (EPPDomainInfo) eppClient.processAction(domainInfo);

        final epp_DomainInfoRsp domain_info_response = domainInfo.getResponseData();
        final epp_Response response = domain_info_response.getRsp();

        //Retrieve extension elements from response
        final String[] extensionStrings = response.getExtensionStrings();
        if (extensionStrings != null && extensionStrings.length > 0 && extensionStrings[0].length() > 0) {

            //Retrieve KV data from extension string
            final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("info");
            kvExtension.fromXML(extensionStrings[0]);

            //Output KV extension data for verification
            final HashMap<String, ArrayList<DomainKeyValueBean>> keyValueLists = kvExtension.getKeyValueLists();
            for (final Map.Entry<String, ArrayList<DomainKeyValueBean>> list : keyValueLists.entrySet()) {
                final String listName = list.getKey();
                System.out.println("List Name: " + listName);

                final ArrayList<DomainKeyValueBean> keyValueList = list.getValue();
                for (final DomainKeyValueBean keyValue : keyValueList) {
                    System.out.println("Key: " + keyValue.getKey());
                    System.out.println("Value: " + keyValue.getValue());
                }
            }
        } else {
            System.err.println("Failed to return extension data");
        }

        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }
}
