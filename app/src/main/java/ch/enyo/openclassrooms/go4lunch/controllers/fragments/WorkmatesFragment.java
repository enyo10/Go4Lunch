package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


import android.support.v4.app.Fragment;
import android.view.View;


import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment {



    @Override
    public BaseFragment newInstance() {
        WorkmatesFragment workmatesFragment=new WorkmatesFragment();
        workmatesFragment.name="Workmates";
        return workmatesFragment;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_workmates;
    }

    @Override
    protected void configureDesign(View v) {

    }

    @Override
    protected void configureView() {

    }

    @Override
    protected void configureOnclickRecyclerView() {

    }


}
