package io.samancore.template_builder.client.github;

import io.samancore.template_builder.client.GitClient;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.model.github.GitHubCommitRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.*;

import static io.samancore.template_builder.GitConstants.*;

@ApplicationScoped
public class GithubClient implements GitClient {
    private static final Logger log = Logger.getLogger(GithubClient.class);

    @Inject
    @RestClient
    GithubApi api;

    @ConfigProperty(name = "git.repo")
    String gitRepo;

    @ConfigProperty(name = "git.owner")
    String gitOwner;

    public List<Node> listDirectories(String directory, AccessInfo accessInfo) {
        List<Map<String, Object>> responseApi = new ArrayList<>();
        try {
            responseApi = api.listDirectory(gitOwner, gitRepo, directory, accessInfo.getBranch(), accessInfo.getToken());
        } catch (WebApplicationException e) {
            log.warnf(e, "WebApplicationException in PATH: %s", directory);
        }
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

    public Node getFile(String file, AccessInfo accessInfo) {
        var responseApi = api.getContent(gitOwner, gitRepo, file, accessInfo.getBranch(), accessInfo.getToken());
        var sha = String.valueOf(responseApi.get(SHA));
        var name = String.valueOf(responseApi.get(NAME));
        var content = String.valueOf(responseApi.get(CONTENT));
        return Node.newBuilder()
                .setId(sha)
                .setName(name)
                .setContent(content)
                .build();
    }

    public Node persistFile(String file, String message, String content, String sha, Author author, AccessInfo accessInfo) {
        var data = GitHubCommitRequest.newBuilder()
                .setMessage(message)
                .setSha(sha)
                .setCommitter(author)
                .setContent(content)
                .setBranch(accessInfo.getBranch())
                .build();

        var responseApi = api.setContent(gitOwner, gitRepo, file, accessInfo.getToken(), data);
        var responseContent = (Map<String, Objects>) responseApi.get(CONTENT);
        var newSha = String.valueOf(responseContent.get(SHA));
        return Node.newBuilder()
                .setId(newSha)
                .setName(FORM_FILE)
                .build();
    }

    public Node deleteFile(String file, String message, String sha, Author author, AccessInfo accessInfo) {
        var data = GitHubCommitRequest.newBuilder()
                .setMessage(message)
                .setSha(sha)
                .setCommitter(author)
                .setBranch(accessInfo.getBranch())
                .build();

        var responseApi = api.deleteContent(gitOwner, gitRepo, file, accessInfo.getToken(), data);
        var responseContent = (Map<String, Objects>) responseApi.get(COMMIT);
        var newSha = String.valueOf(responseContent.get(SHA));
        return Node.newBuilder()
                .setId(newSha)
                .build();
    }

    public Map<String, ConditionsProperty> getMapConditionsTemplate(String path, AccessInfo accessInfo) {
        Map<String, ConditionsProperty> mapConditions = new HashMap<>();
        List<Map<String, Object>> responseApi = new ArrayList<>();
        try {
            responseApi = api.listDirectory(gitOwner, gitRepo, path, accessInfo.getBranch(), accessInfo.getToken());
        } catch (WebApplicationException e) {
            log.warnf(e, "WebApplicationException in PATH: %s", path);
        }
        responseApi.stream()
                .filter(this::isDmnFileProperty)
                .forEach(map -> {
                    var sha = String.valueOf(map.get(SHA));
                    var fullName = String.valueOf(map.get(NAME));

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
        var name = String.valueOf(map.get(NAME));
        return FILE_TYPE.equals(map.get(TYPE)) && name.endsWith(DMN_EXTENSION) && name.contains(UNDERSCORE);
    }
}
