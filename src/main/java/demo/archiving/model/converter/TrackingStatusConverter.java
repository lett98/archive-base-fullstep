package demo.archiving.model.converter;

import demo.archiving.model.TrackingStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class TrackingStatusConverter implements AttributeConverter<TrackingStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TrackingStatus trackingStatus) {
        if (trackingStatus == null) {
            return null;
        }
        return trackingStatus.value();
    }

    @Override
    public TrackingStatus convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        return TrackingStatus.fromInt(value);

    }
}
