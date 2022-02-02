/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.tasks.diagnostics.internal.text;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.gradle.internal.logging.text.StyledTextOutput;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.gradle.internal.logging.text.StyledTextOutput.Style.Header;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Normal;

/**
 * Simple utility for printing a table.
 */
public class StyledTable {
    private final String indent;
    private final List<String> headers;
    private final List<Row> rows;

    public StyledTable(String indent, List<String> headers, List<Row> rows) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).text.size() != headers.size()) {
                throw new IllegalArgumentException("Headers and row " + i + " must have the same number of columns.");
            }
        }
        this.indent = indent;
        this.headers = ImmutableList.copyOf(headers);
        this.rows = ImmutableList.copyOf(rows);
    }

    public void print(StyledTextOutput output) {
        int[] colWidths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            int finalI = i;
            colWidths[i] = Stream.concat(
                    Stream.of(headers.get(i)),
                    rows.stream().map(row -> row.text.get(finalI))
            ).mapToInt(String::length).max().orElse(0);
        }

        output.style(Header);
        printRow(output, colWidths, headers, ' ');
        output.style(Normal);
        // Print the separator row
        printRow(
            output, colWidths,
            IntStream.range(0, colWidths.length).mapToObj(i -> "").collect(ImmutableList.toImmutableList()),
            '-'
        );
        for (Row row : rows) {
            output.style(row.style);
            printRow(output, colWidths, row.text, ' ');
            output.style(Normal);
        }
    }

    private void printRow(StyledTextOutput output, int[] colWidths, List<String> row, char padChar) {
        output.withStyle(Normal).text(indent + "|" + padChar);
        for (int i = 0; i < row.size(); i++) {
            output.text(Strings.padEnd(row.get(i), colWidths[i], padChar));
            if (i < row.size() - 1) {
                output.withStyle(Normal).text(padChar + "|" + padChar);
            }
        }
        output.withStyle(Normal).text(padChar + "|");
        output.println();
    }

    public static final class Row {
        public final List<String> text;
        public final StyledTextOutput.Style style;

        public Row(List<String> text, StyledTextOutput.Style style) {
            this.text = ImmutableList.copyOf(text);
            this.style = style;
        }
    }
}
