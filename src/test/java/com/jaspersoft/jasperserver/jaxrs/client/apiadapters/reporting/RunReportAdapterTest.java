package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Unit tests for {@link RunReportAdapter}
 */
@PrepareForTest({JerseyRequest.class, RunReportAdapter.class})
public class RunReportAdapterTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<InputStream> requestMock;

    @Mock
    private OperationResult<InputStream> resultMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    public void should_return_a_RequestExecution_instance() throws Exception {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        int currentThreadId = (int) Thread.currentThread().getId();

        RunReportAdapter adapterSpy = spy(new RunReportAdapter(sessionStorageMock,
                "fakeReportUnitUri", ReportOutputFormat.PDF, new PageRange(1L, 100L)));

        Callback<OperationResult<InputStream>, Void> callbackSpy =
                spy(new Callback<OperationResult<InputStream>, Void>() {
                    @Override
                    public Void execute(OperationResult<InputStream> data) {
                        newThreadId.set((int) Thread.currentThread().getId());
                        synchronized (this) {
                            this.notify();
                        }
                        return null;
                    }
                });

        doReturn(requestMock).when(adapterSpy, "prepareRunRequest");
        doReturn(resultMock).when(requestMock).get();
        doReturn(null).when(callbackSpy).execute(resultMock);

        /* When */
        RequestExecution retrieved = adapterSpy.asyncRun(callbackSpy);

        /* Wait */
        synchronized (callbackSpy) {
            callbackSpy.wait(1000);
        }

        /* Than */
        Assert.assertNotNull(retrieved);
        Assert.assertNotNull(retrieved.getFuture());
        Assert.assertNotSame(currentThreadId, newThreadId.get());

        Mockito.verify(callbackSpy, times(1)).execute(resultMock);
        Mockito.verify(requestMock, times(1)).get();
    }

    @Test
    public void test() {
        // ...
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, requestMock, resultMock);
    }
}