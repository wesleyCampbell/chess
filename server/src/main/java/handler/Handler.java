package handler;

import util.Debugger;
import java.util.Map;
import java.lang.reflect.Field;

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
	protected static final int HTTP_CODE_ERROR = 400;
	protected static final int HTTP_CODE_UNAUTH = 401;
	protected static final int HTTP_CODE_TAKEN = 403;
	protected static final int HTTP_CODE_NO_EXIST = 404;

	//
	// ======================= HTTP MSG RESULT TOKENS ======================
	//
	
	protected static final String MSG_REPLY_TOKEN = "message";
	protected static final String AUTH_REPLY_TOKEN = "authorization";
	protected static final String ERROR_REPLY_TOKEN = "error";
	
	//
	// ====================== DEFAULT HTTP MESSAGES ===========================
	//

	protected final String successHTTPMsg = toJson(Map.of());
	protected final String errorHTTPMsg = toJson(Map.of(MSG_REPLY_TOKEN, "Unknown Error"));
	protected final String unauthorizedHTTPMsg = toJson(Map.of(MSG_REPLY_TOKEN, "Error: unauthorized"));
	protected final String takenHTTPMsg = toJson(Map.of(MSG_REPLY_TOKEN, "Error: Already taken"));
	protected final String noExistHTTPMsg = toJson(Map.of(MSG_REPLY_TOKEN, "Error: Requested resource doesn't exist"));
	
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

	/**
	 * Extracts a json request into a given class template.
	 * Will return a new instance of the class if the request is correctly
	 * formated, or null, otherwise
	 *
	 * @param jsonBody The json request
	 * @param requestClass The request type to return
	 *
	 * @return true if valid, false otherwise
	 */
	protected <Type> Type extractJsonRequest(String jsonBody, Class<Type> requestClass) {
		Type request = fromJson(jsonBody, requestClass);
		Debugger.debug(String.format("request: %s", request), 2);

		// Iterate through each public attribute and make sure it is not null
		Field[] attributes = requestClass.getDeclaredFields();
		for (Field field : attributes) {
			field.setAccessible(true);
			Debugger.debug(String.format("attr: %s", field), 3);
            try {
                Object value = field.get(request); 
				// Verifies that the attribute is not null
                if (value == null) {
                    System.out.println("Field " + field.getName() + " is null.");
                    return null; 
                }
            } catch (IllegalAccessException e) {
                // This shouldn't happen for public fields
                return null;
            }
        }

		// all is good, return the request
		return request;
	}
}
