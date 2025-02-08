package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CategoriesQuery;
import guru.qa.CurrenciesQuery;
import guru.qa.SpendQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.GqlTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@GqlTest
public class CategoriesGraphQlTest extends BaseGraphQlTest {

    @User(
        categories = @Category(
               name = "test",
                archived = false
        )
    )
    @Test
    @ApiLogin
    void checkCantCategoriesForAnotherUser(@Token String bearerToken, UserJson user){
        final ApolloCall<CategoriesQuery.Data> categoriesCall = apolloClient.query(new CategoriesQuery())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<CategoriesQuery.Data> response = Rx2Apollo.single(categoriesCall).blockingGet();
        final CategoriesQuery.Data data = response.dataOrThrow();
        final CategoriesQuery.User all = data.user;

//        if (!username.equals(user.username())) {
//            throw new IllegalGqlFieldAccessException("Can`t query categories for another user");
//        }
    }
}
