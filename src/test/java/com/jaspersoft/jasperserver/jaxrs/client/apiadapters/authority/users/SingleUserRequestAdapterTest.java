package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.users;

import com.jaspersoft.jasperserver.dto.authority.ClientUser;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.users.attributes.BatchAttributeAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.users.attributes.SingleAttributeAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Unit tests for {@link SingleUserRequestAdapter}
 */
@PrepareForTest({SingleUserRequestAdapter.class, SingleAttributeAdapter.class, JerseyRequest.class, StringBuilder.class})
public class SingleUserRequestAdapterTest extends PowerMockTestCase {

    @Mock
    public SessionStorage sessionStorageMock;

    @Mock
    public SingleAttributeAdapter expectedSingleAttributeAdapterMock;

    @Mock
    private BatchAttributeAdapter expectedBatchAttributeAdapterMock;

    @Mock
    private JerseyRequest<ClientUser> userJerseyRequestMock;

    @Mock
    private OperationResult<ClientUser> operationResultMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test(testName = "constructor_with_String")
    public void should_invoke_constructor_with_proper_three_params() {

        // When
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter(sessionStorageMock, "MyCoolOrg", "Simon");

        final String retrieved = (String) Whitebox.getInternalState(adapter, "userUriPrefix");
        final String expected = "/organizations/MyCoolOrg/users/Simon";

        // Than
        assertSame(adapter.getSessionStorage(), sessionStorageMock);
        Assert.assertEquals(retrieved, expected);
    }

    @Test(testName = "constructor_with_String")
    public void should_invoke_constructor_with_null_organizationId() {

        // When
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter(sessionStorageMock, null, "Simon");

        final String retrieved = (String) Whitebox.getInternalState(adapter, "userUriPrefix");
        final String expected = "/users/Simon";

        // Than
        assertSame(adapter.getSessionStorage(), sessionStorageMock);
        Assert.assertEquals(retrieved, expected);
    }

    @Test(testName = "constructor_with_StringBuilder")
    public void should_invoke_constructor_with_proper_two_params() {

        // When
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter(sessionStorageMock, "MyCoolOrg");

        final StringBuilder retrieved = (StringBuilder) Whitebox.getInternalState(adapter, "uri");
        final String expected = "/organizations/MyCoolOrg/users/";

        // Than
        assertSame(adapter.getSessionStorage(), sessionStorageMock);
        Assert.assertEquals(retrieved.toString(), expected);
    }

    @Test(testName = "constructor_with_StringBuilder")
    public void should_invoke_non_deprecated_constructor_with_null_organizationId() {

        // When
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter(sessionStorageMock, null);

        final StringBuilder retrieved = (StringBuilder) Whitebox.getInternalState(adapter, "uri");
        final String expected = "/users/";

        // Than
        assertSame(adapter.getSessionStorage(), sessionStorageMock);
        Assert.assertEquals(retrieved.toString(), expected);
    }

    @Test
    public void test1() {

        // When
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter("Simon", "MyCoolOrg", sessionStorageMock);
        final StringBuilder retrieved = (StringBuilder) Whitebox.getInternalState(adapter, "uri");
        final String expected = "/organizations/MyCoolOrg/users/Simon";

        // Than
        assertSame(adapter.getSessionStorage(), sessionStorageMock);
        assertEquals(retrieved.toString(), expected);
    }

