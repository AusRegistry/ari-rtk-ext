package ari.dnrs.rtk.addon.extensions.kv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openrtk.idl.epprtk.epp_XMLException;

import ari.dnrs.rtk.addon.bean.DomainKeyValueBean;
import ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension;
import ari.dnrs.rtk.addon.utils.XMLNamespaces;

/**
 * Unit test for response elements in {@link ari.dnrs.rtk.addon.extensions.kvlist.DomainKVCommandExtension}.
 */
public class DomainKVResponseExtensionTest {

    /** The Expected Exception Rule. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DomainKVCommandExtension kvExtension;
    private String responseXML;

    private String defaultKey1;
    private String defaultKey2;
    private String defaultKey3;
    private String defaultValue1;
    private String defaultValue2;
    private String defaultValue3;

    @Before
    public void setUp() {
        kvExtension = new DomainKVCommandExtension(null);
        defaultKey1 = "eligibilityType";
        defaultKey2 = "policyReason";
        defaultKey3 = "registrantName";
        defaultValue1 = "Trademark";
        defaultValue2 = "1";
        defaultValue3 = "AusRegistry";
    }

    /**
     * Should parse info data response XML and instantiate expected KV list in the extension object.
     *
     * @throws epp_XMLException if an epp_XMLException is thrown by the test
     */
    @Test
    public void shouldParseInfDataResponseWithASingleKVList() throws epp_XMLException {
        final String listName = "ae";

        responseXML ="<extension><infData xmlns=\"" + XMLNamespaces.KVLIST_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.KVLIST_NAMESPACE + " \">"
                +  getDefaultKVListXML(listName)
                + "</infData></extension>";
        kvExtension.fromXML(responseXML);

        final HashMap<String, ArrayList<DomainKeyValueBean>> keyValueLists = kvExtension.getKeyValueLists();
        assertNotNull("Should populate response object", keyValueLists);
        assertFalse("Key value lists should have at least one entry", keyValueLists.isEmpty());

        verifyKeyValueList(keyValueLists.get(listName));
    }

    /**
     * Should parse info data response XML with multiple KV lists and instantiate an extension object.
     *
     * @throws epp_XMLException if an epp_XMLException is thrown by the test
     */
    @Test
    public void shouldParseInfDataResponseWithAMultipleKVLists() throws epp_XMLException {
        final String listName1 = "ae";
        final String listName2 = "au";
        final String listName3 = "ru";

        responseXML ="<extension><infData xmlns=\"" + XMLNamespaces.KVLIST_NAMESPACE + "\""
                + " xsi:schemaLocation=\"" + XMLNamespaces.KVLIST_NAMESPACE + " \">"
                + getDefaultKVListXML(listName1)
                + getDefaultKVListXML(listName2)
                + getDefaultKVListXML(listName3)
                + "</infData></extension>";
        kvExtension.fromXML(responseXML);

        final HashMap<String, ArrayList<DomainKeyValueBean>> keyValueLists = kvExtension.getKeyValueLists();
        assertNotNull("Should populate response object", keyValueLists);
        assertFalse("Key value lists should have at least one entry", keyValueLists.isEmpty());

        verifyKeyValueList(keyValueLists.get(listName1));
        verifyKeyValueList(keyValueLists.get(listName2));
        verifyKeyValueList(keyValueLists.get(listName3));
    }

    private String getDefaultKVListXML(final String listName) {
        return "<kvlist name=\"" + listName + "\">"
                + "<item key=\"" + defaultKey1 + "\">" + defaultValue1 + "</item>"
                + "<item key=\"" + defaultKey2 + "\">" + defaultValue2 + "</item>"
                + "<item key=\"" + defaultKey3 + "\">" + defaultValue3 + "</item>"
                + "</kvlist>";
    }

    private void verifyKeyValueList(final ArrayList<DomainKeyValueBean> keyValueList) {
        assertNotNull("Should contain expected list", keyValueList);

        assertEquals("Should return expected key", keyValueList.get(0).getKey(), defaultKey1);
        assertEquals("Should return expected value", keyValueList.get(0).getValue(), defaultValue1);

        assertEquals("Should return expected key", keyValueList.get(1).getKey(), defaultKey2);
        assertEquals("Should return expected value", keyValueList.get(1).getValue(), defaultValue2);

        assertEquals("Should return expected key", keyValueList.get(2).getKey(), defaultKey3);
        assertEquals("Should return expected value", keyValueList.get(2).getValue(), defaultValue3);
    }
}
