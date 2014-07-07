package com.asa.imhere.model.responses;

import com.asa.imhere.lib.foursquare.FsVenue;
import com.google.gson.annotations.SerializedName;

public class VenueResponse extends BaseResponseItem {

	@SerializedName("response")
	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public static class Response {
		@SerializedName("venue")
		private FsVenue venue;

		public FsVenue getVenue() {
			return venue;
		}

		public void setVenue(FsVenue venue) {
			this.venue = venue;
		}
	}

}
