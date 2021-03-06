package io.zrz.graphql.core.parser;

import org.junit.Test;

import io.zrz.graphql.core.doc.GQLDocument;

public class ParseContextTest {

  private final GQLParser PARSER = DefaultGQLParser.instance();

  @Test(expected = SyntaxErrorException.class)
  public void testFail() {
    this.PARSER.parse("fragment on on Moo {}");
  }

  @Test
  public void test() {
    this.parse("fragment A on Moo { aa }");
    this.parse("fragment B on Moo { cows { moo }, meep(field: 1), cows: mooo { again { me } } }");
    this.parse("fragment C on Moo { bb }");
    this.parse("fragment D on Moo { alias: xxx } fragment F on Cows { xxx }");
    this.parse("query  ddd { fff }   ");
    this.parse(" query myquery { xxx } ");
    this.parse(" query myquery @include(if: $condition) {a} ");
    this.parse("query myquery ($condition: Boolean, $another: xxx) {a} ");
    this.parse("query myquery ($condition: Boolean = false) {a} ");
    this.parse("query myquery ($condition: Boolean = \"xxx\") {a} ");
    this.parse("query myquery ($condition: Boolean = 1234) {a} ");
    this.parse("query myquery ($condition: Boolean = []) {a} ");
    this.parse("query myquery ($condition: Boolean = {}) {a} ");
    this.parse("{ some(data: cows) }");

    this.parse("query getZuckProfile($devicePicSize: Int) { user(id: 4) { id name profilePic(size: $devicePicSize) } }");

  }

  private GQLDocument parse(final String string) {
    return this.PARSER.parse(string);
  }

}
