package io.samancore.template_builder.service.impl;

import io.samancore.template_builder.client.GitGraphQLClient;
import io.samancore.template_builder.client.GitReposClient;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.service.OpsService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import static io.samancore.template_builder.GitConstants.*;

@ApplicationScoped
public class OpsServiceImpl implements OpsService {
    private static final String PRODUCTS_SLASH = PRODUCTS.concat(SLASH);

    @Inject
    GitReposClient clientRepos;

    @Inject
    GitGraphQLClient clientGraphQL;

    @Override
    public Node createModule(String module, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord) {
        var basePath = module.concat(SLASH);

        var files = List.of(
                Node.newBuilder().setName(basePath.concat("properties.json")).setContent("{}").build(),
                Node.newBuilder().setName(basePath.concat("er.json")).setContent("{}").build()
        );

        var message = commitRequest.getMessage();
        var sha = commitRequest.getData().getId();
        return clientRepos.persistFiles(files, message, sha, author, accessInfoRecord);
    }

    @Override
    public List<NodeDetail> listModules(AccessInfoRecord accessInfoRecord) {
        var modules = clientRepos.listDirectories("", accessInfoRecord);
        return clientGraphQL.listFoldersDetails("", modules.stream().map(Node::getName).toList(), accessInfoRecord);
    }

    @Override
    public Node deleteModule(String module, String message, Author author, AccessInfoRecord accessInfoRecord) {
        return clientRepos.deleteDirectory(module, message, author, accessInfoRecord);
    }

    @Override
    public Node createProduct(String module, String product, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord) {
        var basePath = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH);

        var files = List.of(
                Node.newBuilder().setName(basePath.concat("properties.json")).setContent("{}").build(),
                Node.newBuilder().setName(basePath.concat("README.md")).setContent("").build(),
                Node.newBuilder().setName(basePath.concat("workflow.json")).setContent("{}").build()
        );

        var message = commitRequest.getMessage();
        var sha = commitRequest.getData().getId();
        return clientRepos.persistFiles(files, message, sha, author, accessInfoRecord);
    }

    @Override
    public List<NodeDetail> listProducts(String module,
                                         AccessInfoRecord accessInfoRecord) {
        var base = module.concat(SLASH).concat(PRODUCTS);
        var products = clientRepos.listDirectories(base, accessInfoRecord);
        return clientGraphQL.listFoldersDetails(base, products.stream().map(Node::getName).toList(), accessInfoRecord);
    }

    @Override
    public Node getProduct(String module,
                           String product,
                           AccessInfoRecord accessInfoRecord) {
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(INFO_FILE);
        return clientRepos.getFile(file, accessInfoRecord);
    }

    @Override
    public Node deleteProduct(String module, String product, String message, Author author, AccessInfoRecord accessInfoRecord) {
        var directory = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product);
        return clientRepos.deleteDirectory(directory, message, author, accessInfoRecord);
    }

    @Override
    public Node createTemplate(String module, String product, String template, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord) {
        String base = module.concat(SLASH)
                .concat(PRODUCTS_SLASH).concat(product)
                .concat(SLASH).concat(TEMPLATES)
                .concat(SLASH).concat(template)
                .concat(SLASH);

        var formFile = base.concat(FORM_FILE);
        var undeletemeFile = base.concat(CONDITIONS).concat(SLASH).concat(".undeleteme");

        var files = List.of(
                Node.newBuilder().setName(formFile).setContent("{}").build(),
                Node.newBuilder().setName(undeletemeFile).setContent("").build()
        );

        var message = commitRequest.getMessage();
        var sha = commitRequest.getData().getId();
        return clientRepos.persistFiles(files, message, sha, author, accessInfoRecord);
    }

    @Override
    public List<Node> listTemplates(String module,
                                    String product,
                                    AccessInfoRecord accessInfoRecord) {
        var path = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES);
        return clientRepos.listDirectories(path, accessInfoRecord);
    }

    @Override
    public Node getTemplateJson(String module,
                                String product,
                                String template,
                                AccessInfoRecord accessInfoRecord) {
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);
        return clientRepos.getFile(file, accessInfoRecord);
    }

    @Override
    public Node persistTemplate(String module,
                                String product,
                                String template,
                                CommitRequest commitRequest,
                                Author author,
                                AccessInfoRecord accessInfoRecord) {
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return clientRepos.persistFile(file, message, content, sha, author, accessInfoRecord);
    }

    @Override
    public Node deleteTemplate(String module, String product, String template, String message, Author author, AccessInfoRecord accessInfoRecord) {
        var directory = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template);
        return clientRepos.deleteDirectory(directory, message, author, accessInfoRecord);
    }

    @Override
    public ConditionsProperty getConditionsProperty(String module,
                                                    String product,
                                                    String template,
                                                    String property,
                                                    AccessInfoRecord accessInfoRecord) {
        var path = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS);
        var mapConditions = clientRepos.getMapConditionsTemplate(path, accessInfoRecord);
        return mapConditions.get(property);
    }

    @Override
    public List<ConditionsProperty> getConditionsTemplate(String module,
                                                          String product,
                                                          String template,
                                                          AccessInfoRecord accessInfoRecord) {
        var path = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS);
        var mapConditions = clientRepos.getMapConditionsTemplate(path, accessInfoRecord);
        return List.copyOf(mapConditions.values());
    }

    @Override
    public Node getConditionProperty(String module,
                                     String product,
                                     String template,
                                     String property,
                                     ConditionType type,
                                     AccessInfoRecord accessInfoRecord) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);
        return clientRepos.getFile(file, accessInfoRecord);
    }

    @Override
    public Node persistConditionProperty(String module,
                                         String product,
                                         String template,
                                         String property,
                                         ConditionType type,
                                         CommitRequest commitRequest,
                                         Author author,
                                         AccessInfoRecord accessInfoRecord) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return clientRepos.persistFile(file, message, content, sha, author, accessInfoRecord);
    }

    @Override
    public Node deleteConditionProperty(String module,
                                        String product,
                                        String template,
                                        String property,
                                        ConditionType type,
                                        CommitRequest commitRequest,
                                        Author author,
                                        AccessInfoRecord accessInfoRecord) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);

        var message = commitRequest.getMessage();
        var sha = commitRequest.getData().getId();
        return clientRepos.deleteFile(file, message, sha, author, accessInfoRecord);
    }

    @Override
    public Node getWorkflowJson(String module,
                                String product,
                                AccessInfoRecord accessInfoRecord) {
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(WORKFLOW_FILE);
        return clientRepos.getFile(file, accessInfoRecord);
    }

    @Override
    public Node persistWorkflow(String module,
                                String product,
                                CommitRequest commitRequest,
                                Author author,
                                AccessInfoRecord accessInfoRecord) {
        var file = module.concat(SLASH).concat(PRODUCTS_SLASH).concat(product).concat(SLASH).concat(WORKFLOW_FILE);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return clientRepos.persistFile(file, message, content, sha, author, accessInfoRecord);
    }

    @Override
    public Node getErJson(String module,
                          AccessInfoRecord accessInfoRecord) {
        var file = module.concat(SLASH).concat(ER_FILE);
        return clientRepos.getFile(file, accessInfoRecord);
    }

    @Override
    public Node persistEr(String module,
                          CommitRequest commitRequest,
                          Author author,
                          AccessInfoRecord accessInfoRecord) {
        var file = module.concat(SLASH).concat(ER_FILE);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return clientRepos.persistFile(file, message, content, sha, author, accessInfoRecord);
    }
}
