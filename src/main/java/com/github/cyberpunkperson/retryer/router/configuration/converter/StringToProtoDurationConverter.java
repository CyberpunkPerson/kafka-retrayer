package com.github.cyberpunkperson.retryer.router.configuration.converter;

import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.springframework.util.ObjectUtils.isEmpty;

@Component
@ConfigurationPropertiesBinding
class StringToProtoDurationConverter implements GenericConverter {

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Duration.class));
    }

    public Duration convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Optional<java.time.Duration> javaDuration = isEmpty(source) ?
                empty() :
                this.convert(source.toString(), this.getStyle(targetType), this.getDurationUnit(targetType));

        return javaDuration
                .map(duration -> Durations.fromMillis(duration.toMillis()))
                .orElseThrow(() -> new ConversionFailedException(sourceType, targetType, source,
                        new IllegalArgumentException("A null value cannot be assigned to a com.google.protobuf.Duration type")));
    }

    private DurationStyle getStyle(TypeDescriptor targetType) {
        DurationFormat annotation = targetType.getAnnotation(DurationFormat.class);
        return annotation != null ? annotation.value() : null;
    }

    private ChronoUnit getDurationUnit(TypeDescriptor targetType) {
        DurationUnit annotation = targetType.getAnnotation(DurationUnit.class);
        return annotation != null ? annotation.value() : null;
    }

    private Optional<java.time.Duration> convert(String source, DurationStyle style, ChronoUnit unit) {
        style = style != null ? style : DurationStyle.detect(source);
        return of(style.parse(source, unit));
    }
}
