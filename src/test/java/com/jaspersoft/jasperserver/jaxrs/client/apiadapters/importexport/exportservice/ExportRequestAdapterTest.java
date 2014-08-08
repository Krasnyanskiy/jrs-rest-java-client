package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.exportservice;

import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestBuilder;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.ExportFailedException;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.importexport.StateDto;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
 * Unit tests for {@link ExportRequestAdapter}
 */
@PrepareForTest({JerseyRequest.class})
public class ExportRequestAdapterTest extends PowerMockTestCase {

    @Mock
    private StateDto stateMock;

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<StateDto> requestStateDtoMock;

    @Mock
    private JerseyRequest<InputStream> requestInputStreamMock;

    @Mock
    private OperationResult<StateDto> operationResultStateDtoMock;

    @Mock
    private OperationResult<InputStream> operationResultInputStreamMock;

    @Mock
    private Callback<OperationResult<InputStream>, Object> callback;

    @Mock
    private RequestBuilder<InputStream> streamRequestBuilderMock;

    @Mock
    private Callback<OperationResult<StateDto>, Object> operationResultObjectCallback;

    private ExportRequestAdapter adapterSpy;
    private String taskId = "njkhfs8374";
    private String[] fakeArrayPath = new String[]{"/export", taskId, "/state"};

    @BeforeMethod
    public void before() {
        initMocks(this);
        adapterSpy = Mockito.spy(new ExportRequestAdapter(sessionStorageMock, taskId));
    }

    @Test(testName = "constructor")
    public void should_pass_corresponding_params_to_the_constructor_and_invoke_parent_class_constructor_with_these_params()
            throws IllegalAccessException {

        // When
        ExportRequestAdapter adapter = new ExportRequestAdapter(sessionStorageMock, taskId);
        SessionStorage retrievedSessionStorage = adapter.getSessionStorage();

        Field field = field(ExportRequestAdapter.class, "taskId");
        Object retrievedField = field.get(adapter);

        // Than
        assertSame(retrievedField, taskId);
        assertEquals(retrievedSessionStorage, sessionStorageMock);
    }

    @Test(testName = "state")
    public void should_return_proper_OperationResult_object() {

        // Given
        mockStatic(JerseyRequest.class);
        when(JerseyRequest.buildRequest(sessionStorageMock, StateDto.class, fakeArrayPath)).thenReturn(requestStateDtoMock);
        when(requestStateDtoMock.get()).thenReturn(operationResultStateDtoMock);

        // When
        ExportRequestAdapter adapter = new ExportRequestAdapter(sessionStorageMock, taskId);
        OperationResult<StateDto> opResult = adapter.state();

        // Then
        assertSame(opResult, operationResultStateDtoMock);
    }

