package com.dev.authservice.config;



import com.dev.authservice.security.repository.JpaRegisteredClientRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClientConfig implements ApplicationRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ClientConfig(RegisteredClientRepository registeredClientRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Only register if not already registered
        if (registeredClientRepository.findByClientId("productservice") == null) {
            RegisteredClient productServiceClient = RegisteredClient
                    .withId(UUID.randomUUID().toString())
                    .clientId("productservice")
                    .clientSecret(bCryptPasswordEncoder.encode("productservicesecret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("https://oauth.pstmn.io/v1/callback")
                    .redirectUri("https://oauth.pstmn.io/v1/browser-callback")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(true)
                            .build())
                    .build();

            registeredClientRepository.save(productServiceClient);
            System.out.println("Registered productservice OAuth2 client");
        } else {
            System.out.println("productservice client already registered, skipping");
        }
    }
}
