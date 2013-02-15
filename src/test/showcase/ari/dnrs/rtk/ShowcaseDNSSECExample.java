package ari.dnrs.rtk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.bean.DSDataBean;
import ari.dnrs.rtk.addon.bean.DomainKeyValueBean;
import ari.dnrs.rtk.addon.bean.DomainVariantBean;
import ari.dnrs.rtk.addon.bean.KeyDataBean;
import ari.dnrs.rtk.addon.bean.RemoveElementBean;
import ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension;
import ari.dnrs.rtk.addon.extensions.secdns.SECDNSCreateCommandExtension;
import ari.dnrs.rtk.addon.extensions.secdns.SECDNSInfoResponseExtension;
import ari.dnrs.rtk.addon.extensions.secdns.SECDNSUpdateCommandExtension;
import ari.dnrs.rtk.addon.extensions.variant.DomainVariantCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

/**
 * Example code for ARI's DNSSEC extension for EPP using domain create, domain
 * update and domain info commands to demonstrate its usage. <br />
 * Extensions supported in this example - 
 * DNSSEC extension for domain create, update and info, 
 * Key-Value extension for domain info.
 * Variant extension for domain info. <br />
 * 
 * INPUT:: 
 * <ol>
 * <li>Command -  Either one of create | update | info has to be provided to the class as command line
 * argument to choose the corresponding operation for domain with DNSSEC
 * extension. </li>
 * <li> All the other relevant input for the request, EPPD details and the location of rtk.properties 
 * and ssl.properties files should be provided in one of the properties files domain-create-dnssec.properties, 
 * domain-update-dnssec.properties,  domain-info-dnssec.properties depending on the request to be sent. </li>
 * <ul><li>EPPD details - The EPP host name, EPP host port number, EPP client ID, 
 * EPP password have to be provided in the same properties file.</li>
 * <li>Property file locations - The location of ssl.properties file and rtk.properties 
 * file have to be provided in the properties files.</li></ul>
 * </ol>
 * 
 */
public class ShowcaseDNSSECExample {

    private static String dnsForm;

    private static final String USAGE = "Usage: ari.dnrs.rtk.addon.examples.ShowcaseDNSSECExample create|update|info";

    public static void main(final String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println(USAGE);
            System.exit(1);
        }

