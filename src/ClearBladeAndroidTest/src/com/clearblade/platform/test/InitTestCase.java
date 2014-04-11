package com.clearblade.platform.test;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.clearblade.platform.api.ClearBlade;
import com.clearblade.platform.api.ClearBladeException;
import com.clearblade.platform.api.InitCallback;

import junit.framework.TestCase;
import android.test.AndroidTestCase;


public class InitTestCase extends AndroidTestCase {
	
	//prod
	//private static String systemKey = "c2c895af0af087bea2e1f2a4fb0b";
	//private static String systemSecret = "C2C895AF0A98FAE0CEF2A4AF890B";
	
	//rtp
	private static String systemKey = "e6cf96b40ab4868aeba0e48e83b601";
	private static String systemSecret = "E6CF96B40AB68BA7C39A91FAB95D";
	
	
	public void testAnonymousInitWithAuthRequired() throws Throwable{
	
		//make sure auth token isn't lingering from other init tests
		if(ClearBlade.getCurrentUser() != null){
			ClearBlade.getCurrentUser().setAuthToken(null);
		}
		
		final CountDownLatch signal = new CountDownLatch(1);
		
		HashMap<String,Object> initOptions = new HashMap<String,Object>();
		
		initOptions.put("platformURL", "https://rtp.clearblade.com");
		initOptions.put("allowUntrusted", true);
		
		ClearBlade.initialize(systemKey, systemSecret, initOptions, new InitCallback(){

			@Override
			public void done(boolean results) {
				assertNotNull(ClearBlade.getCurrentUser().getAuthToken());
				signal.countDown();
			}
			@Override
			public void error(ClearBladeException e){
				fail(e.getMessage());
				signal.countDown();
			}
			
		});
		
		signal.await();

	}
	
	
	public void testUserInitWithAuthRequired() throws Throwable{
		
		if(ClearBlade.getCurrentUser() != null){
			ClearBlade.getCurrentUser().setAuthToken(null);
		}
		
		final CountDownLatch signal = new CountDownLatch(1);
		
		HashMap<String,Object> initOptions = new HashMap<String,Object>();
		
		initOptions.put("email", "android@test.com");
		initOptions.put("password", "android_test");
		initOptions.put("platformURL", "https://rtp.clearblade.com");
		initOptions.put("allowUntrusted", true);
		
		ClearBlade.initialize(systemKey, systemSecret, initOptions, new InitCallback(){

			@Override
			public void done(boolean results) {
				assertNotNull(ClearBlade.getCurrentUser().getAuthToken());
				signal.countDown();
			}
			@Override
			public void error(ClearBladeException e){
				fail(e.getMessage());
				signal.countDown();
			}
			
		});
		
		signal.await();

	}
	
	public void testRegisterInitWithRegUserTrue() throws Throwable{
		
		//make sure auth token isn't lingering from other init tests
		if(ClearBlade.getCurrentUser() != null){
			ClearBlade.getCurrentUser().setAuthToken(null);
		}		
		
		final CountDownLatch signal = new CountDownLatch(1);
		
		HashMap<String,Object> initOptions = new HashMap<String,Object>();
		
		Random rand = new Random();
		Integer randomNum = rand.nextInt(100000);
		
		String email = "test_" + randomNum.toString() + "@test.com";
		
		initOptions.put("email", email);
		initOptions.put("password", "android_test");
		initOptions.put("registerUser", true);
		initOptions.put("platformURL", "https://rtp.clearblade.com");
		initOptions.put("allowUntrusted", true);
		
		ClearBlade.initialize(systemKey, systemSecret, initOptions, new InitCallback(){

			@Override
			public void done(boolean results) {
				assertNotNull(ClearBlade.getCurrentUser().getAuthToken());
				signal.countDown();
			}
			@Override
			public void error(ClearBladeException e){
				fail(e.getMessage());
				signal.countDown();
			}
			
		});
		
		signal.await();
	}
	
	public void testInitWithUnknownUser() throws Throwable{
		
		//make sure auth token isn't lingering from other init tests
		if(ClearBlade.getCurrentUser() != null){
			ClearBlade.getCurrentUser().setAuthToken(null);
		}
		
		final CountDownLatch signal = new CountDownLatch(1);
		
		HashMap<String,Object> initOptions = new HashMap<String,Object>();
		
		Random rand = new Random();
		Integer randomNum = rand.nextInt(100000);
		
		String email = "unreg_user_" + randomNum.toString() + "@test.com";
		
		System.out.println();
		
		initOptions.put("email", email);
		initOptions.put("password", "android_test");
		initOptions.put("registerUser", false);
		initOptions.put("platformURL", "https://rtp.clearblade.com");
		initOptions.put("allowUntrusted", true);
		
		ClearBlade.initialize(systemKey, systemSecret, initOptions, new InitCallback(){

			@Override
			public void done(boolean results) {
				fail("Able to authenticate as an unregistered user");
				signal.countDown();
			}
			@Override
			public void error(ClearBladeException e){
				assertNull(ClearBlade.getCurrentUser().getAuthToken());
				signal.countDown();
			}
			
		});
		
		signal.await();
	}
	
}
