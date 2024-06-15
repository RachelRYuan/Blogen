package com.blogen.bootstrap;

import com.blogen.domain.*;
import com.blogen.repositories.*;
import com.blogen.api.v1.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Bootstrap the blogen embedded JPA database with data
 *
 * @author Cliff
 */
@Slf4j
@Component
public class Bootstrapper implements ApplicationListener<ContextRefreshedEvent> {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final PostRepository postRepository;
    private final RoleRepository roleRepository;
    private final AvatarRepository avatarRepository;

    private static final String IMG_SERVICE = "https://picsum.photos/300/200";

    @Autowired
    public Bootstrapper(CategoryRepository categoryRepository, PostRepository postRepository,
                        RoleRepository roleRepository, AvatarRepository avatarRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.roleRepository = roleRepository;
        this.avatarRepository = avatarRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Note: using script-based creation for data instead of this bootstrap class, but leaving
        // the class here in case it is needed in the future.

        initData();
        log.info("Finished bootstrapping data");
    }

    private void initData() {
        createRoles();
        createCategories();
        List<Avatar> avatars = createAvatars();
        createUsers(avatars);
        createPosts();
    }

    private void createRoles() {
        saveRole("ROLE_USER");
        saveRole("ROLE_ADMIN");
    }

    private void saveRole(String roleName) {
        Role role = new Role();
        role.setRole(roleName);
        roleRepository.save(role);
        log.info("Saved role: {}", roleName);
    }

    private void createCategories() {
        List<String> categories = Arrays.asList("Business", "Web Development", "Tech Gadgets", "Health & Fitness");
        categories.forEach(this::saveCategory);
    }

    private void saveCategory(String categoryName) {
        Category category = CategoryBuilder.build(categoryName);
        categoryRepository.save(category);
        log.info("Saved category: {}", categoryName);
    }

    private List<Avatar> createAvatars() {
        List<String> avatarFiles = Arrays.asList(
                "avatar0.jpg", "avatar1.jpg", "avatar2.jpg", "avatar3.jpg", "avatar4.jpg",
                "avatar5.jpg", "avatar6.jpg", "avatar7.jpg"
        );
        return avatarFiles.stream().map(this::saveAvatar).toList();
    }

    private Avatar saveAvatar(String fileName) {
        Avatar avatar = Avatar.builder().fileName(fileName).build();
        avatarRepository.save(avatar);
        log.info("Saved avatar: {}", fileName);
        return avatar;
    }

    private void createUsers(List<Avatar> avatars) {
        Role adminRole = roleRepository.findByRole("ROLE_ADMIN");
        Role userRole = roleRepository.findByRole("ROLE_USER");

        saveUser("admin", "Carl", "Sagan", "admin@blogen.org", "adminpassword", avatars.get(2), adminRole, userRole);
        saveUser("johndoe", "John", "Doe", "jdoe@gmail.com", "password", avatars.get(3), userRole);
        saveUser("mgill", "Maggie", "McGill", "gilly@yahoo.com", "password", avatars.get(1), userRole);
        saveUser("scotsman", "William", "Wallace", "scotty@hotmail.com", "password", avatars.get(4), userRole);
        saveUser("lizreed", "Elizabeth", "Reed", "liz@gmail.com", "password", avatars.get(5), userRole);
    }

    private void saveUser(String username, String firstName, String lastName, String email, String password, Avatar avatar, Role... roles) {
        UserPrefs userPrefs = UserPrefs.builder().avatar(avatar).build();
        UserBuilder ub = new UserBuilder(username, firstName, lastName, email, password, userPrefs);
        User user = ub.build();
        for (Role role : roles) {
            user.addRole(role);
        }
        userService.saveUser(user);
        log.info("Saved user: {} {}", firstName, lastName);
    }

    private void createPosts() {
        // Add your logic here to create posts
        // Example:
        // createPost("johndoe", "Tech Gadgets", "Love this tech", "Smart-phones are the greatest invention...", LocalDateTime.of(2017, 1, 1, 10, 11, 12));
    }

    private void createPost(String username, String category, String title, String body, LocalDateTime dateTime) {
        User user = userService.findByUsername(username);
        Category cat = categoryRepository.findByName(category);
        PostBuilder pb = new PostBuilder(user, cat, sequentialImageUrl(), title, body, dateTime);
        Post post = pb.build();
        postRepository.save(post);
        log.info("Saved post: {}", title);
    }

    private static int imageStart = 1058;

    private static String sequentialImageUrl() {
        return IMG_SERVICE + "/?image=" + imageStart++;
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}