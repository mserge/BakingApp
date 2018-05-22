package info.markovy.bakingapp;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import info.markovy.bakingapp.testing.SingleFragmentActivity;
import info.markovy.bakingapp.ui.MainActivity;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@Ignore("Doesn't work with custom runner")
@RunWith(AndroidJUnit4.class)
    public class MockSeviceTest extends InstrumentationTestCase {


        @Rule
        public ActivityTestRule<MainActivity> mActivityRule =
                new ActivityTestRule<>(MainActivity.class, true, false);
        private MockWebServer server;

        @Before
        public void setUp() throws Exception {
            super.setUp();
            server = new MockWebServer();
            server.start();
            injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        }

        @Test
        public void testListOfSampleRecipesShown() throws Exception {
            Map<String, String> headers = new HashMap<>();
            enqueueResponse("baking.json", headers);

            Intent intent = new Intent();
            mActivityRule.launchActivity(intent);

            onView(withId(R.id.recipe_list_error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }


        @After
        public void tearDown() throws Exception {
            server.shutdown();
        }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("api-response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        server.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)));
    }
    }
