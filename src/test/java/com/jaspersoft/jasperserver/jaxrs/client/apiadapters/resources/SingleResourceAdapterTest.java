package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

/**
 * Unit Tests for {@link SingleResourceAdapter}
 */
@PrepareForTest(JerseyRequest.class)
public class SingleResourceAdapterTest extends PowerMockTestCase {

    @Captor
    private ArgumentCaptor<FormDataMultiPart> captor;


    @Mock
    private SessionStorage sessionStorageMock;


    @Mock
    private JerseyRequest<ClientResource> jerseyRequestMock;
    @Mock
    private OperationResult<ClientResource> operationResultMock;


    @Mock
    private JerseyRequest<Object> objectJerseyRequestMock;
    @Mock
    private OperationResult<Object> objectOperationResultMock;


    @Mock
    private JerseyRequest<ClientFile> clientFileJerseyRequestMock;
    @Mock
    private OperationResult<ClientFile> clientFileOperationResultMock;


    @Mock
    private File fileMock;


    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    /**
     * for {@link SingleResourceAdapter#asyncDetails(Callback)}
     */
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

    @Test
    @SuppressWarnings("unchecked")
    public void should_set_parameter() {

        /** Given **/
        SingleResourceAdapter adapter = new SingleResourceAdapter(sessionStorageMock, "resourceUri");

        /** When **/
        SingleResourceAdapter retrieved = adapter.parameter(ResourceServiceParameter.CREATE_FOLDERS, "true");

        /** Than **/
        assertSame(adapter, retrieved);
        MultivaluedMap<String, String> retrievedParams = (MultivaluedMap<String, String>) Whitebox.getInternalState(retrieved, "params");
        String param = retrievedParams.get(ResourceServiceParameter.CREATE_FOLDERS.getName()).get(0);
        assertEquals(param, "true");
    }

    @Test
    public void should_delete_resource_asynchronously() throws InterruptedException {

        /** Given **/
        String resourceUri = "requestId";

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        SingleResourceAdapter adapter = new SingleResourceAdapter(sessionStorageMock, resourceUri);

        PowerMockito.mockStatic(JerseyRequest.class);
        when(buildRequest(eq(sessionStorageMock), eq(Object.class), eq(new String[]{"/resources", resourceUri}))).thenReturn(objectJerseyRequestMock);
        doReturn(objectOperationResultMock).when(objectJerseyRequestMock).delete();

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
        doReturn(null).when(callback).execute(objectOperationResultMock);

        /** When **/
        RequestExecution retrieved = adapter.asyncDelete(callback);

        /** Wait **/
        synchronized (callback) {
            callback.wait(1000);
        }

        /** Than **/
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(objectJerseyRequestMock, times(1)).delete();
        verify(callback).execute(objectOperationResultMock);
    }

    @Test
    public void should_upload_file_asynchronously() throws InterruptedException {

        /** Given **/
        String resourceUri = "requestId";

        final AtomicInteger newThreadId = new AtomicInteger();
        final int currentThreadId = (int) Thread.currentThread().getId();

        SingleResourceAdapter adapter = new SingleResourceAdapter(sessionStorageMock, resourceUri);

        PowerMockito.mockStatic(JerseyRequest.class);
        when(buildRequest(eq(sessionStorageMock), eq(ClientFile.class), eq(new String[]{"/resources", resourceUri}))).thenReturn(clientFileJerseyRequestMock);
        doReturn(clientFileOperationResultMock).when(clientFileJerseyRequestMock).post(anyObject());

        final Callback<OperationResult<ClientFile>, Void> callback = spy(new Callback<OperationResult<ClientFile>, Void>() {
            @Override
            public Void execute(OperationResult<ClientFile> data) {
                newThreadId.set((int) Thread.currentThread().getId());
                synchronized (this) {
                    this.notify();
                }
                return null;
            }
        });
        doReturn(null).when(callback).execute(clientFileOperationResultMock);


        /** When **/
        RequestExecution retrieved = adapter.asyncUploadFile(fileMock, ClientFile.FileType.txt, "label_", "description_", callback);


        /** Wait **/
        synchronized (callback) {
            callback.wait(1000);
        }


        /** Than **/
        assertNotNull(retrieved);
        assertNotSame(currentThreadId, newThreadId.get());
        verify(clientFileJerseyRequestMock, times(1)).post(captor.capture());
        verify(callback).execute(clientFileOperationResultMock);

        FormDataMultiPart intercepted = captor.getValue();
        Map<String, List<FormDataBodyPart>> recievedFields = intercepted.getFields();

        assertSame(recievedFields.get("label").get(0).getValue(), "label_");
        assertSame(recievedFields.get("description").get(0).getValue(), "description_");
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, jerseyRequestMock, operationResultMock, objectJerseyRequestMock,
                objectOperationResultMock, clientFileJerseyRequestMock,
                clientFileOperationResultMock, fileMock);
    }
}