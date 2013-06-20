package ari.dnrs.rtk.addon.extensions.tmch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.bean.ClaimsInfoBean;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

/**
 * <p>Extension for the EPP Domain check command, representing the Check aspect of the
 * Domain Name Trademark Clearing House extension. Representation of the EPP Domain Check response Extension
 * with the Claims key Check aspect of the Domain Name Trademark Clearing extension.</p>
 *
 * <p>Use this to express the will to retrieve Lookup key, as part of the result of this command, which is being
 * submitted in as part of an EPP Domain Check command.
 * Also use this to get claims key for domain name during Trademark Clearing House Claims Period as provided
 * in the extension element with an EPP Domain Check response.
 * Such a service element is sent by a compliant EPP server in response
 * to a valid Domain Check command with the Domain Name Trademark Clearing House extension.</p>
 *
 * @see <a href="http://ausregistry.github.io/doc/tmch-1.0/tmch-1.0.html">Domain Name Trademark Clearing House
 * Extension Mapping for the Extensible Provisioning Protocol (EPP)</a>
 * */
public class DomainClaimsCommandExtension extends EPPXMLBase implements epp_Extension {

    private Map<String, ClaimsInfoBean> claimsNameMap;
    private Map<Long, ClaimsInfoBean> claimsIndexMap;

    public DomainClaimsCommandExtension() {
        claimsIndexMap = new HashMap<Long, ClaimsInfoBean>();
        claimsNameMap = new HashMap<String, ClaimsInfoBean>();
    }

    @Override
    public String toXML() throws epp_XMLException {
        final Document extensionDoc = new DocumentImpl();
        final Element commandElement = extensionDoc.createElement("check");
        commandElement.setAttribute("xmlns", XMLNamespaces.TMCH_NAMESPACE);

        extensionDoc.appendChild(commandElement);
        String tmchExtensionXML = null;
        try {
            tmchExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating tmch extension XML.\n" + e.getMessage());
        }
        return tmchExtensionXML;
    }

    /**
     *
     * @param responseXml the XML to be processed
     * @throws epp_XMLException
     */
    @Override
    public void fromXML(String responseXml) throws epp_XMLException {
        try {
            xml_ = responseXml;
            Element infDataNode = getDocumentElement();
            NodeList nodeList = infDataNode.getElementsByTagNameNS(XMLNamespaces.TMCH_NAMESPACE, "*");
            long infoNum;
            int count;
            for (count = 0, infoNum = 1; count < nodeList.getLength();) {
                if (nodeList.item(count).getNodeName().equals("cd")) {
                    count = processNode(nodeList, count + 1, infoNum);
                    infoNum++;
                } else {
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private int processNode(NodeList nodeList, int count, long infoNum) {
        String name = null;
        String key = null;
        boolean claim = false;
        while (count < nodeList.getLength() && !nodeList.item(count).getNodeName().equals("cd")) {
            Node node = nodeList.item(count);
            switch (ClaminsTagNames.valueOf(node.getNodeName())) {
                case name:
                    name = node.getFirstChild().getNodeValue();
                    Node premium = node.getAttributes().getNamedItem("claim");
                    if (premium != null) {
                        claim = premium.getNodeValue().equals("1");
                    }
                    break;
                case key:
                    key = node.getFirstChild().getNodeValue();
            }
            count++;
        }
        ClaimsInfoBean info = new ClaimsInfoBean(claim, key);
        claimsNameMap.put(name, info);
        claimsIndexMap.put(infoNum, info);
        return count;
    }

    /**
     * @param domainName domain name to be checked
     * @return true if the domain is in Domain Name Label List, false otherwise
     */
    public final Boolean claim(String domainName) {
        ClaimsInfoBean claimsInfoBean = claimsNameMap.get(domainName);
        return claimsInfoBean == null ? null : claimsInfoBean.claim();
    }
    /**
     * @param index the index of domain to be checked
     * @return true if the domain is in Domain Name Label List, false otherwise
     */
    public final Boolean claim(final long index) {
        ClaimsInfoBean claimsInfoBean = claimsIndexMap.get(index);
        return claimsInfoBean == null ? null : claimsInfoBean.claim();
    }

    /**
     * @param domainName domain name to be checked
     * @return claimsKey if domain with the claims key is in Domain Name Label List
     */
    public final String getClaimsKey(String domainName) {
        ClaimsInfoBean claimsInfoBean = claimsNameMap.get(domainName);
        return claimsInfoBean == null ? null : claimsInfoBean.getClaimsKey();
    }

    /**
     * @param index the index of domain to be checked
     * @return claimsKey if domain with the claims key is in Domain Name Label List
     */
    public final String getClaimsKey(final long index) {
        ClaimsInfoBean claimsInfoBean = claimsIndexMap.get(index);
        return claimsInfoBean == null ? null : claimsInfoBean.getClaimsKey();
    }

    private enum ClaminsTagNames {
        name, key
    }
}
