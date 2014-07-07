package com.asa.imhere.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asa.imhere.AppData;
import com.asa.imhere.AsaBaseFragment;
import com.asa.imhere.R;
import com.asa.imhere.lib.model.Nameable;
import com.asa.imhere.model.DatabaseQueries;
import com.asa.imhere.model.Favorite;
import com.asa.imhere.model.ImHereContract;
import com.asa.imhere.otto.BusProvider;
import com.asa.imhere.otto.FavoriteDeletedEvent;
import com.asa.imhere.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class FavoriteFragment extends AsaBaseFragment implements OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public final static String TAG = "FavoriteFragment";

    @InjectView(R.id.explore_list)
    ListView mListView;
    @InjectView(R.id.explore_empty_text)
    TextView mTextEmpty;

    private FavoriteAdapter mAdapter;
    // TODO - move this to MainActivity so the favorites tab can use it?
    private ArrayList<Nameable> mFavorites;

    // TODO - Move retrieving favorites off of the UI thread.

    public static FavoriteFragment newInstance() {
        FavoriteFragment frag = new FavoriteFragment();

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO - When moving away from this screen (by paging to the history
        // tab), the items disappear.
        setupAdapter();
        initLoader();
    }

    private void initLoader() {
        Utils.setViewVisibility(mLoadingLayout, true);
        getLoaderManager().initLoader(AppData.Loaders.ID_FAVORITES, null, this);
    }


    private void setupAdapter() {
        mAdapter = new FavoriteAdapter(mActivity, null, 0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            Favorite favorite = cupboard().withCursor(cursor).get(Favorite.class);
            String venueId = favorite.getVenueId();
            Utils.launchDetailActivity(mActivity, venueId, favorite.getName(), view);
        }
    }


    public boolean isFavorited(Nameable venue) {
        return isInFavorite(venue.getVenueId());
    }

    private boolean isInFavorite(String id) {
        long startTime = System.currentTimeMillis();
        boolean isIn = false;
        for (Nameable fav : mFavorites) {
            if (TextUtils.equals(fav.getVenueId(), id)) {
                isIn = true;
                break;
            }
        }
        long dif = System.currentTimeMillis() - startTime;
        Log.d(TAG, "Time using looping: " + dif);
        return isIn;
    }

    private void notifyDataSetChanged() {
        if (mLoadingLayout != null && mLoadingLayout.getVisibility() == View.VISIBLE) {
            Utils.setViewVisibility(mLoadingLayout, false);
        }
        // TODO - Show an empty view if necessary
        if (mFavorites == null || mFavorites.size() == 0) {
            mTextEmpty.setText(R.string.fav_empty);
            Utils.setViewVisibility(mTextEmpty, true);
        } else {
            Utils.setViewVisibility(mTextEmpty, false);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case AppData.Loaders.ID_FAVORITES:
                return new CursorLoader(mActivity, ImHereContract.FavoriteEntry.CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case AppData.Loaders.ID_FAVORITES:
                Utils.setViewVisibility(mLoadingLayout, false);
                mAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Utils.setViewVisibility(mLoadingLayout, true);
        switch (cursorLoader.getId()) {
            case AppData.Loaders.ID_FAVORITES:
                mAdapter.swapCursor(null);
                break;
        }
    }

    class ViewHolder {
        @InjectView(R.id.list_item_venue_text)
        TextView text;
        @InjectView(R.id.list_item_venue_fav_indicator)
        View indicator;
        @InjectView(R.id.list_item_venue_add)
        ImageView add;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private class FavoriteAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        public FavoriteAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mInflater.inflate(R.layout.list_item_venue, parent, false);
            ViewHolder holder = new ViewHolder(v);
            v.setTag(holder);
            holder.indicator.setVisibility(View.GONE);
            holder.add.setImageResource(R.drawable.ic_action_remove);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            final Favorite favorite = cupboard().withCursor(cursor).get(Favorite.class);
            holder.text.setText(favorite.getName());
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatabaseTask(mActivity).execute(favorite);
                }
            });
        }
    }

    private class DatabaseTask extends AsyncTask<Favorite, Void, Void> {
        private Context mContext;

        private DatabaseTask(Context context) {
            this.mContext = context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(Favorite... params) {
            if (params == null || params.length == 0 || isCancelled()) {
                return null;
            }
            Favorite fav = params[0];
            DatabaseQueries.deleteFavorite(mContext, fav);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isAdded() || isCancelled()) {
                return;
            }
            if (mAdapter == null) {
                mAdapter = new FavoriteAdapter(mActivity, null, 0);
            }
            BusProvider.post(new FavoriteDeletedEvent(true));
        }
    }
}
