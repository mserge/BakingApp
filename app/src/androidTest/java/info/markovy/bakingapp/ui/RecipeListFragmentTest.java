/*
 * Copyright (C) 2017 The Android Open Source Project
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

package info.markovy.bakingapp.ui;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import info.markovy.bakingapp.R;
import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.testing.SingleFragmentActivity;
import info.markovy.bakingapp.util.EspressoTestUtil;
import info.markovy.bakingapp.util.RecyclerViewMatcher;
import info.markovy.bakingapp.util.ScreenshotTestWatcher;
import info.markovy.bakingapp.util.TaskExecutorWithIdlingResourceRule;
import info.markovy.bakingapp.util.TestUtil;
import info.markovy.bakingapp.util.ViewModelUtil;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// setup for E2E TestUnits explained
// http://www.davidwong.com.au/blog/2017/08/android-ui-test-mocking-the-viewmodel-with-or-without-dagger/
// https://medium.com/@hiBrianLee/writing-testable-android-mvvm-app-part-5-6a52c3b2a30e
// TODO http://www.davidwong.com.au/blog/2017/10/android-ui-test-mocking-the-viewmodel-with-or-without-dagger-part-2/

@RunWith(AndroidJUnit4.class)
public class RecipeListFragmentTest {
    @Rule
    public ActivityTestRule<SingleFragmentActivity> activityRule =
            new ActivityTestRule<>(SingleFragmentActivity.class, true, true);
    @Rule
    public TaskExecutorWithIdlingResourceRule executorRule =
            new TaskExecutorWithIdlingResourceRule();
    @Rule
    public TestRule watcher = new ScreenshotTestWatcher();

    private RecipeListViewModel viewModel;

    private MutableLiveData<Resource<List<Recipe>>> results = new MutableLiveData<>();
    private NavigationController navigationController;
    private MutableLiveData<Recipe> recipe = new MutableLiveData<>();

    @Before
    public void init() {
        EspressoTestUtil.disableProgressBarAnimations(activityRule);
        RecipeCardsFragment fragment = new RecipeCardsFragment();

        viewModel = mock(RecipeListViewModel.class);
        when(viewModel.getRecipes()).thenReturn(results);
        when(viewModel.getCurrentRecipe()).thenReturn(recipe);

        navigationController = mock(NavigationController.class);

        fragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        fragment.navigationController = navigationController;
        activityRule.getActivity().setFragment(fragment);
    }

    @Test
    public void loadResults() {
        List<Recipe> recipes = TestUtil.createRecipes(10);
        results.postValue(Resource.success(recipes));
        ViewInteraction viewInteraction = onView(listMatcher().atPosition(0));
        viewInteraction
                .check(matches(hasDescendant(withText("Recipe 0"))));
        onView(ViewMatchers.withId(R.id.recipe_list_error_message)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recipe_list_progress)).check(matches(not(isDisplayed())));
    }

    @Test
    public void dataWithLoading() {
        List<Recipe> recipes = TestUtil.createRecipes(10);
        results.postValue(Resource.loading(recipes));
        onView(withId(R.id.recipe_list_progress)).check(matches(isDisplayed()));
    }

    @Test
    public void error() {
        results.postValue(Resource.error("failed to load", null));
        onView(withId(R.id.recipe_list_error_message)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToRecipe() throws Throwable {
        List<Recipe> recipes = TestUtil.createRecipes(10);
        results.postValue(Resource.success(recipes));
        onView(withText("Recipe 0")).perform(click());
        verify(viewModel).setCurrentRecipe(recipes.get(0));
    }


    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recipe_list);
    }
}