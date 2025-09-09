package io.samancore.template_builder.client;

import io.samancore.template_builder.model.AccessInfoRecord;
import io.samancore.template_builder.model.Author;
import io.samancore.template_builder.model.ConditionsProperty;
import io.samancore.template_builder.model.Node;

import java.util.List;
import java.util.Map;

public interface GitReposClient {

    List<Node> listDirectories(String directory, AccessInfoRecord accessInfoRecord);

    Node getFile(String file, AccessInfoRecord accessInfoRecord);

    Node persistFile(String file, String message, String content, String sha, Author author, AccessInfoRecord accessInfoRecord);

    Node deleteFile(String file, String message, String sha, Author author, AccessInfoRecord accessInfoRecord);

    Map<String, ConditionsProperty> getMapConditionsTemplate(String path, AccessInfoRecord accessInfoRecord);

    String getCommitSha(String branch, AccessInfoRecord accessInfoRecord);

    Node persistFiles(List<Node> files, String message, String commitSha, Author author, AccessInfoRecord accessInfoRecord);

    Node deleteDirectory(String directory, String message, Author author, AccessInfoRecord accessInfoRecord);
}
