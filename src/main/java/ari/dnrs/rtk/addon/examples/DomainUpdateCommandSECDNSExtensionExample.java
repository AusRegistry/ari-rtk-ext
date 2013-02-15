package ari.dnrs.rtk.addon.examples;

import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Exception;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.bean.DSDataBean;
import ari.dnrs.rtk.addon.extensions.secdns.SECDNSUpdateCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

public class DomainUpdateCommandSECDNSExtensionExample {

    private static String USAGE = "Usage: ari.dnrs.rtk.addon.examples.DomainUpdateCommandSECDNSExtensionExample"
            + " epp_host_name epp_host_port epp_client_id epp_password" + " dns_form auth_info dsData_digest";

    private static EPPClient eppClient;
    private static epp_Command commandData;
    private static String clientTrid;

    public static void main(final String[] args) throws Exception {
        if (args.length != 6) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final String eppHostName = args[0];
        final String eppHostPortString = args[1];
        final String eppClientID = args[2];
        final String password = args[3];
        final String dnsForm = args[4];
        final String dsDataDigest = args[5];

        int eppHostPort = Integer.parseInt(eppHostPortString);

        // Establish the connection to the EPP server
        eppClient = new EPPClient(eppHostName, eppHostPort, eppClientID, password);

        eppClient.setLang("en");

        // Add the extension name space to tell the EPP server what additional
        // extensions the client supports
        eppClient.setEPPServiceExtensions(new String[] {XMLNamespaces.SEC_DNS_NAMESPACE});

        eppClient.connectAndGetGreeting();

        eppClient.login(eppClientID);

        clientTrid = "ABC:" + eppClientID + ":" + System.currentTimeMillis();

        commandData = new epp_Command();
        commandData.setClientTrid(clientTrid);

        executeDomainUpdate(dnsForm, dsDataDigest);

        // Disconnect from the EPP server
        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }

    private static void executeDomainUpdate(final String dnsForm, final String dsDataDigest) throws epp_XMLException,
            epp_Exception {
        // Update the SECDNS extension and set the DS data
        SECDNSUpdateCommandExtension secDNSExt = new SECDNSUpdateCommandExtension();

        DNSSecDataBean addData = new DNSSecDataBean();
        DSDataBean dsData = new DSDataBean();
        dsData.setAlgorithm(1);
        dsData.setDigest(dsDataDigest);
        dsData.setDigestType(1);
        dsData.setKeyTag(1);

        addData.addToDsData(dsData);

        secDNSExt.setAddData(addData);

        // Update the DomainUpdateRequest with the extension
        EPPDomainUpdate domainUpdate = updateDomain(secDNSExt, dnsForm);

        // Process the response
        final epp_DomainUpdateRsp domainUpdateResponse = domainUpdate.getResponseData();

        final epp_Response response = domainUpdateResponse.getRsp();
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
            System.out.println("Domain Name " + dnsForm + " updated with DS data with digest: " + dsDataDigest);
        }
    }

    private static EPPDomainUpdate updateDomain(final SECDNSUpdateCommandExtension secDNSUpdateExt,
            final String dnsForm) throws epp_XMLException, epp_Exception {
        final epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        domainUpdateRequest.setCmd(commandData);
        domainUpdateRequest.setName(dnsForm);

        // Add the extension to the Domain Update command
        final epp_Extension[] extensions = {secDNSUpdateExt};
        domainUpdateRequest.getCmd().setExtensions(extensions);

        EPPDomainUpdate domainUpdate = new EPPDomainUpdate();
        domainUpdate.setRequestData(domainUpdateRequest);

        // Send the request to the server
        return (EPPDomainUpdate) eppClient.processAction(domainUpdate);
    }
}
