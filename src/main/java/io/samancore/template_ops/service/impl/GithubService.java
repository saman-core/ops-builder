package io.samancore.template_ops.service.impl;

import io.samancore.template_ops.client.GithubApi;
import io.samancore.template_ops.model.Author;
import io.samancore.template_ops.model.ConditionType;
import io.samancore.template_ops.model.ConditionsProperty;
import io.samancore.template_ops.model.Node;
import io.samancore.template_ops.model.github.CommitRequest;
import io.samancore.template_ops.service.GitService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.*;

@ApplicationScoped
public class GithubService implements GitService {
    private static final String FORM_FILE = "form.json";
    private static final String INFO_FILE = "README.md";
    private static final String DIR_TYPE = "dir";
    private static final String FILE_TYPE = "file";
    private static final String EMPTY = "";
    private static final String SLASH = "/";
    private static final String TEMPLATES = "templates";
    private static final String DEFAULT_BRANCH = "main";
    private static final String SHA = "sha";
    private static final String NAME = "name";
    private static final String CONTENT = "content";
    private static final String TYPE = "type";
    private static final String CONDITIONS = "conditions";
    private static final String UNDERSCORE = "_";

    @Inject
    @RestClient
    GithubApi api;

    @ConfigProperty(name = "git.repo")
    String gitRepo;

    @ConfigProperty(name = "git.owner")
    String gitOwner;

    @Override
    public List<Node> listProducts(String token) {
        var responseApi = api.listDirectory(gitOwner, gitRepo, EMPTY, token);
        return responseApi.stream()
                .filter(map -> DIR_TYPE.equals(map.get(TYPE)))
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
    public Node getProduct(String product, String token) {
        var file = product.concat(SLASH).concat(INFO_FILE);
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

    @Override
    public List<Node> listTemplates(String product, String token) {
        var path = product.concat(SLASH).concat(TEMPLATES);
        var responseApi = api.listDirectory(gitOwner, gitRepo, path, token);
        return responseApi.stream()
                .filter(map -> DIR_TYPE.equals(map.get(TYPE)))
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
    public Node getTemplateJson(String product, String template, String token) {
        var file = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);
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

    @Override
    public Node persistTemplate(String product, String template, String message, String content, String sha, Author author, String token) {
        var data = CommitRequest.newBuilder()
                .setMessage(message)
                .setSha(sha)
                .setCommitter(author)
                .setContent(content)
                .setBranch(DEFAULT_BRANCH)
                .build();

        var file = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(FORM_FILE);
        var responseApi = api.setContent(gitOwner, gitRepo, file, token, data);
        var responseContent = (Map<String, Objects>) responseApi.get(CONTENT);
        var newSha = String.valueOf(responseContent.get(SHA));
        return Node.newBuilder()
                .setId(newSha)
                .setName(FORM_FILE)
                .build();
    }

    @Override
    public ConditionsProperty getConditionsProperty(String product, String template, String property, String token) {
        return null;
    }

    @Override
    public List<ConditionsProperty> getConditionsTemplate(String product, String template, String token) {
        Map<String, ConditionsProperty> mapConditions = new HashMap<>();

        var path = product.concat(SLASH).concat(TEMPLATES).concat(SLASH).concat(template).concat(SLASH).concat(CONDITIONS);
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
                                .setConditions(new HashMap<>())
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
        return List.copyOf(mapConditions.values());
    }

    @Override
    public Node getConditionProperty(String product, String template, String property, ConditionType type, String token) {
        return null;
    }

    @Override
    public Node persistConditionProperty(String product, String template, String property, ConditionType type, Node node, String token) {
        return null;
    }

    protected boolean isDmnFileProperty(Map<String, Object> map) {
        var name = String.valueOf(map.get(NAME)).toLowerCase(Locale.ROOT);
        return FILE_TYPE.equals(map.get(TYPE)) && name.endsWith(".dmn") && name.contains(UNDERSCORE);
    }
}
