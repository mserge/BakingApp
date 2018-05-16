package info.markovy.bakingapp.viewmodel;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

public class CategoryViewModel implements ViewModel {
    private String string;

    public CategoryViewModel(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
