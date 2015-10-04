package com.scaffold.common.rest.databind;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MixinModuleFactory {

	public static Module createMixinModule(final Class<?> target, final Class<?> mixinSource) {
        return new SimpleModule(UUID.randomUUID().toString()) {
			private static final long serialVersionUID = 7573981517912949575L;

			@Override
			public void setupModule(SetupContext context) {
				context.setMixInAnnotations(target, mixinSource);
			}
		};
	}

    public static Module createMixinModule(final Map<Class<?>, Class<?>> mixins) {
        return new SimpleModule(UUID.randomUUID().toString()) {

            private static final long serialVersionUID = -8261968530118145046L;

            @Override
			public void setupModule(SetupContext context) {
                for (Entry<Class<?>, Class<?>> mixin : mixins.entrySet()) {
                    context.setMixInAnnotations(mixin.getKey(), mixin.getValue());
				}
			}
		};
	}
}