package com.brc.siso.service;

import com.brc.siso.dto.ReqRes;
import com.brc.siso.entity.OurUsers;
import com.brc.siso.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepo usersRepo;

    public ReqRes register(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();

        try{
            /**ourUsers object is instantiated and the setter  & getter methods
             * for mutating/inputting the values for the users**/
            OurUsers ourUser = new OurUsers();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            OurUsers ourUsersResult = usersRepo.save(ourUser);
            if(ourUsersResult.getId()> 0){
                resp.setOurUsers((ourUsersResult));//save mutated user data
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200); //send active status code
            }

        }catch(Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public ReqRes login(ReqRes loginRequest){
        ReqRes response = new ReqRes();
        try{
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(),user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24hrs");
            response.setMessage("Success!");
        }catch(Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**this section checks for a refresh token**/
    public ReqRes refreshToken(ReqRes refreshTokenRequest){
        ReqRes response = new ReqRes();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow(); //orElseThrow()
            if(jwtUtils.isTokenValid(refreshTokenRequest.getToken(),users)){
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }

    }


    /**this section checks if the user exists or not**/
    public ReqRes getAllUsers(){
        ReqRes reqRes = new ReqRes();
        try{
            List<OurUsers> result = usersRepo.findAll();
            if(!result.isEmpty()){
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else{
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred:" + e.getMessage());
            return reqRes;
        }

    }

    public ReqRes getUsersById(Integer id){
        ReqRes reqRes = new ReqRes();
        try{
            OurUsers usersById = usersRepo.findById(id).orElseThrow(()-> new RuntimeException("User Not Found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "'found successfully'");
        } catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Integer userId){
        ReqRes reqRes = new ReqRes();
        try{
            /**users are loaded into the list after being called by the usersRepo
             * if the user is found, they are deleted, else appropriate error codes
             * are set**/
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if(userOptional.isPresent()){
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User Deleted successfully");
            }else{
                reqRes.setStatusCode(404);
                reqRes.setMessage("Error occurred while deleting user: ");
            }
        } catch(Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, OurUsers updatedUser){
        ReqRes reqRes = new ReqRes();
        try{
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if(userOptional.isPresent()){
                /**updating user data (set f(x)) from input fields (get f(x))**/
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                //Check if password is present in the request
                if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()){
                    //Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("user updated successfully");
            }
            else{
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch(Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: "+ e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getMyInfo(String email){
        ReqRes reqRes = new ReqRes();
        try{
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if(userOptional.isPresent()){
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else{
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        }catch(Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;
    }


}
