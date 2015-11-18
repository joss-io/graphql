package io.joss.graphql.core.converter;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;

/**
 * Converts {@link GQLObjectValue} instances to any java class that looks like it can be constrcucted.
 * 
 * @author theo
 *
 */

public class DynamicClassCreationMaterializer implements TypeMaterializer<GQLObjectValue>
{

  @Override
  public <O> O convert(TypeConverter converter, GQLObjectValue from, Type target, Annotation[] annotations)
  {

    Class<?> targetType = (Class<?>)target;
    
    // ---

    for (Constructor<?> ctor : targetType.getConstructors())
    {

      ConstructorProperties props = ctor.getAnnotation(ConstructorProperties.class);

      if (props != null)
      {
        return (O) create(converter, from, targetType, annotations, ctor, props);
      }

    }

    // look for a public static method named 'builder' that takes no args.

    for (Method m : targetType.getDeclaredMethods())
    {
      if (Modifier.isStatic(m.getModifiers()) && m.getName().equals("builder") && m.getParameterCount() == 0)
      {
        return (O) builder(converter, from, targetType, annotations, m);
      }
    }

    // TODO Auto-generated method stub
    return null;
  }

  /**
   * construct an instance by using the constructor properties.
   * 
   * @param from
   * @param targetType
   * @param annotations
   * @param ctor
   * @param props
   * @return
   */

  private <O> O create(TypeConverter converter, GQLObjectValue from, Class<O> targetType, Annotation[] annotations, Constructor<?> ctor, ConstructorProperties props)
  {
    
    try
    {
      
      
      MethodHandle ref = MethodHandles.publicLookup().unreflectConstructor(ctor);

      for (int i = 0; i < props.value().length; ++i)
      {

        String prop = props.value()[i];
        AnnotatedType ptype = ctor.getAnnotatedParameterTypes()[i];

        // ---


        GQLValue field = from.entries().get(prop);
        Object value = converter.convert(field, ptype);
                
        ref = MethodHandles.insertArguments(ref, 0, value);

      }
      
      return (O) ref.invoke();

    }
    catch (Throwable t)
    {
      t.printStackTrace();
      throw new RuntimeException(t);
    }
    
  }

  /**
   * 
   * @param from
   * @param targetType
   * @param annotations
   * @param m
   * @return
   */

  private <O> O builder(TypeConverter converter, GQLObjectValue from, Class<O> targetType, Annotation[] annotations, Method m)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
