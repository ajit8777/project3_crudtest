package com.restful.booker.crudtest;

import com.restful.booker.model.AuthorizationPojo;
import com.restful.booker.model.BookingPojo;
import com.restful.booker.testbase.TestBase;
import com.restful.booker.utils.TestUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class BookingCRUDTest extends TestBase {

    static int id;

    @Test(priority = 2)
    public String authToken() {

        AuthorizationPojo authorizationPojo = new AuthorizationPojo();
        authorizationPojo.setUsername("admin");
        authorizationPojo.setPassword("password123");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(authorizationPojo)
                .when()
                .post("/auth")
                .then().statusCode(200)
                .extract().response();
        String getToken = response.jsonPath().getString("token");
        System.out.println("Token :" + getToken);
        return getToken;

    }

    @Test(priority = 3)
    public void createBooking() {

        String firstname = TestUtils.getRandomValue() + "Tom";
        String lastname = TestUtils.getRandomValue() + "Jerry";

        HashMap<String, Object> bookingdates = new HashMap<>();
        bookingdates.put("checkin" , "2024-03-04");
        bookingdates.put("checkout" , "2024-04-04");

        BookingPojo bookingPojo = new BookingPojo();
        bookingPojo.setFirstname(firstname);
        bookingPojo.setLastname(lastname);
        bookingPojo.setTotalprice(123);
        bookingPojo.setDepositpaid(true);
        bookingPojo.setAdditionalneeds("Dinner");
        bookingPojo.setBookingdates(bookingdates);

        Response response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .body(bookingPojo)
                        .when()
                        .post("/booking")
                        .then()
                        .extract().response();

        response.prettyPrint();
        response.then().statusCode(200);
        id = response.jsonPath().getInt("bookingid");

    }

    @Test(priority = 4)
    public void updateBooking() {

        String firstname = TestUtils.getRandomValue() + "Tom";
        String lastname = TestUtils.getRandomValue() + "Jerry";

        HashMap<String, Object> bookingdates = new HashMap<>();
        bookingdates.put("checkin" , "2023-01-25");
        bookingdates.put("checkout" , "2023-05-20");

        BookingPojo bookingPojo = new BookingPojo();
        bookingPojo.setFirstname(firstname);
        bookingPojo.setLastname(lastname);
        bookingPojo.setTotalprice(456);
        bookingPojo.setDepositpaid(true);
        bookingPojo.setAdditionalneeds("Breakfast");
        bookingPojo.setBookingdates(bookingdates);

        Response response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .header("cookie", "token=" + authToken())
                        .body(bookingPojo)
                        .when()
                        .put("/booking/"+ id);
        response.then().statusCode(200);
        response.prettyPrint();
    }

    @Test(priority = 5)
    public void partialUpdateBooking() {
        String firstName = TestUtils.getRandomValue() + "Tom";
        String lastName = TestUtils.getRandomValue() + "Jerry";

        BookingPojo bookingPojo = new BookingPojo();
        bookingPojo.setFirstname(firstName);
        bookingPojo.setLastname(lastName);

        Response response =
                given().log().all()
                        .header("Content-Type", "application/json")
                        .header("cookie", "token=" + authToken())
                        .body(bookingPojo)
                        .when()
                        .patch("/booking/"+ id);
        response.then().statusCode(200);
        response.prettyPrint();
    }

    @Test(priority = 6)
    public void deleteBooking() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .header("cookie", "token=" + authToken())
                .when()
                .delete("/booking/"+ id);
        response.then().statusCode(201);
        response.prettyPrint();
    }

    @Test(priority = 7)
    public void pingCheck() {
        Response response = given().log().all()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .get("https://restful-booker.herokuapp.com/ping");
        response.prettyPrint();
        response.then().statusCode(201);
    }
}
