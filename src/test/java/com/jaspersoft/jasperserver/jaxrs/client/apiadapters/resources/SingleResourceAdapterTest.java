package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;

/**
 * Unit Tests for {@link SingleResourceAdapter}
 */
@PrepareForTest(JerseyRequest.class)
public class SingleResourceAdapterTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<ClientResource> jerseyRequestMock;

    @Mock
    private OperationResult<ClientResource> operationResultMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    public void should_return_specified_resource_asynchronously() throws InterruptedException {

        /** Given **/
        String resourceUri = "requestId";

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        SingleResourceAdapter adapterSpy = PowerMockito.spy(new SingleResourceAdapter(sessionStorageMock, resourceUri));
        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(buildRequest(eq(sessionStorageMock), eq(ClientResource.class), eq(new String[]{"/resources", resourceUri}))).thenReturn(jerseyRequestMock);
        PowerMockito.doReturn(operationResultMock).when(jerseyRequestMock).get();

        final Callback<OperationResult<ClientResource>, Void> callback = PowerMockito.spy(new Callback<OperationResult<ClientResource>, Void>() {
            @Override
            public Void execute(OperationResult<ClientResource> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });
        PowerMockito.doReturn(null).when(callback).execute(operationResultMock);

        /** When **/
        RequestExecution retrieved = adapterSpy.asyncDetails(callback);

        /** Wait **/
        synchronized (callback) {
            callback.wait(1000);
        }

        /** Than **/
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(jerseyRequestMock, times(1)).get();
        verify(callback).execute(operationResultMock);
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, jerseyRequestMock, operationResultMock);
    }
}