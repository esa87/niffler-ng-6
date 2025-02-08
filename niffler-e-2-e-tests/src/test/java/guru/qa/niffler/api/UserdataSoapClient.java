package guru.qa.niffler.api;

import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.userdata.wsdl.*;
import io.qameta.allure.Step;

import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UserdataSoapClient extends RestClient {

    protected static final Config CFG = Config.getInstance();
    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @NotNull
    @Step("Get current user info using soap")
    public UserResponse getCurrentUser(CurrentUserRequest request) throws IOException {
        return userdataSoapApi.currentUser(request).execute().body();
    }

    @NotNull
    @Step("Get all users info using soap")
    public UsersResponse gatAllUsers(AllUsersRequest request) throws IOException {
        return userdataSoapApi.allUsers(request).execute().body();
    }

    @NotNull
    @Step("Get friends friends list for current user info using soap")
    public UsersResponse getFriends(FriendsRequest request) throws IOException {
        return userdataSoapApi.friends(request).execute().body();
    }

    @NotNull
    @Step("Get friends list page for current user info using soap")
    public UsersResponse getFriendsPage(FriendsPageRequest request) throws IOException {
        return userdataSoapApi.friendsPage(request).execute().body();
    }

    @Step("Remove friends info using soap")
    public void doRemoveFriend(RemoveFriendRequest request) throws IOException {
        userdataSoapApi.removeFriend(request);
    }

    @NotNull
    @Step("accepted friendship invitation info using soap")
    public UserResponse doAcceptInvitation(AcceptInvitationRequest request) throws IOException {
        return userdataSoapApi.acceptInvitation(request).execute().body();
    }

    @NotNull
    @Step("decline friendship invitation info using soap")
    public UserResponse doDeclineInvitation(DeclineInvitationRequest request) throws IOException {
        return userdataSoapApi.declineInvitation(request).execute().body();
    }

    @NotNull
    @Step("send friendship invitation info using soap")
    public UserResponse doSendInvitation(SendInvitationRequest request) throws IOException {
        return userdataSoapApi.sendInvitationRequest(request).execute().body();
    }


}
