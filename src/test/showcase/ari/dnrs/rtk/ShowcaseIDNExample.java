package ari.dnrs.rtk;

import java.io.InputStream;
import java.util.Arrays;
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
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;
import org.openrtk.idl.epprtk.domain.epp_DomainStatus;

import ari.dnrs.rtk.addon.extensions.idn.DomainIdnCommandExtension;
import ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;

/**
 * Example code for ARI's IDN extension for EPP using domain create, and domain info 
 * commands to demonstrate its usage. <br />
 * Extensions supported in this example - 
 * IDN extension for domain create and domain info. <br />
 * 
 * INPUT:: 
 * <ol>
 * <li>Command -  Either one of create | info has to be provided to the class as command line
 * argument to choose the corresponding operation for domain with IDN
 * extension. </li>
 * <li> All the other relevant input for the request, EPPD details and the location of rtk.properties 
 * and ssl.properties files should be provided in one of the properties files ari-extension-examples.properties 
 * depending on the request to be sent. </li>
 * <ul><li>EPPD details - The EPP host name, EPP host port number, EPP client ID, 
 * EPP password have to be provided in the same properties file.</li>
 * <li>Property file locations - The location of ssl.properties file and rtk.properties 
 * file have to be provided in the properties files.</li>
 * <li> The other properties not relevant to the command can be left blank </li> </ul>
 * </ol>
 * 
 */
public class ShowcaseIDNExample {

    private static String dnsForm;

    private static final String USAGE = "Usage: ari.dnrs.rtk.addon.examples.ShowcaseIDNExample create|info";

    public static void main(final String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final String command = args[0];
        if (!("create".equals(command) || "info".equals(command))) {
            System.err.println(USAGE);
            System.exit(1);
        }
        
        //Extract EPPD properties from the correct properties file.
        InputStream resourceAsStream = null;
        final Properties commandProperties = new Properties();
        try {
            if ("info".equals(command)) {
                resourceAsStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("domain-info.properties");
            } else {
                resourceAsStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("domain-" + command + "-idn.properties");
            }
            commandProperties.load(resourceAsStream);
        } finally {
            if (resourceAsStream != null) {
                resourceAsStream.close();
            }
        }
        System.setProperty("ssl.props.location", commandProperties.getProperty("ssl.props.location"));
        System.setProperty("rtk.props.file", commandProperties.getProperty("rtk.props.file"));
        
        final String eppHostName = commandProperties.getProperty("epp.server.host");
        final String eppHostPortString = commandProperties.getProperty("epp.server.port");
        final String eppClientID = commandProperties.getProperty("epp.client.id");
        final String eppPassword = commandProperties.getProperty("epp.client.password");
        
        final int eppHostPort = Integer.parseInt(eppHostPortString);

         // Set up the EPP client with host name, host port, EPP client ID and
         // EPP password (the values provided in the properties files).
        final EPPClient eppClient = new EPPClient(eppHostName, eppHostPort, eppClientID, eppPassword);
        final String localeLanguage = "en";
        eppClient.setLang(localeLanguage);

        // Set service extensions to the EPP client at login that would be used in this session.
        eppClient.setEPPServiceExtensions(new String[] {XMLNamespaces.KVLIST_NAMESPACE, 
                XMLNamespaces.IDN_NAMESPACE});

        eppClient.connectAndGetGreeting();
        eppClient.login(eppClientID);

        // Set up the EPP command object.
        final epp_Command commandData = new epp_Command();
        commandData.setClientTrid("RTKUTEST." + System.currentTimeMillis());

        dnsForm = commandProperties.getProperty("domain.dns.form");
        
        if ("create".equals(command)) {
            executeIDNCreate(commandProperties, eppClient, commandData, dnsForm);
        } else if ("info".equals(command)) {
            executeIDNDomainInfo(commandProperties, eppClient, commandData);
        } 

        // Close the EPP session.
        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }


