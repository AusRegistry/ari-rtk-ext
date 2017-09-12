package ari.dnrs.rtk.addon.extensions.idn;

import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ari.dnrs.rtk.addon.utils.XMLNamespaces;

import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLBase;

/**
 * Sets the IDN Domain extension properties for an EPP Domain Create command or
 * retrieve the IDN Domain extension properties from an EPP Domain Info command.
 */
public final class DomainIetfIdnCommandExtension extends EPPXMLBase implements epp_Extension {

    private String table;
    private String uname;

	/**
	 * @param table The IDN language. Required.
	 * @param uname the domain name in Unicode format, optional parameter.
	 * @throws IllegalArgumentException if {@code table} is {@code null} or empty.
	 */

	public DomainIetfIdnCommandExtension(String table, String uname)
	{
		if (table == null || table.isEmpty())
		{
			throw new IllegalArgumentException("Language must not be null or empty");
		}
		this.table = table;
		this.uname = uname;
	}

    @Override
    public String toXML() throws epp_XMLException {
        Document extensionDoc = new DocumentImpl();

        Element commandElement = extensionDoc.createElement("idn:data");
        commandElement.setAttribute("xmlns:idn", XMLNamespaces.IETF_IDN_NAMESPACE);

        Element tableElement = extensionDoc.createElement("idn:table");
        tableElement.appendChild(extensionDoc.createTextNode(table));
        commandElement.appendChild(tableElement);

		if(uname != null) {
			Element unameElement = extensionDoc.createElement("idn:uname");
			unameElement.appendChild(extensionDoc.createTextNode(uname));
			commandElement.appendChild(unameElement);
		}

        extensionDoc.appendChild(commandElement);

        String idnExtensionXML = null;
        try {
            idnExtensionXML = createXMLSnippetFromDoc(extensionDoc);
        } catch (final IOException e) {
            throw new epp_XMLException("IOException occured while creating idn extension XML.\n" + e.getMessage());
        }
        return idnExtensionXML;
    }

    @Override
    public void fromXML(String xml) throws epp_XMLException {

    }
}
