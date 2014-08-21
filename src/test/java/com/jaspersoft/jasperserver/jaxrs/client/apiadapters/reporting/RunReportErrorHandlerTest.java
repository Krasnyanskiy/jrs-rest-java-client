package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.ResourceNotFoundException;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.reset;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test for {@link RunReportErrorHandler}
 */
@PrepareForTest({DefaultErrorHandler.class, RunReportErrorHandler.class})
public class RunReportErrorHandlerTest extends PowerMockTestCase {

    @Mock
    private Response responseMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test(enabled = false)
    public void test() {

        Mockito.when(responseMock.getHeaderString("JasperServerError")).thenReturn("true");
        Mockito.when(responseMock.readEntity(String.class)).thenReturn("data");
        Mockito.when(responseMock.getStatus()).thenReturn(404);
        Mockito.when(responseMock.getStatusInfo()).thenReturn(Response.Status.NOT_FOUND);
        Mockito.when(responseMock.getHeaderString("Content-Type")).thenReturn("text/html");

        RunReportErrorHandler handler = new RunReportErrorHandler();

        try {
            handler.handleBodyError(responseMock);
        } catch (Exception e) {
            Assert.assertTrue(instanceOf(ResourceNotFoundException.class).matches(e));
        }
    }

    @AfterMethod
    public void after() {
        reset(responseMock);
    }
}