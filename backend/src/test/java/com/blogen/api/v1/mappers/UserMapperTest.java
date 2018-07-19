package com.blogen.api.v1.mappers;

import com.blogen.api.v1.model.UserDTO;
import com.blogen.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Unit Test mappings between {@link com.blogen.domain.User} and {@link com.blogen.api.v1.model.UserDTO}
 *
 * @author Cliff
 */
public class UserMapperTest {

    private static final Long ID = 11L;
    private static final String FIRSTNAME = "John";
    private static final String LASTNAME = "Doe";
    private static final String USERNAME = "doey156";
    private static final String EMAIL = "jdoe@cox.net";
    private static final String PASSWORD = "sEcReT";
    private static final String AVATAR_IMAGE = "avatar1.jpg";
    User user;

    private UserMapper userMapper = UserMapper.INSTANCE;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setId( ID );
        user.setFirstName( FIRSTNAME );
        user.setLastName( LASTNAME );
        user.setUserName( USERNAME );
        user.setEmail( EMAIL );
        user.setPassword( PASSWORD );
    }

    @Test
    public void userToUserDto() {
        //given a user

        //when
        UserDTO userDTO = userMapper.userToUserDto( user );

        //then
        assertThat( userDTO.getId(), equalTo( ID ) );
        assertThat( userDTO.getFirstName(), equalTo( FIRSTNAME ) );
        assertThat( userDTO.getLastName(), equalTo( LASTNAME ) );
        assertThat( userDTO.getUserName(), equalTo( USERNAME ) );
        assertThat( userDTO.getEmail(), equalTo( EMAIL ) );
        assertThat( userDTO.getPassword(), equalTo( PASSWORD ) );
    }

    @Test
    public void userDtoToUser() {
        //given
        List<String> roles = Arrays.asList("USER","ADMIN");
        UserDTO userDTO = new UserDTO( ID, FIRSTNAME,LASTNAME,USERNAME,EMAIL,PASSWORD,AVATAR_IMAGE, roles,  null );

        //when
        User user = userMapper.userDtoToUser( userDTO );

        //then
        assertThat( user.getId(), equalTo( ID ) );
        assertThat( user.getFirstName(), equalTo( FIRSTNAME ) );
        assertThat( user.getLastName(), equalTo( LASTNAME ) );
        assertThat( user.getUserName(), equalTo( USERNAME ) );
        assertThat( user.getEmail(), equalTo( EMAIL ) );
        assertThat( user.getPassword(), equalTo( PASSWORD ) );
    }

    @Test
    public void userDtoToUser_withNullId_shouldSetUserIdToNull() {
        //given
        List<String> roles = Arrays.asList("USER","ADMIN");
        UserDTO userDTO = new UserDTO( null,FIRSTNAME,LASTNAME,USERNAME,EMAIL,PASSWORD,AVATAR_IMAGE, roles,null );

        //when
        User user = userMapper.userDtoToUser( userDTO );

        //then
        assertNotNull( user );
        assertThat( user.getId(), is( nullValue() ));
    }
}