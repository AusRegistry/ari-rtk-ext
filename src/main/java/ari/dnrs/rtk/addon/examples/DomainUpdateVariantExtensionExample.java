package ari.dnrs.rtk.addon.examples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ari.dnrs.rtk.addon.bean.IdnDomainVariant;
import ari.dnrs.rtk.addon.extensions.variant.DomainUpdateVariantCommandExtensionV1_1;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Exception;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateRsp;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.transport.EPPTransportException;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

/**
 * Example code for ARI's Variant extension for EPP using domain update to
 * demonstrate its usage. <br />
 * 
 * INPUT:: The input file 'domain-update-variant.properties' can be found in the folder 'examples/res'.
 * <ol>
 * <li>The absolute path to the properties file 'domain-update-variant.properties'
 *  should be provided as command line argument.</li>
 * <li> All the relevant input for the request, EPPD details and the property file locations
 *  should be provided in the properties file <b>domain-update-variant.properties. </b> </li>
 * <ul><li>EPPD details - The EPP host name, EPP host port number, EPP client ID, 
 * EPP password have to be provided in the same properties file.</li></ul>
 * <ul><li>Property file locations - The location of ssl.properties file and rtk.properties 
 * file have to be provided in the properties files.</li></ul>
 * </ol>
 * 
 */
public class DomainUpdateVariantExtensionExample {

    public static void main(final String[] args) throws epp_Exception, epp_XMLException,
            EPPTransportException, IOException {
        
        if (args.length != 1) {
            System.out.println("USAGE: DomainUpdateVariantExtensionExample 'absolute path to " +
            		"domain-update-variant.properties'");
            System.exit(1);
        }
        
        final String propertiesFilePath = args[0];
        final Properties classProperties = new Properties();
        InputStream resourceAsStream = null;

        try {
            resourceAsStream = new FileInputStream(propertiesFilePath);
            classProperties.load(resourceAsStream);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found at the location provided: " 
                                + propertiesFilePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the properties file domain-update-variant.properties " +
            		"from the location provided: " + propertiesFilePath, e);
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
        eppClient.setEPPServiceExtensions(new String[] {XMLNamespaces.VARIANT_V1_1_NAMESPACE});

        eppClient.connectAndGetGreeting();
        eppClient.login(eppClientID);

        // Set up the EPP command object.
        final epp_Command commandData = new epp_Command();
        commandData.setClientTrid("RTKUTEST." + System.currentTimeMillis());

        final String domainName = classProperties.getProperty("domain.name");

        final String[] variantsDNSFormToAdd = classProperties.getProperty("variants.dnsform.to.add").split(",");

        final String[] variantsDNSFormToRem = classProperties.getProperty("variants.dnsform.to.remove").split(",");

        DomainUpdateVariantCommandExtensionV1_1 domainVariantExtension = new DomainUpdateVariantCommandExtensionV1_1();
        for (int i = 0; i < variantsDNSFormToAdd.length; i++) {
            domainVariantExtension.addVariant(new IdnDomainVariant(variantsDNSFormToAdd[i]));
        }

        for (int i = 0; i < variantsDNSFormToRem.length; i++) {
            domainVariantExtension.removeVariant(new IdnDomainVariant(variantsDNSFormToRem[i]));
        }

        epp_DomainUpdateReq domainUpdateRequest = new epp_DomainUpdateReq();
        domainUpdateRequest.setName(domainName);
        domainUpdateRequest.setCmd(commandData);

        // Set the extension to the Domain update request
        final epp_Extension[] extensions = {domainVariantExtension};
        domainUpdateRequest.getCmd().setExtensions(extensions);

        EPPDomainUpdate domainUpdate = new EPPDomainUpdate();
        domainUpdate.setRequestData(domainUpdateRequest);

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

}
