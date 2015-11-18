package io.joss.graphql.core.converter;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import io.jgql.core.reflect.ReflectionUtilsTest;
import lombok.Value;

/**
 * Helper class which provides an API for converting between different types - both primitive and collections/arrays.
 *
 * Although there are other libraries out there, we need something that handles {@link AnnotatedType} and generics rather than plain {#link
 * Class<?> instances}.
 *
 * @author theo
 *
 */

public class TypeConverter
{

  public static TypeConverter JRE_COVNERTER = new TypeConverter().registerBuiltins();

  public static TypeConverter defaultConverter()
  {
    return JRE_COVNERTER;
  }

  @Value
  private static class Converstion
  {
    private final Class<?> inputClass;
    private final Class<?> outputClass;
  }

  private Map<Converstion, TypeMapper<Object, Object>> registered = new HashMap<>();

  public TypeConverter()
  {
  }

  private TypeConverter registerBuiltins()
  {

    register(String.class, Integer.class, Integer::new);
    register(Integer.class, String.class, val -> val.toString());

    register(String.class, Long.class, Long::new);
    register(Long.class, String.class, val -> val.toString());

    register(String.class, Double.class, Double::new);
    register(Double.class, String.class, val -> val.toString());

    register(String.class, Float.class, Float::new);
    register(Float.class, String.class, val -> val.toString());

    register(String.class, Boolean.class, Boolean::new);
    register(Boolean.class, String.class, val -> val.toString());

    register(String.class, BigInteger.class, BigInteger::new);
    register(BigInteger.class, String.class, val -> val.toString());

    register(String.class, Instant.class, val -> Instant.parse(val));
    register(Instant.class, String.class, val -> val.toString());

    return this;

  }

  /**
   * Registers a type mapper that can convert from one type to another.
   */

  public <I extends Object, O extends Object> void register(Class<I> inputClass, Class<O> outputClass, TypeMapper<I, O> mapper)
  {
    this.registered.put(new Converstion(inputClass, outputClass), (TypeMapper<Object, Object>) mapper);
  }

  /**
   *
   * @param input
   * @param outputClass
   * @return
   * @throws Exception
   */

  public <I extends Object, O extends Object> O convert(I input, Class<O> outputClass)
  {

    if (input == null)
    {
      return null;
    }

    TypeMapper<? super Object, ? super Object> converter = registered.get(new Converstion(input.getClass(), outputClass));

    if (converter != null)
    {
      return (O) converter.convert(input);
    }

    for (Class<?> iface : input.getClass().getInterfaces())
    {

      converter = registered.get(new Converstion(iface, outputClass));

      if (converter != null)
      {
        return (O) converter.convert(input);
      }

    }
    
    // check out the superclasses.

    Class<?> superclass = input.getClass();

    while (superclass != null)
    {

      converter = registered.get(new Converstion(superclass, outputClass));

      if (converter != null)
      {
        return (O) converter.convert(input);
      }

      superclass = superclass.getSuperclass();
    }

    throw new RuntimeException(String.format("No converter from '%s' to '%s'", input.getClass(), outputClass));

  }

  public <I, O> O convert(I input, Type outputClass)
  {
    return convert(input, (Class<?>) outputClass);
  }

  public <I, O> O convert(I input, AnnotatedType outputClass)
  {
    return convert(input, outputClass.getType());
  }

}
