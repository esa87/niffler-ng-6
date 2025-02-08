package guru.qa.niffler.test.soap;

import guru.qa.niffler.api.UserdataSoapClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.userdata.wsdl.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;


@SoapTest
public class SoapUsersTest {
    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @Test
    @User
    void currentUserTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse response = userdataSoapClient.getCurrentUser(request);
        Assertions.assertEquals(
                user.username(),
                response.getUser().getUsername()
        );
    }

    @Test
    @User(friends = 3, incomeInvitations = 1)
    void getListFriends(UserJson user) throws IOException {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(user.username());
        UsersResponse response = userdataSoapClient.getFriends(request);
        Assertions.assertEquals(
                4L,
                response.getUser().size()
        );
    }


    @Test
    @User(friends = 10)
    void getListFriendsPage(UserJson user) throws IOException {
        FriendsPageRequest request = new FriendsPageRequest();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(5);
        request.setUsername(user.username());
        request.setPageInfo(pageInfo);
        UsersResponse response = userdataSoapClient.getFriendsPage(request);
        Assertions.assertEquals(
                5L,
                response.getUser().size()
        );
    }

    @Test
    @User(friends = 1)
    void testRemoveFriend(UserJson user) throws IOException {
        RemoveFriendRequest request = new RemoveFriendRequest();
        request.setUsername(user.username());
        request.setFriendToBeRemoved(user.testData().friends().get(0).username());
        userdataSoapClient.doRemoveFriend(request);
        CurrentUserRequest requestTwo = new CurrentUserRequest();
        requestTwo.setUsername(user.username());
        UserResponse response = userdataSoapClient.getCurrentUser(requestTwo);
        Assertions.assertEquals(
                FriendshipStatus.VOID,
                response.getUser().getFriendshipStatus()
        );
    }

    @Test
    @User(incomeInvitations = 1)
    void testAcceptFriendship(UserJson user) throws IOException {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setUsername(user.username());
        request.setFriendToBeAdded(user.testData().incomeInvitations().get(0).username());
        userdataSoapClient.doAcceptInvitation(request);
        FriendsRequest requestTwo = new FriendsRequest();
        requestTwo.setUsername(user.username());
        UsersResponse response = userdataSoapClient.getFriends(requestTwo);
        Assertions.assertEquals(
                user.testData().incomeInvitations().get(0).username(),
                response.getUser().get(0).getUsername()
        );
    }

    @Test
    @User(incomeInvitations = 1)
    void testDeclineFriendship(UserJson user) throws IOException {
        DeclineInvitationRequest request = new DeclineInvitationRequest();
        request.setUsername(user.username());
        request.setInvitationToBeDeclined(user.testData().incomeInvitations().get(0).username());
        userdataSoapClient.doDeclineInvitation(request);
        FriendsRequest requestTwo = new FriendsRequest();
        requestTwo.setUsername(user.username());
        UsersResponse response = userdataSoapClient.getFriends(requestTwo);
        Assertions.assertEquals(
                0,
                response.getUser().size()
        );
    }

    @Test
    @User(friends = 2)
    void testSendOutcomeInvitation(UserJson user) throws IOException {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(user.username());
        UsersResponse response = userdataSoapClient.getFriends(request);
        String userOutcome = response.getUser().get(0).getUsername();
        String userIncome = response.getUser().get(1).getUsername();

        SendInvitationRequest sendInvitation = new SendInvitationRequest();
        sendInvitation.setUsername(userOutcome);
        sendInvitation.setFriendToBeRequested(userIncome);
        userdataSoapClient.doSendInvitation(sendInvitation);

        FriendsRequest requestForUserIncome = new FriendsRequest();
        requestForUserIncome.setUsername(userIncome);
        UsersResponse responseForUserIncome = userdataSoapClient.getFriends(requestForUserIncome);
        Assertions.assertEquals(
                userOutcome,
                responseForUserIncome.getUser().get(0).getUsername()
        );
    }


}
