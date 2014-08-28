package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.organizations;

import com.jaspersoft.jasperserver.dto.authority.ClientTenant;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.JSClientException;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
* Unit tests for {@link SingleOrganizationAdapter}
*/
@PrepareForTest({JerseyRequest.class, SingleOrganizationAdapter.class})
public class SingleOrganizationAdapterTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<ClientTenant> requestMock;

    @Mock
    private OperationResult<ClientTenant> resultMock;

    @Mock
    private ClientTenant tenantMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test(testName = "constructor")
    public void constructor() {

        String EXPECTED = "MyCoolOrg";
        SingleOrganizationAdapter adapter = new SingleOrganizationAdapter(sessionStorageMock, EXPECTED);
        String RETRIEVED = (String) getInternalState(adapter, "organizationId");

        assertSame(adapter.getSessionStorage(), sessionStorageMock);
        assertEquals(RETRIEVED, EXPECTED);
    }

    @Test
    public void buildRequest() throws Exception {

        // Given
        mockStatic(JerseyRequest.class);
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        String organizationId = (String) getInternalState(adapter, "organizationId");

        when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(ClientTenant.class),
                eq(new String[]{"/organizations", organizationId}),
                any(DefaultErrorHandler.class))).thenReturn(requestMock);

        Mockito.when(requestMock.delete()).thenReturn(resultMock);

        // When
        OperationResult retrieved = adapter.delete();

        // Than
        verifyStatic(times(1));
        JerseyRequest.buildRequest(
                eq(sessionStorageMock),
                eq(ClientTenant.class),
                eq(new String[]{"/organizations", organizationId}),
                any(DefaultErrorHandler.class));
        verifyPrivate(adapter, times(1)).invoke("buildRequest");
        Assert.assertNotNull(retrieved);
    }

    @Test
    public void get() throws Exception {

        // Given
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        doReturn(requestMock).when(adapter, "buildRequest");
        doReturn(resultMock).when(requestMock).get();

        // When
        adapter.get();

        // Than
        verifyPrivate(adapter, times(1)).invoke("buildRequest");
        verify(requestMock, times(1)).get();
        verifyNoMoreInteractions(requestMock);
        verifyNoMoreInteractions(resultMock);
    }

    @Test
    public void update() throws Exception {

        // Given
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        doReturn("json").when(adapter, "prepareJsonForUpdate", tenantMock);
        doReturn(requestMock).when(adapter, "buildRequest");
        doReturn(resultMock).when(requestMock).put("json");

        // When
        adapter.update(tenantMock);

        // Than
        verifyPrivate(adapter, times(1)).invoke("buildRequest");
        verifyPrivate(adapter, times(1)).invoke("prepareJsonForUpdate", tenantMock);
        verify(requestMock, times(1)).put("json");
        verifyNoMoreInteractions(requestMock);
        verifyNoMoreInteractions(resultMock);
    }

    @Test
    public void prepareJsonForUpdate() {
        // todo
    }

    @Test
    /**
     * for {@link SingleOrganizationAdapter#asyncGet(Callback)}
     */
    public void should_fire_get_method_asynchronously_and_return_holder_object_with_result_of_execution() throws Exception {

        /* Given */
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        doReturn(requestMock).when(adapter, "buildRequest");
        doReturn(resultMock).when(requestMock).get();

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        final Callback<OperationResult<ClientTenant>, Void> callback = spy(new Callback<OperationResult<ClientTenant>, Void>() {
            @Override
            public Void execute(OperationResult<ClientTenant> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(null).when(callback).execute(resultMock);

        /* When */
        RequestExecution retrieved = adapter.asyncGet(callback);

        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        verify(requestMock).get();
        verify(callback).execute(resultMock);
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
    }

    @Test
    /**
     * for {@link SingleOrganizationAdapter#asyncUpdate(ClientTenant, Callback)}
     */
    public void should_fire_update_method_asynchronously_and_return_holder_object_with_result_of_execution() throws Exception {

        /* Given */
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        ClientTenant tenantMock = mock(ClientTenant.class);

        doReturn(requestMock).when(adapter, "buildRequest");
        doReturn("JSONTenant").when(adapter, "prepareJsonForUpdate", any(ClientTenant.class));
        doReturn(resultMock).when(requestMock).put("JSONTenant");

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        final Callback<OperationResult<ClientTenant>, Void> callback = spy(new Callback<OperationResult<ClientTenant>, Void>() {
            @Override
            public Void execute(OperationResult<ClientTenant> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(null).when(callback).execute(resultMock);

        /* When */
        RequestExecution retrieved = adapter.asyncUpdate(tenantMock, callback);

        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        // verifyPrivate(adapter).invoke("buildRequest");
        // verifyPrivate(adapter).invoke("prepareJsonForUpdate", tenantMock);
        verify(requestMock).put("JSONTenant");
        verify(callback).execute(resultMock);
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
    }

    @Test
    /**
     * for {@link SingleOrganizationAdapter#asyncDelete(Callback)}
     */
    public void should_fire_delete_method_asynchronously_and_return_holder_object_with_result_of_execution() throws Exception {

        /* Given */
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        doReturn(requestMock).when(adapter, "buildRequest");
        doReturn(resultMock).when(requestMock).delete();

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        final Callback<OperationResult, Void> callback = spy(new Callback<OperationResult, Void>() {
            @Override
            public Void execute(OperationResult data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(null).when(callback).execute(resultMock);

        /* When */
        RequestExecution retrieved = adapter.asyncDelete(callback);

        synchronized (callback) {
            callback.wait(1000);
        }

        /* Than */
        verify(requestMock).delete();
        verify(callback).execute(resultMock);
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
    }

    @Test
    public void should_invoke_private_method_when_firing_update_method() throws Exception {

        /* Given */
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        ClientTenant tenantMock = mock(ClientTenant.class);
        ObjectMapper mapperMock = mock(ObjectMapper.class);
        SerializationConfig configMock = mock(SerializationConfig.class);

        JaxbAnnotationIntrospector introspectorMock = mock(JaxbAnnotationIntrospector.class);

        doReturn(requestMock).when(adapter, "buildRequest");
        doReturn(resultMock).when(requestMock).put(tenantMock);

        whenNew(ObjectMapper.class).withNoArguments().thenReturn(mapperMock);
        doReturn(configMock).when(mapperMock).getSerializationConfig();
        doReturn(configMock).when(configMock).withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        whenNew(JaxbAnnotationIntrospector.class).withNoArguments().thenReturn(introspectorMock);
        doReturn(mapperMock).when(mapperMock).setSerializationConfig(configMock);
        doReturn(mapperMock).when(mapperMock).setAnnotationIntrospector(introspectorMock);
        doReturn("JSONTenant").when(mapperMock).writeValueAsString(tenantMock);

        InOrder inOrder = Mockito.inOrder(mapperMock);

        /* When */
        adapter.update(tenantMock);

        /* Than */
        inOrder.verify(mapperMock).getSerializationConfig();
        inOrder.verify(mapperMock).setSerializationConfig(configMock);
        inOrder.verify(mapperMock).setAnnotationIntrospector(introspectorMock);
        inOrder.verify(mapperMock).writeValueAsString(tenantMock);
    }

    @Test
    public void should_throw_an_exception_when_invalid_organization() throws Exception {

        /* Given */
        SingleOrganizationAdapter adapter = spy(new SingleOrganizationAdapter(sessionStorageMock, "MyCoolOrg"));
        ClientTenant tenantMock = mock(ClientTenant.class);
        ObjectMapper mapperMock = mock(ObjectMapper.class);
        SerializationConfig configMock = mock(SerializationConfig.class);

        JaxbAnnotationIntrospector introspectorMock = mock(JaxbAnnotationIntrospector.class);

        doReturn(requestMock).when(adapter, "buildRequest");
        doReturn(resultMock).when(requestMock).put(tenantMock);

        whenNew(ObjectMapper.class).withNoArguments().thenReturn(mapperMock);
        doReturn(configMock).when(mapperMock).getSerializationConfig();
        doReturn(configMock).when(configMock).withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        whenNew(JaxbAnnotationIntrospector.class).withNoArguments().thenReturn(introspectorMock);
        doReturn(mapperMock).when(mapperMock).setSerializationConfig(configMock);
        doReturn(mapperMock).when(mapperMock).setAnnotationIntrospector(introspectorMock);
        doThrow(JsonMappingException.class).when(mapperMock).writeValueAsString(tenantMock);

        /* When */
        try{
            adapter.update(tenantMock);
        }catch (JSClientException e){
            assertNotNull(e);
            assertEquals(e.getMessage(), "Cannot marshal organization object.");
        }
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, requestMock, resultMock, tenantMock);
    }
}