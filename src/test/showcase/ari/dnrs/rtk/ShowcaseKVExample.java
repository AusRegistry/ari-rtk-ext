package ari.dnrs.rtk;

import java.io.InputStream;
import java.util.Properties;

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
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateAddRemove;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

/**
 * Example code for ARI's KV list extension for EPP using domain create and
 * domain update to demonstrate its usage. <br />
 * 
 * INPUT::
 * <ol>
 * <li>create | update has to be provided to the class as command line argument
 * to choose the corresponding operation for domain with KV extension.</li>
 * <li>All the relevant input for the request and EPPD details should be
 * provided in the properties file domain-create-kv-extension.properties or
 * domain-update-kv-extension.properties depending on the request to be sent.</li>
 * <ul>
 * <li>EPPD details - The EPP host name, EPP host port number, EPP client ID,
 * EPP password have to be provided in the same properties file.</li>
 * </ul>
 * <ul>
 * <li>Property file locations - The location of ssl.properties file and
 * rtk.properties file have to be provided as input.</li>
 * </ul>
 * </ol>
 * 
 */
public class ShowcaseKVExample {

    private static String rtkPassword;
    private static String techContact;
    private static String registrantName;
    private static String domainName;

    private static final String USAGE = "Usage: ari.dnrs.rtk.addon.examples.ShowcaseKVExample create|update";
    private static DomainKVCommandExtension kvExtension;

    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final String command = args[0];
        if (!(command.equals("create") || command.equals("update"))) {
            System.err.println(USAGE);
            System.exit(1);
        }
        final Properties classProperties = new Properties();

        InputStream resourceAsStream = null;
        try {
            resourceAsStream = ShowcaseKVExample.class.getClassLoader().getResourceAsStream(
                    "domain-" + command + "-kv-extension.properties");
            classProperties.load(resourceAsStream);
        } finally {
            if (resourceAsStream != null) {
                resourceAsStream.close();
            }
        }

        System.setProperty("ssl.props.location", classProperties.getProperty("ssl.props.location"));
        System.setProperty("rtk.props.file", classProperties.getProperty("rtk.props.file"));

        // Set up epp properties
        final String eppHostName = classProperties.getProperty("epp.server.host");
        final String eppHostPortString = classProperties.getProperty("epp.server.port");
        final String eppClientID = classProperties.getProperty("epp.client.id");
        final String eppPassword = classProperties.getProperty("epp.client.password");

        final int eppHostPort = Integer.parseInt(eppHostPortString);

        // Set up the EPP client with host name, host port, EPP client ID and
        // EPP password (the values provided in the properties file).
        final EPPClient eppClient = new EPPClient(eppHostName, eppHostPort, eppClientID, eppPassword);
        final String localeLanguage = "en";
        eppClient.setLang(localeLanguage);

        // Set service extensions to the EPP client at login that would be used
        // in this session.
        eppClient.setEPPServiceExtensions(new String[] {XMLNamespaces.KVLIST_NAMESPACE});

        eppClient.connectAndGetGreeting();
        eppClient.login(eppClientID);

        // Set up the EPP command object.
        final epp_Command commandData = new epp_Command();
        commandData.setClientTrid("RTKUTEST." + System.currentTimeMillis());

        domainName = classProperties.getProperty("domain.name");
        techContact = classProperties.getProperty("tech.contact");

        kvExtension = new DomainKVCommandExtension(command);
        // Set up Key Value extension attributes with properties values
        final int listLimit = Integer.parseInt(classProperties.getProperty("list.count"));

        for (int listCount = 0; listCount < listLimit; listCount++) {
            final String keyValueList = classProperties.getProperty("kvList" + (listCount + 1));
            final String[] kvTokens = keyValueList.split(",");

            final String listName = kvTokens[0];

            for (int itemCount = 1; itemCount < kvTokens.length; itemCount++) {
                final String key = kvTokens[itemCount];
                final String value = kvTokens[++itemCount];

                kvExtension.addKeyValuePairToList(listName, key, value);
            }
        }

        if ("create".equals(command)) {
            executeDomainCreate(classProperties, eppClient, commandData);
        } else if ("update".equals(command)) {
            executeDomainUpdate(classProperties, eppClient, commandData);
        }

