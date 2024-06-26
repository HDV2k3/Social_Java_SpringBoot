package com.example.socialmediaapp;

import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Models.UserLocation;
import com.example.socialmediaapp.Service.UserService;
import com.example.socialmediaapp.persistence.UserLocationRepository;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.InetAddress;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private DatabaseReader mockDatabaseReader;

    @Mock
    private UserLocationRepository mockUserLocationRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testAddUserLocation_LocalhostIPv6() {
        // Mocking the behavior for localhost IPv6 (::1)
        String localhostIPv6 = "::1";
        InetAddress mockAddress = mock(InetAddress.class);
        when(mockAddress.getHostAddress()).thenReturn(localhostIPv6);

        try {
            when(mockDatabaseReader.country(any(InetAddress.class))).thenThrow(AddressNotFoundException.class);
        } catch (AddressNotFoundException e) {
            // Handle exception mock setup
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        // Test method invocation
        User user = new User(); // Create a mock User object or use a real one for testing
        userService.addUserLocation(user, localhostIPv6);

        // Verify that save method was not called
        verify(mockUserLocationRepository, never()).save(any(UserLocation.class));
    }
}
