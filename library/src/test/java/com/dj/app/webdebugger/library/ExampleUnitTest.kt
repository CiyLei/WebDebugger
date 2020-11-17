package com.dj.app.webdebugger.library

import org.codehaus.janino.SimpleCompiler
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)

        val classFiles = SimpleCompiler().apply {
            cook("""
                package com.dj.app.webdebugger.library;

                public class T {
                    void t() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                
                            }
                        }).start();
                    }
                }

            """.trimIndent())
        }.classFiles
    }
}
