package io.samancore.template_ops.api;

import io.quarkus.security.Authenticated;
import io.samancore.template_ops.model.Author;
import io.samancore.template_ops.model.ConditionsProperty;
import io.samancore.template_ops.model.Node;
import io.samancore.template_ops.service.GitService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class TemplateOpsApi {
    @ConfigProperty(name = "user.token")
    String userToken;
    @ConfigProperty(name = "user.name")
    String userName;
    @ConfigProperty(name = "user.email")
    String userEmail;

    @Inject
    GitService service;

    @GET
    @Path("")
    @Authenticated
    public List<Node> products() {
        return service.listProducts(userToken);
    }

    @GET
    @Path("/{product}")
    @RolesAllowed({"group-admin"})
    public Node product(@PathParam("product") String product) {
        return service.getProduct(product, userToken);
    }

    @GET
    @Path("/{product}/templates")
    public List<Node> templates(@PathParam("product") String product) {
        return service.listTemplates(product, userToken);
    }

    @GET
    @Path("/{product}/templates/{template}")
    public Node template(@PathParam("product") String product, @PathParam("template") String template) {
        return service.getTemplateJson(product, template, userToken);
    }

    @GET
    @Path("/{product}/templates/{template}/persist/{sha}")
    public Node template(@PathParam("product") String product, @PathParam("template") String template, @PathParam("sha") String sha) {
        var content = "IyBKU09OIFRFU1Q=";
        var committer = Author.newBuilder()
                .setName(userName)
                .setEmail(userEmail)
                .build();
        var message = "commit test";

        return service.persistTemplate(product, template, message, content, sha, committer, userToken);
    }

    @GET
    @Path("/{product}/templates/{template}/conditions")
    public List<ConditionsProperty> listConditions(@PathParam("product") String product, @PathParam("template") String template) {
        return service.getConditionsTemplate(product, template, userToken);
    }
}
