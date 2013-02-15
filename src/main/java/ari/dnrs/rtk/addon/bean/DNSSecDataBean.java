package ari.dnrs.rtk.addon.bean;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Models the &lt;add&gt;, &lt;infData&gt; and &lt;create&gt; elements as documented in RFC5910.
 */
public class DNSSecDataBean {

    private List<DSDataBean> dsDataList;
    private List<KeyDataBean> keyDataList;
    private MaxSigLifeBean maxSigLife;

    public void addToDsData(final DSDataBean dsData) {
        if (dsDataList == null) {
            dsDataList = new ArrayList<DSDataBean>();
        }
        dsDataList.add(dsData);
    }

    public void addToKeyData(final KeyDataBean keyData) {
        if (keyDataList == null) {
            keyDataList = new ArrayList<KeyDataBean>();
        }
        keyDataList.add(keyData);
    }

    public final List<DSDataBean> getDsDataList() {
        return dsDataList;
    }

    public final void setDsDataList(List<DSDataBean> dsDataList) {
        this.dsDataList = dsDataList;
    }

    public final List<KeyDataBean> getKeyDataList() {
        return keyDataList;
    }

    public final void setKeyDataList(List<KeyDataBean> keyDataList) {
        this.keyDataList = keyDataList;
    }

    public final MaxSigLifeBean getMaxSigLife() {
        return maxSigLife;
    }

    public final void setMaxSigLife(MaxSigLifeBean maxSigLife) {
        this.maxSigLife = maxSigLife;
    }

    /**
     * Adds the DNSSEC details to the addElement provided, if maxSigLife or DS data or Key Data
     * has been provided.
     * Used internally for DNSSEC extension for domain update and domain create commands.
     *
     * @param addElement the add element
     */
    public void createXMLElement(final Element addElement) {
        if (maxSigLife != null) {
            maxSigLife.createXMLElement(addElement);
        }

        if(dsDataList != null) {
            for (DSDataBean dsData : dsDataList) {
                dsData.appendDsDataToElement(addElement);
            }
        }

        if(keyDataList != null) {
            for (KeyDataBean keyData : keyDataList) {
                keyData.appendKeyDataToElement(addElement);
            }
        }
    }

}
