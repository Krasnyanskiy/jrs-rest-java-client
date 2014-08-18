package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportExecutionDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportExecutionOptions;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionStatusEntity;
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
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;

/**
 * Unit tests for {@link ReportExecutionRequestBuilder}
 *
 * @author Alexander Krasnyanskiy
 */
@PrepareForTest({JerseyRequest.class, ReportExecutionRequestBuilder.class, ReportExecutionStatusEntity.class})
public class ReportExecutionRequestBuilderTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private ExportExecutionOptions optionsMock;

    @Mock
    private ReportExecutionStatusEntity statusEntityMock;

    @Mock
    private JerseyRequest<ReportExecutionStatusEntity> requestMock;

    @Mock
    private JerseyRequest<ReportExecutionDescriptor> reportExecutionDescriptorJerseyRequestMock;

    @Mock
    private JerseyRequest<ExportExecutionDescriptor> exportExecutionDescriptorJerseyRequestMock;

    @Mock
    private JerseyRequest<ReportExecutionStatusEntity> reportExecutionStatusEntityJerseyRequestMock;

    @Mock
    private OperationResult<ReportExecutionStatusEntity> resultMock;

    @Mock
    private OperationResult<ReportExecutionDescriptor> reportExecutionDescriptorOperationResultMock;

    @Mock
    private OperationResult<ExportExecutionDescriptor> exportExecutionDescriptorOperationResultMock;

    @Mock
    private OperationResult<ReportExecutionStatusEntity> reportExecutionStatusEntityOperationResultMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    /**
     * for {@link ReportExecutionRequestBuilder#asyncStatus(Callback)}
     */
    public void should_return_ReportExecution_instance_with_proper_Future() throws InterruptedException {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        int currentThreadId = (int) Thread.currentThread().getId();

        mockStatic(JerseyRequest.class);
        when(buildRequest(eq(sessionStorageMock), eq(ReportExecutionStatusEntity.class), eq(new String[]{"/reportExecutions", "requestId", "/status"}))).thenReturn(requestMock);

        ReportExecutionRequestBuilder builderSpy = PowerMockito.spy(new ReportExecutionRequestBuilder(sessionStorageMock, "requestId"));

        Callback<OperationResult<ReportExecutionStatusEntity>, Void> callback = spy(new Callback<OperationResult<ReportExecutionStatusEntity>, Void>() {
            @Override
            public Void execute(OperationResult<ReportExecutionStatusEntity> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(resultMock).when(requestMock).get();
        doReturn(null).when(callback).execute(resultMock);

        /* When */
        RequestExecution retrieved = builderSpy.asyncStatus(callback);

        /* Wait */
        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(resultMock);
        verify(requestMock, times(1)).get();

        //verifyStatic(times(1));
        //buildRequest(eq(sessionStorageMock), eq(ReportExecutionStatusEntity.class), eq(new String[]{"/reportExecutions", "requestId", "/status"}));
    }

    @Test
    /**
     * for {@link ReportExecutionRequestBuilder#asyncExecutionDetails(Callback)}
     */
    public void should_retrieve_proper_ReportExecutionDescriptor_in_separate_thread() throws InterruptedException {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        int currentThreadId = (int) Thread.currentThread().getId();

        mockStatic(JerseyRequest.class);
        when(buildRequest(eq(sessionStorageMock), eq(ReportExecutionDescriptor.class), eq(new String[]{"/reportExecutions", "requestId"}))).thenReturn(reportExecutionDescriptorJerseyRequestMock);

        ReportExecutionRequestBuilder builderSpy = PowerMockito.spy(new ReportExecutionRequestBuilder(sessionStorageMock, "requestId"));

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

        doReturn(reportExecutionDescriptorOperationResultMock).when(reportExecutionDescriptorJerseyRequestMock).get();
        doReturn(null).when(callback).execute(reportExecutionDescriptorOperationResultMock);

        /* When */
        RequestExecution retrieved = builderSpy.asyncExecutionDetails(callback);

        /* Wait */
        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(reportExecutionDescriptorOperationResultMock);
        verify(reportExecutionDescriptorJerseyRequestMock, times(1)).get();

        // todo: please find out the way to verify invocation of static protectedMethod
        //verifyStatic(times(1));
        //buildRequest(eq(sessionStorageMock), eq(ReportExecutionDescriptor.class), eq(new String[]{"/reportExecutions", "requestId"}));
    }

    @Test
    /**
     * for {@link ReportExecutionRequestBuilder#asyncRunExport(ExportExecutionOptions, Callback)}
     */
    public void should_send_ExportExecutionOptions_asynchronously_and_return_proper_RequestExecution_instance() throws InterruptedException {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        int currentThreadId = (int) Thread.currentThread().getId();

        mockStatic(JerseyRequest.class);
        when(buildRequest(eq(sessionStorageMock), eq(ExportExecutionDescriptor.class), eq(new String[]{"/reportExecutions", "requestId", "/exports"}))).thenReturn(exportExecutionDescriptorJerseyRequestMock);

        ReportExecutionRequestBuilder builderSpy = PowerMockito.spy(new ReportExecutionRequestBuilder(sessionStorageMock, "requestId"));

        Callback<OperationResult<ExportExecutionDescriptor>, Void> callback = spy(new Callback<OperationResult<ExportExecutionDescriptor>, Void>() {
            @Override
            public Void execute(OperationResult<ExportExecutionDescriptor> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(exportExecutionDescriptorOperationResultMock).when(exportExecutionDescriptorJerseyRequestMock).post(optionsMock);
        doReturn(null).when(callback).execute(exportExecutionDescriptorOperationResultMock);

        /* When */
        RequestExecution retrieved = builderSpy.asyncRunExport(optionsMock, callback);

        /* Wait */
        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(exportExecutionDescriptorOperationResultMock);
        verify(exportExecutionDescriptorJerseyRequestMock, times(1)).post(optionsMock);
    }

    @Test
    /**
     * for {@link ReportExecutionRequestBuilder#asyncCancelExecution(Callback)}
     */
    public void should_cancel_execution_of_report_asynchronously() throws Exception {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        int currentThreadId = (int) Thread.currentThread().getId();

        whenNew(ReportExecutionStatusEntity.class).withNoArguments().thenReturn(statusEntityMock);

        mockStatic(JerseyRequest.class);
        when(buildRequest(eq(sessionStorageMock), eq(ReportExecutionStatusEntity.class), eq(new String[]{"/reportExecutions", "requestId", "/status"}))).thenReturn(reportExecutionStatusEntityJerseyRequestMock);

        ReportExecutionRequestBuilder builderSpy = PowerMockito.spy(new ReportExecutionRequestBuilder(sessionStorageMock, "requestId"));
        Callback<OperationResult<ReportExecutionStatusEntity>, Void> callback = spy(new Callback<OperationResult<ReportExecutionStatusEntity>, Void>() {
            @Override
            public Void execute(OperationResult<ReportExecutionStatusEntity> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(reportExecutionStatusEntityOperationResultMock).when(reportExecutionStatusEntityJerseyRequestMock).put(statusEntityMock);
        doReturn(null).when(callback).execute(reportExecutionStatusEntityOperationResultMock);

        /* When */
        RequestExecution retrieved = builderSpy.asyncCancelExecution(callback);

        /* Wait */
        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(reportExecutionStatusEntityOperationResultMock);
        verify(reportExecutionStatusEntityJerseyRequestMock, times(1)).put(statusEntityMock);
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, optionsMock, requestMock, reportExecutionDescriptorJerseyRequestMock,
                exportExecutionDescriptorJerseyRequestMock, resultMock, reportExecutionDescriptorOperationResultMock,
                exportExecutionDescriptorOperationResultMock, reportExecutionStatusEntityJerseyRequestMock,
                reportExecutionStatusEntityOperationResultMock, statusEntityMock);
    }
}