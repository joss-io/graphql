package io.zrz.graphql.zulu.doc;

import java.util.List;
import java.util.Optional;

import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.doc.DefaultGQLPreparedOperation.OpInputType;
import io.zrz.zulu.types.ZAnnotation;

public interface GQLPreparedOperation {

  /**
   * the name of the operation, if there is one.
   */

  Optional<String> operationName();

  /**
   * the operation tyoe.
   */

  GQLOperationType type();

  /**
   * annotations on the operation.
   */

  List<ZAnnotation> annotations();

  /**
   * the input type expected for this operation.
   */

  OpInputType inputType();

  /**
   * the return selections made on the operation.
   */

  List<? extends GQLPreparedSelection> selection();

  /**
   * the document this operation was defined in.
   */

  GQLPreparedDocument document();

  /**
   *
   */

  void validate(GQLPreparedValidationListener listener);

}
