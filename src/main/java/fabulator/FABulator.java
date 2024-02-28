/*
    Copyright 2024 Heidelberg University
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    SPDX-License-Identifier: Apache-2.0
 */

package fabulator;

import fabulator.async.FileChangedManager;
import fabulator.ui.icon.ImageIcon;
import fabulator.ui.style.Style;
import fabulator.ui.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class FABulator extends Application {

    public static final String APP_NAME = "FABulator";

    @Getter
    private static FABulator application;
    private Stage stage;
    private MainView mainView;

    @Override
    public void start(Stage stage) {
        application = this;

        this.stage = stage;
        this.stage.setTitle(APP_NAME);
        this.stage.getIcons().add(ImageIcon.FABULOUS.getImage());

        this.mainView = new MainView();
        this.mainView.getStylesheets().addAll(
                Style.DARK.getStyleSheet(),
                Style.DARK.getIconSheet(),
                Style.DARK.getColorSheet()
        );

        Scene scene = new Scene(
                this.mainView,
                960,
                720
        );

        this.stage.setScene(scene);
        this.stage.show();
    }

    @Override
    public void stop() {
        FileChangedManager.getInstance().stopScheduler();
    }
}