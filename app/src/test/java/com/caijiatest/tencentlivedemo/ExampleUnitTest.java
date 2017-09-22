package com.caijiatest.tencentlivedemo;

import com.caijiatest.tencentlivedemo.util.MixStreamHelper;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        new MixStreamHelper().mixStream(null, "8768_c84790876c", "8768_c84790874b");
    }
}