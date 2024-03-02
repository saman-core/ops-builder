package io.samancore.template_builder.service.impl;

import io.samancore.template_builder.client.GitClient;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.service.GitService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import static io.samancore.template_builder.GitConstants.*;

@ApplicationScoped
public class GitServiceImpl implements GitService {

    @Inject
    GitClient client;

    @Override
    public List<Node> listProducts(String token) {
        return client.listDirectories(EMPTY, token);
    }

    @Override
    public Node getProduct(String product, String token) {
        var file = product.concat(SLASH).concat(INFO_FILE);
        return client.getFile(file, token);
    }

    @Override
    public List<Node> listTemplates(String product, String token) {
        var path = product.concat(SLASH).concat(TEMPLATES);
        return client.listDirectories(path, token);
    }

    @Override
    public Node getTemplateJson(String product, String template, String token) {
        var file = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);
        return client.getFile(file, token);
    }

    @Override
    public Node persistTemplate(String product, String template, CommitRequest commitRequest, Author author, String token) {
        var file = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return client.persistFile(file, message, content, sha, author, token);
    }

    @Override
    public ConditionsProperty getConditionsProperty(String product, String template, String property, String token) {
        var path = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS);
        var mapConditions = client.getMapConditionsTemplate(path, token);
        return mapConditions.get(property);
    }

    @Override
    public List<ConditionsProperty> getConditionsTemplate(String product, String template, String token) {
        var path = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS);
        var mapConditions = client.getMapConditionsTemplate(path, token);
        return List.copyOf(mapConditions.values());
    }

    @Override
    public Node getConditionProperty(String product, String template, String property, ConditionType type, String token) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);
        return client.getFile(file, token);
    }

    @Override
    public Node persistConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, String token) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);

        var message = commitRequest.getMessage();
        var content = commitRequest.getData().getContent();
        var sha = commitRequest.getData().getId();
        return client.persistFile(file, message, content, sha, author, token);
    }

    @Override
    public Node deleteConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, String token) {
        var conditionName = property.concat(type.getSuffix()).concat(DMN_EXTENSION);
        var file = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS).concat(SLASH).concat(conditionName);

        var message = commitRequest.getMessage();
        var sha = commitRequest.getData().getId();
        return client.deleteFile(file, message, sha, author, token);
    }
}
