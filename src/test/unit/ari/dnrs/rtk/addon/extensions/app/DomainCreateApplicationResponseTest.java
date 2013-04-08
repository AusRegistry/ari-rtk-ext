package ari.dnrs.rtk.addon.extensions.app;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class DomainCreateApplicationResponseTest {
    String DOMAIN_APPLICATION_CREATE_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
            "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\"" + //
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + //
            " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">" + //
            "<response>" + //
            "<result code=\"1000\">" + //
            "<msg>Command completed successfully</msg>" + //
            "</result>" + //
            "<resData>" + //
            "<creData xmlns=\"urn:ar:params:xml:ns:application-1.0\" >" + //
            "<id>sunrise-application-id</id>" + //
            "<name>domain.zone</name>" + //
            "<crDate>2011-01-01T00:00:00Z</crDate>" + //
            "</creData>" + //
            "</resData>" + //
            "<trID>" + //
            "<clTRID>ABC-12345</clTRID>" + //
            "<svTRID>54321-XYZ</svTRID>" + //
            "</trID>" + //
            "</response>" + //
            "</epp>";

    String DOMAIN_APPLICATION_CREATE_RESPONSE_NO_CR_DATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
            "<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\"" + //
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + //
            " xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">" + //
            "<response>" + //
            "<result code=\"1000\">" + //
            "<msg>Command completed successfully</msg>" + //
            "</result>" + //
            "<resData>" + //
            "<creData xmlns=\"urn:ar:params:xml:ns:application-1.0\" >" + //
            "<id>sunrise-application-id</id>" + //
            "<name>domain.zone</name>" + //
            "</creData>" + //
            "</resData>" + //
            "<trID>" + //
            "<clTRID>ABC-12345</clTRID>" + //
            "<svTRID>54321-XYZ</svTRID>" + //
            "</trID>" + //
            "</response>" + //
            "</epp>";

    private DomainCreateApplicationResponse domainCreateApplicationResponse;

    @Before
    public void setUp() {
        domainCreateApplicationResponse = new DomainCreateApplicationResponse();
    }

    @Test
    public void shouldGetApplicationIdAndNameAndCreateDateFromResponse() throws DatatypeConfigurationException {
        domainCreateApplicationResponse.fromXML(DOMAIN_APPLICATION_CREATE_RESPONSE_NO_CR_DATE);

        assertThat(domainCreateApplicationResponse.getApplicationId(), is("sunrise-application-id"));
        assertThat(domainCreateApplicationResponse.getName(), is("domain.zone"));
        assertNull(domainCreateApplicationResponse.getCreateDate());
    }

}
