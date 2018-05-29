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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import javax.inject.Inject;
import javax.inject.Singleton;

import info.markovy.bakingapp.R;
import timber.log.Timber;

/**
 * A utility class that handles navigation in {@link MainActivity}.
 */
public class NavigationController {
    private static final String RECIPES_TAG = "RECIPES_TAG";
    private static final String STEP_TAG = "STEP_TAG";
    public static final String DETAIL_TAG = "DETAIL_TAG";
    private static final String BACKSTACK_DETAIL = "BACKSTACK_DETAIL";
    private final int containerId;
    private final int detailContainerId;
    private final FragmentManager fragmentManager;

    public void setTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
        Timber.d("Navigation controller set for 2 panes " + mTwoPane);
    }

    private  boolean mTwoPane;
    private final int masterContainerId;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.frameLayout;
        this.masterContainerId = R.id.recipestep_master_container;
        this.detailContainerId = R.id.recipestep_detail_container;
        this.mTwoPane = mainActivity.isTwoPane();
        Timber.d("Navigation controller created 2 panes " + mTwoPane);
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToRecipes() {
        RecipeCardsFragment recipeCardsFragment = RecipeCardsFragment.newInstance();

        fragmentManager.beginTransaction()
                .replace(containerId, recipeCardsFragment, RECIPES_TAG)
                .commitNowAllowingStateLoss();
    }

    public void navigateToDetail(Integer id) {
        RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(id);
        String tag = DETAIL_TAG;
        if(mTwoPane) {
            Fragment fragmentByTag = fragmentManager.findFragmentByTag(RECIPES_TAG);
            RecipeStepDetailFragment stepDetailFragment = RecipeStepDetailFragment.newInstance(0); // navigate to 0
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if(fragmentByTag!=null ) fragmentTransaction.remove(fragmentByTag);
                    fragmentTransaction.replace(masterContainerId, fragment, tag)
                    .replace(detailContainerId, stepDetailFragment, STEP_TAG)
                    .addToBackStack(BACKSTACK_DETAIL)
                    .commit();
        }
        else
            fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToStep(Integer stepIdx) {
        if(mTwoPane){
            RecipeStepDetailFragment fragmentByTag = (RecipeStepDetailFragment)fragmentManager.findFragmentByTag(STEP_TAG);
            if(fragmentByTag != null){ // reuse fragment
                fragmentByTag.navigateToStep(stepIdx);
            } else {
                RecipeStepDetailFragment stepDetailFragment = RecipeStepDetailFragment.newInstance(stepIdx);
                fragmentManager.beginTransaction()
                    .replace(detailContainerId, stepDetailFragment, STEP_TAG)
                    .addToBackStack(BACKSTACK_DETAIL)
                    .commit();
            }


        } else {
            RecipeStepDetailFragment stepDetailFragment = RecipeStepDetailFragment.newInstance(stepIdx);
            fragmentManager.beginTransaction()
                    .replace(containerId, stepDetailFragment, STEP_TAG)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }


}
