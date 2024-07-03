package TranQuocToan.Java.DoAn.Service;


import TranQuocToan.Java.DoAn.Model.Users;
import TranQuocToan.Java.DoAn.Repository.IUserRepository;
import TranQuocToan.Java.DoAn.dto.ReqRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // đăng ký
    public ReqRes register(ReqRes registrationRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            Users user = new Users();
            user.setEmail(registrationRequest.getEmail());
            user.setAddress(registrationRequest.getAddress());
            user.setRole(registrationRequest.getRole());
            user.setName(registrationRequest.getName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            Users UserResult = userRepository.save(user);

            if(UserResult.getId() > 0 ){
                reqRes.setUsers((UserResult));
                reqRes.setMessage("Đăng ký thành công!");
                reqRes.setStatusCode(200);
            }


        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());


        }
        return  reqRes;
    }
            //đăng nhập

    public ReqRes login(ReqRes loginRequest) {
        ReqRes resqonse = new ReqRes();

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
                resqonse.setStatusCode(200);
                resqonse.setToken(jwt);
                resqonse.setRole(user.getRole());
                resqonse.setRefreshToken(refreshToken);
                resqonse.setExpirationTime("24h");
                resqonse.setMessage("Đang nhập thành công!");

        } catch (Exception e) {
            resqonse.setStatusCode(500);
            resqonse.setMessage(e.getMessage());
        }
        return resqonse;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRequiest) {
        ReqRes resqonse = new ReqRes();

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(refreshTokenRequiest.getEmail(), refreshTokenRequiest.getPassword()));
            var user = userRepository.findByEmail(refreshTokenRequiest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            resqonse.setStatusCode(200);
            resqonse.setToken(jwt);
            resqonse.setRefreshToken(refreshToken);
            resqonse.setExpirationTime("24h");
            resqonse.setMessage("Successfully Refreshed Token!");

        } catch (Exception e) {
            resqonse.setStatusCode(500);
            resqonse.setMessage(e.getMessage());
        }
        return resqonse;
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<Users> result = userRepository.findAll();
            if (!result.isEmpty()) {
                reqRes.setUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            Users usersById = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, Users updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                Users existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setAddress(updatedUser.getAddress());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                Users savedUser = userRepository.save(existingUser);
                reqRes.setUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes getMyInfo(String email){
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = userRepository.findByEmail(email);
            if (userOptional != null) {
                reqRes.setUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }
}
