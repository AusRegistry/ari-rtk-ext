package ari.dnrs.rtk.addon.bean;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ari.dnrs.rtk.addon.utils.XMLUtil;

/**
 * Models the &lt;dsData&gt; element as documented in RFC5910.
 */
public class DSDataBean implements Serializable {

    private static final long serialVersionUID = 3729382972073559741L;

    private static final Pattern DIGEST_VALIDATION_PATTERN = Pattern.compile("^[A-Fa-f0-9]+$");

    private int keyTag;
    private int algorithm;
    private int digestType;
    private String digest;
    private KeyDataBean keyData;

    public DSDataBean() {
    }

    public DSDataBean(final int keyTag, final int algorithm, final int digestType, final String digest) {
        this.keyTag = keyTag;
        this.algorithm = algorithm;
        this.digestType = digestType;
        this.digest = digest;
    }

    /**
     * Appends the DS data element to the provided parentElement.
     * Used internally by DNSSEC extension for Domain Update and Domain Create commands.
     * 
     * @param parentElement
     */
    public void appendDsDataToElement(final Element parentElement) {
        final Element dsDataElement = XMLUtil.appendChildElement(parentElement, "dsData");
        XMLUtil.appendChildElement(dsDataElement, "keyTag", keyTag);
        XMLUtil.appendChildElement(dsDataElement, "alg", algorithm);
        XMLUtil.appendChildElement(dsDataElement, "digestType", digestType);
        XMLUtil.appendChildElement(dsDataElement, "digest", digest);
        if (keyData != null) {
            keyData.appendKeyDataToElement(dsDataElement);
        }
    }

    public int getKeyTag() {
        return keyTag;
    }

    /**
     * Sets the key tag value. This must be between 0 and 65535.
     *
     * @param keyTag the new key tag value
     * @throws epp_XMLException if the provided keyTag value is not in valid range.
     */
    public void setKeyTag(final int keyTag) throws epp_XMLException {
        if (keyTag < 0) {
            throw new epp_XMLException("Key tag must be between 0 and 65535.");
        }
        if (keyTag > 65535) {
            throw new epp_XMLException("Key tag must be between 0 and 65535.");
        }
        this.keyTag = keyTag;
    }

    public int getAlg() {
        return algorithm;
    }

    /**
     * Sets the algorithm. This must be between 0 and 255.
     *
     * @param algorithm the new algorithm
     * @throws epp_XMLException if the provided algorithm value is not in valid range.
     */
    public void setAlgorithm(final int algorithm) throws epp_XMLException {
        if (algorithm < 0) {
            throw new epp_XMLException("Algorithm must be between 0 and 255.");
        }
        if (algorithm > 255) {
            throw new epp_XMLException("Algorithm must be between 0 and 255.");
        }
        this.algorithm = algorithm;
    }

    public int getDigestType() {
        return digestType;
    }

    /**
     * Sets the digest type. This must be between 0 and 255.
     *
     * @param digestType the new digest type
     * @throws epp_XMLException if the provided digest type value is not in valid range.
     */
    public void setDigestType(final int digestType) throws epp_XMLException {
        if (digestType < 0) {
            throw new epp_XMLException("Digest type must be between 0 and 255.");
        }
        if (digestType > 255) {
            throw new epp_XMLException("Digest type must be between 0 and 255.");
        }
        this.digestType = digestType;
    }

    public String getDigest() {
        return digest;
    }
    
    /**
     * Sets the digest value. This value should be a XML hexBinary value.
     *
     * @param digest the new digest
     * @throws epp_XMLException if the value is not a XML hexBinary value
     */
    public void setDigest(final String digest) throws epp_XMLException {
        final Matcher matcher = DIGEST_VALIDATION_PATTERN.matcher(digest);
        if (matcher.matches() && digest.length() % 2 == 0) {
            this.digest = digest.toUpperCase();
        } else {
            throw new epp_XMLException("Digest was not valid.");
        }
    }

    public KeyDataBean getKeyData() {
        return keyData;
    }

    public void setKeyData(final KeyDataBean keyData) {
        this.keyData = keyData;
    }

    public static DSDataBean fromXML(Node dsDataNode) {
        final NodeList dsDataNodes = dsDataNode.getChildNodes();

        final Node keyTag = dsDataNodes.item(0).getFirstChild();
        final Node flags = dsDataNodes.item(1).getFirstChild();
        final Node algorithm = dsDataNodes.item(2).getFirstChild();
        final Node digest = dsDataNodes.item(3).getFirstChild();

        final DSDataBean dsData = new DSDataBean(Integer.valueOf(keyTag.getNodeValue()),
                Integer.valueOf(flags.getNodeValue()), Integer.valueOf(algorithm.getNodeValue()),
                digest.getNodeValue());

        if (dsDataNodes.getLength() > 4) {
            final Node keyDataNode = dsDataNodes.item(4);
            dsData.setKeyData(KeyDataBean.fromXML(keyDataNode));
        }

        return dsData;
    }

}
