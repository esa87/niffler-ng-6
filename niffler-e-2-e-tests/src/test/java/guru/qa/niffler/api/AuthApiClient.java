package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.utils.OauthUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import guru.qa.niffler.service.ThreadSafeCookieStore;

public class AuthApiClient extends RestClient {

    protected final static Config CFG = Config.getInstance();
    private final AuthApi authApi;
    private static String response_type = "code";
    private static String client_id = "client";
    private static String scope = "openid";
    private static String redirect_uri = CFG.frontUrl() + "authorized";
    private static String code_challenge_method = "S256";
    private static String grant_type = "authorization_code";


    public AuthApiClient() {
        super(CFG.authUrl());
        this.authApi = retrofit.create(AuthApi.class);
    }

    public String getToken(String username, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final String code_verifier = OauthUtils.generateCodeVerifier();
        final String code_challenge = OauthUtils.generateCodeChallange(code_verifier);
        getAuthorizeCookies(code_challenge);
        final String code = sendAuthorizeData(username, password);
        return genToken(code, code_verifier).toString();
    }

    private void getAuthorizeCookies(String code_challenge) {

        Response<Void> response;
        try {
            response = authApi.getAuthorizeCookies(response_type, client_id, scope, redirect_uri, code_challenge, code_challenge_method)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code());
    }

    private String sendAuthorizeData(String username, String password) {
        Response<Void> response;
        try {
            response = authApi.sendAuthorizeData(
                            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN"),
                            username,
                            password
                    )
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code());
        String urlFromRedirect = response.raw().request().url().toString();
        return urlFromRedirect.substring(urlFromRedirect.lastIndexOf("=") + 1);
    }

    private String genToken(String code, String code_verifier) {
        Response<JsonNode> response;
        try {
            response = authApi.genToken(code, redirect_uri, code_verifier, grant_type, client_id)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(200, response.code());
        return response.body().get("id_token").asText();
    }
}
