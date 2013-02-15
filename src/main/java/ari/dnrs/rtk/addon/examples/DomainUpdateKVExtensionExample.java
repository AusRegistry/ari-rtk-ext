package ari.dnrs.rtk.addon.examples;

import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.domain.epp_DomainContact;
import org.openrtk.idl.epprtk.domain.epp_DomainContactType;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateAddRemove;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

/**
 * Example code for ARI's KV list extension for EPP using domain update to demonstrate its usage.
 */
public class DomainUpdateKVExtensionExample {

    private static final String DOMAIN_TO_UPDATE = "domainName.com";
    private static final String USAGE = "Usage: ari.dnrs.rtk.addon.examples.DomainUpdateKVExtensionExample epp_host_name epp_host_port epp_client_id epp_password";

    /**
     * @param args
     *            EPP host name, EPP host port, EPP client identifier, EPP client password
     * @throws Exception
     *             if any exception occurs during the example.
     */
    public static void main(final String[] args) throws Exception {

        if (args.length != 4) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final String eppHostName = args[0];
        final String eppHostPortString = args[1];
        final String eppClientID = args[2];
        final String eppPassword = args[3];

        final int eppHostPort = Integer.parseInt(eppHostPortString);

        /*
         * Set up the EPP client with host name, host port, EPP client ID and EPP password (the values provided as
         * arguments).
         */
        final EPPClient eppClient = new EPPClient(eppHostName, eppHostPort, eppClientID, eppPassword);
        final String localeLanguage = "en";
        eppClient.setLang(localeLanguage);

        // Set the key value service extension to be used by the client at login
        eppClient.setEPPServiceExtensions(new String[] { XMLNamespaces.KVLIST_NAMESPACE });

        eppClient.connectAndGetGreeting();
        eppClient.login(eppClientID);

        // Set up the EPP command object.
        final epp_Command commandData = new epp_Command();
        commandData.setClientTrid("RTKUTEST.20120601.151901.0");

        // Set up the Domain Update Request data to be used by the EPP command.
        final epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        domainUpdateRequest.setCmd(commandData);

        final epp_DomainUpdateAddRemove domainContactsToAdd = new epp_DomainUpdateAddRemove();
        final epp_DomainContact[] domainContacts = new epp_DomainContact[] { new epp_DomainContact(
                epp_DomainContactType.ADMIN, "adminID") };
        domainContactsToAdd.setContacts(domainContacts);

        domainUpdateRequest.setAdd(domainContactsToAdd);
        domainUpdateRequest.setName(DOMAIN_TO_UPDATE);

        /*
         * Key Value Extension for Domain Update: Set up the Key Value extension data to be included in the extension
         * element of the Domain Update Request.
         */
        final String kvList1Name = "ae";
        final String kvList2Name = "ru";
        final String listItem1Key = "eligibilityType";
        final String listItem1Value = "Trademark";
        final String listItem2Key = "policyReason";
        final String listItem2Value = "1";
        final String listItem3Key = "registrantName";
        final String listItem3Value = "RTK";

        final DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("update");
        kvExtension.addKeyValuePairToList(kvList1Name, listItem1Key, listItem1Value);
        kvExtension.addKeyValuePairToList(kvList1Name, listItem2Key, listItem2Value);
        kvExtension.addKeyValuePairToList(kvList1Name, listItem3Key, listItem3Value);

        // More than a single key value list can be provided to the extension
        kvExtension.addKeyValuePairToList(kvList2Name, listItem1Key, listItem1Value);
        kvExtension.addKeyValuePairToList(kvList2Name, listItem2Key, listItem2Value);
        kvExtension.addKeyValuePairToList(kvList2Name, listItem3Key, listItem3Value);

        // Set the extension to the Domain Update request
        final epp_Extension[] extensions = { kvExtension };
        domainUpdateRequest.getCmd().setExtensions(extensions);

        // Set the Domain Update request to a Domain Update EPP Command
        EPPDomainUpdate domainUpdate = new EPPDomainUpdate();
        domainUpdate.setRequestData(domainUpdateRequest);

        /*
         * The EPPClient is asked here to process the request with the extension and retrieve a response from the
         * server.
         */
        domainUpdate = (EPPDomainUpdate) eppClient.processAction(domainUpdate);

        final epp_DomainUpdateRsp domainUpdateResponse = domainUpdate.getResponseData();
        final epp_Response response = domainUpdateResponse.getRsp();
        final epp_Result[] results = response.getResults();

        System.out.println("Domain Update results: [" + results[0].getCode() + "] [" + results[0].getMsg() + "]");

        // Close the EPP session.
        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }

}