        // Close the EPP session.
        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }

    private static void executeDomainUpdate(Properties classProperties, EPPClient eppClient, epp_Command commandData)
            throws epp_XMLException, epp_Exception {
        // Set up the Domain Update Request data to be used by the EPP command.
        final epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        domainUpdateRequest.setCmd(commandData);

        final epp_DomainUpdateAddRemove domainContactsToAdd = new epp_DomainUpdateAddRemove();
        final epp_DomainContact[] domainContacts = new epp_DomainContact[] {new epp_DomainContact(
                epp_DomainContactType.TECH, techContact)};
        domainContactsToAdd.setContacts(domainContacts);

        domainUpdateRequest.setAdd(domainContactsToAdd);
        domainUpdateRequest.setName(domainName);

        // Set the Domain Update request to a Domain Update EPP Command
        EPPDomainUpdate domainUpdate = new EPPDomainUpdate();
        domainUpdate.setRequestData(domainUpdateRequest);

        // Set the extension to the Domain Create request
        final epp_Extension[] extensions = {kvExtension};
        domainUpdateRequest.getCmd().setExtensions(extensions);

        /*
         * The EPPClient is asked here to process the request with the extension
         * and retrieve a response from the server.
         */
        try {
            domainUpdate = (EPPDomainUpdate) eppClient.processAction(domainUpdate);
        } catch (final epp_Exception e) {
            final epp_Result eppResultDetails = e.getDetails()[0];
            final String reason = (eppResultDetails.getExtValues())[0].getReason();

            System.err.println("An exception occured while processing the request");
            System.err.println("OPERATION:: DOMAIN UPDATE");
            System.err.println("ERROR CODE:: " + eppResultDetails.getCode());
            System.err.println("ERROR MESSAGE:: " + eppResultDetails.getMsg());
            System.err.println("ERROR REASON:: " + reason);
            throw e;
        }

        final epp_DomainUpdateRsp domainUpdateResponse = domainUpdate.getResponseData();
        final epp_Response response = domainUpdateResponse.getRsp();
        final epp_Result[] results = response.getResults();

        System.out.println("Domain Update results: [" + results[0].getCode() + "] [" + results[0].getMsg() + "]");

    }

    private static void executeDomainCreate(final Properties classProperties, final EPPClient eppClient,
            final epp_Command commandData) throws epp_XMLException, epp_Exception {
        // Set up Domain Create operation attributes with properties values
        rtkPassword = classProperties.getProperty("domain.password");
        registrantName = classProperties.getProperty("registrant.contact");

        final epp_DomainCreateReq domainCreateRequest = new epp_DomainCreateReq();
        domainCreateRequest.setCmd(commandData);
        domainCreateRequest.setName(domainName);
        domainCreateRequest.setRegistrant(registrantName);
        domainCreateRequest.setContacts(new epp_DomainContact[] {new epp_DomainContact(epp_DomainContactType.TECH,
                techContact)});
        domainCreateRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", rtkPassword));

        // Set the extension to the Domain Create request
        final epp_Extension[] extensions = {kvExtension};
        domainCreateRequest.getCmd().setExtensions(extensions);

        // Set the Domain Create request to a Domain Create EPP Command
        EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        /*
         * The EPPClient is asked here to process the request with the extension
         * and retrieve a response from the server.
         */
        try {
            domainCreate = (EPPDomainCreate) eppClient.processAction(domainCreate);
        } catch (final epp_Exception e) {
            final epp_Result eppResultDetails = e.getDetails()[0];
            final String reason = (eppResultDetails.getExtValues())[0].getReason();

            System.err.println("An exception occured while processing the request");
            System.err.println("OPERATION:: DOMAIN CREATE");
            System.err.println("ERROR CODE:: " + eppResultDetails.getCode());
            System.err.println("ERROR MESSAGE:: " + eppResultDetails.getMsg());
            System.err.println("ERROR REASON:: " + reason);

            throw e;
        }

        final epp_DomainCreateRsp domainCreateResponse = domainCreate.getResponseData();
        final epp_Response response = domainCreateResponse.getRsp();
        final epp_Result[] results = response.getResults();

        System.out.println("Domain Create results: [" + results[0].getCode() + "] [" + results[0].getMsg() + "]");
    }

}
