package com.hassanabid.androidcupcake.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hassanabid.androidcupcake.CupcakeListActivity;
import com.hassanabid.androidcupcake.R;
import com.hassanabid.androidcupcake.activities.CupcakeDetailActivity;
import com.hassanabid.androidcupcake.databinding.CupcakeDetailBinding;
import com.hassanabid.androidcupcake.model.Cupcake;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * A fragment representing a single cupcake detail screen.
 * This fragment is either contained in a {@link CupcakeListActivity}
 * in two-pane mode (on tablets) or a {@link CupcakeDetailActivity}
 * on handsets.
 */
public class CupcakeDetailFragment extends Fragment {

    private static final String LOG_TAG = CupcakeDetailFragment.class.getSimpleName();

    public static final String ARG_NAME_ID = "item_name_id";
    public static final String ARG_IMAGE_ID = "item_image_id";
    public static final String ARG_RATING_ID = "item_rating_id";
    public static final String ARG_PRICE_ID = "item_price_id";
    public static final String ARG_WRITER_ID = "item_writer_id";
    public static final String ARG_CREATED_ID = "item_createdat_id";

    private String mTitle;
    private String mImageURL;
    private Cupcake cake;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CupcakeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getActivity()).build();
        Realm.setDefaultConfiguration(realmConfig);

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        if (getArguments().containsKey(ARG_NAME_ID)) {

            mTitle = getArguments().getString(ARG_NAME_ID);
            mImageURL = getArguments().getString(ARG_IMAGE_ID);
            cake = realm.where(Cupcake.class).equalTo("name", mTitle).findFirst();
            if(cake != null)
                Log.d(LOG_TAG,"realm object retrieved : " + cake.getName());

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mTitle);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CupcakeDetailBinding binding  = DataBindingUtil.inflate(inflater, R.layout.cupcake_detail, container, false);
        View rootView = binding.getRoot();
        binding.setCake(cake);

//        if (mPrice != null) {
//            ((TextView) rootView.findViewById(R.id.cupcake_price)).setText(mPrice);
//        }

        if(mImageURL != null) {
            loadBackdrop(CupcakeListActivity.API_URL_PROD  + mImageURL,rootView);
        } else {
            Log.d(LOG_TAG,"imageurl is null");
        }
        return rootView;
    }


    private void loadBackdrop(String image_url, View root) {
        final ImageView imageView = (ImageView) getActivity().findViewById(R.id.cake_image);
        Glide.with(getActivity())
                .load(image_url)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }
}
