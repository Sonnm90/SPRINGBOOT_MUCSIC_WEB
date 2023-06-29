package com.example.demo.controller;

import com.example.demo.config.Message;
import com.example.demo.dto.request.*;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.*;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.jwt.JwtTokenFilter;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.security.userprincal.UserPrinciple;
import com.example.demo.service.band.IBandService;
import com.example.demo.service.category.ICategoryService;
import com.example.demo.service.playlist.IPlaylistService;
import com.example.demo.service.role.RoleServiceImpl;
import com.example.demo.service.singer.ISingerService;
import com.example.demo.service.song.ISongService;
import com.example.demo.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RequestMapping("/")
@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    JwtTokenFilter jwtTokenFilter;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private IBandService bandService;
    @Autowired
    private ISingerService singerService;
    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    private ISongService songService;
    @Autowired
    private ICategoryService categoryService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpForm signUpForm) {
        if (userService.existsByUsername(signUpForm.getUsername())) {
            return new ResponseEntity<>(new ResponMessage(Message.NO_USER), HttpStatus.OK);
        }
        if (userService.existsByEmail(signUpForm.getEmail())) {
            return new ResponseEntity<>(new ResponMessage(Message.NO_EMAIL), HttpStatus.OK);
        }
        User user = new User(signUpForm.getName(), signUpForm.getUsername(), signUpForm.getEmail(), passwordEncoder.encode(signUpForm.getPassword()));
        Set<String> strRoles = signUpForm.getRoles();
        Set<Role> roles = new HashSet<>();
        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleService.findByName(RoleName.ADMIN).orElseThrow(
                            () -> new RuntimeException("Role not found")
                    );
                    roles.add(adminRole);
                    break;
                case "pm":
                    Role pmRole = roleService.findByName(RoleName.PM).orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(pmRole);
                    break;
                default:
                    Role userRole = roleService.findByName(RoleName.USER).orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(userRole);
            }
        });
        user.setRoles(roles);
        userService.save(user);
        return new ResponseEntity<>(new ResponMessage(Message.CREATE_SUCCESS), HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SignInForm signInForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (!userService.findByUsername(signInForm.getUsername()).isPresent()) {
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND), HttpStatus.OK);
        }
        User user = userService.findByUsername(signInForm.getUsername()).get();

        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        String token = jwtProvider.createToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(token, userPrinciple.getName(), userPrinciple.getAvatar(), userPrinciple.getAuthorities()));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getInfoOfCurrentUser() {
        User user = userDetailService.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND), HttpStatus.OK);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> detailUserById(@PathVariable Long id) {
        Optional<User> user =userService.findByUserId(id);
        if (!user.isPresent()){
            return new ResponseEntity<>(new ResponMessage("user "+Message.NOT_FOUND),HttpStatus.OK);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PutMapping("/user/avatar")
    public ResponseEntity<?> changeAvatar(HttpServletRequest request, @Valid @RequestBody ChangeAvatar changeAvatar) {
        String token = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUerNameFromToken(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (changeAvatar.getAvatar() == null || changeAvatar.getAvatar().trim().equals("")) {
            return new ResponseEntity<>(new ResponMessage(Message.FAILED), HttpStatus.OK);
        } else {
            user.setAvatar(changeAvatar.getAvatar());
            userService.save(user);
            return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
        }
    }

    // API change Info of User
    @PutMapping("/user/info")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @Valid @RequestBody UpdateUser updateUser) {
        String token = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUerNameFromToken(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (updateUser.getAvatar() != null) {
            user.setAvatar(updateUser.getAvatar());
        }
        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        if (updateUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        userService.save(user);
        return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);

    }

    @PutMapping("/user/role/{id}")
    public ResponseEntity<?> changeRoleOfUser(@PathVariable Long id) {
        Optional<User> user = userService.findByUserId(id);
        Set<Role> roles = new HashSet<>();
        String role;
        if (!user.isPresent()) {
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND), HttpStatus.OK);
        } else {
            User user1 = userDetailService.getCurrentUser();
            role = userService.getUserRole(user1);
            if (role.equals("ADMIN")) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            } else {
                if (userService.getUserRole(user.get()).equals("USER")) {
                    Role pmRole = roleService.findByName(RoleName.PM).orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(pmRole);
                }
                if (userService.getUserRole(user.get()).equals("PM")) {
                    Role userRole = roleService.findByName(RoleName.USER).orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(userRole);
                }
                user.get().setRoles(roles);
                userService.save(user.get());
                return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
            }
        }
    }

    @PutMapping("/user/status/{id}")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        Optional<User> user = userService.findByUserId(id);
        String role;
        if (!user.isPresent()) {
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND), HttpStatus.OK);
        } else {
            User user1 = userDetailService.getCurrentUser();
            role = userService.getUserRole(user1);

            if (!role.equals("ADMIN")) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            if (userService.getUserRole(user.get()).equals("ADMIN")) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            user.get().setStatus(!user.get().isStatus());
            userService.save(user.get());
            return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> showResultSearchByName(@RequestParam String name) {
        List<Band> bands = bandService.findByName(name);
        List<Singer> singers = singerService.findByName(name);
        List<Playlist> playlist = playlistService.findByName(name);
        List<Song> songs = songService.findByName(name);
        List<Category> categories = categoryService.findByName(name);
        ResultSearchDTO searchDTO = new ResultSearchDTO();
        if (!bands.isEmpty()) {
            searchDTO.setBands(bands);
        }
        if (!singers.isEmpty()) {
            searchDTO.setSingers(singers);
        }
        if (!playlist.isEmpty()) {
            searchDTO.setPlaylists(playlist);
        }
        if (!songs.isEmpty()) {
            searchDTO.setSongs(songs);
        }
        if (!categories.isEmpty()) {
            searchDTO.setCategories(categories);
        }
        if (bands.isEmpty() && singers.isEmpty() && playlist.isEmpty() && songs.isEmpty() && categories.isEmpty()) {
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND), HttpStatus.OK);
        }
        return new ResponseEntity<>(searchDTO, HttpStatus.OK);

    }
}
