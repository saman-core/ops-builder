package io.samancore.template_builder.api;

import io.quarkus.oidc.UserInfo;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.service.BusinessService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Path("/business/")
@Produces(MediaType.APPLICATION_JSON)
public class BusinessApi {
    @ConfigProperty(name = "oidc.claim.name")
    String claimName;
    @ConfigProperty(name = "oidc.claim.token")
    String claimToken;
    @ConfigProperty(name = "git.default_branch")
    String defaultBranch;

    @Inject
    BusinessService service;

    @Inject
    UserInfo userInfo;

    @Context
    UriInfo uriInfo;

    @GET
    @Path("templates/")
    @RolesAllowed({"admin"})
    public List<Node> getAllTemplates() {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.listTemplates(accessInfo);
    }

    @GET
    @Path("templates/{template}")
    @RolesAllowed({"admin"})
    public Node getTemplate(@PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getTemplateJson(template, accessInfo);
    }

    @POST
    @Path("templates/{template}")
    @RolesAllowed({"admin"})
    public Node persistTemplate(@PathParam("template") String template, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistTemplate(template, commitRequest, committer, accessInfo);
    }

    @GET
    @Path("templates/{template}/conditions/{property}")
    @RolesAllowed({"admin"})
    public ConditionsProperty getConditionsProperty(@PathParam("template") String template, @PathParam("property") String property) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionsProperty(template, property, accessInfo);
    }

    @GET
    @Path("templates/{template}/conditions/")
    @RolesAllowed({"admin"})
    public List<ConditionsProperty> getAllConditionsPropertiesByTemplate(@PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionsTemplate(template, accessInfo);
    }

    @GET
    @Path("templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node getCondition(@PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionProperty(template, property, type, accessInfo);
    }

    @POST
    @Path("templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node persistCondition(@PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistConditionProperty(template, property, type, commitRequest, committer, accessInfo);
    }

    @DELETE
    @Path("templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node deleteCondition(@PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.deleteConditionProperty(template, property, type, commitRequest, committer, accessInfo);
    }

    private String getBranch() {
        var branch = uriInfo.getQueryParameters().getFirst("_branch");
        return branch == null ? defaultBranch : branch;
    }
}
