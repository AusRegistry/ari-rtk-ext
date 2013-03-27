package ari.dnrs.rtk.addon.utils;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The XML utility class providing XML manipulation functionalities used by the ARI extensions add on.
 */
public class XMLUtil {

    /**
     * Appends a child element to the provided parent element.
     * This child element is created with the name and integer value provided.
     *
     * @param parentElement the parent element
     * @param name the name for the child element to be created
     * @param value the integer value to be assigned to the created child element
     * @return the newly created child element 
     */
    public static Element appendChildElement(final Element parentElement, final String name, final int value) {
        return appendChildElement(parentElement, name, "" + value);
    }

    /**
     * Appends a child element to the provided parent element.
     * This child element is created with the name and boolean value provided.
     *
     * @param parentElement the parent element
     * @param name the name for the child element to be created
     * @param value the boolean value to be assigned to the created child element
     * @return the newly created child element
     */
    public static Element appendChildElement(final Element parentElement, final String name, final boolean value) {
        return appendChildElement(parentElement, name, value ? "true" : "false");
    }

    /**
     * Appends an empty child element to the provided parent element.
     * This child element is created with the name provided.
     *
     * @param parentElement the parent element
     * @param name the name for the child element to be created
     * @return the newly created empty child element 
     */
    public static Element appendChildElement(final Element parentElement, final String name) {
        return appendChildElement(parentElement, name, null);
    }

    /**
     * Appends a child element to the provided parent element.
     * This child element is created with the name and string value provided.
     *
     * @param parentElement the parent element
     * @param name the name for the child element to be created
     * @param value the string value to be assigned to the created child element
     * @return the newly created child element 
     */
    public static Element appendChildElement(final Element parentElement, final String name, final String value) {
        Document parentDocument = parentElement.getOwnerDocument();
        Element createElement = createElement(parentDocument, name);
        if (value != null) {
            createElement.appendChild(parentDocument.createTextNode(value));
        }
        parentElement.appendChild(createElement);
        return createElement;
    }

    private static Element createElement(final Document parentDocument, final String name) {
        return parentDocument.createElement(name);
    }

    public static GregorianCalendar fromXSDateTime(String dateTime) throws DatatypeConfigurationException {
        GregorianCalendar cal = null;

        if (dateTime != null && dateTime.length() != 0) {
            cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime).toGregorianCalendar();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        return cal;
    }
}
