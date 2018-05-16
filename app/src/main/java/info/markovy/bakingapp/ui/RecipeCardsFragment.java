package info.markovy.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;

import info.markovy.bakingapp.R;
import info.markovy.bakingapp.data.Status;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;
import info.markovy.bakingapp.viewmodel.RecipeViewModel;
import timber.log.Timber;



public class RecipeCardsFragment extends Fragment {
    private RendererRecyclerViewAdapter mRecyclerViewAdapter;
    private RecipeListViewModel viewModel;
    private NavigationController navigationController;


    public static RecipeCardsFragment newInstance(NavigationController navigationController) {
        RecipeCardsFragment recipeCardsFragment = new RecipeCardsFragment();
        recipeCardsFragment.navigationController = navigationController;
        return recipeCardsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_cards, container, false);
        mRecyclerViewAdapter = new RendererRecyclerViewAdapter();

        mRecyclerViewAdapter.registerRenderer(new ViewBinder<>(
                R.layout.recipe_card,
                RecipeViewModel.class,
                (model, finder, payloads) -> finder
                        //  .setBackground(R.id.recipe_card_image, model.getBackground())
                        .setText(R.id.recipe_card_name, model.getName())
                        .setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View view) {
                                if(viewModel != null && model != null )
                                    viewModel.setCurrentRecipe(model.getRecipe());
                                else
                                    Timber.e("Some nullable model or view model pasesed");
                            }
                        })

        ));
        ProgressBar progress = (ProgressBar) view.findViewById(R.id.recipe_list_progress);
        TextView tv_error_message = (TextView) view.findViewById(R.id.recipe_list_error_message);
        final RecyclerView recyclerView  = view.findViewById(R.id.recipe_list);
        recyclerView.setAdapter(mRecyclerViewAdapter);

        viewModel = ViewModelProviders.of(getActivity()).get(RecipeListViewModel.class);
        viewModel.getRecipes().observe(this, recipes_resource -> {
            // update UI
            if(recipes_resource.status == Status.LOADING){
                progress.setVisibility(View.VISIBLE);
                tv_error_message.setVisibility(View.GONE);
            } else if(recipes_resource.status == Status.ERROR){
                progress.setVisibility(View.GONE);
                if(recipes_resource.message != null)
                    tv_error_message.setText(recipes_resource.message);
            }else {
                progress.setVisibility(View.GONE);
                tv_error_message.setVisibility(View.GONE);
                if(recipes_resource.data != null){
                    mRecyclerViewAdapter.setItems(RecipeViewModel.mapFromModel(recipes_resource.data));
                }
            }
        });
        viewModel.getCurrentRecipe().observe(this, recipe -> {
            if(recipe != null){
                navigationController.navigateToDetail();
            } else
                Timber.d("Null recipe set, ignoring");
        });
        return  view;
    }

}