    @Test(testName = "fetch")
    public void should_retrieve_streamed_OperationResult_object_when_status_is_finished() {

        // Given
        mockStatic(JerseyRequest.class);
        doReturn(operationResultStateDtoMock).when(adapterSpy).state();
        doReturn(stateMock).when(operationResultStateDtoMock).getEntity();
        doReturn("finished").when(stateMock).getPhase();

        when(JerseyRequest.buildRequest(sessionStorageMock, InputStream.class,
                new String[]{"/export", taskId, "/exportFile"})).thenReturn(requestInputStreamMock);

        doReturn(operationResultInputStreamMock).when(requestInputStreamMock).get();

        // When
        OperationResult<InputStream> retrieved = adapterSpy.fetch();

        // Than
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultInputStreamMock);
    }

    @Test(testName = "fetch", expectedExceptions = ExportFailedException.class)
    public void should_retrieve_streamed_OperationResult_object_when_status_is_failed() {

        // Given
        mockStatic(JerseyRequest.class);
        doReturn(operationResultStateDtoMock).when(adapterSpy).state();
        doReturn(stateMock).when(operationResultStateDtoMock).getEntity();
        doReturn("inprogress").doReturn("failed").when(stateMock).getPhase();

        when(JerseyRequest.buildRequest(sessionStorageMock, InputStream.class,
                new String[]{"/export", taskId, "/exportFile"})).thenReturn(requestInputStreamMock);

        doReturn(operationResultInputStreamMock).when(requestInputStreamMock).get();

        // When
        OperationResult<InputStream> retrieved = adapterSpy.fetch();

        // Than
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultInputStreamMock);
    }

    @Test(testName = "fetch", expectedExceptions = ExportFailedException.class)
    public void should_retrieve_streamed_OperationResult_object_when_status_is_failed_but_state_has_error_descriptor() {

        // Given
        mockStatic(JerseyRequest.class);
        doReturn(operationResultStateDtoMock).when(adapterSpy).state();
        doReturn(stateMock).when(operationResultStateDtoMock).getEntity();
        doReturn("inprogress").doReturn("failed").when(stateMock).getPhase();
        doReturn(new ErrorDescriptor()).when(stateMock).getErrorDescriptor();

        when(JerseyRequest.buildRequest(sessionStorageMock, InputStream.class,
                new String[]{"/export", taskId, "/exportFile"})).thenReturn(requestInputStreamMock);

        doReturn(operationResultInputStreamMock).when(requestInputStreamMock).get();

        // When
        OperationResult<InputStream> retrieved = adapterSpy.fetch();

        // Than
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultInputStreamMock);
    }

    @Test(testName = "fetch", timeOut = 600)
    public void should_retrieve_streamed_OperationResult_object_when_status_is_failed_() {

        // Given
        mockStatic(JerseyRequest.class);
        doReturn(operationResultStateDtoMock).when(adapterSpy).state();
        doReturn(stateMock).when(operationResultStateDtoMock).getEntity();
        doReturn("inprogress").doReturn("inprogress").doReturn("finished").when(stateMock).getPhase();

        when(JerseyRequest.buildRequest(sessionStorageMock, InputStream.class,
                new String[]{"/export", taskId, "/exportFile"})).thenReturn(requestInputStreamMock);

        doReturn(operationResultInputStreamMock).when(requestInputStreamMock).get();

        // When
        OperationResult<InputStream> retrieved = adapterSpy.fetch();

        // Than
        assertNotNull(retrieved);
        assertSame(retrieved, operationResultInputStreamMock);
    }

    @Test(testName = "asyncFetch")
    public void should_return_RequestExecution_with_finished_operation() {

        // Given
        ExportRequestAdapter adapterSpy = PowerMockito.spy(new ExportRequestAdapter(sessionStorageMock, taskId));

        Object resultMock = PowerMockito.mock(Object.class);

        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(
                buildRequest(
                        eq(sessionStorageMock),
                        eq(InputStream.class),
                        eq(new String[]{"/export", taskId, "/exportFile"})))
                .thenReturn(requestInputStreamMock);

        PowerMockito.doReturn(streamRequestBuilderMock).when(requestStateDtoMock).setAccept("application/zip");
        PowerMockito.doReturn(operationResultStateDtoMock).when(adapterSpy).state();
        PowerMockito.doReturn(stateMock).when(operationResultStateDtoMock).getEntity();
        PowerMockito.doReturn("finished").when(stateMock).getPhase();
        PowerMockito.doReturn(operationResultInputStreamMock).when(requestInputStreamMock).get();
        PowerMockito.doReturn(resultMock).when(callback).execute(operationResultInputStreamMock);

        // When
        RequestExecution retrieved = adapterSpy.asyncFetch(callback);

        // Than
        PowerMockito.verifyStatic(times(1));
        JerseyRequest.buildRequest(
                eq(sessionStorageMock),
                eq(InputStream.class),
                eq(new String[]{"/export", taskId, "/exportFile"}));

        Mockito.verify(callback, times(1)).execute(operationResultInputStreamMock);
        Mockito.verify(operationResultStateDtoMock, times(1)).getEntity();
        Mockito.verify(stateMock, times(1)).getPhase();
        Mockito.verify(requestInputStreamMock, times(1)).setAccept("application/zip");
    }

    @Test(testName = "asyncFetch", timeOut = 2000)
    public void should_create_resource_with_waiting_while_op_result_is_ready() throws Exception {

        /*
        // Given
        ExportRequestAdapter adapterSpy = PowerMockito.spy(new ExportRequestAdapter(sessionStorageMock, taskId));

        Object resultMock = PowerMockito.mock(Object.class);

        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(
                buildRequest(
                        eq(sessionStorageMock),
                        eq(InputStream.class),
                        eq(new String[]{"/export", taskId, "/exportFile"})))
                .thenReturn(requestInputStreamMock);

        PowerMockito.doReturn(streamRequestBuilderMock).when(requestStateDtoMock).setAccept("application/zip");
        PowerMockito.doReturn(operationResultStateDtoMock).doReturn(operationResultStateDtoMock).when(adapterSpy).state();
        PowerMockito.doReturn(stateMock).doReturn(stateMock).when(operationResultStateDtoMock).getEntity();
        PowerMockito.doReturn("inprogress").doReturn("ready").doReturn("finished").when(stateMock).getPhase();
        PowerMockito.doReturn(operationResultInputStreamMock).when(requestInputStreamMock).get();
        PowerMockito.doReturn(resultMock).when(callback).execute(operationResultInputStreamMock);

        // When
        RequestExecution retrieved = adapterSpy.asyncFetch(callback);

        // Than
        PowerMockito.verifyStatic(times(1));
        JerseyRequest.buildRequest(
                eq(sessionStorageMock),
                eq(InputStream.class),
                eq(new String[]{"/export", taskId, "/exportFile"}));

        Mockito.verify(callback, times(1)).execute(operationResultInputStreamMock);
        Mockito.verify(operationResultStateDtoMock, times(2)).getEntity();
        Mockito.verify(stateMock, times(3)).getPhase();
        Mockito.verify(requestInputStreamMock, times(1)).setAccept("application/zip");
        */
    }

    @Test(testName = "asyncFetch", timeOut = 750)
    public void should_throw_en_exception_when_fetching_failed() throws Exception {

        // Given
        ExportRequestAdapter adapterSpy = PowerMockito.spy(new ExportRequestAdapter(sessionStorageMock, taskId));
        ErrorDescriptor descriptorMock = PowerMockito.mock(ErrorDescriptor.class);

        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(
                buildRequest(
                        eq(sessionStorageMock),
                        eq(InputStream.class),
                        eq(new String[]{"/export", taskId, "/exportFile"})))
                .thenReturn(requestInputStreamMock);

        PowerMockito.doReturn(streamRequestBuilderMock).when(requestStateDtoMock).setAccept("application/zip");
        PowerMockito.doReturn(operationResultStateDtoMock).when(adapterSpy).state();
        PowerMockito.doReturn(stateMock).when(operationResultStateDtoMock).getEntity();
        PowerMockito.doReturn(descriptorMock).when(stateMock).getErrorDescriptor();
        PowerMockito.doReturn("inprogress").doReturn("failed").doReturn("finished").when(stateMock).getPhase();

        // When
        RequestExecution retrieved = adapterSpy.asyncFetch(callback);

        // Than
        PowerMockito.verifyStatic(times(1));
        JerseyRequest.buildRequest(
                eq(sessionStorageMock),
                eq(InputStream.class),
                eq(new String[]{"/export", taskId, "/exportFile"}));

        Mockito.verify(operationResultStateDtoMock, times(1)).getEntity();
        Mockito.verify(stateMock, times(2)).getPhase();
        Mockito.verify(requestInputStreamMock, times(1)).setAccept("application/zip");
    }

    @Test
    /**
     * for {@link ExportRequestAdapter#asyncState(Callback)}
     */
    public void should_invoke_method_logic_asynchronously() throws Exception {

        // Given
        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(
                buildRequest(
                        eq(sessionStorageMock),
                        eq(StateDto.class),
                        eq(new String[]{"/export", taskId, "/state"})))
                .thenReturn(requestStateDtoMock);

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        ExportRequestAdapter taskAdapterSpy = PowerMockito.spy(new ExportRequestAdapter(sessionStorageMock, taskId));

        final Callback<OperationResult<StateDto>, Void> callback = spy(new Callback<OperationResult<StateDto>, Void>() {
            public Void execute(OperationResult<StateDto> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        PowerMockito.doReturn(operationResultStateDtoMock).when(requestStateDtoMock).get();
        PowerMockito.doReturn(null).when(callback).execute(operationResultStateDtoMock);

        // When
        RequestExecution retrieved = taskAdapterSpy.asyncState(callback);

        // Wait
        synchronized (callback) {
            callback.wait(1000);
        }

        // Than
        Assert.assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(callback, times(1)).execute(operationResultStateDtoMock);
        verify(requestStateDtoMock, times(1)).get();
        //verify(requestStateDtoMock, times(1)).addParams(any(MultivaluedHashMap.class));

        /*
        // Given
        ExportRequestAdapter taskAdapterSpy = PowerMockito.spy(new ExportRequestAdapter(sessionStorageMock, taskId));
        Object objectMock = PowerMockito.mock(Object.class);

        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(
                buildRequest(
                        eq(sessionStorageMock),
                        eq(StateDto.class),
                        eq(new String[]{"/export", taskId, "/state"})))
                .thenReturn(requestStateDtoMock);

        PowerMockito.doReturn(operationResultStateDtoMock).when(requestStateDtoMock).get();
        PowerMockito.doReturn(objectMock).when(operationResultObjectCallback).execute(operationResultStateDtoMock);

        // When
        RequestExecution retrieved = taskAdapterSpy.asyncState(operationResultObjectCallback);

        // Than
        Assert.assertNotNull(retrieved);

        PowerMockito.verifyStatic(times(1));
        JerseyRequest.buildRequest(
                eq(sessionStorageMock),
                eq(StateDto.class),
                eq(new String[]{"/export", taskId, "/state"}));

        Mockito.verify(requestStateDtoMock, times(1)).get();
        Mockito.verify(operationResultObjectCallback, times(1)).execute(operationResultStateDtoMock);
        */
    }

    @Test
    public void should_invoke_method_logic_asynchronously_with_() throws Exception {

        /* Given */
        mockStatic(JerseyRequest.class);
        PowerMockito.doReturn(requestStateDtoMock).when(JerseyRequest.class, "buildRequest",
                eq(sessionStorageMock),
                eq(StateDto.class),
                eq(new String[]{"/export", "task77", "/state"}));

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        ExportRequestAdapter requestAdapterSpy = new ExportRequestAdapter(sessionStorageMock, "task77");
        Callback<OperationResult<StateDto>, Void> callbackSpy = PowerMockito.spy(new Callback<OperationResult<StateDto>, Void>() {
            @Override
            public Void execute(OperationResult<StateDto> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });

        PowerMockito.doReturn(operationResultStateDtoMock).when(requestStateDtoMock).get();
        PowerMockito.doReturn(null).when(callbackSpy).execute(operationResultStateDtoMock);

        /* When */
        RequestExecution retrieved = requestAdapterSpy.asyncState(callbackSpy);

        /* Wait */
        synchronized (callbackSpy) {
            callbackSpy.wait(1000);
        }

        /* Than */
        Assert.assertNotNull(retrieved);
        Assert.assertNotSame(currentThreadId, newThreadId.get());

        Mockito.verify(callbackSpy, times(1)).execute(operationResultStateDtoMock);
        Mockito.verify(requestStateDtoMock, times(1)).get();
    }

//    @Test
//    public void test() throws Exception {
//
//        /* Given */
//        mockStatic(JerseyRequest.class);
//        PowerMockito.doReturn(requestMock).when(JerseyRequest.class, "buildRequest", storageMock, StateDto.class, new String[]{"/export", "task77", "/state"});
//
//        final AtomicInteger newThreadId = new AtomicInteger();
//        Callback<OperationResult<StateDto>, Void> callbackSpy = spy(new Callback<OperationResult<StateDto>, Void>() {
//            @Override
//            public Void execute(OperationResult<StateDto> data) {
//                newThreadId.set((int) Thread.currentThread().getId());
//                synchronized (this) {
//                    this.notify();
//                }
//                return null;
//            }
//        });
//
//        /* When */
//        ExportRequestAdapter adapter = new ExportRequestAdapter(storageMock, "task77");
//        RequestExecution result = adapter.asyncState(null);
//
//        synchronized (callbackSpy) {
//            callbackSpy.wait(5000);
//        }
//
//        /* Than */
//        verifyStatic(times(1));
//        buildRequest(storageMock, StateDto.class, new String[]{"/export", "task77", "/state"});
//        assertSame(result, null);
//    }

    @AfterMethod
    public void after() {
        reset(stateMock, sessionStorageMock, requestStateDtoMock, requestInputStreamMock,
                operationResultStateDtoMock, operationResultInputStreamMock, callback, streamRequestBuilderMock,
                operationResultObjectCallback);
    }
}



