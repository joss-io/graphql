package io.zrz.graphql.zulu.executable;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.doc.GQLTypeResolver;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZType;

/**
 * a resolved and ready-to-execute schema bound to the java and graphql domain.
 *
 * @author theo
 *
 */

public class ExecutableSchema implements ExecutableElement, GQLTypeResolver {

  private final ImmutableMap<GQLOperationType, ExecutableOutputType> operationRoots;
  private final ImmutableMap<String, ExecutableType> types;
  private final ImmutableMap<TypeToken<?>, ExecutableType> tokens;

  ExecutableSchema(final ExecutableSchemaBuilder b) {

    final BuildContext ctx = new BuildContext(b, this);

    final Builder<GQLOperationType, ExecutableOutputType> roots = ImmutableMap.builder();

    b.operationRoots()
        .entrySet()
        .stream()
        .sequential()
        .sorted(Comparator.comparing(Entry::getValue, Comparator.comparing(Symbol::typeName)))
        .forEach(e -> roots.put(e.getKey(), (ExecutableOutputType) ctx.compile(e.getValue())));

    this.operationRoots = roots.build();

    // we also need to force-add any types which are not exposed directly through params/return types but implement
    // interfaces which are.

    b.additionalTypes(ctx.types.keySet())
        .sorted(Comparator.comparing(Symbol::typeName))
        .forEach(ctx::compile);

    this.types = ctx.types
        .entrySet()
        .stream()
        .sorted(Comparator.comparing(Entry::getKey, Comparator.comparing(Symbol::typeName)))
        .collect(ImmutableMap.toImmutableMap(k -> k.getKey().typeName, Entry::getValue, this::mergeTypenames));

    this.tokens = ctx.types
        .entrySet()
        .stream()
        .sorted(Comparator.comparing(Entry::getKey, Comparator.comparing(Symbol::typeName)))
        .collect(ImmutableMap.toImmutableMap(k -> k.getKey().typeToken, k -> k.getValue(), this::mergeTypes));

  }

  /**
   * merges the types.
   *
   * we can have multiple symbols for the same name because of aliasing internally. e.g, multiple scalar types which use
   * {@link String} as their representation.
   *
   * annotations on the type usage would define the name that they use in the schema, and we just need to track them
   * together.
   *
   * this is only supported for scalar types.
   *
   * @param a
   * @param b
   * @return
   */

  private ExecutableType mergeTypenames(final ExecutableType a, final ExecutableType b) {

    if (GraphQLUtils.isBuiltinScalar(a.typeName())) {
      return a;
    }

    throw new IllegalArgumentException(
        "type name registered multiple times:\n - "
            + a + " (" + a.typeName() + ")" + "\n - "
            + b + " (" + b.typeName() + ")");

  }

  private ExecutableType mergeTypes(final ExecutableType a, final ExecutableType b) {

    if (GraphQLUtils.isBuiltinScalar(a.typeName())) {
      return a;
    }

    if (GraphQLUtils.isBuiltinScalar(b.typeName())) {
      return b;
    }

    throw new IllegalArgumentException(
        "type " + a.javaType() + " used with multiple names:\n - "
            + " " + a.typeName() + "\n - "
            + " " + b.typeName());

  }

  /**
   * fetches the root type for a specific operation on this schema.
   *
   * this would normally be {@link GQLOpType#Query}, {@link GQLOpType#Mutation}, or {@link GQLOpType#Subscription}.
   *
   */

  public Optional<ExecutableOutputType> rootType(final GQLOperationType type) {
    return Optional.ofNullable(this.operationRoots.get(type));
  }

  /**
   * the name of each GraphQL type in this schema.
   */

  public Stream<String> typeNames() {
    return this.types.keySet().stream();
  }

  /**
   * returns the ZType that represents the given GraphQL type name.
   */

  public ZType type(final String typeName) {
    return null;
  }

  public Stream<ExecutableType> types() {
    return this.types.values().stream();
  }

  /**
   *
   * @return
   */

  public Map<GQLOperationType, ExecutableOutputType> operationTypes() {
    return this.operationRoots;

  }

  /**
   *
   *
   * @param typeName
   * @param fieldName
   *
   * @return
   */

  public ExecutableOutputField resolve(final String typeName, final String fieldName) {

    final ExecutableType type = this.types.get(typeName);

    if (type.logicalKind() != LogicalTypeKind.OUTPUT) {
      throw new IllegalArgumentException("type '" + typeName + "' is not an output type");
    }

    return this.resolve((ExecutableOutputType) type, fieldName);

  }

  /**
   *
   *
   * @param type
   * @param fieldName
   *
   * @return
   */

  private ExecutableOutputField resolve(final ExecutableOutputType type, final String fieldName) {
    return type.field(fieldName)
        .orElseThrow(() -> type.missingFieldException(fieldName));
  }

  /**
   *
   *
   * @return
   */

  public static ExecutableSchemaBuilder builder() {
    return new ExecutableSchemaBuilder();
  }

  public static ExecutableSchema withRoot(final Type queryRoot) {
    return builder()
        .setRootType(GQLOpType.Query, queryRoot)
        .build();
  }

  public static ExecutableSchema withRoot(final Type queryRoot, final Type mutationRoot) {
    return builder()
        .setRootType(GQLOpType.Query, queryRoot)
        .setRootType(GQLOpType.Mutation, mutationRoot)
        .build();
  }

  /**
   * finds the type with the specified name.
   *
   * @param typeName
   * @return
   */

  public ExecutableType resolveType(final String typeName) {
    return this.types.get(typeName);
  }

  /**
   * converts a registered java type to it's executable type.
   *
   * @param type
   * @return
   */

  public ExecutableType type(final TypeToken<? extends Object> type) {
    return this.tokens.get(type);
  }

  public ExecutableType type(final Type type) {
    return this.tokens.get(TypeToken.of(type));
  }

  @Override
  public ZType resolve(final String typeName) {
    return this.resolveType(typeName);
  }

}