    /**
     * Sets up the Key value extension with the values provided in the properties file.
     *
     * @param command create | update | info
     * @param commandProperties the command properties extracted from the properties file
     * @return the created domain KV command extension 
     */
    private static DomainKVCommandExtension createKVExtension(final String command, final Properties commandProperties) {
        DomainKVCommandExtension kvExtension = null;
        // Set up Key Value extension with the input provided in the properties file
        final String lists = commandProperties.getProperty("list.count");

        if (!"".equals(lists)) {
            final int numberOfLists = Integer.parseInt(lists);
            kvExtension = new DomainKVCommandExtension(command);

            for (int listCount = 0; listCount < numberOfLists; listCount++) {
                final String keyValueList = commandProperties.getProperty("kvList" + (listCount + 1));
                final String[] kvTokens = keyValueList.split(",");

                final String listName = kvTokens[0];

                for (int itemCount = 1; itemCount < kvTokens.length; itemCount++) {
                    final String key = kvTokens[itemCount];
                    final String value = kvTokens[++itemCount];

                    kvExtension.addKeyValuePairToList(listName, key, value);
                }
            }
        }
        
        return kvExtension;
    }

    /**
     * Execute IDN domain create.
     *
     * @param commandProperties the command properties extracted from the properties file.
     * @param eppClient the EPP client
     * @param commandData the EPP command data with the Client TRID
     * @param domainName the domain name DNS form
     * @throws epp_XMLException if an exception occurs while processing the input
     * @throws epp_Exception if an exception occurs while processing the input
     * @throws PunycodeException if an exception occurs while converting the domain DNS form to user form
     */
    private static void executeIDNCreate(final Properties commandProperties, final EPPClient eppClient,
            final epp_Command commandData, final String domainName) throws epp_XMLException, epp_Exception {
        final DomainKVCommandExtension domainCreateKVExtension = createKVExtension("create", commandProperties);
        final String technicalContact = commandProperties.getProperty("tech.contact");
        
        // Set up Domain Create operation attributes with properties values
        final String authInfo = commandProperties.getProperty("auth.info");
        final String registrantContact = commandProperties.getProperty("registrant.contact");
        final String language = commandProperties.getProperty("domain.language");
        
        final epp_DomainCreateReq domainCreateRequest = new epp_DomainCreateReq();
        domainCreateRequest.setCmd(commandData);
        domainCreateRequest.setName(dnsForm);
        domainCreateRequest.setRegistrant(registrantContact);
        domainCreateRequest.setContacts(new epp_DomainContact[] { new epp_DomainContact(epp_DomainContactType.TECH,
                technicalContact) });
        domainCreateRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", authInfo));

        // Create the extension details to be added to the base Domain Create command
        DomainIdnCommandExtension idnExt = new DomainIdnCommandExtension();
        idnExt.setLanguageTag(language);

        // Set the extension to the Domain Create request
        epp_Extension[] extensions = {idnExt};
        if (domainCreateKVExtension != null) {
            extensions = new epp_Extension[] {domainCreateKVExtension, idnExt};
        }
        
        domainCreateRequest.getCmd().setExtensions(extensions);

        EPPDomainCreate domainCreate = new EPPDomainCreate();
        domainCreate.setRequestData(domainCreateRequest);

        // Send the request to the server
        
        try {
            domainCreate = (EPPDomainCreate) eppClient.processAction(domainCreate);
        } catch (final epp_Exception e) {
            final epp_Result eppResultDetails = e.getDetails()[0];
            final String reason = (eppResultDetails.getExtValues())[0].getReason();

            System.err.println("An exception occured while processing the request");
            System.err.println("OPERATION:: DOMAIN IDN CREATE");
            System.err.println("ERROR CODE:: " + eppResultDetails.getCode());
            System.err.println("ERROR MESSAGE:: " + eppResultDetails.getMsg());
            System.err.println("ERROR REASON:: " + reason);

            throw e;
        }
        
        final epp_DomainCreateRsp domainCreateResponse = domainCreate.getResponseData();
        final epp_Response response = domainCreateResponse.getRsp();
        final epp_Result[] results = response.getResults();

        System.out.println("Domain Create with IDN extension::");
        System.out.println("RESULT CODE:: " +  results[0].getCode());
        System.out.println("RESULT MESSAGE:: " +  results[0].getMsg());
        
        final String[] extensionStrings = response.getExtensionStrings();
        if (extensionStrings != null && extensionStrings.length > 0 && extensionStrings[0].length() > 0) {

            // Assume there is only one extension returned and is the IDN extension
            DomainIdnCommandExtension domainIdnaCommandExtension = new DomainIdnCommandExtension();
            domainIdnaCommandExtension.fromXML(extensionStrings[0]);

            System.out.println("IDN Response Language: " + domainIdnaCommandExtension.getLanguageTag());
        } else {
            System.err.println("Failed to return extension data");
            return;
        }
    }

