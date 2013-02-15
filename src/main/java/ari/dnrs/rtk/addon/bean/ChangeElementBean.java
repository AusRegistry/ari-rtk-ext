package ari.dnrs.rtk.addon.bean;

import org.w3c.dom.Element;

/**
 * Models the &lt;chg&gt; element for DNSSEC extension in Domain Update command as documented in RFC5910.
 */
public class ChangeElementBean {

    private MaxSigLifeBean maxSigLife;

    /**
     * Adds the maxSigLife child element to the changeElement provided, if maxSigLife has been provided.
     * Used internally for DNSSEC command Extension for domain update command.
     *
     * @param changeElement the change element
     */
    public void createXMLElement(final Element changeElement) {
        if (maxSigLife != null) {
            maxSigLife.createXMLElement(changeElement);
        }
    }

    public final MaxSigLifeBean getMaxSigLife() {
        return maxSigLife;
    }

    public final void setMaxSigLife(MaxSigLifeBean maxSigLife) {
        this.maxSigLife = maxSigLife;
    }

}
