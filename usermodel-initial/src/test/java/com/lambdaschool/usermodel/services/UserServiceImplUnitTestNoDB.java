package com.lambdaschool.usermodel.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.repository.RoleRepository;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith (SpringRunner.class)
@SpringBootTest (classes = UserModelApplicationTesting.class,
    properties = {
        "command.line.runner.enabled=false"})
@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class UserServiceImplUnitTestNoDB {

    // mocks -> fake data
    // stubs -> fake methods
    // Java -> mocks
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private RoleRepository rolerepos;

    private List<User> userList;

    @Before
    public void setUp() throws Exception
    {
        userList = new ArrayList<>();

        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        r1.setRoleid(1);
        r2.setRoleid(2);
        r3.setRoleid(3);

        // User String name, String address, String city, String state, String telephone
        String user1Name = "Justin Mavity";
        User u1 = new User(user1Name, "Brittany1", "justinmavity102186@gmail.com");
        u1.setUserid(10);

        u1.getRoles().add(new UserRoles(u1, r1));
        u1.getRoles().add(new UserRoles(u1, r2));
        u1.getRoles().add(new UserRoles(u1, r3));
        

        userList.add(u1);

        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com" );
        u2.setUserid(20);
        u2.getRoles().add(new UserRoles(u2, r2));

        userList.add(u2);

        String user3Name = "Shay Mavity";
        User u3 = new User(user3Name, "cupcake", "simple@gmail.com");
        u3.setUserid(20);
        u3.getRoles().add(new UserRoles(u3, r1));
        u3.getRoles().add(new UserRoles(u3, r3));

        userList.add(u3);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void a_findByUsername()
    {
        Mockito.when(userrepos.findByUsernameContainingIgnoreCase("Brittany")).thenReturn(userList);

        assertEquals(3, userRepository.findByUsername("Brittany"));
    }
    

    @Test
    public void c_findAll()
    {
        Mockito.when(userrepos.findAll()).thenReturn(userList);

        System.out.println(userRepository.findAll());
        assertEquals(3, userRepository.findAll());
    }

    @Test
    public void d_findUserById()
    {
        Mockito.when(userrepos.findById(1L))
            .thenReturn(Optional.of(userList.get(0)));

        assertEquals("Justin Mavity", userService.findUserById(1).getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void e_findRestaurantByIdNotFound()
    {
        Mockito.when(userrepos.findById(10000L)).thenThrow(ResourceNotFoundException.class);

        assertEquals("Justin Mavity", userService.findUserById(10000).getUsername());
    }

    @Test
    public void f_findUserByName()
    {
        Mockito.when(userrepos.findByUsername("Justin Mavity")).thenReturn(userList.get(0));

        assertEquals("Justin Mavity", userService.findByName("Justin Mavity").getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void ff_findUserByNameFailed()
    {
        Mockito.when(userrepos.findByUsername("Lambda")).thenThrow(ResourceNotFoundException.class);

        assertEquals("Lambda", userRepository.findByUsername("Stumps").getUsername());
    }

    @Test
    public void g_delete()
    {
        Mockito.when(userrepos.findById(2L)).thenReturn(Optional.of(userList.get(0)));

        Mockito.doNothing().when(userrepos).deleteById(2L);

        userService.delete(2);
        assertEquals(3, userList.size());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void gg_deletefailed()
    {
        Mockito.when(userrepos.findById(777L)).thenReturn(Optional.empty());

        Mockito.doNothing().when(userrepos).deleteById(777L);

        userService.delete(777);
        assertEquals(3, userList.size());
    }

    @Test
    public void h_save()
    {
        // create a restaurant to save
        String user3Name = "Test Mitchell's Cafe";
        User u3 = new User(user3Name, "cupcake", "simple@gmail.com");

        Role r1 = new Role("Turtle");
        r1.setRoleid(1);

        u3.getRoles().add(new UserRoles(u3, r1));

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u3);
        Mockito.when(rolerepos.findById(1L)).thenReturn(Optional.of(r1));

        User addRest = userService.save(u3);
        assertNotNull(addRest);
        assertEquals(user3Name, addRest.getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void ha_savePayTypeNotFound()
    {
        // create a restaurant to save
        String user3Name = "Jace Mavity";
        User u3 = new User(user3Name, "cupcake1", "simple1@gmail.com");

        Role r1 = new Role("DATA");
        r1.setRoleid(1);

        u3.getRoles().add(new UserRoles(u3, r1));

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u3);
        Mockito.when(rolerepos.findById(1L)).thenReturn(Optional.empty());

        User addRest = userRepository.save(u3);
        assertNotNull(addRest);
        assertEquals(user3Name, addRest.getUsername());
    }

    @Test
    public void hh_saveput()
    {
        String user2Name = "Test Cinammon's Place";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(13);
        Role pay1 = new Role("Unknown1");
        pay1.setRoleid(1);
        Role pay2 = new Role("Unknown2");
        pay2.setRoleid(2);

        u2.getRoles().clear();
        u2.getRoles().add(new UserRoles(u2, pay1));
        u2.getRoles().add(new UserRoles(u2, pay2));

        Mockito.when(userrepos.findById(13L)).thenReturn(Optional.of(u2));
        Mockito.when(rolerepos.findById(1L)).thenReturn(Optional.of(pay1));
        Mockito.when(rolerepos.findById(2L)).thenReturn(Optional.of(pay2));

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u2);

        assertEquals(13L, userService.save(u2).getUserid());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void hhh_saveputfailed()
    {
        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(777);
        Role r1 = new Role("Unknown1");
        r1.setRoleid(1);
        Role r2 = new Role("Unknown2");
        r2.setRoleid(2);

        u2.getRoles().add(new UserRoles(u2, r1));
        u2.getRoles().add(new UserRoles(u2, r2));


        Mockito.when(userrepos.findById(777L)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u2);
        Mockito.when(rolerepos.findById(1L)).thenReturn(Optional.of(r1));
        Mockito.when(rolerepos.findById(2L)).thenReturn(Optional.of(r2));

        assertEquals(777L, userService.save(u2).getUserid());
    }

    @Test
    public void i_update() throws Exception
    {
        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(10);
        Role r1 = new Role("Unknown1");
        r1.setRoleid(1);
        Role r2 = new Role("Unknown2");
        r2.setRoleid(2);

        u2.getRoles().add(new UserRoles(u2, r1));
        u2.getRoles().add(new UserRoles(u2, r2));

        // I need a copy of u2 to send to update so the original u2 is not changed.
        // I am using Jackson to make a clone of the object
        ObjectMapper objectMapper = new ObjectMapper();
        User u3 = objectMapper.readValue(objectMapper.writeValueAsString(u2), User.class);

        Mockito.when(userrepos.findById(10L)).thenReturn(Optional.of(u3));
        Mockito.when(rolerepos.findById(1L)).thenReturn(Optional.of(r1));
        Mockito.when(rolerepos.findById(2L)).thenReturn(Optional.of(r2));

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u2);

        User addUser = userService.update(u2, 10);

        assertNotNull(addUser);
        assertEquals(user2Name, addUser.getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void ii_updateNotFound()
    {
        String user2Name = "Test BarnBarn's Place";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(777);
        Role r1 = new Role("Unknown1");
        r1.setRoleid(1);
        Role r2 = new Role("Unknown2");
        r2.setRoleid(2);

        u2.getRoles().add(new UserRoles(u2, r1));
        u2.getRoles().add(new UserRoles(u2, r2));


        Mockito.when(userrepos.findById(777L)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(rolerepos.findById(1L)).thenReturn(Optional.of(r1));
        Mockito.when(rolerepos.findById(2L)).thenReturn(Optional.of(r2));

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u2);
        User addUser = userService.update(u2, 777);

        assertNotNull(addUser);
        assertEquals(user2Name, addUser.getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void iii_updatePayTypeNotFound() throws Exception
    {
        String user2Name = "Test BarnBarn's Place";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(10);
        Role r1 = new Role("Unknown1");
        r1.setRoleid(1);
        Role r2 = new Role("Unknown2");
        r2.setRoleid(2);

        u2.getRoles().add(new UserRoles(u2, r1));
        u2.getRoles().add(new UserRoles(u2, r2));

        // I need a copy of u2 to send to update so the original u2 is not changed.
        // I am using Jackson to make a clone of the object
        ObjectMapper objectMapper = new ObjectMapper();
        User u3 = objectMapper.readValue(objectMapper.writeValueAsString(u2), User.class);

        Mockito.when(userrepos.findById(10L)).thenReturn(Optional.of(u3));
        Mockito.when(rolerepos.findById(1L)).thenReturn(Optional.of(r1));
        Mockito.when(rolerepos.findById(2L)).thenReturn(Optional.empty());

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u2);
        User addRestaurant = userService.update(u2, 10);

        assertNotNull(addRestaurant);
        assertEquals(user2Name, addRestaurant.getUsername());
    }
}