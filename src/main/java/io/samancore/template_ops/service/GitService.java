package io.samancore.template_ops.service;

import io.samancore.template_ops.model.*;

import java.util.List;

public interface GitService {

    List<Node> listProducts(String token);

    Node getProduct(String product, String token);

    List<Node> listTemplates(String product, String token);

    Node getTemplateJson(String product, String template, String token);

    Node persistTemplate(String product, String template, CommitRequest commitRequest, Author author, String token);

    ConditionsProperty getConditionsProperty(String product, String template, String property, String token);

    List<ConditionsProperty> getConditionsTemplate(String product, String template, String token);

    Node getConditionProperty(String product, String template, String property, ConditionType type, String token);

    Node persistConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, String token);
}
