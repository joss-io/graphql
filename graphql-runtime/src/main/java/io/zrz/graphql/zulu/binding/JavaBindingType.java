package io.zrz.graphql.zulu.binding;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaOutputField;

/**
 * a logical representation of a java type.
 */

public class JavaBindingType {

  private TypeToken<?> type;
  private Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
  private JavaBindingClassAnalysis analysis;
  private Set<JavaBindingType> supertypes = new HashSet<>();
  private JavaBindingProvider generator;

  public JavaBindingType(JavaBindingProvider generator, TypeToken<?> type) {
    this.generator = generator;
    this.type = type;
    this.analysis = JavaBindingClassAnalysis.lookup(type.getRawType());
  }

  /**
   * the resulting analysis of this type.
   */

  public JavaBindingClassAnalysis analysis() {
    return this.analysis;
  }

  /**
   * adds an interface.
   */

  public void processInterface(JavaBindingType type) {

    if (type.type.isSubtypeOf(type.type)) {
      this.supertypes.add(type);
    }

  }

  /**
   * all of the supertypes of this java type.
   */

  public Stream<JavaBindingType> supertypes() {
    return this.supertypes.stream();
  }

  /**
   * returns all of the fields in this logical type, which includes methods from all mixin supertypes which are not
   * hidden by methods declared in this class.
   */

  public Stream<? extends JavaOutputField> outputFields() {
    return Stream.concat(
        this.analysis.methods().filter(m -> shouldInclude(m)),
        Stream.concat(
            this.analysis
                .superTypes()
                .filter(a -> a.isMixin())
                .map(a -> generator.include(a.typeToken()))
                .flatMap(t -> t.outputFields()),
            this.generator.extensionsFor(this.type)));
  }

  private boolean shouldInclude(JavaBindingMethodAnalysis m) {
    return m.isAutoInclude();
  }

  /**
   * 
   */

  public JavaBindingType addName(String typeName) {
    this.names.add(typeName);
    return this;
  }

  public boolean hasName(String name) {
    return this.names.contains(name);
  }

  public Set<String> names() {
    return this.names;
  }

  /**
   * 
   */

  @Override
  public String toString() {
    return "JavaType{token=" + this.type + ", names=" + this.names + "}";
  }

}
