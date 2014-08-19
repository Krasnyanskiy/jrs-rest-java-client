package com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling;

import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.JSClientWebException;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.ResourceNotFoundException;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.common.ErrorDescriptor;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

/**
 * Unit tests for {@link DefaultErrorHandler}
 */
@PrepareForTest({DefaultErrorHandler.class, ErrorDescriptor.class})
public class DefaultErrorHandlerTest extends PowerMockTestCase {

    @Mock
    private Response responseMock;

    @Mock
    private ErrorDescriptor descriptorMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    public void should_handle_error() {
        Mockito.when(responseMock.hasEntity()).thenReturn(true);
        DefaultErrorHandler handlerSpy = Mockito.spy(new DefaultErrorHandler());

        Mockito.doNothing().when(handlerSpy).handleBodyError(responseMock);
        Mockito.doNothing().when(handlerSpy).handleStatusCodeError(responseMock, null);

        handlerSpy.handleError(responseMock);

        Mockito.verify(responseMock, Mockito.times(1)).hasEntity();
        Mockito.verify(responseMock, Mockito.times(1)).bufferEntity();

        Mockito.verify(handlerSpy, Mockito.times(1)).handleBodyError(responseMock);
        Mockito.verify(handlerSpy, Mockito.times(1)).handleBodyError(responseMock);
    }

    @Test(expectedExceptions = JSClientWebException.class, enabled = false)
    public void should_throw_an_exception_when_() {

        Response.StatusType fakeStatusSpy = Mockito.spy(new Response.StatusType() {
            @Override
            public int getStatusCode() {
                return 403;
            }

            @Override
            public Response.Status.Family getFamily() {
                return null;
            }

            @Override
            public String getReasonPhrase() {
                return "Forbidden";
            }
        });

        Mockito.when(responseMock.getStatus()).thenReturn(403);
        Mockito.when(responseMock.getStatusInfo()).thenReturn(fakeStatusSpy);

        DefaultErrorHandler handlerSpy = Mockito.spy(new DefaultErrorHandler());
        handlerSpy.handleStatusCodeError(responseMock, "msg");
    }


    @Test
    public void should_return_entity_of_proper_class() {

        /* Given */
        OperationResult resultMock = Mockito.mock(OperationResult.class);
        Mockito.when(responseMock.readEntity(OperationResult.class)).thenReturn(resultMock);
        DefaultErrorHandler handlerSpy = Mockito.spy(new DefaultErrorHandler());

        /* When */
        OperationResult retrieved = handlerSpy.readBody(responseMock, OperationResult.class);

        /* Than */
        assertNotNull(retrieved);
        assertSame(retrieved, resultMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_throw_an_exception_while_reading_entity_from_response() {

        /* Given */
        Mockito.when(responseMock.readEntity(OperationResult.class)).thenThrow(MessageBodyProviderNotFoundException.class);
        DefaultErrorHandler handlerSpy = Mockito.spy(new DefaultErrorHandler());

        /* When */
        OperationResult retrieved = handlerSpy.readBody(responseMock, OperationResult.class);

        /* Than */
        assertNull(retrieved);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_throw_ProcessingException_while_reading_entity_from_response() {

        /* Given */
        Mockito.when(responseMock.readEntity(OperationResult.class)).thenThrow(ProcessingException.class);
        DefaultErrorHandler handlerSpy = Mockito.spy(new DefaultErrorHandler());

        /* When */
        OperationResult retrieved = handlerSpy.readBody(responseMock, OperationResult.class);

        /* Than */
        assertNull(retrieved);
    }

    @Test
    public void should_handle_body_error() {

        /* Given */
        Mockito.when(responseMock.getHeaderString("Content-Type")).thenReturn("text/html");
        DefaultErrorHandler handlerSpy = Mockito.spy(new DefaultErrorHandler());

        /* When */
        handlerSpy.handleBodyError(responseMock);

        /* Than */
        verify(responseMock).getHeaderString("Content-Type");
    }

    @Test(expectedExceptions = ResourceNotFoundException.class, enabled = false)
    public void should_throw_an_exception() throws Exception {

        /* Given */
        ErrorDescriptor descriptorMock = PowerMockito.mock(ErrorDescriptor.class);
        Mockito.doReturn("non-text-html").when(responseMock).getHeaderString("Content-Type");
        DefaultErrorHandler handlerSpy = Mockito.spy(new DefaultErrorHandler());
        Mockito.doReturn(descriptorMock).when(responseMock).readEntity(ErrorDescriptor.class);
        Mockito.doReturn("resource.not.found").when(descriptorMock).getErrorCode();

        /* When */
        handlerSpy.handleBodyError(responseMock);

        /* Than throw an exception */
    }

    @AfterMethod
    public void after() {
        reset(responseMock, descriptorMock);
    }
}