package io.samancore.template_builder.client.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.samancore.template_builder.client.GitGraphQLClient;
import io.samancore.template_builder.model.AccessInfoRecord;
import io.samancore.template_builder.model.NodeDetail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;

@ApplicationScoped
public class GithubGraphQLClient implements GitGraphQLClient {
    private static final Logger log = Logger.getLogger(GithubGraphQLClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    @RestClient
    GithubGraphQL api;

    @ConfigProperty(name = "git.repo")
    String gitRepo;

    @ConfigProperty(name = "git.owner")
    String gitOwner;

    @Override
    public List<NodeDetail> listModulesDetails(List<String> moduleNames, AccessInfoRecord accessInfoRecord) {
        String branch = accessInfoRecord.branch();
        log.infof("Generating GraphQL query for branch '%s' and modules: %s", branch, moduleNames);
        Map<String, Object> queryBody = new HashMap<>();
        queryBody.put("query", generateQuery(branch, moduleNames));
        Map<String, Object> response;
        try {
            response = api.executeGraphQLQuery(queryBody, accessInfoRecord.token());
        } catch (Exception e) {
            log.error("Error executing GraphQL query to GitHub API", e);
            throw e;
        }
        if (response == null || !response.containsKey("data")) {
            log.warn("No data found in GraphQL response");
            return new ArrayList<>();
        }
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null || !data.containsKey("repo")) {
            log.warn("No 'repo' field found in GraphQL response data");
            return new ArrayList<>();
        }
        Map<String, Object> repoData = (Map<String, Object>) data.get("repo");
        return generateResponse(moduleNames, repoData);
    }

    private Map<String, Object> toObjectMap(String obj) {
        try {
            return MAPPER.readValue(obj, Map.class);
        } catch (IOException e) {
            log.error("Error parsing JSON string to Map", e);
            throw new RuntimeException("Error al parsear JSON", e);
        }
    }

    private String generateQuery(String branch, List<String> moduleNames) {
        log.infof("Building GraphQL query for branch '%s' and modules: %s", branch, moduleNames);
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("query {");
        queryBuilder.append("repo: repository(owner: \"").append(gitOwner).append("\", name: \"").append(gitRepo).append("\") {");
        for (String module : moduleNames) {
            queryBuilder.append(module).append(": object(expression: \"")
                    .append(branch).append(":").append(module).append("/properties.json\") { ...on Blob { text } }");

            queryBuilder.append(module).append("History: ref(qualifiedName: \"")
                    .append(branch).append("\") { target { ... on Commit { history(first: 1, path: \"")
                    .append(module).append("\") { edges { node { committedDate } } } } } }");
        }
        queryBuilder.append("}}\n");
        return queryBuilder.toString();
    }

    private List<NodeDetail> generateResponse(List<String> moduleNames, Map<String, Object> repoData) {
        List<NodeDetail> result = new ArrayList<>();
        for (String module : moduleNames) {
            Map<String, Object> propertiesJson = extractPropertiesJson(repoData, module);
            String lastModified = extractLastModified(repoData, module);
            if (propertiesJson == null) {
                log.warnf("No properties.json found for module '%s'", module);
            }
            if (lastModified == null) {
                log.warnf("No last modified date found for module '%s'", module);
            }
            result.add(new NodeDetail(module, propertiesJson, lastModified));
        }
        return result;
    }

    private Map<String, Object> extractPropertiesJson(Map<String, Object> repoData, String module) {
        if (repoData.get(module) instanceof Map<?, ?> blobObj) {
            try {
                return toObjectMap((String) blobObj.get("text"));
            } catch (Exception e) {
                log.errorf(e, "Error parsing properties.json for module '%s'", module);
            }
        }
        return null;
    }

    private String extractLastModified(Map<String, Object> repoData, String module) {
        if (repoData.get(module + "History") instanceof Map<?, ?> historyObj) {
            Map<String, Object> target = (Map<String, Object>) historyObj.get("target");
            if (target != null && target.get("history") instanceof Map<?, ?> hist) {
                List<Map<String, Object>> edges = (List<Map<String, Object>>) hist.get("edges");
                if (edges != null && !edges.isEmpty()) {
                    Map<String, Object> node = (Map<String, Object>) edges.get(0).get("node");
                    if (node != null) {
                        return (String) node.get("committedDate");
                    }
                }
            }
        }
        return null;
    }
}
