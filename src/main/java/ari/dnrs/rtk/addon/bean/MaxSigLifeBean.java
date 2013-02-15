package ari.dnrs.rtk.addon.bean;

import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Element;

import ari.dnrs.rtk.addon.utils.XMLUtil;

/**
 * Models the &lt;maxSigLife&gt; element as documented in RFC5910.
 */
public class MaxSigLifeBean {

    private int maxSigLife;

    public MaxSigLifeBean(int maxSigLife) {
        this.maxSigLife = maxSigLife; 
    }

    public final int getMaxSigLife() {
        return maxSigLife;
    }

    /**
     * Sets the maxSigLife. This value should be a positive number.
     *
     * @param maxSigLife the new maxSigLife
     * @throws epp_XMLException if the provided maxSigLife is not a positive number
     */
    public final void setMaxSigLife(int maxSigLife) throws epp_XMLException {
        if (maxSigLife < 1) {
            throw new epp_XMLException("Max sig life must be a positive number.");
        }

        this.maxSigLife = maxSigLife;
    }

    /**
     * Adds the maxSigLife child element to the changeElement provided, if maxSigLife has been provided.
     * Used internally for DNSSEC command Extension for domain update and domain create commands.
     *
     * @param changeElement the change element
     */
    public void createXMLElement(final Element changeElement) {
        XMLUtil.appendChildElement(changeElement, "maxSigLife", maxSigLife + "");
    }

}
