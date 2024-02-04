package io.samancore.template_ops.client.github;

import io.samancore.template_ops.client.GitClient;
import io.samancore.template_ops.model.Author;
import io.samancore.template_ops.model.ConditionType;
import io.samancore.template_ops.model.ConditionsProperty;
import io.samancore.template_ops.model.Node;
import io.samancore.template_ops.model.github.GitHubCommitRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.*;

import static io.samancore.template_ops.GitConstants.*;

@ApplicationScoped
public class GithubClient implements GitClient {

    @Inject
    @RestClient
    GithubApi api;

    @ConfigProperty(name = "git.repo")
    String gitRepo;

    @ConfigProperty(name = "git.owner")
    String gitOwner;

    public List<Node> listDirectories(String directory, String token) {
        var responseApi = api.listDirectory(gitOwner, gitRepo, directory, token);
        return responseApi.stream()
                .filter(this::isNotHiddenDirectory)
                .map(map -> {
                    var sha = String.valueOf(map.get(SHA));
                    var name = String.valueOf(map.get(NAME));
                    return Node.newBuilder()
                            .setId(sha)
                            .setName(name)
                            .build();
                })
                .toList();
    }

    public Node getFile(String file, String token) {
        var responseApi = api.getContent(gitOwner, gitRepo, file, token);
        var sha = String.valueOf(responseApi.get(SHA));
        var name = String.valueOf(responseApi.get(NAME));
        var content = String.valueOf(responseApi.get(CONTENT));
        return Node.newBuilder()
                .setId(sha)
                .setName(name)
                .setContent(content)
                .build();
    }

    public Node persistFile(String file, String message, String content, String sha, Author author, String token) {
        var data = GitHubCommitRequest.newBuilder()
                .setMessage(message)
                .setSha(sha)
                .setCommitter(author)
                .setContent(content)
                .setBranch(DEFAULT_BRANCH)
                .build();

        var responseApi = api.setContent(gitOwner, gitRepo, file, token, data);
        var responseContent = (Map<String, Objects>) responseApi.get(CONTENT);
        var newSha = String.valueOf(responseContent.get(SHA));
        return Node.newBuilder()
                .setId(newSha)
                .setName(FORM_FILE)
                .build();
    }

    public Map<String, ConditionsProperty> getMapConditionsTemplate(String path, String token) {
        Map<String, ConditionsProperty> mapConditions = new HashMap<>();
        var responseApi = api.listDirectory(gitOwner, gitRepo, path, token);
        responseApi.stream()
                .filter(this::isDmnFileProperty)
                .forEach(map -> {
                    var sha = String.valueOf(map.get(SHA));
                    var fullName = String.valueOf(map.get(NAME)).toLowerCase(Locale.ROOT);

                    var split = fullName.split(UNDERSCORE);
                    var propertyName = StringUtils.join(split, UNDERSCORE, 0, split.length - 1);
                    var suffixNameDmn = StringUtils.join(split, UNDERSCORE, split.length - 1, split.length);
                    var suffixName = UNDERSCORE.concat(suffixNameDmn).substring(0, suffixNameDmn.length() - 3);

                    var conditionType = ConditionType.bySuffix(suffixName);
                    if (conditionType == null)
                        return;

                    if (mapConditions.get(propertyName) == null) {
                        var cp = ConditionsProperty.newBuilder()
                                .setProperty(propertyName)
                                .setConditions(new EnumMap<>(ConditionType.class))
                                .build();
                        mapConditions.put(propertyName, cp);
                    }

                    var node = Node.newBuilder()
                            .setId(sha)
                            .setName(propertyName)
                            .build();
                    var mapConditionsProperty = mapConditions.get(propertyName).getConditions();
                    mapConditionsProperty.put(conditionType, node);
                });
        return mapConditions;
    }

    protected boolean isNotHiddenDirectory(Map<String, Object> map) {
        return DIR_TYPE.equals(map.get(TYPE)) && !String.valueOf(map.get(NAME)).startsWith(DOT);
    }

    protected boolean isDmnFileProperty(Map<String, Object> map) {
        var name = String.valueOf(map.get(NAME)).toLowerCase(Locale.ROOT);
        return FILE_TYPE.equals(map.get(TYPE)) && name.endsWith(DMN_EXTENSION) && name.contains(UNDERSCORE);
    }
}
