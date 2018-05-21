package info.markovy.bakingapp.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import info.markovy.bakingapp.viewmodel.RecipeViewModelFactory;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RecipeListViewModel.class)
    abstract ViewModel bindRecipeListViewModel(RecipeListViewModel userViewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(RecipeViewModelFactory factory);
}
