package nl.marcus.ember;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Fields annotated with this class will be ignored
 * by the schema generator. It is needed mainly for
 * the getLinks method that provides additional
 * metadata for the model, but is not part of the
 * model itself.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EmberIgnore {
}
