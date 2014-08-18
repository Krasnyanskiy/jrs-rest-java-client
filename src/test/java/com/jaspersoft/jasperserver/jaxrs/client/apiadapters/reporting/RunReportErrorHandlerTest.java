package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    public void handleBodyError() throws Exception {

        Mockito.when(responseMock.getHeaderString("JasperServerError")).thenReturn("true");
        RunReportErrorHandler handlerSpy = PowerMockito.spy(new RunReportErrorHandler());
        PowerMockito.when(DefaultErrorHandler.class, "readBody", responseMock, String.class).thenReturn("errorMessage");

        handlerSpy.handleBodyError(responseMock);

        verify(responseMock, times(1)).getHeaderString("JasperServerError");
    }

    @Test (enabled = false)
    public void handleBodyError2() throws Exception {

        Response responseMock = PowerMockito.mock(Response.class);

        RunReportErrorHandler spy = PowerMockito.spy(new RunReportErrorHandler());
        PowerMockito.doReturn("true").when(responseMock).getHeaderString("JasperServerError");
        PowerMockito.doReturn("abc").when(spy, "readBody", responseMock, String.class);

        //PowerMockito.doReturn("abc").when(DefaultErrorHandler.class, "readBody", responseMock, String.class);
        //PowerMockito.when(DefaultErrorHandler.class, "readBody", responseMock, String.class).thenReturn("errorMessage");
        //PowerMockito.doNothing().when(spy, "handleBodyError", responseMock);

        spy.handleBodyError(responseMock);

        //verify(spy, times(1)).handleBodyError(responseMock);
    }

    @AfterMethod
    public void after() {
        reset(responseMock);
    }
}