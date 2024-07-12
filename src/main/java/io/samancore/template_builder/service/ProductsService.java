package io.samancore.template_builder.service;

import io.samancore.template_builder.model.*;

import java.util.List;

public interface ProductsService {

    List<Node> listProducts(AccessInfoRecord accessInfoRecord);

    Node getProduct(String product, AccessInfoRecord accessInfoRecord);

    List<Node> listTemplates(String product, AccessInfoRecord accessInfoRecord);

    Node getTemplateJson(String product, String template, AccessInfoRecord accessInfoRecord);

    Node persistTemplate(String product, String template, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);

    ConditionsProperty getConditionsProperty(String product, String template, String property, AccessInfoRecord accessInfoRecord);

    List<ConditionsProperty> getConditionsTemplate(String product, String template, AccessInfoRecord accessInfoRecord);

    Node getConditionProperty(String product, String template, String property, ConditionType type, AccessInfoRecord accessInfoRecord);

    Node persistConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);

    Node deleteConditionProperty(String product, String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);
}
