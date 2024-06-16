package io.samancore.template_builder.service;

import io.samancore.template_builder.model.*;

import java.util.List;

public interface GitService {

    List<Node> listProducts(String branch, String token);

    Node getProduct(String product, String branch, String token);

    List<Node> listTemplates(String product, String branch, String token);

    Node getTemplateJson(String product, String template, String branch, String token);

    Node persistTemplate(String product, String template, CommitRequest commitRequest, Author author, String branch, String token);

    ConditionsProperty getConditionsProperty(String product, String template, String property, String branch, String token);

    List<ConditionsProperty> getConditionsTemplate(String product, String template, String branch, String token);

    Node getConditionProperty(String product, String template, String property, ConditionType type, String branch, String token);

    Node persistConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, String branch, String token);

    Node deleteConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, String branch, String token);
}
