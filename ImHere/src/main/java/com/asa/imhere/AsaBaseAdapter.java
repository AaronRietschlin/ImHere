package com.asa.imhere;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * A {@link android.widget.BaseAdapter} that takes care of a few things manually (such as
 * getItem, getItemId, etc).
 */
public abstract class AsaBaseAdapter<T> extends BaseAdapter {

	public final static String TAG = "AsaBaseAdapter";

	protected List<T> items;
	protected LayoutInflater mInflater;
	protected Context mContext;

	public AsaBaseAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return items == null ? null : items.get(position);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	/**
	 * Removes an item from the list that is binded to this adapter. Can
	 * automatically notify if you pass in {@code true}.
	 * 
	 * @param position
	 * @param notify
	 * @return
	 * @see #removeItem(int)
	 */
	public boolean removeItem(int position, boolean notify) {
		Object o = items.remove(position);
		if (o == null) {
			return false;
		}
		if (notify) {
			notifyDataSetChanged();
		}
		return true;
	}

	/**
	 * Removes an item from the list that is binded to this adpater. To show on
	 * the device, you must call {@link #notifyDataSetChanged()}.
	 * 
	 * @param position
	 * @return
	 * @see #removeItem(int, boolean)
	 */
	public boolean removeItem(int position) {
		return removeItem(position, false);
	}

	/**
	 * Adds an item to the list that is binded to this adapter. To show the
	 * updated list, pass {@code true} in as the notify param.
	 * 
	 * @param item
	 * @param notify
	 * @see #addItem(Object)
	 */
	public void addItem(T item, boolean notify) {
		if (items == null) {
			items = new ArrayList<T>();
			return;
		}
		items.add(item);
		if (notify) {
			notifyDataSetChanged();
		}
	}

	/**
	 * Adds an item to the list that is binded to this adapter. To show the
	 * updated list, you must call {@link #notifyDataSetChanged()} or use
	 * {@link #addItem(Object, boolean)}.
	 * 
	 * @param item
	 * @see #addItem(Object, boolean)
	 */
	public void addItem(T item) {
		addItem(item, false);
	}

	/**
	 * Adds the entire {@lint List} that is passed in to the data set that this
	 * adapter is observing. If it is null, it will instantiate and then add the
	 * items.
	 * 
	 * @param itemsToAdd
	 *            The items to add to the list.
	 * @param replace
	 *            If {@code true}, it will clear out the current list of items
	 *            (if any).
	 * @param notify
	 *            If {@code true}, it will call {@link #notifyDataSetChanged()}
	 *            for you.
	 */
	public void addAll(List<T> itemsToAdd, boolean replace, boolean notify) {
		// Protect against NullPointer
		if (items == null) {
			items = new ArrayList<T>();
		} else {
			// Replace teh current items, if any.
			if (replace && items.size() > 0) {
				items.clear();
			}
		}
		if (itemsToAdd == null) {
			Log.d(TAG, "Attempting to add null items to AsaBaseAdapter.");
			return;
		}
		items.addAll(itemsToAdd);
		if (notify) {
			notifyDataSetChanged();
		}
	}

	public void add(int to, T item) {
		items.add(to, item);
	}

	public T remove(int from) {
		return items.remove(from);
	}
	
	public void clear(){
		clear(false);
	}
	
	public void clear(boolean notify){
		if(items == null){
			return;
		}
		items.clear();
		if(notify){
			notifyDataSetChanged();
		}
	}

	public abstract View getView(int position, View convertView, ViewGroup parent);

}
