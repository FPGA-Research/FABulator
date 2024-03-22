package fabulator.object;

import fabulator.language.Text;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StatisticsSection {

    private Text name;
    private String value;
    private List<Pair<String, String>> entries;

    public static StatisticsSection of(
            Text name,
            Object value,
            List<Pair<String, String>> entries) {

        StatisticsSection section = new StatisticsSection(
                name,
                String.valueOf(value),
                entries
        );
        return section;
    }
}
