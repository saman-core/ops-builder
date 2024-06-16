package io.samancore.template_builder.client;

import io.samancore.template_builder.model.Author;
import io.samancore.template_builder.model.ConditionsProperty;
import io.samancore.template_builder.model.Node;

import java.util.List;
import java.util.Map;

public interface GitClient {

    List<Node> listDirectories(String directory, String branch, String token);

    Node getFile(String file, String branch, String token);

    Node persistFile(String file, String message, String content, String sha, Author author, String branch, String token);

    Node deleteFile(String file, String message, String sha, Author author, String branch, String token);

    Map<String, ConditionsProperty> getMapConditionsTemplate(String path, String branch, String token);
}
