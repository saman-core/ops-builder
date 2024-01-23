package io.samancore.template_ops.client;

import io.samancore.template_ops.model.Author;
import io.samancore.template_ops.model.ConditionsProperty;
import io.samancore.template_ops.model.Node;

import java.util.List;
import java.util.Map;

public interface GitClient {

    List<Node> listDirectories(String directory, String token);

    Node getFile(String file, String token);

    Node persistFile(String file, String message, String content, String sha, Author author, String token);

    Map<String, ConditionsProperty> getMapConditionsTemplate(String path, String token);
}
