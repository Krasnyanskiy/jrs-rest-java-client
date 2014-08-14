package com.jaspersoft.jasperserver.jaxrs.client.misc;

import com.jaspersoft.jasperserver.dto.authority.ClientTenant;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.jaxrs.client.core.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;

/**
 * @author Alexander Krasnyanskiy
 */
public class TestApp {
    public static void main(String[] args) {

        RestClientConfiguration config = new RestClientConfiguration("http://54.167.115.172/jasperserver-pro");
        JasperserverRestClient client = new JasperserverRestClient(config);

        ClientTenant organization = new ClientTenant()
                .setAlias("MyCoolOrg")
                .setTenantName("MyCoolOrg");

        client.authenticate("superuser", "superuser")
                .organizationsService()
                .organization("MyCoolOrg")
                .update(organization);

        //client().authenticate("name", "password").organization().update();
        //client().authenticate(credentails).service(ClientTenant.class).update();
        //client().authenticate(credentails).service("org").update();
        //client().authenticate(credentails).organizationService().update(tenant);
        // service("org") - factory method
        // service(Class<T> serviceType) - factory method

        OperationResult<ClientResource> result = client.authenticate("superuser", "superuser")
                .resourcesService()
                .resource("")
                .details();

        client.authenticate("superuser", "superuser")
                .reportingService()
                .report("uri")
                .reportParameters()
                .asyncReorder(null, null);
    }
}