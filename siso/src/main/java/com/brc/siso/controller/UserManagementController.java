package com.brc.siso.controller;


import com.brc.siso.dto.ReqRes;
import com.brc.siso.entity.OurUsers;
import com.brc.siso.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/** Spring 4.0 introduced the @RestController annotation in order to simplify the creation of RESTful web services. It's a convenient annotation that combines @Controller and @ResponseBody,
 * which eliminates the need to annotate every request
 * handling method of the controller class with the @ResponseBody annotation.
 * https://www.baeldung.com/spring-controller-vs-restcontroller#:~:text=Spring%204.0%20introduced%20the%20%40RestController,class%20with%20the%20%40ResponseBody%20annotation.**/

@RestController
public class UserManagementController {

    @Autowired
    private UsersManagementService usersManagementService;

    /**@PostMapping annotation in Spring MVC framework is a powerful tool for handling
     *  the HTTP POST requests in your RESTful web services. It maps specific URLs to
     *  handler methods allowing you to receive and process data submitted through POST requests.
     *  The @PostMapping annotation is a Spring annotation that is used to map HTTP POST requests
     *  onto specific handler methods. It is a shortcut for @RequestMapping annotation with
     *  method = RequestMethod.POST attribute.
     *  https://www.geeksforgeeks.org/spring-postmapping-and-getmapping-annotation/**/

    /**Simply put, the @RequestBody annotation maps the HttpRequest body to a transfer or domain object, enabling
     *  automatic deserialization of the inbound HttpRequest body onto a Java object.
     *  For deserialization, Spring Boot automatically converts JSON into Java objects
     *  when receiving HTTP requests.
     *  https://www.baeldung.com/spring-request-response-body
     *  https://www.google.com/search?q=automatic+deserialization+springboot+meaning&sca_esv=42af3b39e79317f4&sca_upv=1&rlz=1C1ONGR_enUS1110US1110&ei=CK2gZs7OLoLGp84Pmd2YqAk&ved=0ahUKEwjO8qvCk7-HAxUC48kDHZkuBpUQ4dUDCBA&uact=5&oq=automatic+deserialization+springboot+meaning&gs_lp=Egxnd3Mtd2l6LXNlcnAiLGF1dG9tYXRpYyBkZXNlcmlhbGl6YXRpb24gc3ByaW5nYm9vdCBtZWFuaW5nMgoQABiwAxjWBBhHMgoQABiwAxjWBBhHMgoQABiwAxjWBBhHMgoQABiwAxjWBBhHMgoQABiwAxjWBBhHMgoQABiwAxjWBBhHMgoQABiwAxjWBBhHMgoQABiwAxjWBBhHSPEYUNQQWJkWcAF4AZABAJgBAKABAKoBALgBA8gBAPgBAZgCAaACIJgDAOIDBRIBMSBAiAYBkAYIkgcBMaAHAA&sclient=gws-wiz-serp**/



    /**Essentially this file is a routing file that converts JSON objects into JSON requests**/
    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg){
        return ResponseEntity.ok(usersManagementService.register(reg));
    };

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.register(req));
    };

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    };

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));
    }


    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody OurUsers reqres){
        return ResponseEntity.ok(usersManagementService.updateUser(userId,reqres));
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }




}
