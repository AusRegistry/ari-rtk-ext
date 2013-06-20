package ari.dnrs.rtk.addon.examples;

import java.util.List;

import ari.dnrs.rtk.addon.bean.IdnDomainVariant;
import ari.dnrs.rtk.addon.extensions.variant.DomainVariantResponseExtensionV1_1;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoReq;
import org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp;

import ari.dnrs.rtk.addon.extensions.idn.DomainIdnCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.EPPClient;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;

public class DomainInfoCommandWithIdnAndVariantExtensionsExample {

    private static String USAGE = "Usage: ari.dnrs.rtk.addon.examples.DomainInfoCommandWithIdnAndVariantExtensionsExample"
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
        eppClient.setEPPServiceExtensions(new String[] {XMLNamespaces.IDN_NAMESPACE,
                XMLNamespaces.VARIANT_V1_1_NAMESPACE});

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

            // IDNA extension
            DomainIdnCommandExtension idnCommandExtension = new DomainIdnCommandExtension();
            idnCommandExtension.fromXML(extensionStrings[0]);

            System.out.println("IDNA Response Language: " + idnCommandExtension.getLanguageTag());

            DomainVariantResponseExtensionV1_1 variantCommandExtension = new DomainVariantResponseExtensionV1_1();
            variantCommandExtension.fromXml(extensionStrings[0]);

            final List<IdnDomainVariant> variantList = variantCommandExtension.getVariants();
            int i = 0;
            for (IdnDomainVariant domainVariant : variantList) {
                i++;
                System.out.println("Variant: " + i);
                System.out.println("Variant Domain DNS Form:" + domainVariant.getName());
            }

        } else {
            System.err.println("Failed to return extension data");
        }

        eppClient.logout(eppClientID);
        eppClient.disconnect();
    }
}
