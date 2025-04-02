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
    public List<Node> getAllModules() {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.listModules(accessInfo);
    }

    @GET
    @Path("{module}")
    @RolesAllowed({"admin"})
    public List<Node> getAllProducts(@PathParam("module") String module) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.listProducts(module, accessInfo);
    }

    @GET
    @Path("{module}/{product}")
    @RolesAllowed({"admin"})
    public Node getProduct(@PathParam("module") String module,
                           @PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getProduct(module, product, accessInfo);
    }

    @GET
    @Path("{module}/{product}/templates/")
    @RolesAllowed({"admin"})
    public List<Node> getAllTemplates(@PathParam("module") String module,
                                      @PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.listTemplates(module, product, accessInfo);
    }

    @GET
    @Path("{module}/{product}/templates/{template}")
    @RolesAllowed({"admin"})
    public Node getTemplate(@PathParam("module") String module,
                            @PathParam("product") String product,
                            @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getTemplateJson(module, product, template, accessInfo);
    }

    @POST
    @Path("{module}/{product}/templates/{template}")
    @RolesAllowed({"admin"})
    public Node persistTemplate(@PathParam("module") String module,
                                @PathParam("product") String product,
                                @PathParam("template") String template,
                                CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistTemplate(module, product, template, commitRequest, committer, accessInfo);
    }

    @GET
    @Path("{module}/{product}/templates/{template}/conditions/{property}")
    @RolesAllowed({"admin"})
    public ConditionsProperty getConditionsProperty(@PathParam("module") String module,
                                                    @PathParam("product") String product,
                                                    @PathParam("template") String template,
                                                    @PathParam("property") String property) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionsProperty(module, product, template, property, accessInfo);
    }

    @GET
    @Path("{module}/{product}/templates/{template}/conditions/")
    @RolesAllowed({"admin"})
    public List<ConditionsProperty> getAllConditionsPropertiesByTemplate(@PathParam("module") String module,
                                                                         @PathParam("product") String product,
                                                                         @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionsTemplate(module, product, template, accessInfo);
    }

    @GET
    @Path("{module}/{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node getCondition(@PathParam("module") String module,
                             @PathParam("product") String product,
                             @PathParam("template") String template,
                             @PathParam("property") String property,
                             @PathParam("type") ConditionType type) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getConditionProperty(module, product, template, property, type, accessInfo);
    }

    @POST
    @Path("{module}/{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node persistCondition(@PathParam("module") String module,
                                 @PathParam("product") String product,
                                 @PathParam("template") String template,
                                 @PathParam("property") String property,
                                 @PathParam("type") ConditionType type,
                                 CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistConditionProperty(module, product, template, property, type, commitRequest, committer, accessInfo);
    }

    @DELETE
    @Path("{module}/{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node deleteCondition(@PathParam("module") String module,
                                @PathParam("product") String product,
                                @PathParam("template") String template,
                                @PathParam("property") String property,
                                @PathParam("type") ConditionType type,
                                CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.deleteConditionProperty(module, product, template, property, type, commitRequest, committer, accessInfo);
    }

    @GET
    @Path("{module}/{product}/workflow")
    @RolesAllowed({"admin"})
    public Node getWorkflow(@PathParam("module") String module,
                            @PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.getWorkflowJson(module, product, accessInfo);
    }

    @POST
    @Path("{module}/{product}/workflow")
    @RolesAllowed({"admin"})
    public Node persistWorkflow(@PathParam("module") String module,
                                @PathParam("product") String product,
                                CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        var branch = getBranch();
        var accessInfo = new AccessInfoRecord(token, branch);
        return service.persistWorkflow(module, product, commitRequest, committer, accessInfo);
    }

    private String getBranch() {
        var branch = uriInfo.getQueryParameters().getFirst("_branch");
        return branch == null ? defaultBranch : branch;
    }
}
