package io.samancore.template_ops.service;

import io.samancore.template_ops.model.Author;
import io.samancore.template_ops.model.ConditionType;
import io.samancore.template_ops.model.ConditionsProperty;
import io.samancore.template_ops.model.Node;

import java.util.List;

public interface GitService {

    List<Node> listProducts(String token);

    Node getProduct(String product, String token);

    List<Node> listTemplates(String product, String token);

    Node getTemplateJson(String product, String template, String token);

    Node persistTemplate(String product, String template, String message, String content, String sha, Author author, String token);

    ConditionsProperty getConditionsProperty(String product, String template, String property, String token);

    List<ConditionsProperty> getConditionsTemplate(String product, String template, String token);

    Node getConditionProperty(String product, String template, String property, ConditionType type, String token);

    Node persistConditionProperty(String product, String template, String property, ConditionType type, String message, String content, String sha, Author author, String token);
}
