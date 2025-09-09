package io.samancore.template_builder.client.github;

import io.samancore.template_builder.client.GitReposClient;
import io.samancore.template_builder.model.*;
import io.samancore.template_builder.model.github.GitHubCommitRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

import static io.samancore.template_builder.GitConstants.*;

@ApplicationScoped
public class GithubReposClient implements GitReposClient {
    private static final Logger log = Logger.getLogger(GithubReposClient.class);
    public static final String DEFAULT_MODE = "100644";

    @Inject
    @RestClient
    GithubRepos api;

    @ConfigProperty(name = "git.repo")
    String gitRepo;

    @ConfigProperty(name = "git.owner")
    String gitOwner;

    @Override
    public List<Node> listDirectories(String directory, AccessInfoRecord accessInfoRecord) {
        List<Map<String, Object>> responseApi = new ArrayList<>();
        try {
            responseApi = api.listDirectory(gitOwner, gitRepo, directory, accessInfoRecord.branch(), accessInfoRecord.token());
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

    @Override
    public Node getFile(String file, AccessInfoRecord accessInfoRecord) {
        var responseApi = api.getContent(gitOwner, gitRepo, file, accessInfoRecord.branch(), accessInfoRecord.token());
        var sha = String.valueOf(responseApi.get(SHA));
        var name = String.valueOf(responseApi.get(NAME));
        var content = String.valueOf(responseApi.get(CONTENT));
        return Node.newBuilder()
                .setId(sha)
                .setName(name)
                .setContent(content)
                .build();
    }

    @Override
    public Node persistFile(String file, String message, String content, String sha, Author author, AccessInfoRecord accessInfoRecord) {
        var data = GitHubCommitRequest.newBuilder()
                .setMessage(message)
                .setSha(sha)
                .setCommitter(author)
                .setContent(content)
                .setBranch(accessInfoRecord.branch())
                .build();

        var responseApi = api.setContent(gitOwner, gitRepo, file, accessInfoRecord.token(), data);
        var responseContent = (Map<String, Objects>) responseApi.get(CONTENT);
        var newSha = String.valueOf(responseContent.get(SHA));
        return Node.newBuilder()
                .setId(newSha)
                .setName(FORM_FILE)
                .build();
    }

    @Override
    public Node deleteFile(String file, String message, String sha, Author author, AccessInfoRecord accessInfoRecord) {
        var data = GitHubCommitRequest.newBuilder()
                .setMessage(message)
                .setSha(sha)
                .setCommitter(author)
                .setBranch(accessInfoRecord.branch())
                .build();

        var responseApi = api.deleteContent(gitOwner, gitRepo, file, accessInfoRecord.token(), data);
        var responseContent = (Map<String, Objects>) responseApi.get(COMMIT);
        var newSha = String.valueOf(responseContent.get(SHA));
        return Node.newBuilder()
                .setId(newSha)
                .build();
    }

    @Override
    public Map<String, ConditionsProperty> getMapConditionsTemplate(String path, AccessInfoRecord accessInfoRecord) {
        Map<String, ConditionsProperty> mapConditions = new HashMap<>();
        List<Map<String, Object>> responseApi = new ArrayList<>();
        try {
            responseApi = api.listDirectory(gitOwner, gitRepo, path, accessInfoRecord.branch(), accessInfoRecord.token());
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

    @Override
    public String getCommitSha(String branch, AccessInfoRecord accessInfoRecord) {
        var refResponse = api.getBranchRef(gitOwner, gitRepo, branch, accessInfoRecord.token());
        Object object = refResponse.get("object");
        if (object instanceof Map) {
            return String.valueOf(((Map<?, ?>) object).get("sha"));
        }
        return "";
    }

    @Override
    public Node persistFiles(List<Node> files, String message, String commitSha, Author author, AccessInfoRecord accessInfoRecord) {
        var commitResponse = api.getCommit(gitOwner, gitRepo, commitSha, accessInfoRecord.token());
        var treeObj = commitResponse.get("tree");
        var treeSha = treeObj instanceof Map ? String.valueOf(((Map<?, ?>) treeObj).get("sha")) : String.valueOf(treeObj);

        List<Map<String, Object>> treeItems = new ArrayList<>();
        for (Node file : files) {
            Map<String, Object> blobData = new HashMap<>();
            String base64Content = Base64.getEncoder().encodeToString(file.getContent().getBytes(StandardCharsets.UTF_8));
            blobData.put("content", base64Content);
            blobData.put("encoding", "base64");
            var blobResponse = api.createBlob(gitOwner, gitRepo, accessInfoRecord.token(), blobData);
            var blobSha = String.valueOf(blobResponse.get("sha"));

            Map<String, Object> treeItem = new HashMap<>();
            treeItem.put("path", file.getName());
            treeItem.put("mode", DEFAULT_MODE);
            treeItem.put("type", "blob");
            treeItem.put("sha", blobSha);
            treeItems.add(treeItem);
        }

        Map<String, Object> treeData = new HashMap<>();
        treeData.put("base_tree", treeSha);
        treeData.put("tree", treeItems);
        var newTreeResponse = api.createTree(gitOwner, gitRepo, accessInfoRecord.token(), treeData);
        var newTreeSha = String.valueOf(newTreeResponse.get("sha"));

        Map<String, Object> commitData = new HashMap<>();
        commitData.put("message", message);
        commitData.put("tree", newTreeSha);
        commitData.put("parents", List.of(commitSha));
        Map<String, Object> committer = new HashMap<>();
        committer.put("name", author.getName());
        committer.put("email", author.getEmail());
        commitData.put("committer", committer);
        var newCommitResponse = api.createCommit(gitOwner, gitRepo, accessInfoRecord.token(), commitData);
        var newCommitSha = String.valueOf(newCommitResponse.get("sha"));

        Map<String, Object> updateRefData = new HashMap<>();
        updateRefData.put("sha", newCommitSha);
        updateRefData.put("force", false);
        api.updateBranchRef(gitOwner, gitRepo, accessInfoRecord.branch(), accessInfoRecord.token(), updateRefData);

        return Node.newBuilder()
                .setId(newCommitSha)
                .setName("multi-file-commit")
                .build();
    }

    @Override
    public Node deleteDirectory(String directory, String message, Author author, AccessInfoRecord accessInfoRecord) {
        String commitSha = getCommitSha(accessInfoRecord.branch(), accessInfoRecord);
        var commitResponse = api.getCommit(gitOwner, gitRepo, commitSha, accessInfoRecord.token());
        var treeObj = commitResponse.get("tree");
        String treeSha = treeObj instanceof Map ? String.valueOf(((Map<?, ?>) treeObj).get("sha")) : String.valueOf(treeObj);

        var treeResponse = api.getTree(gitOwner, gitRepo, treeSha, true, accessInfoRecord.token());
        List<Map<String, Object>> treeItems = (List<Map<String, Object>>) treeResponse.get("tree");

        List<Map<String, Object>> filteredTreeItems = treeItems.stream()
                .filter(item -> {
                    String path = String.valueOf(item.get("path"));
                    return !path.startsWith(directory + "/");
                })
                .map(item -> {
                    Map<String, Object> treeItem = new HashMap<>();
                    treeItem.put("path", item.get("path"));
                    treeItem.put("mode", item.get("mode"));
                    treeItem.put("type", item.get("type"));
                    treeItem.put("sha", item.get("sha"));
                    return treeItem;
                })
                .toList();

        Map<String, Object> treeData = new HashMap<>();
        treeData.put("tree", filteredTreeItems);
        treeData.put("base_tree", treeSha);
        var newTreeResponse = api.createTree(gitOwner, gitRepo, accessInfoRecord.token(), treeData);
        String newTreeSha = String.valueOf(newTreeResponse.get("sha"));

        Map<String, Object> commitData = new HashMap<>();
        commitData.put("message", message);
        commitData.put("tree", newTreeSha);
        commitData.put("parents", List.of(commitSha));
        Map<String, Object> committer = new HashMap<>();
        committer.put("name", author.getName());
        committer.put("email", author.getEmail());
        commitData.put("committer", committer);
        var newCommitResponse = api.createCommit(gitOwner, gitRepo, accessInfoRecord.token(), commitData);
        String newCommitSha = String.valueOf(newCommitResponse.get("sha"));

        Map<String, Object> updateRefData = new HashMap<>();
        updateRefData.put("sha", newCommitSha);
        updateRefData.put("force", false);
        api.updateBranchRef(gitOwner, gitRepo, accessInfoRecord.branch(), accessInfoRecord.token(), updateRefData);

        return Node.newBuilder()
                .setId(newCommitSha)
                .setName("delete-directory-commit")
                .build();
    }

    protected boolean isNotHiddenDirectory(Map<String, Object> map) {
        return DIR_TYPE.equals(map.get(TYPE)) && !String.valueOf(map.get(NAME)).startsWith(DOT);
    }

    protected boolean isDmnFileProperty(Map<String, Object> map) {
        var name = String.valueOf(map.get(NAME));
        return FILE_TYPE.equals(map.get(TYPE)) && name.endsWith(DMN_EXTENSION) && name.contains(UNDERSCORE);
    }
}
