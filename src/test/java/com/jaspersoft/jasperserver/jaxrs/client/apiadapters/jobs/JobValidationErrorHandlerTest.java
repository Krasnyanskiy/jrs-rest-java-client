package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs;

import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.ValidationException;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.dto.common.ValidationErrorsListWrapper;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link JobValidationErrorHandler}
 */
@PrepareForTest({DefaultErrorHandler.class, JobValidationErrorHandler.class})
public class JobValidationErrorHandlerTest extends PowerMockTestCase {

    @Mock
    private Response responseMock;

    @Mock
    private ValidationErrorsListWrapper wrapperMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void should_throw_an_exception_when_retrieved_validation_errors() throws Exception {

        /* Given */
        Mockito.when(responseMock.getHeaderString("Content-Type")).thenReturn("application/xml");
        Mockito.when(responseMock.readEntity(ValidationErrorsListWrapper.class)).thenReturn(wrapperMock);

        /* When */
        JobValidationErrorHandler handler = new JobValidationErrorHandler();
        handler.handleBodyError(responseMock);

        /* Than */
        Mockito.verify(responseMock, times(1)).getHeaderString("Content-Type");
        Mockito.verify(responseMock, times(1)).readEntity(ValidationErrorsListWrapper.class);
    }

    @Test
    public void should_handle_body_error() {

        /* Given */
        final ValidationErrorsListWrapper expected = null;
        final int wantedNumberOfInvocations = 2; // invoke getHeaderString length in child and parent class
        Mockito.when(responseMock.getHeaderString("Content-Type")).thenReturn("application/xml");
        Mockito.when(responseMock.readEntity(ValidationErrorsListWrapper.class)).thenReturn(expected);

        /* When */
        JobValidationErrorHandler handler = new JobValidationErrorHandler();
        handler.handleBodyError(responseMock);

        /* Than */
        Mockito.verify(responseMock, times(wantedNumberOfInvocations)).getHeaderString("Content-Type");
    }

    @Test
    public void should_handle_body_error_when_content_type_of_response_in_text_html() {

        /* Given */
        final ValidationErrorsListWrapper expected = null;
        Mockito.when(responseMock.getHeaderString("Content-Type")).thenReturn("text/html");
        Mockito.when(responseMock.readEntity(ValidationErrorsListWrapper.class)).thenReturn(expected);

        /* When */
        JobValidationErrorHandler handler = Mockito.spy(new JobValidationErrorHandler());
        handler.handleBodyError(responseMock);

        /* Than */
        Mockito.verify(responseMock, times(3)).getHeaderString("Content-Type");
        Mockito.verify(handler, times(1)).handleBodyError(responseMock);
    }

    @AfterMethod
    public void after() {
        reset(responseMock, wrapperMock);
    }
}