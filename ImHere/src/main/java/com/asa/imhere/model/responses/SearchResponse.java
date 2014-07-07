package com.asa.imhere.model.responses;

import com.asa.imhere.lib.foursquare.FsVenue;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse extends BaseResponseItem {

	@SerializedName("response")
	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public static class Response {
		@SerializedName("venues")
		private List<FsVenue> venues;

		public List<FsVenue> getVenues() {
			return venues;
		}

		public void setVenues(List<FsVenue> venues) {
			this.venues = venues;
		}
	}

}
