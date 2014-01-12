package com.asa.imhere.model;

import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

public class DatabaseQueries {

	public static List<Favorite> getListOfFavorites() {
		return new Select().from(Favorite.class).execute();
	}

	public static void deleteFavoriteByRemoteId(String remoteId) {
		new Delete().from(Favorite.class).where(Favorite.Columns.REMOTE_ID + "=?", remoteId).execute();
	}

	public static boolean isFavorited(String remoteId) {
		List<Favorite> favorites = new Select().from(Favorite.class).where(Favorite.Columns.REMOTE_ID + "=?", remoteId).execute();
		return favorites != null && favorites.size() > 0;
	}

	public static Favorite getFavoriteByVenueId(String venueId) {
		return new Select().from(Favorite.class).where(Favorite.Columns.REMOTE_ID + "=?", venueId).executeSingle();
	}

}
