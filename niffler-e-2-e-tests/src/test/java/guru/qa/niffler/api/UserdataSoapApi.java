package guru.qa.niffler.api;

import jaxb.userdata.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserdataSoapApi {
    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> currentUser(@Body CurrentUserRequest currentUserRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> allUsers(@Body AllUsersRequest allUsersRequest);


    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> friendsPage(@Body FriendsPageRequest friendsPageRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> friends(@Body FriendsRequest friendsRequest);


    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> removeFriend(@Body RemoveFriendRequest removeFriendRequest);


    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> acceptInvitation(@Body AcceptInvitationRequest acceptInvitationRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> declineInvitation(@Body DeclineInvitationRequest declineInvitationRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> sendInvitationRequest(@Body SendInvitationRequest sendInvitationRequest);








}
