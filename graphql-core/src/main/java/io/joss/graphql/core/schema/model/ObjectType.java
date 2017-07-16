package io.joss.graphql.core.schema.model;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class ObjectType implements Type {

  private final String name;

  @Singular
  private final List<String> descriptions;

  @Singular
  private final List<ObjectField> fields;

}
