package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.AttachmentDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportDescriptor;
import org.apache.commons.io.IOUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.ArrayList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.testng.Assert.assertNotNull;

/**
 * Unit tests for {@link ExportExecutionRequestBuilder}
 */
@PrepareForTest({JerseyRequest.class, IOUtils.class, ExportExecutionRequestBuilder.class})
public class ExportExecutionRequestBuilderTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private ExportDescriptor descriptorMock;

    @Mock
    private JerseyRequest<String> requestMock;

    @Mock
    private OperationResult<String> resultMock;

    @Mock
    private OperationResult<InputStream> streamedResultMock;

    @Mock
    private InputStream streamMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    public void should_return_HtmlReport_instance_if_ExportDescriptor_is_a_proper_val() throws Exception {

        /* Given */
        String exportId = "exportId";
        String requestId = "requestId";

        final AttachmentDescriptor desc = PowerMockito.spy(new AttachmentDescriptor());
        desc.setContentType("json");
        desc.setFileName("myFile");

        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(JerseyRequest.buildRequest(sessionStorageMock, String.class, new String[]{"/reportExecutions", requestId, "/exports", exportId, "/outputResource"})).thenReturn(requestMock);

        PowerMockito.doReturn(resultMock).when(requestMock).get();
        PowerMockito.doReturn("entity").when(resultMock).getEntity();

        PowerMockito.doReturn(new ArrayList<AttachmentDescriptor>() {{
            add(desc);
        }}).when(descriptorMock).getAttachments();

        ExportExecutionRequestBuilder builderSpy = spy(new ExportExecutionRequestBuilder(sessionStorageMock, requestId, exportId));
        PowerMockito.doReturn(streamedResultMock).when(builderSpy).attachment(anyString());
        PowerMockito.doReturn(streamMock).when(streamedResultMock).getEntity();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toByteArray(streamMock)).thenReturn(new byte[]{});


        /* When */
        HtmlReport retrieved = builderSpy.htmlReport(descriptorMock);


        /* Than */
        assertNotNull(retrieved);

        PowerMockito.verifyStatic(times(1));
        JerseyRequest.buildRequest(sessionStorageMock, String.class, new String[]{"/reportExecutions", requestId, "/exports", exportId, "/outputResource"});

        PowerMockito.verifyStatic(times(1));
        IOUtils.toByteArray(streamMock);

        Mockito.verify(requestMock, times(1)).get();
        Mockito.verify(resultMock, times(1)).getEntity();
        Mockito.verify(descriptorMock, times(1)).getAttachments();
        Mockito.verify(desc, times(2)).getFileName();
        Mockito.verify(builderSpy, times(1)).attachment(anyString());
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, descriptorMock, requestMock, resultMock, streamMock, streamedResultMock);
    }
}