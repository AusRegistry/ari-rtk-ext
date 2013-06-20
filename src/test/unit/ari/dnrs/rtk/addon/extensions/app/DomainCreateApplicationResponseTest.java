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
            "<phase>phaseName</phase>" + //
            "</creData>" + //
            "</resData>" + //
            "<trID>" + //
            "<clTRID>ABC-12345</clTRID>" + //
            "<svTRID>54321-XYZ</svTRID>" + //
            "</trID>" + //
            "</response>" + //
            "</epp>";

    @Test
    public void shouldGetApplicationIdAndNameAndCreateDateFromResponse() throws DatatypeConfigurationException {
        DomainCreateApplicationResponse domainCreateApplicationResponse = new DomainCreateApplicationResponse();
        domainCreateApplicationResponse.fromXML(DOMAIN_APPLICATION_CREATE_RESPONSE);

        assertThat(domainCreateApplicationResponse.getApplicationId(), is("sunrise-application-id"));
        assertThat(domainCreateApplicationResponse.getPhase(), is("phaseName"));
    }

}
