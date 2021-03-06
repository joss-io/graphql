package io.zrz.graphql.core.parser;

public class ParserExceptions
{

  /**
   * Throw an except that is somethign excepted.
   */

  public static SyntaxErrorException expect(ParseContext ctx, String expected, String message)
  {
    return new SyntaxErrorException(ctx, expected, message);
  }

  public static GQLException endOfStream()
  {
    return new GQLException("EOF");
  }

  public static SyntaxErrorException create(ParseContext ctx, String message)
  {
    return new SyntaxErrorException(ctx, message);
  }

  public static GQLException create(Lexer lexer, String message)
  {
    return new GQLException(message);
  }

}
