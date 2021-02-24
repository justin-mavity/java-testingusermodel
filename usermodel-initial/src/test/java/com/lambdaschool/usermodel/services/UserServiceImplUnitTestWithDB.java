package com.lambdaschool.usermodel.services;


import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.repository.RoleRepository;
import com.lambdaschool.usermodel.repository.UserRepository;
import junit.framework.TestCase;
import org.junit.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class UserServiceImplUnitTestWithDB {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private RoleRepository rolerepos;

    @Before
    public void setUp() throws Exception
    {
        // mocks -> fake data
        // stubs -> fake methods
        // Java -> mocks
        MockitoAnnotations.initMocks(this);

        List<User> myList = userService.findAll();

        for (User r : myList) {
            System.out.println("User id: " + r.getUserid() + " User Name: " + r.getUsername());
        }

        System.out.println();

        List<Role> myPayList = roleService.findAll();
        for (Role p : myPayList) {
            System.out.println("Role id: " + p.getRoleid() + " Role : " + p.getName());
        }
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void a_findByNameContaining()
    {
        assertEquals(1, userService.findByNameContaining("eat").size());
    }

    @Test
    public void c_findAll()
    {
        System.out.println(userService.findAll());
        assertEquals(3, userService.findAll().size());
    }

    @Test
    public void d_findUserById()
    {
        assertEquals("Test Apple", userService.findUserById(12).getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void e_findUserByIdNotFound()
    {
        assertEquals("Test Apple", userService.findUserById(10000).getUsername());
    }

    @Test
    public void f_findUserByName()
    {
        assertEquals("Test Apple", userService.findByName("Test Apple").getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void ff_findUserByNameFailed()
    {
        assertEquals("Lambda", userService.findByName("Stumps").getUsername());
    }

    @Test
    public void g_delete()
    {
        userService.delete(12);
        assertEquals(2, userService.findAll().size());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void gg_deletefailed()
    {
        userService.delete(777);
        assertEquals(2, userService.findAll().size());
    }

    @Test
    public void h_save()
    {
        // create a user to save
        String user3Name = "Shay Mavity";
        User r3 = new User(user3Name, "cupcake", "simple@gamil.com");

        Role r1 = new Role("Turtle");
        r1.setRoleid(9);

        r3.getRoles().add(new UserRoles(r3, r1));

        User getUser = userService.save(r3);
        assertNotNull(getUser);
        assertEquals(user3Name, getUser.getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void h_savePayTypeNotFound()
    {
        String user3Name = "Shay";
        User u3 = new User(user3Name, "cupcake", "simple@gamil.com");

        Role r3 = new Role("data");
        r3.setRoleid(3);

        u3.getRoles().add(new UserRoles(u3, r3));

        Mockito.when(userrepos.save(any(User.class))).thenReturn(u3);
        Mockito.when(rolerepos.findById(3L)).thenReturn(Optional.empty());

        User addRest = userService.save(u3);
        Assert.assertNotNull(addRest);
        TestCase.assertEquals(user3Name, addRest.getUsername());
    }

    @Test
    public void hh_saveput()
    {
        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(21);
        Role r1 = new Role("Unknown1");
        r1.setRoleid(2);
        Role r2 = new Role("Unknown2");
        r2.setRoleid(3);

        u2.getRoles().clear();
        u2.getRoles().add(new UserRoles(u2, r1));
        u2.getRoles().add(new UserRoles(u2, r2));

        User addUser = userService.save(u2);

        assertNotNull(addUser);
        assertEquals(user2Name, addUser.getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void hhh_saveputfailed()
    {
        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(777);
        Role r1 = new Role("user");
        r1.setRoleid(2);
        Role r2 = new Role("data");
        r2.setRoleid(3);

        u2.getRoles().add(new UserRoles(u2, r1));
        u2.getRoles().add(new UserRoles(u2, r2));

        User addUser = userService.save(u2);

        assertNotNull(addUser);
        assertEquals(user2Name, addUser.getUsername());
    }

    @Test
    public void i_update()
    {
        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(10);
        Role r2 = new Role("Unknown1");
        r2.setRoleid(2);
        Role r3 = new Role("Unknown2");
        r3.setRoleid(3);


        u2.getRoles().add(new UserRoles(u2, r2));
        u2.getRoles().add(new UserRoles(u2, r3));


        User addUser = userService.update(u2, 15);

        assertNotNull(addUser);
        assertEquals(user2Name, addUser.getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void i_updateRoleNotFound()
    {
        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(10);
        Role r2 = new Role("Unknown1");
        r2.setRoleid(2);
        Role r3 = new Role("Unknown2");
        r3.setRoleid(34);


        u2.getRoles().add(new UserRoles(u2, r2));
        u2.getRoles().add(new UserRoles(u2, r3));

        User addUser = userService.update(u2, 10);

        assertNotNull(addUser);
        assertEquals(user2Name, addUser.getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void ii_updateNotFound()
    {
        String user2Name = "Brittany Mavity";
        User u2 = new User(user2Name, "Brittany1!", "brittanymavity@gmail.com");
        u2.setUserid(10);
        Role r2 = new Role("Unknown1");
        r2.setRoleid(2);
        Role r3 = new Role("Unknown2");
        r3.setRoleid(3);


        u2.getRoles().add(new UserRoles(u2, r2));
        u2.getRoles().add(new UserRoles(u2, r3));

        u2.getRoles().add(new UserRoles(u2, r2));
        u2.getRoles().add(new UserRoles(u2, r3));


        User addUser = userService.update(u2, 777);

        assertNotNull(addUser);
        assertEquals(user2Name, addUser.getUsername());
    }
}
