package io.samancore.template_builder.client.github;

import io.quarkus.rest.client.reactive.NotBody;
import io.samancore.template_builder.model.github.GitHubCommitRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@Path("/repos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "github-api")
@ClientHeaderParam(name = "X-GitHub-Api-Version", value = "2022-11-28")
public interface GithubApi {

    @GET
    @Path("/{gitOwner}/{gitRepo}/contents/{file}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> getContent(@PathParam("gitOwner") String gitOwner,
                                   @PathParam("gitRepo") String gitRepo,
                                   @PathParam("file") String file,
                                   @NotBody String token
    );

    @PUT
    @Path("/{gitOwner}/{gitRepo}/contents/{file}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> setContent(@PathParam("gitOwner") String gitOwner,
                                   @PathParam("gitRepo") String gitRepo,
                                   @PathParam("file") String file,
                                   @NotBody String token,
                                   GitHubCommitRequest data
    );

    @GET
    @Path("/{gitOwner}/{gitRepo}/contents/{path}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    List<Map<String, Object>> listDirectory(@PathParam("gitOwner") String gitOwner,
                                            @PathParam("gitRepo") String gitRepo,
                                            @PathParam("path") String path,
                                            @NotBody String token
    );
}
