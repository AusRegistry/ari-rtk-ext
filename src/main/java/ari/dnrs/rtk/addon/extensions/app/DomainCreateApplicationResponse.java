package ari.dnrs.rtk.addon.extensions.app;

import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;
import ari.dnrs.rtk.addon.utils.XMLUtil;

/**
 * Use this to access create data for a domain as provided in an EPP domain
 * create response compliant with RFC5730 and RFC5731.  Such a service element
 * is sent by a compliant EPP server in response to a valid domain create
 * command with domain create application extension.
 *
 * @see ari.dnrs.rtk.addon.extensions.app.DomainApplicationCommandExtension
 * @see org.openrtk.idl.epprtk.domain.epp_DomainCreateReq
 */
public class DomainCreateApplicationResponse extends EPPXMLBase {
    private String applicationId;
    private String name;
    private GregorianCalendar createDate;

    /**
     *
     * @param responseXml the response from application create operation
     */
    public void fromXML(String responseXml) {
        try {
            xml_ = responseXml;
            Element creDataNode = getDocumentElement();
            NodeList nodeList = creDataNode.getElementsByTagNameNS(XMLNamespaces.APPLICATION_NAMESPACE, "*");

            for (int count = 0; count < nodeList.getLength(); count++) {
                Node node = nodeList.item(count);
                switch (TagNames.valueOf(node.getNodeName())) {
                    case id:
                        applicationId = node.getFirstChild().getNodeValue();
                        break;
                    case name:
                        name = node.getFirstChild().getNodeValue();
                        break;
                    case crDate:
                        createDate = XMLUtil.fromXSDateTime(node.getFirstChild().getNodeValue());
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return the create date
     */
    public GregorianCalendar getCreateDate() {
        return createDate;
    }

    /**
     *
     * @return the domain name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the application id
     */
    public String getApplicationId() {
        return applicationId;
    }

    private enum TagNames {
        id, name, crDate, creData
    }
}
