package ari.dnrs.rtk.addon.extensions.kvlist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ari.dnrs.rtk.addon.bean.DomainKeyValueBean;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Supports the functionality around the ARI key-value pair EPP Extension.
 * Can be used to set the Key value Domain extension properties for an EPP Domain Create command and EPP Domain Update 
 * command or retrieve the Key value Domain extension properties from an EPP Domain Info command.
 */
public class DomainKVCommandExtension extends EPPXMLBase implements epp_Extension {

    private final String command;
    private final HashMap<String, ArrayList<DomainKeyValueBean>> keyValueLists;

    private static final long serialVersionUID = 7106175195620038581L;

    public DomainKVCommandExtension(final String command) {
        this.command = command;
        keyValueLists = new LinkedHashMap<String, ArrayList<DomainKeyValueBean>>();
    }

    /**
     * Adds a new item to a key value list. Creates a new list with the given name if it is not present.
     *
     * @param listName the name of the list that will contain the new key value pair
     * @param key the key for the new key-value pair
     * @param value the value for the new key-value pair
     */
    public void addKeyValuePairToList(final String listName, final String key, final String value) {
        ArrayList<DomainKeyValueBean> keyValueItems = keyValueLists.get(listName);
        if (keyValueItems == null) {
            keyValueItems = new ArrayList<DomainKeyValueBean>();
            keyValueLists.put(listName, keyValueItems);
        }

        final DomainKeyValueBean keyValue = new DomainKeyValueBean(key, value);
        keyValueItems.add(keyValue);
    }

    /**
     * Gets key value lists currently held by the extension object.
     *
     * @return HashMap mapping the list name to a list of key value pairs.
     */
    public HashMap<String, ArrayList<DomainKeyValueBean>> getKeyValueLists() {
        return keyValueLists;
    }

    @Override
    public String toXML() throws epp_XMLException {

        if (command == null || command == "" || keyValueLists.size() == 0) {
            throw new epp_XMLException("Should provide command and at least one kvlist to construct kv extension XML");
        }

        final Document extensionDoc = new DocumentImpl();
        final Element commandElement = extensionDoc.createElement(command);
        commandElement.setAttribute("xmlns", XMLNamespaces.KVLIST_NAMESPACE);

        for (final Entry<String, ArrayList<DomainKeyValueBean>> keyValueList : keyValueLists.entrySet()) {
            final Element kvList = extensionDoc.createElement("kvlist");
            kvList.setAttribute("name", keyValueList.getKey());

            for (final DomainKeyValueBean keyValueItem : keyValueList.getValue()) {
                final Element kvItem = extensionDoc.createElement("item");
                kvItem.setAttribute("key", keyValueItem.getKey());
                kvItem.appendChild(extensionDoc.createTextNode(keyValueItem.getValue()));
                kvList.appendChild(kvItem);
            }
            commandElement.appendChild(kvList);
        }

        extensionDoc.appendChild(commandElement);
        String kvExtensionXML = null;
        try {
            kvExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating kv extension XML.\n" + e.getMessage());
        }
        return kvExtensionXML;
    }

    @Override
    public void fromXML(final String xml) throws epp_XMLException {
        if (xml == null || xml.length() == 0) {
            return;
        }

        try {
            xml_ = xml;

            final Element kvListNode = getDocumentElement();
            final NodeList extensionNodes = kvListNode.getElementsByTagNameNS(XMLNamespaces.KVLIST_NAMESPACE, "*");
            if (extensionNodes.getLength() == 0) {
                return;
            }

            for (int listCount = 0; listCount < extensionNodes.getLength(); listCount++) {
                final Node kvList = extensionNodes.item(listCount);

                if (kvList.getNodeName().equals("kvlist")) {

                    //Retrieve name of current key value list
                    final NamedNodeMap listAttributes = kvList.getAttributes();
                    final String listName = listAttributes.getNamedItem("name").getNodeValue();

                    final NodeList keyValuePairs = kvList.getChildNodes();
                    for (int pairCount = 0; pairCount < keyValuePairs.getLength(); pairCount++) {

                        //Retrieve value from current key value list pair
                        final Node keyValuePair = keyValuePairs.item(pairCount);
                        final String value = keyValuePair.getFirstChild().getNodeValue();

                        //Retrieve key from current key value list pair
                        final NamedNodeMap keyAttribute = keyValuePair.getAttributes();
                        final String key = keyAttribute.getNamedItem("key").getNodeValue();

                        addKeyValuePairToList(listName, key, value);
                    }
                }
            }
        } catch (final Exception e) {
            throw new epp_XMLException("Unable to parse XML [" + e.getClass().getName() + "] [" + e.getMessage() + "]");
        }
    }
}
