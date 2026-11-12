/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.woodstox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tika.parser.ParseContext;
import org.junit.Test;

public class WoodstoxDtdRefusedTest {

    private static final String DECLARED_EXTERNAL_ENTITY =
            "<!DOCTYPE foo [<!ENTITY bar SYSTEM \"tutorials.dtd\">]><foo>&bar;</foo>";

    @Test
    public void testDtdAndExternalEntitySupportAreOff() {
        XMLInputFactory factory = new ParseContext().getXMLInputFactory();
        assertTrue(factory.getClass().getName(), factory.getClass().getName().contains("com.ctc.wstx"));
        assertEquals(Boolean.FALSE, factory.getProperty(XMLInputFactory.SUPPORT_DTD));
        assertEquals(Boolean.FALSE, factory.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES));
    }

    @Test
    public void testDeclaredExternalEntityIsRefused() throws Exception {
        XMLStreamReader reader = new ParseContext().getXMLInputFactory().createXMLStreamReader(
                new ByteArrayInputStream(DECLARED_EXTERNAL_ENTITY.getBytes(StandardCharsets.UTF_8)));
        try {
            while (reader.hasNext()) {
                reader.next();
            }
            fail("the declared external entity was not refused");
        } catch (XMLStreamException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Undeclared general entity"));
        }
    }
}
