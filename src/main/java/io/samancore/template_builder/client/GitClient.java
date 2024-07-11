package io.samancore.template_builder.client;

import io.samancore.template_builder.model.AccessInfo;
import io.samancore.template_builder.model.Author;
import io.samancore.template_builder.model.ConditionsProperty;
import io.samancore.template_builder.model.Node;

import java.util.List;
import java.util.Map;

public interface GitClient {

    List<Node> listDirectories(String directory, AccessInfo accessInfo);

    Node getFile(String file, AccessInfo accessInfo);

    Node persistFile(String file, String message, String content, String sha, Author author, AccessInfo accessInfo);

    Node deleteFile(String file, String message, String sha, Author author, AccessInfo accessInfo);

    Map<String, ConditionsProperty> getMapConditionsTemplate(String path, AccessInfo accessInfo);
}
