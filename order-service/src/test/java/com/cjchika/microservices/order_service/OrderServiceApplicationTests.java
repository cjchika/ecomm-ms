package com.techie.microservices.order;

import com.techie.microservices.order.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

	@ServiceConnection
	static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("mysql:8.3.0");
	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		 RestAssured.baseURI = "http://localhost";
		postgreSQLContainer. = port;
	}

	static {
		postgreSQLContainer.start();
	}

	@Test
	void shouldSubmitOrder() {
		String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 1
                }
                """;
		InventoryClientStub.stubInventoryCall("iphone_15", 1);

		var responseBodyString = RestAssured.given()
				.contentType("application/json")
				.body(submitOrderJson)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201)
				.extract()
				.body().asString();

		assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));
	}

	@Test
	void shouldFailOrderWhenProductIsNotInStock() {
		String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 1000
                }
                """;
		InventoryClientStub.stubInventoryCall("iphone_15", 1000);

		RestAssured.given()
				.contentType("application/json")
				.body(submitOrderJson)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(500);
	}
}