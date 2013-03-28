package ari.dnrs.rtk.addon.extensions.premium;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

public class DomainPremiumCheckResponse extends EPPXMLBase {
    private Map<String, PremiumInfo> premiumNameMap;
    private Map<Integer, PremiumInfo> premiumIndexMap;

    public DomainPremiumCheckResponse() {
        premiumNameMap = new HashMap<String, PremiumInfo>();
        premiumIndexMap = new HashMap<Integer, PremiumInfo>();
    }

    public void fromXML(String premiumXml) {

        try {
            xml_ = premiumXml;
            Element infDataNode = getDocumentElement();
            NodeList nodeList = infDataNode.getElementsByTagNameNS(XMLNamespaces.PREMIUM_NAMESPACE, "*");

            for (int count = 0, infoNum = 1; count < nodeList.getLength(); ) {
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

    private int processNode(NodeList nodeList, int count, int infoNum) {
        String name = null;
        BigDecimal price = null, renewPrice = null;
        boolean isPremium = false;
        while (count < nodeList.getLength() && !nodeList.item(count).getNodeName().equals("cd")) {
            Node node = nodeList.item(count);
            switch (PremiumTagNames.valueOf(node.getNodeName())) {
                case name:
                    name = node.getFirstChild().getNodeValue();
                    isPremium = node.getAttributes().getNamedItem("premium").getNodeValue().equals("1");
                    break;
                case price:
                    price = BigDecimal.valueOf(Double.valueOf(node.getFirstChild().getNodeValue()));
                    break;
                case renewalPrice:
                    renewPrice = BigDecimal.valueOf(Double.valueOf(node.getFirstChild().getNodeValue()));
            }
            count++;
        }
        PremiumInfo info = new PremiumInfo(isPremium, price, renewPrice);
        premiumNameMap.put(name, info);
        premiumIndexMap.put(infoNum, info);
        return count;
    }

    public boolean isPremium(String domainName) {
        PremiumInfo premiumInfo = premiumNameMap.get(domainName);
        return premiumInfo != null && premiumInfo.isPremium();
    }

    public BigDecimal getCreatePrice(String domainName) {
        PremiumInfo premiumInfo = premiumNameMap.get(domainName);
        return premiumInfo == null ? null : premiumInfo.getCreatePrice();
    }

    public BigDecimal getRenewPrice(String domainName) {
        PremiumInfo premiumInfo = premiumNameMap.get(domainName);
        return premiumInfo == null ? null : premiumInfo.getRenewPrice();
    }

    public boolean isPremium(int index) {
        PremiumInfo premiumInfo = premiumIndexMap.get(index);
        return premiumInfo != null && premiumInfo.isPremium();
    }

    public BigDecimal getCreatePrice(int index) {
        PremiumInfo premiumInfo = premiumIndexMap.get(index);
        return premiumInfo == null ? null : premiumInfo.getCreatePrice();
    }

    public BigDecimal getRenewPrice(int index) {
        PremiumInfo premiumInfo = premiumIndexMap.get(index);
        return premiumInfo == null ? null : premiumInfo.getRenewPrice();
    }

    public class PremiumInfo {

        private boolean isPremium;
        private BigDecimal createPrice;
        private BigDecimal renewPrice;

        /**
         * @param isPremium if a domain is premium
         * @param createPrice create price for premium domain
         * @param renewPrice renew price for premium domain
         */
        public PremiumInfo(final boolean isPremium, final BigDecimal createPrice, final BigDecimal renewPrice) {
            this.isPremium = isPremium;
            if (isPremium) {
                this.createPrice = createPrice;
                this.renewPrice = renewPrice;
            }
        }

        /**
         * @return if a domain is premium
         */
        public boolean isPremium() {
            return isPremium;
        }

        /**
         * @return create price for premium domain
         */
        public BigDecimal getCreatePrice() {
            return createPrice;
        }

        /**
         * @return renew price for premium domain
         */
        public BigDecimal getRenewPrice() {
            return renewPrice;
        }

    }

    private enum PremiumTagNames {
        name, price, renewalPrice
    }
}
