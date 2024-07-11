package io.samancore.template_builder.service;

import io.samancore.template_builder.model.*;

import java.util.List;

public interface TemplateProductService {

    List<Node> listProducts(AccessInfo accessInfo);

    Node getProduct(String product, AccessInfo accessInfo);

    List<Node> listTemplates(String product, AccessInfo accessInfo);

    Node getTemplateJson(String product, String template, AccessInfo accessInfo);

    Node persistTemplate(String product, String template, CommitRequest commitRequest, Author author, AccessInfo accessInfo);

    ConditionsProperty getConditionsProperty(String product, String template, String property, AccessInfo accessInfo);

    List<ConditionsProperty> getConditionsTemplate(String product, String template, AccessInfo accessInfo);

    Node getConditionProperty(String product, String template, String property, ConditionType type, AccessInfo accessInfo);

    Node persistConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfo accessInfo);

    Node deleteConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfo accessInfo);
}
