package io.samancore.template_builder.client.github;

import io.quarkus.rest.client.reactive.NotBody;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@Path("/graphql")
@ApplicationScoped
@RegisterRestClient(configKey = "github-api")
public interface GithubGraphQL {

    @POST
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> executeGraphQLQuery(Map<String, Object> queryBody, @NotBody String token);
}
