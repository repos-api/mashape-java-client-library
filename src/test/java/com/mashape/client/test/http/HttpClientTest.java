/*
 * 
 * Mashape Java Client library.
 * Copyright (C) 2011 Mashape, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * The author of this software is Mashape, Inc.
 * For any question or feedback please contact us at: support@mashape.com
 * 
 */

package com.mashape.client.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.mashape.client.exceptions.MashapeClientException;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.callback.MashapeCallback;

public class HttpClientTest {

	@Test
	public void testDoRequest() throws MashapeClientException, JSONException, InterruptedException {
		try {
			HttpClient.doRequest(HttpMethod.DELETE, "http://www.ciao.com", null, null);
			fail();
		} catch (MashapeClientException e) {
			// OK
		}
		
		try {
			HttpClient.doRequest(HttpMethod.GET, "http://www.google.com", null, null);
			fail();
		} catch (MashapeClientException e) {
			// OK
		}
		
		JSONObject response = (JSONObject) HttpClient.doRequest(HttpMethod.POST, "https://api.mashape.com/requestToken", null, null);
		assertNotNull(response);
		assertEquals(2001, response.getJSONArray("errors").getJSONObject(0).getInt("code"));
		
		List<Thread> threads = new ArrayList<Thread>();
			
		threads.add(HttpClient.doRequest(HttpMethod.GET, "https://api.mashape.com/requestToken", null, null, new MashapeCallback() {
			
			public void requestCompleted(Object response) {
				assertNotNull(response);
				try {
					assertEquals(2001, ((JSONObject)response).getJSONArray("errors").getJSONObject(0).getInt("code"));
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			}
			
			public void errorOccurred(MashapeClientException exception) {
				fail();
			}
		}));
		
		threads.add(HttpClient.doRequest(HttpMethod.POST, "http://127.0.0.1/php/api.php", null, null, new MashapeCallback() {
			
			public void requestCompleted(Object response) {
				assertNotNull(response);
			}
			
			public void errorOccurred(MashapeClientException exception) {
				fail();
			}
		}));
		
		for (Thread t : threads) {
			t.join();
		}
		
	}
	
}
