package info.markovy.bakingapp.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import javax.inject.Inject;

import info.markovy.bakingapp.R;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.data.Step;
import info.markovy.bakingapp.di.Injectable;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;


public class RecipeStepDetailFragment extends Fragment implements Injectable {
    private static final String STEP_IDX = "info.markovy.bakingapp.ui.STEP_IDX";
    @Inject
    NavigationController navigationController;

    private RecipeListViewModel viewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private TextView tvDetail;

    public static RecipeStepDetailFragment newInstance(int StepIdx) {
        RecipeStepDetailFragment recipeStepDetailFragment = new RecipeStepDetailFragment();
        Bundle args = new Bundle();
        args.putInt(STEP_IDX, StepIdx);
        recipeStepDetailFragment.setArguments(args);
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

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeListViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.containsKey(STEP_IDX)) {
            viewModel.setCurrentStep(args.getInt(STEP_IDX));
        }
        tvDetail = rootView.findViewById(R.id.recipestep_detail);

        if(viewModel.getCurrentStep() !=null){
            showStep(viewModel.getCurrentStep().getValue());
        }
        viewModel.getCurrentStep().observe(this, step -> {
            // update UI
            showStep(step);

        });
        return rootView;
    }

    private void showStep(Resource<Step> value) {
        // TODO loading
        if(value != null){
            Step step = value.data;
            if(step!= null)
              tvDetail.setText(step.getDescription());
        }
    }


}
