package ari.dnrs.rtk.addon.extensions.variant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openrtk.idl.epprtk.domain.epp_DomainUpdateReq;
import org.openrtk.idl.epprtk.epp_Command;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate;

import ari.dnrs.rtk.addon.bean.IdnDomainVariant;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DomainUpdateVariantCommandExtensionV1_1Test {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final static String DOMAIN_UPDATE_VARIANT_ADD_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<update>"
            + "<domain:update xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>example.com</domain:name>"
            + "</domain:update>"
            + "</update>"
            + "<extension>"
            + "<update xmlns=\"urn:ar:params:xml:ns:variant-1.1\">"
            + "<add>"
            + "<variant>xn--4xa1.example</variant>"
            + "<variant>xn--4xa2.example</variant>"
            + "<variant>xn--4xa3.example</variant>"
            + "</add>"
            + "</update>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private final static String DOMAIN_UPDATE_VARIANT_REM_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<update>"
            + "<domain:update xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>example.com</domain:name>"
            + "</domain:update>"
            + "</update>"
            + "<extension>"
            + "<update xmlns=\"urn:ar:params:xml:ns:variant-1.1\">"
            + "<rem>"
            + "<variant>xn--4xa1.example</variant>"
            + "<variant>xn--4xa2.example</variant>"
            + "<variant>xn--4xa3.example</variant>"
            + "</rem>"
            + "</update>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private final static String DOMAIN_UPDATE_VARIANT_COMPO_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">"
            + "<command>"
            + "<update>"
            + "<domain:update xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" "
            + "xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">"
            + "<domain:name>example.com</domain:name>"
            + "</domain:update>"
            + "</update>"
            + "<extension>"
            + "<update xmlns=\"urn:ar:params:xml:ns:variant-1.1\">"
            + "<rem>"
            + "<variant>xn--3dfh6.example</variant>"
            + "<variant>xn--ii96.example</variant>"
            + "</rem>"
            + "<add>"
            + "<variant>xn--4xal.example</variant>"
            + "<variant>xn--3uyi3.example</variant>"
            + "<variant>xn--3xxr5.example</variant>"
            + "</add>"
            + "</update>"
            + "</extension>"
            + "<clTRID>JTKUTEST.20070101.010101.0</clTRID>"
            + "</command>"
            + "</epp>";

    private epp_Command eppCommand;
    private epp_DomainUpdateReq domainUpdateReq;
    private DomainUpdateVariantCommandExtensionV1_1 extension;

    @Before
    public void setUp() {
        eppCommand = new epp_Command();
        eppCommand.setClientTrid("JTKUTEST.20070101.010101.0");

        domainUpdateReq = new epp_DomainUpdateReq();
        domainUpdateReq.setName("example.com");
        domainUpdateReq.setCmd(eppCommand);

        extension = new DomainUpdateVariantCommandExtensionV1_1();
    }

    @Test
    public void shouldGenerateXmlWithAddVariantExtension() throws Exception {
        extension.addVariant(new IdnDomainVariant("xn--4xa1.example"), new IdnDomainVariant("xn--4xa2.example"),
                new IdnDomainVariant("xn--4xa3.example"));

        eppCommand.setExtensions(new epp_Extension[]{extension});

        EPPDomainUpdate eppDomainCheck = new EPPDomainUpdate();
        eppDomainCheck.setRequestData(domainUpdateReq);

        assertThat(eppDomainCheck.toXML(), is(DOMAIN_UPDATE_VARIANT_ADD_XML));
    }

    @Test
    public void shouldGenerateXmlWithRemVariantExtension() throws Exception {
        extension.removeVariant(new IdnDomainVariant("xn--4xa1.example"), new IdnDomainVariant("xn--4xa2.example"),
                new IdnDomainVariant("xn--4xa3.example"));

        eppCommand.setExtensions(new epp_Extension[]{extension});

        EPPDomainUpdate eppDomainCheck = new EPPDomainUpdate();
        eppDomainCheck.setRequestData(domainUpdateReq);

        assertThat(eppDomainCheck.toXML(), is(DOMAIN_UPDATE_VARIANT_REM_XML));
    }

    @Test
    public void shouldGenerateXmlWithMultipleAddAndRemVariantExtension() throws Exception {
        extension.addVariant(new IdnDomainVariant("xn--4xal.example"),
                new IdnDomainVariant("xn--3uyi3.example"), new IdnDomainVariant("xn--3xxr5.example"));
        extension.removeVariant(new IdnDomainVariant("xn--3dfh6.example"),
                new IdnDomainVariant("xn--ii96.example"));

        eppCommand.setExtensions(new epp_Extension[]{extension});

        EPPDomainUpdate eppDomainCheck = new EPPDomainUpdate();
        eppDomainCheck.setRequestData(domainUpdateReq);

        assertThat(eppDomainCheck.toXML(), is(DOMAIN_UPDATE_VARIANT_COMPO_XML));
    }

    @Test
    public void shouldThrowExceptionWhenAddingNullVariantExtension() throws epp_XMLException {
        thrown.expect(epp_XMLException.class);

        try {
            extension.addVariant(new IdnDomainVariant("something.com"), null);
            fail("should throw exception");
        } catch (epp_XMLException exception) {
            assertThat(exception.getErrorMessage(), is("The domain variant is a required parameter."));
            throw exception;
        }
    }

    @Test
    public void shouldThrowExceptionWhenRemovingNullVariantExtension() throws epp_XMLException {
        thrown.expect(epp_XMLException.class);

        try {
            extension.removeVariant(new IdnDomainVariant("something.com"), null);
            fail("should throw exception");
        } catch (epp_XMLException exception) {
            assertThat(exception.getErrorMessage(), is("The domain variant is a required parameter."));
            throw exception;
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingVariantWithNullName() throws epp_XMLException {
        thrown.expect(epp_XMLException.class);

        try {
            extension.addVariant(new IdnDomainVariant(null));
            fail("should throw exception");
        } catch (epp_XMLException exception) {
            assertThat(exception.getErrorMessage(), is("The domain variant DNS form is a required parameter."));
            throw exception;
        }
    }

    @Test
    public void shouldThrowExceptionWhenRemovingVariantWithNullName() throws epp_XMLException {
        thrown.expect(epp_XMLException.class);

        try {
            extension.removeVariant(new IdnDomainVariant(null));
            fail("should throw exception");
        } catch (epp_XMLException exception) {
            assertThat(exception.getErrorMessage(), is("The domain variant DNS form is a required parameter."));
            throw exception;
        }
    }

}
