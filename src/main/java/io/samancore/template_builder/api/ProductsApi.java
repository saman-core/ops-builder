package io.samancore.template_builder.api;

import io.quarkus.oidc.UserInfo;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.service.ProductsService;
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
public class ProductsApi {
    @ConfigProperty(name = "oidc.claim.name")
    String claimName;
    @ConfigProperty(name = "oidc.claim.token")
    String claimToken;
    @ConfigProperty(name = "git.default_branch")
    String defaultBranch;

    @Inject
    ProductsService service;

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
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.listProducts(accessInfo);
    }

    @GET
    @Path("{product}")
    @RolesAllowed({"admin"})
    public Node getProduct(@PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getProduct(product, accessInfo);
    }

    @GET
    @Path("{product}/templates/")
    @RolesAllowed({"admin"})
    public List<Node> getAllTemplates(@PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.listTemplates(product, accessInfo);
    }

    @GET
    @Path("{product}/templates/{template}")
    @RolesAllowed({"admin"})
    public Node getTemplate(@PathParam("product") String product, @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getTemplateJson(product, template, accessInfo);
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
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistTemplate(product, template, commitRequest, committer, accessInfo);
    }

    @GET
    @Path("{product}/templates/{template}/conditions/{property}")
    @RolesAllowed({"admin"})
    public ConditionsProperty getConditionsProperty(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionsProperty(product, template, property, accessInfo);
    }

    @GET
    @Path("{product}/templates/{template}/conditions/")
    @RolesAllowed({"admin"})
    public List<ConditionsProperty> getAllConditionsPropertiesByTemplate(@PathParam("product") String product, @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionsTemplate(product, template, accessInfo);
    }

    @GET
    @Path("{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node getCondition(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionProperty(product, template, property, type, accessInfo);
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
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistConditionProperty(product, template, property, type, commitRequest, committer, accessInfo);
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
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.deleteConditionProperty(product, template, property, type, commitRequest, committer, accessInfo);
    }

    @GET
    @Path("{product}/workflow")
    @RolesAllowed({"admin"})
    public Node getWorkflow(@PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getWorkflowJson(product, accessInfo);
    }

    @POST
    @Path("{product}/workflow")
    @RolesAllowed({"admin"})
    public Node persistWorkflow(@PathParam("product") String product, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistWorkflow(product, commitRequest, committer, accessInfo);
    }

    private String getBranch() {
        var branch = uriInfo.getQueryParameters().getFirst("_branch");
        return branch == null ? defaultBranch : branch;
    }
}
