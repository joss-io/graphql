package io.zrz.zulu.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Value;

import io.zrz.graphql.zulu.engine.ZuluParameterReader;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;
import io.zrz.zulu.graphql.GraphQLProtos.QueryRequest;

public class GrpcParameterProvider implements ZuluParameterReader {

  private static final ObjectMapper mapper = new ObjectMapper();
  private QueryRequest req;

  public GrpcParameterProvider(QueryRequest req) {
    this.req = req;
  }

  @Override
  public Object get(String parameterName, ExecutableTypeUse targetType) {

    Value value = req.getVariables().getFieldsMap().get(parameterName);

    switch (value.getKindCase()) {
      case BOOL_VALUE:
        return mapper.convertValue(value.getBoolValue(), mapper.getTypeFactory().constructType(targetType.javaType().getType()));
      case NUMBER_VALUE:
        return mapper.convertValue(value.getNumberValue(), mapper.getTypeFactory().constructType(targetType.javaType().getType()));
      case STRING_VALUE:
        return mapper.convertValue(value.getStringValue(), mapper.getTypeFactory().constructType(targetType.javaType().getType()));
      case NULL_VALUE:
        return null;
      case LIST_VALUE:
      case STRUCT_VALUE:
        // need to implement. fall through.
      case KIND_NOT_SET:
      default:
        throw new IllegalArgumentException("unsupported");

    }

  }

}