package io.samancore.template_builder.service;

import io.samancore.template_builder.model.*;

import java.util.List;

public interface BusinessService {

    List<Node> listTemplates(AccessInfoRecord accessInfoRecord);

    Node getTemplateJson(String template, AccessInfoRecord accessInfoRecord);

    Node persistTemplate(String template, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);

    ConditionsProperty getConditionsProperty(String template, String property, AccessInfoRecord accessInfoRecord);

    List<ConditionsProperty> getConditionsTemplate(String template, AccessInfoRecord accessInfoRecord);

    Node getConditionProperty(String template, String property, ConditionType type, AccessInfoRecord accessInfoRecord);

    Node persistConditionProperty(String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);

    Node deleteConditionProperty(String template, String property, ConditionType type, CommitRequest commitRequest, Author author, AccessInfoRecord accessInfoRecord);
}
