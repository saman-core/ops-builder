package io.samancore.template_ops.api;

import io.quarkus.oidc.UserInfo;
import io.samancore.template_ops.model.*;
import io.samancore.template_ops.service.GitService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class TemplateOpsApi {
    @ConfigProperty(name = "oidc.claim.name")
    String claimName;
    @ConfigProperty(name = "oidc.claim.token")
    String claimToken;

    @Inject
    GitService service;

    @Inject
    UserInfo userInfo;

    @GET
    @Path("/")
    @RolesAllowed({"admin"})
    public List<Node> products() {
        var token = userInfo.getString(claimToken);
        return service.listProducts(token);
    }

    @GET
    @Path("/{product}")
    @RolesAllowed({"admin"})
    public Node product(@PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        return service.getProduct(product, token);
    }

    @GET
    @Path("/{product}/templates/")
    @RolesAllowed({"admin"})
    public List<Node> templates(@PathParam("product") String product) {
        var token = userInfo.getString(claimToken);
        return service.listTemplates(product, token);
    }

    @GET
    @Path("/{product}/templates/{template}")
    @RolesAllowed({"admin"})
    public Node template(@PathParam("product") String product, @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        return service.getTemplateJson(product, template, token);
    }

    @POST
    @Path("/{product}/templates/{template}")
    @RolesAllowed({"admin"})
    public Node persistTemplate(@PathParam("product") String product, @PathParam("template") String template, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        return service.persistTemplate(product, template, commitRequest, committer, token);
    }

    @GET
    @Path("/{product}/templates/{template}/conditions/{property}")
    @RolesAllowed({"admin"})
    public ConditionsProperty getConditionsProperty(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property) {
        var token = userInfo.getString(claimToken);
        return service.getConditionsProperty(product, template, property, token);
    }

    @GET
    @Path("/{product}/templates/{template}/conditions/")
    @RolesAllowed({"admin"})
    public List<ConditionsProperty> listConditions(@PathParam("product") String product, @PathParam("template") String template) {
        var token = userInfo.getString(claimToken);
        return service.getConditionsTemplate(product, template, token);
    }

    @GET
    @Path("/{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node getConditionProperty(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type) {
        var token = userInfo.getString(claimToken);
        return service.getConditionProperty(product, template, property, type, token);
    }

    @POST
    @Path("/{product}/templates/{template}/conditions/{property}/{type}")
    @RolesAllowed({"admin"})
    public Node persistConditionProperty(@PathParam("product") String product, @PathParam("template") String template, @PathParam("property") String property, @PathParam("type") ConditionType type, CommitRequest commitRequest) {
        var token = userInfo.getString(claimToken);
        var name = userInfo.getString(claimName);
        var email = userInfo.getEmail();

        var committer = Author.newBuilder()
                .setName(name)
                .setEmail(email)
                .build();
        return service.persistConditionProperty(product, template, property, type, commitRequest, committer, token);
    }
}
