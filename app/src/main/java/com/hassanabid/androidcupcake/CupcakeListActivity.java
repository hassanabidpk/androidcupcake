package com.hassanabid.androidcupcake;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hassanabid.androidcupcake.activities.CupCakeDetailActivity;
import com.hassanabid.androidcupcake.api.CupCakeApi;
import com.hassanabid.androidcupcake.api.CupCakeResponse;
import com.hassanabid.androidcupcake.fragments.CupCakeDetailFragment;
import com.hassanabid.androidcupcake.model.CupCake;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of cupcakes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CupCakeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CupCakeListActivity extends AppCompatActivity {


    private static final String LOG_TAG = CupCakeListActivity.class.getSimpleName();
    public static final String API_URL = "http://127.0.0.1:8000";
    public static final String API_URL_PROD = "https://djangocupcakeshop.azurewebsites.net";

    private List<CupCakeResponse> mCupcakeList;
    private ProgressBar progessBar;
    private TextView emptyTextView;
    private View recyclerView;

    private RealmConfiguration mRealmConfig;
    private Realm mRealm;


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupcake_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        progessBar = (ProgressBar) findViewById(R.id.progressBar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Feature coming soon!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRealmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(mRealmConfig);
        mRealm = Realm.getDefaultInstance();

        recyclerView = findViewById(R.id.cupcake_list);
        assert recyclerView != null;
        final RealmResults<CupCake> cupCakes = getRealmResults();
        if(cupCakes.size() == 0) {
            initiateCupcakeApi(recyclerView);
        }
        else {
            setupRecyclerView((RecyclerView) recyclerView,cupCakes);
        }

        if (findViewById(R.id.cupcake_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, RealmResults<CupCake> results) {
        progessBar.setVisibility(View.GONE);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(results));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RealmResults<CupCake> mRealmObjects;
        public SimpleItemRecyclerViewAdapter(RealmResults<CupCake> realmObjects) {
            mRealmObjects = realmObjects;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cupcake_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mRealmObject = mRealmObjects.get(position);
            holder.mTitle.setText(mRealmObjects.get(position).getName());
            Glide.with(holder.mImage.getContext())
                    .load(API_URL_PROD + mRealmObjects.get(position).getImage())
                    .centerCrop()
//                    .placeholder(R.drawable.loading_spinner)
                    .crossFade()
                    .into(holder.mImage);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CupCakeDetailFragment.ARG_NAME_ID, holder.mRealmObject.getName());
                        arguments.putString(CupCakeDetailFragment.ARG_IMAGE_ID,holder.mRealmObject.getImage());

                        CupCakeDetailFragment fragment = new CupCakeDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.cupcake_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CupCakeDetailActivity.class);
                        intent.putExtra(CupCakeDetailFragment.ARG_NAME_ID, holder.mRealmObject.getName());
                        intent.putExtra(CupCakeDetailFragment.ARG_IMAGE_ID, holder.mRealmObject.getImage());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRealmObjects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitle;
            public final ImageView mImage;
            public CupCake mRealmObject;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitle = (TextView) view.findViewById(R.id.title);
                mImage = (ImageView) view.findViewById(R.id.cupcakeImage);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitle.getText() + "'";
            }
        }
    }


    private void initiateCupcakeApi(final View recyclerView) {

        Log.d(LOG_TAG,"initiateCupcakeApi");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL_PROD)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CupCakeApi api = retrofit.create(CupCakeApi.class);
        Call<CupCakeResponse[]> call = api.getCupcakesList("json");
        progessBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<CupCakeResponse[]>() {
            @Override
            public void onResponse(Call<CupCakeResponse[]> call, Response<CupCakeResponse[]> response) {

                if (response.isSuccessful()) {
                    Log.d(LOG_TAG, "success - response is " + response.body());
                    mCupcakeList = Arrays.asList(response.body());
                    executeRealmWriteTransaction(mCupcakeList);

                } else {
                    progessBar.setVisibility(View.GONE);
                    Log.d(LOG_TAG, "failure response is " + response.raw().toString());

                }
            }

            @Override
            public void onFailure(Call<CupCakeResponse[]> call, Throwable t) {
                Log.e(LOG_TAG, " Error :  " + t.getMessage());
            }

        });

    }

    private void executeRealmWriteTransaction (final List<CupCakeResponse> cupcakeList) {

        Log.d(LOG_TAG,"saveRealmObjects : " + cupcakeList.size());
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                for (int i = 0; i < cupcakeList.size(); i++) {

                    CupCake cake = realm.createObject(CupCake.class);
                    cake.setName(cupcakeList.get(i).name);
                    cake.setRating(cupcakeList.get(i).rating);
                    cake.setPrice(cupcakeList.get(i).price);
                    cake.setImage(cupcakeList.get(i).image);
                    cake.setWritere(cupcakeList.get(i).writer);
                    cake.setCreatedAt(cupcakeList.get(i).createdAt);
                }


            }
        },new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,"savedRealmObjects");
                setupRecyclerView((RecyclerView) recyclerView,getRealmResults());
            }

            },new Realm.Transaction.OnError(){

            @Override
            public void onError(Throwable error) {
                Log.d(LOG_TAG,"error while writing to realm db :" + error.getMessage());
            }
        });
    }

    private RealmResults<CupCake> getRealmResults() {

        RealmResults<CupCake> sortedCakes = mRealm.where(CupCake.class).findAllSorted("rating", Sort.DESCENDING);
        Log.d(LOG_TAG,"getRealmResults : " + sortedCakes.size());
        return sortedCakes;
    }

    private void deleteRealmObjects() {

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(CupCake.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,"realm objects deleted");
                initiateCupcakeApi(recyclerView);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.download:
                deleteRealmObjects();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
