package info.markovy.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import info.markovy.bakingapp.R;
import info.markovy.bakingapp.di.Injectable;


public class RecipeStepDetailFragment extends Fragment implements Injectable {
    @Inject
    NavigationController navigationController;
    public static RecipeStepDetailFragment newInstance() {
        RecipeStepDetailFragment recipeStepDetailFragment = new RecipeStepDetailFragment();
        return recipeStepDetailFragment;
    }

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);


        return rootView;
    }


}
