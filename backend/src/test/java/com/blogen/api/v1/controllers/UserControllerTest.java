package com.blogen.api.v1.controllers;

import com.blogen.api.v1.model.UserDTO;
import com.blogen.api.v1.model.UserListDTO;
import com.blogen.api.v1.services.PostService;
import com.blogen.api.v1.services.UserService;
import com.blogen.exceptions.BadRequestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static com.blogen.api.v1.controllers.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Tests for UserRestController
 * @author Cliff
 */
@RunWith(SpringRunner.class)
@WebMvcTest( controllers = {UserController.class}, secure = false)
public class UserControllerTest {

    @MockBean
    @Qualifier("userRestService")
    private UserService userService;

    @MockBean
    @Qualifier("postRestService")
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    private UserDTO userDTO1;
    private UserDTO userDTO2;
    private UserDTO newUserDTO;
    private UserDTO updateUserDTO1;

    @Before
    public void setUp() throws Exception {
        userDTO1 = new UserDTO( 1L, "jane", "smith","janey","js@zombo.com","secret","avatar1.jpg",null, UserController.BASE_URL + "/1" );
        userDTO2 = new UserDTO( 2L,"fozzy", "zoeller","shelly","foz@gmail.com","secret","avatar2.jpg", null, UserController.BASE_URL + "/2" );
        newUserDTO = new UserDTO( 3L,"new","user","noob","nooblet@hotmail.com","secret","avatar1.jpg", null, null );
        updateUserDTO1 = new UserDTO( 3L,"Joan","Crawford",null,"craw@hotmail.com","newsecret","avatar1.jpg", null,null );

    }

    @Test
    public void should_returnOKandTwoUsers_when_getAllUsers() throws Exception {
        UserListDTO userListDTO = new UserListDTO( Arrays.asList( userDTO1, userDTO2 ) );

        given( userService.getAllUsers() ).willReturn( userListDTO );

        mockMvc.perform( get( UserController.BASE_URL ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.users", hasSize(2) ) );
    }

    @Test
    public void should_returnOneUser_when_getUser() throws Exception {

        given( userService.getUser( anyLong() )).willReturn( userDTO1 );

        mockMvc.perform( get( UserController.BASE_URL + "/1" ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.userUrl", is( userDTO1.getUserUrl() )));
    }

    @Test
    public void should_returnApiErrorJSON_when_getUserWithInvalidID() throws Exception {

        given( userService.getUser( anyLong() )).willThrow( new BadRequestException( "invalid id" ) );

        mockMvc.perform( get( UserController.BASE_URL + "/67874435" ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.globalError[0].message", is( "invalid id" )));
    }

//    @Test
//    public void should_returnCreated_and_newUserAsJSON_when_createNewUser() throws Exception {
//        newUserDTO.setUserUrl( UserController.BASE_URL + "/3" );
//        given( userService.createNewUser( any( UserDTO.class ) ) ).willReturn( newUserDTO );
//
//        mockMvc.perform( post( UserController.BASE_URL )
//        .contentType( MediaType.APPLICATION_JSON )
//        .content( asJsonString( newUserDTO ) ))
//                .andExpect( status().isCreated() )
//                .andExpect( jsonPath( "$.userName", is( newUserDTO.getUserName() ) ) )
//                .andExpect( jsonPath( "$.userUrl", is( newUserDTO.getUserUrl()) ) );
//    }

//    @Test
//    public void should_returnApiErrorJSON_when_createNewUser_with_UserNameThatExists() throws Exception {
//
//        given( userService.createNewUser( any( UserDTO.class ) ) ).willThrow( new BadRequestException( "username exists" ) );
//
//        mockMvc.perform( post( UserController.BASE_URL )
//                .contentType( MediaType.APPLICATION_JSON )
//                .content( asJsonString( newUserDTO ) ))
//                .andExpect( status().isBadRequest())
//                .andExpect( jsonPath( "$.globalError[0].message", is( "username exists" ) ) );
//    }

    @Test
    public void should_returnOK_when_updateUserWithValidRequestDTO() throws Exception {

        given( userService.updateUser( anyLong(), any(UserDTO.class) )).willReturn( updateUserDTO1 );

        mockMvc.perform( patch( UserController.BASE_URL + "/1" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( updateUserDTO1 ) ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.firstName", is( updateUserDTO1.getFirstName() ) ));
    }

    @Test
    public void should_returnBadRequest_when_updateUserWithInvalidID() throws Exception {

        given( userService.updateUser( anyLong(), any(UserDTO.class) )).willThrow( new BadRequestException( "invalid id" ) );

        mockMvc.perform( patch( UserController.BASE_URL + "/67334" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( updateUserDTO1 ) ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.globalError[0].message", is( "invalid id" ) ));
    }

    @Test
    public void should_returnBadRequest_when_updateUserWithUserNameThatExists() throws Exception {

        given( userService.updateUser( anyLong(), any(UserDTO.class) )).willThrow( new BadRequestException( "username exists" ) );

        mockMvc.perform( patch( UserController.BASE_URL + "/1" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( asJsonString( updateUserDTO1 ) ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.globalError[0].message", is( "username exists" ) ));
    }
}