package com.clearblade.platform.api;

import com.clearblade.platform.api.internal.PlatformCallback;
import com.clearblade.platform.api.internal.RequestEngine;
import com.clearblade.platform.api.internal.RequestProperties;
import com.clearblade.platform.api.internal.UserTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class User {
	
	private static String email;				//users email address
	private static String authToken;			//auth token for user
	
	private RequestEngine request;			//used to make api requests
	
	
	public User(String e){
		if(e == null){
			email = "anonymous";
		}else{
			email = e;
		}
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setAuthToken(String aT){
		authToken = aT;
	}
	
	public String getAuthToken(){
		return authToken;
	}
	
	public void authWithCurrentUser(String password, final InitCallback callback){
		//get auth token with current user
		request = new RequestEngine();
		
		JsonObject payload = new JsonObject();
		payload.addProperty("email", this.getEmail());
		payload.addProperty("password", password);
		
		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/user/auth").body(payload).build();
		request.setHeaders(headers);
		
		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
			@Override
			public void done(String response){
				authToken = getUserTokenFromJSON(response);
				callback.done(true);
			}
			@Override
			public void error(ClearBladeException exception){
				ClearBlade.setInitError(true);
				callback.error(exception);
			}
		});
		
		asyncFetch.execute(request);
		

	}

	public void registerUser(final String password, final InitCallback callback) {
		request = new RequestEngine();
		
		JsonObject payload = new JsonObject();
		payload.addProperty("email", this.getEmail());
		payload.addProperty("password", password);
		
		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/user/reg").body(payload).build();
		request.setHeaders(headers);
		
		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
			@Override
			public void done(String response){
				//user reg successful, now auth as them
				authWithCurrentUser(password, callback);
			}
			@Override
			public void error(ClearBladeException exception){
				ClearBlade.setInitError(true);
				if(exception.getMessage().toLowerCase().contains("bad request")){
					callback.error(new ClearBladeException("Unable to register user, email taken"));
				}else{
					callback.error(exception);
				}
			}
		});
		
		asyncFetch.execute(request);
		
	}

	public void authWithAnonUser(final InitCallback callback) {
		request = new RequestEngine();

		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/user/anon").build();
		request.setHeaders(headers);

		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
			@Override
			public void done(String response){
				authToken = getUserTokenFromJSON(response);
				callback.done(true);
			}
			@Override
			public void error(ClearBladeException exception){
				ClearBlade.setInitError(true);
				callback.error(exception);
			}
		});
		
		asyncFetch.execute(request);
		
	}
	
	public void checkUserAuth(final InitCallback callback){
		request = new RequestEngine();

		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/user/check").build();
		request.setHeaders(headers);

		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
			@Override
			public void done(String response){
				callback.done(true);
			}
			@Override
			public void error(ClearBladeException exception){
				ClearBlade.setInitError(true);
				callback.error(exception);
			}
		});

		asyncFetch.execute(request);
	}

	public void logout(final InitCallback callback){

		request = new RequestEngine();

		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/user/logout").build();
		request.setHeaders(headers);

		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
			@Override
			public void done(String response){
				authToken = null;
				callback.done(true);
			}
			@Override
			public void error(ClearBladeException exception){
				ClearBlade.setInitError(true);
				callback.error(exception);
			}
		});
		
		asyncFetch.execute(request);

	}
	
	private String getUserTokenFromJSON(String json){
		String authToken = null;
		
		JsonParser parser = new JsonParser();
		JsonObject o = (JsonObject)parser.parse(json);
		
		authToken = o.get("user_token").getAsString();
		
		return authToken;
	}
}