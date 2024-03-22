package fabulator.object;

import fabulator.language.Text;
import lombok.Getter;

import java.util.List;

@Getter
public class StatisticsCategory {

    public enum Type {
        SIMPLE,
        COMPLEX
    }

    private Text name;
    private String value;
    private List<StatisticsSection> sections;

    public StatisticsCategory(
            Text name,
            String value,
            StatisticsSection... sections) {

        this.name = name;
        this.value = value;
        this.sections = List.of(sections);
    }

    public static StatisticsCategory of(
            Text name,
            Object value,
            StatisticsSection... sections) {

        return new StatisticsCategory(
                name,
                String.valueOf(value),
                sections
        );
    }

    public Type getType() {
        if (this.sections.isEmpty()) {
            return Type.SIMPLE;
        } else {
            return Type.COMPLEX;
        }
    }
}
