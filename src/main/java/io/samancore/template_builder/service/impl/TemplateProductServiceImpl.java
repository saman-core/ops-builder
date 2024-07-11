package io.samancore.template_builder.service.impl;

import io.samancore.template_builder.client.GitClient;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.service.TemplateProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import static io.samancore.template_builder.GitConstants.*;

@ApplicationScoped
public class TemplateProductServiceImpl implements TemplateProductService {

    @Inject
    GitClient client;

    @Override
    public List<Node> listProducts(AccessInfo accessInfo) {
        return client.listDirectories(PRODUCTS, accessInfo);
    }

    @Override
    public Node getProduct(String product, AccessInfo accessInfo) {
        var file = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(INFO_FILE);
        return client.getFile(file, accessInfo);
    }

    @Override
    public List<Node> listTemplates(String product, AccessInfo accessInfo) {
        var path = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES);
        return client.listDirectories(path, accessInfo);
    }

    @Override
    public Node getTemplateJson(String product, String template, AccessInfo accessInfo) {
        var file = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);
        return client.getFile(file, accessInfo);
    }

    @Override
    public Node persistTemplate(String product, String template, CommitRequest commitRequest, Author author, AccessInfo accessInfo) {
        var file = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return client.persistFile(file, message, content, sha, author, accessInfo);
    }

    @Override
    public ConditionsProperty getConditionsProperty(String product, String template, String property, AccessInfo accessInfo) {
        var path = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS);
        var mapConditions = client.getMapConditionsTemplate(path, accessInfo);
        return mapConditions.get(property);
    }

    @Override
    public List<ConditionsProperty> getConditionsTemplate(String product, String template, AccessInfo accessInfo) {
        var path = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS);
        var mapConditions = client.getMapConditionsTemplate(path, accessInfo);
        return List.copyOf(mapConditions.values());
    }

    @Override
    public Node getConditionProperty(String product, String template, String property, ConditionType type, AccessInfo accessInfo) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);
        return client.getFile(file, accessInfo);
    }

    @Override
    public Node persistConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfo accessInfo) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return client.persistFile(file, message, content, sha, author, accessInfo);
    }

    @Override
    public Node deleteConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfo accessInfo) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = PRODUCTS.concat(SLASH).concat(product).concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);

        var message = commitRequest.getMessage();
        var sha = commitRequest.getData().getId();
        return client.deleteFile(file, message, sha, author, accessInfo);
    }
}
