package io.zrz.graphql.core.binder.execution.case1;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import io.zrz.graphql.core.binder.TypeBindingResult;
import io.zrz.graphql.core.binder.TypeScanner;
import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLType;
import io.zrz.graphql.core.binder.execution.QueryEnvironment;
import io.zrz.graphql.core.binder.execution.planner.PreparedQuery;
import io.zrz.graphql.core.binder.execution.planner.basic.BasicExecutionPlanner;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.binder.runtime.DataContexts;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.graphql.core.parser.GQLParser;
import lombok.extern.slf4j.Slf4j;

/**
 * test all of the pipeline stage paths.
 * 
 * @author theo
 *
 */

@Slf4j
public class TestCase2
{

  @GQLType
  public static class ChildFieldsTestInner
  {

    @GQLField
    public String getId()
    {
      return "hello";
    }

    @GQLField
    public List<ChildFieldsTestInner> getSelfs()
    {
      return Lists.newArrayList(this, this, this);
    }

  }

  @GQLType
  public static class ChildFieldsTestRoot
  {

    @GQLField
    public ChildFieldsTestInner getChild()
    {
      return new ChildFieldsTestInner();
    }

  }

  @Test
  public void test()
  {

    // startup time:

    TypeBindingResult scanner = TypeScanner.bind(ChildFieldsTestRoot.class);

    // parse incoming request:

    GQLDocument doc = GQLParser.parseDocument("{ child { selfs { id, selfs { id } } } }");

    DataContext root = DataContexts.build(scanner.registry(), scanner.root(), GQLSelectedOperation.defaultQuery(doc));

    // plan it:
    
    PreparedQuery query = new BasicExecutionPlanner(scanner.scanner()).plan(root);
    
    // execution time:

    log.info(" ---- Executing query");
    

    query.execute(new ChildFieldsTestRoot(), QueryEnvironment.emptyEnvironment(), result -> {
      
      System.err.println(result);

    });
    

  }

}