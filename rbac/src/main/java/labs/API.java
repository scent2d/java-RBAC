package labs;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.casbin.jcasbin.main.Enforcer;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;

import com.opencsv.CSVWriter;
import java.io.FileWriter; 

import org.springframework.core.env.Environment;

@Controller
@RequestMapping(path="/api") 
public class API {

    @Autowired 
    private UserService service; 

    @Autowired 
    private OrganizationService orgService;

    @Autowired
    private Environment env; 

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping(path="/register") 
    public @ResponseBody User addNewUser (@RequestBody User user) {        
        ClassPathResource policyCSV = new ClassPathResource("policy.csv");       
        try {            
            User newUser = new User();
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser.setIsAdmin(user.getIsAdmin());
            service.save(newUser); 
            FileWriter outputfile = new FileWriter(policyCSV.getPath(), true);                
            CSVWriter writer = new CSVWriter(outputfile);                        
            if (newUser.getIsAdmin()==true){                
                String[] data1 = { "g", newUser.getEmail(), "admin" };                              
                writer.writeNext(data1);
                writer.close();                
            } 
            return newUser;
        }
        catch (Exception e) {            
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error!");                  
        }
    }

    @PostMapping(path="/login") 
    public @ResponseBody Map<String, String> login (@RequestBody User user) {
        User existingUser = service.findByEmail(user.getEmail());        
        if (existingUser != null) {            
            boolean isPasswordMatch = passwordEncoder.matches(user.getPassword(), existingUser.getPassword());            
            if (isPasswordMatch == true) {
                try {
                    Algorithm algorithm = Algorithm.HMAC256(env.getProperty("JWT_SECRET"));
                    Map<String, Object> claims = new HashMap();
                    claims.put("isAdmin", existingUser.getIsAdmin());
                    claims.put("email", existingUser.getEmail());
                    String token = JWT.create()
                        .withPayload(claims)
                        .sign(algorithm);
                    Map<String, String> tokenMap = new HashMap();
                    tokenMap.put("token",token); 
                    return  tokenMap;                
                } catch (JWTCreationException exception){
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");      
                }                
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");      
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");      
    }    

    @GetMapping(path="/organizations/list")
    public @ResponseBody Iterable<Organization> getAllOrganizations(@RequestHeader("Authorization") String token) {        
        ClassPathResource modelConf = new ClassPathResource("model.conf");
        ClassPathResource policyCSV = new ClassPathResource("policy.csv");        
        Enforcer enforcer = new Enforcer(modelConf.getPath(), policyCSV.getPath());        
        try {
            Algorithm algorithm = Algorithm.HMAC256(env.getProperty("JWT_SECRET"));
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            String sub = claims.get("email").asString();
            if (sub != null){            
                User existingUser = service.findByEmail(sub); 
                if (existingUser != null){                                
                    String obj = "organizations"; 
                    String act = "read";         
                    if (enforcer.enforce(sub, obj, act) == true) {            
                        return orgService.listAll();
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden!");      
                    }
                }
            }  
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden!");      
            
        } catch (JWTDecodeException exception){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden!");      
        }
    }
}