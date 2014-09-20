package nl.marcus.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Mixin for activating the {@link ExplicitPropertiesFilter} (assuming it is named 'explicitProperties').
 * 
 * @author Marcus Klimstra
 */
@JsonFilter("explicitProperties")
public abstract class ExplicitPropertiesMixin {}
