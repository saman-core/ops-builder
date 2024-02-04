package io.samancore.template_ops.model.github;

import io.samancore.template_ops.model.Author;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(
        setterPrefix = "set",
        builderMethodName = "newBuilder",
        toBuilder = true
)
public class GitHubCommitRequest {
    String message;
    String content;
    String sha;
    String branch;
    Author committer;
}
