/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.reark.data.stores;

import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reark.reark.data.stores.mock.SimpleMockContentProvider;
import io.reark.reark.data.stores.mock.SimpleMockStore;
import io.reark.reark.data.stores.mock.SimpleMockStoreCore;
import rx.functions.Action1;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RunWith(AndroidJUnit4.class)
public class ContentProviderStoreCoreTest extends ProviderTestCase2<SimpleMockContentProvider> {

    private SimpleMockStoreCore core;

    public ContentProviderStoreCoreTest() {
        super(SimpleMockContentProvider.class, SimpleMockStoreCore.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void getCached_WithId_WithData_ReturnsData_AndCompletes() {
        new ArrangeBuilder().withTestData();
        List<String> expected = singletonList("parsnip");

        core.getCached(SimpleMockStore.getIdFor("parsnip"))
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .assertNoErrors()
                .assertReceivedOnNext(expected);
    }

    @Test
    public void getCached_WithId_WithNoData_ReturnsNoValues_AndCompletes() {
        new ArrangeBuilder();

        core.getCached(SimpleMockStore.getIdFor("parsnip"))
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .assertNoErrors()
                .assertNoValues();
    }

    @Test
    public void getCached_WithNoId_WithData_ReturnsAllData_AndCompletes() {
        new ArrangeBuilder().withTestData();
        List<List<String>> expected = singletonList(asList("parsnip", "lettuce", "spinach"));

        core.getCached()
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .assertNoErrors()
                .assertReceivedOnNext(expected);
    }

    @Test
    public void getCached_WithNoId_WithNoData_ReturnsEmptyList_AndCompletes() {
        new ArrangeBuilder();
        List<List<String>> expected = singletonList(emptyList());

        core.getCached()
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .assertNoErrors()
                .assertReceivedOnNext(expected);
    }

    private class ArrangeBuilder {

        ArrangeBuilder() {
            core = new SimpleMockStoreCore(getMockContentResolver());
        }

        ArrangeBuilder withTestData() {
            Action1<String> insert = value ->
                    getProvider().insert(
                            core.getUriForId(SimpleMockStore.getIdFor(value)),
                            core.getContentValuesForItem(value)
                    );

            // Prepare the mock content provider with values
            insert.call("parsnip");
            insert.call("lettuce");
            insert.call("spinach");

            return this;
        }
    }

}
