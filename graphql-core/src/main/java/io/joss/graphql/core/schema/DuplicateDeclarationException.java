package io.joss.graphql.core.schema;

public class DuplicateDeclarationException extends RuntimeException {

  public DuplicateDeclarationException(String symbol, String msg) {
    super(msg);
  }

}
