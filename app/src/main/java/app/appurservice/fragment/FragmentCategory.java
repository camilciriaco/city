package app.appurservice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.appurservice.ActivityPlaceDetail;
import app.appurservice.ActivityMain;
import app.appurservice.R;
import app.appurservice.adapter.AdapterPlaceGrid;
import app.appurservice.data.DatabaseHandler;
import app.appurservice.data.SharedPref;
import app.appurservice.data.ThisApplication;
import app.appurservice.loader.ApiClientLoader;
import app.appurservice.model.ApiClient;
import app.appurservice.model.Category;
import app.appurservice.model.Place;
import app.appurservice.utils.Callback;
import app.appurservice.utils.Tools;

public class FragmentCategory extends Fragment {

    public static String TAG_CATEGORY   = "app.appurservice.tagCategory";

    private View view;
    private static RecyclerView recyclerView;
    private View lyt_progress;
    private static View lyt_not_found;
    private static AdapterPlaceGrid adapter;
    private DatabaseHandler db;
    private int category_id;
    private List<Place> items = new ArrayList<>();
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, null);

        // activate fragment menu
        setHasOptionsMenu(true);

        db = new DatabaseHandler(getActivity());
        sharedPref = new SharedPref(getActivity());
        category_id = getArguments().getInt(TAG_CATEGORY);

        lyt_progress = view.findViewById(R.id.lyt_progress);
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity())));

        displayDataFromDatabase();

        return view;
    }

    private void displayDataFromDatabase(){
        if(category_id == -1){       // All Place
            items = db.getAllPlace();
        }else if(category_id == -2){ // Favorites
            items = db.getAllFavorites();
        }else{
            items = db.getAllPlaceByCategory(category_id);

            Category category = db.getCategory(category_id);
            // analytics tracking
            ThisApplication.getInstance().trackScreenView("View category : "+category.name);
        }
        adapter = new AdapterPlaceGrid(getActivity(), items);
        adapter.setOnItemClickListener(new AdapterPlaceGrid.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Place p) {
                ActivityPlaceDetail.navigate((ActivityMain) getActivity(), v.findViewById(R.id.image), p);
            }
        });
        recyclerView.setAdapter(adapter);
        checkItems();
    }

    private static void checkItems(){
        if(adapter.getItemCount()==0){
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    public static void filterAdapter(String keyword){
        adapter.getFilter().filter(keyword);
        checkItems();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            actionRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionRefresh(){
        boolean conn = Tools.cekConnection(getActivity().getApplicationContext(), view);
        if(conn){
            if(!onProcess){
                onRefresh();
            }else{
                Snackbar.make(view, "Task still running", Snackbar.LENGTH_SHORT).show();
            }
        }else {
            Tools.noConnectionSnackBar(view);
        }
    }
    private boolean onProcess = false;
    private void onRefresh(){
        Snackbar.make(view, "Start refresh...", Snackbar.LENGTH_LONG).show();
        onProcess = true;
        showProgress(onProcess);
        ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
            @Override
            public void onSuccess(ApiClient result) {
                onProcess = false;
                showProgress(onProcess);
                // save result into database
                db.addListPlace(result.places);
                db.addListPlaceCategory(result.place_category);
                db.addListImages(result.images);

                displayDataFromDatabase();
                sharedPref.setRefreshPlaces(false);
                Snackbar.make(view, "Success", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(String result) {
                onProcess = false;
                showProgress(onProcess);
                Snackbar.make(view, "Refresh failed", Snackbar.LENGTH_LONG).show();
            }
        });
        task.execute();
    }

    private void showProgress(boolean show){
        if(show){
            lyt_progress.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        }else{
            lyt_progress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(sharedPref.isRefreshPlaces() || db.getPlacesSize() == 0){
            actionRefresh();
        }
    }
}
