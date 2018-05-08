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

package info.markovy.bakingapp;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.util.EspressoTestUtil;
import info.markovy.bakingapp.util.RecyclerViewMatcher;
import info.markovy.bakingapp.util.TaskExecutorWithIdlingResourceRule;
import info.markovy.bakingapp.util.TestUtil;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {
    @Rule
    public ActivityTestRule<RecipeListActivity> activityRule =
            new ActivityTestRule<>(RecipeListActivity.class, true, true);
    @Rule
    public TaskExecutorWithIdlingResourceRule executorRule =
            new TaskExecutorWithIdlingResourceRule();

    private RecipeListViewModel viewModel;

    private MutableLiveData<Resource<List<Recipe>>> results = new MutableLiveData<>();

    @Before
    public void init() {
        EspressoTestUtil.disableProgressBarAnimations(activityRule);
        viewModel = mock(RecipeListViewModel.class);
        when(viewModel.getRecipes()).thenReturn(results);

    }

    @Test
    public void loadResults() {
        List<Recipe> recipes = TestUtil.createRecipes(10);
        results.postValue(Resource.success(recipes));
        onView(listMatcher().atPosition(0))
                .check(matches(hasDescendant(withText("Recipe 0"))));
        onView(withId(R.id.recipe_list_error_message)).check(matches(not(isDisplayed())));
    }
//
//    @Test
//    public void dataWithLoading() {
//        Recipe recipe = TestUtil.createRecipe("foo", "bar", "desc");
//        results.postValue(Resource.loading(Arrays.asList(recipe)));
//        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo/bar"))));
//        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
//    }
//
//    @Test
//    public void error() {
//        results.postValue(Resource.error("failed to load", null));
//        onView(withId(R.id.error_msg)).check(matches(isDisplayed()));
//    }
//
//
//    @Test
//    public void navigateToRecipe() throws Throwable {
//        doNothing().when(viewModel).loadNextPage();
//        Recipe recipe = TestUtil.createRecipe("foo", "bar", "desc");
//        results.postValue(Resource.success(Arrays.asList(recipe)));
//        onView(withText("desc")).perform(click());
//        verify(navigationController).navigateToRecipe("foo", "bar");
//    }
//
//    @Test
//    public void loadMoreProgress() {
//        loadMoreStatus.postValue(new SearchViewModel.LoadMoreState(true, null));
//        onView(withId(R.id.load_more_bar)).check(matches(isDisplayed()));
//        loadMoreStatus.postValue(new SearchViewModel.LoadMoreState(false, null));
//        onView(withId(R.id.load_more_bar)).check(matches(not(isDisplayed())));
//    }
//
//    @Test
//    public void loadMoreProgressError() {
//        loadMoreStatus.postValue(new SearchViewModel.LoadMoreState(true, "QQ"));
//        onView(withText("QQ")).check(matches(
//                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
//    }

    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recipe_list);
    }
}