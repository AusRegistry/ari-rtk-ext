package ari.dnrs.rtk.addon.bean;

import java.io.Serializable;

import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ari.dnrs.rtk.addon.utils.XMLUtil;

/**
 * Models the &lt;keyData&gt; element as documented in RFC5910.
 */
public class KeyDataBean implements Serializable {

    private static final long serialVersionUID = -5073494654814738965L;

    private int algorithm;
    private int flags;
    private int protocol;
    private String publicKey;

    public KeyDataBean() {
    }

    public KeyDataBean(final int flags, final int protocol, final int algorithm, final String publicKey) {
        this.flags = flags;
        this.protocol = protocol;
        this.algorithm = algorithm;
        this.publicKey = publicKey;
    }

    /**
     * Appends the key data element to the provided parentElement.
     * Used internally by DNSSEC extension for Domain Update and Domain Create commands.
     * 
     * @param parentElement
     */
    public void appendKeyDataToElement(final Element parentElement) {
        final Element keyDataElement = XMLUtil.appendChildElement(parentElement, "keyData");
        XMLUtil.appendChildElement(keyDataElement, "flags", flags);
        XMLUtil.appendChildElement(keyDataElement, "protocol", protocol);
        XMLUtil.appendChildElement(keyDataElement, "alg", algorithm);
        XMLUtil.appendChildElement(keyDataElement, "pubKey", publicKey);
    }

    public int getFlags() {
        return flags;
    }

    /**
     * Sets the flags. This must be between 0 and 65535.
     *
     * @param flags the new flags
     * @throws epp_XMLException if the provided flags value is not in valid range.
     */
    public void setFlags(final int flags) throws epp_XMLException {
        if (flags < 0) {
            throw new epp_XMLException("Flags must be between 0 and 65535.");
        }
        if (flags > 65535) {
            throw new epp_XMLException("Flags must be between 0 and 65535.");
        }
        this.flags = flags;
    }

    public int getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol. This must be between 0 and 255.
     *
     * @param protocol the new protocol
     * @throws epp_XMLException if the provided protocol value is not in valid range.
     */
    public void setProtocol(final int protocol) throws epp_XMLException {
        if (protocol < 0) {
            throw new epp_XMLException("Protocol must be between 0 and 255.");
        }
        if (protocol > 255) {
            throw new epp_XMLException("Protocol must be between 0 and 255.");
        }
        this.protocol = protocol;
    }

    public int getAlgorithm() {
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

    public String getPubKey() {
        return publicKey;
    }

    /**
     * Sets the public key. This should not be an empty string.
     *
     * @param publicKey the new public key
     * @throws epp_XMLException if the provided publicKey is an empty string.
     */
    public void setPubKey(final String publicKey) throws epp_XMLException {
        if (publicKey.length() < 1) {
            throw new epp_XMLException("Public key must not be empty.");
        }
        this.publicKey = publicKey;
    }

    public static KeyDataBean fromXML(Node keyDataNode) {
        NodeList keyDataNodes = keyDataNode.getChildNodes();

        final Node flags = keyDataNodes.item(0).getFirstChild();
        final Node protocol = keyDataNodes.item(1).getFirstChild();
        final Node algorithm = keyDataNodes.item(2).getFirstChild();
        final Node pubKey = keyDataNodes.item(3).getFirstChild();

        final KeyDataBean keyData = new KeyDataBean(Integer.valueOf(flags.getNodeValue()), Integer.valueOf(protocol
                .getNodeValue()), Integer.valueOf(algorithm.getNodeValue()), pubKey.getNodeValue());

        return keyData;
    }

}
