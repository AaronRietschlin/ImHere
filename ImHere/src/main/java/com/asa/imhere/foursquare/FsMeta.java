package com.asa.imhere.foursquare;

import com.google.gson.annotations.SerializedName;

/**
 * The metadata returned with each resposne. See:
 * https://developer.foursquare.com/overview/responses
 */
public class FsMeta {

	@SerializedName("code")
	private int code;
	@SerializedName("errorType")
	private String errorType;
	@SerializedName("errorDetail")
	private String errorDetail;
	@SerializedName("errorMessage")
	private String errorMessage;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static class Code {
		/** Something was null that shouldn't be. My custom check. */
		public static final int NULL = -1;
		/** The call was okay. Foursquares. */
		public static final int OK = 200;
		/**
		 * Any case where a parameter is invalid, or a required parameter is
		 * missing. This includes the case where no OAuth token is provided and
		 * the case where a resource ID is specified incorrectly in a path.
		 */
		public static final int BAD_RESPONSE = 400;
		/** The OAuth token was provided but was invalid. */
		public static final int UNAUTHORIZE = 401;
		/**
		 * The requested information cannot be viewed by the acting user, for
		 * example, because they are not friends with the user whose data they
		 * are trying to read.
		 */
		public static final int FORBIDDEN = 403;
		/** Endpoint does not exist. */
		public static final int NOT_FOUND = 404;
		/** Attempting to use POST with a GET-only endpoint, or vice-versa. */
		public static final int METHOD_NOT_ALLOWED = 405;
		/**
		 * The request could not be completed as it is. Use the information
		 * included in the response to modify the request and retry.
		 */
		public static final int CONFLICT = 409;
		/**
		 * Foursquare’s servers are unhappy. The request is probably valid but
		 * needs to be retried later.
		 */
		public static final int INTERNAL_SERVER_ERROR = 500;
	}

	public static class Type {
		/** OAuth token was not provided or was invalid. */
		public static final String INVALID_AUTH = "invalid_auth";
		/**
		 * A required parameter was missing or a parameter was malformed. This
		 * is also used if the resource ID in the path is incorrect.
		 */
		public static final String PARAM_ERROR = "param_error";
		/** The requested path does not exist. */
		public static final String ENDPOINT_ERROR = "endpoint_error";
		/**
		 * Although authentication succeeded, the acting user is not allowed to
		 * see this information due to privacy restrictions.
		 */
		public static final String NOT_AUTHORIZED = "not_authorized";
		/** Rate limit for this hour exceeded. */
		public static final String RATE_LIMIT_EXCEEDED = "rate_limit_exceeded";
		/**
		 * Something about this request is using deprecated functionality, or
		 * the response format may be about to change.
		 */
		public static final String DEPRECATED = "deprecated";
		/** Server is currently experiencing issues. */
		public static final String SERVER_ERROR = "server_error";
		/** Some other type of error occurred. */
		public static final String OTHER = "other";
	}
}
