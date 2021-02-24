package com.lambdaschool.usermodel.controllers;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.services.RoleService;
import com.lambdaschool.usermodel.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.number.OrderingComparison.lessThan;

@RunWith (SpringRunner.class)
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = UserModelApplicationTesting.class)
@AutoConfigureMockMvc
@WithUserDetails (value = "admin")
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Before
    public void setUp() throws Exception {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        List<User> userList = userService.findAll();
        for (User u : userList) {
            System.out.println(u.getUserid() + " " + u.getUsername());
        }

        List<Role> userRole = roleService.findAll();
        for (Role r : userRole) {
            System.out.println(r.getRoleid() + " " + r.getName());

        }

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void whenMeasuredResponseTime() {
        given().when().get("/restaurants").then().time(lessThan(5000L));
    }

    @Test
    public void getUserById() {
        long aUser = 12;

        given().when().get("/user/" + aUser).then().statusCode(200).and().body(containsString("Test"));
    }

    @Test
    public void getRestaurantByIdNotFound() {
        long aUser = 7777;

        given().when().get("/user/" + aUser).then().statusCode(404).and().body(containsString("Resource"));
    }

    @Test
    public void getUserByName() {
        String aUser = "Justin Mavity";

        given().when().get("/user/name/" + aUser).then().statusCode(200).and().body(containsString("Justin Mavity"));
    }

    @Test
    public void getUserByNameNotFound()
    {
        String aUser = "Turtle";

        given().when()
            .get("/user/name/" + aUser)
            .then()
            .statusCode(404)
            .and()
            .body(containsString("Resource"));
    }

    @Test
    public void listUserNameLike()
    {
        given().when()
            .get("/user/name/like/shay")
            .then()
            .statusCode(200)
            .and()
            .body(containsString("Shay"));
    }




    @Test
    public void deleteUserById()
    {
        long aUser = 10;
        given().when()
            .delete("/user/" + aUser)
            .then()
            .statusCode(200);
    }

    @Test
    public void deleteUserByIdNotFound()
    {
        long aUser = 7777;
        given().when()
            .delete("/user/" + aUser)
            .then()
            .statusCode(404);
    }

}
