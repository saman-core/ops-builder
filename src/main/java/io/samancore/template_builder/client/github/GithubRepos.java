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
public interface GithubRepos {

    @GET
    @Path("/{gitOwner}/{gitRepo}/contents/{file}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> getContent(@PathParam("gitOwner") String gitOwner,
                                   @PathParam("gitRepo") String gitRepo,
                                   @PathParam("file") String file,
                                   @QueryParam("ref") String branch,
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

    @DELETE
    @Path("/{gitOwner}/{gitRepo}/contents/{file}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> deleteContent(@PathParam("gitOwner") String gitOwner,
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
                                            @QueryParam("ref") String branch,
                                            @NotBody String token
    );

    @GET
    @Path("/{gitOwner}/{gitRepo}/git/ref/heads/{branch}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> getBranchRef(@PathParam("gitOwner") String gitOwner,
                                     @PathParam("gitRepo") String gitRepo,
                                     @PathParam("branch") String branch,
                                     @NotBody String token
    );

    @GET
    @Path("/{gitOwner}/{gitRepo}/git/commits/{commitSha}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> getCommit(@PathParam("gitOwner") String gitOwner,
                                  @PathParam("gitRepo") String gitRepo,
                                  @PathParam("commitSha") String commitSha,
                                  @NotBody String token
    );

    @POST
    @Path("/{gitOwner}/{gitRepo}/git/blobs")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> createBlob(@PathParam("gitOwner") String gitOwner,
                                   @PathParam("gitRepo") String gitRepo,
                                   @NotBody String token,
                                   Map<String, Object> data
    );

    @POST
    @Path("/{gitOwner}/{gitRepo}/git/trees")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> createTree(@PathParam("gitOwner") String gitOwner,
                                   @PathParam("gitRepo") String gitRepo,
                                   @NotBody String token,
                                   Map<String, Object> data
    );

    @POST
    @Path("/{gitOwner}/{gitRepo}/git/commits")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> createCommit(@PathParam("gitOwner") String gitOwner,
                                     @PathParam("gitRepo") String gitRepo,
                                     @NotBody String token,
                                     Map<String, Object> data
    );

    @PATCH
    @Path("/{gitOwner}/{gitRepo}/git/refs/heads/{branch}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> updateBranchRef(@PathParam("gitOwner") String gitOwner,
                                        @PathParam("gitRepo") String gitRepo,
                                        @PathParam("branch") String branch,
                                        @NotBody String token,
                                        Map<String, Object> data
    );

    @GET
    @Path("/{gitOwner}/{gitRepo}/git/trees/{treeSha}")
    @ClientHeaderParam(name = "Authorization", value = "Bearer {token}")
    Map<String, Object> getTree(@PathParam("gitOwner") String gitOwner,
                                @PathParam("gitRepo") String gitRepo,
                                @PathParam("treeSha") String treeSha,
                                @QueryParam("recursive") Boolean recursive,
                                @NotBody String token
    );
}
