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
package org.apache.tika.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tika.parser.ParseContext;
import org.junit.Test;

public class XMLReaderUtilsStaxConfigTest {

    @Test
    public void testExternalDtdAccessIsBlocked() throws Exception {
        //getProperty does not report this setting on the JDK, so check what the parser does.
        //SUPPORT_DTD and the resolver also stop this; undo them so only this setting decides.
        XMLInputFactory factory = new ParseContext().getXMLInputFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, true);
        factory.setXMLResolver((publicID, systemID, baseURI, namespace) -> null);
        XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(
                "<!DOCTYPE foo SYSTEM \"tutorials.dtd\"><foo/>".getBytes(StandardCharsets.UTF_8)));
        try {
            while (reader.hasNext()) {
                reader.next();
            }
            fail("the external DTD was not refused");
        } catch (XMLStreamException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("accessExternalDTD"));
        }
    }
}
