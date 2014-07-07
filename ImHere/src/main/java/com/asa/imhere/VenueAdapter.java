package com.asa.imhere;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asa.imhere.lib.model.Nameable;
import com.asa.imhere.utils.Utils;

public class VenueAdapter extends AsaBaseAdapter<Nameable> implements OnClickListener {

	private boolean mShowAddBtn;
	private OnAddButtonClickListener mBtnClickListener;

	public interface OnAddButtonClickListener {
		abstract void onAddButtonClicked(Nameable venue);
	}

	public VenueAdapter(Context context) {
		this(context, false);
	}

	public VenueAdapter(Context context, boolean showAddBtn) {
		super(context);
		mShowAddBtn = showAddBtn;
	}

	static class ViewHolder {
		TextView text;
		View indicator;
		ImageView add;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_venue, parent, false);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.list_item_venue_text);
			holder.indicator = convertView.findViewById(R.id.list_item_venue_fav_indicator);
			holder.add = (ImageView) convertView.findViewById(R.id.list_item_venue_add);

			// Only show the button and indicator if needed
			Utils.setViewVisibility(holder.add, mShowAddBtn);
			Utils.setViewVisibility(holder.indicator, mShowAddBtn);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Nameable venue = items.get(position);
		if (venue == null) {
			throw new IllegalStateException("There was no venue in the list of Explore items.");
		}
		String name = venue.getName();
		holder.text.setText(name);

		if (mShowAddBtn) {
			holder.add.setTag(position);
            // TODO - check if is favorite
//			if (mIsFavoritedListener != null) {
//				boolean favorited = mIsFavoritedListener.isFavorited(venue);
//				Utils.setViewVisibility(holder.indicator, favorited);
//				setImageViewImage(holder.add, favorited);
//			}
			holder.add.setOnClickListener(this);
		}

		return convertView;
	}

	public void setOnAddButtonClickListener(OnAddButtonClickListener listener) {
		mBtnClickListener = listener;
	}

	private void setImageViewImage(ImageView btn, boolean favorited) {
		btn.setImageResource(favorited ? R.drawable.ic_action_remove : R.drawable.ic_action_add);
	}

	@Override
	public void onClick(View v) {
		if (mBtnClickListener != null) {
			int pos = (Integer) v.getTag();
			Nameable venue = items.get(pos);
			mBtnClickListener.onAddButtonClicked(venue);
		}
	}

	public void removeById(String venueId, boolean notify) {
		for (int i = 0; i < getCount(); i++) {
			Nameable venue = (Nameable) getItem(i);
			if (TextUtils.equals(venueId, venue.getVenueId())) {
				remove(i);
				return;
			}
		}
		if (notify) {
			notifyDataSetChanged();
		}
	}

}
