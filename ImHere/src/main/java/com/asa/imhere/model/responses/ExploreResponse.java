package com.asa.imhere.model.responses;

import java.util.List;

import com.asa.imhere.foursquare.ExploreGroup;
import com.google.gson.annotations.SerializedName;

public class ExploreResponse extends BaseResponseItem {

	@SerializedName("response")
	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public static class Response {
		/**
		 * A list of words that are suggested for the user to input as
		 * refinements. Has a count with the number of elements, and items with
		 * the actual keyword objects. Each object has a displayName as well as
		 * a keyword which is meant to be fed back into the system as a query.
		 */
		@SerializedName("keywords")
		private FsKeyword keywords;
		/**
		 * If no radius was specified in the request, presents the radius that
		 * was used for the query (based upon the density of venues in the query
		 * area).
		 */
		@SerializedName("suggestedRadius")
		private int suggestedRadius;
		/**
		 * A text name for the location the user searched, e.g. "SoHo"
		 */
		@SerializedName("headerLocation")
		private String headerLocation;
		@SerializedName("headerFullLocation")
		/**
		 * A full text name for the location the user searched, e.g. "SoHo, New York".
		 */
		private String headerFullLocation;
		@SerializedName("headerLocationGranularity")
		private String headerLocationGranularity;
		/**
		 * A message to the user based on their current context, e.g.
		 * "Suggestions for Tuesday afternoon".
		 */
		@SerializedName("headerMessage")
		private String headerMessage;
		@SerializedName("totalResults")
		private int totalResults;
		@SerializedName("groups")
		// TODO - Getting error: Expected BEGIN_ARRAY got BEGIN_OBJECT line 1 of
		// column 919
		private List<ExploreGroup> groups;

		public FsKeyword getKeywords() {
			return keywords;
		}

		public void setKeywords(FsKeyword keywords) {
			this.keywords = keywords;
		}

		public int getSuggestedRadius() {
			return suggestedRadius;
		}

		public void setSuggestedRadius(int suggestedRadius) {
			this.suggestedRadius = suggestedRadius;
		}

		public String getHeaderLocation() {
			return headerLocation;
		}

		public void setHeaderLocation(String headerLocation) {
			this.headerLocation = headerLocation;
		}

		public String getHeaderFullLocation() {
			return headerFullLocation;
		}

		public void setHeaderFullLocation(String headerFullLocation) {
			this.headerFullLocation = headerFullLocation;
		}

		public String getHeaderLocationGranularity() {
			return headerLocationGranularity;
		}

		public void setHeaderLocationGranularity(String headerLocationGranularity) {
			this.headerLocationGranularity = headerLocationGranularity;
		}

		public String getHeaderMessage() {
			return headerMessage;
		}

		public void setHeaderMessage(String headerMessage) {
			this.headerMessage = headerMessage;
		}

		public int getTotalResults() {
			return totalResults;
		}

		public void setTotalResults(int totalResults) {
			this.totalResults = totalResults;
		}

		public List<ExploreGroup> getGroups() {
			return groups;
		}

		public void setGroups(List<ExploreGroup> groups) {
			this.groups = groups;
		}
	}

}
