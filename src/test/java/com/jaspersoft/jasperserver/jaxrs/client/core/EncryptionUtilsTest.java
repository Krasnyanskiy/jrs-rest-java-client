package com.jaspersoft.jasperserver.jaxrs.client.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.jettison.json.JSONObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link EncryptionUtils}
 */
@PrepareForTest({
        EncryptionUtils.class,
        JSONObject.class,
        LogFactory.class,
        Cipher.class,
        KeyFactory.class,
        Log.class
})
public class EncryptionUtilsTest extends PowerMockTestCase {

    @Mock
    private Log logMock;

    @Mock
    private PublicKey keyMock;

    @Mock
    private Response responseMock;

    @Mock
    public KeyFactory keyFactoryMock;

    @Mock
    public BigInteger publicExponentMock;

    @Mock
    public BigInteger modulusMock;

    @Mock
    public RSAPublicKeySpec rsaPublicKeySpecMock;

    @Mock
    public Cipher cipherMock;

    @Mock
    public JSONObject jsonObjectMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    public void should_invoke_all_private_static_methods_only_once() throws Exception {

        // Given
        PowerMock.mockStaticPartial(EncryptionUtils.class, "getEncryptedPassword", "getPublicKey");

        Method[] keys = MemberMatcher.methods(EncryptionUtils.class, "getPublicKey");
        Method[] passwords = MemberMatcher.methods(EncryptionUtils.class, "getEncryptedPassword");

        PowerMock.expectPrivate(EncryptionUtils.class, keys[0], "n", "e").andReturn(keyMock);
        PowerMock.expectPrivate(EncryptionUtils.class, passwords[0], keyMock, "pwd").andReturn("encryptedPassword");

        PowerMock.replay(EncryptionUtils.class);

        // When
        String retrieved = EncryptionUtils.encryptPassword("pwd", "n", "e");

        // Than
        PowerMock.verify(EncryptionUtils.class);
        Assert.assertNotNull(retrieved);
        assertEquals(retrieved, "encryptedPassword");
    }

    @Test(enabled = false)
    public void should_invoke_error_method_of_Log_class_only_once() throws Exception {

        // Given
        PowerMockito.mockStatic(LogFactory.class);
        /*Power*/Mockito.when(LogFactory.getLog(EncryptionUtils.class)).thenReturn(logMock);

        // When
        try {
            EncryptionUtils.encryptPassword("pass", "n", "e");
        } catch (RuntimeException e) {/* NOP */}

        // Than
        verify(logMock, times(1)).error(anyString()); // был вызван только один раз
    }

    @Test(enabled = false)
    public void should_invoke_info_method_of_Log_class_only_once() {

//        PowerMockito.mockStatic(LogFactory.class);
//        PowerMockito.when(LogFactory.getLog(EncryptionUtils.class)).thenReturn(logMock);
//        PowerMockito.when(responseMock.readEntity(String.class)).thenReturn("a");
//
//        try {
//            EncryptionUtils.parseEncryptionParams(responseMock);
//        } catch (Exception e) {
//            // NOP
//        }
//
//        verify(logMock, times(1)).info(anyString());

    }

    @Test(enabled = true)
    public void should_() throws Exception {

        /* Given */
        KeyFactory keyFactoryMock = PowerMockito.mock(KeyFactory.class);
        Cipher cipherMock = PowerMockito.mock(Cipher.class);

        PowerMockito.mockStatic(KeyFactory.class);
        PowerMockito.when(KeyFactory.getInstance("RSA")).thenReturn(keyFactoryMock);

        PowerMockito.mockStatic(Cipher.class);
        PowerMockito.when(Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider())).thenReturn(cipherMock);

        PowerMockito.whenNew(BigInteger.class).withArguments("e", 16).thenReturn(publicExponentMock);
        PowerMockito.whenNew(BigInteger.class).withArguments("n", 16).thenReturn(modulusMock);
        PowerMockito.whenNew(RSAPublicKeySpec.class).withArguments(modulusMock, publicExponentMock).thenReturn(rsaPublicKeySpecMock);

        PowerMockito.doReturn(keyMock).when(keyFactoryMock).generatePublic(rsaPublicKeySpecMock);
        PowerMockito.doNothing().when(cipherMock).init(1, keyMock);
        PowerMockito.doReturn(new byte[]{0, 23, 23, 113, 2}).when(cipherMock).doFinal(any(byte[].class));

        /* When */
        String retrieved = EncryptionUtils.encryptPassword("pass", "n", "e");

        /* Than */
        assertNotNull(retrieved);
        assertEquals(retrieved, "0017177102");

        PowerMockito.verifyStatic(times(1));
        Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider());

        PowerMockito.verifyStatic(times(1));
        KeyFactory.getInstance("RSA");

        PowerMockito.verifyNew(BigInteger.class).withArguments("e", 16);
        PowerMockito.verifyNew(BigInteger.class).withArguments("n", 16);
        PowerMockito.verifyNew(RSAPublicKeySpec.class).withArguments(modulusMock, publicExponentMock);

        Mockito.verify(keyFactoryMock, times(1)).generatePublic(rsaPublicKeySpecMock);
        Mockito.verify(cipherMock, times(1)).init(1, keyMock);
    }

    @Test
    public void parseEncryptionParams() throws Exception {

        /* Given */
        Mockito.doReturn("{number:1}").when(responseMock).readEntity(String.class);
        PowerMockito.whenNew(JSONObject.class).withArguments("{number:1}").thenReturn(jsonObjectMock);
        PowerMockito.doReturn("n_").doReturn("e_").when(jsonObjectMock).getString(anyString());

        /* When */
        Map<String, String> retrieved = EncryptionUtils.parseEncryptionParams(responseMock);

        /* Than */
        PowerMockito.verifyNew(JSONObject.class).withArguments("{number:1}");
        Mockito.verify(jsonObjectMock, times(2)).getString(anyString());

        assertTrue(retrieved.size() == 2);
        assertEquals(retrieved.get("e"), "e_");
    }

    @AfterMethod
    public void after() {
        Mockito.reset(logMock, keyMock, jsonObjectMock, publicExponentMock, keyFactoryMock, cipherMock,
                rsaPublicKeySpecMock, modulusMock, responseMock);
    }
}