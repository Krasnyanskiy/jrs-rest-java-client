package com.jaspersoft.jasperserver.jaxrs.client.core.exceptions;

import com.jaspersoft.jasperserver.jaxrs.client.dto.common.ErrorDescriptor;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit tests for {@link com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.NoResultException}
 */
public class NoResultExceptionTest {

    @Test
    public void should_invoke_parent_constructor() {
        NoResultException exception = new NoResultException();
        assertNotNull(exception.getErrorDescriptors());
    }

    @Test
    public void should_pass_message_to_parent_class() {
        NoResultException exception = new NoResultException("msg");
        assertEquals("msg", exception.getMessage());
    }

    @Test
    public void should_pass_message_and_descriptors_to_parent_class() {
        List<ErrorDescriptor> expected = new ArrayList<ErrorDescriptor>();
        NoResultException exception = new NoResultException("msg", expected);
        assertEquals(expected, exception.getErrorDescriptors());
        assertEquals("msg", exception.getMessage());
    }
}