        final String command = args[0];
        if (!("create".equals(command) || "update".equals(command) || "info".equals(command))) {
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
                        .getResourceAsStream("domain-" + command + "-dnssec.properties");
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
        eppClient.setEPPServiceExtensions(new String[] {XMLNamespaces.SEC_DNS_NAMESPACE, 
                XMLNamespaces.KVLIST_NAMESPACE, XMLNamespaces.VARIANT_NAMESPACE});

        eppClient.connectAndGetGreeting();
        eppClient.login(eppClientID);

        // Set up the EPP command object.
        final epp_Command commandData = new epp_Command();
        commandData.setClientTrid("RTKUTEST." + System.currentTimeMillis());

        dnsForm = commandProperties.getProperty("domain.dns.form");
        
        if ("create".equals(command)) {
            executeDNSSECDomainCreate(commandProperties, eppClient, commandData);
        } else if ("update".equals(command)) {
            executeDNSSECDomainUpdate(commandProperties, eppClient, commandData);
        } else if ("info".equals(command)) {
            executeDNSSECDomainInfo(commandProperties, eppClient, commandData);
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
     * Gets the DNSSEC details to be added to the domain provided as input.
     *
     * @param commandProperties the command properties extracted from the properties file.
     * @return the DNSSEC details to be added
     * @throws epp_XMLException if the DS data input provided is not valid
     */
    private static DNSSecDataBean getDNSSECDetailsToAdd(final Properties commandProperties) throws epp_XMLException {
        final DNSSecDataBean dnssecDataToAdd = new DNSSecDataBean();
        
        final String dsDataDigest = commandProperties.getProperty("dsData.digest.add");
        final String digestTypeString = commandProperties.getProperty("dsData.digest.type.add");
        final String digestAlgorithmString = commandProperties.getProperty("dsData.algorithm.add");
        final String keyTagString = commandProperties.getProperty("dsData.key.tag.add");

        if ((dsDataDigest != null && digestTypeString != null && digestAlgorithmString != null && keyTagString != null)
                && !("".equals(digestTypeString) || "".equals(digestAlgorithmString) || "".equals(keyTagString) || ""
                        .equals(dsDataDigest))) {
            final int digestType = Integer.parseInt(digestTypeString);
            final int digestAlgorithm = Integer.parseInt(digestAlgorithmString);
            final int keyTag = Integer.parseInt(keyTagString);
            
            final DSDataBean dsData = new DSDataBean();
            dsData.setAlgorithm(digestAlgorithm);
            dsData.setDigest(dsDataDigest);
            dsData.setDigestType(digestType);
            dsData.setKeyTag(keyTag);

            dnssecDataToAdd.addToDsData(dsData);
        }

        final String publicKey = commandProperties.getProperty("keyData.publicKey.add");
        final String protocolString = commandProperties.getProperty("keyData.protocol.add");
        final String flagsString = commandProperties.getProperty("keyData.flags.add");
        final String algorithmString = commandProperties.getProperty("keyData.algorithm.add");
        
        
        if ((publicKey != null && protocolString != null && flagsString != null && algorithmString != null)
                && !("".equals(publicKey) || "".equals(protocolString) || "".equals(flagsString) || ""
                        .equals(algorithmString))) {
            final int protocol = Integer.parseInt(protocolString);
            final int flags = Integer.parseInt(flagsString);
            final int algorithm = Integer.parseInt(algorithmString);
            
            final KeyDataBean keyData = new KeyDataBean(flags, protocol, algorithm, publicKey);
            dnssecDataToAdd.addToKeyData(keyData);
        }
        
        return dnssecDataToAdd;
    }

    /**
     * Execute domain info command for a domain, returns the DNSSEC details for a domain with DNSSEC details .
     *
     * @param commandProperties the command properties extracted from the properties file.
     * @param eppClient the EPP client
     * @param commandData the EPP command data with the Client TRID
     * @throws epp_XMLException if an exception occurs while processing the input
     * @throws epp_Exception if an exception occurs while processing the input
     */
    private static void executeDNSSECDomainInfo(final Properties commandProperties, final EPPClient eppClient,
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
            if (i > 0 && i != contacts.length - 1) {
                System.out.print(",");
            }
        }
        
        final String[] extensionStrings = response.getExtensionStrings();
        if (extensionStrings != null && extensionStrings.length > 0 && extensionStrings[0].length() > 0) {

            //Handle the DNSSEC extension response.
            final SECDNSInfoResponseExtension secDnsDomainInfoResponse = new SECDNSInfoResponseExtension();
            secDnsDomainInfoResponse.fromXML(extensionStrings[0]);
            
            DNSSecDataBean secDNSInfData = secDnsDomainInfoResponse.getInfData();
            if (secDNSInfData != null) {
                System.out.println();
                System.out.println();
                System.out.println("DOMAIN DNSSEC DETAILS:: \n");
                final List<DSDataBean> dsDataList = secDNSInfData.getDsDataList();
                if (dsDataList != null) {
                    for (int i = 0; i < dsDataList.size(); i++) {
                        if (i > 0) {
                            System.out.println("-----------------------------");
                        }
                        final DSDataBean dsData = dsDataList.get(i);
                        System.out.println("DS Data Algorithm: " + dsData.getAlg());
                        System.out.println("DS Data Digest: " + dsData.getDigest());
                        System.out.println("DS Data Digest Type: " + dsData.getDigestType());
                        System.out.println("DS Data Key Tag: " + dsData.getKeyTag());
                        final KeyDataBean keyData = dsData.getKeyData();
                        if (keyData != null) {
                            System.out.println("Key Data Algorithm: " + keyData.getAlgorithm());
                            System.out.println("Key Data Flags: " + keyData.getFlags());
                            System.out.println("Key Data Protocol: " + keyData.getProtocol());
                            System.out.println("Key Data Public Key: " + keyData.getPubKey());
                        }
                    }
                }
                final List<KeyDataBean> keyDataList = secDNSInfData.getKeyDataList();
                if (keyDataList != null) {
                    for (int i = 0; i < keyDataList.size(); i++) {
                        if (i > 0) {
                            System.out.println("-----------------------------");
                        }
                        final KeyDataBean keyData = keyDataList.get(i);
                        System.out.println("Key Data Algorithm: " + keyData.getAlgorithm());
                        System.out.println("Key Data Flags: " + keyData.getFlags());
                        System.out.println("Key Data Protocol: " + keyData.getProtocol());
                        System.out.println("Key Data Public Key: " + keyData.getPubKey());
                    }
                }
            }
        } 
        
        //Handle the variant extension response.
        DomainVariantCommandExtension variantCommandExtension = new DomainVariantCommandExtension();
        variantCommandExtension.fromXML(extensionStrings[0]);

        final List<DomainVariantBean> variantList = variantCommandExtension.getInfoVariantList();
        
        if (variantList != null && variantList.size() > 0) {
            System.out.println();
            System.out.println("DOMAIN VARIANT DETAILS:: \n");
            int i = 0;
            for (DomainVariantBean domainVariant : variantList) {
                i++;
                System.out.println("Variant " + i + " DNS Form: " + domainVariant.getName());
                System.out.println("Variant " + i + " User Form:" + domainVariant.getUserForm());
            }
        }
        
        //Handle Key value extension response
        DomainKVCommandExtension kvExtension = new DomainKVCommandExtension("info");
        kvExtension.fromXML(extensionStrings[0]);
        
        HashMap<String, ArrayList<DomainKeyValueBean>> keyValueLists = kvExtension.getKeyValueLists();
        if (keyValueLists != null && keyValueLists.size() > 0) {
            System.out.println();
            System.out.println("DOMAIN KEY VALUE DETAILS:: \n");
            for (Entry<String, ArrayList<DomainKeyValueBean>> keyValueList : keyValueLists.entrySet()) {
                System.out.println("LIST NAME: " + keyValueList.getKey());
                ArrayList<DomainKeyValueBean> kvList = keyValueList.getValue();
                if (kvList != null && kvList.size() > 0) {
                    for (DomainKeyValueBean domainKeyValueBean : kvList) {
                        System.out.println("Key: " + domainKeyValueBean.getKey() + " Value: "
                                + domainKeyValueBean.getValue());
                    }
                }
            }
        }
        
    }

    /**
     * Update DNSSEC details of a domain.
     *
     * @param commandProperties the command properties extracted from the properties file.
     * @param eppClient the EPP client
     * @param commandData the EPP command data with the Client TRID
     * @throws epp_XMLException if an exception occurs while processing the input
     * @throws epp_Exception if an exception occurs while processing the input
     */
    private static void executeDNSSECDomainUpdate(final Properties commandProperties, final EPPClient eppClient,
            final epp_Command commandData) throws epp_XMLException, epp_Exception {
        
        final SECDNSUpdateCommandExtension secDNSExt = new SECDNSUpdateCommandExtension();
        
        final DNSSecDataBean dnssecDataToAdd = getDNSSECDetailsToAdd(commandProperties);
        secDNSExt.setAddData(dnssecDataToAdd);
        
        final RemoveElementBean removeElement = new RemoveElementBean();
        
        final String dsDataDigestToRem = commandProperties.getProperty("dsData.digest.rem");
        final String digestTypeString = commandProperties.getProperty("dsData.digest.type.rem");
        final String digestAlgorithmString = commandProperties.getProperty("dsData.algorithm.rem");
        final String keyTagString = commandProperties.getProperty("dsData.key.tag.rem");

        if ((dsDataDigestToRem != null && digestTypeString != null && digestAlgorithmString != null && keyTagString != null)
                && !("".equals(digestTypeString) || "".equals(digestAlgorithmString) || "".equals(keyTagString) || ""
                        .equals(dsDataDigestToRem))) {
            DSDataBean dsDataToRem = new DSDataBean();

            final int digestTypeToRem = Integer.parseInt(digestTypeString);
            final int digestAlgorithmToRem = Integer.parseInt(digestAlgorithmString);
            final int keyTagToRem = Integer.parseInt(keyTagString);
            
            dsDataToRem.setAlgorithm(digestAlgorithmToRem);
            dsDataToRem.setDigest(dsDataDigestToRem);
            dsDataToRem.setDigestType(digestTypeToRem);
            dsDataToRem.setKeyTag(keyTagToRem);
            
            removeElement.addToDsData(dsDataToRem);
        }
        
        final String publicKeyToRem = commandProperties.getProperty("keyData.publicKey.rem");
        final String protocolString = commandProperties.getProperty("keyData.protocol.rem");
        final String flagsString = commandProperties.getProperty("keyData.flags.rem");
        final String algorithmString = commandProperties.getProperty("keyData.algorithm.rem");
        
        
        if ((publicKeyToRem != null && protocolString != null && flagsString != null && algorithmString != null)
                && !("".equals(publicKeyToRem) || "".equals(protocolString) || "".equals(flagsString) || ""
                        .equals(algorithmString))) {
            final int protocolToRem = Integer.parseInt(protocolString);
            final int flagsToRem = Integer.parseInt(flagsString);
            final int algorithmToRem = Integer.parseInt(algorithmString);
            KeyDataBean keyDataToRem = new KeyDataBean(flagsToRem, protocolToRem, algorithmToRem, publicKeyToRem);
            removeElement.addToKeyData(keyDataToRem);
        }
        
        if (dsDataDigestToRem != null) {
            
        }
        
        secDNSExt.setRemData(removeElement);
        
        // Set up the Domain Update Request data to be used by the EPP command.
        final epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        domainUpdateRequest.setCmd(commandData);

        domainUpdateRequest.setName(dnsForm);

        // Set the Domain Update request to a Domain Update EPP Command
        EPPDomainUpdate domainUpdate = new EPPDomainUpdate();
        domainUpdate.setRequestData(domainUpdateRequest);
        
        // Set the extension to the Domain update request
        final epp_Extension[] extensions = {secDNSExt};
        
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

    /**
     * Execute domain create with DNSSEC details provided as input.
     *
     * @param commandProperties the command properties extracted from the properties file.
     * @param eppClient the EPP client
     * @param commandData the EPP command data with the Client TRID
     * @throws epp_XMLException if an exception occurs while processing the input
     * @throws epp_Exception if an exception occurs while processing the input
     */
    private static void executeDNSSECDomainCreate(final Properties commandProperties, final EPPClient eppClient,
            final epp_Command commandData) throws epp_XMLException, epp_Exception {
        final DomainKVCommandExtension domainCreateKVExtension = createKVExtension("create", commandProperties);
        final String techContact = commandProperties.getProperty("tech.contact");
        final DNSSecDataBean dnssecDataToAdd = getDNSSECDetailsToAdd(commandProperties);
        
        final SECDNSCreateCommandExtension secDNSExt = new SECDNSCreateCommandExtension();
        secDNSExt.setCreateData(dnssecDataToAdd);
        
        // Set up Domain Create operation attributes with properties values
        final String authInfo = commandProperties.getProperty("auth.info");
        final String registrantContact = commandProperties.getProperty("registrant.contact");

        final epp_DomainCreateReq domainCreateRequest = new epp_DomainCreateReq();
        domainCreateRequest.setCmd(commandData);
        domainCreateRequest.setName(dnsForm);
        domainCreateRequest.setRegistrant(registrantContact);
        domainCreateRequest.setContacts(new epp_DomainContact[] {new epp_DomainContact(epp_DomainContactType.TECH,
                techContact)});
        domainCreateRequest.setAuthInfo(new epp_AuthInfo(epp_AuthInfoType.PW, "", authInfo));

        // Set the extension to the Domain Create request
        epp_Extension[] extensions = {secDNSExt};
        if (domainCreateKVExtension != null) {
            extensions = new epp_Extension[] {domainCreateKVExtension, secDNSExt};
        }
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

        System.out.println("Domain Create with DNSSEC result::");
        System.out.println("RESULT CODE:: " +  results[0].getCode());
        System.out.println("RESULT MESSAGE:: " +  results[0].getMsg());
    }
}
