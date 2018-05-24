package info.markovy.bakingapp.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;

import javax.inject.Inject;

import info.markovy.bakingapp.R;
import info.markovy.bakingapp.data.Status;
import info.markovy.bakingapp.di.Injectable;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;
import info.markovy.bakingapp.viewmodel.RecipeViewModel;
import info.markovy.bakingapp.viewmodel.RecipeViewModelFactory;
import timber.log.Timber;



public class RecipeCardsFragment extends Fragment implements Injectable{
    private RendererRecyclerViewAdapter mRecyclerViewAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RecipeListViewModel viewModel;

    @Inject
    NavigationController navigationController;


    public static RecipeCardsFragment newInstance() {
        RecipeCardsFragment recipeCardsFragment = new RecipeCardsFragment();
        return recipeCardsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_cards, container, false);
        mRecyclerViewAdapter = new RendererRecyclerViewAdapter();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeListViewModel.class);

        mRecyclerViewAdapter.registerRenderer(new ViewBinder<>(
                R.layout.recipe_card,
                RecipeViewModel.class,
                (model, finder, payloads) -> finder
                        //  .setBackground(R.id.recipe_card_image, model.getBackground())
                        .find(R.id.recipe_card_image, (ViewProvider<ImageView>) imageView -> {
                            Glide.with(getContext()).load(model.getImage()).into(imageView);
                        })
                        .setText(R.id.recipe_card_name, model.getName())
                        // TODO String extarpolation
                        .setText(R.id.recipe_card_details, "Ingridients: " + model.getIngredients_num() + ", " + "Steps: " + model.getSteps_num())
                        .setText(R.id.recipe_subtitle_text, "For " + model.getServings() + " servings.")

                        .setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View view) {
                                if(viewModel != null && model != null ){
                                    navigationController.navigateToDetail(model.getRecipe().getId());

                                }
                                else
                                    Timber.e("Some nullable model or view model pasesed");
                            }
                        })

        ));
        ProgressBar progress = (ProgressBar) view.findViewById(R.id.recipe_list_progress);
        TextView tv_error_message = (TextView) view.findViewById(R.id.recipe_list_error_message);
        final RecyclerView recyclerView  = view.findViewById(R.id.recipe_list);
        final int columns = getResources().getInteger(R.integer.recipes_columns);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), columns));
        recyclerView.setAdapter(mRecyclerViewAdapter);

        viewModel.getRecipes().observe(this, recipes_resource -> {
            // update UI
            if(recipes_resource.status == Status.LOADING){
                progress.setVisibility(View.VISIBLE);
                tv_error_message.setVisibility(View.GONE);
            } else if(recipes_resource.status == Status.ERROR){
                progress.setVisibility(View.GONE);
                if(recipes_resource.message != null){
                    tv_error_message.setText(recipes_resource.message);
                    tv_error_message.setVisibility(View.VISIBLE);
                }
            }else {
                progress.setVisibility(View.GONE);
                tv_error_message.setVisibility(View.GONE);
                if(recipes_resource.data != null){
                    mRecyclerViewAdapter.setItems(RecipeViewModel.mapFromModel(recipes_resource.data));
                }
                getActivity().setTitle(getActivity().getTitle());
            }
        });

        return  view;
    }

}
