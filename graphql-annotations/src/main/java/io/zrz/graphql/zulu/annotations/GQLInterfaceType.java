package io.zrz.graphql.zulu.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.zrz.graphql.zulu.annotations.GQLType.Kind;

/**
 * indicates that the specified type is a GQL object type
 *
 * @author theo
 *
 */

@Retention(RUNTIME)
@Target(TYPE)
@GQLType(kind = Kind.INTERFACE)
public @interface GQLInterfaceType {

  String name() default "";

  Class<?>[] implementations() default {};

}
