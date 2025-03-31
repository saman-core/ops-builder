package io.samancore.template_builder.service;

import io.samancore.template_builder.model.*;

import java.util.List;

public interface ProductsService {

    List<Node> listProducts(String module, AccessInfoRecord accessInfoRecord);

    Node getProduct(String module, String product, AccessInfoRecord accessInfoRecord);

    List<Node> listTemplates(String module, String product, AccessInfoRecord accessInfoRecord);

    Node getTemplateJson(String module, String product, String template, AccessInfoRecord accessInfoRecord);

    Node persistTemplate(String module, String product, String template, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);

    ConditionsProperty getConditionsProperty(String module, String product, String template, String property, AccessInfoRecord accessInfoRecord);

    List<ConditionsProperty> getConditionsTemplate(String module, String product, String template, AccessInfoRecord accessInfoRecord);

    Node getConditionProperty(String module, String product, String template, String property, ConditionType type, AccessInfoRecord accessInfoRecord);

    Node persistConditionProperty(String module, String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);

    Node deleteConditionProperty(String module, String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);

    Node getWorkflowJson(String module, String product, AccessInfoRecord accessInfoRecord);

    Node persistWorkflow(String module, String product, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);
}
