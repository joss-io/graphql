package io.zrz.graphql.zulu.schema;

import java.util.List;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;

@GQLObjectType(name = "__Field")
public class GQLSchemaField {

  private final ExecutableOutputField field;

  public GQLSchemaField(final ExecutableOutputField field) {
    this.field = field;
  }

  // name: String!

  public String name() {
    return this.field.fieldName();
  }

  // description: String
  public String description() {
    return this.field.documentation();
  }

  // args: [__InputValue!]!
  public List<GQLSchemaInputValue> args() {
    return this.field
        .parameters()
        .map(p -> p.fieldValues())
        .orElse(ImmutableList.of())
        .stream()
        .map(x -> new GQLSchemaInputValue(x))
        .collect(ImmutableList.toImmutableList());
  }

  // type: __Type!

  public GQLSchemaType type() {
    return new GQLSchemaType(this.field.fieldType());
  }

  // isDeprecated: Boolean!

  public boolean isDeprecated() {
    return false;
  }

  // deprecationReason: String

  public String deprecationReason() {
    return null;
  }

}
