package ari.dnrs.rtk.addon.extensions.price;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.openrtk.idl.epprtk.domain.epp_DomainPeriod;
import org.openrtk.idl.epprtk.domain.epp_DomainPeriodUnitType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

/**
 * Use this to fetch the price price information for domain check with price response
 *
 * @see ari.dnrs.rtk.addon.extensions.price.DomainPriceCommandExtension
 */
public class DomainPriceCheckExtensionResponse extends EPPXMLBase {
    private Map<String, PriceInfo> priceNameMap;
    private Map<Integer, PriceInfo> priceIndexMap;

    public DomainPriceCheckExtensionResponse() {
        priceNameMap = new HashMap<String, PriceInfo>();
        priceIndexMap = new HashMap<Integer, PriceInfo>();
    }

    /**
     *
     * @param priceXml the xml to be processed
     */
    public void fromXML(String priceXml) {

        try {
            xml_ = priceXml;
            Element infDataNode = getDocumentElement();
            NodeList nodeList = infDataNode.getElementsByTagNameNS(XMLNamespaces.PRICE_NAMESPACE, "*");

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
        Boolean isPremium = null;
        epp_DomainPeriod period = null;
        BigDecimal price = null, renewPrice = null;
        String reason = null;
        while (count < nodeList.getLength() && !nodeList.item(count).getNodeName().equals("cd")) {
            Node node = nodeList.item(count);
            switch (PremiumTagNames.valueOf(node.getNodeName())) {
                case name:
                    name = node.getFirstChild().getNodeValue();
                    Node premium = node.getAttributes().getNamedItem("premium");
                    if (premium != null) {
                        isPremium = premium.getNodeValue().equals("1");
                    }
                    break;
                case period:
                    epp_DomainPeriodUnitType unit = node.getAttributes().getNamedItem("unit").getNodeValue()
                            .equals("y") ? epp_DomainPeriodUnitType.YEAR : epp_DomainPeriodUnitType.MONTH;
                    period = new epp_DomainPeriod(unit, Short.parseShort(node.getFirstChild().getNodeValue()));
                    break;
                case price:
                    price = BigDecimal.valueOf(Double.valueOf(node.getFirstChild().getNodeValue()));
                    break;
                case renewalPrice:
                    renewPrice = BigDecimal.valueOf(Double.valueOf(node.getFirstChild().getNodeValue()));
                    break;
                case reason:
                    reason = node.getFirstChild().getNodeValue();
            }
            count++;
        }
        PriceInfo info = new PriceInfo(isPremium, period, price, renewPrice, reason);
        priceNameMap.put(name, info);
        priceIndexMap.put(infoNum, info);
        return count;
    }

    /**
     * @param domainName domain name to be checked
     * @return true if the domain is premium, false otherwise
     */
    public final Boolean isPremium(final String domainName) {
        PriceInfo priceInfo = priceNameMap.get(domainName);
        return priceInfo == null ? null : priceInfo.isPremium();
    }

    /**
     * @param domainName domain name to be checked
     * @return period for which prices are for
     */
    public final epp_DomainPeriod getPeriod(final String domainName) {
        PriceInfo priceInfo = priceNameMap.get(domainName);
        return priceInfo == null ? null : priceInfo.getPeriod();
    }

    /**
     * @param domainName domain name to be checked
     * @return create price for domain if exists otherwise null
     */
    public final BigDecimal getCreatePrice(final String domainName) {
        PriceInfo priceInfo = priceNameMap.get(domainName);
        return priceInfo == null ? null : priceInfo.getCreatePrice();
    }

    /**
     * @param domainName domain name to be checked
     * @return renew price for domain if exists otherwise null
     */
    public final BigDecimal getRenewPrice(final String domainName) {
        PriceInfo priceInfo = priceNameMap.get(domainName);
        return priceInfo == null ? null : priceInfo.getRenewPrice();
    }

    /**
     * @param domainName domain name to be checked
     * @return error reason
     */
    public final String getReason(final String domainName) {
        PriceInfo priceInfo = priceNameMap.get(domainName);
        return priceInfo == null ? null : priceInfo.getReason();
    }

    /**
     * @param index the index of domain to be checked
     * @return true if the domain is premium, false otherwise
     */
    public final Boolean isPremium(final int index) {
        PriceInfo priceInfo = priceIndexMap.get(index);
        return priceInfo == null ? null : priceInfo.isPremium();
    }

    /**
     * @param index the index of domain to be checked
     * @return period for which prices are for
     */
    public final epp_DomainPeriod getPeriod(final int index) {
        PriceInfo priceInfo = priceIndexMap.get(index);
        return priceInfo == null ? null : priceInfo.getPeriod();
    }

    /**
     * @param index the index of domain to be checked
     * @return create price for domain if exists otherwise null
     */
    public final BigDecimal getCreatePrice(final int index) {
        PriceInfo priceInfo = priceIndexMap.get(index);
        return priceInfo == null ? null : priceInfo.getCreatePrice();
    }

    /**
     * @param index the index of domain to be checked
     * @return renew price for domain if exists otherwise null
     */
    public final BigDecimal getRenewPrice(final int index) {
        PriceInfo priceInfo = priceIndexMap.get(index);
        return priceInfo == null ? null : priceInfo.getRenewPrice();
    }

    /**
     * @param index the index of domain to be checked
     * @return error reason
     */
    public final String getReason(final int index) {
        PriceInfo priceInfo = priceIndexMap.get(index);
        return priceInfo == null ? null : priceInfo.getReason();
    }

    public class PriceInfo {

        private Boolean isPremium;
        private epp_DomainPeriod period;
        private BigDecimal createPrice;
        private BigDecimal renewPrice;
        private String reason;

        /**
         * @param isPremium if a domain is premium
         * @param period period for which prices are for
         * @param createPrice create price for domain
         * @param renewPrice renew price for domain
         * @param reason error reason
         */
        public PriceInfo(final Boolean isPremium, final epp_DomainPeriod period, final BigDecimal createPrice, final BigDecimal renewPrice, final String reason) {
            this.isPremium = isPremium;
            this.period = period;
            this.createPrice = createPrice;
            this.renewPrice = renewPrice;
            this.reason = reason;
        }

        /**
         * @return if a domain is premium
         */
        public Boolean isPremium() {
            return isPremium;
        }

        /**
         * @return period for which prices are for
         */
        public epp_DomainPeriod getPeriod() {
            return period;
        }

        /**
         * @return create price for domain
         */
        public BigDecimal getCreatePrice() {
            return createPrice;
        }

        /**
         * @return renew price for domain
         */
        public BigDecimal getRenewPrice() {
            return renewPrice;
        }

        /**
         * @return error reason
         */
        public String getReason() {
            return reason;
        }
    }

    private enum PremiumTagNames {
        name, period, price, renewalPrice, reason
    }
}
