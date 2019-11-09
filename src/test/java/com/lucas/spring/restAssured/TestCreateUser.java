package com.lucas.spring.restAssured;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

class TestCreateUser {

	private final String CONTEXT_PATH = "/web_service";

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8888;
	}

	@Test
	void testCreateUser() {

		List<Map<String, Object>> userAddresses = new ArrayList<>();

		Map<String, Object> shippingAddress = new HashMap<>();
		shippingAddress.put("city", "City without a name");
		shippingAddress.put("country", "Brazil");
		shippingAddress.put("streetName", "98, Oak Avenue");
		shippingAddress.put("postalCode", "910212-101");
		shippingAddress.put("type", "shipping");

		userAddresses.add(shippingAddress);

		Map<String, Object> billingAddress = new HashMap<>();
		billingAddress.put("city", "City without a name");
		billingAddress.put("country", "Brazil");
		billingAddress.put("streetName", "98, Oak Avenue");
		billingAddress.put("postalCode", "910212-101");
		billingAddress.put("type", "shipping");

		userAddresses.add(billingAddress);

		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "Test");
		userDetails.put("lastName", "User");
		userDetails.put("email", "yopop15831@net3mail.com");
		userDetails.put("password", "123456");
		userDetails.put("addresses", userAddresses);

		Response response = given().contentType("application/json").accept("application/json").body(userDetails).when()
				.post(CONTEXT_PATH + "/users").then().statusCode(200).contentType("application/json").extract()
				.response();

		String userId = response.jsonPath().getString("userId");
		assertNotNull(userId);

		assertTrue(userId.length() == 15);

		String bodyString = response.body().asString();

		try {
			JSONObject responseJson = new JSONObject(bodyString);
			JSONArray addresses = responseJson.getJSONArray("addresses");

			assertNotNull(addresses);
			assertTrue(addresses.length() == 2);

			String addressId = addresses.getJSONObject(0).getString("addressId");
			assertNotNull(addressId);
			assertTrue(addressId.length() == 10);

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
