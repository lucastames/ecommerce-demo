package org.tames.ecommercecrud.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;

public class CustomPagedModel<T> extends PagedModel<T> {
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public CustomPagedModel(
      @JsonProperty("content") List<T> content, @JsonProperty("page") JsonNode page) {
    super(
        new PageImpl<>(
            content,
            PageRequest.of(page.get("number").asInt(), page.get("size").asInt()),
            page.get("totalElements").asInt()));
  }
}
