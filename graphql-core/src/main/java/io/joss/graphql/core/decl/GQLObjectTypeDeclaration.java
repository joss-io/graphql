package io.joss.graphql.core.decl;

import java.util.Collections;
import java.util.List;

import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLTypeReference;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@Wither
@Builder(builderClassName = "Builder")
public final class GQLObjectTypeDeclaration implements GQLExtendableTypeDeclaration {

  private final String name;
  private final String description;
  private final boolean extension;

  @Singular
  private final List<GQLParameterableFieldDeclaration> fields;

  @Singular
  private final List<GQLDirective> directives;

  @Singular
  private final List<GQLDeclarationRef> ifaces;

  public static class Builder {

    public Builder addField(final String name, final GQLTypeReference type) {
      return this.field(GQLParameterableFieldDeclaration.builder().name(name).type(type).build());
    }

  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String description() {
    return this.description;
  }

  public List<GQLParameterableFieldDeclaration> fields() {
    return this.fields;
  }

  public List<GQLDeclarationRef> ifaces() {
    return this.ifaces;
  }

  public List<GQLDirective> directives() {
    if (this.directives == null) {
      return Collections.emptyList();
    }
    return this.directives;
  }

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitObject(this);
  }

  public GQLParameterableFieldDeclaration field(String name) {
    return this.fields.stream().filter(d -> d.name().equals(name)).findAny().orElse(null);
  }

  @Override
  public boolean isExtension() {
    return this.extension;
  }

}