    @Test
    public void test2() {

        // When
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter("Simon", null, sessionStorageMock);
        final StringBuilder retrieved = (StringBuilder) Whitebox.getInternalState(adapter, "uri");
        final String expected = "/users/Simon";

        // Than
        assertSame(adapter.getSessionStorage(), sessionStorageMock);
        assertEquals(retrieved.toString(), expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test3() {

        // When
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter(null, null, sessionStorageMock);

        // Than
        // throw exception
    }

    @Test
    public void attribute() throws Exception {

        // Given
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter("Simon", "MyCoolOrg", sessionStorageMock);
        PowerMockito.whenNew(SingleAttributeAdapter.class)
                .withArguments(eq(sessionStorageMock), any(StringBuilder.class))
                .thenReturn(expectedSingleAttributeAdapterMock);

        // When
        SingleAttributeAdapter retrieved = adapter.attribute();

        // Than
        PowerMockito.verifyNew(SingleAttributeAdapter.class)
                .withArguments(eq(sessionStorageMock), any(StringBuilder.class));
        assertSame(retrieved, expectedSingleAttributeAdapterMock);
    }

    @Test
    public void multipleAttributes() throws Exception {

        // Given
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter("Simon", "MyCoolOrg", sessionStorageMock);
        PowerMockito.whenNew(BatchAttributeAdapter.class)
                .withArguments(eq(sessionStorageMock), any(StringBuilder.class))
                .thenReturn(expectedBatchAttributeAdapterMock);

        // When
        BatchAttributeAdapter retrieved = adapter.multipleAttributes();

        // Than
        PowerMockito.verifyNew(BatchAttributeAdapter.class)
                .withArguments(eq(sessionStorageMock), any(StringBuilder.class));
        assertSame(retrieved, expectedBatchAttributeAdapterMock);
    }

    @Test
    public void should_return_proper_SingleAttributeInterfaceAdapter_object() throws Exception {

        // Given
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter("Simon", "MyCoolOrg", sessionStorageMock);
        SingleUserRequestAdapter.SingleAttributeInterfaceAdapter expected =
                PowerMockito.mock(SingleUserRequestAdapter.SingleAttributeInterfaceAdapter.class);

        PowerMockito.whenNew(SingleUserRequestAdapter.SingleAttributeInterfaceAdapter.class)
                .withArguments("State")
                .thenReturn(expected);

        // When
        SingleUserRequestAdapter.SingleAttributeInterfaceAdapter retrieved = adapter.attribute("State");

        // Than
        PowerMockito.verifyNew(SingleUserRequestAdapter.SingleAttributeInterfaceAdapter.class)
                .withArguments(eq("State"));
        Assert.assertSame(retrieved, expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_if_invalid_param() {

        // Given
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter("Simon", "MyCoolOrg", sessionStorageMock);

        // When
        adapter.attribute("");

        // Than
        // throw an exception
    }

    @Test
    public void should_return_proper_adapter_class() throws Exception {

        // Given
        SingleUserRequestAdapter adapter = new SingleUserRequestAdapter("Simon", "MyCoolOrg", sessionStorageMock);

        SingleUserRequestAdapter.BatchAttributeInterfaceAdapter expected =
                PowerMockito.mock(SingleUserRequestAdapter.BatchAttributeInterfaceAdapter.class);
        PowerMockito.whenNew(SingleUserRequestAdapter.BatchAttributeInterfaceAdapter.class)
                .withNoArguments()
                .thenReturn(expected);

        // When
        SingleUserRequestAdapter.BatchAttributeInterfaceAdapter retrieved = adapter.attributes();

        // Than
        PowerMockito.verifyNew(SingleUserRequestAdapter.BatchAttributeInterfaceAdapter.class).withNoArguments();
        assertSame(expected, retrieved);
    }

    @Test
    public void should_return_OperationResult_object() throws Exception {

        // Given
        SingleUserRequestAdapter adapterSpy = PowerMockito.spy(new SingleUserRequestAdapter("Simon", "MyCoolOrg",
                sessionStorageMock));
        PowerMockito.doReturn(userJerseyRequestMock).when(adapterSpy, "buildRequest");
        PowerMockito.doReturn(operationResultMock).when(userJerseyRequestMock).get();

        // When
        OperationResult<ClientUser> retrieved = adapterSpy.get();

        // Than
        PowerMockito.verifyPrivate(adapterSpy, times(1)).invoke("buildRequest");
        Mockito.verify(userJerseyRequestMock, times(1)).get();
        Assert.assertSame(retrieved, operationResultMock);
    }

    @Test
    public void test8() throws Exception {

        // Given
        final String userId = "Simon";

        PowerMockito.mockStatic(JerseyRequest.class);
        SingleUserRequestAdapter adapterSpy = PowerMockito.spy(new SingleUserRequestAdapter("Simon", "MyCoolOrg", sessionStorageMock));

        PowerMockito.when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(ClientUser.class), eq(new String[]{"/organizations/MyCoolOrg/users/" + userId}), any(DefaultErrorHandler.class))).thenReturn(userJerseyRequestMock);
        PowerMockito.doReturn(operationResultMock).when(userJerseyRequestMock).get();

        // When
        OperationResult<ClientUser> retrieved = adapterSpy.get(userId);

        // Than
        verifyStatic(times(1));
        JerseyRequest.buildRequest(eq(sessionStorageMock), eq(ClientUser.class), eq(new String[]{"/organizations/MyCoolOrg/users/" + userId}), any(DefaultErrorHandler.class));
    }

    @Test
    public void test9() throws Exception {

        // Given
        final String userId = "Simon";

        PowerMockito.mockStatic(JerseyRequest.class);
        SingleUserRequestAdapter adapterSpy = PowerMockito.spy(new SingleUserRequestAdapter(sessionStorageMock, "MyCoolOrg"));

        PowerMockito.when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(ClientUser.class), eq(new String[]{"/organizations/MyCoolOrg/users/" + userId}), any(DefaultErrorHandler.class))).thenReturn(userJerseyRequestMock);
        PowerMockito.doReturn(operationResultMock).when(userJerseyRequestMock).get();

        // When
        OperationResult<ClientUser> retrieved = adapterSpy.get(userId);

        // Than
        verifyStatic(times(1));
        JerseyRequest.buildRequest(eq(sessionStorageMock), eq(ClientUser.class), eq(new String[]{"/organizations/MyCoolOrg/users/" + userId}), any(DefaultErrorHandler.class));
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, expectedSingleAttributeAdapterMock, expectedBatchAttributeAdapterMock,
                userJerseyRequestMock, operationResultMock);
    }
}