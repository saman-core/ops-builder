package io.samancore.template_builder.client;

import io.samancore.template_builder.model.AccessInfoRecord;
import io.samancore.template_builder.model.NodeDetail;

import java.util.List;

public interface GitGraphQLClient {

    List<NodeDetail> listModulesDetails(List<String> moduleNames, AccessInfoRecord accessInfoRecord);
}
