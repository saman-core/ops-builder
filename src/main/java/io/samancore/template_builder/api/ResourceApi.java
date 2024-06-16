package io.samancore.template_builder.api;

import io.quarkus.oidc.UserInfo;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.service.GitService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Path("/products/")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceApi {
    @ConfigProperty(name = "oidc.claim.name")
    String claimName;
    @ConfigProperty(name = "oidc.claim.token")
    String claimToken;
    @ConfigProperty(name = "git.default_branch")
    String defaultBranch;

    @Inject
    GitService service;

    @Inject
    UserInfo userInfo;

    @Context
    UriInfo uriInfo;

    @GET
    @Path("")
    @RolesAllowed({"admin"})
    public List<Node> getAllProducts() {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        return service.listProducts(branch, token);
    }

    @GET
    @Path("{product}")
    @RolesAllowed({"admin"})
    public Node getProduct(@PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        return service.getProduct(product, branch, token);
    }

    @GET
    @Path("{product}/templates/")
    @RolesAllowed({"admin"})
    public List<Node> getAllTemplatesByProduct(@PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        return service.listTemplates(product, branch, token);
    }

    @GET
    @Path("{product}/templates/{template}")
    @RolesAllowed({"admin"})
    public Node getTemplate(@PathParam("product") String product, @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        return service.getTemplateJson(product, template, branch, token);
    }

    @POST
    @Path("{product}/templates/{template}")
    @RolesAllowed({"admin"})
    public Node persistTemplate(@PathParam("product") String product, @PathParam("template") String template, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        return service.persistTemplate(product, template, commitRequest, committer, branch, token);
    }

    @GET
    @Path("{product}/templates/{template}/conditions/{property}")
    @RolesAllowed({"admin"})
    public ConditionsProperty getConditionsProperty(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        return service.getConditionsProperty(product, template, property, branch, token);
    }

    @GET
    @Path("{product}/templates/{template}/conditions/")
    @RolesAllowed({"admin"})
    public List<ConditionsProperty> getAllConditionsPropertiesByTemplate(@PathParam("product") String product, @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        return service.getConditionsTemplate(product, template, branch, token);
    }

    @GET
    @Path("{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node getCondition(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        return service.getConditionProperty(product, template, property, type, branch, token);
    }

    @POST
    @Path("{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node persistCondition(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        return service.persistConditionProperty(product, template, property, type, commitRequest, committer, branch, token);
    }

    @DELETE
    @Path("{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node deleteCondition(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        return service.deleteConditionProperty(product, template, property, type, commitRequest, committer, branch, token);
    }

    private String getBranch() {
        var branch = uriInfo.getQueryParameters().getFirst("_branch");
        return branch == null ? defaultBranch : branch;
    }
}
