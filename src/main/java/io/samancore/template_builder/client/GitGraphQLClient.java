package io.samancore.template_builder.client;

import io.samancore.template_builder.model.AccessInfoRecord;
import io.samancore.template_builder.model.NodeDetail;

import java.util.List;

public interface GitGraphQLClient {

    List<NodeDetail> listFoldersDetails(String baseFolder, List<String> subFoldersNames, AccessInfoRecord accessInfoRecord);
}
