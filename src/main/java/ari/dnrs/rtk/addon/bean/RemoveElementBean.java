package ari.dnrs.rtk.addon.bean;

import java.util.ArrayList;

import org.w3c.dom.Element;

import ari.dnrs.rtk.addon.utils.XMLUtil;

/**
 * Models the &lt;rem&gt; element as documented in RFC5910.
 */
public class RemoveElementBean {

    private ArrayList<DSDataBean> dsDataList;
    private ArrayList<KeyDataBean> keyDataList;
    private boolean removeAll;

    public void addToDsData(final DSDataBean dsData) {
        if (this.dsDataList == null) {
            this.dsDataList = new ArrayList<DSDataBean>();
        }
        this.dsDataList.add(dsData);
    }

    public void addToKeyData(final KeyDataBean keyData) {
        if (this.keyDataList == null) {
            this.keyDataList = new ArrayList<KeyDataBean>();
        }
        this.keyDataList.add(keyData);
    }

    public final ArrayList<DSDataBean> getDsDataList() {
        return dsDataList;
    }

    public final void setDsDataList(ArrayList<DSDataBean> dsDataList) {
        this.dsDataList = dsDataList;
    }

    public final ArrayList<KeyDataBean> getKeyDataList() {
        return keyDataList;
    }

    public final void setKeyDataList(ArrayList<KeyDataBean> keyDataList) {
        this.keyDataList = keyDataList;
    }

    public final boolean isRemoveAll() {
        return removeAll;
    }

    public final void setRemoveAll(boolean removeAll) {
        this.removeAll = removeAll;
    }

    /**
     * Adds the DNSSEC details to the remove element provided, if remove all or DS data or Key Data
     * has been provided.
     * Used internally for DNSSEC command Extension for domain update command.
     *
     * @param removeElement the remove element
     */
    public void createXMLElement(final Element removeElement) {

        if (isRemoveAll()) {
            XMLUtil.appendChildElement(removeElement, "all", isRemoveAll());
        }

        if (getDsDataList() != null) {
            for (DSDataBean dsData : getDsDataList()) {
                dsData.appendDsDataToElement(removeElement);
            }
        }

        if (getKeyDataList() != null) {
            for (KeyDataBean keyData : this.getKeyDataList()) {
                keyData.appendKeyDataToElement(removeElement);
            }
        }
    }

}
