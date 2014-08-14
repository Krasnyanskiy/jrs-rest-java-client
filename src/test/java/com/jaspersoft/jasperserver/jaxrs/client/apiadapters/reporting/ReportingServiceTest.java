package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionRequest;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;

/**
 * Unit tests for {@link ReportingService}
 */
@PrepareForTest({JerseyRequest.class})
public class ReportingServiceTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<ReportExecutionDescriptor> requestMock;

    @Mock
    private ReportExecutionRequest executionRequestMock;

    @Mock
    private OperationResult<ReportExecutionDescriptor> resultMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    /**
     * for {@link ReportingService#asyncNewReportExecutionRequest(ReportExecutionRequest, Callback)}
     */
    public void should_return_strangely_wrapped_ReportExecutionDescriptor() throws InterruptedException {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        int currentThreadId = (int) Thread.currentThread().getId();

        mockStatic(JerseyRequest.class);
        when(buildRequest(eq(sessionStorageMock), eq(ReportExecutionDescriptor.class), eq(new String[]{"/reportExecutions"}))).thenReturn(requestMock);

        ReportingService serviceSpy = PowerMockito.spy(new ReportingService(sessionStorageMock));
        Callback<OperationResult<ReportExecutionDescriptor>, Void> callback = spy(new Callback<OperationResult<ReportExecutionDescriptor>, Void>() {
            @Override
            public Void execute(OperationResult<ReportExecutionDescriptor> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(resultMock).when(requestMock).post(executionRequestMock);
        doReturn(null).when(callback).execute(resultMock);

        /* When */
        RequestExecution retrieved = serviceSpy.asyncNewReportExecutionRequest(executionRequestMock, callback);

        /* Wait */
        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(resultMock);
        verify(requestMock, times(1)).post(executionRequestMock);
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, requestMock, executionRequestMock, resultMock);
    }
}