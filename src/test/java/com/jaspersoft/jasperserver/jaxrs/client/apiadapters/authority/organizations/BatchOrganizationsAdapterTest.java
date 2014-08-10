package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.organizations;

import com.jaspersoft.jasperserver.dto.authority.OrganizationsListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link BatchOrganizationsAdapter}
 */
@PrepareForTest({JerseyRequest.class})
public class BatchOrganizationsAdapterTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<OrganizationsListWrapper> requestMock;

    @Mock
    private OperationResult<OrganizationsListWrapper> resultMock;

    @Mock
    private MultivaluedHashMap<String, String> paramsMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    /**
     * for {@link BatchOrganizationsAdapter#asyncGet(Callback)}
     */
    public void should_fire_get_method_asynchronously_and_return_OperationResult_object() throws Exception {

        /* Given */
        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(buildRequest(eq(sessionStorageMock), eq(OrganizationsListWrapper.class), eq(new String[]{"/organizations"}), any(DefaultErrorHandler.class))).thenReturn(requestMock);
        PowerMockito.doReturn(resultMock).when(requestMock).get();
        BatchOrganizationsAdapter adapterSpy = PowerMockito.spy(new BatchOrganizationsAdapter(sessionStorageMock));

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        final Callback<OperationResult<OrganizationsListWrapper>, Void> callback = PowerMockito.spy(new Callback<OperationResult<OrganizationsListWrapper>, Void>() {
            @Override
            public Void execute(OperationResult<OrganizationsListWrapper> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        PowerMockito.doReturn(null).when(callback).execute(resultMock);

        /* When */
        RequestExecution retrieved = adapterSpy.asyncGet(callback);

        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        Mockito.verify(requestMock).get();
        Mockito.verify(callback).execute(resultMock);
        Assert.assertNotNull(retrieved);
        Assert.assertNotSame(currentThreadId, newThreadId.get());
    }

    @Test
    /**
     * for {@link BatchOrganizationsAdapter#parameter(OrganizationParameter, String)}
     */
    @SuppressWarnings("unchecked")
    public void should_add_parameter_to_map_and_return_this() throws Exception {

        /* Given */
        BatchOrganizationsAdapter adapterSpy = PowerMockito.spy(new BatchOrganizationsAdapter(sessionStorageMock));

        /* When */
        BatchOrganizationsAdapter retrieved = adapterSpy.parameter(OrganizationParameter.CREATE_DEFAULT_USERS, "true");
        MultivaluedHashMap<String, String> params = (MultivaluedHashMap<String, String>) Whitebox.getInternalState(adapterSpy, "params");

        /* Than */
        Assert.assertSame(retrieved, adapterSpy);
        Assert.assertTrue(params.size() == 1);
        Assert.assertEquals(params.getFirst(OrganizationParameter.CREATE_DEFAULT_USERS.getParamName()), "true");
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, requestMock, resultMock, paramsMock);
    }
}