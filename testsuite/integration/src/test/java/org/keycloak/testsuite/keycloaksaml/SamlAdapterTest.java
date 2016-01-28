package org.keycloak.testsuite.keycloaksaml;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SamlAdapterTest {

    @ClassRule
    public static SamlKeycloakRule keycloakRule = new SamlKeycloakRule() {
        @Override
        public void initWars() {
             ClassLoader classLoader = SamlAdapterTest.class.getClassLoader();

            initializeSamlSecuredWar("/keycloak-saml/simple-post", "/sales-post",  "post.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/simple-post-passive", "/sales-post-passive", "post-passive.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/signed-post", "/sales-post-sig",  "post-sig.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/signed-post-email", "/sales-post-sig-email",  "post-sig-email.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/signed-post-transient", "/sales-post-sig-transient",  "post-sig-transient.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/signed-post-persistent", "/sales-post-sig-persistent",  "post-sig-persistent.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/signed-metadata", "/sales-metadata",  "post-metadata.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/signed-get", "/employee-sig",  "employee-sig.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/mappers", "/employee2",  "employee2.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/signed-front-get", "/employee-sig-front",  "employee-sig-front.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/bad-client-signed-post", "/bad-client-sales-post-sig",  "bad-client-post-sig.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/bad-realm-signed-post", "/bad-realm-sales-post-sig",  "bad-realm-post-sig.war", classLoader);
            initializeSamlSecuredWar("/keycloak-saml/encrypted-post", "/sales-post-enc",  "post-enc.war", classLoader);
            System.setProperty("app.server.base.url", "http://localhost:8081");
            initializeSamlSecuredWar("/keycloak-saml/simple-input", "/input-portal",  "input.war", classLoader, InputServlet.class, "/secured/*");
            SamlAdapterTestStrategy.uploadSP("http://localhost:8081/auth");
            server.getServer().deploy(createDeploymentInfo("employee.war", "/employee", SamlSPFacade.class));



        }

        @Override
        public String getRealmJson() {
            return "/keycloak-saml/testsaml.json";
        }
    };

    @Rule
    public SamlAdapterTestStrategy testStrategy = new SamlAdapterTestStrategy("http://localhost:8081/auth", "http://localhost:8081", keycloakRule);

    //@Test
    public void testIDE() throws Exception {
        Thread.sleep(100000000);
    }

    @Test
    public void testPostBadRealmSignature() {
        testStrategy.testPostBadRealmSignature();
    }

    @Test
    public void testPostSimpleUnauthorized() {
        testStrategy.testPostSimpleUnauthorized( new SamlAdapterTestStrategy.CheckAuthError() {
            @Override
            public void check(WebDriver driver) {
                String pageSource = driver.getPageSource();
                Assert.assertTrue(pageSource.contains("Error Page"));
            }
        });
    }


    //@Test Doesn't work for Wildfly as the input stream is read by getParameter for SAML POST binding
    public void testSavedPostRequest() throws Exception {
        testStrategy.testSavedPostRequest();
    }
    @Test
    public void testErrorHandling() throws Exception {
        testStrategy.testErrorHandling();
    }
    @Test
    public void testMetadataPostSignedLoginLogout() throws Exception {
        testStrategy.testMetadataPostSignedLoginLogout();
    }

    @Test
    public void testRedirectSignedLoginLogout() {
        testStrategy.testRedirectSignedLoginLogout();
    }

    @Test
    public void testPostSignedLoginLogoutEmailNameID() {
        testStrategy.testPostSignedLoginLogoutEmailNameID();
    }

    @Test
    public void testPostEncryptedLoginLogout() {
        testStrategy.testPostEncryptedLoginLogout();
    }

    @Test
    public void testRedirectSignedLoginLogoutFrontNoSSO() {
        testStrategy.testRedirectSignedLoginLogoutFrontNoSSO();
    }

    @Test
    public void testPostSimpleLoginLogout() {
        testStrategy.testPostSimpleLoginLogout();
    }

    @Test
    public void testPostPassiveLoginLogout() {
        testStrategy.testPostPassiveLoginLogout(true);
    }

    @Test
    public void testPostSignedLoginLogoutTransientNameID() {
        testStrategy.testPostSignedLoginLogoutTransientNameID();
    }

    @Test
    public void testPostSimpleLoginLogoutIdpInitiated() {
        testStrategy.testPostSimpleLoginLogoutIdpInitiated();
    }

    @Test
    public void testAttributes() throws Exception {
        testStrategy.testAttributes();
    }

    @Test
    public void testPostSignedLoginLogoutPersistentNameID() {
        testStrategy.testPostSignedLoginLogoutPersistentNameID();
    }

    @Test
    public void testRelayStateEncoding() throws Exception {
        testStrategy.testRelayStateEncoding();
    }

    @Test
    public void testPostBadClientSignature() {
        testStrategy.testPostBadClientSignature();
    }

    @Test
    public void testRedirectSignedLoginLogoutFront() {
        testStrategy.testRedirectSignedLoginLogoutFront();
    }

    @Test
    public void testPostSignedLoginLogout() {
        testStrategy.testPostSignedLoginLogout();
    }

    @Test
    public void testIDPDescriptor() throws Exception {
        Client client = ClientBuilder.newClient();
        String text = client.target("http://localhost:8081/auth/realms/master/protocol/saml/descriptor").request().get(String.class);
        client.close();

    }


}
