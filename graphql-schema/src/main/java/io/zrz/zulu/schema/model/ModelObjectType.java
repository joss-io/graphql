package io.zrz.zulu.schema.model;

import com.google.common.collect.ImmutableMap;

import io.zrz.zulu.schema.ResolvedType;
import io.zrz.zulu.schema.binding.BoundObjectSelection;
import io.zrz.zulu.schema.model.ModelElementVisitor.FunctionVisitor;
import io.zrz.zulu.schema.model.ModelElementVisitor.VoidVisitor;

/**
 * a type generated by a selection that contains multiple named fields.
 * 
 * @author theo
 *
 */

public class ModelObjectType implements ModelElement {

  private BoundObjectSelection obj;
  private ImmutableMap<String, ModelElement> fields;

  public ModelObjectType(BoundObjectSelection obj, ImmutableMap<String, ModelElement> fields) {
    this.obj = obj;
    this.fields = fields;
  }

  public ResolvedType type() {
    return obj.selectionType();
  }

  public ImmutableMap<String, ModelElement> fields() {
    return this.fields;
  }

  public BoundObjectSelection selection() {
    return this.obj;
  }

  @Override
  public void accept(VoidVisitor visitor) {
    visitor.visitModelObject(this);
  }

  @Override
  public <T, R> R accept(FunctionVisitor<T, R> visitor, T value) {
    return visitor.visitModelObject(this, value);
  }

  public String outputName() {
    return obj.outputName();
  }

}