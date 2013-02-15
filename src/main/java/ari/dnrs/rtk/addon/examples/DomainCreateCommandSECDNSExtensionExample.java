package ari.dnrs.rtk.addon.examples;

import org.openrtk.idl.epprtk.epp_AuthInfo;
import org.openrtk.idl.epprtk.epp_AuthInfoType;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Exception;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainContact;
import org.openrtk.idl.epprtk.domain.epp_DomainContactType;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainCreateRsp;

import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.bean.DSDataBean;
import ari.dnrs.rtk.addon.extensions.secdns.SECDNSCreateCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;

public class DomainCreateCommandSECDNSExtensionExample {

    private static String USAGE = "Usage: ari.dnrs.rtk.addon.examples.DomainCreateCommandSECDNSExtensionExample"
            + " epp_host_name epp_host_port epp_client_id epp_password"
            + " dns_form auth_info registrant_contact technical_contact dsData_digest";

    private static EPPClient eppClient;
    private static epp_Command commandData;
    private static String clientTrid;

    public static void main(final String[] args) throws Exception {

        if (args.length != 9) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final String eppHostName = args[0];
        final String eppHostPortString = args[1];
        final String eppClientID = args[2];
        final String password = args[3];
        final String dnsForm = args[4];
        final String authInfo = args[5];
        final String registrantContact = args[6];
        final String technicalContact = args[7];
        final String dsDataDigest = args[8];

        int eppHostPort = Integer.parseInt(eppHostPortString);

        // Establish the connection to the EPP server

        eppClient = new EPPClient(eppHostName, eppHostPort, eppClientID, password);

        eppClient.setLang("en");

        // Add the extension name space to tell the EPP server what additional extensions the client supports
        eppClient.setEPPServiceExtensions(new String[] { XMLNamespaces.SEC_DNS_NAMESPACE });

        eppClient.connectAndGetGreeting();

        eppClient.login(eppClientID);

        clientTrid = "ABC:" + eppClientID + ":" + System.currentTimeMillis();

        commandData = new epp_Command();
        commandData.setClientTrid(clientTrid);

        executeDomainCreate(dnsForm, dsDataDigest, authInfo, registrantContact, technicalContact);

        // Disconnect from the EPP server

        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }

    private static void executeDomainCreate(final String dnsForm, final String dsDataDigest, final String authInfo,
            final String registrantContact, final String technicalContact) throws epp_XMLException, epp_Exception {

        // Create the SECDNS extension and set the DS data
        SECDNSCreateCommandExtension secDNSExt = new SECDNSCreateCommandExtension();

        DNSSecDataBean createData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest(dsDataDigest);
        dsData.setDigestType(1);
        dsData.setKeyTag(1);

        createData.addToDsData(dsData);

        secDNSExt.setCreateData(createData);

        // Create the DomainCreateRequest with the extension
        EPPDomainCreate domainCreate = createDomain(secDNSExt, dnsForm, authInfo, registrantContact, technicalContact);

        // Process the response
        final epp_DomainCreateRsp domainCreateResponse = domainCreate.getResponseData();

        final epp_Response response = domainCreateResponse.getRsp();
        final epp_Result[] results = response.getResults();

        // Verify the result was successful or successful pending action
        if (results[0].getCode() != 1000 && results[0].getCode() != 1001) {
            String resultExtendedReason = null;
            if (results[0].getExtValues() != null) {
                resultExtendedReason = results[0].getExtValues()[0].getReason();
                System.err.println("Failed to execute command due to extend reason: " + resultExtendedReason);
                return;
            }
        } else {
            System.out.println("Domain Name " + dnsForm + " created.");
        }
    }

    private static EPPDomainCreate createDomain(final SECDNSCreateCommandExtension secDNSExt,
            final String dnsForm, final String authInfo, final String registrantContact, final String technicalContact)
            throws epp_XMLException, epp_Exception {
        final epp_DomainCreateReq domainCreateRequest = new epp_DomainCreateReq();
        domainCreateRequest.setCmd(commandData);
        domainCreateRequest.setName(dnsForm);
        domainCreateRequest.setRegistrant(registrantContact);
        domainCreateRequest.setContacts(new epp_DomainContact[] { new epp_DomainContact(epp_DomainContactType.TECH,
                technicalContact) });
        domainCreateRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", authInfo));

        // Add the extension to the Domain Create command
        final epp_Extension[] extensions = { secDNSExt };
        domainCreateRequest.getCmd().setExtensions(extensions);

        EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        // Send the request to the server
        return (EPPDomainCreate) eppClient.processAction(domainCreate);
    }

}
