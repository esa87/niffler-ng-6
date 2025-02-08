package guru.qa.niffler.test.soap;

import guru.qa.niffler.api.UserdataSoapClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.UserJson;
import jaxb.userdata.CurrentUserRequest;
import jaxb.userdata.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@SoapTest
public class SoapUsersTest {
    private  final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

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

}
