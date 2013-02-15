package ari.dnrs.rtk.addon.extensions.secdns;

import java.io.IOException;

import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ari.dnrs.rtk.addon.bean.DNSSecDataBean;
import ari.dnrs.rtk.addon.bean.DSDataBean;
import ari.dnrs.rtk.addon.bean.KeyDataBean;
import ari.dnrs.rtk.addon.bean.MaxSigLifeBean;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Use this Class to retrieve the DNSSEC Domain extension properties from an EPP Domain Info command.
 *
 */
public class SECDNSInfoResponseExtension extends EPPXMLBase {

    private DNSSecDataBean infData;

    public void fromXML(final String xml) throws epp_XMLException {

        if (xml == null || xml.length() == 0) {
            return;
        }

        try {
            xml_ = xml;

            Element infDataNode = getDocumentElement();
            NodeList extensionNodes = infDataNode.getElementsByTagNameNS(XMLNamespaces.SEC_DNS_NAMESPACE, "*");

            if (extensionNodes.getLength() == 0) {
                return;
            }

            infData = new DNSSecDataBean();

            for (int count = 0; count < extensionNodes.getLength(); count++) {
                Node childNode = extensionNodes.item(count);

                if (childNode.getNodeName().equals("maxSigLife")) {
                    MaxSigLifeBean maxSigLife = new MaxSigLifeBean(Integer.parseInt(childNode.getFirstChild()
                            .getNodeValue()));
                    infData.setMaxSigLife(maxSigLife);
                } else if (childNode.getNodeName().equals("dsData")) {
                    infData.addToDsData(DSDataBean.fromXML(childNode));
                } else if (!childNode.getParentNode().getNodeName().equals("dsData")
                        && childNode.getNodeName().equals("keyData")) {
                    infData.addToKeyData(KeyDataBean.fromXML(childNode));
                }
            }

        } catch (SAXException xcp) {
            throw new epp_XMLException("unable to parse xml [" + xcp.getClass().getName() + "] [" + xcp.getMessage()
                    + "]");
        } catch (IOException xcp) {
            throw new epp_XMLException("unable to parse xml [" + xcp.getClass().getName() + "] [" + xcp.getMessage()
                    + "]");
        }

    }

    public final DNSSecDataBean getInfData() {
        return infData;
    }

}
