package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Mappers.UserMapper;
import com.example.socialmediaapp.Models.Follow;
import com.example.socialmediaapp.Models.NewLocationToken;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Models.UserLocation;
import com.example.socialmediaapp.Repository.FollowRepository;
import com.example.socialmediaapp.Repository.NewLocationTokenRepository;
import com.example.socialmediaapp.Repository.UserLocationRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.UserAddRequest;
import com.example.socialmediaapp.Responses.UserFollowingResponse;
import com.example.socialmediaapp.Responses.UserResponse;

import com.maxmind.geoip2.DatabaseReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;


import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private  UserMapper userMapper;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  FollowRepository followRepository;
    @Autowired
    private UserLocationRepository userLocationRepository;


    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    @Qualifier("GeoIPCountry")
    private DatabaseReader databaseReader;


    @Autowired
    private NewLocationTokenRepository newLocationTokenRepository;

    @Autowired
    private Environment env;

    public List<UserResponse> getAll(){

        return userMapper.usersToResponses(userRepository.findAll());
    }
    public UserResponse getResponseById(int id){
        User user = userRepository.findById(id).orElse(null);
        return userMapper.userToResponse(user);
    }
    public UserResponse getByEmail(String email){
        User user = userRepository.findByEmail(email);
        return userMapper.userToResponse(user);
    }

    public List<UserFollowingResponse> getUserFollowing(int userId){
        return userMapper.followsToFollowingResponses(followRepository.findAllByUser_Id(userId));
    }

    public boolean isFollowing(int userId,int followingId){
        Optional<Follow> follow = followRepository.findByUser_IdAndFollowing_Id(userId,followingId);
        return follow.isPresent();
    }

    public User getById(int id){
        return userRepository.findById(id).get();
    }
    public void add(UserAddRequest userAddRequest){
        User user = userMapper.requestToUser(userAddRequest);
        userRepository.save(user);
    }

    public void delete(int id){
        userRepository.deleteById(id);
    }
    public List<UserResponse> searchUsers(String query) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(query);
        return userMapper.usersToResponses(users);
    }

    public NewLocationToken isNewLoginLocation(String username, String ip) {

        if(!isGeoIpLibEnabled()) {
            return null;
        }

        try {
            final InetAddress ipAddress = InetAddress.getByName(ip);
            final String country = databaseReader.country(ipAddress)
                    .getCountry()
                    .getName();

            final User user = userRepository.findByEmail(username);
            final UserLocation loc = userLocationRepository.findByCountryAndUser(country, user);
            if ((loc == null) || !loc.isEnabled()) {
                return createNewLocationToken(country, user);
            }
        } catch (final Exception e) {
            return null;
        }
        return null;
    }

    public String isValidNewLocationToken(String token) {
        final NewLocationToken locToken = newLocationTokenRepository.findByToken(token);
        if (locToken == null) {
            return null;
        }
        UserLocation userLoc = locToken.getUserLocation();
        userLoc.setEnabled(true);
        userLoc = userLocationRepository.save(userLoc);
        newLocationTokenRepository.delete(locToken);
        return userLoc.getCountry();
    }

    public void addUserLocation(User user, String ip) {

        if(!isGeoIpLibEnabled()) {
            return;
        }

        try {
            // Check if the IP address is localhost (IPv4 or IPv6)
            InetAddress ipAddress = InetAddress.getByName(ip);
            final String country = databaseReader.country(ipAddress)
                    .getCountry()
                    .getName();
            UserLocation loc = new UserLocation(country, user);
            loc.setEnabled(true);
            userLocationRepository.save(loc);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isGeoIpLibEnabled() {
        return Boolean.parseBoolean(env.getProperty("geo.ip.lib.enabled"));
    }

    private NewLocationToken createNewLocationToken(String country, User user) {
        UserLocation loc = new UserLocation(country, user);
        loc = userLocationRepository.save(loc);

        final NewLocationToken token = new NewLocationToken(UUID.randomUUID()
                .toString(), loc);
        return newLocationTokenRepository.save(token);
    }




}
