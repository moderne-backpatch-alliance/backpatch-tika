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
package org.apache.tika.sax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StandardsTextTest {

    @Test
    public void testLongNumberedHeaderReturnsInTime() {
        StringBuilder sb = new StringBuilder("1.");
        for (int i = 0; i < 30; i++) {
            sb.append("11.");
        }
        sb.append('x');
        String text = sb.toString();
        List<StandardReference> refs = assertTimeoutPreemptively(Duration.ofSeconds(10),
                () -> StandardsText.extractStandardReferences(text, 0.25));
        assertEquals(0, refs.size());
    }

    @Test
    public void testStandardsDocumentStillYieldsReferences() {
        String text = "1.0 SCOPE\n" + "this document covers antenna towers.\n" +
                "2.0 APPLICABLE DOCUMENTS\n" +
                "the tower shall comply with ANSI/TIA 222-G and NIST 800-53.\n" +
                "3.0 REQUIREMENTS\n" + "controls shall follow NIST 800-171.\n" +
                "4.0 NOTES\n" + "none.\n";
        assertEquals("[ANSI/TIA 222-G, NIST 800-53]",
                toStrings(StandardsText.extractStandardReferences(text, 0.75)));
        assertEquals("[ANSI/TIA 222-G, NIST 800-53, NIST 800-171]",
                toStrings(StandardsText.extractStandardReferences(text, 0.5)));
    }

    private static String toStrings(List<StandardReference> refs) {
        List<String> found = new ArrayList<>();
        for (StandardReference ref : refs) {
            found.add(ref.toString());
        }
        return found.toString();
    }
}
