package fabulator.object;

import lombok.Getter;

import java.util.List;

public class Statistics {

    @Getter
    private List<StatisticsCategory> categories;

    public Statistics(StatisticsCategory... categories) {
        this.categories = List.of(categories);
    }

    public static Statistics of(StatisticsCategory... categories) {
        return new Statistics(categories);
    }
}
