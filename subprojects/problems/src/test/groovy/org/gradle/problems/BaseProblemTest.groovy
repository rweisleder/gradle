/*
 * Copyright 2021 the original author or authors.
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

/*
 * This Spock specification was generated by the Gradle 'init' task.
 */
package org.gradle.problems


import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Supplier

class BaseProblemTest extends Specification implements ProblemsVerifier {
    def "can describe a simple problem"() {
        given:
        def problem = new TestProblem(
                TestProblemId.PROBLEM1,
                TestSeverity.LOW,
                context("c"),
                null,
                { "a short description of the problem" },
                { "a long description of the problem giving more details" },
                { "this is why this problem happened" },
                { "https://some.url" },
                []
        )

        expect:
        with(problem) {
            id == TestProblemId.PROBLEM1
            severity == TestSeverity.LOW
            what == "a short description of the problem"
            why.get() == "this is why this problem happened"
            documentationLink.get() == "https://some.url"
        }
        descriptionOf(problem) {
            hasShort "a short description of the problem"
            hasLong "a long description of the problem giving more details"
        }
    }

    @Unroll
    def "some parameters are mandatory (#item)"() {
        when:
        new TestProblem(
                TestProblemId.PROBLEM1,
                TestSeverity.LOW,
                context,
                null,
                shortDesc,
                longDesc,
                reason,
                url,
                solutions
        )

        then:
        NullPointerException ex = thrown()
        ex.message == "$item must not be null"

        where:
        item                          | context        | shortDesc | longDesc | reason | url  | solutions
        "context"                     | null           | {}        | {}       | {}     | {}   | []
        "short description supplier"  | context("foo") | null      | {}       | {}     | {}   | []
        "long description supplier"   | context("foo") | {}        | null     | {}     | {}   | []
        "reason supplier"             | context("foo") | {}        | {}       | null   | {}   | []
        "documentation link supplier" | context("foo") | {}        | {}       | {}     | null | []
        "solutions"                   | context("foo") | {}        | {}       | {}     | {}   | null
    }

    def "can associate a solution to a problem"() {
        given:
        def problem = new TestProblem(
                TestProblemId.PROBLEM1,
                TestSeverity.LOW,
                context("c"),
                null,
                { "a short description of the problem" },
                { "a long description of the problem giving more details" },
                { "this is why this problem happened" },
                { "https://some.url" },
                [ { solution("hello") } as Supplier]
        )

        expect:
        descriptionOf(problem.possibleSolutions[0]) {
            hasShort "hello"
            doesNotHaveLong()
        }
    }
}