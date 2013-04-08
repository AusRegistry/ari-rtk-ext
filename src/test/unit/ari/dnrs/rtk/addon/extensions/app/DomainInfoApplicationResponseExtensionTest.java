package ari.dnrs.rtk.addon.extensions.app;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DomainInfoApplicationResponseExtensionTest {

    @Test
    public void shouldReturnApplicationDetailsForInfoCommand() {
        final String dnsForm = "test-domain";
        final DomainInfoApplicationResponseExtension appExtension = new DomainInfoApplicationResponseExtension();
        String applicationId = "sunrise-application-id";
        String phase = "sunrise";
        List<String> statuses = new ArrayList<String>();
        statuses.add("ok");
        statuses.add("pendingOutcome");
        statuses.add("deleteProhibited");
        statuses.add("updateProhibited");
        appExtension.fromXML(getInfoResponseExpectedXml(dnsForm, applicationId, phase, statuses));

        assertTrue("Application extension should have been initialised", appExtension.isInitialised());
        assertThat(appExtension.getApplicationId(), is(applicationId));
        assertThat(appExtension.getPhase(), is(phase));
        assertThat(appExtension.getStatuses(), is(statuses));
    }

    private String getInfoResponseExpectedXml(final String domainName, final String applicationId, final String phase,
                                              final List<String> statuses) {
        final StringBuilder result = new StringBuilder();
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        result.append("<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\"");
        result.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        result.append(" xsi:schemaLocation=\"urn:ietf:params:xml:ns:epp-1.0 epp-1.0.xsd\">");
        result.append("<response>");
        result.append("<result code=\"1000\">");
        result.append("<msg>Command completed successfully</msg>");
        result.append("</result>");
        result.append("<resData>");
        result.append("<infData xmlns=\"urn:ietf:params:xml:ns:domain-1.0\"");
        result.append(" xsi:schemaLocation=\"urn:ietf:params:xml:ns:domain-1.0 domain-1.0.xsd\">");
        result.append("<name>" + domainName + "</name>");
        result.append("<roid>D0000003-AR</roid>");
        result.append("<status s=\"ok\" lang=\"en\"/>");
        result.append("<registrant>EXAMPLE</registrant>");
        result.append("<contact type=\"tech\">EXAMPLE</contact>");
        result.append("<ns>");
        result.append("<hostObj>ns1.example.com.au</hostObj>");
        result.append("<hostObj>ns2.example.com.au</hostObj>");
        result.append("</ns>");
        result.append("<host>ns1.example.com.au</host>");
        result.append("<host>ns2.exmaple.com.au</host>");
        result.append("<clID>Registrar</clID>");
        result.append("<crID>Registrar</crID>");
        result.append("<crDate>2006-02-09T15:44:58.0Z</crDate>");
        result.append("<exDate>2008-02-10T00:00:00.0Z</exDate>");
        result.append("<authInfo>");
        result.append("<pw>0192pqow</pw>");
        result.append("</authInfo>");
        result.append("</infData>");
        result.append("</resData>");

        result.append("<extension>");
        result.append("<app:infData xmlns:app=\"urn:ar:params:xml:ns:application-1.0\"");
        result.append(" xsi:schemaLocation=\"urn:ar:params:xml:ns:application-1.0 application-1.0.xsd\">");
        result.append("<app:id>" + applicationId + "</app:id>");
        result.append("<app:phase>" + phase + "</app:phase>");
        for (String status : statuses) {
            result.append("<app:status s=\"" + status + "\" />");
        }
        result.append("</app:infData>");
        result.append("</extension>");

        result.append("<trID>");
        result.append("<clTRID>ABC-12345</clTRID>");
        result.append("<svTRID>54321-XYZ</svTRID>");
        result.append("</trID>");
        result.append("</response>");
        result.append("</epp>");

        return result.toString();
    }
}
