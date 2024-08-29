package io.runescript.plugin.ide.searchEverywhere;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFiltersStatisticsCollector;

public class RsTriggerFilterCollector extends SearchEverywhereFiltersStatisticsCollector.BaseFilterStatisticsCollector<RsTriggerRef> {

    @Override
    public void elementMarkChanged(RsTriggerRef element, boolean isMarked) {
    }
}