    /**
     * Execute domain info command for a domain, returns the IDN details for an IDN.
     *
     * @param commandProperties the command properties extracted from the properties file.
     * @param eppClient the EPP client
     * @param commandData the EPP command data with the Client TRID
     * @throws epp_XMLException if an exception occurs while processing the input
     * @throws epp_Exception if an exception occurs while processing the input
     */
    private static void executeIDNDomainInfo(final Properties commandProperties, final EPPClient eppClient,
            final epp_Command commandData) throws epp_XMLException, epp_Exception {
        final epp_DomainInfoReq domainInfoRequest = new epp_DomainInfoReq();

        domainInfoRequest.setCmd(commandData);
        domainInfoRequest.setName(dnsForm);

        EPPDomainInfo domainInfo = new EPPDomainInfo();
        domainInfo.setRequestData(domainInfoRequest);

        try {
            domainInfo = (EPPDomainInfo) eppClient.processAction(domainInfo);
        } catch (final epp_Exception e) {
            final epp_Result eppResultDetails = e.getDetails()[0];
            final String reason = (eppResultDetails.getExtValues())[0].getReason();

            System.err.println("An exception occured while processing the request");
            System.err.println("OPERATION:: DOMAIN INFO");
            System.err.println("ERROR CODE:: " + eppResultDetails.getCode());
            System.err.println("ERROR MESSAGE:: " + eppResultDetails.getMsg());
            System.err.println("ERROR REASON:: " + reason);

            throw e;
        }

        final epp_DomainInfoRsp domain_info_response = domainInfo.getResponseData();
        final epp_Response response = domain_info_response.getRsp();
        System.out.println("DOMAIN INFO DETAILS:: \n");
        System.out.println("Domain Name: " + domain_info_response.getName());
        System.out.println("Domain ROID: " + domain_info_response.getRoid());
        System.out.println("Domain Client ID: " + domain_info_response.getClientId());
        System.out.println("Registrant: " + domain_info_response.getRegistrant());
        System.out.println("Domain Auth Info: " + domain_info_response.getAuthInfo().getValue());
        
        final epp_DomainStatus[] statuses = domain_info_response.getStatus();
        System.out.print("Domain Statuses: ");
        for (int i = 0; i < statuses.length; i++) {
            System.out.print(statuses[i].getType());
            if (i > 0) {
                System.out.print(",");
            }
        }
        System.out.println();
        
        System.out.println("Domain Expiration Date: " + domain_info_response.getExpirationDate());
        System.out.println("Domain Created Date: " + domain_info_response.getCreatedDate());
        System.out.println("Domain Created By: " + domain_info_response.getCreatedBy());
        System.out.println("Domain Updated Date: " + domain_info_response.getUpdatedDate());
        System.out.println("Domain Updated By: " + domain_info_response.getUpdatedBy());
        System.out.println("Domain Name Servers : " + Arrays.toString(domain_info_response.getNameServers()));
        
        final epp_DomainContact[] contacts = domain_info_response.getContacts();
        System.out.print("Domain Contacts: ");
        for (int i = 0; i < contacts.length; i++) {
            System.out.print(contacts[i].getId() + " [" + contacts[i].getType() + "]");
            if (i > 0) {
                System.out.print(",");
            }
        }
        System.out.println("\n");
        
        
        final String[] extensionStrings = response.getExtensionStrings();
        if (extensionStrings != null && extensionStrings.length > 0 && extensionStrings[0].length() > 0) {

            // IDN extension
            DomainIdnCommandExtension idnCommandExtension = new DomainIdnCommandExtension();
            idnCommandExtension.fromXML(extensionStrings[0]);

            System.out.println("IDN Response Language: " + idnCommandExtension.getLanguageTag());
        }
    }
}
