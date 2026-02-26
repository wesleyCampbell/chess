package handler;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class Handler {

	//
	// ====================== HELPER CLASSES ===========================
	//

	protected record HTTPCodeRequest(int code, String msg) {};
	
	private final Gson gson = new Gson();
	
	//
	// ======================= HTTP HEADER CODES =====================
	//
	
	protected static final String HTTP_HEADER_AUTH = "Authorization";
	
	//
	// ====================== HTTP CODE DEFINITIONS ===========================
	//

	protected static final int HTTP_CODE_OK = 200;
	protected static final int HTTP_CODE_UNAUTH = 401;
	protected static final int HTTP_CODE_TAKEN = 403;

	//
	// ======================= HTTP MSG RESULT TOKENS ======================
	//
	
	protected static final String MSG_REPLY_TOKEN = "msg";
	protected static final String AUTH_REPLY_TOKEN = "authorization";
	
	//
	// ====================== DEFAULT HTTP MESSAGES ===========================
	//

	protected final String successHTTPMsg = toJson(Map.of());
	protected final String unauthorizedHTTPMsg = toJson(Map.of(MSG_REPLY_TOKEN, "Error: unauthorized"));
	protected final String takenHTTPMsg = toJson(Map.of(MSG_REPLY_TOKEN, "Already taken"));
	
	//
	// ====================== JSON FORMATING METHODS ===========================
	//
	
	/**
	 * Takes a json string and a class type and will return the deserialized object
	 *
	 * @param json The JSON string to deserialize
	 * @param objType The object type
	 *
	 * @return The deserialized object
	 */
	protected <Type> Type fromJson(String json, Class<Type> objType) {
		Type obj = this.gson.fromJson(json, objType);	

		return obj;
	}

	/**
	 * Takes an object and serializes it into a json object
	 *
	 * @param obj: The object to serialize
	 *
	 * @return The serialized object as a string
	 */
	protected String toJson(Object obj) {
		return this.gson.toJson(obj);
	}

	/**
	 * Takes an object and serializes it to a json object with a given code attached
	 *
	 * @param obj: The object to serialize
	 * @param code: The HTTP code to serialize into the json
	 *
	 * @return The serialized object and HTTP code
	 */
	protected String toJsonWithHTTPCode(Object obj, int code) {
		JsonObject jsonObj = gson.toJsonTree(obj).getAsJsonObject();
		jsonObj.addProperty("code", code);

		String json = gson.toJson(jsonObj);
		return json;
	}

	/**
	 * Takes a json string and adds in an authToken property.
	 *
	 * @param jsonBody The json string
	 * @param authToken the authToken to add
	 *
	 * @return The new Json string
	 */
	protected String addAuthTokenJsonProperty(String jsonBody, String authToken) {
		JsonObject obj = JsonParser.parseString(jsonBody).getAsJsonObject();
		obj.addProperty("authToken", authToken);

		return obj.toString();
	}
}
