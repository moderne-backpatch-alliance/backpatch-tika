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
package org.apache.tika.parser.iptc;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;

public class IptcAnpaParserTest {

    // SOH header STX body, with no ETX: the body section runs to the end of the input
    private static byte[] message(String body) {
        return ("\u0001hdr\u0002" + body).getBytes(US_ASCII);
    }

    private static String parse(byte[] input) throws Exception {
        BodyContentHandler handler = new BodyContentHandler();
        new IptcAnpaParser().parse(new ByteArrayInputStream(input), handler,
                new Metadata(), new ParseContext());
        return handler.toString();
    }

    @Test(timeout = 20000)
    public void testInputEndingInsideHeading() throws Exception {
        parse(message("^abc"));
    }

    @Test(timeout = 20000)
    public void testInputEndingInsideTitle() throws Exception {
        parse(message("^heading<\r\n^title"));
    }

    @Test(timeout = 20000)
    public void testTerminatedMessageStillParses() throws Exception {
        String text = parse(message("^heading<\r\n^title<\r\nthe story text\r\n\u0003"));
        assertTrue(text, text.contains("the story text"));
    }
}
