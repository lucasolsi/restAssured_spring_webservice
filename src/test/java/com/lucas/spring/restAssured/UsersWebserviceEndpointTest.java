package com.lucas.spring.restAssured;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@TestMethodOrder(OrderAnnotation.class)
class UsersWebserviceEndpointTest {

	private final String CONTEXT_PATH = "/web_service";

	private final String EMAIL_ADDRESS = "yopop15831@net3mail.com";

	private final String PASSWORD = "123456";

	private final String JSON_CONTENT = "application/json";

	private static String authHeader;

	private static String responseUserId;

	private final String newFirstName = "Maybe an";

	private final String newLastName = "Error";

	private final String newEmail = "cskobxrr@grr.la";

	private static List<Map<String, String>> addresses;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8888;
	}

	@Test
	@Order(1)
	void testUserLogin() {
		Map<String, Object> loginUser = new HashMap<>();
		loginUser.put("email", EMAIL_ADDRESS);
		loginUser.put("password", PASSWORD);

		Response response = given().contentType(JSON_CONTENT).accept(JSON_CONTENT).body(loginUser).when()
				.post(CONTEXT_PATH + "/users/login").then().statusCode(200).extract().response();

		authHeader = response.header("Authorization");
		responseUserId = response.header("UserID");

		assertNotNull(authHeader);
		assertNotNull(responseUserId);
	}

	@Test
	@Order(2)
	void testGetUserDetails() {
		Response response = given().pathParam("id", responseUserId).header("Authorization", authHeader)
				.accept(JSON_CONTENT).when().get(CONTEXT_PATH + "/users/{id}").then().statusCode(200)
				.contentType(JSON_CONTENT).extract().response();

		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");

		addresses = response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");

		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);

		assertEquals(EMAIL_ADDRESS, userEmail);

		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 10);

	}

	// Not working
	@Test
	@Order(3)
	final void testUpdateUserDetails() {

		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", newFirstName);
		userDetails.put("lastName", newLastName);
		// userDetails.put("email", newEmail);
		// userDetails.put("password", PASSWORD);

		Response response = given().contentType(JSON_CONTENT).accept(JSON_CONTENT).header("Authorization", authHeader)
				.pathParam("id", responseUserId).body(userDetails).when().put(CONTEXT_PATH + "/users/{id}").then()
				.statusCode(200).contentType(JSON_CONTENT).extract().response();

		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
//		String email = response.jsonPath().getString("email");

		List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

		assertEquals(newFirstName, firstName);
		assertEquals(newLastName, lastName);
		// assertEquals(newEmail, email);
		assertNotNull(storedAddresses);
		assertTrue(addresses.size() == storedAddresses.size());
		assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
	}

	@Test
	@Disabled
	@Order(4)
	final void testDeleteUser() {

		Response response = given().accept(JSON_CONTENT).header("Authorization", authHeader)
				.pathParam("id", responseUserId).when().delete(CONTEXT_PATH + "/users/{id}").then().statusCode(200)
				.contentType(JSON_CONTENT).extract().response();

		String operationResult = response.jsonPath().getString("operationResult");
		assertEquals("SUCCESS", operationResult);
	}
}
