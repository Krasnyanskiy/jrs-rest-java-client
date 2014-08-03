package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.organizations;

import com.jaspersoft.jasperserver.dto.authority.ClientTenant;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import static org.testng.Assert.assertEquals;
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

    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, requestMock, resultMock, tenantMock);
    }
}