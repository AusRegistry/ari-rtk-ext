import java.io.InputStream;
import java.util.List;

import ari.dnrs.rtk.addon.bean.IdnDomainVariant;
import ari.dnrs.rtk.addon.extensions.idn.DomainIdnCommandExtension;
import ari.dnrs.rtk.addon.extensions.variant.DomainVariantResponseExtensionV1_1;


/**
 * The class to process response XMLs and print out the data collected from the extension responses.
 * In the case of multiple extensions coming back in the response, re run the test for each of the extensions.
 *
 * A new method needs to be added to the class for each new extension response handling added to the tool kit.
 * 
 */
public class ManualResponseTest {

    //Valid extension types can be: variant, idn, kv
    static String extensionType = "idn";
    
    public static void main(String[] args) throws Exception {
        System.out.println("[" + extensionType + " Test Started]");
        if(extensionType == null) {
            System.out.println("No argument provided. Please specify command extension type [variant, idn, kv]");
        }
        
        //Load response.xml as String
        final InputStream responseStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("response.xml");
        String responseXML = new java.util.Scanner(responseStream).useDelimiter("\\A").next();
        
        if ("variant".equals(extensionType)) {
            testVariantExtension(responseXML);
        }
        
        if ("idn".equals(extensionType)) {
            testIdnExtension(responseXML);
        }
        
        System.out.println("[Test Complete]");
    }

    /**
     * Perform the fromXML operation of the DomainIdnCommandExtension and output
     * the values that have been read for verification
     * 
     * @param responseXML XML containing a EPP response with a IDN extension
     * @throws Exception if any exception occurs during the test 
     */
    private static void testIdnExtension(String responseXML) throws Exception {
        final DomainIdnCommandExtension idnExtension = new DomainIdnCommandExtension();
        try {
            System.out.println("[Attempting to read XML]");
            idnExtension.fromXML(responseXML);
        } catch (Exception e) {
            System.out.println("[XML was not able to be read correctly by command extension]");
            throw e;
        }
        System.out.println("[Reading XML completed]");

        System.out.println("[Printing populated values]");
        final String userFormLanguage = idnExtension.getLanguageTag();

        System.out.println("[Language: " + userFormLanguage + "]");
        System.out.println("[Printing complete]");
    }

    /**
     * Perform the fromXML operation of the DomainVariantCommandExtension and output
     * the values that have been read for verification
     * 
     * @param responseXML XML containing a EPP response with a variant extension
     * @throws Exception if any exception occurs during the test 
     */
    private static void testVariantExtension(String responseXML) throws Exception {
        final DomainVariantResponseExtensionV1_1 variantExtension = new DomainVariantResponseExtensionV1_1();
        try {
            System.out.println("[Attempting to read XML]");
            variantExtension.fromXml(responseXML);
        } catch (Exception e) {
            System.out.println("[XML was not able to be read correctly by command extension]");
            throw e;
        }
        System.out.println("[Reading XML completed]");

        System.out.println("[Printing populated values]");
        final List<IdnDomainVariant> variantList = variantExtension.getVariants();
        int count = 1;
        for (IdnDomainVariant variant : variantList) {
            System.out.println("Variant " + count++ + ":");
            System.out.println("DNS form: " + variant.getName());
        }
        
        System.out.println("[Printing complete]");
    }
}
