package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.users.attributes;

import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestBuilder;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.mockito.Mock;
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

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
 * Unit tests for {@link SingleAttributeAdapter}
 */
@PrepareForTest({JerseyRequest.class, SingleAttributeAdapter.class, MultivaluedHashMap.class})
public class SingleAttributeAdapterTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<ClientUserAttribute> requestMock;

    @Mock
    private JerseyRequest ungenerifiedRequestMock;


    @Mock
    private Callback<OperationResult<ClientUserAttribute>, Object> callbackMock;

    @Mock
    private Callback<OperationResult, Object> ungenerifiedCallbackMock;

    @Mock
    private Object resultMock;

    @Mock
    private OperationResult<ClientUserAttribute> operationResultMock;

    @Mock
    private RequestBuilder<ClientUserAttribute> requestBuilderMock;

    @Mock
    private ClientUserAttribute userAttribute;

    @Mock
    private OperationResult operationResultMock2;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void constructor() {
        new SingleAttributeAdapter(null, null);
    }

    @Test
    /**
     * -- for {@link SingleAttributeAdapter#asyncGet(Callback, String)}
     */
    @SuppressWarnings("unchecked")
    public void should_invoke_method_get_asynchronously_and_return_RequestExecution_object() throws Exception {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        SingleAttributeAdapter adapterSpy = PowerMockito.spy(new SingleAttributeAdapter(sessionStorageMock, new StringBuilder()));

        doReturn(requestMock).when(adapterSpy, "request");
        doReturn(operationResultMock).when(requestMock).get();

        Callback<OperationResult<ClientUserAttribute>, Void> callbackSpy = PowerMockito.spy(new Callback<OperationResult<ClientUserAttribute>, Void>() {
            public Void execute(OperationResult<ClientUserAttribute> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(null).when(callbackSpy).execute(operationResultMock);

        /* When */
        RequestExecution retrieved = adapterSpy.asyncGet(callbackSpy, "State");

        synchronized (callbackSpy) {
            callbackSpy.wait(100);
        }

        /* Than */
        assertNotNull(retrieved);
        //verifyPrivate(adapterSpy, times(1)).invoke("request");
        verify(callbackSpy).execute(operationResultMock);
        assertNotSame(currentThreadId, newThreadId.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void asyncDelete() throws Exception {

        /* Given */
        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        SingleAttributeAdapter adapterSpy = PowerMockito.spy(new SingleAttributeAdapter(sessionStorageMock, new StringBuilder()));

        doReturn(ungenerifiedRequestMock).when(adapterSpy, "request");
        doReturn(operationResultMock).when(ungenerifiedRequestMock).delete();

        Callback<OperationResult, Void> callbackSpy = PowerMockito.spy(new Callback<OperationResult, Void>() {
            public Void execute(OperationResult data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        doReturn(null).when(callbackSpy).execute(operationResultMock);

        /* When */
        RequestExecution retrieved = adapterSpy.asyncDelete(callbackSpy, "State");

        synchronized (callbackSpy) {
            callbackSpy.wait(100);
        }

        /* Than */
        assertNotNull(retrieved);
        verify(callbackSpy).execute(operationResultMock);
        verify(ungenerifiedRequestMock).delete();
        assertNotSame(currentThreadId, newThreadId.get());
    }


    @Test(timeOut = 1000)
    @SuppressWarnings("unchecked")
    public void asyncUpdateOrCreate() throws Exception {

        // Given
        StringBuilder builderMock = PowerMockito.mock(StringBuilder.class);
        SingleAttributeAdapter adapterSpy = PowerMockito.spy(new SingleAttributeAdapter(sessionStorageMock, builderMock));

        doReturn(requestMock).when(adapterSpy, "request");
        doReturn(operationResultMock).when(requestMock).put(userAttribute);
        doReturn(resultMock).when(callbackMock).execute(operationResultMock);

        // When
        adapterSpy.asyncUpdateOrCreate(userAttribute, callbackMock, "State"); // State = attribute name

        // Than
        verifyPrivate(adapterSpy, times(1)).invoke("request");
        verify(callbackMock, times(1)).execute(operationResultMock);
        PowerMockito.verifyNoMoreInteractions(callbackMock);
    }


    @Test(testName = "private")
    public void request() throws Exception {

        // Given
//        final String attribute = "State";
//        PowerMockito.mockStatic(JerseyRequest.class);
//
//        PowerMockito.when(
//                buildRequest(
//                        eq(sessionStorageMock),
//                        eq(ClientUserAttribute.class),
//                        eq(new String[]{/*"/uri", "/attributes/", attribute}*/ "abc"}),
//                        eq(new DefaultErrorHandler())))
//                .thenReturn(requestMock);
//
//        SingleAttributeAdapter adapterSpy = PowerMockito.spy(new SingleAttributeAdapter(sessionStorageMock, new StringBuilder("/uri")));
//
//        // When
//        adapterSpy.delete(attribute);
//
//        // Than
//        PowerMockito.verifyStatic(times(1));
//        buildRequest(eq(sessionStorageMock), eq(ClientUserAttribute.class), eq(new String[]{/*"/uri", "/attributes/", attribute*/ "abc"}), eq(new DefaultErrorHandler()));
//        PowerMockito.verifyPrivate(adapterSpy, times(1)).invoke("request");
    }


    @Test
    public void updateOrCreate() throws Exception {

        // Given
        SingleAttributeAdapter adapterSpy = PowerMockito.spy(new SingleAttributeAdapter(sessionStorageMock, new StringBuilder("/uri")));

        doReturn(requestMock).when(adapterSpy, "request");
        doReturn(operationResultMock2).when(requestMock).put(userAttribute);
        doReturn("State").when(userAttribute).getName();

        // When
        OperationResult retrieved = adapterSpy.updateOrCreate(userAttribute);

        // Than
        verifyPrivate(adapterSpy, times(1)).invoke("request");
        verify(requestMock, times(1)).put(userAttribute);
        Assert.assertEquals(Whitebox.getInternalState(adapterSpy, "attributeName"), "State");
        assertSame(operationResultMock2, retrieved);
    }


    @Test
    public void get() throws Exception {

        // Given
        SingleAttributeAdapter adapterSpy = PowerMockito.spy(new SingleAttributeAdapter(sessionStorageMock, new StringBuilder("/uri")));
        doReturn(requestMock).when(adapterSpy, "request");
        doReturn(operationResultMock).when(requestMock).get();
        doReturn("State").when(userAttribute).getName();

        // When
        OperationResult retrieved = adapterSpy.get("State");

        // Than
        verifyPrivate(adapterSpy, times(1)).invoke("request");
        verify(requestMock, times(1)).get();
        Assert.assertEquals(Whitebox.getInternalState(adapterSpy, "attributeName"), "State");
        assertSame(operationResultMock, retrieved);
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, requestMock, callbackMock, resultMock, operationResultMock,
                requestBuilderMock, userAttribute, operationResultMock2);
    }
}